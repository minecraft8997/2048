package ru.deewend.game2048;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Objects;

import static ru.deewend.game2048.Logic.DURATION;

public final class Game2048 extends Game implements InputProcessor {
    public static final int lengthOfTheGameFieldSide;
    public static final int winningValue;
    public static final boolean IKnowThatTheCreatorOfThisGameIsDeewend;

    private static final int realWinningValue;
    static long highScore;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private long currentScore;

    static {
        lengthOfTheGameFieldSide = Values.INSTANCE.getInt(Constants.lengthOfTheGameFieldSide);
        winningValue = Values.INSTANCE.getInt(Constants.winningValue);

        {
            final int IKnowThatTheCreatorOfThisGameIsDeewendTmp =
                    Values.INSTANCE.getInt(Constants.IKnowThatTheCreatorOfThisGameIsDeewend);

            if (IKnowThatTheCreatorOfThisGameIsDeewendTmp < 0 || IKnowThatTheCreatorOfThisGameIsDeewendTmp > 1)
                throw new RuntimeException("Invalid value of \"IKnowThatTheCreatorOfThisGameIsDeewend\" property. " +
                        "Probably it's a bug :(");

            IKnowThatTheCreatorOfThisGameIsDeewend = IKnowThatTheCreatorOfThisGameIsDeewendTmp == 1;
        }

        highScore = Values.INSTANCE.getLong(Constants.highScore);

        Values.INSTANCE.dispose();

        realWinningValue = (int) Math.pow(2, winningValue);
        if (lengthOfTheGameFieldSide == 3 && winningValue > 10)
            throw new RuntimeException("You can't play " + realWinningValue + " in 3 x 3, " +
                    "because reaching this tile is impossible!");
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        TextureManager.INSTANCE.init();
        Logic.INSTANCE.init();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        final int[][] currentGameField = Logic.INSTANCE.getField();
        final Pair<int[], Float> newlyAddedTile = Logic.INSTANCE.getNewlyAddedTile();
        final boolean gameOver = Logic.INSTANCE.gameOver();

        {
            final long score = Logic.INSTANCE.score;

            if (currentScore != score) {
                currentScore = score;

                Gdx.graphics.setTitle(
                        realWinningValue +
                                " | " +
                                "Score: " + score + (score > highScore ? " (!)" : "") + " | " +
                                "High score: " + highScore
                );
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (gameOver)
            setTransparency(batch, 0.3f);

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
                            (newlyAddedTile.getSecond() / DURATION));
                    newlyAddedTile.setSecond(newlyAddedTile.getSecond() + Gdx.graphics.getDeltaTime());
                }

                batch.draw(tile,
                        j * tile.getHeight(),
                        (currentGameField.length - i - 1) * tile.getHeight()
                );

                setTransparency(batch, currentBatchTransparency);
            }

        batch.end();

        {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);

            final int lengthOfTile = TextureManager.INSTANCE.getLengthOfTile();
            final int a = 0;
            final int b = currentGameField.length * lengthOfTile;
            int xCache;
            int yCache;

            for (int i = 1; i < currentGameField.length; ++i) {
                shapeRenderer.line(a, (yCache = i * lengthOfTile), b, yCache);
                shapeRenderer.line((xCache = i * lengthOfTile), b, xCache, a);
            }

            shapeRenderer.end();
        }

        setTransparency(batch, 1f);

        if (!(gameOver || !IKnowThatTheCreatorOfThisGameIsDeewend))
            return;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (gameOver) {
            final Texture title = Logic.INSTANCE.won() ?
                    TextureManager.INSTANCE.getYouWonTexture() :
                    TextureManager.INSTANCE.getGameOverTexture();
            textureIsNonNull(title, null);

            final Texture pressEnterToStartANewGameTexture =
                    TextureManager.INSTANCE.getPressEnterToStartANewGameTexture();
            textureIsNonNull(pressEnterToStartANewGameTexture, null);

            batch.draw(title,
                    (Gdx.graphics.getWidth() / 2f) - (title.getWidth() / 2f),
                    (Gdx.graphics.getHeight() / 2f) - (title.getHeight() / 2f)
            );

            batch.draw(pressEnterToStartANewGameTexture, 0f, 0f);
        }

        if (!IKnowThatTheCreatorOfThisGameIsDeewend) {
            final Texture author = TextureManager.INSTANCE.getAuthor();
            textureIsNonNull(author, null);

            batch.draw(author, 0f, Gdx.graphics.getHeight() - author.getHeight());
        }

        batch.end();
    }

    @Override
    public void resize(final int width, final int height) {
        final int requiredLengthOfWindowSide =
                Logic.INSTANCE.getField().length * TextureManager.INSTANCE.getLengthOfTile();
        if (width != requiredLengthOfWindowSide || height != requiredLengthOfWindowSide)
            throw new RuntimeException("Illegal width/height!");

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2f, height / 2f, 0f);
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

    private void textureIsNonNull(final Texture texture, final Integer valueOfTile) {
        if (texture == null)
            throw new IllegalStateException(valueOfTile == null ?
                    "Couldn't find the required texture!" :
                    "Couldn't find the texture for a tile, which value is " +
                            (int) Math.pow(2, valueOfTile) + "!"
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