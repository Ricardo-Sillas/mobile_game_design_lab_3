package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public abstract class GameObject {

    private RectHitbox rectHitbox = new RectHitbox();
    private Vector2Point5D worldLocation;
    private float width;
    private float height;
    private boolean active = true;
    private boolean visible = true;
    private int animFrameCount = 1;
    private char type;
    private String bitmapName;
    private boolean pushable = false;

    private float xVelocity;
    private float yVelocity;
    final int LEFT = -1;
    final int RIGHT = 1;
    private static int facing;
    private boolean moves = false;

    // Most objects only have 1 frame
    // And don't need to bother with these
    private Animation anim = null;
    private boolean animated;
    private int animFps = 1;

    private boolean traversable = false;

    public abstract void update(long fps, float gravity);

    public String getBitmapName() {
        return bitmapName;
    }

    public Bitmap prepareBitmap(Context context,
                                String bitmapName,
                                int pixelsPerMeter) {
        int resID = context.getResources().getIdentifier(bitmapName,
                "drawable", context.getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (width * animFrameCount * pixelsPerMeter),
                (int) (height * pixelsPerMeter),
                false);
        return bitmap;
    }

    public Vector2Point5D getWorldLocation() {
        return worldLocation;
    }

    public void setWorldLocation(float x, float y, int z) {
        worldLocation = new Vector2Point5D(x, y, z);
    }

    public void setWorldLocationX(float x) {
        worldLocation.x = x;
    }

    public void setWorldLocationY(float y) {
        worldLocation.y = y;
    }

    public void setBitmapName(String bitmapName){
        this.bitmapName = bitmapName;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    void move(long fps){
        if(xVelocity != 0) {
            this.worldLocation.x += xVelocity / fps;
        }
        if(yVelocity != 0) {
            this.worldLocation.y += yVelocity / fps;
        }
    }

    public static int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(float xVelocity) {
        if(moves) {
            this.xVelocity = xVelocity;
        }
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(float yVelocity) {
        if(moves) {
            this.yVelocity = yVelocity;
        }
    }

    public boolean isMoves() {
        return moves;
    }

    public void setMoves(boolean moves) {
        this.moves = moves;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRectHitbox() {
        rectHitbox.setTop(worldLocation.y);
        rectHitbox.setLeft(worldLocation.x);
        rectHitbox.setBottom(worldLocation.y + height);
        rectHitbox.setRight(worldLocation.x + width);
    }

    RectHitbox getHitbox(){
        return rectHitbox;
    }

    public void setAnimFps(int animFps) {
        this.animFps = animFps;
    }
    public void setAnimFrameCount(int animFrameCount) {
        this.animFrameCount = animFrameCount;
    }
    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(Context context, int pixelsPerMetre,
                            boolean animated){
        this.animated = animated;
        this.anim = new Animation(context, bitmapName,
                height,
                width,
                animFps,
                animFrameCount,
                pixelsPerMetre );
    }

    public Rect getRectToDraw(long deltaTime){
        return anim.getCurrentFrame(
                deltaTime,
                xVelocity,
                isMoves());
    }

    public void setTraversable(){
        traversable = true;
    }

    public boolean isTraversable(){
        return traversable;
    }

    public void setPushable() {
        pushable = true;
    }

    public boolean isPushable() {
        return pushable;
    }
}