package modelo.artistas;

import excepciones.contenido.DuracionInvalidaException;
import excepciones.artista.AlbumCompletoException;
import excepciones.playlist.CancionNoEncontradaException;
import enums.GeneroMusical;
import modelo.contenido.Cancion;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Album {
    private static final int MAX_CANCIONES = 20;
    private String id;
    private String titulo;
    private Artista artista;
    private Date fechaLanzamiento;
    private ArrayList<Cancion> canciones;
    private String portadaURL;
    private String discografica;
    private String tipoAlbum;

    public Album(String titulo, Artista artista, Date fechaLanzamiento) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.artista = artista;
        this.fechaLanzamiento = fechaLanzamiento;
        this.canciones = new ArrayList<>();
        this.portadaURL = "https://soundwave.com/covers/" + id + ".jpg";
        this.discografica = "";
        this.tipoAlbum = "Álbum";
    }

    public Album(String titulo, Artista artista, Date fechaLanzamiento, String discografica, String tipoAlbum) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.artista = artista;
        this.fechaLanzamiento = fechaLanzamiento;
        this.canciones = new ArrayList<>();
        this.portadaURL = "https://soundwave.com/covers/" + id + ".jpg";
        this.discografica = discografica;
        this.tipoAlbum = tipoAlbum;
    }

    public Cancion crearCancion(String titulo, int duracionSegundos, GeneroMusical genero)
            throws AlbumCompletoException, DuracionInvalidaException {
        if (canciones.size() >= MAX_CANCIONES) {
            throw new AlbumCompletoException("El álbum ha alcanzado el límite de " + MAX_CANCIONES + " canciones");
        }
        Cancion cancion = new Cancion(titulo, duracionSegundos, artista, genero);
        cancion.setAlbum(this);
        canciones.add(cancion);
        artista.publicarCancion(cancion);
        return cancion;
    }

    public Cancion crearCancion(String titulo, int duracionSegundos, GeneroMusical genero, String letra, boolean explicit)
            throws AlbumCompletoException, DuracionInvalidaException {
        if (canciones.size() >= MAX_CANCIONES) {
            throw new AlbumCompletoException("El álbum ha alcanzado el límite de " + MAX_CANCIONES + " canciones");
        }
        Cancion cancion = new Cancion(titulo, duracionSegundos, artista, genero, letra, explicit);
        cancion.setAlbum(this);
        canciones.add(cancion);
        artista.publicarCancion(cancion);
        return cancion;
    }

    public void eliminarCancion(int posicion) throws CancionNoEncontradaException {
        if (posicion < 1 || posicion > canciones.size()) {
            throw new CancionNoEncontradaException("Posición " + posicion + " no válida");
        }
        canciones.remove(posicion - 1);
    }

    public void eliminarCancion(Cancion cancion) throws CancionNoEncontradaException {
        if (!canciones.remove(cancion)) {
            throw new CancionNoEncontradaException("La canción no existe en este álbum");
        }
    }

    public int getDuracionTotal() {
        int total = 0;
        for (Cancion c : canciones) {
            total += c.getDuracionSegundos();
        }
        return total;
    }

    public String getDuracionTotalFormateada() {
        int segundos = getDuracionTotal();
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int secs = segundos % 60;
        if (horas > 0) {
            return String.format("%d:%02d:%02d", horas, minutos, secs);
        }
        return String.format("%d:%02d", minutos, secs);
    }

    public int getNumCanciones() {
        return canciones.size();
    }

    public void ordenarPorPopularidad() {
        canciones.sort((c1, c2) -> Integer.compare(c2.getReproducciones(), c1.getReproducciones()));
    }

    public Cancion getCancion(int posicion) throws CancionNoEncontradaException {
        if (posicion < 1 || posicion > canciones.size()) {
            throw new CancionNoEncontradaException("Posición " + posicion + " no válida");
        }
        return canciones.get(posicion - 1);
    }

    public int getTotalReproducciones() {
        int total = 0;
        for (Cancion c : canciones) {
            total += c.getReproducciones();
        }
        return total;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Artista getArtista() {
        return artista;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public Date getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(Date fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public ArrayList<Cancion> getCanciones() {
        return new ArrayList<>(canciones);
    }

    public String getPortadaURL() {
        return portadaURL;
    }

    public void setPortadaURL(String portadaURL) {
        this.portadaURL = portadaURL;
    }

    public String getDiscografica() {
        return discografica;
    }

    public void setDiscografica(String discografica) {
        this.discografica = discografica;
    }

    public String getTipoAlbum() {
        return tipoAlbum;
    }

    public void setTipoAlbum(String tipoAlbum) {
        this.tipoAlbum = tipoAlbum;
    }

    public int getMaxCanciones() {
        return MAX_CANCIONES;
    }

    @Override
    public String toString() {
        return "Album{" + "titulo='" + titulo + '\'' + ", artista=" + artista.getNombreArtistico() +
               ", numCanciones=" + canciones.size() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album album = (Album) obj;
        return id.equals(album.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Cancion agregarCancion(String titulo, int duracion, Artista artista, GeneroMusical genero) {
    }
}
