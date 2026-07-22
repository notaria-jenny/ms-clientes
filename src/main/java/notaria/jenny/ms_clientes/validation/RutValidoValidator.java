package notaria.jenny.ms_clientes.validation;

import notaria.jenny.ms_clientes.util.RutUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidoValidator implements ConstraintValidator<RutValido, String> {

    @Override
    public boolean isValid(String rut, ConstraintValidatorContext context){

        if(rut == null || rut.isBlank()){

            return true;
        }
        return RutUtil.esValido(rut);
    }

}
