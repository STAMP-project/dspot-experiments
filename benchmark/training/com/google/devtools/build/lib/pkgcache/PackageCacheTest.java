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
package com.google.devtools.build.lib.pkgcache;


import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.ActionKeyContext;
import com.google.devtools.build.lib.analysis.ConfiguredRuleClassProvider;
import com.google.devtools.build.lib.analysis.util.AnalysisMock;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.packages.BuildFileContainsErrorsException;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.packages.NoSuchTargetException;
import com.google.devtools.build.lib.packages.Target;
import com.google.devtools.build.lib.skyframe.SkyframeExecutor;
import com.google.devtools.build.lib.syntax.BuildFileAST;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.Root;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for package loading.
 */
@RunWith(JUnit4.class)
public class PackageCacheTest extends FoundationTestCase {
    private AnalysisMock analysisMock;

    private ConfiguredRuleClassProvider ruleClassProvider;

    private SkyframeExecutor skyframeExecutor;

    private final ActionKeyContext actionKeyContext = new ActionKeyContext();

    @Test
    public void testGetPackage() throws Exception {
        createPkg1();
        Package pkg1 = getPackage("pkg1");
        assertThat(pkg1.getName()).isEqualTo("pkg1");
        assertThat(getFilename().asPath().getPathString()).isEqualTo("/workspace/pkg1/BUILD");
        assertThat(getPackageManager().getPackage(reporter, PackageIdentifier.createInMainRepo("pkg1"))).isSameAs(pkg1);
    }

    @Test
    public void testASTIsNotRetained() throws Exception {
        createPkg1();
        Package pkg1 = getPackage("pkg1");
        MoreAsserts.assertInstanceOfNotReachable(pkg1, BuildFileAST.class);
    }

    @Test
    public void testGetNonexistentPackage() throws Exception {
        checkGetPackageFails("not-there", ("no such package 'not-there': " + "BUILD file not found on package path"));
    }

    @Test
    public void testGetPackageWithInvalidName() throws Exception {
        scratch.file("invalidpackagename:42/BUILD", "cc_library(name = 'foo') # a BUILD file");
        checkGetPackageFails("invalidpackagename:42", "no such package 'invalidpackagename:42': Invalid package name 'invalidpackagename:42'");
    }

    @Test
    public void testGetTarget() throws Exception {
        createPkg1();
        Label label = Label.parseAbsolute("//pkg1:foo", ImmutableMap.of());
        Target target = getTarget(label);
        assertThat(target.getLabel()).isEqualTo(label);
    }

    @Test
    public void testGetNonexistentTarget() throws Exception {
        createPkg1();
        try {
            getTarget("//pkg1:not-there");
            Assert.fail();
        } catch (NoSuchTargetException e) {
            assertThat(e).hasMessage(("no such target '//pkg1:not-there': target 'not-there' " + "not declared in package 'pkg1' defined by /workspace/pkg1/BUILD"));
        }
    }

    /**
     * A missing package is one for which no BUILD file can be found. The PackageCache caches failures
     * of this kind until the next sync.
     */
    @Test
    public void testRepeatedAttemptsToParseMissingPackage() throws Exception {
        checkGetPackageFails("missing", ("no such package 'missing': " + "BUILD file not found on package path"));
        // Still missing:
        checkGetPackageFails("missing", ("no such package 'missing': " + "BUILD file not found on package path"));
        // Update the BUILD file on disk so "missing" is no longer missing:
        scratch.file("missing/BUILD", "# an ok build file");
        // Still missing:
        checkGetPackageFails("missing", ("no such package 'missing': " + "BUILD file not found on package path"));
        invalidatePackages();
        // Found:
        Package missing = getPackage("missing");
        assertThat(missing.getName()).isEqualTo("missing");
    }

