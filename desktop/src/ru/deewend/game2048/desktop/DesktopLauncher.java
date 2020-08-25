package ru.deewend.game2048.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.deewend.game2048.Game2048;
import ru.deewend.game2048.IntValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public final class DesktopLauncher {
	private static final String lengthOfTheGameFieldSide = "lengthOfTheGameFieldSide";
	private static final String winningValue = "winningValue";

	private static final String viewReadMe =
			"Please view README.txt before editing game.properties file!";

	public static void main(final String[] args) {
		initValues();

		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final int lengthOfTheWindowSide = IntValues.INSTANCE.get(lengthOfTheGameFieldSide) * 180;

		config.width = lengthOfTheWindowSide;
		config.height = lengthOfTheWindowSide;
		config.resizable = false;
		config.title = (int) Math.pow(2, IntValues.INSTANCE.get(winningValue))
				+ " | Score: 0";
		config.vSyncEnabled = true;

		new LwjglApplication(new Game2048(), config);
	}

	private static void initValues() {
		final File gameDir = new File("2048");

		if (!(gameDir.exists() && gameDir.isDirectory()) && !gameDir.mkdir())
			throw new RuntimeException("Unable to create the game directory!");

		final Properties properties = new Properties();
		final File propertiesFile =
				new File(gameDir.getPath() + File.separator + "game.properties");

		if (!propertiesFile.exists()) {
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
			IntValues.INSTANCE.lock();

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
				IntValues.INSTANCE.add(lengthOfTheGameFieldSide, 3);
				IntValues.INSTANCE.add(winningValue, 10);

				break;
			}

			case "IMPOSSIBLE": {
				IntValues.INSTANCE.add(lengthOfTheGameFieldSide, 4);
				IntValues.INSTANCE.add(winningValue, 17);

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

		IntValues.INSTANCE.lock();
	}

	private static void putDefaultValues() {
		IntValues.INSTANCE.add(lengthOfTheGameFieldSide, 4);
		IntValues.INSTANCE.add(winningValue, 11);
	}

	private static void put(final Properties properties, final String key) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(key);

		try {
			int value = Integer.parseInt(properties.getProperty(key));

			if (key.equals(winningValue)) {
				if (!(value >= 1024 && value <= 131072 && (value & -value) == value))
					throw new RuntimeException("Illegal value for \"" + key + "\" key! " +
							viewReadMe);

				value = (int) (Math.log(value) / Math.log(2));
			}

			IntValues.INSTANCE.add(key, value);
		} catch (final NumberFormatException e) {
			if (e.getMessage() != null && e.getMessage().equals("null"))
				throw new RuntimeException("Couldn't find the value for \"" + key + "\" key!");

			throw new RuntimeException("The value for \"" + key + "\" key must be an integer!");
		}
	}
}