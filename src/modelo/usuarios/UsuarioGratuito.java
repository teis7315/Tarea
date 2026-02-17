package modelo.usuarios;

import enums.TipoSuscripcion;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.usuario.AnuncioRequeridoException;
import excepciones.usuario.EmailInvalidoException;
import excepciones.usuario.LimiteDiarioAlcanzadoException;
import excepciones.usuario.PasswordDebilException;
import modelo.contenido.Contenido;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class UsuarioGratuito extends Usuario {
    private static final int LIMITE_DIARIO = 50;
    private static final int CANCIONES_ENTRE_ANUNCIOS = 3;
    private int anunciosEscuchados;
    private Date ultimoAnuncio;
    private int reproduccionesHoy;
    private int limiteReproducciones;
    private int cancionesSinAnuncio;
    private Date fechaUltimaReproduccion;

    public UsuarioGratuito(String nombre, String email, String password)
            throws EmailInvalidoException, PasswordDebilException {
        super(nombre, email, password, TipoSuscripcion.GRATUITO);
        this.anunciosEscuchados = 0;
        this.ultimoAnuncio = null;
        this.reproduccionesHoy = 0;
        this.limiteReproducciones = LIMITE_DIARIO;
        this.cancionesSinAnuncio = 0;
        this.fechaUltimaReproduccion = new Date();
    }

    @Override
    public void reproducir(Contenido contenido)
            throws ContenidoNoDisponibleException, LimiteDiarioAlcanzadoException, AnuncioRequeridoException {
        if (!contenido.isDisponible()) {
            throw new ContenidoNoDisponibleException("El contenido no está disponible");
        }

        if (!puedeReproducir()) {
            throw new LimiteDiarioAlcanzadoException("Se ha alcanzado el límite diario de " + LIMITE_DIARIO + " reproducciones");
        }

        if (debeVerAnuncio()) {
            throw new AnuncioRequeridoException("Se requiere ver un anuncio antes de continuar");
        }

        contenido.reproducir();
        reproduccionesHoy++;
        cancionesSinAnuncio++;
        fechaUltimaReproduccion = new Date();
        agregarAlHistorial(contenido);
    }

    public void verAnuncio() {
        verAnuncio(null);
    }

    public void verAnuncio(modelo.plataforma.Anuncio anuncio) {
        if (anuncio != null && anuncio.puedeMostrarse()) {
            anuncio.registrarImpresion();
        }
        anunciosEscuchados++;
        ultimoAnuncio = new Date();
        cancionesSinAnuncio = 0;
    }

    public boolean puedeReproducir() {
        if (reproduccionesHasChanged()) {
            reiniciarContadorDiario();
        }
        return reproduccionesHoy < limiteReproducciones;
    }

    public boolean debeVerAnuncio() {
        return cancionesSinAnuncio >= CANCIONES_ENTRE_ANUNCIOS;
    }

    public void reiniciarContadorDiario() {
        reproduccionesHoy = 0;
        cancionesSinAnuncio = 0;
    }

    private boolean reproduccionesHasChanged() {
        if (fechaUltimaReproduccion == null) {
            return false;
        }
        Calendar hoy = Calendar.getInstance();
        Calendar ultimoAcceso = Calendar.getInstance();
        ultimoAcceso.setTime(fechaUltimaReproduccion);

        return !hoy.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                .equals(ultimoAcceso.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
    }

    public int getReproduccionesRestantes() {
        if (reproduccionesHasChanged()) {
            return limiteReproducciones;
        }
        return limiteReproducciones - reproduccionesHoy;
    }

    public int getCancionesHastaAnuncio() {
        return Math.max(0, CANCIONES_ENTRE_ANUNCIOS - cancionesSinAnuncio);
    }

    // Getters y Setters
    public int getAnunciosEscuchados() {
        return anunciosEscuchados;
    }

    public Date getUltimoAnuncio() {
        return ultimoAnuncio;
    }

    public int getReproduccionesHoy() {
        return reproduccionesHoy;
    }

    public void setReproduccionesHoy(int reproduccionesHoy) {
        this.reproduccionesHoy = reproduccionesHoy;
    }

    public int getLimiteReproducciones() {
        return limiteReproducciones;
    }

    public int getCancionesSinAnuncio() {
        return cancionesSinAnuncio;
    }

    public void setCancionesSinAnuncio(int cancionesSinAnuncio) {
        this.cancionesSinAnuncio = cancionesSinAnuncio;
    }

    @Override
    public String toString() {
        return "UsuarioGratuito{" + "nombre='" + nombre + '\'' + ", email='" + email + '\'' +
               ", reproduccionesHoy=" + reproduccionesHoy + "/" + limiteReproducciones + '}';
    }
}
