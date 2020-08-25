package ru.deewend.game2048;

import java.util.Objects;

final class Pair<A, B> {
    private A first;
    private B second;

    Pair(final A first, final B second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        this.first = first;
        this.second = second;
    }

    A getFirst() {
        return first;
    }

    B getSecond() {
        return second;
    }

    void setFirst(final A first) {
        Objects.requireNonNull(first);

        this.first = first;
    }

    void setSecond(final B second) {
        Objects.requireNonNull(second);

        this.second = second;
    }
}