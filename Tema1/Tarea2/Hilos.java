import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Hilos extends Thread{

    private char vocal;
    private String entrada;
    private int resultado;

    public Hilos(char vocal, String entrada) {
        this.vocal = vocal;
        this.entrada = entrada;
    }

    public int getResultado() {
        return resultado;
    }

    @Override
    public void run() {

        resultado = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(entrada));

            int c;

            while ((c = br.read()) != -1) {
                if (Character.toLowerCase((char) c) == vocal) {
                    resultado++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
