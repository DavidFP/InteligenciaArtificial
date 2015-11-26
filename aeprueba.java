package mouserun.mouse;

import mouserun.game.*;
import java.util.*;

/**
     * VARIABLES laberinto: almacena la información del laberinto
     * nodosCalculados:contiene los nodos visitados/conocidos
     * esInaccesible:marca si el nodo es accesible camino: almacena los
     * movimientos que debe seguir el ratón contadorMovimientos:la usamos para
     * saber cuando colocar bombas BombarQuedan: Numero de bombas que dejamos
     * que el raton ponga
     */

public class aeprueba extends Mouse {
    
    private HashMap<Pair<Integer, Integer>, Nodo> laberinto;
 
    private boolean esInaccesible;
    private Stack<Integer> camino;
    private int contadorMovimientos;
    private int bombasQuedan;

    /**
     * Pair sirve para almacenar la clave del mapa hash usado para almacenar el
     * laberinto
     */
    private class Pair<A, B> {

        public A posX;
        public B posY;

        /*CONSTRUCTORES*/
        public Pair() {
        }

        public Pair(A _first, B _second) {
            posX = _first;
            posY = _second;
        }

        /*
         *Redefinimos las clases con una sobrecarga del método equals y hascode
         */
        @Override
        public boolean equals(Object objeto) {
            if (this == objeto) {
                return true;
            }
            if (!(objeto instanceof Pair)) {
                return false;
            }
            Pair key = (Pair) objeto;
            return posX == key.posX && posY == key.posY;
        }

        @Override
        public int hashCode() {
            if (posX instanceof Integer && posY instanceof Integer) {
                Integer result = (Integer) posX;
                Integer sec = (Integer) posY;
                return result * 1000000 + sec;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Posición en X: " + posX + "  Posición en Y: " + posY;
        }
    }

    /**
     * Almacenamos una posición y las direcciones a las que se puede acceder
     * desde ella
     */
    private class Nodo {

        public int x;
        public int y;

        public boolean up;
        public boolean down;
        public boolean left;
        public boolean right;

        public boolean explorada;

        /*
         *CONSTRUCTORES
         */
        public Nodo(int _x, int _y, boolean _up, boolean _down, boolean _left, boolean _right) {
            x = _x;
            y = _y;

            up = _up;
            down = _down;
            left = _left;
            right = _right;
            explorada = true;
        }

        public Nodo(Pair<Integer, Integer> posicion, boolean _up, boolean _down, boolean _left, boolean _right) {
            this(posicion.posX, posicion.posY, _up, _down, _left, _right);
        }

        public Nodo(int _x, int _y) {
            x = _x;
            y = _y;
            explorada = false;
        }

        public Nodo(Pair<Integer, Integer> pos) {
            this(pos.posX, pos.posY);
        }

        public Pair<Integer, Integer> getPosicion() {
            return new Pair(x, y);
        }
        /*
         *Redefinimos las clases con una sobrecarga del método equals y hascode
         */

        @Override
        public boolean equals(Object objeto) {
            if (this == objeto) {
                return true;
            }
            if (!(objeto instanceof Nodo)) {
                return false;
            }
            Nodo node = (Nodo) objeto;
            return x == node.x && y == node.y;
        }

        /**
         * @return clave para el mapa hash
         */
        @Override
        public int hashCode() {
            return x * 10000 + y;
        }

        @Override
        public String toString() {
            return "Posicion X: " + x + " Posicion Y: " + y;
        }
    }

    

