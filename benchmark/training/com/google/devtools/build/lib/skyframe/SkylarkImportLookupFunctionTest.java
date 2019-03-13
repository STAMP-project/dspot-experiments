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


import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.skyframe.SkylarkImportLookupFunction.SkylarkImportFailedException;
import com.google.devtools.build.lib.skyframe.util.SkyframeExecutorTestUtils;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.skyframe.ErrorInfo;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.EvaluationResultSubjectFactory;
import com.google.devtools.build.skyframe.SkyKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for SkylarkImportLookupFunction.
 */
@RunWith(JUnit4.class)
public class SkylarkImportLookupFunctionTest extends BuildViewTestCase {
    String preludeLabelRelativePath;

    @Test
    public void testSkylarkImportLabels() throws Exception {
        scratch.file("pkg1/BUILD");
        scratch.file("pkg1/ext.bzl");
        checkSuccessfulLookup("//pkg1:ext.bzl");
        scratch.file("pkg2/BUILD");
        scratch.file("pkg2/dir/ext.bzl");
        checkSuccessfulLookup("//pkg2:dir/ext.bzl");
        scratch.file("dir/pkg3/BUILD");
        scratch.file("dir/pkg3/dir/ext.bzl");
        checkSuccessfulLookup("//dir/pkg3:dir/ext.bzl");
    }

    @Test
    public void testSkylarkImportLabelsAlternativeRoot() throws Exception {
        scratch.file("/root_2/pkg4/BUILD");
        scratch.file("/root_2/pkg4/ext.bzl");
        checkSuccessfulLookup("//pkg4:ext.bzl");
    }

    @Test
    public void testSkylarkImportLabelsMultipleBuildFiles() throws Exception {
        scratch.file("dir1/BUILD");
        scratch.file("dir1/dir2/BUILD");
        scratch.file("dir1/dir2/ext.bzl");
        checkSuccessfulLookup("//dir1/dir2:ext.bzl");
    }

    @Test
    public void testLoadFromSkylarkFileInRemoteRepo() throws Exception {
        scratch.deleteFile(preludeLabelRelativePath);
        scratch.overwriteFile("WORKSPACE", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo'", ")");
        scratch.file("/a_remote_repo/WORKSPACE");
        scratch.file("/a_remote_repo/remote_pkg/BUILD");
        scratch.file("/a_remote_repo/remote_pkg/ext1.bzl", "load(':ext2.bzl', 'CONST')");
        scratch.file("/a_remote_repo/remote_pkg/ext2.bzl", "CONST = 17");
        checkSuccessfulLookup("@a_remote_repo//remote_pkg:ext1.bzl");
    }

    @Test
    public void testLoadRelativeLabel() throws Exception {
        scratch.file("pkg/BUILD");
        scratch.file("pkg/ext1.bzl", "a = 1");
        scratch.file("pkg/ext2.bzl", "load(':ext1.bzl', 'a')");
        checkSuccessfulLookup("//pkg:ext2.bzl");
    }

    @Test
    public void testLoadAbsoluteLabel() throws Exception {
        scratch.file("pkg2/BUILD");
        scratch.file("pkg3/BUILD");
        scratch.file("pkg2/ext.bzl", "b = 1");
        scratch.file("pkg3/ext.bzl", "load('//pkg2:ext.bzl', 'b')");
        checkSuccessfulLookup("//pkg3:ext.bzl");
    }

    @Test
    public void testLoadFromSameAbsoluteLabelTwice() throws Exception {
        scratch.file("pkg1/BUILD");
        scratch.file("pkg2/BUILD");
        scratch.file("pkg1/ext.bzl", "a = 1", "b = 2");
        scratch.file("pkg2/ext.bzl", "load('//pkg1:ext.bzl', 'a')", "load('//pkg1:ext.bzl', 'b')");
        checkSuccessfulLookup("//pkg2:ext.bzl");
    }

    @Test
    public void testLoadFromSameRelativeLabelTwice() throws Exception {
        scratch.file("pkg/BUILD");
        scratch.file("pkg/ext1.bzl", "a = 1", "b = 2");
        scratch.file("pkg/ext2.bzl", "load(':ext1.bzl', 'a')", "load(':ext1.bzl', 'b')");
        checkSuccessfulLookup("//pkg:ext2.bzl");
    }

    @Test
    public void testLoadFromRelativeLabelInSubdir() throws Exception {
        scratch.file("pkg/BUILD");
        scratch.file("pkg/subdir/ext1.bzl", "a = 1");
        scratch.file("pkg/subdir/ext2.bzl", "load(':subdir/ext1.bzl', 'a')");
        checkSuccessfulLookup("//pkg:subdir/ext2.bzl");
    }

    @Test
    public void testSkylarkImportLookupNoBuildFile() throws Exception {
        scratch.file("pkg/ext.bzl", "");
        SkyKey skylarkImportLookupKey = key("//pkg:ext.bzl");
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skylarkImportLookupKey);
        String errorMessage = errorInfo.getException().getMessage();
        assertThat(errorMessage).isEqualTo("Unable to load package for '//pkg:ext.bzl': BUILD file not found on package path");
    }

