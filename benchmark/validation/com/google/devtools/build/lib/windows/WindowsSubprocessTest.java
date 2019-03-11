/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.windows;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.shell.ShellUtils;
import com.google.devtools.build.lib.shell.Subprocess;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.windows.jni.WindowsProcesses;
import com.google.devtools.build.runfiles.Runfiles;
import java.util.function.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link WindowsSubprocess}.
 */
@RunWith(JUnit4.class)
@TestSpec(localOnly = true, supportedOs = OS.WINDOWS)
public class WindowsSubprocessTest {
    private String mockSubprocess;

    private String mockBinary;

    private Subprocess process;

    private Runfiles runfiles;

    @Test
    public void testSystemRootIsSetByDefaultNoWindowsStyleArgEscaping() throws Exception {
        assertSystemRootIsSetByDefault(false);
    }

    @Test
    public void testSystemRootIsSetByDefaultWithWindowsStyleArgEscaping() throws Exception {
        assertSystemRootIsSetByDefault(true);
    }

    @Test
    public void testSystemDriveIsSetByDefaultNoWindowsStyleArgEscaping() throws Exception {
        assertSystemDriveIsSetByDefault(false);
    }

    @Test
    public void testSystemDriveIsSetByDefaultWithWindowsStyleArgEscaping() throws Exception {
        assertSystemDriveIsSetByDefault(true);
    }

    @Test
    public void testSystemRootIsSetNoWindowsStyleArgEscaping() throws Exception {
        assertSystemRootIsSet(false);
    }

    @Test
    public void testSystemRootIsSetWithWindowsStyleArgEscaping() throws Exception {
        assertSystemRootIsSet(true);
    }

    @Test
    public void testSystemDriveIsSetNoWindowsStyleArgEscaping() throws Exception {
        assertSystemDriveIsSet(false);
    }

    @Test
    public void testSystemDriveIsSetWithWindowsStyleArgEscaping() throws Exception {
        assertSystemDriveIsSet(true);
    }

    /**
     * An argument and its command-line-escaped counterpart.
     *
     * <p>Such escaping ensures that Bazel correctly forwards arguments to subprocesses.
     */
    private static final class ArgPair {
        public final String original;

        public final String escaped;

        public ArgPair(String original, String escaped) {
            this.original = original;
            this.escaped = escaped;
        }
    }

    @Test
    public void testSubprocessReceivesArgsAsIntendedNoWindowsStyleArgEscaping() throws Exception {
        assertSubprocessReceivesArgsAsIntended(false, ( x) -> WindowsProcesses.quoteCommandLine(ImmutableList.of(x)), new WindowsSubprocessTest.ArgPair("", "\"\""), new WindowsSubprocessTest.ArgPair(" ", "\" \""), new WindowsSubprocessTest.ArgPair("foo", "foo"), new WindowsSubprocessTest.ArgPair("foo\\bar", "foo\\bar"), new WindowsSubprocessTest.ArgPair("foo bar", "\"foo bar\""));
        // TODO(laszlocsomor): the escaping logic in WindowsProcesses.quoteCommandLine is wrong, because
        // it fails to properly escape things like a single backslash followed by a quote, e.g. a\"b
        // Fix the escaping logic and add more test here.
    }

