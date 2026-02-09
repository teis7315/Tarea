package excepciones.usuario;

public class LimiteDiarioAlcanzadoException extends Exception {
    public LimiteDiarioAlcanzadoException() {
    }

    public LimiteDiarioAlcanzadoException(String message) {
        super(message);
    }
}
