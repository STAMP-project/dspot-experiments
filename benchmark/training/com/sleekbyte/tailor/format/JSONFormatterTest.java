package com.sleekbyte.tailor.format;


import ExitCode.FAILURE;
import ExitCode.SUCCESS;
import Messages.ANALYZED_KEY;
import Messages.ERRORS_KEY;
import Messages.FILES_KEY;
import Messages.PARSED_KEY;
import Messages.PATH_KEY;
import Messages.SKIPPED_KEY;
import Messages.SUMMARY_KEY;
import Messages.VIOLATIONS_KEY;
import Messages.WARNINGS_KEY;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class JSONFormatterTest {
    private static final String WARNING_MSG = "this is a warning";

    private static final String ERROR_MSG = "this is an error";

    private static final ColorSettings colorSettings = new ColorSettings(false, false);

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    protected ByteArrayOutputStream outContent;

    private File inputFile = new File("abc.swift");

    private JSONFormatter formatter;

    @Test
    public void testDisplayMessages() throws IOException {
        List<ViolationMessage> messages = new ArrayList<>();
        messages.add(new ViolationMessage(Rules.LOWER_CAMEL_CASE, inputFile.getCanonicalPath(), 10, 12, Severity.WARNING, JSONFormatterTest.WARNING_MSG));
        messages.add(new ViolationMessage(Rules.UPPER_CAMEL_CASE, inputFile.getCanonicalPath(), 11, 14, Severity.ERROR, JSONFormatterTest.ERROR_MSG));
        Collections.sort(messages);
        formatter.displayViolationMessages(messages, inputFile);
        List<Map<String, Object>> actualOutput = formatter.getFiles();
        Assert.assertEquals(expectedOutput(messages), actualOutput);
    }

    @Test
    public void testDisplayParseErrorMessage() throws IOException {
        formatter.displayParseErrorMessage(inputFile);
        Map<String, Object> output = new HashMap<>();
        output.put(PATH_KEY, inputFile.getCanonicalPath());
        output.put(PARSED_KEY, false);
        output.put(VIOLATIONS_KEY, new ArrayList());
        List<Object> files = new ArrayList<>();
        files.add(output);
        Assert.assertEquals(files, formatter.getFiles());
    }

    @Test
    public void testDisplaySummary() throws IOException {
        final long files = 5;
        final long skipped = 1;
        final long errors = 7;
        final long warnings = 4;
        final long analyzed = files - skipped;
        final long violations = errors + warnings;
        formatter.displaySummary(files, skipped, errors, warnings);
        Map<String, Object> summary = new HashMap<>();
        summary.put(ANALYZED_KEY, analyzed);
        summary.put(SKIPPED_KEY, skipped);
        summary.put(VIOLATIONS_KEY, violations);
        summary.put(ERRORS_KEY, errors);
        summary.put(WARNINGS_KEY, warnings);
        Map<String, Object> output = new LinkedHashMap<>();
        output.put(FILES_KEY, new ArrayList());
        output.put(SUMMARY_KEY, summary);
        Assert.assertEquals(((JSONFormatterTest.GSON.toJson(output)) + (System.lineSeparator())), outContent.toString(Charset.defaultCharset().name()));
    }

    @Test
    public void testSuccessExitStatus() {
        Assert.assertEquals(SUCCESS, formatter.getExitStatus(0));
    }

    @Test
    public void testFailureExitStatus() {
        Assert.assertEquals(FAILURE, formatter.getExitStatus(10));
    }
}

