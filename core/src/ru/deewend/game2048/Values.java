package ru.deewend.game2048;

import java.util.HashMap;
import java.util.Objects;

public enum Values {
    INSTANCE;

    private volatile HashMap<String, Integer> ints = new HashMap<>();
    private volatile HashMap<String, Long> longs = new HashMap<>();

    private volatile boolean locked = false;

    public synchronized void addInt(final String key, final int value) {
        Objects.requireNonNull(key);

        if (ints == null) throw new IllegalAccessError(
                "Attempted to add a new entry to the disposed Values object!"
        );

        if (locked) throw new IllegalAccessError(
                "Attempted to add a new entry to the locked Values object!"
        );

        ints.put(key, value);
    }

    public synchronized void addLong(final String key, final long value) {
        Objects.requireNonNull(key);

        if (longs == null) throw new IllegalAccessError(
                "Attempted to add a new entry to the disposed Values object!"
        );

        if (locked) throw new IllegalAccessError(
                "Attempted to add a new entry to the locked Values object!"
        );

        longs.put(key, value);
    }

    public synchronized int getInt(final String key) {
        Objects.requireNonNull(key);

        if (ints == null) throw new IllegalAccessError(
                "Attempted to get a value from the disposed Values object!"
        );

        return ints.get(key);
    }

    public synchronized long getLong(final String key) {
        Objects.requireNonNull(key);

        if (longs == null) throw new IllegalAccessError(
                "Attempted to get a value from the disposed Values object!"
        );

        return longs.get(key);
    }

    public synchronized void lock() {
        if (locked) throw new IllegalAccessError(
                "Attempted to lock the Values object, which is already locked!"
        );

        locked = true;
    }

    synchronized void dispose() {
        if (ints == null || longs == null) throw new IllegalAccessError(
                "Attempted to dispose the Values object, which was already disposed!"
        );

        if (!locked) throw new IllegalAccessError(
                "Attempted to dispose the not locked Values object!"
        );

        ints.clear();
        longs.clear();

        ints = null;
        longs = null;
    }
}