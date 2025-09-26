package Tema1.EjemplosClase;

public class HIloR implements Runnable{

    private String nombre;

    public HIloR(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        for (int i = 0; i <= 5; i++) {
            System.out.println(nombre + " ejecuta paso " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
