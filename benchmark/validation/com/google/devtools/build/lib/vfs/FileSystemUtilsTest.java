/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.vfs;


import MoveResult.FILE_COPIED;
import MoveResult.FILE_MOVED;
import Symlinks.FOLLOW;
import Symlinks.NOFOLLOW;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.devtools.build.lib.testutil.BlazeTestUtils;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * This class tests the file system utilities.
 */
@RunWith(JUnit4.class)
public class FileSystemUtilsTest {
    private ManualClock clock;

    private FileSystem fileSystem;

    private Path workingDir;

    Path topDir;

    Path file1;

    Path file2;

    Path aDir;

    Path bDir;

    Path file3;

    Path innerDir;

    Path link1;

    Path dirLink;

    Path file4;

    Path file5;

    // tests
    @Test
    public void testChangeModtime() throws IOException {
        Path file = fileSystem.getPath("/my-file");
        try {
            BlazeTestUtils.changeModtime(file);
            Assert.fail();
        } catch (FileNotFoundException e) {
            /* ok */
        }
        FileSystemUtils.createEmptyFile(file);
        long prevMtime = file.getLastModifiedTime();
        BlazeTestUtils.changeModtime(file);
        assertThat((prevMtime == (file.getLastModifiedTime()))).isFalse();
    }

    @Test
    public void testCommonAncestor() {
        assertThat(FileSystemUtils.commonAncestor(topDir, topDir)).isEqualTo(topDir);
        assertThat(FileSystemUtils.commonAncestor(file1, file3)).isEqualTo(topDir);
        assertThat(FileSystemUtils.commonAncestor(file1, dirLink)).isEqualTo(topDir);
    }

    @Test
    public void testRelativePath() throws IOException {
        createTestDirectoryTree();
        assertThat(FileSystemUtils.relativePath(PathFragment.create("/top-dir"), PathFragment.create("/top-dir/file-1")).getPathString()).isEqualTo("file-1");
        assertThat(FileSystemUtils.relativePath(PathFragment.create("/top-dir"), PathFragment.create("/top-dir")).getPathString()).isEqualTo("");
        assertThat(FileSystemUtils.relativePath(PathFragment.create("/top-dir"), PathFragment.create("/top-dir/a-dir/inner-dir/dir-link")).getPathString()).isEqualTo("a-dir/inner-dir/dir-link");
        assertThat(FileSystemUtils.relativePath(PathFragment.create("/top-dir"), PathFragment.create("/file-4")).getPathString()).isEqualTo("../file-4");
        assertThat(FileSystemUtils.relativePath(PathFragment.create("/top-dir/a-dir/inner-dir"), PathFragment.create("/file-4")).getPathString()).isEqualTo("../../../file-4");
    }

    @Test
    public void testRemoveExtension_Strings() throws Exception {
        assertThat(FileSystemUtils.removeExtension("foo.c")).isEqualTo("foo");
        assertThat(FileSystemUtils.removeExtension("a/foo.c")).isEqualTo("a/foo");
        assertThat(FileSystemUtils.removeExtension("a.b/foo")).isEqualTo("a.b/foo");
        assertThat(FileSystemUtils.removeExtension("foo")).isEqualTo("foo");
        assertThat(FileSystemUtils.removeExtension("foo.")).isEqualTo("foo");
    }

    @Test
    public void testRemoveExtension_Paths() throws Exception {
        FileSystemUtilsTest.assertPath("/foo", FileSystemUtils.removeExtension(fileSystem.getPath("/foo.c")));
        FileSystemUtilsTest.assertPath("/a/foo", FileSystemUtils.removeExtension(fileSystem.getPath("/a/foo.c")));
        FileSystemUtilsTest.assertPath("/a.b/foo", FileSystemUtils.removeExtension(fileSystem.getPath("/a.b/foo")));
        FileSystemUtilsTest.assertPath("/foo", FileSystemUtils.removeExtension(fileSystem.getPath("/foo")));
        FileSystemUtilsTest.assertPath("/foo", FileSystemUtils.removeExtension(fileSystem.getPath("/foo.")));
    }

