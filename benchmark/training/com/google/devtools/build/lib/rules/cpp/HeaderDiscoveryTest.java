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


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.ActionExecutionException;
import com.google.devtools.build.lib.actions.ArtifactResolver;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Test.
 */
@RunWith(JUnit4.class)
public class HeaderDiscoveryTest {
    private final FileSystem fs = new InMemoryFileSystem();

    private final Path execRoot = fs.getPath("/execroot");

    private final Path derivedRoot = execRoot.getRelative("derived");

    private final ArtifactRoot artifactRoot = ArtifactRoot.asDerivedRoot(execRoot, derivedRoot);

    /**
     * Test that an included header is satisfied (=doesn't cause an error) if it provided by a tree
     * artifact.
     */
    @Test
    public void treeArtifactInclusionCheck() throws Exception {
        ArtifactResolver artifactResolver = Mockito.mock(ArtifactResolver.class);
        checkHeaderInclusion(artifactResolver, ImmutableList.of(derivedRoot.getRelative("tree_artifact/foo.h"), derivedRoot.getRelative("tree_artifact/subdir/foo.h")), ImmutableList.of(treeArtifact(derivedRoot.getRelative("tree_artifact"))));
        // Implicitly check that there are no exceptions thrown.
    }

    @Test
    public void errorsWhenMissingHeaders() {
        ArtifactResolver artifactResolver = Mockito.mock(ArtifactResolver.class);
        MoreAsserts.assertThrows(ActionExecutionException.class, () -> checkHeaderInclusion(artifactResolver, ImmutableList.of(derivedRoot.getRelative("tree_artifact1/foo.h"), derivedRoot.getRelative("tree_artifact1/subdir/foo.h")), ImmutableList.of(treeArtifact(derivedRoot.getRelative("tree_artifact2")))));
    }
}

