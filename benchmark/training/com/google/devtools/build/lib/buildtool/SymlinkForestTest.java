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
package com.google.devtools.build.lib.buildtool;


import LabelConstants.EXTERNAL_PACKAGE_IDENTIFIER;
import LabelConstants.EXTERNAL_PATH_PREFIX;
import PathFragment.EMPTY_FRAGMENT;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.cmdline.LabelConstants;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link SymlinkForest}.
 */
@RunWith(JUnit4.class)
public class SymlinkForestTest {
    private FileSystem fileSystem;

    private Path topDir;

    private Path file1;

    private Path file2;

    private Path aDir;

    // The execution root.
    private Path linkRoot;

    @Test
    public void testLongestPathPrefix() {
        PathFragment a = PathFragment.create("A");
        assertThat(SymlinkForestTest.longestPathPrefix("A/b", "A", "B")).isEqualTo(a);// simple parent

        assertThat(SymlinkForestTest.longestPathPrefix("A", "A", "B")).isEqualTo(a);// self

        assertThat(SymlinkForestTest.longestPathPrefix("A/B/c", "A", "A/B")).isEqualTo(a.getRelative("B"));// want longest

        assertThat(SymlinkForestTest.longestPathPrefix("C/b", "A", "B")).isNull();// not found in other parents

        assertThat(SymlinkForestTest.longestPathPrefix("A", "A/B", "B")).isNull();// not found in child

        assertThat(SymlinkForestTest.longestPathPrefix("A/B/C/d/e/f.h", "A/B/C", "B/C/d")).isEqualTo(a.getRelative("B/C"));
        assertThat(SymlinkForestTest.longestPathPrefix("A/f.h", "", "B/C/d")).isEqualTo(EMPTY_FRAGMENT);
    }

    @Test
    public void testDeleteTreesBelowNotPrefixed() throws IOException {
        createTestDirectoryTree();
        SymlinkForest.deleteTreesBelowNotPrefixed(topDir, new String[]{ "file-" });
        assertThat(file1.exists()).isTrue();
        assertThat(file2.exists()).isTrue();
        assertThat(aDir.exists()).isFalse();
    }

    @Test
    public void testPlantLinkForest() throws IOException {
        Root rootA = Root.fromPath(fileSystem.getPath("/A"));
        Root rootB = Root.fromPath(fileSystem.getPath("/B"));
        ImmutableMap<PackageIdentifier, Root> packageRootMap = ImmutableMap.<PackageIdentifier, Root>builder().put(createPkg(rootA, rootB, "pkgA"), rootA).put(createPkg(rootA, rootB, "dir1/pkgA"), rootA).put(createPkg(rootA, rootB, "dir1/pkgB"), rootB).put(createPkg(rootA, rootB, "dir2/pkg"), rootA).put(createPkg(rootA, rootB, "dir2/pkg/pkg"), rootB).put(createPkg(rootA, rootB, "pkgB"), rootB).put(createPkg(rootA, rootB, "pkgB/dir/pkg"), rootA).put(createPkg(rootA, rootB, "pkgB/pkg"), rootA).put(createPkg(rootA, rootB, "pkgB/pkg/pkg"), rootA).build();
        createPkg(rootA, rootB, "pkgB/dir");// create a file in there

        Path linkRoot = fileSystem.getPath("/linkRoot");
        FileSystemUtils.createDirectoryAndParents(linkRoot);
        plantSymlinkForest();
        assertLinksTo(linkRoot, rootA, "pkgA");
        assertIsDir(linkRoot, "dir1");
        assertLinksTo(linkRoot, rootA, "dir1/pkgA");
        assertLinksTo(linkRoot, rootB, "dir1/pkgB");
        assertIsDir(linkRoot, "dir2");
        assertIsDir(linkRoot, "dir2/pkg");
        assertLinksTo(linkRoot, rootA, "dir2/pkg/file");
        assertLinksTo(linkRoot, rootB, "dir2/pkg/pkg");
        assertIsDir(linkRoot, "pkgB");
        assertIsDir(linkRoot, "pkgB/dir");
        assertLinksTo(linkRoot, rootB, "pkgB/dir/file");
        assertLinksTo(linkRoot, rootA, "pkgB/dir/pkg");
        assertLinksTo(linkRoot, rootA, "pkgB/pkg");
    }

