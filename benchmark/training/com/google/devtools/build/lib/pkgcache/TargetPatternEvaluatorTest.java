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


import ModifiedFileSet.EVERYTHING_MODIFIED;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.cmdline.ResolvedTargets;
import com.google.devtools.build.lib.cmdline.TargetParsingException;
import com.google.devtools.build.lib.packages.Target;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.lib.vfs.ModifiedFileSet;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TargetPatternEvaluator}.
 */
@RunWith(JUnit4.class)
public class TargetPatternEvaluatorTest extends AbstractTargetPatternEvaluatorTest {
    private PathFragment fooOffset;

    private Set<Label> rulesBeneathFoo;

    private Set<Label> rulesInFoo;

    private Set<Label> targetsInFoo;

    private Set<Label> targetsInFooBar;

    private Set<Label> targetsBeneathFoo;

    private Set<Label> targetsInOtherrules;

    @Test
    public void testModifiedBuildFile() throws Exception {
        assertThat(parseList("foo:all")).containsExactlyElementsIn(rulesInFoo);
        assertNoEvents();
        scratch.overwriteFile("foo/BUILD", "cc_library(name = 'foo1', srcs = [ 'foo1.cc' ], hdrs = [ 'foo1.h' ])", "cc_library(name = 'foo2', srcs = [ 'foo1.cc' ], hdrs = [ 'foo1.h' ])");
        invalidate("foo/BUILD");
        assertThat(parseList("foo:all")).containsExactlyElementsIn(AbstractTargetPatternEvaluatorTest.labels("//foo:foo1", "//foo:foo2"));
    }

    /**
     * Test that the relative path label parsing behaves as stated in the target-syntax documentation.
     */
    @Test
    public void testRelativePathLabel() throws Exception {
        scratch.file("sub/BUILD", "exports_files(['dir2/dir2'])");
        scratch.file("sub/dir/BUILD", "exports_files(['dir2'])");
        scratch.file("sub/dir/dir/BUILD", "exports_files(['dir'])");
        // sub/dir/dir is a package
        assertThat(parseIndividualTarget("sub/dir/dir").toString()).isEqualTo("//sub/dir/dir:dir");
        // sub/dir is a package but not sub/dir/dir2
        assertThat(parseIndividualTarget("sub/dir/dir2").toString()).isEqualTo("//sub/dir:dir2");
        // sub is a package but not sub/dir2
        assertThat(parseIndividualTarget("sub/dir2/dir2").toString()).isEqualTo("//sub:dir2/dir2");
    }

    /**
     * Regression test for a bug.
     */
    @Test
    public void testDotDotDotDoesntMatchDeletedPackages() throws Exception {
        scratch.file("x/y/BUILD", "cc_library(name='y')");
        scratch.file("x/z/BUILD", "cc_library(name='z')");
        setDeletedPackages(Sets.newHashSet(PackageIdentifier.createInMainRepo("x/y")));
        assertThat(parseList("x/...")).isEqualTo(Sets.newHashSet(Label.parseAbsolute("//x/z", ImmutableMap.of())));
    }

    @Test
    public void testDotDotDotDoesntMatchDeletedPackagesRelative() throws Exception {
        scratch.file("x/y/BUILD", "cc_library(name='y')");
        scratch.file("x/z/BUILD", "cc_library(name='z')");
        setDeletedPackages(Sets.newHashSet(PackageIdentifier.createInMainRepo("x/y")));
        assertThat(AbstractTargetPatternEvaluatorTest.targetsToLabels(TargetPatternEvaluatorTest.getFailFast(AbstractTargetPatternEvaluatorTest.parseTargetPatternList(PathFragment.create("x"), parser, parsingListener, ImmutableList.of("..."), false)))).isEqualTo(Sets.newHashSet(Label.parseAbsolute("//x/z", ImmutableMap.of())));
    }

