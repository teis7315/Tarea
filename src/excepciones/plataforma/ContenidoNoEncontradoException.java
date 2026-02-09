package excepciones.plataforma;

public class ContenidoNoEncontradoException extends Exception {
    public ContenidoNoEncontradoException() {
    }

    public ContenidoNoEncontradoException(String message) {
        super(message);
    }
}
