/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.util;


import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the {@link CommandBuilder} class.
 */
@RunWith(JUnit4.class)
public class CommandBuilderTest {
    @Test
    public void linuxBuilderTest() {
        assertArgv(linuxBuilder().addArg("abc"), "abc");
        assertArgv(linuxBuilder().addArg("abc def"), "abc def");
        assertArgv(linuxBuilder().addArgs("abc", "def"), "abc", "def");
        assertArgv(linuxBuilder().addArgs(ImmutableList.of("abc", "def")), "abc", "def");
        assertArgv(linuxBuilder().addArg("abc").useShell(true), "/bin/sh", "-c", "abc");
        assertArgv(linuxBuilder().addArg("abc def").useShell(true), "/bin/sh", "-c", "abc def");
        assertArgv(linuxBuilder().addArgs("abc", "def").useShell(true), "/bin/sh", "-c", "abc def");
        assertArgv(linuxBuilder().addArgs("/bin/sh", "-c", "abc").useShell(true), "/bin/sh", "-c", "abc");
        assertArgv(linuxBuilder().addArgs("/bin/sh", "-c"), "/bin/sh", "-c");
        assertArgv(linuxBuilder().addArgs("/bin/bash", "-c"), "/bin/bash", "-c");
        assertArgv(linuxBuilder().addArgs("/bin/sh", "-c").useShell(true), "/bin/sh", "-c");
        assertArgv(linuxBuilder().addArgs("/bin/bash", "-c").useShell(true), "/bin/bash", "-c");
    }

    @Test
    public void windowsBuilderTest() {
        assertArgv(winBuilder().addArg("abc.exe"), "abc.exe");
        assertArgv(winBuilder().addArg("abc.exe -o"), "abc.exe -o");
        assertArgv(winBuilder().addArg("ABC.EXE"), "ABC.EXE");
        assertWinCmdArgv(winBuilder().addArg("abc def.exe"), "abc def.exe");
        assertArgv(winBuilder().addArgs("abc.exe", "def"), "abc.exe", "def");
        assertArgv(winBuilder().addArgs(ImmutableList.of("abc.exe", "def")), "abc.exe", "def");
        assertWinCmdArgv(winBuilder().addArgs("abc.exe", "def").useShell(true), "abc.exe def");
        assertWinCmdArgv(winBuilder().addArg("abc"), "abc");
        assertWinCmdArgv(winBuilder().addArgs("abc", "def"), "abc def");
        assertWinCmdArgv(winBuilder().addArgs("/bin/sh", "-c", "abc", "def"), "abc def");
        assertWinCmdArgv(winBuilder().addArgs("/bin/sh", "-c"), "");
        assertWinCmdArgv(winBuilder().addArgs("/bin/bash", "-c"), "");
        assertWinCmdArgv(winBuilder().addArgs("/bin/sh", "-c").useShell(true), "");
        assertWinCmdArgv(winBuilder().addArgs("/bin/bash", "-c").useShell(true), "");
    }

    @Test
    public void failureScenarios() {
        assertFailure(linuxBuilder(), "At least one argument is expected");
        assertFailure(addArg("a"), "Unidentified operating system");
        assertFailure(addArg("a"), "Working directory must be set");
    }
}

