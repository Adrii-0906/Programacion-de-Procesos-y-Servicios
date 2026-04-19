import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameManager {
    private ArrayList<ClienteHandler> clientes = new ArrayList<>();
    private static volatile boolean rondaAbierta = false;
    private ArrayList<Pregunta> preguntas = new ArrayList<>();

    public GameManager(ArrayList<ClienteHandler> clientes) {
        this.clientes = clientes;
        // Pregunta 1
        this.preguntas.add(new Pregunta("¿Qué es el \"Hardware\" de un ordenador?", "a) Los programas y aplicaciones instaladas.", "b) El sistema operativo que hace funcionar el equipo.", "c) Las partes físicas y tangibles del ordenador (pantalla, teclado, placa, etc.).", "d) La conexión a internet.", "c"));
        // Pregunta 2
        this.preguntas.add(new Pregunta("¿Cuál de estos NO es un lenguaje de programación?", "a) Python", "b) Java", "c) HTML", "d) C++", "c"));
        // Pregunta 3
        this.preguntas.add(new Pregunta("¿Qué componente se encarga de ejecutar las instrucciones de los programas?", "a) La memoria RAM", "b) El disco duro", "c) La CPU (Unidad Central de Procesamiento)", "d) La tarjeta gráfica", "c"));
        // Pregunta 4
        this.preguntas.add(new Pregunta("¿Qué tipo de memoria es volátil y pierde su contenido al apagar el ordenador?", "a) Disco duro (HDD)", "b) Memoria RAM", "c) SSD (Unidad de Estado Sólido)", "d) Memoria ROM", "b"));
        // Pregunta 5
        this.preguntas.add(new Pregunta("¿Qué protocolo se utiliza para enviar correos electrónicos?", "a) HTTP", "b) FTP", "c) SMTP", "d) POP3", "c"));
    }

    public void iniciarPartida() {
        try {
            System.out.println("Partida iniciada");
            enviarTodos("=== LA PARTIDA HA COMENZADO ===");

            int numPregunta = 1;
            for (Pregunta p : preguntas) {
                enviarTodos("\n--- Pregunta " + numPregunta + " de " + preguntas.size() + " ---");
                enviarTodos(extraerPregunta(p));
                enviarTodos("Tienes 15 segundos para responder. Formato: RESPUESTA|letra");
                rondaAbierta = true;
                Thread.sleep(15000);
                rondaAbierta = false;
                corregirRespuestas(p.getRespuestaCorrecta());
                // Ranking tras cada pregunta
                mostrarRanking(false);
                limpiarRespuestas();
                numPregunta++;
            }

            // Ranking final con ganador
            enviarTodos("\n========== PARTIDA FINALIZADA ==========");
            mostrarRanking(true);

        } catch (Exception e) {
            System.out.println("Error en iniciar partida: " + e.getMessage());
        }
    }

    public void mostrarRanking(boolean esFinal) {
        // Ordenar por puntos de mayor a menor
        Collections.sort(clientes, new Comparator<ClienteHandler>() {
            @Override
            public int compare(ClienteHandler a, ClienteHandler b) {
                return b.getPuntos() - a.getPuntos();
            }
        });

        String titulo;
        if (esFinal) {
            titulo = "=== RANKING FINAL ===";
        } else {
            titulo = "=== RANKING PARCIAL ===";
        }

        String ranking = titulo + "\n";
        for (int i = 0; i < clientes.size(); i++) {
            ClienteHandler cl = clientes.get(i);
            ranking += (i + 1) + "º - " + cl.getNick() + ": " + cl.getPuntos() + " puntos\n";
        }

        if (esFinal) {
            // Anunciar ganador
            ClienteHandler ganador = clientes.get(0);
            ranking += "GANADOR: " + ganador.getNick() + " con " + ganador.getPuntos() + " puntos!";
        }

        // Mostrar en consola del servidor
        System.out.println(ranking);

        // Enviar a todos los clientes
        enviarTodos(ranking);
    }

    public void limpiarRespuestas() {
        for (ClienteHandler cl : clientes) {
            cl.limpiaRespuesta();
        }
    }

    public void enviarTodos(String msg) {
        for (ClienteHandler cl : clientes) {
            cl.enviarMensaje(msg);
        }
    }

    public void corregirRespuestas(String sol) {
        for (ClienteHandler cl : clientes) {
            cl.corregirRespuesta(sol);
        }
    }

    public String extraerPregunta(Pregunta p) {
        return p.getEnunciado() + "\n  A: " + p.getRespuestaA()
                + "\n  B: " + p.getRespuestaB()
                + "\n  C: " + p.getRespuestaC()
                + "\n  D: " + p.getRespuestaD();
    }

    public static boolean isRondaAbierta() {
        return rondaAbierta;
    }

    public ArrayList<ClienteHandler> getClientes() {
        return clientes;
    }

    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }
}
