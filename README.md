config
======

A tiny library (just a few hundred LOC) to simplify reading configuration properties from several locations. It tries to be unobtrusive, to work without surprises, and to be thread-safe. This is not a huge "configuration framework", but more or less a wrapper around Java's Properties class that shortens some common tasks and provides what I consider saner default behaviour.

```java
import static net.e175.klaus.config.PropertiesConfigBuilder.*:

Config config = defaultFromClassloader("myapp.properties")
		.overrideFromFilesystem("/etc/myapp.properties")
		.load();


String hostName = config.key("hostName").asString();
double frequency = config.key("frequency").asDouble();
```

The Config builder handles null values without choking up, so you can safely do this:
```java
Config config = defaultFromClassloader("myapp.properties")
		.overrideFromFilesystem("/etc/myapp/properties")
		.overrideFromFilesystem(System.getProperty("myapp.propertyfile.location"))
		.load();
```

The load() method will only throw an exception if it cannot load properties from //any// of the given locations. The config object is immutable: once built, it will not change. If you want to reload properties, you need to explicitly create a new object with the PropertiesConfigBuilder.

The ConfigValue object returned from Config.key() is //never// null. You can use it to explicitly check if a value was found at all:

```java

String value = config.key("someKey").asString(); // this throws NoSuchElementException if not found

if(!config.key("someKey").exists()) { // this allows you to check first
	System.err.println("no configuration value found for someKey");
}
```

In addition, you can find out where a value was loaded from (this is also safe for nonexistent values):

```java
System.out.println( config.key("someKey").loadedFrom() );
```

Differing from the usual (and hopelessly outdated) convention of Latin-1, configuration files are read as UTF-8. Backslash-u encoding is still possible though:

```
unicodeKey  = \u0440\u0443\u0441\u0441\u043a\u0438\u0439 \u044f\u0437\u044b\u043a
\u0440\u0443\u0441\u0441\u043a\u0438\u0439 = язык
```

Requirements and Dependencies
-----------------------------

* requires Java SE 7 
* depends on slf4j API (http://www.slf4j.org/) for logging (only at DEBUG and TRACE level, so it's usually silent)

Licence
-------

* MIT License (http://www.opensource.org/licenses/mit-license.php)
