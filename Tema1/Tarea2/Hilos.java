import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Hilos extends Thread{

    // Creamos los atributos del hilo
    private char vocal;
    private String entrada;
    private int resultado;

    // Creamos el constructor
    public Hilos(char vocal, String entrada) {
        this.vocal = vocal;
        this.entrada = entrada;
    }

    // Aqui generamos el get del resultado
    public int getResultado() {
        return resultado;
    }

    // Creamos el metodo run
    @Override
    public void run() {
        // Iniciamos el resultado a 0
        resultado = 0;

        // Aqui lo que hacemos es leer el fichero entrada y vamos leyendo las vocales y las sumamos a resultado
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
