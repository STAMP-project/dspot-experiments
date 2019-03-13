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
package com.google.devtools.build.lib.pkgcache;


import com.google.devtools.build.lib.actions.ActionKeyContext;
import com.google.devtools.build.lib.analysis.ConfiguredRuleClassProvider;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.skyframe.SkyframeExecutor;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for package loading.
 */
@RunWith(JUnit4.class)
public class BuildFileModificationTest extends FoundationTestCase {
    private ManualClock clock = new ManualClock();

    private AnalysisMock analysisMock;

    private ConfiguredRuleClassProvider ruleClassProvider;

    private SkyframeExecutor skyframeExecutor;

    private final ActionKeyContext actionKeyContext = new ActionKeyContext();

    @Test
    public void testCTimeChangeDetectedWithError() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path build = scratch.file("a/BUILD", "cc_library(name='a', feet='stinky')".getBytes(StandardCharsets.ISO_8859_1));
        Package a1 = getPackage("a");
        assertThat(containsErrors()).isTrue();
        assertContainsEvent("//a:a: no such attribute 'feet'");
        eventCollector.clear();
        // writeContent updates mtime and ctime. Note that we keep the content length exactly the same.
        clock.advanceMillis(1);
        FileSystemUtils.writeContent(build, "cc_library(name='a', srcs=['a.cc'])".getBytes(StandardCharsets.ISO_8859_1));
        invalidatePackages();
        Package a2 = getPackage("a");
        assertThat(a2).isNotSameAs(a1);
        assertThat(containsErrors()).isFalse();
        assertNoEvents();
    }

    @Test
    public void testCTimeChangeDetected() throws Exception {
        Path path = scratch.file("pkg/BUILD", "cc_library(name = \'foo\')\n".getBytes(StandardCharsets.ISO_8859_1));
        Package oldPkg = getPackage("pkg");
        // Note that the content has exactly the same length as before.
        clock.advanceMillis(1);
        FileSystemUtils.writeContent(path, "cc_library(name = \'bar\')\n".getBytes(StandardCharsets.ISO_8859_1));
        assertThat(getPackage("pkg")).isSameAs(oldPkg);// Change only becomes visible after invalidatePackages.

        invalidatePackages();
        Package newPkg = getPackage("pkg");
        assertThat(newPkg).isNotSameAs(oldPkg);
        assertThat(getTarget("bar")).isNotNull();
    }

    @Test
    public void testLengthChangeDetected() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path build = scratch.file("a/BUILD", "cc_library(name='a', srcs=['a.cc'])".getBytes(StandardCharsets.ISO_8859_1));
        Package a1 = getPackage("a");
        eventCollector.clear();
        // Note that we didn't advance the clock, so ctime/mtime is the same as before.
        // However, the file contents are one byte longer.
        FileSystemUtils.writeContent(build, "cc_library(name='ab', srcs=['a.cc'])".getBytes(StandardCharsets.ISO_8859_1));
        invalidatePackages();
        Package a2 = getPackage("a");
        assertThat(a2).isNotSameAs(a1);
        assertNoEvents();
    }

    @Test
    public void testTouchedBuildFileCausesReloadAfterSync() throws Exception {
        Path path = scratch.file("pkg/BUILD", "cc_library(name = 'foo')");
        Package oldPkg = getPackage("pkg");
        // Change ctime to 1.
        clock.advanceMillis(1);
        path.setLastModifiedTime(1001);
        assertThat(getPackage("pkg")).isSameAs(oldPkg);// change not yet visible

        invalidatePackages();
        Package newPkg = getPackage("pkg");
        assertThat(newPkg).isNotSameAs(oldPkg);
    }
}

