package edu.utep.cs.cs4381.platformer;

import java.util.List;

public class LevelData {

    protected List<String> tiles;

    protected List<BackgroundData> backgroundDataList;

    protected List<Location> locations;

    // Tile types
    // . = no tile
    // 1 = Grass
    // 2 = Snow
    // 3 = Brick
    // 4 = Coal
    // 5 = Concrete
    // 6 = Scorched
    // 7 = Stone
    //Active objects
    // g = guard
    // d =
    // drone
    // t = teleport
    // c = coin
    // u = upgrade
    // f = fire
    // e = extra life
    //Inactive objects
    // w = tree
    // x = tree2 (snowy)
    // l = lampost
    // r = stalactite
    // s = stalacmite
    // m = mine cart
    // z = boulders
}