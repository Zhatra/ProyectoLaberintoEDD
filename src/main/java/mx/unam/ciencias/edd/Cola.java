package mx.unam.ciencias.edd;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString() {
        String text = "";
        Nodo nodo = cabeza;

        while (nodo != null) {
            text += nodo.elemento.toString() + ",";
            nodo = nodo.siguiente;
        }

        return text;
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento no es válido.");

        Nodo nodo = new Nodo(elemento);

        if (rabo == null)
            cabeza = rabo = nodo;
        else {
            rabo.siguiente = nodo;
            rabo = rabo.siguiente;
        }
    }
}