package ru.deewend.game2048;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class Game2048 extends Game {
	private SpriteBatch batch;
	private TextureManager textureManager;

	@Override
	public void create() {
		batch = new SpriteBatch();
		textureManager = new TextureManager();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.end();

		try {
			Thread.sleep(100L);
		} catch (final InterruptedException e) {
			e.printStackTrace();
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		textureManager.dispose();
	}
}
