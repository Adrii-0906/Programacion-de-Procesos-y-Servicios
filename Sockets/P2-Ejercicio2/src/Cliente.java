import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 1234);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner sc = new Scanner(System.in);
                ) {
            System.out.println("Conectado al servidor");
            String opcion;
            while (true) {
                System.out.println("----Menu----" +
                        "\n 1. Incrementar" +
                        "\n 2. Obtener" +
                        "\n 3. Resetear" +
                        "\n Exit");

                opcion = sc.nextLine();

                if (opcion == null || opcion.equals("Exit")) {
                    if (opcion != null) {
                        out.println(opcion);

                        System.out.println("El servidor dice: " + in.readLine());
                    }
                    break;
                }

                switch (opcion) {
                    case "1":
                        out.println(opcion);
                        System.out.println("Servidor: " + in.readLine());
                        break;
                    case "2":
                        out.println(opcion);
                        System.out.println("Servidor: " + in.readLine());
                        break;
                    case "3":
                        out.println(opcion);
                        System.out.println("Servidor: " + in.readLine());
                        break;
                    default:
                        out.println(opcion);
                        System.out.println("Servidor: " + in.readLine());
                        break;
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
