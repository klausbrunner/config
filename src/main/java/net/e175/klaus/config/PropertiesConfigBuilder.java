package net.e175.klaus.config;

import net.e175.klaus.config.PropertiesConfig.ContextClassloader;
import net.e175.klaus.config.PropertiesConfig.Filesystem;

import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of {@link ConfigBuilder} to create instances of {@link PropertiesConfig}.
 * Provides a fluent API to chain several configuration file locations together,
 * the first location providing defaults, the following ones overriding definitions.
 * <p/>
 * This class synchronizes access to its shared mutable state, and is thus
 * thread-safe.
 */
public final class PropertiesConfigBuilder implements ConfigBuilder {

    private final List<Location> locations = new LinkedList<>();

    private PropertiesConfigBuilder() {
    }

    @Override
    public synchronized Config load() {
        return new PropertiesConfig(locations);
    }

    /**
     * Create a new instance.
     *
     * @param filesystemPath path (including filename) to a properties file accessible on the filesystem.
     *                       May be null, in which case it is ignored.
     * @return PropertiesConfigBuilder instance
     */
    public static PropertiesConfigBuilder defaultFromFilesystem(final String filesystemPath) {
        PropertiesConfigBuilder instance = new PropertiesConfigBuilder();
        return instance.overrideFromFilesystem(filesystemPath);
    }

    /**
     * Create a new instance.
     *
     * @param classloaderPath path (including filename) to a properties file accessible via context classloader.
     *                        May be null, in which case it is ignored.
     * @return PropertiesConfigBuilder instance
     */
    public static PropertiesConfigBuilder defaultFromClassloader(final String classloaderPath) {
        PropertiesConfigBuilder instance = new PropertiesConfigBuilder();
        return instance.overrideFromClassloader(classloaderPath);
    }

    /**
     * Add an overriding location to an instance.
     *
     * @param filesystemPath path (including filename) to a properties file accessible on the filesystem.
     *                       May be null, in which case it is ignored.
     * @return PropertiesConfigBuilder instance
     */
    public PropertiesConfigBuilder overrideFromFilesystem(final String filesystemPath) {
        return filesystemPath != null ? appendLocation(new Filesystem(filesystemPath)) : this;
    }

    /**
     * Add an overriding location to an instance.
     *
     * @param classloaderPath path (including filename) to a properties file accessible via context classloader.
     *                        May be null, in which case it is ignored.
     * @return PropertiesConfigBuilder instance
     */
    public PropertiesConfigBuilder overrideFromClassloader(final String classloaderPath) {
        return classloaderPath != null ? appendLocation(new ContextClassloader(classloaderPath)) : this;
    }

    private synchronized PropertiesConfigBuilder appendLocation(final Location location) {
        this.locations.add(location);
        return this;
    }

}
