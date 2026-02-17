package modelo.plataforma;

import enums.GeneroMusical;
import enums.TipoSuscripcion;
import enums.CategoriaPodcast;
import excepciones.artista.AlbumCompletoException;
import excepciones.artista.AlbumYaExisteException;
import excepciones.artista.ArtistaNoVerificadoException;
import excepciones.artista.LimiteEpisodiosException;
import excepciones.plataforma.*;
import excepciones.contenido.*;
import excepciones.usuario.*;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.artistas.Creador;
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

    // ====================== Álbumes ======================
    public Album crearAlbum(Artista artista, String titulo, Date fecha) throws ArtistaNoVerificadoException, AlbumYaExisteException {
        if (!artista.isVerificado()) throw new ArtistaNoVerificadoException("Artista no verificado");
        for (Album a : albumes) if (a.getTitulo().equalsIgnoreCase(titulo) && a.getArtista().equals(artista)) throw new AlbumYaExisteException("Álbum ya existe");
        Album nuevo = new Album(titulo, artista, fecha);
        albumes.add(nuevo);
        return nuevo;
    }

    public ArrayList<Album> getAlbumes() {
        return new ArrayList<>(albumes);
    }

    // ====================== Canciones ======================
    public Cancion crearCancion(String titulo, int duracion, Artista artista, GeneroMusical genero) throws DuracionInvalidaException {
        Cancion c = new Cancion(titulo, duracion, artista, genero);
        catalogo.add(c);
        return c;
    }

    public Cancion crearCancionEnAlbum(String titulo, int duracion, Artista artista, GeneroMusical genero, Album album)
            throws DuracionInvalidaException, AlbumCompletoException {
        return album.agregarCancion(titulo, duracion, artista, genero);
    }

    public void agregarContenidoCatalogo(Contenido contenido) { catalogo.add(contenido); }

    public ArrayList<Cancion> getCanciones() {
        ArrayList<Cancion> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Cancion) res.add((Cancion) c);
        return res;
    }

    // ====================== Creadores / Podcasts ======================
    public Creador registrarCreador(String nombreCanal, String nombre, String descripcion) {
        Creador c = new Creador(nombreCanal, nombre, descripcion);
        creadores.put(c.getId(), c);
        return c;
    }

    public void registrarCreador(Creador creador) {
        creadores.put(creador.getId(), creador);
    }

    public Podcast crearPodcast(String titulo, int duracion, Creador creador, int numEpisodio, int temporada, CategoriaPodcast categoria)
            throws DuracionInvalidaException, LimiteEpisodiosException {
        Podcast p = new Podcast(titulo, duracion, creador, numEpisodio, temporada, categoria);
        catalogo.add(p);
        return p;
    }

    public ArrayList<Podcast> getPodcasts() {
        ArrayList<Podcast> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Podcast) res.add((Podcast) c);
        return res;
    }

    public ArrayList<Creador> getTodosLosCreadores() { return new ArrayList<>(creadores.values()); }

    // ====================== Playlists públicas ======================
    public Playlist crearPlaylistPublica(String nombre, Usuario creador) {
        Playlist p = new Playlist(nombre, creador, true, "");
        playlistsPublicas.add(p);
        return p;
    }

    public ArrayList<Playlist> getPlaylistsPublicas() { return new ArrayList<>(playlistsPublicas); }

    // ====================== Búsquedas ======================
    public ArrayList<Contenido> buscarContenido(String termino) throws ContenidoNoEncontradoException {
        ArrayList<Contenido> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c.getTitulo().toLowerCase().contains(termino.toLowerCase())) res.add(c);
        if (res.isEmpty()) throw new ContenidoNoEncontradoException("No se encontró contenido: " + termino);
        return res;
    }

    public ArrayList<Cancion> buscarPorGenero(GeneroMusical genero) throws ContenidoNoEncontradoException {
        ArrayList<Cancion> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Cancion cancion && cancion.getGenero() == genero) res.add(cancion);
        if (res.isEmpty()) throw new ContenidoNoEncontradoException("No se encontró contenido del género: " + genero);
        return res;
    }

    public ArrayList<Podcast> buscarPorCategoria(CategoriaPodcast categoria) throws ContenidoNoEncontradoException {
        ArrayList<Podcast> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Podcast p && p.getCategoria() == categoria) res.add(p);
        if (res.isEmpty()) throw new ContenidoNoEncontradoException("No se encontró podcast de categoría: " + categoria);
        return res;
    }

    public ArrayList<Contenido> obtenerTopContenidos(int cantidad) {
        // Simple: retorna los primeros 'cantidad' contenidos del catálogo
        return new ArrayList<>(catalogo.subList(0, Math.min(cantidad, catalogo.size())));
    }

    // ====================== Anuncios ======================
    public Anuncio obtenerAnuncioAleatorio() {
        if (anuncios.isEmpty()) return null;
        int idx = (int) (Math.random() * anuncios.size());
        Anuncio a = anuncios.get(idx);
        if (!a.puedeMostrarse()) return null;
        return a;
    }

    public void incrementarAnunciosReproducidos() { totalAnunciosReproducidos++; }

    // ====================== Estadísticas ======================
    public String obtenerEstadisticasGenerales() { return "Usuarios: " + usuarios.size() + ", Contenido: " + catalogo.size(); }

    // ====================== Getters básicos ======================
    public String getNombre() { return nombre; }
    public ArrayList<Contenido> getCatalogo() { return new ArrayList<>(catalogo); }
    public HashMap<String, Artista> getArtistas() { return new HashMap<>(artistas); }
    public HashMap<String, Creador> getCreadores() { return new HashMap<>(creadores); }
    public ArrayList<Anuncio> getAnuncios() { return new ArrayList<>(anuncios); }
    public RecomendadorIA getRecomendador() { return recomendador; }
    public int getTotalUsuarios() { return usuarios.size(); }
    public int getTotalContenido() { return catalogo.size(); }
    public int getTotalAnunciosReproducidos() { return totalAnunciosReproducidos; }

    @Override
    public String toString() { return "Plataforma{" + "nombre='" + nombre + '\'' + '}'; }
}
