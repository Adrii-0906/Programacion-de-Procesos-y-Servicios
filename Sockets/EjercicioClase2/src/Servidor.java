import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    public static void main(String[] args) {
        System.out.println("Servidor arrancado y esperando...");

        try(ServerSocket server = new ServerSocket(1234);
            Socket cliente = server.accept();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream()));
            PrintWriter out = new PrintWriter(
                    cliente.getOutputStream(), true);
            ) {
            System.out.println("Cliente conectado");
            List<Integer> numeros = new ArrayList<>();

            String operacion;

            while((operacion = in.readLine()) != null && !operacion.equals("6")) {
                switch (operacion) {
                    case "1":
                        // Le decimos que al cliente que tenemos que hacer
                        out.println("Agregar");
                        String numeroAgregar = in.readLine();
                        numeros.add(Integer.parseInt(numeroAgregar));
                        System.out.println("Numero " + numeroAgregar + " agregado a la lista.");
                        break;
                    case "2":
                        if (numeros.isEmpty()) {
                            out.println("La lista se encuentra vacia. Agregue un numero para poder verla");
                        } else {
                            out.println("Numeros de la lista " + numeros.toString());
                        }
                        break;
                    case "3":
                        if (numeros.isEmpty()) {
                            out.println("No se puede calcular la media, por que la lista esta vacia. Introduce un numero para poder calcular la media");
                        } else {
                            int total = 0;
                            for (int n: numeros) {
                                total += n;
                            }
                            out.println("La media de la Lista es: " + total / numeros.size());
                        }
                        break;
                    case "4":
                        if (numeros.isEmpty()) {
                            out.println("No podemos el encontrar el numero maximo, porque la lista esta vacia. Introduce un numero para poder encontrar el numero maximo");
                        } else {
                            int mayor = numeros.get(0);
                            for (int n : numeros) {
                                if (n > mayor) {
                                    mayor = n;
                                }
                            }
                            out.println("El numero maximos que hay en la lista es: " + mayor);
                        }
                        break;
                    case "5":
                        numeros.clear();
                        out.println("La lista se ha vacio correctamente");
                        break;
                    default:
                        out.println("Operacion no valida. Intentalo de nuevo");
                }
            }
            out.println("Cerrando sesion...");

        } catch (IOException e) {
            System.err.println("Error de conexion");
        }
    }
}
