package ru.deewend.game2048;

import java.util.Objects;

enum Direction {
    LEFT(field -> { // movable
        line(field, true);
    }, (field, real) -> { // fixable
        fixLines(field, real, true);
    }),
    RIGHT(field -> { // movable
        line(field, false);
    }, (field, real) -> { // fixable
        fixLines(field, real, false);
    }),
    UP(field -> { // movable
        column(field, true);
    }, (field, real) -> { // fixable
        fixColumns(field, real, true);
    }),
    DOWN(field -> { // movable
        column(field, false);
    }, (field, real) -> { // fixable
        fixColumns(field, real, false);
    });

    private final Movable movable;
    private final Fixable fixable;

    Direction(final Movable movable, final Fixable fixable) {
        Objects.requireNonNull(movable);
        Objects.requireNonNull(fixable);

        this.movable = movable;
        this.fixable = fixable;
    }

    // accepted directions: LEFT, RIGHT
    private static void line(final int[][] field, final boolean left) {
        if (left)
            for (int i = 0; i < field.length; ++i)
                global:
                        while (!shouldStopL(field[i]))
                            for (int j = 0; j < field[i].length; ++j) {
                                if (field[i][j] == 0) {
                                    swap(field, i, j, i,
                                            indexOfNearestNonZeroValueFromTheStartOfLine(field[i], j)
                                    );

                                    continue global;
                                }
                            }
        else
            for (int i = 0; i < field.length; ++i)
                global:
                        while (!shouldStopR(field[i]))
                            for (int j = field[i].length - 1; j > 0; --j) {
                                if (field[i][j] == 0) {
                                    swap(field, i, j, i,
                                            indexOfNearestNonZeroValueFromTheEndOfLine(field[i], j)
                                    );

                                    continue global;
                                }
                            }
    }

    // right
    private static int indexOfNearestNonZeroValueFromTheEndOfLine(final int[] line, int i) {
        for (; i >= 0; --i)
            if (line[i] != 0) return i;

        return -1;
    }

    // right
    private static boolean shouldStopR(final int[] line) {
        boolean currentValueMustBeNonZero = false;

        for (final int e : line) {
            if (!currentValueMustBeNonZero && e != 0) {
                currentValueMustBeNonZero = true;
                continue;
            }

            if (currentValueMustBeNonZero && e == 0)
                return false;
        }

        return true;
    }

    // left
    private static int indexOfNearestNonZeroValueFromTheStartOfLine(final int[] line, int i) {
        for (; i < line.length; ++i)
            if (line[i] != 0) return i;

        return -1;
    }

    // left
    private static boolean shouldStopL(final int[] line) {
        boolean currentValueMustBeZero = false;

        for (final int e : line) {
            if (!currentValueMustBeZero && e == 0) {
                currentValueMustBeZero = true;
                continue;
            }

            if (currentValueMustBeZero && e != 0)
                return false;
        }

        return true;
    }

    // accepted directions: UP, DOWN
    private static void column(final int[][] field, final boolean up) {
        transform(field);
        line(field, !up);
        retrieve(field);
    }

    // this method rotates the matrix by 90 degrees in clockwise direction
    private static void transform(final int[][] field) {
        final int n = field.length;

        // Transpose the matrix
        for (int i = 0; i < n; i++)
            for (int j = 0; j < i; j++) {
                final int temp = field[i][j];

                field[i][j] = field[j][i];
                field[j][i] = temp;
            }

        // swap columns
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n / 2; j++) {
                final int temp = field[i][j];

                field[i][j] = field[i][n - j - 1];
                field[i][n - j - 1] = temp;
            }
    }

    // and this method rotates the matrix by 90 degrees in anti-clockwise direction
    private static void retrieve(final int[][] field) {
        final int n = field.length;

        for (int x = 0; x < n / 2; x++) {
            for (@SuppressWarnings("SuspiciousNameCombination") int y = x; y < n - x - 1; y++) {
                final int temp = field[x][y];

                field[x][y] = field[y][n - 1 - x];
                field[y][n - 1 - x] = field[n - 1 - x][n - 1 - y];
                field[n - 1 - x][n - 1 - y] = field[n - 1 - y][x];
                field[n - 1 - y][x] = temp;
            }
        }
    }

    private static void swap(
            final int[][] field, final int i0, final int j0, final int i1, final int j1
    ) {
        final int temp = field[i0][j0];

        field[i0][j0] = field[i1][j1];
        field[i1][j1] = temp;
    }

    void move(final int[][] field) {
        movable.move(field);
    }

    private static void fixLines(final int[][] field, final boolean real, final boolean left) {
        for (int i = 0; i < field.length; ++i)
            if (left)
                for (int j = 0; j < field[i].length - 1; ++j) {
                    final int valueOfTile = field[i][j];

                    if (valueOfTile > 0 && valueOfTile == field[i][j + 1]) {
                        field[i][j]++;
                        if (real) Logic.INSTANCE.score += ((long) Math.pow(2, field[i][j + 1])) * 2L;
                        field[i][j + 1] = 0;

                        LEFT.move(field);
                    }
                }
            else
                for (int j = field[i].length - 1; j > 0; --j) {
                    final int valueOfTile = field[i][j];

                    if (valueOfTile > 0 && valueOfTile == field[i][j - 1]) {
                        field[i][j]++;
                        if (real) Logic.INSTANCE.score += (((long) Math.pow(2, field[i][j - 1])) * 2L);
                        field[i][j - 1] = 0;

                        RIGHT.move(field);
                    }
                }
    }

    private static void fixColumns(final int[][] field, final boolean real, final boolean up) {
        transform(field);
        fixLines(field, real, !up);
        retrieve(field);
    }

    void fix(final int[][] field, final boolean real) {
        fixable.fix(field, real);
    }
}