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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.devtools.build.lib.actions.MutableActionGraph.ActionConflictException;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link ArtifactFactory}. Also see {@link ArtifactTest} for a test
 * of individual artifacts.
 */
@RunWith(JUnit4.class)
public class ArtifactFactoryTest {
    private static final RepositoryName MAIN = RepositoryName.MAIN;

    private Scratch scratch = new Scratch();

    private Path execRoot;

    private Root clientRoot;

    private Root clientRoRoot;

    private Root alienRoot;

    private ArtifactRoot outRoot;

    private PathFragment fooPath;

    private PackageIdentifier fooPackage;

    private PathFragment fooRelative;

    private PathFragment barPath;

    private PackageIdentifier barPackage;

    private PathFragment barRelative;

    private PathFragment alienPath;

    private PackageIdentifier alienPackage;

    private PathFragment alienRelative;

    private ArtifactFactory artifactFactory;

    private final ActionKeyContext actionKeyContext = new ActionKeyContext();

    @Test
    public void testGetSourceArtifactYieldsSameArtifact() throws Exception {
        assertThat(artifactFactory.getSourceArtifact(fooRelative, clientRoot)).isSameAs(artifactFactory.getSourceArtifact(fooRelative, clientRoot));
    }

    @Test
    public void testGetSourceArtifactUnnormalized() throws Exception {
        assertThat(artifactFactory.getSourceArtifact(PathFragment.create("foo/./foosource.txt"), clientRoot)).isSameAs(artifactFactory.getSourceArtifact(fooRelative, clientRoot));
    }

    @Test
    public void testResolveArtifact_noDerived_simpleSource() throws Exception {
        assertThat(artifactFactory.resolveSourceArtifact(fooRelative, ArtifactFactoryTest.MAIN)).isSameAs(artifactFactory.getSourceArtifact(fooRelative, clientRoot));
        assertThat(artifactFactory.resolveSourceArtifact(barRelative, ArtifactFactoryTest.MAIN)).isSameAs(artifactFactory.getSourceArtifact(barRelative, clientRoRoot));
    }

    @Test
    public void testResolveArtifact_inExternalRepo() throws Exception {
        Artifact a1 = artifactFactory.getSourceArtifact(alienRelative, alienRoot);
        Artifact a2 = artifactFactory.resolveSourceArtifact(alienRelative, ArtifactFactoryTest.MAIN);
        assertThat(a1).isSameAs(a2);
    }

    @Test
    public void testResolveArtifact_noDerived_derivedRoot() throws Exception {
        assertThat(artifactFactory.resolveSourceArtifact(outRoot.getRoot().getRelative(fooRelative).relativeTo(execRoot), ArtifactFactoryTest.MAIN)).isNull();
        assertThat(artifactFactory.resolveSourceArtifact(outRoot.getRoot().getRelative(barRelative).relativeTo(execRoot), ArtifactFactoryTest.MAIN)).isNull();
    }

    @Test
    public void testResolveArtifact_noDerived_simpleSource_other() throws Exception {
        Artifact actual = artifactFactory.resolveSourceArtifact(fooRelative, ArtifactFactoryTest.MAIN);
        assertThat(actual).isSameAs(artifactFactory.getSourceArtifact(fooRelative, clientRoot));
        actual = artifactFactory.resolveSourceArtifact(barRelative, ArtifactFactoryTest.MAIN);
        assertThat(actual).isSameAs(artifactFactory.getSourceArtifact(barRelative, clientRoRoot));
    }

    @Test
    public void testResolveArtifactWithUpLevelFailsCleanly() throws Exception {
        // We need a package in the root directory to make every exec path (even one with up-level
        // references) be in a package.
        Map<PackageIdentifier, Root> packageRoots = ImmutableMap.of(PackageIdentifier.createInMainRepo(PathFragment.create("")), clientRoot);
        artifactFactory.setPackageRoots(packageRoots::get);
        PathFragment outsideWorkspace = PathFragment.create("../foo");
        PathFragment insideWorkspace = PathFragment.create("../workspace/foo");
        assertThat(artifactFactory.resolveSourceArtifact(outsideWorkspace, ArtifactFactoryTest.MAIN)).isNull();
        assertWithMessage("Up-level-containing paths that descend into the right workspace aren't allowed").that(artifactFactory.resolveSourceArtifact(insideWorkspace, ArtifactFactoryTest.MAIN)).isNull();
        ArtifactFactoryTest.MockPackageRootResolver packageRootResolver = new ArtifactFactoryTest.MockPackageRootResolver();
        packageRootResolver.setPackageRoots(packageRoots);
        Map<PathFragment, Artifact> result = new HashMap<>();
        result.put(insideWorkspace, null);
        result.put(outsideWorkspace, null);
        assertThat(artifactFactory.resolveSourceArtifacts(ImmutableList.of(insideWorkspace, outsideWorkspace), packageRootResolver).entrySet()).containsExactlyElementsIn(result.entrySet());
    }

