public class hilo extends Thread {

    private String nombre;


    public hilo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "hilo{" +
                "nombre='" + nombre + '\'' +
                '}';
    }

    public void secuencial_run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println(nombre);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void secuencial_run1() {
        for (int i = 1; i <= 2; i++) {
            System.out.println(nombre);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void secuencial_run2() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(nombre);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
