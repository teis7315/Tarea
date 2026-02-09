package excepciones.descarga;

public class LimiteDescargasException extends Exception {
    public LimiteDescargasException() {
    }

    public LimiteDescargasException(String message) {
        super(message);
    }
}
