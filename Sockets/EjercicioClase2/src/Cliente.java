import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        System.out.println("Cliente conectado");

        try (
                Socket socket = new Socket("localhost", 1234);
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                BufferedReader teclado = new BufferedReader(
                        new InputStreamReader(System.in));
        ) {
            System.out.println("Conectado al Servidor");

            Scanner sc = new Scanner(System.in);

            String opcion = "";

            while(!opcion.equals("6")) {
                System.out.println("----------MENU----------" +
                        "\n 1.- Agregar" +
                        "\n 2.- Mostrar" +
                        "\n 3.- Calcular Media" +
                        "\n 4.- Buscar Maximo" +
                        "\n 5.- Borrar Lista" +
                        "\n 6.- Cerrar Sesion" +
                        "------------------------"
                );

                opcion = sc.nextLine();
                out.println(opcion);

                String respuesta = in.readLine();

                if ("Agregar".equals(respuesta)) {
                    System.out.println("Introduce el numero que quieras anadir a la lista: ");
                    out.println(sc.nextLine());
                    System.out.println("Numero enviado");
                } else {
                    System.out.println(respuesta);
                }

            }

        } catch (UnknownHostException e) {
            System.err.println("Error de conexión");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Error de conexión");
            throw new RuntimeException(e);
        }
    }
}
