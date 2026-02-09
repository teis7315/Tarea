package modelo.contenido;

import enums.GeneroMusical;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.contenido.DuracionInvalidaException;
import excepciones.descarga.ContenidoYaDescargadoException;
import excepciones.descarga.LimiteDescargasException;
import interfaces.Descargable;
import interfaces.Reproducible;

import java.util.UUID;

public class Cancion extends Contenido implements Reproducible, Descargable {
    private String letra;
    Artista artista;
    Album album;
    GeneroMusical genero;
    String audioURL;
    boolean explicit;
    String ISRC;
    boolean reproduciendo;
    boolean pausado;
    boolean descargado;

    public Cancion(String titulo, int duracionSegundos, Artista artista, GeneroMusical genero) throws DuracionInvalidaException {
        super(titulo, duracionSegundos);
        this.artista = artista;
        this.genero = genero;
        this.audioURL = "https://sounwave.com/audio/"+id+".mp3";
        this.ISRC = UUID.randomUUID().toString(); ;
        this.reproduciendo = false;
        this.pausado = false;
        this.descargado = false;
    }
    public Cancion(String titulo, int duracionSegundos, Artista artista, GeneroMusical genero, boolean explicit, String letra) throws DuracionInvalidaException {
        super(titulo, duracionSegundos);
        this.artista = artista;
        this.genero = genero;
        this.audioURL = "https://sounwave.com/audio/"+id+".mp3";
        this.ISRC = UUID.randomUUID().toString();
        this.reproduciendo = false;
        this.pausado = false;
        this.descargado = false;
        this.explicit = explicit;
        this.letra = letra;
    }
    private String generarISRC(){
        return UUID.randomUUID().toString();
    }
    @Override
    public void reproducir() throws ContenidoNoDisponibleException {
        if(disponible){
            reproduciendo=true;
            reproducciones++;
        }else{
            throw new ContenidoNoDisponibleException();
        }
    }

    @Override
    public void play() {
        reproduciendo=true;
        pausado=false;
        System.out.println(artista+" - "+id+" - "+album);
    }

    @Override
    public void pause() {
        if(reproduciendo){
        pausado=true;
        }
    }

    @Override
    public void stop() {
        reproduciendo=false;
        pausado=false;
    }

    @Override
    public int getDuration() {
        return duracionSegundos;
    }

    @Override
    public boolean descargar() throws LimiteDescargasException, ContenidoYaDescargadoException {
        if(!descargado){
            descargado=true;
        }else{
            throw new ContenidoYaDescargadoException();
        }
        //LIMITE  DESCARGAS?
    }

    @Override
    public boolean eliminarDescargar() {
        descargado=false;
    }

    @Override
    public int expacioRequerido() {
        return 0;//duracion-->mb?
    }

    public String getLetra() {
        return letra;
    }

    public Artista getArtista() {
        return artista;
    }

    public Album getAlbum() {
        return album;
    }

    public GeneroMusical getGenero() {
        return genero;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public boolean isExplicit() {
        return explicit;
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

    public void setLetra(String letra) {
        this.letra = letra;
    }

    public void setArtista(Artista artista) {
        this.artista = artista;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setGenero(GeneroMusical genero) {
        this.genero = genero;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public void setDescargado(boolean descargado) {
        this.descargado = descargado;
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "artista=" + artista +
                ", duracionSegundos=" + duracionSegundos +
                '}';
    }
}