    @Test
    public void testDeletedPackagesIncrementality() throws Exception {
        scratch.file("x/y/BUILD", "cc_library(name='y')");
        scratch.file("x/z/BUILD", "cc_library(name='z')");
        assertThat(parseList("x/...")).containsExactly(Label.parseAbsolute("//x/y", ImmutableMap.of()), Label.parseAbsolute("//x/z", ImmutableMap.of()));
        setDeletedPackages(Sets.newHashSet(PackageIdentifier.createInMainRepo("x/y")));
        assertThat(parseList("x/...")).containsExactly(Label.parseAbsolute("//x/z", ImmutableMap.of()));
        setDeletedPackages(ImmutableSet.<PackageIdentifier>of());
        assertThat(parseList("x/...")).containsExactly(Label.parseAbsolute("//x/y", ImmutableMap.of()), Label.parseAbsolute("//x/z", ImmutableMap.of()));
    }

    @Test
    public void testSequenceOfTargetPatterns_Union() throws Exception {
        // No prefix negation operator => union.  Order is not significant.
        assertThat(parseList("foo/...", "foo/bar/...")).containsExactlyElementsIn(rulesBeneathFoo);
        assertThat(parseList("foo/bar/...", "foo/...")).containsExactlyElementsIn(rulesBeneathFoo);
    }

    @Test
    public void testSequenceOfTargetPatterns_SetDifference() throws Exception {
        // Prefix negation operator => set difference.  Order is significant.
        assertThat(parseList("foo/...", "-foo/bar/...")).containsExactlyElementsIn(rulesInFoo);
        assertThat(parseList("-foo/bar/...", "foo/...")).containsExactlyElementsIn(rulesBeneathFoo);
    }

    @Test
    public void testSequenceOfTargetPatterns_SetDifferenceRelative() throws Exception {
        // Prefix negation operator => set difference.  Order is significant.
        assertThat(parseListRelative("...", "-bar/...")).containsExactlyElementsIn(rulesInFoo);
        assertThat(parseListRelative("-bar/...", "...")).containsExactlyElementsIn(rulesBeneathFoo);
    }

    /**
     * Regression test for bug: "Bogus 'helpful' error message"
     */
    @Test
    public void testHelpfulMessageForDirectoryWhichIsASubdirectoryOfAPackage() throws Exception {
        scratch.file("bar/BUILD");
        scratch.file("bar/quux/somefile");
        expectError(("no such target '//bar:quux': target 'quux' not declared in package 'bar'; " + (("however, a source directory of this name exists.  (Perhaps add " + "\'exports_files([\"quux\"])\' to bar/BUILD, or define a filegroup?) defined by ") + "/workspace/bar/BUILD")), "bar/quux");
    }

    @Test
    public void testKeepGoingPartiallyBadPackage() throws Exception {
        scratch.file("x/y/BUILD", "filegroup(name = 'a')", "BROKEN", "filegroup(name = 'b')");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Pair<Set<Label>, Boolean> result = parseListKeepGoing("//x/...");
        assertContainsEvent("name 'BROKEN' is not defined");
        assertThat(result.first).containsExactlyElementsIn(Sets.newHashSet(Label.parseAbsolute("//x/y:a", ImmutableMap.of()), Label.parseAbsolute("//x/y:b", ImmutableMap.of())));
        assertThat(result.second).isFalse();
    }

    @Test
    public void testKeepGoingMissingRecursiveDirectory() throws Exception {
        assertKeepGoing(rulesBeneathFoo, "Skipping 'nosuchpkg/...': no targets found beneath 'nosuchpkg'", "nosuchpkg/...", "foo/...");
        eventCollector.clear();
        assertKeepGoing(rulesBeneathFoo, "Skipping 'nosuchdirectory/...': no targets found beneath 'nosuchdirectory'", "nosuchdirectory/...", "foo/...");
    }

    @Test
    public void testKeepGoingMissingTarget() throws Exception {
        assertKeepGoing(rulesBeneathFoo, ("Skipping '//otherrules:missing_target': no such target " + ("'//otherrules:missing_target': target 'missing_target' not declared in " + "package 'otherrules'")), "//otherrules:missing_target", "foo/...");
    }

