package org.embulk.config;


import com.google.common.base.Optional;
import org.embulk.EmbulkTestRuntime;
import org.embulk.spi.time.TimestampParser;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestConfigSource {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    private ConfigSource config;

    private static interface TypeFields extends Task {
        @Config("boolean")
        public boolean getBoolean();

        @Config("double")
        public double getDouble();

        @Config("int")
        public int getInt();

        @Config("long")
        public long getLong();

        @Config("string")
        public String getString();
    }

    private static interface OptionalFields extends Task {
        @Config("guava_optional")
        @ConfigDefault("null")
        public Optional<String> getGuavaOptional();

        @Config("java_util_optional")
        @ConfigDefault("null")
        public java.util.Optional<String> getJavaUtilOptional();
    }

    private static interface DuplicationParent extends Task {
        @Config("duplicated_number")
        public int getInteger();
    }

    private static interface Duplicated extends TestConfigSource.DuplicationParent {
        @Config("duplicated_number")
        public String getString();

        @Config("duplicated_number")
        public double getDouble();
    }

    // getDefaultTimeZone() with org.joda.time.DateTimeZone is deprecated, but intentionally tested.
    @SuppressWarnings("deprecation")
    private static interface DuplicatedDateTimeZone extends Task , TimestampParser.Task {
        @Config("default_timezone")
        @ConfigDefault("\"America/Los_Angeles\"")
        public DateTimeZone getDefaultTimeZone();

        @Config("dummy_value")
        public String getDummyValue();
    }

    @Test
    public void testSetGet() {
        config.set("boolean", true);
        config.set("int", 3);
        config.set("double", 0.2);
        config.set("long", Long.MAX_VALUE);
        config.set("string", "sf");
        Assert.assertEquals(true, ((boolean) (config.get(boolean.class, "boolean"))));
        Assert.assertEquals(3, ((int) (config.get(int.class, "int"))));
        Assert.assertEquals(0.2, ((double) (config.get(double.class, "double"))), 0.001);
        Assert.assertEquals(Long.MAX_VALUE, ((long) (config.get(long.class, "long"))));
        Assert.assertEquals("sf", config.get(String.class, "string"));
    }

    @Test
    public void testOptionalPresent() {
        config.set("guava_optional", "Guava");
        config.set("java_util_optional", "JavaUtil");
        final TestConfigSource.OptionalFields loaded = config.loadConfig(TestConfigSource.OptionalFields.class);
        Assert.assertTrue(loaded.getGuavaOptional().isPresent());
        Assert.assertEquals("Guava", loaded.getGuavaOptional().get());
        Assert.assertTrue(loaded.getJavaUtilOptional().isPresent());
        Assert.assertEquals("JavaUtil", loaded.getJavaUtilOptional().get());
    }

    @Test
    public void testOptionalAbsent() {
        final TestConfigSource.OptionalFields loaded = config.loadConfig(TestConfigSource.OptionalFields.class);
        Assert.assertFalse(loaded.getGuavaOptional().isPresent());
        Assert.assertFalse(loaded.getJavaUtilOptional().isPresent());
    }

    @Test
    public void testLoadConfig() {
        config.set("boolean", true);
        config.set("int", 3);
        config.set("double", 0.2);
        config.set("long", Long.MAX_VALUE);
        config.set("string", "sf");
        TestConfigSource.TypeFields task = config.loadConfig(TestConfigSource.TypeFields.class);
        Assert.assertEquals(true, task.getBoolean());
        Assert.assertEquals(3, task.getInt());
        Assert.assertEquals(0.2, task.getDouble(), 0.001);
        Assert.assertEquals(Long.MAX_VALUE, task.getLong());
        Assert.assertEquals("sf", task.getString());
    }

    @Test
    public void testDuplicatedConfigName() {
        config.set("duplicated_number", "1034");
        TestConfigSource.Duplicated task = config.loadConfig(TestConfigSource.Duplicated.class);
        Assert.assertEquals(1034, task.getInteger());
        Assert.assertEquals("1034", task.getString());
        Assert.assertEquals(1034.0, task.getDouble(), 1.0E-6);
    }

    @Test
    public void testDuplicatedDateTimeZone() {
        config.set("default_timezone", "Asia/Tokyo");
        config.set("default_timestamp_format", "%Y");
        config.set("dummy_value", "foobar");
        TestConfigSource.DuplicatedDateTimeZone task = config.loadConfig(TestConfigSource.DuplicatedDateTimeZone.class);
        Assert.assertEquals("Asia/Tokyo", getDefaultTimeZoneId());
        Assert.assertEquals(org.joda.time.DateTimeZone.forID("Asia/Tokyo"), task.getDefaultTimeZone());
        Assert.assertEquals("%Y", getDefaultTimestampFormat());
        Assert.assertEquals("1970-01-01", getDefaultDate());
        Assert.assertEquals("foobar", task.getDummyValue());
    }

    @Test
    public void testDuplicatedDateTimeZoneWithDefault() {
        config.set("default_timestamp_format", "%Y");
        config.set("dummy_value", "foobar");
        TestConfigSource.DuplicatedDateTimeZone task = config.loadConfig(TestConfigSource.DuplicatedDateTimeZone.class);
        Assert.assertEquals("UTC", getDefaultTimeZoneId());
        Assert.assertEquals(org.joda.time.DateTimeZone.forID("America/Los_Angeles"), task.getDefaultTimeZone());
        Assert.assertEquals("%Y", getDefaultTimestampFormat());
        Assert.assertEquals("1970-01-01", getDefaultDate());
        Assert.assertEquals("foobar", task.getDummyValue());
    }

    private static interface ValidateFields extends Task {
        @Config("valid")
        public String getValid();
    }

    @Test
    public void testValidatePasses() {
        config.set("valid", "data");
        TestConfigSource.ValidateFields task = config.loadConfig(TestConfigSource.ValidateFields.class);
        validate();
        Assert.assertEquals("data", task.getValid());
    }

    @Test(expected = ConfigException.class)
    public void testDefaultValueValidateFails() {
        TestConfigSource.ValidateFields task = config.loadConfig(TestConfigSource.ValidateFields.class);
        validate();
    }

    // TODO test Min, Max, and other validations
    private static interface SimpleFields extends Task {
        @Config("type")
        public String getType();
    }

    @Test
    public void testFromJson() {
        String json = "{\"type\":\"test\"}";
        // TODO
    }
}

