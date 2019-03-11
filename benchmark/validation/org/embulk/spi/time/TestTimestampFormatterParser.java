package org.embulk.spi.time;


import com.google.common.base.Optional;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.spi.Exec;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestTimestampFormatterParser {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    private interface FormatterTestTask extends Task , TimestampFormatter.Task {}

    private interface ParserTestTask extends Task , TimestampParser.Task {}

    @Test
    public void testSimpleFormat() throws Exception {
        ConfigSource config = Exec.newConfigSource().set("default_timestamp_format", "%Y-%m-%d %H:%M:%S.%9N %z");// %Z is OS-dependent

        TestTimestampFormatterParser.FormatterTestTask task = config.loadConfig(TestTimestampFormatterParser.FormatterTestTask.class);
        TimestampFormatter formatter = TimestampFormatter.of(task, Optional.<TimestampFormatter.TimestampColumnOption>absent());
        Assert.assertEquals("2014-11-19 02:46:29.123456000 +0000", formatter.format(Timestamp.ofEpochSecond(1416365189, (123456 * 1000))));
    }

    @Test
    public void testSimpleParse() throws Exception {
        ConfigSource config = Exec.newConfigSource().set("default_timestamp_format", "%Y-%m-%d %H:%M:%S %z");// %Z is OS-dependent

        TestTimestampFormatterParser.ParserTestTask task = config.loadConfig(TestTimestampFormatterParser.ParserTestTask.class);
        TimestampParser parser = TimestampParserLegacy.createTimestampParserForTesting(task);
        Assert.assertEquals(Timestamp.ofEpochSecond(1416365189, 0), parser.parse("2014-11-19 02:46:29 +0000"));
    }

    @Test
    public void testUnixtimeFormat() throws Exception {
        ConfigSource config = Exec.newConfigSource().set("default_timestamp_format", "%s");
        TestTimestampFormatterParser.FormatterTestTask ftask = config.loadConfig(TestTimestampFormatterParser.FormatterTestTask.class);
        TimestampFormatter formatter = TimestampFormatter.of(ftask, Optional.<TimestampFormatter.TimestampColumnOption>absent());
        Assert.assertEquals("1416365189", formatter.format(Timestamp.ofEpochSecond(1416365189)));
        TestTimestampFormatterParser.ParserTestTask ptask = config.loadConfig(TestTimestampFormatterParser.ParserTestTask.class);
        TimestampParser parser = TimestampParserLegacy.createTimestampParserForTesting(ptask);
        Assert.assertEquals(Timestamp.ofEpochSecond(1416365189), parser.parse("1416365189"));
    }

    @Test
    public void testDefaultDate() throws Exception {
        ConfigSource config = Exec.newConfigSource().set("default_timestamp_format", "%H:%M:%S %Z").set("default_date", "2016-02-03");
        TestTimestampFormatterParser.ParserTestTask ptask = config.loadConfig(TestTimestampFormatterParser.ParserTestTask.class);
        TimestampParser parser = TimestampParserLegacy.createTimestampParserForTesting(ptask);
        Assert.assertEquals(Timestamp.ofEpochSecond(1454467589, 0), parser.parse("02:46:29 +0000"));
    }
}

