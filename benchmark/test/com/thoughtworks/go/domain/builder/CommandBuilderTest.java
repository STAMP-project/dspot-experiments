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
package com.thoughtworks.go.domain.builder;


import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.command.CommandLine;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;


@RunWith(JunitExtRunner.class)
public class CommandBuilderTest {
    private File tempWorkDir;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { EnhancedOSChecker.WINDOWS })
    public void commandWithArgsList_shouldAddCmdBeforeAWindowsCommand() {
        String[] args = new String[]{ "some thing" };
        CommandBuilderWithArgList commandBuilderWithArgList = new CommandBuilderWithArgList("echo", args, tempWorkDir, null, null, "some desc");
        CommandLine commandLine = commandBuilderWithArgList.buildCommandLine();
        Assert.assertThat(commandLine.toStringForDisplay(), Matchers.is("cmd /c echo some thing"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { EnhancedOSChecker.WINDOWS })
    public void commandWithArgs_shouldAddCmdBeforeAWindowsCommand() {
        CommandBuilder commandBuilder = new CommandBuilder("echo", "some thing", tempWorkDir, null, null, "some desc");
        CommandLine commandLine = commandBuilder.buildCommandLine();
        Assert.assertThat(commandLine.toStringForDisplay(), Matchers.is("cmd /c echo some thing"));
    }
}

