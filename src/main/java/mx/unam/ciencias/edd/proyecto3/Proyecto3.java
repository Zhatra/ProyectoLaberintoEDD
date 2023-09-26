package mx.unam.ciencias.edd.proyecto3;

import java.io.BufferedInputStream;


/**
 * Proyecto 3
 */
public class Proyecto3 {
   public static void main(String[] args){
      // Verificar si se pasaron argumentos en la línea de comandos
      if(args.length == 0){
         try{
         // Si no se pasaron argumentos, se lee la entrada estándar para crear un laberinto
         BufferedInputStream in =new BufferedInputStream(System.in);
         // Crear una instancia de Laberinto con la entrada proporcionada
         Laberinto l = new Laberinto(in);
         // Resolver el laberinto
         l.resolverLaberinto(); 
         // Imprimir el laberinto resuelto en formato SVG
         System.out.println(l.toSVG());
         }catch (Exception e){}
         
      }
      else{
         // Si se pasaron argumentos, se crean las banderas correspondientes
         Bandera b = new Bandera(args);
         // Crear un objeto de Laberinto vacío
         Laberinto l;
         // Si la bandera no tiene una semilla, se crea el laberinto con ancho y alto
         if (b.getSeed() == null) {
             l = new Laberinto(b.getWidth(), b.getHeight());
         // Si la bandera tiene una semilla, se crea el laberinto con ancho, alto y semilla
         } else {
             l = new Laberinto(b.getWidth(), b.getHeight(), b.getSeed());
         }
         // Imprimir los bytes del laberinto
         l.imprimeBytes();
     }
    
     
      
   }
    
    
}
