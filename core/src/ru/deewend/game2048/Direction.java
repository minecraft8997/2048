package ru.deewend.game2048;

import java.util.Objects;

public enum Direction {
    LEFT(field -> {
        line(field, true);
    }),
    RIGHT(field -> {
        line(field, false);
    }),
    UP(field -> {
        column(field, true);
    }),
    DOWN(field -> {
        column(field, false);
    });

    private final Movable movable;

    Direction(final Movable movable) {
        Objects.requireNonNull(movable);

        this.movable = movable;
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
        if (up) {
            for (int i = 0; i < field.length; ++i) {
                global:
                while (!shouldStopU(field, i)) {
                    for (int j = 0; j < field.length; ++j) { // j = cell index
                        if (field[j][i] == 0) {
                            swap(field, j, i, j, indexOfNearestNonZeroValueFromTheEndOfColumn(field, i, j));
                            continue global;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < field.length; ++i) {
                global:
                while (!shouldStopD(field, i)) {
                    for (int j = field.length - 1; j > 0; --j) { // j = cell index
                        if (field[j][i] == 0) {
                            swap(field, j, i, j, indexOfNearestNonZeroValueFromTheStartOfColumn(field, i, j));
                            continue global;
                        }
                    }
                }
            }
        }
    }

    // up
    private static int indexOfNearestNonZeroValueFromTheEndOfColumn(
            final int[][] field, final int columnI, int i
    ) {
        for (; i >= 0; --i)
            if (field[i][columnI] != 0)
                return i;

        return -1;
    }

    // up
    private static boolean shouldStopU(final int[][] field, final int columnI) {
        boolean currentValueMustBeZero = false;

        for (final int[] line : field) {
            if (!currentValueMustBeZero && line[columnI] == 0) {
                currentValueMustBeZero = true;
                continue;
            }

            if (currentValueMustBeZero && line[columnI] != 0)
                return false;
        }

        return true;
    }

    // down
    private static int indexOfNearestNonZeroValueFromTheStartOfColumn(
            final int[][] field, final int columnI, int i
    ) {
        for (; i < field.length; ++i)
            if (field[i][columnI] != 0)
                return i;

        return -1;
    }

    // down
    private static boolean shouldStopD(final int[][] field, final int columnI) {
        boolean currentValueMustBeNonZero = false;

        for (final int[] line : field) {
            if (!currentValueMustBeNonZero && line[columnI] != 0) {
                currentValueMustBeNonZero = true;
                continue;
            }

            if (currentValueMustBeNonZero && line[columnI] == 0)
                return false;
        }

        return true;
    }

    private static void swap(final int[][] field, final int i0, final int j0, final int i1, final int j1) {
        final int temp = field[i0][j0];

        field[i0][j0] = field[i1][j1];
        field[i1][j1] = temp;
    }

    public void move(final int[][] field) {
        movable.move(field);
    }
}