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


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.ActionKeyContext;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.actions.Executor;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.analysis.BlazeDirectories;
import com.google.devtools.build.lib.exec.BinTools;
import com.google.devtools.build.lib.exec.util.TestExecutorBuilder;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link TemplateExpansionAction}.
 */
@RunWith(JUnit4.class)
public class TemplateExpansionActionTest extends FoundationTestCase {
    private static final String TEMPLATE = Joiner.on('\n').join("key=%key%", "value=%value%");

    private static final String SPECIAL_CHARS = "????_str?get";

    private ArtifactRoot outputRoot;

    private Artifact inputArtifact;

    private Artifact outputArtifact;

    private Path output;

    private List<Substitution> substitutions;

    private BlazeDirectories directories;

    private BinTools binTools;

    private final ActionKeyContext actionKeyContext = new ActionKeyContext();

    @Test
    public void testInputsIsEmpty() {
        assertThat(create().getInputs()).isEmpty();
    }

    @Test
    public void testDestinationArtifactIsOutput() {
        assertThat(create().getOutputs()).containsExactly(outputArtifact);
    }

    @Test
    public void testExpansion() throws Exception {
        Executor executor = new TestExecutorBuilder(fileSystem, directories, binTools).build();
        create().execute(createContext(executor));
        String content = new String(FileSystemUtils.readContentAsLatin1(output));
        String expected = Joiner.on('\n').join("key=foo", "value=bar");
        assertThat(content).isEqualTo(expected);
    }

    @Test
    public void testKeySameIfSame() throws Exception {
        Artifact outputArtifact2 = new Artifact(scratch.resolve("/workspace/out/destination.txt"), outputRoot);
        TemplateExpansionAction a = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact, Template.forString(TemplateExpansionActionTest.TEMPLATE), ImmutableList.of(Substitution.of("%key%", "foo")), false);
        TemplateExpansionAction b = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact2, Template.forString(TemplateExpansionActionTest.TEMPLATE), ImmutableList.of(Substitution.of("%key%", "foo")), false);
        assertThat(computeKey(a)).isEqualTo(computeKey(b));
    }

    @Test
    public void testKeyDiffersForSubstitution() throws Exception {
        Artifact outputArtifact2 = new Artifact(scratch.resolve("/workspace/out/destination.txt"), outputRoot);
        TemplateExpansionAction a = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact, Template.forString(TemplateExpansionActionTest.TEMPLATE), ImmutableList.of(Substitution.of("%key%", "foo")), false);
        TemplateExpansionAction b = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact2, Template.forString(TemplateExpansionActionTest.TEMPLATE), ImmutableList.of(Substitution.of("%key%", "foo2")), false);
        assertThat(computeKey(a)).isNotEqualTo(computeKey(b));
    }

    @Test
    public void testKeyDiffersForExecutable() throws Exception {
        Artifact outputArtifact2 = new Artifact(scratch.resolve("/workspace/out/destination.txt"), outputRoot);
        TemplateExpansionAction a = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact, Template.forString(TemplateExpansionActionTest.TEMPLATE), ImmutableList.of(Substitution.of("%key%", "foo")), false);
        TemplateExpansionAction b = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact2, Template.forString(TemplateExpansionActionTest.TEMPLATE), ImmutableList.of(Substitution.of("%key%", "foo")), true);
        assertThat(computeKey(a)).isNotEqualTo(computeKey(b));
    }

    @Test
    public void testKeyDiffersForTemplates() throws Exception {
        Artifact outputArtifact2 = new Artifact(scratch.resolve("/workspace/out/destination.txt"), outputRoot);
        TemplateExpansionAction a = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact, Template.forString(TemplateExpansionActionTest.TEMPLATE), ImmutableList.of(Substitution.of("%key%", "foo")), false);
        TemplateExpansionAction b = new TemplateExpansionAction(ActionsTestUtil.NULL_ACTION_OWNER, outputArtifact2, Template.forString(((TemplateExpansionActionTest.TEMPLATE) + " ")), ImmutableList.of(Substitution.of("%key%", "foo")), false);
        assertThat(computeKey(a)).isNotEqualTo(computeKey(b));
    }

    @Test
    public void testArtifactTemplateHasInput() {
        assertThat(createWithArtifact().getInputs()).containsExactly(inputArtifact);
    }

    @Test
    public void testArtifactTemplateHasOutput() {
        assertThat(createWithArtifact().getOutputs()).containsExactly(outputArtifact);
    }

    @Test
    public void testArtifactTemplateExpansion() throws Exception {
        // The trailing "" is needed because scratch.overwriteFile implicitly appends "\n".
        String expected = Joiner.on('\n').join("key=foo", "value=bar", "");
        executeTemplateExpansion(expected);
    }

    @Test
    public void testWithSpecialCharacters() throws Exception {
        // We have to overwrite the artifacts since we need our template in "inputs"
        createArtifacts(((TemplateExpansionActionTest.SPECIAL_CHARS) + "%key%"));
        // scratch.overwriteFile appends a newline, so we need an additional \n here
        String expected = String.format("%s%s\n", TemplateExpansionActionTest.SPECIAL_CHARS, TemplateExpansionActionTest.SPECIAL_CHARS);
        executeTemplateExpansion(expected, ImmutableList.of(Substitution.of("%key%", TemplateExpansionActionTest.SPECIAL_CHARS)));
    }
}

