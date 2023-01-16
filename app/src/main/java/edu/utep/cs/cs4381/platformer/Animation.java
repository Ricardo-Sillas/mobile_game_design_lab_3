package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class Animation {
    Bitmap bitmapSheet;
    String bitmapName;
    private Rect sourceRect;
    private int frameCount;
    private int currentFrame;
    private long frameTicker;
    private int framePeriod;
    private int frameWidth;
    private int frameHeight;
    int pixelsPerMeter;

    Animation(Context context,
              String bitmapName, float frameHeight,
              float frameWidth, int animFps,
              int frameCount, int pixelsPerMeter){
        this.currentFrame = 0;
        this.frameCount = frameCount;
        this.frameWidth = (int)frameWidth * pixelsPerMeter;
        this.frameHeight = (int)frameHeight * pixelsPerMeter;
        sourceRect = new Rect(0, 0, this.frameWidth,
                this.frameHeight);
        framePeriod = 1000 / animFps;
        frameTicker = 0l;
        this.bitmapName = "" + bitmapName;
        this.pixelsPerMeter = pixelsPerMeter;
    }

    public Rect getCurrentFrame(long time,
                                float xVelocity, boolean moves){
        if(xVelocity!=0 || !moves) {
            // Only animate if the object is moving
            // or it is an object which doesn't move
            // but is still animated (like fire)
            if (time > frameTicker + framePeriod) {
                frameTicker = time;
                currentFrame++;
                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                }
            }
        }
        //update the left and right values of the source of
        //the next frame on the spritesheet
        this.sourceRect.left = currentFrame * frameWidth;
        this.sourceRect.right = this.sourceRect.left + frameWidth;
        return sourceRect;
    }
}