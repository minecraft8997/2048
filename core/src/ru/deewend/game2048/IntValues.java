package ru.deewend.game2048;

import java.util.HashMap;
import java.util.Objects;

public enum IntValues {
    INSTANCE;

    private volatile HashMap<String, Integer> data = new HashMap<>();
    private volatile boolean locked = false;

    public synchronized void add(final String key, final int value) {
        Objects.requireNonNull(key);

        if (data == null) throw new IllegalAccessError(
                "Attempted to add a new entry to the disposed IntValues!"
        );

        if (locked) throw new IllegalAccessError(
                "Attempted to add a new entry to the locked IntValues!"
        );

        data.put(key, value);
    }

    public synchronized void lock() {
        if (locked) throw new IllegalAccessError(
                "Attempted to lock the IntValues, which is already locked!"
        );

        locked = true;
    }

    public synchronized int get(final String key) {
        Objects.requireNonNull(key);

        if (data == null) throw new IllegalAccessError(
                "Attempted to get a value from the disposed IntValues!"
        );

        return data.get(key);
    }

    synchronized void dispose() {
        if (data == null) throw new IllegalAccessError(
                "Attempted to dispose the IntValues, which was already disposed!"
        );

        if (!locked) throw new IllegalAccessError(
                "Attempted to dispose the not locked IntValues!"
        );

        data.clear();
        data = null;
    }
}