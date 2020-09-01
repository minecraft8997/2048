package ru.deewend.game2048.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.deewend.game2048.Game2048;
import ru.deewend.game2048.Values;
import ru.deewend.game2048.HighScoreManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static ru.deewend.game2048.Constants.*;

public final class DesktopLauncher {
	private static final String readMe = "Hello, dear player!\n" +
			"\n" +
			"Thank you for playing this game! Here you will see how to customize it " +
			"by editing game.properties file & more.\n" +
			"\n" +
			"Q: What values are acceptable in the \"mode\" property?\n" +
			"A:\n" +
			"1) \"classic\". You need to reach the 2048 tile. Size of the game field is 4 x 4.\n" +
			"2) \"1024complicated\". You need to reach the 1024 tile. Size of the game field is 3 x 3.\n" +
			"3) \"IMPOSSIBLE\". You need to reach the 131072 tile. Size of the game field is 4 x 4. " +
			"Seems to be very easy. :)\n" +
			"4) \"custom\". See below about this mode.\n" +
			"\n" +
			"Q: Can I play 4096 in 5 x 5?\n" +
			"A: Yes! Follow these steps:\n" +
			"1. Set value of \"mode\" property to \"custom\".\n" +
			"2. Set value of \"lengthOfTheGameFieldSide\" property to 5.\n" +
			"3. Set value of \"winningValue\" property to 4096.\n" +
			"4. Save changes. Profit!\n" +
			"\n" +
			"Q: How can I play 8192 in 4 x 4?\n" +
			"A:\n" +
			"mode=custom\n" +
			"lengthOfTheGameFieldSide=4\n" +
			"winningValue=8192\n" +
			"\n" +
			"Q: How can I play 1024 in 6 x 6?\n" +
			"A:\n" +
			"mode=custom\n" +
			"lengthOfTheGameFieldSide=6\n" +
			"winningValue=1024\n" +
			"\n" +
			"Q: How can I play 1024 in 3 x 3?\n" +
			"A:\n" +
			"mode=1024complicated\n" +
			"OR:\n" +
			"mode=custom\n" +
			"lengthOfTheGameFieldSide=3\n" +
			"winningValue=1024\n" +
			"\n" +
			"Hope you understood how it works :)\n" +
			"\n" +
			"PLEASE NOTE:\n" +
			"1) If value of \"mode\" property is not equal to \"custom\", values of " +
			"\"lengthOfTheGameFieldSide\" and \"winningValue\" properties will be ignored.\n" +
			"2) Min value of \"lengthOfTheGameFieldSide\" property is 3, max is 32.\n" +
			"3) Min value of \"winningValue\" property is 1024, max is 131072.\n" +
			"WARNING #1: providing a value, which is not a power of 2, will produce a game crash! " +
			"So acceptable values are 1024, 2048, 4096, 8192, 16384, 32768, 65536 and 131072.\n" +
			"WARNING #2: if value of \"lengthOfTheGameFieldSide\" property is 3, " +
			"the ONLY acceptable value for \"winningValue\" property is 1024, " +
			"because you can't reach the 2048 tile (or greater) in 3 x 3! " +
			"Providing another value will also produce a game crash.\n" +
			"\n" +
			"Let's continue Q&A.\n" +
			"\n" +
			"Q: I wish to remove \"by deewend (Ivan Shubin)\" watermark, what should I do?\n" +
			"A: Set value of \"IKnowThatTheCreatorOfThisGameIsDeewend\" property to true.\n" +
			"\n" +
			"Q: How can I contact you?\n" +
			"A: If you found a bug, or you know how to improve the game, please " +
			"e-mail me at realdeewend@gmail.com. Thanks!\n" +
			"\n" +
			"Enjoy! :)";

	private static final String viewReadMe =
			"Please view ReadMe.txt before editing game.properties file!";

	public static void main(final String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

		initValues();

		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final int lengthOfTheWindowSide = Values.INSTANCE.getInt(lengthOfTheGameFieldSide) *
				getPreLengthOfTheTextureSide();

		config.width = lengthOfTheWindowSide;
		config.height = lengthOfTheWindowSide;
		config.resizable = false;
		config.title = (int) Math.pow(2, Values.INSTANCE.getInt(winningValue))
				+ " | Score: 0 | High score: " + Values.INSTANCE.getLong(highScore);
		config.vSyncEnabled = true;

		new LwjglApplication(new Game2048(), config);
	}

	private static int getPreLengthOfTheTextureSide() {
		try {
			return ImageIO.read(
					Objects.requireNonNull(DesktopLauncher.class.getClassLoader().getResource("1.png"),
							"Couldn't find the required texture!")
			).getWidth();
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static void initValues() {
		final File gameDir = new File("2048");

		if (!gameDir.isDirectory() && !gameDir.mkdir())
			throw new RuntimeException("Unable to create the game directory!");

		final File readMeFile = new File(gameDir.getPath() + File.separator + "ReadMe.txt");

		try {
			{
				boolean createdJustNow = false;

				if (!readMeFile.isFile() && (createdJustNow = true) && !readMeFile.createNewFile())
					throw new RuntimeException("Unable to create ReadMe.txt file!");

				if (createdJustNow) {
					final PrintWriter writer = new PrintWriter(readMeFile);
					writer.print(readMe);
					writer.close();
				}
			}

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
			properties.setProperty(IKnowThatTheCreatorOfThisGameIsDeewend, "false");

			try {
				if (!propertiesFile.createNewFile())
					throw new RuntimeException("Unable to create the game properties file!");

				final FileOutputStream outputStream = new FileOutputStream(propertiesFile);
				properties.store(outputStream,
						"If you see this file first time, " +
								"view ReadMe.txt to get more information"
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

		Values.INSTANCE.addInt(IKnowThatTheCreatorOfThisGameIsDeewend,
				Boolean.TRUE.toString().equals(properties.getProperty(IKnowThatTheCreatorOfThisGameIsDeewend)) ?
						1 : 0
		);

		Values.INSTANCE.lock();
	}

	private static void putDefaultValues() {
		Values.INSTANCE.addInt(lengthOfTheGameFieldSide, 4);
		Values.INSTANCE.addInt(winningValue, 11);
		Values.INSTANCE.addInt(IKnowThatTheCreatorOfThisGameIsDeewend, 0);
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
				} else if (key.equals(lengthOfTheGameFieldSide) && !(value >= 3 && value <= 32))
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