package modelo.contenido;

import modelo.artistas.Creador;
import enums.CategoriaPodcast;
import excepciones.contenido.*;
import excepciones.descarga.ContenidoYaDescargadoException;
import interfaces.Descargable;
import interfaces.Reproducible;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Podcast extends Contenido implements Reproducible, Descargable {
    private final Creador creador;
    private int numeroEpisodio;
    private int temporada;
    private String descripcion;
    private CategoriaPodcast categoria;
    private final ArrayList<String> invitados;
    private String transcripcion;
    private boolean reproduciendo;
    private boolean pausado;
    private boolean descargado;

    public Podcast(String titulo, int duracionSegundos, Creador creador, int numeroEpisodio,
                   int temporada, CategoriaPodcast categoria) throws DuracionInvalidaException {
        super(titulo, duracionSegundos);
        if (numeroEpisodio <= 0 || temporada <= 0) {
            throw new DuracionInvalidaException("Número de episodio y temporada deben ser mayores a 0");
        }
        this.creador = creador;
        this.numeroEpisodio = numeroEpisodio;
        this.temporada = temporada;
        this.categoria = categoria;
        this.descripcion = "";
        this.invitados = new ArrayList<>();
        this.transcripcion = "";
        this.reproduciendo = false;
        this.pausado = false;
        this.descargado = false;
    }

    public Podcast(String titulo, int duracionSegundos, Creador creador, int numeroEpisodio,
                   int temporada, CategoriaPodcast categoria, String descripcion) throws DuracionInvalidaException {
        this(titulo, duracionSegundos, creador, numeroEpisodio, temporada, categoria);
        if (descripcion != null) this.descripcion = descripcion;
    }

    @Override
    public void reproducir() throws ContenidoNoDisponibleException {
        if (!disponible) {
            throw new ContenidoNoDisponibleException("El podcast no está disponible");
        }
        reproduciendo = true;
        pausado = false;
        reproducciones++;
    }

    @Override
    public void play() throws ContenidoNoDisponibleException {
        reproducir();
    }

    @Override
    public void pause() {
        if (reproduciendo) pausado = true;
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
        if (descargado) throw new ContenidoYaDescargadoException("El podcast ya está descargado");
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
        return Math.max(1, duracionSegundos / 120); // 0.5MB por minuto
    }

    public void agregarInvitado(String nombre) {
        if (nombre != null && !invitados.contains(nombre)) invitados.add(nombre);
    }

    public boolean esTemporadaNueva() {
        return temporada > 1;
    }

    public String obtenerTranscripcion() throws TranscripcionNoDisponibleException {
        if (transcripcion == null || transcripcion.isBlank()) {
            throw new TranscripcionNoDisponibleException("Transcripción no disponible para este episodio");
        }
        return transcripcion;
    }

    public void validarEpisodio() throws EpisodioNoEncontradoException {
        if (numeroEpisodio <= 0 || temporada <= 0) {
            throw new EpisodioNoEncontradoException("Episodio no válido: " + numeroEpisodio + ", Temporada: " + temporada);
        }
    }

    // Getters y Setters
    public Creador getCreador() { return creador; }
    public int getNumeroEpisodio() { return numeroEpisodio; }
    public void setNumeroEpisodio(int numeroEpisodio) { this.numeroEpisodio = numeroEpisodio; }
    public int getTemporada() { return temporada; }
    public void setTemporada(int temporada) { this.temporada = temporada; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public CategoriaPodcast getCategoria() { return categoria; }
    public void setCategoria(CategoriaPodcast categoria) { this.categoria = categoria; }
    public ArrayList<String> getInvitados() { return new ArrayList<>(invitados); }
    public String getTranscripcion() { return transcripcion; }
    public void setTranscripcion(String transcripcion) { this.transcripcion = transcripcion; }
    public boolean isReproduciendo() { return reproduciendo; }
    public boolean isPausado() { return pausado; }
    public boolean isDescargado() { return descargado; }

    @Override
    public String toString() {
        return "Podcast{" +
                "titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", temporada=" + temporada +
                ", categoria=" + categoria +
                '}';
    }

    @Override
    public ArrayList<String> getEtiquetas() {
        ArrayList<String> etiquetas = new ArrayList<>(super.getEtiquetas());
        etiquetas.add(categoria.name());
        return etiquetas;
    }
}

