/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.util;


import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ProcessWrapperTest {
    @Test
    public void shouldReturnTrueWhenAProcessIsRunning() {
        Process process = getMockedProcess(Mockito.mock(OutputStream.class));
        Mockito.when(process.exitValue()).thenThrow(new IllegalThreadStateException());
        ProcessWrapper processWrapper = new ProcessWrapper(process, "", "", ProcessOutputStreamConsumer.inMemoryConsumer(), "utf-8", null);
        Assert.assertThat(processWrapper.isRunning(), is(true));
    }

    @Test
    public void shouldReturnFalseWhenAProcessHasExited() {
        Process process = getMockedProcess(Mockito.mock(OutputStream.class));
        Mockito.when(process.exitValue()).thenReturn(1);
        ProcessWrapper processWrapper = new ProcessWrapper(process, "", "", ProcessOutputStreamConsumer.inMemoryConsumer(), "utf-8", null);
        Assert.assertThat(processWrapper.isRunning(), is(false));
    }

    @Test
    public void shouldTypeInputToConsole() {
        OutputStream processInputStream = new ByteArrayOutputStream();// mock(OutputStream.class);

        Process process = getMockedProcess(processInputStream);
        ProcessWrapper processWrapper = new ProcessWrapper(process, "", "", ProcessOutputStreamConsumer.inMemoryConsumer(), "utf-8", null);
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("input1");
        inputs.add("input2");
        processWrapper.typeInputToConsole(inputs);
        String input = processInputStream.toString();
        String[] parts = input.split("\\r?\\n");
        Assert.assertThat(parts[0], is("input1"));
        Assert.assertThat(parts[1], is("input2"));
    }

    @Test
    public void shouldThrowExceptionWhenExecutableDoesNotExist() throws IOException {
        CommandLine line = CommandLine.createCommandLine("doesnotexist").withEncoding("utf-8");
        try {
            ProcessOutputStreamConsumer outputStreamConsumer = ProcessOutputStreamConsumer.inMemoryConsumer();
            line.execute(outputStreamConsumer, new EnvironmentVariableContext(), null);
            Assert.fail("Expected exception");
        } catch (CommandLineException e) {
            Assert.assertThat(e.getMessage(), containsString("Make sure this command can execute manually."));
            Assert.assertThat(e.getMessage(), containsString("doesnotexist"));
            Assert.assertThat(e.getResult(), notNullValue());
        }
    }

    @Test
    public void shouldTryCommandWithTimeout() throws IOException {
        CommandLine line = CommandLine.createCommandLine("doesnotexist").withEncoding("utf-8");
        try {
            line.waitForSuccess(100);
            Assert.fail("Expected Exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), containsString("Timeout after 0.1 seconds waiting for command 'doesnotexist'"));
        }
    }

    @Test
    public void shouldCollectOutput() throws Exception {
        String output = "SYSOUT: Hello World!";
        String error = "SYSERR: Some error happened!";
        CommandLine line = CommandLine.createCommandLine("ruby").withEncoding("utf-8").withArgs(script("echo"), output, error);
        ConsoleResult result = run(line);
        Assert.assertThat(("Errors: " + (result.errorAsString())), result.returnValue(), is(0));
        Assert.assertThat(result.output(), contains(output));
        Assert.assertThat(result.error(), contains(error));
    }

    @Test
    public void shouldAcceptInputString() throws Exception {
        String input = "SYSIN: Hello World!";
        CommandLine line = CommandLine.createCommandLine("ruby").withEncoding("utf-8").withArgs(script("echo-input"));
        ConsoleResult result = run(line, input);
        Assert.assertThat(result.output(), contains(input));
        Assert.assertThat(result.error().size(), is(0));
    }

    @Test
    public void shouldBeAbleToCompleteInput() throws Exception {
        String input1 = "SYSIN: Line 1!";
        String input2 = "SYSIN: Line 2!";
        CommandLine line = CommandLine.createCommandLine("ruby").withEncoding("utf-8").withArgs(script("echo-all-input"));
        ConsoleResult result = run(line, input1, input2);
        Assert.assertThat(result.returnValue(), is(0));
        Assert.assertThat(result.output(), contains(("You said: " + input1)));
        Assert.assertThat(result.output(), contains(("You said: " + input2)));
        Assert.assertThat(result.error().size(), is(0));
    }

    @Test
    public void shouldReportReturnValueIfProcessFails() {
        CommandLine line = CommandLine.createCommandLine("ruby").withEncoding("utf-8").withArgs(script("nonexistent-script"));
        ConsoleResult result = run(line);
        Assert.assertThat(result.returnValue(), is(1));
    }

    @Test
    public void shouldSetGoServerVariablesIfTheyExist() {
        System.setProperty("GO_DEPENDENCY_LABEL_PIPELINE_NAME", "999");
        CommandLine line = CommandLine.createCommandLine("ruby").withEncoding("utf-8").withArgs(script("dump-environment"));
        ConsoleResult result = run(line);
        Assert.assertThat(("Errors: " + (result.errorAsString())), result.returnValue(), is(0));
        Assert.assertThat(result.output(), contains("GO_DEPENDENCY_LABEL_PIPELINE_NAME=999"));
    }
}

