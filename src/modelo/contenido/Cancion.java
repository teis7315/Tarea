package modelo.contenido;

import enums.GeneroMusical;
import excepciones.contenido.ArchivoDeAudioNoEncontradoException;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.contenido.DuracionInvalidaException;
import excepciones.contenido.LetraNoDisponibleException;
import excepciones.descarga.ContenidoYaDescargadoException;
import interfaces.Descargable;
import interfaces.Reproducible;
import modelo.artistas.Album;
import modelo.artistas.Artista;

import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("unused")
public class Cancion extends Contenido implements Reproducible, Descargable {
    private String letra;
    private modelo.artistas.Artista artista;
    private modelo.artistas.Album album;
    private GeneroMusical genero;
    private String audioURL;
    private boolean explicit;
    private String ISRC;
    private boolean reproduciendo;
    private boolean pausado;
    private boolean descargado;

    public Cancion(String titulo, int duracionSegundos, modelo.artistas.Artista artista, GeneroMusical genero)
            throws DuracionInvalidaException {
        super(titulo, duracionSegundos);
        this.artista = artista;
        this.genero = genero;
        this.audioURL = "https://soundwave.com/audio/" + id + ".mp3";
        this.ISRC = generarISRC();
        this.reproduciendo = false;
        this.pausado = false;
        this.descargado = false;
        this.explicit = false;
        this.letra = null;
    }

    public Cancion(String titulo, int duracionSegundos, modelo.artistas.Artista artista, GeneroMusical genero,
                   String letra, boolean explicit) throws DuracionInvalidaException {
        super(titulo, duracionSegundos);
        this.artista = artista;
        this.genero = genero;
        this.audioURL = "https://soundwave.com/audio/" + id + ".mp3";
        this.ISRC = generarISRC();
        this.reproduciendo = false;
        this.pausado = false;
        this.descargado = false;
        this.explicit = explicit;
        this.letra = letra;
    }

    private String generarISRC() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void reproducir() throws ContenidoNoDisponibleException {
        if (!disponible) {
            throw new ContenidoNoDisponibleException("La canción no está disponible");
        }
        reproduciendo = true;
        reproducciones++;
    }

    @Override
    public void play() throws ContenidoNoDisponibleException {
        reproducir();
        reproduciendo = true;
        pausado = false;
        System.out.println("Reproduciendo: " + artista.getNombreArtistico() + " - " + titulo);
    }

    @Override
    public void pause() {
        if (reproduciendo) {
            pausado = true;
        }
    }

    @Override
    public void stop() {
        reproduciendo = false;
        pausado = false;
    }

    @Override
    public int getDuracion() {
        return duracionSegundos;
    }

    @Override
    public boolean descargar() throws ContenidoYaDescargadoException {
        if (descargado) {
            throw new ContenidoYaDescargadoException("La canción ya está descargada");
        }
        descargado = true;
        return true;
    }

    @Override
    public boolean eliminarDescarga() {
        if (descargado) {
            descargado = false;
            return true;
        }
        return false;
    }

    @Override
    public int espacioRequerido() {
        // Aproximadamente 1MB por minuto de audio a 128kbps
        int minutos = duracionSegundos / 60;
        return minutos > 0 ? minutos : 1;
    }

    public String obtenerLetra() throws LetraNoDisponibleException {
        if (letra == null || letra.isBlank()) {
            letra = null; // aseguramos que getLetra() devuelva null
            throw new LetraNoDisponibleException("La letra no está disponible para esta canción");
        }
        return letra;
    }


    public boolean esExplicit() {
        return explicit;
    }

    public void cambiarGenero(GeneroMusical nuevoGenero) {
        this.genero = nuevoGenero;
    }

    public void validarAudioURL() throws ArchivoDeAudioNoEncontradoException {
        if (audioURL == null || audioURL.isEmpty()) {
            throw new ArchivoDeAudioNoEncontradoException("URL de audio no encontrada");
        }
    }

    // Getters y Setters
    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public modelo.artistas.Artista getArtista() {
        return artista;
    }

    public void setArtista(modelo.artistas.Artista artista) {
        this.artista = artista;
    }

    public modelo.artistas.Album getAlbum() {
        return album;
    }

    public void setAlbum(modelo.artistas.Album album) {
        this.album = album;
    }

    public GeneroMusical getGenero() {
        return genero;
    }

    public void setGenero(GeneroMusical genero) {
        this.genero = genero;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public String getISRC() {
        return ISRC;
    }

    public boolean isReproduciendo() {
        return reproduciendo;
    }

    public boolean isPausado() {
        return pausado;
    }

    public boolean isDescargado() {
        return descargado;
    }

    public void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }

    @Override
    public String toString() {
        return titulo + " - " + artista.getNombreArtistico() + " [" + getDuracionFormateada() + "]";
    }
    @Override
    public ArrayList<String> getEtiquetas() {
        ArrayList<String> etiquetas = new ArrayList<>(super.getEtiquetas());
        etiquetas.add(genero.name()); // agrega el género musical como etiqueta
        return etiquetas;
    }

}
