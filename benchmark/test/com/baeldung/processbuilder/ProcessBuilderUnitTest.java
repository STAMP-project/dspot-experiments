package com.baeldung.processbuilder;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static java.lang.ProcessBuilder.Redirect.appendTo;


public class ProcessBuilderUnitTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void givenProcessBuilder_whenInvokeStart_thenSuccess() throws IOException, InterruptedException, ExecutionException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-version");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        List<String> results = readOutput(process.getInputStream());
        Assert.assertThat("Results should not be empty", results, Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat("Results should contain java version: ", results, Matchers.hasItem(Matchers.containsString("java version")));
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
    }

    @Test
    public void givenProcessBuilder_whenModifyEnvironment_thenSuccess() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        Map<String, String> environment = processBuilder.environment();
        environment.forEach(( key, value) -> System.out.println((key + value)));
        environment.put("GREETING", "Hola Mundo");
        List<String> command = getGreetingCommand();
        processBuilder.command(command);
        Process process = processBuilder.start();
        List<String> results = readOutput(process.getInputStream());
        Assert.assertThat("Results should not be empty", results, Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat("Results should contain a greeting ", results, Matchers.hasItem(Matchers.containsString("Hola Mundo")));
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
    }

    @Test
    public void givenProcessBuilder_whenModifyWorkingDir_thenSuccess() throws IOException, InterruptedException {
        List<String> command = getDirectoryListingCommand();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File("src"));
        Process process = processBuilder.start();
        List<String> results = readOutput(process.getInputStream());
        Assert.assertThat("Results should not be empty", results, Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat("Results should contain directory listing: ", results, Matchers.hasItems(Matchers.containsString("main"), Matchers.containsString("test")));
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
    }

    @Test
    public void givenProcessBuilder_whenRedirectStandardOutput_thenSuccessWriting() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-version");
        processBuilder.redirectErrorStream(true);
        File log = tempFolder.newFile("java-version.log");
        processBuilder.redirectOutput(log);
        Process process = processBuilder.start();
        Assert.assertEquals("If redirected, should be -1 ", (-1), process.getInputStream().read());
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
        List<String> lines = Files.lines(log.toPath()).collect(Collectors.toList());
        Assert.assertThat("Results should not be empty", lines, Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat("Results should contain java version: ", lines, Matchers.hasItem(Matchers.containsString("java version")));
    }

    @Test
    public void givenProcessBuilder_whenRedirectStandardOutput_thenSuccessAppending() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-version");
        File log = tempFolder.newFile("java-version-append.log");
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(appendTo(log));
        Process process = processBuilder.start();
        Assert.assertEquals("If redirected output, should be -1 ", (-1), process.getInputStream().read());
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
        List<String> lines = Files.lines(log.toPath()).collect(Collectors.toList());
        Assert.assertThat("Results should not be empty", lines, Matchers.is(Matchers.not(Matchers.empty())));
        Assert.assertThat("Results should contain java version: ", lines, Matchers.hasItem(Matchers.containsString("java version")));
    }

    @Test
    public void givenProcessBuilder_whenStartingPipeline_thenSuccess() throws IOException, InterruptedException {
        if (!(isWindows())) {
            List<ProcessBuilder> builders = Arrays.asList(new ProcessBuilder("find", "src", "-name", "*.java", "-type", "f"), new ProcessBuilder("wc", "-l"));
            List<Process> processes = startPipeline(builders);
            Process last = processes.get(((processes.size()) - 1));
            List<String> output = readOutput(last.getInputStream());
            Assert.assertThat("Results should not be empty", output, Matchers.is(Matchers.not(Matchers.empty())));
        }
    }

    @Test
    public void givenProcessBuilder_whenInheritIO_thenSuccess() throws IOException, InterruptedException {
        List<String> command = getEchoCommand();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        Assert.assertEquals("No errors should be detected", 0, exitCode);
    }
}

