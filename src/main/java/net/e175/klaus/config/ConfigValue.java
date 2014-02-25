package net.e175.klaus.config;


/**
 * An interface for a class that holds values read from config,
 * even nonexistent ones (similar to a Null Object).
 */
public interface ConfigValue {

    /**
     * @return true if value is available, false if no value was found
     */
    boolean exists();

    /**
     * @return configuration value
     * @throws NoSuchElementException if no value is available
     * @see ConfigValue#exists()
     */
    String asString();

    /**
     * @param orDefault if no value is available, use this value
     * @return value from configuration or the passed default if none found
     */
    String asString(String orDefault);

    /**
     * @return configuration value as a double
     * @throws NoSuchElementException if no value is available
     * @see ConfigValue#exists()
     */
    double asDouble();

    /**
     * @param orDefault if no value is available, use this value
     * @return double value from configuration or the passed default if none found
     * @throws NumberFormatException if value is available but cannot be converted to double
     */
    double asDouble(double orDefault);

    /**
     * @return configuration value as a long
     * @throws NoSuchElementException if no value is available
     * @see ConfigValue#exists()
     */
    long asLong();

    /**
     * @param orDefault if no value is available, use this value
     * @return double value from configuration or the passed default if none found
     * @throws NumberFormatException if value is available but cannot be converted to long
     */
    long asLong(long orDefault);

    /**
     * @return true iff string value equals "true" or "yes" (case-insensitive)
     * @throws NoSuchElementException if no value is available
     */
    boolean isTrue();

    /**
     * @param orDefault if no value is available, use this value
     * @return true iff string value equals "true" or "yes" (case-insensitive)
     */
    boolean isTrue(boolean orDefault);

    /**
     * @return Location where this value (if available) was loaded from.
     */
    Location loadedFrom();
}
