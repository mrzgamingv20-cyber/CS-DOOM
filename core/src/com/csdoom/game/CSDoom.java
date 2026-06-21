package com.csdoom.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class CSDoom extends ApplicationAdapter {
    SpriteBatch batch;
    BitmapFont font;
    RaycastEngine engine;
    boolean engineReady = false;

    @Override
    public void create() {
        try {
            batch = new SpriteBatch();
            font = new BitmapFont();
            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            engine = new RaycastEngine();
            engineReady = true;
        } catch (Exception e) {
            Gdx.app.log("CSDOOM", "Error: " + e.getMessage());
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!engineReady) {
            batch.begin();
            font.draw(batch, "Loading...", 100, Gdx.graphics.getHeight() / 2);
            batch.end();
            return;
        }

        try {
            engine.update(Gdx.graphics.getDeltaTime());
            engine.render(batch);

            batch.begin();
            font.draw(batch, "HP: " + engine.getHealth(), 10, Gdx.graphics.getHeight() - 10);
            batch.end();
        } catch (Exception e) {
            batch.begin();
            font.draw(batch, "Error: " + e.getMessage(), 10, Gdx.graphics.getHeight() / 2);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (engine != null) engine.dispose();
    }
}
