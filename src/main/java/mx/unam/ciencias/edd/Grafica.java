package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().elemento;
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>,
                          ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* El diccionario de vecinos del vértice. */
        public Diccionario<T, Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            color = Color.NINGUNO;
            vecinos = new Diccionario<>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
            return Double.compare(distancia, vertice.distancia);
        }
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
            return vecino.elemento;
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return vecino.getGrado();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return vecino.color;
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos;
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica.Vertice v, Grafica.Vecino a);
    }

    /* Vértices. */
    private Diccionario<T, Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Diccionario<>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento es nulo.");

        if (contiene(elemento))
            throw new IllegalArgumentException("El elemento ya se encuentra en los vértices.");

        Vertice vertice = new Vertice(elemento);
        vertices.agrega(elemento, vertice);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        conecta(a, b, 1);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
        if (a.equals(b))
            throw new IllegalArgumentException("Los elementos son iguales.");

        if (peso <= 0)
            throw new IllegalArgumentException("El peso no es válido.");

        Vertice verticeA = (Vertice) vertice(a);
        Vertice verticeB = (Vertice) vertice(b);

        if (sonVecinos(verticeA.elemento, verticeB.elemento))
            throw new IllegalArgumentException("Los elementos ya están conectados.");

        verticeA.vecinos.agrega(b, new Vecino(verticeB, peso));
        verticeB.vecinos.agrega(a, new Vecino(verticeA, peso));
        aristas++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        Vertice verticeA = (Vertice) vertice(a);
        Vertice verticeB = (Vertice) vertice(b);

        if (!sonVecinos(verticeA.elemento, verticeB.elemento))
            throw new IllegalArgumentException("Los elementos no están conectados.");

        verticeA.vecinos.elimina(b);
        verticeB.vecinos.elimina(a);

        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return vertices.contiene(elemento);
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        Vertice vertice = (Vertice) vertice(elemento);

        for (Vecino vecino : vertice.vecinos)
            desconecta(vertice.elemento, vecino.vecino.elemento);

        vertices.elimina(elemento);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        Vertice verticeA = (Vertice) vertice(a);
        Vertice verticeB = (Vertice) vertice(b);

        return verticeA.vecinos.contiene(b);
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {
        if (!contiene(a))
            throw new NoSuchElementException("El vértice no es elemento de la gráfica.");

        Vertice vertice = (Vertice) vertice(b);

        if (!sonVecinos(a, b))
            throw new IllegalArgumentException("Los vértices no están conectados.");

        return vertice.vecinos.get(a).peso;
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        Vertice verticeA = (Vertice) vertice(a);
        Vertice verticeB = (Vertice) vertice(b);

        if (peso <= 0)
            throw new IllegalArgumentException("El peso es inválido.");

        if (!sonVecinos(verticeA.elemento, verticeB.elemento))
            throw new IllegalArgumentException("Los vértices no son vecinos.");

        verticeA.vecinos.get(b).peso = peso;
        verticeB.vecinos.get(a).peso = peso;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        if (!vertices.contiene(elemento))
            throw new NoSuchElementException("El elemento no se encuentra en la gráfica.");

        return vertices.get(elemento);
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if (vertice == null ||
                (vertice.getClass() != Vertice.class &&
                 vertice.getClass() != Vecino.class))
            throw new IllegalArgumentException("El vértice no es válido.");

        if (vertice.getClass() == Vertice.class) {
            Vertice verticeAux = (Vertice) vertice;
            verticeAux.color = color;
        }

        if (vertice.getClass() == Vecino.class) {
            Vecino verticeAux = (Vecino) vertice;
            verticeAux.vecino.color = color;
        }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
        // En realidad el for solo se ejecuta una vez, pero lo necesito para
        // poder acceder a algún vértice.
        for (Vertice vertice : vertices) {
            recorre(vertice.elemento, e -> {}, new Cola<Vertice>());
            break;
        }

        for (Vertice v : vertices)
            if (v.color == Color.ROJO)
                return false;

        return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice vertice : vertices)
            accion.actua(vertice);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        recorre(elemento, accion, new Cola<Vertice>());
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        recorre(elemento, accion, new Pila<Vertice>());
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices.limpia();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
        String texto = "{";

        for (Vertice vertice : vertices)
            texto += String.format("%s, ", vertice.elemento.toString());

        texto += "}, {";

        Conjunto<T> verticesPasados = new Conjunto<>();
        for (Vertice vertice : vertices) {
            for (Vecino vecino : vertice.vecinos)
                if (!verticesPasados.contiene(vecino.vecino.elemento))
                    texto += String.format("(%s, %s), ",
                        vertice.elemento.toString(), vecino.vecino.elemento.toString());

            verticesPasados.agrega(vertice.elemento);
        }

        texto += "}";
        return texto;
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;

        if (aristas != grafica.aristas ||
                vertices.getElementos() != grafica.vertices.getElementos())
            return false;

        for (Vertice vertice : vertices) {
            if (!grafica.contiene(vertice.elemento))
                return false;

            Vertice vertice2 = (Vertice) grafica.vertice(vertice.elemento);
            if (vertice.vecinos.getElementos() != vertice2.vecinos.getElementos())
                return false;

            for (Vecino vecino : vertice.vecinos) {
                if (!vertice2.vecinos.contiene(vecino.vecino.elemento))
                    return false;
            }
        }

        return true;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException("Los elementos no están en la gráfica.");

        Vertice vertice = (Vertice) vertice(origen);
        if (origen.equals(destino)) {
            Lista<VerticeGrafica<T>> lista = new Lista<>();
            lista.agrega(vertice);
            return lista;
        }

        for (Vertice v : vertices)
            v.distancia = Double.MAX_VALUE;

        vertice.distancia = 0;

        Cola<Vertice> cola = new Cola<>();
        cola.mete(vertice);

        while (!cola.esVacia()) {
            vertice = cola.saca();
            for (Vecino vecino : vertice.vecinos)
                if (vecino.vecino.distancia == Double.MAX_VALUE) {
                    vecino.vecino.distancia = vertice.distancia + 1;
                    cola.mete(vecino.vecino);
                }
        }

        return reconstruyeTrayectoria(
                (aux, vecino) -> vecino.vecino.distancia == aux.distancia - 1,
                (Vertice) vertice(destino));
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        if (!contiene(origen) || !contiene(destino))
            throw new NoSuchElementException("Los vértices no están en la gráfica.");

        for (Vertice vertice : vertices)
            vertice.distancia = Double.MAX_VALUE;

        Vertice verticeOrigen = (Vertice) vertice(origen);
        verticeOrigen.distancia = 0;

        MonticuloDijkstra<Vertice> monticulo;
        int n = vertices.getElementos();
        
        monticulo = new MonticuloMinimo<>(vertices, vertices.getElementos());

        while (!monticulo.esVacia()) {
            Vertice raiz = monticulo.elimina();

            for (Vecino vecino : raiz.vecinos)
                if (vecino.vecino.distancia > raiz.distancia + vecino.peso) {
                    vecino.vecino.distancia = raiz.distancia + vecino.peso;
                    monticulo.reordena(vecino.vecino);
                }
        }

        return reconstruyeTrayectoria(
                (vertice, vecino) -> vecino.vecino.distancia + vecino.peso == vertice.distancia,
                (Vertice) vertice(destino));
    }

    /**
     * Reconstruye la trayectoria desde un vértice destino dado hasta el
     * vértice de origen, según sus distancias y un buscador recibido.
     * @param buscador el buscador que nos indica si dos vértices son vecinos.
     * @param destino el vértice desde el cuál reconstruimos la trayectoria.
     * @return la trayectoria como una lista.
     */
    private Lista<VerticeGrafica<T>> reconstruyeTrayectoria(BuscadorCamino buscador, Vertice destino) {
        Vertice verticeAux = destino;
        Lista<VerticeGrafica<T>> trayectoria = new Lista<>();

        if (verticeAux.distancia == Double.MAX_VALUE)
            return new Lista<VerticeGrafica<T>>();

        trayectoria.agrega(verticeAux);

        while (verticeAux.distancia != 0) {
            for (Vecino vecino : verticeAux.vecinos) {
                if (buscador.seSiguen(verticeAux, vecino)) {
                    trayectoria.agrega(vecino.vecino);
                    verticeAux = vecino.vecino;
                    break;
                }

            }
        }

        return trayectoria.reversa();
    }

    /**
     * Realiza un recorrido a través de los vértices de la gráfica. Dependiendo
     * de la estructura recibida (Pila o Cola) será el tipo de recorrido (DFS o
     * BFS).
     * @param elemento el elemento por cuyo vértice comenzar el recorrido.
     * @param accion la acción a realizar en cada vértice.
     * @param estructura la estructura con la que realizamos el recorrido.
     */
    private void recorre(T elemento, AccionVerticeGrafica<T> accion, MeteSaca<Vertice> estructura) {
        Vertice vertice = (Vertice) vertice(elemento);

        paraCadaVertice((v) -> setColor(v, Color.ROJO));

        vertice.color = Color.NEGRO;
        estructura.mete(vertice);

        while (!estructura.esVacia()) {
            vertice = estructura.saca();
            accion.actua(vertice);

            for (Vecino vecino : vertice.vecinos)
                if (vecino.vecino.color == Color.ROJO) {
                    vecino.vecino.color = Color.NEGRO;
                    estructura.mete(vecino.vecino);
                }
        }
    }
}