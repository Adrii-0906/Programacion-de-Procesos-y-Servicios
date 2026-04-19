import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(1234);) {
            System.out.println("Servidor concurrente escuchando el puerto 1234...");

            while (true) {
                Socket cliente = server.accept();
                System.out.println("Cliente conectado desde: " + cliente.getInetAddress());

                new ClienteHadler(cliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
