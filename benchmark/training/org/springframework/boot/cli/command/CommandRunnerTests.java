/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.cli.command;


import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link CommandRunner}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 */
public class CommandRunnerTests {
    private CommandRunner commandRunner;

    @Mock
    private Command regularCommand;

    @Mock
    private Command anotherCommand;

    private final Set<CommandRunnerTests.Call> calls = EnumSet.noneOf(CommandRunnerTests.Call.class);

    private ClassLoader loader;

    @Test
    public void runWithoutArguments() throws Exception {
        assertThatExceptionOfType(NoArgumentsException.class).isThrownBy(this.commandRunner::run);
    }

    @Test
    public void runCommand() throws Exception {
        this.commandRunner.run("command", "--arg1", "arg2");
        Mockito.verify(this.regularCommand).run("--arg1", "arg2");
    }

    @Test
    public void missingCommand() throws Exception {
        assertThatExceptionOfType(NoSuchCommandException.class).isThrownBy(() -> this.commandRunner.run("missing"));
    }

    @Test
    public void appArguments() throws Exception {
        this.commandRunner.runAndHandleErrors("command", "--", "--debug", "bar");
        Mockito.verify(this.regularCommand).run("--", "--debug", "bar");
        // When handled by the command itself it shouldn't cause the system property to be
        // set
        assertThat(System.getProperty("debug")).isNull();
    }

    @Test
    public void handlesSuccess() {
        int status = this.commandRunner.runAndHandleErrors("command");
        assertThat(status).isEqualTo(0);
        assertThat(this.calls).isEmpty();
    }

    @Test
    public void handlesNoSuchCommand() {
        int status = this.commandRunner.runAndHandleErrors("missing");
        assertThat(status).isEqualTo(1);
        assertThat(this.calls).containsOnly(CommandRunnerTests.Call.ERROR_MESSAGE);
    }

    @Test
    public void handlesRegularExceptionWithMessage() throws Exception {
        BDDMockito.willThrow(new RuntimeException("With Message")).given(this.regularCommand).run();
        int status = this.commandRunner.runAndHandleErrors("command");
        assertThat(status).isEqualTo(1);
        assertThat(this.calls).containsOnly(CommandRunnerTests.Call.ERROR_MESSAGE);
    }

    @Test
    public void handlesRegularExceptionWithoutMessage() throws Exception {
        BDDMockito.willThrow(new NullPointerException()).given(this.regularCommand).run();
        int status = this.commandRunner.runAndHandleErrors("command");
        assertThat(status).isEqualTo(1);
        assertThat(this.calls).containsOnly(CommandRunnerTests.Call.ERROR_MESSAGE, CommandRunnerTests.Call.PRINT_STACK_TRACE);
    }

    @Test
    public void handlesExceptionWithDashD() throws Exception {
        BDDMockito.willThrow(new RuntimeException()).given(this.regularCommand).run();
        int status = this.commandRunner.runAndHandleErrors("command", "-d");
        assertThat(System.getProperty("debug")).isEqualTo("true");
        assertThat(status).isEqualTo(1);
        assertThat(this.calls).containsOnly(CommandRunnerTests.Call.ERROR_MESSAGE, CommandRunnerTests.Call.PRINT_STACK_TRACE);
    }

    @Test
    public void handlesExceptionWithDashDashDebug() throws Exception {
        BDDMockito.willThrow(new RuntimeException()).given(this.regularCommand).run();
        int status = this.commandRunner.runAndHandleErrors("command", "--debug");
        assertThat(System.getProperty("debug")).isEqualTo("true");
        assertThat(status).isEqualTo(1);
        assertThat(this.calls).containsOnly(CommandRunnerTests.Call.ERROR_MESSAGE, CommandRunnerTests.Call.PRINT_STACK_TRACE);
    }

    @Test
    public void exceptionMessages() {
        assertThat(new NoSuchCommandException("name").getMessage()).isEqualTo("'name' is not a valid command. See 'help'.");
    }

    @Test
    public void help() throws Exception {
        this.commandRunner.run("help", "command");
        Mockito.verify(this.regularCommand).getHelp();
    }

    @Test
    public void helpNoCommand() throws Exception {
        assertThatExceptionOfType(NoHelpCommandArgumentsException.class).isThrownBy(() -> this.commandRunner.run("help"));
    }

    @Test
    public void helpUnknownCommand() throws Exception {
        assertThatExceptionOfType(NoSuchCommandException.class).isThrownBy(() -> this.commandRunner.run("help", "missing"));
    }

    private enum Call {

        SHOW_USAGE,
        ERROR_MESSAGE,
        PRINT_STACK_TRACE;}
}

