package interfaces;

import excepciones.descarga.ContenidoYaDescargadoException;

public interface Descargable {
    boolean descargar() throws LimiteDescargasException,
            ContenidoYaDescargadoException;
    boolean eliminarDescargar();
    int expacioRequerido();
}
