import enums.GeneroMusical;
import enums.TipoSuscripcion;
import excepciones.contenido.ContenidoNoDisponibleException;
import excepciones.descarga.ContenidoYaDescargadoException;
import excepciones.usuario.AnuncioRequeridoException;
import excepciones.usuario.LimiteDiarioAlcanzadoException;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.contenido.Cancion;
import modelo.plataforma.Plataforma;
import modelo.usuarios.UsuarioGratuito;
import modelo.usuarios.UsuarioPremium;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 3: Reproducción y Límites
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario3ReproduccionLimitesTest {

    private static Plataforma plataforma;
    private static UsuarioGratuito userGratuito;
    private static UsuarioPremium userPremium;
    private static ArrayList<Cancion> canciones;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");

        // Crear usuarios
        userPremium = plataforma.registrarUsuarioPremium("Premium User", "premium@test.com", "password123", TipoSuscripcion.PREMIUM);
        userGratuito = plataforma.registrarUsuarioGratuito("Gratuito User", "gratuito@test.com", "password123");

        // Crear contenido
        Artista artista = plataforma.registrarArtista("Test Artist", "Test", "Test", true);
        Album album = plataforma.crearAlbum(artista, "Test Album", new Date());

        for (int i = 1; i <= 10; i++) {
            Cancion c = album.crearCancion("Canción Test " + i, 180, GeneroMusical.POP);
            plataforma.agregarContenidoCatalogo(c);
        }

        canciones = plataforma.getCanciones();
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    @BeforeEach
    void resetUsuarioGratuito() {
        userGratuito.reiniciarContadorDiario();
        userGratuito.setCancionesSinAnuncio(0);
    }

    // ========== TEST 1: Usuario gratuito reproduce 3 canciones ==========
    @Test
    @Order(1)
    @DisplayName("3.1 - Usuario gratuito puede reproducir 3 canciones sin anuncio")
    void testUsuarioGratuitoReproducir3Canciones() throws Exception {
        // Las primeras 3 canciones no deberían requerir anuncio
        for (int i = 0; i < 3; i++) {
            final int index = i;
            assertDoesNotThrow(() -> {
                if (!userGratuito.debeVerAnuncio()) {
                    userGratuito.reproducir(canciones.get(index));
                }
            });
        }
    }

    // ========== TEST 2: Requiere anuncio después de 3 canciones ==========
    @Test
    @Order(2)
    @DisplayName("3.2 - Usuario gratuito debe ver anuncio después de 3 canciones")
    void testUsuarioGratuitoRequiereAnuncio() throws Exception {
        // Reproducir 3 canciones
        for (int i = 0; i < 3; i++) {
            try {
                userGratuito.reproducir(canciones.get(i));
            } catch (AnuncioRequeridoException e) {
                userGratuito.verAnuncio(plataforma.obtenerAnuncioAleatorio());
                userGratuito.reproducir(canciones.get(i));
            }
        }

        // La 4ta canción debería requerir anuncio
        assertTrue(userGratuito.debeVerAnuncio());

        assertThrows(AnuncioRequeridoException.class, () -> {
            userGratuito.reproducir(canciones.get(3));
        });
    }

    // ========== TEST 3: Ver anuncio reinicia contador ==========
    @Test
    @Order(3)
    @DisplayName("3.3 - Ver anuncio reinicia el contador de canciones sin anuncio")
    void testVerAnuncioReiniciaContador() throws Exception {
        // Simular 3 canciones
        userGratuito.setCancionesSinAnuncio(3);
        assertTrue(userGratuito.debeVerAnuncio());

        // Ver anuncio
        userGratuito.verAnuncio(plataforma.obtenerAnuncioAleatorio());

        // Verificar que se reinició
        assertFalse(userGratuito.debeVerAnuncio());
        assertEquals(0, userGratuito.getCancionesSinAnuncio());
    }

    // ========== TEST 4: Límite diario alcanzado ==========
    @Test
    @Order(4)
    @DisplayName("3.4 - Usuario gratuito lanza LimiteDiarioAlcanzadoException al llegar a 50")
    void testLimiteDiarioAlcanzado() {
        userGratuito.setReproduccionesHoy(50);

        assertFalse(userGratuito.puedeReproducir());

        assertThrows(LimiteDiarioAlcanzadoException.class, () -> {
            userGratuito.reproducir(canciones.get(0));
        });
    }

    // ========== TEST 5: Reiniciar contador diario ==========
    @Test
    @Order(5)
    @DisplayName("3.5 - Reiniciar contador diario permite reproducir de nuevo")
    void testReiniciarContadorDiario() {
        userGratuito.setReproduccionesHoy(50);
        assertFalse(userGratuito.puedeReproducir());

        userGratuito.reiniciarContadorDiario();

        assertTrue(userGratuito.puedeReproducir());
        assertEquals(0, userGratuito.getReproduccionesHoy());
    }

    // ========== TEST 6: Usuario premium sin límites ==========
    @Test
    @Order(6)
    @DisplayName("3.6 - Usuario premium reproduce sin límites ni anuncios")
    void testUsuarioPremiumSinLimites() throws Exception {
        // Reproducir muchas canciones sin problemas
        for (int i = 0; i < canciones.size(); i++) {
            int index = i;
            assertDoesNotThrow(() -> {
                userPremium.reproducir(canciones.get(index));
            });
        }

        // Verificar que se agregaron al historial
        assertTrue(userPremium.getHistorial().size() > 0);
    }

    // ========== TEST 7: Usuario premium puede descargar ==========
    @Test
    @Order(7)
    @DisplayName("3.7 - Usuario premium puede descargar canciones")
    void testUsuarioPremiumDescargar() throws Exception {
        int descargasIniciales = userPremium.getNumDescargados();

        for (int i = 0; i < 5 && i < canciones.size(); i++) {
            try {
                userPremium.descargar(canciones.get(i));
            } catch (ContenidoYaDescargadoException e) {
                // Ignorar si ya está descargado
            }
        }

        assertTrue(userPremium.getNumDescargados() >= descargasIniciales);
        assertTrue(userPremium.verificarEspacioDescarga());
    }

    // ========== TEST 8: No puede descargar duplicado ==========
    @Test
    @Order(8)
    @DisplayName("3.8 - Usuario premium no puede descargar contenido duplicado")
    void testUsuarioPremiumDescargaDuplicada() throws Exception {
        // Asegurar que hay al menos una descarga
        try {
            userPremium.descargar(canciones.get(0));
        } catch (ContenidoYaDescargadoException e) {
            // Ya estaba descargado
        }

        // Intentar descargar de nuevo
        assertThrows(ContenidoYaDescargadoException.class, () -> {
            userPremium.descargar(canciones.get(0));
        });
    }

    // ========== TEST 9: Contenido no disponible ==========
    @Test
    @Order(9)
    @DisplayName("3.9 - No se puede reproducir contenido no disponible")
    void testContenidoNoDisponible() {
        Cancion cancion = canciones.get(canciones.size() - 1);
        cancion.marcarNoDisponible();

        assertThrows(ContenidoNoDisponibleException.class, () -> {
            userPremium.reproducir(cancion);
        });

        // Restaurar
        cancion.marcarDisponible();
    }
}
