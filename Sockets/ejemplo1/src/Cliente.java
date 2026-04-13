import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        // Al definir el socket y los buffers aquí, Java los cierra solo
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Conectado al servidor.");

            out.println("DIV:5:0");
            System.out.println("Respuesta del servidor: " + in.readLine());

        } catch (IOException e) {
            System.err.println("Error en conexión");
        } // Aquí se cierra el socket automáticamente
    }
}