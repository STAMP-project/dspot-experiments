/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.testing;


import com.google.common.collect.ImmutableList;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.lang.ProcessBuilder.Redirect.PIPE;
import static java.lang.ProcessBuilder.Redirect.to;


public class CommandWrapperTest {
    private static final List<String> COMMAND = ImmutableList.of("my", "command");

    private static final List<String> WIN_COMMAND = ImmutableList.of("cmd", "/C", "my", "command");

    private static final List<String> UNIX_COMMAND = ImmutableList.of("bash", "my", "command");

    private static final Path DIRECTORY = Paths.get("my-path");

    private static final File WIN_NULL_FILE = new File("NUL:");

    private static final File UNIX_NULL_FILE = new File("/dev/null");

    @Test
    public void testCommandWrapperCommand() {
        CommandWrapper commandWrapper = CommandWrapper.create();
        commandWrapper.setCommand(CommandWrapperTest.COMMAND);
        ProcessBuilder processBuilder = commandWrapper.getBuilder();
        if (BaseEmulatorHelper.isWindows()) {
            Assert.assertEquals(CommandWrapperTest.WIN_COMMAND, processBuilder.command());
        } else {
            Assert.assertEquals(CommandWrapperTest.UNIX_COMMAND, processBuilder.command());
        }
        Assert.assertNull(processBuilder.directory());
        Assert.assertFalse(processBuilder.redirectErrorStream());
        Assert.assertEquals(PIPE, processBuilder.redirectError());
    }

    @Test
    public void testCommandWrapperRedirectErrorStream() {
        CommandWrapper commandWrapper = CommandWrapper.create();
        commandWrapper.setCommand(CommandWrapperTest.COMMAND);
        commandWrapper.setRedirectErrorStream();
        ProcessBuilder processBuilder = commandWrapper.getBuilder();
        if (BaseEmulatorHelper.isWindows()) {
            Assert.assertEquals(CommandWrapperTest.WIN_COMMAND, processBuilder.command());
        } else {
            Assert.assertEquals(CommandWrapperTest.UNIX_COMMAND, processBuilder.command());
        }
        Assert.assertNull(processBuilder.directory());
        Assert.assertTrue(processBuilder.redirectErrorStream());
        Assert.assertEquals(PIPE, processBuilder.redirectError());
    }

    @Test
    public void testCommandWrapperRedirectErrorInherit() {
        CommandWrapper commandWrapper = CommandWrapper.create();
        commandWrapper.setCommand(CommandWrapperTest.COMMAND);
        commandWrapper.setRedirectErrorInherit();
        ProcessBuilder processBuilder = commandWrapper.getBuilder();
        if (BaseEmulatorHelper.isWindows()) {
            Assert.assertEquals(CommandWrapperTest.WIN_COMMAND, processBuilder.command());
        } else {
            Assert.assertEquals(CommandWrapperTest.UNIX_COMMAND, processBuilder.command());
        }
        Assert.assertNull(processBuilder.directory());
        Assert.assertFalse(processBuilder.redirectErrorStream());
        Assert.assertEquals(INHERIT, processBuilder.redirectError());
    }

    @Test
    public void testCommandWrapperDirectory() {
        CommandWrapper commandWrapper = CommandWrapper.create();
        commandWrapper.setCommand(CommandWrapperTest.COMMAND);
        commandWrapper.setDirectory(CommandWrapperTest.DIRECTORY);
        ProcessBuilder processBuilder = commandWrapper.getBuilder();
        if (BaseEmulatorHelper.isWindows()) {
            Assert.assertEquals(CommandWrapperTest.WIN_COMMAND, processBuilder.command());
        } else {
            Assert.assertEquals(CommandWrapperTest.UNIX_COMMAND, processBuilder.command());
        }
        Assert.assertEquals(CommandWrapperTest.DIRECTORY, processBuilder.directory().toPath());
        Assert.assertFalse(processBuilder.redirectErrorStream());
        Assert.assertEquals(PIPE, processBuilder.redirectError());
    }

    @Test
    public void testCommandWrapperRedirectOutputToNull() {
        CommandWrapper commandWrapper = CommandWrapper.create();
        commandWrapper.setCommand(CommandWrapperTest.COMMAND);
        commandWrapper.setRedirectOutputToNull();
        ProcessBuilder processBuilder = commandWrapper.getBuilder();
        if (BaseEmulatorHelper.isWindows()) {
            Assert.assertEquals(CommandWrapperTest.WIN_COMMAND, processBuilder.command());
            Assert.assertEquals(to(CommandWrapperTest.WIN_NULL_FILE), processBuilder.redirectOutput());
        } else {
            Assert.assertEquals(CommandWrapperTest.UNIX_COMMAND, processBuilder.command());
            Assert.assertEquals(to(CommandWrapperTest.UNIX_NULL_FILE), processBuilder.redirectOutput());
        }
        Assert.assertNull(processBuilder.directory());
        Assert.assertFalse(processBuilder.redirectErrorStream());
        Assert.assertEquals(PIPE, processBuilder.redirectError());
    }
}

