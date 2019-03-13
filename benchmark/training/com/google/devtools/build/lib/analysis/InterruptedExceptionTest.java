/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.analysis;


import com.google.devtools.build.lib.analysis.util.AnalysisTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests verifying appropriate propagation of {@link InterruptedException} during filesystem
 * operations.
 */
@RunWith(JUnit4.class)
public class InterruptedExceptionTest extends AnalysisTestCase {
    private final Thread mainThread = Thread.currentThread();

    @Test
    public void testGlobInterruptedException() throws Exception {
        scratch.file("a/BUILD", "sh_library(name = 'a', srcs = glob(['**/*']))");
        scratch.file("a/b/foo.sh", "testfile");
        scratch.file("a/causes_interrupt/bar.sh", "testfile");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//a:a");
            Assert.fail("Expected interrupted exception");
        } catch (InterruptedException expected) {
        }
    }

    @Test
    public void testSkylarkGlobInterruptedException() throws Exception {
        scratch.file("a/gen.bzl", "def gen():", "  native.filegroup(name = 'a', srcs = native.glob(['**/*']))");
        scratch.file("a/BUILD", "load('//a:gen.bzl', 'gen')", "gen()");
        scratch.file("a/b/foo.sh", "testfile");
        scratch.file("a/causes_interrupt/bar.sh", "testfile");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            update("//a:a");
            Assert.fail("Expected interrupted exception");
        } catch (InterruptedException expected) {
        }
    }
}

