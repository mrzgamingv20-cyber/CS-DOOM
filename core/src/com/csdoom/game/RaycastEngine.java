package com.csdoom.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class RaycastEngine {
    private static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,0,0,0,0,0,0,0,1,1,0,0,1},
        {1,0,1,0,0,0,0,0,0,0,0,0,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,1,1,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,1,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,1,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    private float playerX = 2.5f, playerY = 2.5f;
    private float dirX = 1f, dirY = 0f;
    private float planeX = 0f, planeY = 0.66f;
    private int health = 100;
    private int screenW, screenH;
    private Pixmap pixmap;
    private Texture screenTexture;

    // Virtual joystick kiri (gerak)
    private Vector2 moveJoyCenter = new Vector2();
    private Vector2 moveJoyCurrent = new Vector2();
    private boolean moveJoyActive = false;
    private int moveJoyPointer = -1;

    // Virtual joystick kanan (rotasi)
    private Vector2 rotJoyCenter = new Vector2();
    private Vector2 rotJoyCurrent = new Vector2();
    private boolean rotJoyActive = false;
    private int rotJoyPointer = -1;

    // Tombol tembak
    private boolean shooting = false;

    public RaycastEngine() {
        screenW = Gdx.graphics.getWidth();
        screenH = Gdx.graphics.getHeight();
        pixmap = new Pixmap(screenW, screenH, Pixmap.Format.RGBA8888);
    }

    public void update(float delta) {
        float moveSpeed = 3f * delta;
        float rotSpeed = 2f * delta;

        // Handle touch input
        handleTouch();

        // Gerak dari joystick kiri
        if (moveJoyActive) {
            float dx = moveJoyCurrent.x - moveJoyCenter.x;
            float dy = moveJoyCurrent.y - moveJoyCenter.y;
            float len = (float) Math.sqrt(dx*dx + dy*dy);
            if (len > 20) {
                float nx = dx / len;
                float ny = dy / len;
                // Maju/mundur
                if (ny < -0.3f) {
                    if (MAP[(int)(playerX + dirX * moveSpeed)][(int)playerY] == 0)
                        playerX += dirX * moveSpeed;
                    if (MAP[(int)playerX][(int)(playerY + dirY * moveSpeed)] == 0)
                        playerY += dirY * moveSpeed;
                }
                if (ny > 0.3f) {
                    if (MAP[(int)(playerX - dirX * moveSpeed)][(int)playerY] == 0)
                        playerX -= dirX * moveSpeed;
                    if (MAP[(int)playerX][(int)(playerY - dirY * moveSpeed)] == 0)
                        playerY -= dirY * moveSpeed;
                }
            }
        }

        // Rotasi dari joystick kanan
        if (rotJoyActive) {
            float dx = rotJoyCurrent.x - rotJoyCenter.x;
            if (Math.abs(dx) > 20) {
                float rot = dx > 0 ? -rotSpeed : rotSpeed;
                float oldDirX = dirX;
                dirX = dirX * MathUtils.cos(rot) - dirY * MathUtils.sin(rot);
                dirY = oldDirX * MathUtils.sin(rot) + dirY * MathUtils.cos(rot);
                float oldPlaneX = planeX;
                planeX = planeX * MathUtils.cos(rot) - planeY * MathUtils.sin(rot);
                planeY = oldPlaneX * MathUtils.sin(rot) + planeY * MathUtils.cos(rot);
            }
        }

        // Keyboard fallback
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (MAP[(int)(playerX + dirX * moveSpeed)][(int)playerY] == 0) playerX += dirX * moveSpeed;
            if (MAP[(int)playerX][(int)(playerY + dirY * moveSpeed)] == 0) playerY += dirY * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (MAP[(int)(playerX - dirX * moveSpeed)][(int)playerY] == 0) playerX -= dirX * moveSpeed;
            if (MAP[(int)playerX][(int)(playerY - dirY * moveSpeed)] == 0) playerY -= dirY * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            float oldDirX = dirX;
            dirX = dirX * MathUtils.cos(rotSpeed) - dirY * MathUtils.sin(rotSpeed);
            dirY = oldDirX * MathUtils.sin(rotSpeed) + dirY * MathUtils.cos(rotSpeed);
            float oldPlaneX = planeX;
            planeX = planeX * MathUtils.cos(rotSpeed) - planeY * MathUtils.sin(rotSpeed);
            planeY = oldPlaneX * MathUtils.sin(rotSpeed) + planeY * MathUtils.cos(rotSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            float oldDirX = dirX;
            dirX = dirX * MathUtils.cos(-rotSpeed) - dirY * MathUtils.sin(-rotSpeed);
            dirY = oldDirX * MathUtils.sin(-rotSpeed) + dirY * MathUtils.cos(-rotSpeed);
            float oldPlaneX = planeX;
            planeX = planeX * MathUtils.cos(-rotSpeed) - planeY * MathUtils.sin(-rotSpeed);
            planeY = oldPlaneX * MathUtils.sin(-rotSpeed) + planeY * MathUtils.cos(-rotSpeed);
        }
    }

    private void handleTouch() {
        int halfW = screenW / 2;

        for (int i = 0; i < 5; i++) {
            if (Gdx.input.isTouched(i)) {
                float tx = Gdx.input.getX(i);
                float ty = screenH - Gdx.input.getY(i);

                // Tombol tembak - pojok kanan bawah
                if (tx > screenW - 150 && ty < 150) {
                    shooting = true;
                    continue;
                }

                // Joystick kiri - setengah layar kiri
                if (tx < halfW) {
                    if (!moveJoyActive || moveJoyPointer == i) {
                        if (!moveJoyActive) {
                            moveJoyCenter.set(tx, ty);
                            moveJoyPointer = i;
                            moveJoyActive = true;
                        }
                        moveJoyCurrent.set(tx, ty);
                    }
                }
                // Joystick kanan - setengah layar kanan
                else if (tx > halfW && tx < screenW - 150) {
                    if (!rotJoyActive || rotJoyPointer == i) {
                        if (!rotJoyActive) {
                            rotJoyCenter.set(tx, ty);
                            rotJoyPointer = i;
                            rotJoyActive = true;
                        }
                        rotJoyCurrent.set(tx, ty);
                    }
                }
            } else {
                if (moveJoyPointer == i) { moveJoyActive = false; moveJoyPointer = -1; }
                if (rotJoyPointer == i) { rotJoyActive = false; rotJoyPointer = -1; }
            }
        }

        if (!Gdx.input.isTouched()) shooting = false;
    }

    public void render(SpriteBatch batch) {
        // Langit
        pixmap.setColor(0.2f, 0.2f, 0.4f, 1f);
        for (int y = 0; y < screenH/2; y++)
            pixmap.drawLine(0, y, screenW, y);

        // Lantai
        pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
        for (int y = screenH/2; y < screenH; y++)
            pixmap.drawLine(0, y, screenW, y);

        // Raycasting
        for (int x = 0; x < screenW; x++) {
            float cameraX = 2 * x / (float)screenW - 1;
            float rayDirX = dirX + planeX * cameraX;
            float rayDirY = dirY + planeY * cameraX;

            int mapX = (int)playerX;
            int mapY = (int)playerY;

            float deltaDistX = Math.abs(1 / rayDirX);
            float deltaDistY = Math.abs(1 / rayDirY);

            int stepX, stepY;
            float sideDistX, sideDistY;

            if (rayDirX < 0) { stepX = -1; sideDistX = (playerX - mapX) * deltaDistX; }
            else { stepX = 1; sideDistX = (mapX + 1f - playerX) * deltaDistX; }
            if (rayDirY < 0) { stepY = -1; sideDistY = (playerY - mapY) * deltaDistY; }
            else { stepY = 1; sideDistY = (mapY + 1f - playerY) * deltaDistY; }

            boolean hit = false;
            int side = 0;

            while (!hit) {
                if (sideDistX < sideDistY) { sideDistX += deltaDistX; mapX += stepX; side = 0; }
                else { sideDistY += deltaDistY; mapY += stepY; side = 1; }
                if (mapX >= 0 && mapX < MAP.length && mapY >= 0 && mapY < MAP[0].length) {
                    if (MAP[mapX][mapY] > 0) hit = true;
                } else break;
            }

            float perpWallDist = (side == 0) ?
                (mapX - playerX + (1 - stepX) / 2f) / rayDirX :
                (mapY - playerY + (1 - stepY) / 2f) / rayDirY;

            int lineHeight = (int)(screenH / perpWallDist);
            int drawStart = Math.max(0, -lineHeight / 2 + screenH / 2);
            int drawEnd = Math.min(screenH - 1, lineHeight / 2 + screenH / 2);

            if (side == 0) pixmap.setColor(0.8f, 0.2f, 0.2f, 1f);
            else pixmap.setColor(0.5f, 0.1f, 0.1f, 1f);
            pixmap.drawLine(x, drawStart, x, drawEnd);
        }

        // Crosshair
        pixmap.setColor(Color.WHITE);
        int cx = screenW/2, cy = screenH/2;
        pixmap.drawLine(cx-15, cy, cx+15, cy);
        pixmap.drawLine(cx, cy-15, cx, cy+15);

        // Gambar joystick kiri
        if (moveJoyActive) {
            drawCircle(pixmap, (int)moveJoyCenter.x, (int)moveJoyCenter.y, 60, 0.5f, 0.5f, 0.5f);
            drawCircle(pixmap, (int)moveJoyCurrent.x, (int)moveJoyCurrent.y, 30, 0.8f, 0.8f, 0.8f);
        } else {
            drawCircle(pixmap, 150, 150, 60, 0.3f, 0.3f, 0.3f);
        }

        // Gambar joystick kanan
        if (rotJoyActive) {
            drawCircle(pixmap, (int)rotJoyCenter.x, (int)rotJoyCenter.y, 60, 0.5f, 0.5f, 0.5f);
            drawCircle(pixmap, (int)rotJoyCurrent.x, (int)rotJoyCurrent.y, 30, 0.8f, 0.8f, 0.8f);
        } else {
            drawCircle(pixmap, screenW - 300, 150, 60, 0.3f, 0.3f, 0.3f);
        }

        // Tombol tembak
        if (shooting) pixmap.setColor(1f, 0.3f, 0.3f, 0.8f);
        else pixmap.setColor(0.8f, 0.2f, 0.2f, 0.6f);
        drawCircle(pixmap, screenW - 80, 80, 60, shooting ? 1f : 0.8f, 0.2f, 0.2f);

        if (screenTexture != null) screenTexture.dispose();
        screenTexture = new Texture(pixmap);

        batch.begin();
        batch.draw(screenTexture, 0, 0, screenW, screenH);
        batch.end();
    }

    private void drawCircle(Pixmap pm, int cx, int cy, int r, float red, float green, float blue) {
        pm.setColor(red, green, blue, 0.6f);
        for (int angle = 0; angle < 360; angle += 5) {
            int x = (int)(cx + r * MathUtils.cosDeg(angle));
            int y = (int)(cy + r * MathUtils.sinDeg(angle));
            if (x >= 0 && x < screenW && y >= 0 && y < screenH)
                pm.drawPixel(x, y);
        }
    }

    public int getHealth() { return health; }

    public void dispose() {
        pixmap.dispose();
        if (screenTexture != null) screenTexture.dispose();
    }
}
