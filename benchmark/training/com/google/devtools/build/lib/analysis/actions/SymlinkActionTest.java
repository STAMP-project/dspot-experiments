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
package com.google.devtools.build.lib.analysis.actions;


import SerializationDepsUtils.SERIALIZATION_DEPS_FOR_TEST;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.ActionInputPrefetcher;
import com.google.devtools.build.lib.actions.ActionResult;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Executor;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.exec.util.TestExecutorBuilder;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link SymlinkAction}.
 */
@RunWith(JUnit4.class)
public class SymlinkActionTest extends BuildViewTestCase {
    private Path input;

    private Artifact inputArtifact;

    private Path output;

    private Artifact outputArtifact;

    private SymlinkAction action;

    @Test
    public void testInputArtifactIsInput() {
        Iterable<Artifact> inputs = action.getInputs();
        assertThat(inputs).containsExactly(inputArtifact);
    }

    @Test
    public void testDestinationArtifactIsOutput() {
        Iterable<Artifact> outputs = action.getOutputs();
        assertThat(outputs).containsExactly(outputArtifact);
    }

    @Test
    public void testSymlink() throws Exception {
        Executor executor = new TestExecutorBuilder(fileSystem, directories, null).build();
        ActionResult actionResult = action.execute(/* actionFileSystem= */
        /* skyframeDepsResult= */
        new com.google.devtools.build.lib.actions.ActionExecutionContext(executor, null, ActionInputPrefetcher.NONE, actionKeyContext, null, null, executor.getEventHandler(), ImmutableMap.<String, String>of(), ImmutableMap.of(), null, null, null));
        assertThat(actionResult.spawnResults()).isEmpty();
        assertThat(output.isSymbolicLink()).isTrue();
        assertThat(output.resolveSymbolicLinks()).isEqualTo(input);
        assertThat(action.getPrimaryInput()).isEqualTo(inputArtifact);
        assertThat(action.getPrimaryOutput()).isEqualTo(outputArtifact);
    }

    @Test
    public void testCodec() throws Exception {
        new com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester(action).addDependency(FileSystem.class, scratch.getFileSystem()).addDependencies(SERIALIZATION_DEPS_FOR_TEST).setVerificationFunction(( in, out) -> {
            SymlinkAction inAction = ((SymlinkAction) (in));
            SymlinkAction outAction = ((SymlinkAction) (out));
            assertThat(inAction.getPrimaryInput().getFilename()).isEqualTo(outAction.getPrimaryInput().getFilename());
            assertThat(inAction.getPrimaryOutput().getFilename()).isEqualTo(outAction.getPrimaryOutput().getFilename());
            assertThat(inAction.getOwner()).isEqualTo(outAction.getOwner());
            assertThat(inAction.getProgressMessage()).isEqualTo(outAction.getProgressMessage());
        }).runTests();
    }
}

