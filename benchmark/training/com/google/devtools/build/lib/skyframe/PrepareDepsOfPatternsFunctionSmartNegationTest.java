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


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.skyframe.WalkableGraph;
import com.google.devtools.build.skyframe.WalkableGraphUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PrepareDepsOfPatternsFunction}.
 */
@RunWith(JUnit4.class)
public class PrepareDepsOfPatternsFunctionSmartNegationTest extends FoundationTestCase {
    private SkyframeExecutor skyframeExecutor;

    private static final String ADDITIONAL_BLACKLISTED_PACKAGE_PREFIXES_FILE_PATH_STRING = "config/blacklist.txt";

    @Test
    public void testRecursiveEvaluationFailsOnBadBuildFile() throws Exception {
        // Given a well-formed package "@//foo" and a malformed package "@//foo/foo",
        createFooAndFooFoo();
        // Given a target pattern sequence consisting of a recursive pattern for "//foo/...",
        ImmutableList<String> patternSequence = ImmutableList.of("//foo/...");
        // When PrepareDepsOfPatternsFunction completes evaluation (with no error because it was
        // recovered from),
        WalkableGraph walkableGraph = /* successExpected= */
        /* keepGoing= */
        getGraphFromPatternsEvaluation(patternSequence, true, true);
        // Then the graph contains package values for "@//foo" and "@//foo/foo",
        assertThat(WalkableGraphUtils.exists(PackageValue.key(PackageIdentifier.parse("@//foo")), walkableGraph)).isTrue();
        assertThat(WalkableGraphUtils.exists(PackageValue.key(PackageIdentifier.parse("@//foo/foo")), walkableGraph)).isTrue();
        // But the graph does not contain a value for the target "@//foo/foo:foofoo".
        assertThat(WalkableGraphUtils.exists(PrepareDepsOfPatternsFunctionSmartNegationTest.getKeyForLabel(Label.create("@//foo/foo", "foofoo")), walkableGraph)).isFalse();
    }

    @Test
    public void testNegativePatternBlocksPatternEvaluation() throws Exception {
        // Given a well-formed package "//foo" and a malformed package "//foo/foo",
        createFooAndFooFoo();
        // Given a target pattern sequence consisting of a recursive pattern for "//foo/..." followed
        // by a negative pattern for the malformed package,
        ImmutableList<String> patternSequence = ImmutableList.of("//foo/...", "-//foo/foo/...");
        assertSkipsFoo(patternSequence);
    }

    @Test
    public void testBlacklistPatternBlocksPatternEvaluation() throws Exception {
        // Given a well-formed package "//foo" and a malformed package "//foo/foo",
        createFooAndFooFoo();
        // Given a target pattern sequence consisting of a recursive pattern for "//foo/...",
        ImmutableList<String> patternSequence = ImmutableList.of("//foo/...");
        // and a blacklist for the malformed package,
        scratch.overwriteFile(PrepareDepsOfPatternsFunctionSmartNegationTest.ADDITIONAL_BLACKLISTED_PACKAGE_PREFIXES_FILE_PATH_STRING, "foo/foo");
        assertSkipsFoo(patternSequence);
    }

    @Test
    public void testNegativeNonTBDPatternsAreSkippedWithWarnings() throws Exception {
        // Given a target pattern sequence with a negative non-TBD pattern,
        ImmutableList<String> patternSequence = ImmutableList.of("-//foo/bar");
        // When PrepareDepsOfPatternsFunction completes evaluation,
        /* successExpected= */
        /* keepGoing= */
        getGraphFromPatternsEvaluation(patternSequence, true, true);
        // Then a event is published that says that negative non-TBD patterns are skipped.
        assertContainsEvent(("Skipping '-//foo/bar, excludedSubdirs=[], filteringPolicy=[]': Negative target patterns of" + " types other than \"targets below directory\" are not permitted."));
    }
}

