package net.e175.klaus.config;

import java.util.NoSuchElementException;

/**
 * Value implements {@link ConfigValue} and holds values read from config,
 * even nonexistent ones (similar to a Null Object).
 * <p/>
 * This class is immutable and thus thread-safe.
 */
public final class Value implements ConfigValue {

    private final boolean exists;
    private final String internalValue;
    private final Location fromLocation;

    Value(final String internalValue, final Location fromLocation) {
        this.exists = internalValue != null;
        this.internalValue = internalValue;
        this.fromLocation = fromLocation != null ? fromLocation : Location.getNonexistentLocation();
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override
    public String asString() {
        if (exists) {
            return internalValue;
        } else {
            throw new NoSuchElementException("value does not exist");
        }
    }

    @Override
    public String asString(final String orDefault) {
        return exists ? asString() : orDefault;
    }

    @Override
    public double asDouble() {
        return Double.valueOf(asString());
    }

    @Override
    public double asDouble(final double orDefault) {
        return exists ? asDouble() : orDefault;
    }

    @Override
    public long asLong() {
        return Long.valueOf(asString());
    }

    @Override
    public long asLong(final long orDefault) {
        return exists ? asLong() : orDefault;
    }

    @Override
    public boolean isTrue() {
        return "true".equalsIgnoreCase(asString()) || "yes".equalsIgnoreCase(asString());
    }

    @Override
    public boolean isTrue(boolean orDefault) {
        return exists ? isTrue() : orDefault;
    }

    @Override
    public Location loadedFrom() {
        return fromLocation;
    }

    @Override
    public String toString() {
        return "Value{" + "exists=" + exists + ", internalValue=" + internalValue + ", fromLocation=" + fromLocation + '}';
    }


}
