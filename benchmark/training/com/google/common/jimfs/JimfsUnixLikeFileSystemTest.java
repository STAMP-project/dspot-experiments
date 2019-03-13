/**
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.jimfs;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Bytes;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedDirectoryStreamException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.NotLinkException;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests an in-memory file system through the public APIs in {@link Files}, etc. This also acts as
 * the tests for {@code FileSystemView}, as each public API method is (mostly) implemented by a
 * method in {@code FileSystemView}.
 *
 * <p>These tests uses a Unix-like file system, but most of what they test applies to any file
 * system configuration.
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class JimfsUnixLikeFileSystemTest extends AbstractJimfsIntegrationTest {
    private static final Configuration UNIX_CONFIGURATION = // 256 MB
    // 1 GB
    Configuration.unix().toBuilder().setAttributeViews("basic", "owner", "posix", "unix").setMaxSize(((1024 * 1024) * 1024)).setMaxCacheSize(((256 * 1024) * 1024)).build();

    @Test
    public void testFileSystem() {
        assertThat(fs.getSeparator()).isEqualTo("/");
        assertThat(fs.getRootDirectories()).containsExactlyElementsIn(ImmutableSet.of(path("/"))).inOrder();
        assertThat(fs.isOpen()).isTrue();
        assertThat(fs.isReadOnly()).isFalse();
        assertThat(fs.supportedFileAttributeViews()).containsExactly("basic", "owner", "posix", "unix");
        assertThat(fs.provider()).isInstanceOf(JimfsFileSystemProvider.class);
    }

    @Test
    public void testFileStore() throws IOException {
        FileStore fileStore = Iterables.getOnlyElement(fs.getFileStores());
        assertThat(fileStore.name()).isEqualTo("jimfs");
        assertThat(fileStore.type()).isEqualTo("jimfs");
        assertThat(fileStore.isReadOnly()).isFalse();
        long totalSpace = (1024 * 1024) * 1024;// 1 GB

        assertThat(fileStore.getTotalSpace()).isEqualTo(totalSpace);
        assertThat(fileStore.getUnallocatedSpace()).isEqualTo(totalSpace);
        assertThat(fileStore.getUsableSpace()).isEqualTo(totalSpace);
        Files.write(fs.getPath("/foo"), new byte[10000]);
        assertThat(fileStore.getTotalSpace()).isEqualTo(totalSpace);
        // We wrote 10000 bytes, but since the file system allocates fixed size blocks, more than 10k
        // bytes may have been allocated. As such, the unallocated space after the write can be at most
        // maxUnallocatedSpace.
        assertThat(((fileStore.getUnallocatedSpace()) <= (totalSpace - 10000))).isTrue();
        // Usable space is at most unallocated space. (In this case, it's currently exactly unallocated
        // space, but that's not required.)
        assertThat(((fileStore.getUsableSpace()) <= (fileStore.getUnallocatedSpace()))).isTrue();
        Files.delete(fs.getPath("/foo"));
        assertThat(fileStore.getTotalSpace()).isEqualTo(totalSpace);
        assertThat(fileStore.getUnallocatedSpace()).isEqualTo(totalSpace);
        assertThat(fileStore.getUsableSpace()).isEqualTo(totalSpace);
    }

    @Test
    public void testPaths() {
        assertThatPath("/").isAbsolute().and().hasRootComponent("/").and().hasNoNameComponents();
        assertThatPath("foo").isRelative().and().hasNameComponents("foo");
        assertThatPath("foo/bar").isRelative().and().hasNameComponents("foo", "bar");
        assertThatPath("/foo/bar/baz").isAbsolute().and().hasRootComponent("/").and().hasNameComponents("foo", "bar", "baz");
    }

    @Test
    public void testPaths_equalityIsCaseSensitive() {
        isNotEqualTo(path("FOO"));
    }

    @Test
    public void testPaths_areSortedCaseSensitive() {
        Path p1 = path("a");
        Path p2 = path("B");
        Path p3 = path("c");
        Path p4 = path("D");
        assertThat(Ordering.natural().immutableSortedCopy(Arrays.asList(p3, p4, p1, p2))).isEqualTo(ImmutableList.of(p2, p4, p1, p3));
        // would be p1, p2, p3, p4 if sorting were case insensitive
    }

    @Test
    public void testPaths_resolve() {
        AbstractJimfsIntegrationTest.assertThatPath(path("/").resolve("foo/bar")).isAbsolute().and().hasRootComponent("/").and().hasNameComponents("foo", "bar");
        AbstractJimfsIntegrationTest.assertThatPath(path("foo/bar").resolveSibling("baz")).isRelative().and().hasNameComponents("foo", "baz");
        AbstractJimfsIntegrationTest.assertThatPath(path("foo/bar").resolve("/one/two")).isAbsolute().and().hasRootComponent("/").and().hasNameComponents("one", "two");
    }

    @Test
    public void testPaths_normalize() {
        AbstractJimfsIntegrationTest.assertThatPath(path("foo/bar/..").normalize()).isRelative().and().hasNameComponents("foo");
        AbstractJimfsIntegrationTest.assertThatPath(path("foo/./bar/../baz/test/./../stuff").normalize()).isRelative().and().hasNameComponents("foo", "baz", "stuff");
        AbstractJimfsIntegrationTest.assertThatPath(path("../../foo/./bar").normalize()).isRelative().and().hasNameComponents("..", "..", "foo", "bar");
        AbstractJimfsIntegrationTest.assertThatPath(path("foo/../../bar").normalize()).isRelative().and().hasNameComponents("..", "bar");
        AbstractJimfsIntegrationTest.assertThatPath(path(".././..").normalize()).isRelative().and().hasNameComponents("..", "..");
    }

    @Test
    public void testPaths_relativize() {
        AbstractJimfsIntegrationTest.assertThatPath(path("/foo/bar").relativize(path("/foo/bar/baz"))).isRelative().and().hasNameComponents("baz");
        AbstractJimfsIntegrationTest.assertThatPath(path("/foo/bar/baz").relativize(path("/foo/bar"))).isRelative().and().hasNameComponents("..");
        AbstractJimfsIntegrationTest.assertThatPath(path("/foo/bar/baz").relativize(path("/foo/baz/bar"))).isRelative().and().hasNameComponents("..", "..", "baz", "bar");
        AbstractJimfsIntegrationTest.assertThatPath(path("foo/bar").relativize(path("foo"))).isRelative().and().hasNameComponents("..");
        AbstractJimfsIntegrationTest.assertThatPath(path("foo").relativize(path("foo/bar"))).isRelative().and().hasNameComponents("bar");
        try {
            Path unused = path("/foo/bar").relativize(path("bar"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            Path unused = path("bar").relativize(path("/foo/bar"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testPaths_startsWith_endsWith() {
        assertThat(path("/foo/bar").startsWith("/")).isTrue();
        assertThat(path("/foo/bar").startsWith("/foo")).isTrue();
        assertThat(path("/foo/bar").startsWith("/foo/bar")).isTrue();
        assertThat(path("/foo/bar").endsWith("bar")).isTrue();
        assertThat(path("/foo/bar").endsWith("foo/bar")).isTrue();
        assertThat(path("/foo/bar").endsWith("/foo/bar")).isTrue();
        assertThat(path("/foo/bar").endsWith("/foo")).isFalse();
        assertThat(path("/foo/bar").startsWith("foo/bar")).isFalse();
    }

    @Test
    public void testPaths_toAbsolutePath() {
        isEqualTo(path("/foo/bar"));
        isEqualTo(path("/work/foo/bar"));
    }

    @Test
    public void testPaths_toRealPath() throws IOException {
        Files.createDirectories(path("/foo/bar"));
        Files.createSymbolicLink(path("/link"), path("/"));
        isEqualTo(path("/foo/bar"));
        isEqualTo(path("/work"));
        isEqualTo(path("/work"));
        isEqualTo(path("/"));
        isEqualTo(path("/"));
        isEqualTo(path("/"));
        isEqualTo(path("/"));
    }

    @Test
    public void testPaths_toUri() {
        assertThat(path("/").toUri()).isEqualTo(URI.create("jimfs://unix/"));
        assertThat(path("/foo").toUri()).isEqualTo(URI.create("jimfs://unix/foo"));
        assertThat(path("/foo/bar").toUri()).isEqualTo(URI.create("jimfs://unix/foo/bar"));
        assertThat(path("foo").toUri()).isEqualTo(URI.create("jimfs://unix/work/foo"));
        assertThat(path("foo/bar").toUri()).isEqualTo(URI.create("jimfs://unix/work/foo/bar"));
        assertThat(path("").toUri()).isEqualTo(URI.create("jimfs://unix/work/"));
        assertThat(path("./../.").toUri()).isEqualTo(URI.create("jimfs://unix/work/./.././"));
    }

    @Test
    public void testPaths_getFromUri() {
        isEqualTo(path("/"));
        isEqualTo(path("/foo"));
        isEqualTo(path("/foo bar"));
        isEqualTo(path("/foo/./bar"));
        isEqualTo(path("/foo/bar"));
    }

    @Test
    public void testPathMatchers_regex() {
        assertThatPath("bar").matches("regex:.*");
        assertThatPath("bar").matches("regex:bar");
        assertThatPath("bar").matches("regex:[a-z]+");
        assertThatPath("/foo/bar").matches("regex:/.*");
        assertThatPath("/foo/bar").matches("regex:/.*/bar");
    }

    @Test
    public void testPathMatchers_glob() {
        assertThatPath("bar").matches("glob:bar");
        assertThatPath("bar").matches("glob:*");
        assertThatPath("/foo").doesNotMatch("glob:*");
        assertThatPath("/foo/bar").doesNotMatch("glob:*");
        assertThatPath("/foo/bar").matches("glob:**");
        assertThatPath("/foo/bar").matches("glob:/**");
        assertThatPath("foo/bar").doesNotMatch("glob:/**");
        assertThatPath("/foo/bar/baz/stuff").matches("glob:/foo/**");
        assertThatPath("/foo/bar/baz/stuff").matches("glob:/**/stuff");
        assertThatPath("/foo").matches("glob:/[a-z]*");
        assertThatPath("/Foo").doesNotMatch("glob:/[a-z]*");
        assertThatPath("/foo/bar/baz/Stuff.java").matches("glob:**/*.java");
        assertThatPath("/foo/bar/baz/Stuff.java").matches("glob:**/*.{java,class}");
        assertThatPath("/foo/bar/baz/Stuff.class").matches("glob:**/*.{java,class}");
        assertThatPath("/foo/bar/baz/Stuff.java").matches("glob:**/*.*");
        try {
            fs.getPathMatcher("glob:**/*.{java,class");
            Assert.fail();
        } catch (PatternSyntaxException expected) {
        }
    }

    @Test
    public void testPathMatchers_invalid() {
        try {
            fs.getPathMatcher("glob");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            fs.getPathMatcher("foo:foo");
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
            assertThat(expected.getMessage()).contains("syntax");
        }
    }

    @Test
    public void testNewFileSystem_hasRootAndWorkingDirectory() throws IOException {
        assertThatPath("/").hasChildren("work");
        assertThatPath("/work").hasNoChildren();
    }

    @Test
    public void testCreateDirectory_absolute() throws IOException {
        Files.createDirectory(path("/test"));
        assertThatPath("/test").exists();
        assertThatPath("/").hasChildren("test", "work");
        Files.createDirectory(path("/foo"));
        Files.createDirectory(path("/foo/bar"));
        assertThatPath("/foo/bar").exists();
        assertThatPath("/foo").hasChildren("bar");
    }

    @Test
    public void testCreateFile_absolute() throws IOException {
        Files.createFile(path("/test.txt"));
        assertThatPath("/test.txt").isRegularFile();
        assertThatPath("/").hasChildren("test.txt", "work");
        Files.createDirectory(path("/foo"));
        Files.createFile(path("/foo/test.txt"));
        assertThatPath("/foo/test.txt").isRegularFile();
        assertThatPath("/foo").hasChildren("test.txt");
    }

    @Test
    public void testCreateSymbolicLink_absolute() throws IOException {
        Files.createSymbolicLink(path("/link.txt"), path("test.txt"));
        assertThatPath("/link.txt", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("test.txt");
        assertThatPath("/").hasChildren("link.txt", "work");
        Files.createDirectory(path("/foo"));
        Files.createSymbolicLink(path("/foo/link.txt"), path("test.txt"));
        assertThatPath("/foo/link.txt").noFollowLinks().isSymbolicLink().withTarget("test.txt");
        assertThatPath("/foo").hasChildren("link.txt");
    }

    @Test
    public void testCreateLink_absolute() throws IOException {
        Files.createFile(path("/test.txt"));
        Files.createLink(path("/link.txt"), path("/test.txt"));
        // don't assert that the link is the same file here, just that it was created
        // later tests check that linking works correctly
        assertThatPath("/link.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("/").hasChildren("link.txt", "test.txt", "work");
        Files.createDirectory(path("/foo"));
        Files.createLink(path("/foo/link.txt"), path("/test.txt"));
        assertThatPath("/foo/link.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("/foo").hasChildren("link.txt");
    }

    @Test
    public void testCreateDirectory_relative() throws IOException {
        Files.createDirectory(path("test"));
        assertThatPath("/work/test", LinkOption.NOFOLLOW_LINKS).isDirectory();
        assertThatPath("test", LinkOption.NOFOLLOW_LINKS).isDirectory();
        assertThatPath("/work").hasChildren("test");
        assertThatPath("test").isSameFileAs("/work/test");
        Files.createDirectory(path("foo"));
        Files.createDirectory(path("foo/bar"));
        assertThatPath("/work/foo/bar", LinkOption.NOFOLLOW_LINKS).isDirectory();
        assertThatPath("foo/bar", LinkOption.NOFOLLOW_LINKS).isDirectory();
        assertThatPath("/work/foo").hasChildren("bar");
        assertThatPath("foo").hasChildren("bar");
        assertThatPath("foo/bar").isSameFileAs("/work/foo/bar");
    }

    @Test
    public void testCreateFile_relative() throws IOException {
        Files.createFile(path("test.txt"));
        assertThatPath("/work/test.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("test.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("/work").hasChildren("test.txt");
        assertThatPath("test.txt").isSameFileAs("/work/test.txt");
        Files.createDirectory(path("foo"));
        Files.createFile(path("foo/test.txt"));
        assertThatPath("/work/foo/test.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("foo/test.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("/work/foo").hasChildren("test.txt");
        assertThatPath("foo").hasChildren("test.txt");
        assertThatPath("foo/test.txt").isSameFileAs("/work/foo/test.txt");
    }

    @Test
    public void testCreateSymbolicLink_relative() throws IOException {
        Files.createSymbolicLink(path("link.txt"), path("test.txt"));
        assertThatPath("/work/link.txt", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("test.txt");
        assertThatPath("link.txt", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("test.txt");
        assertThatPath("/work").hasChildren("link.txt");
        Files.createDirectory(path("foo"));
        Files.createSymbolicLink(path("foo/link.txt"), path("test.txt"));
        assertThatPath("/work/foo/link.txt", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("test.txt");
        assertThatPath("foo/link.txt", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("test.txt");
        assertThatPath("/work/foo").hasChildren("link.txt");
        assertThatPath("foo").hasChildren("link.txt");
    }

    @Test
    public void testCreateLink_relative() throws IOException {
        Files.createFile(path("test.txt"));
        Files.createLink(path("link.txt"), path("test.txt"));
        // don't assert that the link is the same file here, just that it was created
        // later tests check that linking works correctly
        assertThatPath("/work/link.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("link.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("/work").hasChildren("link.txt", "test.txt");
        Files.createDirectory(path("foo"));
        Files.createLink(path("foo/link.txt"), path("test.txt"));
        assertThatPath("/work/foo/link.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("foo/link.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();
        assertThatPath("foo").hasChildren("link.txt");
    }

    @Test
    public void testCreateFile_existing() throws IOException {
        Files.createFile(path("/test"));
        try {
            Files.createFile(path("/test"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/test", expected.getMessage());
        }
        try {
            Files.createDirectory(path("/test"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/test", expected.getMessage());
        }
        try {
            Files.createSymbolicLink(path("/test"), path("/foo"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/test", expected.getMessage());
        }
        Files.createFile(path("/foo"));
        try {
            Files.createLink(path("/test"), path("/foo"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/test", expected.getMessage());
        }
    }

    @Test
    public void testCreateFile_parentDoesNotExist() throws IOException {
        try {
            Files.createFile(path("/foo/test"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/foo/test", expected.getMessage());
        }
        try {
            Files.createDirectory(path("/foo/test"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/foo/test", expected.getMessage());
        }
        try {
            Files.createSymbolicLink(path("/foo/test"), path("/bar"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/foo/test", expected.getMessage());
        }
        Files.createFile(path("/bar"));
        try {
            Files.createLink(path("/foo/test"), path("/bar"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/foo/test", expected.getMessage());
        }
    }

    @Test
    public void testCreateFile_parentIsNotDirectory() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createFile(path("/foo/bar"));
        try {
            Files.createFile(path("/foo/bar/baz"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo/bar/baz");
        }
    }

    @Test
    public void testCreateFile_nonDirectoryHigherInPath() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createFile(path("/foo/bar"));
        try {
            Files.createFile(path("/foo/bar/baz/stuff"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo/bar/baz/stuff");
        }
    }

    @Test
    public void testCreateFile_parentSymlinkDoesNotExist() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createSymbolicLink(path("/foo/bar"), path("/foo/nope"));
        try {
            Files.createFile(path("/foo/bar/baz"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo/bar/baz");
        }
    }

    @Test
    public void testCreateFile_symlinkHigherInPathDoesNotExist() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createSymbolicLink(path("/foo/bar"), path("nope"));
        try {
            Files.createFile(path("/foo/bar/baz/stuff"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo/bar/baz/stuff");
        }
    }

    @Test
    public void testCreateFile_parentSymlinkDoesPointsToNonDirectory() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createFile(path("/foo/file"));
        Files.createSymbolicLink(path("/foo/bar"), path("/foo/file"));
        try {
            Files.createFile(path("/foo/bar/baz"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo/bar/baz");
        }
    }

    @Test
    public void testCreateFile_symlinkHigherInPathPointsToNonDirectory() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createFile(path("/foo/file"));
        Files.createSymbolicLink(path("/foo/bar"), path("file"));
        try {
            Files.createFile(path("/foo/bar/baz/stuff"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo/bar/baz/stuff");
        }
    }

    @Test
    public void testCreateFile_withInitialAttributes() throws IOException {
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
        FileAttribute<?> permissionsAttr = PosixFilePermissions.asFileAttribute(permissions);
        Files.createFile(path("/normal"));
        Files.createFile(path("/foo"), permissionsAttr);
        assertThatPath("/normal").attribute("posix:permissions").isNot(permissions);
        assertThatPath("/foo").attribute("posix:permissions").is(permissions);
    }

    @Test
    public void testCreateFile_withInitialAttributes_illegalInitialAttribute() throws IOException {
        try {
            Files.createFile(path("/foo"), new BasicFileAttribute<>("basic:lastModifiedTime", FileTime.fromMillis(0L)));
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        assertThatPath("/foo").doesNotExist();
        try {
            Files.createFile(path("/foo"), new BasicFileAttribute<>("basic:noSuchAttribute", "foo"));
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        assertThatPath("/foo").doesNotExist();
    }

    @Test
    public void testOpenChannel_withInitialAttributes_createNewFile() throws IOException {
        FileAttribute<Set<PosixFilePermission>> permissions = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx"));
        Files.newByteChannel(path("/foo"), ImmutableSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE), permissions).close();
        assertThatPath("/foo").isRegularFile().and().attribute("posix:permissions").is(permissions.value());
    }

    @Test
    public void testOpenChannel_withInitialAttributes_fileExists() throws IOException {
        Files.createFile(path("/foo"));
        FileAttribute<Set<PosixFilePermission>> permissions = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx"));
        Files.newByteChannel(path("/foo"), ImmutableSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE), permissions).close();
        assertThatPath("/foo").isRegularFile().and().attribute("posix:permissions").isNot(permissions.value());
    }

    @Test
    public void testCreateDirectory_withInitialAttributes() throws IOException {
        FileAttribute<Set<PosixFilePermission>> permissions = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx"));
        Files.createDirectory(path("/foo"), permissions);
        assertThatPath("/foo").isDirectory().and().attribute("posix:permissions").is(permissions.value());
        Files.createDirectory(path("/normal"));
        assertThatPath("/normal").isDirectory().and().attribute("posix:permissions").isNot(permissions.value());
    }

    @Test
    public void testCreateSymbolicLink_withInitialAttributes() throws IOException {
        FileAttribute<Set<PosixFilePermission>> permissions = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx"));
        Files.createSymbolicLink(path("/foo"), path("bar"), permissions);
        assertThatPath("/foo", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().and().attribute("posix:permissions").is(permissions.value());
        Files.createSymbolicLink(path("/normal"), path("bar"));
        assertThatPath("/normal", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().and().attribute("posix:permissions").isNot(permissions.value());
    }

    @Test
    public void testCreateDirectories() throws IOException {
        Files.createDirectories(path("/foo/bar/baz"));
        assertThatPath("/foo").isDirectory();
        assertThatPath("/foo/bar").isDirectory();
        assertThatPath("/foo/bar/baz").isDirectory();
        Files.createDirectories(path("/foo/asdf/jkl"));
        assertThatPath("/foo/asdf").isDirectory();
        assertThatPath("/foo/asdf/jkl").isDirectory();
        Files.createDirectories(path("bar/baz"));
        assertThatPath("bar/baz").isDirectory();
        assertThatPath("/work/bar/baz").isDirectory();
    }

    @Test
    public void testDirectories_newlyCreatedDirectoryHasTwoLinks() throws IOException {
        // one link from its parent to it; one from it to itself
        Files.createDirectory(path("/foo"));
        assertThatPath("/foo").hasLinkCount(2);
    }

    @Test
    public void testDirectories_creatingDirectoryAddsOneLinkToParent() throws IOException {
        // from the .. direntry
        Files.createDirectory(path("/foo"));
        Files.createDirectory(path("/foo/bar"));
        assertThatPath("/foo").hasLinkCount(3);
        Files.createDirectory(path("/foo/baz"));
        assertThatPath("/foo").hasLinkCount(4);
    }

    @Test
    public void testDirectories_creatingNonDirectoryDoesNotAddLinkToParent() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createFile(path("/foo/file"));
        Files.createSymbolicLink(path("/foo/fileSymlink"), path("file"));
        Files.createLink(path("/foo/link"), path("/foo/file"));
        Files.createSymbolicLink(path("/foo/fooSymlink"), path("/foo"));
        assertThatPath("/foo").hasLinkCount(2);
    }

    @Test
    public void testSize_forNewFile_isZero() throws IOException {
        Files.createFile(path("/test"));
        assertThatPath("/test").hasSize(0);
    }

    @Test
    public void testRead_forNewFile_isEmpty() throws IOException {
        Files.createFile(path("/test"));
        assertThatPath("/test").containsNoBytes();
    }

    @Test
    public void testWriteFile_succeeds() throws IOException {
        Files.createFile(path("/test"));
        Files.write(path("/test"), new byte[]{ 0, 1, 2, 3 });
    }

    @Test
    public void testSize_forFileAfterWrite_isNumberOfBytesWritten() throws IOException {
        Files.write(path("/test"), new byte[]{ 0, 1, 2, 3 });
        assertThatPath("/test").hasSize(4);
    }

    @Test
    public void testRead_forFileAfterWrite_isBytesWritten() throws IOException {
        byte[] bytes = new byte[]{ 0, 1, 2, 3 };
        Files.write(path("/test"), bytes);
        assertThatPath("/test").containsBytes(bytes);
    }

    @Test
    public void testWriteFile_withStandardOptions() throws IOException {
        Path test = path("/test");
        byte[] bytes = new byte[]{ 0, 1, 2, 3 };
        try {
            // CREATE and CREATE_NEW not specified
            Files.write(test, bytes, StandardOpenOption.WRITE);
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals(test.toString(), expected.getMessage());
        }
        Files.write(test, bytes, StandardOpenOption.CREATE_NEW);// succeeds, file does not exist

        assertThatPath("/test").containsBytes(bytes);
        try {
            Files.write(test, bytes, StandardOpenOption.CREATE_NEW);// CREATE_NEW requires file not exist

            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals(test.toString(), expected.getMessage());
        }
        Files.write(test, new byte[]{ 4, 5 }, StandardOpenOption.CREATE);// succeeds, ok for file to already exist

        assertThatPath("/test").containsBytes(4, 5, 2, 3);// did not truncate or append, so overwrote

        Files.write(test, bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);// default options

        assertThatPath("/test").containsBytes(bytes);
        Files.write(test, bytes, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        assertThatPath("/test").containsBytes(0, 1, 2, 3, 0, 1, 2, 3);
        Files.write(test, bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.APPEND, StandardOpenOption.SPARSE, StandardOpenOption.DSYNC, StandardOpenOption.SYNC);
        assertThatPath("/test").containsBytes(bytes);
        try {
            Files.write(test, bytes, StandardOpenOption.READ, StandardOpenOption.WRITE);// READ not allowed

            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    public void testWriteLines_succeeds() throws IOException {
        Files.write(path("/test.txt"), ImmutableList.of("hello", "world"), StandardCharsets.UTF_8);
    }

    @Test
    public void testOpenFile_withReadAndTruncateExisting_doesNotTruncateFile() throws IOException {
        byte[] bytes = TestUtils.bytes(1, 2, 3, 4);
        Files.write(path("/test"), bytes);
        try (FileChannel channel = FileChannel.open(path("/test"), StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING)) {
            // TRUNCATE_EXISTING ignored when opening for read
            byte[] readBytes = new byte[4];
            channel.read(ByteBuffer.wrap(readBytes));
            assertThat(Bytes.asList(readBytes)).isEqualTo(Bytes.asList(bytes));
        }
    }

    @Test
    public void testRead_forFileAfterWriteLines_isLinesWritten() throws IOException {
        Files.write(path("/test.txt"), ImmutableList.of("hello", "world"), StandardCharsets.UTF_8);
        assertThatPath("/test.txt").containsLines("hello", "world");
    }

    @Test
    public void testWriteLines_withStandardOptions() throws IOException {
        Path test = path("/test.txt");
        ImmutableList<String> lines = ImmutableList.of("hello", "world");
        try {
            // CREATE and CREATE_NEW not specified
            Files.write(test, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals(test.toString(), expected.getMessage());
        }
        Files.write(test, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);// succeeds, file does not exist

        AbstractJimfsIntegrationTest.assertThatPath(test).containsLines(lines);
        try {
            Files.write(test, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);// CREATE_NEW requires file not exist

            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
        }
        // succeeds, ok for file to already exist
        Files.write(test, ImmutableList.of("foo"), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        // did not truncate or append, so overwrote
        if ((System.getProperty("line.separator").length()) == 2) {
            // on Windows, an extra character is overwritten by the \r\n line separator
            AbstractJimfsIntegrationTest.assertThatPath(test).containsLines("foo", "", "world");
        } else {
            AbstractJimfsIntegrationTest.assertThatPath(test).containsLines("foo", "o", "world");
        }
        Files.write(test, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);// default options

        AbstractJimfsIntegrationTest.assertThatPath(test).containsLines(lines);
        Files.write(test, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        AbstractJimfsIntegrationTest.assertThatPath(test).containsLines("hello", "world", "hello", "world");
        Files.write(test, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.APPEND, StandardOpenOption.SPARSE, StandardOpenOption.DSYNC, StandardOpenOption.SYNC);
        AbstractJimfsIntegrationTest.assertThatPath(test).containsLines(lines);
        try {
            Files.write(test, lines, StandardCharsets.UTF_8, StandardOpenOption.READ, StandardOpenOption.WRITE);// READ not allowed

            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    public void testWrite_fileExistsButIsNotRegularFile() throws IOException {
        Files.createDirectory(path("/foo"));
        try {
            // non-CREATE mode
            Files.write(path("/foo"), TestUtils.preFilledBytes(10), StandardOpenOption.WRITE);
            Assert.fail();
        } catch (FileSystemException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo");
            assertThat(expected.getMessage()).contains("regular file");
        }
        try {
            // CREATE mode
            Files.write(path("/foo"), TestUtils.preFilledBytes(10));
            Assert.fail();
        } catch (FileSystemException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo");
            assertThat(expected.getMessage()).contains("regular file");
        }
    }

    @Test
    public void testDelete_file() throws IOException {
        try {
            Files.delete(path("/test"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/test", expected.getMessage());
        }
        try {
            Files.delete(path("/foo/bar"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/foo/bar", expected.getMessage());
        }
        Assert.assertFalse(Files.deleteIfExists(path("/test")));
        Assert.assertFalse(Files.deleteIfExists(path("/foo/bar")));
        Files.createFile(path("/test"));
        assertThatPath("/test").isRegularFile();
        Files.delete(path("/test"));
        assertThatPath("/test").doesNotExist();
        Files.createFile(path("/test"));
        Assert.assertTrue(Files.deleteIfExists(path("/test")));
        assertThatPath("/test").doesNotExist();
    }

    @Test
    public void testDelete_file_whenOpenReferencesRemain() throws IOException {
        // the open streams should continue to function normally despite the deletion
        Path foo = path("/foo");
        byte[] bytes = TestUtils.preFilledBytes(100);
        Files.write(foo, bytes);
        InputStream in = Files.newInputStream(foo);
        OutputStream out = Files.newOutputStream(foo, StandardOpenOption.APPEND);
        FileChannel channel = FileChannel.open(foo, StandardOpenOption.READ, StandardOpenOption.WRITE);
        assertThat(channel.size()).isEqualTo(100L);
        Files.delete(foo);
        assertThatPath("/foo").doesNotExist();
        assertThat(channel.size()).isEqualTo(100L);
        ByteBuffer buf = ByteBuffer.allocate(100);
        while (buf.hasRemaining()) {
            channel.read(buf);
        } 
        Assert.assertArrayEquals(bytes, buf.array());
        byte[] moreBytes = new byte[]{ 1, 2, 3, 4, 5 };
        out.write(moreBytes);
        assertThat(channel.size()).isEqualTo(105L);
        buf.clear();
        assertThat(channel.read(buf)).isEqualTo(5);
        buf.flip();
        byte[] b = new byte[5];
        buf.get(b);
        Assert.assertArrayEquals(moreBytes, b);
        byte[] allBytes = new byte[105];
        int off = 0;
        int read;
        while ((read = in.read(allBytes, off, ((allBytes.length) - off))) != (-1)) {
            off += read;
        } 
        Assert.assertArrayEquals(Bytes.concat(bytes, moreBytes), allBytes);
        channel.close();
        out.close();
        in.close();
    }

    @Test
    public void testDelete_directory() throws IOException {
        Files.createDirectories(path("/foo/bar"));
        assertThatPath("/foo").isDirectory();
        assertThatPath("/foo/bar").isDirectory();
        Files.delete(path("/foo/bar"));
        assertThatPath("/foo/bar").doesNotExist();
        Assert.assertTrue(Files.deleteIfExists(path("/foo")));
        assertThatPath("/foo").doesNotExist();
    }

    @Test
    public void testDelete_pathPermutations() throws IOException {
        Path bar = path("/work/foo/bar");
        Files.createDirectories(bar);
        for (Path path : TestUtils.permutations(bar)) {
            Files.createDirectories(bar);
            AbstractJimfsIntegrationTest.assertThatPath(path).isSameFileAs(bar);
            Files.delete(path);
            AbstractJimfsIntegrationTest.assertThatPath(bar).doesNotExist();
            AbstractJimfsIntegrationTest.assertThatPath(path).doesNotExist();
        }
        Path baz = path("/test/baz");
        Files.createDirectories(baz);
        Path hello = baz.resolve("hello.txt");
        for (Path path : TestUtils.permutations(hello)) {
            Files.createFile(hello);
            AbstractJimfsIntegrationTest.assertThatPath(path).isSameFileAs(hello);
            Files.delete(path);
            AbstractJimfsIntegrationTest.assertThatPath(hello).doesNotExist();
            AbstractJimfsIntegrationTest.assertThatPath(path).doesNotExist();
        }
    }

    @Test
    public void testDelete_directory_cantDeleteNonEmptyDirectory() throws IOException {
        Files.createDirectories(path("/foo/bar"));
        try {
            Files.delete(path("/foo"));
            Assert.fail();
        } catch (DirectoryNotEmptyException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo");
        }
        try {
            Files.deleteIfExists(path("/foo"));
            Assert.fail();
        } catch (DirectoryNotEmptyException expected) {
            assertThat(expected.getFile()).isEqualTo("/foo");
        }
    }

    @Test
    public void testDelete_directory_canDeleteWorkingDirectoryByAbsolutePath() throws IOException {
        assertThatPath("/work").exists();
        assertThatPath("").exists();
        assertThatPath(".").exists();
        Files.delete(path("/work"));
        assertThatPath("/work").doesNotExist();
        assertThatPath("").exists();
        assertThatPath(".").exists();
    }

    @Test
    public void testDelete_directory_cantDeleteWorkingDirectoryByRelativePath() throws IOException {
        try {
            Files.delete(path(""));
            Assert.fail();
        } catch (FileSystemException expected) {
            assertThat(expected.getFile()).isEqualTo("");
        }
        try {
            Files.delete(path("."));
            Assert.fail();
        } catch (FileSystemException expected) {
            assertThat(expected.getFile()).isEqualTo(".");
        }
        try {
            Files.delete(path("../../work"));
            Assert.fail();
        } catch (FileSystemException expected) {
            assertThat(expected.getFile()).isEqualTo("../../work");
        }
        try {
            Files.delete(path("./../work/.././../work/."));
            Assert.fail();
        } catch (FileSystemException expected) {
            assertThat(expected.getFile()).isEqualTo("./../work/.././../work/.");
        }
    }

    @Test
    public void testDelete_directory_cantDeleteRoot() throws IOException {
        // delete working directory so that root is empty
        // don't want to just be testing the "can't delete when not empty" logic
        Files.delete(path("/work"));
        try {
            Files.delete(path("/"));
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected.getMessage()).contains("root");
        }
        Files.createDirectories(path("/foo/bar"));
        try {
            Files.delete(path("/foo/bar/../.."));
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected.getMessage()).contains("root");
        }
        try {
            Files.delete(path("/foo/./../foo/bar/./../bar/.././../../.."));
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected.getMessage()).contains("root");
        }
    }

    @Test
    public void testSymbolicLinks() throws IOException {
        Files.createSymbolicLink(path("/link.txt"), path("/file.txt"));
        assertThatPath("/link.txt", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("/file.txt");
        assertThatPath("/link.txt").doesNotExist();// following the link; target doesn't exist

        try {
            Files.createFile(path("/link.txt"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
        }
        try {
            Files.readAllBytes(path("/link.txt"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
        }
        Files.createFile(path("/file.txt"));
        assertThatPath("/link.txt").isRegularFile();// following the link; target does exist

        assertThatPath("/link.txt").containsNoBytes();
        Files.createSymbolicLink(path("/foo"), path("/bar/baz"));
        assertThatPath("/foo", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("/bar/baz");
        assertThatPath("/foo").doesNotExist();// following the link; target doesn't exist

        Files.createDirectories(path("/bar/baz"));
        assertThatPath("/foo").isDirectory();// following the link; target does exist

        Files.createFile(path("/bar/baz/test.txt"));
        assertThatPath("/foo/test.txt", LinkOption.NOFOLLOW_LINKS).isRegularFile();// follow intermediate link

        try {
            Files.readSymbolicLink(path("/none"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/none", expected.getMessage());
        }
        try {
            Files.readSymbolicLink(path("/file.txt"));
            Assert.fail();
        } catch (NotLinkException expected) {
            Assert.assertEquals("/file.txt", expected.getMessage());
        }
    }

    @Test
    public void testSymbolicLinks_symlinkCycle() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createSymbolicLink(path("/foo/bar"), path("baz"));
        Files.createSymbolicLink(path("/foo/baz"), path("bar"));
        try {
            Files.createFile(path("/foo/bar/file"));
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected.getMessage()).contains("symbolic link");
        }
        try {
            Files.write(path("/foo/bar"), TestUtils.preFilledBytes(10));
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected.getMessage()).contains("symbolic link");
        }
    }

    @Test
    public void testSymbolicLinks_lookupOfAbsoluteSymlinkPathFromRelativePath() throws IOException {
        // relative path lookups are in the FileSystemView for the working directory
        // this tests that when an absolute path is encountered, the lookup handles it correctly
        Files.createDirectories(path("/foo/bar/baz"));
        Files.createFile(path("/foo/bar/baz/file"));
        Files.createDirectories(path("one/two/three"));
        Files.createSymbolicLink(path("/work/one/two/three/link"), path("/foo/bar"));
        assertThatPath("one/two/three/link/baz/file").isSameFileAs("/foo/bar/baz/file");
    }

    @Test
    public void testLink() throws IOException {
        Files.createFile(path("/file.txt"));
        // checking link count requires "unix" attribute support, which we're using here
        assertThatPath("/file.txt").hasLinkCount(1);
        Files.createLink(path("/link.txt"), path("/file.txt"));
        assertThatPath("/link.txt").isSameFileAs("/file.txt");
        assertThatPath("/file.txt").hasLinkCount(2);
        assertThatPath("/link.txt").hasLinkCount(2);
        assertThatPath("/file.txt").containsNoBytes();
        assertThatPath("/link.txt").containsNoBytes();
        byte[] bytes = new byte[]{ 0, 1, 2, 3 };
        Files.write(path("/file.txt"), bytes);
        assertThatPath("/file.txt").containsBytes(bytes);
        assertThatPath("/link.txt").containsBytes(bytes);
        Files.write(path("/link.txt"), bytes, StandardOpenOption.APPEND);
        assertThatPath("/file.txt").containsBytes(0, 1, 2, 3, 0, 1, 2, 3);
        assertThatPath("/link.txt").containsBytes(0, 1, 2, 3, 0, 1, 2, 3);
        Files.delete(path("/file.txt"));
        assertThatPath("/link.txt").hasLinkCount(1);
        assertThatPath("/link.txt").containsBytes(0, 1, 2, 3, 0, 1, 2, 3);
    }

    @Test
    public void testLink_forSymbolicLink_usesSymbolicLinkTarget() throws IOException {
        Files.createFile(path("/file"));
        Files.createSymbolicLink(path("/symlink"), path("/file"));
        Object key = getFileKey("/file");
        Files.createLink(path("/link"), path("/symlink"));
        assertThatPath("/link").isRegularFile().and().hasLinkCount(2).and().attribute("fileKey").is(key);
    }

    @Test
    public void testLink_failsWhenTargetDoesNotExist() throws IOException {
        try {
            Files.createLink(path("/link"), path("/foo"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/foo", expected.getFile());
        }
        Files.createSymbolicLink(path("/foo"), path("/bar"));
        try {
            Files.createLink(path("/link"), path("/foo"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/foo", expected.getFile());
        }
    }

    @Test
    public void testLink_failsForNonRegularFile() throws IOException {
        Files.createDirectory(path("/dir"));
        try {
            Files.createLink(path("/link"), path("/dir"));
            Assert.fail();
        } catch (FileSystemException expected) {
            Assert.assertEquals("/link", expected.getFile());
            Assert.assertEquals("/dir", expected.getOtherFile());
        }
        assertThatPath("/link").doesNotExist();
    }

    @Test
    public void testLinks_failsWhenTargetFileAlreadyExists() throws IOException {
        Files.createFile(path("/file"));
        Files.createFile(path("/link"));
        try {
            Files.createLink(path("/link"), path("/file"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/link", expected.getFile());
        }
    }

    @Test
    public void testStreams() throws IOException {
        try (OutputStream out = Files.newOutputStream(path("/test"))) {
            for (int i = 0; i < 100; i++) {
                out.write(i);
            }
        }
        byte[] expected = new byte[100];
        for (byte i = 0; i < 100; i++) {
            expected[i] = i;
        }
        try (InputStream in = Files.newInputStream(path("/test"))) {
            byte[] bytes = new byte[100];
            ByteStreams.readFully(in, bytes);
            Assert.assertArrayEquals(expected, bytes);
        }
        try (Writer writer = Files.newBufferedWriter(path("/test.txt"), StandardCharsets.UTF_8)) {
            writer.write("hello");
        }
        try (Reader reader = Files.newBufferedReader(path("/test.txt"), StandardCharsets.UTF_8)) {
            Assert.assertEquals("hello", CharStreams.toString(reader));
        }
        try (Writer writer = Files.newBufferedWriter(path("/test.txt"), StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(" world");
        }
        try (Reader reader = Files.newBufferedReader(path("/test.txt"), StandardCharsets.UTF_8)) {
            Assert.assertEquals("hello world", CharStreams.toString(reader));
        }
    }

    @Test
    public void testChannels() throws IOException {
        try (FileChannel channel = FileChannel.open(path("/test.txt"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            ByteBuffer buf1 = StandardCharsets.UTF_8.encode("hello");
            ByteBuffer buf2 = StandardCharsets.UTF_8.encode(" world");
            while ((buf1.hasRemaining()) || (buf2.hasRemaining())) {
                channel.write(new ByteBuffer[]{ buf1, buf2 });
            } 
            Assert.assertEquals(11, channel.position());
            Assert.assertEquals(11, channel.size());
            channel.write(StandardCharsets.UTF_8.encode("!"));
            Assert.assertEquals(12, channel.position());
            Assert.assertEquals(12, channel.size());
        }
        try (SeekableByteChannel channel = Files.newByteChannel(path("/test.txt"), StandardOpenOption.READ)) {
            Assert.assertEquals(0, channel.position());
            Assert.assertEquals(12, channel.size());
            ByteBuffer buffer = ByteBuffer.allocate(100);
            while ((channel.read(buffer)) != (-1)) {
            } 
            buffer.flip();
            Assert.assertEquals("hello world!", StandardCharsets.UTF_8.decode(buffer).toString());
        }
        byte[] bytes = TestUtils.preFilledBytes(100);
        Files.write(path("/test"), bytes);
        try (SeekableByteChannel channel = Files.newByteChannel(path("/test"), StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.wrap(TestUtils.preFilledBytes(50));
            channel.position(50);
            channel.write(buffer);
            buffer.flip();
            channel.write(buffer);
            channel.position(0);
            ByteBuffer readBuffer = ByteBuffer.allocate(150);
            while (readBuffer.hasRemaining()) {
                channel.read(readBuffer);
            } 
            byte[] expected = Bytes.concat(TestUtils.preFilledBytes(50), TestUtils.preFilledBytes(50), TestUtils.preFilledBytes(50));
            Assert.assertArrayEquals(expected, readBuffer.array());
        }
        try (FileChannel channel = FileChannel.open(path("/test"), StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            Assert.assertEquals(150, channel.size());
            channel.truncate(10);
            Assert.assertEquals(10, channel.size());
            ByteBuffer buffer = ByteBuffer.allocate(20);
            Assert.assertEquals(10, channel.read(buffer));
            buffer.flip();
            byte[] expected = new byte[20];
            System.arraycopy(TestUtils.preFilledBytes(10), 0, expected, 0, 10);
            Assert.assertArrayEquals(expected, buffer.array());
        }
    }

    @Test
    public void testCopy_inputStreamToFile() throws IOException {
        byte[] bytes = TestUtils.preFilledBytes(512);
        Files.copy(new ByteArrayInputStream(bytes), path("/test"));
        assertThatPath("/test").containsBytes(bytes);
        try {
            Files.copy(new ByteArrayInputStream(bytes), path("/test"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/test", expected.getMessage());
        }
        Files.copy(new ByteArrayInputStream(bytes), path("/test"), StandardCopyOption.REPLACE_EXISTING);
        assertThatPath("/test").containsBytes(bytes);
        Files.copy(new ByteArrayInputStream(bytes), path("/foo"), StandardCopyOption.REPLACE_EXISTING);
        assertThatPath("/foo").containsBytes(bytes);
    }

    @Test
    public void testCopy_fileToOutputStream() throws IOException {
        byte[] bytes = TestUtils.preFilledBytes(512);
        Files.write(path("/test"), bytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Files.copy(path("/test"), out);
        Assert.assertArrayEquals(bytes, out.toByteArray());
    }

    @Test
    public void testCopy_fileToPath() throws IOException {
        byte[] bytes = TestUtils.preFilledBytes(512);
        Files.write(path("/foo"), bytes);
        assertThatPath("/bar").doesNotExist();
        Files.copy(path("/foo"), path("/bar"));
        assertThatPath("/bar").containsBytes(bytes);
        byte[] moreBytes = TestUtils.preFilledBytes(2048);
        Files.write(path("/baz"), moreBytes);
        Files.copy(path("/baz"), path("/bar"), StandardCopyOption.REPLACE_EXISTING);
        assertThatPath("/bar").containsBytes(moreBytes);
        try {
            Files.copy(path("/none"), path("/bar"));
            Assert.fail();
        } catch (NoSuchFileException expected) {
            Assert.assertEquals("/none", expected.getMessage());
        }
    }

    @Test
    public void testCopy_withCopyAttributes() throws IOException {
        Path foo = path("/foo");
        Files.createFile(foo);
        Files.getFileAttributeView(foo, BasicFileAttributeView.class).setTimes(FileTime.fromMillis(100), FileTime.fromMillis(1000), FileTime.fromMillis(10000));
        assertThat(Files.getAttribute(foo, "lastModifiedTime")).isEqualTo(FileTime.fromMillis(100));
        UserPrincipal zero = fs.getUserPrincipalLookupService().lookupPrincipalByName("zero");
        Files.setAttribute(foo, "owner:owner", zero);
        Path bar = path("/bar");
        Files.copy(foo, bar, StandardCopyOption.COPY_ATTRIBUTES);
        BasicFileAttributes attributes = Files.readAttributes(bar, BasicFileAttributes.class);
        assertThat(attributes.lastModifiedTime()).isEqualTo(FileTime.fromMillis(100));
        assertThat(attributes.lastAccessTime()).isEqualTo(FileTime.fromMillis(1000));
        assertThat(attributes.creationTime()).isEqualTo(FileTime.fromMillis(10000));
        assertThat(Files.getAttribute(bar, "owner:owner")).isEqualTo(zero);
        Path baz = path("/baz");
        Files.copy(foo, baz);
        // test that attributes are not copied when COPY_ATTRIBUTES is not specified
        attributes = Files.readAttributes(baz, BasicFileAttributes.class);
        assertThat(attributes.lastModifiedTime()).isNotEqualTo(FileTime.fromMillis(100));
        assertThat(attributes.lastAccessTime()).isNotEqualTo(FileTime.fromMillis(1000));
        assertThat(attributes.creationTime()).isNotEqualTo(FileTime.fromMillis(10000));
        assertThat(Files.getAttribute(baz, "owner:owner")).isNotEqualTo(zero);
    }

    @Test
    public void testCopy_doesNotSupportAtomicMove() throws IOException {
        try {
            Files.copy(path("/foo"), path("/bar"), StandardCopyOption.ATOMIC_MOVE);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    public void testCopy_directoryToPath() throws IOException {
        Files.createDirectory(path("/foo"));
        assertThatPath("/bar").doesNotExist();
        Files.copy(path("/foo"), path("/bar"));
        assertThatPath("/bar").isDirectory();
    }

    @Test
    public void testCopy_withoutReplaceExisting_failsWhenTargetExists() throws IOException {
        Files.createFile(path("/bar"));
        Files.createDirectory(path("/foo"));
        // dir -> file
        try {
            Files.copy(path("/foo"), path("/bar"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/bar", expected.getMessage());
        }
        Files.delete(path("/foo"));
        Files.createFile(path("/foo"));
        // file -> file
        try {
            Files.copy(path("/foo"), path("/bar"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/bar", expected.getMessage());
        }
        Files.delete(path("/bar"));
        Files.createDirectory(path("/bar"));
        // file -> dir
        try {
            Files.copy(path("/foo"), path("/bar"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/bar", expected.getMessage());
        }
        Files.delete(path("/foo"));
        Files.createDirectory(path("/foo"));
        // dir -> dir
        try {
            Files.copy(path("/foo"), path("/bar"));
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/bar", expected.getMessage());
        }
    }

    @Test
    public void testCopy_withReplaceExisting() throws IOException {
        Files.createFile(path("/bar"));
        Files.createDirectory(path("/test"));
        assertThatPath("/bar").isRegularFile();
        // overwrite regular file w/ directory
        Files.copy(path("/test"), path("/bar"), StandardCopyOption.REPLACE_EXISTING);
        assertThatPath("/bar").isDirectory();
        byte[] bytes = new byte[]{ 0, 1, 2, 3 };
        Files.write(path("/baz"), bytes);
        // overwrite directory w/ regular file
        Files.copy(path("/baz"), path("/bar"), StandardCopyOption.REPLACE_EXISTING);
        assertThatPath("/bar").containsSameBytesAs("/baz");
    }

    @Test
    public void testCopy_withReplaceExisting_cantReplaceNonEmptyDirectory() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createDirectory(path("/foo/bar"));
        Files.createFile(path("/foo/baz"));
        Files.createDirectory(path("/test"));
        try {
            Files.copy(path("/test"), path("/foo"), StandardCopyOption.REPLACE_EXISTING);
            Assert.fail();
        } catch (DirectoryNotEmptyException expected) {
            Assert.assertEquals("/foo", expected.getMessage());
        }
        Files.delete(path("/test"));
        Files.createFile(path("/test"));
        try {
            Files.copy(path("/test"), path("/foo"), StandardCopyOption.REPLACE_EXISTING);
            Assert.fail();
        } catch (DirectoryNotEmptyException expected) {
            Assert.assertEquals("/foo", expected.getMessage());
        }
        Files.delete(path("/foo/baz"));
        Files.delete(path("/foo/bar"));
        Files.copy(path("/test"), path("/foo"), StandardCopyOption.REPLACE_EXISTING);
        assertThatPath("/foo").isRegularFile();// replaced

    }

    @Test
    public void testCopy_directoryToPath_doesNotCopyDirectoryContents() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createDirectory(path("/foo/baz"));
        Files.createFile(path("/foo/test"));
        Files.copy(path("/foo"), path("/bar"));
        assertThatPath("/bar").hasNoChildren();
    }

    @Test
    public void testCopy_symbolicLinkToPath() throws IOException {
        byte[] bytes = TestUtils.preFilledBytes(128);
        Files.write(path("/test"), bytes);
        Files.createSymbolicLink(path("/link"), path("/test"));
        assertThatPath("/bar").doesNotExist();
        Files.copy(path("/link"), path("/bar"));
        assertThatPath("/bar", LinkOption.NOFOLLOW_LINKS).containsBytes(bytes);
        Files.delete(path("/bar"));
        Files.copy(path("/link"), path("/bar"), LinkOption.NOFOLLOW_LINKS);
        assertThatPath("/bar", LinkOption.NOFOLLOW_LINKS).isSymbolicLink().withTarget("/test");
        assertThatPath("/bar").isRegularFile();
        assertThatPath("/bar").containsBytes(bytes);
        Files.delete(path("/test"));
        assertThatPath("/bar", LinkOption.NOFOLLOW_LINKS).isSymbolicLink();
        assertThatPath("/bar").doesNotExist();
    }

    @Test
    public void testCopy_toDifferentFileSystem() throws IOException {
        try (FileSystem fs2 = Jimfs.newFileSystem(JimfsUnixLikeFileSystemTest.UNIX_CONFIGURATION)) {
            Path foo = fs.getPath("/foo");
            byte[] bytes = new byte[]{ 0, 1, 2, 3, 4 };
            Files.write(foo, bytes);
            Path foo2 = fs2.getPath("/foo");
            Files.copy(foo, foo2);
            AbstractJimfsIntegrationTest.assertThatPath(foo).exists();
            AbstractJimfsIntegrationTest.assertThatPath(foo2).exists().and().containsBytes(bytes);
        }
    }

    @Test
    public void testCopy_toDifferentFileSystem_copyAttributes() throws IOException {
        try (FileSystem fs2 = Jimfs.newFileSystem(JimfsUnixLikeFileSystemTest.UNIX_CONFIGURATION)) {
            Path foo = fs.getPath("/foo");
            byte[] bytes = new byte[]{ 0, 1, 2, 3, 4 };
            Files.write(foo, bytes);
            Files.getFileAttributeView(foo, BasicFileAttributeView.class).setTimes(FileTime.fromMillis(0), FileTime.fromMillis(1), FileTime.fromMillis(2));
            UserPrincipal owner = fs.getUserPrincipalLookupService().lookupPrincipalByName("foobar");
            Files.setOwner(foo, owner);
            AbstractJimfsIntegrationTest.assertThatPath(foo).attribute("owner:owner").is(owner);
            Path foo2 = fs2.getPath("/foo");
            Files.copy(foo, foo2, StandardCopyOption.COPY_ATTRIBUTES);
            AbstractJimfsIntegrationTest.assertThatPath(foo).exists();
            // when copying with COPY_ATTRIBUTES to a different FileSystem, only basic attributes (that
            // is, file times) can actually be copied
            AbstractJimfsIntegrationTest.assertThatPath(foo2).exists().and().attribute("lastModifiedTime").is(FileTime.fromMillis(0)).and().attribute("lastAccessTime").is(FileTime.fromMillis(1)).and().attribute("creationTime").is(FileTime.fromMillis(2)).and().attribute("owner:owner").isNot(owner).and().attribute("owner:owner").isNot(fs2.getUserPrincipalLookupService().lookupPrincipalByName("foobar")).and().containsBytes(bytes);// do this last; it updates the access time

        }
    }

    @Test
    public void testMove() throws IOException {
        byte[] bytes = TestUtils.preFilledBytes(100);
        Files.write(path("/foo"), bytes);
        Object fooKey = getFileKey("/foo");
        Files.move(path("/foo"), path("/bar"));
        assertThatPath("/foo").doesNotExist().andThat("/bar").containsBytes(bytes).and().attribute("fileKey").is(fooKey);
        Files.createDirectory(path("/foo"));
        Files.move(path("/bar"), path("/foo/bar"));
        assertThatPath("/bar").doesNotExist().andThat("/foo/bar").isRegularFile();
        Files.move(path("/foo"), path("/baz"));
        assertThatPath("/foo").doesNotExist().andThat("/baz").isDirectory().andThat("/baz/bar").isRegularFile();
    }

    @Test
    public void testMove_movesSymbolicLinkNotTarget() throws IOException {
        byte[] bytes = TestUtils.preFilledBytes(100);
        Files.write(path("/foo.txt"), bytes);
        Files.createSymbolicLink(path("/link"), path("foo.txt"));
        Files.move(path("/link"), path("/link.txt"));
        assertThatPath("/foo.txt").noFollowLinks().isRegularFile().and().containsBytes(bytes);
        AbstractJimfsIntegrationTest.assertThatPath(path("/link")).doesNotExist();
        AbstractJimfsIntegrationTest.assertThatPath(path("/link.txt")).noFollowLinks().isSymbolicLink();
        AbstractJimfsIntegrationTest.assertThatPath(path("/link.txt")).isRegularFile().and().containsBytes(bytes);
    }

    @Test
    public void testMove_cannotMoveDirIntoOwnSubtree() throws IOException {
        Files.createDirectories(path("/foo"));
        try {
            Files.move(path("/foo"), path("/foo/bar"));
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected.getMessage()).contains("sub");
        }
        Files.createDirectories(path("/foo/bar/baz/stuff"));
        Files.createDirectories(path("/hello/world"));
        Files.createSymbolicLink(path("/hello/world/link"), path("../../foo/bar/baz"));
        try {
            Files.move(path("/foo/bar"), path("/hello/world/link/bar"));
            Assert.fail();
        } catch (IOException expected) {
            assertThat(expected.getMessage()).contains("sub");
        }
    }

    @Test
    public void testMove_withoutReplaceExisting_failsWhenTargetExists() throws IOException {
        byte[] bytes = TestUtils.preFilledBytes(50);
        Files.write(path("/test"), bytes);
        Object testKey = getFileKey("/test");
        Files.createFile(path("/bar"));
        try {
            Files.move(path("/test"), path("/bar"), StandardCopyOption.ATOMIC_MOVE);
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/bar", expected.getMessage());
        }
        assertThatPath("/test").containsBytes(bytes).and().attribute("fileKey").is(testKey);
        Files.delete(path("/bar"));
        Files.createDirectory(path("/bar"));
        try {
            Files.move(path("/test"), path("/bar"), StandardCopyOption.ATOMIC_MOVE);
            Assert.fail();
        } catch (FileAlreadyExistsException expected) {
            Assert.assertEquals("/bar", expected.getMessage());
        }
        assertThatPath("/test").containsBytes(bytes).and().attribute("fileKey").is(testKey);
    }

    @Test
    public void testMove_toDifferentFileSystem() throws IOException {
        try (FileSystem fs2 = Jimfs.newFileSystem(Configuration.unix())) {
            Path foo = fs.getPath("/foo");
            byte[] bytes = new byte[]{ 0, 1, 2, 3, 4 };
            Files.write(foo, bytes);
            Files.getFileAttributeView(foo, BasicFileAttributeView.class).setTimes(FileTime.fromMillis(0), FileTime.fromMillis(1), FileTime.fromMillis(2));
            Path foo2 = fs2.getPath("/foo");
            Files.move(foo, foo2);
            AbstractJimfsIntegrationTest.assertThatPath(foo).doesNotExist();
            AbstractJimfsIntegrationTest.assertThatPath(foo2).exists().and().attribute("lastModifiedTime").is(FileTime.fromMillis(0)).and().attribute("lastAccessTime").is(FileTime.fromMillis(1)).and().attribute("creationTime").is(FileTime.fromMillis(2)).and().containsBytes(bytes);// do this last; it updates the access time

        }
    }

    @Test
    public void testIsSameFile() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createSymbolicLink(path("/bar"), path("/foo"));
        Files.createFile(path("/bar/test"));
        assertThatPath("/foo").isSameFileAs("/foo");
        assertThatPath("/bar").isSameFileAs("/bar");
        assertThatPath("/foo/test").isSameFileAs("/foo/test");
        assertThatPath("/bar/test").isSameFileAs("/bar/test");
        assertThatPath("/foo").isNotSameFileAs("test");
        assertThatPath("/bar").isNotSameFileAs("/test");
        assertThatPath("/foo").isSameFileAs("/bar");
        assertThatPath("/foo/test").isSameFileAs("/bar/test");
        Files.createSymbolicLink(path("/baz"), path("bar"));// relative path

        assertThatPath("/baz").isSameFileAs("/foo");
        assertThatPath("/baz/test").isSameFileAs("/foo/test");
    }

    @Test
    public void testIsSameFile_forPathFromDifferentFileSystemProvider() throws IOException {
        Path defaultFileSystemRoot = FileSystems.getDefault().getRootDirectories().iterator().next();
        assertThat(Files.isSameFile(path("/"), defaultFileSystemRoot)).isFalse();
    }

    @Test
    public void testPathLookups() throws IOException {
        assertThatPath("/").isSameFileAs("/");
        assertThatPath("/..").isSameFileAs("/");
        assertThatPath("/../../..").isSameFileAs("/");
        assertThatPath("../../../..").isSameFileAs("/");
        assertThatPath("").isSameFileAs("/work");
        Files.createDirectories(path("/foo/bar/baz"));
        Files.createSymbolicLink(path("/foo/bar/link1"), path("../link2"));
        Files.createSymbolicLink(path("/foo/link2"), path("/"));
        assertThatPath("/foo/bar/link1/foo/bar/link1/foo").isSameFileAs("/foo");
    }

    @Test
    public void testSecureDirectoryStream() throws IOException {
        Files.createDirectories(path("/foo/bar"));
        Files.createFile(path("/foo/a"));
        Files.createFile(path("/foo/b"));
        Files.createSymbolicLink(path("/foo/barLink"), path("bar"));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path("/foo"))) {
            if (!(stream instanceof SecureDirectoryStream)) {
                Assert.fail("should be a secure directory stream");
            }
            SecureDirectoryStream<Path> secureStream = ((SecureDirectoryStream<Path>) (stream));
            assertThat(ImmutableList.copyOf(secureStream)).isEqualTo(ImmutableList.of(path("/foo/a"), path("/foo/b"), path("/foo/bar"), path("/foo/barLink")));
            secureStream.deleteFile(path("b"));
            assertThatPath("/foo/b").doesNotExist();
            secureStream.newByteChannel(path("b"), ImmutableSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)).close();
            assertThatPath("/foo/b").isRegularFile();
            assertThatPath("/foo").hasChildren("a", "b", "bar", "barLink");
            Files.createDirectory(path("/baz"));
            Files.move(path("/foo"), path("/baz/stuff"));
            AbstractJimfsIntegrationTest.assertThatPath(path("/foo")).doesNotExist();
            assertThatPath("/baz/stuff").hasChildren("a", "b", "bar", "barLink");
            secureStream.deleteFile(path("b"));
            assertThatPath("/baz/stuff/b").doesNotExist();
            assertThatPath("/baz/stuff").hasChildren("a", "bar", "barLink");
            assertThat(secureStream.getFileAttributeView(BasicFileAttributeView.class).readAttributes().isDirectory()).isTrue();
            assertThat(secureStream.getFileAttributeView(path("a"), BasicFileAttributeView.class).readAttributes().isRegularFile()).isTrue();
            try {
                secureStream.deleteFile(path("bar"));
                Assert.fail();
            } catch (FileSystemException expected) {
                assertThat(expected.getFile()).isEqualTo("bar");
            }
            try {
                secureStream.deleteDirectory(path("a"));
                Assert.fail();
            } catch (FileSystemException expected) {
                assertThat(expected.getFile()).isEqualTo("a");
            }
            try (SecureDirectoryStream<Path> barStream = secureStream.newDirectoryStream(path("bar"))) {
                barStream.newByteChannel(path("stuff"), ImmutableSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)).close();
                assertThat(barStream.getFileAttributeView(path("stuff"), BasicFileAttributeView.class).readAttributes().isRegularFile()).isTrue();
                assertThat(secureStream.getFileAttributeView(path("bar/stuff"), BasicFileAttributeView.class).readAttributes().isRegularFile()).isTrue();
            }
            try (SecureDirectoryStream<Path> barLinkStream = secureStream.newDirectoryStream(path("barLink"))) {
                assertThat(barLinkStream.getFileAttributeView(path("stuff"), BasicFileAttributeView.class).readAttributes().isRegularFile()).isTrue();
                assertThat(barLinkStream.getFileAttributeView(path(".."), BasicFileAttributeView.class).readAttributes().isDirectory()).isTrue();
            }
            try {
                secureStream.newDirectoryStream(path("barLink"), LinkOption.NOFOLLOW_LINKS);
                Assert.fail();
            } catch (NotDirectoryException expected) {
                assertThat(expected.getFile()).isEqualTo("barLink");
            }
            try (SecureDirectoryStream<Path> barStream = secureStream.newDirectoryStream(path("bar"))) {
                secureStream.move(path("a"), barStream, path("moved"));
                AbstractJimfsIntegrationTest.assertThatPath(path("/baz/stuff/a")).doesNotExist();
                AbstractJimfsIntegrationTest.assertThatPath(path("/baz/stuff/bar/moved")).isRegularFile();
                assertThat(barStream.getFileAttributeView(path("moved"), BasicFileAttributeView.class).readAttributes().isRegularFile()).isTrue();
            }
        }
    }

    @Test
    public void testSecureDirectoryStreamBasedOnRelativePath() throws IOException {
        Files.createDirectories(path("foo"));
        Files.createFile(path("foo/a"));
        Files.createFile(path("foo/b"));
        Files.createDirectory(path("foo/c"));
        Files.createFile(path("foo/c/d"));
        Files.createFile(path("foo/c/e"));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path("foo"))) {
            SecureDirectoryStream<Path> secureStream = ((SecureDirectoryStream<Path>) (stream));
            assertThat(ImmutableList.copyOf(secureStream)).containsExactly(path("foo/a"), path("foo/b"), path("foo/c"));
            try (DirectoryStream<Path> stream2 = secureStream.newDirectoryStream(path("c"))) {
                assertThat(ImmutableList.copyOf(stream2)).containsExactly(path("foo/c/d"), path("foo/c/e"));
            }
        }
    }

    @SuppressWarnings("StreamResourceLeak")
    @Test
    public void testClosedSecureDirectoryStream() throws IOException {
        Files.createDirectory(path("/foo"));
        SecureDirectoryStream<Path> stream = ((SecureDirectoryStream<Path>) (Files.newDirectoryStream(path("/foo"))));
        stream.close();
        try {
            stream.iterator();
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            stream.deleteDirectory(fs.getPath("a"));
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            stream.deleteFile(fs.getPath("a"));
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            stream.newByteChannel(fs.getPath("a"), ImmutableSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            stream.newDirectoryStream(fs.getPath("a"));
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            stream.move(fs.getPath("a"), stream, fs.getPath("b"));
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            stream.getFileAttributeView(BasicFileAttributeView.class);
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            stream.getFileAttributeView(fs.getPath("a"), BasicFileAttributeView.class);
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
    }

    @SuppressWarnings("StreamResourceLeak")
    @Test
    public void testClosedSecureDirectoryStreamAttributeViewAndIterator() throws IOException {
        Files.createDirectory(path("/foo"));
        Files.createDirectory(path("/foo/bar"));
        SecureDirectoryStream<Path> stream = ((SecureDirectoryStream<Path>) (Files.newDirectoryStream(path("/foo"))));
        Iterator<Path> iter = stream.iterator();
        BasicFileAttributeView view1 = stream.getFileAttributeView(BasicFileAttributeView.class);
        BasicFileAttributeView view2 = stream.getFileAttributeView(path("bar"), BasicFileAttributeView.class);
        try {
            stream.iterator();
            Assert.fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
        }
        stream.close();
        try {
            iter.next();
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            view1.readAttributes();
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            view2.readAttributes();
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            view1.setTimes(null, null, null);
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
        try {
            view2.setTimes(null, null, null);
            Assert.fail("expected ClosedDirectoryStreamException");
        } catch (ClosedDirectoryStreamException expected) {
        }
    }

    @Test
    public void testDirectoryAccessAndModifiedTimeUpdates() throws IOException {
        Files.createDirectories(path("/foo/bar"));
        AbstractJimfsIntegrationTest.FileTimeTester tester = new AbstractJimfsIntegrationTest.FileTimeTester(path("/foo/bar"));
        tester.assertAccessTimeDidNotChange();
        tester.assertModifiedTimeDidNotChange();
        // TODO(cgdecker): Use a Clock for file times so I can test this reliably without sleeping
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        Files.createFile(path("/foo/bar/baz.txt"));
        tester.assertAccessTimeDidNotChange();
        tester.assertModifiedTimeChanged();
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        // access time is updated by reading the full contents of the directory
        // not just by doing a lookup in it
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path("/foo/bar"))) {
            // iterate the stream, forcing the directory to actually be read
            Iterators.advance(stream.iterator(), Integer.MAX_VALUE);
        }
        tester.assertAccessTimeChanged();
        tester.assertModifiedTimeDidNotChange();
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        Files.move(path("/foo/bar/baz.txt"), path("/foo/bar/baz2.txt"));
        tester.assertAccessTimeDidNotChange();
        tester.assertModifiedTimeChanged();
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        Files.delete(path("/foo/bar/baz2.txt"));
        tester.assertAccessTimeDidNotChange();
        tester.assertModifiedTimeChanged();
    }

    @Test
    public void testRegularFileAccessAndModifiedTimeUpdates() throws IOException {
        Path foo = path("foo");
        Files.createFile(foo);
        AbstractJimfsIntegrationTest.FileTimeTester tester = new AbstractJimfsIntegrationTest.FileTimeTester(foo);
        tester.assertAccessTimeDidNotChange();
        tester.assertModifiedTimeDidNotChange();
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        try (FileChannel channel = FileChannel.open(foo, StandardOpenOption.READ)) {
            // opening READ channel does not change times
            tester.assertAccessTimeDidNotChange();
            tester.assertModifiedTimeDidNotChange();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
            channel.read(ByteBuffer.allocate(100));
            // read call on channel does
            tester.assertAccessTimeChanged();
            tester.assertModifiedTimeDidNotChange();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
            channel.read(ByteBuffer.allocate(100));
            tester.assertAccessTimeChanged();
            tester.assertModifiedTimeDidNotChange();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
            try {
                channel.write(ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3 }));
            } catch (NonWritableChannelException ignore) {
            }
            // failed write on non-readable channel does not change times
            tester.assertAccessTimeDidNotChange();
            tester.assertModifiedTimeDidNotChange();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        }
        // closing channel does not change times
        tester.assertAccessTimeDidNotChange();
        tester.assertModifiedTimeDidNotChange();
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        try (FileChannel channel = FileChannel.open(foo, StandardOpenOption.WRITE)) {
            // opening WRITE channel does not change times
            tester.assertAccessTimeDidNotChange();
            tester.assertModifiedTimeDidNotChange();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
            channel.write(ByteBuffer.wrap(new byte[]{ 0, 1, 2, 3 }));
            // write call on channel does
            tester.assertAccessTimeDidNotChange();
            tester.assertModifiedTimeChanged();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
            channel.write(ByteBuffer.wrap(new byte[]{ 4, 5, 6, 7 }));
            tester.assertAccessTimeDidNotChange();
            tester.assertModifiedTimeChanged();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
            try {
                channel.read(ByteBuffer.allocate(100));
            } catch (NonReadableChannelException ignore) {
            }
            // failed read on non-readable channel does not change times
            tester.assertAccessTimeDidNotChange();
            tester.assertModifiedTimeDidNotChange();
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MILLISECONDS);
        }
        // closing channel does not change times
        tester.assertAccessTimeDidNotChange();
        tester.assertModifiedTimeDidNotChange();
    }

    @Test
    public void testUnsupportedFeatures() throws IOException {
        FileSystem fileSystem = Jimfs.newFileSystem(// none
        Configuration.unix().toBuilder().setSupportedFeatures().build());
        Path foo = fileSystem.getPath("foo");
        Path bar = foo.resolveSibling("bar");
        try {
            Files.createLink(foo, bar);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            Files.createSymbolicLink(foo, bar);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            Files.readSymbolicLink(foo);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            FileChannel.open(foo);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            AsynchronousFileChannel.open(foo);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        Files.createDirectory(foo);
        Files.createFile(bar);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(foo)) {
            assertThat(stream).isNotInstanceOf(SecureDirectoryStream.class);
        }
        try (SeekableByteChannel channel = Files.newByteChannel(bar)) {
            assertThat(channel).isNotInstanceOf(FileChannel.class);
        }
    }
}

