package ru.deewend.game2048.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.deewend.game2048.Game2048;
import ru.deewend.game2048.Values;
import ru.deewend.game2048.HighScoreManager;

import java.io.*;
import java.util.*;

import static ru.deewend.game2048.Constants.*;

public final class DesktopLauncher {
	private static final String viewReadMe =
			"Please view README.txt before editing game.properties file!";

	public static void main(final String[] args) {
		initValues();

		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final int lengthOfTheWindowSide = Values.INSTANCE.getInt(lengthOfTheGameFieldSide) * 180;

		config.width = lengthOfTheWindowSide;
		config.height = lengthOfTheWindowSide;
		config.resizable = false;
		config.title = (int) Math.pow(2, Values.INSTANCE.getInt(winningValue))
				+ " | Score: 0 | High score: " + Values.INSTANCE.getLong(highScore);
		config.vSyncEnabled = true;

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

		new LwjglApplication(new Game2048(), config);
	}

	private static void initValues() {
		final File gameDir = new File("2048");

		if (!gameDir.isDirectory() && !gameDir.mkdir())
			throw new RuntimeException("Unable to create the game directory!");

		try {
			Values.INSTANCE.addLong(highScore, HighScoreManager.INSTANCE.readHighScore());
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}

		final Properties properties = new Properties();
		final File propertiesFile =
				new File(gameDir.getPath() + File.separator + "game.properties");

		if (!propertiesFile.isFile()) {
			properties.setProperty("mode", "classic");
			properties.setProperty(lengthOfTheGameFieldSide, "4");
			properties.setProperty(winningValue, "2048");

			try {
				if (!propertiesFile.createNewFile())
					throw new RuntimeException("Unable to create the game properties file!");

				final FileOutputStream outputStream = new FileOutputStream(propertiesFile);
				properties.store(outputStream,
						"If you see this file first time, " +
								"view README.txt to get more information"
				);

				outputStream.close();
			} catch (final Throwable t) { throw new RuntimeException(t); }

			putDefaultValues();
			Values.INSTANCE.lock();

			return;
		}

		try {
			final FileInputStream inputStream = new FileInputStream(propertiesFile);
			properties.load(inputStream);

			inputStream.close();
		} catch (final Throwable t) { throw new RuntimeException(t); }

		switch (properties.getProperty("mode")) {
			case "classic": {
				putDefaultValues();

				break;
			}

			case "1024complicated": {
				Values.INSTANCE.addInt(lengthOfTheGameFieldSide, 3);
				Values.INSTANCE.addInt(winningValue, 10);

				break;
			}

			case "IMPOSSIBLE": {
				Values.INSTANCE.addInt(lengthOfTheGameFieldSide, 4);
				Values.INSTANCE.addInt(winningValue, 17);

				break;
			}

			case "custom": {
				put(properties, lengthOfTheGameFieldSide);
				put(properties, winningValue);

				break;
			}

			default: {
				throw new RuntimeException("Unknown value for \"mode\" key! " + viewReadMe);
			}
		}

		Values.INSTANCE.lock();
	}

	private static void putDefaultValues() {
		Values.INSTANCE.addInt(lengthOfTheGameFieldSide, 4);
		Values.INSTANCE.addInt(winningValue, 11);
	}

	private static void put(final Properties properties, final String key) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(key);

		try {
			int value = Integer.parseInt(properties.getProperty(key));

			{
				final String errorMessage = "Illegal value for \"" + key + "\" key! " + viewReadMe;

				if (key.equals(winningValue)) {
					if (!(value >= 1024 && value <= 131072 && (value & -value) == value))
						throw new RuntimeException(errorMessage);

					value = (int) (Math.log(value) / Math.log(2));
				} else if (key.equals(lengthOfTheGameFieldSide) && !(value >= 3 && value <= 256))
					throw new RuntimeException(errorMessage);
			}

			Values.INSTANCE.addInt(key, value);
		} catch (final NumberFormatException e) {
			if (e.getMessage() != null && e.getMessage().equals("null"))
				throw new RuntimeException("Couldn't find the value for \"" + key + "\" key!");

			throw new RuntimeException("The value for \"" + key + "\" key must be an integer!");
		}
	}
}