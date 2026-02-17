package modelo.plataforma;

import enums.TipoAnuncion;

import java.util.UUID;

public class Anuncio {

    private String id;
    private String empresa;
    private int duracionSegundos;
    private String audioURL;
    private TipoAnuncion tipo;
    private int impresiones;
    private double presupuesto;
    private boolean activo;

    public Anuncio(String empresa, TipoAnuncion tipo, double presupuesto) {
        this(empresa, tipo, presupuesto, "");
    }

    public Anuncio(String empresa, TipoAnuncion tipo, double presupuesto, String audioURL) {
        this.id = UUID.randomUUID().toString();
        this.empresa = empresa;
        this.tipo = tipo;
        this.presupuesto = presupuesto;
        this.audioURL = audioURL;
        this.duracionSegundos = tipo.getDuracionSegundos();
        this.impresiones = 0;
        this.activo = true;
    }

    public void reproducir() {
        if (activo) {
            registrarImpresion();
            System.out.println("Reproduciendo anuncio de " + empresa);
        }
    }

    public void registrarImpresion() {
        impresiones++;
        double costo = calcularCostoPorImpresion();
        presupuesto -= costo;
        if (presupuesto <= 0) {
            activo = false;
        }
    }

    public double calcularCostoPorImpresion() {
        return tipo.getCostoPorImpresion();
    }

    public double calcularCostoTotal() {
        return impresiones * calcularCostoPorImpresion();
    }

    public int calcularImpresionesRestantes() {
        if (calcularCostoPorImpresion() <= 0) return Integer.MAX_VALUE;
        return (int) Math.floor(presupuesto / calcularCostoPorImpresion());
    }

    public void desactivar() { activo = false; }
    public void activar() { activo = true; }
    public boolean puedeMostrarse() { return activo && presupuesto > 0; }

    // Getters/Setters
    public String getId() { return id; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = duracionSegundos; }
    public String getAudioURL() { return audioURL; }
    public void setAudioURL(String audioURL) { this.audioURL = audioURL; }
    public TipoAnuncion getTipo() { return tipo; }
    public void setTipo(TipoAnuncion tipo) { this.tipo = tipo; }
    public int getImpresiones() { return impresiones; }
    public double getPresupuesto() { return presupuesto; }
    public void setPresupuesto(double presupuesto) { this.presupuesto = presupuesto; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Anuncio{" + "empresa='" + empresa + '\'' + ", tipo=" + tipo + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Anuncio anuncio = (Anuncio) obj;
        return id.equals(anuncio.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
