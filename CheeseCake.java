/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouserun.mouse;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import mouserun.game.Mouse;
import mouserun.game.Grid;
import mouserun.game.Cheese;

/**
 *
 * @author pc
 */
public class CheeseCake extends Mouse {

    private int ultimoMov;
    public int totalMovimientoBomba;

    public CheeseCake() {
        super("Cake");
        ultimoMov = -1;
        totalMovimientoBomba = 0;

    }

    public int move(Grid currentGrid, Cheese cheese) {

        Random random = new Random();
        LinkedList<Integer> posible = new LinkedList<Integer>();
        int movimiento = 0;

        //aqui calculamos los posibles movimientos a partir del movimiento anterior
        if ((currentGrid.canGoUp()) && ultimoMov != Mouse.DOWN) {
            posible.add(Mouse.UP);
        }
        if ((currentGrid.canGoRight()) && ultimoMov != Mouse.LEFT) {
            posible.add(Mouse.RIGHT);
        }
        if ((currentGrid.canGoDown()) && ultimoMov != Mouse.UP) {
            posible.add(Mouse.DOWN);
        }
        if ((currentGrid.canGoLeft()) && ultimoMov != Mouse.RIGHT) {
            posible.add(Mouse.LEFT);
        }

        movimiento = ProximaDireccion(currentGrid, cheese);
        if (posible.contains(movimiento)) {
            ultimoMov = movimiento;

        } else {

            if (posible.size() > 0) {

                ultimoMov = posible.get(random.nextInt(posible.size()));

            } else {
                switch (ultimoMov) {
                    case Mouse.UP:
                        ultimoMov = Mouse.DOWN;
                        break;

                    case Mouse.DOWN:
                        ultimoMov = Mouse.UP;
                        break;

                    case Mouse.LEFT:
                        ultimoMov = Mouse.RIGHT;
                        break;

                    case Mouse.RIGHT:
                        ultimoMov = Mouse.LEFT;
                        break;
                }
            }
        }
        totalMovimientoBomba++;
//Pone bomba cada 20 movimientos.

        if (totalMovimientoBomba == 20) {
            totalMovimientoBomba = 0;
            ultimoMov = Mouse.BOMB;
        }
        System.out.println("Queso X" + cheese.getX() + " Y:" + cheese.getY() + "");
        System.out.println("Raton X" + currentGrid.getX() + "Y: " + currentGrid.getY() + "");
        //Pone bomba si está cerca del queso
//        if((Math.abs( cheese.getX() - currentGrid.getX()) < 3)&& (Math.abs(cheese.getY()-currentGrid.getY())<3)){
//           ultimoMov = Mouse.BOMB;
//        }
//        if(getDistance(cheese.getX(), cheese.getY(), currentGrid.getX(), currentGrid.getY()) <2){
//            totalMovimientoBomba = 0;
//            ultimoMov = Mouse.BOMB;
//        }
        return ultimoMov;

    }

    public void newCheese() {

    }

    public void respawned() {
    }

    //aqui calculamos la próxima dirección a partir de la distancia al queso
    private int ProximaDireccion(Grid grid, Cheese cheese) {
        int direccion = 0;
        double distancia;
        Vector<Double> distancias = new Vector<>();

        //arriba
        if (grid.canGoUp()) {
            distancia = getDistance(grid.getX(), grid.getY() + 1, cheese.getX(), cheese.getY());
            distancias.add(distancia);
        } else {
            distancias.add(10000.0);
        }
        //abajo
        if (grid.canGoDown()) {
            distancia = getDistance(grid.getX(), grid.getY() - 1, cheese.getX(), cheese.getY());
            distancias.add(distancia);
        } else {
            distancias.add(10000.0);
        }
        //derecha
        if (grid.canGoRight()) {
            distancia = getDistance(grid.getX() + 1, grid.getY(), cheese.getX(), cheese.getY());
            distancias.add(distancia);
        } else {
            distancias.add(10000.0);
        }
        //izquierda
        if (grid.canGoLeft()) {
            distancia = getDistance(grid.getX() - 1, grid.getY(), cheese.getX(), cheese.getY());
            distancias.add(distancia);
        } else {
            distancias.add(10000.0);
        }

        switch (MejorOpcion(distancias)) {
            case 0:
                direccion = Mouse.UP;
                break;
            case 1:
                direccion = Mouse.RIGHT;
                break;
            case 2:
                direccion = Mouse.DOWN;
                break;
            case 3:
                direccion = Mouse.LEFT;
                break;
        }
        return direccion;
    }

    //en este método se calcula la distancia euclida al queso
    private double getDistance(int x1, int y1, int x2, int y2) {
        //Distancia Euclidea
        return Math.sqrt(Math.pow((double) (x2 - x1), 2) + Math.pow((double) (y2 - y1), 2));
    }

    private int MejorOpcion(Vector<Double> distancia) {

        int eleccion = 0;
        int i;
        double m = 10000000;

        for (i = 0; i < 4; i++) {
            if (m > distancia.get(i)) {
                m = distancia.get(i);
                eleccion = i;
            }
        }
        return eleccion;
    }

}
