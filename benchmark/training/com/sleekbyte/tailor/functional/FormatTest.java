package com.sleekbyte.tailor.functional;


import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sleekbyte.tailor.Tailor;
import com.sleekbyte.tailor.format.Format;
import com.sleekbyte.tailor.format.HTMLFormatter;
import com.sleekbyte.tailor.output.Printer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link Tailor} output formats.
 */
@RunWith(MockitoJUnitRunner.class)
public final class FormatTest {
    private static final String TEST_INPUT_DIR = "src/test/swift/com/sleekbyte/tailor/functional";

    private static final String NEWLINE_REGEX = "\\r?\\n";

    private static final String NEWLINE_PATTERN = "\n";

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    protected ByteArrayOutputStream outContent;

    protected File inputFile;

    protected List<String> expectedMessages;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testXcodeFormat() throws IOException {
        Format format = Format.XCODE;
        final String[] command = new String[]{ "--format", format.getName(), "--no-color", "--only=upper-camel-case", inputFile.getPath() };
        expectedMessages.addAll(getExpectedMsgs().stream().map(( msg) -> Printer.genOutputStringForTest(msg.getRule(), inputFile.getName(), msg.getLineNumber(), msg.getColumnNumber(), msg.getSeverity(), msg.getMessage())).collect(Collectors.toList()));
        Tailor.main(command);
        List<String> actualOutput = new ArrayList<>();
        String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(FormatTest.NEWLINE_REGEX);
        // Skip first four lines for file header, last two lines for summary
        msgs = Arrays.copyOfRange(msgs, 4, ((msgs.length) - 2));
        for (String msg : msgs) {
            String truncatedMsg = msg.substring(msg.indexOf(inputFile.getName()));
            actualOutput.add(truncatedMsg);
        }
        Assert.assertArrayEquals(outContent.toString(Charset.defaultCharset().name()), this.expectedMessages.toArray(), actualOutput.toArray());
    }

    @Test
    public void testJSONFormat() throws IOException {
        Format format = Format.JSON;
        final String[] command = new String[]{ "--format", format.getName(), "--no-color", "--only=upper-camel-case", inputFile.getPath() };
        Map<String, Object> expectedOutput = getJSONMessages();
        Tailor.main(command);
        List<String> expected = new ArrayList<>();
        List<String> actual = new ArrayList<>();
        expectedMessages.addAll(Arrays.asList(((FormatTest.GSON.toJson(expectedOutput)) + (System.lineSeparator())).split(FormatTest.NEWLINE_REGEX)));
        for (String msg : expectedMessages) {
            String strippedMsg = msg.replaceAll(inputFile.getCanonicalPath(), "");
            expected.add(strippedMsg);
        }
        String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(FormatTest.NEWLINE_REGEX);
        for (String msg : msgs) {
            String strippedMsg = msg.replaceAll(inputFile.getCanonicalPath(), "");
            actual.add(strippedMsg);
        }
        Assert.assertArrayEquals(outContent.toString(Charset.defaultCharset().name()), expected.toArray(), actual.toArray());
    }

    @Test
    public void testXcodeConfigOption() throws IOException {
        File configurationFile = xcodeFormatConfigFile(".tailor.yml");
        final String[] command = new String[]{ "--config", configurationFile.getAbsolutePath(), "--no-color", "--only=upper-camel-case", inputFile.getPath() };
        expectedMessages.addAll(getExpectedMsgs().stream().map(( msg) -> Printer.genOutputStringForTest(msg.getRule(), inputFile.getName(), msg.getLineNumber(), msg.getColumnNumber(), msg.getSeverity(), msg.getMessage())).collect(Collectors.toList()));
        Tailor.main(command);
        List<String> actualOutput = new ArrayList<>();
        String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(FormatTest.NEWLINE_REGEX);
        // Skip first four lines for file header, last two lines for summary
        msgs = Arrays.copyOfRange(msgs, 4, ((msgs.length) - 2));
        for (String msg : msgs) {
            String truncatedMsg = msg.substring(msg.indexOf(inputFile.getName()));
            actualOutput.add(truncatedMsg);
        }
        Assert.assertArrayEquals(outContent.toString(Charset.defaultCharset().name()), this.expectedMessages.toArray(), actualOutput.toArray());
    }

    @Test
    public void testHTMLFormat() throws IOException {
        Format format = Format.HTML;
        final String[] command = new String[]{ "--format", format.getName(), "--no-color", "--only=upper-camel-case", inputFile.getPath() };
        Map<String, Object> expectedOutput = getHTMLMessages();
        Tailor.main(command);
        List<String> expected = new ArrayList<>();
        List<String> actual = new ArrayList<>();
        Mustache mustache = new DefaultMustacheFactory().compile(new InputStreamReader(HTMLFormatter.class.getResourceAsStream("index.html"), Charset.defaultCharset()), "index.html");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mustache.execute(new OutputStreamWriter(baos, Charset.defaultCharset()), expectedOutput).flush();
        expectedMessages.addAll(Arrays.asList(baos.toString(Charset.defaultCharset().name()).split(FormatTest.NEWLINE_REGEX)));
        for (String msg : expectedMessages) {
            String strippedMsg = msg.replaceAll(inputFile.getCanonicalPath(), "");
            expected.add(strippedMsg);
        }
        String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(FormatTest.NEWLINE_REGEX);
        for (String msg : msgs) {
            String strippedMsg = msg.replaceAll(inputFile.getCanonicalPath(), "");
            actual.add(strippedMsg);
        }
        Assert.assertArrayEquals(outContent.toString(Charset.defaultCharset().name()), expected.toArray(), actual.toArray());
    }
}

