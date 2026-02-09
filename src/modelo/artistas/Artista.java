package modelo.artistas;

import excepciones.artista.AlbumYaExisteException;
import excepciones.artista.ArtistaNoVerificadoException;
import modelo.contenido.Cancion;

import java.util.ArrayList;
import java.util.Date;

public class Artista {
    String id;
    String nombreArtistico;
    String nombreReal;
    ArrayList<Cancion> discografia;
    ArrayList<Album> albumes;
    int oyentesMensuales;
    boolean verificado;
    String biografia;

    public Artista() {
    }

    public Artista(String id, String nombreArtistico, String nombreReal, ArrayList<Cancion> discografia, ArrayList<Album> albumes, int oyentesMensuales, boolean verificado, String biografia) {
        this.id = id;
        this.nombreArtistico = nombreArtistico;
        this.nombreReal = nombreReal;
        this.discografia = discografia;
        this.albumes = albumes;
        this.oyentesMensuales = oyentesMensuales;
        this.verificado = verificado;
        this.biografia = biografia;
    }
    void publicarCancion(Cancion cancion){
        discografia.add(cancion);
    }
    void crearAlbum(String titulo, Date fecha) throws ArtistaNoVerificadoException, AlbumYaExisteException {
        if(verificado&&!albumes.contains(titulo)){
        albumes.add(new Album());
        }
    }


    public ArrayList<Cancion> obtenerTopCanciones(int cantidad) {
        ArrayList<Cancion> copia = new ArrayList<>(canciones);

        copia.sort((c1, c2) -> Integer.compare(
                c2.getReproducciones(),
                c1.getReproducciones()
        ));

        ArrayList<Cancion> top = new ArrayList<>();

        for (int i = 0; i < Math.min(cantidad, copia.size()); i++) {
            top.add(copia.get(i));
        }

        return top;
    }
    double calcularPromedioReproducciones() {
        if (canciones.isEmpty()) {
            return 0;
        }

        int total = 0;

        for (Cancion c : canciones) {
            total += c.getReproducciones();
        }

        return (double) total / canciones.size();
    }
    boolean esVerificado(){
        return verificado;
}
    public int getTotalReproducciones() {
        int total = 0;

        for (Cancion c : canciones) {
            total += c.getReproducciones();
        }

        return total;
    }
    void verifircar(){
        verificado=true;
    }
    void incrementarOyentes(){
        oyentesMensuales++;
    }

    public String getId() {
        return id;
    }

    public String getNombreArtistico() {
        return nombreArtistico;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public ArrayList<Cancion> getDiscografia() {
        return new ArrayList<>(canciones);
    }


    public ArrayList<Album> getAlbumes() {
        return new ArrayList<>(albumes);
    }


    public int getOyentesMensuales() {
        return oyentesMensuales;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombreArtistico(String nombreArtistico) {
        this.nombreArtistico = nombreArtistico;
    }

    public void setNombreReal(String nombreReal) {
        this.nombreReal = nombreReal;
    }

    public void setOyentesMensuales(int oyentesMensuales) {
        this.oyentesMensuales = oyentesMensuales;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    @Override
    public String toString() {
        return "Artista{" +
                "id='" + id + '\'' +
                ", nombreArtistico='" + nombreArtistico + '\'' +
                ", nombreReal='" + nombreReal + '\'' +
                ", discografia=" + discografia +
                ", albumes=" + albumes +
                ", oyentesMensuales=" + oyentesMensuales +
                ", verificado=" + verificado +
                ", biografia='" + biografia + '\'' +
                '}';
    }


}
