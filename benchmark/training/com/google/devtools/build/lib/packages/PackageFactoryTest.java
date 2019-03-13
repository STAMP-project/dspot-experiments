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
package com.google.devtools.build.lib.packages;


import BuildType.LABEL_LIST;
import Type.STRING;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.events.Reporter;
import com.google.devtools.build.lib.packages.PackageFactory.GlobPatternExtractor;
import com.google.devtools.build.lib.packages.util.PackageFactoryTestBase;
import com.google.devtools.build.lib.syntax.BuildFileAST;
import com.google.devtools.build.lib.syntax.Type.BOOLEAN;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.testutil.TestUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.RootedPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@code PackageFactory}.
 */
@RunWith(JUnit4.class)
public class PackageFactoryTest extends PackageFactoryTestBase {
    @Test
    public void testCreatePackage() throws Exception {
        Path buildFile = scratch.file("/pkgname/BUILD", "# empty build file ");
        Package pkg = packages.createPackage("pkgname", RootedPath.toRootedPath(root, buildFile));
        assertThat(pkg.getName()).isEqualTo("pkgname");
        assertThat(Sets.newHashSet(getTargets(Rule.class))).isEmpty();
    }

    @Test
    public void testCreatePackageIsolatedFromOuterErrors() throws Exception {
        ExecutorService e = Executors.newCachedThreadPool();
        final Semaphore beforeError = new Semaphore(0);
        final Semaphore afterError = new Semaphore(0);
        Reporter reporter = new Reporter(new EventBus());
        PackageFactoryTestBase.ParsingTracker parser = new PackageFactoryTestBase.ParsingTracker(beforeError, afterError, reporter);
        final Logger log = Logger.getLogger(PackageFactory.class.getName());
        log.addHandler(parser);
        Level originalLevel = log.getLevel();
        log.setLevel(Level.FINE);
        e.execute(new PackageFactoryTestBase.ErrorReporter(reporter, beforeError, afterError));
        e.execute(parser);
        // wait for all to finish
        e.shutdown();
        assertThat(e.awaitTermination(TestUtils.WAIT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)).isTrue();
        log.removeHandler(parser);
        log.setLevel(originalLevel);
        assertThat(parser.hasParsed()).isTrue();
    }

    @Test
    public void testBadRuleName() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/badrulename/BUILD", "cc_library(name = 3)");
        Package pkg = packages.createPackage("badrulename", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError("cc_library 'name' attribute must be a string");
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testNoRuleName() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/badrulename/BUILD", "cc_library()");
        Package pkg = packages.createPackage("badrulename", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError("cc_library rule has no 'name' attribute");
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testBadPackageName() throws Exception {
        try {
            // PathFragment parsing de-double slashes, and normalization of the path fragment removes
            // up reference (/../), so use triple dot /.../ that will always be a forbidden package name.
            packages.createPackage("not even a legal/.../label", RootedPath.toRootedPath(root, emptyBuildFile("not even a legal/.../label")));
            Assert.fail();
        } catch (NoSuchPackageException e) {
            assertThat(e).hasMessageThat().contains(("no such package 'not even a legal/.../label': " + "illegal package name: 'not even a legal/.../label' "));
        }
    }

    @Test
    public void testColonInExportsFilesTargetName() throws Exception {
        events.setFailFast(false);
        Path path = scratch.file("/googledata/cafe/BUILD", "exports_files(['houseads/house_ads:ca-aol_parenting_html'])");
        Package pkg = packages.createPackage("googledata/cafe", RootedPath.toRootedPath(root, path));
        events.assertContainsError("target names may not contain ':'");
        assertThat(getTargets(FileTarget.class).toString()).doesNotContain("houseads/house_ads:ca-aol_parenting_html");
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testPackageNameWithPROTECTEDIsOk() throws Exception {
        events.setFailFast(false);
        // One "PROTECTED":
        assertThat(isValidPackageName("foo/PROTECTED/bar")).isTrue();
        // Multiple "PROTECTED"s:
        assertThat(isValidPackageName("foo/PROTECTED/bar/PROTECTED/wiz")).isTrue();
    }

    @Test
    public void testDuplicateRuleName() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/duplicaterulename/BUILD", "# -*- python -*-", "proto_library(name = 'spell_proto', srcs = ['spell.proto'], cc_api_version = 2)", "cc_library(name = 'spell_proto')");
        Package pkg = packages.createPackage("duplicaterulename", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("cc_library rule 'spell_proto' in package " + "'duplicaterulename' conflicts with existing proto_library rule"));
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testDuplicatedDependencies() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/has_dupe/BUILD", "cc_library(name='dep')", "cc_library(name='has_dupe', deps=[':dep', ':dep'])");
        Package pkg = packages.createPackage("has_dupe", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("Label '//has_dupe:dep' is duplicated in the 'deps' " + "attribute of rule 'has_dupe'"));
        assertThat(containsErrors()).isTrue();
        assertThat(getRule("has_dupe")).isNotNull();
        assertThat(getRule("dep")).isNotNull();
        assertThat(containsErrors()).isTrue();
        assertThat(containsErrors()).isTrue();// because all rules in an

        // errant package are
        // themselves errant.
    }

    @Test
    public void testPrefixWithinSameRule1() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/fruit/orange/BUILD", "genrule(name='orange', srcs=[], outs=['a', 'a/b'], cmd='')");
        packages.createPackage("fruit/orange", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError("rule 'orange' has conflicting output files 'a/b' and 'a");
    }

    @Test
    public void testPrefixWithinSameRule2() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/fruit/orange/BUILD", "genrule(name='orange', srcs=[], outs=['a/b', 'a'], cmd='')");
        packages.createPackage("fruit/orange", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError("rule 'orange' has conflicting output files 'a' and 'a/b");
    }

    @Test
    public void testPrefixBetweenRules1() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/fruit/kiwi/BUILD", "genrule(name='kiwi1', srcs=[], outs=['a'], cmd='')", "genrule(name='kiwi2', srcs=[], outs=['a/b'], cmd='')");
        packages.createPackage("fruit/kiwi", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("output file 'a/b' of rule 'kiwi2' conflicts " + "with output file 'a' of rule 'kiwi1'"));
    }

    @Test
    public void testPrefixBetweenRules2() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/fruit/kiwi/BUILD", "genrule(name='kiwi1', srcs=[], outs=['a/b'], cmd='')", "genrule(name='kiwi2', srcs=[], outs=['a'], cmd='')");
        packages.createPackage("fruit/kiwi", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("output file 'a' of rule 'kiwi2' conflicts " + "with output file 'a/b' of rule 'kiwi1'"));
    }

