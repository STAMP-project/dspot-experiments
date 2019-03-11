package com.sleekbyte.tailor.cli;


import com.sleekbyte.tailor.Tailor;
import com.sleekbyte.tailor.common.ExitCode;
import com.sleekbyte.tailor.common.Rules;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link Tailor} CLI options.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CliOptionsTest {
    private static final String NEWLINE_REGEX = "\\r?\\n";

    private static final String TEST_DIR = "src/test/swift/com/sleekbyte/tailor/functional/";

    private ByteArrayOutputStream outContent;

    private ByteArrayOutputStream errContent;

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testHelpMessage() throws IOException {
        exit.expectSystemExitWithStatus(ExitCode.success());
        String[] command = new String[]{ "--help" };
        exit.checkAssertionAfterwards(() -> {
            String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(NEWLINE_REGEX);
            String actualUsageMessage = msgs[0];
            String expectedUsageMessage = "Usage: " + Messages.CMD_LINE_SYNTAX;
            assertEquals(expectedUsageMessage, actualUsageMessage);
        });
        Tailor.main(command);
    }

    @Test
    public void testVersionOutput() throws IOException {
        exit.expectSystemExitWithStatus(ExitCode.success());
        String[] command = new String[]{ "--version" };
        exit.checkAssertionAfterwards(() -> {
            String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(NEWLINE_REGEX);
            String actualVersion = msgs[0];
            assertTrue("Version number should match MAJOR.MINOR.PATCH format from http://semver.org.", actualVersion.matches("\\d+\\.\\d+\\.\\d+"));
        });
        Tailor.main(command);
    }

    @Test
    public void testShowRules() throws IOException {
        exit.expectSystemExitWithStatus(ExitCode.success());
        String[] command = new String[]{ "--show-rules" };
        Tailor.main(command);
        Assert.assertTrue(((Rules.values().length) > 0));
    }

    @Test
    public void testNoSourceInputAndNoConfigFile() throws IOException {
        exit.expectSystemExitWithStatus(ExitCode.failure());
        String[] command = new String[]{ "" };
        exit.checkAssertionAfterwards(() -> {
            String[] msgs = errContent.toString(Charset.defaultCharset().name()).split(NEWLINE_REGEX);
            String actualErrorMessage = msgs[0];
            String expectedErrorMessage = Messages.NO_SWIFT_FILES_FOUND;
            assertEquals(expectedErrorMessage, actualErrorMessage);
        });
        Tailor.main(command);
    }

    @Test
    public void testTwoSourceInputFiles() throws IOException {
        File inputFile1 = new File(((CliOptionsTest.TEST_DIR) + "/UpperCamelCaseTest.swift"));
        File inputFile2 = new File(((CliOptionsTest.TEST_DIR) + "/LowerCamelCaseTest.swift"));
        String[] command = new String[]{ "--no-color", inputFile1.getPath(), inputFile2.getPath() };
        Tailor.main(command);
        String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(CliOptionsTest.NEWLINE_REGEX);
        String actualSummary = msgs[((msgs.length) - 1)];
        Assert.assertTrue(actualSummary.startsWith("Analyzed 2 files, skipped 0 files"));
    }

    @Test
    public void testListFiles() throws IOException {
        exit.expectSystemExitWithStatus(ExitCode.success());
        String inputPath = Paths.get(CliOptionsTest.TEST_DIR).toString();
        String[] command = new String[]{ "--list-files", inputPath };
        exit.checkAssertionAfterwards(() -> {
            String[] msgs = outContent.toString(Charset.defaultCharset().name()).split(NEWLINE_REGEX);
            assertTrue((msgs.length > 1));
            String actualOutput = msgs[0];
            String expectedOutput = Messages.FILES_TO_BE_ANALYZED;
            assertEquals(expectedOutput, actualOutput);
        });
        Tailor.main(command);
    }

    @Test
    public void testPurgeWithInvalidInput() throws IOException {
        exit.expectSystemExitWithStatus(ExitCode.failure());
        String inputPath = Paths.get(CliOptionsTest.TEST_DIR).toString();
        String[] command = new String[]{ "--purge=-1", inputPath };
        exit.checkAssertionAfterwards(() -> assertTrue(errContent.toString().startsWith("Invalid number of files specified for purge")));
        Tailor.main(command);
    }
}

