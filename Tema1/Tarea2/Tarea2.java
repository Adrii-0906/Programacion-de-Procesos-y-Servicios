import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Tarea2 {
    public static void main(String[] args) {
        //ejercicio1Secuencial();
        //ejercicio1Concurrente();
        ejercicio2();
    }

    public static void ejercicio1Secuencial() {

        // Hacemos una lista donde metemos las vocales que queremos contar del texto
        String[] vocales = {"a", "e", "i", "o", "u"};


        // Hacemos una lista con los nombres de los ficheros que vamos a crear al ejecutar el programa
        String[] ficheros = {
            "ficheroA.txt",
            "ficheroE.txt",
            "ficheroI.txt",
            "ficheroO.txt",
            "ficheroU.txt"
        };

        // Hacemos un contador para hacer de las vocales que aparecen
        int[] contadorVocales = {0, 0, 0, 0, 0};

        try {
            // Leemos el fichero entrada.txt
            BufferedReader br = new BufferedReader(new FileReader("entrada.txt"));

            String linea;

            while ((linea = br.readLine()) != null) {
                String[] lineasSep = linea.split("\\s");

                // Aqui separamos las lineas para leerlas y contar los caracteres
                for (int i = 0; i < lineasSep.length; i++) {
                    char[] palabraSep = lineasSep[i].toCharArray();

                    // Aqui en el segundo bucle ya vamos lleyendo caracter por caractes y los vamos anadiendo a las listas de caracteres
                    for (int j = 0; j < palabraSep.length; j++) {
                        if (comprobarA(Character.toLowerCase(palabraSep[j]))) {
                            contadorVocales[0]++;
                        } else if (comprobarE(Character.toLowerCase(palabraSep[j]))) {
                            contadorVocales[1]++;
                        } else if (comprobarI(Character.toLowerCase(palabraSep[j]))) {
                            contadorVocales[2]++;
                        } else if (comprobarO(Character.toLowerCase(palabraSep[j]))) {
                            contadorVocales[3]++;
                        } else if (comprobarU(Character.toLowerCase(palabraSep[j]))) {
                            contadorVocales[4]++;
                        }
                    }
                }
            }

            br.close();

            // Ahora escribimos en los ficheros cunatas vocales hay en el fichero entrada
            for (int i = 0; i < ficheros.length; i++) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(ficheros[i]));

                bw.write("Veces totales " + vocales[i] + ": " +contadorVocales[i]);
                bw.close();
            }

            System.out.println("Informacion recuperada de los ficheros de las vocales");

            int[] contadoresFicheros = new int[5];

            int sumaTotal = 0;

            // Ahora vamos lleyendo los ficheros
            for (int i = 0; i < ficheros.length; i++) {
                br = new BufferedReader(new FileReader(ficheros[i]));

                // Aqui le decimos que cuando llegue a los ":" lea la cifra
                while ((linea = br.readLine()) != null) {
                    String[] lineaSep = linea.split(":");

                    // Lo pasamos a valor entero y hacemos la suma
                    contadoresFicheros[i] = Integer.parseInt(lineaSep[1].trim());

                    sumaTotal += contadoresFicheros[i];

                    System.out.println("Vocal " + vocales[i] + " aparece " + contadoresFicheros[i] + " veces.");
                }
                br.close();
            }

            System.out.println("La aparicion total de todas las vocales es: " + sumaTotal);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Aqui hacemos unos metodos para poder comprobar cada vocal

    private static boolean comprobarA(char lineaSep) {
        return lineaSep == 'a' || lineaSep == 'á' || lineaSep == 'ä';
    }

    private static boolean comprobarE(char lineaSep) {
        return lineaSep == 'e' || lineaSep == 'é' || lineaSep == 'ë';
    }

    private static boolean comprobarI(char lineaSep) {
        return lineaSep == 'i' || lineaSep == 'í' || lineaSep == 'ï';
    }

    private static boolean comprobarO(char lineaSep) {
        return lineaSep == 'o' || lineaSep == 'ó' || lineaSep == 'ö';
    }

    private static boolean comprobarU(char lineaSep) {
        return lineaSep == 'u' || lineaSep == 'ú' || lineaSep == 'ü';
    }


    public static void ejercicio1Concurrente() {
        /*
        Crear un programa que sea capaz de contar cuántas vocales hay en un fichero.
        El programa padre debe lanzar cinco hilos hijo, donde cada uno de ellos se
        ocupará de contar una vocal concreta (que puede ser minúscula o mayúscula).
        Cada subproceso que cuenta vocales deberá dejar el resultado en un fichero. El
        programa padre se ocupará de recuperar los resultados de los ficheros, sumar
        todos los subtotales y mostrar el resultado final en pantalla.
         */

        // Indicamos el fichero que queremos leer

        String entrada = "entrada.txt";

        // Inicializamos los hilos y le indicamos la vocal y el fichero que queremos que lea
        Hilos hA = new Hilos('a', entrada);
        Hilos hE = new Hilos('e', entrada);
        Hilos hI = new Hilos('i', entrada);
        Hilos hO = new Hilos('o', entrada);
        Hilos hU = new Hilos('u', entrada);

        // Iniciamos los hilos
        hA.start();
        hE.start();
        hI.start();
        hO.start();
        hU.start();

        // Aqui inidicamos que hasta que uno no acabe no empiece el otro
        try {
            hA.join();
            hE.join();
            hI.join();
            hO.join();
            hU.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Aqui creamos un contandor donde sumamos todos los hilos para ver el total de vocales
        int total = 0;

        System.out.println("vocal A: " + hA.getResultado());
        System.out.println("vocal E: " + hE.getResultado());
        System.out.println("vocal I: " + hI.getResultado());
        System.out.println("vocal O: " + hO.getResultado());
        System.out.println("vocal U: " + hU.getResultado());

        total = hA.getResultado() + hE.getResultado() + hI.getResultado() + hO.getResultado() + hU.getResultado();
        System.out.println("Total: " + total);
    }

    public static void ejercicio2() {
        /*
        Crea un programa en Java que simule el problema Productor–Consumidor,
        pero con un buffer de tamaño limitado (por ejemplo, 3 posiciones):
            - El Productor debe ir generando números y guardarlos en el buffer.
            - El Consumidor debe ir sacando esos números del buffer y mostrándolos
              por pantalla.
            - Si el buffer está lleno, el Productor debe esperar hasta que el
              Consumidor libere espacio.
            - Si el buffer está vacío, el Consumidor debe esperar hasta que el
              Productor produzca algo.
            - Usa wait() y notifyAll() para coordinar la comunicación entre hilos.

            Inicialmente hacer el ejercicio con 1 productor y 1 consumidor.
            Posteriormente, ampliar a 2 y 2.
         */

        // Hacemos que el buffer tenga una capacidad maxima de 3 numeros
        int capacidadBuffer = 3;
        Recurso buffer = new Recurso(capacidadBuffer);

        System.out.println(" == INICIAMOS EL BUFFER ==");
        System.out.println("Capacidad actual: " + capacidadBuffer);

        // Vamos a inicializar las clases productor y consumidor, es decir, crear los hilos

        Thread productor = new Thread(new Productor(buffer), "Productor");
        Thread consumidor = new Thread(new Consumidor(buffer), "Consumidor");

        // Ahora los iniciamos
        productor.start();
        consumidor.start();

        try {
            productor.join();
            consumidor.join();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        
    }
}
