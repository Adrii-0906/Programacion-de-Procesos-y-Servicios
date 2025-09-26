import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Multiprogramacion extends Thread{

    public static void main(String[] args) {
        //ejercicio1();
        //ejercicio2();
        //ejercicio3Secuencial();
        ejercicio3Concurrente();

    }

    public static void ejercicio1() {
        /*
        Crea un programa en Java que crea un hilo y abra firefox o un programa que ya tenéis
        abierto y se queda esperando hasta que se cierre. Una vez se cierra escribe un mensaje en
        pantalla de que el programa ha sido cerrado y acaba la ejecución.
        Nota: Si descubrís algún problema investigad la causa y también cual es una posible
        solución.
         */

        try {
            ProcessBuilder pb = new ProcessBuilder("firefox");
            Process proceso = pb.start();
            System.out.println("Se ha lanzado la aplicacion");

            int exitCode = proceso.waitFor();
            System.out.println("La aplicacion se a cerrado correctamente");
        }catch (Exception e) {
            e.printStackTrace();
        }

        /*
        Al abrir el firefox si ya tenemos una pestana abierta de antemano, se abre otra pestana con el mismo pid, lo que puede pasar es que al ejecutarlo el sistema reconozca que este ya esta iniciado y el tener el mismo pid lo cierra directamente
         */

    }

    public static void ejercicio2() {
        /*
        Crea 3 hilos con las siguientes tareas:
            - Hilo A: imprime “Descargando datos…” cada 2 segundos (3 veces).
            - Hilo B: imprime “Procesando…” cada 3 segundos (2 veces).
            - Hilo C: imprime “Guardando…” cada 1 segundo (5 veces).
        El programa principal debe esperar que terminen los 3 hilos y muestren el siguiente mensaje: “Todas las tareas finalizadas”.
         */
        class hilo1 extends Thread {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.println("Descargando datos...");
                    try {
                        Thread.sleep(2000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        class hilo2 extends Thread {
            public void run() {
                for (int i = 0; i < 2; i++) {
                    System.out.println("Procesando...");
                    try {
                        Thread.sleep(3000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        class hilo3 extends Thread {
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println("Guardando...");
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Thread hiloA = new hilo1();
        Thread hiloB = new hilo2();
        Thread hiloC = new hilo3();

        hiloA.start();
        hiloB.start();
        hiloC.start();

        try {
            hiloA.join();
            hiloB.join();
            hiloC.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Todas las tareas finalizadas");

    }


    public static void ejercicio3Secuencial() {

        /*
        Lea un fichero de texto donde cada línea tiene el formato:
        nivel;mensaje
        Calcule y muestre por pantalla:
            - El número total de líneas del fichero.
            - El número de registros por nivel (INFO, WARN, ERROR).
        Implemente dos versiones:
            - Secuencial: procesa los ficheros uno a uno.
            - Concurrente: cada fichero se procesa en un hilo independiente.
        Mida y compare el tiempo de ejecución de ambas versiones.
         */


        File file = new File("fichero_entrada.txt");

        int contadorLinea = 0;
        int info = 0;
        int error = 0;
        int warn = 0;

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));

            String linea;
            while ((linea= br.readLine()) != null) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Numero total de registros: " + contadorLinea);
        System.out.println("INFO: " + info);
        System.out.println("ERROR: " + error);
        System.out.println("WARN: " + warn);
        
    }

    public static void ejercicio3Concurrente() {
        String[] rutasFicheros = {
                "fichero_entrada.txt",
                "fichero_entrada1.txt",
                "fichero_entrada2.txt"
        };

        ProcesadorFicheros[] hilos = new ProcesadorFicheros[rutasFicheros.length];
        for (int i = 0; i < rutasFicheros.length; i++) {
            hilos[i] = new ProcesadorFicheros(rutasFicheros[i]);
            hilos[i].start();
        }
        try {
            for (ProcesadorFicheros h : hilos) {
                h.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
