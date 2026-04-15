import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class HiloConexiones extends Thread {

    private ArrayList<ClienteHandler> jugadores;
    private int puerto;
    private int maxJugadores;

    public HiloConexiones(ArrayList<ClienteHandler> jugadores, int puerto, int maxJugadores) {
        this.jugadores = jugadores;
        this.puerto = puerto;
        this.maxJugadores = maxJugadores;
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(puerto);

            while (true) {
                Socket cliente = server.accept();

                if (jugadores.size() >= maxJugadores) {
                    // Servidor lleno: rechazar al cliente
                    PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
                    out.println("NICK_ERROR|El servidor esta lleno (max " + maxJugadores + " jugadores)");
                    cliente.close();
                    continue;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);

                // Crear handler para este jugador inmediatamente en un hilo separado
                ClienteHandler handler = new ClienteHandler(cliente, in, out, jugadores, maxJugadores);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor de conexiones: " + e.getMessage());
        }
    }
}
