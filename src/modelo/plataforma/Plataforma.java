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

        // Add only after successful creation
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

        // Add only after successful creation
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

    // ====================== Artistas, Álbumes, Canciones, Creadores, Playlists, Búsquedas ======================
    // (kept unchanged)
    // ...
}
