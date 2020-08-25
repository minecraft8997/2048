package ru.deewend.game2048;

import com.badlogic.gdx.graphics.Texture;

enum TextureManager {
    INSTANCE;

    private final Texture[] texturesOfTiles = new Texture[17];
    private volatile Texture gameOverTexture;
    private volatile Texture youWonTexture;
    private volatile boolean initialized;

    synchronized void init() {
        if (initialized) return;

        {
            int tileLength = -1;

            for (int i = 0; i < texturesOfTiles.length; ++i) {
                final Texture currentTexture = new Texture((i + 1) + ".png");
                if (currentTexture.getHeight() != currentTexture.getWidth())
                    throw new IllegalStateException("Texture's height and width must be equal!");

                if (i == 0) tileLength = currentTexture.getHeight();
                else if (currentTexture.getHeight() != tileLength)
                    throw new IllegalStateException(
                            "Texture's (" + ((i + 1) + ".png") + ") length isn't equal to " + tileLength
                    );

                texturesOfTiles[i] = currentTexture;
            }
        }

        gameOverTexture = new Texture("game_over.png");
        youWonTexture = new Texture("you_won.png");

        initialized = true;
    }

    Texture get(final int power) {
        if (power < 1 || power > 11) return null;

        synchronized (this) {
            return texturesOfTiles[power - 1];
        }
    }

    Texture getGameOverTexture() {
        return gameOverTexture;
    }

    Texture getYouWonTexture() {
        return youWonTexture;
    }

    synchronized void dispose() {
        if (!initialized) return;

        for (int i = 0; i < texturesOfTiles.length; ++i) {
            texturesOfTiles[i].dispose();
            texturesOfTiles[i] = null;
        }

        gameOverTexture.dispose();
        gameOverTexture = null;

        youWonTexture.dispose();
        youWonTexture = null;

        initialized = false;
    }
}
