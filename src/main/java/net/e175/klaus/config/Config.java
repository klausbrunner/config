package net.e175.klaus.config;

/**
 * An interface for a class that provides {@link ConfigValue} objects.
 */
public interface Config {

    /**
     * Look up the value for a given key string.
     *
     * @param key
     * @return ConfigValue object (never null)
     */
    ConfigValue key(String key);

    /**
     * @return Timestamp when this object was created, as returned by {@link System#currentTimeMillis()}.
     */
    long getCreationTimeMillis();

}
