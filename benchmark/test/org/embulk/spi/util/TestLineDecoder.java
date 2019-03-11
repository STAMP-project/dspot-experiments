package org.embulk.spi.util;


import LineDecoder.DecoderTask;
import LineDelimiter.CR;
import LineDelimiter.LF;
import Newline.CRLF;
import com.google.common.collect.ImmutableList;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigSource;
import org.embulk.spi.Exec;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class TestLineDecoder {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Test
    public void testDefaultValues() {
        ConfigSource config = Exec.newConfigSource();
        LineDecoder.DecoderTask task = config.loadConfig(DecoderTask.class);
        Assert.assertEquals(StandardCharsets.UTF_8, task.getCharset());
        Assert.assertEquals(CRLF, task.getNewline());
    }

    @Test
    public void testLoadConfig() {
        ConfigSource config = Exec.newConfigSource().set("charset", "utf-16").set("newline", "CRLF").set("line_delimiter_recognized", "LF");
        LineDecoder.DecoderTask task = config.loadConfig(DecoderTask.class);
        Assert.assertEquals(StandardCharsets.UTF_16, task.getCharset());
        Assert.assertEquals(CRLF, task.getNewline());
        Assert.assertEquals(LF, task.getLineDelimiterRecognized().get());
    }

    @Test
    public void testDecodeBasicAscii() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "test1\ntest2\ntest3\n"));
        Assert.assertEquals(ImmutableList.of("test1", "test2", "test3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeBasicAsciiCRLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, CRLF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "test1\r\ntest2\r\ntest3\r\n"));
        Assert.assertEquals(ImmutableList.of("test1", "test2", "test3"), decoded);
    }

    @Test
    public void testDecodeBasicAsciiTail() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "test1"));
        Assert.assertEquals(ImmutableList.of("test1"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "t", "1", "\n", "t", "2"));
        Assert.assertEquals(ImmutableList.of("t1", "t2"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksCRLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, CRLF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "t", "1", "\r\n", "t", "2", "\r", "\n", "t3"));
        Assert.assertEquals(ImmutableList.of("t1", "t2", "t3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeBasicUTF8() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "\u3066\u3059\u30681\n\u30c6\u30b9\u30c82\n\u3066\u3059\u30683\n"));
        Assert.assertEquals(ImmutableList.of("???1", "???2", "???3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeBasicUTF8Tail() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "???1"));
        Assert.assertEquals(ImmutableList.of("???1"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksUTF8LF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "?", "1", "\n", "?", "2"));
        Assert.assertEquals(ImmutableList.of("?1", "?2"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksUTF8CRLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, CRLF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "?", "1", "\r\n", "?", "2", "\r", "\n", "?3"));
        Assert.assertEquals(ImmutableList.of("?1", "?2", "?3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeBasicUTF16LE() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_16LE, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_16LE, "\u3066\u3059\u30681\n\u30c6\u30b9\u30c82\n\u3066\u3059\u30683\n"));
        Assert.assertEquals(ImmutableList.of("???1", "???2", "???3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeBasicUTF16LETail() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_16LE, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_16LE, "???1"));
        Assert.assertEquals(ImmutableList.of("???1"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksUTF16LELF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_16LE, Newline.LF, TestLineDecoder.bufferList(StandardCharsets.UTF_16LE, "?", "1", "\n", "?", "2"));
        Assert.assertEquals(ImmutableList.of("?1", "?2"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksUTF16LECRLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_16LE, CRLF, TestLineDecoder.bufferList(StandardCharsets.UTF_16LE, "?", "1", "\r\n", "?", "2", "\r", "\n", "?3"));
        Assert.assertEquals(ImmutableList.of("?1", "?2", "?3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeBasicMS932() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(Charset.forName("ms932"), Newline.LF, TestLineDecoder.bufferList(Charset.forName("ms932"), "\u3066\u3059\u30681\n\u30c6\u30b9\u30c82\n\u3066\u3059\u30683\n"));
        Assert.assertEquals(ImmutableList.of("???1", "???2", "???3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeBasicMS932Tail() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(Charset.forName("ms932"), Newline.LF, TestLineDecoder.bufferList(Charset.forName("ms932"), "???1"));
        Assert.assertEquals(ImmutableList.of("???1"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksMS932LF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(Charset.forName("ms932"), Newline.LF, TestLineDecoder.bufferList(Charset.forName("ms932"), "?", "1", "\n", "?", "2"));
        Assert.assertEquals(ImmutableList.of("?1", "?2"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeChunksMS932CRLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(Charset.forName("ms932"), CRLF, TestLineDecoder.bufferList(Charset.forName("ms932"), "?", "1", "\r\n", "?", "2", "\r", "\n", "?3"));
        Assert.assertEquals(ImmutableList.of("?1", "?2", "?3"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeWithLineDelimiterRecognizedCR() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, CRLF, CR, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "test1\r\ntest2\rtest3\ntest4"));
        Assert.assertEquals(ImmutableList.of("test1\r\ntest2", "test3\ntest4"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeWithLineDelimiterRecognizedLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, CRLF, LF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "test1\r\ntest2\rtest3\ntest4"));
        Assert.assertEquals(ImmutableList.of("test1\r\ntest2\rtest3", "test4"), decoded);
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @Test
    public void testDecodeWithLineDelimiterRecognizedCRLF() throws Exception {
        List<String> decoded = TestLineDecoder.doDecode(StandardCharsets.UTF_8, CRLF, LineDelimiter.CRLF, TestLineDecoder.bufferList(StandardCharsets.UTF_8, "test1\r\ntest2\rtest3\ntest4"));
        Assert.assertEquals(ImmutableList.of("test1", "test2\rtest3\ntest4"), decoded);
    }
}

