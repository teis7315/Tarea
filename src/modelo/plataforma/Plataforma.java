package modelo.plataforma;

import enums.GeneroMusical;
import enums.TipoSuscripcion;
import enums.CategoriaPodcast;
import excepciones.artista.*;
import excepciones.plataforma.*;
import excepciones.contenido.*;
import excepciones.usuario.*;
import modelo.artistas.*;
import modelo.contenido.*;
import modelo.usuarios.*;
import interfaces.Recomendador;
import utilidades.RecomendadorIA;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
    private final ArrayList<Album> albumes;
    private final ArrayList<Anuncio> anuncios;
    private RecomendadorIA recomendador;
    private int totalAnunciosReproducidos;

    private Plataforma(String nombre) {
        this.nombre = nombre;
        this.usuarios = new HashMap<>();
        this.usuariosPorEmail = new HashMap<>();
        this.catalogo = new ArrayList<>();
        this.playlistsPublicas = new ArrayList<>();
        this.artistas = new HashMap<>();
        this.creadores = new HashMap<>();
        this.albumes = new ArrayList<>();
        this.anuncios = new ArrayList<>();
        this.recomendador = new RecomendadorIA();
        this.totalAnunciosReproducidos = 0;
    }

    // Singleton
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

    // ====================== Usuarios ======================
    public UsuarioPremium registrarUsuarioPremium(String nombre, String email, String password, TipoSuscripcion tipo)
            throws UsuarioYaExisteException, EmailInvalidoException, PasswordDebilException {
        if (usuariosPorEmail.containsKey(email)) {
            throw new UsuarioYaExisteException("Email en uso");
        }
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
        if (usuariosPorEmail.containsKey(email)) {
            throw new UsuarioYaExisteException("Email en uso");
        }
        UsuarioGratuito u = new UsuarioGratuito(nombre, email, password);
        usuarios.put(u.getId(), u);
        usuariosPorEmail.put(email, u);
        return u;
    }

    public ArrayList<UsuarioPremium> getUsuariosPremium() {
        ArrayList<UsuarioPremium> res = new ArrayList<>();
        for (Usuario u : usuarios.values()) if (u instanceof UsuarioPremium) res.add((UsuarioPremium) u);
        return res;
    }

    public ArrayList<UsuarioGratuito> getUsuariosGratuitos() {
        ArrayList<UsuarioGratuito> res = new ArrayList<>();
        for (Usuario u : usuarios.values()) if (u instanceof UsuarioGratuito) res.add((UsuarioGratuito) u);
        return res;
    }

    public ArrayList<Usuario> getTodosLosUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuariosPorEmail.get(email);
    }

    // ====================== Artistas ======================
    public Artista registrarArtista(String nombreArtistico, String nombreReal, String pais, boolean verificado) {
        Artista a = new Artista(nombreArtistico, nombreReal, pais, verificado);
        artistas.put(nombreArtistico, a);
        return a;
    }

    public ArrayList<Artista> getArtistas() {
        return new ArrayList<>(artistas.values());
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

    // ====================== Álbumes / Canciones ======================
    public Album crearAlbum(Artista artista, String nombre, Date fecha) {
        Album album = new Album(nombre, artista, fecha);
        albumes.add(album);
        return album;
    }

    public void agregarContenidoCatalogo(Cancion c) {
        catalogo.add(c);
    }

    public ArrayList<Album> getAlbumes() {
        return new ArrayList<>(albumes);
    }

    public ArrayList<Cancion> getCanciones() {
        ArrayList<Cancion> canciones = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Cancion) canciones.add((Cancion) c);
        return canciones;
    }

    // ====================== Creadores / Podcasts ======================
    public Creador registrarCreador(String nombreCanal, String nombreReal, String descripcion) {
        Creador c = new Creador(nombreCanal, nombreReal, descripcion);
        creadores.put(nombreCanal, c);
        return c;
    }

    public ArrayList<Creador> getTodosLosCreadores() {
        return new ArrayList<>(creadores.values());
    }

    public Podcast crearPodcast(String nombre, int duracion, Creador creador, int episodio, int temporada, CategoriaPodcast categoria) throws DuracionInvalidaException {
        Podcast p = new Podcast(nombre, duracion, creador, episodio, temporada, categoria);
        catalogo.add(p);
        return p;
    }

    public ArrayList<Podcast> getPodcasts() {
        ArrayList<Podcast> podcasts = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Podcast) podcasts.add((Podcast) c);
        return podcasts;
    }

    // ====================== Catálogo / Búsquedas / Estadísticas ======================
    public ArrayList<Contenido> getCatalogo() {
        return new ArrayList<>(catalogo);
    }

    public ArrayList<Contenido> buscarContenido(String texto) {
        ArrayList<Contenido> res = new ArrayList<>();
        for (Contenido c : catalogo)
            if (c.getNombre().toLowerCase().contains(texto.toLowerCase()))
                res.add(c);
        return res;
    }

    public ArrayList<Cancion> buscarPorGenero(GeneroMusical genero) {
        ArrayList<Cancion> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Cancion && ((Cancion)c).getGenero() == genero) res.add((Cancion)c);
        return res;
    }

    public ArrayList<Podcast> buscarPorCategoria(CategoriaPodcast categoria) {
        ArrayList<Podcast> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Podcast && ((Podcast)c).getCategoria() == categoria) res.add((Podcast)c);
        return res;
    }

    public ArrayList<Contenido> obtenerTopContenidos(int n) {
        ArrayList<Contenido> res = new ArrayList<>(catalogo);
        // Simple placeholder: return first n
        return new ArrayList<>(res.subList(0, Math.min(n, res.size())));
    }

    public Artista buscarArtista(String nombre) {
        return artistas.get(nombre);
    }

    public String obtenerEstadisticasGenerales() {
        return "Estadísticas placeholder";
    }

    // ====================== Recomendador / Anuncios ======================
    public RecomendadorIA getRecomendador() {
        return recomendador;
    }

    public Anuncio obtenerAnuncioAleatorio() {
        if (anuncios.isEmpty()) return null;
        return anuncios.get(0); // simple placeholder
    }
}
