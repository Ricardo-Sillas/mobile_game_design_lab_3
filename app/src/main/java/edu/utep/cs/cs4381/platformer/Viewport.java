package edu.utep.cs.cs4381.platformer;

import android.graphics.Rect;

class Viewport {

    private static int pixelsPerMeterY;
    private static int pixelsPerMeterX;
    private Vector2Point5D currentViewportWorldCenter;
    private Rect convertedRect;
    private int screenXResolution;
    private int screenYResolution;
    private int screenCenterX;
    private int screenCenterY;
    private int metresToShowX;
    private int metresToShowY;
    private int numClipped;

    public Viewport(int width, int height) {
        screenXResolution = width;
        screenYResolution = height;
        screenCenterX = screenXResolution / 2;
        screenCenterY = screenYResolution / 2;
        pixelsPerMeterX = screenXResolution / 32;
        pixelsPerMeterY = screenYResolution / 16; //16;
        metresToShowX = 34;
        metresToShowY = 20;

        convertedRect = new Rect();
        currentViewportWorldCenter = new Vector2Point5D();
    }

    void setWorldCenter(float x, float y) {
        currentViewportWorldCenter.x = x;
        currentViewportWorldCenter.y = y;
    }

    public int getScreenWidth() {
        return screenXResolution;
    }

    public int getScreenHeight() {
        return screenYResolution;
    }

    public static int getPixelsPerMeterX() {
        return pixelsPerMeterX;
    }

    public Rect worldToScreen(float x, float y, float width, float height) {
        int left = (int) (screenCenterX - (currentViewportWorldCenter.x - x) * pixelsPerMeterX);
        int top = (int) (screenCenterY - (currentViewportWorldCenter.y - y) * pixelsPerMeterY);
        int right = (int) (left + width * pixelsPerMeterX);
        int bottom = (int) (top + height * pixelsPerMeterY);
        convertedRect.set(left, top, right, bottom);
        return convertedRect;
    }

    public boolean clipObject(float x, float y, float width, float height) {
        boolean notClipped = (x - width < currentViewportWorldCenter.x + metresToShowX / 2)
                && (x + width > currentViewportWorldCenter.x - metresToShowX / 2)
                && (y - height < currentViewportWorldCenter.y + metresToShowY / 2)
                && (y + height > currentViewportWorldCenter.y - metresToShowY / 2);
        if (notClipped) {
            numClipped++;
        }
        return !notClipped;
    }

    public int getNumClipped(){
        return numClipped;
    }
    public void resetNumClipped(){
        numClipped = 0;
    }

    public static int getPixelsPerMetreY(){
        return pixelsPerMeterY;
    }
    public int getyCentre(){
        return screenCenterY;
    }
    public float getViewportWorldCentreY(){
        return currentViewportWorldCenter.y;
    }

    public void moveViewportRight(int maxWidth){
        if(currentViewportWorldCenter.x < maxWidth -
                (metresToShowX/2)+3) {
            currentViewportWorldCenter.x += 1;
        }
    }
    public void moveViewportLeft(){
        if(currentViewportWorldCenter.x > (metresToShowX/2)-3){
            currentViewportWorldCenter.x -= 1;
        }
    }
    public void moveViewportUp(){
        if(currentViewportWorldCenter.y > (metresToShowY /2)-3) {
            currentViewportWorldCenter.y -= 1;
        }
    }
    public void moveViewportDown(int maxHeight){
        if(currentViewportWorldCenter.y <
                maxHeight - (metresToShowY / 2)+3) {
            currentViewportWorldCenter.y += 1;
        }
    }
}