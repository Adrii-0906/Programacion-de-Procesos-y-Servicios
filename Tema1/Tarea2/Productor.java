public class Productor implements Runnable{

    // Creamos el atributo buffer de la clse recurso
    private final Recurso buffer;

    public Productor(Recurso buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        // Hacemos un recorrido para que nos cree 10 numeros, por ejemplo.
        for (int i = 1; i < 10; i++) {
            try {
                // Ahora llamamos al metodo agregarNumeros de la clase recursos
                buffer.agregarNumeros(i);
                // Hacemos que tarde un poco en producir los numeros.
                Thread.sleep(500);
            }catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }


}
