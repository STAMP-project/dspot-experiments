package org.embulk.standards;


import CsvFormatterPlugin.PluginTask;
import CsvFormatterPlugin.QuotePolicy;
import CsvFormatterPlugin.QuotePolicy.ALL;
import CsvFormatterPlugin.QuotePolicy.MINIMAL;
import CsvFormatterPlugin.QuotePolicy.NONE;
import Newline.CR;
import Newline.CRLF;
import Newline.LF;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigSource;
import org.embulk.spi.Exec;
import org.joda.time.DateTimeZone.UTC;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestCsvFormatterPlugin {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Test
    public void checkDefaultValues() {
        ConfigSource config = Exec.newConfigSource();
        CsvFormatterPlugin.PluginTask task = config.loadConfig(PluginTask.class);
        Assert.assertEquals(Charset.forName("utf-8"), task.getCharset());
        Assert.assertEquals(CRLF, task.getNewline());
        Assert.assertEquals(true, task.getHeaderLine());
        Assert.assertEquals(',', task.getDelimiterChar());
        Assert.assertEquals('\"', task.getQuoteChar());
        Assert.assertEquals(MINIMAL, task.getQuotePolicy());
        Assert.assertEquals(false, task.getEscapeChar().isPresent());
        Assert.assertEquals("", task.getNullString());
        Assert.assertEquals(UTC, task.getDefaultTimeZone());
        Assert.assertEquals("%Y-%m-%d %H:%M:%S.%6N %z", task.getDefaultTimestampFormat());
        Assert.assertEquals(LF, task.getNewlineInField());
    }

    @Test
    public void checkLoadConfig() {
        ConfigSource config = Exec.newConfigSource().set("charset", "utf-16").set("newline", "LF").set("header_line", false).set("delimiter", "\t").set("quote", "\\").set("quote_policy", "ALL").set("escape", "\"").set("null_string", "\\N").set("newline_in_field", "CRLF");
        CsvFormatterPlugin.PluginTask task = config.loadConfig(PluginTask.class);
        Assert.assertEquals(Charset.forName("utf-16"), task.getCharset());
        Assert.assertEquals(LF, task.getNewline());
        Assert.assertEquals(false, task.getHeaderLine());
        Assert.assertEquals('\t', task.getDelimiterChar());
        Assert.assertEquals('\\', task.getQuoteChar());
        Assert.assertEquals(ALL, task.getQuotePolicy());
        Assert.assertEquals('\"', ((char) (task.getEscapeChar().get())));
        Assert.assertEquals("\\N", task.getNullString());
        Assert.assertEquals(CRLF, task.getNewlineInField());
    }

    @Test
    public void testQuoteValue() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = CsvFormatterPlugin.class.getDeclaredMethod("setQuoteValue", String.class, char.class);
        method.setAccessible(true);
        CsvFormatterPlugin formatter = new CsvFormatterPlugin();
        Assert.assertEquals("\"ABCD\"", method.invoke(formatter, "ABCD", '"'));
        Assert.assertEquals("\"\"", method.invoke(formatter, "", '"'));
        Assert.assertEquals("'ABCD'", method.invoke(formatter, "ABCD", '\''));
        Assert.assertEquals("''", method.invoke(formatter, "", '\''));
    }

    @Test
    public void testEscapeQuote() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = CsvFormatterPlugin.class.getDeclaredMethod("setEscapeAndQuoteValue", String.class, char.class, QuotePolicy.class, char.class, char.class, String.class, String.class);
        method.setAccessible(true);
        CsvFormatterPlugin formatter = new CsvFormatterPlugin();
        char delimiter = ',';
        CsvFormatterPlugin.QuotePolicy policy = QuotePolicy.MINIMAL;
        String newline = LF.getString();
        Assert.assertEquals("\"AB\\\"CD\"", method.invoke(formatter, "AB\"CD", delimiter, policy, '"', '\\', newline, ""));
        Assert.assertEquals("\"AB\"\"CD\"", method.invoke(formatter, "AB\"CD", delimiter, policy, '"', '"', newline, ""));
    }

    @Test
    public void testQuotePolicyAll() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = CsvFormatterPlugin.class.getDeclaredMethod("setEscapeAndQuoteValue", String.class, char.class, QuotePolicy.class, char.class, char.class, String.class, String.class);
        method.setAccessible(true);
        CsvFormatterPlugin formatter = new CsvFormatterPlugin();
        char delimiter = ',';
        char quote = '"';
        char escape = '"';
        CsvFormatterPlugin.QuotePolicy policy = QuotePolicy.ALL;
        String newline = LF.getString();
        String nullString = "";
        @SuppressWarnings("unchecked")
        ImmutableList<ImmutableMap<String, String>> testCases = ImmutableList.of(ImmutableMap.of("expected", "\"true\"", "actual", "true"), ImmutableMap.of("expected", "\"false\"", "actual", "false"), ImmutableMap.of("expected", "\"0\"", "actual", "0"), ImmutableMap.of("expected", "\"1\"", "actual", "1"), ImmutableMap.of("expected", "\"1234\"", "actual", "1234"), ImmutableMap.of("expected", "\"-1234\"", "actual", "-1234"), ImmutableMap.of("expected", "\"+1234\"", "actual", "+1234"), ImmutableMap.of("expected", "\"0x4d2\"", "actual", "0x4d2"), ImmutableMap.of("expected", "\"123L\"", "actual", "123L"), ImmutableMap.of("expected", "\"3.141592\"", "actual", "3.141592"), ImmutableMap.of("expected", "\"1,000\"", "actual", "1,000"), ImmutableMap.of("expected", "\"ABC\"", "actual", "ABC"), ImmutableMap.of("expected", "\"ABC\"\"DEF\"", "actual", "ABC\"DEF"), ImmutableMap.of("expected", "\"ABC\nDEF\"", "actual", "ABC\nDEF"), ImmutableMap.of("expected", "\"\"", "actual", ""), ImmutableMap.of("expected", "\"NULL\"", "actual", "NULL"), ImmutableMap.of("expected", "\"2015-01-01 12:01:01\"", "actual", "2015-01-01 12:01:01"), ImmutableMap.of("expected", "\"20150101\"", "actual", "20150101"));
        for (ImmutableMap testCase : testCases) {
            String expected = ((String) (testCase.get("expected")));
            String actual = ((String) (testCase.get("actual")));
            Assert.assertEquals(expected, method.invoke(formatter, actual, delimiter, policy, quote, escape, newline, nullString));
        }
    }

    @Test
    public void testQuotePolicyMinimal() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = CsvFormatterPlugin.class.getDeclaredMethod("setEscapeAndQuoteValue", String.class, char.class, QuotePolicy.class, char.class, char.class, String.class, String.class);
        method.setAccessible(true);
        CsvFormatterPlugin formatter = new CsvFormatterPlugin();
        char delimiter = ',';
        char quote = '"';
        char escape = '"';
        CsvFormatterPlugin.QuotePolicy policy = QuotePolicy.MINIMAL;
        String newline = LF.getString();
        String nullString = "";
        @SuppressWarnings("unchecked")
        ImmutableList<ImmutableMap<String, String>> testCases = ImmutableList.of(ImmutableMap.of("expected", "true", "actual", "true"), ImmutableMap.of("expected", "false", "actual", "false"), ImmutableMap.of("expected", "0", "actual", "0"), ImmutableMap.of("expected", "1", "actual", "1"), ImmutableMap.of("expected", "1234", "actual", "1234"), ImmutableMap.of("expected", "-1234", "actual", "-1234"), ImmutableMap.of("expected", "+1234", "actual", "+1234"), ImmutableMap.of("expected", "0x4d2", "actual", "0x4d2"), ImmutableMap.of("expected", "123L", "actual", "123L"), ImmutableMap.of("expected", "3.141592", "actual", "3.141592"), ImmutableMap.of("expected", "\"1,000\"", "actual", "1,000"), ImmutableMap.of("expected", "ABC", "actual", "ABC"), ImmutableMap.of("expected", "\"ABC\"\"DEF\"", "actual", "ABC\"DEF"), ImmutableMap.of("expected", "\"ABC\nDEF\"", "actual", "ABC\nDEF"), ImmutableMap.of("expected", "\"\"", "actual", ""), ImmutableMap.of("expected", "NULL", "actual", "NULL"), ImmutableMap.of("expected", "2015-01-01 12:01:01", "actual", "2015-01-01 12:01:01"), ImmutableMap.of("expected", "20150101", "actual", "20150101"));
        for (ImmutableMap testCase : testCases) {
            String expected = ((String) (testCase.get("expected")));
            String actual = ((String) (testCase.get("actual")));
            Assert.assertEquals(expected, method.invoke(formatter, actual, delimiter, policy, quote, escape, newline, nullString));
        }
    }

    @Test
    public void testQuotePolicyNone() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = CsvFormatterPlugin.class.getDeclaredMethod("setEscapeAndQuoteValue", String.class, char.class, QuotePolicy.class, char.class, char.class, String.class, String.class);
        method.setAccessible(true);
        CsvFormatterPlugin formatter = new CsvFormatterPlugin();
        char delimiter = ',';
        char quote = '"';
        char escape = '\\';
        CsvFormatterPlugin.QuotePolicy policy = QuotePolicy.NONE;
        String newline = LF.getString();
        String nullString = "";
        @SuppressWarnings("unchecked")
        ImmutableList<ImmutableMap<String, String>> testCases = ImmutableList.of(ImmutableMap.of("expected", "true", "actual", "true"), ImmutableMap.of("expected", "false", "actual", "false"), ImmutableMap.of("expected", "0", "actual", "0"), ImmutableMap.of("expected", "1", "actual", "1"), ImmutableMap.of("expected", "1234", "actual", "1234"), ImmutableMap.of("expected", "-1234", "actual", "-1234"), ImmutableMap.of("expected", "+1234", "actual", "+1234"), ImmutableMap.of("expected", "0x4d2", "actual", "0x4d2"), ImmutableMap.of("expected", "123L", "actual", "123L"), ImmutableMap.of("expected", "3.141592", "actual", "3.141592"), ImmutableMap.of("expected", "1\\,000", "actual", "1,000"), ImmutableMap.of("expected", "ABC", "actual", "ABC"), ImmutableMap.of("expected", "ABC\"DEF", "actual", "ABC\"DEF"), ImmutableMap.of("expected", "ABC\\\nDEF", "actual", "ABC\nDEF"), ImmutableMap.of("expected", "", "actual", ""), ImmutableMap.of("expected", "NULL", "actual", "NULL"), ImmutableMap.of("expected", "2015-01-01 12:01:01", "actual", "2015-01-01 12:01:01"), ImmutableMap.of("expected", "20150101", "actual", "20150101"));
        for (ImmutableMap testCase : testCases) {
            String expected = ((String) (testCase.get("expected")));
            String actual = ((String) (testCase.get("actual")));
            Assert.assertEquals(expected, method.invoke(formatter, actual, delimiter, policy, quote, escape, newline, nullString));
        }
    }

    @Test
    public void testNewlineInField() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = CsvFormatterPlugin.class.getDeclaredMethod("setEscapeAndQuoteValue", String.class, char.class, QuotePolicy.class, char.class, char.class, String.class, String.class);
        method.setAccessible(true);
        CsvFormatterPlugin formatter = new CsvFormatterPlugin();
        char delimiter = ',';
        char quote = '"';
        char escape = '"';
        String newline;
        CsvFormatterPlugin.QuotePolicy policy = QuotePolicy.MINIMAL;
        String nullString = "";
        ImmutableList<ImmutableMap<String, String>> testCases;
        newline = LF.getString();
        testCases = ImmutableList.of(ImmutableMap.of("expected", "\"ABC\nDEF\"", "actual", "ABC\r\nDEF"), ImmutableMap.of("expected", "\"ABC\nDEF\"", "actual", "ABC\rDEF"), ImmutableMap.of("expected", "\"ABC\nDEF\"", "actual", "ABC\nDEF"));
        for (ImmutableMap testCase : testCases) {
            String expected = ((String) (testCase.get("expected")));
            String actual = ((String) (testCase.get("actual")));
            Assert.assertEquals(expected, method.invoke(formatter, actual, delimiter, policy, quote, escape, newline, nullString));
        }
        newline = CRLF.getString();
        testCases = ImmutableList.of(ImmutableMap.of("expected", "\"ABC\r\nDEF\"", "actual", "ABC\r\nDEF"), ImmutableMap.of("expected", "\"ABC\r\nDEF\"", "actual", "ABC\rDEF"), ImmutableMap.of("expected", "\"ABC\r\nDEF\"", "actual", "ABC\nDEF"));
        for (ImmutableMap testCase : testCases) {
            String expected = ((String) (testCase.get("expected")));
            String actual = ((String) (testCase.get("actual")));
            Assert.assertEquals(expected, method.invoke(formatter, actual, delimiter, policy, quote, escape, newline, nullString));
        }
        newline = CR.getString();
        testCases = ImmutableList.of(ImmutableMap.of("expected", "\"ABC\rDEF\"", "actual", "ABC\r\nDEF"), ImmutableMap.of("expected", "\"ABC\rDEF\"", "actual", "ABC\rDEF"), ImmutableMap.of("expected", "\"ABC\rDEF\"", "actual", "ABC\nDEF"));
        for (ImmutableMap testCase : testCases) {
            String expected = ((String) (testCase.get("expected")));
            String actual = ((String) (testCase.get("actual")));
            Assert.assertEquals(expected, method.invoke(formatter, actual, delimiter, policy, quote, escape, newline, nullString));
        }
    }

    @Test
    public void testNullString() throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Method method = CsvFormatterPlugin.class.getDeclaredMethod("setEscapeAndQuoteValue", String.class, char.class, QuotePolicy.class, char.class, char.class, String.class, String.class);
        method.setAccessible(true);
        CsvFormatterPlugin formatter = new CsvFormatterPlugin();
        char delimiter = ',';
        char quote = '"';
        char escape = '"';
        CsvFormatterPlugin.QuotePolicy policy = QuotePolicy.MINIMAL;
        String newline = LF.getString();
        Assert.assertEquals("\"\"", method.invoke(formatter, "", delimiter, MINIMAL, quote, escape, newline, ""));
        Assert.assertEquals("N/A", method.invoke(formatter, "N/A", delimiter, MINIMAL, quote, escape, newline, ""));
        Assert.assertEquals("", method.invoke(formatter, "", delimiter, NONE, quote, escape, newline, ""));
        Assert.assertEquals("N/A", method.invoke(formatter, "N/A", delimiter, NONE, quote, escape, newline, ""));
        Assert.assertEquals("", method.invoke(formatter, "", delimiter, MINIMAL, quote, escape, newline, "N/A"));
        Assert.assertEquals("\"N/A\"", method.invoke(formatter, "N/A", delimiter, MINIMAL, quote, escape, newline, "N/A"));
        Assert.assertEquals("", method.invoke(formatter, "", delimiter, NONE, quote, escape, newline, "N/A"));
        Assert.assertEquals("N/A", method.invoke(formatter, "N/A", delimiter, NONE, quote, escape, newline, "N/A"));
    }
}

