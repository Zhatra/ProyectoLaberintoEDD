package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int resultado = 0;

        int posicion = 0;
        while(posicion < llave.length)
            resultado ^= combina(sacaInt(llave, posicion++), sacaInt(llave, posicion++),
                                 sacaInt(llave, posicion++), sacaInt(llave, posicion++));

        return resultado;
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int a = 0x9E3779B9;
        int b = 0x9E3779B9;
        int c = 0xFFFFFFFF;

        int posicion = 0;
        boolean ejecucion = true;
        while (ejecucion) {
            a += combina(sacaInt(llave, posicion+3), sacaInt(llave, posicion+2),
                         sacaInt(llave, posicion+1), sacaInt(llave, posicion));
            posicion += 4;

            b += combina(sacaInt(llave, posicion+3), sacaInt(llave, posicion+2),
                         sacaInt(llave, posicion+1), sacaInt(llave, posicion));
            posicion += 4;

            if (llave.length - posicion >= 4)
                c += combina(sacaInt(llave, posicion+3), sacaInt(llave, posicion+2),
                             sacaInt(llave, posicion+1), sacaInt(llave, posicion));
            else {
                ejecucion = false;
                c += llave.length;
                c += combina(sacaInt(llave, posicion+2), sacaInt(llave, posicion+1),
                            sacaInt(llave, posicion), 0);
            }
            posicion += 4;

            a -= b + c;
            a ^= (c >>> 13);
            b -= c + a;
            b ^= (a << 8);
            c -= a + b;
            c ^= (b >>> 13);

            a -= b + c;
            a ^= (c >>> 12);
            b -= c + a;
            b ^= (a << 16);
            c -= a + b;
            c ^= (b >>> 5);

            a -= b + c;
            a ^= (c >>> 3);
            b -= c + a;
            b ^= (a << 10);
            c -= a + b;
            c ^= (b >>> 15);
        }

        return c;
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int h = 5381;

        for (int i = 0; i < llave.length; i++)
            h += (h << 5) + sacaInt(llave, i);

        return h;
    }

    private static int combina(int a, int b, int c, int d) {
        return (a << 24) | (b << 16) | (c << 8)  | d;
    }

    private static int sacaInt(byte[] llave, int posicion) {
        if (posicion < llave.length)
            return (0xFF & llave[posicion]);

        return 0;
    }
}