    /**
     * Función principal con la que calculamos el camino al queso o a la mejor
     * casilla posible
     */
    private void algoritmoAEstrella(Nodo NodoPrincipal, Pair<Integer, Integer> queso) {
        Nodo celdaObjetivo;
        Nodo posicionRaton;
        List<Nodo> noExploradas = new ArrayList();
        HashMap<Pair<Integer, Integer>, Nodo> anteriores = getPadre(NodoPrincipal, queso, noExploradas);

        /*Si tenemos almacenado el queso hemos encontrado el queso, sino buscamos la mejor casilla a la que ir para seguir buscando*/
        if (laberinto.containsKey(queso) && anteriores.containsKey(queso)) {
            celdaObjetivo = laberinto.get(queso);
        } else {
            int i = getMenorDistancia(noExploradas, queso);
            celdaObjetivo = noExploradas.get(i);
            esInaccesible = true;
        }

        Pair<Integer, Integer> casillaMejorOpcion = celdaObjetivo.getPosicion();
        int contadorOpciones = 0;
        
        /*Una vez que estamos en la casilla más cercana vemos que opciones son accesibles*/
        while (contadorOpciones < 4) {
            switch (contadorOpciones) {
                case 0:
                    casillaMejorOpcion.posX++;
                    break;
                case 1:
                    casillaMejorOpcion.posX -= 2;
                    break;
                case 2:                    
                    casillaMejorOpcion.posY++;
                    break;
                case 3:
                    casillaMejorOpcion.posY -= 2;
                    break;
            }
            if (laberinto.containsKey(casillaMejorOpcion)) {
                if (laberinto.get(casillaMejorOpcion).explorada == true) {
                    esInaccesible = false;
                }
            }
            contadorOpciones++;
        }

        posicionRaton = anteriores.get(celdaObjetivo.getPosicion());
        camino.add(getDireccion(posicionRaton.getPosicion(), celdaObjetivo.getPosicion()));

        while (true) {
            if (posicionRaton== NodoPrincipal) {
                break;
            }
            Pair<Integer, Integer> targetPos = posicionRaton.getPosicion();
            posicionRaton = anteriores.get(posicionRaton.getPosicion());
            camino.add(getDireccion(posicionRaton.getPosicion(), targetPos));
        }
    }

    /**
     * Funcion que obtiene los nodos anteriores, los "padres"
     */
    private HashMap<Pair<Integer, Integer>, Nodo> getPadre(Nodo nodoPrincipal, Pair<Integer, Integer> queso, List<Nodo> noExploradas) {
        
        HashMap<Pair<Integer, Integer>, Nodo> anteriores = new HashMap();
        Queue<Nodo> colaNodos = new LinkedList();
        List<Nodo> visitados = new ArrayList();
        
        boolean objetivoExplorado = laberinto.containsKey(queso);
        boolean insertarNoExploradas = true;

        colaNodos.add(nodoPrincipal);
        visitados.add(nodoPrincipal);
        

        while (!colaNodos.isEmpty()) {
            Nodo celdaAux;
            Nodo noExplorado;
            Nodo celda = colaNodos.poll();
            Pair<Integer, Integer> posicionObjetivo;
            

            if (!noExploradas.isEmpty()) {
                insertarNoExploradas = false;
                if (!objetivoExplorado || esInaccesible == true) {
                    break;
                }
            }

            if (celda.getPosicion() == queso) {
                break;
            }

            
            /*Para cada celda en la cola analizamos sus posibles movimientos y almacenamos las opciones en el mapa de nodos explorados*/
            //ABAJO
            if (celda.down) {
                posicionObjetivo = celda.getPosicion();
                posicionObjetivo.posY -= 1;

                if (laberinto.containsKey(posicionObjetivo)) {
                    celdaAux = laberinto.get(posicionObjetivo);

                    if (!visitados.contains(celdaAux)) {
                        visitados.add(celdaAux);
                        colaNodos.add(celdaAux);
                        
                        anteriores.put(celdaAux.getPosicion(), celda);
                    }
                } else {
                    if (insertarNoExploradas) {
                        noExplorado = new Nodo(posicionObjetivo);
                        visitados.add(noExplorado);
                        anteriores.put(noExplorado.getPosicion(), celda);
                        noExploradas.add(noExplorado);
                    }
                }
            }

            //ARRIBA
            if (celda.up) {
                posicionObjetivo = celda.getPosicion();
                posicionObjetivo.posY += 1;

                if (laberinto.containsKey(posicionObjetivo)) {
                    celdaAux = laberinto.get(posicionObjetivo);

                    if (!visitados.contains(celdaAux)) {
                        visitados.add(celdaAux);
                        colaNodos.add(celdaAux);
                        
                        anteriores.put(celdaAux.getPosicion(), celda);
                    }
                } else {
                    if (insertarNoExploradas) {
                        noExplorado = new Nodo(posicionObjetivo);
                        visitados.add(noExplorado);
                        anteriores.put(noExplorado.getPosicion(), celda);
                        noExploradas.add(noExplorado);
                    }
                }
            }
            //IZQUIERDA                  
            if (celda.left) {
                posicionObjetivo = celda.getPosicion();
                posicionObjetivo.posX -= 1;

                if (laberinto.containsKey(posicionObjetivo)) {
                    celdaAux = laberinto.get(posicionObjetivo);

                    if (!visitados.contains(celdaAux)) {
                        visitados.add(celdaAux);
                        colaNodos.add(celdaAux);
                        
                        anteriores.put(celdaAux.getPosicion(), celda);
                    }
                } else {
                    if (insertarNoExploradas) {
                        noExplorado = new Nodo(posicionObjetivo);
                        visitados.add(noExplorado);
                        anteriores.put(noExplorado.getPosicion(), celda);
                        noExploradas.add(noExplorado);
                    }
                }
            }
            //DERECHA                   
            if (celda.right) {
                posicionObjetivo = celda.getPosicion();
                posicionObjetivo.posX += 1;

                if (laberinto.containsKey(posicionObjetivo)) {
                    celdaAux = laberinto.get(posicionObjetivo);

                    if (!visitados.contains(celdaAux)) {
                        visitados.add(celdaAux);
                        colaNodos.add(celdaAux);
                        
                        anteriores.put(celdaAux.getPosicion(), celda);
                    }
                } else {
                    if (insertarNoExploradas) {
                        noExplorado = new Nodo(posicionObjetivo);
                        visitados.add(noExplorado);
                        anteriores.put(noExplorado.getPosicion(), celda);
                        noExploradas.add(noExplorado);
                    }
                }
            }
        }

        return anteriores;
    }

