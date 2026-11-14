package co.edu.uniquindio.monedero.modelo;

public enum CategoriaGasto {
    ALIMENTACION("Alimentación"),
    TRANSPORTE("Transporte"),
    SERVICIOS("Servicios"),
    ENTRETENIMIENTO("Entretenimiento"),
    SALUD("Salud"),
    EDUCACION("Educación"),
    OTROS("Otros");

    private final String etiqueta;

    CategoriaGasto(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
