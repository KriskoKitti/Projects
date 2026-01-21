/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.security.Timestamp;

/**
 * Egy játékos adatainak tárolására szolgáló osztály.
 * 
 * A PlayerData tartalmazza a játékos nevét és elért pontszámát.
 * Ezeket az adatokat a játékban a ranglista kezeléséhez használjuk.
 */
public class PlayerData {
    private String name;
    private int score;

    public PlayerData( String name, int score) {
        this.name = name;
        this.score = score;

    }

    /**
     * Visszaadja a játékos nevét.
     *
     * @return a játékos neve
     */
    public String getName() {
        return name;
    }

    /**
     * Visszaadja a játékos pontszámát.
     *
     * @return a játékos pontszáma
     */
    public int getScore() {
        return score;
    }
}
