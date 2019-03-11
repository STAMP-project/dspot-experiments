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
package com.google.devtools.build.lib.cmdline;


import ContainsTBDForTBDResult.DIRECTORY_EXCLUSION_WOULD_BE_EXACT;
import ContainsTBDForTBDResult.DIRECTORY_EXCLUSION_WOULD_BE_TOO_BROAD;
import ContainsTBDForTBDResult.OTHER;
import TargetPattern.Type.PATH_AS_TARGET;
import TargetPattern.Type.SINGLE_TARGET;
import TargetPattern.Type.TARGETS_BELOW_DIRECTORY;
import TargetPattern.Type.TARGETS_IN_PACKAGE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link com.google.devtools.build.lib.cmdline.TargetPattern}.
 */
@RunWith(JUnit4.class)
public class TargetPatternTest {
    @Test
    public void testPassingValidations() throws TargetParsingException {
        TargetPatternTest.parse("foo:bar");
        TargetPatternTest.parse("foo:all");
        TargetPatternTest.parse("foo/...:all");
        TargetPatternTest.parse("foo:*");
        TargetPatternTest.parse("//foo");
        TargetPatternTest.parse("//foo:bar");
        TargetPatternTest.parse("//foo:all");
        TargetPatternTest.parse("//foo/all");
        TargetPatternTest.parse("java/com/google/foo/Bar.java");
        TargetPatternTest.parse("//foo/...:all");
        TargetPatternTest.parse("//...");
        TargetPatternTest.parse("@repo//foo:bar");
        TargetPatternTest.parse("@repo//foo:all");
        TargetPatternTest.parse("@repo//:bar");
    }

    @Test
    public void testInvalidPatterns() throws TargetParsingException {
        expectError("Bar\\java");
        expectError("");
        expectError("\\");
    }

    @Test
    public void testNormalize() {
        // Good cases.
        assertThat(TargetPattern.normalize("empty")).isEqualTo("empty");
        assertThat(TargetPattern.normalize("a/b")).isEqualTo("a/b");
        assertThat(TargetPattern.normalize("a/b/c")).isEqualTo("a/b/c");
        assertThat(TargetPattern.normalize("a/b/c.d")).isEqualTo("a/b/c.d");
        assertThat(TargetPattern.normalize("a/b/c..")).isEqualTo("a/b/c..");
        assertThat(TargetPattern.normalize("a/b/c...")).isEqualTo("a/b/c...");
        assertThat(TargetPattern.normalize("a/b/")).isEqualTo("a/b");// Remove trailing empty segments

        assertThat(TargetPattern.normalize("a//c")).isEqualTo("a/c");// Remove empty inner segments

        assertThat(TargetPattern.normalize("a/./d")).isEqualTo("a/d");// Remove inner dot segments

        assertThat(TargetPattern.normalize("a/.")).isEqualTo("a");// Remove trailing dot segments

        // Remove .. segment and its predecessor
        assertThat(TargetPattern.normalize("a/b/../e")).isEqualTo("a/e");
        // Remove trailing .. segment and its predecessor
        assertThat(TargetPattern.normalize("a/g/b/..")).isEqualTo("a/g");
        // Remove double .. segments and two predecessors
        assertThat(TargetPattern.normalize("a/b/c/../../h")).isEqualTo("a/h");
        // Don't remove leading .. segments
        assertThat(TargetPattern.normalize("../a")).isEqualTo("../a");
        assertThat(TargetPattern.normalize("../../a")).isEqualTo("../../a");
        assertThat(TargetPattern.normalize("../../../a")).isEqualTo("../../../a");
        assertThat(TargetPattern.normalize("a/../../../b")).isEqualTo("../../b");
    }

    @Test
    public void testTargetsBelowDirectoryContainsColonStar() throws Exception {
        // Given an outer pattern '//foo/...', that matches rules only,
        TargetPattern outerPattern = TargetPatternTest.parseAsExpectedType("//foo/...", TARGETS_BELOW_DIRECTORY);
        // And a nested inner pattern '//foo/bar/...:*', that matches all targets,
        TargetPattern innerPattern = TargetPatternTest.parseAsExpectedType("//foo/bar/...:*", TARGETS_BELOW_DIRECTORY);
        // Then a directory exclusion would exactly describe the subtraction of the inner pattern from
        // the outer pattern,
        assertThat(outerPattern.containsTBDForTBD(innerPattern)).isEqualTo(DIRECTORY_EXCLUSION_WOULD_BE_EXACT);
        // And the inner pattern does not contain the outer pattern.
        assertThat(innerPattern.containsTBDForTBD(outerPattern)).isEqualTo(OTHER);
    }

