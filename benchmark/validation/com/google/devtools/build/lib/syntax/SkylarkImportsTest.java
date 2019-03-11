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
package com.google.devtools.build.lib.syntax;


import SkylarkImports.EXTERNAL_PKG_NOT_ALLOWED_MSG;
import SkylarkImports.INVALID_LABEL_PREFIX;
import SkylarkImports.INVALID_PATH_SYNTAX;
import SkylarkImports.INVALID_TARGET_PREFIX;
import SkylarkImports.MUST_HAVE_BZL_EXT_MSG;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link SkylarkImports}.
 */
@RunWith(JUnit4.class)
public class SkylarkImportsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValidAbsoluteLabel() throws Exception {
        /* expected label */
        /* expected path */
        validAbsoluteLabelTest("//some/skylark:file.bzl", "//some/skylark:file.bzl", "/some/skylark/file.bzl");
    }

    @Test
    public void testValidAbsoluteLabelWithRepo() throws Exception {
        /* expected label */
        /* expected path */
        validAbsoluteLabelTest("@my_repo//some/skylark:file.bzl", "@my_repo//some/skylark:file.bzl", "/some/skylark/file.bzl");
    }

    @Test
    public void testValidAbsoluteLabelWithRepoRemapped() throws Exception {
        String labelString = "@orig_repo//some/skylark:file.bzl";
        String remappedLabelString = "@new_repo//some/skylark:file.bzl";
        String expectedPath = "/some/skylark/file.bzl";
        ImmutableMap<RepositoryName, RepositoryName> repositoryMapping = ImmutableMap.of(RepositoryName.create("@orig_repo"), RepositoryName.create("@new_repo"));
        SkylarkImport importForLabel = SkylarkImports.create(labelString, repositoryMapping);
        assertThat(importForLabel.getImportString()).named("getImportString()").isEqualTo(labelString);
        Label irrelevantContainingFile = Label.parseAbsoluteUnchecked("//another/path:BUILD");
        assertThat(importForLabel.getLabel(irrelevantContainingFile)).named("getLabel()").isEqualTo(Label.parseAbsoluteUnchecked(remappedLabelString));
        assertThat(importForLabel.asPathFragment()).named("asPathFragment()").isEqualTo(PathFragment.create(expectedPath));
    }

    @Test
    public void testValidRelativeSimpleLabelInPackageDir() throws Exception {
        /* containing */
        /* expected label */
        /* expected path */
        validRelativeLabelTest(":file.bzl", "//some/skylark:BUILD", "//some/skylark:file.bzl", "file.bzl");
    }

    @Test
    public void testValidRelativeSimpleLabelInPackageSubdir() throws Exception {
        /* containing */
        /* expected label */
        /* expected path */
        validRelativeLabelTest(":file.bzl", "//some/path/to:skylark/parent.bzl", "//some/path/to:file.bzl", "file.bzl");
    }

    @Test
    public void testValidRelativeComplexLabelInPackageDir() throws Exception {
        /* containing */
        /* expected label */
        /* expected path */
        validRelativeLabelTest(":subdir/containing/file.bzl", "//some/skylark:BUILD", "//some/skylark:subdir/containing/file.bzl", "subdir/containing/file.bzl");
    }

    @Test
    public void testValidRelativeComplexLabelInPackageSubdir() throws Exception {
        /* containing */
        /* expected label */
        /* expected path */
        validRelativeLabelTest(":subdir/containing/file.bzl", "//some/path/to:skylark/parent.bzl", "//some/path/to:subdir/containing/file.bzl", "subdir/containing/file.bzl");
    }

    @Test
    public void testInvalidAbsoluteLabelSyntax() throws Exception {
        // final '/' is illegal
        invalidImportTest("//some/skylark/:file.bzl", INVALID_LABEL_PREFIX);
    }

    @Test
    public void testInvalidPathSyntax() throws Exception {
        invalidImportTest("some/path/foo.bzl", INVALID_PATH_SYNTAX);
    }

    @Test
    public void testInvalidAbsoluteLabelSyntaxWithRepo() throws Exception {
        // final '/' is illegal
        invalidImportTest("@my_repo//some/skylark/:file.bzl", INVALID_LABEL_PREFIX);
    }

    @Test
    public void tesInvalidAbsoluteLabelMissingBzlExt() throws Exception {
        invalidImportTest("//some/skylark:file", MUST_HAVE_BZL_EXT_MSG);
    }

    @Test
    public void tesInvalidAbsoluteLabelReferencesExternalPkg() throws Exception {
        invalidImportTest("//external:file.bzl", EXTERNAL_PKG_NOT_ALLOWED_MSG);
    }

    @Test
    public void tesInvalidAbsolutePathBzlExtImplicit() throws Exception {
        invalidImportTest("/some/skylark/file.bzl", INVALID_PATH_SYNTAX);
    }

    @Test
    public void testInvalidRelativeLabelMissingBzlExt() throws Exception {
        invalidImportTest(":file", MUST_HAVE_BZL_EXT_MSG);
    }

    @Test
    public void testInvalidRelativeLabelSyntax() throws Exception {
        invalidImportTest("::file.bzl", INVALID_TARGET_PREFIX);
    }

    @Test
    public void testInvalidRelativePathBzlExtImplicit() throws Exception {
        invalidImportTest("file.bzl", INVALID_PATH_SYNTAX);
    }

    @Test
    public void testInvalidRelativePathNoSubdirs() throws Exception {
        invalidImportTest("path/to/file", INVALID_PATH_SYNTAX);
    }

    @Test
    public void testInvalidRelativePathInvalidFilename() throws Exception {
        // tab character is invalid
        invalidImportTest("\tfile", INVALID_PATH_SYNTAX);
    }

    @Test
    public void serialization() throws Exception {
        runTests();
    }
}

