package co.edu.uniquindio.monedero.controlador.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminClientesController {

    // Clase temporal; luego Cliente del modelo + ClienteServicio
    public static class Fila {
        final SimpleStringProperty nombre, documento, estado;

        Fila(String nombre, String documento, String estado) {
            this.nombre = new SimpleStringProperty(nombre);
            this.documento = new SimpleStringProperty(documento);
            this.estado = new SimpleStringProperty(estado);
        }
    }

    @FXML private TextField txtNombre, txtDocumento;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;
    @FXML private TableView<Fila> tabla;
    @FXML private TableColumn<Fila, String> colNombre, colDocumento, colEstado;

    private final ObservableList<Fila> datos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(c -> c.getValue().nombre);
        colDocumento.setCellValueFactory(c -> c.getValue().documento);
        colEstado.setCellValueFactory(c -> c.getValue().estado);
        tabla.setItems(datos);

        // Datos falsos
        datos.add(new Fila("Cliente Demo", "1001", "Activo"));
        datos.add(new Fila("Ana Gómez", "1002", "Activo"));
        datos.add(new Fila("Luis Pérez", "1005", "Bloqueado"));
    }

    @FXML
    private void crear() {
        String n = txtNombre.getText().trim();
        String d = txtDocumento.getText().trim();
        if (n.isEmpty() || d.isEmpty() || txtClave.getText().isBlank()) {
            lblMensaje.setText("Completa nombre, documento y clave");
            return;
        }
        for (Fila f : datos) {
            if (f.documento.get().equals(d)) {
                lblMensaje.setText("Ya existe un cliente con ese documento");
                return;
            }
        }
        // Luego: ClienteServicio.crearCliente(nombre, documento, clave)
        datos.add(new Fila(n, d, "Activo"));
        txtNombre.clear();
        txtDocumento.clear();
        txtClave.clear();
        lblMensaje.setText("Cliente creado");
    }

    @FXML
    private void alternarEstado() {
        Fila f = tabla.getSelectionModel().getSelectedItem();
        if (f == null) {
            lblMensaje.setText("Selecciona un cliente");
            return;
        }
        f.estado.set("Activo".equals(f.estado.get()) ? "Bloqueado" : "Activo");
        lblMensaje.setText(f.nombre.get() + ": " + f.estado.get());
    }
}