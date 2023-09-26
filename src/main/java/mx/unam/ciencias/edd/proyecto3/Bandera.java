package mx.unam.ciencias.edd.proyecto3;
import mx.unam.ciencias.edd.Lista;

// Clase Bandera que maneja las banderas pasadas en la línea de comandos
public class Bandera {
    private Integer seed = null; // Semilla para generar el laberinto / inicializamos la semilla como null
    private int height; // Altura del laberinto
    private int width; // Anchura del laberinto
    private Lista<String> argumentos;  // Lista de argumentos pasados en la línea de comandos
    // Método para procesar cada bandera individualmente
    private void processFlag(String flag) {
        if (!argumentos.contiene(flag)) {
            if (!flag.equals("-s")) {  // si la bandera no es "-s"
                // Imprime un error y termina el programa
                System.err.printf("No se paso la bandera %s%n", flag);
                uso();
                System.exit(1);
            }
        } else {
            int i = argumentos.indiceDe(flag);
            try {
                // Si el índice de la bandera más 1 es mayor o igual a la longitud de los argumentos, lanza una excepción
                if (i + 1 >= argumentos.getLongitud()) {
                    throw new Exception();
                }
                String value = argumentos.get(i + 1);
                int intValue = Integer.parseInt(value);
                // Si la bandera es "-w" o "-h", y el valor es menor que 2 o mayor que 255, imprime un error y termina el programa
                if (flag.equals("-w") || flag.equals("-h")) {
                    if(intValue < 2){
                        System.err.printf("El valor para %s no debe ser menor que 2%n", flag);
                        uso();
                        System.exit(1);
                    }
                    if (intValue > 255) {
                        System.err.printf("El valor para %s debe ser menor que 256%n", flag);
                        uso();
                        System.exit(1);
                    }
                    
                }
                // Dependiendo de la bandera, establece la semilla, la altura o la anchura
                switch (flag) {
                    case "-w":
                        this.width = intValue;
                        break;
                    case "-h":
                        this.height = intValue;
                        break;
                    case "-s":
                        this.seed = intValue;
                        break;
                }
                // Elimina la bandera y su valor de la lista de argumentos
                argumentos.elimina(flag);
                argumentos.elimina(value);
            } catch (Exception e) {
                // Si se produce una excepción, imprime un error y termina el programa
                System.err.printf("No se paso correctamente la bandera %s. Se esperaba un numero entero.%n", flag);
                uso();
                System.exit(1);
            }
        }
    }
    // Constructor de la clase que toma los argumentos de la línea de comandos
    public Bandera(String[] args) {
        this.argumentos = new Lista<>();
        // Agrega cada argumento a la lista de argumentos
        for (String s: args) {
            argumentos.agrega(s);
        }
        // Si no se pasa la bandera "-g", imprime un error y termina el programa
        if (!argumentos.contiene("-g")) {
            System.err.print("No se paso la bandera -g");
            uso();
            System.exit(1);
        } else {
            int i = argumentos.indiceDe("-g");
            String g = argumentos.get(i);
            argumentos.elimina(g);
        }
        // Procesa las banderas "-w" y "-h"
        processFlag("-w");
        processFlag("-h");
        // Si se pasa la bandera "-s", la procesa
        if (argumentos.contiene("-s")) {  // si la lista de argumentos contiene "-s"
            processFlag("-s");
        }
    }
    // Método que imprime el uso correcto del programa
    public void uso(){
        System.err.println("\nUso: java -jar target/proyecto3.jar -g [-s <semilla>] -w <ancho> -h <alto>");
    }
    // Getters para la semilla, la altura y la anchura
    public Integer getSeed(){
        return seed;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }
    
}
