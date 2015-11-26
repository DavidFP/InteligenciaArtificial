/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouserun.mouse;

import mouserun.game.*;
import java.util.*;

/**
 *
 * @author mery
 */
public class M15C04Practica2a extends Mouse {
private int contadorMovimientos;
    private int bombasQuedan;
    private final HashMap<Integer, Grid> laberinto;   
    private final Stack<Integer> camino;
    
    /**
     * Constructor 
     */
    public M15C04Practica2a() {
		super("MickeyDFS");
		contadorMovimientos = 0;
        bombasQuedan = 5;
        laberinto = new HashMap<>();
        camino = new Stack<>();

    }
    
    /**
     * @param x Posición del ratón en X
     * @param y Posición del ratón en Y
     * @return clave para el mapa hash
     */
    private static int hashCode(int x, int y, int opcion) {
        switch (opcion) {
            case UP:
                return x * 10000 +(y+1);
            case DOWN:
                return x * 10000 +(y-1);
            case LEFT:
                return (x-1) * 10000 +y;
            case RIGHT:
                return (x+1) * 10000 +y;
        }
        return x*10000+ y;
    }

    

    /**
     * Función principal que realiza el la busquedad primero en profundidad
     * @param g Casilla donde el ratón está posicionado.
     * @param c Queso objetivo.
     * @return camino al queso
     */
    private int DFS(Grid g, Cheese c) {
        int i;
        int proxMovimimiento = 0;
        boolean distanciaProxMovimiento;
        
        if (laberinto.get(hashCode(g.getX(), g.getY(),6)) == null) {
            laberinto.put(hashCode(g.getX(), g.getY(),6), g);
        }        
        
        for (i = 1; i < 5; ++i) {
            distanciaProxMovimiento=heurísticaManhattan(g,c,i);  
            
            
            if (posiblesMovimientos(g, i) && laberinto.get(M15C04Practica2a.hashCode(g.getX(),g.getY(), i)) == null && (proxMovimimiento == 0 || distanciaProxMovimiento)) {
                proxMovimimiento = i;
            }
        }

        if (proxMovimimiento!= 0) {
            camino.add(proxMovimimiento);
            return proxMovimimiento;
        }

        /*Obtener del camino el siguiente movimiento*/
        switch (camino.pop()) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return BOMB;

    }

    /**
     * Indica si una casilla puede desplazarse a una dirección concreta.
     *
     * @param g Casilla.
     * @param opcion Desplazamiento.
     * @return True si puede, False en cualquier otro caso.
     */
    private static boolean posiblesMovimientos(Grid g, int opcion) {
        switch (opcion) {
            case UP:
                return g.canGoUp();
            case DOWN:
                return g.canGoDown();
            case LEFT:
                return g.canGoLeft();
            case RIGHT:
                return g.canGoRight();
        }
        return false;
    }

   
    /**
     * Medimos la distancia al queso para ver el movimiento optimo.
     * @param g Casilla en el que esta el ratón.
     * @param opcion movimiento a hacer.
     * @param c Queso.
     */
    private static boolean heurísticaManhattan(Grid g, Cheese c,int opcion) {
        int distancia=0;
        int posActual;
        
        posActual=Math.abs(g.getX() - c.getX()) + Math.abs(g.getY()- c.getY());
        
        switch (opcion) {
            case UP:
                distancia=Math.abs(g.getX() - c.getX()) + Math.abs(g.getY() + 1 - c.getY());
            case DOWN:
                distancia=Math.abs(g.getX() - c.getX()) + Math.abs(g.getY() - 1 - c.getY());
            case LEFT:
                distancia=Math.abs(g.getX()-1 - c.getX()) + Math.abs(g.getY() + 1 - c.getY());
            case RIGHT:
                distancia=Math.abs(g.getX()+1 - c.getX()) + Math.abs(g.getY() + 1 - c.getY());
        }

        return (distancia < posActual);
    }


    
    
    /**
     * Siguiente acción del ratón en el laberinto.
     *
     * @param currentGrid Casilla actual.
     * @param cheese Queso objetivo actual.
     */

    public int move(Grid currentGrid, Cheese cheese) {
        
        
        /* Un queso hizo spawn justo donde ya se encontraba. No hay que mover el ratón. */
        if (currentGrid.getX() == cheese.getX() && currentGrid.getY() == cheese.getY()) {
            return 0;
        }
        
        contadorMovimientos++;
        if(contadorMovimientos>=20 && bombasQuedan>0){
            bombasQuedan--;
            contadorMovimientos=0;
            return BOMB;
        }
        return DFS(currentGrid, cheese);
    }

    /**
     * Nuevo queso
     */
    public void newCheese() {

        laberinto.clear();
        camino.clear();
    }

    /**
     * Explotamos debido a una bomba
     */
    public void respawned() {
        laberinto.clear();
        camino.clear();
    }

}
