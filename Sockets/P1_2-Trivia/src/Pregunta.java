public class Pregunta {
    private String enunciado;
    private String respuestaA;
    private String respuestaB;
    private String respuestaC;
    private String respuestaD;
    private String respuestaCorrecta;

    public Pregunta(String enunciado, String respuestaA, String respuestaB, String respuestaC, String respuestaD,
                    String respuestaCorrecta) {
        this.enunciado = enunciado;
        this.respuestaA = respuestaA;
        this.respuestaB = respuestaB;
        this.respuestaC = respuestaC;
        this.respuestaD = respuestaD;
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getRespuestaA() {
        return respuestaA;
    }

    public String getRespuestaB() {
        return respuestaB;
    }

    public String getRespuestaC() {
        return respuestaC;
    }

    public String getRespuestaD() {
        return respuestaD;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setRespuestaA(String respuestaA) {
        this.respuestaA = respuestaA;
    }

    public void setRespuestaB(String respuestaB) {
        this.respuestaB = respuestaB;
    }

    public void setRespuestaC(String respuestaC) {
        this.respuestaC = respuestaC;
    }

    public void setRespuestaD(String respuestaD) {
        this.respuestaD = respuestaD;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }




}