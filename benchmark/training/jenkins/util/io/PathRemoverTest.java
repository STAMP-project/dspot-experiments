/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.util.io;


import hudson.Functions;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.jvnet.hudson.test.Issue;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class PathRemoverTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public Timeout timeout = new Timeout(10, TimeUnit.SECONDS);

    @Rule
    public FileLockerRule locker = new FileLockerRule();

    @Test
    public void testForceRemoveFile() throws IOException {
        File file = tmp.newFile();
        PathRemoverTest.touchWithFileName(file);
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveFile(file.toPath());
        Assert.assertFalse(("Unable to delete file: " + file), file.exists());
    }

    @Test
    public void testForceRemoveFile_LockedFile() throws Exception {
        String filename = "/var/lock/jenkins.lock";
        File file = Mockito.mock(File.class);
        Path path = Mockito.mock(Path.class);
        FileSystem fs = Mockito.mock(FileSystem.class);
        FileSystemProvider fsProvider = Mockito.mock(FileSystemProvider.class);
        BasicFileAttributes attributes = Mockito.mock(BasicFileAttributes.class);
        BDDMockito.given(file.getPath()).willReturn(filename);
        BDDMockito.given(file.toPath()).willReturn(path);
        BDDMockito.given(path.toString()).willReturn(filename);
        BDDMockito.given(path.toFile()).willReturn(file);
        BDDMockito.given(path.getFileSystem()).willReturn(fs);
        BDDMockito.given(path.normalize()).willReturn(path);
        BDDMockito.given(fs.provider()).willReturn(fsProvider);
        BDDMockito.given(fsProvider.deleteIfExists(path)).willThrow(new FileSystemException(filename));
        BDDMockito.given(fsProvider.readAttributes(path, BasicFileAttributes.class)).willReturn(attributes);
        BDDMockito.given(attributes.isDirectory()).willReturn(false);
        PathRemover remover = PathRemover.newSimpleRemover();
        try {
            remover.forceRemoveFile(file.toPath());
            Assert.fail(("Should not have been deleted: " + file));
        } catch (IOException e) {
            Assert.assertThat(PathRemoverTest.calcExceptionHierarchy(e), Matchers.hasItem(FileSystemException.class));
            Assert.assertThat(e.getMessage(), Matchers.containsString(filename));
        }
    }

    @Test
    public void testForceRemoveFile_ReadOnly() throws IOException {
        File dir = tmp.newFolder();
        File file = new File(dir, "file.tmp");
        PathRemoverTest.touchWithFileName(file);
        Assert.assertTrue(("Unable to make file read-only: " + file), file.setWritable(false));
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveFile(file.toPath());
        Assert.assertFalse(("Unable to delete file: " + file), file.exists());
    }

    @Test
    public void testForceRemoveFile_DoesNotExist() throws IOException {
        File dir = tmp.newFolder();
        File file = new File(dir, "invalid.file");
        Assert.assertFalse(file.exists());
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveFile(file.toPath());
        Assert.assertFalse(("Unable to delete file: " + file), file.exists());
    }

    @Test
    public void testForceRemoveFile_SymbolicLink() throws IOException {
        File file = tmp.newFile();
        PathRemoverTest.touchWithFileName(file);
        Path link = Files.createSymbolicLink(tmp.getRoot().toPath().resolve("test-link"), file.toPath());
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveFile(link);
        Assert.assertTrue(("Unable to delete symbolic link: " + link), Files.notExists(link, LinkOption.NOFOLLOW_LINKS));
        Assert.assertTrue(("Should not have deleted target file: " + file), file.exists());
    }

    @Test
    @Issue("JENKINS-55448")
    public void testForceRemoveFile_DotsInPath() throws IOException {
        Path folder = tmp.newFolder().toPath();
        File test = tmp.newFile("test");
        PathRemoverTest.touchWithFileName(test);
        Path path = folder.resolve("../test");
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveFile(path);
        Assert.assertTrue(("Unable to delete file: " + path), Files.notExists(path));
        Assert.assertFalse(test.exists());
        Assert.assertTrue(("Should not have deleted directory: " + folder), Files.exists(folder));
    }

    @Test
    @Issue("JENKINS-55448")
    public void testForceRemoveFile_ParentIsSymbolicLink() throws IOException {
        Path realParent = tmp.newFolder().toPath();
        Path path = realParent.resolve("test-file");
        PathRemoverTest.touchWithFileName(path.toFile());
        Path symParent = Files.createSymbolicLink(tmp.getRoot().toPath().resolve("sym-parent"), realParent);
        Path toDelete = symParent.resolve("test-file");
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveFile(toDelete);
        Assert.assertTrue(("Unable to delete file: " + toDelete), Files.notExists(toDelete));
        Assert.assertTrue(("Should not have deleted directory: " + realParent), Files.exists(realParent));
        Assert.assertTrue(("Should not have deleted symlink: " + symParent), Files.exists(symParent, LinkOption.NOFOLLOW_LINKS));
    }

    @Test
    public void testForceRemoveDirectoryContents() throws IOException {
        File dir = tmp.newFolder();
        File d1 = new File(dir, "d1");
        File d2 = new File(dir, "d2");
        File f1 = new File(dir, "f1");
        File d1f1 = new File(d1, "d1f1");
        File d2f2 = new File(d2, "d1f2");
        PathRemoverTest.mkdirs(d1, d2);
        PathRemoverTest.touchWithFileName(f1, d1f1, d2f2);
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveDirectoryContents(dir.toPath());
        Assert.assertTrue(dir.exists());
        Assert.assertFalse(d1.exists());
        Assert.assertFalse(d2.exists());
        Assert.assertFalse(f1.exists());
    }

    @Test
    public void testForceRemoveDirectoryContents_LockedFile() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        File dir = tmp.newFolder();
        File d1 = new File(dir, "d1");
        File d2 = new File(dir, "d2");
        File f1 = new File(dir, "f1");
        File d1f1 = new File(d1, "d1f1");
        File d2f2 = new File(d2, "d1f2");
        PathRemoverTest.mkdirs(d1, d2);
        PathRemoverTest.touchWithFileName(f1, d1f1, d2f2);
        locker.acquireLock(d1f1);
        PathRemover remover = PathRemover.newRemoverWithStrategy(( retriesAttempted) -> retriesAttempted < 1);
        expectedException.expectMessage(Matchers.allOf(Matchers.containsString(dir.getPath()), Matchers.containsString("Tried 1 time.")));
        remover.forceRemoveDirectoryContents(dir.toPath());
        Assert.assertFalse(d2.exists());
        Assert.assertFalse(f1.exists());
        Assert.assertFalse(d2f2.exists());
    }

    @Test
    public void testForceRemoveRecursive() throws IOException {
        File dir = tmp.newFolder();
        File d1 = new File(dir, "d1");
        File d2 = new File(dir, "d2");
        File f1 = new File(dir, "f1");
        File d1f1 = new File(d1, "d1f1");
        File d2f2 = new File(d2, "d1f2");
        PathRemoverTest.mkdirs(d1, d2);
        PathRemoverTest.touchWithFileName(f1, d1f1, d2f2);
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveRecursive(dir.toPath());
        Assert.assertFalse(dir.exists());
    }

    @Test
    public void testForceRemoveRecursive_DeletesAsMuchAsPossibleWithLockedFiles() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        File dir = tmp.newFolder();
        File d1 = new File(dir, "d1");
        File d2 = new File(dir, "d2");
        File f1 = new File(dir, "f1");
        File d1f1 = new File(d1, "d1f1");
        File d2f2 = new File(d2, "d1f2");
        PathRemoverTest.mkdirs(d1, d2);
        PathRemoverTest.touchWithFileName(f1, d1f1, d2f2);
        locker.acquireLock(d1f1);
        PathRemover remover = PathRemover.newSimpleRemover();
        expectedException.expectMessage(Matchers.containsString(dir.getPath()));
        remover.forceRemoveRecursive(dir.toPath());
        Assert.assertTrue(dir.exists());
        Assert.assertTrue(d1.exists());
        Assert.assertTrue(d1f1.exists());
        Assert.assertFalse(d2.exists());
        Assert.assertFalse(d2f2.exists());
        Assert.assertFalse(f1.exists());
    }

    @Test
    public void testForceRemoveRecursive_RetryOnFailure() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        File dir = tmp.newFolder();
        File d1 = new File(dir, "d1");
        File d2 = new File(dir, "d2");
        File f1 = new File(dir, "f1");
        File d1f1 = new File(d1, "d1f1");
        File d2f2 = new File(d2, "d1f2");
        PathRemoverTest.mkdirs(d1, d2);
        PathRemoverTest.touchWithFileName(f1, d1f1, d2f2);
        locker.acquireLock(d2f2);
        CountDownLatch unlockLatch = new CountDownLatch(1);
        CountDownLatch deleteLatch = new CountDownLatch(1);
        AtomicBoolean lockedFileExists = new AtomicBoolean();
        Thread thread = new Thread(() -> {
            try {
                unlockLatch.await();
                locker.releaseLock(d2f2);
                deleteLatch.countDown();
            } catch (Exception ignored) {
            }
        });
        thread.start();
        PathRemover remover = PathRemover.newRemoverWithStrategy(( retriesAttempted) -> {
            if (retriesAttempted == 0) {
                lockedFileExists.set(d2f2.exists());
                unlockLatch.countDown();
                try {
                    deleteLatch.await();
                    return true;
                } catch ( e) {
                    return false;
                }
            }
            return false;
        });
        remover.forceRemoveRecursive(dir.toPath());
        thread.join();
        Assert.assertTrue(lockedFileExists.get());
        Assert.assertFalse(dir.exists());
    }

    @Test
    public void testForceRemoveRecursive_FailsWhenInterrupted() throws Exception {
        Assume.assumeTrue(Functions.isWindows());
        File dir = tmp.newFolder();
        File d1 = new File(dir, "d1");
        File d2 = new File(dir, "d2");
        File f1 = new File(dir, "f1");
        File d1f1 = new File(d1, "d1f1");
        File d2f2 = new File(d2, "d1f2");
        PathRemoverTest.mkdirs(d1, d2);
        PathRemoverTest.touchWithFileName(f1, d1f1, d2f2);
        locker.acquireLock(d1f1);
        AtomicReference<InterruptedException> interrupted = new AtomicReference<>();
        AtomicReference<IOException> removed = new AtomicReference<>();
        PathRemover remover = PathRemover.newRemoverWithStrategy(( retriesAttempted) -> {
            try {
                TimeUnit.SECONDS.sleep((retriesAttempted + 1));
                return true;
            } catch ( e) {
                interrupted.set(e);
                return false;
            }
        });
        Thread thread = new Thread(() -> {
            try {
                remover.forceRemoveRecursive(dir.toPath());
            } catch (IOException e) {
                removed.set(e);
            }
        });
        thread.start();
        TimeUnit.MILLISECONDS.sleep(100);
        thread.interrupt();
        thread.join();
        Assert.assertFalse(thread.isAlive());
        Assert.assertTrue(d1f1.exists());
        IOException ioException = removed.get();
        Assert.assertNotNull(ioException);
        Assert.assertThat(ioException.getMessage(), Matchers.containsString(dir.getPath()));
        Assert.assertNotNull(interrupted.get());
    }

    @Test
    public void testForceRemoveRecursive_ContainsSymbolicLinks() throws IOException {
        File folder = tmp.newFolder();
        File d1 = new File(folder, "d1");
        File d1f1 = new File(d1, "d1f1");
        File f2 = new File(folder, "f2");
        PathRemoverTest.mkdirs(d1);
        PathRemoverTest.touchWithFileName(d1f1, f2);
        Path path = tmp.newFolder().toPath();
        Files.createSymbolicLink(path.resolve("sym-dir"), d1.toPath());
        Files.createSymbolicLink(path.resolve("sym-file"), f2.toPath());
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveRecursive(path);
        Assert.assertTrue(("Unable to delete directory: " + path), Files.notExists(path));
        for (File file : Arrays.asList(d1, d1f1, f2)) {
            Assert.assertTrue(("Should not have deleted target: " + file), file.exists());
        }
    }

    @Test
    @Issue("JENKINS-55448")
    public void testForceRemoveRecursive_ContainsDotPath() throws IOException {
        File folder = tmp.newFolder();
        File d1 = new File(folder, "d1");
        File d1f1 = new File(d1, "d1f1");
        File f2 = new File(folder, "f2");
        PathRemoverTest.mkdirs(d1);
        PathRemoverTest.touchWithFileName(d1f1, f2);
        Path path = Paths.get(d1.getPath(), "..", "d1");
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveRecursive(path);
        Assert.assertTrue(("Unable to delete directory: " + folder), Files.notExists(path));
    }

    @Test
    @Issue("JENKINS-55448")
    public void testForceRemoveRecursive_ParentIsSymbolicLink() throws IOException {
        File folder = tmp.newFolder();
        File d1 = new File(folder, "d1");
        File d1f1 = new File(d1, "d1f1");
        File f2 = new File(folder, "f2");
        PathRemoverTest.mkdirs(d1);
        PathRemoverTest.touchWithFileName(d1f1, f2);
        Path symlink = Files.createSymbolicLink(tmp.getRoot().toPath().resolve("linked"), folder.toPath());
        Path d1p = symlink.resolve("d1");
        PathRemover remover = PathRemover.newSimpleRemover();
        remover.forceRemoveRecursive(d1p);
        Assert.assertTrue(("Unable to delete directory: " + d1p), Files.notExists(d1p));
        Assert.assertFalse(d1.exists());
    }
}

