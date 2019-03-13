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


import build.bazel.remote.execution.v2.Digest;
import build.bazel.remote.execution.v2.Directory;
import build.bazel.remote.execution.v2.DirectoryNode;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.actions.ActionInput;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.actions.FileArtifactValue;
import com.google.devtools.build.lib.remote.util.DigestUtil;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link MerkleTree}.
 */
@RunWith(JUnit4.class)
public class MerkleTreeTest {
    private Path execRoot;

    private ArtifactRoot artifactRoot;

    private DigestUtil digestUtil;

    @Test
    public void emptyMerkleTree() throws IOException {
        MerkleTree tree = MerkleTree.build(Collections.emptySortedMap(), new StaticMetadataProvider(Collections.emptyMap()), execRoot, digestUtil);
        Digest emptyDigest = digestUtil.compute(new byte[0]);
        assertThat(tree.getRootDigest()).isEqualTo(emptyDigest);
    }

    @Test
    public void buildMerkleTree() throws IOException {
        // arrange
        SortedMap<PathFragment, ActionInput> sortedInputs = new TreeMap<>();
        Map<ActionInput, FileArtifactValue> metadata = new HashMap<>();
        Artifact foo = addFile("srcs/foo.cc", "foo", sortedInputs, metadata);
        Artifact bar = addFile("srcs/bar.cc", "bar", sortedInputs, metadata);
        Artifact buzz = addFile("srcs/fizz/buzz.cc", "buzz", sortedInputs, metadata);
        Artifact fizzbuzz = addFile("srcs/fizz/fizzbuzz.cc", "fizzbuzz", sortedInputs, metadata);
        Directory fizzDir = Directory.newBuilder().addFiles(MerkleTreeTest.newFileNode("buzz.cc", digestUtil.computeAsUtf8("buzz"))).addFiles(MerkleTreeTest.newFileNode("fizzbuzz.cc", digestUtil.computeAsUtf8("fizzbuzz"))).build();
        Directory srcsDir = Directory.newBuilder().addFiles(MerkleTreeTest.newFileNode("bar.cc", digestUtil.computeAsUtf8("bar"))).addFiles(MerkleTreeTest.newFileNode("foo.cc", digestUtil.computeAsUtf8("foo"))).addDirectories(DirectoryNode.newBuilder().setName("fizz").setDigest(digestUtil.compute(fizzDir))).build();
        Directory rootDir = Directory.newBuilder().addDirectories(DirectoryNode.newBuilder().setName("srcs").setDigest(digestUtil.compute(srcsDir))).build();
        // act
        MerkleTree tree = MerkleTree.build(sortedInputs, new StaticMetadataProvider(metadata), execRoot, digestUtil);
        // assert
        Digest expectedRootDigest = digestUtil.compute(rootDir);
        assertThat(tree.getRootDigest()).isEqualTo(expectedRootDigest);
        Digest[] dirDigests = new Digest[]{ digestUtil.compute(fizzDir), digestUtil.compute(srcsDir), digestUtil.compute(rootDir) };
        assertThat(tree.getDirectoryByDigest(dirDigests[0])).isEqualTo(fizzDir);
        assertThat(tree.getDirectoryByDigest(dirDigests[1])).isEqualTo(srcsDir);
        assertThat(tree.getDirectoryByDigest(dirDigests[2])).isEqualTo(rootDir);
        Digest[] inputDigests = new Digest[]{ digestUtil.computeAsUtf8("foo"), digestUtil.computeAsUtf8("bar"), digestUtil.computeAsUtf8("buzz"), digestUtil.computeAsUtf8("fizzbuzz") };
        assertThat(tree.getInputByDigest(inputDigests[0])).isEqualTo(foo);
        assertThat(tree.getInputByDigest(inputDigests[1])).isEqualTo(bar);
        assertThat(tree.getInputByDigest(inputDigests[2])).isEqualTo(buzz);
        assertThat(tree.getInputByDigest(inputDigests[3])).isEqualTo(fizzbuzz);
        Digest[] allDigests = Iterables.toArray(tree.getAllDigests(), Digest.class);
        assertThat(allDigests.length).isEqualTo(((dirDigests.length) + (inputDigests.length)));
        assertThat(allDigests).asList().containsAllIn(dirDigests);
        assertThat(allDigests).asList().containsAllIn(inputDigests);
    }
}

