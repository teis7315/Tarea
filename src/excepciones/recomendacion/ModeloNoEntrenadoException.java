package excepciones.recomendacion;

public class ModeloNoEntrenadoException extends RecomendacionException {
    public ModeloNoEntrenadoException() {
    }

    public ModeloNoEntrenadoException(String message) {
        super(message);
    }
}
