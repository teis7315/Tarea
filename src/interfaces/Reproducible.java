package interfaces;

import excepciones.contenido.ContenidoNoDisponibleException;

public interface Reproducible {
    void play() throws ContenidoNoDisponibleException;
    void pause();
    void stop();
    int getDuracion();
}
