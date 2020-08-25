package ru.deewend.game2048;

import java.util.concurrent.atomic.AtomicLong;

interface Fixable {
    void fix(int[][] field, AtomicLong score);
}