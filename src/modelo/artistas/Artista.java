package modelo.artistas;

import excepciones.artista.AlbumYaExisteException;
import excepciones.artista.ArtistaNoVerificadoException;
import modelo.contenido.Cancion;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Artista {
    private String id;
    private String nombreArtistico;
    private String nombreReal;
    private String paisOrigen;
    private ArrayList<Cancion> discografia;
    private ArrayList<Album> albumes;
    private int oyentesMensuales;
    private boolean verificado;
    private String biografia;

    public Artista(String nombreArtistico, String nombreReal, String paisOrigen) {
        this.id = UUID.randomUUID().toString();
        this.nombreArtistico = nombreArtistico;
        this.nombreReal = nombreReal;
        this.paisOrigen = paisOrigen;
        this.discografia = new ArrayList<>();
        this.albumes = new ArrayList<>();
        this.oyentesMensuales = 0;
        this.verificado = false;
        this.biografia = "";
    }

    public Artista(String nombreArtistico, String nombreReal, String paisOrigen, boolean verificado, String biografia) {
        this.id = UUID.randomUUID().toString();
        this.nombreArtistico = nombreArtistico;
        this.nombreReal = nombreReal;
        this.paisOrigen = paisOrigen;
        this.discografia = new ArrayList<>();
        this.albumes = new ArrayList<>();
        this.oyentesMensuales = 0;
        this.verificado = verificado;
        this.biografia = biografia;
    }

    public Artista(String nombreArtistico, String nombreReal, String pais, boolean verificado) {
        this.id = UUID.randomUUID().toString();
        this.nombreArtistico = nombreArtistico;
        this.nombreReal = nombreReal;
        this.paisOrigen = pais;
        this.discografia = new ArrayList<>();
        this.albumes = new ArrayList<>();
        this.oyentesMensuales = 0;
        this.verificado = verificado;
        this.biografia = "";
    }


    public void publicarCancion(Cancion cancion) {
        if (!discografia.contains(cancion)) {
            discografia.add(cancion);
        }
    }

    public Album crearAlbum(String titulo, Date fecha) throws ArtistaNoVerificadoException, AlbumYaExisteException {
        if (!verificado) {
            throw new ArtistaNoVerificadoException("El artista no está verificado para crear álbumes");
        }

        for (Album a : albumes) {
            if (a.getTitulo().equalsIgnoreCase(titulo)) {
                throw new AlbumYaExisteException("Ya existe un álbum con el título: " + titulo);
            }
        }

        Album album = new Album(titulo, this, fecha);
        albumes.add(album);
        return album;
    }

    public ArrayList<Cancion> obtenerTopCanciones(int cantidad) {
        ArrayList<Cancion> copia = new ArrayList<>(discografia);
        copia.sort((c1, c2) -> Integer.compare(c2.getReproducciones(), c1.getReproducciones()));

        ArrayList<Cancion> top = new ArrayList<>();
        for (int i = 0; i < Math.min(cantidad, copia.size()); i++) {
            top.add(copia.get(i));
        }
        return top;
    }

    public double calcularPromedioReproducciones() {
        if (discografia.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Cancion c : discografia) {
            total += c.getReproducciones();
        }
        return (double) total / discografia.size();
    }

    public boolean esVerificado() {
        return verificado;
    }

    public int getTotalReproducciones() {
        int total = 0;
        for (Cancion c : discografia) {
            total += c.getReproducciones();
        }
        return total;
    }

    public void verificar() {
        this.verificado = true;
    }

    public void incrementarOyentes() {
        oyentesMensuales++;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getNombreArtistico() {
        return nombreArtistico;
    }

    public void setNombreArtistico(String nombreArtistico) {
        this.nombreArtistico = nombreArtistico;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public void setNombreReal(String nombreReal) {
        this.nombreReal = nombreReal;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public ArrayList<Cancion> getDiscografia() {
        return new ArrayList<>(discografia);
    }

    public ArrayList<Album> getAlbumes() {
        return new ArrayList<>(albumes);
    }

    public int getOyentesMensuales() {
        return oyentesMensuales;
    }

    public void setOyentesMensuales(int oyentesMensuales) {
        this.oyentesMensuales = oyentesMensuales;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    @Override
    public String toString() {
        return "Artista{" + "nombreArtistico='" + nombreArtistico + '\'' +
               ", oyentesMensuales=" + oyentesMensuales + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Artista artista = (Artista) obj;
        return id.equals(artista.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
