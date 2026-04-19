import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteHandler extends Thread {

    private Socket cliente;
    private boolean haRespondido;
    private String respuestaActual;
    private int puntos;
    private String nick;

    private BufferedReader in;
    private PrintWriter out;

    // Objeto de bloqueo para synchronized
    private final Object lock = new Object();

    public ClienteHandler(Socket socket) throws Exception {
        this.cliente = socket;
        this.puntos = 0;
        respuestaActual = null;
        this.in = new BufferedReader(
                new InputStreamReader(cliente.getInputStream()));
        this.out = new PrintWriter(
                cliente.getOutputStream(), true);
        // No leemos el nick aquí para no bloquear el hilo de accept
    }

    public int getPuntos() {
        return puntos;
    }

    public String getNick() {
        return nick;
    }

    @Override
    public void run() {
        try {
            // Leemos el nick aquí, en el hilo propio del cliente
            this.nick = in.readLine();
            System.out.println("Nick recibido: " + nick);

            while (true) {
                String mensaje = in.readLine();
                // Escucha constante (Bloqueante) - Lee respuesta.

                if (mensaje == null) break; // El cliente se desconectó

                // Reglas del juego: solo guardamos si la ronda está abierta
                synchronized (lock) {
                    if (GameManager.isRondaAbierta() && !haRespondido) {
                        // Parsear formato RESPUESTA|LETRA
                        if (mensaje.startsWith("RESPUESTA|")) {
                            String[] partes = mensaje.split("\\|");
                            if (partes.length == 2) {
                                this.respuestaActual = partes[1].trim();
                                this.haRespondido = true;
                                out.println("INFO|Respuesta registrada");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de conexión con " + nick);
        }
    }

    public String mostrarNota() {
        String nota = nick + " ha sacado: " + puntos + " puntos";
        out.println(nota);
        return nota;
    }

    public void enviarMensaje(String msg) {
        System.out.println("Mensaje enviado a " + nick + ": " + msg);
        out.println(msg);
    }

    public void limpiaRespuesta() {
        synchronized (lock) {
            haRespondido = false;
            respuestaActual = null;
        }
    }

    public void corregirRespuesta(String sol) {
        synchronized (lock) {
            if (respuestaActual == null) {
                // No respondió a tiempo
                enviarMensaje("No respondiste a tiempo. La respuesta correcta era: " + sol);
            } else if (respuestaActual.equalsIgnoreCase(sol)) {
                // Respuesta correcta
                puntos++;
                enviarMensaje("Respuesta correcta! +1 punto");
            } else {
                // Respuesta incorrecta - no resta puntos
                enviarMensaje("Respuesta incorrecta. La correcta era: " + sol);
            }
        }
    }
}