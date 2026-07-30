package notaria.jenny.ms_clientes.service.impl;

import notaria.jenny.ms_clientes.dto.ClientesRequestDTO;
import notaria.jenny.ms_clientes.dto.ClientesResponseDTO;
import notaria.jenny.ms_clientes.dto.ClientesUpdateDTO;
import notaria.jenny.ms_clientes.exception.RecursoDuplicadoException;
import notaria.jenny.ms_clientes.exception.RecursoNoEncontradoException;
import notaria.jenny.ms_clientes.model.Clientes;
import notaria.jenny.ms_clientes.repository.ClientesRepository;
import notaria.jenny.ms_clientes.service.ClientesService;
import notaria.jenny.ms_clientes.util.RutUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientesServiceImpl implements ClientesService {

    private final ClientesRepository ClientesRepository;

    // ──────────────────────────────────────────────
    // CRUD
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public ClientesResponseDTO crear(ClientesRequestDTO request) {
        // Normaliza el RUT ("12345678-k" → "12345678-K") antes de comparar y guardar
        String rutNormalizado = RutUtil.normalizar(request.getRut());

        if (ClientesRepository.existsByEmail(request.getEmail()))
            throw new RecursoDuplicadoException("El email '" + request.getEmail() + "' ya está registrado");
        if (ClientesRepository.existsByRut(rutNormalizado))
            throw new RecursoDuplicadoException("El RUT '" + rutNormalizado + "' ya está registrado");

        Clientes cliente = new Clientes();
        cliente.setNombreCompleto(request.getNombreCompleto());
        cliente.setRut(rutNormalizado);
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setFechaNacimiento(request.getFechaNacimiento());
        cliente.setActivo(true);
        cliente.setFechaRegistro(LocalDate.now());

        return toResponse(ClientesRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClientesResponseDTO actualizar(
            Long id,
            ClientesUpdateDTO request
    ) {
        Clientes cliente = ClientesRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con ID " + id + " no encontrado"));

        boolean emailCambiado = !cliente
                .getEmail()
                .equalsIgnoreCase(request.getEmail());
        if (emailCambiado && ClientesRepository.existsByEmail(request.getEmail()))
            throw new RecursoDuplicadoException("El email '" + request.getEmail() + "' ya está en uso por otro cliente");

        cliente.setNombreCompleto(request.getNombreCompleto());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setFechaNacimiento(request.getFechaNacimiento());

        return toResponse(ClientesRepository.save(cliente));
    }

    @Override
    @Transactional
    public void toggleActivo(Long id) {
        Clientes cliente = ClientesRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con ID " + id + " no encontrado"));
        cliente.setActivo(!cliente.getActivo());
        ClientesRepository.save(cliente);
    }

    // ──────────────────────────────────────────────
    // BÚSQUEDAS INDIVIDUALES
    // ──────────────────────────────────────────────

    @Override
    public ClientesResponseDTO buscarPorId(Long id) {
        return ClientesRepository
                .findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con ID " + id + " no encontrado"));
    }

    @Override
    public ClientesResponseDTO buscarPorEmail(String email) {
        return ClientesRepository
                .findByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con email '" + email + "' no encontrado"));
    }

    @Override
    public ClientesResponseDTO buscarPorRut(String rut) {
        String rutNormalizado = RutUtil.normalizar(rut);
        return ClientesRepository
                .findByRut(rutNormalizado)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con RUT '" + rutNormalizado +
                        "' no encontrado"));
    }

    // ──────────────────────────────────────────────
    // LISTADOS
    // ──────────────────────────────────────────────

    @Override
    public List<ClientesResponseDTO> listarTodos() {
        return ClientesRepository
                .findAllByOrderByNombreCompletoAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public Page<ClientesResponseDTO> listarPaginado(Pageable pageable) {
        return ClientesRepository
                .findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public List<ClientesResponseDTO> listarPorNombre(String nombre) {
        return ClientesRepository
                .findByNombreCompletoContainingIgnoreCase(nombre)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ClientesResponseDTO> listarActivos(Boolean activo) {
        return ClientesRepository
                .findByActivo(activo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ClientesResponseDTO> listarPorFechaRegistro(
            LocalDate desde,
            LocalDate hasta
    ) {
        if (hasta.isBefore(desde))
            throw new IllegalArgumentException(
                    "La fecha 'hasta' (" + hasta + ") no puede ser anterior a la fecha 'desde' (" + desde + ")");

        return ClientesRepository
                .findByFechaRegistroBetween(
                        desde,
                        hasta
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ──────────────────────────────────────────────
    // CONTADORES
    // ──────────────────────────────────────────────

    @Override
    public long contarPorActivo(Boolean activo) {
        return ClientesRepository.countByActivo(activo);
    }

    // ──────────────────────────────────────────────
    // MAPPER
    // ──────────────────────────────────────────────

    private ClientesResponseDTO toResponse(Clientes cliente) {
        ClientesResponseDTO dto = new ClientesResponseDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setNombreCompleto(cliente.getNombreCompleto());
        dto.setRut(cliente.getRut());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setEdad(cliente.getEdad());          // ← la edad calculada (@Transient) viaja al JSON aquí
        dto.setActivo(cliente.getActivo());
        dto.setFechaRegistro(cliente.getFechaRegistro());
        return dto;
    }
}
