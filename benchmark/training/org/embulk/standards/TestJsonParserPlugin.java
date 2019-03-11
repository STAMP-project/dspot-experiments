package org.embulk.standards;


import JsonParserPlugin.InvalidEscapeStringPolicy.PASSTHROUGH;
import JsonParserPlugin.PluginTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigSource;
import org.embulk.spi.DataException;
import org.embulk.spi.Exec;
import org.embulk.spi.TestPageBuilderReader.MockPageOutput;
import org.embulk.spi.json.JsonParser;
import org.embulk.spi.time.TimestampParser;
import org.embulk.spi.util.Pages;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.msgpack.value.Value;


public class TestJsonParserPlugin {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    private ConfigSource config;

    private JsonParserPlugin plugin;

    private MockPageOutput output;

    @Test
    public void checkDefaultValues() {
        ConfigSource config = Exec.newConfigSource();
        JsonParserPlugin.PluginTask task = config.loadConfig(PluginTask.class);
        Assert.assertEquals(false, task.getStopOnInvalidRecord());
        Assert.assertEquals(PASSTHROUGH, task.getInvalidEscapeStringPolicy());
    }

    @Test
    public void readNormalJson() throws Exception {
        transaction(config, // this line should be skipped.
        // this line should be skipped.
        // this line should be skipped.
        // this line should be skipped.
        // this line should be skipped.
        // this line should be skipped.
        fileInput("{\"_c0\":true,\"_c1\":10,\"_c2\":\"embulk\",\"_c3\":{\"k\":\"v\"}}", "{}", ("{\n" + (((("\"_c0\":false,\n" + "\"_c1\":-10,\n") + "\"_c2\":\"\u30a8\u30f3\u30d0\u30eb\u30af\",\n") + "\"_c3\":[\"e0\",\"e1\"]\n") + "}")), "[1, 2, 3]", "\"embulk\"", "10", "true", "false", "null"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(3, records.size());
        Object[] record;
        Map<Value, Value> map;
        {
            // "{\"_c0\":true,\"_c1\":10,\"_c2\":\"embulk\",\"_c3\":{\"k\":\"v\"}}"
            record = records.get(0);
            Assert.assertEquals(1, record.length);
            map = asMapValue().map();
            Assert.assertEquals(newBoolean(true), map.get(newString("_c0")));
            Assert.assertEquals(newInteger(10L), map.get(newString("_c1")));
            Assert.assertEquals(newString("embulk"), map.get(newString("_c2")));
            Assert.assertEquals(newMap(newString("k"), newString("v")), map.get(newString("_c3")));
        }
        {
            // "{}"
            record = records.get(1);
            Assert.assertEquals(1, record.length);
            Assert.assertTrue(asMapValue().map().isEmpty());
        }
        {
            record = records.get(2);
            Assert.assertEquals(1, record.length);
            map = asMapValue().map();
            Assert.assertEquals(newBoolean(false), map.get(newString("_c0")));
            Assert.assertEquals(newInteger((-10L)), map.get(newString("_c1")));
            Assert.assertEquals(newString("?????"), map.get(newString("_c2")));
            Assert.assertEquals(newArray(newString("e0"), newString("e1")), map.get(newString("_c3")));
        }
    }

    @Test
    public void useStopOnInvalidRecord() throws Exception {
        ConfigSource config = this.config.deepCopy().set("stop_on_invalid_record", true);
        try {
            transaction(config, // throw DataException
            fileInput("[1, 2, 3]"));
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof DataException));
        }
    }

