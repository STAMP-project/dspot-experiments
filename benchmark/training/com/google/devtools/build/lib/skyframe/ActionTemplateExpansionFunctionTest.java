/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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


import ActionLookupValue.ActionLookupKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.SpecialArtifact;
import com.google.devtools.build.lib.actions.Artifact.TreeFileArtifact;
import com.google.devtools.build.lib.actions.ArtifactOwner;
import com.google.devtools.build.lib.actions.ArtifactPrefixConflictException;
import com.google.devtools.build.lib.actions.MutableActionGraph.ActionConflictException;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.actions.util.InjectedActionLookupKey;
import com.google.devtools.build.lib.analysis.actions.CustomCommandLine;
import com.google.devtools.build.lib.analysis.actions.SpawnActionTemplate;
import com.google.devtools.build.lib.analysis.actions.SpawnActionTemplate.OutputPathMapper;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.skyframe.SequencedRecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import com.google.devtools.build.skyframe.SkyFunction;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ActionTemplateExpansionFunction}.
 */
@RunWith(JUnit4.class)
public final class ActionTemplateExpansionFunctionTest extends FoundationTestCase {
    private Map<Artifact, TreeArtifactValue> artifactValueMap;

    private SequentialBuildDriver driver;

    private SequencedRecordingDifferencer differencer;

    @Test
    public void testActionTemplateExpansionFunction() throws Exception {
        SpecialArtifact inputTreeArtifact = createAndPopulateTreeArtifact("inputTreeArtifact", "child0", "child1", "child2");
        SpecialArtifact outputTreeArtifact = createTreeArtifact("outputTreeArtifact");
        SpawnActionTemplate spawnActionTemplate = ActionsTestUtil.createDummySpawnActionTemplate(inputTreeArtifact, outputTreeArtifact);
        List<Action> actions = evaluate(spawnActionTemplate);
        assertThat(actions).hasSize(3);
        ArtifactOwner owner = ActionTemplateExpansionValue.key(ActionTemplateExpansionFunctionTest.CTKEY, 0);
        int i = 0;
        for (Action action : actions) {
            String childName = "child" + i;
            assertThat(Artifact.toExecPaths(action.getInputs())).contains(("out/inputTreeArtifact/" + childName));
            assertThat(Artifact.toExecPaths(action.getOutputs())).containsExactly(("out/outputTreeArtifact/" + childName));
            assertThat(Iterables.getOnlyElement(action.getOutputs()).getArtifactOwner()).isEqualTo(owner);
            ++i;
        }
    }

    @Test
    public void testThrowsOnActionConflict() throws Exception {
        SpecialArtifact inputTreeArtifact = createAndPopulateTreeArtifact("inputTreeArtifact", "child0", "child1", "child2");
        SpecialArtifact outputTreeArtifact = createTreeArtifact("outputTreeArtifact");
        OutputPathMapper mapper = new OutputPathMapper() {
            @Override
            public PathFragment parentRelativeOutputPath(TreeFileArtifact inputTreeFileArtifact) {
                return PathFragment.create("conflict_path");
            }
        };
        SpawnActionTemplate spawnActionTemplate = new SpawnActionTemplate.Builder(inputTreeArtifact, outputTreeArtifact).setExecutable(PathFragment.create("/bin/cp")).setCommandLineTemplate(CustomCommandLine.builder().build()).setOutputPathMapper(mapper).build(ActionsTestUtil.NULL_ACTION_OWNER);
        try {
            evaluate(spawnActionTemplate);
            Assert.fail("Expected ActionConflictException");
        } catch (ActionConflictException e) {
            // Expected ActionConflictException
        }
    }

    @Test
    public void testThrowsOnArtifactPrefixConflict() throws Exception {
        SpecialArtifact inputTreeArtifact = createAndPopulateTreeArtifact("inputTreeArtifact", "child0", "child1", "child2");
        SpecialArtifact outputTreeArtifact = createTreeArtifact("outputTreeArtifact");
        OutputPathMapper mapper = new OutputPathMapper() {
            private int i = 0;

            @Override
            public PathFragment parentRelativeOutputPath(TreeFileArtifact inputTreeFileArtifact) {
                PathFragment path;
                switch (i) {
                    case 0 :
                        path = PathFragment.create("path_prefix");
                        break;
                    case 1 :
                        path = PathFragment.create("path_prefix/conflict");
                        break;
                    default :
                        path = inputTreeFileArtifact.getParentRelativePath();
                }
                ++(i);
                return path;
            }
        };
        SpawnActionTemplate spawnActionTemplate = new SpawnActionTemplate.Builder(inputTreeArtifact, outputTreeArtifact).setExecutable(PathFragment.create("/bin/cp")).setCommandLineTemplate(CustomCommandLine.builder().build()).setOutputPathMapper(mapper).build(ActionsTestUtil.NULL_ACTION_OWNER);
        try {
            evaluate(spawnActionTemplate);
            Assert.fail("Expected ArtifactPrefixConflictException");
        } catch (ArtifactPrefixConflictException e) {
            // Expected ArtifactPrefixConflictException
        }
    }

    private static final ActionLookupKey CTKEY = new InjectedActionLookupKey("key");

    /**
     * Dummy ArtifactFunction that just returns injected values
     */
    private static class DummyArtifactFunction implements SkyFunction {
        private final Map<Artifact, TreeArtifactValue> artifactValueMap;

        DummyArtifactFunction(Map<Artifact, TreeArtifactValue> artifactValueMap) {
            this.artifactValueMap = artifactValueMap;
        }

        @Override
        public SkyValue compute(SkyKey skyKey, Environment env) {
            return Preconditions.checkNotNull(artifactValueMap.get(skyKey));
        }

        @Override
        public String extractTag(SkyKey skyKey) {
            return null;
        }
    }
}