    @Test
    public void testSubprocessReceivesArgsAsIntendedWithWindowsStyleArgEscaping() throws Exception {
        assertSubprocessReceivesArgsAsIntended(true, ( x) -> ShellUtils.windowsEscapeArg(x), new WindowsSubprocessTest.ArgPair("", "\"\""), new WindowsSubprocessTest.ArgPair(" ", "\" \""), new WindowsSubprocessTest.ArgPair("\"", "\"\\\"\""), new WindowsSubprocessTest.ArgPair("\"\\", "\"\\\"\\\\\""), new WindowsSubprocessTest.ArgPair("\\", "\\"), new WindowsSubprocessTest.ArgPair("\\\"", "\"\\\\\\\"\""), new WindowsSubprocessTest.ArgPair("with space", "\"with space\""), new WindowsSubprocessTest.ArgPair("with^caret", "with^caret"), new WindowsSubprocessTest.ArgPair("space ^caret", "\"space ^caret\""), new WindowsSubprocessTest.ArgPair("caret^ space", "\"caret^ space\""), new WindowsSubprocessTest.ArgPair("with\"quote", "\"with\\\"quote\""), new WindowsSubprocessTest.ArgPair("with\\backslash", "with\\backslash"), new WindowsSubprocessTest.ArgPair("one\\ backslash and \\space", "\"one\\ backslash and \\space\""), new WindowsSubprocessTest.ArgPair("two\\\\backslashes", "two\\\\backslashes"), new WindowsSubprocessTest.ArgPair("two\\\\ backslashes \\\\and space", "\"two\\\\ backslashes \\\\and space\""), new WindowsSubprocessTest.ArgPair("one\\\"x", "\"one\\\\\\\"x\""), new WindowsSubprocessTest.ArgPair("two\\\\\"x", "\"two\\\\\\\\\\\"x\""), new WindowsSubprocessTest.ArgPair("a \\ b", "\"a \\ b\""), new WindowsSubprocessTest.ArgPair("a \\\" b", "\"a \\\\\\\" b\""), new WindowsSubprocessTest.ArgPair("A", "A"), new WindowsSubprocessTest.ArgPair("\"a\"", "\"\\\"a\\\"\""), new WindowsSubprocessTest.ArgPair("B C", "\"B C\""), new WindowsSubprocessTest.ArgPair("\"b c\"", "\"\\\"b c\\\"\""), new WindowsSubprocessTest.ArgPair("D\"E", "\"D\\\"E\""), new WindowsSubprocessTest.ArgPair("\"d\"e\"", "\"\\\"d\\\"e\\\"\""), new WindowsSubprocessTest.ArgPair("C:\\F G", "\"C:\\F G\""), new WindowsSubprocessTest.ArgPair("\"C:\\f g\"", "\"\\\"C:\\f g\\\"\""), new WindowsSubprocessTest.ArgPair("C:\\H\"I", "\"C:\\H\\\"I\""), new WindowsSubprocessTest.ArgPair("\"C:\\h\"i\"", "\"\\\"C:\\h\\\"i\\\"\""), new WindowsSubprocessTest.ArgPair("C:\\J\\\"K", "\"C:\\J\\\\\\\"K\""), new WindowsSubprocessTest.ArgPair("\"C:\\j\\\"k\"", "\"\\\"C:\\j\\\\\\\"k\\\"\""), new WindowsSubprocessTest.ArgPair("C:\\L M ", "\"C:\\L M \""), new WindowsSubprocessTest.ArgPair("\"C:\\l m \"", "\"\\\"C:\\l m \\\"\""), new WindowsSubprocessTest.ArgPair("C:\\N O\\", "\"C:\\N O\\\\\""), new WindowsSubprocessTest.ArgPair("\"C:\\n o\\\"", "\"\\\"C:\\n o\\\\\\\"\""), new WindowsSubprocessTest.ArgPair("C:\\P Q\\ ", "\"C:\\P Q\\ \""), new WindowsSubprocessTest.ArgPair("\"C:\\p q\\ \"", "\"\\\"C:\\p q\\ \\\"\""), new WindowsSubprocessTest.ArgPair("C:\\R\\S\\", "C:\\R\\S\\"), new WindowsSubprocessTest.ArgPair("C:\\R x\\S\\", "\"C:\\R x\\S\\\\\""), new WindowsSubprocessTest.ArgPair("\"C:\\r\\s\\\"", "\"\\\"C:\\r\\s\\\\\\\"\""), new WindowsSubprocessTest.ArgPair("\"C:\\r x\\s\\\"", "\"\\\"C:\\r x\\s\\\\\\\"\""), new WindowsSubprocessTest.ArgPair("C:\\T U\\W\\", "\"C:\\T U\\W\\\\\""), new WindowsSubprocessTest.ArgPair("\"C:\\t u\\w\\\"", "\"\\\"C:\\t u\\w\\\\\\\"\""));
    }
}

