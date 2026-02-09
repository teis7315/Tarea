package excepciones.artista;

public class AlbumYaExisteException extends RuntimeException {
    public AlbumYaExisteException() {
    }

    public AlbumYaExisteException(String message) {
        super(message);
    }
}
