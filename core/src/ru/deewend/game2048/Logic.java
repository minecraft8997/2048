package ru.deewend.game2048;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static ru.deewend.game2048.Game2048.lengthOfTheGameFieldSide;
import static ru.deewend.game2048.Game2048.winningValue;

enum Logic {
    INSTANCE;

    private final SecureRandom random = new SecureRandom();

    private final AtomicLong score = new AtomicLong();
    private volatile boolean gameOver = false;
    private volatile boolean won = false;

    private final int[][] field = new int[lengthOfTheGameFieldSide][lengthOfTheGameFieldSide];

    public static final int MAX_TRANSPARENCY = 1200;
    private volatile Pair<int[], Integer> newlyAddedTile;

    // this method requires an external synchronization!
    private static int[][] clone(final int[][] field) {
        return Arrays.stream(field).map(int[]::clone).toArray(int[][]::new);
    }

    synchronized void init() {
        final int fieldSize = lengthOfTheGameFieldSide * lengthOfTheGameFieldSide;

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

        field[firstCellPos / lengthOfTheGameFieldSide][firstCellPos % lengthOfTheGameFieldSide] =
                (random.nextInt(10) == 0 ? 2 : 1);
        field[secondCellPos / lengthOfTheGameFieldSide][secondCellPos % lengthOfTheGameFieldSide] = 1;
    }

    // calling this method is required when player decides to start a new game.
    synchronized void reInit() {
        for (final int[] e : field)
            Arrays.fill(e, 0);

        score.set(0L);
        gameOver = false;
        won = false;

        init();
    }

    void makeMove(final Direction direction) {
        Objects.requireNonNull(direction);

        synchronized (this) {
            final int[][] before = clone(field);

            direction.move(field);
            direction.fix(field, score);

            if (Arrays.deepEquals(field, before)) // that was not a move, nothing was changed!
                return;

            newlyAddedTile = null;

            placeNewTileIfPossible();

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

            newlyAddedTile = new Pair<>(randomPosition, 0);
        }
    }

    // this method requires an external synchronization!
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
            final int[][] fieldCopy = clone(field);
            final int[][] oneMoreFieldCopy = clone(fieldCopy);

            direction.move(fieldCopy);
            direction.fix(fieldCopy, null);

            if (!Arrays.deepEquals(fieldCopy, oneMoreFieldCopy))
                return true;
        }

        return false;
    }

    long getScore() {
        return score.get();
    }

    synchronized int[][] getField() {
        return clone(field);
    }

    synchronized Pair<int[], Integer> getNewlyAddedTile() {
        Pair<int[], Integer> snapshot = newlyAddedTile;

        if (snapshot != null && snapshot.getSecond() > MAX_TRANSPARENCY) {
            newlyAddedTile = null;

            return null;
        }

        return snapshot;
    }

    boolean gameOver() {
        return gameOver;
    }

    boolean won() {
        return won;
    }
}