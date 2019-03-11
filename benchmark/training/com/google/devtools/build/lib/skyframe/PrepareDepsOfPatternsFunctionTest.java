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
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.packages.NoSuchTargetException;
import com.google.devtools.build.skyframe.WalkableGraph;
import com.google.devtools.build.skyframe.WalkableGraphUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link com.google.devtools.build.lib.skyframe.PrepareDepsOfPatternsFunction}.
 */
@RunWith(JUnit4.class)
public class PrepareDepsOfPatternsFunctionTest extends BuildViewTestCase {
    @Test
    public void testFunctionLoadsTargetAndNotUnspecifiedTargets() throws Exception {
        // Given a package "//foo" with independent target rules ":foo" and ":foo2",
        /* dependent= */
        createFooAndFoo2(false);
        // Given a target pattern sequence consisting of a single-target pattern for "//foo",
        ImmutableList<String> patternSequence = ImmutableList.of("//foo");
        // When PrepareDepsOfPatternsFunction successfully completes evaluation,
        WalkableGraph walkableGraph = getGraphFromPatternsEvaluation(patternSequence);
        // Then the graph contains a value for the target "@//foo:foo",
        PrepareDepsOfPatternsFunctionTest.assertValidValue(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo")));
        // And the graph does not contain a value for the target "@//foo:foo2".
        assertThat(WalkableGraphUtils.exists(PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo2")), walkableGraph)).isFalse();
    }

    @Test
    public void testFunctionLoadsTargetDependencies() throws Exception {
        // Given a package "//foo" with target rules ":foo" and ":foo2",
        // And given ":foo" depends on ":foo2",
        /* dependent= */
        createFooAndFoo2(true);
        // Given a target pattern sequence consisting of a single-target pattern for "//foo",
        ImmutableList<String> patternSequence = ImmutableList.of("//foo");
        // When PrepareDepsOfPatternsFunction successfully completes evaluation,
        WalkableGraph walkableGraph = getGraphFromPatternsEvaluation(patternSequence);
        // Then the graph contains an entry for ":foo"'s dependency, ":foo2".
        PrepareDepsOfPatternsFunctionTest.assertValidValue(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo2")));
    }

    @Test
    public void testFunctionExpandsTargetPatterns() throws Exception {
        // Given a package "@//foo" with independent target rules ":foo" and ":foo2",
        /* dependent= */
        createFooAndFoo2(false);
        // Given a target pattern sequence consisting of a pattern for "//foo:*",
        ImmutableList<String> patternSequence = ImmutableList.of("//foo:*");
        // When PrepareDepsOfPatternsFunction successfully completes evaluation,
        WalkableGraph walkableGraph = getGraphFromPatternsEvaluation(patternSequence);
        // Then the graph contains an entry for ":foo" and ":foo2".
        PrepareDepsOfPatternsFunctionTest.assertValidValue(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo")));
        PrepareDepsOfPatternsFunctionTest.assertValidValue(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo2")));
    }

    @Test
    public void testTargetParsingException() throws Exception {
        // Given no packages, and a target pattern sequence referring to a non-existent target,
        String nonexistentTarget = "//foo:foo";
        ImmutableList<String> patternSequence = ImmutableList.of(nonexistentTarget);
        // When PrepareDepsOfPatternsFunction completes evaluation,
        WalkableGraph walkableGraph = getGraphFromPatternsEvaluation(patternSequence);
        // Then the graph does not contain an entry for ":foo",
        assertThat(WalkableGraphUtils.exists(PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo")), walkableGraph)).isFalse();
    }

    @Test
    public void testDependencyTraversalNoSuchPackageException() throws Exception {
        // Given a package "//foo" with a target ":foo" that has a dependency on a non-existent target
        // "//bar:bar" in a non-existent package "//bar",
        createFooWithDependencyOnMissingBarPackage();
        // Given a target pattern sequence consisting of a single-target pattern for "//foo",
        ImmutableList<String> patternSequence = ImmutableList.of("//foo");
        // When PrepareDepsOfPatternsFunction completes evaluation,
        WalkableGraph walkableGraph = getGraphFromPatternsEvaluation(patternSequence);
        // Then the graph contains an entry for ":foo",
        /* expectTransitiveException= */
        PrepareDepsOfPatternsFunctionTest.assertValidValue(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo")), true);
        // And an entry with a NoSuchPackageException for "//bar:bar",
        Exception e = PrepareDepsOfPatternsFunctionTest.assertException(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//bar", "bar")));
        assertThat(e).isInstanceOf(NoSuchPackageException.class);
    }

    @Test
    public void testDependencyTraversalNoSuchTargetException() throws Exception {
        // Given a package "//foo" with a target ":foo" that has a dependency on a non-existent target
        // "//bar:bar" in an existing package "//bar",
        createFooWithDependencyOnBarPackageWithMissingTarget();
        // Given a target pattern sequence consisting of a single-target pattern for "//foo",
        ImmutableList<String> patternSequence = ImmutableList.of("//foo");
        // When PrepareDepsOfPatternsFunction completes evaluation,
        WalkableGraph walkableGraph = getGraphFromPatternsEvaluation(patternSequence);
        // Then the graph contains an entry for ":foo" which has both a value and an exception,
        /* expectTransitiveException= */
        PrepareDepsOfPatternsFunctionTest.assertValidValue(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//foo", "foo")), true);
        // And an entry with a NoSuchTargetException for "//bar:bar",
        Exception e = PrepareDepsOfPatternsFunctionTest.assertException(walkableGraph, PrepareDepsOfPatternsFunctionTest.getKeyForLabel(Label.create("@//bar", "bar")));
        assertThat(e).isInstanceOf(NoSuchTargetException.class);
    }

    @Test
    public void testParsingProblemsKeepGoing() throws Exception {
        /* keepGoing= */
        parsingProblem(true);
    }

    /**
     * PrepareDepsOfPatternsFunction always keeps going despite any target pattern parsing errors, in
     * keeping with the original behavior of {@link WalkableGraph.WalkableGraphFactory#prepareAndGet},
     * which always used {@code keepGoing=true} during target pattern parsing because it was
     * responsible for ensuring that queries had a complete graph to work on.
     */
    @Test
    public void testParsingProblemsNoKeepGoing() throws Exception {
        /* keepGoing= */
        parsingProblem(false);
    }
}

