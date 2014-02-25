package net.e175.klaus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * An implementation of {@link Config} based on Java Property files encoded
 * in UTF-8.
 * <p/>
 * This class is immutable and thus thread-safe.
 */
public final class PropertiesConfig implements Config {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesConfig.class);

    private final Map<Location, Properties> locationProperties;

    private final long creationTimeMillis;

    PropertiesConfig(final List<Location> locations) {
        if (locations == null) {
            throw new IllegalArgumentException("locations must not be null");
        }

        locationProperties = new LinkedHashMap<>(locations.size());
        load(locations);
        creationTimeMillis = System.currentTimeMillis();
    }

    private void load(final List<Location> locations) {
        boolean success = false;
        for (final Location loc : locations) {
            try {
                Properties loaded = loc.load();
                locationProperties.put(loc, loaded);
                success = true;
                LOG.debug("loaded config from {}", loc);
            } catch (IOException ex) {
                LOG.debug("could not load config from location " + loc, ex);
            }
        }

        if (!success) {
            throw new IllegalStateException("unable to load configuration data from any location (" + locations + ")");
        }
    }

    public Map<Location, Properties> getLocationProperties() {
        return locationProperties;
    }

    @Override
    public ConfigValue key(final String key) {
        String lastValue = null;
        Location lastLocation = null;

        if (key != null) {
            for (final Entry<Location, Properties> locProp : locationProperties.entrySet()) {
                Properties props = locProp.getValue();
                String value = props.getProperty(key);
                Location location = locProp.getKey();

                if (value != null) {
                    lastLocation = location;
                    lastValue = value;
                    LOG.trace("key {} found in location {}", key, location);
                } else {
                    LOG.trace("key {} not found in location {}", key, location);
                }
            }
        }

        return new Value(lastValue, lastLocation);
    }

    @Override
    public long getCreationTimeMillis() {
        return creationTimeMillis;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (final Entry<Location, Properties> locProp : locationProperties.entrySet()) {
            builder.append(locProp.getKey());
            builder.append("(").append(locProp.getValue()).append(")");
            builder.append("; ");
        }

        return builder.toString();
    }

    static final class Filesystem extends Location {

        Filesystem(final String location) {
            super(location);
        }

        @Override
        protected InputStream getInputStreamAndSetResolvedLocation() throws IOException {
            File f = new File(getLocation());
            setResolvedLocation(f.getAbsolutePath());
            return new FileInputStream(f);
        }
    }

    static final class ContextClassloader extends Location {

        ContextClassloader(final String location) {
            super(location);
        }

        @Override
        protected InputStream getInputStreamAndSetResolvedLocation() throws IOException {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL foundUrl = loader.getResource(getLocation());
            if (foundUrl != null) {
                setResolvedLocation(foundUrl.toString());
                return foundUrl.openStream();
            } else {
                throw new IOException("could not resolve classloader location " + getLocation());
            }
        }
    }
}