    @Test
    public void testReplaceExtension_Path() throws Exception {
        FileSystemUtilsTest.assertPath("/foo/bar.baz", FileSystemUtils.replaceExtension(fileSystem.getPath("/foo/bar"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo/bar.baz", FileSystemUtils.replaceExtension(fileSystem.getPath("/foo/bar.cc"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(fileSystem.getPath("/foo/"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(fileSystem.getPath("/foo.cc/"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(fileSystem.getPath("/foo"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(fileSystem.getPath("/foo.cc"), ".baz"));
        FileSystemUtilsTest.assertPath("/.baz", FileSystemUtils.replaceExtension(fileSystem.getPath("/.cc"), ".baz"));
        assertThat(FileSystemUtils.replaceExtension(fileSystem.getPath("/"), ".baz")).isNull();
    }

    @Test
    public void testReplaceExtension_PathFragment() throws Exception {
        FileSystemUtilsTest.assertPath("foo/bar.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo/bar"), ".baz"));
        FileSystemUtilsTest.assertPath("foo/bar.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo/bar.cc"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo/bar.baz", FileSystemUtils.replaceExtension(PathFragment.create("/foo/bar"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo/bar.baz", FileSystemUtils.replaceExtension(PathFragment.create("/foo/bar.cc"), ".baz"));
        FileSystemUtilsTest.assertPath("foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo/"), ".baz"));
        FileSystemUtilsTest.assertPath("foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo.cc/"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("/foo/"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("/foo.cc/"), ".baz"));
        FileSystemUtilsTest.assertPath("foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo"), ".baz"));
        FileSystemUtilsTest.assertPath("foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo.cc"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("/foo"), ".baz"));
        FileSystemUtilsTest.assertPath("/foo.baz", FileSystemUtils.replaceExtension(PathFragment.create("/foo.cc"), ".baz"));
        FileSystemUtilsTest.assertPath(".baz", FileSystemUtils.replaceExtension(PathFragment.create(".cc"), ".baz"));
        assertThat(FileSystemUtils.replaceExtension(PathFragment.create("/"), ".baz")).isNull();
        assertThat(FileSystemUtils.replaceExtension(PathFragment.create(""), ".baz")).isNull();
        FileSystemUtilsTest.assertPath("foo/bar.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo/bar.pony"), ".baz", ".pony"));
        FileSystemUtilsTest.assertPath("foo/bar.baz", FileSystemUtils.replaceExtension(PathFragment.create("foo/bar"), ".baz", ""));
        assertThat(FileSystemUtils.replaceExtension(PathFragment.create(""), ".baz", ".pony")).isNull();
        assertThat(FileSystemUtils.replaceExtension(PathFragment.create("foo/bar.pony"), ".baz", ".unicorn")).isNull();
    }

    @Test
    public void testAppendWithoutExtension() throws Exception {
        FileSystemUtilsTest.assertPath("libfoo-src.jar", FileSystemUtils.appendWithoutExtension(PathFragment.create("libfoo.jar"), "-src"));
        FileSystemUtilsTest.assertPath("foo/libfoo-src.jar", FileSystemUtils.appendWithoutExtension(PathFragment.create("foo/libfoo.jar"), "-src"));
        FileSystemUtilsTest.assertPath("java/com/google/foo/libfoo-src.jar", FileSystemUtils.appendWithoutExtension(PathFragment.create("java/com/google/foo/libfoo.jar"), "-src"));
        FileSystemUtilsTest.assertPath("libfoo.bar-src.jar", FileSystemUtils.appendWithoutExtension(PathFragment.create("libfoo.bar.jar"), "-src"));
        FileSystemUtilsTest.assertPath("libfoo-src", FileSystemUtils.appendWithoutExtension(PathFragment.create("libfoo"), "-src"));
        FileSystemUtilsTest.assertPath("libfoo-src.jar", FileSystemUtils.appendWithoutExtension(PathFragment.create("libfoo.jar/"), "-src"));
        FileSystemUtilsTest.assertPath("libfoo.src.jar", FileSystemUtils.appendWithoutExtension(PathFragment.create("libfoo.jar"), ".src"));
        assertThat(FileSystemUtils.appendWithoutExtension(PathFragment.create("/"), "-src")).isNull();
        assertThat(FileSystemUtils.appendWithoutExtension(PathFragment.create(""), "-src")).isNull();
    }

    @Test
    public void testGetWorkingDirectory() {
        String userDir = System.getProperty("user.dir");
        assertThat(fileSystem.getPath(System.getProperty("user.dir", "/"))).isEqualTo(FileSystemUtils.getWorkingDirectory(fileSystem));
        System.setProperty("user.dir", "/blah/blah/blah");
        assertThat(fileSystem.getPath("/blah/blah/blah")).isEqualTo(FileSystemUtils.getWorkingDirectory(fileSystem));
        System.setProperty("user.dir", userDir);
    }

    @Test
    public void testResolveRelativeToFilesystemWorkingDir() {
        PathFragment relativePath = PathFragment.create("relative/path");
        assertThat(workingDir.getRelative(relativePath)).isEqualTo(workingDir.getRelative(relativePath));
        PathFragment absolutePath = PathFragment.create("/absolute/path");
        assertThat(workingDir.getRelative(absolutePath)).isEqualTo(fileSystem.getPath(absolutePath));
    }

    @Test
    public void testTouchFileCreatesFile() throws IOException {
        createTestDirectoryTree();
        Path nonExistingFile = fileSystem.getPath("/previously-non-existing");
        assertThat(nonExistingFile.exists()).isFalse();
        FileSystemUtils.touchFile(nonExistingFile);
        assertThat(nonExistingFile.exists()).isTrue();
    }

    @Test
    public void testTouchFileAdjustsFileTime() throws IOException {
        createTestDirectoryTree();
        Path testFile = file4;
        long oldTime = testFile.getLastModifiedTime();
        testFile.setLastModifiedTime(42);
        FileSystemUtils.touchFile(testFile);
        assertThat(testFile.getLastModifiedTime()).isAtLeast(oldTime);
    }

    @Test
    public void testCopyFile() throws IOException {
        createTestDirectoryTree();
        Path originalFile = file1;
        byte[] content = new byte[]{ 'a', 'b', 'c', 23, 42 };
        FileSystemUtils.writeContent(originalFile, content);
        Path copyTarget = file2;
        FileSystemUtils.copyFile(originalFile, copyTarget);
        assertThat(FileSystemUtils.readContent(copyTarget)).isEqualTo(content);
    }

    @Test
    public void testMoveFile() throws IOException {
        createTestDirectoryTree();
        Path originalFile = file1;
        byte[] content = new byte[]{ 'a', 'b', 'c', 23, 42 };
        FileSystemUtils.writeContent(originalFile, content);
        Path moveTarget = file2;
        assertThat(FileSystemUtils.moveFile(originalFile, moveTarget)).isEqualTo(FILE_MOVED);
        assertThat(FileSystemUtils.readContent(moveTarget)).isEqualTo(content);
        assertThat(originalFile.exists()).isFalse();
    }

    @Test
    public void testMoveFileAcrossDevices() throws Exception {
        class MultipleDeviceFS extends InMemoryFileSystem {
            @Override
            public void renameTo(Path source, Path target) throws IOException {
                if (!(source.startsWith(target.asFragment().subFragment(0, 1)))) {
                    throw new IOException("EXDEV");
                }
                super.renameTo(source, target);
            }
        }
        FileSystem fs = new MultipleDeviceFS();
        Path dev1 = fs.getPath("/fs1");
        dev1.createDirectory();
        Path dev2 = fs.getPath("/fs2");
        dev2.createDirectory();
        Path source = dev1.getChild("source");
        Path target = dev2.getChild("target");
        FileSystemUtils.writeContent(source, StandardCharsets.UTF_8, "hello, world");
        source.setLastModifiedTime(142);
        assertThat(FileSystemUtils.moveFile(source, target)).isEqualTo(FILE_COPIED);
        assertThat(source.exists(NOFOLLOW)).isFalse();
        assertThat(target.isFile(NOFOLLOW)).isTrue();
        assertThat(FileSystemUtils.readContent(target, StandardCharsets.UTF_8)).isEqualTo("hello, world");
        assertThat(target.getLastModifiedTime()).isEqualTo(142);
        source.createSymbolicLink(PathFragment.create("link-target"));
        assertThat(FileSystemUtils.moveFile(source, target)).isEqualTo(FILE_COPIED);
        assertThat(source.exists(NOFOLLOW)).isFalse();
        assertThat(target.isSymbolicLink()).isTrue();
        assertThat(target.readSymbolicLink()).isEqualTo(PathFragment.create("link-target"));
    }

    @Test
    public void testReadContentWithLimit() throws IOException {
        createTestDirectoryTree();
        String str = "this is a test of readContentWithLimit method";
        FileSystemUtils.writeContent(file1, StandardCharsets.ISO_8859_1, str);
        assertThat(readStringFromFile(file1, 0)).isEmpty();
        assertThat(str.substring(0, 10)).isEqualTo(readStringFromFile(file1, 10));
        assertThat(str).isEqualTo(readStringFromFile(file1, 1000000));
    }

    @Test
    public void testAppend() throws IOException {
        createTestDirectoryTree();
        FileSystemUtils.writeIsoLatin1(file1, "nobody says ");
        FileSystemUtils.writeIsoLatin1(file1, "mary had");
        FileSystemUtils.appendIsoLatin1(file1, "a little lamb");
        assertThat(new String(FileSystemUtils.readContentAsLatin1(file1))).isEqualTo("mary had\na little lamb\n");
    }

    @Test
    public void testCopyFileAttributes() throws IOException {
        createTestDirectoryTree();
        Path originalFile = file1;
        byte[] content = new byte[]{ 'a', 'b', 'c', 23, 42 };
        FileSystemUtils.writeContent(originalFile, content);
        file1.setLastModifiedTime(12345L);
        file1.setWritable(false);
        file1.setExecutable(false);
        Path copyTarget = file2;
        FileSystemUtils.copyFile(originalFile, copyTarget);
        assertThat(file2.getLastModifiedTime()).isEqualTo(12345L);
        assertThat(file2.isExecutable()).isFalse();
        assertThat(file2.isWritable()).isFalse();
        file1.setWritable(true);
        file1.setExecutable(true);
        FileSystemUtils.copyFile(originalFile, copyTarget);
        assertThat(file2.getLastModifiedTime()).isEqualTo(12345L);
        assertThat(file2.isExecutable()).isTrue();
        assertThat(file2.isWritable()).isTrue();
    }

    @Test
    public void testCopyFileThrowsExceptionIfTargetCantBeDeleted() throws IOException {
        createTestDirectoryTree();
        Path originalFile = file1;
        byte[] content = new byte[]{ 'a', 'b', 'c', 23, 42 };
        FileSystemUtils.writeContent(originalFile, content);
        try {
            FileSystemUtils.copyFile(originalFile, aDir);
            Assert.fail();
        } catch (IOException ex) {
            assertThat(ex).hasMessage((("error copying file: couldn't delete destination: " + (aDir)) + " (Directory not empty)"));
        }
    }

    @Test
    public void testCopyTool() throws IOException {
        createTestDirectoryTree();
        Path originalFile = file1;
        byte[] content = new byte[]{ 'a', 'b', 'c', 23, 42 };
        FileSystemUtils.writeContent(originalFile, content);
        Path copyTarget = FileSystemUtils.copyTool(topDir.getRelative("file-1"), aDir.getRelative("file-1"));
        assertThat(FileSystemUtils.readContent(copyTarget)).isEqualTo(content);
        assertThat(copyTarget.isWritable()).isEqualTo(file1.isWritable());
        assertThat(copyTarget.isExecutable()).isEqualTo(file1.isExecutable());
        assertThat(copyTarget.getLastModifiedTime()).isEqualTo(file1.getLastModifiedTime());
    }

    @Test
    public void testCopyTreesBelow() throws IOException {
        createTestDirectoryTree();
        Path toPath = fileSystem.getPath("/copy-here");
        toPath.createDirectory();
        FileSystemUtils.copyTreesBelow(topDir, toPath, FOLLOW);
        checkTestDirectoryTreesBelow(toPath);
    }

    @Test
    public void testCopyTreesBelowWithOverriding() throws IOException {
        createTestDirectoryTree();
        Path toPath = fileSystem.getPath("/copy-here");
        toPath.createDirectory();
        toPath.getChild("file-2");
        FileSystemUtils.copyTreesBelow(topDir, toPath, FOLLOW);
        checkTestDirectoryTreesBelow(toPath);
    }

    @Test
    public void testCopyTreesBelowToSubtree() throws IOException {
        createTestDirectoryTree();
        try {
            FileSystemUtils.copyTreesBelow(topDir, aDir, FOLLOW);
            Assert.fail("Should not be able to copy a directory to a subdir");
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("/top-dir/a-dir is a subdirectory of /top-dir");
        }
    }

    @Test
    public void testCopyFileAsDirectoryTree() throws IOException {
        createTestDirectoryTree();
        try {
            FileSystemUtils.copyTreesBelow(file1, aDir, FOLLOW);
            Assert.fail("Should not be able to copy a file with copyDirectory method");
        } catch (IOException expected) {
            assertThat(expected).hasMessage("/top-dir/file-1 (Not a directory)");
        }
    }

    @Test
    public void testCopyTreesBelowToFile() throws IOException {
        createTestDirectoryTree();
        Path copyDir = fileSystem.getPath("/my-dir");
        Path copySubDir = fileSystem.getPath("/my-dir/subdir");
        FileSystemUtils.createDirectoryAndParents(copySubDir);
        try {
            FileSystemUtils.copyTreesBelow(copyDir, file4, FOLLOW);
            Assert.fail("Should not be able to copy a directory to a file");
        } catch (IOException expected) {
            assertThat(expected).hasMessage("/file-4 (Not a directory)");
        }
    }

    @Test
    public void testCopyTreesBelowFromUnexistingDir() throws IOException {
        createTestDirectoryTree();
        try {
            Path unexistingDir = fileSystem.getPath("/unexisting-dir");
            FileSystemUtils.copyTreesBelow(unexistingDir, aDir, FOLLOW);
            Assert.fail("Should not be able to copy from an unexisting path");
        } catch (FileNotFoundException expected) {
            assertThat(expected).hasMessage("/unexisting-dir (No such file or directory)");
        }
    }

    @Test
    public void testCopyTreesBelowNoFollowSymlinks() throws IOException {
        createTestDirectoryTree();
        PathFragment relative1 = file1.relativeTo(topDir);
        topDir.getRelative("relative-link").createSymbolicLink(relative1);
        PathFragment relativeInner = PathFragment.create("..").getRelative(relative1);
        bDir.getRelative("relative-inner-link").createSymbolicLink(relativeInner);
        PathFragment rootDirectory = PathFragment.create("/");
        topDir.getRelative("absolute-link").createSymbolicLink(rootDirectory);
        Path toPath = fileSystem.getPath("/copy-here");
        toPath.createDirectory();
        FileSystemUtils.copyTreesBelow(topDir, toPath, NOFOLLOW);
        Path copiedInnerDir = checkTestDirectoryTreesBelowExceptSymlinks(toPath);
        Path copiedLink1 = copiedInnerDir.getChild("link-1");
        assertThat(copiedLink1.exists()).isTrue();
        assertThat(copiedLink1.isSymbolicLink()).isTrue();
        assertThat(copiedLink1.readSymbolicLink()).isEqualTo(file4.asFragment());
        Path copiedDirLink = copiedInnerDir.getChild("dir-link");
        assertThat(copiedDirLink.exists()).isTrue();
        assertThat(copiedDirLink.isDirectory()).isTrue();
        assertThat(copiedDirLink.readSymbolicLink()).isEqualTo(bDir.asFragment());
        assertThat(toPath.getRelative("relative-link").readSymbolicLink()).isEqualTo(relative1);
        assertThat(toPath.getRelative(bDir.relativeTo(topDir)).getRelative("relative-inner-link").readSymbolicLink()).isEqualTo(relativeInner);
        assertThat(toPath.getRelative("absolute-link").readSymbolicLink()).isEqualTo(rootDirectory);
    }

    @Test
    public void testTraverseTree() throws IOException {
        createTestDirectoryTree();
        Collection<Path> paths = FileSystemUtils.traverseTree(topDir, new Predicate<Path>() {
            @Override
            public boolean apply(Path p) {
                return !(p.getPathString().contains("a-dir"));
            }
        });
        assertThat(paths).containsExactly(file1, file2, bDir, file5);
    }

    @Test
    public void testTraverseTreeDeep() throws IOException {
        createTestDirectoryTree();
        Collection<Path> paths = FileSystemUtils.traverseTree(topDir, Predicates.alwaysTrue());
        assertThat(paths).containsExactly(aDir, file3, innerDir, link1, file1, file2, dirLink, bDir, file5);
    }

    @Test
    public void testTraverseTreeLinkDir() throws IOException {
        // Use a new little tree for this test:
        // top-dir/
        // dir-link2 => linked-dir
        // linked-dir/
        // file
        topDir = fileSystem.getPath("/top-dir");
        Path dirLink2 = fileSystem.getPath("/top-dir/dir-link2");
        Path linkedDir = fileSystem.getPath("/linked-dir");
        Path linkedDirFile = fileSystem.getPath("/top-dir/dir-link2/file");
        topDir.createDirectory();
        linkedDir.createDirectory();
        dirLink2.createSymbolicLink(linkedDir);// simple symlink

        FileSystemUtils.createEmptyFile(linkedDirFile);// created through the link

        // traverseTree doesn't follow links:
        Collection<Path> paths = FileSystemUtils.traverseTree(topDir, Predicates.alwaysTrue());
        assertThat(paths).containsExactly(dirLink2);
        paths = FileSystemUtils.traverseTree(linkedDir, Predicates.alwaysTrue());
        assertThat(paths).containsExactly(fileSystem.getPath("/linked-dir/file"));
    }

    @Test
    public void testDeleteTreeCommandDeletesTree() throws IOException {
        createTestDirectoryTree();
        Path toDelete = topDir;
        FileSystemUtils.deleteTree(toDelete);
        assertThat(file4.exists()).isTrue();
        assertThat(topDir.exists()).isFalse();
        assertThat(file1.exists()).isFalse();
        assertThat(file2.exists()).isFalse();
        assertThat(aDir.exists()).isFalse();
        assertThat(file3.exists()).isFalse();
    }

    @Test
    public void testDeleteTreeCommandsDeletesUnreadableDirectories() throws IOException {
        createTestDirectoryTree();
        Path toDelete = topDir;
        try {
            aDir.setReadable(false);
        } catch (UnsupportedOperationException e) {
            // For file systems that do not support setting readable attribute to
            // false, this test is simply skipped.
            return;
        }
        FileSystemUtils.deleteTree(toDelete);
        assertThat(topDir.exists()).isFalse();
        assertThat(aDir.exists()).isFalse();
    }

    @Test
    public void testDeleteTreeCommandDoesNotFollowLinksOut() throws IOException {
        createTestDirectoryTree();
        Path toDelete = topDir;
        Path outboundLink = fileSystem.getPath("/top-dir/outbound-link");
        outboundLink.createSymbolicLink(file4);
        FileSystemUtils.deleteTree(toDelete);
        assertThat(file4.exists()).isTrue();
        assertThat(topDir.exists()).isFalse();
        assertThat(file1.exists()).isFalse();
        assertThat(file2.exists()).isFalse();
        assertThat(aDir.exists()).isFalse();
        assertThat(file3.exists()).isFalse();
    }

    @Test
    public void testWriteIsoLatin1() throws Exception {
        Path file = fileSystem.getPath("/does/not/exist/yet.txt");
        FileSystemUtils.writeIsoLatin1(file, "Line 1", "Line 2", "Line 3");
        String expected = "Line 1\nLine 2\nLine 3\n";
        String actual = new String(FileSystemUtils.readContentAsLatin1(file));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testWriteLinesAs() throws Exception {
        Path file = fileSystem.getPath("/does/not/exist/yet.txt");
        FileSystemUtils.writeLinesAs(file, StandardCharsets.UTF_8, "\u00f6");// an oe umlaut

        byte[] expected = new byte[]{ ((byte) (195)), ((byte) (182)), 10 };// "\u00F6\n";

        byte[] actual = FileSystemUtils.readContent(file);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testUpdateContent() throws Exception {
        Path file = fileSystem.getPath("/test.txt");
        clock.advanceMillis(1000);
        byte[] content = new byte[]{ 'a', 'b', 'c', 23, 42 };
        FileSystemUtils.maybeUpdateContent(file, content);
        byte[] actual = FileSystemUtils.readContent(file);
        assertThat(actual).isEqualTo(content);
        FileStatus stat = file.stat();
        assertThat(stat.getLastChangeTime()).isEqualTo(1000);
        assertThat(stat.getLastModifiedTime()).isEqualTo(1000);
        clock.advanceMillis(1000);
        // Update with same contents; should not write anything.
        FileSystemUtils.maybeUpdateContent(file, content);
        assertThat(actual).isEqualTo(content);
        stat = file.stat();
        assertThat(stat.getLastChangeTime()).isEqualTo(1000);
        assertThat(stat.getLastModifiedTime()).isEqualTo(1000);
        clock.advanceMillis(1000);
        // Update with different contents; file should be rewritten.
        content[0] = 'b';
        file.chmod(256);// Protect the file to ensure we can rewrite it.

        FileSystemUtils.maybeUpdateContent(file, content);
        actual = FileSystemUtils.readContent(file);
        assertThat(actual).isEqualTo(content);
        stat = file.stat();
        assertThat(stat.getLastChangeTime()).isEqualTo(3000);
        assertThat(stat.getLastModifiedTime()).isEqualTo(3000);
    }

    @Test
    public void testGetFileSystem() throws Exception {
        Path mountTable = fileSystem.getPath("/proc/mounts");
        FileSystemUtils.writeIsoLatin1(mountTable, "/dev/sda1 / ext2 blah 0 0", "/dev/mapper/_dev_sda6 /usr/local/google ext3 blah 0 0", "devshm /dev/shm tmpfs blah 0 0", "/dev/fuse /fuse/mnt fuse blah 0 0", "mtvhome22.nfs:/vol/mtvhome22/johndoe /home/johndoe nfs blah 0 0", "/dev/foo /foo dummy_foo blah 0 0", "/dev/foobar /foobar dummy_foobar blah 0 0", "proc proc proc rw,noexec,nosuid,nodev 0 0");
        Path path = fileSystem.getPath("/usr/local/google/_blaze");
        FileSystemUtils.createDirectoryAndParents(path);
        assertThat(FileSystemUtils.getFileSystem(path)).isEqualTo("ext3");
        // Should match the root "/"
        path = fileSystem.getPath("/usr/local/tmp");
        FileSystemUtils.createDirectoryAndParents(path);
        assertThat(FileSystemUtils.getFileSystem(path)).isEqualTo("ext2");
        // Make sure we don't consider /foobar matches /foo
        path = fileSystem.getPath("/foo");
        FileSystemUtils.createDirectoryAndParents(path);
        assertThat(FileSystemUtils.getFileSystem(path)).isEqualTo("dummy_foo");
        path = fileSystem.getPath("/foobar");
        FileSystemUtils.createDirectoryAndParents(path);
        assertThat(FileSystemUtils.getFileSystem(path)).isEqualTo("dummy_foobar");
        path = fileSystem.getPath("/dev/shm/blaze");
        FileSystemUtils.createDirectoryAndParents(path);
        assertThat(FileSystemUtils.getFileSystem(path)).isEqualTo("tmpfs");
        Path fusePath = fileSystem.getPath("/fuse/mnt/tmp");
        FileSystemUtils.createDirectoryAndParents(fusePath);
        assertThat(FileSystemUtils.getFileSystem(fusePath)).isEqualTo("fuse");
        // Create a symlink and make sure it gives the file system of the symlink target.
        path = fileSystem.getPath("/usr/local/google/_blaze/out");
        path.createSymbolicLink(fusePath);
        assertThat(FileSystemUtils.getFileSystem(path)).isEqualTo("fuse");
        // Non existent path should return "unknown"
        path = fileSystem.getPath("/does/not/exist");
        assertThat(FileSystemUtils.getFileSystem(path)).isEqualTo("unknown");
    }

    @Test
    public void testStartsWithAnySuccess() throws Exception {
        PathFragment a = PathFragment.create("a");
        assertThat(FileSystemUtils.startsWithAny(a, Arrays.asList(PathFragment.create("b"), PathFragment.create("a")))).isTrue();
    }

    @Test
    public void testStartsWithAnyNotFound() throws Exception {
        PathFragment a = PathFragment.create("a");
        assertThat(FileSystemUtils.startsWithAny(a, Arrays.asList(PathFragment.create("b"), PathFragment.create("c")))).isFalse();
    }

    @Test
    public void testIterateLines() throws Exception {
        Path file = fileSystem.getPath("/test.txt");
        FileSystemUtils.writeContent(file, StandardCharsets.ISO_8859_1, "a\nb");
        assertThat(Lists.newArrayList(FileSystemUtils.iterateLinesAsLatin1(file))).isEqualTo(Arrays.asList("a", "b"));
        FileSystemUtils.writeContent(file, StandardCharsets.ISO_8859_1, "a\rb");
        assertThat(Lists.newArrayList(FileSystemUtils.iterateLinesAsLatin1(file))).isEqualTo(Arrays.asList("a", "b"));
        FileSystemUtils.writeContent(file, StandardCharsets.ISO_8859_1, "a\r\nb");
        assertThat(Lists.newArrayList(FileSystemUtils.iterateLinesAsLatin1(file))).isEqualTo(Arrays.asList("a", "b"));
    }

    @Test
    public void testEnsureSymbolicLinkDoesNotMakeUnnecessaryChanges() throws Exception {
        PathFragment target = PathFragment.create("/b");
        Path file = fileSystem.getPath("/a");
        file.createSymbolicLink(target);
        long prevTimeMillis = clock.currentTimeMillis();
        clock.advanceMillis(1000);
        FileSystemUtils.ensureSymbolicLink(file, target);
        long timestamp = file.getLastModifiedTime(NOFOLLOW);
        assertThat(timestamp).isEqualTo(prevTimeMillis);
    }

    @Test
    public void testCreateHardLinkForFile_Success() throws Exception {
        /* Original file exists and link file does not exist */
        Path originalPath = workingDir.getRelative("original");
        Path linkPath = workingDir.getRelative("link");
        FileSystemUtils.createEmptyFile(originalPath);
        FileSystemUtils.createHardLink(linkPath, originalPath);
        assertThat(originalPath.exists()).isTrue();
        assertThat(linkPath.exists()).isTrue();
        assertThat(fileSystem.stat(linkPath, false).getNodeId()).isEqualTo(fileSystem.stat(originalPath, false).getNodeId());
    }

    @Test
    public void testCreateHardLinkForEmptyDirectory_Success() throws Exception {
        Path originalDir = workingDir.getRelative("originalDir");
        Path linkPath = workingDir.getRelative("link");
        FileSystemUtils.createDirectoryAndParents(originalDir);
        /* Original directory is empty, no link to be created. */
        FileSystemUtils.createHardLink(linkPath, originalDir);
        assertThat(linkPath.exists()).isFalse();
    }

    @Test
    public void testCreateHardLinkForNonEmptyDirectory_Success() throws Exception {
        /* Test when original path is a directory */
        Path originalDir = workingDir.getRelative("originalDir");
        Path linkPath = workingDir.getRelative("link");
        Path originalPath1 = originalDir.getRelative("original1");
        Path originalPath2 = originalDir.getRelative("original2");
        Path originalPath3 = originalDir.getRelative("original3");
        Path linkPath1 = linkPath.getRelative("original1");
        Path linkPath2 = linkPath.getRelative("original2");
        Path linkPath3 = linkPath.getRelative("original3");
        FileSystemUtils.createDirectoryAndParents(originalDir);
        FileSystemUtils.createEmptyFile(originalPath1);
        FileSystemUtils.createEmptyFile(originalPath2);
        FileSystemUtils.createEmptyFile(originalPath3);
        /* Three link files created under linkPath */
        FileSystemUtils.createHardLink(linkPath, originalDir);
        assertThat(linkPath.exists()).isTrue();
        assertThat(linkPath1.exists()).isTrue();
        assertThat(linkPath2.exists()).isTrue();
        assertThat(linkPath3.exists()).isTrue();
        assertThat(fileSystem.stat(linkPath1, false).getNodeId()).isEqualTo(fileSystem.stat(originalPath1, false).getNodeId());
        assertThat(fileSystem.stat(linkPath2, false).getNodeId()).isEqualTo(fileSystem.stat(originalPath2, false).getNodeId());
        assertThat(fileSystem.stat(linkPath3, false).getNodeId()).isEqualTo(fileSystem.stat(originalPath3, false).getNodeId());
    }
}

