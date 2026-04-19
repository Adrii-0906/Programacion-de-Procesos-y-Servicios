import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteHadler extends Thread{
    private Socket cliente;
    private static int contador = 0;

    public ClienteHadler(Socket socket) {
        this.cliente = socket;
    }

    @Override
    public void run() {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
            ) {
            String operacion;
            while ((operacion = in.readLine()) != null && !operacion.equals("Exit")) {
                String cadena = null;
                switch (operacion) {
                    case "1":
                        incrementar();
                        out.println("Contador incrementado");
                        break;
                    case "2":
                        out.println(obtener());
                        break;
                    case "3":
                        resetear();
                        out.println("Contador resetedo");
                        break;
                    default:
                        out.println("Operacion no valida");
                }
            }
            out.println("Adios");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void incrementar() {
        contador++;
    }

    public static synchronized int obtener() {
        return contador;
    }

    public static synchronized void resetear() {
        contador = 0;
    }
}
