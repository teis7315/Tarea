package modelo.plataforma;

import excepciones.playlist.ContenidoDuplicadoException;
import excepciones.playlist.PlaylistLlenaException;
import excepciones.playlist.PlaylistVaciaException;
import modelo.contenido.Contenido;
import modelo.usuarios.Usuario;
import enums.CriterioOrden;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Playlist {
    private static final int MAX_CONTENIDOS_DEFAULT = 500;

    private final String id;
    private String nombre;
    private final Usuario creador;
    private final ArrayList<Contenido> contenidos;
    private boolean esPublica;
    private int seguidores;
    private String descripcion;
    private String portadaURL;
    private final Date fechaCreacion;
    private final int maxContenidos;

    // Constructores
    public Playlist(String nombre, Usuario creador) {
        this(nombre, creador, true, "");
    }

    public Playlist(String nombre, Usuario creador, boolean esPublica, String descripcion) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.creador = creador;
        this.contenidos = new ArrayList<>();
        this.esPublica = esPublica;
        this.seguidores = 0;
        this.descripcion = descripcion;
        this.portadaURL = "";
        this.fechaCreacion = new Date();
        this.maxContenidos = MAX_CONTENIDOS_DEFAULT;
    }

    // Métodos de gestión
    public void agregarContenido(Contenido contenido) throws PlaylistLlenaException, ContenidoDuplicadoException {
        if (contenidos.size() >= maxContenidos) {
            throw new PlaylistLlenaException("La playlist está llena");
        }
        if (contenidos.contains(contenido)) {
            throw new ContenidoDuplicadoException("El contenido ya existe en la playlist");
        }
        contenidos.add(contenido);
    }

    public boolean eliminarContenido(String idContenido) {
        return contenidos.removeIf(c -> c.getId().equals(idContenido));
    }

    public boolean eliminarContenido(Contenido contenido) {
        return contenidos.remove(contenido);
    }

    public void ordenarPor(CriterioOrden criterio) throws PlaylistVaciaException {
        if (contenidos.isEmpty()) throw new PlaylistVaciaException("Playlist vacía");

        switch (criterio) {
            case POPULARIDAD:
                // De mayor a menor reproducciones
                contenidos.sort((c1, c2) -> Integer.compare(c2.getReproducciones(), c1.getReproducciones()));
                break;
            case DURACION:
                // De menor a mayor duración
                contenidos.sort((c1, c2) -> Integer.compare(c1.getDuracionSegundos(), c2.getDuracionSegundos()));
                break;
            case ALFABETICO:
                // Alfabéticamente por título
                contenidos.sort((c1, c2) -> c1.getTitulo().compareToIgnoreCase(c2.getTitulo()));
                break;
            default:
                throw new IllegalArgumentException("Criterio de orden no soportado");
        }
    }



    public int getDuracionTotal() {
        int total = 0;
        for (Contenido c : contenidos) total += c.getDuracionSegundos();
        return total;
    }

    public String getDuracionTotalFormateada() {
        int segundos = getDuracionTotal();
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int secs = segundos % 60;
        if (horas > 0) return String.format("%d:%02d:%02d", horas, minutos, secs);
        return String.format("%d:%02d", minutos, secs);
    }

    public void shuffle() {
        Collections.shuffle(contenidos);
    }

    public ArrayList<Contenido> buscarContenido(String termino) {
        ArrayList<Contenido> resultado = new ArrayList<>();
        for (Contenido c : contenidos) {
            if (c.getTitulo().toLowerCase().contains(termino.toLowerCase())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public void hacerPublica() { this.esPublica = true; }
    public void hacerPrivada() { this.esPublica = false; }
    public void incrementarSeguidores() { seguidores++; }
    public void decrementarSeguidores() { if (seguidores > 0) seguidores--; }
    public int getNumContenidos() { return contenidos.size(); }
    public boolean estaVacia() { return contenidos.isEmpty(); }
    public Contenido getContenido(int posicion) {
        return posicion >= 0 && posicion < contenidos.size() ? contenidos.get(posicion) : null;
    }

    // Getters/Setters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Usuario getCreador() { return creador; }
    public ArrayList<Contenido> getContenidos() { return new ArrayList<>(contenidos); }
    public boolean isEsPublica() { return esPublica; }
    public void setEsPublica(boolean esPublica) { this.esPublica = esPublica; }
    public int getSeguidores() { return seguidores; }
    public void setSeguidores(int seguidores) { this.seguidores = seguidores; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getPortadaURL() { return portadaURL; }
    public void setPortadaURL(String portadaURL) { this.portadaURL = portadaURL; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public int getMaxContenidos() { return maxContenidos; }

    @Override
    public String toString() {
        return nombre + " (" + contenidos.size() + " items)";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Playlist other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
