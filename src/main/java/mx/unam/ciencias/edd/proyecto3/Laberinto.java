package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.Pila;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.VerticeGrafica;

import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Random;

/*Clase para crear Laberintos y resolverlos */
public class Laberinto implements Iterable<Laberinto.Casilla>{
    /**
     * La clase Iterador es un iterador personalizado para el laberinto, 
     * que permite recorrer cada una de las casillas de forma secuencial,
     *  fila por fila, de izquierda a derecha.
     */
    private class Iterador implements Iterator<Casilla>{
        private int fila;
        private int columna;
        public Iterador(){}
        @Override
        public boolean hasNext() {
            return fila != alto && columna != ancho; 
        }

        @Override
        public Casilla next() {
            Casilla c = laberinto[fila][columna];
            if (columna == ancho-1){
                columna = 0;
                fila = fila+1; 
                return c;
            }
            columna=columna +1;
            return c;

        }

    }
    
    /**
     * La clase Casilla representa una celda individual en el laberinto.
     * Cada casilla tiene cuatro posibles puertas (norte, este, sur y oeste)
     * y cada una puede estar abierta o cerrada. Además, cada casilla tiene una
     * posición específica en la grilla del laberinto (determinada por i y j), un 
     * tipo que indica su ubicación en la grilla (esquina, borde, etc.), y un puntaje.
     */
    public class Casilla{
        private boolean pNorte;
        private boolean pOeste;
        private boolean pSur;
        private boolean pEste;
        /*nos dice si ha sido visitado o no la casilla */
        private boolean visitado;

        /*i = hacia abajo
         *j= hacia la derecha
         */
        private int i,j ;

        /*El tipo de casilla */
        private int tipo;

        /*El puntaje del cuarto */
        private int puntaje;
        /*nos dice si esta casilla es inicio o final */
        private boolean inicio;
        private boolean fin;

        /*Casilla anterior a esta. Nos sirve par a recostruir el camino */

        public Casilla(int i,int j){
            this.i = i;
            this.j =j;
            pNorte = pEste = pOeste = pSur = true;
            asignaTipoCasilla();
            puntaje = r.nextInt(16);
        }

        // Constructor que crea una Casilla en una posición dada y establece los estados de las puertas según un byte
        public Casilla(int i,int j, byte b){
            this.puntaje = (b&0xf0)>>4;
            int casilla = b&(0x0f);
            // Extraer el estado de las puertas de los bits de orden inferior
            switch (casilla){
                case 0:
                    // No hay puertas
                    pSur = pEste = pNorte = pOeste = false;
                    break;
                case 1:
                    pOeste = true;
                    pSur = pEste = pNorte = false;
                    break;
                case 2:
                    pNorte = true;
                    pSur = pEste = pOeste = false;
                    break;
                case 3:
                    pNorte=pOeste = true;
                    pSur = pEste = false;
                    break;
                case 4:
                    pEste =true;
                    pSur = pNorte = pOeste = false;
                    break;
                case 5:
                    pEste=pOeste=true;
                    pSur = pNorte = false;
                    break;
                case 6:
                    pNorte=pEste=true;
                    pSur = pOeste = false;
                    break;
                case 7:
                    pEste=pNorte=pOeste = true;
                    pSur = false;
                    break;
                case 8:
                    pSur = true;
                    pEste = pNorte = pOeste = false;
                    break;
                case 9:
                    pSur=pOeste = true;
                    pEste = pNorte = false;
                    break;
                case 10:
                    pSur = pNorte= true;
                    pEste = pOeste = false;
                    break;
                case 11:
                    pSur = pNorte = pOeste = true;
                    pEste = false;
                    break;
                case 12:
                    pSur = pEste = true;
                    pNorte = pOeste = false;
                    break;
                case 13:
                    pSur = pEste = pOeste = true;
                    pNorte = false;
                    break;
                case 14:
                    pSur=pEste=pNorte = true;
                    pOeste = false;
                    break;
                default:
                    break;
                
               
            }
            this.i =i;
            this.j =j;
            asignaTipoCasilla();
        }
        // Método para representar la Casilla como una cadena
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(parteArriba()).append("\n").append(parteAbajo());
            return sb.toString();
        }
    
