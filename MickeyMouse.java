package mouserun.mouse;

import java.util.ArrayList;
import java.util.Random;
import mouserun.game.Mouse;
import mouserun.game.Grid;
import mouserun.game.Cheese;
import static java.lang.Math.abs; //Libreria valor absoluto de java

public class MickeyMouse extends Mouse {

    public int totalMovimientoBomba = 0;
    private Grid lastGrid;

    public MickeyMouse() {
        super("Mickey");

    }

    public int move(Grid currentGrid, Cheese cheese) {
        Random random = new Random(); //Creamos una nueva variable de tipo aleatorio
        ArrayList<Integer> movimientosPosibles = new ArrayList<Integer>(); //Creamos vector de los posibles movimientos
        ArrayList<Integer> movimientoAuxiliar = new ArrayList<Integer>(); //Creo una lista auxiliar donde almacenare solo los movimientos que me permitan acercarme al queso

        //Añadimos movimientos posibles, en el orden; subir, bajar, izquierda,derecha
        if (currentGrid.canGoUp()) {
            movimientosPosibles.add(Mouse.UP);
        }
        if (currentGrid.canGoDown()) {
            movimientosPosibles.add(Mouse.DOWN);
        }
        if (currentGrid.canGoLeft()) {
            movimientosPosibles.add(Mouse.LEFT);
        }
        if (currentGrid.canGoRight()) {
            movimientosPosibles.add(Mouse.RIGHT);
        }

        //Podemos colocar bomba, da igual la circunstancia
        movimientosPosibles.add(Mouse.BOMB);

        //Este es el caso en el que solo está el movimiento bomba,la colocas
        if (movimientosPosibles.size() == 1) {
            lastGrid = currentGrid; //A la variable última celda le asignamos la actual
            return movimientosPosibles.get(0);
        } else {
            if (movimientosPosibles.size() == 2) {
                lastGrid = currentGrid;
                return movimientosPosibles.get(0); //Devuelve get 0 porque la bomba siempre se coloca en última posición por lo que el único movimiento esta en esta posición
            } else {

                //¿Como meter en esta lista los movimientos que me permitan acercarme al queso?
                for (int i = 0; i < movimientosPosibles.size() - 1; i++) { //En el bucle nunca entramos en la posicion de la bomba
                    int variable = movimientosPosibles.get(i); //Leo el dato correspondiente a la posicion en la que estamos
                    switch (variable) {
                        case Mouse.UP:
                            int a = currentGrid.getY() + 1;
                            int b = currentGrid.getY();
                            if (abs(a - cheese.getY()) < abs(b - cheese.getY())) {
                                movimientoAuxiliar.add(Mouse.UP);
                            }
                            break;

                        case Mouse.DOWN:
                            int c = currentGrid.getY() - 1;
                            int d = currentGrid.getY();
                            if (abs(c - cheese.getY()) < abs(d - cheese.getY())) {
                                movimientoAuxiliar.add(Mouse.DOWN);
                            }
                            break;

                        case Mouse.LEFT:   //Si se cumple la condicion de acercamiento, lo metemos en vector auxiliar
                            int e = currentGrid.getX() - 1;
                            int f = currentGrid.getX();
                            if (abs(e - cheese.getX()) < abs(f - cheese.getX())) {
                                movimientoAuxiliar.add(Mouse.LEFT);
                            }
                            break;

                        case Mouse.RIGHT:
                            int g = currentGrid.getX() + 1;
                            int h = currentGrid.getX();
                            if (abs(g - cheese.getX()) < abs(h - cheese.getX())) {
                                movimientoAuxiliar.add(Mouse.RIGHT);
                            }
                            break;
                    }
                }//Aqui deberiamos tener almacenados en la lista auxiliar los movimientos que nos permiten un acercamiento a queso
            }

                   //Posible error. que esta funcion no haga lo que yo creo.. es decir, eliminar aquel movimiento del que venimos anteriormente
            //Ahora hay que eliminar del vector auxiliar los movimientos de los cuales venimos anteriormente
            if (!testGrid(Mouse.UP, currentGrid)) {
                movimientoAuxiliar.remove((Integer) Mouse.UP); //Si el movimiento consiste en volver a la casilla anterior, lo elimina
            }
            if (!testGrid(Mouse.DOWN, currentGrid)) {
                movimientoAuxiliar.remove((Integer) Mouse.DOWN);
            }
            if (!testGrid(Mouse.LEFT, currentGrid)) {
                movimientoAuxiliar.remove((Integer) Mouse.LEFT);
            }
            if (!testGrid(Mouse.RIGHT, currentGrid)) {
                movimientoAuxiliar.remove((Integer) Mouse.RIGHT);
            }

            //Ahora en la lista auxiliar solo debe haber aquellos movimientos disponibles que no vienen de la casilla anterior
            if (movimientoAuxiliar.size() == 0) { //No hay ningun movimiento que me permita acercarme al queso

                if (movimientosPosibles.size() == 2) { //si solo hay un movimiento y la bomba hago el movimiento aunque sea volver hacia atras
                    lastGrid = currentGrid;
                            ////////////////////////////////////
                    //Pone bomba cada 20 movimientos.
                    totalMovimientoBomba++;
                    if (totalMovimientoBomba == 20) {
                        totalMovimientoBomba = 0;
                        return Mouse.BOMB;
                    }
                            ////////////////////////////////////

                    return movimientosPosibles.get(0);
                } else { //El unico fallo del programa esta akkiii. si no hay ningun movimiento que me permita acercarme hago aleatorio entre todos los posibles
                    //Aqui eliminar los movimientos que vengan de la anterior posicion del vector posibles moves
                    if (!testGrid(Mouse.UP, currentGrid)) {
                        movimientosPosibles.remove((Integer) Mouse.UP); //Si el movimiento consiste en volver a la casilla anterior, lo elimina
                    }
                    if (!testGrid(Mouse.DOWN, currentGrid)) {
                        movimientosPosibles.remove((Integer) Mouse.DOWN);
                    }
                    if (!testGrid(Mouse.LEFT, currentGrid)) {
                        movimientosPosibles.remove((Integer) Mouse.LEFT);
                    }
                    if (!testGrid(Mouse.RIGHT, currentGrid)) {
                        movimientosPosibles.remove((Integer) Mouse.RIGHT);
                    }
                    lastGrid = currentGrid; //A la casilla anterior le asigno la actual y aleatoriamente hago uno de los posibles
                    ////////////////////////////////////
                    //Pone bomba cada 20 movimientos.
                    totalMovimientoBomba++;
                    if (totalMovimientoBomba == 20) {
                        totalMovimientoBomba = 0;
                        return Mouse.BOMB;
                    }
                            ////////////////////////////////////

                    return movimientosPosibles.get(random.nextInt(movimientosPosibles.size() - 1));
                }
            }

            if (movimientoAuxiliar.size() == 1) {
                lastGrid = currentGrid;
                                                    ////////////////////////////////////
                //Pone bomba cada 20 movimientos.
                totalMovimientoBomba++;
                if (totalMovimientoBomba == 20) {
                    totalMovimientoBomba = 0;
                    return Mouse.BOMB;
                }
                            ////////////////////////////////////

                return movimientoAuxiliar.get(0);
            }

            if (movimientoAuxiliar.size() > 1) {
                lastGrid = currentGrid;
                                                    ////////////////////////////////////
                //Pone bomba cada 20 movimientos.
                totalMovimientoBomba++;
                if (totalMovimientoBomba == 20) {
                    totalMovimientoBomba = 0;
                    return Mouse.BOMB;
                }
                            ////////////////////////////////////

                return movimientoAuxiliar.get(random.nextInt(movimientoAuxiliar.size()));
            }

        }

        return 0;
    }

    public void newCheese() {

    }

    public void respawned() {

    }

           //Devuelve V si la casilla anterior es vacia(acabamos de empezar el juego
    //Si vuelve a la casilla anterior devuelve falso
    private boolean testGrid(int direction, Grid currentGrid) {
        if (lastGrid == null) {
            return true;
        }

        int x = currentGrid.getX();
        int y = currentGrid.getY();

        switch (direction) {
            case Mouse.UP:
                y += 1;
                break;

            case Mouse.DOWN:
                y -= 1;
                break;

            case Mouse.LEFT:
                x -= 1;
                break;

            case Mouse.RIGHT:
                x += 1;
                break;
        }

        return !(lastGrid.getX() == x && lastGrid.getY() == y);

    }

}
