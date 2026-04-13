import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        System.out.println("Esperando conexión...");

        // Un solo try que gestiona todos los recursos en cascada
        try (ServerSocket server = new ServerSocket(1234);
             Socket cliente = server.accept(); // Se detiene aquí hasta que alguien entra
             BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
             PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {

            System.out.println("Cliente conectado!");

            String mensaje = in.readLine();

            if (mensaje != null) {
                String[] partes = mensaje.split(":");
                String operacion = partes[0];
                int num1 = Integer.parseInt(partes[1]);
                int num2 = Integer.parseInt(partes[2]);

                switch (operacion) {
                    case "SUMA":
                        out.println("Suma: " + (num1 + num2));
                        break;
                    case "RESTA":
                        out.println("Resta: " + (num1 - num2));
                        break;
                    case "DIV":
                        out.println("Division: " + (num1 / num2));
                        break;
                    default:
                        out.println("Operación no válida");
                        break;
                }
            }

        } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } catch (ArithmeticException a) {
            System.err.println("Division sobre 0 " + a.getMessage());
        }
        // Al llegar aquí, TODO (server, cliente, in, out) se ha cerrado automáticamente.
    }
}