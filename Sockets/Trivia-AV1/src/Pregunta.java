public class Pregunta {
    private String texto;
    private String[] opciones;
    private char respuestaCorrecta;

    public Pregunta(String texto, String opA, String opB, String opC, String opD, char respuestaCorrecta) {
        this.texto = texto;
        this.opciones = new String[]{opA, opB, opC, opD};
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getTexto() {
        return texto;
    }

    public String[] getOpciones() {
        return opciones;
    }

    public char getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public boolean esCorrecta(char respuesta) {
        return Character.toLowerCase(respuesta) == Character.toLowerCase(respuestaCorrecta);
    }
}
