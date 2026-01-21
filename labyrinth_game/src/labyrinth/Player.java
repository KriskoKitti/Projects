/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.awt.Image;

/**
 * A játékos karaktert megvalósító osztály.
 *
 * A Player a Sprite osztályból származik, így rendelkezik
 * pozícióval és megjeleníthető képpel.
 * A játékos mozgása rács alapú, fix méretű mezőkön történik.
 */
public class Player extends Sprite {
    private static final int TILE_SIZE = 40;
    private int indexX;
    private int indexY;

    public Player(int x, int y, int height, int width, Image image, int indexX, int indexY){
        super(x,y,width,height,image);
        this.indexX = indexX;
        this.indexY = indexY;
    }

    /**
     * @return a játékos aktuális rácsbeli X pozíciója
     */
    public int getIndexX(){return indexX;}
    
    /**
     * @return a játékos aktuális rácsbeli Y pozíciója
     */
    public int getIndexY(){return indexY;}

    /**
     * A játékos mozgatása felfelé egy mezővel.
     */
    public void moveUp()    { indexY--; y -= TILE_SIZE; }
    
    /**
     * A játékos mozgatása balra egy mezővel.
     */
    public void moveLeft()  { indexX--; x -= TILE_SIZE; }
    
    /**
     * A játékos mozgatása lefelé egy mezővel.
     */
    public void moveDown()  { indexY++; y += TILE_SIZE; }
    
    /**
     * A játékos mozgatása jobbra egy mezővel.
     */
    public void moveRight() { indexX++; x += TILE_SIZE; }
}
