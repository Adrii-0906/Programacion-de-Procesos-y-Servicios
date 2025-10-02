public class Consumidor implements Runnable {

    // Creamos el atributo Buffer de la clase Recurso
    private final Recurso buffer;

    public Consumidor(Recurso buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            try {
                // Ahora a traver del atributo buffer llamamos al metodo sacarNumeros de la clase Recurso
                int numero = buffer.sacarNumeros();
                // Vamos hacer que entre numero y numero haya un tiempo de espera
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }
}
