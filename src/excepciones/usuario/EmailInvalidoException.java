package excepciones.usuario;

public class EmailInvalidoException extends Exception {
    public EmailInvalidoException() {
    }

    public EmailInvalidoException(String message) {
        super(message);
    }
}
