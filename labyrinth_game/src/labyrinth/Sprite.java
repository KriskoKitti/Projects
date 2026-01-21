/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.awt.Graphics;
import java.awt.Image;

/**
 * A Sprite osztály egy általános grafikus objektumot reprezentál a játékban.
 * Minden olyan entitás (például Player, Dragon, Block, ShadowBlock) örökölheti ezt az osztályt,
 * amely képernyőn való megjelenítéssel rendelkezik.
 */
public class Sprite {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;
    
    public Sprite(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }
    
    /**
     * Kirajzolja a sprite-ot a képernyőre.
     *
     * @param g a Graphics objektum, amelyre a sprite-ot rajzolni kell
     */
    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }
    
    /**
     * Visszaadja a sprite X koordinátáját.
     *
     * @return a sprite X koordinátája
     */
    public int getX() {
        return x;
    }

    /**
     * Visszaadja a sprite Y koordinátáját.
     *
     * @return a sprite Y koordinátája
     */
    public int getY() {
        return y;
    }

     /**
     * Beállítja a sprite képét.
     *
     * @param image a használni kívánt új Image objektum
     */
    public void setImage(Image image){
        this.image = image;
    }
}
