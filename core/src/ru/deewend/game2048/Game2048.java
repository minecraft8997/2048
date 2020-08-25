package ru.deewend.game2048;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Objects;

import static ru.deewend.game2048.Logic.MAX_TRANSPARENCY;

public final class Game2048 extends Game implements InputProcessor {
    public static final int lengthOfTheGameFieldSide;
    public static final int winningValue;
    private static final int realWinningValue;

    private SpriteBatch batch;
    private long currentScore;

    static {
        lengthOfTheGameFieldSide = IntValues.INSTANCE.get("lengthOfTheGameFieldSide");
        winningValue = IntValues.INSTANCE.get("winningValue");
        realWinningValue = (int) Math.pow(2, winningValue);

        IntValues.INSTANCE.dispose();
    }

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

        System.out.println(Gdx.graphics.getDeltaTime());
        final int[][] currentGameField = Logic.INSTANCE.getField();
        final Pair<int[], Integer> newlyAddedTile = Logic.INSTANCE.getNewlyAddedTile();
        final boolean gameOver = Logic.INSTANCE.gameOver();

        batch.begin();

        if (gameOver) setTransparency(batch, 0.3f);
        else {
            final long score = Logic.INSTANCE.getScore();
            if (currentScore != score) {
                currentScore = score;

                Gdx.graphics.setTitle(realWinningValue + " | Score: " + score);
            }
        }

        for (int i = 0; i < currentGameField.length; ++i)
            for (int j = 0; j < currentGameField[i].length; ++j) {
                final int valueOfTile = currentGameField[i][j];
                if (valueOfTile == 0) continue;

                final Texture tile = TextureManager.INSTANCE.get(valueOfTile);
                textureIsNonNull(tile, valueOfTile);

                final float currentBatchTransparency = getTransparency(batch);
                if (newlyAddedTile != null &&
                        i == newlyAddedTile.getFirst()[0] && j == newlyAddedTile.getFirst()[1]
                ) {
                    setTransparency(batch, currentBatchTransparency *
                            ((float) newlyAddedTile.getSecond() / MAX_TRANSPARENCY));

                    final double delta = round(Gdx.graphics.getDeltaTime(), 3);
                    newlyAddedTile.setSecond(newlyAddedTile.getSecond() + (int) (delta * 1000));
                }

                batch.draw(tile,
                        j * tile.getHeight(),
                        (currentGameField.length - i - 1) * tile.getHeight()
                );

                setTransparency(batch, currentBatchTransparency);
            }

        if (gameOver) {
            setTransparency(batch, 1f);

            final Texture title = Logic.INSTANCE.won() ?
                    TextureManager.INSTANCE.getYouWonTexture() :
                    TextureManager.INSTANCE.getGameOverTexture();
            textureIsNonNull(title, null);

            batch.draw(title,
                    (Gdx.graphics.getWidth() / 2f) - (title.getWidth() / 2f),
                    (Gdx.graphics.getHeight() / 2f) - (title.getHeight() / 2f)
            );
        }

        batch.end();
    }

    private static float getTransparency(final SpriteBatch batch) {
        Objects.requireNonNull(batch);

        return batch.getColor().a;
    }

    private static void setTransparency(final SpriteBatch batch, final float transparency) {
        Objects.requireNonNull(batch);

        if (transparency < 0f || transparency > 1f)
            throw new IllegalArgumentException();

        final Color currentColor = batch.getColor();
        batch.setColor(currentColor.r, currentColor.g, currentColor.b, transparency);
    }

    private static double round(double number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        return (double) (int) ((tmp - (int) tmp) >= 0.5 ? tmp + 1 : tmp) / pow;
    }

    private void textureIsNonNull(final Texture texture, final Integer valueOfTile) {
        if (texture == null)
            throw new IllegalStateException(valueOfTile == null ?
                    "Couldn't find the required texture!" :
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
        if (Logic.INSTANCE.gameOver()) {
            if (keycode == Input.Keys.ENTER) {
                Logic.INSTANCE.reInit();

                return true;
            }

            return false;
        }

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