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

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        engine = new RaycastEngine();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(Gdx.graphics.getDeltaTime());
        engine.render(batch);

        batch.begin();
        font.draw(batch, "CS-DOOM", 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "HP: " + engine.getHealth(), 10, 30);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        engine.dispose();
    }
}