    @Test
    public void testPackageConstantIsForbidden() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/pina/BUILD", "cc_library(name=PACKAGE_NAME + '-colada')");
        packages.createPackage("pina", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError("The value 'PACKAGE_NAME' has been removed");
    }

    @Test
    public void testPackageNameFunction() throws Exception {
        Path buildFile = scratch.file("/pina/BUILD", "cc_library(name=package_name() + '-colada')");
        Package pkg = packages.createPackage("pina", RootedPath.toRootedPath(root, buildFile));
        events.assertNoWarningsOrErrors();
        assertThat(containsErrors()).isFalse();
        assertThat(getRule("pina-colada")).isNotNull();
        assertThat(containsErrors()).isFalse();
        assertThat(Sets.newHashSet(getTargets(Rule.class)).size()).isSameAs(1);
    }

    @Test
    public void testPackageConstantInExternalRepositoryIsForbidden() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/external/a/b/BUILD", "genrule(name='c', srcs=[], outs=['ao'], cmd=REPOSITORY_NAME)");
        packages.createPackage(PackageIdentifier.create("@a", PathFragment.create("b")), RootedPath.toRootedPath(root, buildFile), events.reporter());
        events.assertContainsError("The value 'REPOSITORY_NAME' has been removed");
    }

    @Test
    public void testPackageFunctionInExternalRepository() throws Exception {
        Path buildFile = scratch.file("/external/a/b/BUILD", "genrule(name='c', srcs=[], outs=['o'], cmd=repository_name() + ' ' + package_name())");
        Package pkg = packages.createPackage(PackageIdentifier.create("@a", PathFragment.create("b")), RootedPath.toRootedPath(root, buildFile), events.reporter());
        Rule c = pkg.getRule("c");
        assertThat(AggregatingAttributeMapper.of(c).get("cmd", STRING)).isEqualTo("@a b");
    }

    @Test
    public void testMultipleDuplicateRuleName() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/multipleduplicaterulename/BUILD", "# -*- python -*-", "proto_library(name = 'spellcheck_proto',", "         srcs = ['spellcheck.proto'],", "         cc_api_version = 2)", "cc_library(name = 'spellcheck_proto')", "proto_library(name = 'spell_proto',", "         srcs = ['spell.proto'],", "         cc_api_version = 2)", "cc_library(name = 'spell_proto')");
        Package pkg = packages.createPackage("multipleduplicaterulename", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("cc_library rule 'spellcheck_proto' in package " + "'multipleduplicaterulename' conflicts with existing proto_library rule"));
        events.assertContainsError(("cc_library rule 'spell_proto' in package " + "'multipleduplicaterulename' conflicts with existing proto_library rule"));
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testBuildFileTargetExists() throws Exception {
        Path buildFile = scratch.file("/foo/BUILD", "");
        Package pkg = packages.createPackage("foo", RootedPath.toRootedPath(root, buildFile));
        Target target = getTarget("BUILD");
        assertThat(target.getName()).isEqualTo("BUILD");
        // Test that it's memoized:
        assertThat(pkg.getTarget("BUILD")).isSameAs(target);
    }

    @Test
    public void testCreationOfInputFiles() throws Exception {
        Path buildFile = scratch.file("/foo/BUILD", "exports_files(['Z'])", "cc_library(name='W', deps=['X', 'Y'])", "cc_library(name='X', srcs=['X'])", "cc_library(name='Y')");
        Package pkg = packages.createPackage("foo", RootedPath.toRootedPath(root, buildFile));
        assertThat(containsErrors()).isFalse();
        // X is a rule with a circular self-dependency.
        assertThat(pkg.getTarget("X").getClass()).isSameAs(Rule.class);
        // Y is a rule
        assertThat(pkg.getTarget("Y").getClass()).isSameAs(Rule.class);
        // Z is a file
        assertThat(pkg.getTarget("Z").getClass()).isSameAs(InputFile.class);
        // A is nothing
        try {
            pkg.getTarget("A");
            Assert.fail();
        } catch (NoSuchTargetException e) {
            assertThat(e).hasMessage(("no such target '//foo:A': " + "target 'A' not declared in package 'foo' defined by /foo/BUILD"));
        }
        // These are the only input files: BUILD, Z
        Set<String> inputFiles = Sets.newTreeSet();
        for (InputFile inputFile : getTargets(InputFile.class)) {
            inputFiles.add(inputFile.getName());
        }
        assertThat(Lists.newArrayList(inputFiles)).containsExactly("BUILD", "Z").inOrder();
    }

    @Test
    public void testThirdPartyLicenseError() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/third_party/foo/BUILD", "# line 1", "cc_library(name='bar')", "# line 3");
        Package pkg = packages.createPackage("third_party/foo", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("third-party rule '//third_party/foo:bar' lacks a license " + ("declaration with one of the following types: " + "notice, reciprocal, permissive, restricted, unencumbered, by_exception_only")));
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testThirdPartyLicenseExportsFileError() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/third_party/foo/BUILD", "exports_files(['bar'])");
        Package pkg = packages.createPackage("third_party/foo", RootedPath.toRootedPath(root, buildFile));
        events.assertContainsError(("third-party file 'bar' lacks a license " + ("declaration with one of the following types: " + "notice, reciprocal, permissive, restricted, unencumbered, by_exception_only")));
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testDuplicateRuleIsNotAddedToPackage() throws Exception {
        events.setFailFast(false);
        Path path = scratch.file("/dup/BUILD", "proto_library(name = 'dup_proto',", "              srcs  = ['dup.proto'],", "              cc_api_version = 2)", "", "cc_library(name = 'dup_proto',", "           srcs = ['dup.pb.cc', 'dup.pb.h'])");
        Package pkg = packages.createPackage("dup", RootedPath.toRootedPath(root, path));
        events.assertContainsError(("cc_library rule 'dup_proto' in package 'dup' " + "conflicts with existing proto_library rule"));
        assertThat(containsErrors()).isTrue();
        Rule dupProto = pkg.getRule("dup_proto");
        // Check that the first rule of the given name "wins", and that each of the
        // "winning" rule's outputs is a member of the package.
        assertThat(dupProto.getRuleClass()).isEqualTo("proto_library");
        for (OutputFile out : dupProto.getOutputFiles()) {
            assertThat(getTargets(FileTarget.class)).contains(out);
        }
    }

    @Test
    public void testConflictingRuleDoesNotUpdatePackage() throws Exception {
        events.setFailFast(false);
        // In this test, rule2's outputs conflict with rule1, so rule2 is rejected.
        // However, we must check that neither rule2, nor any of its inputs or
        // outputs is a member of the package, and that the conflicting output file
        // "out2" still has rule1 as its getGeneratingRule().
        Path path = scratch.file("/conflict/BUILD", "genrule(name = 'rule1',", "        cmd = '',", "        srcs = ['in1', 'in2'],", "        outs = ['out1', 'out2'])", "genrule(name = 'rule2',", "        cmd = '',", "        srcs = ['in3', 'in4'],", "        outs = ['out3', 'out2'])");
        Package pkg = packages.createPackage("conflict", RootedPath.toRootedPath(root, path));
        events.assertContainsError(("generated file 'out2' in rule 'rule2' " + "conflicts with existing generated file from rule 'rule1'"));
        assertThat(containsErrors()).isTrue();
        assertThat(getRule("rule2")).isNull();
        // Ensure that rule2's "out2" didn't overwrite rule1's:
        assertThat(getGeneratingRule()).isSameAs(getRule("rule1"));
        // None of rule2, its inputs, or its outputs should belong to pkg:
        List<Target> found = new ArrayList<>();
        for (String targetName : ImmutableList.of("rule2", "in3", "in4", "out3")) {
            try {
                found.add(pkg.getTarget(targetName));
                // No fail() here: if there's no exception, we add the name to a list
                // and we check below that it's empty.
            } catch (NoSuchTargetException e) {
                /* good! */
            }
        }
        assertThat(found).isEmpty();
    }

    // Was: Regression test for bug "Rules declared after an error in
    // a package should be considered 'in error'".
    // Now: Regression test for bug "Why aren't ERRORS considered
    // fatal?*"
    @Test
    public void testAllRulesInErrantPackageAreInError() throws Exception {
        events.setFailFast(false);
        Path path = // undefined => error
        scratch.file("/error/BUILD", "genrule(name = 'rule1',", "        cmd = ':',", "        outs = ['out.1'])", "list = ['bad']", "PopulateList(list)", "genrule(name = 'rule2',", "        cmd = ':',", "        outs = list)");
        Package pkg = packages.createPackage("error", RootedPath.toRootedPath(root, path));
        events.assertContainsError("name 'PopulateList' is not defined");
        assertThat(containsErrors()).isTrue();
        // rule1 would be fine but is still marked as in error:
        assertThat(containsErrors()).isTrue();
        // rule2 is considered "in error" because it's after an error.
        // Indeed, it has the wrong "outs" set because the call to PopulateList
        // failed.
        Rule rule2 = pkg.getRule("rule2");
        assertThat(rule2.containsErrors()).isTrue();
        assertThat(Sets.newHashSet(rule2.getOutputFiles())).isEqualTo(Sets.newHashSet(pkg.getTarget("bad")));
    }

    @Test
    public void testHelpfulErrorForMissingExportsFiles() throws Exception {
        Path path = scratch.file("/x/BUILD", "cc_library(name='x', srcs=['x.cc'])");
        scratch.file("/x/x.cc");
        scratch.file("/x/y.cc");
        scratch.file("/x/dir/dummy");
        Package pkg = packages.createPackage("x", RootedPath.toRootedPath(root, path));
        assertThat(pkg.getTarget("x.cc")).isNotNull();// existing and mentioned.

        try {
            pkg.getTarget("y.cc");// existing but not mentioned.

            Assert.fail();
        } catch (NoSuchTargetException e) {
            assertThat(e).hasMessage(("no such target '//x:y.cc': " + ((("target 'y.cc' not declared in package 'x'; " + "however, a source file of this name exists.  ") + "(Perhaps add \'exports_files([\"y.cc\"])\' to x/BUILD?) ") + "defined by /x/BUILD")));
        }
        try {
            pkg.getTarget("z.cc");// non-existent and unmentioned.

            Assert.fail();
        } catch (NoSuchTargetException e) {
            assertThat(e).hasMessage(("no such target '//x:z.cc': " + ("target 'z.cc' not declared in package 'x' (did you mean 'x.cc'?) " + "defined by /x/BUILD")));
        }
        try {
            pkg.getTarget("dir");// existing directory but not mentioned.

            Assert.fail();
        } catch (NoSuchTargetException e) {
            assertThat(e).hasMessage(("no such target '//x:dir': target 'dir' not declared in package 'x'; " + (("however, a source directory of this name exists.  " + "(Perhaps add \'exports_files([\"dir\"])\' to x/BUILD, ") + "or define a filegroup?) defined by /x/BUILD")));
        }
    }

    @Test
    public void testTestSuitesImplicitlyDependOnAllRulesInPackage() throws Exception {
        Path path = scratch.file("/x/BUILD", "java_test(name='j')", "test_suite(name='t1')", "test_suite(name='t2', tests=['//foo'])", "test_suite(name='t3', tests=['//foo'])", "cc_test(name='c')");
        Package pkg = packages.createPackage("x", RootedPath.toRootedPath(root, path));
        // Things to note:
        // - the t1 refers to both :j and :c, even though :c is a forward reference.
        // - $implicit_tests is empty unless tests=[]
        assertThat(PackageFactoryTestBase.attributes(getRule("t1")).get("$implicit_tests", LABEL_LIST)).containsExactlyElementsIn(Sets.newHashSet(Label.parseAbsolute("//x:c", ImmutableMap.of()), Label.parseAbsolute("//x:j", ImmutableMap.of())));
        assertThat(PackageFactoryTestBase.attributes(getRule("t2")).get("$implicit_tests", LABEL_LIST)).isEmpty();
        assertThat(PackageFactoryTestBase.attributes(getRule("t3")).get("$implicit_tests", LABEL_LIST)).isEmpty();
    }

    @Test
    public void testGlobDirectoryExclusion() throws Exception {
        emptyFile("/fruit/data/apple");
        emptyFile("/fruit/data/pear");
        emptyFile("/fruit/data/berry/black");
        emptyFile("/fruit/data/berry/blue");
        Path file = scratch.file("/fruit/BUILD", "cc_library(name = 'yes', srcs = glob(['data/*']))", "cc_library(name = 'no',  srcs = glob(['data/*'], exclude_directories=0))");
        Package pkg = packages.eval("fruit", RootedPath.toRootedPath(root, file));
        events.assertNoWarningsOrErrors();
        List<Label> yesFiles = PackageFactoryTestBase.attributes(getRule("yes")).get("srcs", LABEL_LIST);
        List<Label> noFiles = PackageFactoryTestBase.attributes(getRule("no")).get("srcs", LABEL_LIST);
        assertThat(yesFiles).containsExactly(Label.parseAbsolute("@//fruit:data/apple", ImmutableMap.of()), Label.parseAbsolute("@//fruit:data/pear", ImmutableMap.of()));
        assertThat(noFiles).containsExactly(Label.parseAbsolute("@//fruit:data/apple", ImmutableMap.of()), Label.parseAbsolute("@//fruit:data/pear", ImmutableMap.of()), Label.parseAbsolute("@//fruit:data/berry", ImmutableMap.of()));
    }

    // TODO(bazel-team): This is really a test for GlobCache.
    @Test
    public void testRecursiveGlob() throws Exception {
        emptyFile("/rg/a.cc");
        emptyFile("/rg/foo/bar.cc");
        emptyFile("/rg/foo/foo.cc");
        emptyFile("/rg/foo/wiz/bam.cc");
        emptyFile("/rg/foo/wiz/bum.cc");
        emptyFile("/rg/foo/wiz/quid/gav.cc");
        Path file = scratch.file("/rg/BUILD", "cc_library(name = 'ri', srcs = glob(['**/*.cc']))", "cc_library(name = 're', srcs = glob(['*.cc'], exclude=['**/*.c']))");
        Package pkg = packages.eval("rg", RootedPath.toRootedPath(root, file));
        events.assertNoWarningsOrErrors();
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("BUILD", "a.cc", "foo", "foo/bar.cc", "foo/foo.cc", "foo/wiz", "foo/wiz/bam.cc", "foo/wiz/bum.cc", "foo/wiz/quid", "foo/wiz/quid/gav.cc"), "**");
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("a.cc", "foo/bar.cc", "foo/foo.cc", "foo/wiz/bam.cc", "foo/wiz/bum.cc", "foo/wiz/quid/gav.cc"), "**/*.cc");
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("foo/bar.cc", "foo/wiz/bam.cc", "foo/wiz/bum.cc"), "**/b*.cc");
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("foo/bar.cc", "foo/foo.cc", "foo/wiz/bam.cc", "foo/wiz/bum.cc", "foo/wiz/quid/gav.cc"), "**/*/*.cc");
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("foo/wiz/quid/gav.cc"), "foo/**/quid/*.cc");
        PackageFactoryTestBase.assertEvaluates(pkg, Collections.<String>emptyList(), ImmutableList.of("*.cc", "*/*.cc", "*/*/*.cc"), ImmutableList.of("**/*.cc"));
        PackageFactoryTestBase.assertEvaluates(pkg, Collections.<String>emptyList(), ImmutableList.of("**/*.cc"), ImmutableList.of("**/*.cc"));
        PackageFactoryTestBase.assertEvaluates(pkg, Collections.<String>emptyList(), ImmutableList.of("**/*.cc"), ImmutableList.of("*.cc", "*/*.cc", "*/*/*.cc", "*/*/*/*.cc"));
        PackageFactoryTestBase.assertEvaluates(pkg, Collections.<String>emptyList(), ImmutableList.of("**"), ImmutableList.of("*", "*/*", "*/*/*", "*/*/*/*"));
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("foo/bar.cc", "foo/foo.cc", "foo/wiz/bam.cc", "foo/wiz/bum.cc", "foo/wiz/quid/gav.cc"), ImmutableList.of("**/*.cc"), ImmutableList.of("*.cc"));
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("a.cc", "foo/wiz/bam.cc", "foo/wiz/bum.cc", "foo/wiz/quid/gav.cc"), ImmutableList.of("**/*.cc"), ImmutableList.of("*/*.cc"));
        PackageFactoryTestBase.assertEvaluates(pkg, ImmutableList.of("a.cc", "foo/bar.cc", "foo/foo.cc", "foo/wiz/quid/gav.cc"), ImmutableList.of("**/*.cc"), ImmutableList.of("**/wiz/*.cc"));
    }

    @Test
    public void testInsufficientArgumentGlobErrors() throws Exception {
        events.setFailFast(false);
        assertGlobFails("glob()", ("insufficient arguments received by glob(include: sequence of strings, " + ("*, exclude: sequence of strings = [], exclude_directories: int = 1) " + "(got 0, expected at least 1)")));
    }

    @Test
    public void testGlobUnamedExclude() throws Exception {
        events.setFailFast(false);
        assertGlobFails("glob(['a'], ['b'])", ("too many (2) positional arguments in call to glob(include: sequence of strings, " + "*, exclude: sequence of strings = [], exclude_directories: int = 1)"));
    }

    @Test
    public void testTooManyArgumentsGlobErrors() throws Exception {
        events.setFailFast(false);
        assertGlobFails("glob(1,2,3,4)", ("too many (4) positional arguments in call to glob(include: sequence of strings, " + "*, exclude: sequence of strings = [], exclude_directories: int = 1)"));
    }

    @Test
    public void testGlobEnforcesListArgument() throws Exception {
        events.setFailFast(false);
        assertGlobFails("glob(1, exclude=2)", ("argument \'include\' has type \'int\', but should be \'sequence\'\n" + "in call to builtin function glob(include, *, exclude, exclude_directories)"));
    }

    @Test
    public void testGlobEnforcesListOfStringsArguments() throws Exception {
        events.setFailFast(false);
        assertGlobFails("glob(['a', 'b'], exclude=['c', 42])", "expected value of type 'string' for element 1 of 'glob' argument, but got 42 (int)");
    }

    @Test
    public void testGlobNegativeTest() throws Exception {
        // Negative test that assertGlob does throw an error when asserting against the wrong values.
        try {
            /* result= */
            /* includes= */
            /* excludes= */
            /* excludeDirs= */
            assertGlobMatches(ImmutableList.of("Wombat1.java", "This_file_doesn_t_exist.java"), ImmutableList.of("W*", "subdir"), ImmutableList.<String>of(), true);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("ERROR /globs/BUILD:2:73: name 'this_will_fail' is not defined");
        }
    }

    @Test
    public void testGlobExcludeDirectories() throws Exception {
        /* result= */
        /* includes= */
        /* excludes= */
        /* excludeDirs= */
        assertGlobMatches(ImmutableList.of("Wombat1.java", "Wombat2.java"), ImmutableList.of("W*", "subdir"), ImmutableList.<String>of(), true);
    }

    @Test
    public void testGlobDoesNotExcludeDirectories() throws Exception {
        /* result= */
        /* includes= */
        /* excludes= */
        /* excludeDirs= */
        assertGlobMatches(ImmutableList.of("Wombat1.java", "Wombat2.java", "subdir"), ImmutableList.of("W*", "subdir"), ImmutableList.<String>of(), false);
    }

    @Test
    public void testGlobWithEmptyExcludedList() throws Exception {
        /* result= */
        /* includes= */
        /* excludes= */
        /* excludeDirs= */
        assertGlobMatches(ImmutableList.of("Wombat1.java", "Wombat2.java"), ImmutableList.of("W*"), Collections.<String>emptyList(), false);
    }

    @Test
    public void testGlobWithQuestionMarkProducesError() throws Exception {
        assertGlobProducesError("Wombat?.java", true);
    }

    @Test
    public void testGlobWithoutQuestionMarkDoesntProduceError() throws Exception {
        assertGlobProducesError("Wombat*.java", false);
    }

    @Test
    public void testGlobWithNonMatchingExcludedList() throws Exception {
        /* result= */
        /* includes= */
        /* excludes= */
        /* excludeDirs= */
        assertGlobMatches(ImmutableList.of("Wombat1.java"), ImmutableList.of("W*"), ImmutableList.of("*2*"), false);
    }

    @Test
    public void testGlobWithTwoMatchingGlobExpressionsAndNonmatchingExclusion() throws Exception {
        /* result= */
        /* includes= */
        /* excludes= */
        /* excludeDirs= */
        assertGlobMatches(ImmutableList.of("Wombat1.java", "subdir/Wombat3.java"), ImmutableList.of("W*", "subdir/W*"), ImmutableList.of("*2*"), false);
    }

    @Test
    public void testGlobWithSubdirMatchAndExclusion() throws Exception {
        /* result= */
        /* includes= */
        /* excludes= */
        /* excludeDirs= */
        assertGlobMatches(ImmutableList.of("subdir/Wombat3.java"), ImmutableList.of("W*", "subdir/W*"), ImmutableList.of("Wombat*.java"), false);
    }

    @Test
    public void testBadCharacterInGlob() throws Exception {
        events.setFailFast(false);
        assertGlobFails("glob(['?'])", "glob pattern '?' contains forbidden '?' wildcard");
    }

    /**
     * Tests that a glob evaluation that encounters an I/O error throws instead of constructing a
     * package.
     */
    @Test
    public void testGlobWithIOErrors() throws Exception {
        events.setFailFast(false);
        scratch.dir("/pkg");
        scratch.dir("/pkg/globs");
        Path unreadableSubdir = scratch.resolve("/pkg/globs/unreadable_subdir");
        unreadableSubdir.createDirectory();
        unreadableSubdir.setReadable(false);
        Path file = scratch.file("/pkg/BUILD", "cc_library(name = 'c', srcs = glob(['globs/**']))");
        MoreAsserts.assertThrows(NoSuchPackageException.class, () -> packages.eval("pkg", RootedPath.toRootedPath(root, file)));
        events.assertContainsError("Directory is not readable");
    }

    @Test
    public void testNativeModuleIsAvailable() throws Exception {
        Path buildFile = scratch.file("/pkg/BUILD", "native.cc_library(name='bar')");
        Package pkg = packages.createPackage("pkg", RootedPath.toRootedPath(root, buildFile), "--incompatible_disallow_native_in_build_file=false");
        assertThat(containsErrors()).isFalse();
    }

    @Test
    public void testNativeModuleIsDisabled() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/pkg/BUILD", "native.cc_library(name='bar')");
        Package pkg = packages.createPackage("pkg", RootedPath.toRootedPath(root, buildFile), "--incompatible_disallow_native_in_build_file=true");
        assertThat(containsErrors()).isTrue();
    }

    @Test
    public void testPackageGroupSpecMinimal() throws Exception {
        expectEvalSuccess("package_group(name='skin', packages=[])");
    }

    @Test
    public void testPackageGroupSpecSimple() throws Exception {
        expectEvalSuccess("package_group(name='skin', packages=['//group/abelian'])");
    }

    @Test
    public void testPackageGroupSpecEmpty() throws Exception {
        expectEvalSuccess("package_group(name='seed')");
    }

    @Test
    public void testPackageGroupSpecIncludes() throws Exception {
        expectEvalSuccess("package_group(name='wine',", "              includes=['//wine:cabernet_sauvignon',", "                        '//wine:pinot_noir'])");
    }

    @Test
    public void testPackageGroupSpecBad() throws Exception {
        expectEvalError("invalid package name", "package_group(name='skin', packages=['--25:17--'])");
    }

    @Test
    public void testPackageGroupsWithSameName() throws Exception {
        expectEvalError("conflicts with existing package group", "package_group(name='skin', packages=[])", "package_group(name='skin', packages=[])");
    }

    @Test
    public void testPackageGroupNamedArguments() throws Exception {
        expectEvalError("does not accept positional arguments", "package_group('skin')");
    }

    @Test
    public void testPackageSpecMinimal() throws Exception {
        Package pkg = expectEvalSuccess("package(default_visibility=[])");
        assertThat(getDefaultVisibility()).isNotNull();
    }

    @Test
    public void testPackageSpecSimple() throws Exception {
        expectEvalSuccess("package(default_visibility=['//group:lie'])");
    }

    @Test
    public void testPackageSpecBad() throws Exception {
        expectEvalError("invalid target name", "package(default_visibility=[':::'])");
    }

    @Test
    public void testDoublePackageSpecification() throws Exception {
        expectEvalError("can only be used once", "package(default_visibility=[])", "package(default_visibility=[])");
    }

    @Test
    public void testEmptyPackageSpecification() throws Exception {
        expectEvalError("at least one argument must be given to the 'package' function", "package()");
    }

    @Test
    public void testDefaultTestonly() throws Exception {
        Package pkg = expectEvalSuccess("package(default_testonly = 1)");
        assertThat(getDefaultTestOnly()).isTrue();
    }

    @Test
    public void testDefaultDeprecation() throws Exception {
        String testMessage = "OMG PONIES!";
        Package pkg = expectEvalSuccess((("package(default_deprecation = \"" + testMessage) + "\")"));
        assertThat(getDefaultDeprecation()).isEqualTo(testMessage);
    }

    @Test
    public void testExportsBuildFile() throws Exception {
        Package pkg = expectEvalSuccess("exports_files(['BUILD'], visibility=['//visibility:private'])");
        assertThat(pkg.getTarget("BUILD")).isEqualTo(getBuildFile());
    }

    @Test
    public void testDefaultDeprecationPropagation() throws Exception {
        String msg = "I am completely operational, and all my circuits are functioning perfectly.";
        Path file = scratch.file("/foo/BUILD", (("package(default_deprecation = \"" + msg) + "\")"), "sh_library(name = 'bar', srcs=['b'])");
        Package pkg = packages.eval("foo", RootedPath.toRootedPath(root, file));
        Rule fooRule = ((Rule) (pkg.getTarget("bar")));
        String deprAttr = PackageFactoryTestBase.attributes(fooRule).get("deprecation", com.google.devtools.build.lib.syntax.Type.STRING);
        assertThat(deprAttr).isEqualTo(msg);
    }

    @Test
    public void testDefaultTestonlyPropagation() throws Exception {
        Path file = scratch.file("/foo/BUILD", "package(default_testonly = 1)", "sh_library(name = 'foo', srcs=['b'])", "sh_library(name = 'bar', srcs=['b'], testonly = 0)");
        Package pkg = packages.eval("foo", RootedPath.toRootedPath(root, file));
        Rule fooRule = ((Rule) (pkg.getTarget("foo")));
        assertThat(PackageFactoryTestBase.attributes(fooRule).get("testonly", BOOLEAN)).isTrue();
        Rule barRule = ((Rule) (pkg.getTarget("bar")));
        assertThat(PackageFactoryTestBase.attributes(barRule).get("testonly", BOOLEAN)).isFalse();
    }

    @Test
    public void testDefaultDeprecationOverriding() throws Exception {
        String msg = "I am completely operational, and all my circuits are functioning perfectly.";
        String deceive = "OMG PONIES!";
        Path file = scratch.file("/foo/BUILD", (("package(default_deprecation = \"" + deceive) + "\")"), (("sh_library(name = \'bar\', srcs=[\'b\'], deprecation = \"" + msg) + "\")"));
        Package pkg = packages.eval("foo", RootedPath.toRootedPath(root, file));
        Rule fooRule = ((Rule) (pkg.getTarget("bar")));
        String deprAttr = PackageFactoryTestBase.attributes(fooRule).get("deprecation", com.google.devtools.build.lib.syntax.Type.STRING);
        assertThat(deprAttr).isEqualTo(msg);
    }

    @Test
    public void testPackageFeatures() throws Exception {
        Path file = scratch.file("/a/BUILD", "sh_library(name='before')", "package(features=['b', 'c'])", "sh_library(name='after')");
        Package pkg = packages.eval("a", RootedPath.toRootedPath(root, file));
        assertThat(getFeatures()).containsExactly("b", "c");
    }

    @Test
    public void testTransientErrorsInGlobbing() throws Exception {
        events.setFailFast(false);
        Path buildFile = scratch.file("/e/BUILD", "sh_library(name = 'e', data = glob(['*.txt']))");
        Path parentDir = buildFile.getParentDirectory();
        scratch.file("/e/data.txt");
        throwOnReaddir = parentDir;
        MoreAsserts.assertThrows(NoSuchPackageException.class, () -> packages.createPackage("e", RootedPath.toRootedPath(root, buildFile)));
        events.setFailFast(true);
        throwOnReaddir = null;
        Package pkg = packages.createPackage("e", RootedPath.toRootedPath(root, buildFile));
        assertThat(containsErrors()).isFalse();
        assertThat(getRule("e")).isNotNull();
        List globList = ((List) (getRule("e").getAttributeContainer().getAttr("data")));
        assertThat(globList).containsExactly(Label.parseAbsolute("//e:data.txt", ImmutableMap.of()));
    }

    @Test
    public void testExportTwicePublicOK() throws Exception {
        // In theory, this could be an error, but too many existing files rely on it
        // and it is okay.
        expectEvalSuccess("exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:public\" ])", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:public\" ])");
    }

    @Test
    public void testExportTwicePublicOK2() throws Exception {
        expectEvalSuccess("exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:private\" ])", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:private\" ])");
    }

    @Test
    public void testExportTwiceFail() throws Exception {
        expectEvalError("visibility for exported file 'a.cc' declared twice", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:private\" ])", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:public\" ])");
    }

    @Test
    public void testExportTwiceFail2() throws Exception {
        expectEvalError("visibility for exported file 'a.cc' declared twice", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:public\" ])", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:private\" ])");
    }

    @Test
    public void testExportLicenseTwice() throws Exception {
        expectEvalError("licenses for exported file 'a.cc' declared twice", "exports_files([\"a.cc\"], licenses = [\"notice\"])", "exports_files([\"a.cc\"], licenses = [\"notice\"])");
    }

    @Test
    public void testExportGenruleConflict() throws Exception {
        expectEvalError("generated file 'a.cc' in rule 'foo' conflicts with existing source file", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:public\" ])", "genrule(name = 'foo',", "    outs = ['a.cc'],", "    cmd = '')");
    }

    @Test
    public void testGenruleExportConflict() throws Exception {
        expectEvalError("generated label '//pkg:a.cc' conflicts with existing generated file", "genrule(name = 'foo',", "    outs = ['a.cc'],", "    cmd = '')", "exports_files([\"a.cc\"],", "    visibility = [ \"//visibility:public\" ])");
    }

    @Test
    public void testValidEnvironmentGroup() throws Exception {
        expectEvalSuccess("environment(name = 'foo')", "environment_group(name='group', environments = [':foo'], defaults = [':foo'])");
    }

    @Test
    public void testIncompleteEnvironmentGroup() throws Exception {
        expectEvalError(("missing mandatory named-only argument 'defaults' while calling " + "environment_group(*, name: string, "), "environment(name = 'foo')", "environment_group(name='group', environments = [':foo'])");
    }

    @Test
    public void testEnvironmentGroupMissingTarget() throws Exception {
        expectEvalError("environment //pkg:foo does not exist", "environment_group(name='group', environments = [':foo'], defaults = [':foo'])");
    }

    @Test
    public void testEnvironmentGroupWrongTargetType() throws Exception {
        expectEvalError("//pkg:foo is not a valid environment", "cc_library(name = 'foo')", "environment_group(name='group', environments = [':foo'], defaults = [':foo'])");
    }

    @Test
    public void testEnvironmentGroupWrongPackage() throws Exception {
        expectEvalError("//foo:foo is not in the same package as group //pkg:group", "environment_group(name='group', environments = ['//foo'], defaults = ['//foo'])");
    }

    @Test
    public void testEnvironmentGroupInvalidDefault() throws Exception {
        expectEvalError("default //pkg:bar is not a declared environment for group //pkg:group", "environment(name = 'foo')", "environment(name = 'bar')", "environment_group(name='group', environments = [':foo'], defaults = [':bar'])");
    }

    @Test
    public void testEnvironmentGroupDuplicateEnvironments() throws Exception {
        expectEvalError("label '//pkg:foo' is duplicated in the 'environments' list of 'group'", "environment(name = 'foo')", "environment_group(name='group', environments = [':foo', ':foo'], defaults = [':foo'])");
    }

    @Test
    public void testEnvironmentGroupDuplicateDefaults() throws Exception {
        expectEvalError("label '//pkg:foo' is duplicated in the 'defaults' list of 'group'", "environment(name = 'foo')", "environment_group(name='group', environments = [':foo'], defaults = [':foo', ':foo'])");
    }

    @Test
    public void testMultipleEnvironmentGroupsValidMembership() throws Exception {
        expectEvalSuccess("environment(name = 'foo')", "environment(name = 'bar')", "environment_group(name='foo_group', environments = [':foo'], defaults = [':foo'])", "environment_group(name='bar_group', environments = [':bar'], defaults = [':bar'])");
    }

    @Test
    public void testMultipleEnvironmentGroupsConflictingMembership() throws Exception {
        expectEvalError("environment //pkg:foo belongs to both //pkg:bar_group and //pkg:foo_group", "environment(name = 'foo')", "environment(name = 'bar')", "environment_group(name='foo_group', environments = [':foo'], defaults = [':foo'])", "environment_group(name='bar_group', environments = [':foo'], defaults = [':foo'])");
    }

    @Test
    public void testFulfillsReferencesWrongTargetType() throws Exception {
        expectEvalError("in \"fulfills\" attribute of //pkg:foo: //pkg:bar is not a valid environment", "environment(name = 'foo', fulfills = [':bar'])", "cc_library(name = 'bar')", "environment_group(name='foo_group', environments = [':foo'], defaults = [])");
    }

    @Test
    public void testFulfillsNotInEnvironmentGroup() throws Exception {
        expectEvalError("in \"fulfills\" attribute of //pkg:foo: //pkg:bar is not a member of this group", "environment(name = 'foo', fulfills = [':bar'])", "environment(name = 'bar')", "environment_group(name='foo_group', environments = [':foo'], defaults = [])");
    }

    @Test
    public void testPackageDefaultEnvironments() throws Exception {
        Package pkg = expectEvalSuccess("package(", "    default_compatible_with=['//foo'],", "    default_restricted_to=['//bar'],", ")");
        assertThat(getDefaultCompatibleWith()).containsExactly(Label.parseAbsolute("//foo", ImmutableMap.of()));
        assertThat(getDefaultRestrictedTo()).containsExactly(Label.parseAbsolute("//bar", ImmutableMap.of()));
    }

    @Test
    public void testPackageDefaultCompatibilityDuplicates() throws Exception {
        expectEvalError("'//foo:foo' is duplicated in the 'default_compatible_with' list", "package(default_compatible_with=['//foo', '//bar', '//foo'])");
    }

    @Test
    public void testPackageDefaultRestrictionDuplicates() throws Exception {
        expectEvalError("'//foo:foo' is duplicated in the 'default_restricted_to' list", "package(default_restricted_to=['//foo', '//bar', '//foo'])");
    }

    @Test
    public void testGlobPatternExtractor() {
        GlobPatternExtractor globPatternExtractor = new GlobPatternExtractor();
        globPatternExtractor.visit(BuildFileAST.parseString(( event) -> {
            throw new IllegalArgumentException(event.getMessage());
        }, "pattern = '*'", "some_variable = glob([", "  '**/*',", "  'a' + 'b',", "  pattern,", "])", "other_variable = glob(include = ['a'], exclude = ['b'])", "third_variable = glob(['c'], exclude_directories = 0)"));
        assertThat(globPatternExtractor.getExcludeDirectoriesPatterns()).containsExactly("ab", "a", "**/*");
        assertThat(globPatternExtractor.getIncludeDirectoriesPatterns()).containsExactly("c");
    }
}

