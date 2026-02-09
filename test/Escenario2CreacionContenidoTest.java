import enums.CategoriaPodcast;
import enums.GeneroMusical;
import excepciones.artista.ArtistaNoVerificadoException;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.artistas.Creador;
import modelo.contenido.Cancion;
import modelo.contenido.Podcast;
import modelo.plataforma.Plataforma;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 2: Creación de Contenido
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario2CreacionContenidoTest {

    private static Plataforma plataforma;

    @BeforeAll
    static void setUp() {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Crear artistas verificados ==========
    @Test
    @Order(1)
    @DisplayName("2.1 - Crear 3 artistas verificados")
    void testCrearArtistasVerificados() {
        Artista artista1 = plataforma.registrarArtista("Bad Bunny", "Benito Martínez", "Puerto Rico", true);
        Artista artista2 = plataforma.registrarArtista("Shakira", "Shakira Mebarak", "Colombia", true);
        Artista artista3 = plataforma.registrarArtista("Taylor Swift", "Taylor Swift", "USA", true);

        assertNotNull(artista1);
        assertNotNull(artista2);
        assertNotNull(artista3);

        assertTrue(artista1.isVerificado());
        assertTrue(artista2.isVerificado());
        assertTrue(artista3.isVerificado());

        assertEquals(3, plataforma.getArtistasVerificados().size());
    }

    // ========== TEST 2: Crear artistas no verificados ==========
    @Test
    @Order(2)
    @DisplayName("2.2 - Crear 2 artistas no verificados")
    void testCrearArtistasNoVerificados() {
        Artista artista1 = plataforma.registrarArtista("Artista Emergente", "Juan Nuevo", "México", false);
        Artista artista2 = plataforma.registrarArtista("Nuevo Talento", "María Nueva", "España", false);

        assertNotNull(artista1);
        assertNotNull(artista2);

        assertFalse(artista1.isVerificado());
        assertFalse(artista2.isVerificado());

        assertEquals(2, plataforma.getArtistasNoVerificados().size());
        assertEquals(5, plataforma.getArtistas().size());
    }

    // ========== TEST 3: Crear álbumes con canciones (COMPOSICIÓN) ==========
    @Test
    @Order(3)
    @DisplayName("2.3 - Crear álbumes con canciones usando COMPOSICIÓN")
    void testCrearAlbumesConCanciones() throws Exception {
        Artista badBunny = plataforma.getArtistasVerificados().get(0);

        // Crear álbum
        Album album = plataforma.crearAlbum(badBunny, "Un Verano Sin Ti", new Date());
        assertNotNull(album);
        assertEquals("Un Verano Sin Ti", album.getTitulo());
        assertEquals(badBunny, album.getArtista());

        // COMPOSICIÓN: El álbum crea sus canciones
        String[] titulos = {"Moscow Mule", "Después de la Playa", "Me Porto Bonito",
                           "Tití Me Preguntó", "Party", "Aguacero"};

        for (String titulo : titulos) {
            Cancion cancion = album.crearCancion(titulo, 200, GeneroMusical.REGGAETON);
            assertNotNull(cancion);
            assertEquals(album, cancion.getAlbum());
            assertEquals(badBunny, cancion.getArtista());
            plataforma.agregarContenidoCatalogo(cancion);
        }

        assertEquals(6, album.getNumCanciones());
        assertEquals(6, plataforma.getCanciones().size());
    }

    // ========== TEST 4: Artista no verificado no puede crear álbum ==========
    @Test
    @Order(4)
    @DisplayName("2.4 - Artista no verificado no puede crear álbum (ArtistaNoVerificadoException)")
    void testArtistaNoVerificadoNoPuedeCrearAlbum() {
        Artista artistaNoVerificado = plataforma.getArtistasNoVerificados().get(0);

        assertThrows(ArtistaNoVerificadoException.class, () -> {
            plataforma.crearAlbum(artistaNoVerificado, "Mi Primer Álbum", new Date());
        });
    }

    // ========== TEST 5: Crear creadores de podcasts ==========
    @Test
    @Order(5)
    @DisplayName("2.5 - Crear 2 creadores de podcasts")
    void testCrearCreadores() {
        Creador creador1 = plataforma.registrarCreador("The Wild Project", "Jordi Wild", "Podcast de entrevistas");
        creador1.setSuscriptores(2500000);

        Creador creador2 = plataforma.registrarCreador("Leyendas Legendarias", "Bsjf", "Podcast de crímenes");
        creador2.setSuscriptores(1800000);

        assertNotNull(creador1);
        assertNotNull(creador2);

        assertEquals(2500000, creador1.getSuscriptores());
        assertEquals(2, plataforma.getTodosLosCreadores().size());
    }

    // ========== TEST 6: Crear episodios de podcast ==========
    @Test
    @Order(6)
    @DisplayName("2.6 - Crear episodios de podcast para cada creador")
    void testCrearPodcasts() throws Exception {
        Creador creador1 = plataforma.getTodosLosCreadores().get(0);
        Creador creador2 = plataforma.getTodosLosCreadores().get(1);

        // Crear 5 episodios para creador1
        for (int i = 1; i <= 5; i++) {
            Podcast p = plataforma.crearPodcast("Episodio " + i, 3600, creador1, i, 1, CategoriaPodcast.ENTRETENIMIENTO);
            assertNotNull(p);
            assertEquals(creador1, p.getCreador());
        }

        // Crear 5 episodios para creador2
        for (int i = 1; i <= 5; i++) {
            Podcast p = plataforma.crearPodcast("Crimen " + i, 4200, creador2, i, 1, CategoriaPodcast.TRUE_CRIME);
            assertNotNull(p);
            assertEquals(CategoriaPodcast.TRUE_CRIME, p.getCategoria());
        }

        assertEquals(10, plataforma.getPodcasts().size());
        assertEquals(5, creador1.getNumEpisodios());
        assertEquals(5, creador2.getNumEpisodios());
    }

    // ========== TEST 7: Verificar catálogo total ==========
    @Test
    @Order(7)
    @DisplayName("2.7 - Verificar contenido total en catálogo")
    void testVerificarCatalogo() {
        assertTrue(plataforma.getCanciones().size() >= 6);
        assertEquals(10, plataforma.getPodcasts().size());
        assertTrue(plataforma.getCatalogo().size() >= 16);
    }
}