    @Test
    public void testClearResetsFactory() {
        Artifact fooArtifact = artifactFactory.getSourceArtifact(fooRelative, clientRoot);
        artifactFactory.clear();
        setupRoots();
        assertThat(artifactFactory.getSourceArtifact(fooRelative, clientRoot)).isNotSameAs(fooArtifact);
    }

    @Test
    public void testFindDerivedRoot() throws Exception {
        assertThat(artifactFactory.isDerivedArtifact(fooRelative)).isFalse();
        assertThat(artifactFactory.isDerivedArtifact(PathFragment.create("bazel-out/local-fastbuild/bin/foo"))).isTrue();
    }

    @Test
    public void testAbsoluteArtifact() throws Exception {
        Root absoluteRoot = Root.absoluteRoot(scratch.getFileSystem());
        assertThat(artifactFactory.getSourceArtifact(PathFragment.create("foo"), clientRoot).getExecPath()).isEqualTo(PathFragment.create("foo"));
        assertThat(artifactFactory.getSourceArtifact(PathFragment.create("/foo"), absoluteRoot).getExecPath()).isEqualTo(PathFragment.create("/foo"));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> artifactFactory.getSourceArtifact(PathFragment.create("/foo"), clientRoot));
        MoreAsserts.assertThrows(IllegalArgumentException.class, () -> artifactFactory.getSourceArtifact(PathFragment.create("foo"), absoluteRoot));
    }

    @Test
    public void testSetGeneratingActionIdempotenceNewActionGraph() throws Exception {
        Artifact a = artifactFactory.getDerivedArtifact(fooRelative, outRoot, ActionsTestUtil.NULL_ARTIFACT_OWNER);
        Artifact b = artifactFactory.getDerivedArtifact(barRelative, outRoot, ActionsTestUtil.NULL_ARTIFACT_OWNER);
        MutableActionGraph actionGraph = new MapBasedActionGraph(actionKeyContext);
        Action originalAction = new ActionsTestUtil.NullAction(ActionsTestUtil.NULL_ACTION_OWNER, a);
        actionGraph.registerAction(originalAction);
        // Creating a second Action referring to the Artifact should create a conflict.
        try {
            Action action = new ActionsTestUtil.NullAction(ActionsTestUtil.NULL_ACTION_OWNER, a, b);
            actionGraph.registerAction(action);
            Assert.fail();
        } catch (ActionConflictException e) {
            assertThat(e.getArtifact()).isSameAs(a);
            assertThat(actionGraph.getGeneratingAction(a)).isSameAs(originalAction);
        }
    }

    private static class MockPackageRootResolver implements PackageRootResolver {
        private final Map<PathFragment, Root> packageRoots = Maps.newHashMap();

        public void setPackageRoots(Map<PackageIdentifier, Root> packageRoots) {
            for (Map.Entry<PackageIdentifier, Root> packageRoot : packageRoots.entrySet()) {
                this.packageRoots.put(packageRoot.getKey().getPackageFragment(), packageRoot.getValue());
            }
        }

        @Override
        public Map<PathFragment, Root> findPackageRootsForFiles(Iterable<PathFragment> execPaths) {
            Map<PathFragment, Root> result = new HashMap<>();
            for (PathFragment execPath : execPaths) {
                for (PathFragment dir = execPath.getParentDirectory(); dir != null; dir = dir.getParentDirectory()) {
                    if ((packageRoots.get(dir)) != null) {
                        result.put(execPath, packageRoots.get(dir));
                    }
                }
                if ((result.get(execPath)) == null) {
                    result.put(execPath, null);
                }
            }
            return result;
        }
    }
}

