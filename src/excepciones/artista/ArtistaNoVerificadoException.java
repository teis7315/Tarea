package excepciones.artista;

public class ArtistaNoVerificadoException extends RuntimeException {
    public ArtistaNoVerificadoException() {
    }

    public ArtistaNoVerificadoException(String message) {
        super(message);
    }
}
