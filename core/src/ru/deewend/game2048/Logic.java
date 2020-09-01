package ru.deewend.game2048;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static ru.deewend.game2048.Game2048.lengthOfTheGameFieldSide;
import static ru.deewend.game2048.Game2048.winningValue;

enum Logic {
    INSTANCE;

    private final SecureRandom random = new SecureRandom();

    long score = 0L;
    private boolean gameOver = false; // LibGDX is single-threaded, so "volatile" keyword isn't needed
    private boolean won = false;

    private final int[][] field = new int[lengthOfTheGameFieldSide][lengthOfTheGameFieldSide];

    public static final float DURATION = 0.7f;
    private Pair<int[], Float> newlyAddedTile;

    private static int[][] clone(final int[][] field) {
        return Arrays.stream(field).map(int[]::clone).toArray(int[][]::new);
    }

    void init() {
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
    void reInit() {
        for (final int[] e : field)
            Arrays.fill(e, 0);

        if (score > Game2048.highScore) Game2048.highScore = score;

        score = 0L;
        gameOver = false;
        won = false;
        newlyAddedTile = null;

        init();
    }

    void makeMove(final Direction direction) {
        Objects.requireNonNull(direction);

        final int[][] before = clone(field);

        direction.move(field);
        direction.fix(field, true);

        if (Arrays.deepEquals(field, before)) // that was not a move, nothing was changed!
            return;

        newlyAddedTile = null;

        placeNewTileIfPossible();

        try {
            if (score > Game2048.highScore && HighScoreManager.INSTANCE.readHighScore() < score)
                HighScoreManager.INSTANCE.storeHighScore(score);
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }

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

    private void placeNewTileIfPossible() {
        final ArrayList<int[]> availableIndexes = new ArrayList<>();

        for (int i = 0; i < field.length; ++i)
            for (int j = 0; j < field[i].length; ++j)
                if (field[i][j] == 0) availableIndexes.add(new int[]{i, j});

        if (availableIndexes.size() > 0) {
            final int[] randomPosition = availableIndexes.get(random.nextInt(availableIndexes.size()));
            field[randomPosition[0]][randomPosition[1]]
                    = random.nextInt(10) == 0 ? 2 : 1;

            newlyAddedTile = new Pair<>(randomPosition, 0.0f);
        }
    }

    private boolean checkPlayerWon() {
        for (final int[] l : field)
            for (final int e : l)
                if (e >= winningValue)
                    return true;

        return false;
    }

    private boolean isPlayerAbleToMakeOneMoreMove() {
        for (final Direction direction : Direction.values()) {
            final int[][] fieldCopy = clone(field);
            final int[][] oneMoreFieldCopy = clone(fieldCopy);

            direction.move(fieldCopy);
            direction.fix(fieldCopy, false);

            if (!Arrays.deepEquals(fieldCopy, oneMoreFieldCopy))
                return true;
        }

        return false;
    }

    int[][] getField() {
        return clone(field);
    }

    Pair<int[], Float> getNewlyAddedTile() {
        final Pair<int[], Float> snapshot = newlyAddedTile;

        if (snapshot != null && snapshot.getSecond() >= DURATION) {
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