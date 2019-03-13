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
package com.google.devtools.build.lib.skyframe;


import Dirent.Type;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.actions.FileStateValue;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.packages.BuildFileNotFoundException;
import com.google.devtools.build.lib.packages.ConstantRuleVisibility;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.packages.NoSuchTargetException;
import com.google.devtools.build.lib.packages.StarlarkSemanticsOptions;
import com.google.devtools.build.lib.pkgcache.PackageCacheOptions;
import com.google.devtools.build.lib.skyframe.util.SkyframeExecutorTestUtils;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.vfs.Dirent;
import com.google.devtools.build.lib.vfs.FileStatus;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.ModifiedFileSet;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import com.google.devtools.build.skyframe.ErrorInfo;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import com.google.devtools.common.options.Options;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static BazelSkyframeExecutorConstants.BUILD_FILES_BY_PRIORITY;


/**
 * Unit tests of specific functionality of PackageFunction. Note that it's already tested indirectly
 * in several other places.
 */
@RunWith(JUnit4.class)
public class PackageFunctionTest extends BuildViewTestCase {
    private PackageFunctionTest.CustomInMemoryFs fs = new PackageFunctionTest.CustomInMemoryFs(new ManualClock());

    @Test
    public void testValidPackage() throws Exception {
        scratch.file("pkg/BUILD");
        validPackage(PackageValue.key(PackageIdentifier.parse("@//pkg")));
    }

    @Test
    public void testPropagatesFilesystemInconsistencies() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        RecordingDifferencer differencer = getSkyframeExecutor().getDifferencerForTesting();
        Root pkgRoot = getSkyframeExecutor().getPathEntries().get(0);
        Path fooBuildFile = scratch.file("foo/BUILD");
        Path fooDir = fooBuildFile.getParentDirectory();
        // Our custom filesystem says that fooDir is neither a file nor directory nor symlink
        FileStatus inconsistentFileStatus = new FileStatus() {
            @Override
            public boolean isFile() {
                return false;
            }

            @Override
            public boolean isDirectory() {
                return false;
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isSpecialFile() {
                return false;
            }

            @Override
            public long getSize() throws IOException {
                return 0;
            }

            @Override
            public long getLastModifiedTime() throws IOException {
                return 0;
            }

            @Override
            public long getLastChangeTime() throws IOException {
                return 0;
            }

            @Override
            public long getNodeId() throws IOException {
                return 0;
            }
        };
        fs.stubStat(fooBuildFile, inconsistentFileStatus);
        RootedPath pkgRootedPath = RootedPath.toRootedPath(pkgRoot, fooDir);
        SkyValue fooDirValue = FileStateValue.create(pkgRootedPath, tsgm);
        differencer.inject(ImmutableMap.of(FileStateValue.key(pkgRootedPath), fooDirValue));
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        String expectedMessage = "according to stat, existing path /workspace/foo/BUILD is neither" + " a file nor directory nor symlink.";
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        String errorMessage = errorInfo.getException().getMessage();
        assertThat(errorMessage).contains("Inconsistent filesystem operations");
        assertThat(errorMessage).contains(expectedMessage);
    }

