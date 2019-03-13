/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.shell;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.sandbox.LinuxSandboxUtil;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.Path;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Command}s that are run using the {@code linux-sandbox}.
 */
@RunWith(JUnit4.class)
public final class CommandUsingLinuxSandboxTest {
    private FileSystem testFS;

    private Path runfilesDir;

    @Test
    public void testCommand_Echo() throws Exception {
        ImmutableList<String> commandArguments = ImmutableList.of("echo", "colorless green ideas");
        Command command = new Command(commandArguments.toArray(new String[0]));
        CommandResult commandResult = command.execute();
        assertThat(commandResult.getTerminationStatus().success()).isTrue();
        assertThat(commandResult.getStdoutStream().toString()).contains("colorless green ideas");
    }

    @Test
    public void testLinuxSandboxedCommand_Echo() throws Exception {
        // TODO(b/62588075) Currently no linux-sandbox tool support in Windows.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        // TODO(b/62588075) Currently no linux-sandbox tool support in MacOS.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.DARWIN)));
        ImmutableList<String> commandArguments = ImmutableList.of("echo", "sleep furiously");
        List<String> fullCommandLine = LinuxSandboxUtil.commandLineBuilder(getLinuxSandboxPath(), commandArguments).build();
        Command command = new Command(fullCommandLine.toArray(new String[0]));
        CommandResult commandResult = command.execute();
        assertThat(commandResult.getTerminationStatus().success()).isTrue();
        assertThat(commandResult.getStdoutStream().toString()).contains("sleep furiously");
    }

    @Test
    public void testLinuxSandboxedCommand_WithStatistics_SpendUserTime() throws CommandException, IOException {
        // TODO(b/62588075) Currently no linux-sandbox tool support in Windows.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        // TODO(b/62588075) Currently no linux-sandbox tool support in MacOS.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.DARWIN)));
        Duration userTimeToSpend = Duration.ofSeconds(10);
        Duration systemTimeToSpend = Duration.ZERO;
        checkLinuxSandboxStatistics(userTimeToSpend, systemTimeToSpend);
    }

    @Test
    public void testLinuxSandboxedCommand_WithStatistics_SpendSystemTime() throws CommandException, IOException {
        // TODO(b/62588075) Currently no linux-sandbox tool support in Windows.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        // TODO(b/62588075) Currently no linux-sandbox tool support in MacOS.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.DARWIN)));
        Duration userTimeToSpend = Duration.ZERO;
        Duration systemTimeToSpend = Duration.ofSeconds(10);
        checkLinuxSandboxStatistics(userTimeToSpend, systemTimeToSpend);
    }

    @Test
    public void testLinuxSandboxedCommand_WithStatistics_SpendUserAndSystemTime() throws CommandException, IOException {
        // TODO(b/62588075) Currently no linux-sandbox tool support in Windows.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        // TODO(b/62588075) Currently no linux-sandbox tool support in MacOS.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.DARWIN)));
        Duration userTimeToSpend = Duration.ofSeconds(10);
        Duration systemTimeToSpend = Duration.ofSeconds(10);
        checkLinuxSandboxStatistics(userTimeToSpend, systemTimeToSpend);
    }
}

