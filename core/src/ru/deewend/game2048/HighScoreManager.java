package ru.deewend.game2048;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public enum HighScoreManager {
    INSTANCE;

    private final String CURRENT_DATA_FILE_VERSION = "1";

    private String encrypt(final String str) {
        final StringBuilder encrypted = new StringBuilder();

        final char key = '"';
        for (int i = 0; i < str.length(); i++)
            encrypted.append((char) (str.charAt(i) ^ key));

        return encrypted.toString();
    }

    private String decrypt(final String encryptedStr) {
        return encrypt(encryptedStr);
    }

    public void storeHighScore(final long score) throws IOException {
        if (score < 0L) throw new IllegalArgumentException("Invalid score provided! (" + score + ")");

        final File data = new File("2048" + File.separator + "data.db");
        if (!data.isFile() && !data.createNewFile())
            throw new RuntimeException("Unable to create data file! Sorry :( " +
                    "But now your high score is equal to " + score + ". Congratulations! :)");

        final int minOutputLength = 50; // can be lower than (String.valueOf(Long.MAX_VALUE).length)!
        final int maxOutputLength = 195;

        final char[] output =
                new char[minOutputLength + (int) (Math.random() * (maxOutputLength - minOutputLength))];
        final char[] digits = String.valueOf(score).toCharArray();

        int remaining = digits.length;
        for (int i = 0; i < output.length; ++i) {
            if (remaining > 0 && output.length - i - 1 <= remaining) {
                if (output.length - i - 1 < remaining)
                    throw new RuntimeException("Unable to save your high score. Probably it's a bug. Sorry :( " +
                            "But now your high score is equal to " + score + ". Congratulations! :)");

                output[i] = digits[digits.length - remaining--];
                continue;
            }

            if (remaining > 0 && (int) (Math.random() * (minOutputLength / digits.length)) == 0)
                output[i] = digits[digits.length - remaining--];
            else output[i] = (char) ('A' + (int) (Math.random() * 26));
        }

        final PrintWriter writer = new PrintWriter(data);
        writer.print(CURRENT_DATA_FILE_VERSION + " " + encrypt(new String(output)));
        writer.close();
    }

    // assuming that there is ./2048 directory!
    public long readHighScore() throws IOException {
        final File dataFile = new File("2048" + File.separator + "data.db");

        boolean createdJustNow = false;
        if (!dataFile.isFile() && (createdJustNow = true) && !dataFile.createNewFile())
            throw new RuntimeException("Unable to create data file!");

        if (createdJustNow) {
            storeHighScore(0L);

            return 0L;
        }

        final Scanner input = new Scanner(dataFile);
        final String[] data = input.nextLine().split(" ");
        if (data.length < 2 || !data[0].equals(CURRENT_DATA_FILE_VERSION))
            throw new RuntimeException("Unsupported data file version!");

        final String decryptedContents = decrypt(input.nextLine());
        input.close();

        final StringBuilder digits = new StringBuilder();
        for (final char e : decryptedContents.toCharArray())
            if (e >= '0' && e <= '9') digits.append(e);

        return Long.parseLong(digits.toString());
    }
}