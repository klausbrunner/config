package net.e175.klaus.config;

import static net.e175.klaus.config.PropertiesConfigBuilder.defaultFromClassloader;
import static net.e175.klaus.config.PropertiesConfigBuilder.defaultFromFilesystem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PropertiesConfigTest {

	Config config;

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void acceptsDefaultLocation() {
		defaultFromClassloader("foo-does-not-exist");
		defaultFromFilesystem("/etc/foo/bar");
	}

	@Test
	public void acceptsNullDefaultLocation() {
		defaultFromClassloader(null);
		defaultFromFilesystem(null);
	}

	@Test(expected = IllegalStateException.class)
	public void throwsExceptionIfNoLocationReadable() {
		defaultFromClassloader("foo-does-not-exist").load();
	}

	@Test(expected = IllegalStateException.class)
	public void throwsExceptionIfNoLocationReadableOfMultiple() {
		defaultFromClassloader("foo-does-not-exist").overrideFromFilesystem(null).load();
	}

	@Test
	public void loadsFromClasspath1() {
		config = defaultFromClassloader("classpath1.properties").load();
	}

	@Test
	public void loadsFromClasspath1WithoutComplainingAboutSingleFailedLocation() {
		config = defaultFromClassloader("classpath1.properties").overrideFromClassloader("nonexistent.properteees").load();

		config = defaultFromClassloader("nonexistent.properteees").overrideFromClassloader("classpath1.properties").load();
	}

	@Test
	public void hasUsefulToString() {
		config = defaultFromClassloader("classpath1.properties").overrideFromClassloader("nonexistent.properteees").load();

		String toString = config.toString();

		assertTrue(toString.contains("classpath1.properties"));
		assertFalse(toString.contains("nonexistent.properteees"));
	}

	@Test
	public void getsAnyConfigValueEvenIfNotLoaded() {
		config = defaultFromClassloader("classpath1.properties").load();

		assertNotNull(config.key("nonexistent key"));
		assertNotNull(config.key(""));
		assertNotNull(config.key(null));
	}

	@Test
	public void getsStringConfigValueFromSimpleClasspathLocation() {
		config = defaultFromClassloader("classpath1.properties").load();

		assertEquals("value1", config.key("key1").asString());
	}

	@Test(expected = NoSuchElementException.class)
	public void throwsExceptionOnGettingUnknownValue() {
		config = defaultFromClassloader("classpath1.properties").load();

		config.key("keydoesnotexist").asString();
	}

	@Test(expected = NumberFormatException.class)
	public void throwsExceptionOnConvertingNonLong() {
		config = defaultFromClassloader("classpath1.properties").load();

		config.key("key1").asLong();
	}

	@Test(expected = NumberFormatException.class)
	public void throwsExceptionOnConvertingNonDouble() {
		config = defaultFromClassloader("classpath1.properties").load();

		config.key("key1").asDouble();
	}

	@Test
	public void getsStringConfigValueFromSimpleClasspathLocationWithDefault() {
		config = defaultFromClassloader("classpath1.properties").load();

		assertEquals("value1", config.key("key1").asString("default"));
		assertFalse(config.key("keydoesnotexist").exists());
		assertEquals("default", config.key("keydoesnotexist").asString("default"));
	}

	@Test
	public void configValueHasUsefulToString() {
		config = defaultFromClassloader("classpath1.properties").load();

		ConfigValue value = config.key("key1");
		assertTrue(value.toString().contains("value1"));
	}

	@Test
	public void getsLongConfigValueFromSimpleClasspathLocationWithDefault() {
		config = defaultFromClassloader("classpath1.properties").load();

		assertEquals(-30, config.key("longKey1").asLong(9999));
		assertFalse(config.key("keydoesnotexist").exists());
		assertEquals(9999, config.key("keydoesnotexist").asLong(9999));
	}
	
	@Test
	public void getsBooleanConfigValuesFromSimpleClasspathLocationWithDefault() {
		config = defaultFromClassloader("classpath1.properties").load();

		assertTrue(config.key("trueKey1").isTrue(false));
		assertTrue(config.key("trueKey2").isTrue());
		assertTrue(config.key("trueKey3").isTrue());
		
		assertFalse(config.key("falseKey1").isTrue(true));
		assertFalse(config.key("falseKey2").isTrue());		
		
		assertTrue(config.key("nonexistent-key").isTrue(true));
	}	

	@Test
	public void getsDoubleConfigValueFromSimpleClasspathLocationWithDefault() {
		config = defaultFromClassloader("classpath1.properties").load();

		assertEquals(-9.9E19, config.key("doubleKey2").asDouble(9999), 1E-9);
		assertFalse(config.key("keydoesnotexist").exists());
		assertEquals(9999.0, config.key("keydoesnotexist").asDouble(9999.0), 1E-9);
	}

	@Test
	public void knowsConfigValueLocationFromSimpleClasspathLocation() {
		config = defaultFromClassloader("classpath1.properties").load();

		ConfigValue cv = config.key("key1");

		assertTrue(cv.loadedFrom().toString().contains("classpath1.properties"));

		System.out.println(cv.loadedFrom());
	}

	@Test(expected = RuntimeException.class)
	public void returnsNonexistentLocationForNonexistentKey() throws Exception {
		config = defaultFromClassloader("classpath1.properties").load();

		ConfigValue cv = config.key("key-that-does-not-exist");

		cv.loadedFrom().load();
	}

	@Test(expected = IllegalArgumentException.class)
	public void doesNotAcceptNullLocations() {
		new PropertiesConfig(null);
	}

	@Test
	public void getsUnicodeStringConfigValueFromSimpleClasspathLocation() {
		config = defaultFromClassloader("classpath1.properties").load();

		assertEquals("русский язык", config.key("unicodeKey").asString());

		assertEquals("язык", config.key("русский").asString());
	}

	@Test
	public void getsStringConfigValuesFromTwoClasspathLocations() {
		config = defaultFromClassloader("classpath1.properties") //
				.overrideFromClassloader("classpath2.properties") //
				.load();

		System.out.println(config);

		assertEquals("value1-2", config.key("key1").asString("default"));
		assertEquals("value2-2", config.key("key2").asString("default"));
		assertEquals("value3", config.key("key3").asString("default"));
		assertEquals("value4-2", config.key("key4").asString("default"));
		assertEquals("default", config.key("key5").asString("default"));
	}

	@Test
	public void getsStringConfigValuesFromTwoClasspathAndOneFileLocation() throws Exception {
		String filename = createFilesystemProps();

		config = defaultFromClassloader("classpath1.properties") //
				.overrideFromClassloader("classpath2.properties") //
				.overrideFromFilesystem(filename).load();

		System.out.println(config);

		assertEquals("value1-3", config.key("key1").asString("default"));
		assertEquals("value2-2", config.key("key2").asString("default"));
		assertEquals("value3", config.key("key3").asString("default"));
		assertEquals("value4-2", config.key("key4").asString("default"));
		assertEquals("value6", config.key("key6").asString("default"));
		assertEquals("default", config.key("key5").asString("default"));
	}

	@Test
	public void getsStringConfigValuesFromOneFileLocationAndTwoClasspaths() throws Exception {
		String filename = createFilesystemProps();

		config = defaultFromFilesystem(filename) //
				.overrideFromClassloader("classpath1.properties") //
				.overrideFromClassloader("classpath2.properties").load();

		System.out.println(config);

		assertEquals("value1-2", config.key("key1").asString("default"));
		assertEquals("value2-2", config.key("key2").asString("default"));
		assertEquals("value3", config.key("key3").asString("default"));
		assertEquals("value4-2", config.key("key4").asString("default"));
		assertEquals("value6", config.key("key6").asString("default"));
		assertEquals("default", config.key("key5").asString("default"));
	}
	
	@Test
	public void knowsItsCreationTime() throws Exception {
		String filename = createFilesystemProps();

		final long before = System.currentTimeMillis();
		Thread.sleep(10);
		config = defaultFromFilesystem(filename).load();
		Thread.sleep(10);	
		final long after = System.currentTimeMillis();
		
		assertTrue(before <= config.getCreationTimeMillis() &&
				   config.getCreationTimeMillis() <= after);		
	}
	
	private String createFilesystemProps() throws Exception {
		File f = File.createTempFile("configtest", "properties");
		f.deleteOnExit();

		Writer w = new FileWriter(f);
		w.write("key6 = value6\nkey1  : value1-3\n");
		w.close();

		return f.getAbsolutePath();
	}

}
