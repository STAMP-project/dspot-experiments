/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.nativeio;


import Errno.EBADF;
import Errno.EEXIST;
import Errno.ENOENT;
import Errno.ENOTDIR;
import NativeIO.Windows;
import NativeIO.Windows.AccessRight.ACCESS_EXECUTE;
import NativeIO.Windows.AccessRight.ACCESS_READ;
import NativeIO.Windows.AccessRight.ACCESS_WRITE;
import NativeIO.Windows.FILE_BEGIN;
import NativeIO.Windows.GENERIC_READ;
import NativeIO.Windows.OPEN_EXISTING;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.LambdaTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;


public class TestNativeIO {
    static final Logger LOG = LoggerFactory.getLogger(TestNativeIO.class);

    static final File TEST_DIR = GenericTestUtils.getTestDir("testnativeio");

    @Test(timeout = 30000)
    public void testFstat() throws Exception {
        FileOutputStream fos = new FileOutputStream(new File(TestNativeIO.TEST_DIR, "testfstat"));
        NativeIO.POSIX.Stat stat = NativeIO.POSIX.getFstat(fos.getFD());
        fos.close();
        TestNativeIO.LOG.info(("Stat: " + (String.valueOf(stat))));
        String owner = stat.getOwner();
        String expectedOwner = System.getProperty("user.name");
        if (Path.WINDOWS) {
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(expectedOwner);
            final String adminsGroupString = "Administrators";
            if (Arrays.asList(ugi.getGroupNames()).contains(adminsGroupString)) {
                expectedOwner = adminsGroupString;
            }
        }
        Assert.assertEquals(expectedOwner, owner);
        Assert.assertNotNull(stat.getGroup());
        Assert.assertTrue((!(stat.getGroup().isEmpty())));
        Assert.assertEquals("Stat mode field should indicate a regular file", S_IFREG, ((stat.getMode()) & (S_IFMT)));
    }

