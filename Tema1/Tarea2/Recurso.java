import java.util.ArrayList;
import java.util.List;

public class Recurso {

    // Creamos los atributos de la clase recurso

    private final List<Integer> listaNumeros = new ArrayList<>();
    private final int capacidad;

    // Creamos su constructor


    public Recurso(int capacidad) {
        this.capacidad = capacidad;
    }

    // Ahora creamos el metodo donde el productor va ir agragando numeros
    public synchronized void agregarNumeros(int numero) {
        try {
            // Si el buffer donde se guardan los numeros esta lleno hacemos un wait para pararlo hasta que el consumidor no quite ningun numero.
            while (listaNumeros.size() == capacidad) {
                System.out.println("El Buffer esta lleno. Productor en espera");
                 wait();
            }

            // Si tenemos espacio para anadir numeros los anadimos al Buffer
            listaNumeros.add(numero);
            System.out.println("El productor ha anadido un numero: " + numero);
            System.out.println("Ahora mismo el Buffer tiene: " + capacidad);

            // Ahora como hemos anadido un prodcuto tenemos que avisar a todos los hilos que tenemos esperando.
            notifyAll();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public synchronized int sacarNumeros() throws InterruptedException {
            // Si tenemos el Buffer vacio tenemos que esperar a que el productor agregue algun numero para que el consumidos los pueda sacar

            while (listaNumeros.isEmpty()) {
                System.out.println("El Buffer esta vacio.");
                wait();
            }

            // Si el Buffer ya no esta vacio lo que hacemos es sacar el primer numero

            int numero = listaNumeros.remove(0);
            System.out.println("El consumidor saco el numero: " + numero);

            // Ahora tenemos que avisar a los demas hilos y al productor que hemos sacado un numero
            notifyAll();

            return numero;
    }
}
