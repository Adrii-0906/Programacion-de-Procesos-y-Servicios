package Tema1.EjemplosClase;

public class Recurso {
    private int valor;
    private  boolean disponible = false;

    public synchronized void producir(int nuevoValor) {
        while (disponible) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        valor = nuevoValor;
        disponible = true;
        System.out.println("Productor produjo: " + valor);
        notify();
    }

    public synchronized int consumir() {
        while (!disponible) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        disponible = false;
        System.out.println("Consumidor consumio: " + valor);
        notify();
        return valor;
    }
}
