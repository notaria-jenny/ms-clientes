package notaria.jenny.ms_clientes.util;

import notaria.jenny.ms_clientes.model.Clientes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import notaria.jenny.ms_clientes.repository.ClientesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private static final int CANTIDAD = 150;

    private final ClientesRepository clientesRepository;

    @Override
    public void run(String... args) throws Exception{
        if (clientesRepository.count() > 0){
            log.info(">> ms-clientes: Base de datos ya contiene datos, omitiendo DataLoader.");
            return;

        }

        Faker faker = new Faker();
        Random random = new Random();
        Set<String> rutsUsados = new HashSet<>();
        for (int i = 0; i < CANTIDAD; i++){

            Clientes clientes = new Clientes();
            clientes.setNombreCompleto(faker.name().fullName());
            clientes.setRut(generarRutUnico(random, rutsUsados));
            clientes.setEmail("cliente " + i + "." + faker.internet().emailAddress());
            clientes.setTelefono("569" + faker.number().numberBetween(10000000, 99999999));
            clientes.setDireccion(faker.address().fullAddress());
            clientes.setFechaNacimiento(generarFechaNacimiento(faker));
            clientes.setActivo(faker.bool().bool());
            clientes.setFechaRegistro(LocalDate.now().minusDays(faker.number().numberBetween(1, 730)));
            clientesRepository.save(clientes);

        }

        log.info(">> ms-clientes: Clientes generados con DataFaker exitosamente!", CANTIDAD);

    }

    private String generarRutUnico(Random random, Set<String> usados){

        String rut;
        do{

            int numero = random.nextInt(20000000-5000000) + 5000000;
            rut = numero + "-" + RutUtil.calcularDv(numero);

        } while (!usados.add(rut));
        return rut;

    }

    private LocalDate generarFechaNacimiento(Faker faker){

        return faker.timeAndDate().birthday(18, 90).atStartOfDay(ZoneId.systemDefault()).toLocalDate();

    }

}
