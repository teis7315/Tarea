package enums;

public enum TipoAnuncion {
    AUDIO(15 ,0.05),
    BANNER(0,0.02),
    VIDEO(30,0.10);

    private int duracionSegundos;
    private double costoPorImpresion;

    TipoAnuncion(int duracionSegundos, double costoPorImpresion) {
        this.duracionSegundos = duracionSegundos;
        this.costoPorImpresion = costoPorImpresion;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public double getCostoPorImpresion() {
        return costoPorImpresion;
    }

    @Override
    public String toString() {
        return this.name() + " (" + duracionSegundos + "s)";
    }
}
