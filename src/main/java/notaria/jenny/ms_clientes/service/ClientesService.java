package notaria.jenny.ms_clientes.service;

import notaria.jenny.ms_clientes.dto.ClientesRequestDTO;
import notaria.jenny.ms_clientes.dto.ClientesResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ClientesService {

    ClientesResponseDTO crear(ClientesRequestDTO request);
    ClientesResponseDTO actualizar(Long id, ClientesRequestDTO request);
    void toggleActivo(Long id);

    ClientesResponseDTO buscarPorId(Long id);
    ClientesResponseDTO buscarPorEmail(String email);
    ClientesResponseDTO buscarPorRut(String rut);

    List<ClientesResponseDTO> listarTodos();
    Page<ClientesResponseDTO> listarPaginado(Pageable pageable);
    List<ClientesResponseDTO> listarPorNombre(String nombre);
    List<ClientesResponseDTO> listarActivos(Boolean activo);
    List<ClientesResponseDTO> listarPorFechaRegistro(LocalDate desde, LocalDate hasta);

    long     contarPorActivo(Boolean activo);

}
