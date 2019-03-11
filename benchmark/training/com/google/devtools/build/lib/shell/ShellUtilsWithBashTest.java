/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for ShellUtils that call out to Bash.
 */
@RunWith(JUnit4.class)
public class ShellUtilsWithBashTest {
    @Test
    public void testTokenizeIsDualToPrettyPrint() throws Exception {
        // tokenize() is the inverse of prettyPrintArgv().  (However, the reverse
        // is not true, since there are many ways to escape the same string,
        // e.g. "foo" and 'foo'.)
        assertTokenizeIsDualToPrettyPrint("foo");
        assertTokenizeIsDualToPrettyPrint("foo bar");
        assertTokenizeIsDualToPrettyPrint("foo bar", "wiz");
        assertTokenizeIsDualToPrettyPrint("'foo'");
        assertTokenizeIsDualToPrettyPrint("\\\'foo\\\'");
        assertTokenizeIsDualToPrettyPrint("${filename%.c}.o");
        assertTokenizeIsDualToPrettyPrint("<html!>");
        assertTokenizeIsDualToPrettyPrint("");
        assertTokenizeIsDualToPrettyPrint("!@#$%^&*()");
        assertTokenizeIsDualToPrettyPrint("x\'y\" z");
    }
}