        // Método para marcar Casilla como el inicio del camino
        public void SetInicio(){
            this.pEste =false;
            this.inicio =true;
        }
        // Método para marcar Casilla como el final del camino
        public void SetFinal(){
            this.pOeste =false;
            this.inicio =true;
        }
        // Métodos para generar representaciones de cadena de la parte superior e inferior de la casilla, en función del estado de las puertas
        private String parteArriba(){
            String s;
            //000
            if (!pNorte && !pOeste && !pEste)
                s = "    ";
            //001
            else if (!pNorte && !pOeste && pEste)
                s = "|   ";
            //010
            else if (!pNorte && pOeste && !pEste)
                s = "   |";
            //011
            else if (!pNorte && pOeste && pEste)
                s = "|  |";
            //100
            else if (pNorte && !pOeste && !pEste)
                s = " ^^ ";
            //101
            else if (pNorte && !pOeste && pEste)
                s = "|^^ ";
            //110
            else if (pNorte && pOeste && !pEste)
                s = " ^^|";
            //111
            else s = "|^^|";
            return s;

        }

        private String parteAbajo(){
            String s;
            //000
            if (!pSur && !pOeste && !pEste)
                s = "    ";
            //000
            else if (!pSur && !pOeste && pEste)
                s = "|   ";
            //010
            else if (!pSur && pOeste && !pEste)
                s = "   |";
            //011
            else if (!pSur && pOeste && pEste)
                s = "|  |";
            //100
            else if (pSur && !pOeste && !pEste)
                s = " __ ";
            //101
            else if (pSur && !pOeste && pEste)
                s = "|__ ";
            //110
            else if (pSur && pOeste && !pEste)
                s = " __|";
            //111
            else s = "|__|";
            return s;
        }
        // Método para asignar el tipo de casilla en función de su posición en el laberinto
        private void asignaTipoCasilla() {
            // Si la casilla es la esquina superior izquierda, 
            if (i == 0 && j == 0)
                tipo = 8;
            // Si la casilla está en la primera fila, pero no en las esquinas, 
            else if (i == 0 && j > 0 && j < ancho - 1)
                tipo = 1;
            // Si la casilla es la esquina superior derecha, 
            else if (i == 0 && j == ancho - 1)
                tipo = 2;
            // Si la casilla está en la última columna, pero no en las esquinas, 
            else if (i > 0 && i < alto - 1 && j == ancho - 1)
                tipo = 3;
            // Si la casilla es la esquina inferior derecha, 
            else if (i == alto - 1 && j == ancho - 1)
                tipo = 4;
            // Si la casilla está en la última fila, pero no en las esquinas, 
            else if (i == alto - 1 && j > 0 && j < ancho - 1)
                tipo = 5;
            // Si la casilla es la esquina inferior izquierda, 
            else if (i == alto - 1 && j == 0)
                tipo = 6;
            // Si la casilla está en la primera columna, pero no en las esquinas.
            else if (i > 0 && i < alto - 1 && j == 0)
                tipo = 7;
            
            // Si la casilla no cumple ninguna de las condiciones anteriores, se asigna el tipo 0 (casilla interna)
            else
                tipo = 9; // Tipo de casilla por defecto para casillas internas
        }
        // Dibujar los muros
        private String dibujaMuro() {
            StringBuilder s = new StringBuilder();
        
            int x1, y1, x2, y2;
            
            if (pNorte && i == 0) {
                x1 = 0 + j * d;
                y1 = 0 + i * d;
                x2 = d + j * d;
                y2 = 0 + i * d;
        
                s.append("\t\t<line x1=\"").append(x1).append("\" y1=\"").append(y1).append("\" x2=\"").append(x2).append("\" y2=\"").append(y2).append("\" stroke=\"black\" stroke-with=\"3\" />\n");
            }
        
            if (pOeste) {
                x1 = d + j * d;
                y1 = 0 + i * d;
                x2 = d + j * d;
                y2 = d + i * d;
        
                s.append("\t\t<line x1=\"").append(x1).append("\" y1=\"").append(y1).append("\" x2=\"").append(x2).append("\" y2=\"").append(y2).append("\" stroke=\"black\" stroke-with=\"3\" />\n");
            }
        
            if (pSur) {
                x1 = d + j * d;
                y1 = d + i * d;
                x2 = 0 + j * d;
                y2 = d + i * d;
        
                s.append("\t\t<line x1=\"").append(x1).append("\" y1=\"").append(y1).append("\" x2=\"").append(x2).append("\" y2=\"").append(y2).append("\" stroke=\"black\" stroke-with=\"3\" />\n");
            }
        
            if (pEste && j == 0) {
                x1 = 0 + j * d;
                y1 = d + i * d;
                x2 = 0 + j * d;
                y2 = 0 + i * d;
        
                s.append("\t\t<line x1=\"").append(x1).append("\" y1=\"").append(y1).append("\" x2=\"").append(x2).append("\" y2=\"").append(y2).append("\" stroke=\"black\" stroke-with=\"3\" />\n");
            }
        
            return s.toString();
        }
        //Dibuja Bola
        private StringBuilder dibujaBolaBlue(){
            String cx = String.valueOf(10 + j*d);
            String cy = String.valueOf(10 + i*d);
            StringBuilder s = new StringBuilder("\t<circle cx=\"");
            s.append(cx);
            s.append("\" cy=\"");
            s.append(cy);
            s.append("\" r=\"8\" fill=\"blue\" stroke=\"black\" stroke-with=\"1\" />\n");
            return s;
        }
        private StringBuilder dibujaBolaRed(){
            String cx = String.valueOf(10 + j*d);
            String cy = String.valueOf(10 + i*d);
            StringBuilder s = new StringBuilder("\t<circle cx=\"");
            s.append(cx);
            s.append("\" cy=\"");
            s.append(cy);
            s.append("\" r=\"8\" fill=\"red\" stroke=\"black\" stroke-with=\"1\" />\n");
            return s;
        }

