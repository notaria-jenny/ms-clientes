package notaria.jenny.ms_clientes.repository;

import notaria.jenny.ms_clientes.model.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Long> {

    //por email
    Optional<Clientes> findByEmail(String email);
    boolean existsByEmail(String email);

    //por rut
    Optional<Clientes> findByRut(String rut);
    boolean existsByRut(String rut);

    //por nombre (parcial, ignora mayusculas)
    List<Clientes> findByNombreCompletoContainingIgnoreCase(String nombre);

    //por estado
    List<Clientes> findByActivo(Boolean activo);
    long countByActivo(Boolean activo);

    //por rango de fecha de registro
    List<Clientes> findByFechaRegistroBetween(LocalDate desde, LocalDate hasta);

    //por rango de fecha de nacimiento
    List<Clientes> findByFechaNacimientoBetween(LocalDate desde, LocalDate hasta);

    //ordenado alfabeticamente
    List<Clientes> findAllByOrderByNombreCompletoAsc();
}
