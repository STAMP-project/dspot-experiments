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
package com.google.devtools.build.lib.rules.cpp;


import Symlinks.NOFOLLOW;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.ActionKeyContext;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for {@link CreateIncSymlinkAction}.
 */
@RunWith(JUnit4.class)
public class CreateIncSymlinkActionTest extends FoundationTestCase {
    private final ActionKeyContext actionKeyContext = new ActionKeyContext();

    @Test
    public void testDifferentOrderSameActionKey() throws Exception {
        Path includePath = rootDirectory.getRelative("out");
        ArtifactRoot root = ArtifactRoot.asDerivedRoot(rootDirectory, includePath);
        Artifact a = new Artifact(PathFragment.create("a"), root);
        Artifact b = new Artifact(PathFragment.create("b"), root);
        Artifact c = new Artifact(PathFragment.create("c"), root);
        Artifact d = new Artifact(PathFragment.create("d"), root);
        CreateIncSymlinkAction action1 = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(a, b, c, d), includePath);
        // Can't reuse the artifacts here; that would lead to DuplicateArtifactException.
        a = new Artifact(PathFragment.create("a"), root);
        b = new Artifact(PathFragment.create("b"), root);
        c = new Artifact(PathFragment.create("c"), root);
        d = new Artifact(PathFragment.create("d"), root);
        CreateIncSymlinkAction action2 = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(c, d, a, b), includePath);
        assertThat(computeKey(action2)).isEqualTo(computeKey(action1));
    }

    @Test
    public void testDifferentTargetsDifferentActionKey() throws Exception {
        Path includePath = rootDirectory.getRelative("out");
        ArtifactRoot root = ArtifactRoot.asDerivedRoot(rootDirectory, includePath);
        Artifact a = new Artifact(PathFragment.create("a"), root);
        Artifact b = new Artifact(PathFragment.create("b"), root);
        CreateIncSymlinkAction action1 = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(a, b), includePath);
        // Can't reuse the artifacts here; that would lead to DuplicateArtifactException.
        a = new Artifact(PathFragment.create("a"), root);
        b = new Artifact(PathFragment.create("c"), root);
        CreateIncSymlinkAction action2 = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(a, b), includePath);
        assertThat(computeKey(action2)).isNotEqualTo(computeKey(action1));
    }

    @Test
    public void testDifferentSymlinksDifferentActionKey() throws Exception {
        Path includePath = rootDirectory.getRelative("out");
        ArtifactRoot root = ArtifactRoot.asDerivedRoot(rootDirectory, includePath);
        Artifact a = new Artifact(PathFragment.create("a"), root);
        Artifact b = new Artifact(PathFragment.create("b"), root);
        CreateIncSymlinkAction action1 = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(a, b), includePath);
        // Can't reuse the artifacts here; that would lead to DuplicateArtifactException.
        a = new Artifact(PathFragment.create("c"), root);
        b = new Artifact(PathFragment.create("b"), root);
        CreateIncSymlinkAction action2 = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(a, b), includePath);
        assertThat(computeKey(action2)).isNotEqualTo(computeKey(action1));
    }

    @Test
    public void testExecute() throws Exception {
        Path outputDir = rootDirectory.getRelative("out");
        outputDir.createDirectory();
        ArtifactRoot root = ArtifactRoot.asDerivedRoot(rootDirectory, outputDir);
        Path symlink = rootDirectory.getRelative("out/a");
        Artifact a = new Artifact(symlink, root);
        Artifact b = new Artifact(PathFragment.create("b"), root);
        CreateIncSymlinkAction action = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(a, b), outputDir);
        action.execute(makeDummyContext());
        symlink.stat(NOFOLLOW);
        assertThat(symlink.isSymbolicLink()).isTrue();
        assertThat(b.getPath().asFragment()).isEqualTo(symlink.readSymbolicLink());
        assertThat(rootDirectory.getRelative("a").exists()).isFalse();
    }

    @Test
    public void testFileRemoved() throws Exception {
        Path outputDir = rootDirectory.getRelative("out");
        outputDir.createDirectory();
        ArtifactRoot root = ArtifactRoot.asDerivedRoot(rootDirectory, outputDir);
        Path symlink = rootDirectory.getRelative("out/subdir/a");
        Artifact a = new Artifact(symlink, root);
        Artifact b = new Artifact(PathFragment.create("b"), root);
        CreateIncSymlinkAction action = new CreateIncSymlinkAction(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableMap.of(a, b), outputDir);
        Path extra = rootDirectory.getRelative("out/extra");
        FileSystemUtils.createEmptyFile(extra);
        assertThat(extra.exists()).isTrue();
        action.prepare(fileSystem, rootDirectory);
        assertThat(extra.exists()).isFalse();
    }
}

