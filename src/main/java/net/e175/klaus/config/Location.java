package net.e175.klaus.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Location describes a source of configuration data, such as a
 * specific file with properties.
 */
public abstract class Location {

    private static final Location NONEXISTENT_LOCATION = new NonexistentLocation("nonexistent");

    private final String location;
    private String resolvedLocation;

    Location(final String location) {
        this.location = location;
    }

    protected abstract InputStream getInputStreamAndSetResolvedLocation() throws IOException;

    Properties load() throws IOException {
        try (InputStream input = getInputStreamAndSetResolvedLocation()) {
            return loadFromStream(input);
        }
    }

    private Properties loadFromStream(final InputStream inputStream) throws IOException {
        try (InputStreamReader r = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Properties p = new Properties();
            p.load(r);
            return p;
        }
    }

    protected String getLocation() {
        return location;
    }

    protected void setResolvedLocation(final String resolvedLocation) {
        this.resolvedLocation = resolvedLocation;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + location + " -> " + (resolvedLocation != null ? resolvedLocation : "unresolved");
    }

    static Location getNonexistentLocation() {
        return NONEXISTENT_LOCATION;
    }

    private static final class NonexistentLocation extends Location {

        private NonexistentLocation(final String location) {
            super(location);
        }

        @Override
        protected InputStream getInputStreamAndSetResolvedLocation() throws IOException {
            throw new IllegalStateException("cannot load from nonexisting location");
        }
    }

}
