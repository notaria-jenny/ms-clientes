package notaria.jenny.ms_clientes.controller;

import notaria.jenny.ms_clientes.dto.ClientesRequestDTO;
import notaria.jenny.ms_clientes.dto.ClientesResponseDTO;
import notaria.jenny.ms_clientes.dto.ClientesUpdateDTO;
import notaria.jenny.ms_clientes.service.ClientesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/clientes")
@RequiredArgsConstructor
@Tag(
        name = "Clientes",
        description = "Gestión de clientes de la Notaría"
)
public class ClientesController {

    private final ClientesService clientesService;

    // ──────────────────────────────────────────────
    // CRUD
    // ──────────────────────────────────────────────

    @Operation(summary = "Crear un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email o RUT ya registrado")
    })
    @PostMapping
    public ResponseEntity<ClientesResponseDTO> crear(@Valid @RequestBody ClientesRequestDTO request) {
        ClientesResponseDTO response = clientesService.crear(request);
        agregarLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Email o RUT ya en uso por otro cliente")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientesResponseDTO> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ClientesUpdateDTO request) {
        ClientesResponseDTO response = clientesService.actualizar(id, request);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar o desactivar un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estado cambiado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<Void> toggleActivo(@PathVariable Long id) {
        clientesService.toggleActivo(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // BÚSQUEDAS INDIVIDUALES
    // ──────────────────────────────────────────────

    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientesResponseDTO> buscarPorId(@PathVariable Long id) {
        ClientesResponseDTO response = clientesService.buscarPorId(id);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar cliente por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ClientesResponseDTO> buscarPorEmail(@PathVariable String email) {
        ClientesResponseDTO response = clientesService.buscarPorEmail(email);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar cliente por RUT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<ClientesResponseDTO> buscarPorRut(@PathVariable String rut) {
        ClientesResponseDTO response = clientesService.buscarPorRut(rut);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────────
    // LISTADOS
    // ──────────────────────────────────────────────

    @Operation(summary = "Listar todos los clientes ordenados por nombre")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<CollectionModel<ClientesResponseDTO>> listarTodos() {
        List<ClientesResponseDTO> lista = clientesService.listarTodos();
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(ClientesController.class).listarTodos()).withSelfRel()));
    }

    @Operation(summary = "Listar clientes paginados (?page=0&size=20&sort=nombreCompleto)")
    @ApiResponse(responseCode = "200", description = "Página obtenida exitosamente")
    @GetMapping("/paginado")
    public ResponseEntity<Page<ClientesResponseDTO>> listarPaginado(
            @ParameterObject @PageableDefault(size = 20, sort = "nombreCompleto") Pageable pageable) {
        return ResponseEntity.ok(clientesService.listarPaginado(pageable));
    }

    @Operation(summary = "Buscar clientes por nombre")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<ClientesResponseDTO>> listarPorNombre(@RequestParam String nombre) {
        List<ClientesResponseDTO> lista = clientesService.listarPorNombre(nombre);
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(ClientesController.class).listarPorNombre(nombre)).withSelfRel()));
    }

    @Operation(summary = "Listar clientes por estado activo/inactivo")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/activos")
    public ResponseEntity<CollectionModel<ClientesResponseDTO>> listarActivos(@RequestParam Boolean activo) {
        List<ClientesResponseDTO> lista = clientesService.listarActivos(activo);
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(ClientesController.class).listarActivos(activo)).withSelfRel()));
    }

    @Operation(summary = "Listar clientes por rango de fecha de registro")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/fecha")
    public ResponseEntity<CollectionModel<ClientesResponseDTO>> listarPorFechaRegistro(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<ClientesResponseDTO> lista = clientesService.listarPorFechaRegistro(desde, hasta);
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(ClientesController.class).listarPorFechaRegistro(desde, hasta)).withSelfRel()));
    }

    // ──────────────────────────────────────────────
    // CONTADORES
    // ──────────────────────────────────────────────

    @Operation(summary = "Contar clientes por estado activo/inactivo")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
    @GetMapping("/contar/activo")
    public ResponseEntity<Long> contarPorActivo(@RequestParam Boolean activo) {
        return ResponseEntity.ok(clientesService.contarPorActivo(activo));
    }

    // ──────────────────────────────────────────────
    // LINKS HATEOAS
    // ──────────────────────────────────────────────

    private void agregarLinks(ClientesResponseDTO dto) {
        dto.add(linkTo(methodOn(ClientesController.class)
                .buscarPorId(dto.getIdCliente())).withSelfRel());
        dto.add(linkTo(methodOn(ClientesController.class)
                .toggleActivo(dto.getIdCliente())).withRel("toggle-activo"));
        dto.add(linkTo(methodOn(ClientesController.class)
                .listarTodos()).withRel("todos"));
    }
}
