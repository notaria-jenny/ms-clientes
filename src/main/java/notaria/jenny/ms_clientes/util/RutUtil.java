package notaria.jenny.ms_clientes.util;

public final class RutUtil {

    private RutUtil(){

    }

    public static String normalizar(String rut){

        if (rut == null) return null;
        return rut.trim().toUpperCase();

    }

    public static boolean esValido(String rut) {
        String limpio = normalizar(rut);
        if (limpio == null || !limpio.matches("^\\d{7,8}-[\\dK]$")) {
            return false;
        }
        String[] partes = limpio.split("-");
        int numero = Integer.parseInt(partes[0]);
        return calcularDv(numero).equals(partes[1]);
    }

    /** Calcula el dígito verificador (módulo 11). Devuelve "0"-"9" o "K". */
    public static String calcularDv(int numero) {
        int suma = 0;
        int multiplicador = 2;
        while (numero != 0) {
            suma += (numero % 10) * multiplicador;
            numero /= 10;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resultado = 11 - (suma % 11);
        if (resultado == 11) return "0";
        if (resultado == 10) return "K";
        return String.valueOf(resultado);
    }

}
