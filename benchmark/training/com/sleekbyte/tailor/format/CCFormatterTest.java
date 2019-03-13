package com.sleekbyte.tailor.format;


import ExitCode.SUCCESS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sleekbyte.tailor.common.ColorSettings;
import com.sleekbyte.tailor.common.Rules;
import com.sleekbyte.tailor.common.Severity;
import com.sleekbyte.tailor.output.ViolationMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class CCFormatterTest {
    private static final String WARNING_MSG = "this is a warning";

    private static final String ERROR_MSG = "this is an error";

    private static final ColorSettings colorSettings = new ColorSettings(false, false);

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    protected ByteArrayOutputStream outContent;

    private File inputFile = new File("abc.swift");

    private CCFormatter formatter;

    @Test
    public void testDisplayMessages() throws IOException {
        List<ViolationMessage> messages = new ArrayList<>();
        messages.add(new ViolationMessage(Rules.MULTIPLE_IMPORTS, inputFile.getCanonicalPath(), 1, 0, Severity.WARNING, CCFormatterTest.WARNING_MSG));
        messages.add(new ViolationMessage(Rules.TERMINATING_SEMICOLON, inputFile.getCanonicalPath(), 1, 18, Severity.ERROR, CCFormatterTest.ERROR_MSG));
        Collections.sort(messages);
        formatter.displayViolationMessages(messages, inputFile);
        Assert.assertEquals(expectedOutput(messages), outContent.toString(Charset.defaultCharset().name()));
    }

    @Test
    public void testDisplayParseErrorMessage() throws IOException {
        formatter.displayParseErrorMessage(inputFile);
        Assert.assertThat(outContent.toString(Charset.defaultCharset().name()), Matchers.isEmptyString());
    }

    @Test
    public void testDisplaySummary() throws IOException {
        final long files = 5;
        final long skipped = 1;
        final long errors = 7;
        final long warnings = 4;
        formatter.displaySummary(files, skipped, errors, warnings);
        Assert.assertThat(outContent.toString(Charset.defaultCharset().name()), Matchers.isEmptyString());
    }

    @Test
    public void testExitStatusWithNoErrors() {
        Assert.assertEquals(SUCCESS, formatter.getExitStatus(0));
    }

    @Test
    public void testExitStatusWithSomeErrors() {
        Assert.assertEquals(SUCCESS, formatter.getExitStatus(10));
    }
}

