import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Servidor {

    private static final int PUERTO = 5000;
    private static final int MAX_JUGADORES = 10;
    private static final int TIEMPO_RESPUESTA = 15; // segundos por pregunta

    private static ArrayList<ClienteHandler> jugadores = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("       SERVIDOR DE TRIVIA INICIADO");
        System.out.println("==============================================");
        System.out.println("Esperando conexiones en el puerto " + PUERTO + "...");
        System.out.println("Maximo de jugadores: " + MAX_JUGADORES);
        System.out.println("Escribe START para comenzar la partida.");
        System.out.println("----------------------------------------------");

        // Hilo para aceptar conexiones de clientes
        HiloConexiones hiloConexiones = new HiloConexiones(jugadores, PUERTO, MAX_JUGADORES);
        hiloConexiones.start();

        // El hilo principal ahora puede simplemente esperar
        // ya que HiloConexiones mantiene y genera los ClienteHandlers
        // y el Administrador enviara el comando ADMIN_START por socket.
        System.out.println("Servidor esperando comandos de inicio vía TCP/React...");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // =============================================
    //  LOGICA DE LA PARTIDA (LLamado por el Profesor)
    // =============================================
    public static void iniciarPartida(ArrayList<Pregunta> preguntas) {
        if (jugadores.isEmpty()) {
            System.out.println("[!] No hay jugadores conectados (excluyendo Admin).");
            return;
        }
        System.out.println("[*] Iniciando la partida desde el Panel de Admin...");

        // Avisar a todos que comienza el juego
        broadcast("ESPERA|La partida va a comenzar! Preparate...");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Ignorar
        }

        System.out.println();
        System.out.println("==============================================");
        System.out.println("       PARTIDA EN CURSO");
        System.out.println("==============================================");

        // Enviar cada pregunta
        for (int i = 0; i < preguntas.size(); i++) {
            Pregunta pregunta = preguntas.get(i);

            // Resetear respuestas de todos los jugadores
            for (int j = 0; j < jugadores.size(); j++) {
                jugadores.get(j).resetRespuesta();
            }

            System.out.println();
            System.out.println("--- Pregunta " + (i + 1) + " de " + preguntas.size() + " ---");
            System.out.println(pregunta.getTexto());
            System.out.println("  a) " + pregunta.getOpciones()[0]);
            System.out.println("  b) " + pregunta.getOpciones()[1]);
            System.out.println("  c) " + pregunta.getOpciones()[2]);
            System.out.println("  d) " + pregunta.getOpciones()[3]);
            System.out.println("Respuesta correcta: " + pregunta.getRespuestaCorrecta());

            // Enviar pregunta a todos los clientes
            String mensajePregunta = "PREGUNTA|" + (i + 1) + "|" + pregunta.getTexto()
                    + "|" + pregunta.getOpciones()[0]
                    + "|" + pregunta.getOpciones()[1]
                    + "|" + pregunta.getOpciones()[2]
                    + "|" + pregunta.getOpciones()[3]
                    + "|" + TIEMPO_RESPUESTA;
            broadcast(mensajePregunta);

            // Esperar respuestas (maximo TIEMPO_RESPUESTA segundos)
            long inicio = System.currentTimeMillis();
            long limite = inicio + (TIEMPO_RESPUESTA * 1000L);

            while (System.currentTimeMillis() < limite) {
                // Comprobar si todos han respondido
                boolean todosRespondieron = true;
                for (int j = 0; j < jugadores.size(); j++) {
                    if (jugadores.get(j).estaConectado() && !jugadores.get(j).haRespondido()) {
                        todosRespondieron = false;
                        break;
                    }
                }

                if (todosRespondieron) {
                    System.out.println("Todos los jugadores han respondido!");
                    break;
                }

                try {
                    Thread.sleep(500); // Comprobar cada medio segundo
                } catch (InterruptedException e) {
                    // Ignorar
                }
            }

            // Procesar respuestas
            System.out.println();
            System.out.println("Resultados de la pregunta " + (i + 1) + ":");
            for (int j = 0; j < jugadores.size(); j++) {
                ClienteHandler jugador = jugadores.get(j);
                if (!jugador.estaConectado()) continue;

                if (jugador.haRespondido()) {
                    boolean acierto = pregunta.esCorrecta(jugador.getRespuesta().charAt(0));
                    if (acierto) {
                        jugador.incrementarRacha();
                        int puntosSumar = (jugador.getRacha() >= 2) ? 2 : 1;
                        jugador.sumarPuntos(puntosSumar);
                        
                        String msgRacha = (jugador.getRacha() >= 2) ? " (Racha x" + jugador.getRacha() + "!)" : "";
                        jugador.enviarMensaje("RESULTADO|SI|" + pregunta.getRespuestaCorrecta() + "|" + jugador.getPuntos());
                        System.out.println("  " + jugador.getNick() + " -> " + jugador.getRespuesta() + " CORRECTO!" + msgRacha + " (" + jugador.getPuntos() + " pts)");
                    } else {
                        jugador.resetRacha();
                        jugador.enviarMensaje("RESULTADO|NO|" + pregunta.getRespuestaCorrecta() + "|" + jugador.getPuntos());
                        System.out.println("  " + jugador.getNick() + " -> " + jugador.getRespuesta() + " INCORRECTO (" + jugador.getPuntos() + " pts)");
                    }
                } else {
                    jugador.resetRacha();
                    jugador.enviarMensaje("RESULTADO|TIEMPO|" + pregunta.getRespuestaCorrecta() + "|" + jugador.getPuntos());
                    System.out.println("  " + jugador.getNick() + " -> SIN RESPUESTA (" + jugador.getPuntos() + " pts)");
                }
            }

            // Enviar ranking actualizado
            String ranking = generarRanking();
            broadcast("RANKING|" + ranking);
            System.out.println();
            System.out.println("Ranking actual:");
            mostrarRankingServidor();

            // Pausa entre preguntas
            if (i < preguntas.size() - 1) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // Ignorar
                }
            }
        }

        // Fin del juego
        System.out.println();
        System.out.println("==============================================");
        System.out.println("       PARTIDA FINALIZADA");
        System.out.println("==============================================");
        mostrarRankingServidor();

        // Buscar ganador
        String ganador = "";
        int maxPuntos = -1;
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getPuntos() > maxPuntos) {
                maxPuntos = jugadores.get(i).getPuntos();
                ganador = jugadores.get(i).getNick();
            }
        }

        System.out.println();
        System.out.println("GANADOR: " + ganador + " con " + maxPuntos + " puntos!");
        System.out.println("==============================================");

        // Enviar resultado final a todos los clientes
        broadcast("FIN|" + ganador + "|" + maxPuntos);

        // Cerrar conexiones
        for (int i = 0; i < jugadores.size(); i++) {
            try {
                jugadores.get(i).enviarMensaje("ESPERA|Gracias por jugar! Hasta la proxima.");
            } catch (Exception e) {
                // Ignorar
            }
        }
    }

    // =============================================
    //  UTILIDADES
    // =============================================

    // Enviar mensaje a todos los jugadores conectados
    private static void broadcast(String mensaje) {
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).estaConectado()) {
                jugadores.get(i).enviarMensaje(mensaje);
            }
        }
    }

    // Generar string del ranking ordenado
    private static String generarRanking() {
        // Ordenar jugadores por puntos (burbuja, simple)
        ArrayList<ClienteHandler> ordenados = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++) {
            ordenados.add(jugadores.get(i));
        }

        for (int i = 0; i < ordenados.size() - 1; i++) {
            for (int j = 0; j < ordenados.size() - i - 1; j++) {
                if (ordenados.get(j).getPuntos() < ordenados.get(j + 1).getPuntos()) {
                    ClienteHandler temp = ordenados.get(j);
                    ordenados.set(j, ordenados.get(j + 1));
                    ordenados.set(j + 1, temp);
                }
            }
        }

        // Construir string: pos1:nick1:pts1:racha1,pos2:nick2:pts2:racha2,...
        String ranking = "";
        for (int i = 0; i < ordenados.size(); i++) {
            if (i > 0) {
                ranking += ",";
            }
            ranking += (i + 1) + ":" + ordenados.get(i).getNick() + ":" + ordenados.get(i).getPuntos() + ":" + ordenados.get(i).getRacha();
        }
        return ranking;
    }

    // Mostrar ranking en la consola del servidor
    private static void mostrarRankingServidor() {
        ArrayList<ClienteHandler> ordenados = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++) {
            ordenados.add(jugadores.get(i));
        }

        for (int i = 0; i < ordenados.size() - 1; i++) {
            for (int j = 0; j < ordenados.size() - i - 1; j++) {
                if (ordenados.get(j).getPuntos() < ordenados.get(j + 1).getPuntos()) {
                    ClienteHandler temp = ordenados.get(j);
                    ordenados.set(j, ordenados.get(j + 1));
                    ordenados.set(j + 1, temp);
                }
            }
        }

        for (int i = 0; i < ordenados.size(); i++) {
            String medalla = "";
            if (i == 0) medalla = " \uD83E\uDD47";
            else if (i == 1) medalla = " \uD83E\uDD48";
            else if (i == 2) medalla = " \uD83E\uDD49";

            System.out.println("  " + (i + 1) + ". " + ordenados.get(i).getNick()
                    + " - " + ordenados.get(i).getPuntos() + " pts" + medalla);
        }
    }

    // Las preguntas ahora son inyectadas por ADMIN_START desde ClienteHandler
}
