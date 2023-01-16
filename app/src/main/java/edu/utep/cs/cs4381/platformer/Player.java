package edu.utep.cs.cs4381.platformer;

import android.content.Context;

public class Player extends GameObject {

    private static final float HEIGHT = 2;
    private static final float WIDTH = 1;

    final float MAX_X_VELOCITY = 10;
    boolean isPressingRight = false;
    boolean isPressingLeft = false;
    public boolean isFalling;
    private boolean isJumping;
    private long jumpTime;
    private long maxJumpTime = 700;// jump 7 10ths of second
    private float currentVelocity = 0;

    RectHitbox rectHitboxFeet;
    RectHitbox rectHitboxHead;
    RectHitbox rectHitboxLeft;
    RectHitbox rectHitboxRight;

    public MachineGun bfg;

    public Player(Context context, float worldStartX, float worldStartY, int pixelsPerMetre) {
        setHeight(HEIGHT); // 2 meters tall
        setWidth(WIDTH);   // 1 meter wide
        setType('p');
//        setBitmapName("player");
        setBitmapName("player1");  // Bob with a gun

        final int ANIMATION_FPS = 16;
        final int ANIMATION_FRAME_COUNT = 5;
        // Set this object up to be animated
        setAnimFps(ANIMATION_FPS);
        setAnimFrameCount(ANIMATION_FRAME_COUNT);
        setAnimated(context, pixelsPerMetre, true);

        setWorldLocation(worldStartX, worldStartY, 0);

        // Standing still to start with
        setxVelocity(0);
        setyVelocity(0);
        setFacing(LEFT);
        isFalling = false;
        // Now for the player's other attributes
        // Our game engine will use these
        setMoves(true);
        setActive(true);
        setVisible(true);

        rectHitboxFeet = new RectHitbox();
        rectHitboxHead = new RectHitbox();
        rectHitboxLeft = new RectHitbox();
        rectHitboxRight = new RectHitbox();

        bfg = new MachineGun();
    }

    public void setPressingRight(boolean flag) {
        isPressingRight = flag;
    }

    public void update(long fps, float gravity) {
        if (isPressingRight) {
            this.setxVelocity(currentVelocity);
            if(currentVelocity < MAX_X_VELOCITY) {
                currentVelocity += 0.25;
            }
        } else if (isPressingLeft) {
            this.setxVelocity(-currentVelocity);
            if(currentVelocity < MAX_X_VELOCITY) {
                currentVelocity += 0.25;
            }
        } else {
            if(GameObject.getFacing() == 1) {
                this.setxVelocity(currentVelocity);
            }
            else {
                this.setxVelocity(-currentVelocity);
            }
            if(currentVelocity > 0) {
                currentVelocity -= 0.25;
            }
        }
        //which way is player facing?
        if (this.getxVelocity() > 0) {
            //facing right
            setFacing(RIGHT);
        } else if (this.getxVelocity() < 0) {
            //facing left
            setFacing(LEFT);
        }//if 0 then unchanged

        // Jumping and gravity
        if (isJumping) {
            long timeJumping = System.currentTimeMillis() - jumpTime;
            if (timeJumping < maxJumpTime) {
                if (timeJumping < maxJumpTime / 2) {
                    this.setyVelocity(-gravity);//on the way up
                } else if (timeJumping > maxJumpTime / 2) {
                    this.setyVelocity(gravity);//going down
                }
            } else {
                isJumping = false;
            }
        } else {
            this.setyVelocity(gravity);
            // Read Me!
            // Remove this next line to make the game easier
            // it means the long jumps are less punishing
            // because the player can take off just after the platform
            // They will also be able to cheat by jumping in thin air
            isFalling = true;
        }

        bfg.update(fps, gravity);

        // Let's go!
        this.move(fps);

        // Update all the hitboxes to the new location
        // Get the current world location of the player
        // and save them as local variables we will use next
        Vector2Point5D location = getWorldLocation();
        float lx = location.x;
        float ly = location.y;
        //update the player feet hitbox
        rectHitboxFeet.top = ly + getHeight() * .95f;
        rectHitboxFeet.left = lx + getWidth() * .2f;
        rectHitboxFeet.bottom = ly + getHeight() * .98f;
        rectHitboxFeet.right = lx + getWidth() * .8f;
        // Update player head hitbox
        rectHitboxHead.top = ly;
        rectHitboxHead.left = lx + getWidth() * .4f;
        rectHitboxHead.bottom = ly + getHeight() * .2f;
        rectHitboxHead.right = lx + getWidth() * .6f;
        // Update player left hitbox
        rectHitboxLeft.top = ly + getHeight() * .2f;
        rectHitboxLeft.left = lx + getWidth() * .2f;
        rectHitboxLeft.bottom = ly + getHeight() * .8f;
        rectHitboxLeft.right = lx + getWidth() * .3f;
        // Update player right hitbox
        rectHitboxRight.top = ly + getHeight() * .2f;
        rectHitboxRight.left = lx + getWidth() * .8f;
        rectHitboxRight.bottom = ly + getHeight() * .8f;
        rectHitboxRight.right = lx + getWidth() * .7f;
    }

    public int checkCollisions(RectHitbox rectHitbox) {
        int collided = 0;// No collision
        // The left
        if (this.rectHitboxLeft.intersects(rectHitbox)) {
            // Left has collided
            // Move player just to right of current hitbox
            this.setWorldLocationX(rectHitbox.right - getWidth() * .2f);
            collided = 1;
        }
        // The right
        if (this.rectHitboxRight.intersects(rectHitbox)) {
            // Right has collided
            // Move player just to left of current hitbox
            this.setWorldLocationX(rectHitbox.left - getWidth() * .8f);
            collided = 1;
        }
        // The feet
        if (this.rectHitboxFeet.intersects(rectHitbox)) {
            // Feet have collided
            // Move feet to just above current hitbox
            this.setWorldLocationY(rectHitbox.top - getHeight());
            collided = 2;
        }
        // Now the head
        if (this.rectHitboxHead.intersects(rectHitbox)) {
            // Head has collided. Ouch!
            // Move head to just below current hitbox bottom
            this.setWorldLocationY(rectHitbox.bottom);
            collided = 3;
        }
        return collided;
    }

    public void setPressingLeft(boolean isPressingLeft) {
        this.isPressingLeft = isPressingLeft;
    }
    public void startJump(SoundManager sm) {
        if (!isFalling) {//can't jump if falling
            if (!isJumping) {//not already jumping
                isJumping = true;
                jumpTime = System.currentTimeMillis();
                sm.play(SoundManager.Sound.JUMP);
            }
        }
    }

    public boolean pullTrigger() {
        //Try and fire a shot
        return bfg.shoot(this.getWorldLocation().x,
                this.getWorldLocation().y,
                getFacing(), getHeight());
    }

    public void restorePreviousVelocity() {
        if (!isJumping && !isFalling) {
            if (getFacing() == LEFT) {
                isPressingLeft = true;
                setxVelocity(-MAX_X_VELOCITY);
            } else {
                isPressingRight = true;
                setxVelocity(MAX_X_VELOCITY);
            }
        }
    }
}