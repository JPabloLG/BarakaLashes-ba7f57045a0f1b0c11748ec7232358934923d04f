package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import co.edu.uniquindio.BarakaLashes.servicio.RecordatorioSchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecordatorioSchedulerServiceImpl implements RecordatorioSchedulerService {
    private final CitaServicio citaServicio;
    public RecordatorioSchedulerServiceImpl(CitaServicio citaServicio) {
        this.citaServicio = citaServicio;
    }
    @Override
    // Se ejecuta todos los días a las 8:00 a.m. (SIN parámetros, ya que @Scheduled no los permite)
    @Scheduled(cron = "0 08 15 * * *", zone = "America/Bogota")
    public void ejecutarRecordatoriosDiarios() throws Exception {  // QUITADO: String userEmail
        System.out.println("🕗 [Scheduler] Ejecutando recordatorios diarios: " + LocalDateTime.now());
        // Obtiene TODAS las citas de los próximos 2 días (de todos los usuarios)
        List<Cita> citasProximas = citaServicio.obtenerCitasProximosDosDias();
        // Envía un recordatorio para cada cita (a su respectivo usuario)
        for (Cita cita : citasProximas) {
            citaServicio.enviarRecordatorio(cita);
        }
        System.out.println("✅ [Scheduler] Recordatorios procesados: " + citasProximas.size());
    }
}