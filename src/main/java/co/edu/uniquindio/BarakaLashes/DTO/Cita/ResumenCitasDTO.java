package co.edu.uniquindio.BarakaLashes.DTO.Cita;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumenCitasDTO {
    private long totalCitas;
    private long citasConfirmadas;
    private long citasPendientes;
    private long citasCanceladas;
    private long citasCompletadas;
}