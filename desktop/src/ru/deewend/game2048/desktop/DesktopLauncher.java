package ru.deewend.game2048.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.deewend.game2048.Game2048;

public final class DesktopLauncher {
	public static void main(final String[] arg) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 720;
		config.height = 720;
		config.resizable = false;
		config.title = "2048";

		new LwjglApplication(new Game2048(), config);
	}
}
