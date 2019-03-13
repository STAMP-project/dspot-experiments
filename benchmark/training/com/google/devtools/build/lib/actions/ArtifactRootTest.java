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
package com.google.devtools.build.lib.actions;


import PathFragment.EMPTY_FRAGMENT;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ArtifactRoot}.
 */
@RunWith(JUnit4.class)
public class ArtifactRootTest {
    private Scratch scratch = new Scratch();

    @Test
    public void testAsSourceRoot() throws IOException {
        Path sourceDir = scratch.dir("/source");
        ArtifactRoot root = ArtifactRoot.asSourceRoot(Root.fromPath(sourceDir));
        assertThat(root.isSourceRoot()).isTrue();
        assertThat(root.getExecPath()).isEqualTo(EMPTY_FRAGMENT);
        assertThat(root.getRoot()).isEqualTo(Root.fromPath(sourceDir));
        assertThat(root.toString()).isEqualTo("/source[source]");
    }

    @Test
    public void testBadAsSourceRoot() {
        try {
            ArtifactRoot.asSourceRoot(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testAsDerivedRoot() throws IOException {
        Path execRoot = scratch.dir("/exec");
        Path rootDir = scratch.dir("/exec/root");
        ArtifactRoot root = ArtifactRoot.asDerivedRoot(execRoot, rootDir);
        assertThat(root.isSourceRoot()).isFalse();
        assertThat(root.getExecPath()).isEqualTo(PathFragment.create("root"));
        assertThat(root.getRoot()).isEqualTo(Root.fromPath(rootDir));
        assertThat(root.toString()).isEqualTo("/exec/root[derived]");
    }

    @Test
    public void testBadAsDerivedRoot() throws IOException {
        try {
            Path execRoot = scratch.dir("/exec");
            Path outsideDir = scratch.dir("/not_exec");
            ArtifactRoot.asDerivedRoot(execRoot, outsideDir);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testBadAsDerivedRootSameForBoth() throws IOException {
        try {
            Path execRoot = scratch.dir("/exec");
            ArtifactRoot.asDerivedRoot(execRoot, execRoot);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testBadAsDerivedRootNullDir() throws IOException {
        try {
            Path execRoot = scratch.dir("/exec");
            ArtifactRoot.asDerivedRoot(execRoot, null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testBadAsDerivedRootNullExecRoot() throws IOException {
        try {
            Path execRoot = scratch.dir("/exec");
            ArtifactRoot.asDerivedRoot(null, execRoot);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testEquals() throws IOException {
        Path execRoot = scratch.dir("/exec");
        Path rootDir = scratch.dir("/exec/root");
        Path otherRootDir = scratch.dir("/");
        Path sourceDir = scratch.dir("/source");
        ArtifactRoot rootA = ArtifactRoot.asDerivedRoot(execRoot, rootDir);
        assertEqualsAndHashCode(true, rootA, ArtifactRoot.asDerivedRoot(execRoot, rootDir));
        assertEqualsAndHashCode(false, rootA, ArtifactRoot.asSourceRoot(Root.fromPath(sourceDir)));
        assertEqualsAndHashCode(false, rootA, ArtifactRoot.asSourceRoot(Root.fromPath(rootDir)));
        assertEqualsAndHashCode(false, rootA, ArtifactRoot.asDerivedRoot(otherRootDir, rootDir));
    }
}

