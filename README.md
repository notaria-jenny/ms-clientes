# ms-clientes

Microservicio de gestión de clientes de la Notaría Jenny. Expone un CRUD REST sobre las
personas que solicitan servicios notariales, con validación de RUT chileno, cálculo
automático de edad y documentación OpenAPI.

Forma parte de un sistema de microservicios junto a `ms-administradores` y `APIGateway`.
A diferencia de `ms-administradores`, este servicio no gestiona contraseñas ni roles:
los clientes son registros administrados, no usuarios que inician sesión.

## Stack

- Java 25 · Spring Boot 4.1.0
- Spring Data JPA · MySQL
- Spring HATEOAS · springdoc-openapi 3.0.3 · Spring Boot Actuator
- Lombok · DataFaker (datos de prueba)

## Requisitos

- JDK 25
- MySQL en ejecución (la base se crea sola, ver más abajo)
- Maven (o el wrapper `./mvnw` incluido)

## Configuración

La conexión se define por variables de entorno, con valores por defecto para desarrollo local:

| Variable      | Por defecto                                                          |
|---------------|----------------------------------------------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/db_clientes?createDatabaseIfNotExist=true` |
| `DB_USER`     | `root`                                                               |
| `DB_PASSWORD` | *(vacío)*                                                            |

El servicio corre en el puerto **8082**.

Dos decisiones de la configuración:

- `createDatabaseIfNotExist=true` — no hace falta crear `db_clientes` a mano.
- `ddl-auto: update` — el esquema se actualiza sin borrar datos, así que **los clientes
  persisten entre reinicios**.

## Cómo levantarlo

Local:

```bash
./mvnw spring-boot:run
```

Con Docker:

```bash
docker build -t ms-clientes . && docker run -p 8082:8082 ms-clientes
```

Con el perfil `dev` activo (el de por defecto), el `DataLoader` puebla la base con **150
clientes** generados con DataFaker, con RUT únicos y edades entre 18 y 90 años. Si la
tabla ya tiene registros, no hace nada.

## Documentación

- Swagger UI: http://localhost:8082/swagger-ui.html
- OpenAPI JSON: http://localhost:8082/v3/api-docs
- Health check: http://localhost:8082/actuator/health

## Endpoints

Ruta base: `/api/v2/clientes`

### CRUD

| Método  | Ruta                  | Descripción                    |
|---------|-----------------------|--------------------------------|
| `POST`  | `/`                   | Crear cliente                  |
| `PUT`   | `/{id}`               | Actualizar datos (sin el RUT)  |
| `PATCH` | `/{id}/toggle-activo` | Activar o desactivar           |

### Búsquedas

| Método | Ruta             | Descripción      |
|--------|------------------|------------------|
| `GET`  | `/{id}`          | Buscar por ID    |
| `GET`  | `/email/{email}` | Buscar por email |
| `GET`  | `/rut/{rut}`     | Buscar por RUT   |

### Listados

| Método | Ruta                       | Descripción                     |
|--------|----------------------------|---------------------------------|
| `GET`  | `/`                        | Todos, ordenados por nombre     |
| `GET`  | `/paginado?page=0&size=20` | Paginado y ordenable            |
| `GET`  | `/buscar?nombre=`          | Filtrar por nombre (parcial)    |
| `GET`  | `/activos?activo=true`     | Filtrar por estado              |
| `GET`  | `/fecha?desde=&hasta=`     | Por rango de fecha de registro  |
| `GET`  | `/contar/activo?activo=`   | Contar por estado               |

Las respuestas individuales incluyen enlaces HATEOAS (`self`, `toggle-activo`, `todos`).

## Modelo

| Campo             | Tipo        | Notas                                     |
|-------------------|-------------|-------------------------------------------|
| `idCliente`       | `Long`      | Autogenerado                              |
| `nombreCompleto`  | `String`    | Máx. 200                                  |
| `rut`             | `String`    | Único, validado con dígito verificador    |
| `email`           | `String`    | Único, formato validado                   |
| `telefono`        | `String`    | Máx. 20                                   |
| `direccion`       | `String`    | Máx. 255                                  |
| `fechaNacimiento` | `LocalDate` | Debe ser anterior a hoy (`@Past`)         |
| `activo`          | `Boolean`   | `true` al crear                           |
| `fechaRegistro`   | `LocalDate` | Asignada por el sistema                   |
| `edad`            | `Integer`   | **Calculada**, no se almacena             |

La edad se deriva de `fechaNacimiento` con `Period.between` y viaja en la respuesta como
campo `@Transient`. Se calcula al momento de consultar, así que nunca queda desactualizada
—que es justamente el motivo para no persistirla.

### Validación de RUT

El formato canónico de almacenamiento es **sin puntos, con guion y dígito verificador en
mayúscula**: `12345678-5`, `9876543-K`.

- El dígito verificador se valida con el algoritmo módulo 11.
- Antes de comparar duplicados y de guardar, el RUT se normaliza: se recortan espacios
  accidentales y la `k` pasa a mayúscula. Así la unicidad no depende de la collation de MySQL.
- Los RUT con puntos (`12.345.678-5`) se rechazan con `400`. Dar formato para mostrar es
  responsabilidad del frontend.
- El RUT es **inmutable**: identifica a la persona, por lo que el `PUT` no permite modificarlo.

## Manejo de erroress

Todas las respuestas de error comparten el mismo formato (`timestamp`, `status`, `error`):

| Código | Cuándo ocurre                                                     |
|--------|-------------------------------------------------------------------|
| `400`  | Validación fallida, rango de fechas invertido                     |
| `404`  | El cliente no existe                                              |
| `409`  | Email o RUT ya registrado                                         |
| `500`  | Error inesperado (el detalle queda en el log, no en la respuesta) |

## Estado actual

La API está completamente abierta (`permitAll`) para desarrollo. La autenticación está
planificada como JWT centralizado en el API Gateway, no en cada microservicio.