/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * A ShadowBlock osztály egy rejtett/látható blokkot reprezentál a labirintusban.
 * Ezek a blokkok takarják a pálya egyes részeit, és a játékos
 * közelsége szerint válnak láthatóvá vagy rejtetté.
 */
public class ShadowBlock extends Sprite {
    private boolean visible;
    private int indexX;
    private int indexY;

    public ShadowBlock(int x, int y, int width, int height, Image image, int indexX, int indexY){
        super(x,y,width,height,image);
        this.visible = true;
        this.indexX = indexX;
        this.indexY = indexY;
    }

    /**
     * Visszaadja, hogy a blokk jelenleg látható-e.
     *
     * @return true, ha a blokk látható, false ha rejtett
     */
    public boolean isVisible(){return this.visible;}

    /**
     * Ellenőrzi a játékos távolságát a blokkhoz, és ennek megfelelően
     * láthatóvá vagy rejtetté teszi a blokkot.
     *
     * @param playerX a játékos X indexe
     * @param playerY a játékos Y indexe
     */
    public void checkPlayerDist(int playerX, int playerY){
        if (Math.abs(indexX-playerX) < 3 && Math.abs(indexY-playerY) < 3){
            this.reveal();
        }else{
            this.hide();
        }
        if (Math.abs(indexX-playerX) == 2 && Math.abs(indexY-playerY) == 2){
            this.hide();
        }
    }

    /**
     * Rejtetté teszi a blokkot, és sötét képet állít be rá.
     */
    public void hide(){ 
        this.visible = true; 
        this.setImage(new ImageIcon("pics/dark.png").getImage()); 
    }
    
    /**
     * Láthatóvá teszi a blokkot, és átlátszó (invis) képet állít be rá.
     */
    public void reveal(){ 
        this.visible = false; 
        this.setImage(new ImageIcon("pics/invis.png").getImage()); 
    }
}