    @Test
    public void testTargetsBelowDirectoryColonStarContains() throws Exception {
        // Given an outer pattern '//foo/...:*', that matches all targets,
        TargetPattern outerPattern = TargetPatternTest.parseAsExpectedType("//foo/...:*", TARGETS_BELOW_DIRECTORY);
        // And a nested inner pattern '//foo/bar/...', that matches rules only,
        TargetPattern innerPattern = TargetPatternTest.parseAsExpectedType("//foo/bar/...", TARGETS_BELOW_DIRECTORY);
        // Then a directory exclusion would be too broad,
        assertThat(outerPattern.containsTBDForTBD(innerPattern)).isEqualTo(DIRECTORY_EXCLUSION_WOULD_BE_TOO_BROAD);
        // And the inner pattern does not contain the outer pattern.
        assertThat(innerPattern.containsTBDForTBD(outerPattern)).isEqualTo(OTHER);
    }

    @Test
    public void testTargetsBelowDirectoryContainsNestedPatterns() throws Exception {
        // Given an outer pattern '//foo/...',
        TargetPattern outerPattern = TargetPatternTest.parseAsExpectedType("//foo/...", TARGETS_BELOW_DIRECTORY);
        // And a nested inner pattern '//foo/bar/...',
        TargetPattern innerPattern = TargetPatternTest.parseAsExpectedType("//foo/bar/...", TARGETS_BELOW_DIRECTORY);
        // Then the outer pattern contains the inner pattern,
        assertThat(outerPattern.containsTBDForTBD(innerPattern)).isEqualTo(DIRECTORY_EXCLUSION_WOULD_BE_EXACT);
        // And the inner pattern does not contain the outer pattern.
        assertThat(innerPattern.containsTBDForTBD(outerPattern)).isEqualTo(OTHER);
    }

    @Test
    public void testTargetsBelowDirectoryIsExcludableFromForIndependentPatterns() throws Exception {
        // Given a pattern '//foo/...',
        TargetPattern patternFoo = TargetPatternTest.parseAsExpectedType("//foo/...", TARGETS_BELOW_DIRECTORY);
        // And a pattern '//bar/...',
        TargetPattern patternBar = TargetPatternTest.parseAsExpectedType("//bar/...", TARGETS_BELOW_DIRECTORY);
        // Then neither pattern contains the other.
        assertThat(patternFoo.containsTBDForTBD(patternBar)).isEqualTo(OTHER);
        assertThat(patternBar.containsTBDForTBD(patternFoo)).isEqualTo(OTHER);
    }

    @Test
    public void testTargetsBelowDirectoryContainsForOtherPatternTypes() throws Exception {
        // Given a TargetsBelowDirectory pattern, tbdFoo of '//foo/...',
        TargetPattern tbdFoo = TargetPatternTest.parseAsExpectedType("//foo/...", TARGETS_BELOW_DIRECTORY);
        // And target patterns of each type other than TargetsBelowDirectory, e.g. 'foo/bar',
        // '//foo:bar', and 'foo:all',
        TargetPattern pathAsTargetPattern = TargetPatternTest.parseAsExpectedType("foo/bar", PATH_AS_TARGET);
        TargetPattern singleTargetPattern = TargetPatternTest.parseAsExpectedType("//foo:bar", SINGLE_TARGET);
        TargetPattern targetsInPackagePattern = TargetPatternTest.parseAsExpectedType("foo:all", TARGETS_IN_PACKAGE);
        // Then the non-TargetsBelowDirectory patterns do not contain tbdFoo.
        assertThat(pathAsTargetPattern.containsTBDForTBD(tbdFoo)).isEqualTo(OTHER);
        // And are not considered to be a contained directory of the TargetsBelowDirectory pattern.
        assertThat(tbdFoo.containsTBDForTBD(pathAsTargetPattern)).isEqualTo(OTHER);
        assertThat(singleTargetPattern.containsTBDForTBD(tbdFoo)).isEqualTo(OTHER);
        assertThat(tbdFoo.containsTBDForTBD(singleTargetPattern)).isEqualTo(OTHER);
        assertThat(targetsInPackagePattern.containsTBDForTBD(tbdFoo)).isEqualTo(OTHER);
        assertThat(tbdFoo.containsTBDForTBD(targetsInPackagePattern)).isEqualTo(OTHER);
    }

