package ru.deewend.game2048;

import com.badlogic.gdx.graphics.Texture;

public enum TextureManager {
    INSTANCE;

    private final Texture[] textures = new Texture[11];

    synchronized void init() {
        int tileLength = -1;

        for (int i = 0; i < textures.length; ++i) {
            final Texture currentTexture = new Texture((i + 1) + ".png");
            if (currentTexture.getHeight() != currentTexture.getWidth())
                throw new IllegalStateException("Texture's height and width must be equal!");

            if (i == 0) tileLength = currentTexture.getHeight();
            else if (currentTexture.getHeight() != tileLength)
                throw new IllegalStateException(
                        "Texture's (" + ((i + 1) + ".png") + ") length isn't equal to " + tileLength
                );

            textures[i] = currentTexture;
        }
    }

    Texture get(final int power) {
        if (power < 1 || power > 11) return null;

        synchronized (this) {
            return textures[power - 1];
        }
    }

    synchronized void dispose() {
        for (int i = 0; i < textures.length; ++i) {
            textures[i].dispose();
            textures[i] = null;
        }
    }
}