    /**
     * A broken package is one that exists but contains lexer/parser/evaluator errors. The
     * PackageCache only makes one attempt to parse each package once found.
     *
     * <p>Depending on the strictness of the PackageFactory, parsing a broken package may cause a
     * Package object to be returned (possibly missing some rules) or an exception to be thrown. For
     * this test we need that strict behavior.
     *
     * <p>Note: since the PackageCache.setStrictPackageCreation method was deleted (since it wasn't
     * used by any significant clients) creating a "broken" build file got trickier--syntax errors are
     * not enough. For now, we create an unreadable BUILD file, which will cause an IOException to be
     * thrown. This test seems less valuable than it once did.
     */
    @Test
    public void testParseBrokenPackage() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path brokenBuildFile = scratch.file("broken/BUILD");
        brokenBuildFile.setReadable(false);
        try {
            getPackage("broken");
            Assert.fail();
        } catch (BuildFileContainsErrorsException e) {
            assertThat(e).hasMessageThat().contains("/workspace/broken/BUILD (Permission denied)");
        }
        eventCollector.clear();
        // Update the BUILD file on disk so "broken" is no longer broken:
        scratch.overwriteFile("broken/BUILD", "# an ok build file");
        invalidatePackages();// resets cache of failures

        Package broken = getPackage("broken");
        assertThat(broken.getName()).isEqualTo("broken");
        assertNoEvents();
    }

    @Test
    public void testMovedBuildFileCausesReloadAfterSync() throws Exception {
        // PackageLoader doesn't support --package_path.
        /* doPackageLoadingChecks= */
        initializeSkyframeExecutor(false);
        Path buildFile1 = scratch.file("pkg/BUILD", "cc_library(name = 'foo')");
        Path buildFile2 = scratch.file("/otherroot/pkg/BUILD", "cc_library(name = 'bar')");
        setOptions("--package_path=/workspace:/otherroot");
        Package oldPkg = getPackage("pkg");
        assertThat(getPackage("pkg")).isSameAs(oldPkg);// change not yet visible

        assertThat(getFilename().asPath()).isEqualTo(buildFile1);
        assertThat(getSourceRoot()).isEqualTo(Root.fromPath(rootDirectory));
        buildFile1.delete();
        invalidatePackages();
        Package newPkg = getPackage("pkg");
        assertThat(newPkg).isNotSameAs(oldPkg);
        assertThat(getFilename().asPath()).isEqualTo(buildFile2);
        assertThat(getSourceRoot()).isEqualTo(Root.fromPath(scratch.dir("/otherroot")));
        // TODO(bazel-team): (2009) test BUILD file moves in the other direction too.
    }

    private Path rootDir1;

    private Path rootDir2;

    @Test
    public void testLocationForLabelCrossingSubpackage() throws Exception {
        scratch.file("e/f/BUILD");
        scratch.file("e/BUILD", "# Whatever", "filegroup(name='fg', srcs=['f/g'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        List<Event> events = getEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getLocation().getStartLineAndColumn().getLine()).isEqualTo(2);
    }

    /**
     * Static tests (i.e. no changes to filesystem, nor calls to sync).
     */
    @Test
    public void testLabelValidity() throws Exception {
        // PackageLoader doesn't support --package_path.
        /* doPackageLoadingChecks= */
        initializeSkyframeExecutor(false);
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        setUpCacheWithTwoRootLocator();
        scratch.file(((rootDir2) + "/c/d/foo.txt"));
        assertLabelValidity(true, "//a:foo.txt");
        assertLabelValidity(true, "//a:bar/foo.txt");
        assertLabelValidity(false, "//a/bar:foo.txt");// no such package a/bar

        assertLabelValidity(true, "//b:foo.txt");
        assertLabelValidity(true, "//b:bar/foo.txt");
        assertLabelValidity(false, "//b/bar:foo.txt");// no such package b/bar

        assertLabelValidity(true, "//c:foo.txt");
        assertLabelValidity(true, "//c:bar/foo.txt");
        assertLabelValidity(false, "//c/bar:foo.txt");// no such package c/bar

        assertLabelValidity(true, "//c:foo.txt");
        assertLabelValidity(false, "//c:d/foo.txt");// crosses boundary of c/d

        assertLabelValidity(true, "//c/d:foo.txt");
        assertLabelValidity(true, "//c:foo.txt");
        assertLabelValidity(true, "//c:e");
        assertLabelValidity(true, "//c:e/foo.txt");
        assertLabelValidity(false, "//c/e:foo.txt");// no such package c/e

        assertLabelValidity(true, "//f:foo.txt");
        assertLabelValidity(true, "//f:g/foo.txt");
        assertLabelValidity(false, "//f/g:foo.txt");// no such package f/g

        assertLabelValidity(false, "//f:g/h/foo.txt");// crosses boundary of f/g/h

        assertLabelValidity(false, "//f/g:h/foo.txt");// no such package f/g

        assertLabelValidity(true, "//f/g/h:foo.txt");
    }

    /**
     * Dynamic tests of label validity.
     */
    @Test
    public void testAddedBuildFileCausesLabelToBecomeInvalid() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "cc_library(name = 'foo', srcs = ['x/y.cc'])");
        assertLabelValidity(true, "//pkg:x/y.cc");
        // The existence of this file makes 'x/y.cc' an invalid reference.
        scratch.file("pkg/x/BUILD");
        // but not yet...
        assertLabelValidity(true, "//pkg:x/y.cc");
        invalidatePackages();
        // now:
        assertPackageLoadingFails("pkg", ("Label '//pkg:x/y.cc' crosses boundary of subpackage 'pkg/x' " + "(perhaps you meant to put the colon here: '//pkg/x:y.cc'?)"));
    }

    @Test
    public void testDeletedPackages() throws Exception {
        // PackageLoader doesn't support --deleted_packages.
        /* doPackageLoadingChecks= */
        initializeSkyframeExecutor(false);
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        setUpCacheWithTwoRootLocator();
        createBuildFile(rootDir1, "c", "d/x");
        // Now package c exists in both roots, and c/d exists in only in the second
        // root.  It's as if we've merged c and c/d in the first root.
        // c/d is still a subpackage--found in the second root:
        assertThat(getFilename().asPath()).isEqualTo(rootDir2.getRelative("c/d/BUILD"));
        // Subpackage labels are still valid...
        assertLabelValidity(true, "//c/d:foo.txt");
        // ...and this crosses package boundaries:
        assertLabelValidity(false, "//c:d/x");
        assertPackageLoadingFails("c", ("Label '//c:d/x' crosses boundary of subpackage 'c/d' (have you deleted c/d/BUILD? " + "If so, use the --deleted_packages=c/d option)"));
        assertThat(getPackageManager().isPackage(reporter, PackageIdentifier.createInMainRepo("c/d"))).isTrue();
        setOptions("--deleted_packages=c/d");
        invalidatePackages();
        assertThat(getPackageManager().isPackage(reporter, PackageIdentifier.createInMainRepo("c/d"))).isFalse();
        // c/d is no longer a subpackage--even though there's a BUILD file in the
        // second root:
        try {
            getPackage("c/d");
            Assert.fail();
        } catch (NoSuchPackageException e) {
            assertThat(e).hasMessage("no such package 'c/d': Package is considered deleted due to --deleted_packages");
        }
        // Labels in the subpackage are no longer valid...
        assertLabelValidity(false, "//c/d:x");
        // ...and now d is just a subdirectory of c:
        assertLabelValidity(true, "//c:d/x");
    }

    @Test
    public void testPackageFeatures() throws Exception {
        scratch.file("peach/BUILD", "package(features = ['crosstool_default_false'])", "cc_library(name = 'cc', srcs = ['cc.cc'])");
        assertThat(getFeatures()).hasSize(1);
    }

    @Test
    public void testBrokenPackageOnMultiplePackagePathEntries() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        setOptions("--package_path=.:.");
        scratch.file("x/y/BUILD");
        scratch.file("x/BUILD", "genrule(name = 'x',", "srcs = [],", "outs = ['y/z.h'],", "cmd  = '')");
        Package p = getPackage("x");
        assertThat(containsErrors()).isTrue();
    }
}

