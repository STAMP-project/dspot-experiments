/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.config.materials;


import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.security.GoCipher;
import java.io.File;
import java.util.regex.Pattern;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class IgnoreTest {
    private MaterialConfig hgMaterialConfig = MaterialConfigsMother.hgMaterialConfig();

    private String separator = File.separator;

    @Test
    public void shouldIncludeWhenTheTextDoesnotMatchDocumentUnderRoot() {
        IgnoredFiles ignore = new IgnoredFiles("a.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "b.doc"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.doc1"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("A" + (separator)) + "B") + (separator)) + "a.doc")), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("A" + (separator)) + "a.doc")), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreWhenTheTextDoesMatchDocumentUnderRoot() {
        IgnoredFiles ignore = new IgnoredFiles("a.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.doc"), Matchers.is(true));
    }

    @Test
    public void shouldIncludeWhenTheTextUnderRootIsNotADocument() {
        IgnoredFiles ignore = new IgnoredFiles("*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.pdf"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.doc.aaa"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("A" + (separator)) + "a.doc")), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreWhenTheTextUnderRootIsADocument() {
        IgnoredFiles ignore = new IgnoredFiles("*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.doc"), Matchers.is(true));
    }

    @Test
    public void shouldIncludeWhenTextIsNotDocumentInChildOfRootFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("*/*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.doc"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("A" + (separator)) + "B") + (separator)) + "c.doc")), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreWhenTextIsDocumentInChildOfRootFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("*/*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("A" + (separator)) + "a.doc")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("B" + (separator)) + "c.doc")), Matchers.is(true));
    }

    @Test
    public void shouldNormalizeRegex() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("*\\*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("A" + (separator)) + "a.doc")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("B" + (separator)) + "c.doc")), Matchers.is(true));
    }

    @Test
    public void shouldIncludeWhenTextIsNotADocumentInAnyFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("**/*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.pdf"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("B" + (separator)) + "a.pdf")), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("A" + (separator)) + "B") + (separator)) + "a.pdf")), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreWhenTextIsADocumentInAnyFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("**/*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "a.doc"), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("A" + (separator)) + "a.doc")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("A" + (separator)) + "B") + (separator)) + "a.doc")), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreWhenTextIsADocumentInAnyFolderWhenDirectoryWildcardNotInTheBegining() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("foo*/**/*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "foo-hi/bar/a.doc"), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "bar/baz/b.doc"), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreWhenTextIsADocumentInAnyFolderWhenDirectoryWildcardNotInTheBeginingAndTerminatesInWildcard() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("**/foo*/**/*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "foo-hi/bar/a.doc"), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "bar/baz/b.doc"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "hello-world/woods/foo-hi/bar/a.doc"), Matchers.is(true));
    }

    @Test
    public void shouldIncludeIfTheTextIsNotADocumentInTheSpecifiedFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("ROOTFOLDER/*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, "shouldNotBeIgnored.doc"), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("ANYFOLDER" + (separator)) + "shouldNotBeIgnored.doc")), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, ((((("ROOTFOLDER" + (separator)) + "") + "ANYFOLDER") + (separator)) + "shouldNotBeIgnored.doc")), Matchers.is(false));
    }

    @Test
    public void shouldIncludIgnoreIfTextIsADocumentInTheSpecifiedFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("ROOTFOLDER/*.doc");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("ROOTFOLDER" + (separator)) + "a.doc")), Matchers.is(true));
    }

    @Test
    public void shouldIncludeIfTextIsNotUnderAGivenFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("**/DocumentFolder/*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("A" + (separator)) + "shouldNotBeIgnored.doc")), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((((("A" + (separator)) + "DocumentFolder") + (separator)) + "B") + (separator)) + "d.doc")), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreIfTextIsUnderAGivenFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("**/DocumentFolder/*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("A" + (separator)) + "DocumentFolder") + (separator)) + "d.doc")), Matchers.is(true));
    }

    @Test
    public void shouldIncludeIfTextIsNotUnderAFolderMatchingTheGivenFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("**/*Test*/*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("A" + (separator)) + "shouldNotBeIgnored.doc")), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("B/NotIgnored" + (separator)) + "isIgnored")), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreIfTextIsUnderAFolderMatchingTheGivenFolder() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("**/*Test*/*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("B" + (separator)) + "SomethingTestThis") + (separator)) + "isIgnored")), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreEverythingUnderAFolderWithWildcards() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("Test/**/*.*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("Test" + (separator)) + "foo.txt")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("Test" + (separator)) + "subdir") + (separator)) + "foo.txt")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((((("Test" + (separator)) + "subdir") + (separator)) + "subdir") + (separator)) + "foo.txt")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((((("Test" + (separator)) + "subdir") + (separator)) + "subdir") + (separator)) + "foo")), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreEverythingUnderAFolderWithWildcardsWithoutExtension() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("Test/**/*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("Test" + (separator)) + "foo.txt")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("Test" + (separator)) + "subdir") + (separator)) + "foo.txt")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((((("Test" + (separator)) + "subdir") + (separator)) + "subdir") + (separator)) + "foo.txt")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((((("Test" + (separator)) + "subdir") + (separator)) + "subdir") + (separator)) + "foo")), Matchers.is(true));
    }

    @Test
    public void shouldSkipDiot() throws Exception {
        IgnoredFiles ignore = new IgnoredFiles("helper/*.*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (("helper" + (separator)) + "configuration_reference.html")), Matchers.is(true));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("helper" + (separator)) + "conf-docs") + (separator)) + "configuration_reference.html")), Matchers.is(false));
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((((((("helper" + (separator)) + "resources") + (separator)) + "images") + (separator)) + "cruise") + (separator)) + "dependent_build.png")), Matchers.is(false));
    }

    @Test
    public void shouldAddErrorToItsErrorCollection() {
        IgnoredFiles ignore = new IgnoredFiles("helper/*.*");
        ignore.addError("pattern", "not allowed");
        Assert.assertThat(ignore.errors().on("pattern"), Matchers.is("not allowed"));
    }

    @Test
    public void shouldEscapeAllValidSpecialCharactersInPattern() {
        // see mingle #5700
        hgMaterialConfig = new com.thoughtworks.go.config.materials.tfs.TfsMaterialConfig(new GoCipher());
        IgnoredFiles ignore = new IgnoredFiles("$/tfs_repo/Properties/*.*");
        Assert.assertThat(ignore.shouldIgnore(hgMaterialConfig, (((("$/tfs_repo" + (separator)) + "Properties") + (separator)) + "AssemblyInfo.cs")), Matchers.is(true));
    }

    @Test
    public void understandPatternPunct() {
        Assert.assertThat(Pattern.matches("a\\.doc", "a.doc"), Matchers.is(true));
        Assert.assertThat(Pattern.matches("\\p{Punct}", "*"), Matchers.is(true));
        Assert.assertThat(Pattern.matches("\\p{Punct}", "{"), Matchers.is(true));
        Assert.assertThat(Pattern.matches("\\p{Punct}", "]"), Matchers.is(true));
        Assert.assertThat(Pattern.matches("\\p{Punct}", "-"), Matchers.is(true));
        Assert.assertThat(Pattern.matches("\\p{Punct}", "."), Matchers.is(true));
    }
}

