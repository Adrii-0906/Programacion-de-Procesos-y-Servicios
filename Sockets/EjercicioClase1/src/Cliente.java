import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        System.out.println("Cliente conectado");

        try (
                Socket socket = new Socket("192.168.14.16", 1234);
                PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

                BufferedReader teclado = new BufferedReader(
                new InputStreamReader(System.in));
            ) {


            String opcion;
            System.out.println("Elige la opcion: CIFRAR/DESCIFRAR/EXIT");
            String respuesta;
            while((opcion=teclado.readLine())!=null && !opcion.equals("EXIT")) {

                switch (opcion) {
                    case "CIFRAR":
                    case "DESCIFRAR":
                        out.println(opcion);
                        respuesta=in.readLine();
                        if (respuesta.equals("Pasame la cadena")){
                            System.out.println("Introduzca la cadena");
                            out.println(teclado.readLine());
                            System.out.println(opcion + ": " + in.readLine());
                        }else{
                            System.out.println(respuesta);
                        }
                        break;
                    default:

                }
                System.out.println("Elige la opcion: CIFRAR/DESCIFRAR/EXIT");
            }
            out.println("EXIT");
            String mensajeDespedida = in.readLine();
            System.out.println(mensajeDespedida);
        }catch (IOException e){
            System.err.println("Error en conexión");
        }catch (ArithmeticException a){
                System.err.println("División sobre 0"+a.getMessage());
        }
    }
}