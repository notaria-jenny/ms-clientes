package notaria.jenny.ms_clientes.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientesResponseDTO extends RepresentationModel<ClientesResponseDTO> {

    private Long idCliente;
    private String nombreCompleto;
    private String rut;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private boolean activo;
    private LocalDate fechaRegistro;
}
