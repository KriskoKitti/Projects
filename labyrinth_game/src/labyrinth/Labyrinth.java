/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.ImageIcon;
import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * A Labyrinth osztály a játék pályáját reprezentálja.
 * 
 * Feladata:
 * - a pálya betöltése fájlból,
 * - a blokkok és árnyékmezők (ShadowBlock) kezelése,
 * - a pálya kirajzolása,
 * - a célmezők (finish) beállítása.
 */
public class Labyrinth {
    private Block[][] blocks;
    private ShadowBlock[][] shadows;
    private int sizeX;
    private int sizeY;

    /**
 * Betölti a labirintus pályát egy fájlból.
 * A fájl első két száma a pálya szélessége és magassága,
 * majd ezt követik a blokkok típusai.
 *
 * @param fileName a pályafájlt tartalmazó fájl neve
 * @throws FileNotFoundException ha a fájl nem található
 */
    public Labyrinth(String fileName) throws FileNotFoundException{
        Scanner sc = new Scanner(new BufferedReader(new FileReader(fileName)));
        this.sizeX = sc.nextInt();
        this.sizeY = sc.nextInt();
        this.blocks = new Block[this.sizeX][this.sizeY];
        this.shadows = new ShadowBlock[this.sizeX][this.sizeY];
        for (int i = 0; i < this.sizeY; i++){
            for (int j = 0; j < this.sizeX; j++){
                int num = sc.nextInt();
                Image blockImage = new ImageIcon("pics/" + num + ".jpg").getImage();
                this.blocks[i][j] = new Block(num,j,i,j*40,i*40,40,40,blockImage);
                Image shadowImage = new ImageIcon("pics/dark.png").getImage();
                this.shadows[i][j] = new ShadowBlock(j*40,i*40,40,40,shadowImage,j,i);
            }
        }
        setupFinishes();
    }

    /**
    * Visszaadja a pálya szélességét mezőkben.
    *
    * @return a pálya szélessége
    */
    public int getSizeX(){ return this.sizeX; };
    
    /**
    * Visszaadja a pálya magasságát mezőkben.
    *
    * @return a pálya magassága
    */
    public int getSizeY(){ return this.sizeY; };

    /**
     * Visszaadja a pálya egy adott mezőjén található blokkot.
     *
     * @param x a blokk X koordinátája
     * @param y a blokk Y koordinátája
     * @return az adott pozíción lévő Block objektum
     */
    public Block getBlock(int x, int y){ return this.blocks[y][x]; }

    /**
    * Kirajzolja a labirintus összes blokkját a megadott
    * grafikus felületre.
    *
    * @param g a rajzoláshoz használt Graphics objektum
    */
    public void draw(Graphics g) {
        for (int i = 0; i < this.sizeY; i++){
            for (int j = 0; j < this.sizeX; j++){
                this.blocks[i][j].draw(g);
            }
        }
    }

    /**
    * Kirajzolja az árnyékmezőket (ShadowBlock),
    * de csak azokat, amelyek jelenleg láthatóak.
    *
    * @param g a rajzoláshoz használt Graphics objektum
    */
    public void drawShadows(Graphics g){
        for (int i = 0; i < this.sizeY; i++){
            for (int j = 0; j < this.sizeX; j++){
                if (this.shadows[i][j].isVisible()){
                    this.shadows[i][j].draw(g);
                }
            }
        }
    }

    /**
    * Frissíti az árnyékmezők állapotát a játékos aktuális
    * pozíciója alapján.
    *
    * @param player a játékos objektum
    */
    public void updateShadows(Player player){
        for (int i = 0; i < this.sizeY; i++){
            for (int j = 0; j < this.sizeX; j++){
                this.shadows[i][j].checkPlayerDist(player.getIndexX(), player.getIndexY());
            }
        }
    }

    /**
    * Beállítja a célmezőket (finish) a pálya szélén lévő
    * blokkoknál a falak elhelyezkedése alapján.
    */
    private void setupFinishes(){
        for (int j = 0; j < this.sizeY; j++){
            for (int i = 0; i < this.sizeX; i++){
                this.blocks[j][i].checkFinish(this.sizeX,this.sizeY);
            }
        }
    }

}

