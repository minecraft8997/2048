package ru.deewend.game2048;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public enum Logic {
    INSTANCE;

    private final int fieldLength = 4;
    private final int winningValue = 11;

    private final Random random = new SecureRandom();

    private volatile boolean gameOver = false;
    private volatile boolean won = false;

    private final int[][] field = new int[fieldLength][fieldLength];
    private final int fieldSize = fieldLength * fieldLength;

    synchronized void init() {
        final int firstCellPos = random.nextInt(fieldSize);
        final int secondCellPos;

        {
            int tmpSecondCellPos;
            int attempts = 0;
            do {
                tmpSecondCellPos = random.nextInt(fieldSize);
            } while (tmpSecondCellPos == firstCellPos && ++attempts < 10);
            if (tmpSecondCellPos == firstCellPos) {
                if (firstCellPos > 0) tmpSecondCellPos = firstCellPos - 1;
                else tmpSecondCellPos = 1;
            }

            secondCellPos = tmpSecondCellPos;
        }

        field[firstCellPos / fieldLength][firstCellPos % fieldLength]
                = (random.nextInt(10) == 0 ? 2 : 1);
        field[secondCellPos / fieldLength][secondCellPos % fieldLength]
                = (random.nextInt(10) == 0 ? 2 : 1);
    }

    // calling this method is required when player decides to start a new game.
    synchronized void reInit() {
        for (final int[] e : field)
            Arrays.fill(e, 0);

        gameOver = false;
        won = false;

        init();
    }

    void makeMove(final Direction direction) { // todo make synchronized
        Objects.requireNonNull(direction);

        synchronized (this) {
            direction.move(field);
            fix(direction, field);

            if (checkPlayerWon()) {
                gameOver = true;
                won = true;

                return;
            }

            if (!isPlayerAbleToMakeOneMoreMove())
                gameOver = true;
        }
    }

    private boolean checkPlayerWon() {
        for (final int[] l : field)
            for (final int e : l)
                if (e >= winningValue)
                    return true;

        return false;
    }

    // this method requires an external synchronization!
    private boolean isPlayerAbleToMakeOneMoreMove() {
        for (final Direction direction : Direction.values()) {
            final int[][] fieldCopy = Arrays.stream(field).map(int[]::clone).toArray(int[][]::new);
            final int[][] oneMoreFieldCopy = Arrays.stream(fieldCopy).map(int[]::clone).toArray(int[][]::new);

            direction.move(fieldCopy);
            fix(direction, fieldCopy);

            if (!Arrays.deepEquals(fieldCopy, oneMoreFieldCopy))
                return true;
        }

        return false;
    }

    // this method requires an external synchronization!
    private void fix(final Direction direction, final int[][] field) {
        int[] previousLine = field[0];

        for (int i = 0; i < field.length; ++i) {
            if (i == 0) {
                if (direction == Direction.LEFT || direction == Direction.RIGHT)
                    lineFix(direction, previousLine);

                continue;
            }

            if (direction == Direction.LEFT || direction == Direction.RIGHT)
                lineFix(direction, field[i]);
            else
                for (int j = 0; j < field[i].length; ++j)
                    if (previousLine[j] == field[i][j]) {
                        previousLine[j]++;
                        field[i][j] = 0;
                        direction.move(field);
                    }

            previousLine = field[i];
        }
    }

    // this method requires an external synchronization!
    private void lineFix(final Direction direction, final int[] line) {
        for (int j = 0; j < line.length - 1; ++j)
            if (line[j] == line[j + 1]) {
                line[j + 1]++;
                line[j] = 0;

                direction.move(field);
            }
    }

    boolean isGameOver() {
        return gameOver;
    }

    boolean won() {
        return won;
    }
}