    private int getMenorDistancia(List<Nodo> nodos, Pair<Integer, Integer> objetivo) {
        int posicionMinima = 0;
        double minDist = 0;
        for (int i = 0; i < nodos.size(); i++) {
            if (nodos.get(i).getPosicion() == objetivo) {
                return i;
            }
            double distAct = getDistancia(nodos.get(i).getPosicion(), objetivo);
            if (distAct < minDist) {
                posicionMinima = i;
                minDist = distAct;
            }
        }
        return posicionMinima;
    }

    private double getDistancia(Pair<Integer, Integer> inicial, Pair<Integer, Integer> objetivo) {
        return Math.sqrt(
                (objetivo.posX - inicial.posX) * (objetivo.posX - inicial.posX)
                + (objetivo.posY - inicial.posY) * (objetivo.posY - inicial.posY)
        );
    }

    private int getDireccion(Pair<Integer, Integer> inicial, Pair<Integer, Integer> objetivo) {
        if (objetivo.posY + 1 == inicial.posY) {
            return Mouse.DOWN;
        } else if (objetivo.posY - 1 == inicial.posY) {
            return Mouse.UP;
        } else if (objetivo.posX - 1 == inicial.posX) {
            return Mouse.RIGHT;
        } else {
            return Mouse.LEFT;
        }
    }

    public aeprueba() {
        super("CakeAE");
        camino = new Stack();
        laberinto = new HashMap();

        contadorMovimientos = 0;
        bombasQuedan = 5;
    }

    @Override
    public int move(Grid currentGrid, Cheese cheese) {
        Nodo NodoActual;
        Pair<Integer, Integer> posicionActual = new Pair(currentGrid.getX(), currentGrid.getY());

        /*Comprobamos si la celda en la que esta el ratón ya la tenemos almacenada en nuestro laberinto*/
        if (laberinto.containsKey(posicionActual)) {
            NodoActual = laberinto.get(posicionActual);
        } else {
            NodoActual = new Nodo(posicionActual,currentGrid.canGoUp(),currentGrid.canGoDown(),
                    currentGrid.canGoLeft(),currentGrid.canGoRight());

            laberinto.put(posicionActual, NodoActual);
        }

        /*Si podemos, ponemos bombas cada díez movimientos*/
        if (bombasQuedan > 0) {
            if (contadorMovimientos > 10 ) {
                contadorMovimientos = 0;
                return Mouse.BOMB;
            } else {
                contadorMovimientos++;
            }
        }

        /*Llamamos a la función aeprueba*/
        if (camino.isEmpty()) {
            if (laberinto.isEmpty()) {
                algoritmoAEstrella(NodoActual, posicionActual);
                getDireccion(posicionActual, posicionActual);
            } else {
                algoritmoAEstrella(NodoActual,
                        new Pair(cheese.getX(), cheese.getY()));
            }
        }

        return camino.pop();
    }

    /**
     * Nuevo queso
     */
    @Override
    public void newCheese() {
        laberinto.clear();
        camino.clear();
        
        esInaccesible = false;
    }

    /**
     * Explotamos debido a una bomba
     */
    @Override
    public void respawned() {
        laberinto.clear();
        camino.clear();
        
        esInaccesible = false;
    }

}
