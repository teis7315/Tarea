package modelo.plataforma;

import enums.GeneroMusical;
import enums.TipoSuscripcion;
import excepciones.plataforma.ArtistaNoEncontradoException;
import excepciones.contenido.DuracionInvalidaException;
import excepciones.plataforma.UsuarioYaExisteException;
import excepciones.usuario.EmailInvalidoException;
import excepciones.usuario.PasswordDebilException;
import modelo.artistas.Artista;
import modelo.artistas.Creador;
import modelo.contenido.Cancion;
import modelo.contenido.Contenido;
import modelo.contenido.Podcast;
import modelo.usuarios.Usuario;
import modelo.usuarios.UsuarioGratuito;
import modelo.usuarios.UsuarioPremium;
import interfaces.Recomendador;

import java.util.ArrayList;
import java.util.HashMap;
import enums.CategoriaPodcast;

@SuppressWarnings("unused")
public class Plataforma {
    private static Plataforma instancia;
    private final String nombre;
    private final HashMap<String, Usuario> usuarios;
    private final HashMap<String, Usuario> usuariosPorEmail;
    private final ArrayList<Contenido> catalogo;
    private final ArrayList<Playlist> playlistsPublicas;
    private final HashMap<String, Artista> artistas;
    private final HashMap<String, Creador> creadores;
    private final ArrayList<Anuncio> anuncios;
    private Recomendador recomendador;
    private int totalAnunciosReproducidos;

    private Plataforma(String nombre) {
        this.nombre = nombre;
        this.usuarios = new HashMap<>();
        this.usuariosPorEmail = new HashMap<>();
        this.catalogo = new ArrayList<>();
        this.playlistsPublicas = new ArrayList<>();
        this.artistas = new HashMap<>();
        this.creadores = new HashMap<>();
        this.anuncios = new ArrayList<>();
        this.recomendador = null;
        this.totalAnunciosReproducidos = 0;
    }

    public static synchronized Plataforma getInstancia(String nombre) {
        if (instancia == null) instancia = new Plataforma(nombre);
        return instancia;
    }

    public static synchronized Plataforma getInstancia() {
        if (instancia == null) instancia = new Plataforma("SoundWave");
        return instancia;
    }

    public static synchronized void reiniciarInstancia() {
        instancia = null;
    }

    public UsuarioPremium registrarUsuarioPremium(String nombre, String email, String password, TipoSuscripcion tipo)
            throws UsuarioYaExisteException, EmailInvalidoException, PasswordDebilException {
        if (usuariosPorEmail.containsKey(email)) throw new UsuarioYaExisteException("Email en uso");
        UsuarioPremium u = new UsuarioPremium(nombre, email, password, tipo);
        usuarios.put(u.getId(), u);
        usuariosPorEmail.put(email, u);
        return u;
    }

    public UsuarioPremium registrarUsuarioPremium(String nombre, String email, String password)
            throws UsuarioYaExisteException, EmailInvalidoException, PasswordDebilException {
        return registrarUsuarioPremium(nombre, email, password, TipoSuscripcion.PREMIUM);
    }

    public UsuarioGratuito registrarUsuarioGratuito(String nombre, String email, String password)
            throws UsuarioYaExisteException, EmailInvalidoException, PasswordDebilException {
        if (usuariosPorEmail.containsKey(email)) throw new UsuarioYaExisteException("Email en uso");
        UsuarioGratuito u = new UsuarioGratuito(nombre, email, password);
        usuarios.put(u.getId(), u);
        usuariosPorEmail.put(email, u);
        return u;
    }

