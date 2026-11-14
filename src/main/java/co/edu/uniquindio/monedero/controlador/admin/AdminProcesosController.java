package co.edu.uniquindio.monedero.controlador.admin;

import co.edu.uniquindio.monedero.servicio.IntegridadServicio;
import co.edu.uniquindio.monedero.servicio.ProgramadorServicio;
import co.edu.uniquindio.monedero.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.time.LocalDate;

public class AdminProcesosController {
    @FXML private ListView<String> log;

    @FXML
    private void procesar() {
        LocalDate hoy = LocalDate.now();
        try {
            int avisos = Sesion.servicios().programador().enviarRecordatorios(hoy);
            int ok = 0;
            int fallos = 0;
            for (ProgramadorServicio.Ejecucion e : Sesion.servicios().programador().procesarVencidas(hoy)) {
                if (e.exitosa()) {
                    ok++;
                } else {
                    fallos++;
                    log.getItems().add(0, "   Falló " + e.idProgramada() + " (" + e.fechaPrevista() + "): " + e.mensaje());
                }
            }
            log.getItems().add(0, hoy + " - Programadas: " + ok + " ejecutadas, " + fallos
                    + " con error. Recordatorios enviados: " + avisos
                    + ". Pendientes en cola: " + Sesion.servicios().programador().pendientesEnCola());
        } catch (Exception ex) {
            log.getItems().add(0, "Error: " + ex.getMessage());
        }
    }

    @FXML
    private void verificar() {
        try {
            int invalidos = 0;
            for (IntegridadServicio.Informe inf : Sesion.servicios().integridad().verificarTodos()) {
                if (!inf.valido()) {
                    invalidos++;
                    for (String problema : inf.problemas()) {
                        log.getItems().add(0, "   Cliente " + inf.clienteId() + ": " + problema);
                    }
                }
            }
            log.getItems().add(0, LocalDate.now() + " - Integridad: "
                    + (invalidos == 0 ? "CORRECTA en todos los clientes"
                    : "ERRORES en " + invalidos + " cliente(s)"));
        } catch (Exception ex) {
            log.getItems().add(0, "Error: " + ex.getMessage());
        }
    }
}