package notaria.jenny.ms_clientes.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RutValidoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RutValido {

    String message() default "Rut inválido. Formato esperado: 12345678-5 (sin puntos, con guión) y dígito verificador" +
            " correcto";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
