package co.edu.uniquindio.monedero.controlador.admin;

import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminRangosController {

    public static class Fila {
        final SimpleIntegerProperty puesto;
        final SimpleStringProperty nombre, rango;
        final SimpleLongProperty puntos;

        Fila(int puesto, Cliente c) {
            this.puesto = new SimpleIntegerProperty(puesto);
            this.nombre = new SimpleStringProperty(c.getNombre());
            this.puntos = new SimpleLongProperty(c.getRegistroPuntos().getAcumulados());
            this.rango = new SimpleStringProperty(c.getRango().toString());
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

        ObservableList<Fila> datos = FXCollections.observableArrayList();
        int pos = 1;
        // Ranking del AVL de Andrés: mayor a menor puntaje
        for (Cliente c : Sesion.servicios().rangos().ranking(
                Sesion.servicios().rangos().totalClientes())) {
            datos.add(new Fila(pos++, c));
        }
        tabla.setItems(datos);
    }
}