import enums.GeneroMusical;
import excepciones.artista.AlbumCompletoException;
import excepciones.artista.AlbumYaExisteException;
import excepciones.playlist.CancionNoEncontradaException;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.contenido.Cancion;
import modelo.plataforma.Plataforma;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 7: Gestión de Álbumes y Artistas
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario7AlbumesArtistasTest {

    private static Plataforma plataforma;
    private static Artista artista;
    private static Album album;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");

        // Crear artista verificado
        artista = plataforma.registrarArtista("Test Artist", "Test", "Test", true);
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Crear nuevo álbum ==========
    @Test
    @Order(1)
    @DisplayName("7.1 - Crear nuevo álbum para artista verificado")
    void testCrearNuevoAlbum() throws Exception {
        album = plataforma.crearAlbum(artista, "Nuevo Álbum 2024", new Date());

        assertNotNull(album);
        assertEquals("Nuevo Álbum 2024", album.getTitulo());
        assertEquals(artista, album.getArtista());
        assertEquals(0, album.getNumCanciones());

        assertTrue(plataforma.getAlbumes().contains(album));
    }

    // ========== TEST 2: Álbum duplicado ==========
    @Test
    @Order(2)
    @DisplayName("7.2 - Crear álbum duplicado lanza AlbumYaExisteException")
    void testCrearAlbumDuplicado() {
        assertThrows(AlbumYaExisteException.class, () -> {
            plataforma.crearAlbum(artista, "Nuevo Álbum 2024", new Date());
        });
    }

    // ========== TEST 3: Agregar 20 canciones usando COMPOSICIÓN ==========
    @Test
    @Order(3)
    @DisplayName("7.3 - Agregar 20 canciones al álbum usando COMPOSICIÓN")
    void testAgregar20Canciones() throws Exception {
        for (int i = 1; i <= 20; i++) {
            Cancion c = album.crearCancion("Canción " + i, 180 + i * 5, GeneroMusical.POP);
            assertNotNull(c);
            assertEquals(album, c.getAlbum());
            assertEquals(artista, c.getArtista());
            plataforma.agregarContenidoCatalogo(c);
        }

        assertEquals(20, album.getNumCanciones());
        assertEquals(20, album.getMaxCanciones());
    }

    // ========== TEST 4: Álbum completo no acepta más canciones ==========
    @Test
    @Order(4)
    @DisplayName("7.4 - Álbum completo lanza AlbumCompletoException")
    void testAlbumCompleto() {
        assertThrows(AlbumCompletoException.class, () -> {
            album.crearCancion("Canción Extra", 200, GeneroMusical.POP);
        });
    }

    // ========== TEST 5: Eliminar canción por posición ==========
    @Test
    @Order(5)
    @DisplayName("7.5 - Eliminar canción del álbum por posición")
    void testEliminarCancionPorPosicion() throws CancionNoEncontradaException {
        int cancionesAntes = album.getNumCanciones();

        album.eliminarCancion(1); // Eliminar la primera

        assertEquals(cancionesAntes - 1, album.getNumCanciones());
    }

    // ========== TEST 6: Eliminar canción con posición inválida ==========
    @Test
    @Order(6)
    @DisplayName("7.6 - Eliminar canción con posición inválida lanza CancionNoEncontradaException")
    void testEliminarCancionPosicionInvalida() {
        assertThrows(CancionNoEncontradaException.class, () -> {
            album.eliminarCancion(100);
        });

        assertThrows(CancionNoEncontradaException.class, () -> {
            album.eliminarCancion(0);
        });

        assertThrows(CancionNoEncontradaException.class, () -> {
            album.eliminarCancion(-1);
        });
    }

    // ========== TEST 7: Duración total del álbum ==========
    @Test
    @Order(7)
    @DisplayName("7.7 - Calcular duración total del álbum")
    void testDuracionTotalAlbum() {
        int duracionCalculada = 0;
        for (Cancion c : album.getCanciones()) {
            duracionCalculada += c.getDuracionSegundos();
        }

        assertEquals(duracionCalculada, album.getDuracionTotal());
        assertNotNull(album.getDuracionTotalFormateada());
        assertTrue(album.getDuracionTotalFormateada().contains(":"));
    }

    // ========== TEST 8: Top canciones del artista ==========
    @Test
    @Order(8)
    @DisplayName("7.8 - Obtener top 5 canciones del artista")
    void testTopCancionesArtista() {
        // Establecer reproducciones diferentes
        ArrayList<Cancion> canciones = album.getCanciones();
        for (int i = 0; i < canciones.size(); i++) {
            canciones.get(i).setReproducciones((i + 1) * 10000);
        }

        ArrayList<Cancion> top5 = artista.obtenerTopCanciones(5);

        assertNotNull(top5);
        assertTrue(top5.size() <= 5);

        // Verificar orden descendente
        if (top5.size() > 1) {
            for (int i = 0; i < top5.size() - 1; i++) {
                assertTrue(top5.get(i).getReproducciones() >= top5.get(i + 1).getReproducciones());
            }
        }
    }

    // ========== TEST 9: Ordenar álbum por popularidad ==========
    @Test
    @Order(9)
    @DisplayName("7.9 - Ordenar canciones del álbum por popularidad")
    void testOrdenarAlbumPorPopularidad() {
        album.ordenarPorPopularidad();

        ArrayList<Cancion> canciones = album.getCanciones();
        for (int i = 0; i < canciones.size() - 1; i++) {
            assertTrue(canciones.get(i).getReproducciones() >= canciones.get(i + 1).getReproducciones(),
                "Las canciones deberían estar ordenadas de mayor a menor reproducciones");
        }
    }

    // ========== TEST 10: Total reproducciones del álbum ==========
    @Test
    @Order(10)
    @DisplayName("7.10 - Calcular total de reproducciones del álbum")
    void testTotalReproduccionesAlbum() {
        int totalCalculado = 0;
        for (Cancion c : album.getCanciones()) {
            totalCalculado += c.getReproducciones();
        }

        assertEquals(totalCalculado, album.getTotalReproducciones());
    }

    // ========== TEST 11: Obtener canción por posición ==========
    @Test
    @Order(11)
    @DisplayName("7.11 - Obtener canción por posición")
    void testObtenerCancionPorPosicion() throws CancionNoEncontradaException {
        Cancion cancion = album.getCancion(1);

        assertNotNull(cancion);
        assertEquals(album.getCanciones().get(0), cancion);
    }

    // ========== TEST 12: Promedio reproducciones artista ==========
    @Test
    @Order(12)
    @DisplayName("7.12 - Calcular promedio de reproducciones del artista")
    void testPromedioReproduccionesArtista() {
        double promedio = artista.calcularPromedioReproducciones();

        assertTrue(promedio >= 0);

        // Verificar cálculo manual
        int total = 0;
        int count = artista.getDiscografia().size();
        for (Cancion c : artista.getDiscografia()) {
            total += c.getReproducciones();
        }

        if (count > 0) {
            assertEquals((double) total / count, promedio, 0.01);
        }
    }
}
