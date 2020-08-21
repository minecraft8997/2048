package ru.deewend.game2048;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Arrays;

public final class Game2048 extends Game implements InputProcessor {
	private SpriteBatch batch;

	@Override
	public void create() {
		batch = new SpriteBatch();

		TextureManager.INSTANCE.init();
		Logic.INSTANCE.init();

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//System.out.println("ACTUAL:");
		final int[][] currentGameField = Logic.INSTANCE.getField();
		//for (int[] line : currentGameField)
		//	System.out.println(Arrays.toString(line));
		//System.out.println();

		batch.begin();

		for (int i = 0; i < currentGameField.length; ++i)
			for (int j = 0; j < currentGameField[i].length; ++j) {
				final int valueOfTile = currentGameField[i][j];
				if (valueOfTile == 0) continue;

				//System.out.println(currentGameField[0][0]);

				final Texture tile = TextureManager.INSTANCE.get(valueOfTile);
				textureIsNonNull(tile, valueOfTile);

				final int x = j * tile.getHeight();
				final int y = (currentGameField.length - i - 1) * tile.getHeight();

				batch.draw(tile, x, y);
			}

		/*
		for (int i = currentGameField.length - 1; i >= 0; --i)
			for (int j = 0; j < currentGameField[i].length; ++j) {
				final int valueOfTile = currentGameField[i][j];
				if (valueOfTile == 0) continue;

				final Texture tile = TextureManager.INSTANCE.get(valueOfTile);
				textureIsNonNull(tile, valueOfTile);

				final int x = j * tile.getHeight();
				final int y = (currentGameField.length - i - 1) * tile.getHeight();

				batch.draw(tile, x, y);
			}
		 */

		batch.end();
	}

	private void textureIsNonNull(final Texture texture, final int valueOfTile) {
		if (texture == null)
			throw new IllegalStateException(
					"Couldn't find the texture for a tile, which value is "
							+ Math.pow(2, valueOfTile) + "!"
			);
	}
	
	@Override
	public void dispose() {
		batch.dispose();

		TextureManager.INSTANCE.dispose();
	}

	@Override
	public boolean keyDown(final int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT: {
				Logic.INSTANCE.makeMove(Direction.LEFT);
				return true;
			}

			case Input.Keys.RIGHT: {
				Logic.INSTANCE.makeMove(Direction.RIGHT);
				return true;
			}

			case Input.Keys.UP: {
				Logic.INSTANCE.makeMove(Direction.UP);
				return true;
			}

			case Input.Keys.DOWN: {
				Logic.INSTANCE.makeMove(Direction.DOWN);
				return true;
			}

			default: {
				return false;
			}
		}
	}

	@Override
	public boolean keyUp(final int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(final int amount) {
		return false;
	}
}
