package co.edu.uniquindio.monedero.controlador.admin;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.time.LocalDate;

public class AdminProcesosController {
    @FXML private ListView<String> log;

    // Datos falsos; luego vienen de TransaccionServicio
    private final double[] montos = {500000, 120000, 300000, 870000};

    @FXML
    private void procesar() {
        // Luego: ProgramadorServicio (saca de la ColaPrioridad las de fecha <= hoy)
        log.getItems().add(0, LocalDate.now() + " - 2 transacciones programadas procesadas (simulado)");
    }

    @FXML
    private void verificar() {
        // Luego: IntegridadServicio / VerificadorRecursivo
        boolean ok = verificarDesde(0);
        log.getItems().add(0, LocalDate.now() + " - Integridad: " + (ok ? "CORRECTA" : "ERROR DETECTADO"));
    }

    // Verificación recursiva: cada transacción debe ser válida y el resto también
    private boolean verificarDesde(int i) {
        if (i == montos.length) return true;
        if (montos[i] <= 0) return false;
        return verificarDesde(i + 1);
    }
}