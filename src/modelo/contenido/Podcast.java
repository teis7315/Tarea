package modelo.contenido;
import artistas.Creador;
import enums.CategoriaPodcast;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.contenido.DuracionInvalidaException;
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

}
