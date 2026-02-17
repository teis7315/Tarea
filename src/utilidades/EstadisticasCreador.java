package utilidades;

import modelo.artistas.Creador;
import modelo.contenido.Podcast;

import java.util.HashMap;

@SuppressWarnings("unused")
public class EstadisticasCreador {
    private Creador creador;
    private int totalEpisodios;
    private int totalReproducciones;
    private double promedioReproducciones;
    private int totalSuscriptores;
    private int totalLikes;
    private int duracionTotalSegundos;
    private Podcast episodioMasPopular;
    private HashMap<Integer, Integer> episodiosPorTemporada;

    public EstadisticasCreador(Creador creador) {
        this.creador = creador;
        this.episodiosPorTemporada = new HashMap<>();
        calcularEstadisticas();
    }

    private void calcularEstadisticas() {
        if (creador.getEpisodios().isEmpty()) {
            totalEpisodios = 0;
            totalReproducciones = 0;
            promedioReproducciones = 0;
            totalSuscriptores = creador.getSuscriptores();
            totalLikes = 0;
            duracionTotalSegundos = 0;
            episodioMasPopular = null;
            return;
        }

        totalEpisodios = creador.getEpisodios().size();
        totalSuscriptores = creador.getSuscriptores();
        totalLikes = 0;
        duracionTotalSegundos = 0;
        totalReproducciones = 0;
        episodioMasPopular = null;
        int maxReproducciones = 0;

        for (modelo.contenido.Podcast podcast : creador.getEpisodios()) {
            totalReproducciones += podcast.getReproducciones();
            totalLikes += podcast.getLikes();
            duracionTotalSegundos += podcast.getDuracionSegundos();

            if (podcast.getReproducciones() > maxReproducciones) {
                maxReproducciones = podcast.getReproducciones();
                episodioMasPopular = podcast;
            }

            int temporada = podcast.getTemporada();
            episodiosPorTemporada.put(temporada, episodiosPorTemporada.getOrDefault(temporada, 0) + 1);
        }

        promedioReproducciones = (double) totalReproducciones / totalEpisodios;
    }

    public String generarReporte() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("===== ESTADÍSTICAS DE CREADOR =====\n");
        reporte.append("Creador: ").append(creador.getNombre()).append(" (@").append(creador.getNombreCanal()).append(")\n");
        reporte.append("Total Episodios: ").append(totalEpisodios).append("\n");
        reporte.append("Total Reproducciones: ").append(totalReproducciones).append("\n");
        reporte.append("Promedio Reproducciones: ").append(String.format("%.2f", promedioReproducciones)).append("\n");
        reporte.append("Total Suscriptores: ").append(totalSuscriptores).append("\n");
        reporte.append("Total Likes: ").append(totalLikes).append("\n");
        reporte.append("Duración Total: ").append(formatearDuracion(duracionTotalSegundos)).append("\n");
        if (episodioMasPopular != null) {
            reporte.append("Episodio Más Popular: ").append(episodioMasPopular.getTitulo()).append("\n");
        }
        reporte.append("Engagement: ").append(String.format("%.2f%%", calcularEngagement())).append("\n");
        return reporte.toString();
    }

    public double calcularEngagement() {
        if (totalReproducciones == 0) return 0;
        return (totalLikes * 100.0) / totalReproducciones;
    }

    public int estimarCrecimientoMensual() {
        if (totalEpisodios < 2) return 0;
        return (int) (promedioReproducciones * 0.1);
    }

    private String formatearDuracion(int segundos) {
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int secs = segundos % 60;
        if (horas > 0) {
            return String.format("%d:%02d:%02d", horas, minutos, secs);
        }
        return String.format("%d:%02d", minutos, secs);
    }

    public Creador getCreador() {
        return creador;
    }

    public int getTotalEpisodios() {
        return totalEpisodios;
    }

    public int getTotalReproducciones() {
        return totalReproducciones;
    }

    public double getPromedioReproducciones() {
        return promedioReproducciones;
    }

    public int getTotalSuscriptores() {
        return totalSuscriptores;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public int getDuracionTotalSegundos() {
        return duracionTotalSegundos;
    }

    public Podcast getEpisodioMasPopular() {
        return episodioMasPopular;
    }

    public HashMap<Integer, Integer> getEpisodiosPorTemporada() {
        return new HashMap<>(episodiosPorTemporada);
    }

    @Override
    public String toString() {
        return "EstadisticasCreador{" + "creador=" + creador.getNombre() +
               ", totalEpisodios=" + totalEpisodios + ", totalReproducciones=" + totalReproducciones + '}';
    }
}