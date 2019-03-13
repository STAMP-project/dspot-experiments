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
package com.google.devtools.build.lib.pkgcache;


import BazelSkyframeExecutorConstants.BUILD_FILES_BY_PRIORITY;
import UnixGlob.FilesystemCalls;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.lib.vfs.UnixGlob;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test package-path logic.
 */
@RunWith(JUnit4.class)
public class PathPackageLocatorTest extends FoundationTestCase {
    private Path buildBazelFile1A;

    private Path buildFile1B;

    private Path buildFile2C;

    private Path buildFile2CD;

    private Path buildFile2F;

    private Path buildFile2FGH;

    private Path buildBazelFile3A;

    private Path buildFile3B;

    private Path buildFile3CI;

    private Path rootDir1;

    private Path rootDir1WorkspaceFile;

    private Path rootDir2;

    private Path rootDir3ParentParent;

    private Path rootDir3;

    private Path rootDir4Parent;

    private Path rootDir4;

    private Path rootDir5;

    private PathPackageLocator locator;

    private PathPackageLocator locatorWithSymlinks;

    @Test
    public void testGetPackageBuildFile() throws Exception {
        AtomicReference<? extends UnixGlob.FilesystemCalls> cache = UnixGlob.DEFAULT_SYSCALLS_REF;
        assertThat(locator.getPackageBuildFile(PackageIdentifier.createInMainRepo("A"))).isEqualTo(buildBazelFile1A);
        assertThat(locator.getPackageBuildFileNullable(PackageIdentifier.createInMainRepo("A"), cache)).isEqualTo(buildBazelFile1A);
        assertThat(locator.getPackageBuildFile(PackageIdentifier.createInMainRepo("B"))).isEqualTo(buildFile1B);
        assertThat(locator.getPackageBuildFileNullable(PackageIdentifier.createInMainRepo("B"), cache)).isEqualTo(buildFile1B);
        assertThat(locator.getPackageBuildFile(PackageIdentifier.createInMainRepo("C"))).isEqualTo(buildFile2C);
        assertThat(locator.getPackageBuildFileNullable(PackageIdentifier.createInMainRepo("C"), cache)).isEqualTo(buildFile2C);
        assertThat(locator.getPackageBuildFile(PackageIdentifier.createInMainRepo("C/D"))).isEqualTo(buildFile2CD);
        assertThat(locator.getPackageBuildFileNullable(PackageIdentifier.createInMainRepo("C/D"), cache)).isEqualTo(buildFile2CD);
        checkFails("C/E", "no such package 'C/E': BUILD file not found on package path");
        assertThat(locator.getPackageBuildFileNullable(PackageIdentifier.createInMainRepo("C/E"), cache)).isNull();
        assertThat(locator.getPackageBuildFile(PackageIdentifier.createInMainRepo("F"))).isEqualTo(buildFile2F);
        checkFails("F/G", "no such package 'F/G': BUILD file not found on package path");
        assertThat(locator.getPackageBuildFileNullable(PackageIdentifier.createInMainRepo("F/G"), cache)).isNull();
        assertThat(locator.getPackageBuildFile(PackageIdentifier.createInMainRepo("F/G/H"))).isEqualTo(buildFile2FGH);
        assertThat(locator.getPackageBuildFileNullable(PackageIdentifier.createInMainRepo("F/G/H"), cache)).isEqualTo(buildFile2FGH);
        checkFails("I", "no such package 'I': BUILD file not found on package path");
    }

    @Test
    public void testGetPackageBuildFileWithSymlinks() throws Exception {
        assertThat(locatorWithSymlinks.getPackageBuildFile(PackageIdentifier.createInMainRepo("A"))).isEqualTo(buildBazelFile3A);
        assertThat(locatorWithSymlinks.getPackageBuildFile(PackageIdentifier.createInMainRepo("B"))).isEqualTo(buildFile3B);
        assertThat(locatorWithSymlinks.getPackageBuildFile(PackageIdentifier.createInMainRepo("C/I"))).isEqualTo(buildFile3CI);
        PathPackageLocatorTest.checkFails(locatorWithSymlinks, "C/D", "no such package 'C/D': BUILD file not found on package path");
    }

    @Test
    public void testGetWorkspaceFile() throws Exception {
        assertThat(locator.getWorkspaceFile()).isEqualTo(rootDir1WorkspaceFile);
    }

    @Test
    public void testExists() throws Exception {
        Path nonExistentRoot1 = setLocator("/non/existent/1/workspace");
        // Now let's create the root:
        createBuildFile(nonExistentRoot1, "X");
        // The package isn't found
        // The package is found, because we didn't drop the root:
        MoreAsserts.assertThrows(NoSuchPackageException.class, () -> locator.getPackageBuildFile(PackageIdentifier.createInMainRepo("X")));
        Path nonExistentRoot2 = setLocator("/non/existent/2/workspace");
        // Now let's create the root:
        createBuildFile(nonExistentRoot2, "X");
        // ...but the package is still not found, because we dropped the root:
        checkFails("X", "no such package 'X': BUILD file not found on package path");
    }

    @Test
    public void testPathResolution() throws Exception {
        Path workspace = scratch.dir("/some/path/to/workspace");
        Path clientPath = workspace.getRelative("somewhere/below/workspace");
        scratch.dir(clientPath.getPathString());
        Path belowClient = clientPath.getRelative("below/client");
        scratch.dir(belowClient.getPathString());
        List<String> pathElements = // Client-relative
        // Client-relative
        // Workspace-relative
        // Absolute
        ImmutableList.of("./below/client", ".", "%workspace%/somewhere", clientPath.getRelative("below").getPathString());
        assertThat(PathPackageLocator.create(null, pathElements, reporter, workspace, clientPath, BUILD_FILES_BY_PRIORITY).getPathEntries()).containsExactly(Root.fromPath(belowClient), Root.fromPath(clientPath), Root.fromPath(workspace.getRelative("somewhere")), Root.fromPath(clientPath.getRelative("below"))).inOrder();
    }

    @Test
    public void testRelativePathWarning() throws Exception {
        Path workspace = scratch.dir("/some/path/to/workspace");
        // No warning if workspace == cwd.
        PathPackageLocator.create(null, ImmutableList.of("./foo"), reporter, workspace, workspace, BUILD_FILES_BY_PRIORITY);
        assertThat(eventCollector.count()).isSameAs(0);
        PathPackageLocator.create(null, ImmutableList.of("./foo"), reporter, workspace, workspace.getRelative("foo"), BUILD_FILES_BY_PRIORITY);
        assertThat(eventCollector.count()).isSameAs(1);
        assertContainsEvent("The package path element 'foo' will be taken relative");
    }

    /**
     * Regression test for bug: "IllegalArgumentException in PathPackageLocator.create()"
     */
    @Test
    public void testDollarSigns() throws Exception {
        Path workspace = scratch.dir("/some/path/to/workspace$1");
        PathPackageLocator.create(null, ImmutableList.of("%workspace%/blabla"), reporter, workspace, workspace.getRelative("foo"), BUILD_FILES_BY_PRIORITY);
    }
}

