package excepciones.contenido;

public class ContenidoNoDisponibleException extends Exception {
    public ContenidoNoDisponibleException() {
    }

    public ContenidoNoDisponibleException(String message) {
        super(message);
    }
}
