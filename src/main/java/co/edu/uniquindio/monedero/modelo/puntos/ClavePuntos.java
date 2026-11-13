package main.java.co.edu.uniquindio.monedero.modelo.puntos;

/**
 * Clave compuesta (puntos, idCliente) para árboles que ordenan clientes por
 * puntos: el id desempata, así dos clientes con los mismos puntos no se pisan.
 */
public final class ClavePuntos implements Comparable<ClavePuntos> {
    private final long puntos;
    private final String clienteId;

    public ClavePuntos(long puntos, String clienteId) {
        this.puntos = puntos;
        this.clienteId = clienteId;
    }

    public long getPuntos() { return puntos; }
    public String getClienteId() { return clienteId; }

    @Override
    public int compareTo(ClavePuntos otra) {
        int cmp = Long.compare(puntos, otra.puntos);
        return cmp != 0 ? cmp : clienteId.compareTo(otra.clienteId);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ClavePuntos c && c.puntos == puntos && c.clienteId.equals(clienteId);
    }

    @Override
    public int hashCode() { return 31 * Long.hashCode(puntos) + clienteId.hashCode(); }

    @Override
    public String toString() { return clienteId + ":" + puntos; }
}
