import java.io.*;
import java.net.*;

public class Cliente {

    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("       TRIVIA - CLIENTE");
        System.out.println("==============================================");

        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        try {
            // Pedir la IP del servidor
            System.out.print("Introduce la IP del servidor (o 'localhost'): ");
            String host = teclado.readLine().trim();
            if (host.isEmpty()) {
                host = "localhost";
            }

            // Conectar al servidor
            System.out.println("Conectando a " + host + ":" + PUERTO + "...");
            Socket socket = new Socket(host, PUERTO);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Conectado al servidor!");
            System.out.println("----------------------------------------------");

            // Pedir nick al jugador
            System.out.print("Introduce tu nombre de usuario (nick): ");
            String nick = teclado.readLine().trim();
            out.println("NICK|" + nick);

            // Leer respuesta del servidor
            String respuestaServidor = in.readLine();
            if (respuestaServidor == null || respuestaServidor.startsWith("NICK_ERROR")) {
                if (respuestaServidor != null) {
                    String error = respuestaServidor.split("\\|").length > 1
                            ? respuestaServidor.split("\\|")[1] : "Error desconocido";
                    System.out.println("Error: " + error);
                }
                socket.close();
                return;
            }

            System.out.println("Nick aceptado! Bienvenido, " + nick);

            // Leer mensaje de espera
            String espera = in.readLine();
            if (espera != null && espera.startsWith("ESPERA|")) {
                System.out.println(espera.split("\\|", 2)[1]);
            }

            // Hilo de lectura: recibe mensajes del servidor
            HiloLectura hiloLectura = new HiloLectura(in);
            hiloLectura.start();

            // Hilo principal: lee input del usuario
            String input;
            while ((input = teclado.readLine()) != null) {
                input = input.trim().toLowerCase();

                if (input.equals("a") || input.equals("b") || input.equals("c") || input.equals("d")) {
                    out.println("RESPUESTA|" + input);
                    System.out.println("Respuesta enviada: " + input.toUpperCase());
                } else if (input.equals("salir")) {
                    break;
                } else {
                    System.out.println("Introduce una opcion valida: a, b, c o d");
                }
            }

            socket.close();

        } catch (IOException e) {
            System.err.println("Error de conexion: " + e.getMessage());
        }
    }
}
