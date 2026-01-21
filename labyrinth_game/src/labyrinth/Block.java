/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.awt.Image;

/**
 * A Block osztály egyetlen mezőt (cellát) reprezentál a labirintusban.
 * 
 * Egy blokk:
 * - ismeri a rácsbeli pozícióját (indexX, indexY),
 * - tudja, mely irányokba van átjárás (west, north, east, south),
 * - képes eldönteni, hogy célmező (finish)-e,
 * - grafikus megjelenítéssel rendelkezik (Sprite ősosztály).
 */
public class Block extends Sprite{
    private int type;
    private int indexX;
    private int indexY;
    private boolean west;
    private boolean north;
    private boolean east;
    private boolean south;
    private boolean finish;
    
    public Block(int type, int IndexX, int IndexY, int x, int y, int width, int height, Image image){
        super(x,y,width,height,image);
        this.indexX = IndexX;
        this.indexY = IndexY;
        this.type = type;
        this.west = false;
        this.north = false;
        this.east = false;
        this.south = false;
        setupWalls();
    }
    
    /** @return a blokk típusa */
    public int getType(){return this.type;}
    
    /** @return van-e átjárás nyugatra */
    public boolean getWest(){return this.west;}
    
    /** @return van-e átjárás északra */
    public boolean getNorth(){return this.north;}
    
    /** @return van-e átjárás keletre */
    public boolean getEast(){return this.east;}
    
    /** @return van-e átjárás délre */
    public boolean getSouth(){return this.south;}
    
    /** @return igaz, ha a blokk célmező */
    public boolean getFinish(){return this.finish;}
    
    /**
     * Eldönti, hogy a blokk célmező-e.
     * 
     * A blokk akkor számít célmezőnek, ha:
     * - a pálya szélén van,
     * - és van nyitott kijárata a pálya széle felé.
     *
     * A bal alsó sarok mindig tiltott célmező.
     *
     * @param maxX a pálya szélessége
     * @param maxY a pálya magassága
     */
    public void checkFinish(int maxX, int maxY){
        finish =
        (indexY == 0 && north) ||
        (indexX == 0 && west) ||
        (indexY == maxY - 1 && south) ||
        (indexX == maxX - 1 && east);

        if (indexX == 0 && indexY == maxY - 1) {
            finish = false;
        }
    }
    
    /** Nyugati irány megnyitása */
    public void goWest(){
        this.west = true;
    }

    /** Északi irány megnyitása */
    public void goNorth(){
        this.north = true;
    }

    /** Keleti irány megnyitása */
    public void goEast(){
        this.east = true;
    }

    /** Déli irány megnyitása */
    public void goSouth(){
        this.south = true;
    }
    
    /**
     * A blokk típusának megfelelően beállítja,
     * hogy mely irányokba van átjárás.
     */
    private void setupWalls(){
        switch (type) {
            case 1 -> goWest();
            case 2 -> goNorth();
            case 3 -> goEast();
            case 4 -> goSouth();
            case 5 -> { goNorth(); goWest(); }
            case 6 -> { goNorth(); goEast(); }
            case 7 -> { goEast(); goSouth(); }
            case 8 -> { goSouth(); goWest(); }
            case 9 -> { goEast(); goWest(); }
            case 10 -> { goNorth(); goSouth(); }
            case 11 -> { goNorth();  goEast(); goSouth(); }
            case 12 -> { goEast(); goSouth(); goWest(); }
            case 13 -> { goNorth(); goSouth(); goWest(); }
            case 14 -> { goNorth(); goEast(); goWest(); }
            case 15 -> { goNorth(); goEast(); goSouth(); goWest(); }
            default -> {}
        }
    }

     /**
     * Szöveges reprezentáció debugoláshoz vagy konzolos megjelenítéshez.
     *
     * @return a blokk típust jelölő karakter
     */
    @Override
    public String toString(){
        return switch (type) {
            case 1 -> "]";
            case 2 -> "U";
            case 3 -> "[";
            case 4 -> "∩";
            case 5 -> "˩";
            case 6 -> "L";
            case 7 -> "ſ";
            case 8 -> "⅂";
            case 9 -> "=";
            case 10 -> "∥";
            case 11 -> "⎸";
            case 12 -> "‾";
            case 13 -> "⎹";
            case 14 -> "_";
            case 15 -> " ";
            default -> "ERROR";
        };
    }
    
}
