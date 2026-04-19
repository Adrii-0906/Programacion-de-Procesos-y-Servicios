import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try(
            Socket socket = new Socket("localhost", 1234);
            BufferedReader in = new BufferedReader(
              new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {

            System.out.println("Conectado al servidor");
            Scanner sc = new Scanner(System.in);
            boolean login = false;

            String respuestaAutenticacion;
            while ((respuestaAutenticacion = in.readLine()) != null) {
                if (respuestaAutenticacion.equals("LOGIN_OK")) {
                    System.out.println("Login correcto!! Accediendo al sistema...");
                    login = true;
                    break;
                } else if (respuestaAutenticacion.equals("LOGIN_BAD")) {
                    System.out.println("Demasiados intentos incorrectos. El servidor ha rechazado la conexion.");
                    break;
                } else if (respuestaAutenticacion.equals("Usuario")) {
                    System.out.println("Introduce el usuario: ");
                    out.println(sc.nextLine());
                } else if (respuestaAutenticacion.equals("password")) {
                    System.out.println("Introduce la contrasena: ");
                    out.println(sc.nextLine());
                } else if (respuestaAutenticacion.equals("CREDENTIALS_BAD")) {
                    System.out.println("Credenciales erroneas. Vuelve a intentarlo.");
                }
            }

            String opcion = "";
            while(!opcion.equals("5") && login) {
                System.out.println("\n--- Menú Principal ---" +
                        "\n1. Sumar" +
                        "\n2. Contador" +
                        "\n3. Invierte" +
                        "\n4. EsPrimo" +
                        "\n5. Salir");

                System.out.println("Introduce una opcion: ");
                opcion = sc.nextLine();
                out.println(opcion);

                if (opcion.equals("1")) {
                    String orden1 = in.readLine();
                    if (orden1.equals("num1_sumar")) {
                        System.out.println("Introduce el primer numero a sumar: ");
                        out.println(sc.nextLine());
                    }

                    String orden2 = in.readLine();
                    if (orden2.equals("num2_sumar")) {
                        System.out.println("Introduce el segundo numero a sumar: ");
                        out.println(sc.nextLine());
                    }

                    String resultadoFinal = in.readLine();
                    System.out.println("El servidor dice que el resultado es: " + resultadoFinal);
                }

                if (opcion.equals("2")) {
                    String orden1 = in.readLine();
                    if (orden1.equals("palabra_contador")) {
                        System.out.println("Introduce la palabra para contar sus vocales: ");
                        out.println(sc.nextLine());
                    }
                    String numeroVocales = in.readLine();
                    System.out.println("El servidor dice que son: " + numeroVocales + " vocales");
                }

                if (opcion.equals("3")) {
                    String orden1 = in.readLine();
                    if (orden1.equals("texto_invertir")) {
                        System.out.println("Introduce el texto que quieras invertir: ");
                        out.println(sc.nextLine());
                    }

                    String textoInvertido = in.readLine();
                    System.out.println("El servidor dice que el texto invertdo es: " + textoInvertido);
                }

                if (opcion.equals("4")) {
                    String orden1 = in.readLine();
                    if (orden1.equals("numero_primo")) {
                        System.out.println("Introduce el numero para comprobar si es primo o no: ");
                        out.println(sc.nextLine());
                    }

                    String numeroPrimo = in.readLine();
                    System.out.println(numeroPrimo);
                }
            }

            System.out.println("Desconectado de servidor");





        } catch (UnknownHostException e) {
            System.err.println("Error de conexion");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Error de conexion");
            throw new RuntimeException(e);
        }


    }
}
