package main.java.co.edu.uniquindio.monedero.estructuras.grafos;

/** Arista dirigida con peso (monto acumulado, frecuencia, etc.). */
public class Arista<T> {
    private final T destino;
    private double peso;

    public Arista(T destino, double peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public T getDestino() { return destino; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
}
