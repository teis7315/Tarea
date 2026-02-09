import enums.CategoriaPodcast;
import enums.GeneroMusical;
import enums.TipoSuscripcion;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.contenido.LetraNoDisponibleException;
import excepciones.contenido.TranscripcionNoDisponibleException;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.artistas.Creador;
import modelo.contenido.Cancion;
import modelo.contenido.Podcast;
import modelo.plataforma.Plataforma;
import modelo.usuarios.UsuarioPremium;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 9: Contenido No Disponible
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario9ContenidoNoDisponibleTest {

    private static Plataforma plataforma;
    private static UsuarioPremium user;
    private static Cancion cancion;
    private static Cancion cancionSinLetra;
    private static Podcast podcast;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");

        // Crear usuario
        user = plataforma.registrarUsuarioPremium("Test User", "test@test.com", "password123", TipoSuscripcion.PREMIUM);

        // Crear contenido
        Artista artista = plataforma.registrarArtista("Test Artist", "Test", "Test", true);
        Album album = plataforma.crearAlbum(artista, "Test Album", new Date());

        cancion = album.crearCancion("Test Song", 200, GeneroMusical.POP);
        cancion.setLetra("Esta es la letra de la canción de prueba...");
        plataforma.agregarContenidoCatalogo(cancion);

        cancionSinLetra = album.crearCancion("Song Without Lyrics", 180, GeneroMusical.ROCK);
        // No establecemos letra
        plataforma.agregarContenidoCatalogo(cancionSinLetra);

        Creador creador = plataforma.registrarCreador("Test Channel", "Test", "Test");
        podcast = plataforma.crearPodcast("Test Episode", 3600, creador, 1, 1, CategoriaPodcast.TECNOLOGIA);
        // No establecemos transcripción
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Marcar contenido como no disponible ==========
    @Test
    @Order(1)
    @DisplayName("9.1 - Marcar contenido como no disponible")
    void testMarcarNoDisponible() {
        assertTrue(cancion.isDisponible());

        cancion.marcarNoDisponible();

        assertFalse(cancion.isDisponible());
    }

    // ========== TEST 2: Reproducir contenido no disponible ==========
    @Test
    @Order(2)
    @DisplayName("9.2 - Reproducir contenido no disponible lanza ContenidoNoDisponibleException")
    void testReproducirContenidoNoDisponible() {
        assertFalse(cancion.isDisponible());

        assertThrows(ContenidoNoDisponibleException.class, () -> {
            user.reproducir(cancion);
        });
    }

    // ========== TEST 3: Marcar contenido como disponible ==========
    @Test
    @Order(3)
    @DisplayName("9.3 - Marcar contenido como disponible nuevamente")
    void testMarcarDisponible() {
        cancion.marcarDisponible();

        assertTrue(cancion.isDisponible());

        // Ahora debería poder reproducirse
        assertDoesNotThrow(() -> {
            user.reproducir(cancion);
        });
    }

    // ========== TEST 4: Obtener letra de canción sin letra ==========
    @Test
    @Order(4)
    @DisplayName("9.4 - Obtener letra de canción sin letra lanza LetraNoDisponibleException")
    void testObtenerLetraSinLetra() {
        assertNull(cancionSinLetra.getLetra());

        assertThrows(LetraNoDisponibleException.class, () -> {
            cancionSinLetra.obtenerLetra();
        });
    }

    // ========== TEST 5: Establecer y obtener letra ==========
    @Test
    @Order(5)
    @DisplayName("9.5 - Establecer letra y obtenerla correctamente")
    void testEstablecerYObtenerLetra() throws LetraNoDisponibleException {
        String letra = "Nueva letra de la canción...";

        cancionSinLetra.setLetra(letra);

        assertEquals(letra, cancionSinLetra.obtenerLetra());
        assertEquals(letra, cancionSinLetra.getLetra());
    }

    // ========== TEST 6: Obtener transcripción de podcast sin transcripción ==========
    @Test
    @Order(6)
    @DisplayName("9.6 - Obtener transcripción sin transcripción lanza TranscripcionNoDisponibleException")
    void testObtenerTranscripcionSinTranscripcion() {
        assertNull(podcast.getTranscripcion());

        assertThrows(TranscripcionNoDisponibleException.class, () -> {
            podcast.obtenerTranscripcion();
        });
    }

    // ========== TEST 7: Establecer y obtener transcripción ==========
    @Test
    @Order(7)
    @DisplayName("9.7 - Establecer transcripción y obtenerla correctamente")
    void testEstablecerYObtenerTranscripcion() throws TranscripcionNoDisponibleException {
        String transcripcion = "Transcripción completa del episodio...";

        podcast.setTranscripcion(transcripcion);

        assertEquals(transcripcion, podcast.obtenerTranscripcion());
        assertEquals(transcripcion, podcast.getTranscripcion());
    }

    // ========== TEST 8: Canción con letra obtenerLetra() funciona ==========
    @Test
    @Order(8)
    @DisplayName("9.8 - Canción con letra permite obtenerLetra()")
    void testObtenerLetraConLetra() throws LetraNoDisponibleException {
        assertNotNull(cancion.getLetra());

        String letra = cancion.obtenerLetra();

        assertNotNull(letra);
        assertFalse(letra.isEmpty());
    }

    // ========== TEST 9: Reproducir directamente canción no disponible ==========
    @Test
    @Order(9)
    @DisplayName("9.9 - Método reproducir() de Cancion valida disponibilidad")
    void testReproducirDirectoCancionNoDisponible() {
        Cancion otraCancion = plataforma.getCanciones().get(0);
        otraCancion.marcarNoDisponible();

        assertThrows(ContenidoNoDisponibleException.class, () -> {
            otraCancion.reproducir();
        });

        // Restaurar
        otraCancion.marcarDisponible();
    }

    // ========== TEST 10: Reproducir directamente podcast no disponible ==========
    @Test
    @Order(10)
    @DisplayName("9.10 - Método reproducir() de Podcast valida disponibilidad")
    void testReproducirDirectoPodcastNoDisponible() {
        podcast.marcarNoDisponible();

        assertThrows(ContenidoNoDisponibleException.class, () -> {
            podcast.reproducir();
        });

        // Restaurar
        podcast.marcarDisponible();
    }

    // ========== TEST 11: Estado inicial de disponibilidad ==========
    @Test
    @Order(11)
    @DisplayName("9.11 - Contenido nuevo está disponible por defecto")
    void testEstadoInicialDisponible() throws Exception {
        Artista artista = plataforma.getArtistasVerificados().get(0);
        Album album = artista.getAlbumes().get(0);

        Cancion nuevaCancion = album.crearCancion("Nueva Canción Test", 150, GeneroMusical.JAZZ);

        assertTrue(nuevaCancion.isDisponible());
    }

    // ========== TEST 12: Letra vacía también lanza excepción ==========
    @Test
    @Order(12)
    @DisplayName("9.12 - Letra vacía también lanza LetraNoDisponibleException")
    void testLetraVaciaLanzaExcepcion() throws Exception {
        Artista artista = plataforma.getArtistasVerificados().get(0);
        Album album = artista.getAlbumes().get(0);

        Cancion cancionLetraVacia = album.crearCancion("Sin Letra Vacía", 160, GeneroMusical.BLUES);
        cancionLetraVacia.setLetra("");

        assertThrows(LetraNoDisponibleException.class, () -> {
            cancionLetraVacia.obtenerLetra();
        });
    }
}
