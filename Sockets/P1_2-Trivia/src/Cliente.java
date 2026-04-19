import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {

        // IP configurable: si se pasa como argumento se usa, si no, localhost
        String ip = "192.168.0.25";
        if (args.length > 0) {
            ip = args[0];
        }

        try (
                Socket socket = new Socket(ip, 5000);
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedReader sc = new BufferedReader(
                        new InputStreamReader(System.in));
        ) {

            System.out.println("Conectado al servidor.");

            // Pedir nick y enviarlo al servidor
            System.out.print("Introduce tu nick: ");
            String nick = sc.readLine();
            out.println(nick);

            // Hilo paralelo para ESCUCHAR la red constantemente
            new ReceptorMensajes(in).start();

            System.out.println("Esperando a que el administrador inicie la partida...");

            // El hilo MAIN se bloquea aquí esperando al TECLADO del usuario
            while (true) {
                String respuesta = sc.readLine(); // BLOQUEANTE - RESPUESTA USUARIO
                if (respuesta != null) {
                    out.println("RESPUESTA|" + respuesta); // Formato RESPUESTA|LETRA
                }
            }

        } catch (IOException e) {
            System.err.println("Error en conexion: " + e.getMessage());
        }
    }
}