    @Test
    public void testTopLevelPackage() throws Exception {
        Root rootX = Root.fromPath(fileSystem.getPath("/X"));
        Root rootY = Root.fromPath(fileSystem.getPath("/Y"));
        ImmutableMap<PackageIdentifier, Root> packageRootMap = ImmutableMap.<PackageIdentifier, Root>builder().put(createPkg(rootX, rootY, ""), rootX).put(createPkg(rootX, rootY, "foo"), rootX).build();
        plantSymlinkForest();
        assertLinksTo(linkRoot, rootX, "file");
    }

    @Test
    public void testRemotePackage() throws Exception {
        Root outputBase = Root.fromPath(fileSystem.getPath("/ob"));
        Root rootY = Root.fromPath(outputBase.getRelative(EXTERNAL_PATH_PREFIX).getRelative("y"));
        Root rootZ = Root.fromPath(outputBase.getRelative(EXTERNAL_PATH_PREFIX).getRelative("z"));
        Root rootW = Root.fromPath(outputBase.getRelative(EXTERNAL_PATH_PREFIX).getRelative("w"));
        FileSystemUtils.createDirectoryAndParents(rootY.asPath());
        FileSystemUtils.createEmptyFile(rootY.getRelative("file"));
        ImmutableMap<PackageIdentifier, Root> packageRootMap = // Only top-level pkg.
        // Remote repo with and without top-level package.
        // Remote repo without top-level package.
        ImmutableMap.<PackageIdentifier, Root>builder().put(createPkg(outputBase, "y", "w"), outputBase).put(createPkg(outputBase, "z", ""), outputBase).put(createPkg(outputBase, "z", "a/b/c"), outputBase).put(createPkg(outputBase, "w", ""), outputBase).build();
        plantSymlinkForest();
        assertThat(linkRoot.getRelative(((LabelConstants.EXTERNAL_PATH_PREFIX) + "/y/file")).exists()).isFalse();
        assertLinksTo(linkRoot.getRelative(((LabelConstants.EXTERNAL_PATH_PREFIX) + "/y/w")), rootY.getRelative("w"));
        assertLinksTo(linkRoot.getRelative(((LabelConstants.EXTERNAL_PATH_PREFIX) + "/z/file")), rootZ.getRelative("file"));
        assertLinksTo(linkRoot.getRelative(((LabelConstants.EXTERNAL_PATH_PREFIX) + "/z/a")), rootZ.getRelative("a"));
        assertLinksTo(linkRoot.getRelative(((LabelConstants.EXTERNAL_PATH_PREFIX) + "/w/file")), rootW.getRelative("file"));
    }

    @Test
    public void testExternalPackage() throws Exception {
        Root root = Root.fromPath(fileSystem.getPath("/src"));
        ImmutableMap<PackageIdentifier, Root> packageRootMap = // Virtual root, shouldn't actually be linked in.
        ImmutableMap.<PackageIdentifier, Root>builder().put(EXTERNAL_PACKAGE_IDENTIFIER, root).build();
        plantSymlinkForest();
        assertThat(linkRoot.getRelative(EXTERNAL_PATH_PREFIX).exists()).isFalse();
    }

    @Test
    public void testWorkspaceName() throws Exception {
        Root root = Root.fromPath(fileSystem.getPath("/src"));
        ImmutableMap<PackageIdentifier, Root> packageRootMap = // Remote repo without top-level package.
        ImmutableMap.<PackageIdentifier, Root>builder().put(createPkg(root, "y", "w"), root).build();
        plantSymlinkForest();
        assertThat(linkRoot.getRelative("../wsname").exists()).isTrue();
    }

    @Test
    public void testExecrootVersionChanges() throws Exception {
        ImmutableMap<PackageIdentifier, Root> packageRootMap = ImmutableMap.of();
        linkRoot.getRelative("wsname").createDirectory();
        plantSymlinkForest();
        assertThat(linkRoot.getRelative("../wsname").isSymbolicLink()).isTrue();
    }
}

