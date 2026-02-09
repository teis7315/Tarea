import enums.CategoriaPodcast;
import enums.GeneroMusical;
import enums.TipoSuscripcion;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.artistas.Creador;
import modelo.contenido.Cancion;
import modelo.plataforma.Plataforma;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 10: Estadísticas Finales
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario10EstadisticasFinalesTest {

    private static Plataforma plataforma;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");

        // Crear datos completos para estadísticas

        // Usuarios
        plataforma.registrarUsuarioPremium("Premium 1", "premium1@test.com", "password123", TipoSuscripcion.PREMIUM);
        plataforma.registrarUsuarioPremium("Premium 2", "premium2@test.com", "password123", TipoSuscripcion.FAMILIAR);
        plataforma.registrarUsuarioPremium("Premium 3", "premium3@test.com", "password123", TipoSuscripcion.ESTUDIANTE);
        plataforma.registrarUsuarioGratuito("Gratuito 1", "gratuito1@test.com", "password123");
        plataforma.registrarUsuarioGratuito("Gratuito 2", "gratuito2@test.com", "password123");

        // Artistas
        Artista artista1 = plataforma.registrarArtista("Artista Pop", "Pop", "USA", true);
        Artista artista2 = plataforma.registrarArtista("Artista Reggaeton", "Reggaeton", "PR", true);
        Artista artista3 = plataforma.registrarArtista("Artista Rock", "Rock", "UK", true);
        plataforma.registrarArtista("Artista Nuevo 1", "Nuevo", "MX", false);
        plataforma.registrarArtista("Artista Nuevo 2", "Nuevo", "ES", false);

        // Álbumes y canciones
        Album album1 = plataforma.crearAlbum(artista1, "Pop Album", new Date());
        for (int i = 1; i <= 5; i++) {
            Cancion c = album1.crearCancion("Pop Song " + i, 180, GeneroMusical.POP);
            c.setReproducciones(100000 * i);
            plataforma.agregarContenidoCatalogo(c);
        }

        Album album2 = plataforma.crearAlbum(artista2, "Reggaeton Album", new Date());
        for (int i = 1; i <= 5; i++) {
            Cancion c = album2.crearCancion("Reggaeton Song " + i, 200, GeneroMusical.REGGAETON);
            c.setReproducciones(150000 * i);
            plataforma.agregarContenidoCatalogo(c);
        }

        Album album3 = plataforma.crearAlbum(artista3, "Rock Album", new Date());
        for (int i = 1; i <= 5; i++) {
            Cancion c = album3.crearCancion("Rock Song " + i, 240, GeneroMusical.ROCK);
            c.setReproducciones(80000 * i);
            plataforma.agregarContenidoCatalogo(c);
        }

        // Creadores y podcasts
        Creador creador1 = plataforma.registrarCreador("Tech Podcast", "Tech", "Tecnología");
        creador1.setSuscriptores(1000000);

        Creador creador2 = plataforma.registrarCreador("Crime Podcast", "Crime", "Crímenes");
        creador2.setSuscriptores(800000);

        for (int i = 1; i <= 3; i++) {
            plataforma.crearPodcast("Tech Ep " + i, 3600, creador1, i, 1, CategoriaPodcast.TECNOLOGIA).setReproducciones(50000 * i);
            plataforma.crearPodcast("Crime Ep " + i, 4000, creador2, i, 1, CategoriaPodcast.TRUE_CRIME).setReproducciones(60000 * i);
        }
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Total usuarios por tipo ==========
    @Test
    @Order(1)
    @DisplayName("10.1 - Verificar total de usuarios por tipo")
    void testTotalUsuariosPorTipo() {
        assertEquals(5, plataforma.getTodosLosUsuarios().size());
        assertEquals(3, plataforma.getUsuariosPremium().size());
        assertEquals(2, plataforma.getUsuariosGratuitos().size());
    }

    // ========== TEST 2: Total contenido (canciones vs podcasts) ==========
    @Test
    @Order(2)
    @DisplayName("10.2 - Verificar total de contenido por tipo")
    void testTotalContenidoPorTipo() {
        int totalCanciones = plataforma.getCanciones().size();
        int totalPodcasts = plataforma.getPodcasts().size();
        int totalCatalogo = plataforma.getCatalogo().size();

        assertEquals(15, totalCanciones);
        assertEquals(6, totalPodcasts);
        assertEquals(totalCanciones + totalPodcasts, totalCatalogo);
    }

    // ========== TEST 3: Artistas verificados vs no verificados ==========
    @Test
    @Order(3)
    @DisplayName("10.3 - Verificar artistas verificados y no verificados")
    void testArtistasVerificados() {
        assertEquals(5, plataforma.getArtistas().size());
        assertEquals(3, plataforma.getArtistasVerificados().size());
        assertEquals(2, plataforma.getArtistasNoVerificados().size());
    }

    // ========== TEST 4: Total álbumes ==========
    @Test
    @Order(4)
    @DisplayName("10.4 - Verificar total de álbumes")
    void testTotalAlbumes() {
        assertEquals(3, plataforma.getAlbumes().size());

        for (Album album : plataforma.getAlbumes()) {
            assertEquals(5, album.getNumCanciones());
        }
    }

    // ========== TEST 5: Total creadores ==========
    @Test
    @Order(5)
    @DisplayName("10.5 - Verificar total de creadores")
    void testTotalCreadores() {
        assertEquals(2, plataforma.getTodosLosCreadores().size());
    }

    // ========== TEST 6: Obtener estadísticas generales ==========
    @Test
    @Order(6)
    @DisplayName("10.6 - Obtener estadísticas generales de la plataforma")
    void testObtenerEstadisticasGenerales() {
        String estadisticas = plataforma.obtenerEstadisticasGenerales();

        assertNotNull(estadisticas);
        assertFalse(estadisticas.isEmpty());

        // Verificar que contiene información relevante
        assertTrue(estadisticas.contains("Usuario"));
        assertTrue(estadisticas.contains("Contenido"));
    }

    // ========== TEST 7: Precios de suscripciones ==========
    @Test
    @Order(7)
    @DisplayName("10.7 - Verificar precios de tipos de suscripción")
    void testPreciosSuscripciones() {
        assertEquals(0.0, TipoSuscripcion.GRATUITO.getPrecioMensual());
        assertEquals(9.99, TipoSuscripcion.PREMIUM.getPrecioMensual());
        assertEquals(14.99, TipoSuscripcion.FAMILIAR.getPrecioMensual());
        assertEquals(4.99, TipoSuscripcion.ESTUDIANTE.getPrecioMensual());
    }

    // ========== TEST 8: Artista con más reproducciones ==========
    @Test
    @Order(8)
    @DisplayName("10.8 - Identificar artista con más reproducciones")
    void testArtistaMasReproducciones() {
        Artista artistaMasPopular = null;
        int maxRepros = 0;

        for (Artista artista : plataforma.getArtistasVerificados()) {
            int totalRepros = artista.getTotalReproducciones();
            if (totalRepros > maxRepros) {
                maxRepros = totalRepros;
                artistaMasPopular = artista;
            }
        }

        assertNotNull(artistaMasPopular);
        assertTrue(maxRepros > 0);

        // Reggaeton debería ser el más popular (150000 * (1+2+3+4+5) = 2,250,000)
        assertEquals("Artista Reggaeton", artistaMasPopular.getNombreArtistico());
    }

    // ========== TEST 9: Creador con más suscriptores ==========
    @Test
    @Order(9)
    @DisplayName("10.9 - Identificar creador con más suscriptores")
    void testCreadorMasSuscriptores() {
        Creador creadorMasPopular = null;
        int maxSuscriptores = 0;

        for (Creador creador : plataforma.getTodosLosCreadores()) {
            if (creador.getSuscriptores() > maxSuscriptores) {
                maxSuscriptores = creador.getSuscriptores();
                creadorMasPopular = creador;
            }
        }

        assertNotNull(creadorMasPopular);
        assertEquals(1000000, maxSuscriptores);
        assertEquals("Tech Podcast", creadorMasPopular.getNombreCanal());
    }

    // ========== TEST 10: Calcular ingresos totales ==========
    @Test
    @Order(10)
    @DisplayName("10.10 - Calcular ingresos totales basado en suscripciones")
    void testCalcularIngresosTotales() {
        double ingresos = 0;

        for (var usuario : plataforma.getTodosLosUsuarios()) {
            ingresos += usuario.getSuscripcion().getPrecioMensual();
        }

        // Premium(9.99) + Familiar(14.99) + Estudiante(4.99) + 2*Gratuito(0) = 29.97
        assertEquals(29.97, ingresos, 0.01);
    }

    // ========== TEST 11: Verificar nombre de la plataforma ==========
    @Test
    @Order(11)
    @DisplayName("10.11 - Verificar nombre de la plataforma")
    void testNombrePlataforma() {
        assertEquals("SoundWave Test", plataforma.getNombre());
    }

    // ========== TEST 12: Singleton funciona correctamente ==========
    @Test
    @Order(12)
    @DisplayName("10.12 - Patrón Singleton funciona correctamente")
    void testSingletonFunciona() {
        Plataforma otraReferencia = Plataforma.getInstancia();

        assertSame(plataforma, otraReferencia);
        assertEquals(plataforma.getTodosLosUsuarios().size(), otraReferencia.getTodosLosUsuarios().size());
    }

    // ========== TEST 13: Duración total de todo el contenido ==========
    @Test
    @Order(13)
    @DisplayName("10.13 - Calcular duración total de todo el contenido")
    void testDuracionTotalContenido() {
        int duracionTotal = 0;

        for (var contenido : plataforma.getCatalogo()) {
            duracionTotal += contenido.getDuracionSegundos();
        }

        assertTrue(duracionTotal > 0);

        // 15 canciones * ~200s + 6 podcasts * ~3800s ≈ 25,800 segundos
        assertTrue(duracionTotal > 20000);
    }

    // ========== TEST 14: Verificar anuncios disponibles ==========
    @Test
    @Order(14)
    @DisplayName("10.14 - Verificar que hay anuncios disponibles")
    void testAnunciosDisponibles() {
        var anuncios = plataforma.getAnuncios();

        assertNotNull(anuncios);
        assertFalse(anuncios.isEmpty());

        // Verificar que se puede obtener anuncio aleatorio
        var anuncioAleatorio = plataforma.obtenerAnuncioAleatorio();
        assertNotNull(anuncioAleatorio);
    }

    // ========== TEST 15: Recomendador está disponible ==========
    @Test
    @Order(15)
    @DisplayName("10.15 - Verificar que el recomendador está disponible")
    void testRecomendadorDisponible() {
        var recomendador = plataforma.getRecomendador();

        assertNotNull(recomendador);
    }
}