    public ArrayList<Usuario> getTodosLosUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuariosPorEmail.get(email);
    }

    public Artista registrarArtista(String nombreArtistico, String nombreReal, String paisOrigen, boolean verificado) {
        Artista a = new Artista(nombreArtistico, nombreReal, paisOrigen, verificado, "");
        artistas.put(a.getId(), a);
        return a;
    }

    public void registrarArtista(Artista artista) {
        artistas.put(artista.getId(), artista);
    }

    public ArrayList<Artista> getArtistasVerificados() {
        ArrayList<Artista> res = new ArrayList<>();
        for (Artista a : artistas.values()) if (a.isVerificado()) res.add(a);
        return res;
    }

    public ArrayList<Artista> getArtistasNoVerificados() {
        ArrayList<Artista> res = new ArrayList<>();
        for (Artista a : artistas.values()) if (!a.isVerificado()) res.add(a);
        return res;
    }

    public Artista buscarArtista(String nombre) throws ArtistaNoEncontradoException {
        for (Artista a : artistas.values()) if (a.getNombreArtistico().equalsIgnoreCase(nombre)) return a;
        throw new ArtistaNoEncontradoException("Artista no encontrado: " + nombre);
    }

    public Cancion crearCancion(String titulo, int duracion, Artista artista, GeneroMusical genero) throws DuracionInvalidaException {
        Cancion c = new Cancion(titulo, duracion, artista, genero);
        catalogo.add(c);
        return c;
    }

    public void agregarContenidoCatalogo(Contenido contenido) {
        catalogo.add(contenido);
    }

    public ArrayList<Cancion> getCanciones() {
        ArrayList<Cancion> res = new ArrayList<>();
        for (Contenido c : catalogo) { if (c instanceof Cancion) res.add((Cancion)c); }
        return res;
    }

    // Métodos mínimos para creadores/podcasts
    public Creador registrarCreador(String nombreCanal, String nombre, String descripcion) {
        Creador c = new Creador(nombreCanal, nombre, descripcion);
        creadores.put(c.getId(), c);
        return c;
    }

    public Podcast crearPodcast(String titulo, int duracion, Creador creador, int numEpisodio, int temporada, CategoriaPodcast categoria) throws DuracionInvalidaException {
        Podcast p = new Podcast(titulo, duracion, creador, numEpisodio, temporada, categoria);
        catalogo.add(p);
        return p;
    }

    public ArrayList<Podcast> getPodcasts() {
        ArrayList<Podcast> res = new ArrayList<>();
        for (Contenido c : catalogo) { if (c instanceof Podcast) res.add((Podcast)c); }
        return res;
    }

    public ArrayList<Creador> getTodosLosCreadores() {
        return new ArrayList<>(creadores.values());
    }

    public Playlist crearPlaylistPublica(String nombre, Usuario creador) {
        Playlist p = new Playlist(nombre, creador, true, "");
        playlistsPublicas.add(p);
        return p;
    }

    public ArrayList<Playlist> getPlaylistsPublicas() {
        return new ArrayList<>(playlistsPublicas);
    }

    public Anuncio obtenerAnuncioAleatorio() {
        if (anuncios.isEmpty()) return null;
        int idx = (int)(Math.random()*anuncios.size());
        Anuncio a = anuncios.get(idx);
        if (!a.puedeMostrarse()) return null;
        return a;
    }

    public void incrementarAnunciosReproducidos() { totalAnunciosReproducidos++; }

    public String obtenerEstadisticasGenerales() {
        return "Usuarios: " + usuarios.size() + ", Contenido: " + catalogo.size();
    }

    // Getters básicos
    public String getNombre() { return nombre; }
    public ArrayList<Contenido> getCatalogo() { return new ArrayList<>(catalogo); }
    public HashMap<String, Artista> getArtistas() { return new HashMap<>(artistas); }
    public HashMap<String, Creador> getCreadores() { return new HashMap<>(creadores); }
    public ArrayList<Anuncio> getAnuncios() { return new ArrayList<>(anuncios); }
    public Recomendador getRecomendador() { return recomendador; }
    public int getTotalUsuarios() { return usuarios.size(); }
    public int getTotalContenido() { return catalogo.size(); }
    public int getTotalAnunciosReproducidos() { return totalAnunciosReproducidos; }

    @Override
    public String toString() { return "Plataforma{" + "nombre='" + nombre + '\'' + '}'; }
}
