package enums;

public enum GeneroMusical {
     POP("Pop", "Música popular contemporánea"),
 ROCK("Rock", "Rock clásico y moderno"),
 HIPHOP("Hip Hop", "Hip hop y rap"),
 JAZZ("Jazz", "Jazz clásico y contemporáneo"),
 ELECTRONICA("Electrónica", "Música electrónica y EDM"),
 REGGAETON("Reggaetón", "Reggaetón y música urbana latina"),
 INDIE("Indie", "Música independiente"),
 CLASICA("Clásica", "Música clásica"),
 COUNTRY("Country", "Música country"),
 METAL("Metal", "Heavy metal y subgéneros"),
 RNB("R&B", "Rhythm and Blues"),
 SOUL("Soul", "Música soul"),
 BLUES("Blues", "Blues clásico y contemporáneo"),
 TRAP("Trap", "Trap y música urbana");

     private String nombre;
     private String descripcion;

    GeneroMusical(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
