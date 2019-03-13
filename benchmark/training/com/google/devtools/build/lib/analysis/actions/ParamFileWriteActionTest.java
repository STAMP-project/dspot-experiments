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


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.ActionExecutionContext;
import com.google.devtools.build.lib.actions.ActionResult;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.SpecialArtifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.actions.CommandLine;
import com.google.devtools.build.lib.actions.ParameterFile.ParameterFileType;
import com.google.devtools.build.lib.analysis.util.ActionTester;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for ParamFileWriteAction.
 */
@RunWith(JUnit4.class)
public class ParamFileWriteActionTest extends BuildViewTestCase {
    private ArtifactRoot rootDir;

    private Artifact outputArtifact;

    private SpecialArtifact treeArtifact;

    @Test
    public void testOutputs() {
        Action action = createParameterFileWriteAction(ImmutableList.<Artifact>of(), createNormalCommandLine());
        assertThat(Artifact.toRootRelativePaths(action.getOutputs())).containsExactly("destination.txt");
    }

    @Test
    public void testInputs() {
        Action action = createParameterFileWriteAction(ImmutableList.of(treeArtifact), createTreeArtifactExpansionCommandLineDefault());
        assertThat(Artifact.toExecPaths(action.getInputs())).containsExactly("out/artifact/myTreeFileArtifact");
    }

    @Test
    public void testWriteCommandLineWithoutTreeArtifactExpansion() throws Exception {
        Action action = createParameterFileWriteAction(ImmutableList.<Artifact>of(), createNormalCommandLine());
        ActionExecutionContext context = actionExecutionContext();
        ActionResult actionResult = action.execute(context);
        assertThat(actionResult.spawnResults()).isEmpty();
        String content = new String(FileSystemUtils.readContentAsLatin1(outputArtifact.getPath()));
        assertThat(content.trim()).isEqualTo("--flag1\n--flag2\n--flag3\nvalue1\nvalue2");
    }

    @Test
    public void testWriteCommandLineWithTreeArtifactExpansionDefault() throws Exception {
        Action action = createParameterFileWriteAction(ImmutableList.of(treeArtifact), createTreeArtifactExpansionCommandLineDefault());
        ActionExecutionContext context = actionExecutionContext();
        ActionResult actionResult = action.execute(context);
        assertThat(actionResult.spawnResults()).isEmpty();
        String content = new String(FileSystemUtils.readContentAsLatin1(outputArtifact.getPath()));
        assertThat(content.trim()).isEqualTo(("--flag1\n" + ("out/artifact/myTreeFileArtifact/artifacts/treeFileArtifact1\n" + "out/artifact/myTreeFileArtifact/artifacts/treeFileArtifact2")));
    }

    @Test
    public void testWriteCommandLineWithTreeArtifactExpansionExpandedFunction() throws Exception {
        Action action = createParameterFileWriteAction(ImmutableList.of(treeArtifact), createTreeArtifactExpansionCommandLineExpandedFunction());
        ActionExecutionContext context = actionExecutionContext();
        ActionResult actionResult = action.execute(context);
        assertThat(actionResult.spawnResults()).isEmpty();
        String content = new String(FileSystemUtils.readContentAsLatin1(outputArtifact.getPath()));
        assertThat(content.trim()).isEqualTo(("--flag1=out/artifact/myTreeFileArtifact/artifacts/treeFileArtifact1\n" + "--flag1=out/artifact/myTreeFileArtifact/artifacts/treeFileArtifact2"));
    }

    private enum KeyAttributes {

        COMMANDLINE,
        FILE_TYPE,
        CHARSET;}

    @Test
    public void testComputeKey() throws Exception {
        final Artifact outputArtifact = getSourceArtifact("output");
        ActionTester.runTest(ParamFileWriteActionTest.KeyAttributes.class, ( attributesToFlip) -> {
            String arg = (attributesToFlip.contains(KeyAttributes.COMMANDLINE)) ? "foo" : "bar";
            CommandLine commandLine = CommandLine.of(ImmutableList.of(arg));
            ParameterFileType parameterFileType = (attributesToFlip.contains(KeyAttributes.FILE_TYPE)) ? ParameterFileType.SHELL_QUOTED : ParameterFileType.UNQUOTED;
            Charset charset = (attributesToFlip.contains(KeyAttributes.CHARSET)) ? StandardCharsets.UTF_8 : StandardCharsets.US_ASCII;
            return new ParameterFileWriteAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact, commandLine, parameterFileType, charset);
        }, actionKeyContext);
    }
}

