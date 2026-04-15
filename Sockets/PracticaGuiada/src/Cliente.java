import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Escribe algo para enviar al servidor: ");
            salida.println(teclado.readLine());
            String respuesta = entrada.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);
            socket.close();
            teclado.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
