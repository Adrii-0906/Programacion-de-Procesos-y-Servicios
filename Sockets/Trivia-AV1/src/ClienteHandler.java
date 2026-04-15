import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClienteHandler extends Thread {
    private Socket cliente;
    private BufferedReader in;
    private PrintWriter out;
    private String nick;
    private int puntos;
    private String respuesta;
    private boolean haRespondido;
    private boolean conectado;
    private int racha;

    private ArrayList<ClienteHandler> jugadores;
    private int maxJugadores;

    public ClienteHandler(Socket cliente, BufferedReader in, PrintWriter out, ArrayList<ClienteHandler> jugadores, int maxJugadores) {
        this.cliente = cliente;
        this.in = in;
        this.out = out;
        this.jugadores = jugadores;
        this.maxJugadores = maxJugadores;
        this.puntos = 0;
        this.racha = 0;
        this.respuesta = null;
        this.haRespondido = false;
        this.conectado = true;
    }

    @Override
    public void run() {
        try {
            // FASE DE INICIO: Leer Nickname del cliente primero
            String mensajeNick = in.readLine();
            if (mensajeNick != null && mensajeNick.startsWith("NICK|")) {
                String nickCandidato = mensajeNick.split("\\|")[1].trim();

                // Verificar recursivamente la lista para evitar nicks duplicados
                boolean nickRepetido = false;
                // Sincronizar el acceso a la lista de jugadores para curarse en salud
                synchronized(jugadores) {
                    for (int i = 0; i < jugadores.size(); i++) {
                        if (jugadores.get(i).getNick() != null && jugadores.get(i).getNick().equalsIgnoreCase(nickCandidato)) {
                            nickRepetido = true;
                            break;
                        }
                    }

                    if (nickRepetido) {
                        out.println("NICK_ERROR|El nick '" + nickCandidato + "' ya esta en uso");
                        desconectar();
                        return;
                    } else {
                        // Aceptado! 
                        this.nick = nickCandidato;
                        jugadores.add(this);
                        
                        out.println("NICK_OK");
                        out.println("ESPERA|Bienvenido " + this.nick + "! Esperando a que el admin inicie la partida...");
                        
                        System.out.println("[+] Jugador conectado: " + this.nick + " (" + jugadores.size() + "/" + maxJugadores + ")");

                        // Broadcast de actualización de sala
                        for (int i = 0; i < jugadores.size(); i++) {
                            if (jugadores.get(i).estaConectado()) {
                                jugadores.get(i).enviarMensaje("JUGADORES|" + jugadores.size());
                            }
                        }
                    }
                }
            } else {
                out.println("NICK_ERROR|Formato incorrecto. Usa NICK|tunombre");
                desconectar();
                return;
            }

            // Una vez pasado el login, el jugador se queda leyendo comandos normalmente
            String mensaje;
            while (conectado && (mensaje = in.readLine()) != null) {
                if (mensaje.startsWith("ADMIN_START|")) {
                    String[] p = mensaje.split("\\|");
                    java.util.ArrayList<Pregunta> preguntasNuevas = new java.util.ArrayList<>();
                    int idx = 1;
                    for (int i = 0; i < 5; i++) {
                        if (idx + 5 < p.length) {
                             preguntasNuevas.add(new Pregunta(p[idx], p[idx+1], p[idx+2], p[idx+3], p[idx+4], p[idx+5].charAt(0)));
                             idx += 6;
                        }
                    }
                    // Iniciar la partida en un hilo separado para no bloquear el ClienteHandler actual
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Servidor.iniciarPartida(preguntasNuevas);
                        }
                    }).start();
                } else if (mensaje.startsWith("RESPUESTA|")) {
                    String[] partes = mensaje.split("\\|");
                    if (partes.length == 2 && !haRespondido) {
                        respuesta = partes[1].trim().toLowerCase();
                        haRespondido = true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Se desconecto el jugador: " + nick);
        } finally {
            conectado = false;
            try {
                cliente.close();
            } catch (IOException e) {
                // Error al cerrar
            }
        }
    }

    // Enviar un mensaje al cliente
    public void enviarMensaje(String mensaje) {
        if (out != null && conectado) {
            out.println(mensaje);
        }
    }

    // Getters y setters
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getPuntos() {
        return puntos;
    }

    public void sumarPuntos(int cantidad) {
        this.puntos += cantidad;
    }

    public int getRacha() {
        return racha;
    }

    public void incrementarRacha() {
        this.racha++;
    }

    public void resetRacha() {
        this.racha = 0;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public boolean haRespondido() {
        return haRespondido;
    }

    public void resetRespuesta() {
        this.respuesta = null;
        this.haRespondido = false;
    }

    public boolean estaConectado() {
        return conectado;
    }

    public void desconectar() {
        conectado = false;
        try {
            cliente.close();
        } catch (IOException e) {
            // Error al cerrar
        }
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }
}
