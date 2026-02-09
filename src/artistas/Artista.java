package artistas;

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
    void crearAlbum(String titulo, Date fecha){
        //crear album
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

    public ArrayList<Cancion> getDiscografia() {
        return discografia;
    }
}
