package enums;

public enum TipoSuscripcion {
    GRATUITO(0.0,false,50,false),
    PREMIUM(9.99, true,-1,true),
    FAMILIAR(14.99,true,-1,true),
    ESTUDIANTE(4.99,true,-1,true);

    private double precioMensual;
    private boolean sinAnuncios;
    private int limiteReproducciones;
    private boolean descargaOffline;

    TipoSuscripcion(double precioMensual, boolean sinAnuncios, int limiteReproducciones, boolean descargaOffline) {
        this.precioMensual = precioMensual;
        this.sinAnuncios = sinAnuncios;
        this.limiteReproducciones = limiteReproducciones;
        this.descargaOffline = descargaOffline;
    }

    public double getPrecioMensual() {
        return precioMensual;
    }

    public boolean isDescargaOffline() {
        return descargaOffline;
    }

    public int getLimiteReproducciones() {
        return limiteReproducciones;
    }

    public boolean isSinAnuncios() {
        return sinAnuncios;
    }
    boolean tieneReproduccionesIlimitadas(){
        return limiteReproducciones == -1;
    }

    @Override
    public String toString() {
        return "TipoSuscripcion{" +
                "precioMensual=" + precioMensual +
                '}';
    }
}