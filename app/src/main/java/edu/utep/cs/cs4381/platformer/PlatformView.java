package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class PlatformView extends SurfaceView implements Runnable {

    private final SoundManager soundManager;
    private boolean debugging = true;
    private volatile boolean running;
    private Thread gameThread = null;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    private Context context;
    private long startFrameTime;
    private long timeThisFrame;
    private long fps;
    private PointF location;

    private LevelManager lm;
    private Viewport vp;
    private InputController ic;
    private PlayerState ps;

    public PlatformView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        holder = getHolder();
        paint = new Paint();
        vp = new Viewport(screenWidth, screenHeight);
        soundManager = SoundManager.instance(context);
        ps = new PlayerState();
        loadLevel("LevelAll", 3, 20);
    }

    private void loadLevel(String level, float px, float py) {
        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());
        PointF location = new PointF(px, py);
        ps.saveLocation(location);
        lm = new LevelManager(context, vp.getPixelsPerMeterX(),
                vp.getScreenWidth(), ic, level, px, py);
        vp.setWorldCenter(
                lm.gameObjects.get(lm.playerIndex).getWorldLocation().x,
                lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
        // reload the players current fire rate from the player state
        lm.player.bfg.setFireRate(ps.getFireRate());
    }

    @Override
    public void run() {
        while (running) {
            startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        for (GameObject go : lm.gameObjects) {
            if (go.isActive()) {
                if (!vp.clipObject(go.getWorldLocation().x,
                        go.getWorldLocation().y, go.getWidth(), go.getHeight())) {
                    go.setVisible(true);

                    // check collisions with player
                    int hit = lm.player.checkCollisions(go.getHitbox());
                    if (hit > 0) {
                        switch (go.getType()) {
                            case 'c':
                                soundManager.play(SoundManager.Sound.COIN_PICKUP);
                                go.setActive(false);
                                go.setVisible(false);
                                ps.gotCredit();
                                if (hit != 2) { // hit not by feet
                                    lm.player.restorePreviousVelocity();
                                }
                                break;
                            case 'e':
                                soundManager.play(SoundManager.Sound.EXTRA_LIFE);
                                go.setActive(false);
                                go.setVisible(false);
                                ps.addLife();
                                if (hit != 2) {
                                    lm.player.restorePreviousVelocity();
                                }
                                break;
                            case 'u':
                                soundManager.play(SoundManager.Sound.GUN_UPGRADE);
                                go.setActive(false);
                                go.setVisible(false);
                                lm.player.bfg.upgradeRateOfFire();
                                ps.increaseFireRate();
                                break;
                            case 'd':
                                soundManager.play(SoundManager.Sound.PLAYER_BURN);
                                ps.loseLife();
                                PointF loc= new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player.setWorldLocationX(loc.x);
                                lm.player.setWorldLocationY(loc.y);
                                lm.player.setxVelocity(0);
                                break;
                            case 'g':
                                soundManager.play(SoundManager.Sound.PLAYER_BURN);
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setxVelocity(0);
                                break;
                            case 'f':
                                soundManager.play(SoundManager.Sound.PLAYER_BURN);
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setxVelocity(0);
                                break;
                            case 't':
                                Teleport teleport = (Teleport) go;
                                Location target = teleport.getTarget();
                                loadLevel(target.level, target.x, target.y);
                                soundManager.play(SoundManager.Sound.TELEPORT);
                                break;
                            default:
                                if (hit == 1) { // left or right
                                    lm.player.setxVelocity(0);
                                    lm.player.setPressingRight(false);
                                }
                                if (hit == 2) { // feet
                                    lm.player.isFalling = false;
                                }
                                break;
                        }
                    }
                    for (int i = 0; i < lm.player.bfg.getNumBullets(); i++) {
                        RectHitbox r = new RectHitbox();
                        r.setLeft(lm.player.bfg.getBulletX(i));
                        r.setTop(lm.player.bfg.getBulletY(i));
                        r.setRight(lm.player.bfg.getBulletX(i) + .1f);
                        r.setBottom(lm.player.bfg.getBulletY(i) + .1f);
                        if (go.getHitbox().intersects(r)) {
                            lm.player.bfg.hideBullet(i);
                            switch (go.getType()) {
                                case 'g': // guard
                                    go.setWorldLocation(-100,-100,0);
                                    soundManager.play(SoundManager.Sound.HIT_GUARD);
                                    break;
                                case 'd': // drone
                                    soundManager.play(SoundManager.Sound.EXPLODE);
                                    go.setWorldLocation(-100, -100, 0);
                                    break;
                                default:
                                    soundManager.play(SoundManager.Sound.RICOCHET);
                            }
                        }
                    }
                    if (lm.isPlaying()) {
                        go.update(fps, lm.gravity);
                        if (go.getType() == 'd') {
                            Drone d = (Drone) go;
                            d.setWaypoint(lm.player.getWorldLocation());
                        }
                    }
                } else {
                    go.setVisible(false);
                }
            }
        }
        if (lm.isPlaying()) {
            vp.setWorldCenter(
                    lm.player.getWorldLocation().x,
                    lm.player.getWorldLocation().y);
            // has player fallen out of the map?
            if (lm.player.getWorldLocation().x < 0
                    || lm.player.getWorldLocation().x > lm.mapWidth
                    || lm.player.getWorldLocation().y > lm.mapHeight) {
                soundManager.play(SoundManager.Sound.PLAYER_BURN);
                ps.loseLife();
                PointF location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                lm.player.setWorldLocationX(location.x);
                lm.player.setWorldLocationY(location.y);
                lm.player.setxVelocity(0);
            }
            // check if game is over
            if (ps.getLives() == 0) {
                ps = new PlayerState();
                loadLevel("LevelAll", 3, 20);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (lm != null) {
            ic.handleInput(motionEvent, lm, soundManager, vp);
        }
//        switch (motionEvent.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                lm.switchPlayingStatus();
//                break;
//        }
        return true;
    }

    private void draw() {
        if (holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            paint.setColor(Color.argb(255, 0, 0, 255));
            canvas.drawColor(Color.argb(255, 0, 0, 255));
            drawBackground(0, -3);  // behind Bob (at z=0)
            Rect toScreen2D = new Rect();
            for (int layer = -1; layer <= 1; layer++) {
                for (GameObject go : lm.gameObjects) {
                    if (go.isVisible() && go.getWorldLocation().z == layer) {
                        toScreen2D.set(vp.worldToScreen(go.getWorldLocation().x,  go.getWorldLocation().y,
                                go.getWidth(), go.getHeight()));
                        if (go.isAnimated()) {
                            // Get the next frame of the bitmap
                            // Rotate if necessary
                            if (go.getFacing() == 1) {
                                // Rotate
                                Matrix flipper = new Matrix();
                                flipper.preScale(-1, 1.15f);
                                Rect r = go.getRectToDraw(System.currentTimeMillis());
                                Bitmap b = Bitmap.createBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                        r.left, r.top, r.width(), r.height(), flipper, true);
                                canvas.drawBitmap(b, toScreen2D.left, toScreen2D.top, paint);
                            } else {
                                // draw it the regular way round
                                canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                        go.getRectToDraw(System.currentTimeMillis()), toScreen2D, paint);
                            }
                        } else { // Just draw the whole bitmap
                            canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                    toScreen2D.left, toScreen2D.top, paint);
                        }

                    }
                }
            }
            // draw buttons
            paint.setColor(Color.argb(80, 255, 255, 255));
            List<Rect> buttonsToDraw = ic.getButtons();
            for (Rect r: buttonsToDraw) {
                RectF rf = new RectF(r.left, r.top, r.right, r.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
            }

            // draw paused text
            if (!lm.isPlaying()) {
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(120);
                canvas.drawText("Paused", vp.getScreenWidth() / 2,
                        vp.getScreenHeight() / 2, paint);
            }

            paint.setColor(Color.argb(255, 255, 255, 255));
            for (int i = 0; i < lm.player.bfg.getNumBullets(); i++) {
                toScreen2D.set(vp.worldToScreen(lm.player.bfg.getBulletX(i), lm.player.bfg.getBulletY(i)+0.45f,
                        .25f, .05f)); // bullet width and height
                canvas.drawRect(toScreen2D, paint);
            }
            drawBackground(4, 0);   // in front of Bob
            // draw the HUD
            int topSpace = Viewport.getPixelsPerMetreY() / 4;
            int iconSize = Viewport.getPixelsPerMeterX();
            int padding = Viewport.getPixelsPerMeterX() / 5;
            int centring = Viewport.getPixelsPerMetreY() / 6;
            paint.setTextSize(Viewport.getPixelsPerMetreY() / 2);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.argb(100, 0, 0, 0));
            canvas.drawRect(0,0,iconSize * 7.0f, topSpace*2 + iconSize,paint);
            paint.setColor(Color.argb(255, 255, 255, 0));
            canvas.drawBitmap(LevelManager.getBitmap('e'), 0, topSpace, paint);
            canvas.drawText("" + ps.getLives(), (iconSize * 1) + padding, iconSize - centring, paint);
            canvas.drawBitmap(LevelManager.getBitmap('c'), iconSize * 2.5f + padding, topSpace, paint);
            canvas.drawText("" + ps.getCredits(), (iconSize * 3.5f) + padding * 2, iconSize - centring, paint);
            canvas.drawBitmap(LevelManager.getBitmap('u'), iconSize * 5.0f + padding, topSpace, paint);
            canvas.drawText("" + ps.getFireRate(), iconSize * 6.0f + padding * 2, iconSize - centring, paint);

            if (debugging) {
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawText("fps:" + fps, 10, 180, paint);
                canvas.drawText("num objects:" + lm.gameObjects.size(), 10, 235, paint);
                canvas.drawText("num clipped:" + vp.getNumClipped(), 10, 290, paint);
                canvas.drawText("playerX:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().x, 10, 345, paint);
                canvas.drawText("playerY:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().y, 10, 400, paint);
                canvas.drawText("Gravity:" + lm.gravity, 10, 455, paint);
                canvas.drawText("X velocity:" + lm.gameObjects.get(lm.playerIndex).getxVelocity(), 10, 510, paint);
                canvas.drawText("Y velocity:" + lm.gameObjects.get(lm.playerIndex).getyVelocity(), 10, 565, paint);
                vp.resetNumClipped();
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBackground(int start, int stop) {
        Rect fromRect1 = new Rect(), toRect1 = new Rect();
        Rect fromRect2 = new Rect(), toRect2 = new Rect();
        for(Background bg : lm.backgrounds) {
            if (bg.z < start && bg.z > stop) {

                // clip anything off-screen
                if (!vp.clipObject(-1, bg.y, 1000, bg.height)) {
                    int startY = (int) (vp.getyCentre() -  (vp.getViewportWorldCentreY() - bg.y) *
                            vp.getPixelsPerMetreY());
                    int endY = (int) (vp.getyCentre() - (vp.getViewportWorldCentreY() - bg.endY) *
                            vp.getPixelsPerMetreY());

                    // define what portion of bitmaps to capture and what coordinates to draw them at
                    fromRect1 = new Rect(0, 0, bg.width - bg.xClip,  bg.height);
                    toRect1 = new Rect(bg.xClip, startY, bg.width, endY);
                    fromRect2 = new Rect(bg.width - bg.xClip, 0, bg.width, bg.height);
                    toRect2 = new Rect(0, startY, bg.xClip, endY);
                }
                // draw backgrounds
                if (!bg.reversedFirst) {
                    canvas.drawBitmap(bg.bitmap, fromRect1, toRect1, paint);
                    canvas.drawBitmap(bg.bitmapReversed, fromRect2, toRect2, paint);
                } else {
                    canvas.drawBitmap(bg.bitmap, fromRect2, toRect2, paint);
                    canvas.drawBitmap(bg.bitmapReversed, fromRect1, toRect1, paint);
                }

                // calculate the next value for the background's clipping position by modifying xClip
                // and switching which background is drawn first, if necessary.
                bg.xClip -= lm.player.getxVelocity() / (20 / bg.speed);
                if (bg.xClip >= bg.width) {
                    bg.xClip = 0;
                    bg.reversedFirst = !bg.reversedFirst;
                } else if (bg.xClip <= 0) {
                    bg.xClip = bg.width;
                    bg.reversedFirst = !bg.reversedFirst;
                }
            }
        }
    }

    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}