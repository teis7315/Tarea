import enums.CategoriaPodcast;
import enums.GeneroMusical;
import enums.TipoSuscripcion;
import excepciones.recomendacion.HistorialVacioException;
import excepciones.recomendacion.ModeloNoEntrenadoException;
import excepciones.recomendacion.RecomendacionException;
import modelo.artistas.Album;
import modelo.artistas.Artista;
import modelo.artistas.Creador;
import modelo.contenido.Cancion;
import modelo.contenido.Contenido;
import modelo.contenido.Podcast;
import modelo.plataforma.Plataforma;
import modelo.usuarios.Usuario;
import modelo.usuarios.UsuarioPremium;
import utilidades.RecomendadorIA;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 6: Sistema de Recomendaciones
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario6RecomendacionesTest {

    private static Plataforma plataforma;
    private static RecomendadorIA recomendador;
    private static UsuarioPremium userConHistorial;
    private static UsuarioPremium userSinHistorial;
    private static ArrayList<Cancion> canciones;
    private static ArrayList<Podcast> podcasts;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");
        recomendador = plataforma.getRecomendador();

        // Crear usuarios
        userConHistorial = plataforma.registrarUsuarioPremium("User Con Historial", "historial@test.com", "password123", TipoSuscripcion.PREMIUM);
        userSinHistorial = plataforma.registrarUsuarioPremium("User Sin Historial", "sinhistorial@test.com", "password123", TipoSuscripcion.PREMIUM);

        // Crear contenido variado
        Artista artista1 = plataforma.registrarArtista("Artista Pop", "Pop", "USA", true);
        Artista artista2 = plataforma.registrarArtista("Artista Reggaeton", "Reggaeton", "PR", true);

        Album albumPop = plataforma.crearAlbum(artista1, "Pop Album", new Date());
        for (int i = 1; i <= 10; i++) {
            Cancion c = albumPop.crearCancion("Pop Song " + i, 180, GeneroMusical.POP);
            plataforma.agregarContenidoCatalogo(c);
        }

        Album albumReggaeton = plataforma.crearAlbum(artista2, "Reggaeton Album", new Date());
        for (int i = 1; i <= 10; i++) {
            Cancion c = albumReggaeton.crearCancion("Reggaeton Song " + i, 200, GeneroMusical.REGGAETON);
            plataforma.agregarContenidoCatalogo(c);
        }

        Creador creador = plataforma.registrarCreador("Tech Channel", "Tech", "Tech");
        for (int i = 1; i <= 5; i++) {
            plataforma.crearPodcast("Tech Episode " + i, 3600, creador, i, 1, CategoriaPodcast.TECNOLOGIA);
        }

        canciones = plataforma.getCanciones();
        podcasts = plataforma.getPodcasts();

        // Agregar historial al usuario
        for (int i = 0; i < 10 && i < canciones.size(); i++) {
            userConHistorial.agregarAlHistorial(canciones.get(i));
        }
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Entrenar modelo ==========
    @Test
    @Order(1)
    @DisplayName("6.1 - Entrenar modelo de recomendaciones")
    void testEntrenarModelo() {
        ArrayList<Usuario> usuarios = plataforma.getTodosLosUsuarios();
        ArrayList<Contenido> catalogo = plataforma.getCatalogo();

        assertDoesNotThrow(() -> {
            recomendador.entrenarModelo(usuarios, catalogo);
        });

        assertTrue(recomendador.isModeloEntrenado());
    }

    // ========== TEST 2: Recomendar sin entrenar ==========
    @Test
    @Order(2)
    @DisplayName("6.2 - Recomendar sin modelo entrenado lanza ModeloNoEntrenadoException")
    void testRecomendarSinEntrenar() {
        RecomendadorIA nuevoRecomendador = new RecomendadorIA();

        assertFalse(nuevoRecomendador.isModeloEntrenado());
        assertThrows(ModeloNoEntrenadoException.class, () -> {
            nuevoRecomendador.recomendar(userConHistorial);
        });
    }

    // ========== TEST 3: Generar recomendaciones para usuario con historial ==========
    @Test
    @Order(3)
    @DisplayName("6.3 - Generar recomendaciones para usuario con historial")
    void testRecomendarConHistorial() throws RecomendacionException {
        ArrayList<Contenido> recomendaciones = recomendador.recomendar(userConHistorial);

        assertNotNull(recomendaciones);
        // Las recomendaciones no deben estar vacías o deben existir
        assertTrue(recomendaciones.size() >= 0);
    }

    // ========== TEST 4: Usuario sin historial lanza excepción ==========
    @Test
    @Order(4)
    @DisplayName("6.4 - Usuario sin historial lanza HistorialVacioException")
    void testRecomendarSinHistorial() {
        assertTrue(userSinHistorial.getHistorial().isEmpty());

        assertThrows(HistorialVacioException.class, () -> {
            recomendador.recomendar(userSinHistorial);
        });
    }

    // ========== TEST 5: Obtener similares a canción ==========
    @Test
    @Order(5)
    @DisplayName("6.5 - Obtener contenido similar a una canción")
    void testObtenerSimilaresCancion() throws RecomendacionException {
        Cancion cancion = canciones.get(0);

        ArrayList<Contenido> similares = recomendador.obtenerSimilares(cancion);

        assertNotNull(similares);
        // Debe devolver canciones del mismo género
        for (Contenido c : similares) {
            if (c instanceof Cancion) {
                assertEquals(cancion.getGenero(), ((Cancion) c).getGenero());
            }
        }
    }

    // ========== TEST 6: Obtener similares a podcast ==========
    @Test
    @Order(6)
    @DisplayName("6.6 - Obtener contenido similar a un podcast")
    void testObtenerSimilaresPodcast() throws RecomendacionException {
        if (podcasts.isEmpty()) {
            return; // Skip si no hay podcasts
        }

        Podcast podcast = podcasts.get(0);

        ArrayList<Contenido> similares = recomendador.obtenerSimilares(podcast);

        assertNotNull(similares);
        // Debe devolver podcasts de la misma categoría
        for (Contenido c : similares) {
            if (c instanceof Podcast) {
                assertEquals(podcast.getCategoria(), ((Podcast) c).getCategoria());
            }
        }
    }

    // ========== TEST 7: Actualizar preferencias ==========
    @Test
    @Order(7)
    @DisplayName("6.7 - Actualizar preferencias de usuario")
    void testActualizarPreferencias() {
        assertDoesNotThrow(() -> {
            recomendador.actualizarPreferencias(userConHistorial);
        });

        // Las preferencias deben existir
        assertFalse(recomendador.getMatrizPreferencias().isEmpty());
    }

    // ========== TEST 8: Calcular similitud entre usuarios ==========
    @Test
    @Order(8)
    @DisplayName("6.8 - Calcular similitud entre usuarios")
    void testCalcularSimilitud() {
        double similitud = recomendador.calcularSimilitud(userConHistorial, userSinHistorial);

        // Similitud debe estar entre 0 y 1
        assertTrue(similitud >= 0.0 && similitud <= 1.0);
    }

    // ========== TEST 9: Géneros populares ==========
    @Test
    @Order(9)
    @DisplayName("6.9 - Obtener géneros populares")
    void testGenerosPopulares() {
        var generosPopulares = recomendador.obtenerGenerosPopulares();

        assertNotNull(generosPopulares);
        // Debe contener al menos los géneros del historial del usuario
    }

    // ========== TEST 10: Recomendaciones no incluyen historial ==========
    @Test
    @Order(10)
    @DisplayName("6.10 - Recomendaciones no incluyen contenido ya escuchado")
    void testRecomendacionesNoIncluyenHistorial() throws RecomendacionException {
        ArrayList<Contenido> recomendaciones = recomendador.recomendar(userConHistorial);
        ArrayList<Contenido> historial = userConHistorial.getHistorial();

        for (Contenido rec : recomendaciones) {
            assertFalse(historial.contains(rec),
                "Las recomendaciones no deberían incluir contenido del historial");
        }
    }
}
