package edu.utep.cs.cs4381.platformer;

import java.util.Random;

public class Cart extends GameObject {
    Cart(float worldStartX, float worldStartY, char type) {

        setTraversable();

        final float HEIGHT = 2;
        final float WIDTH = 3;
        setWidth(WIDTH);
        setHeight(HEIGHT);
        setType(type);
        setBitmapName("cart");
        setActive(false);
        Random rand = new Random();
        if(rand.nextInt(2)==0) {
            setWorldLocation(worldStartX, worldStartY, -1);
        }else{
            setWorldLocation(worldStartX, worldStartY, 1);
        }
        setRectHitbox();
    }
    public void update(long fps, float gravity) {
    }
}