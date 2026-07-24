package notaria.jenny.ms_clientes.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientesRequestDTO {

    @NotBlank
    @Size (max = 200)
    private String nombreCompleto;

    @NotBlank
    @Size(max = 12)
    private String rut;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @NotBlank
    @Size(max = 255)
    private String direccion;

    @NotNull
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechaNacimiento;
}
