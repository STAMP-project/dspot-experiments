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


import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.packages.util.PackageLoadingTestCase;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.RootedPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class OutputFileTest extends PackageLoadingTestCase {
    private Package pkg;

    private Rule rule;

    @Test
    public void testGetAssociatedRule() throws Exception {
        assertThat(pkg.getTarget("x").getAssociatedRule()).isSameAs(rule);
    }

    @Test
    public void testOutputFileInPackageDir() throws Exception {
        OutputFile outputFileX = ((OutputFile) (pkg.getTarget("x")));
        checkTargetRetainsGeneratingRule(outputFileX);
        checkName(outputFileX, "x");
        checkLabel(outputFileX, "//pkg:x");
        assertThat(outputFileX.getTargetKind()).isEqualTo("generated file");
    }

    @Test
    public void testOutputFileInSubdirectory() throws Exception {
        OutputFile outputFileY = ((OutputFile) (pkg.getTarget("subdir/y")));
        checkTargetRetainsGeneratingRule(outputFileY);
        checkName(outputFileY, "subdir/y");
        checkLabel(outputFileY, "//pkg:subdir/y");
    }

    @Test
    public void testEquivalenceRelation() throws Exception {
        OutputFile outputFileX1 = ((OutputFile) (pkg.getTarget("x")));
        OutputFile outputFileX2 = ((OutputFile) (pkg.getTarget("x")));
        OutputFile outputFileY1 = ((OutputFile) (pkg.getTarget("subdir/y")));
        OutputFile outputFileY2 = ((OutputFile) (pkg.getTarget("subdir/y")));
        assertThat(outputFileX2).isSameAs(outputFileX1);
        assertThat(outputFileY2).isSameAs(outputFileY1);
        new EqualsTester().addEqualityGroup(outputFileX1, outputFileX2).addEqualityGroup(outputFileY1, outputFileY2).testEquals();
    }

    @Test
    public void testDuplicateOutputFilesInDifferentRules() throws Exception {
        Path buildfile = scratch.file("two_outs/BUILD", "genrule(name='a',", "        cmd='ls >$(location out)',", "        outs=['out'])", "", "genrule(name='b',", "        cmd='ls >$(location out)',", "        outs=['out'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        packageFactory.createPackageForTesting(PackageIdentifier.createInMainRepo("two_outs"), RootedPath.toRootedPath(root, buildfile), getPackageManager(), reporter);
        assertContainsEvent(("generated file 'out' in rule 'b' conflicts with " + "existing generated file from rule 'a'"));
    }

    @Test
    public void testOutputFileNameConflictsWithExistingRule() throws Exception {
        Path buildfile = scratch.file("out_is_rule/BUILD", "genrule(name='a',", "        cmd='ls >$(location out)',", "        outs=['out'])", "", "genrule(name='b',", "        cmd='ls >$(location out)',", "        outs=['a'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        packageFactory.createPackageForTesting(PackageIdentifier.createInMainRepo("out_is_rule"), RootedPath.toRootedPath(root, buildfile), getPackageManager(), reporter);
        assertContainsEvent("generated file 'a' in rule 'b' conflicts with existing genrule rule");
    }

    @Test
    public void testDuplicateOutputFilesInSameRule() throws Exception {
        Path buildfile = scratch.file("two_outs/BUILD", "genrule(name='a',", "        cmd='ls >$(location out)',", "        outs=['out', 'out'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        packageFactory.createPackageForTesting(PackageIdentifier.createInMainRepo("two_outs"), RootedPath.toRootedPath(root, buildfile), getPackageManager(), reporter);
        assertContainsEvent(("generated file 'out' in rule 'a' conflicts with " + "existing generated file from rule 'a'"));
    }

    @Test
    public void testOutputFileWithIllegalName() throws Exception {
        Path buildfile = scratch.file("bad_out_name/BUILD", "genrule(name='a',", "        cmd='ls',", "        outs=['!@#:'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        packageFactory.createPackageForTesting(PackageIdentifier.createInMainRepo("bad_out_name"), RootedPath.toRootedPath(root, buildfile), getPackageManager(), reporter);
        assertContainsEvent("illegal output file name '!@#:' in rule //bad_out_name:a");
    }

    @Test
    public void testOutputFileWithCrossPackageLabel() throws Exception {
        Path buildfile = scratch.file("cross_package_out/BUILD", "genrule(name='a',", "        cmd='ls',", "        outs=['//foo:bar'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        packageFactory.createPackageForTesting(PackageIdentifier.createInMainRepo("cross_package_out"), RootedPath.toRootedPath(root, buildfile), getPackageManager(), reporter);
        assertContainsEvent("label '//foo:bar' is not in the current package");
    }

    @Test
    public void testOutputFileNamedBUILD() throws Exception {
        Path buildfile = scratch.file("output_called_build/BUILD", "genrule(name='a',", "        cmd='ls',", "        outs=['BUILD'])");
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        packageFactory.createPackageForTesting(PackageIdentifier.createInMainRepo("output_called_build"), RootedPath.toRootedPath(root, buildfile), getPackageManager(), reporter);
        assertContainsEvent("generated file 'BUILD' in rule 'a' conflicts with existing source file");
    }
}

