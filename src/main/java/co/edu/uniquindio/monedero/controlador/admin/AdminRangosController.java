package co.edu.uniquindio.monedero.controlador.admin;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminRangosController {

    // Clase temporal; luego los datos vienen de RangoServicio
    public static class Fila {
        final SimpleStringProperty nombre, rango;
        final SimpleIntegerProperty puntos;
        final SimpleIntegerProperty puesto = new SimpleIntegerProperty();

        Fila(String nombre, int puntos) {
            this.nombre = new SimpleStringProperty(nombre);
            this.puntos = new SimpleIntegerProperty(puntos);
            this.rango = new SimpleStringProperty(calcular(puntos));
        }

        static String calcular(int p) {
            if (p <= 500) return "BRONCE";
            if (p <= 1000) return "PLATA";
            if (p <= 5000) return "ORO";
            return "PLATINO";
        }
    }

    @FXML private TableView<Fila> tabla;
    @FXML private TableColumn<Fila, String> colNombre, colRango;
    @FXML private TableColumn<Fila, Number> colPuesto, colPuntos;

    @FXML
    private void initialize() {
        colPuesto.setCellValueFactory(c -> c.getValue().puesto);
        colNombre.setCellValueFactory(c -> c.getValue().nombre);
        colPuntos.setCellValueFactory(c -> c.getValue().puntos);
        colRango.setCellValueFactory(c -> c.getValue().rango);

        // Datos falsos. Luego: RangoServicio (ArbolAVL, recorrido inorden descendente)
        ObservableList<Fila> datos = FXCollections.observableArrayList(
                new Fila("Cliente Demo", 320),
                new Fila("Ana Gómez", 1200),
                new Fila("Luis Pérez", 5400),
                new Fila("María Ruiz", 800));
        datos.sort((a, b) -> Integer.compare(b.puntos.get(), a.puntos.get()));
        for (int i = 0; i < datos.size(); i++) datos.get(i).puesto.set(i + 1);
        tabla.setItems(datos);
    }
}