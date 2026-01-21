/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A DatabaseHandler osztály a játékosok pontszámainak
 * fájlba mentéséért és visszaolvasásáért felel.
 * 
 * Az adatokat egyszerű szöveges fájlban tárolja
 * az alábbi formátumban:
 * 
 * név;pontszám
 * 
 * Az osztály statikus metódusokat tartalmaz,
 * így példányosítás nélkül használható.
 */
public class DatabaseHandler {

    private static final String FILE_NAME = "player_data.txt";

    /**
     * Elmenti egy játékos adatait a fájl végére.
     * 
     * A mentés hozzáfűzéssel történik (append),
     * így a korábbi eredmények nem vesznek el.
     *
     * @param playerData a mentendő játékos neve és pontszáma
     */
    public static void savePlayerData(PlayerData playerData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            // formátum: név;pontszám
            writer.write(playerData.getName() + ";" + playerData.getScore());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Beolvassa az összes mentett játékosadatot,
     * majd visszaadja a legjobb 10 pontszámot.
     * 
     * A pontszámok csökkenő sorrendbe vannak rendezve.
     * 
     * @return az elért Top 10 játékos listája
     */
    public static ArrayList<PlayerData> getHighScores() {
        ArrayList<PlayerData> highScores = new ArrayList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return highScores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    highScores.add(new PlayerData(name, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        highScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        if (highScores.size() > 10) {
            return new ArrayList<>(highScores.subList(0, 10));
        }

        return highScores;
    }
}


