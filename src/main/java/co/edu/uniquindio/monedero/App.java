package co.edu.uniquindio.monedero;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        scene.getStylesheets().add(getClass().getResource("/estilos/estilos.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Monedero Virtual");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}