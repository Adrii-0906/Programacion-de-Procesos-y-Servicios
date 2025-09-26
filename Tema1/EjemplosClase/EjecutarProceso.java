package Tema1.EjemplosClase;

public class EjecutarProceso {
    public static void main(String[] args) {
        //Procesos
        //ejemplo1();
        //ejemplo2();
        //ejemplo3();
        //ejemplo_runnable();
        //ejemploComunicacionBasico();
        ejemploSinc();
    }

    public static void ejemplo1(){
        try {
            ProcessBuilder pb = new ProcessBuilder("nautilus");
            Process proceso = pb.start();
            System.out.println("Se ha lanzado una terminal.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ejemplo2(){
        try {
            // Lazamos una aplicacion grafica
            ProcessBuilder pb = new ProcessBuilder("firefox");
            Process proceso = pb.start();
            System.out.println("Se ha lanzado una aplicacion grafica");
            int exitCode = proceso.waitFor();
            System.out.println("Firefox se cerro. Codigo de salida" + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ejemplo3() {
        try {
            System.out.println("== Ejecucion secuencial ==");
            long inicioSec = System.currentTimeMillis();

            new Contador("Contador 1").secuencial_run();
            new Contador("Contador 2").secuencial_run();

            long findSec = System.currentTimeMillis();
            System.out.println("Tiempo total secuencial: " + (findSec - inicioSec) / 1000.0 + "segundos");

            System.out.println(" == Ejecucion Concurrente ==");

            Contador h1 = new Contador("Contador 1");
            Contador h2 = new Contador("Contador 2");

            h1.start();
            h2.start();

            h1.join();
            h2.join();

        } catch (Exception e) {

        }
    }

    public static void ejemplo_runnable() {
        HIloR tarea1 = new HIloR("Hilo A");
        HIloR tarea2 = new HIloR("Hilo B");

        Thread h1 = new Thread(tarea1);
        Thread h2 = new Thread(tarea2);

        h1.start();
        h2.start();

        System.out.println("Fin del hilo principal...");
    }

    public static void ejemploComunicacionBasico() {
        Compartido recurso = new Compartido();

        Productor p = new Productor(recurso);
        Consumidor c = new Consumidor(recurso);

        p.start();
        c.start();
    }

    public static void ejemploSinc() {
        Recurso recurso = new Recurso();
        new ProductorCom(recurso).start();
        new ConsumidorCom(recurso).start();
    }
}