    @Test
    public void testKeepGoingOnAllRulesBeneath() throws Exception {
        scratch.file("foo/bar/bad/BUILD", "invalid build file");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Pair<Set<Label>, Boolean> result = parseListKeepGoing("foo/...");
        assertThat(result.first).containsExactlyElementsIn(rulesBeneathFoo);
        assertContainsEvent("syntax error at 'build'");
        assertContainsEvent("package contains errors");
        reporter.addHandler(FoundationTestCase.failFastHandler);
        // Even though there was a loading error in the package, parsing the target pattern was
        // successful.
        assertThat(result.second).isFalse();
    }

    @Test
    public void testKeepGoingBadFilenameTarget() throws Exception {
        assertKeepGoing(rulesBeneathFoo, "no such target '//:bad/filename/target'", "bad/filename/target", "foo/...");
    }

    @Test
    public void testMoreThanOneBadPatternFailFast() throws Exception {
        try {
            /* keepGoing= */
            AbstractTargetPatternEvaluatorTest.parseTargetPatternList(parser, parsingListener, ImmutableList.of("bad/filename/target", "other/bad/filename/target"), false);
            Assert.fail();
        } catch (TargetParsingException e) {
            assertThat(e).hasMessageThat().contains("no such target");
        }
    }

    @Test
    public void testMentioningBuildFile() throws Exception {
        ResolvedTargets<Target> result = AbstractTargetPatternEvaluatorTest.parseTargetPatternList(parser, parsingListener, Arrays.asList("//foo/bar/BUILD"), false);
        assertThat(result.hasError()).isFalse();
        assertThat(result.getTargets()).hasSize(1);
        Label label = Iterables.getOnlyElement(result.getTargets()).getLabel();
        assertThat(label.getName()).isEqualTo("BUILD");
        assertThat(label.getPackageName()).isEqualTo("foo/bar");
    }

