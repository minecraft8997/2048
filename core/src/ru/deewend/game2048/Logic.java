package ru.deewend.game2048;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public enum Logic {
    INSTANCE;

    private final int fieldLength = 4;

    private final Random random = new SecureRandom();

    private volatile boolean gameOver = false;
    private volatile boolean won = false;

    private final int[][] field = new int[fieldLength][fieldLength];

    // this method requires an external synchronization!
    private static int[][] clone(final int[][] field) {
        return Arrays.stream(field).map(int[]::clone).toArray(int[][]::new);
    }

    synchronized void init() {
        final int fieldSize = fieldLength * fieldLength;

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

        System.out.println("CURRENT:");
        for (int[] line : field)
            System.out.println(Arrays.toString(line));
        System.out.println();
    }

    // calling this method is required when player decides to start a new game.
    synchronized void reInit() {
        for (final int[] e : field)
            Arrays.fill(e, 0);

        gameOver = false;
        won = false;

        init();
    }

    void makeMove(final Direction direction) {
        Objects.requireNonNull(direction);

        synchronized (this) {
            final int[][] before = clone(field);

            direction.move(field);
            direction.fix(field);

            if (Arrays.deepEquals(field, before)) // that was not a move, nothing was changed!
                return;

            placeNewTileIfPossible();

            System.out.println("CURRENT:");
            for (int[] line : field)
                System.out.println(Arrays.toString(line));
            System.out.println();

            if (checkPlayerWon()) {
                gameOver = true;
                won = true;

                return;
            }

            if (!isPlayerAbleToMakeOneMoreMove()) {
                gameOver = true;
                if (won) won = false;
            }
        }
    }

    // this method requires an external synchronization!
    private void placeNewTileIfPossible() {
        final ArrayList<int[]> availableIndexes = new ArrayList<>();

        for (int i = 0; i < field.length; ++i)
            for (int j = 0; j < field[i].length; ++j)
                if (field[i][j] == 0) availableIndexes.add(new int[] {i, j});

        if (availableIndexes.size() > 0) {
            final int[] randomPosition = availableIndexes.get(random.nextInt(availableIndexes.size()));
            field[randomPosition[0]][randomPosition[1]] = 1;
        }
    }

    // this method requires an external synchronization!
    private boolean checkPlayerWon() {
        for (final int[] l : field)
            for (final int e : l)
                if (e >= 11)
                    return true;

        return false;
    }

    // this method requires an external synchronization!
    private boolean isPlayerAbleToMakeOneMoreMove() {
        for (final Direction direction : Direction.values()) {
            final int[][] fieldCopy = clone(field);
            final int[][] oneMoreFieldCopy = clone(fieldCopy);

            direction.move(fieldCopy);
            direction.fix(fieldCopy);

            if (!Arrays.deepEquals(fieldCopy, oneMoreFieldCopy))
                return true;
        }

        return false;
    }

    /*
    // this method requires an external synchronization!
    private void fix(final Direction direction, final int[][] field) {

        /*
        int[] previousLine = field[0];

        for (int i = 0; i < field.length; ++i) {
            if (i == 0) {
                if (direction == Direction.LEFT || direction == Direction.RIGHT)
                    lineFix(field, direction, previousLine);

                continue;
            }

            if (direction == Direction.LEFT || direction == Direction.RIGHT)
                lineFix(field, direction, field[i]);
            else
                for (int j = 0; j < field[i].length; ++j)
                    if (previousLine[j] == field[i][j]) {
                        previousLine[j]++;
                        field[i][j] = 0;
                        direction.move(field);
                    }

            previousLine = field[i];
        }
         *//*
    }

    /*
    // this method requires an external synchronization!
    private void lineFix(final int[][] field, final Direction direction, final int[] line) {
        for (int j = 0; j < line.length - 1; ++j)
            if (line[j] == line[j + 1]) {
                line[j + 1]++;
                line[j] = 0;

                direction.move(field);
            }
    }
     */

    synchronized int[][] getField() {
        return clone(field);
    }

    boolean isGameOver() {
        return gameOver;
    }

    boolean won() {
        return won;
    }
}