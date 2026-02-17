package modelo.usuarios;

import enums.TipoSuscripcion;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.usuario.AnuncioRequeridoException;
import excepciones.usuario.EmailInvalidoException;
import excepciones.usuario.LimiteDiarioAlcanzadoException;
import excepciones.usuario.PasswordDebilException;
import modelo.contenido.Contenido;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class Usuario {
    protected String id;
    protected String nombre;
    protected String email;
    protected String password;
    protected TipoSuscripcion suscripcion;
    protected ArrayList<modelo.plataforma.Playlist> misPlaylists;
    protected ArrayList<Contenido> historial;
    protected Date fechaRegistro;
    protected ArrayList<modelo.plataforma.Playlist> playlistsSeguidas;
    protected ArrayList<Contenido> contenidosLiked;

    private static final String EMAIL_REGEX = "^[\\w+.-]+@[\\w.-]+\\.\\w+$";
    private static final int HISTORIAL_MAX = 1000;

    public Usuario(String nombre, String email, String password, TipoSuscripcion suscripcion)
            throws EmailInvalidoException, PasswordDebilException {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.suscripcion = suscripcion;
        this.misPlaylists = new ArrayList<>();
        this.historial = new ArrayList<>();
        this.fechaRegistro = new Date();
        this.playlistsSeguidas = new ArrayList<>();
        this.contenidosLiked = new ArrayList<>();

        if (!validarEmail()) {
            throw new EmailInvalidoException("Email inválido: " + email);
        }
        if (!validarPassword()) {
            throw new PasswordDebilException("Password muy débil. Requiere al menos 8 caracteres, mayúscula y número");
        }
    }

    public abstract void reproducir(Contenido contenido)
            throws ContenidoNoDisponibleException, LimiteDiarioAlcanzadoException, AnuncioRequeridoException;

    public modelo.plataforma.Playlist crearPlaylist(String nombrePlaylist) {
        modelo.plataforma.Playlist playlist = new modelo.plataforma.Playlist(nombrePlaylist, this, false, "");
        misPlaylists.add(playlist);
        return playlist;
    }

    public void seguirPlaylist(modelo.plataforma.Playlist playlist) {
        if (!playlistsSeguidas.contains(playlist)) {
            playlistsSeguidas.add(playlist);
            playlist.incrementarSeguidores();
        }
    }

    public void dejarDeSeguirPlaylist(modelo.plataforma.Playlist playlist) {
        if (playlistsSeguidas.remove(playlist)) {
            playlist.decrementarSeguidores();
        }
    }

    public void darLike(Contenido contenido) {
        if (!contenidosLiked.contains(contenido)) {
            contenidosLiked.add(contenido);
            contenido.agregarLike();
        }
    }

    public void quitarLike(Contenido contenido) {
        contenidosLiked.remove(contenido);
    }

    public boolean validarEmail() throws EmailInvalidoException {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        if (!pattern.matcher(email).matches()) {
            throw new EmailInvalidoException("Formato de email inválido");
        }
        return true;
    }

    public boolean validarPassword() throws PasswordDebilException {
        if (password == null || password.length() < 8) {
            throw new PasswordDebilException("Password debe tener al menos 8 caracteres");
        }
        boolean tieneNumero = password.matches(".*\\d.*");
        boolean tieneMayuscula = password.matches(".*[A-Z].*");

        if (!tieneNumero || !tieneMayuscula) {
            throw new PasswordDebilException("Password debe contener al menos un número y una mayúscula");
        }
        return true;
    }

    public void agregarAlHistorial(Contenido contenido) {
        if (historial.size() >= HISTORIAL_MAX) {
            historial.remove(0); // remove oldest
        }
        historial.add(contenido);
    }

    public void limpiarHistorial() {
        historial.clear();
    }

    public boolean esPremium() {
        return suscripcion != TipoSuscripcion.GRATUITO;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws EmailInvalidoException {
        String oldEmail = this.email;
        this.email = email;
        if (!validarEmail()) {
            this.email = oldEmail;
            throw new EmailInvalidoException("Formato de email inválido");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws PasswordDebilException {
        String oldPassword = this.password;
        this.password = password;
        if (!validarPassword()) {
            this.password = oldPassword;
            throw new PasswordDebilException("Password inválida");
        }
    }

    public TipoSuscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(TipoSuscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public ArrayList<modelo.plataforma.Playlist> getMisPlaylists() {
        return new ArrayList<>(misPlaylists);
    }

    public ArrayList<Contenido> getHistorial() {
        return new ArrayList<>(historial);
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public ArrayList<modelo.plataforma.Playlist> getPlaylistsSeguidas() {
        return new ArrayList<>(playlistsSeguidas);
    }

    public ArrayList<Contenido> getContenidosLiked() {
        return new ArrayList<>(contenidosLiked);
    }

    @Override
    public String toString() {
        return "Usuario{" + "id='" + id + '\'' + ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' + ", suscripcion=" + suscripcion + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
