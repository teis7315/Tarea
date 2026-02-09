package excepciones.contenido;

public class ArchivoDeAudioNoEncontradoException extends Exception {
    public ArchivoDeAudioNoEncontradoException() {
    }

    public ArchivoDeAudioNoEncontradoException(String message) {
        super(message);
    }
}