    /**
     * Test for races in fstat usage
     *
     * NOTE: this test is likely to fail on RHEL 6.0 which has a non-threadsafe
     * implementation of getpwuid_r.
     */
    @Test(timeout = 30000)
    public void testMultiThreadedFstat() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        final FileOutputStream fos = new FileOutputStream(new File(TestNativeIO.TEST_DIR, "testfstat"));
        final AtomicReference<Throwable> thrown = new AtomicReference<Throwable>();
        List<Thread> statters = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            Thread statter = new Thread() {
                @Override
                public void run() {
                    long et = (Time.now()) + 5000;
                    while ((Time.now()) < et) {
                        try {
                            NativeIO.POSIX.Stat stat = NativeIO.POSIX.getFstat(fos.getFD());
                            Assert.assertEquals(System.getProperty("user.name"), stat.getOwner());
                            Assert.assertNotNull(stat.getGroup());
                            Assert.assertTrue((!(stat.getGroup().isEmpty())));
                            Assert.assertEquals("Stat mode field should indicate a regular file", S_IFREG, ((stat.getMode()) & (S_IFMT)));
                        } catch (Throwable t) {
                            thrown.set(t);
                        }
                    } 
                }
            };
            statters.add(statter);
            statter.start();
        }
        for (Thread t : statters) {
            t.join();
        }
        fos.close();
        if ((thrown.get()) != null) {
            throw new RuntimeException(thrown.get());
        }
    }

    @Test(timeout = 30000)
    public void testFstatClosedFd() throws Exception {
        FileOutputStream fos = new FileOutputStream(new File(TestNativeIO.TEST_DIR, "testfstat2"));
        fos.close();
        try {
            NativeIO.POSIX.Stat stat = NativeIO.POSIX.getFstat(fos.getFD());
        } catch (NativeIOException nioe) {
            TestNativeIO.LOG.info("Got expected exception", nioe);
            Assert.assertEquals(EBADF, nioe.getErrno());
        }
    }

    @Test(timeout = 30000)
    public void testStat() throws Exception {
        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.getLocal(conf).getRawFileSystem();
        Path path = new Path(TestNativeIO.TEST_DIR.getPath(), "teststat2");
        fileSystem.createNewFile(path);
        String testFilePath = path.toString();
        try {
            doStatTest(testFilePath);
            TestNativeIO.LOG.info("testStat() is successful.");
        } finally {
            ContractTestUtils.cleanup(("cleanup test file: " + (path.toString())), fileSystem, path);
        }
    }

    @Test
    public void testStatOnError() throws Exception {
        final String testNullFilePath = null;
        LambdaTestUtils.intercept(IOException.class, "Path is null", () -> NativeIO.POSIX.getStat(testNullFilePath));
        final String testInvalidFilePath = "C:\\nonexisting_path\\nonexisting_file";
        LambdaTestUtils.intercept(IOException.class, PathIOException.class.getName(), () -> NativeIO.POSIX.getStat(testInvalidFilePath));
    }

    @Test(timeout = 30000)
    public void testMultiThreadedStat() throws Exception {
        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.getLocal(conf).getRawFileSystem();
        Path path = new Path(TestNativeIO.TEST_DIR.getPath(), "teststat2");
        fileSystem.createNewFile(path);
        String testFilePath = path.toString();
        int numOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        try {
            for (int i = 0; i < numOfThreads; i++) {
                Future<Boolean> result = executorService.submit(() -> doStatTest(testFilePath));
                Assert.assertTrue(result.get());
            }
            TestNativeIO.LOG.info("testMultiThreadedStat() is successful.");
        } finally {
            executorService.shutdown();
            ContractTestUtils.cleanup(("cleanup test file: " + (path.toString())), fileSystem, path);
        }
    }

    @Test
    public void testMultiThreadedStatOnError() throws Exception {
        final String testInvalidFilePath = "C:\\nonexisting_path\\nonexisting_file";
        int numOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            try {
                Future<Boolean> result = executorService.submit(() -> doStatTest(testInvalidFilePath));
                result.get();
            } catch (Exception e) {
                Assert.assertTrue(((e.getCause()) instanceof PathIOException));
            }
        }
        executorService.shutdown();
    }

    @Test(timeout = 30000)
    public void testSetFilePointer() throws Exception {
        PlatformAssumptions.assumeWindows();
        TestNativeIO.LOG.info("Set a file pointer on Windows");
        try {
            File testfile = new File(TestNativeIO.TEST_DIR, "testSetFilePointer");
            Assert.assertTrue("Create test subject", ((testfile.exists()) || (testfile.createNewFile())));
            FileWriter writer = new FileWriter(testfile);
            try {
                for (int i = 0; i < 200; i++)
                    if (i < 100)
                        writer.write('a');
                    else
                        writer.write('b');


                writer.flush();
            } catch (Exception writerException) {
                Assert.fail(("Got unexpected exception: " + (writerException.getMessage())));
            } finally {
                writer.close();
            }
            FileDescriptor fd = Windows.createFile(testfile.getCanonicalPath(), GENERIC_READ, (((Windows.FILE_SHARE_READ) | (Windows.FILE_SHARE_WRITE)) | (Windows.FILE_SHARE_DELETE)), OPEN_EXISTING);
            Windows.setFilePointer(fd, 120, FILE_BEGIN);
            FileReader reader = new FileReader(fd);
            try {
                int c = reader.read();
                Assert.assertTrue(("Unexpected character: " + c), (c == 'b'));
            } catch (Exception readerException) {
                Assert.fail(("Got unexpected exception: " + (readerException.getMessage())));
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            Assert.fail(("Got unexpected exception: " + (e.getMessage())));
        }
    }

    @Test(timeout = 30000)
    public void testCreateFile() throws Exception {
        PlatformAssumptions.assumeWindows();
        TestNativeIO.LOG.info("Open a file on Windows with SHARE_DELETE shared mode");
        try {
            File testfile = new File(TestNativeIO.TEST_DIR, "testCreateFile");
            Assert.assertTrue("Create test subject", ((testfile.exists()) || (testfile.createNewFile())));
            FileDescriptor fd = Windows.createFile(testfile.getCanonicalPath(), GENERIC_READ, (((Windows.FILE_SHARE_READ) | (Windows.FILE_SHARE_WRITE)) | (Windows.FILE_SHARE_DELETE)), OPEN_EXISTING);
            FileInputStream fin = new FileInputStream(fd);
            try {
                fin.read();
                File newfile = new File(TestNativeIO.TEST_DIR, "testRenamedFile");
                boolean renamed = testfile.renameTo(newfile);
                Assert.assertTrue("Rename failed.", renamed);
                fin.read();
            } catch (Exception e) {
                Assert.fail(("Got unexpected exception: " + (e.getMessage())));
            } finally {
                fin.close();
            }
        } catch (Exception e) {
            Assert.fail(("Got unexpected exception: " + (e.getMessage())));
        }
    }

    /**
     * Validate access checks on Windows
     */
    @Test(timeout = 30000)
    public void testAccess() throws Exception {
        PlatformAssumptions.assumeWindows();
        File testFile = new File(TestNativeIO.TEST_DIR, "testfileaccess");
        Assert.assertTrue(testFile.createNewFile());
        // Validate ACCESS_READ
        FileUtil.setReadable(testFile, false);
        Assert.assertFalse(Windows.access(testFile.getAbsolutePath(), ACCESS_READ));
        FileUtil.setReadable(testFile, true);
        Assert.assertTrue(Windows.access(testFile.getAbsolutePath(), ACCESS_READ));
        // Validate ACCESS_WRITE
        FileUtil.setWritable(testFile, false);
        Assert.assertFalse(Windows.access(testFile.getAbsolutePath(), ACCESS_WRITE));
        FileUtil.setWritable(testFile, true);
        Assert.assertTrue(Windows.access(testFile.getAbsolutePath(), ACCESS_WRITE));
        // Validate ACCESS_EXECUTE
        FileUtil.setExecutable(testFile, false);
        Assert.assertFalse(Windows.access(testFile.getAbsolutePath(), ACCESS_EXECUTE));
        FileUtil.setExecutable(testFile, true);
        Assert.assertTrue(Windows.access(testFile.getAbsolutePath(), ACCESS_EXECUTE));
        // Validate that access checks work as expected for long paths
        // Assemble a path longer then 260 chars (MAX_PATH)
        String testFileRelativePath = "";
        for (int i = 0; i < 15; ++i) {
            testFileRelativePath += "testfileaccessfolder\\";
        }
        testFileRelativePath += "testfileaccess";
        testFile = new File(TestNativeIO.TEST_DIR, testFileRelativePath);
        Assert.assertTrue(testFile.getParentFile().mkdirs());
        Assert.assertTrue(testFile.createNewFile());
        // Validate ACCESS_READ
        FileUtil.setReadable(testFile, false);
        Assert.assertFalse(Windows.access(testFile.getAbsolutePath(), ACCESS_READ));
        FileUtil.setReadable(testFile, true);
        Assert.assertTrue(Windows.access(testFile.getAbsolutePath(), ACCESS_READ));
        // Validate ACCESS_WRITE
        FileUtil.setWritable(testFile, false);
        Assert.assertFalse(Windows.access(testFile.getAbsolutePath(), ACCESS_WRITE));
        FileUtil.setWritable(testFile, true);
        Assert.assertTrue(Windows.access(testFile.getAbsolutePath(), ACCESS_WRITE));
        // Validate ACCESS_EXECUTE
        FileUtil.setExecutable(testFile, false);
        Assert.assertFalse(Windows.access(testFile.getAbsolutePath(), ACCESS_EXECUTE));
        FileUtil.setExecutable(testFile, true);
        Assert.assertTrue(Windows.access(testFile.getAbsolutePath(), ACCESS_EXECUTE));
    }

    @Test(timeout = 30000)
    public void testOpenMissingWithoutCreate() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        TestNativeIO.LOG.info("Open a missing file without O_CREAT and it should fail");
        try {
            FileDescriptor fd = NativeIO.POSIX.open(new File(TestNativeIO.TEST_DIR, "doesntexist").getAbsolutePath(), O_WRONLY, 448);
            Assert.fail("Able to open a new file without O_CREAT");
        } catch (NativeIOException nioe) {
            TestNativeIO.LOG.info("Got expected exception", nioe);
            Assert.assertEquals(ENOENT, nioe.getErrno());
        }
    }

    @Test(timeout = 30000)
    public void testOpenWithCreate() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        TestNativeIO.LOG.info("Test creating a file with O_CREAT");
        FileDescriptor fd = NativeIO.POSIX.open(new File(TestNativeIO.TEST_DIR, "testWorkingOpen").getAbsolutePath(), ((O_WRONLY) | (O_CREAT)), 448);
        Assert.assertNotNull(true);
        Assert.assertTrue(fd.valid());
        FileOutputStream fos = new FileOutputStream(fd);
        fos.write("foo".getBytes());
        fos.close();
        Assert.assertFalse(fd.valid());
        TestNativeIO.LOG.info("Test exclusive create");
        try {
            fd = NativeIO.POSIX.open(new File(TestNativeIO.TEST_DIR, "testWorkingOpen").getAbsolutePath(), (((O_WRONLY) | (O_CREAT)) | (O_EXCL)), 448);
            Assert.fail("Was able to create existing file with O_EXCL");
        } catch (NativeIOException nioe) {
            TestNativeIO.LOG.info("Got expected exception for failed exclusive create", nioe);
            Assert.assertEquals(EEXIST, nioe.getErrno());
        }
    }

    /**
     * Test that opens and closes a file 10000 times - this would crash with
     * "Too many open files" if we leaked fds using this access pattern.
     */
    @Test(timeout = 30000)
    public void testFDDoesntLeak() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        for (int i = 0; i < 10000; i++) {
            FileDescriptor fd = NativeIO.POSIX.open(new File(TestNativeIO.TEST_DIR, "testNoFdLeak").getAbsolutePath(), ((O_WRONLY) | (O_CREAT)), 448);
            Assert.assertNotNull(true);
            Assert.assertTrue(fd.valid());
            FileOutputStream fos = new FileOutputStream(fd);
            fos.write("foo".getBytes());
            fos.close();
        }
    }

    /**
     * Test basic chmod operation
     */
    @Test(timeout = 30000)
    public void testChmod() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        try {
            NativeIO.POSIX.chmod("/this/file/doesnt/exist", 777);
            Assert.fail("Chmod of non-existent file didn't fail");
        } catch (NativeIOException nioe) {
            Assert.assertEquals(ENOENT, nioe.getErrno());
        }
        File toChmod = new File(TestNativeIO.TEST_DIR, "testChmod");
        Assert.assertTrue("Create test subject", ((toChmod.exists()) || (toChmod.mkdir())));
        NativeIO.POSIX.chmod(toChmod.getAbsolutePath(), 511);
        assertPermissions(toChmod, 511);
        NativeIO.POSIX.chmod(toChmod.getAbsolutePath(), 0);
        assertPermissions(toChmod, 0);
        NativeIO.POSIX.chmod(toChmod.getAbsolutePath(), 420);
        assertPermissions(toChmod, 420);
    }

    @Test(timeout = 30000)
    public void testPosixFadvise() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        FileInputStream fis = new FileInputStream("/dev/zero");
        try {
            NativeIO.POSIX.posix_fadvise(fis.getFD(), 0, 0, POSIX_FADV_SEQUENTIAL);
        } catch (UnsupportedOperationException uoe) {
            // we should just skip the unit test on machines where we don't
            // have fadvise support
            Assume.assumeTrue(false);
        } catch (NativeIOException nioe) {
            // ignore this error as FreeBSD returns EBADF even if length is zero
        } finally {
            fis.close();
        }
        try {
            NativeIO.POSIX.posix_fadvise(fis.getFD(), 0, 1024, POSIX_FADV_SEQUENTIAL);
            Assert.fail("Did not throw on bad file");
        } catch (NativeIOException nioe) {
            Assert.assertEquals(EBADF, nioe.getErrno());
        }
        try {
            NativeIO.POSIX.posix_fadvise(null, 0, 1024, POSIX_FADV_SEQUENTIAL);
            Assert.fail("Did not throw on null file");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test(timeout = 30000)
    public void testSyncFileRange() throws Exception {
        FileOutputStream fos = new FileOutputStream(new File(TestNativeIO.TEST_DIR, "testSyncFileRange"));
        try {
            fos.write("foo".getBytes());
            NativeIO.POSIX.sync_file_range(fos.getFD(), 0, 1024, SYNC_FILE_RANGE_WRITE);
            // no way to verify that this actually has synced,
            // but if it doesn't throw, we can assume it worked
        } catch (UnsupportedOperationException uoe) {
            // we should just skip the unit test on machines where we don't
            // have fadvise support
            Assume.assumeTrue(false);
        } finally {
            fos.close();
        }
        try {
            NativeIO.POSIX.sync_file_range(fos.getFD(), 0, 1024, SYNC_FILE_RANGE_WRITE);
            Assert.fail("Did not throw on bad file");
        } catch (NativeIOException nioe) {
            Assert.assertEquals(EBADF, nioe.getErrno());
        }
    }

    @Test(timeout = 30000)
    public void testGetUserName() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        Assert.assertFalse(NativeIO.POSIX.getUserName(0).isEmpty());
    }

    @Test(timeout = 30000)
    public void testGetGroupName() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        Assert.assertFalse(NativeIO.POSIX.getGroupName(0).isEmpty());
    }

    @Test(timeout = 30000)
    public void testRenameTo() throws Exception {
        final File TEST_DIR = GenericTestUtils.getTestDir("renameTest");
        Assume.assumeTrue(TEST_DIR.mkdirs());
        File nonExistentFile = new File(TEST_DIR, "nonexistent");
        File targetFile = new File(TEST_DIR, "target");
        // Test attempting to rename a nonexistent file.
        try {
            NativeIO.renameTo(nonExistentFile, targetFile);
            Assert.fail();
        } catch (NativeIOException e) {
            if (Path.WINDOWS) {
                Assert.assertEquals(String.format("The system cannot find the file specified.%n"), e.getMessage());
            } else {
                Assert.assertEquals(ENOENT, e.getErrno());
            }
        }
        // Test renaming a file to itself.  It should succeed and do nothing.
        File sourceFile = new File(TEST_DIR, "source");
        Assert.assertTrue(sourceFile.createNewFile());
        NativeIO.renameTo(sourceFile, sourceFile);
        // Test renaming a source to a destination.
        NativeIO.renameTo(sourceFile, targetFile);
        // Test renaming a source to a path which uses a file as a directory.
        sourceFile = new File(TEST_DIR, "source");
        Assert.assertTrue(sourceFile.createNewFile());
        File badTarget = new File(targetFile, "subdir");
        try {
            NativeIO.renameTo(sourceFile, badTarget);
            Assert.fail();
        } catch (NativeIOException e) {
            if (Path.WINDOWS) {
                Assert.assertEquals(String.format("The parameter is incorrect.%n"), e.getMessage());
            } else {
                Assert.assertEquals(ENOTDIR, e.getErrno());
            }
        }
        // Test renaming to an existing file
        Assert.assertTrue(targetFile.exists());
        NativeIO.renameTo(sourceFile, targetFile);
    }

    @Test(timeout = 10000)
    public void testMlock() throws Exception {
        Assume.assumeTrue(NativeIO.isAvailable());
        final File TEST_FILE = GenericTestUtils.getTestDir("testMlockFile");
        final int BUF_LEN = 12289;
        byte[] buf = new byte[BUF_LEN];
        int bufSum = 0;
        for (int i = 0; i < (buf.length); i++) {
            buf[i] = ((byte) (i % 60));
            bufSum += buf[i];
        }
        FileOutputStream fos = new FileOutputStream(TEST_FILE);
        try {
            fos.write(buf);
            fos.getChannel().force(true);
        } finally {
            fos.close();
        }
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            // Map file into memory
            fis = new FileInputStream(TEST_FILE);
            channel = fis.getChannel();
            long fileSize = channel.size();
            MappedByteBuffer mapbuf = channel.map(READ_ONLY, 0, fileSize);
            // mlock the buffer
            NativeIO.POSIX.mlock(mapbuf, fileSize);
            // Read the buffer
            int sum = 0;
            for (int i = 0; i < fileSize; i++) {
                sum += mapbuf.get(i);
            }
            Assert.assertEquals("Expected sums to be equal", bufSum, sum);
            // munmap the buffer, which also implicitly unlocks it
            NativeIO.POSIX.munmap(mapbuf);
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    @Test(timeout = 10000)
    public void testGetMemlockLimit() throws Exception {
        Assume.assumeTrue(NativeIO.isAvailable());
        NativeIO.getMemlockLimit();
    }

    @Test(timeout = 30000)
    public void testCopyFileUnbuffered() throws Exception {
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        File srcFile = new File(TestNativeIO.TEST_DIR, (METHOD_NAME + ".src.dat"));
        File dstFile = new File(TestNativeIO.TEST_DIR, (METHOD_NAME + ".dst.dat"));
        final int fileSize = 134217728;// 128 MB

        final int SEED = 48879;
        final int batchSize = 4096;
        final int numBatches = fileSize / batchSize;
        Random rb = new Random(SEED);
        FileChannel channel = null;
        RandomAccessFile raSrcFile = null;
        try {
            raSrcFile = new RandomAccessFile(srcFile, "rw");
            channel = raSrcFile.getChannel();
            byte[] bytesToWrite = new byte[batchSize];
            MappedByteBuffer mapBuf;
            mapBuf = channel.map(READ_WRITE, 0, fileSize);
            for (int i = 0; i < numBatches; i++) {
                rb.nextBytes(bytesToWrite);
                mapBuf.put(bytesToWrite);
            }
            NativeIO.copyFileUnbuffered(srcFile, dstFile);
            Assert.assertEquals(srcFile.length(), dstFile.length());
        } finally {
            IOUtils.cleanupWithLogger(TestNativeIO.LOG, channel);
            IOUtils.cleanupWithLogger(TestNativeIO.LOG, raSrcFile);
            FileUtils.deleteQuietly(TestNativeIO.TEST_DIR);
        }
    }

    @Test(timeout = 10000)
    public void testNativePosixConsts() {
        PlatformAssumptions.assumeNotWindows("Native POSIX constants not required for Windows");
        Assert.assertTrue("Native 0_RDONLY const not set", ((O_RDONLY) >= 0));
        Assert.assertTrue("Native 0_WRONLY const not set", ((O_WRONLY) >= 0));
        Assert.assertTrue("Native 0_RDWR const not set", ((O_RDWR) >= 0));
        Assert.assertTrue("Native 0_CREAT const not set", ((O_CREAT) >= 0));
        Assert.assertTrue("Native 0_EXCL const not set", ((O_EXCL) >= 0));
        Assert.assertTrue("Native 0_NOCTTY const not set", ((O_NOCTTY) >= 0));
        Assert.assertTrue("Native 0_TRUNC const not set", ((O_TRUNC) >= 0));
        Assert.assertTrue("Native 0_APPEND const not set", ((O_APPEND) >= 0));
        Assert.assertTrue("Native 0_NONBLOCK const not set", ((O_NONBLOCK) >= 0));
        Assert.assertTrue("Native 0_SYNC const not set", ((O_SYNC) >= 0));
        Assert.assertTrue("Native S_IFMT const not set", ((S_IFMT) >= 0));
        Assert.assertTrue("Native S_IFIFO const not set", ((S_IFIFO) >= 0));
        Assert.assertTrue("Native S_IFCHR const not set", ((S_IFCHR) >= 0));
        Assert.assertTrue("Native S_IFDIR const not set", ((S_IFDIR) >= 0));
        Assert.assertTrue("Native S_IFBLK const not set", ((S_IFBLK) >= 0));
        Assert.assertTrue("Native S_IFREG const not set", ((S_IFREG) >= 0));
        Assert.assertTrue("Native S_IFLNK const not set", ((S_IFLNK) >= 0));
        Assert.assertTrue("Native S_IFSOCK const not set", ((S_IFSOCK) >= 0));
        Assert.assertTrue("Native S_ISUID const not set", ((S_ISUID) >= 0));
        Assert.assertTrue("Native S_ISGID const not set", ((S_ISGID) >= 0));
        Assert.assertTrue("Native S_ISVTX const not set", ((S_ISVTX) >= 0));
        Assert.assertTrue("Native S_IRUSR const not set", ((S_IRUSR) >= 0));
        Assert.assertTrue("Native S_IWUSR const not set", ((S_IWUSR) >= 0));
        Assert.assertTrue("Native S_IXUSR const not set", ((S_IXUSR) >= 0));
    }

    @Test(timeout = 10000)
    public void testNativeFadviseConsts() {
        Assume.assumeTrue("Fadvise constants not supported", fadvisePossible);
        Assert.assertTrue("Native POSIX_FADV_NORMAL const not set", ((POSIX_FADV_NORMAL) >= 0));
        Assert.assertTrue("Native POSIX_FADV_RANDOM const not set", ((POSIX_FADV_RANDOM) >= 0));
        Assert.assertTrue("Native POSIX_FADV_SEQUENTIAL const not set", ((POSIX_FADV_SEQUENTIAL) >= 0));
        Assert.assertTrue("Native POSIX_FADV_WILLNEED const not set", ((POSIX_FADV_WILLNEED) >= 0));
        Assert.assertTrue("Native POSIX_FADV_DONTNEED const not set", ((POSIX_FADV_DONTNEED) >= 0));
        Assert.assertTrue("Native POSIX_FADV_NOREUSE const not set", ((POSIX_FADV_NOREUSE) >= 0));
    }
}

