/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.awt.Image;
import java.util.Random;
import java.util.ArrayList;

/**
 * A Dragon osztály a játékban szereplő ellenséget (sárkányt) reprezentálja.
 * 
 * A sárkány rácsalapú pályán mozog, ahol az indexX és indexY mezők
 * a pályán elfoglalt pozícióját, míg az x és y koordináták
 * a képernyőn elfoglalt pixelpozícióját jelölik.
 * 
 * A mozgás iránya véletlenszerűen kerül kiválasztásra az adott mező
 * által engedélyezett irányok közül.
 */
public class Dragon extends Sprite {
    private double velX;
    private double velY;
    private int indexX;
    private int indexY;
    private int direction;

    public Dragon(int x, int y, int width, int height, Image image, int indexX, int indexY){
        super(x,y,width,height,image);
        this.velX = 0;
        this.velY = 0;
        this.indexX = indexX;
        this.indexY = indexY;
        this.direction = 0;
    }

    /**
     * @return a sárkány aktuális X rácskoordinátája
     */
    public int getIndexX(){return indexX;}
    
    /**
     * @return a sárkány aktuális Y rácskoordinátája
     */
    public int getIndexY(){return indexY;}
    
    /**
     * @return a sárkány aktuális mozgásiránya
     */
    public int getDirection(){return direction;}


     /**
     * Elmozdítja a sárkányt vízszintesen a sebessége alapján.
     */
    public void moveX() {
        x += velX;
    }

    /**
     * Elmozdítja a sárkányt függőlegesen a sebessége alapján.
     */
    public void moveY() {
        y += velY;
    }
    
    /**
     * Véletlenszerű irányt választ a megadott lehetőségek közül,
     * figyelembe véve a pálya határait.
     *
     * Az irányválasztás után frissíti a sárkány sebességét,
     * rácskoordinátáját és aktuális irányát.
     *
     * @param directions az engedélyezett irányok listája
     * @param maxX a pálya szélessége rácsban
     * @param maxY a pálya magassága rácsban
     */
    public void goDirection(ArrayList<Integer> directions, int maxX, int maxY){
        Random rand = new Random();
        int direction = directions.get(rand.nextInt(directions.size()));
        while ((this.indexX == 0 && direction == 1) || (this.indexX == maxX-1 && direction == 3) || (this.indexY == 0 && direction == 2) || (this.indexY == maxY-1 && direction == 4) ){
            direction = directions.get(rand.nextInt(directions.size()));
        }
        switch (direction){
            case 1:
                this.indexX--;
                this.velY = 0;
                this.velX = -40;
                this.direction = 1;
                break;
            case 2:
                this.indexY--;
                this.velX = 0;
                this.velY = -40;
                this.direction = 2;
                break;
            case 3:
                this.indexX++;
                this.velY = 0;
                this.velX = 40;
                this.direction = 3;
                break;
            case 4:
                this.indexY++;
                this.velX = 0;
                this.velY = 40;
                this.direction = 4;
                break;

        }
    }
    
    /**
     * A sárkány rácskoordinátáját frissíti
     * anélkül, hogy új irányt választana.
     * 
     * Akkor használatos, ha nincs akadály a jelenlegi irányban.
     */
    public void moveNoChange(){
        switch (this.direction){
            case 1:
                this.indexX--;
                break;
            case 2:
                this.indexY--;
                break;
            case 3:
                this.indexX++;
                break;
            case 4:
                this.indexY++;
                break;

        }
    }
}

