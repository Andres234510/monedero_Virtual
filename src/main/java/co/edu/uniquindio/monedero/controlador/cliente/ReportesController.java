package co.edu.uniquindio.monedero.controlador.cliente;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ReportesController {

    // Clase temporal; luego los datos vienen de ReporteServicio
    public static class Fila {
        final SimpleStringProperty concepto, valor, detalle;

        Fila(String concepto, String valor, String detalle) {
            this.concepto = new SimpleStringProperty(concepto);
            this.valor = new SimpleStringProperty(valor);
            this.detalle = new SimpleStringProperty(detalle);
        }
    }

    @FXML private ComboBox<String> cmbReporte;
    @FXML private Label lblTitulo, lblMensaje;
    @FXML private TableView<Fila> tabla;
    @FXML private TableColumn<Fila, String> colConcepto, colValor, colDetalle;
    @FXML private ListView<String> listaRelaciones;

    private final ObservableList<Fila> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cmbReporte.getItems().addAll(
                "Gasto por categoría",
                "Resumen de transacciones",
                "Transferencias entre clientes",
                "Puntos por tipo de transacción");
        cmbReporte.setValue("Gasto por categoría");

        colConcepto.setCellValueFactory(c -> c.getValue().concepto);
        colValor.setCellValueFactory(c -> c.getValue().valor);
        colDetalle.setCellValueFactory(c -> c.getValue().detalle);
        tabla.setItems(datos);
        generar();
    }

    @FXML
    private void generar() {
        String tipo = cmbReporte.getValue();
        if (tipo == null) {
            lblMensaje.setText("Elige un tipo de reporte");
            return;
        }
        lblMensaje.setText("");
        lblTitulo.setText(tipo);
        datos.clear();
        listaRelaciones.getItems().clear();

        // Datos falsos. Luego: ReporteServicio + AnalisisGastoServicio (grafos)
        switch (tipo) {
            case "Gasto por categoría" -> {
                datos.add(new Fila("Alimentación", "$ 420.000", "35%"));
                datos.add(new Fila("Transporte", "$ 180.000", "15%"));
                datos.add(new Fila("Entretenimiento", "$ 240.000", "20%"));
                datos.add(new Fila("Servicios", "$ 360.000", "30%"));
                listaRelaciones.getItems().addAll(
                        "Alimentación → Transporte (frecuencia: 12)",
                        "Servicios → Entretenimiento (frecuencia: 5)");
            }
            case "Resumen de transacciones" -> {
                datos.add(new Fila("Depósitos", "4", "$ 2.100.000"));
                datos.add(new Fila("Retiros", "3", "$ 650.000"));
                datos.add(new Fila("Transferencias", "2", "$ 500.000"));
            }
            case "Transferencias entre clientes" -> {
                datos.add(new Fila("A cuenta 1002", "$ 300.000", "2026-11-07"));
                datos.add(new Fila("A cuenta 1005", "$ 200.000", "2026-11-12"));
                listaRelaciones.getItems().addAll(
                        "Cliente Demo → 1002",
                        "Cliente Demo → 1005");
            }
            default -> {
                datos.add(new Fila("Depósitos", "21 pts", "1 por cada 100"));
                datos.add(new Fila("Retiros", "24 pts", "2 por cada 100"));
                datos.add(new Fila("Transferencias", "15 pts", "3 por cada 100"));
            }
        }
    }
}