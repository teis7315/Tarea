import enums.CriterioOrden;
import enums.GeneroMusical;
import enums.TipoSuscripcion;
import excepciones.playlist.ContenidoDuplicadoException;
import excepciones.playlist.PlaylistVaciaException;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.contenido.Cancion;
import modelo.plataforma.Plataforma;
import modelo.plataforma.Playlist;
import modelo.usuarios.UsuarioGratuito;
import modelo.usuarios.UsuarioPremium;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 4: Gestión de Playlists
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario4GestionPlaylistsTest {

    private static Plataforma plataforma;
    private static UsuarioPremium userPremium;
    private static UsuarioGratuito userGratuito;
    private static ArrayList<Cancion> canciones;
    private static Playlist playlistPrivada;
    private static Playlist playlistPublica;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");

        // Crear usuarios
        userPremium = plataforma.registrarUsuarioPremium("Premium", "premium@test.com", "password123", TipoSuscripcion.PREMIUM);
        userGratuito = plataforma.registrarUsuarioGratuito("Gratuito", "gratuito@test.com", "password123");

        // Crear contenido
        Artista artista = plataforma.registrarArtista("Test Artist", "Test", "Test", true);
        Album album = plataforma.crearAlbum(artista, "Test Album", new Date());

        for (int i = 1; i <= 20; i++) {
            Cancion c = album.crearCancion("Canción " + i, 100 + i * 10, GeneroMusical.POP);
            c.setReproducciones(i * 1000); // Diferentes reproducciones para ordenar
            plataforma.agregarContenidoCatalogo(c);
        }

        canciones = plataforma.getCanciones();
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Crear playlists privadas ==========
    @Test
    @Order(1)
    @DisplayName("4.1 - Usuario premium crea playlists privadas")
    void testCrearPlaylistsPrivadas() {
        playlistPrivada = userPremium.crearPlaylist("Mis Favoritas");
        Playlist playlist2 = userPremium.crearPlaylist("Para Entrenar");

        assertNotNull(playlistPrivada);
        assertNotNull(playlist2);

        assertEquals("Mis Favoritas", playlistPrivada.getNombre());
        assertFalse(playlistPrivada.isEsPublica());
        assertEquals(userPremium, playlistPrivada.getCreador());

        assertEquals(2, userPremium.getMisPlaylists().size());
    }

    // ========== TEST 2: Crear playlist pública ==========
    @Test
    @Order(2)
    @DisplayName("4.2 - Crear playlist pública a través de la plataforma")
    void testCrearPlaylistPublica() {
        playlistPublica = plataforma.crearPlaylistPublica("Éxitos del Momento", userGratuito);

        assertNotNull(playlistPublica);
        assertTrue(playlistPublica.isEsPublica());
        assertEquals(userGratuito, playlistPublica.getCreador());

        assertTrue(plataforma.getPlaylistsPublicas().contains(playlistPublica));
    }

    // ========== TEST 3: Agregar canciones a playlist ==========
    @Test
    @Order(3)
    @DisplayName("4.3 - Agregar múltiples canciones a una playlist")
    void testAgregarCancionesAPlaylist() throws Exception {
        int numCanciones = 15;

        for (int i = 0; i < numCanciones && i < canciones.size(); i++) {
            playlistPrivada.agregarContenido(canciones.get(i));
        }

        assertEquals(numCanciones, playlistPrivada.getNumContenidos());
        assertFalse(playlistPrivada.estaVacia());
    }

    // ========== TEST 4: Contenido duplicado ==========
    @Test
    @Order(4)
    @DisplayName("4.4 - No se puede agregar contenido duplicado (ContenidoDuplicadoException)")
    void testContenidoDuplicado() {
        assertThrows(ContenidoDuplicadoException.class, () -> {
            playlistPrivada.agregarContenido(canciones.get(0));
        });
    }

    // ========== TEST 5: Ordenar por popularidad ==========
    @Test
    @Order(5)
    @DisplayName("4.5 - Ordenar playlist por popularidad")
    void testOrdenarPorPopularidad() throws PlaylistVaciaException {
        playlistPrivada.ordenarPor(CriterioOrden.POPULARIDAD);

        // La canción con más reproducciones debería estar primero
        int reprosAnterior = playlistPrivada.getContenido(0).getReproducciones();
        for (int i = 1; i < playlistPrivada.getNumContenidos(); i++) {
            int reprosActual = playlistPrivada.getContenido(i).getReproducciones();
            assertTrue(reprosAnterior >= reprosActual,
                "Las canciones deberían estar ordenadas de mayor a menor reproducciones");
            reprosAnterior = reprosActual;
        }
    }

    // ========== TEST 6: Ordenar por duración ==========
    @Test
    @Order(6)
    @DisplayName("4.6 - Ordenar playlist por duración")
    void testOrdenarPorDuracion() throws PlaylistVaciaException {
        playlistPrivada.ordenarPor(CriterioOrden.DURACION);

        // La canción más corta debería estar primero
        int duracionAnterior = playlistPrivada.getContenido(0).getDuracionSegundos();
        for (int i = 1; i < playlistPrivada.getNumContenidos(); i++) {
            int duracionActual = playlistPrivada.getContenido(i).getDuracionSegundos();
            assertTrue(duracionAnterior <= duracionActual,
                "Las canciones deberían estar ordenadas de menor a mayor duración");
            duracionAnterior = duracionActual;
        }
    }

    // ========== TEST 7: Shuffle ==========
    @Test
    @Order(7)
    @DisplayName("4.7 - Shuffle mezcla la playlist")
    void testShuffle() {
        // Guardar orden original
        ArrayList<String> ordenOriginal = new ArrayList<>();
        for (int i = 0; i < playlistPrivada.getNumContenidos(); i++) {
            ordenOriginal.add(playlistPrivada.getContenido(i).getId());
        }

        // Hacer shuffle
        playlistPrivada.shuffle();

        // Verificar que el tamaño es el mismo
        assertEquals(ordenOriginal.size(), playlistPrivada.getNumContenidos());

        // No verificamos que cambió el orden porque shuffle puede resultar igual
        // Solo verificamos que no se perdieron elementos
    }

    // ========== TEST 8: Seguir playlist ==========
    @Test
    @Order(8)
    @DisplayName("4.8 - Usuario puede seguir playlist pública")
    void testSeguirPlaylist() {
        int seguidoresAntes = playlistPublica.getSeguidores();

        userPremium.seguirPlaylist(playlistPublica);

        assertEquals(seguidoresAntes + 1, playlistPublica.getSeguidores());
        assertTrue(userPremium.getPlaylistsSeguidas().contains(playlistPublica));
    }

    // ========== TEST 9: Playlist vacía no se puede ordenar ==========
    @Test
    @Order(9)
    @DisplayName("4.9 - Ordenar playlist vacía lanza PlaylistVaciaException")
    void testOrdenarPlaylistVacia() {
        Playlist playlistVacia = userPremium.crearPlaylist("Vacía");

        assertThrows(PlaylistVaciaException.class, () -> {
            playlistVacia.ordenarPor(CriterioOrden.POPULARIDAD);
        });
    }

    // ========== TEST 10: Duración total ==========
    @Test
    @Order(10)
    @DisplayName("4.10 - Calcular duración total de playlist")
    void testDuracionTotal() {
        int duracionCalculada = 0;
        for (int i = 0; i < playlistPrivada.getNumContenidos(); i++) {
            duracionCalculada += playlistPrivada.getContenido(i).getDuracionSegundos();
        }

        assertEquals(duracionCalculada, playlistPrivada.getDuracionTotal());
    }
}
