package co.edu.uniquindio.BarakaLashes.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RegistroDTO {
    private String nombre;
    private String apellido;
    private String cedula;
    private String email;
    private String telefono;
    private String password;
    private String confirmarPassword;
}