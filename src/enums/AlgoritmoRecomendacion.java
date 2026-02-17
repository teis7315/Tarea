package enums;

public enum AlgoritmoRecomendacion {
    COLABORATIVO("Basado en usuarios similares"),
    CONTENIDO("Basado en características del contenido"),
    HIBRIDO("Combinación de ambos");

    private String descripcion;

    AlgoritmoRecomendacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return this.name() + " - " + descripcion;
    }
}