    @Test
    public void testPropagatesFilesystemInconsistencies_Globbing() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        RecordingDifferencer differencer = getSkyframeExecutor().getDifferencerForTesting();
        Root pkgRoot = getSkyframeExecutor().getPathEntries().get(0);
        scratch.file("foo/BUILD", "subinclude('//a:a')", "sh_library(name = 'foo', srcs = glob(['bar/**/baz.sh']))");
        scratch.file("a/BUILD");
        scratch.file("a/a");
        Path bazFile = scratch.file("foo/bar/baz/baz.sh");
        Path bazDir = bazFile.getParentDirectory();
        Path barDir = bazDir.getParentDirectory();
        // Our custom filesystem says "foo/bar/baz" does not exist but it also says that "foo/bar"
        // has a child directory "baz".
        fs.stubStat(bazDir, null);
        RootedPath barDirRootedPath = RootedPath.toRootedPath(pkgRoot, barDir);
        differencer.inject(ImmutableMap.of(DirectoryListingStateValue.key(barDirRootedPath), DirectoryListingStateValue.create(ImmutableList.of(new Dirent("baz", Type.DIRECTORY)))));
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        String expectedMessage = "/workspace/foo/bar/baz is no longer an existing directory";
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        String errorMessage = errorInfo.getException().getMessage();
        assertThat(errorMessage).contains("Inconsistent filesystem operations");
        assertThat(errorMessage).contains(expectedMessage);
    }

    /**
     * Regression test for unexpected exception type from PackageValue.
     */
    @Test
    public void testDiscrepancyBetweenLegacyAndSkyframePackageLoadingErrors() throws Exception {
        // Normally, legacy globbing and skyframe globbing share a cache for `readdir` filesystem calls.
        // In order to exercise a situation where they observe different results for filesystem calls,
        // we disable the cache. This might happen in a real scenario, e.g. if the cache hits a limit
        // and evicts entries.
        getSkyframeExecutor().turnOffSyscallCacheForTesting();
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path fooBuildFile = scratch.file("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['bar/*.sh']))");
        Path fooDir = fooBuildFile.getParentDirectory();
        Path barDir = fooDir.getRelative("bar");
        scratch.file("foo/bar/baz.sh");
        fs.scheduleMakeUnreadableAfterReaddir(barDir);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        String expectedMessage = "Encountered error 'Directory is not readable'";
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        String errorMessage = errorInfo.getException().getMessage();
        assertThat(errorMessage).contains("Inconsistent filesystem operations");
        assertThat(errorMessage).contains(expectedMessage);
    }

    // Cast of srcs attribute to Iterable<Label>.
    @SuppressWarnings("unchecked")
    @Test
    public void testGlobOrderStable() throws Exception {
        scratch.file("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['**/*.txt']))");
        scratch.file("foo/b.txt");
        scratch.file("foo/c/c.txt");
        preparePackageLoading(rootDirectory);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageValue value = validPackage(skyKey);
        assertThat(((Iterable<Label>) (value.getPackage().getTarget("foo").getAssociatedRule().getAttributeContainer().getAttr("srcs")))).containsExactly(Label.parseAbsoluteUnchecked("//foo:b.txt"), Label.parseAbsoluteUnchecked("//foo:c/c.txt")).inOrder();
        scratch.file("foo/d.txt");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/d.txt")).build(), Root.fromPath(rootDirectory));
        value = validPackage(skyKey);
        assertThat(((Iterable<Label>) (value.getPackage().getTarget("foo").getAssociatedRule().getAttributeContainer().getAttr("srcs")))).containsExactly(Label.parseAbsoluteUnchecked("//foo:b.txt"), Label.parseAbsoluteUnchecked("//foo:c/c.txt"), Label.parseAbsoluteUnchecked("//foo:d.txt")).inOrder();
    }

    @Test
    public void testGlobOrderStableWithLegacyAndSkyframeComponents() throws Exception {
        scratch.file("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['*.txt']))");
        scratch.file("foo/b.txt");
        scratch.file("foo/a.config");
        preparePackageLoading(rootDirectory);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageFunctionTest.assertSrcs(validPackage(skyKey), "foo", "//foo:b.txt");
        scratch.overwriteFile("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['*.txt', '*.config']))");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/BUILD")).build(), Root.fromPath(rootDirectory));
        PackageFunctionTest.assertSrcs(validPackage(skyKey), "foo", "//foo:a.config", "//foo:b.txt");
        scratch.overwriteFile("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['*.txt', '*.config'])) # comment");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/BUILD")).build(), Root.fromPath(rootDirectory));
        PackageFunctionTest.assertSrcs(validPackage(skyKey), "foo", "//foo:a.config", "//foo:b.txt");
        getSkyframeExecutor().resetEvaluator();
        PackageCacheOptions packageCacheOptions = Options.getDefaults(PackageCacheOptions.class);
        packageCacheOptions.defaultVisibility = ConstantRuleVisibility.PUBLIC;
        packageCacheOptions.showLoadingProgress = true;
        packageCacheOptions.globbingThreads = 7;
        getSkyframeExecutor().preparePackageLoading(new com.google.devtools.build.lib.pkgcache.PathPackageLocator(outputBase, ImmutableList.of(Root.fromPath(rootDirectory)), BUILD_FILES_BY_PRIORITY), packageCacheOptions, Options.getDefaults(StarlarkSemanticsOptions.class), UUID.randomUUID(), ImmutableMap.<String, String>of(), tsgm);
        getSkyframeExecutor().setActionEnv(ImmutableMap.<String, String>of());
        PackageFunctionTest.assertSrcs(validPackage(skyKey), "foo", "//foo:a.config", "//foo:b.txt");
    }

    /**
     * Tests that a symlink to a file outside of the package root is handled consistently. If the
     * default behavior of Bazel was changed from {@code ExternalFileAction#DEPEND_ON_EXTERNAL_PKG_FOR_EXTERNAL_REPO_PATHS} to {@code ExternalFileAction#ASSUME_NON_EXISTENT_AND_IMMUTABLE_FOR_EXTERNAL_PATHS} then foo/link.sh
     * should no longer appear in the srcs of //foo:foo. However, either way the srcs should be the
     * same independent of the evaluation being incremental or clean.
     */
    @Test
    public void testGlobWithExternalSymlink() throws Exception {
        scratch.file("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['*.sh']))", "sh_library(name = 'bar', srcs = glob(['link.sh']))", "sh_library(name = 'baz', srcs = glob(['subdir_link/*.txt']))");
        scratch.file("foo/ordinary.sh");
        Path externalTarget = scratch.file("../ops/target.txt");
        FileSystemUtils.ensureSymbolicLink(scratch.resolve("foo/link.sh"), externalTarget);
        FileSystemUtils.ensureSymbolicLink(scratch.resolve("foo/subdir_link"), externalTarget.getParentDirectory());
        preparePackageLoading(rootDirectory);
        SkyKey fooKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageValue fooValue = validPackage(fooKey);
        PackageFunctionTest.assertSrcs(fooValue, "foo", "//foo:link.sh", "//foo:ordinary.sh");
        PackageFunctionTest.assertSrcs(fooValue, "bar", "//foo:link.sh");
        PackageFunctionTest.assertSrcs(fooValue, "baz", "//foo:subdir_link/target.txt");
        scratch.overwriteFile("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['*.sh'])) #comment", "sh_library(name = 'bar', srcs = glob(['link.sh']))", "sh_library(name = 'baz', srcs = glob(['subdir_link/*.txt']))");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/BUILD")).build(), Root.fromPath(rootDirectory));
        PackageValue fooValue2 = validPackage(fooKey);
        assertThat(fooValue2).isNotEqualTo(fooValue);
        PackageFunctionTest.assertSrcs(fooValue2, "foo", "//foo:link.sh", "//foo:ordinary.sh");
        PackageFunctionTest.assertSrcs(fooValue2, "bar", "//foo:link.sh");
        PackageFunctionTest.assertSrcs(fooValue2, "baz", "//foo:subdir_link/target.txt");
    }

    @Test
    public void testOneNewElementInMultipleGlob() throws Exception {
        scratch.file("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['*.sh']))", "sh_library(name = 'bar', srcs = glob(['*.sh', '*.txt']))");
        preparePackageLoading(rootDirectory);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageValue value = validPackage(skyKey);
        scratch.file("foo/irrelevent");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/irrelevant")).build(), Root.fromPath(rootDirectory));
        assertThat(validPackage(skyKey)).isSameAs(value);
    }

    @Test
    public void testNoNewElementInMultipleGlob() throws Exception {
        scratch.file("foo/BUILD", "sh_library(name = 'foo', srcs = glob(['*.sh', '*.txt']))", "sh_library(name = 'bar', srcs = glob(['*.sh', '*.txt']))");
        preparePackageLoading(rootDirectory);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageValue value = validPackage(skyKey);
        scratch.file("foo/irrelevent");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/irrelevant")).build(), Root.fromPath(rootDirectory));
        assertThat(validPackage(skyKey)).isSameAs(value);
    }

    @Test
    public void testTransitiveSkylarkDepsStoredInPackage() throws Exception {
        scratch.file("foo/BUILD", "load('//bar:ext.bzl', 'a')");
        scratch.file("bar/BUILD");
        scratch.file("bar/ext.bzl", "load('//baz:ext.bzl', 'b')", "a = b");
        scratch.file("baz/BUILD");
        scratch.file("baz/ext.bzl", "b = 1");
        scratch.file("qux/BUILD");
        scratch.file("qux/ext.bzl", "c = 1");
        preparePackageLoading(rootDirectory);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageValue value = validPackage(skyKey);
        assertThat(value.getPackage().getSkylarkFileDependencies()).containsExactly(Label.parseAbsolute("//bar:ext.bzl", ImmutableMap.of()), Label.parseAbsolute("//baz:ext.bzl", ImmutableMap.of()));
        scratch.overwriteFile("bar/ext.bzl", "load('//qux:ext.bzl', 'c')", "a = c");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("bar/ext.bzl")).build(), Root.fromPath(rootDirectory));
        value = validPackage(skyKey);
        assertThat(value.getPackage().getSkylarkFileDependencies()).containsExactly(Label.parseAbsolute("//bar:ext.bzl", ImmutableMap.of()), Label.parseAbsolute("//qux:ext.bzl", ImmutableMap.of()));
    }

    @Test
    public void testNonExistingSkylarkExtension() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("test/skylark/BUILD", "load('//test/skylark:bad_extension.bzl', 'some_symbol')", "genrule(name = gr,", "    outs = ['out.txt'],", "    cmd = 'echo hello >@')");
        invalidatePackages();
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//test/skylark"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        String expectedMsg = "error loading package 'test/skylark': " + "Unable to load file '//test/skylark:bad_extension.bzl': file doesn't exist";
        assertThat(errorInfo.getException()).hasMessage(expectedMsg);
    }

    @Test
    public void testNonExistingSkylarkExtensionFromExtension() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("test/skylark/extension.bzl", "load('//test/skylark:bad_extension.bzl', 'some_symbol')", "a = 'a'");
        scratch.file("test/skylark/BUILD", "load('//test/skylark:extension.bzl', 'a')", "genrule(name = gr,", "    outs = ['out.txt'],", "    cmd = 'echo hello >@')");
        invalidatePackages();
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//test/skylark"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        assertThat(errorInfo.getException()).hasMessage(("error loading package 'test/skylark': " + ("in /workspace/test/skylark/extension.bzl: " + "Unable to load file '//test/skylark:bad_extension.bzl': file doesn't exist")));
    }

    @Test
    public void testSymlinkCycleWithSkylarkExtension() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path extensionFilePath = scratch.resolve("/workspace/test/skylark/extension.bzl");
        FileSystemUtils.ensureSymbolicLink(extensionFilePath, PathFragment.create("extension.bzl"));
        scratch.file("test/skylark/BUILD", "load('//test/skylark:extension.bzl', 'a')", "genrule(name = gr,", "    outs = ['out.txt'],", "    cmd = 'echo hello >@')");
        invalidatePackages();
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//test/skylark"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        assertThat(errorInfo.getRootCauseOfException()).isEqualTo(skyKey);
        assertThat(errorInfo.getException()).hasMessage(("error loading package 'test/skylark': Encountered error while reading extension " + "file 'test/skylark/extension.bzl': Symlink cycle"));
    }

    @Test
    public void testIOErrorLookingForSubpackageForLabelIsHandled() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("foo/BUILD", "sh_library(name = 'foo', srcs = ['bar/baz.sh'])");
        Path barBuildFile = scratch.file("foo/bar/BUILD");
        fs.stubStatError(barBuildFile, new IOException("nope"));
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        assertContainsEvent("nope");
    }

    @Test
    public void testLoadRelativePath() throws Exception {
        scratch.file("pkg/BUILD", "load(':ext.bzl', 'a')");
        scratch.file("pkg/ext.bzl", "a = 1");
        validPackage(PackageValue.key(PackageIdentifier.parse("@//pkg")));
    }

    @Test
    public void testLoadAbsolutePath() throws Exception {
        scratch.file("pkg1/BUILD");
        scratch.file("pkg2/BUILD", "load('//pkg1:ext.bzl', 'a')");
        scratch.file("pkg1/ext.bzl", "a = 1");
        validPackage(PackageValue.key(PackageIdentifier.parse("@//pkg2")));
    }

    @Test
    public void testBadWorkspaceFile() throws Exception {
        Path workspacePath = scratch.overwriteFile("WORKSPACE", "junk");
        SkyKey skyKey = PackageValue.key(PackageIdentifier.createInMainRepo("external"));
        getSkyframeExecutor().invalidate(Predicates.equalTo(FileStateValue.key(RootedPath.toRootedPath(Root.fromPath(workspacePath.getParentDirectory()), PathFragment.create(workspacePath.getBaseName())))));
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isFalse();
        assertThat(result.get(skyKey).getPackage().containsErrors()).isTrue();
    }

    // Regression test for the two ugly consequences of a bug where GlobFunction incorrectly matched
    // dangling symlinks.
    @Test
    public void testIncrementalSkyframeHybridGlobbingOnDanglingSymlink() throws Exception {
        Path packageDirPath = scratch.file("foo/BUILD", "exports_files(glob(['*.txt']))").getParentDirectory();
        scratch.file("foo/existing.txt");
        FileSystemUtils.ensureSymbolicLink(packageDirPath.getChild("dangling.txt"), "nope");
        preparePackageLoading(rootDirectory);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageValue value = validPackage(skyKey);
        assertThat(value.getPackage().containsErrors()).isFalse();
        assertThat(value.getPackage().getTarget("existing.txt").getName()).isEqualTo("existing.txt");
        try {
            value.getPackage().getTarget("dangling.txt");
            Assert.fail();
        } catch (NoSuchTargetException expected) {
        }
        scratch.overwriteFile("foo/BUILD", "exports_files(glob(['*.txt']))", "#some-irrelevant-comment");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/BUILD")).build(), Root.fromPath(rootDirectory));
        value = validPackage(skyKey);
        assertThat(value.getPackage().containsErrors()).isFalse();
        assertThat(value.getPackage().getTarget("existing.txt").getName()).isEqualTo("existing.txt");
        try {
            value.getPackage().getTarget("dangling.txt");
            Assert.fail();
        } catch (NoSuchTargetException expected) {
            // One consequence of the bug was that dangling symlinks were matched by globs evaluated by
            // Skyframe globbing, meaning there would incorrectly be corresponding targets in packages
            // that had skyframe cache hits during skyframe hybrid globbing.
        }
        scratch.file("foo/nope");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/nope")).build(), Root.fromPath(rootDirectory));
        PackageValue newValue = validPackage(skyKey);
        assertThat(newValue.getPackage().containsErrors()).isFalse();
        assertThat(newValue.getPackage().getTarget("existing.txt").getName()).isEqualTo("existing.txt");
        // Another consequence of the bug is that change pruning would incorrectly cut off changes that
        // caused a dangling symlink potentially matched by a glob to come into existence.
        assertThat(newValue.getPackage().getTarget("dangling.txt").getName()).isEqualTo("dangling.txt");
        assertThat(newValue.getPackage()).isNotSameAs(value.getPackage());
    }

    // Regression test for Skyframe globbing incorrectly matching the package's directory path on
    // 'glob(['**'], exclude_directories = 0)'. We test for this directly by triggering
    // hybrid globbing (gives coverage for both legacy globbing and skyframe globbing).
    @Test
    public void testRecursiveGlobNeverMatchesPackageDirectory() throws Exception {
        scratch.file("foo/BUILD", "[sh_library(name = x + '-matched') for x in glob(['**'], exclude_directories = 0)]");
        scratch.file("foo/bar");
        preparePackageLoading(rootDirectory);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        PackageValue value = validPackage(skyKey);
        assertThat(value.getPackage().containsErrors()).isFalse();
        assertThat(value.getPackage().getTarget("bar-matched").getName()).isEqualTo("bar-matched");
        try {
            value.getPackage().getTarget("-matched");
            Assert.fail();
        } catch (NoSuchTargetException expected) {
        }
        scratch.overwriteFile("foo/BUILD", "[sh_library(name = x + '-matched') for x in glob(['**'], exclude_directories = 0)]", "#some-irrelevant-comment");
        getSkyframeExecutor().invalidateFilesUnderPathForTesting(reporter, ModifiedFileSet.builder().modify(PathFragment.create("foo/BUILD")).build(), Root.fromPath(rootDirectory));
        value = validPackage(skyKey);
        assertThat(value.getPackage().containsErrors()).isFalse();
        assertThat(value.getPackage().getTarget("bar-matched").getName()).isEqualTo("bar-matched");
        try {
            value.getPackage().getTarget("-matched");
            Assert.fail();
        } catch (NoSuchTargetException expected) {
        }
    }

    @Test
    public void testPackageLoadingErrorOnIOExceptionReadingBuildFile() throws Exception {
        Path fooBuildFilePath = scratch.file("foo/BUILD");
        IOException exn = new IOException("nope");
        fs.throwExceptionOnGetInputStream(fooBuildFilePath, exn);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        String errorMessage = errorInfo.getException().getMessage();
        assertThat(errorMessage).contains("nope");
        assertThat(errorInfo.getException()).isInstanceOf(NoSuchPackageException.class);
        assertThat(errorInfo.getException()).hasCauseThat().isInstanceOf(IOException.class);
    }

    @Test
    public void testPackageLoadingErrorOnIOExceptionReadingBzlFile() throws Exception {
        scratch.file("foo/BUILD", "load('//foo:bzl.bzl', 'x')");
        Path fooBzlFilePath = scratch.file("foo/bzl.bzl");
        IOException exn = new IOException("nope");
        fs.throwExceptionOnGetInputStream(fooBzlFilePath, exn);
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//foo"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skyKey);
        String errorMessage = errorInfo.getException().getMessage();
        assertThat(errorMessage).contains("nope");
        assertThat(errorInfo.getException()).isInstanceOf(NoSuchPackageException.class);
        assertThat(errorInfo.getException()).hasCauseThat().isInstanceOf(IOException.class);
    }

    @Test
    public void testLabelsCrossesSubpackageBoundaries() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "exports_files(['sub/blah'])");
        scratch.file("pkg/sub/BUILD");
        invalidatePackages();
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//pkg"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasNoError();
        assertThat(result.get(skyKey).getPackage().containsErrors()).isTrue();
        assertContainsEvent("Label '//pkg:sub/blah' crosses boundary of subpackage 'pkg/sub'");
    }

    @Test
    public void testSymlinkCycleEncounteredWhileHandlingLabelCrossingSubpackageBoundaries() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("pkg/BUILD", "exports_files(['sub/blah'])");
        Path subBuildFilePath = scratch.dir("pkg/sub").getChild("BUILD");
        FileSystemUtils.ensureSymbolicLink(subBuildFilePath, subBuildFilePath);
        invalidatePackages();
        SkyKey skyKey = PackageValue.key(PackageIdentifier.parse("@//pkg"));
        EvaluationResult<PackageValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skyKey, false, reporter);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasError();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skyKey).hasExceptionThat().isInstanceOf(BuildFileNotFoundException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skyKey).hasExceptionThat().hasMessageThat().contains("no such package 'pkg/sub': Symlink cycle detected while trying to find BUILD file");
        assertContainsEvent("circular symlinks detected");
    }

    private static class CustomInMemoryFs extends InMemoryFileSystem {
        private abstract static class FileStatusOrException {
            abstract FileStatus get() throws IOException;

            private static class ExceptionImpl extends PackageFunctionTest.CustomInMemoryFs.FileStatusOrException {
                private final IOException exn;

                private ExceptionImpl(IOException exn) {
                    this.exn = exn;
                }

                @Override
                FileStatus get() throws IOException {
                    throw exn;
                }
            }

            private static class FileStatusImpl extends PackageFunctionTest.CustomInMemoryFs.FileStatusOrException {
                @Nullable
                private final FileStatus fileStatus;

                private FileStatusImpl(@Nullable
                FileStatus fileStatus) {
                    this.fileStatus = fileStatus;
                }

                @Override
                @Nullable
                FileStatus get() {
                    return fileStatus;
                }
            }
        }

        private final Map<Path, PackageFunctionTest.CustomInMemoryFs.FileStatusOrException> stubbedStats = Maps.newHashMap();

        private final Set<Path> makeUnreadableAfterReaddir = Sets.newHashSet();

        private final Map<Path, IOException> pathsToErrorOnGetInputStream = Maps.newHashMap();

        public CustomInMemoryFs(ManualClock manualClock) {
            super(manualClock);
        }

        public void stubStat(Path path, @Nullable
        FileStatus stubbedResult) {
            stubbedStats.put(path, new PackageFunctionTest.CustomInMemoryFs.FileStatusOrException.FileStatusImpl(stubbedResult));
        }

        public void stubStatError(Path path, IOException stubbedResult) {
            stubbedStats.put(path, new PackageFunctionTest.CustomInMemoryFs.FileStatusOrException.ExceptionImpl(stubbedResult));
        }

        @Override
        public FileStatus statIfFound(Path path, boolean followSymlinks) throws IOException {
            if (stubbedStats.containsKey(path)) {
                return stubbedStats.get(path).get();
            }
            return super.statIfFound(path, followSymlinks);
        }

        public void scheduleMakeUnreadableAfterReaddir(Path path) {
            makeUnreadableAfterReaddir.add(path);
        }

        @Override
        public Collection<Dirent> readdir(Path path, boolean followSymlinks) throws IOException {
            Collection<Dirent> result = super.readdir(path, followSymlinks);
            if (makeUnreadableAfterReaddir.contains(path)) {
                path.setReadable(false);
            }
            return result;
        }

        public void throwExceptionOnGetInputStream(Path path, IOException exn) {
            pathsToErrorOnGetInputStream.put(path, exn);
        }

        @Override
        protected InputStream getInputStream(Path path) throws IOException {
            IOException exnToThrow = pathsToErrorOnGetInputStream.get(path);
            if (exnToThrow != null) {
                throw exnToThrow;
            }
            return super.getInputStream(path);
        }
    }
}