    @Test
    public void testSkylarkImportLookupNoBuildFileForLoad() throws Exception {
        scratch.file("pkg2/BUILD");
        scratch.file("pkg1/ext.bzl", "a = 1");
        scratch.file("pkg2/ext.bzl", "load('//pkg1:ext.bzl', 'a')");
        SkyKey skylarkImportLookupKey = key("//pkg:ext.bzl");
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        ErrorInfo errorInfo = result.getError(skylarkImportLookupKey);
        String errorMessage = errorInfo.getException().getMessage();
        assertThat(errorMessage).isEqualTo("Unable to load package for '//pkg:ext.bzl': BUILD file not found on package path");
    }

    @Test
    public void testSkylarkImportFilenameWithControlChars() throws Exception {
        scratch.file("pkg/BUILD", "");
        scratch.file("pkg/ext.bzl", "load(\'//pkg:oops\u0000.bzl\', \'a\')");
        try {
            SkyKey skylarkImportLookupKey = key("//pkg:ext.bzl");
            /* keepGoing= */
            SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
            Assert.fail("Expected exception");
        } catch (AssertionError e) {
            String errorMessage = e.getMessage();
            assertThat(errorMessage).contains(("invalid target name 'oops<?>.bzl': " + "target names may not contain non-printable characters: \'\\x00\'"));
        }
    }

    @Test
    public void testLoadFromExternalRepoInWorkspaceFileAllowed() throws Exception {
        scratch.deleteFile(preludeLabelRelativePath);
        Path p = scratch.overwriteFile("WORKSPACE", "local_repository(", "    name = 'a_remote_repo',", "    path = '/a_remote_repo'", ")");
        scratch.file("/a_remote_repo/WORKSPACE");
        scratch.file("/a_remote_repo/remote_pkg/BUILD");
        scratch.file("/a_remote_repo/remote_pkg/ext.bzl", "CONST = 17");
        RootedPath rootedPath = RootedPath.toRootedPath(Root.fromPath(p.getParentDirectory()), PathFragment.create("WORKSPACE"));
        SkyKey skylarkImportLookupKey = /* inWorkspace= */
        /* workspaceChunk= */
        SkylarkImportLookupValue.keyInWorkspace(Label.parseAbsoluteUnchecked("@a_remote_repo//remote_pkg:ext.bzl"), 0, rootedPath);
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isFalse();
    }

    @Test
    public void testLoadUsingLabelThatDoesntCrossBoundaryOfPackage() throws Exception {
        scratch.file("a/BUILD");
        scratch.file("a/a.bzl", "load('//a:b/b.bzl', 'b')");
        scratch.file("a/b/b.bzl", "b = 42");
        checkSuccessfulLookup("//a:a.bzl");
    }

    @Test
    public void testLoadUsingLabelThatCrossesBoundaryOfPackage_Allow_OfSamePkg() throws Exception {
        scratch.file("a/BUILD");
        scratch.file("a/a.bzl", "load('//a:b/b.bzl', 'b')");
        scratch.file("a/b/BUILD", "");
        scratch.file("a/b/b.bzl", "b = 42");
        checkSuccessfulLookup("//a:a.bzl");
    }

    @Test
    public void testLoadUsingLabelThatCrossesBoundaryOfPackage_Disallow_OfSamePkg() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_disallow_load_labels_to_cross_package_boundaries");
        scratch.file("a/BUILD");
        scratch.file("a/a.bzl", "load('//a:b/b.bzl', 'b')");
        scratch.file("a/b/BUILD", "");
        scratch.file("a/b/b.bzl", "b = 42");
        SkyKey skylarkImportLookupKey = key("//a:a.bzl");
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().isInstanceOf(SkylarkImportFailedException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().hasMessageThat().contains(("Label '//a:b/b.bzl' crosses boundary of subpackage 'a/b' (perhaps you meant to put " + "the colon here: '//a/b:b.bzl'?)"));
    }

    @Test
    public void testLoadUsingLabelThatCrossesBoundaryOfPackage_Allow_OfDifferentPkgUnder() throws Exception {
        scratch.file("a/BUILD");
        scratch.file("a/a.bzl", "load('//a/b:c/c.bzl', 'c')");
        scratch.file("a/b/BUILD", "");
        scratch.file("a/b/c/BUILD", "");
        scratch.file("a/b/c/c.bzl", "c = 42");
        checkSuccessfulLookup("//a:a.bzl");
    }

    @Test
    public void testLoadUsingLabelThatCrossesBoundaryOfPackage_Disallow_OfDifferentPkgUnder() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_disallow_load_labels_to_cross_package_boundaries");
        scratch.file("a/BUILD");
        scratch.file("a/a.bzl", "load('//a/b:c/c.bzl', 'c')");
        scratch.file("a/b/BUILD", "");
        scratch.file("a/b/c/BUILD", "");
        scratch.file("a/b/c/c.bzl", "c = 42");
        SkyKey skylarkImportLookupKey = key("//a:a.bzl");
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().isInstanceOf(SkylarkImportFailedException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().hasMessageThat().contains(("Label '//a/b:c/c.bzl' crosses boundary of subpackage 'a/b/c' (perhaps you meant to " + "put the colon here: '//a/b/c:c.bzl'?)"));
    }

