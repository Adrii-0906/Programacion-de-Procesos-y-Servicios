import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcesadorFicheros extends Thread{
    private final String ruta;


    int contadorLinea = 0;
    int info = 0;
    int error = 0;
    int warn = 0;


    public ProcesadorFicheros(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(ruta));

            String linea;
            while ((linea= br.readLine()) != null) {
                System.out.println(linea);
                contadorLinea++;
                String[] partes = linea.split(";");

                if (partes.length > 0) {
                    String parte = partes[0].trim();
                    if (parte.equals("INFO")) {
                        info++;
                    } else if (parte.equals("ERROR")) {
                        error++;
                    } else if (parte.equals("WARN")) {
                        warn++;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(this.getName() + " Número total de registros: " + contadorLinea); // Podemos también sumar todos los contadores :)
        System.out.println(this.getName() + "INFO = " + info);
        System.out.println(this.getName() +"WARN = " + warn);
        System.out.println(this.getName() + "ERROR = " + error);
    }
}