    /**
     * Regression test for bug: '"Target pattern parsing failed. Continuing anyway" appears, even
     * without --keep_going'
     */
    @Test
    public void testLoadingErrorsAreNotParsingErrors() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.file("loading/BUILD", "cc_library(name='y', deps=['a'])", "cc_library(name='a', deps=['b'])", "cc_library(name='b', deps=['c'])", "genrule(name='c', outs=['c.out'])");
        Pair<Set<Label>, Boolean> result = parseListKeepGoing("//loading:y");
        assertThat(result.first).containsExactly(Label.parseAbsolute("//loading:y", ImmutableMap.of()));
        assertContainsEvent("missing value for mandatory attribute");
        assertThat(result.second).isFalse();
    }

    /**
     * Regression test for bug: "IllegalStateException in BuildTool.prepareToBuild()"
     */
    @Test
    public void testTestingIsSubset() throws Exception {
        scratch.file("test/BUILD", "cc_library(name = 'bar1')", "cc_test(name = 'test', deps = [':bar1'], tags = ['manual'])");
        assertThat(parseList(FilteringPolicies.FILTER_TESTS, "//test:test", "-//test:all")).containsExactlyElementsIn(AbstractTargetPatternEvaluatorTest.labels());
    }

    @Test
    public void testAddedPkg() throws Exception {
        invalidate(EVERYTHING_MODIFIED);
        scratch.dir("h/i/j/k/BUILD");
        scratch.file("h/BUILD", "sh_library(name='h')");
        assertThat(parseList("//h/...")).containsExactlyElementsIn(AbstractTargetPatternEvaluatorTest.labels("//h"));
        scratch.file("h/i/j/BUILD", "sh_library(name='j')");
        // Modifications not yet known.
        assertThat(parseList("//h/...")).containsExactlyElementsIn(AbstractTargetPatternEvaluatorTest.labels("//h"));
        ModifiedFileSet modifiedFileSet = ModifiedFileSet.builder().modify(PathFragment.create("h/i/j/BUILD")).build();
        invalidate(modifiedFileSet);
        assertThat(parseList("//h/...")).containsExactly(Label.parseAbsolute("//h/i/j:j", ImmutableMap.of()), Label.parseAbsolute("//h", ImmutableMap.of()));
    }

    @Test
    public void testAddedFilesAndDotDotDot() throws Exception {
        invalidate(EVERYTHING_MODIFIED);
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        scratch.dir("h");
        try {
            parseList("//h/...");
            Assert.fail("TargetParsingException expected");
        } catch (TargetParsingException e) {
            // expected
        }
        scratch.file("h/i/j/k/BUILD", "sh_library(name='l')");
        ModifiedFileSet modifiedFileSet = ModifiedFileSet.builder().modify(PathFragment.create("h")).modify(PathFragment.create("h/i")).modify(PathFragment.create("h/i/j")).modify(PathFragment.create("h/i/j/k")).modify(PathFragment.create("h/i/j/k/BUILD")).build();
        invalidate(modifiedFileSet);
        reporter.addHandler(FoundationTestCase.failFastHandler);
        Set<Label> nonEmptyResult = parseList("//h/...");
        assertThat(nonEmptyResult).containsExactly(Label.parseAbsolute("//h/i/j/k:l", ImmutableMap.of()));
    }

    @Test
    public void testBrokenSymlinkRepaired() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path tuv = scratch.dir("t/u/v");
        tuv.getChild("BUILD").createSymbolicLink(PathFragment.create("../../BUILD"));
        try {
            parseList("//t/...");
            Assert.fail("TargetParsingException expected");
        } catch (TargetParsingException e) {
            // expected
        }
        scratch.file("t/BUILD", "sh_library(name='t')");
        ModifiedFileSet modifiedFileSet = ModifiedFileSet.builder().modify(PathFragment.create("t/BUILD")).build();
        invalidate(modifiedFileSet);
        reporter.addHandler(FoundationTestCase.failFastHandler);
        Set<Label> result = parseList("//t/...");
        assertThat(result).containsExactly(Label.parseAbsolute("//t:t", ImmutableMap.of()), Label.parseAbsolute("//t/u/v:t", ImmutableMap.of()));
    }

    @Test
    public void testInfiniteTreeFromSymlinks() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path ab = scratch.dir("a/b");
        ab.getChild("c").createSymbolicLink(PathFragment.create("../b"));
        scratch.file("a/b/BUILD", "filegroup(name='g')");
        ResolvedTargets<Target> result = AbstractTargetPatternEvaluatorTest.parseTargetPatternList(parser, parsingListener, ImmutableList.of("//a/b/..."), true);
        assertThat(AbstractTargetPatternEvaluatorTest.targetsToLabels(result.getTargets())).containsExactly(Label.parseAbsolute("//a/b:g", ImmutableMap.of()));
    }

    @Test
    public void testSymlinkCycle() throws Exception {
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        Path ab = scratch.dir("a/b");
        ab.getChild("c").createSymbolicLink(PathFragment.create("c"));
        scratch.file("a/b/BUILD", "filegroup(name='g')");
        ResolvedTargets<Target> result = AbstractTargetPatternEvaluatorTest.parseTargetPatternList(parser, parsingListener, ImmutableList.of("//a/b/..."), true);
        assertThat(AbstractTargetPatternEvaluatorTest.targetsToLabels(result.getTargets())).contains(Label.parseAbsolute("//a/b:g", ImmutableMap.of()));
    }

    @Test
    public void testPerDirectorySymlinkTraversalOptOut() throws Exception {
        scratch.dir("from-b");
        scratch.file("from-b/BUILD", "filegroup(name = 'from-b')");
        scratch.dir("from-c");
        scratch.file("from-c/BUILD", "filegroup(name = 'from-c')");
        Path ab = scratch.dir("a/b");
        ab.getChild("symlink").createSymbolicLink(PathFragment.create("../../from-b"));
        scratch.dir("a/b/not-a-symlink");
        scratch.file("a/b/not-a-symlink/BUILD", "filegroup(name = 'not-a-symlink')");
        scratch.file("a/b/DONT_FOLLOW_SYMLINKS_WHEN_TRAVERSING_THIS_DIRECTORY_VIA_A_RECURSIVE_TARGET_PATTERN");
        Path ac = scratch.dir("a/c");
        ac.getChild("symlink").createSymbolicLink(PathFragment.create("../../from-c"));
        ResolvedTargets<Target> result = AbstractTargetPatternEvaluatorTest.parseTargetPatternList(parser, parsingListener, ImmutableList.of("//a/..."), true);
        assertThat(AbstractTargetPatternEvaluatorTest.targetsToLabels(result.getTargets())).containsExactly(Label.parseAbsolute("//a/c/symlink:from-c", ImmutableMap.of()), Label.parseAbsolute("//a/b/not-a-symlink:not-a-symlink", ImmutableMap.of()));
    }

    @Test
    public void testDoesNotRecurseIntoSymlinksToOutputBase() throws Exception {
        Path outputBaseBuildFile = outputBase.getRelative("execroot/workspace/test/BUILD");
        scratch.file(outputBaseBuildFile.getPathString(), "filegroup(name='c')");
        PathFragment targetFragment = outputBase.asFragment().getRelative("execroot/workspace/test");
        Path d = scratch.dir("d");
        d.getChild("c").createSymbolicLink(targetFragment);
        rootDirectory.getChild("convenience").createSymbolicLink(targetFragment);
        Set<Label> result = parseList("//...");
        assertThat(result).doesNotContain(Label.parseAbsolute("//convenience:c", ImmutableMap.of()));
        assertThat(result).doesNotContain(Label.parseAbsolute("//d/c:c", ImmutableMap.of()));
    }

    @Test
    public void testExternalPackage() throws Exception {
        parseList("external:all");
    }

    @Test
    public void testTopLevelPackage_Relative_BuildFile() throws Exception {
        Set<Label> result = parseList("BUILD");
        assertThat(result).containsExactly(Label.parseAbsolute("//:BUILD", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Relative_DeclaredTarget() throws Exception {
        Set<Label> result = parseList("fg");
        assertThat(result).containsExactly(Label.parseAbsolute("//:fg", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Relative_All() throws Exception {
        expectError("no such target '//:all'", "all");
    }

    @Test
    public void testTopLevelPackage_Relative_ColonAll() throws Exception {
        Set<Label> result = parseList(":all");
        assertThat(result).containsExactly(Label.parseAbsolute("//:fg", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Relative_InputFile() throws Exception {
        Set<Label> result = parseList("foo.cc");
        assertThat(result).containsExactly(Label.parseAbsolute("//:foo.cc", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Relative_InputFile_NoSuchInputFile() throws Exception {
        expectError("no such target '//:nope.cc'", "nope.cc");
    }

    @Test
    public void testTopLevelPackage_Absolute_BuildFile() throws Exception {
        Set<Label> result = parseList("//:BUILD");
        assertThat(result).containsExactly(Label.parseAbsolute("//:BUILD", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Absolute_DeclaredTarget() throws Exception {
        Set<Label> result = parseList("//:fg");
        assertThat(result).containsExactly(Label.parseAbsolute("//:fg", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Absolute_All() throws Exception {
        Set<Label> result = parseList("//:all");
        assertThat(result).containsExactly(Label.parseAbsolute("//:fg", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Absolute_InputFile() throws Exception {
        Set<Label> result = parseList("//:foo.cc");
        assertThat(result).containsExactly(Label.parseAbsolute("//:foo.cc", ImmutableMap.of()));
    }

    @Test
    public void testTopLevelPackage_Absolute_InputFile_NoSuchInputFile() throws Exception {
        expectError("no such target '//:nope.cc'", "//:nope.cc");
    }
}

