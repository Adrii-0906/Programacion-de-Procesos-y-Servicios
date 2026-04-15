import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            System.out.println("Servidor concurrente escuchando en el puerto 5000...");

            while (true) {
                Socket cliente = server.accept();
                System.out.println("¡Nuevo cliente conectado desde " + cliente.getInetAddress() + "!");

                // Iniciamos el hilo sin bloquear el servidor
                new ClienteHandler(cliente).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}