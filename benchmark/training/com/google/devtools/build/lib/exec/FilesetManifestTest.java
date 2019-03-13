/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.exec;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.FilesetOutputSymlink;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FilesetManifest}.
 */
@RunWith(JUnit4.class)
public final class FilesetManifestTest {
    private static final PathFragment EXEC_ROOT = PathFragment.create("/root");

    @Test
    public void testEmptyManifest() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of();
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).isEmpty();
    }

    @Test
    public void testManifestWithSingleFile() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "/dir/file"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), "/dir/file");
    }

    @Test
    public void testManifestWithTwoFiles() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "/dir/file"), FilesetManifestTest.filesetSymlink("baz", "/dir/file"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), "/dir/file", PathFragment.create("out/foo/baz"), "/dir/file");
    }

    @Test
    public void testManifestWithDirectory() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "/some"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), "/some");
    }

    /**
     * Regression test: code was previously crashing in this case.
     */
    @Test
    public void testManifestWithEmptyPath() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", ""));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), null);
    }

    @Test
    public void testManifestWithErrorOnRelativeSymlink() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "foo"), FilesetManifestTest.filesetSymlink("foo", "/foo/bar"));
        try {
            FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.ERROR);
            Assert.fail("Expected to throw");
        } catch (IOException e) {
            assertThat(e).hasMessageThat().isEqualTo("runfiles target is not absolute: foo");
        }
    }

    @Test
    public void testManifestWithIgnoredRelativeSymlink() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "foo"), FilesetManifestTest.filesetSymlink("foo", "/foo/bar"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/foo"), "/foo/bar");
    }

    @Test
    public void testManifestWithResolvedRelativeSymlink() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "foo"), FilesetManifestTest.filesetSymlink("foo", "/foo/bar"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.RESOLVE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), "/foo/bar", PathFragment.create("out/foo/foo"), "/foo/bar");
    }

    @Test
    public void testManifestWithResolvedRelativeSymlinkWithDotSlash() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "./foo"), FilesetManifestTest.filesetSymlink("foo", "/foo/bar"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.RESOLVE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), "/foo/bar", PathFragment.create("out/foo/foo"), "/foo/bar");
    }

    @Test
    public void testManifestWithResolvedRelativeSymlinkWithDotDotSlash() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar/bar", "../foo/foo"), FilesetManifestTest.filesetSymlink("foo/foo", "/foo/bar"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.RESOLVE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar/bar"), "/foo/bar", PathFragment.create("out/foo/foo/foo"), "/foo/bar");
    }

    @Test
    public void testManifestWithUnresolvableRelativeSymlink() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "foo"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.RESOLVE);
        assertThat(manifest.getEntries()).isEmpty();
        assertThat(manifest.getArtifactValues()).isEmpty();
    }

    @Test
    public void testManifestWithUnresolvableRelativeSymlinkToRelativeSymlink() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "foo"), FilesetManifestTest.filesetSymlink("foo", "baz"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.RESOLVE);
        assertThat(manifest.getEntries()).isEmpty();
        assertThat(manifest.getArtifactValues()).isEmpty();
    }

    /**
     * Current behavior is first one wins.
     */
    @Test
    public void testDefactoBehaviorWithDuplicateEntries() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", "/foo/bar"), FilesetManifestTest.filesetSymlink("bar", "/baz"));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), "/foo/bar");
    }

    @Test
    public void testManifestWithExecRootRelativePath() throws Exception {
        List<FilesetOutputSymlink> symlinks = ImmutableList.of(FilesetManifestTest.filesetSymlink("bar", FilesetManifestTest.EXEC_ROOT.getRelative("foo/bar").getPathString()));
        FilesetManifest manifest = FilesetManifest.constructFilesetManifest(symlinks, PathFragment.create("out/foo"), RelativeSymlinkBehavior.IGNORE);
        assertThat(manifest.getEntries()).containsExactly(PathFragment.create("out/foo/bar"), "foo/bar");
    }
}

