package net.e175.klaus.config;

/**
 * A Config builder creates and initializes {@link Config} objects.
 */
public interface ConfigBuilder {

    /**
     * Loads configuration data from whichever configuration locations were
     * defined before.
     *
     * @return Config object
     * @throws IllegalStateException if no configuration location could be accessed.
     */
    Config load();

}
