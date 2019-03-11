/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util.command;


import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.ProcessManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import static com.thoughtworks.go.util.LogFixture.logFixtureFor;


@RunWith(JunitExtRunner.class)
public class CommandLineTest {
    private static final String DBL_QUOTE = "\"";

    private static final String EXEC_WITH_SPACES = "dummyExecutable with spaces";

    private static final String ARG_SPACES_NOQUOTES = "arg1='spaced single quoted value'";

    private static final String ARG_NOSPACES = "arg2=value2";

    private static final String ARG_SPACES = "arg3=value for 3";

    private File subFolder;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testToStringWithSeparator() throws Exception {
        final String separator = "], [";
        Assert.assertEquals("", CommandLine.toString(null, false, separator));
        Assert.assertEquals(CommandLineTest.ARG_SPACES_NOQUOTES, CommandLine.toString(new String[]{ CommandLineTest.ARG_SPACES_NOQUOTES }, false, separator));
        Assert.assertEquals((((CommandLineTest.ARG_SPACES_NOQUOTES) + separator) + (CommandLineTest.ARG_NOSPACES)), CommandLine.toString(new String[]{ CommandLineTest.ARG_SPACES_NOQUOTES, CommandLineTest.ARG_NOSPACES }, false, separator));
        Assert.assertEquals((((((CommandLineTest.ARG_SPACES_NOQUOTES) + separator) + (CommandLineTest.ARG_NOSPACES)) + separator) + (CommandLineTest.ARG_SPACES)), CommandLine.toString(new String[]{ CommandLineTest.ARG_SPACES_NOQUOTES, CommandLineTest.ARG_NOSPACES, CommandLineTest.ARG_SPACES }, false, separator));
    }

    @Test
    public void testToStrings() throws Exception {
        final CommandLine cl = CommandLine.createCommandLine(CommandLineTest.EXEC_WITH_SPACES).withEncoding("utf-8");
        cl.withArg(CommandLineTest.ARG_SPACES_NOQUOTES);
        cl.withArg(CommandLineTest.ARG_NOSPACES);
        cl.withArg(CommandLineTest.ARG_SPACES);
        final String expectedWithQuotes = ((((((((((((CommandLineTest.DBL_QUOTE) + (CommandLineTest.EXEC_WITH_SPACES)) + (CommandLineTest.DBL_QUOTE)) + " ") + (CommandLineTest.DBL_QUOTE)) + (CommandLineTest.ARG_SPACES_NOQUOTES)) + (CommandLineTest.DBL_QUOTE)) + " ") + (CommandLineTest.ARG_NOSPACES)) + " ") + (CommandLineTest.DBL_QUOTE)) + (CommandLineTest.ARG_SPACES)) + (CommandLineTest.DBL_QUOTE);
        Assert.assertEquals(expectedWithQuotes, cl.toString());
        Assert.assertEquals(expectedWithQuotes.replaceAll(CommandLineTest.DBL_QUOTE, ""), cl.toStringForDisplay());
        Assert.assertEquals("Did the impl of CommandLine.toString() change?", expectedWithQuotes, (cl + ""));
    }

    @Test
    public void testToStringMisMatchedQuote() {
        final CommandLine cl2 = CommandLine.createCommandLine(CommandLineTest.EXEC_WITH_SPACES).withEncoding("utf-8");
        final String argWithMismatchedDblQuote = "argMisMatch=\'singlequoted\"WithMismatchedDblQuote\'";
        cl2.withArg(argWithMismatchedDblQuote);
        Assert.assertEquals("Should escape double quotes inside the string", (((((((CommandLineTest.DBL_QUOTE) + (CommandLineTest.EXEC_WITH_SPACES)) + (CommandLineTest.DBL_QUOTE)) + " ") + (CommandLineTest.DBL_QUOTE)) + (argWithMismatchedDblQuote.replaceAll("\"", Matcher.quoteReplacement("\\\"")))) + (CommandLineTest.DBL_QUOTE)), cl2.toString());
    }

    @Test
    public void shouldReportPasswordsOnTheLogAsStars() {
        CommandLine line = CommandLine.createCommandLine("notexist").withArg(new PasswordArgument("secret")).withEncoding("utf-8");
        Assert.assertThat(line.toString(), not(containsString("secret")));
    }

