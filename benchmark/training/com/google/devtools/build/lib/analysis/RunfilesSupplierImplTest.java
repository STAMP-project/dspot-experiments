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
package com.google.devtools.build.lib.analysis;


import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.actions.RunfilesSupplier;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Runfiles.EMPTY;


/**
 * Tests for RunfilesSupplierImpl
 */
@RunWith(JUnit4.class)
public class RunfilesSupplierImplTest {
    private Path execRoot;

    private ArtifactRoot rootDir;

    private ArtifactRoot middlemanRoot;

    @Test
    public void testGetArtifactsWithSingleMapping() {
        List<Artifact> artifacts = RunfilesSupplierImplTest.mkArtifacts(rootDir, "thing1", "thing2");
        RunfilesSupplierImpl underTest = new RunfilesSupplierImpl(PathFragment.create("notimportant"), RunfilesSupplierImplTest.mkRunfiles(artifacts));
        assertThat(underTest.getArtifacts()).containsExactlyElementsIn(artifacts);
    }

    @Test
    public void testGetManifestsWhenNone() {
        RunfilesSupplier underTest = new RunfilesSupplierImpl(PathFragment.create("ignored"), EMPTY, null);
        assertThat(underTest.getManifests()).isEmpty();
    }

    @Test
    public void testGetManifestsWhenSupplied() {
        Artifact manifest = new Artifact(PathFragment.create("manifest"), rootDir);
        RunfilesSupplier underTest = new RunfilesSupplierImpl(PathFragment.create("ignored"), EMPTY, manifest);
        assertThat(underTest.getManifests()).containsExactly(manifest);
    }
}

