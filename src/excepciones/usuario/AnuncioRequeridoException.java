package excepciones.usuario;

public class AnuncioRequeridoException extends Exception {
    public AnuncioRequeridoException() {
    }

    public AnuncioRequeridoException(String message) {
        super(message);
    }
}