        //Dibuja linea Derecha
        private StringBuilder lineaDerecha(){
            String cx = String.valueOf(10 + j*d);
            String cy = String.valueOf(10 + i*d);
            StringBuilder s = new StringBuilder("\t<line x1=\"");
            s.append(cx);
            s.append("\" y1=\"");
            s.append(cy);
            cx = String.valueOf(10 + j*d +20);
            cy = String.valueOf( 10 + i*d);
            s.append("\" x2=\"");
            s.append(cx);
            s.append("\" y2=\"");
            s.append(cy);
            s.append("\" stroke=\"red\" stroke-with=\"3\" />\n");
            return s;
        }

        
        //Dibuja linea Abajo
        private StringBuilder lineaAbajo(){
            String cx = String.valueOf(10 + j*d);
            String cy = String.valueOf(10 + i*d);
            StringBuilder s = new StringBuilder("\t<line x1=\"");
            s.append(cx);
            s.append("\" y1=\"");
            s.append(cy);
            cx = String.valueOf(10 + j*d);
            cy = String.valueOf( 10 + i*d + 20);
            s.append("\" x2=\"");
            s.append(cx);
            s.append("\" y2=\"");
            s.append(cy);
            s.append("\" stroke=\"red\" stroke-with=\"3\" />\n");
            return s;
        }
        // Método para convertir el estado de la Casilla a un byte.
        public byte toByte(){
            int puerta = 0;
            if (!pSur && !pEste && !pNorte && !pOeste)
                puerta = 0;
            if (!pSur && !pEste && !pNorte && pOeste)
                puerta = 1;
            if (!pSur && !pEste && pNorte && !pOeste)
                puerta = 2;
            if (!pSur && !pEste && pNorte && pOeste)
                puerta = 3;
            if (!pSur && pEste && !pNorte && !pOeste)
                puerta = 4;
            if (!pSur && pEste && !pNorte && pOeste)
                puerta = 5;
            if (!pSur && pEste && pNorte && !pOeste)
                puerta = 6;
            if (!pSur && pEste && pNorte && pOeste)
                puerta = 7;
            if (pSur && !pEste && !pNorte && !pOeste)
                puerta = 8;
            if (pSur && !pEste && !pNorte && pOeste)
                puerta = 9;
            if (pSur && !pEste && pNorte && !pOeste)
                puerta = 10;
            if (pSur && !pEste && pNorte && pOeste)
                puerta = 11;
            if (pSur && pEste && !pNorte && !pOeste)
                puerta = 12;
            if (pSur && pEste && !pNorte && pOeste)
                puerta = 13;
            if (pSur && pEste && pNorte && !pOeste)
                puerta = 14;
            if (pSur && pEste && pNorte && pOeste)
                puerta = 15;

                puntaje = puntaje << 4;

                puerta = puntaje + puerta;

                return (byte) (puerta & 0xFF);
            
        }

}
    
    /* Laberinto modelado en una matriz */
    private Casilla[][] laberinto;
    /*Casilla inicio */
    private Casilla inicio;
    /*Casilla del final */
    private Casilla fin;

    private final int d = 20;
    /*Edd grafica para hacer Dijkstra */
    private Grafica<Casilla> grafica;

    private StringBuilder lineas;

    private int ancho, alto;

    private Random r = new Random();
    /**
     * Este es un constructor para la clase Laberinto. Toma el ancho y la 
     * altura como parámetros e inicializa los miembros de la clase. 
     * También selecciona una celda de inicio y fin aleatorios y construye el laberinto.
     * @param ancho
     * @param alto
     */
    public Laberinto(int ancho, int alto){
        this.r = new Random();
        this.ancho = ancho;
        this.alto = alto;
        this.laberinto = new Casilla[alto][ancho];
         for (int i = 0; i < alto;i++){
             for (int j = 0;  j < ancho; j++)
                laberinto[i][j]=new Casilla(i,j);
         }
         //Inicializar objetos
         lineas = new StringBuilder();

         //Seleccionar casilla
         inicio = seleccionaInicio();
         inicio.SetInicio();

         //Construir laberinto
         fin = seleccionaFin();
         fin.SetFinal();
         construyeLaberinto();
            
    }
    /**
     * Este es otro constructor para la clase Laberinto. Toma el ancho, 
     * la altura y una semilla para la generación de números aleatorios
     * como parámetros. Este constructor es útil si quieres poder 
     * reproducir el mismo laberinto varias veces.
     * @param ancho
     * @param alto
     * @param seed
     */
    public Laberinto(int ancho, int alto, int seed){
        this.r = new Random(seed);
        this.ancho = ancho;
        this.alto = alto;
        this.laberinto = new Casilla[alto][ancho];
         for (int i = 0; i < alto;i++){
             for (int j = 0;  j < ancho; j++)
                laberinto[i][j]=new Casilla(i,j);
         }
        //Inicializar objetos
        lineas = new StringBuilder();

        //Seleccionar casilla
        inicio = seleccionaInicio();
        inicio.SetInicio();

        //Construir laberinto
        fin = seleccionaFin();
        fin.SetFinal();
        construyeLaberinto();
    }

    /**
     * Constructor de laberinto dado unos bytes
     * @param in
     */
    public Laberinto(BufferedInputStream in){
        lineas = new StringBuilder();
        try{
            int info = 0;
            int i=0;

            int y = 0; //ancho
            int x = 0; //alto
            while ((i = in.read()) != -1){
                if(info > 5){
                    laberinto[x][y]=new Casilla(x,y,(byte)i);

                    if (y == ancho-1){
                        y = 0;
                        x = x+1;
                    }else{
                        y=y +1;
                    }



                    }else{
                    if(info ==0 && i != 0x4d)
                        throw new Exception("Se esperaba 0x4d en el valor 0");
                    if(info ==1 && i != 0x41)
                        throw new Exception("Se esperaba 0x41 en el valor 1");
                    if(info ==2 && i != 0x5a)
                        throw new Exception("Se esperaba 0x5a en el valor 2");
                    if(info ==3 && i != 0x45)
                        throw new Exception("Se esperaba 0x45 en el valor 3");

                    if(info == 4){
                        this.alto = i;
                    }
                    if(info == 5){
                        this.ancho = i;
                        laberinto = new Casilla[alto][ancho];
                    }
                    info++;
                }
            }
            in.close();
        }catch(Exception x){
            System.err.printf("Archivo .mze invalido");

        }

        
        Lista<Casilla> candidatos = new Lista<>();
        for (Casilla c:this){
            if (c.tipo != 9){
                if ((c.tipo == 8 || c.tipo == 7 || c.tipo ==6)&&c.pEste==false){
                    candidatos.agrega(c);
                }
                if ((c.tipo == 2 || c.tipo == 3 || c.tipo ==4)&&c.pOeste==false){
                    candidatos.agrega(c);
                    

                }
            }   
        }

        if (candidatos.getElementos() != 2)
            return;

        this.inicio = candidatos.getPrimero();
        inicio.SetInicio();

        this.fin = candidatos.getUltimo();
        fin.SetFinal();

        
        creaGrafica();
    }



    /**
     *  Toma un número de paredes y las elimina aleatoriamente del laberinto.
     * @param numeroParedes
     */
    private void eliminaParedesRandom(int numeroParedes) {
        for (int i = 0; i < numeroParedes; i++) {
            // Asegúrate de que la celda seleccionada no esté en el borde del laberinto
            int filaRandom;
            int columnaRandom;
            if (alto > 2) {
             filaRandom = r.nextInt(alto - 2) + 1;
            } else {
                continue;
            }
            if (ancho > 2) {
                columnaRandom = r.nextInt(ancho - 2) + 1;
            } else{
                continue;
            }
            //int filaRandom = r.nextInt(alto - 2) + 1;
            //int columnaRandom = r.nextInt(ancho - 2) + 1;
    
            Casilla c = laberinto[filaRandom][columnaRandom];
    
            // Selecciona aleatoriamente una pared para eliminar (si es posible)
            boolean[] paredes = {c.pNorte, c.pEste, c.pSur, c.pOeste};
    
            // Intenta hasta 4 veces (número de paredes) para eliminar una pared
            for (int j = 0; j < 4; j++) {
                int paredRandom = r.nextInt(4);
    
                // Si la pared existe, la elimina
                if (paredes[paredRandom]) {
                    switch (paredRandom) {
                        case 0:
                            c.pNorte = false;
                            break;
                        case 1:
                            c.pEste = false;
                            break;
                        case 2:
                            c.pSur = false;
                            break;
                        case 3:
                            c.pOeste = false;
                            break;
                    }
    
                    // Si la pared fue eliminada, se rompe el bucle
                    break;
                }
            }
        }
    }

    /**
     * Construye el laberinto utilizando un algoritmo de profundidad en primer lugar
     * y luego elimina algunas paredes aleatoriamente.
     */
    private void construyeLaberinto(){
        Pila<Casilla> p = new Pila<>();

        p.mete(inicio);
        while(!p.esVacia()){
            Casilla c = p.mira();
            c.visitado = true;

            Lista<Casilla> posibilidades = metePosibilidades(c);
            if (!posibilidades.esVacia()){
                if (posibilidades.getLongitud() == 1){
                    tiraPuertas(c, posibilidades.getPrimero());
                    p.mete(posibilidades.getPrimero());
                    
                }  
                else{
                    int random = r.nextInt(posibilidades.getLongitud());
                    Casilla siguiente = posibilidades.get(random);
                    tiraPuertas(c, siguiente);
                    p.mete(siguiente);
                }
            }
            else{
             p.saca();
            }
        }
        //Calcula el número de paredes a eliminar como el 2% del total de celdas
        int numeroParedesEliminar = (int) (0.1 * alto * ancho);
        eliminaParedesRandom(numeroParedesEliminar);

        creaGrafica();
    }
    
    /**
     * Resuelve el laberinto utilizando el algoritmo de Dijkstra.
     */
    public void resolverLaberinto(){
        
        Lista<VerticeGrafica<Casilla>> l = grafica.dijkstra(inicio, fin);
        Lista<Casilla> c = new Lista<>();
        //Pasar a lista
        for (VerticeGrafica<Casilla> v: l){
            c.agrega(v.get());
        }

        //Dibujar camino de lineas de la solucion
        for(Casilla a: c){
            // For the case to connect to the right:
            if (!a.pOeste && a.j < ancho - 1 && c.contiene(laberinto[a.i][a.j + 1])) {
                lineas.append(a.lineaDerecha());
            }
            // For the case to connect below:
            if (!a.pSur && a.i < alto - 1 && c.contiene(laberinto[a.i + 1][a.j])) {
                lineas.append(a.lineaAbajo());
            }
        }

        
    }

    
    /**
     * Tira puertas entre 2 casillas adyacentes, ya sea vertical u horizontal
     * @param c1
     * @param c2
     */
    private void tiraPuertas(Casilla c1, Casilla c2) {
        if (c1.i == c2.i){
            //tirar horizontalmente
            if (c1.j < c2.j){
                c2.pEste = false;
                c1.pOeste = false;
            }else{
                c1.pEste = false;
                c2.pOeste = false;
            }
        }else {
            if (c1.i < c2.i){
                c2.pNorte = false;
                c1.pSur = false;
            }else{
                c1.pNorte = false;
                c2.pSur = false;
            }
        }
    }

    /**
     * Mete las posibles casillas a avanzar desde la casilla
     * @param c
     * @return
     */
    private Lista<Casilla> metePosibilidades(Casilla c) {
        Casilla c1, c2, c3, c4;
        Lista<Casilla> posibilidades = new Lista<>();
        switch (c.tipo) {
            case 8:
                c1 = laberinto[c.i][c.j + 1];
                c2 = laberinto[c.i + 1][c.j];
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                break;
            case 1:
                c1 = laberinto[c.i][c.j + 1];
                c2 = laberinto[c.i + 1][c.j];
                c3 = laberinto[c.i][c.j - 1];
    
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                if (c3.visitado == false)
                    posibilidades.agrega(c3);
                break;
            case 2:
                c1 = laberinto[c.i][c.j - 1];
                c2 = laberinto[c.i + 1][c.j];
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                break;
            case 3:
                c1 = laberinto[c.i + 1][c.j];
                c2 = laberinto[c.i][c.j - 1];
                c3 = laberinto[c.i - 1][c.j];
    
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                if (c3.visitado == false)
                    posibilidades.agrega(c3);
                break;
            case 4:
                c1 = laberinto[c.i][c.j - 1];
                c2 = laberinto[c.i - 1][c.j];
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                break;
            case 5:
                c1 = laberinto[c.i][c.j - 1];
                c2 = laberinto[c.i - 1][c.j];
                c3 = laberinto[c.i][c.j + 1];
    
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                if (c3.visitado == false)
                    posibilidades.agrega(c3);
                break;
            case 6:
                c1 = laberinto[c.i - 1][c.j];
                c2 = laberinto[c.i][c.j + 1];
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                break;
            case 7:
                c1 = laberinto[c.i + 1][c.j];
                c2 = laberinto[c.i][c.j + 1];
                c3 = laberinto[c.i - 1][c.j];
    
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                if (c3.visitado == false)
                    posibilidades.agrega(c3);
                break;
            case 9:
                c1 = laberinto[c.i + 1][c.j];
                c2 = laberinto[c.i][c.j - 1];
                c3 = laberinto[c.i - 1][c.j];
                c4 = laberinto[c.i][c.j + 1];
    
                if (c1.visitado == false)
                    posibilidades.agrega(c1);
                if (c2.visitado == false)
                    posibilidades.agrega(c2);
                if (c3.visitado == false)
                    posibilidades.agrega(c3);
                if (c4.visitado == false)
                    posibilidades.agrega(c4);
                break;
            default:
                break;
    
        }
        return posibilidades;
    }
    
    /**
     * Selecciona aleatoriamente el inicio del laberinto
     * @return
     */
    private Casilla seleccionaInicio(){
    int alturaRandom = r.nextInt(alto); // Asume que 'altura' es la altura del laberinto

    return laberinto[alturaRandom][0];
    }

    /**
     * Selecciona aleatoriamente el final del laberinto
     * @return
     */
    private Casilla seleccionaFin(){
    int alturaRandom = r.nextInt(alto); // Asume que 'altura' es la altura del laberinto

    return laberinto[alturaRandom][ancho-1];
    }

   
    
    /**
     * Imprime el laberinto en un formato específico de bytes.
     */
    public void imprimeBytes(){
        byte m = (byte)(0x4d);
        byte a = (byte)(0x41);
        byte z = (byte)(0x5a);
        byte e = (byte)(0x45);

        byte filas = (byte)(alto&0xFF);
        byte columnas = (byte)(ancho&0xFF);

        try{
            PrintStream o = new PrintStream(System.out);
            o.write(m);
            o.write(a);
            o.write(z);
            o.write(e);
            o.write(filas);
            o.write(columnas);
            for(Casilla c: this){
                int b = ((int)(c.toByte()))&0xff;
                o.write(b);
            }

            o.close();
        }catch(Exception ioe){}

    }

    /**
     * crea una representación gráfica del laberinto
     */
    private void creaGrafica(){
        grafica = new Grafica<>();
        //agregar todos los vertices 
        for (Casilla c:this)
            grafica.agrega(c);

        for(Casilla c: this){
            if(c.pOeste == false && c.j<ancho-1)
                grafica.conecta(c, laberinto[c.i][c.j+1], 1 +c.puntaje +laberinto[c.i][c.j+1].puntaje);
                
            if(c.pSur == false && c.i<alto-1)
                grafica.conecta(c, laberinto[c.i+1][c.j], 1 +c.puntaje +laberinto[c.i+1][c.j].puntaje);
                
        }


    }


    /**
     * Este es un método sobrescrito toString. Convierte el laberinto en una cadena de texto.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < alto; i++){
            StringBuilder x = new StringBuilder();
            StringBuilder y = new StringBuilder();
            for (int j = 0; j < ancho; j++){
                x.append(laberinto[i][j].parteArriba());
                y.append(laberinto[i][j].parteAbajo());
               
            }
            sb.append(x).append("\n").append(y).append("\n");
        }
         return sb.toString();   
    }

    /**
     * Este método convierte el laberinto en un formato de gráfico vectorial escalable (SVG). 
     * @return
     */
    public String toSVG(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version = \"1.0\" encoding = \"utf-8\" ?>\n");
        sb.append(String.format("<svg width=\"%d\" height=\"%d\">\n", ancho*d,alto*d));
        sb.append(lineas);
        for(Casilla c: this){
            sb.append(c.dibujaMuro());            
        }
        sb.append(inicio.dibujaBolaBlue());
        sb.append(fin.dibujaBolaRed());

        sb.append("</svg>");
        return sb.toString();
    }
    
    /**
     * Este método sobrescribe el método iterator de la interfaz Iterable.
     */
    @Override
    public Iterator<Casilla> iterator() {
        return new Iterador();
    }
}