package interfaces;

import excepciones.descarga.ContenidoYaDescargadoException;
import excepciones.descarga.LimiteDescargasException;

public interface Descargable {
    boolean descargar() throws LimiteDescargasException, ContenidoYaDescargadoException;
    boolean eliminarDescarga();
    int espacioRequerido();
}
