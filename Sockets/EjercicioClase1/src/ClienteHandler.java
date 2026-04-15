import java.io.*;
import java.net.*;

public class ClienteHandler extends Thread {
    private Socket cliente;

    public ClienteHandler(Socket socket) {
        this.cliente = socket; // Recibe el socket de la conexión
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream()));
            PrintWriter out = new PrintWriter(
                    cliente.getOutputStream(), true);
        ){
            String operacion;
            while((operacion=in.readLine())!=null && !operacion.equals("EXIT")){
                String cadena=null;

                switch (operacion){
                    case "CIFRAR":
                    case "DESCIFRAR":
                        out.println("Pasame la cadena");
                        cadena=in.readLine();
                        break;
                    default:
                        out.println("Operación no permitida");
                }

                out.println(cifrar_descifrar(cadena,operacion,3));
            }

            out.println("ADIOS");


        }catch (IOException e){
            System.err.println("Error de conexión");
        }catch (ArithmeticException a){
            System.err.println("División sobre 0"+a.getMessage());
        }

    }

    public static String cifrar_descifrar(String cadena,String operacion,int desplazamiento){
        int rango=('z'-'a')+1;

        String resultado="";
        char c;
        switch (operacion){
            case "CIFRAR":
                for (int i=0;i<cadena.length();i++){
                    c=cadena.charAt(i);

                    if (c>='a' && c<='z'){
                        resultado+= (char) (((c-'a')+desplazamiento)%rango + 'a');
                    }else if (c>='A' && c<='Z'){
                        resultado+= (char) (((c-'A')+desplazamiento)%rango + 'A');
                    }else{
                        resultado+=c;
                    }
                }
                break;
            case "DESCIFRAR":
                for (int i=0;i<cadena.length();i++){
                    c=cadena.charAt(i);

                    if (c>='a' && c<='z'){
                        resultado+= (char) (((c-'a')-desplazamiento+rango)%rango + 'a');
                    }else if (c>='A' && c<='Z'){
                        resultado+= (char) (((c-'A')-desplazamiento+rango)%rango + 'A');
                    }else{
                        resultado+=c;
                    }
                }
                break;
        }
        return resultado;
    }
}