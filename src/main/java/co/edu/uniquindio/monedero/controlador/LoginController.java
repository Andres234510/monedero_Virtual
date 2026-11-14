package co.edu.uniquindio.monedero.controlador;

import co.edu.uniquindio.monedero.modelo.Cliente;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    @FXML
    private void iniciarSesion() throws Exception {
        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText();
        if (usuario.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("Completa los campos");
            return;
        }

        String vista;
        if (usuario.equalsIgnoreCase("admin")) {
            // Admin temporal: aún no existe en el sistema de tu compañero
            if (!clave.equals("admin")) {
                lblMensaje.setText("Clave de administrador incorrecta");
                return;
            }
            vista = "/vistas/admin/dashboard_admin.fxml";
        } else {
            try {
                Cliente c = Sesion.servicios().clientes().autenticar(usuario, clave);
                Sesion.iniciar(c);
            } catch (Exception ex) {
                lblMensaje.setText(ex.getMessage());
                return;
            }
            vista = "/vistas/cliente/dashboard.fxml";
        }

        Parent root = FXMLLoader.load(getClass().getResource(vista));
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/estilos/estilos.css").toExternalForm());
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}