    @Test
    public void readBrokenJson() {
        try {
            transaction(config, // throw DataException
            fileInput("{\"_c0\":true,\"_c1\":10,"));
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof DataException));
        }
    }

    @Test
    public void useDefaultInvalidEscapeStringFunction() throws Exception {
        try {
            transaction(config, // throw DataException
            fileInput("{\"\\a\":\"b\"}\\"));
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof DataException));
        }
    }

    @Test
    public void usePassthroughInvalidEscapeStringFunction() throws Exception {
        try {
            ConfigSource config = this.config.deepCopy().set("invalid_string_escapes", "PASSTHROUGH");
            transaction(config, // throw DataException
            fileInput("{\"\\a\":\"b\"}\\"));
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue((t instanceof DataException));
        }
    }

    @Test
    public void useSkipInvalidEscapeString() throws Exception {
        ConfigSource config = this.config.deepCopy().set("invalid_string_escapes", "SKIP");
        transaction(config, fileInput("{\"\\a\":\"b\"}\\"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(1, records.size());
        Object[] record = records.get(0);
        Map<Value, Value> map = asMapValue().map();
        Assert.assertEquals(newString("b"), map.get(newString("")));
    }

    @Test
    public void useUnEscapeInvalidEscapeString() throws Exception {
        ConfigSource config = this.config.deepCopy().set("invalid_string_escapes", "UNESCAPE");
        transaction(config, fileInput("{\"\\a\":\"b\"}\\"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(1, records.size());
        Object[] record = records.get(0);
        Map<Value, Value> map = asMapValue().map();
        Assert.assertEquals(newString("b"), map.get(newString("a")));
    }

    @Test
    public void checkInvalidEscapeStringFunction() {
        // PASSTHROUGH
        {
            String json = "{\\\"_c0\\\":true,\\\"_c1\\\":10,\\\"_c2\\\":\\\"embulk\\\",\\\"_c3\\\":{\\\"k\\\":\\\"v\\\"}}";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.PASSTHROUGH).apply(json);
            Assert.assertEquals(json, actual);
        }
        {
            String json = "{\"abc\b\f\n\r\t\\\\u0001\":\"efg\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.PASSTHROUGH).apply(json);
            Assert.assertEquals(json, actual);
        }
        {
            String json = "{\"\\a\":\"b\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.PASSTHROUGH).apply(json);
            Assert.assertEquals(json, actual);
        }
        // SKIP
        {
            String json = "{\\\"_c0\\\":true,\\\"_c1\\\":10,\\\"_c2\\\":\\\"embulk\\\",\\\"_c3\\\":{\\\"k\\\":\\\"v\\\"}}";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.SKIP).apply(json);
            Assert.assertEquals(json, actual);
        }
        {
            // valid charset u0001
            String json = "{\"abc\b\f\n\r\t\\\\u0001\":\"efg\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.SKIP).apply(json);
            Assert.assertEquals("{\"abc\b\f\n\r\t\\\\u0001\":\"efg\"}", actual);
        }
        {
            // invalid charset \\u12xY remove forwarding backslash and u
            String json = "{\"\\u12xY\":\"efg\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.SKIP).apply(json);
            Assert.assertEquals("{\"12xY\":\"efg\"}", actual);
        }
        {
            String json = "{\"\\a\":\"b\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.SKIP).apply(json);
            // backslash and `a` will removed.
            Assert.assertEquals("{\"\":\"b\"}", actual);
        }
        {
            // end of lines backspash.
            String json = "{\"\\a\":\"b\"}" + ("\n" + "\\");
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.SKIP).apply(json);
            // backslash and `a` will removed.
            Assert.assertEquals("{\"\":\"b\"}\n", actual);
        }
        // UNESCAPE
        {
            String json = "{\\\"_c0\\\":true,\\\"_c1\\\":10,\\\"_c2\\\":\\\"embulk\\\",\\\"_c3\\\":{\\\"k\\\":\\\"v\\\"}}";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.UNESCAPE).apply(json);
            Assert.assertEquals(json, actual);
        }
        {
            String json = "{\"abc\b\f\n\r\t\\\\u0001\":\"efg\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.UNESCAPE).apply(json);
            Assert.assertEquals("{\"abc\b\f\n\r\t\\\\u0001\":\"efg\"}", actual);
        }
        {
            // invalid charset u000x remove forwarding backslash
            String json = "{\"\\u000x\":\"efg\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.UNESCAPE).apply(json);
            Assert.assertEquals("{\"u000x\":\"efg\"}", actual);
        }
        {
            String json = "{\"\\a\":\"b\"}\\";
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.UNESCAPE).apply(json);
            // backslash will removed.
            Assert.assertEquals("{\"a\":\"b\"}", actual);
        }
        {
            // end of lines backspash.
            String json = "{\"\\a\":\"b\"}" + ("\n" + "\\");
            String actual = JsonParserPlugin.invalidEscapeStringFunction(InvalidEscapeStringPolicy.SKIP).apply(json);
            // backslash and `a` will removed.
            Assert.assertEquals("{\"\":\"b\"}\n", actual);
        }
    }

    @Test
    public void useRoot() throws Exception {
        ConfigSource config = this.config.deepCopy().set("root", "/_c0");
        transaction(config, // should be skipped because it doesn't have "_c0" and stop_on_invalid_record is false
        // should be skipped because the "_c0"'s value isn't map value and stop_on_invalid_record is false
        fileInput("{\"_c0\":{\"b\": 1}, \"_c1\": true}", "{}", "{\"_c0\": 1}", "{\"_c0\":{\"b\": 2}, \"_c1\": false}"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(2, records.size());
        Object[] record = records.get(0);
        Map<Value, Value> map = asMapValue().map();
        Assert.assertEquals(newInteger(1), map.get(newString("b")));
        record = records.get(1);
        map = asMapValue().map();
        Assert.assertEquals(newInteger(2), map.get(newString("b")));
    }

    @Test(expected = DataException.class)
    public void useRootWithStopOnInvalidRecord() throws Exception {
        ConfigSource config = this.config.deepCopy().set("root", "/_c0").set("stop_on_invalid_record", true);
        transaction(config, // Stop with the record
        fileInput("{\"_c0\":{\"b\": 1}, \"_c1\": true}", "{}", "{\"_c0\":{\"b\": 2}, \"_c1\": false}"));
    }

    @Test
    public void useSchemaConfig() throws Exception {
        // Check parsing all types and inexistent column
        final List<Object> schemaConfig = new ArrayList<>();
        schemaConfig.add(config().set("name", "_c0").set("type", "long"));
        schemaConfig.add(config().set("name", "_c1").set("type", "double"));
        schemaConfig.add(config().set("name", "_c2").set("type", "string"));
        schemaConfig.add(config().set("name", "_c3").set("type", "boolean"));
        schemaConfig.add(config().set("name", "_c4").set("type", "timestamp").set("format", "%Y-%m-%d %H:%M:%S"));
        schemaConfig.add(config().set("name", "_c5").set("type", "json"));
        schemaConfig.add(config().set("name", "_c99").set("type", "string"));
        ConfigSource config = this.config.set("columns", schemaConfig);
        transaction(config, fileInput("{\"_c0\": 1, \"_c1\": 1.234, \"_c2\": \"a\", \"_c3\": true, \"_c4\": \"2019-01-02 03:04:56\", \"_c5\":{\"a\": 1}}"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(1, records.size());
        Object[] record = records.get(0);
        Assert.assertArrayEquals(record, new Object[]{ 1L, 1.234, "a", true, TestJsonParserPlugin.toTimestamp("2019-01-02 03:04:56"), TestJsonParserPlugin.toJson("{\"a\": 1}"), null });
    }

    @Test
    public void useDefaultSchemaIfSchemaConfigIsEmptyArray() throws Exception {
        ConfigSource config = this.config.set("columns", new ArrayList());
        transaction(config, fileInput("{\"_c0\": 1, \"_c1\": 1.234, \"_c2\": \"a\", \"_c3\": true, \"_c4\": \"2019-01-02 03:04:56\", \"_c5\":{\"a\": 1}}"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(1, records.size());
        Object[] record = records.get(0);
        Assert.assertArrayEquals(record, new Object[]{ TestJsonParserPlugin.toJson("{\"_c0\": 1, \"_c1\": 1.234, \"_c2\": \"a\", \"_c3\": true, \"_c4\": \"2019-01-02 03:04:56\", \"_c5\":{\"a\": 1}}") });
    }

    @Test
    public void useSchemaConfigWithPointer() throws Exception {
        // Check parsing all types and inexistent column
        final List<Object> schemaConfig = new ArrayList<>();
        schemaConfig.add(config().set("name", "_c0").set("type", "long").set("pointer", "/a/0"));
        schemaConfig.add(config().set("name", "_c1").set("type", "double").set("pointer", "/a/1"));
        schemaConfig.add(config().set("name", "_c2").set("type", "string").set("pointer", "/a/2"));
        schemaConfig.add(config().set("name", "_c3").set("type", "boolean").set("pointer", "/a/3/b/0"));
        schemaConfig.add(config().set("name", "_c4").set("type", "timestamp").set("format", "%Y-%m-%d %H:%M:%S").set("pointer", "/a/3/b/1"));
        schemaConfig.add(config().set("name", "_c5").set("type", "json").set("pointer", "/c"));
        schemaConfig.add(config().set("name", "_c99").set("type", "json").set("pointer", "/d"));
        ConfigSource config = this.config.set("columns", schemaConfig);
        transaction(config, fileInput("{\"a\": [1, 1.234, \"foo\", {\"b\": [true, \"2019-01-02 03:04:56\"]}], \"c\": {\"a\": 1}}"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(1, records.size());
        Object[] record = records.get(0);
        Assert.assertArrayEquals(record, new Object[]{ 1L, 1.234, "foo", true, TestJsonParserPlugin.toTimestamp("2019-01-02 03:04:56"), TestJsonParserPlugin.toJson("{\"a\": 1}"), null });
    }

    @Test
    public void useFlattenJsonArray() throws Exception {
        ConfigSource config = this.config.set("flatten_json_array", true);
        transaction(config, fileInput("[{\"_c0\": 1},{\"_c0\": 2}]"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(2, records.size());
        Assert.assertArrayEquals(records.get(0), new Object[]{ TestJsonParserPlugin.toJson("{\"_c0\": 1}") });
        Assert.assertArrayEquals(records.get(1), new Object[]{ TestJsonParserPlugin.toJson("{\"_c0\": 2}") });
    }

    @Test(expected = DataException.class)
    public void useFlattenJsonArrayWithNonArrayJson() throws Exception {
        ConfigSource config = this.config.set("flatten_json_array", true).set("stop_on_invalid_record", true);
        transaction(config, fileInput("{\"_c0\": 1}"));
    }

    @Test
    public void useFlattenJsonArrayWithRootPointer() throws Exception {
        ConfigSource config = this.config.set("flatten_json_array", true).set("root", "/a");
        transaction(config, fileInput("{\"a\": [{\"_c0\": 1},{\"_c0\": 2}]}"));
        List<Object[]> records = Pages.toObjects(newSchema(), output.pages);
        Assert.assertEquals(2, records.size());
        Assert.assertArrayEquals(records.get(0), new Object[]{ TestJsonParserPlugin.toJson("{\"_c0\": 1}") });
        Assert.assertArrayEquals(records.get(1), new Object[]{ TestJsonParserPlugin.toJson("{\"_c0\": 2}") });
    }

    private static final TimestampParser TIMESTAMP_PARSER = TimestampParser.of("java:yyyy-MM-dd HH:mm:ss", "UTC");

    private static final JsonParser JSON_PARSER = new JsonParser();
}

