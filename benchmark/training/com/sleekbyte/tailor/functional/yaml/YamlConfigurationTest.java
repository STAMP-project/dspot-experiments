package com.sleekbyte.tailor.functional.yaml;


import Rules.TERMINATING_SEMICOLON;
import Rules.TRAILING_CLOSURE;
import Rules.UPPER_CAMEL_CASE;
import com.sleekbyte.tailor.Tailor;
import com.sleekbyte.tailor.common.ExitCode;
import com.sleekbyte.tailor.common.Messages;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link Tailor} configuration file flow.
 * Test config file functionality.
 */
@RunWith(MockitoJUnitRunner.class)
public final class YamlConfigurationTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    protected static final String TEST_INPUT_DIR = "src/test/swift/com/sleekbyte/tailor/functional/yaml";

    protected static final String NEWLINE_REGEX = "\\r?\\n";

    private static final String YAML_TEST_1 = "YamlTest1.swift";

    private static final String YAML_TEST_2 = "YamlTest2.swift";

    protected ByteArrayOutputStream outContent;

    protected ByteArrayOutputStream errContent;

    protected File inputFile;

    protected List<String> expectedMessages;

    @Test
    public void testIncludeOption() throws IOException {
        // Add expected output
        addExpectedMsg(3, 7, UPPER_CAMEL_CASE, (((Messages.CLASS) + (Messages.NAMES)) + (Messages.UPPER_CAMEL_CASE)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(7, 7, UPPER_CAMEL_CASE, (((Messages.CLASS) + (Messages.NAMES)) + (Messages.UPPER_CAMEL_CASE)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(12, 15, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(13, 18, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(17, 15, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(18, 18, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(20, 26, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(21, 16, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(25, 63, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(27, 71, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(29, 57, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(31, 4, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        // Create config file that wants to only analyze YAML_TEST_1.swift
        File configurationFile = includeOptionConfig(".tailor.yml");
        String[] command = new String[]{ "--config", configurationFile.getAbsolutePath(), "--no-color" };
        runTest(command);
    }

    @Test
    public void testExceptOption() throws IOException {
        // Add expected output
        addExpectedMsg(8, 33, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_2);
        addExpectedMsg(9, 16, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_2);
        addExpectedMsg(10, 23, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_2);
        addExpectedMsg(11, 39, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_2);
        addExpectedMsg(12, 2, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_2);
        addExpectedMsg(14, 23, TRAILING_CLOSURE, ((Messages.CLOSURE) + (Messages.TRAILING_CLOSURE)), YamlConfigurationTest.YAML_TEST_2);
        addExpectedMsg(18, 2, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_2);
        // Create config file that will only analyze YAML_TEST_2.swift for violations other than upper-camel-case
        File configurationFile = exceptOptionConfig(".tailor.yml");
        String[] command = new String[]{ "--config", configurationFile.getAbsolutePath() };
        runTest(command);
    }

    @Test
    public void testOnlyExceptOptionPrecedence() throws IOException {
        // Add expected output
        addExpectedMsg(3, 7, UPPER_CAMEL_CASE, (((Messages.CLASS) + (Messages.NAMES)) + (Messages.UPPER_CAMEL_CASE)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(7, 7, UPPER_CAMEL_CASE, (((Messages.CLASS) + (Messages.NAMES)) + (Messages.UPPER_CAMEL_CASE)), YamlConfigurationTest.YAML_TEST_1);
        // Create config file that wants to only analyze YAML_TEST_1.swift for upper-camel-case violations
        // Test for precedence of `only` over `except`
        File configurationFile = onlyAndExceptPrecedenceConfig(".tailor.yml");
        String[] command = new String[]{ "--config", configurationFile.getAbsolutePath(), "--no-color" };
        runTest(command);
    }

    @Test
    public void testNoColorOption() throws IOException {
        // Add expected output
        addExpectedMsg(3, 7, UPPER_CAMEL_CASE, (((Messages.CLASS) + (Messages.NAMES)) + (Messages.UPPER_CAMEL_CASE)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(7, 7, UPPER_CAMEL_CASE, (((Messages.CLASS) + (Messages.NAMES)) + (Messages.UPPER_CAMEL_CASE)), YamlConfigurationTest.YAML_TEST_1);
        File configurationFile = noColorConfig(".tailor.yml");
        String[] command = new String[]{ "--config", configurationFile.getAbsolutePath() };
        runTest(command);
    }

    @Test
    public void testCliAndConfigFilePrecedence() throws IOException {
        addExpectedMsg(12, 15, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(13, 18, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(17, 15, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(18, 18, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(20, 26, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(21, 16, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(25, 63, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(27, 71, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(29, 57, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(31, 4, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        File configurationFile = onlyAndExceptPrecedenceConfig(".tailor.yml");
        String[] command = new String[]{ "--only", "terminating-semicolon", "--config", configurationFile.getAbsolutePath(), "--no-color" };
        runTest(command);
    }

    @Test
    public void testPurge() throws IOException {
        addExpectedMsg(12, 15, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(13, 18, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(17, 15, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(18, 18, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(20, 26, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(21, 16, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(25, 63, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(27, 71, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(29, 57, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        addExpectedMsg(31, 4, TERMINATING_SEMICOLON, ((Messages.STATEMENTS) + (Messages.SEMICOLON)), YamlConfigurationTest.YAML_TEST_1);
        File configurationFile = purgeConfig(".tailor.yml");
        String[] command = new String[]{ "--only", "terminating-semicolon", "--config", configurationFile.getAbsolutePath(), "--no-color" };
        runTest(command);
    }

    @Test
    public void testInvalidPurge() throws IOException {
        exit.expectSystemExitWithStatus(ExitCode.failure());
        exit.checkAssertionAfterwards(() -> assertTrue("STDERR should contain error message", errContent.toString().contains("Invalid number of files specified for purge in config file")));
        File configurationFile = invalidPurgeConfig(".tailor.yml");
        String[] command = new String[]{ "--only", "terminating-semicolon", "--config", configurationFile.getAbsolutePath(), "--no-color" };
        Tailor.main(command);
    }
}

