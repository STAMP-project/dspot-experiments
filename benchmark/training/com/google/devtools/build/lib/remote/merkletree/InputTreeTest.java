/**
 * Copyright 2019 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.remote.merkletree;


import com.google.devtools.build.lib.actions.ActionInput;
import com.google.devtools.build.lib.actions.ActionInputHelper;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.actions.FileArtifactValue;
import com.google.devtools.build.lib.actions.cache.VirtualActionInput;
import com.google.devtools.build.lib.remote.merkletree.InputTree.FileNode;
import com.google.devtools.build.lib.remote.util.DigestUtil;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link InputTree}.
 */
@RunWith(JUnit4.class)
public class InputTreeTest {
    private Path execRoot;

    private ArtifactRoot artifactRoot;

    private DigestUtil digestUtil;

    @Test
    public void emptyTreeShouldWork() throws Exception {
        InputTree tree = InputTree.build(new TreeMap(), new StaticMetadataProvider(Collections.emptyMap()), execRoot, digestUtil);
        assertThat(InputTreeTest.directoryNodesAtDepth(tree, 0)).isEmpty();
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 0)).isEmpty();
    }

    @Test
    public void buildingATreeOfFilesShouldWork() throws Exception {
        SortedMap<PathFragment, ActionInput> sortedInputs = new TreeMap<>();
        Map<ActionInput, FileArtifactValue> metadata = new HashMap<>();
        Artifact foo = addFile("srcs/foo.cc", "foo", sortedInputs, metadata);
        Artifact bar = addFile("srcs/bar.cc", "bar", sortedInputs, metadata);
        Artifact buzz = addFile("srcs/fizz/buzz.cc", "buzz", sortedInputs, metadata);
        InputTree tree = InputTree.build(sortedInputs, new StaticMetadataProvider(metadata), execRoot, digestUtil);
        InputTreeTest.assertLexicographicalOrder(tree);
        assertThat(InputTreeTest.directoriesAtDepth(0, tree)).containsExactly("srcs");
        assertThat(InputTreeTest.directoriesAtDepth(1, tree)).containsExactly("fizz");
        assertThat(InputTreeTest.directoriesAtDepth(2, tree)).isEmpty();
        FileNode expectedFooNode = new FileNode("foo.cc", foo, digestUtil.computeAsUtf8("foo"));
        FileNode expectedBarNode = new FileNode("bar.cc", bar, digestUtil.computeAsUtf8("bar"));
        FileNode expectedBuzzNode = new FileNode("buzz.cc", buzz, digestUtil.computeAsUtf8("buzz"));
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 0)).isEmpty();
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 1)).containsExactly(expectedFooNode, expectedBarNode);
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 2)).containsExactly(expectedBuzzNode);
    }

    @Test
    public void virtualActionInputShouldWork() throws Exception {
        SortedMap<PathFragment, ActionInput> sortedInputs = new TreeMap<>();
        Map<ActionInput, FileArtifactValue> metadata = new HashMap<>();
        Artifact foo = addFile("srcs/foo.cc", "foo", sortedInputs, metadata);
        VirtualActionInput bar = addVirtualFile("srcs/bar.cc", "bar", sortedInputs);
        InputTree tree = InputTree.build(sortedInputs, new StaticMetadataProvider(metadata), execRoot, digestUtil);
        InputTreeTest.assertLexicographicalOrder(tree);
        assertThat(InputTreeTest.directoriesAtDepth(0, tree)).containsExactly("srcs");
        assertThat(InputTreeTest.directoriesAtDepth(1, tree)).isEmpty();
        FileNode expectedFooNode = new FileNode("foo.cc", foo, digestUtil.computeAsUtf8("foo"));
        FileNode expectedBarNode = new FileNode("bar.cc", bar, digestUtil.computeAsUtf8("bar"));
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 0)).isEmpty();
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 1)).containsExactly(expectedFooNode, expectedBarNode);
    }

    @Test
    public void directoryInputShouldBeExpanded() throws Exception {
        // Test that directory inputs are fully expanded and added to the input tree.
        // Note that this test is not about tree artifacts, but normal artifacts that point to
        // a directory on disk.
        SortedMap<PathFragment, ActionInput> sortedInputs = new TreeMap<>();
        Map<ActionInput, FileArtifactValue> metadata = new HashMap<>();
        Artifact foo = addFile("srcs/foo.cc", "foo", sortedInputs, metadata);
        Path dirPath = execRoot.getRelative("srcs/dir");
        dirPath.createDirectoryAndParents();
        Path barPath = dirPath.getRelative("bar.cc");
        FileSystemUtils.writeContentAsLatin1(barPath, "bar");
        ActionInput bar = ActionInputHelper.fromPath(barPath.relativeTo(execRoot));
        metadata.put(bar, FileArtifactValue.createShareable(barPath));
        dirPath.getRelative("fizz").createDirectoryAndParents();
        Path buzzPath = dirPath.getRelative("fizz/buzz.cc");
        FileSystemUtils.writeContentAsLatin1(dirPath.getRelative("fizz/buzz.cc"), "buzz");
        ActionInput buzz = ActionInputHelper.fromPath(buzzPath.relativeTo(execRoot));
        metadata.put(buzz, FileArtifactValue.createShareable(buzzPath));
        Artifact dir = new Artifact(dirPath, artifactRoot);
        sortedInputs.put(dirPath.relativeTo(execRoot), dir);
        metadata.put(dir, FileArtifactValue.createShareable(dirPath));
        InputTree tree = InputTree.build(sortedInputs, new StaticMetadataProvider(metadata), execRoot, digestUtil);
        InputTreeTest.assertLexicographicalOrder(tree);
        assertThat(InputTreeTest.directoriesAtDepth(0, tree)).containsExactly("srcs");
        assertThat(InputTreeTest.directoriesAtDepth(1, tree)).containsExactly("dir");
        assertThat(InputTreeTest.directoriesAtDepth(2, tree)).containsExactly("fizz");
        assertThat(InputTreeTest.directoriesAtDepth(3, tree)).isEmpty();
        FileNode expectedFooNode = new FileNode("foo.cc", foo, digestUtil.computeAsUtf8("foo"));
        FileNode expectedBarNode = new FileNode("bar.cc", bar, digestUtil.computeAsUtf8("bar"));
        FileNode expectedBuzzNode = new FileNode("buzz.cc", buzz, digestUtil.computeAsUtf8("buzz"));
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 0)).isEmpty();
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 1)).containsExactly(expectedFooNode);
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 2)).containsExactly(expectedBarNode);
        assertThat(InputTreeTest.fileNodesAtDepth(tree, 3)).containsExactly(expectedBuzzNode);
    }
}

