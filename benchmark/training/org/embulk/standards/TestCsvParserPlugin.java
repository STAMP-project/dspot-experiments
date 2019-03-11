package org.embulk.standards;


import CsvParserPlugin.PluginTask;
import Newline.CRLF;
import Newline.LF;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.nio.charset.Charset;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigException;
import org.embulk.config.ConfigSource;
import org.embulk.spi.Exec;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestCsvParserPlugin {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Test
    public void checkDefaultValues() {
        ConfigSource config = Exec.newConfigSource().set("columns", ImmutableList.of(ImmutableMap.of("name", "date_code", "type", "string")));
        CsvParserPlugin.PluginTask task = config.loadConfig(PluginTask.class);
        Assert.assertEquals(Charset.forName("utf-8"), task.getCharset());
        Assert.assertEquals(CRLF, task.getNewline());
        Assert.assertEquals(false, task.getHeaderLine().or(false));
        Assert.assertEquals(",", task.getDelimiter());
        Assert.assertEquals(Optional.of(new CsvParserPlugin.QuoteCharacter('\"')), task.getQuoteChar());
        Assert.assertEquals(false, task.getAllowOptionalColumns());
        Assert.assertEquals("UTC", task.getDefaultTimeZoneId());
        Assert.assertEquals("%Y-%m-%d %H:%M:%S.%N %z", task.getDefaultTimestampFormat());
    }

    @Test(expected = ConfigException.class)
    public void checkColumnsRequired() {
        ConfigSource config = Exec.newConfigSource();
        config.loadConfig(PluginTask.class);
    }

    @Test
    public void checkLoadConfig() {
        ConfigSource config = Exec.newConfigSource().set("charset", "utf-16").set("newline", "LF").set("header_line", true).set("delimiter", "\t").set("quote", "\\").set("allow_optional_columns", true).set("columns", ImmutableList.of(ImmutableMap.of("name", "date_code", "type", "string")));
        CsvParserPlugin.PluginTask task = config.loadConfig(PluginTask.class);
        Assert.assertEquals(Charset.forName("utf-16"), task.getCharset());
        Assert.assertEquals(LF, task.getNewline());
        Assert.assertEquals(true, task.getHeaderLine().or(false));
        Assert.assertEquals("\t", task.getDelimiter());
        Assert.assertEquals(Optional.of(new CsvParserPlugin.QuoteCharacter('\\')), task.getQuoteChar());
        Assert.assertEquals(true, task.getAllowOptionalColumns());
    }
}

