import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        System.out.println("Servidor iniciado y esperando...");
        try {
            ServerSocket server = new ServerSocket(1234);
            Socket cliente = server.accept();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream())
            );
            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);

            System.out.println("Cliente conectado");
            boolean login = false;
            int intentos = 0;

            while (!login) {
                if (intentos == 4) {
                    System.out.println("Login incorrecto");
                    out.println("LOGIN_BAD");
                    break;
                }

                System.out.println("Usuario: ");
                out.println("Usuario");
                String usuarioRecibido = in.readLine();

                System.out.println("Contrasena: ");
                out.println("password");
                String contrasenaRecibida = in.readLine();


                if (usuarioRecibido.equals("admin") && contrasenaRecibida.equals("1234") && intentos <= 3) {
                    System.out.println("Login Correcto");
                    out.println("LOGIN_OK");
                    login = true;
                } else {
                    System.out.println("Credenciales incorrectas, te quedan " + (3 - intentos) + " intentos");
                    out.println("CREDENTIALS_BAD");
                    intentos++;
                }
            }

            if (login) {
                System.out.println("Menu:" +
                        "\n1.- Sumar" +
                        "\n2.- Contador" +
                        "\n3.- Invierte" +
                        "\n4.- EsPrimo" +
                        "\n5.- Salir");

                String opcion;
                while ((opcion = in.readLine()) != null && !opcion.equals("5")) {
                    System.out.println("Opcion recibida del cliente: " + opcion);

                    switch (opcion) {
                        case "1":
                            System.out.println("Introduce el primer numero: ");
                            out.println("num1_sumar");
                            String numeroRecibido = in.readLine();

                            System.out.println("Introduce el segundo numero: ");
                            out.println("num2_sumar");
                            String numero2Recibido = in.readLine();

                            double suma = (Double.parseDouble(numeroRecibido) + Double.parseDouble(numero2Recibido));

                            System.out.println("El resultado de la suma es: " + suma);
                            out.println("El resultado de la suma es: " + suma);
                            break;
                        case "2":
                            System.out.println("Introduce la palabra para contar sus vocales: ");
                            out.println("palabra_contador");
                            String palabraContador = in.readLine();
                            int contadorVocales = 0;
                            String palabraMin = palabraContador.toLowerCase();

                            for (int i = 0; i < palabraMin.length(); i++) {
                                char c = palabraContador.charAt(i);
                                if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                                    contadorVocales++;
                                }
                            }

                            System.out.println("Numero de vocales en la palabra " + palabraContador + "; " + contadorVocales);
                            out.println(contadorVocales);
                            break;
                        case "3":
                            System.out.println("Introduce el texto para devolverlo invertido: ");
                            out.println("texto_invertir");
                            String textoInvertir = in.readLine();

                            String textoInvertido = "";
                            for (int i = textoInvertir.length() - 1; i >=0; i--) {
                                textoInvertido += textoInvertir.charAt(i);
                            }
                            System.out.println("El texto invertido es: " + textoInvertido);
                            out.println(textoInvertido);
                            break;
                        case "4":
                            System.out.println("Introduce el numero para comprobar si es primo: ");
                            out.println("numero_primo");
                            String numeroPrimo = in.readLine();

                            int n = Integer.parseInt(numeroPrimo);

                            if (esPrimo(n)) {
                                System.out.println("El numero " + n + " es primo");
                                out.println("El numero " + n + " es primo");
                            } else {
                                System.out.println("El numero " + n + " no es primo");
                                out.println("El numero " + n + " no es primo");
                            }
                            break;
                        case "5":
                            break;
                        default:
                            System.out.println("Opcion no valida");
                    }
                }
            }

            System.out.println("Cerrando sesion con el cliente");

        } catch (IOException e) {
            System.err.println("Error de conexion");
            throw new RuntimeException();
        }
    }

    public static boolean esPrimo(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
