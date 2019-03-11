package org.embulk.standards;


import CsvParserPlugin.PluginTask;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigSource;
import org.embulk.spi.FileInput;
import org.embulk.spi.Schema;
import org.embulk.spi.util.LineDecoder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/* @Test
public void parseEscapedQuotedValues() throws Exception {
List<List<String>> parsed = doParse(task, bufferList("utf-8",
"\"aa,a\",\",aaa\",\"aaa,\"", "\n",
"\"bb\"\"b\",\"\"\"bbb\",\"bbb\"\"\"", "\n",
"\"cc\\\"c\",\"\\\"ccc\",\"ccc\\\"\"", "\n",
"\"dd\nd\",\"\nddd\",\"ddd\n\"", "\n"));
assertEquals(Arrays.asList(
Arrays.asList("aa,a", ",aaa", "aaa,"),
Arrays.asList("bb\"b", "\"bbb", "bbb\""),
Arrays.asList("cc\"c", "\"ccc", "ccc\""),
Arrays.asList("dd\nd", "\nddd", "ddd\n")),
parsed);
}
 */
public class TestCsvTokenizer {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    protected ConfigSource config;

    protected PluginTask task;

    @Test
    public void testSimple() throws Exception {
        Assert.assertEquals(expectedRecords(2, "aaa", "bbb", "ccc", "ddd"), TestCsvTokenizer.parse(task, "aaa,bbb", "ccc,ddd"));
    }

    @Test
    public void testSkipEmptyLine() throws Exception {
        Assert.assertEquals(expectedRecords(2, "aaa", "bbb", "ccc", "ddd"), TestCsvTokenizer.parse(task, "", "aaa,bbb", "", "", "ccc,ddd", "", ""));
    }

    @Test
    public void parseEmptyColumnsToNull() throws Exception {
        // not trimmed
        Assert.assertEquals(expectedRecords(2, null, null, "", "", "  ", "  "), TestCsvTokenizer.parse(task, ",", "\"\",\"\"", "  ,  "));
    }

    @Test
    public void parseEmptyColumnsToNullTrimmed() throws Exception {
        config.set("trim_if_not_quoted", true);
        reloadPluginTask();
        // trimmed
        Assert.assertEquals(expectedRecords(2, null, null, "", "", null, null), TestCsvTokenizer.parse(task, ",", "\"\",\"\"", "  ,  "));
    }

    @Test
    public void testMultilineQuotedValueWithEmptyLine() throws Exception {
        Assert.assertEquals(expectedRecords(2, "a", "\nb\n\n", "c", "d"), TestCsvTokenizer.parse(task, "", "a,\"", "b", "", "\"", "c,d"));
    }

    @Test
    public void testEndOfFileWithoutNewline() throws Exception {
        // In RFC 4180, the last record in the file may or may not have
        // an ending line break.
        Assert.assertEquals(expectedRecords(2, "aaa", "bbb", "ccc", "ddd"), TestCsvTokenizer.parse(task, TestCsvTokenizer.newFileInputFromText(task, "aaa,bbb\nccc,ddd")));
    }

