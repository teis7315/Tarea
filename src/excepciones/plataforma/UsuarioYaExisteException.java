package excepciones.plataforma;

public class UsuarioYaExisteException extends Exception {
    public UsuarioYaExisteException() {
    }

    public UsuarioYaExisteException(String message) {
        super(message);
    }
}
