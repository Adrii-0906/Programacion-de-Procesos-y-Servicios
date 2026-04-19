import java.io.BufferedReader;
import java.io.IOException;

public class ReceptorMensajes extends Thread {
    BufferedReader in;

    public ReceptorMensajes(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message=in.readLine())!=null){
                System.out.println(message);
            }
            System.out.println("Servidor finalizado");
        }catch (IOException e){
            System.out.println("Error"+e.getMessage());
        }


    }

}
