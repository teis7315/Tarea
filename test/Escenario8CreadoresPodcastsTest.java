import enums.CategoriaPodcast;
import excepciones.contenido.EpisodioNoEncontradoException;
import modelo.artistas.Creador;
import modelo.contenido.Podcast;
import modelo.plataforma.Plataforma;
import utilidades.EstadisticasCreador;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 8: Creadores y Podcasts
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario8CreadoresPodcastsTest {

    private static Plataforma plataforma;
    private static Creador creador;
    private static Podcast nuevoEpisodio;

    @BeforeAll
    static void setUp() throws Exception {
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");

        // Crear creador con episodios
        creador = plataforma.registrarCreador("The Wild Project", "Jordi Wild", "Podcast de entrevistas");
        creador.setSuscriptores(2500000);

        // Crear episodios iniciales
        for (int i = 1; i <= 5; i++) {
            Podcast p = plataforma.crearPodcast("Episodio " + i, 3600 + i * 300, creador, i, 1, CategoriaPodcast.ENTRETENIMIENTO);
            p.setReproducciones(100000 + i * 50000);
        }
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Crear nuevo episodio ==========
    @Test
    @Order(1)
    @DisplayName("8.1 - Creador publica nuevo episodio")
    void testCrearNuevoEpisodio() throws Exception {
        int episodiosAntes = creador.getNumEpisodios();

        nuevoEpisodio = plataforma.crearPodcast(
            "Episodio Especial",
            5400,
            creador,
            episodiosAntes + 1,
            2,
            CategoriaPodcast.ENTRETENIMIENTO
        );

        assertNotNull(nuevoEpisodio);
        assertEquals("Episodio Especial", nuevoEpisodio.getTitulo());
        assertEquals(creador, nuevoEpisodio.getCreador());
        assertEquals(2, nuevoEpisodio.getTemporada());
        assertEquals(episodiosAntes + 1, creador.getNumEpisodios());
    }

    // ========== TEST 2: Agregar invitados ==========
    @Test
    @Order(2)
    @DisplayName("8.2 - Agregar invitados al episodio")
    void testAgregarInvitados() {
        nuevoEpisodio.agregarInvitado("Ibai Llanos");
        nuevoEpisodio.agregarInvitado("AuronPlay");
        nuevoEpisodio.agregarInvitado("TheGrefg");

        ArrayList<String> invitados = nuevoEpisodio.getInvitados();

        assertEquals(3, invitados.size());
        assertTrue(invitados.contains("Ibai Llanos"));
        assertTrue(invitados.contains("AuronPlay"));
        assertTrue(invitados.contains("TheGrefg"));
    }

    // ========== TEST 3: No agregar invitados duplicados ==========
    @Test
    @Order(3)
    @DisplayName("8.3 - No se agregan invitados duplicados")
    void testNoAgregarInvitadosDuplicados() {
        int invitadosAntes = nuevoEpisodio.getInvitados().size();

        nuevoEpisodio.agregarInvitado("Ibai Llanos"); // Ya existe

        assertEquals(invitadosAntes, nuevoEpisodio.getInvitados().size());
    }

    // ========== TEST 4: Obtener estadísticas del creador ==========
    @Test
    @Order(4)
    @DisplayName("8.4 - Obtener estadísticas del creador")
    void testObtenerEstadisticas() {
        EstadisticasCreador stats = creador.obtenerEstadisticas();

        assertNotNull(stats);
        assertEquals(creador, stats.getCreador());
        assertEquals(creador.getNumEpisodios(), stats.getTotalEpisodios());
        assertEquals(creador.getSuscriptores(), stats.getTotalSuscriptores());
        assertTrue(stats.getTotalReproducciones() >= 0);
    }

    // ========== TEST 5: Generar reporte de estadísticas ==========
    @Test
    @Order(5)
    @DisplayName("8.5 - Generar reporte de estadísticas")
    void testGenerarReporte() {
        EstadisticasCreador stats = creador.obtenerEstadisticas();
        String reporte = stats.generarReporte();

        assertNotNull(reporte);
        assertFalse(reporte.isEmpty());
        // El reporte convierte el nombre del canal a mayúsculas
        assertTrue(reporte.toUpperCase().contains(creador.getNombreCanal().toUpperCase()));
    }

    // ========== TEST 6: Eliminar episodio ==========
    @Test
    @Order(6)
    @DisplayName("8.6 - Eliminar episodio del creador")
    void testEliminarEpisodio() throws EpisodioNoEncontradoException {
        int episodiosAntes = creador.getNumEpisodios();

        // Obtener ID del primer episodio
        String idEliminar = creador.getEpisodios().get(0).getId();

        creador.eliminarEpisodio(idEliminar);

        assertEquals(episodiosAntes - 1, creador.getNumEpisodios());
    }

    // ========== TEST 7: Eliminar episodio inexistente ==========
    @Test
    @Order(7)
    @DisplayName("8.7 - Eliminar episodio inexistente lanza EpisodioNoEncontradoException")
    void testEliminarEpisodioInexistente() {
        assertThrows(EpisodioNoEncontradoException.class, () -> {
            creador.eliminarEpisodio("id-que-no-existe-123");
        });
    }

    // ========== TEST 8: Calcular promedio de reproducciones ==========
    @Test
    @Order(8)
    @DisplayName("8.8 - Calcular promedio de reproducciones del creador")
    void testCalcularPromedioReproducciones() {
        double promedio = creador.calcularPromedioReproducciones();

        assertTrue(promedio >= 0);

        // Verificar cálculo
        int total = 0;
        for (Podcast p : creador.getEpisodios()) {
            total += p.getReproducciones();
        }

        double promedioEsperado = (double) total / creador.getNumEpisodios();
        assertEquals(promedioEsperado, promedio, 0.01);
    }

    // ========== TEST 9: Total reproducciones ==========
    @Test
    @Order(9)
    @DisplayName("8.9 - Calcular total de reproducciones del creador")
    void testTotalReproducciones() {
        int totalCalculado = 0;
        for (Podcast p : creador.getEpisodios()) {
            totalCalculado += p.getReproducciones();
        }

        assertEquals(totalCalculado, creador.getTotalReproducciones());
    }

    // ========== TEST 10: Obtener top episodios ==========
    @Test
    @Order(10)
    @DisplayName("8.10 - Obtener top episodios del creador")
    void testObtenerTopEpisodios() {
        ArrayList<Podcast> top3 = creador.obtenerTopEpisodios(3);

        assertNotNull(top3);
        assertTrue(top3.size() <= 3);

        // Verificar orden descendente
        if (top3.size() > 1) {
            for (int i = 0; i < top3.size() - 1; i++) {
                assertTrue(top3.get(i).getReproducciones() >= top3.get(i + 1).getReproducciones());
            }
        }
    }

    // ========== TEST 11: Última temporada ==========
    @Test
    @Order(11)
    @DisplayName("8.11 - Obtener última temporada")
    void testUltimaTemporada() {
        int ultimaTemporada = creador.getUltimaTemporada();

        assertTrue(ultimaTemporada >= 1);
        assertEquals(2, ultimaTemporada); // Creamos episodio en temporada 2
    }

    // ========== TEST 12: Redes sociales ==========
    @Test
    @Order(12)
    @DisplayName("8.12 - Agregar redes sociales al creador")
    void testAgregarRedesSociales() {
        creador.agregarRedSocial("Twitter", "@jordiwild");
        creador.agregarRedSocial("Instagram", "@jordiwild");
        creador.agregarRedSocial("YouTube", "TheWildProject");

        var redesSociales = creador.getRedesSociales();

        assertEquals(3, redesSociales.size());
        assertEquals("@jordiwild", redesSociales.get("twitter"));
    }

    // ========== TEST 13: Episodio más popular ==========
    @Test
    @Order(13)
    @DisplayName("8.13 - Identificar episodio más popular en estadísticas")
    void testEpisodioMasPopular() {
        EstadisticasCreador stats = creador.obtenerEstadisticas();
        Podcast masPopular = stats.getEpisodioMasPopular();

        if (masPopular != null) {
            int maxRepros = 0;
            for (Podcast p : creador.getEpisodios()) {
                if (p.getReproducciones() > maxRepros) {
                    maxRepros = p.getReproducciones();
                }
            }
            assertEquals(maxRepros, masPopular.getReproducciones());
        }
    }
}