    @Test
    public void testTargetsBelowDirectoryDoesNotContainCoincidentPrefixPatterns() throws Exception {
        // Given a TargetsBelowDirectory pattern, tbdFoo of '//foo/...',
        TargetPattern tbdFoo = TargetPatternTest.parseAsExpectedType("//foo/...", TARGETS_BELOW_DIRECTORY);
        // And target patterns with prefixes equal to the directory of the TBD pattern, but not below
        // it,
        TargetPattern targetsBelowDirectoryPattern = TargetPatternTest.parseAsExpectedType("//food/...", TARGETS_BELOW_DIRECTORY);
        TargetPattern pathAsTargetPattern = TargetPatternTest.parseAsExpectedType("food/bar", PATH_AS_TARGET);
        TargetPattern singleTargetPattern = TargetPatternTest.parseAsExpectedType("//food:bar", SINGLE_TARGET);
        TargetPattern targetsInPackagePattern = TargetPatternTest.parseAsExpectedType("food:all", TARGETS_IN_PACKAGE);
        // Then the non-TargetsBelowDirectory patterns are not contained by tbdFoo.
        assertThat(tbdFoo.containsTBDForTBD(targetsBelowDirectoryPattern)).isEqualTo(OTHER);
        assertThat(tbdFoo.containsTBDForTBD(pathAsTargetPattern)).isEqualTo(OTHER);
        assertThat(tbdFoo.containsTBDForTBD(singleTargetPattern)).isEqualTo(OTHER);
        assertThat(tbdFoo.containsTBDForTBD(targetsInPackagePattern)).isEqualTo(OTHER);
    }

    @Test
    public void testDepotRootTargetsBelowDirectoryContainsPatterns() throws Exception {
        // Given a TargetsBelowDirectory pattern, tbdDepot of '//...',
        TargetPattern tbdDepot = TargetPatternTest.parseAsExpectedType("//...", TARGETS_BELOW_DIRECTORY);
        // And target patterns of each type other than TargetsBelowDirectory, e.g. 'foo/bar',
        // '//foo:bar', and 'foo:all',
        TargetPattern tbdFoo = TargetPatternTest.parseAsExpectedType("//foo/...", TARGETS_BELOW_DIRECTORY);
        TargetPattern pathAsTargetPattern = TargetPatternTest.parseAsExpectedType("foo/bar", PATH_AS_TARGET);
        TargetPattern singleTargetPattern = TargetPatternTest.parseAsExpectedType("//foo:bar", SINGLE_TARGET);
        TargetPattern targetsInPackagePattern = TargetPatternTest.parseAsExpectedType("foo:all", TARGETS_IN_PACKAGE);
        // Then the patterns are contained by tbdDepot, and do not contain tbdDepot.
        assertThat(tbdDepot.containsTBDForTBD(tbdFoo)).isEqualTo(DIRECTORY_EXCLUSION_WOULD_BE_EXACT);
        assertThat(tbdFoo.containsTBDForTBD(tbdDepot)).isEqualTo(OTHER);
        assertThat(tbdDepot.containsTBDForTBD(pathAsTargetPattern)).isEqualTo(OTHER);
        assertThat(pathAsTargetPattern.containsTBDForTBD(tbdDepot)).isEqualTo(OTHER);
        assertThat(tbdDepot.containsTBDForTBD(singleTargetPattern)).isEqualTo(OTHER);
        assertThat(singleTargetPattern.containsTBDForTBD(tbdDepot)).isEqualTo(OTHER);
        assertThat(tbdDepot.containsTBDForTBD(targetsInPackagePattern)).isEqualTo(OTHER);
        assertThat(targetsInPackagePattern.containsTBDForTBD(tbdDepot)).isEqualTo(OTHER);
    }
}

