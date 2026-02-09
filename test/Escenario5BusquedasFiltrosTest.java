import enums.CategoriaPodcast;
import enums.GeneroMusical;
import excepciones.plataforma.ArtistaNoEncontradoException;
import excepciones.plataforma.ContenidoNoEncontradoException;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.artistas.Creador;
import modelo.contenido.Cancion;
import modelo.contenido.Contenido;
import modelo.contenido.Podcast;
import modelo.plataforma.Plataforma;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 5: Búsquedas y Filtros
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario5BusquedasFiltrosTest {

    private static Plataforma plataforma;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");

        // Crear artistas
        Artista badBunny = plataforma.registrarArtista("Bad Bunny", "Benito", "Puerto Rico", true);
        Artista shakira = plataforma.registrarArtista("Shakira", "Shakira", "Colombia", true);
        Artista taylor = plataforma.registrarArtista("Taylor Swift", "Taylor", "USA", true);

        // Crear álbumes y canciones con diferentes géneros
        Album albumReggaeton = plataforma.crearAlbum(badBunny, "Reggaeton Album", new Date());
        albumReggaeton.crearCancion("Moscow Mule", 200, GeneroMusical.REGGAETON).setReproducciones(1000000);
        albumReggaeton.crearCancion("Tití Me Preguntó", 210, GeneroMusical.REGGAETON).setReproducciones(900000);
        albumReggaeton.crearCancion("Me Porto Bonito", 180, GeneroMusical.REGGAETON).setReproducciones(800000);
        for (Cancion c : albumReggaeton.getCanciones()) {
            plataforma.agregarContenidoCatalogo(c);
        }

        Album albumPop = plataforma.crearAlbum(taylor, "Pop Album", new Date());
        albumPop.crearCancion("Anti-Hero", 190, GeneroMusical.POP).setReproducciones(2000000);
        albumPop.crearCancion("Lavender Haze", 200, GeneroMusical.POP).setReproducciones(500000);
        for (Cancion c : albumPop.getCanciones()) {
            plataforma.agregarContenidoCatalogo(c);
        }

        // Crear creadores y podcasts
        Creador creador1 = plataforma.registrarCreador("Tech Podcast", "Tech Host", "Tecnología");
        Creador creador2 = plataforma.registrarCreador("Crime Stories", "Crime Host", "Crímenes");

        plataforma.crearPodcast("Intro Tech", 3600, creador1, 1, 1, CategoriaPodcast.TECNOLOGIA).setReproducciones(100000);
        plataforma.crearPodcast("AI Future", 4000, creador1, 2, 1, CategoriaPodcast.TECNOLOGIA).setReproducciones(150000);
        plataforma.crearPodcast("True Crime 1", 5000, creador2, 1, 1, CategoriaPodcast.TRUE_CRIME).setReproducciones(200000);
        plataforma.crearPodcast("True Crime 2", 5500, creador2, 2, 1, CategoriaPodcast.TRUE_CRIME).setReproducciones(250000);
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Buscar por término ==========
    @Test
    @Order(1)
    @DisplayName("5.1 - Buscar contenido por término")
    void testBuscarPorTermino() throws ContenidoNoEncontradoException {
        ArrayList<Contenido> resultados = plataforma.buscarContenido("Anti");

        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(c -> c.getTitulo().contains("Anti")));
    }

    // ========== TEST 2: Buscar por género ==========
    @Test
    @Order(2)
    @DisplayName("5.2 - Buscar canciones por género REGGAETON")
    void testBuscarPorGenero() throws ContenidoNoEncontradoException {
        ArrayList<Cancion> reggaeton = plataforma.buscarPorGenero(GeneroMusical.REGGAETON);

        assertFalse(reggaeton.isEmpty());
        assertEquals(3, reggaeton.size());

        for (Cancion c : reggaeton) {
            assertEquals(GeneroMusical.REGGAETON, c.getGenero());
        }
    }

    // ========== TEST 3: Buscar por categoría ==========
    @Test
    @Order(3)
    @DisplayName("5.3 - Buscar podcasts por categoría TRUE_CRIME")
    void testBuscarPorCategoria() throws ContenidoNoEncontradoException {
        ArrayList<Podcast> trueCrime = plataforma.buscarPorCategoria(CategoriaPodcast.TRUE_CRIME);

        assertFalse(trueCrime.isEmpty());
        assertEquals(2, trueCrime.size());

        for (Podcast p : trueCrime) {
            assertEquals(CategoriaPodcast.TRUE_CRIME, p.getCategoria());
        }
    }

    // ========== TEST 4: Buscar término inexistente ==========
    @Test
    @Order(4)
    @DisplayName("5.4 - Buscar término inexistente lanza ContenidoNoEncontradoException")
    void testBuscarTerminoInexistente() {
        assertThrows(ContenidoNoEncontradoException.class, () -> {
            plataforma.buscarContenido("xyznoexiste123abc");
        });
    }

    // ========== TEST 5: Obtener top contenidos ==========
    @Test
    @Order(5)
    @DisplayName("5.5 - Obtener top 10 contenidos más reproducidos")
    void testObtenerTopContenidos() {
        ArrayList<Contenido> top = plataforma.obtenerTopContenidos(10);

        assertFalse(top.isEmpty());
        assertTrue(top.size() <= 10);

        // Verificar orden descendente
        int reprosAnterior = Integer.MAX_VALUE;
        for (Contenido c : top) {
            assertTrue(c.getReproducciones() <= reprosAnterior,
                "Los contenidos deberían estar ordenados de mayor a menor reproducciones");
            reprosAnterior = c.getReproducciones();
        }
    }

    // ========== TEST 6: Buscar artista existente ==========
    @Test
    @Order(6)
    @DisplayName("5.6 - Buscar artista por nombre")
    void testBuscarArtistaExistente() throws ArtistaNoEncontradoException {
        Artista artista = plataforma.buscarArtista("Bad Bunny");

        assertNotNull(artista);
        assertEquals("Bad Bunny", artista.getNombreArtistico());
    }

    // ========== TEST 7: Buscar artista inexistente ==========
    @Test
    @Order(7)
    @DisplayName("5.7 - Buscar artista inexistente lanza ArtistaNoEncontradoException")
    void testBuscarArtistaInexistente() {
        assertThrows(ArtistaNoEncontradoException.class, () -> {
            plataforma.buscarArtista("ArtistaQueNoExiste");
        });
    }

    // ========== TEST 8: Buscar por género inexistente ==========
    @Test
    @Order(8)
    @DisplayName("5.8 - Buscar género sin resultados lanza excepción")
    void testBuscarGenereSinResultados() {
        assertThrows(ContenidoNoEncontradoException.class, () -> {
            plataforma.buscarPorGenero(GeneroMusical.JAZZ);
        });
    }

    // ========== TEST 9: Top contenidos con menos elementos ==========
    @Test
    @Order(9)
    @DisplayName("5.9 - Top contenidos devuelve todos si hay menos del solicitado")
    void testTopConMenosElementos() {
        int totalContenido = plataforma.getCatalogo().size();
        ArrayList<Contenido> top = plataforma.obtenerTopContenidos(1000);

        assertEquals(totalContenido, top.size());
    }

    // ========== TEST 10: Búsqueda case-insensitive ==========
    @Test
    @Order(10)
    @DisplayName("5.10 - Búsqueda es case-insensitive")
    void testBusquedaCaseInsensitive() throws ContenidoNoEncontradoException {
        ArrayList<Contenido> resultados1 = plataforma.buscarContenido("anti");
        ArrayList<Contenido> resultados2 = plataforma.buscarContenido("ANTI");
        ArrayList<Contenido> resultados3 = plataforma.buscarContenido("Anti");

        assertEquals(resultados1.size(), resultados2.size());
        assertEquals(resultados2.size(), resultados3.size());
    }
}