    @Test
    public void shouldLogPasswordsOnTheLogAsStars() {
        try (LogFixture logFixture = logFixtureFor(ProcessManager.class, Level.DEBUG)) {
            CommandLine line = CommandLine.createCommandLine("notexist").withArg(new PasswordArgument("secret")).withEncoding("utf-8");
            try {
                line.runOrBomb(null);
            } catch (Exception e) {
                // ignored
            }
            Assert.assertThat(logFixture.getLog(), containsString("notexist ******"));
        }
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldNotLogPasswordsFromStream() {
        try (LogFixture logFixture = logFixtureFor(com.thoughtworks.go.util.CommandLine.class, Level.DEBUG)) {
            CommandLine line = CommandLine.createCommandLine("/bin/echo").withArg("=>").withArg(new PasswordArgument("secret")).withEncoding("utf-8");
            line.runOrBomb(null);
            Assert.assertThat(logFixture.getLog(), not(containsString("secret")));
            Assert.assertThat(logFixture.getLog(), containsString("=> ******"));
        }
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldNotLogPasswordsOnExceptionThrown() throws IOException {
        File dir = temporaryFolder.newFolder();
        File file = new File(dir, "test.sh");
        FileOutputStream out = new FileOutputStream(file);
        out.write("echo $1 && exit 10".getBytes());
        out.close();
        CommandLine line = CommandLine.createCommandLine("/bin/sh").withArg(file.getAbsolutePath()).withArg(new PasswordArgument("secret")).withEncoding("utf-8");
        try {
            line.runOrBomb(null);
        } catch (CommandLineException e) {
            Assert.assertThat(e.getMessage(), not(containsString("secret")));
        }
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldLogPasswordsOnOutputAsStarsUnderLinux() throws IOException {
        CommandLine line = CommandLine.createCommandLine("echo").withArg("My Password is:").withArg(new PasswordArgument("secret")).withEncoding("utf-8");
        InMemoryStreamConsumer output = new InMemoryStreamConsumer();
        InMemoryStreamConsumer displayOutputStreamConsumer = InMemoryStreamConsumer.inMemoryConsumer();
        ProcessWrapper processWrapper = line.execute(output, new EnvironmentVariableContext(), null);
        processWrapper.waitForExit();
        Assert.assertThat(output.getAllOutput(), containsString("secret"));
        Assert.assertThat(displayOutputStreamConsumer.getAllOutput(), not(containsString("secret")));
    }

    @Test
    @RunIf(value = OSChecker.class, arguments = OSChecker.WINDOWS)
    public void shouldLogPasswordsOnOutputAsStarsUnderWindows() throws IOException {
        CommandLine line = CommandLine.createCommandLine("cmd").withEncoding("utf-8").withArg("/c").withArg("echo").withArg("My Password is:").withArg(new PasswordArgument("secret"));
        InMemoryStreamConsumer output = new InMemoryStreamConsumer();
        InMemoryStreamConsumer displayOutputStreamConsumer = InMemoryStreamConsumer.inMemoryConsumer();
        ProcessWrapper processWrapper = line.execute(output, new EnvironmentVariableContext(), null);
        processWrapper.waitForExit();
        Assert.assertThat(output.getAllOutput(), containsString("secret"));
        Assert.assertThat(displayOutputStreamConsumer.getAllOutput(), not(containsString("secret")));
    }

    @Test
    public void shouldShowPasswordsInToStringForDisplayAsStars() throws IOException {
        CommandLine line = CommandLine.createCommandLine("echo").withArg("My Password is:").withArg(new PasswordArgument("secret")).withEncoding("utf-8");
        Assert.assertThat(line.toStringForDisplay(), not(containsString("secret")));
    }

    @Test
    public void shouldShowPasswordsInDescribeAsStars() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("password1", "secret");
        map.put("password2", "secret");
        CommandLine line = CommandLine.createCommandLine("echo").withArg("My Password is:").withEnv(map).withArg(new PasswordArgument("secret")).withArg(new PasswordArgument("new-pwd")).withEncoding("utf-8");
        line.addInput(new String[]{ "my pwd is: new-pwd " });
        Assert.assertThat(line.describe(), not(containsString("secret")));
        Assert.assertThat(line.describe(), not(containsString("new-pwd")));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldLogPasswordsOnEnvironemntAsStarsUnderLinux() throws IOException {
        CommandLine line = CommandLine.createCommandLine("echo").withArg("My Password is:").withArg("secret").withArg(new PasswordArgument("secret")).withEncoding("utf-8");
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        environmentVariableContext.setProperty("ENV_PASSWORD", "secret", false);
        InMemoryStreamConsumer output = new InMemoryStreamConsumer();
        InMemoryStreamConsumer forDisplay = InMemoryStreamConsumer.inMemoryConsumer();
        ProcessWrapper processWrapper = line.execute(output, environmentVariableContext, null);
        processWrapper.waitForExit();
        Assert.assertThat(forDisplay.getAllOutput(), not(containsString("secret")));
        Assert.assertThat(output.getAllOutput(), containsString("secret"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldBeAbleToSpecifyEncoding() throws IOException {
        String chrisWasHere = "?????";
        CommandLine line = CommandLine.createCommandLine("echo").withArg(chrisWasHere).withEncoding("UTF-8");
        InMemoryStreamConsumer output = new InMemoryStreamConsumer();
        ProcessWrapper processWrapper = line.execute(output, new EnvironmentVariableContext(), null);
        processWrapper.waitForExit();
        Assert.assertThat(output.getAllOutput(), containsString(chrisWasHere));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldBeAbleToRunCommandsInSubdirectories() throws IOException {
        File shellScript = createScript("hello-world.sh", "echo ${PWD}");
        Assert.assertThat(shellScript.setExecutable(true), is(true));
        CommandLine line = CommandLine.createCommandLine("./hello-world.sh").withWorkingDir(subFolder).withEncoding("utf-8");
        InMemoryStreamConsumer out = new InMemoryStreamConsumer();
        line.execute(out, new EnvironmentVariableContext(), null).waitForExit();
        Assert.assertThat(out.getAllOutput().trim(), endsWith("subFolder"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldBeAbleToRunCommandsInSubdirectoriesWithNoWorkingDir() throws IOException {
        File shellScript = createScript("hello-world.sh", "echo 'Hello World!'");
        Assert.assertThat(shellScript.setExecutable(true), is(true));
        CommandLine line = CommandLine.createCommandLine("subFolder/hello-world.sh").withWorkingDir(temporaryFolder.getRoot()).withEncoding("utf-8");
        InMemoryStreamConsumer out = new InMemoryStreamConsumer();
        line.execute(out, new EnvironmentVariableContext(), null).waitForExit();
        Assert.assertThat(out.getAllOutput(), containsString("Hello World!"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldNotRunLocalCommandsThatAreNotExecutable() throws IOException {
        createScript("echo", "echo 'this should not be here'");
        CommandLine line = CommandLine.createCommandLine("echo").withArg("Using the REAL echo").withWorkingDir(subFolder).withEncoding("utf-8");
        InMemoryStreamConsumer out = new InMemoryStreamConsumer();
        line.execute(out, new EnvironmentVariableContext(), null).waitForExit();
        Assert.assertThat(out.getAllOutput(), containsString("Using the REAL echo"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldBeAbleToRunCommandsFromRelativeDirectories() throws IOException {
        File shellScript = temporaryFolder.newFile("hello-world.sh");
        FileUtils.writeStringToFile(shellScript, "echo ${PWD}", StandardCharsets.UTF_8);
        Assert.assertThat(shellScript.setExecutable(true), is(true));
        CommandLine line = CommandLine.createCommandLine("../hello-world.sh").withWorkingDir(subFolder).withEncoding("utf-8");
        InMemoryStreamConsumer out = new InMemoryStreamConsumer();
        line.execute(out, new EnvironmentVariableContext(), null).waitForExit();
        Assert.assertThat(out.getAllOutput().trim(), endsWith("subFolder"));
    }

    @Test
    public void shouldReturnEchoResult() throws Exception {
        if (SystemUtils.IS_OS_WINDOWS) {
            ConsoleResult result = CommandLine.createCommandLine("cmd").withEncoding("utf-8").runOrBomb(null);
            Assert.assertThat(result.outputAsString(), containsString("Windows"));
        } else {
            String expectedValue = "my input";
            ConsoleResult result = CommandLine.createCommandLine("echo").withEncoding("utf-8").withArgs(expectedValue).runOrBomb(null);
            Assert.assertThat(result.outputAsString(), is(expectedValue));
        }
    }

    @Test(expected = Exception.class)
    public void shouldReturnThrowExceptionWhenCommandNotExist() throws Exception {
        CommandLine.createCommandLine("something").withEncoding("utf-8").runOrBomb(null);
    }

    @Test
    public void shouldGetTheCommandFromCommandlineAsIs() throws IOException {
        String file = "originalCommand";
        CommandLine command = CommandLine.createCommandLine(file);
        command.setWorkingDir(new File("."));
        String[] commandLineArgs = command.getCommandLine();
        Assert.assertThat(commandLineArgs[0], is(file));
    }

    @Test
    public void shouldPrefixStderrOutput() {
        CommandLine line = CommandLine.createCommandLine("git").withArg("clone").withArg("https://foo/bar").withEncoding("utf-8");
        InMemoryStreamConsumer output = new InMemoryStreamConsumer();
        ProcessWrapper processWrapper = line.execute(output, new EnvironmentVariableContext(), null);
        processWrapper.waitForExit();
        Assert.assertThat(output.getAllOutput(), containsString("STDERR: "));
    }
}

