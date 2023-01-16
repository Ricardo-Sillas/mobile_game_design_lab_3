package edu.utep.cs.cs4381.platformer;

import java.util.Random;

public class Boulders extends GameObject {
    Boulders(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = 1;
        final float WIDTH = 3;
        setHeight(HEIGHT); // 1 metre tall
        setWidth(WIDTH); // 1 metre wide
        setType(type);

        // Choose a Bitmap
        setBitmapName("boulder");
        setActive(false);//don't check for collisions etc

        Random rand = new Random();
        if (rand.nextInt(2) == 0) {
            setWorldLocation(worldStartX, worldStartY, -1);
        } else {
            setWorldLocation(worldStartX, worldStartY, 1);//
        }
        //No hitbox!!
    }

    public void update(long fps, float gravity) {
    }
}