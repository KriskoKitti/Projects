# Labyrinth Game

**Labyrinth** egy Java alapú labirintus játék, ahol a játékosnak ki kell jutnia a pályáról, miközben elkerüli a sárkányt. A játék grafikus felületen fut, és dinamikus árnyékmechanikát, többféle pályát és pontszám-rendszert tartalmaz.

## Főbb jellemzők
- Egyszerű, vizuális labirintus Java Swing felülettel.
- Fő karakter: **Player**, aki a WASD billentyűkkel mozog.
- Ellenfél: **Dragon**, aki véletlenszerűen mozog a labirintusban, a játékost próbálja követni.
- Árnyékblokkok (**ShadowBlock**) dinamikusan változnak a játékos pozíciója alapján.
- Több pálya betöltése és pontszám-rendszer.
- Fő cél: eljutni a labirintus kijáratához anélkül, hogy a sárkány elkapná.
- Eredmények mentése és lekérdezése a **DatabaseHandler** segítségével (top 10 pontszám).

## Technológiák
- **Java** (Swing)
- Képek: `.png` és `.jpg` formátum
- Játéklogika teljesen objektumorientáltan (OOP)
- Mentés: szöveges fájl (`player_data.txt`)

## Osztályok
- **Main** – Játék indító osztály.
- **LabyrinthGUI** – Grafikus felület kezelése, menük, idő és pontszám megjelenítése.
- **Game** – Játéklogika, fő ciklus, billentyűkezelés.
- **Labyrinth** – Pálya betöltése fájlból, blokkok és árnyékok kezelése.
- **Block** – Egy labirintus mező, falak és kijáratok.
- **ShadowBlock** – Árnyékmező, amely láthatóvá válik a játékos közelében.
- **Player** – Játékos karakter, pozíció és mozgás.
- **Dragon** – Ellenfél karakter, véletlenszerű mozgás és irány.
- **Sprite** – Alap osztály minden vizuális elemhez.
- **PlayerData** – Játékos adatok (név, pontszám).
- **DatabaseHandler** – Pontszámok mentése és lekérdezése.

## Telepítés és futtatás
1. Klónozd a repository-t:
```bash
git clone https://github.com/KriskoKitti/Projects.git
```
2. Lépj a projekt mappájába:
```bash
cd labyrinth-game
```
3. Fordítsd le a Java fájlokat:
```bash
javac -d bin src/labyrinth/*.java
```
4. Futtasd a játékot:
```bash
java -cp bin labyrinth.Main
```
