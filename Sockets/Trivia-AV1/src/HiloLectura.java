import java.io.*;

public class HiloLectura extends Thread {
    private BufferedReader in;

    public HiloLectura(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                procesarMensaje(mensaje);
            }
        } catch (IOException e) {
            System.out.println("Conexion con el servidor cerrada.");
        }
    }

    private void procesarMensaje(String mensaje) {
        if (mensaje.startsWith("PREGUNTA|")) {
            mostrarPregunta(mensaje);
        } else if (mensaje.startsWith("RESULTADO|")) {
            mostrarResultado(mensaje);
        } else if (mensaje.startsWith("RANKING|")) {
            mostrarRanking(mensaje);
        } else if (mensaje.startsWith("FIN|")) {
            mostrarFinal(mensaje);
        } else if (mensaje.startsWith("ESPERA|")) {
            String texto = mensaje.split("\\|", 2)[1];
            System.out.println();
            System.out.println(">>> " + texto);
        } else if (mensaje.startsWith("JUGADORES|")) {
            String num = mensaje.split("\\|")[1];
            System.out.println(">>> Jugadores conectados: " + num);
        }
    }

    private void mostrarPregunta(String mensaje) {
        String[] partes = mensaje.split("\\|");
        // PREGUNTA|num|texto|opA|opB|opC|opD|tiempo
        if (partes.length >= 8) {
            String num = partes[1];
            String texto = partes[2];
            String opA = partes[3];
            String opB = partes[4];
            String opC = partes[5];
            String opD = partes[6];
            String tiempo = partes[7];

            System.out.println();
            System.out.println("==============================================");
            System.out.println("  PREGUNTA " + num + "  (Tienes " + tiempo + " segundos)");
            System.out.println("==============================================");
            System.out.println();
            System.out.println("  " + texto);
            System.out.println();
            System.out.println("  a) " + opA);
            System.out.println("  b) " + opB);
            System.out.println("  c) " + opC);
            System.out.println("  d) " + opD);
            System.out.println();
            System.out.println("----------------------------------------------");
            System.out.print("Tu respuesta (a/b/c/d): ");
        }
    }

    private void mostrarResultado(String mensaje) {
        String[] partes = mensaje.split("\\|");
        // RESULTADO|SI/NO/TIEMPO|respCorrecta|puntos
        if (partes.length >= 4) {
            String resultado = partes[1];
            String correcta = partes[2];
            String puntos = partes[3];

            System.out.println();
            if (resultado.equals("SI")) {
                System.out.println("  *** CORRECTO! ***");
            } else if (resultado.equals("NO")) {
                System.out.println("  *** INCORRECTO ***");
                System.out.println("  La respuesta correcta era: " + correcta);
            } else {
                System.out.println("  *** TIEMPO AGOTADO ***");
                System.out.println("  La respuesta correcta era: " + correcta);
            }
            System.out.println("  Tus puntos: " + puntos);
        }
    }

    private void mostrarRanking(String mensaje) {
        String datos = mensaje.split("\\|", 2)[1];
        String[] entradas = datos.split(",");

        System.out.println();
        System.out.println("---------- RANKING ----------");
        for (int i = 0; i < entradas.length; i++) {
            String[] partes = entradas[i].split(":");
            if (partes.length >= 4) {
                String posicion = partes[0];
                String nick = partes[1];
                String pts = partes[2];
                int racha = Integer.parseInt(partes[3]);

                String medalla = "";
                if (posicion.equals("1")) medalla = " \uD83E\uDD47";
                else if (posicion.equals("2")) medalla = " \uD83E\uDD48";
                else if (posicion.equals("3")) medalla = " \uD83E\uDD49";
                
                String rachaStr = (racha >= 2) ? " (\uD83D\udd25 Racha x" + racha + ")" : "";

                System.out.println("  " + posicion + ". " + nick + " - " + pts + " pts" + medalla + rachaStr);
            }
        }
        System.out.println("-----------------------------");
    }

    private void mostrarFinal(String mensaje) {
        String[] partes = mensaje.split("\\|");
        if (partes.length >= 3) {
            String ganador = partes[1];
            String puntos = partes[2];

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       PARTIDA FINALIZADA");
            System.out.println("==============================================");
            System.out.println();
            System.out.println("  GANADOR: " + ganador + " con " + puntos + " puntos!");
            System.out.println();
            System.out.println("==============================================");
            System.out.println("  Gracias por jugar! Hasta la proxima.");
            System.out.println("==============================================");
        }
    }
}
