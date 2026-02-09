package modelo.contenido;
import modelo.artistas.Creador;
import enums.CategoriaPodcast;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.contenido.DuracionInvalidaException;
import excepciones.contenido.EpisodioNoEncontradoException;
import excepciones.contenido.TranscripcionNoDisponibleException;
import interfaces.Descargable;
import interfaces.Reproducible;
import java.util.ArrayList;
public abstract class Podcast extends Contenido implements Reproducible,Descargable {
    private Creador creador;
    private int numeroEpisodio;
    private int temporada;
    private String descripcion;
    private CategoriaPodcast categoria;
    private ArrayList<String> invitados;
    private String transcripcion;
    private boolean reproduciendo;
    private boolean pausado;
    private boolean descargado;

    public Podcast(String titulo, int duracionSegundos, Creador creador, int numeroEpisodio, int temporada, CategoriaPodcast categoria) throws DuracionInvalidaException {
        super(titulo, duracionSegundos);
        this.creador = creador;
        this.numeroEpisodio = numeroEpisodio;
        this.temporada = temporada;
        this.categoria = categoria;
    }

    public Podcast(String titulo, int duracionSegundos, Creador creador, int numeroEpisodio, int temporada, String descripcion, CategoriaPodcast categoria) throws DuracionInvalidaException {
        super(titulo, duracionSegundos);
        this.creador = creador;
        this.numeroEpisodio = numeroEpisodio;
        this.temporada = temporada;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public  void reproducir() throws ContenidoNoDisponibleException{
        if(disponible){
            reproduciendo=true;
            reproducciones++;
        }else{
            throw new ContenidoNoDisponibleException();
        }
    }

    @Override
    public void play(){
        pausado=false;
    }

    @Override
    public void pause() {
    pausado=true;
    }

    @Override
    public void stop() {
    reproduciendo=false;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }
    void agregarInvitado(String nombre){
        this.invitados.add(nombre);
    }
    public boolean esTemporadaNueva(){
        return false;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
    public String obtenerTranscripcion() throws TranscripcionNoDisponibleException {
        if(!descripcion.isBlank()){
            return  descripcion;
        }else{
            throw new TranscripcionNoDisponibleException();
        }

    }
    public void validarEpisodio() throws EpisodioNoEncontradoException{
        if(!(numeroEpisodio>0)){
            throw new EpisodioNoEncontradoException();
        }


    }

    public Creador getCreador() {
        return creador;
    }

    public int getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public int getTemporada() {
        return temporada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public CategoriaPodcast getCategoria() {
        return categoria;
    }

    public ArrayList<String> getInvitados() {
        return new ArrayList<>(invitados);
    }


    public String getTranscripcion() {
        return transcripcion;
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

    public void setCreador(Creador creador) {
        this.creador = creador;
    }

    public void setNumeroEpisodio(int numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public void setTemporada(int temporada) {
        this.temporada = temporada;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCategoria(CategoriaPodcast categoria) {
        this.categoria = categoria;
    }

    public void setTranscripcion(String transcripcion) {
        this.transcripcion = transcripcion;
    }

    public void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "creador=" + creador +
                ", numeroEpisodio=" + numeroEpisodio +
                ", temporada=" + temporada +
                ", descripcion='" + descripcion + '\'' +
                ", categoria=" + categoria +
                ", invitados=" + invitados +
                ", transcripcion='" + transcripcion + '\'' +
                ", reproduciendo=" + reproduciendo +
                ", pausado=" + pausado +
                ", descargado=" + descargado +
                ", id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", reproducciones=" + reproducciones +
                ", likes=" + likes +
                ", duracionSegundos=" + duracionSegundos +
                ", tags=" + tags +
                ", disponible=" + disponible +
                ", fechaPublicacion=" + fechaPublicacion +
                '}';
    }
}
