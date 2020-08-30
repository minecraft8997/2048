package ru.deewend.game2048;

import com.badlogic.gdx.graphics.Texture;

enum TextureManager {
    INSTANCE;

    private final Texture[] texturesOfTiles = new Texture[17];
    private int lengthOfTile = -1;
    private Texture gameOverTexture;
    private Texture youWonTexture;
    private Texture pressEnterToStartANewGameTexture;
    private Texture author;
    private boolean initialized;

    void init() {
        if (initialized) return;

        {
            for (int i = 0; i < texturesOfTiles.length; ++i) {
                final Texture currentTexture = new Texture((i + 1) + ".png");
                if (currentTexture.getHeight() != currentTexture.getWidth())
                    throw new IllegalStateException("Texture's height and width must be equal!");

                if (i == 0) lengthOfTile = currentTexture.getHeight();
                else if (currentTexture.getHeight() != lengthOfTile)
                    throw new IllegalStateException(
                            "Texture's (" + ((i + 1) + ".png") + ") length isn't equal to " + lengthOfTile
                    );

                texturesOfTiles[i] = currentTexture;
            }
        }

        gameOverTexture = new Texture("game_over.png");
        youWonTexture = new Texture("you_won.png");
        pressEnterToStartANewGameTexture =
                new Texture("press_enter_to_start_a_new_game.png");
        author = new Texture("author.png");

        initialized = true;
    }

    Texture get(final int power) {
        if (power < 1 || power > texturesOfTiles.length) return null;

        return texturesOfTiles[power - 1];
    }

    int getLengthOfTile() {
        return lengthOfTile;
    }

    Texture getGameOverTexture() {
        return gameOverTexture;
    }

    Texture getYouWonTexture() {
        return youWonTexture;
    }

    Texture getPressEnterToStartANewGameTexture() {
        return pressEnterToStartANewGameTexture;
    }

    Texture getAuthor() {
        return author;
    }

    void dispose() {
        if (!initialized) return;

        for (int i = 0; i < texturesOfTiles.length; ++i) {
            texturesOfTiles[i].dispose();
            texturesOfTiles[i] = null;
        }

        lengthOfTile = -1;

        gameOverTexture.dispose();
        gameOverTexture = null;

        youWonTexture.dispose();
        youWonTexture = null;

        pressEnterToStartANewGameTexture.dispose();
        pressEnterToStartANewGameTexture = null;

        author.dispose();
        author = null;

        initialized = false;
    }
}