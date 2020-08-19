package ru.deewend.game2048;

import com.badlogic.gdx.graphics.Texture;

public final class TextureManager {
    private final Texture[] textures = new Texture[11];

    public TextureManager() {
        for (int i = 0; i < textures.length; ++i)
            textures[i] = new Texture((i + 1) + ".png");
    }

    public Texture get(final int power) {
        if (power < 1 || power > 11) return null;

        synchronized (this) {
            return textures[power - 1];
        }
    }

    public synchronized void dispose() {
        for (int i = 0; i < textures.length; ++i) {
            textures[i].dispose();
            textures[i] = null;
        }
    }
}
