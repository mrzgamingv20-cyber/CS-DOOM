package com.csdoom.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class RaycastEngine {
    // Map level 1 (0=kosong, 1=dinding)
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
        {1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // Posisi dan arah player
    private float playerX = 2.5f, playerY = 2.5f;
    private float dirX = 1f, dirY = 0f;
    private float planeX = 0f, planeY = 0.66f;

    private int health = 100;
    private int screenW, screenH;
    private Pixmap pixmap;
    private Texture screenTexture;

    public RaycastEngine() {
        screenW = Gdx.graphics.getWidth();
        screenH = Gdx.graphics.getHeight();
        pixmap = new Pixmap(screenW, screenH, Pixmap.Format.RGBA8888);
    }

    public void update(float delta) {
        float moveSpeed = 3f * delta;
        float rotSpeed = 2f * delta;

        // Input keyboard/touch
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (MAP[(int)(playerX + dirX * moveSpeed)][(int)playerY] == 0)
                playerX += dirX * moveSpeed;
            if (MAP[(int)playerX][(int)(playerY + dirY * moveSpeed)] == 0)
                playerY += dirY * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (MAP[(int)(playerX - dirX * moveSpeed)][(int)playerY] == 0)
                playerX -= dirX * moveSpeed;
            if (MAP[(int)playerX][(int)(playerY - dirY * moveSpeed)] == 0)
                playerY -= dirY * moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            float oldDirX = dirX;
            dirX = dirX * MathUtils.cos(rotSpeed) - dirY * MathUtils.sin(rotSpeed);
            dirY = oldDirX * MathUtils.sin(rotSpeed) + dirY * MathUtils.cos(rotSpeed);
            float oldPlaneX = planeX;
            planeX = planeX * MathUtils.cos(rotSpeed) - planeY * MathUtils.sin(rotSpeed);
            planeY = oldPlaneX * MathUtils.sin(rotSpeed) + planeY * MathUtils.cos(rotSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            float oldDirX = dirX;
            dirX = dirX * MathUtils.cos(-rotSpeed) - dirY * MathUtils.sin(-rotSpeed);
            dirY = oldDirX * MathUtils.sin(-rotSpeed) + dirY * MathUtils.cos(-rotSpeed);
            float oldPlaneX = planeX;
            planeX = planeX * MathUtils.cos(-rotSpeed) - planeY * MathUtils.sin(-rotSpeed);
            planeY = oldPlaneX * MathUtils.sin(-rotSpeed) + planeY * MathUtils.cos(-rotSpeed);
        }
    }

    public void render(SpriteBatch batch) {
        pixmap.setColor(0.1f, 0.1f, 0.3f, 1f);
        pixmap.fill();

        // Gambar lantai dan langit
        for (int y = screenH/2; y < screenH; y++) {
            pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
            pixmap.drawLine(0, y, screenW, y);
        }
        for (int y = 0; y < screenH/2; y++) {
            pixmap.setColor(0.2f, 0.2f, 0.4f, 1f);
            pixmap.drawLine(0, y, screenW, y);
        }

        // Raycasting - cast satu ray per kolom pixel
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

            // DDA algorithm
            while (!hit) {
                if (sideDistX < sideDistY) { sideDistX += deltaDistX; mapX += stepX; side = 0; }
                else { sideDistY += deltaDistY; mapY += stepY; side = 1; }
                if (mapX >= 0 && mapX < MAP.length && mapY >= 0 && mapY < MAP[0].length)
                    if (MAP[mapX][mapY] > 0) hit = true;
                else break;
            }

            float perpWallDist = (side == 0) ?
                (mapX - playerX + (1 - stepX) / 2f) / rayDirX :
                (mapY - playerY + (1 - stepY) / 2f) / rayDirY;

            int lineHeight = (int)(screenH / perpWallDist);
            int drawStart = Math.max(0, -lineHeight / 2 + screenH / 2);
            int drawEnd = Math.min(screenH - 1, lineHeight / 2 + screenH / 2);

            // Warna dinding
            if (side == 0) pixmap.setColor(0.8f, 0.2f, 0.2f, 1f);
            else pixmap.setColor(0.5f, 0.1f, 0.1f, 1f);

            pixmap.drawLine(x, drawStart, x, drawEnd);
        }

        // Crosshair
        pixmap.setColor(Color.WHITE);
        int cx = screenW/2, cy = screenH/2;
        pixmap.drawLine(cx-10, cy, cx+10, cy);
        pixmap.drawLine(cx, cy-10, cx, cy+10);

        if (screenTexture != null) screenTexture.dispose();
        screenTexture = new Texture(pixmap);

        batch.begin();
        batch.draw(screenTexture, 0, 0, screenW, screenH);
        batch.end();
    }

    public int getHealth() { return health; }

    public void dispose() {
        pixmap.dispose();
        if (screenTexture != null) screenTexture.dispose();
    }
}
