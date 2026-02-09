package modelo.artistas;

import enums.CategoriaPodcast;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Creador {
    String id;
    String nombreCanal;
    String nombre;
    ArrayList<Podcast> episodios;
    int suscriptores;
    String descripcion;
    HashMap<String,String> redesSociales;
    ArrayList<CategoriaPodcast> categoriasPrincipales;

    public Creador() {
    }

    public Creador(String id, String nombreCanal, String nombre, ArrayList<Podcast> episodios, int suscriptores, String descripcion, HashMap<String, String> redesSociales, ArrayList<CategoriaPodcast> categoriasPrincipales) {
        this.id = id;
        this.nombreCanal = nombreCanal;
        this.nombre = nombre;
        this.episodios = episodios;
        this.suscriptores = suscriptores;
        this.descripcion = descripcion;
        this.redesSociales = redesSociales;
        this.categoriasPrincipales = categoriasPrincipales;
    }
    public abstract void publicarPodcast(Podcast episodio);

    public String getId() {
        return id;
    }

    public String getNombreCanal() {
        return nombreCanal;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Podcast> getEpisodios() {
        return episodios;
    }

    public int getSuscriptores() {
        return suscriptores;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public HashMap<String, String> getRedesSociales() {
        return redesSociales;
    }

    public ArrayList<CategoriaPodcast> getCategoriasPrincipales() {
        return categoriasPrincipales;
    }
    //^all this as obtenerEstadisticas();
    public void agregarRedSocial(String red,String usuario){
        redesSociales.put(red,usuario);
    }
    public double calcularPromedioReproducciones(){
        return 0.0;
        //calcular reproducciones medias de canciones
    }
    public void eliminarEpisodio(){
        //episodios.remove();
    }
}
