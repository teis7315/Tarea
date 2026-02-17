package modelo.plataforma;
import enums.GeneroMusical;
import enums.TipoAnuncion;
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

        this.anuncios.add(new Anuncio("Empresa de prueba", TipoAnuncion.AUDIO, 100.0));

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

    // ====================== Plataforma Info ======================
    public String getNombre() {
        return nombre;
    }

    public ArrayList<Anuncio> getAnuncios() {
        return new ArrayList<>(anuncios);
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
        ArrayList<Artista> verificados = new ArrayList<>();
        for (Artista a : artistas.values()) {
            if (a.isVerificado()) {
                verificados.add(a);
            }
        }
        return verificados;
    }


    public ArrayList<Artista> getArtistasNoVerificados() {
        ArrayList<Artista> res = new ArrayList<>();
        for (Artista a : artistas.values()) if (!a.isVerificado()) res.add(a);
        return res;
    }

    public Artista buscarArtista(String nombre) throws ArtistaNoEncontradoException {
        Artista a = artistas.get(nombre);
        if (a == null) {
            throw new ArtistaNoEncontradoException("Artista '" + nombre + "' no encontrado");
        }
        return a;
    }


    // ====================== Álbumes / Canciones ======================
    public Album crearAlbum(Artista artista, String nombre, Date fecha) throws ArtistaNoVerificadoException, AlbumYaExisteException {
        Album album = artista.crearAlbum(nombre, fecha);
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

    public Podcast crearPodcast(String nombre, int duracion, Creador creador, int episodio, int temporada, CategoriaPodcast categoria)
            throws DuracionInvalidaException, excepciones.artista.LimiteEpisodiosException {

        Podcast p = new Podcast(nombre, duracion, creador, episodio, temporada, categoria);

        // 1. Agregar al catálogo
        catalogo.add(p);

        // 2. Registrar el podcast en el creador
        creador.publicarPodcast(p);

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

    public ArrayList<Contenido> buscarContenido(String texto) throws ContenidoNoEncontradoException {
        ArrayList<Contenido> res = new ArrayList<>();
        for (Contenido c : catalogo)
            if (c.getNombre().toLowerCase().contains(texto.toLowerCase()))
                res.add(c);
        if (res.isEmpty()) {
            throw new ContenidoNoEncontradoException("No se encontró contenido para: " + texto);
        }
        return res;
    }


    public ArrayList<Cancion> buscarPorGenero(GeneroMusical genero) throws ContenidoNoEncontradoException {
        ArrayList<Cancion> res = new ArrayList<>();
        for (Contenido c : catalogo)
            if (c instanceof Cancion && ((Cancion)c).getGenero() == genero)
                res.add((Cancion)c);

        if (res.isEmpty()) {
            throw new ContenidoNoEncontradoException("No se encontraron canciones del género: " + genero);
        }
        return res;
    }


    public ArrayList<Podcast> buscarPorCategoria(CategoriaPodcast categoria) {
        ArrayList<Podcast> res = new ArrayList<>();
        for (Contenido c : catalogo) if (c instanceof Podcast && ((Podcast)c).getCategoria() == categoria) res.add((Podcast)c);
        return res;
    }

    public ArrayList<Contenido> obtenerTopContenidos(int n) {
        ArrayList<Contenido> res = new ArrayList<>(catalogo);
        res.sort((c1, c2) -> Integer.compare(c2.getReproducciones(), c1.getReproducciones()));
        return new ArrayList<>(res.subList(0, Math.min(n, res.size())));
    }


    public String obtenerEstadisticasGenerales() {
        int numUsuarios = usuarios.size();
        int numContenidos = catalogo.size();
        int numArtistas = artistas.size();
        int numPlaylists = playlistsPublicas.size(); // corregido

        return "Estadísticas de la plataforma:\n" +
                "Usuarios registrados: " + numUsuarios + "\n" +
                "Contenidos disponibles: " + numContenidos + "\n" +
                "Artistas: " + numArtistas + "\n" +
                "Playlists: " + numPlaylists;
    }



    // ====================== Playlists ======================
    public Playlist crearPlaylistPublica(String nombre, UsuarioGratuito creador) {
        Playlist p = new Playlist(nombre, creador);
        playlistsPublicas.add(p);
        return p;
    }

    public ArrayList<Playlist> getPlaylistsPublicas() {
        return new ArrayList<>(playlistsPublicas);
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
