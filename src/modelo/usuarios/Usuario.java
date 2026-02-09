package modelo.usuarios;

import enums.TipoSuscripcion;
import excepciones.usuario.EmailInvalidoException;

import java.util.ArrayList;
import java.util.Date;

public abstract class Usuario {
    protected String id;
    protected String nombre;
    protected String email;
    protected String password;
    protected TipoSuscripcion suscripcion;
    protected ArrayList<Playlist> misPlaylists;
    protected ArrayList<Contenido> historial;
    protected Date fechaRegistro;

    public abstract void reproducir(Contenido contenido);
    public void crearPlaylist(String nombre){

    }
    public void darLike(Contenido contenido){
        contenido.likes++;
    }
    public void validarEmail() throws EmailInvalidoException(){


        }

    }
    public void validarPassword(){
    if(password.length()<8){
        throw PasswordDebilException("Contraseña debil");
    }
    }
    public void agregarAlHistorial(Contenido contenido){
        historial.add(contenido);
    }
}
