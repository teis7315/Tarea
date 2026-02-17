package modelo.contenido;

import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.contenido.DuracionInvalidaException;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public abstract class Contenido {
    String id;
    String titulo;
    int reproducciones;
    int likes;
    int duracionSegundos;
    ArrayList<String> tags;
    boolean disponible;
    Date fechaPublicacion;

    public Contenido(String titulo,int duracionSegundos)throws DuracionInvalidaException {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.reproducciones = 0;
        this.likes = 0;
        this.duracionSegundos = duracionSegundos;
        this.tags = new ArrayList<>();
        this.disponible = true;
        this.fechaPublicacion = new Date();

        if(!(duracionSegundos>0)){
            throw new DuracionInvalidaException();
        }
    }

    public abstract void reproducir() throws ContenidoNoDisponibleException;
    //fallo if disponible==false en hijo

    public void aumentarReproducciones(){
        reproducciones++;
    }
    public void agregarLike(){
        likes++;
    }
    public boolean esPopular() {
        return (reproducciones > 100000);
    }
    public void validarDuracion() throws DuracionInvalidaException{
        if(duracionSegundos>0) {
            System.out.println("Duración válida");
        }else{
            throw new DuracionInvalidaException();
        }
    }
    public void agregarTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public boolean tieneTag(String tag) {
        return tags.contains(tag);
    }

    public void marcarNoDisponible() {
        disponible = false;
    }

    public void marcarDisponible() {
        disponible = true;
    }

    public String getDuracionFormateada() {
        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getReproducciones() {
        return reproducciones;
    }

    public void setReproducciones(int reproducciones) {
        this.reproducciones = reproducciones;
    }

    public int getLikes() {
        return likes;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public ArrayList<String> getTags() {
        return new ArrayList<>(tags);
    }

    public boolean isDisponible() {
        return disponible;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
    @Override
    public String toString(){return titulo+" ["+getDuracionFormateada()+"]";}
    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(obj==null||getClass() !=obj.getClass()) return false;
        Contenido contenido=(Contenido) obj;
        return id.equals(contenido.id);
    }
    @Override
    public int hashCode(){return id.hashCode();}
    public ArrayList<String> getEtiquetas() {
        return new ArrayList<>(tags); // copia defensiva
    }
}
