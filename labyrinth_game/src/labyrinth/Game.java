/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.Random;

/**
 * A Game osztály a játék fő vezérlőlogikáját valósítja meg.
 * 
 * Felelős:
 * - a pálya (Labyrinth) kezeléséért
 * - a játékos (Player) és az ellenség (Dragon) irányításáért
 * - a billentyűzetes vezérlésért
 * - a játékmenet ciklusának futtatásáért
 * - a pontszám és szintek kezeléséért
 * 
 * A Game osztály egy JPanel leszármazottja, amely kirajzolja
 * a játék elemeit, és egy Swing Timer segítségével frissíti
 * a játék állapotát.
 */
public class Game extends JPanel {
    private LabyrinthGUI gui;
    private final int FPS = 60;
    private final int DRAGON_WAIT = 30;
    private static final int TILE_SIZE = 40;

    private Labyrinth map;
    private Dragon dragon;
    private Player player;

    private int waited = 0;
    private Timer newFrameTimer;
    private int mapCount = 1;
    private int score;
    private int sizeX;
    private int sizeY;
    protected boolean submitted = false;

    private JLabel mapLabel;

    private String playerName;

    /**
     * Létrehozza a Game objektumot, beállítja a billentyűvezérlést,
     * bekéri a játékos nevét és elindítja a játékmenetet.
     *
     * @param gui a játék grafikus felülete
     */
    public Game(LabyrinthGUI gui) {
        super();
        this.gui = gui;
        mapLabel = new JLabel(" ");
        playerName = JOptionPane.showInputDialog(gui.frame, "Enter your name:");
        score = 0;

        this.getInputMap().put(KeyStroke.getKeyStroke("W"), "pressed W");
        this.getActionMap().put("pressed W", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!isBlocked(0, -1) && !isOver()){
                    player.moveUp();
                }
            }
        });

        this.getInputMap().put(KeyStroke.getKeyStroke("A"), "pressed A");
        this.getActionMap().put("pressed A", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!isBlocked(-1, 0) && !isOver()){
                    player.moveLeft();
                }
            }
        });

        this.getInputMap().put(KeyStroke.getKeyStroke("S"), "pressed S");
        this.getActionMap().put("pressed S", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!isBlocked(0, 1) && !isOver()){
                    player.moveDown();
                }
            }
        });

        this.getInputMap().put(KeyStroke.getKeyStroke("D"), "pressed D");
        this.getActionMap().put("pressed D", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!isBlocked(1, 0) && !isOver()){
                    player.moveRight();
                }
            }
        });
        restart();

        newFrameTimer = new Timer(1000 / FPS, new NewFrameListener());
        newFrameTimer.start();
    }

    /**
     * Újraindítja az aktuális pályát.
     * Betölti a térképet, létrehozza a játékost és a sárkányt.
     */
    public void restart() {
        Random rand = new Random();
        //cleanup();
        waited = 0;
        try {
            map = new Labyrinth("pics/maps/map" + mapCount + ".txt");
        }catch(FileNotFoundException ex){
            try {
                System.out.println("Level not found, loading level 1.");
                map = new Labyrinth("pics/maps/map1.txt");
            }catch(FileNotFoundException e){
                System.out.println("Level 1 not found either. Terminating.");
                System.exit(0);
            }
        }
        sizeX = map.getSizeX();
        sizeY = map.getSizeY();
        Image playerImage = new ImageIcon("pics/Player.png").getImage();
        player = new Player(0, (sizeY - 1) * TILE_SIZE, TILE_SIZE, TILE_SIZE, playerImage, 0, sizeY - 1);
        generateDragon(rand.nextInt(sizeX),rand.nextInt(sizeY));
        dragon.goDirection(whichDirections(dragon.getIndexX(),dragon.getIndexY()),sizeX,sizeY);
        dragon.moveX();
        dragon.moveY();
    }

    /**
     * Kirajzolja a játék elemeit.
     *
     * @param grphcs a Graphics objektum
     */
    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        map.draw(grphcs);
        dragon.draw(grphcs);
        map.drawShadows(grphcs);
        player.draw(grphcs);
    }

    /**
     * A játék fő ciklusát vezérlő belső osztály.
     * Minden képkockában frissíti a játék állapotát.
     */
    class NewFrameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            regularCycle();
            if (isOver()) {
                if (isPlayerOnFinish()) {
                    score++;
                    waited = 0;
                    mapCount++;
                    restart();
                    newFrameTimer.restart();
                } else if (isPlayerNextToDragon()) {
                    savePlayerData();
                    JOptionPane.showMessageDialog(gui.frame, "You Lost!");
                    resetMenu();
                }
                if (score >= 10) {
                    savePlayerData();
                    JOptionPane.showMessageDialog(gui.frame, "You Won!");
                    resetMenu();
                }
            }
            mapLabel.setText("Level: " + mapCount + " Score: " + score);
        }
    }

    /**
     * Elmenti az aktuális játékos adatait.
     */
    private void savePlayerData() {
        try {
            PlayerData currentPlayerData = new PlayerData(playerName, score);
            DatabaseHandler.savePlayerData(currentPlayerData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A játék egy frissítési ciklusát hajtja végre.
     * Kezeli a sárkány mozgását, árnyékfrissítést és újrarajzolást.
     */
    private void regularCycle(){
            if (waited == DRAGON_WAIT){
                waited = 0;
                if (checkIfWallAhead()){
                    dragon.goDirection(whichDirections(dragon.getIndexX(),dragon.getIndexY()),sizeX,sizeY);
                }else{
                    dragon.moveNoChange();
                }
                dragon.moveX();
                dragon.moveY();
            }
            waited++;

        map.updateShadows(player);
        repaint();
    }
    
    /**
    * Létrehozza a sárkányt a megadott rácskoordinátákon.
    * A metódus gondoskodik arról is, hogy a sárkány
    * ne a játékos kezdőpozíciójához túl közel jelenjen meg.
    *
    * @param x a sárkány kezdeti X koordinátája rács szerint
    * @param y a sárkány kezdeti Y koordinátája rács szerint
    */
    private void generateDragon(int x, int y){
        Image dragonImage = new ImageIcon("pics/Dragon.png").getImage();
        dragon = new Dragon(x*TILE_SIZE,y*TILE_SIZE,TILE_SIZE,TILE_SIZE,dragonImage,x,y);
        dragonNotNearStart();
    }
    
    /**
    * Biztosítja, hogy a sárkány ne a játékos közvetlen közelében
    * helyezkedjen el a játék indításakor.
    * A sárkány pozíciója addig újragenerálódik,
    * amíg legalább 3 mező távolságra nem kerül a játékostól.
    */
    private void dragonNotNearStart(){
        Random rand = new Random();
        int tempX;
        int tempY;
        while (Math.abs(dragon.getIndexX()- player.getIndexX()) <= 3 && Math.abs(dragon.getIndexY()- player.getIndexY()) <= 3){
            tempX = rand.nextInt(map.getSizeX());
            tempY = rand.nextInt(map.getSizeY());
            Image dragonImage = new ImageIcon("pics/Dragon.png").getImage();
            dragon = new Dragon(tempX*TILE_SIZE,tempY*TILE_SIZE,TILE_SIZE,TILE_SIZE,dragonImage,tempX,tempY);
        }
    }
    
    /**
    * Meghatározza, hogy egy adott mezőről milyen irányokba
    * lehet továbbhaladni a pálya falai alapján.
    *
    * @param x a vizsgált mező X koordinátája
    * @param y a vizsgált mező Y koordinátája
    * @return az elérhető irányok listája
    *         (1 = nyugat, 2 = észak, 3 = kelet, 4 = dél)
    */
    private ArrayList<Integer> whichDirections(int x, int y){
        ArrayList<Integer> directions = new ArrayList<>();
        if (this.map.getBlock(x,y).getWest()){
            directions.add(1);
        }
        if (this.map.getBlock(x,y).getNorth()){
            directions.add(2);
        }
        if (this.map.getBlock(x,y).getEast()){
            directions.add(3);
        }
        if (this.map.getBlock(x,y).getSouth()){
            directions.add(4);
        }
        return directions;
    }
    
    /**
    * Ellenőrzi, hogy a sárkány aktuális mozgásiránya előtt
    * található-e fal vagy pályahatár.
    *
    * @return true, ha a sárkány nem tud továbbhaladni az adott irányba,
    *         false, ha az út szabad
    */
    private boolean checkIfWallAhead(){
        switch (dragon.getDirection()){
            case 1:
                if (dragon.getIndexX() == 0){
                    return true;
                }
                return !this.map.getBlock(dragon.getIndexX(),dragon.getIndexY()).getWest();
            case 2:
                if (dragon.getIndexY() == 0){
                    return true;
                }
                return !this.map.getBlock(dragon.getIndexX(),dragon.getIndexY()).getNorth();
            case 3:
                if (dragon.getIndexX() == sizeX-1){
                    return true;
                }
                return !this.map.getBlock(dragon.getIndexX(),dragon.getIndexY()).getEast();
            case 4:
                if (dragon.getIndexY() == sizeY-1){
                    return true;
                }
                return !this.map.getBlock(dragon.getIndexX(),dragon.getIndexY()).getSouth();
            default:
                return false;
        }
    }
    
    /**
    * Megvizsgálja, hogy a játékos el tud-e mozdulni
    * a megadott irányba fal vagy pályahatár nélkül.
    *
    * @param dx az elmozdulás X irányban (-1 balra, 1 jobbra)
    * @param dy az elmozdulás Y irányban (-1 felfelé, 1 lefelé)
    * @return true, ha a mozgás tiltott,
    *         false, ha a mozgás engedélyezett
    */
    private boolean isBlocked(int dx, int dy) {
        int x = player.getIndexX();
        int y = player.getIndexY();

        if (x + dx < 0 || x + dx >= sizeX || y + dy < 0 || y + dy >= sizeY) {
            return true;
        }

        Block b = map.getBlock(x, y);
        if (dx == -1) return !b.getWest();
        if (dx == 1)  return !b.getEast();
        if (dy == -1) return !b.getNorth();
        return !b.getSouth();
    }
   
    /**
     * @return igaz, ha a játékos célmezőn áll
     */
    private boolean isPlayerOnFinish(){
        return map.getBlock(player.getIndexX(), player.getIndexY()).getFinish();
    }

    /**
     * @return igaz, ha a játékos a sárkány mellett áll
     */
    public boolean isPlayerNextToDragon(){
        if (player.getIndexX()-1 == dragon.getIndexX() && player.getIndexY() == dragon.getIndexY() && map.getBlock(player.getIndexX(), player.getIndexY()).getWest()){
            return true;
        }else if (player.getIndexX() == dragon.getIndexX() && player.getIndexY()-1 == dragon.getIndexY() && map.getBlock(player.getIndexX(), player.getIndexY()).getNorth()){
            return true;
        }else if (player.getIndexX()+1 == dragon.getIndexX() && player.getIndexY() == dragon.getIndexY() && map.getBlock(player.getIndexX(), player.getIndexY()).getEast()){
            return true;
        }else if (player.getIndexX() == dragon.getIndexX() && player.getIndexY()+1 == dragon.getIndexY() && map.getBlock(player.getIndexX(), player.getIndexY()).getSouth()){
            return true;
        }else {
            return false;
        }
    }

    /**
     * @return igaz, ha a játék véget ért
     */
    private boolean isOver(){
        return isPlayerOnFinish() || isPlayerNextToDragon();
    }

    /**
     * @return a szint- és pontszám kijelzésére szolgáló JLabel
     */
    public JLabel getMapLabel(){
        return this.mapLabel;
    }

    /**
     * Visszaállítja a játékot az alapállapotba,
     * új név bekérésével és pontszám nullázásával.
     */
    public void resetMenu(){
        playerName = JOptionPane.showInputDialog(gui.frame, "Enter your name:");
        mapCount = 1;
        waited = 0;
        score = 0;
        submitted = false;
        repaint();
        gui.game.restart();
        gui.startTime = System.currentTimeMillis();
        newFrameTimer.restart();
    }
}