    @Test
    public void testChangeDelimiter() throws Exception {
        config.set("delimiter", JsonNodeFactory.instance.textNode("\t"));// TSV format

        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, "aaa", "bbb", "ccc", "ddd"), TestCsvTokenizer.parse(task, "aaa\tbbb", "ccc\tddd"));
    }

    @Test
    public void testDefaultNullString() throws Exception {
        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, null, "", "NULL", "NULL"), TestCsvTokenizer.parse(task, ",\"\"", "NULL,\"NULL\""));
    }

    @Test
    public void testChangeNullString() throws Exception {
        config.set("null_string", "NULL");
        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, "", "", null, null), TestCsvTokenizer.parse(task, ",\"\"", "NULL,\"NULL\""));
    }

    @Test
    public void testQuotedValues() throws Exception {
        Assert.assertEquals(expectedRecords(2, "a\na\na", "b,bb", "cc\"c", "\"ddd", null, ""), TestCsvTokenizer.parse(task, TestCsvTokenizer.newFileInputFromText(task, "\n\"a\na\na\",\"b,bb\"\n\n\"cc\"\"c\",\"\"\"ddd\"\n,\"\"\n")));
    }

    @Test
    public void parseEscapedValues() throws Exception {
        Assert.assertEquals(expectedRecords(2, "a\"aa", "b,bb\"", "cc\"c", "\"ddd", null, ""), TestCsvTokenizer.parse(task, TestCsvTokenizer.newFileInputFromText(task, "\n\"a\\\"aa\",\"b,bb\\\"\"\n\n\"cc\"\"c\",\"\"\"ddd\"\n,\"\"\n")));
    }

    @Test
    public void testCommentLineMarker() throws Exception {
        config.set("comment_line_marker", JsonNodeFactory.instance.textNode("#"));
        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, "aaa", "bbb", "eee", "fff"), TestCsvTokenizer.parse(task, "aaa,bbb", "#ccc,ddd", "eee,fff"));
    }

    @Test
    public void trimNonQuotedValues() throws Exception {
        // quoted values are not changed
        Assert.assertEquals(expectedRecords(2, "  aaa  ", "  b cd ", "  ccc", "dd d \n "), TestCsvTokenizer.parse(task, TestCsvTokenizer.newFileInputFromText(task, "  aaa  ,  b cd \n\"  ccc\",\"dd d \n \"")));
        // trim_if_not_quoted is true
        config.set("trim_if_not_quoted", true);
        reloadPluginTask();
        // quoted values are not changed
        Assert.assertEquals(expectedRecords(2, "aaa", "b cd", "  ccc", "dd d \n "), TestCsvTokenizer.parse(task, TestCsvTokenizer.newFileInputFromText(task, "  aaa  ,  b cd \n\"  ccc\",\"dd d \n \"")));
    }

    @Test
    public void parseQuotedValueWithSpacesAndTrimmingOption() throws Exception {
        config.set("trim_if_not_quoted", true);
        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, "heading1", "heading2", "trailing1", "trailing2", "trailing\n3", "trailing\n4"), TestCsvTokenizer.parse(task, "  \"heading1\",  \"heading2\"", "\"trailing1\"  ,\"trailing2\"  ", "\"trailing\n3\"  ,\"trailing\n4\"  "));
    }

    @Test
    public void parseWithDefaultQuotesInQuotedFields() throws Exception {
        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, "foo\"bar", "foofoo\"barbar", "baz\"\"qux", "bazbaz\"\"quxqux"), TestCsvTokenizer.parse(task, "\"foo\"\"bar\",\"foofoo\"\"barbar\"", "\"baz\"\"\"\"qux\",\"bazbaz\"\"\"\"quxqux\""));
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void parseWithQuotesInQuotedFields_ACCEPT_ONLY_RFC4180_ESCAPED() throws Exception {
        config.set("quotes_in_quoted_fields", "ACCEPT_ONLY_RFC4180_ESCAPED");
        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, "foo\"bar", "foofoo\"barbar", "baz\"\"qux", "bazbaz\"\"quxqux"), TestCsvTokenizer.parse(task, "\"foo\"\"bar\",\"foofoo\"\"barbar\"", "\"baz\"\"\"\"qux\",\"bazbaz\"\"\"\"quxqux\""));
    }

    @Test
    public void throwWithDefaultQuotesInQuotedFields() throws Exception {
        reloadPluginTask();
        try {
            TestCsvTokenizer.parse(task, "\"foo\"bar\",\"hoge\"fuga\"");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof CsvTokenizer.InvalidValueException));
            Assert.assertEquals("Unexpected extra character \'b\' after a value quoted by \'\"\'", e.getMessage());
            return;
        }
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void throwWithQuotesInQuotedFields_ACCEPT_ONLY_RFC4180_ESCAPED() throws Exception {
        config.set("quotes_in_quoted_fields", "ACCEPT_ONLY_RFC4180_ESCAPED");
        reloadPluginTask();
        try {
            TestCsvTokenizer.parse(task, "\"foo\"bar\",\"hoge\"fuga\"");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof CsvTokenizer.InvalidValueException));
            Assert.assertEquals("Unexpected extra character \'b\' after a value quoted by \'\"\'", e.getMessage());
            return;
        }
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void parseWithQuotesInQuotedFields_ACCEPT_STRAY_QUOTES_ASSUMING_NO_DELIMITERS_IN_FIELDS() throws Exception {
        config.set("quotes_in_quoted_fields", "ACCEPT_STRAY_QUOTES_ASSUMING_NO_DELIMITERS_IN_FIELDS");
        reloadPluginTask();
        Assert.assertEquals(expectedRecords(2, "foo\"bar", "foofoo\"barbar", "baz\"\"qux", "bazbaz\"\"quxqux", "\"embulk\"", "\"embul\"\"k\""), TestCsvTokenizer.parse(task, "\"foo\"bar\",\"foofoo\"\"barbar\"", "\"baz\"\"\"qux\",\"bazbaz\"\"\"\"quxqux\"", "\"\"\"embulk\"\",\"\"embul\"\"\"k\"\""));
    }

    @Test
    public void throwQuotedSizeLimitExceededException() throws Exception {
        config.set("max_quoted_size_limit", 8);
        reloadPluginTask();
        try {
            TestCsvTokenizer.parse(task, "v1,v2", "v3,\"0123456789\"");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof CsvTokenizer.QuotedSizeLimitExceededException));
        }
        // multi-line
        try {
            TestCsvTokenizer.parse(task, "v1,v2", "\"012345\n6789\",v3");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof CsvTokenizer.QuotedSizeLimitExceededException));
        }
    }

    @Test
    public void recoverFromQuotedSizeLimitExceededException() throws Exception {
        config.set("max_quoted_size_limit", 12);
        reloadPluginTask();
        String[] lines = new String[]{ "v1,v2", "v3,\"0123"// this is a broken line and should be skipped
        , "v4,v5"// this line should be not be skiped
        , "v6,v7"// this line should be not be skiped
         };
        FileInput input = TestCsvTokenizer.newFileInputFromLines(task, lines);
        LineDecoder decoder = new LineDecoder(input, task);
        CsvTokenizer tokenizer = new CsvTokenizer(decoder, task);
        Schema schema = task.getSchemaConfig().toSchema();
        tokenizer.nextFile();
        Assert.assertTrue(tokenizer.nextRecord());
        Assert.assertEquals("v1", tokenizer.nextColumn());
        Assert.assertEquals("v2", tokenizer.nextColumn());
        Assert.assertTrue(tokenizer.nextRecord());
        Assert.assertEquals("v3", tokenizer.nextColumn());
        try {
            tokenizer.nextColumn();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof CsvTokenizer.QuotedSizeLimitExceededException));
        }
        Assert.assertEquals("v3,\"0123", tokenizer.skipCurrentLine());
        Assert.assertTrue(tokenizer.nextRecord());
        Assert.assertEquals("v4", tokenizer.nextColumn());
        Assert.assertEquals("v5", tokenizer.nextColumn());
        Assert.assertTrue(tokenizer.nextRecord());
        Assert.assertEquals("v6", tokenizer.nextColumn());
        Assert.assertEquals("v7", tokenizer.nextColumn());
    }
}

