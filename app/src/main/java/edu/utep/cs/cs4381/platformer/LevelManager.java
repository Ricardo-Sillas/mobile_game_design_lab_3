package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    public static Bitmap[] bitmapsArray;
    private String level;
    public int mapWidth;
    public int mapHeight;
    public Player player;
    public int playerIndex;
    private boolean playing;
    public float gravity;
    private LevelData levelData;
    public ArrayList<GameObject> gameObjects;
    private List<Rect> currentButtons;
    public ArrayList<Background> backgrounds;

    public LevelManager(Context context, int pixelsPerMeter, int screenWidth,
                        InputController ic, String level, float px, float py) {
        this.level = level;
        switch (level) {
            case "LevelTesting":
                levelData = new LevelTesting();
                break;
            case "LevelCave":
                levelData = new LevelCave();
                break;
            case "LevelCity":
                levelData = new LevelCity();
                break;
            case "LevelForest":
                levelData = new LevelForest();
                break;
            case "LevelMountain":
                levelData = new LevelMountain();
                break;
            case "LevelAll":
                levelData = new LevelAll();
                break;
        }
        gameObjects = new ArrayList<>();
        bitmapsArray = new Bitmap[25];
        loadMapData(context, pixelsPerMeter, px, py);
        loadBackgrounds(context, pixelsPerMeter, screenWidth);
        setWaypoints();
    }

    private void loadBackgrounds(Context context, int pixelsPerMetre, int screenWidth) {
        backgrounds = new ArrayList<>();
        for (BackgroundData bgData : levelData.backgroundDataList) {
            backgrounds.add(new Background(context, pixelsPerMetre, screenWidth, bgData));
        }
    }

    public void switchPlayingStatus() {
        playing = !playing;
        if (playing) {
            gravity = 6;
        } else {
            gravity = 0;
        }
    }

    public static int getBitmapIndex(char blockType) {
        int index = 0;
        switch (blockType) {
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            case 'c':
                index = 3;
                break;
            case 'u':
                index = 4;
                break;
            case 'e':
                index = 5;
                break;
            case 'd':
                index = 6;
                break;
            case 'g':
                index = 7;
                break;
            case 'f':
                index = 8;
                break;
            case '2':
                index = 9;
                break;

            case '3':
                index = 10;
                break;

            case '4':
                index = 11;
                break;

            case '5':
                index = 12;
                break;

            case '6':
                index = 13;
                break;

            case '7':
                index = 14;
                break;

            case 'w':
                index = 15;
                break;

            case 'x':
                index = 16;
                break;

            case 'l':
                index = 17;
                break;

            case 'r':
                index = 18;
                break;

            case 's':
                index = 19;
                break;

            case 'm':
                index = 20;
                break;

            case 'z':
                index = 21;
                break;

            case 't':
                index = 22;
                break;
        }
        return index;
    }

    public static Bitmap getBitmap(char blockType) {
        return bitmapsArray[getBitmapIndex(blockType)];
    }

    private void loadMapData(Context context, int pixelsPerMeter, float px, float py) {
        int currentIndex = -1;
        int teleportIndex = -1;
        mapHeight = levelData.tiles.size();
        mapWidth = levelData.tiles.get(0).length();
        for (int i = 0; i < levelData.tiles.size(); i++) {
            for (int j = 0; j < levelData.tiles.get(i).length(); j++) {
                char c = levelData.tiles.get(i).charAt(j);
                if (c != '.') {
                    currentIndex++;
                    switch (c) {
                        case '1':
                            gameObjects.add(new Grass(j, i, c));
                            break;
                        case 'p':
                            player = new Player(context, px, py, pixelsPerMeter);
                            gameObjects.add(player);
                            playerIndex = currentIndex;
                            break;
                        case 'c':
                            gameObjects.add(new Coin(j, i, c));
                            break;
                        case 'u':
                            gameObjects.add(new MachineGunUpgrade(j, i, c));
                            break;
                        case 'e':
                            gameObjects.add(new ExtraLife(j, i, c));
                            break;
                        case 'd':
                            gameObjects.add(new Drone(j, i, c));
                            break;
                        case 'g':
                            gameObjects.add(new Guard(context, j, i, c, pixelsPerMeter));
                            break;
                        case 'f':
                            gameObjects.add(new Fire (context, j, i, c, pixelsPerMeter));
                            break;
                        case '2':
                            // Add a tile to the gameObjects
                             gameObjects.add(new Snow(j, i, c));
                             break;
                        case '3':
                            // Add a tile to the gameObjects
                             gameObjects.add(new Brick(j, i, c));
                             break;
                        case '4':
                            // Add a tile to the gameObjects
                             gameObjects.add(new Coal(j, i, c));
                             break;
                        case '5':
                            // Add a tile to the gameObjects
                             gameObjects.add(new Concrete(j, i, c));
                             break;
                        case '6':
                            // Add a tile to the gameObjects
                             gameObjects.add(new Scorched(j, i, c));
                             break;
                        case '7':
                            // Add a tile to the gameObjects
                             gameObjects.add(new Stone(j, i, c));
                             break;
                        case 'w':
                            // Add a tree to the gameObjects
                             gameObjects.add(new Tree(j, i, c));
                             break;
                        case 'x':
                            // Add a tree2 to the gameObjects
                             gameObjects.add(new Tree2(j, i, c));
                             break;
                        case 'l':
                            // Add a tree to the gameObjects
                             gameObjects.add(new Lampost(j, i, c));
                             break;
                        case 'r':
                            // Add a stalactite to the gameObjects
                             gameObjects.add(new Stalactite(j, i, c));
                             break;
                        case 's':
                            // Add a stalagmite to the gameObjects
                             gameObjects.add(new Stalagmite(j, i, c));
                             break;
                        case 'm':
                            // Add a cart to the gameObjects
                             gameObjects.add(new Cart(j, i, c));
                             break;
                        case 'z':
                            // Add a boulders to the gameObjects
                             gameObjects.add(new Boulders(j, i, c));
                             break;
                        case 't':
                            teleportIndex++;
                            gameObjects.add(new Teleport(j, i, c,
                                    levelData.locations.get(teleportIndex)));
                            break;
                    }
                    if (bitmapsArray[getBitmapIndex(c)] == null) {
                        GameObject go = gameObjects.get(currentIndex);
                        bitmapsArray[getBitmapIndex(c)] = go.prepareBitmap(context,
                                go.getBitmapName(), pixelsPerMeter);
                    }
                }
            }
        }
    }

    public void setWaypoints() {
        for (GameObject guard: gameObjects) {
            if (guard.getType() == 'g') {
                int feetTileIndex = -1; // index of the tile beneath the guard
                float leftEnd = -1, rightEnd = -1;  // left and right ends of the calculated route
                for (GameObject tile: gameObjects) {
                    feetTileIndex++;
                    if (tile.getWorldLocation().y == guard.getWorldLocation().y + 2
                            &&  tile.getWorldLocation().x == guard.getWorldLocation().x) {
                        leftEnd = gameObjects.get(feetTileIndex - 5).getWorldLocation().x;
                        for (int i = 1; i <= 5; i++) {
                            GameObject left = gameObjects.get(feetTileIndex - i);
                            if (left.getWorldLocation().x != guard.getWorldLocation().x - i
                                    || left.getWorldLocation().y != guard.getWorldLocation().y + 2
                                    || !left.isTraversable()) {
                                leftEnd = gameObjects.get(feetTileIndex - (i - 1)).getWorldLocation().x;
                                break; } }

                        rightEnd = gameObjects.get(feetTileIndex + 5).getWorldLocation().x;
                        for (int i = 1; i <= 5; i++) {
                            GameObject right = gameObjects.get(feetTileIndex + i);
                            if (right.getWorldLocation().x != guard.getWorldLocation().x + i
                                    || right.getWorldLocation().y != guard.getWorldLocation().y + 2
                                    || !right.isTraversable()) {
                                rightEnd = gameObjects.get(feetTileIndex + (i - 1)).getWorldLocation().x;
                                break; } }

                        ((Guard) guard).setWaypoints(leftEnd, rightEnd);
                    }
                }
            }
        }
    }

    public boolean isPlaying() {
        return playing;
    }
}