/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.time.Duration;
import java.util.ArrayList;

/**
 * A LabyrinthGUI osztály a játék grafikus felhasználói felületéért felelős.
 *
 * Feladatai:
 * - az ablak (JFrame) létrehozása és kezelése,
 * - a játékpanel (Game) megjelenítése,
 * - a menürendszer kezelése,
 * - az idő és az aktuális pálya/pontszám kijelzése,
 * - a toplista megjelenítése.
 */
public class LabyrinthGUI {

    protected JFrame frame;
    protected Game game;
    private JLabel mapLabel;
    protected long startTime;
    private JLabel timeLabel;
    private Timer GUIframeTimer;

    /**
    * Létrehozza és inicializálja a grafikus felhasználói felületet.
    *
    * Beállítja:
    * - a főablakot,
    * - a menüt,
    * - a játékpanelt,
    * - az időmérőt és a frissítési időzítőt.
    */
    public LabyrinthGUI() {
        frame = new JFrame("Labyrinth");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapLabel = new JLabel(" ");
        mapLabel.setHorizontalAlignment(JLabel.CENTER);
        mapLabel.setVerticalAlignment(JLabel.CENTER);
        timeLabel = new JLabel(" ");

        game = new Game(this);
        mapLabel = game.getMapLabel();

        frame.getContentPane().add(game, BorderLayout.CENTER);
        frame.getContentPane().add(mapLabel,BorderLayout.NORTH);
        frame.getContentPane().add(timeLabel,BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu restartMenu = new JMenu("Game");
        menuBar.add(restartMenu);
        JMenuItem restartButton = new JMenuItem("Restart");
        restartMenu.add(restartButton);

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.resetMenu();
                startTime = System.currentTimeMillis();
                GUIframeTimer.restart();
            }
        });
        JMenuItem scoresButton = new JMenuItem("High Scores");
        scoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHighScores();
            }
        });
        restartMenu.add(scoresButton);


        frame.setPreferredSize(new Dimension(420, 520));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        GUIframeTimer = new Timer(10, new GUIFrameListener());
        startTime = System.currentTimeMillis();
        GUIframeTimer.start();
    }

    /**
    * Megjeleníti a legjobb 10 játékos pontszámát egy felugró ablakban.
    *
    * A pontszámokat a DatabaseHandler osztálytól kéri le.
    * Hiba esetén figyelmeztető üzenetet jelenít meg.
    */
    private void showHighScores() {
        try {
            ArrayList<PlayerData> highScores = DatabaseHandler.getHighScores();
            StringBuilder scoresMessage = new StringBuilder("Top 10 Players:\n\n");

            int count = 1;
            for (PlayerData playerData : highScores) {
                scoresMessage.append(count++)
                             .append(". ")
                             .append(playerData.getName())
                             .append(" - ")
                             .append(playerData.getScore())
                             .append("\n");
            }

            if (highScores.isEmpty()) {
                scoresMessage.append("No scores yet.");
            }

            JOptionPane.showMessageDialog(frame, scoresMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading high scores.");
        }
    }

    /**
     * Kiszámolja a játék indulása óta eltelt időt.
     *
     * @return az eltelt idő milliszekundumban
     */
    private long elapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
    * Időzítő által meghívott eseménykezelő osztály.
    *
    * Feladata:
    * - az idő kijelzésének frissítése,
    * - a játék aktuális állapotának figyelése.
    */
    class GUIFrameListener implements ActionListener {

        /**
        * Az időzítő által meghívott metódus,
        * amely frissíti az idő kijelzését.
        *
        * @param ae az esemény objektuma
        */
        @Override
        public void actionPerformed(ActionEvent ae) {
            mapLabel = game.getMapLabel();
            if (!game.isPlayerNextToDragon()){
                timeLabel.setText(properTime());
            }
        }
    }
    
    /**
    * Az eltelt időt felhasználóbarát formában adja vissza.
    *
    * Perc és másodperc formátumot használ,
    * ha az eltelt idő eléri az egy percet.
    *
    * @return az idő szöveges formában
    */
    private String properTime() {
        Duration d = Duration.ofMillis(elapsedTime());

        long minutes = d.toMinutes();
        long seconds = d.toSecondsPart();

        if (minutes > 0) {
            return minutes + " m " + seconds + " s ";
        } else {
            return seconds + " s ";
        } 
    }
}
