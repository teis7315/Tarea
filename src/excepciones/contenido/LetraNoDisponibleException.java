package excepciones.contenido;

public class LetraNoDisponibleException extends Exception {
    public LetraNoDisponibleException() {
    }

    public LetraNoDisponibleException(String message) {
        super(message);
    }
}
