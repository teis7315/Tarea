package excepciones.artista;

public class AlbumCompletoException extends RuntimeException {
    public AlbumCompletoException() {
    }

    public AlbumCompletoException(String message) {
        super(message);
    }
}