    @Test
    public void testLoadUsingLabelThatCrossesBoundaryOfPackage_Allow_OfDifferentPkgAbove() throws Exception {
        scratch.file("a/b/BUILD");
        scratch.file("a/b/b.bzl", "load('//a/c:c/c.bzl', 'c')");
        scratch.file("a/BUILD");
        scratch.file("a/c/c/c.bzl", "c = 42");
        // With the default of
        // --incompatible_disallow_load_labels_to_cross_subpackage_boundaries=false,
        // SkylarkImportLookupValue(//a/b:b.bzl) has an error because ASTFileLookupValue(//a/c:c/c.bzl)
        // because package //a/c doesn't exist. The behavior with
        // --incompatible_disallow_load_labels_to_cross_subpackage_boundaries=true is stricter, but we
        // still have an explicit test for this case so that way we don't forget to think about it when
        // we address the TODO in ASTFileLookupFunction.
        SkyKey skylarkImportLookupKey = key("//a/b:b.bzl");
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().isInstanceOf(SkylarkImportFailedException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().hasMessageThat().contains(("Unable to load package for '//a/c:c/c.bzl': BUILD file not " + "found on package path"));
    }

    @Test
    public void testLoadUsingLabelThatCrossesBoundaryOfPackage_Disallow_OfDifferentPkgAbove() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_disallow_load_labels_to_cross_package_boundaries");
        scratch.file("a/b/BUILD");
        scratch.file("a/b/b.bzl", "load('//a/c:c/c.bzl', 'c')");
        scratch.file("a/BUILD");
        scratch.file("a/c/c/c.bzl", "c = 42");
        SkyKey skylarkImportLookupKey = key("//a/b:b.bzl");
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().isInstanceOf(SkylarkImportFailedException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().hasMessageThat().contains(("Label '//a/c:c/c.bzl' crosses boundary of package 'a' (perhaps you meant to put the " + "colon here: '//a:c/c/c.bzl'?)"));
    }

    @Test
    public void testWithNonExistentRepository_And_DisallowLoadUsingLabelThatCrossesBoundaryOfPackage() throws Exception {
        setSkylarkSemanticsOptions("--incompatible_disallow_load_labels_to_cross_package_boundaries");
        scratch.file("BUILD", "load(\"@repository//dir:file.bzl\", \"foo\")");
        SkyKey skylarkImportLookupKey = key("@repository//dir:file.bzl");
        EvaluationResult<com.google.devtools.build.lib.skyframe.SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.hasError()).isTrue();
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().isInstanceOf(SkylarkImportFailedException.class);
        EvaluationResultSubjectFactory.assertThatEvaluationResult(result).hasErrorEntryForKeyThat(skylarkImportLookupKey).hasExceptionThat().hasMessageThat().contains(("Unable to find package for @repository//dir:file.bzl: The repository '@repository' " + "could not be resolved."));
    }

    @Test
    public void testLoadBzlFileFromWorkspaceWithRemapping() throws Exception {
        scratch.deleteFile(preludeLabelRelativePath);
        Path p = scratch.overwriteFile("WORKSPACE", "local_repository(", "    name = 'y',", "    path = '/y'", ")", "local_repository(", "    name = 'a',", "    path = '/a',", "    repo_mapping = {'@x' : '@y'}", ")", "load('@a//:a.bzl', 'a_symbol')");
        scratch.file("/y/WORKSPACE");
        scratch.file("/y/BUILD");
        scratch.file("/y/y.bzl", "y_symbol = 5");
        scratch.file("/a/WORKSPACE");
        scratch.file("/a/BUILD");
        scratch.file("/a/a.bzl", "load('@x//:y.bzl', 'y_symbol')", "a_symbol = y_symbol");
        Root root = Root.fromPath(p.getParentDirectory());
        RootedPath rootedPath = RootedPath.toRootedPath(root, PathFragment.create("WORKSPACE"));
        SkyKey skylarkImportLookupKey = SkylarkImportLookupValue.keyInWorkspace(Label.parseAbsoluteUnchecked("@a//:a.bzl"), 1, rootedPath);
        EvaluationResult<SkylarkImportLookupValue> result = /* keepGoing= */
        SkyframeExecutorTestUtils.evaluate(getSkyframeExecutor(), skylarkImportLookupKey, false, reporter);
        assertThat(result.get(skylarkImportLookupKey).getEnvironmentExtension().getBindings()).containsEntry("a_symbol", 5);
        assertThat(result.get(skylarkImportLookupKey).getEnvironmentExtension().getBindings()).containsEntry("y_symbol", 5);
    }
}

