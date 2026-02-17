import enums.TipoSuscripcion;
import excepciones.plataforma.UsuarioYaExisteException;
import excepciones.usuario.EmailInvalidoException;
import excepciones.usuario.PasswordDebilException;
import modelo.plataforma.Plataforma;
import modelo.usuarios.UsuarioGratuito;
import modelo.usuarios.UsuarioPremium;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el Escenario 1: Registro y Validación de Usuarios
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Escenario1RegistroUsuariosTest {

    private static Plataforma plataforma;

    @BeforeAll
    static void setUp() {
        // Reiniciar la instancia singleton antes de todos los tests
        Plataforma.reiniciarInstancia();
        plataforma = Plataforma.getInstancia("SoundWave Test");
    }

    @AfterAll
    static void tearDown() {
        Plataforma.reiniciarInstancia();
    }

    // ========== TEST 1: Crear plataforma Singleton ==========
    @Test
    @Order(1)
    @DisplayName("1.1 - Crear plataforma con patrón Singleton")
    void testCrearPlataformaSingleton() {
        assertNotNull(plataforma);
        assertEquals("SoundWave Test", plataforma.getNombre());

        // Verificar que es Singleton (misma instancia)
        Plataforma otraInstancia = Plataforma.getInstancia("Otro Nombre");
        assertSame(plataforma, otraInstancia);
    }

    // ========== TEST 2: Email inválido ==========
    @Test
    @Order(2)
    @DisplayName("1.2 - Registrar usuario con email inválido debe lanzar EmailInvalidoException")
    void testRegistrarUsuarioEmailInvalido() {
        assertThrows(EmailInvalidoException.class, () -> {
            plataforma.registrarUsuarioPremium("Test", "emailsinArroba", "password123");
        });

        assertThrows(EmailInvalidoException.class, () -> {
            plataforma.registrarUsuarioPremium("Test", "email@", "password123");
        });

        assertThrows(EmailInvalidoException.class, () -> {
            plataforma.registrarUsuarioGratuito("Test", "", "password123");
        });
    }

    // ========== TEST 3: Password débil ==========
    @Test
    @Order(3)
    @DisplayName("1.3 - Registrar usuario con password débil debe lanzar PasswordDebilException")
    void testRegistrarUsuarioPasswordDebil() {
        assertThrows(PasswordDebilException.class, () -> {
            plataforma.registrarUsuarioGratuito("Test", "test@gmail.com", "123");
        });

        assertThrows(PasswordDebilException.class, () -> {
            plataforma.registrarUsuarioPremium("Test", "test2@gmail.com", "1234567");
        });

        assertThrows(PasswordDebilException.class, () -> {
            plataforma.registrarUsuarioGratuito("Test", "test3@gmail.com", "");
        });
    }

    // ========== TEST 4: Registrar usuarios correctamente ==========
    @Test
    @Order(4)
    @DisplayName("1.4 - Registrar 3 usuarios premium con diferentes suscripciones")
    void testRegistrarUsuariosPremium() throws Exception {
        UsuarioPremium user1 = plataforma.registrarUsuarioPremium(
            "Juan Pérez", "juan@gmail.com", "password123", TipoSuscripcion.PREMIUM);
        assertNotNull(user1);
        assertEquals("Juan Pérez", user1.getNombre());
        assertEquals(TipoSuscripcion.PREMIUM, user1.getSuscripcion());

        UsuarioPremium user2 = plataforma.registrarUsuarioPremium(
            "María López", "maria@hotmail.com", "securepass456", TipoSuscripcion.FAMILIAR);
        assertNotNull(user2);
        assertEquals(TipoSuscripcion.FAMILIAR, user2.getSuscripcion());

        UsuarioPremium user3 = plataforma.registrarUsuarioPremium(
            "Carlos García", "carlos@outlook.com", "mypassword789", TipoSuscripcion.ESTUDIANTE);
        assertNotNull(user3);
        assertEquals(TipoSuscripcion.ESTUDIANTE, user3.getSuscripcion());

        assertEquals(3, plataforma.getUsuariosPremium().size());
    }

    @Test
    @Order(5)
    @DisplayName("1.5 - Registrar 2 usuarios gratuitos")
    void testRegistrarUsuariosGratuitos() throws Exception {
        UsuarioGratuito user1 = plataforma.registrarUsuarioGratuito(
            "Pedro Sánchez", "pedro@yahoo.com", "freeuser123");
        assertNotNull(user1);
        assertEquals(TipoSuscripcion.GRATUITO, user1.getSuscripcion());

        UsuarioGratuito user2 = plataforma.registrarUsuarioGratuito(
            "Ana Martínez", "ana@gmail.com", "anapass456");
        assertNotNull(user2);

        assertEquals(2, plataforma.getUsuariosGratuitos().size());
    }

    // ========== TEST 5: Usuario duplicado ==========
    @Test
    @Order(6)
    @DisplayName("1.6 - Registrar usuario con email duplicado debe lanzar UsuarioYaExisteException")
    void testRegistrarUsuarioDuplicado() {
        assertThrows(UsuarioYaExisteException.class, () -> {
            plataforma.registrarUsuarioGratuito("Duplicado", "juan@gmail.com", "duplicate123");
        });
    }

    // ========== TEST 6: Total usuarios ==========
    @Test
    @Order(7)
    @DisplayName("1.7 - Verificar total de usuarios registrados")
    void testTotalUsuarios() {
        assertEquals(5, plataforma.getTodosLosUsuarios().size());
        assertEquals(3, plataforma.getUsuariosPremium().size());
        assertEquals(2, plataforma.getUsuariosGratuitos().size());
    }
}
