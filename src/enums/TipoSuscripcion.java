package enums;

public enum TipoSuscripcion {
    GRATUITO(0.0, false, 50, false),
    PREMIUM(9.99, true, -1, true),
    FAMILIAR(14.99, true, -1, true),
    ESTUDIANTE(4.99, true, -1, true);

    private double precioMensual;
    private boolean sinAnuncios;
    private int limiteReproducciones;
    private boolean descargasOffline;

    TipoSuscripcion(double precioMensual, boolean sinAnuncios, int limiteReproducciones, boolean descargasOffline) {
        this.precioMensual = precioMensual;
        this.sinAnuncios = sinAnuncios;
        this.limiteReproducciones = limiteReproducciones;
        this.descargasOffline = descargasOffline;
    }

    public double getPrecioMensual() {
        return precioMensual;
    }

    public boolean isDescargasOffline() {
        return descargasOffline;
    }

    public int getLimiteReproducciones() {
        return limiteReproducciones;
    }

    public boolean isSinAnuncios() {
        return sinAnuncios;
    }

    public boolean tieneReproduccionesIlimitadas() {
        return limiteReproducciones == -1;
    }

    @Override
    public String toString() {
        return this.name() + " - $" + precioMensual;
    }
}