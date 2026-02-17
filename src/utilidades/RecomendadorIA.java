package utilidades;

import enums.AlgoritmoRecomendacion;
import excepciones.recomendacion.HistorialVacioException;
import excepciones.recomendacion.ModeloNoEntrenadoException;
import interfaces.Recomendador;
import excepciones.recomendacion.RecomendacionException;
import modelo.contenido.Contenido;
import modelo.usuarios.Usuario;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class RecomendadorIA implements Recomendador {
    private static final double UMBRAL_DEFAULT = 0.6;

    private final HashMap<String, ArrayList<String>> matrizPreferencias;
    private final HashMap<String, ArrayList<Contenido>> historialCompleto;
    private AlgoritmoRecomendacion algoritmo;
    private double umbralSimilitud;
    private boolean modeloEntrenado;
    private ArrayList<Contenido> catalogoReferencia;

    public RecomendadorIA() {
        this.matrizPreferencias = new HashMap<>();
        this.historialCompleto = new HashMap<>();
        this.algoritmo = null;
        this.umbralSimilitud = UMBRAL_DEFAULT;
        this.modeloEntrenado = false;
        this.catalogoReferencia = new ArrayList<>();
    }

    public RecomendadorIA(AlgoritmoRecomendacion algoritmo) {
        this();
        this.algoritmo = algoritmo;
    }

    @Override
    public ArrayList<Contenido> recomendar(Usuario usuario)
            throws RecomendacionException, ModeloNoEntrenadoException, HistorialVacioException {
        if (!modeloEntrenado)
            throw new ModeloNoEntrenadoException("El modelo de recomendación no ha sido entrenado");

        if (usuario.getHistorial().isEmpty())
            throw new HistorialVacioException("El usuario no tiene historial suficiente");

        ArrayList<Contenido> recomendaciones = new ArrayList<>();
        ArrayList<String> preferencias = matrizPreferencias.getOrDefault(usuario.getId(), new ArrayList<>());

        for (Contenido c : catalogoReferencia) {
            if (!usuario.getHistorial().contains(c) && calcularSimilitudContenido(c, preferencias) >= umbralSimilitud) {
                recomendaciones.add(c);
            }
        }

        return recomendaciones;
    }


    @Override
    public ArrayList<Contenido> obtenerSimilares(Contenido contenido) throws RecomendacionException {
        if (!modeloEntrenado) throw new RecomendacionException("Modelo no entrenado");

        ArrayList<Contenido> similares = new ArrayList<>();
        ArrayList<String> etiquetasContenido = contenido.getEtiquetas();

        for (Contenido c : catalogoReferencia) {
            if (c != contenido && calcularSimilitudContenido(c, etiquetasContenido) >= umbralSimilitud) {
                similares.add(c);
            }
        }

        return similares;
    }

    public void entrenarModelo(ArrayList<Usuario> usuarios) {
        entrenarModelo(usuarios, null);
    }

    public void entrenarModelo(ArrayList<Usuario> usuarios, ArrayList<Contenido> catalogo) {
        matrizPreferencias.clear();
        historialCompleto.clear();
        if (catalogo != null) catalogoReferencia = new ArrayList<>(catalogo);

        for (Usuario u : usuarios) {
            actualizarPreferencias(u);
            historialCompleto.put(u.getId(), u.getHistorial());
        }

        modeloEntrenado = true;
    }

    public double calcularSimilitud(Usuario u1, Usuario u2) {
        ArrayList<String> prefs1 = matrizPreferencias.getOrDefault(u1.getId(), new ArrayList<>());
        ArrayList<String> prefs2 = matrizPreferencias.getOrDefault(u2.getId(), new ArrayList<>());

        if (prefs1.isEmpty() || prefs2.isEmpty()) return 0.0;

        int coincidencias = 0;
        for (String g : prefs1) if (prefs2.contains(g)) coincidencias++;
        return (double) coincidencias / Math.max(prefs1.size(), prefs2.size());
    }

    public void actualizarPreferencias(Usuario usuario) {
        ArrayList<String> prefs = new ArrayList<>();
        for (Contenido c : usuario.getHistorial()) {
            for (String tag : c.getEtiquetas()) {
                if (!prefs.contains(tag)) prefs.add(tag);
            }
        }
        matrizPreferencias.put(usuario.getId(), prefs);
    }

    private double calcularSimilitudContenido(Contenido contenido, ArrayList<String> preferencias) {
        if (preferencias == null || preferencias.isEmpty()) return 0.0;
        ArrayList<String> etiquetas = contenido.getEtiquetas();
        if (etiquetas.isEmpty()) return 0.0;

        int coincidencias = 0;
        for (String e : etiquetas) if (preferencias.contains(e)) coincidencias++;
        return (double) coincidencias / Math.max(preferencias.size(), etiquetas.size());
    }

    public ArrayList<String> obtenerGenerosPopulares() {
        HashMap<String, Integer> contador = new HashMap<>();
        for (ArrayList<String> prefs : matrizPreferencias.values()) {
            for (String g : prefs) {
                contador.put(g, contador.getOrDefault(g, 0) + 1);
            }
        }

        ArrayList<String> populares = new ArrayList<>(contador.keySet());
        populares.sort((a, b) -> Integer.compare(contador.get(b), contador.get(a)));
        return populares;
    }

    // Getters y Setters
    public AlgoritmoRecomendacion getAlgoritmo() { return algoritmo; }
    public void setAlgoritmo(AlgoritmoRecomendacion algoritmo) { this.algoritmo = algoritmo; }

    public double getUmbralSimilitud() { return umbralSimilitud; }
    public void setUmbralSimilitud(double umbralSimilitud) { this.umbralSimilitud = umbralSimilitud; }

    public boolean isModeloEntrenado() { return modeloEntrenado; }

    public HashMap<String, ArrayList<String>> getMatrizPreferencias() {
        HashMap<String, ArrayList<String>> copia = new HashMap<>();
        for (String k : matrizPreferencias.keySet()) {
            copia.put(k, new ArrayList<>(matrizPreferencias.get(k)));
        }
        return copia;
    }

    public void setCatalogoReferencia(ArrayList<Contenido> catalogo) { this.catalogoReferencia = new ArrayList<>(catalogo); }
}
