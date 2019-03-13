/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.analysis;


import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.analysis.TopLevelArtifactHelper.ArtifactsInOutputGroup;
import com.google.devtools.build.lib.analysis.TopLevelArtifactHelper.ArtifactsToBuild;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.lib.vfs.Path;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TopLevelArtifactHelper}.
 */
@RunWith(JUnit4.class)
public class TopLevelArtifactHelperTest {
    private TopLevelArtifactContext ctx;

    private OutputGroupInfo groupProvider;

    private Path path;

    private ArtifactRoot root;

    private int artifactIdx;

    @Test
    public void artifactsShouldBeSeparateByGroup() {
        setup(Arrays.asList(Pair.of("foo", 3), Pair.of("bar", 2)));
        ArtifactsToBuild allArtifacts = TopLevelArtifactHelper.getAllArtifactsToBuild(groupProvider, null, ctx);
        assertThat(allArtifacts.getAllArtifacts()).hasSize(5);
        assertThat(allArtifacts.getImportantArtifacts()).hasSize(5);
        NestedSet<ArtifactsInOutputGroup> artifactsByGroup = allArtifacts.getAllArtifactsByOutputGroup();
        // Two groups
        assertThat(artifactsByGroup).hasSize(2);
        for (ArtifactsInOutputGroup artifacts : artifactsByGroup) {
            String outputGroup = artifacts.getOutputGroup();
            if ("foo".equals(outputGroup)) {
                assertThat(artifacts.getArtifacts()).hasSize(3);
            } else
                if ("bar".equals(outputGroup)) {
                    assertThat(artifacts.getArtifacts()).hasSize(2);
                }

        }
    }

    @Test
    public void emptyGroupsShouldBeIgnored() {
        setup(Arrays.asList(Pair.of("foo", 1), Pair.of("bar", 0)));
        ArtifactsToBuild allArtifacts = TopLevelArtifactHelper.getAllArtifactsToBuild(groupProvider, null, ctx);
        assertThat(allArtifacts.getAllArtifacts()).hasSize(1);
        assertThat(allArtifacts.getImportantArtifacts()).hasSize(1);
        NestedSet<ArtifactsInOutputGroup> artifactsByGroup = allArtifacts.getAllArtifactsByOutputGroup();
        // The bar list should not appear here, as it contains no artifacts.
        assertThat(artifactsByGroup).hasSize(1);
        assertThat(artifactsByGroup.toList().get(0).getOutputGroup()).isEqualTo("foo");
    }

    @Test
    public void importantArtifacts() {
        setup(Arrays.asList(Pair.of(((OutputGroupInfo.HIDDEN_OUTPUT_GROUP_PREFIX) + "notimportant"), 1), Pair.of("important", 2)));
        ArtifactsToBuild allArtifacts = TopLevelArtifactHelper.getAllArtifactsToBuild(groupProvider, null, ctx);
        assertThat(allArtifacts.getAllArtifacts()).hasSize(3);
        assertThat(allArtifacts.getImportantArtifacts()).hasSize(2);
    }
}

