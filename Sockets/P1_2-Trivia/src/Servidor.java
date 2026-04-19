import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {

    public static ArrayList<ClienteHandler> clientes = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Servidor arrancado y esperando conexiones...");

        try {
            ServerSocket server = new ServerSocket(5000);

            // Hilo que acepta conexiones en segundo plano
            Thread hiloAceptar = new Thread(() -> {
                try {
                    while (clientes.size() < 10) {
                        Socket cliente = server.accept();
                        ClienteHandler cl = new ClienteHandler(cliente);
                        cl.start();
                        synchronized (clientes) {
                            clientes.add(cl);
                        }
                        System.out.println("Nuevo jugador conectado (" + clientes.size() + "/10)");
                    }
                    System.out.println("Se ha alcanzado el maximo de 10 jugadores.");
                } catch (Exception e) {
                    // El server.close() lanza excepcion, es normal
                }
            });
            hiloAceptar.start();

            // Hilo principal espera el comando START
            BufferedReader teclado = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Escribe START para iniciar la partida:");

            while (true) {
                String comando = teclado.readLine();
                if (comando != null && comando.equalsIgnoreCase("START")) {
                    if (clientes.size() > 0) {
                        break;
                    } else {
                        System.out.println("No hay jugadores conectados. Espera a que se conecte al menos uno.");
                    }
                } else {
                    System.out.println("Comando no reconocido. Escribe START para iniciar.");
                }
            }

            // Cerramos el ServerSocket para que el hilo de accept pare
            server.close();

            System.out.println("Iniciando partida con " + clientes.size() + " jugadores...");
            new GameManager(clientes).iniciarPartida();

        } catch (Exception e) {
            System.err.println("Error de conexion: " + e.getMessage());
        }
    }
}