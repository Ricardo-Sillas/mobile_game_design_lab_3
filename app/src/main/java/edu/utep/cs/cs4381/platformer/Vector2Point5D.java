package edu.utep.cs.cs4381.platformer;

public class Vector2Point5D {
    public float x;
    public float y;
    public int z;

    public Vector2Point5D() {
        this(0, 0, 0);
    }

    public Vector2Point5D(float x, float y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}