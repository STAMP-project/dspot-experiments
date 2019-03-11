/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.fs;


import PropertyKey.USER_FILE_BUFFER_BYTES;
import PropertyKey.USER_FILE_WAITCOMPLETED_POLL_MS;
import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.client.file.FileSystemUtils;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.AlluxioException;
import alluxio.grpc.CreateFilePOptions;
import alluxio.master.LocalAlluxioJobCluster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.CommonUtils;
import alluxio.util.io.PathUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link alluxio.client.file.FileSystemUtils}.
 */
public class FileSystemUtilsIntegrationTest extends BaseIntegrationTest {
    private static final int USER_QUOTA_UNIT_BYTES = 1000;

    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(USER_FILE_BUFFER_BYTES, FileSystemUtilsIntegrationTest.USER_QUOTA_UNIT_BYTES).build();

    public static LocalAlluxioJobCluster sJobCluster;

    private static CreateFilePOptions sWriteBoth;

    private static FileSystem sFileSystem = null;

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test
    public void waitCompletedTest1() throws AlluxioException, IOException, InterruptedException {
        final String uniqPath = PathUtils.uniqPath();
        final int numWrites = 4;// random value chosen through a fair dice roll :P

        final AlluxioURI uri = new AlluxioURI(uniqPath);
        final Runnable writer = new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutStream os = FileSystemUtilsIntegrationTest.sFileSystem.createFile(uri, FileSystemUtilsIntegrationTest.sWriteBoth);
                    boolean completed = FileSystemUtilsIntegrationTest.sFileSystem.getStatus(uri).isCompleted();
                    Assert.assertFalse(completed);
                    for (int i = 0; i < numWrites; i++) {
                        os.write(42);
                        CommonUtils.sleepMs(200);
                    }
                    os.close();
                    completed = FileSystemUtilsIntegrationTest.sFileSystem.getStatus(uri).isCompleted();
                    Assert.assertTrue(completed);
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        final Runnable waiter = new Runnable() {
            @Override
            public void run() {
                try {
                    boolean completed = FileSystemUtils.waitCompleted(FileSystemUtilsIntegrationTest.sFileSystem, uri);
                    Assert.assertTrue(completed);
                    completed = FileSystemUtilsIntegrationTest.sFileSystem.getStatus(uri).isCompleted();
                    Assert.assertTrue(completed);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                }
            }
        };
        final Thread waitingThread = new Thread(waiter);
        waitingThread.start();
        final Thread writingThread = new Thread(writer);
        writingThread.start();
        waitingThread.join();
        writingThread.join();
    }

    @Test
    public void waitCompletedTest2() throws AlluxioException, IOException, InterruptedException {
        final String uniqPath = PathUtils.uniqPath();
        final int numWrites = 4;// random value chosen through a fair dice roll :P

        final AlluxioURI uri = new AlluxioURI(uniqPath);
        final Runnable writer = new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutStream os = FileSystemUtilsIntegrationTest.sFileSystem.createFile(uri, FileSystemUtilsIntegrationTest.sWriteBoth);
                    boolean completed = FileSystemUtilsIntegrationTest.sFileSystem.getStatus(uri).isCompleted();
                    Assert.assertFalse(completed);
                    // four writes that will take > 600ms due to the sleeps
                    for (int i = 0; i < numWrites; i++) {
                        os.write(42);
                        CommonUtils.sleepMs(200);
                    }
                    os.close();
                    completed = FileSystemUtilsIntegrationTest.sFileSystem.getStatus(uri).isCompleted();
                    Assert.assertTrue(completed);
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        };
        final Runnable waiter = new Runnable() {
            @Override
            public void run() {
                try {
                    // set the slow default polling period to a more sensible value, in order
                    // to speed up the tests artificial waiting times
                    String original = ServerConfiguration.get(USER_FILE_WAITCOMPLETED_POLL_MS);
                    ServerConfiguration.set(USER_FILE_WAITCOMPLETED_POLL_MS, "100");
                    try {
                        // The write will take at most 600ms I am waiting for at most 400ms - epsilon.
                        boolean completed = FileSystemUtils.waitCompleted(FileSystemUtilsIntegrationTest.sFileSystem, uri, 300, TimeUnit.MILLISECONDS);
                        Assert.assertFalse(completed);
                        completed = FileSystemUtilsIntegrationTest.sFileSystem.getStatus(uri).isCompleted();
                        Assert.assertFalse(completed);
                    } finally {
                        ServerConfiguration.set(USER_FILE_WAITCOMPLETED_POLL_MS, original);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                }
            }
        };
        final Thread waitingThread = new Thread(waiter);
        waitingThread.start();
        final Thread writingThread = new Thread(writer);
        writingThread.start();
        waitingThread.join();
        writingThread.join();
    }

    @Test
    public void waitPersistTimeoutTest() throws Exception {
        String path = PathUtils.uniqPath();
        AlluxioURI alluxioPath = new AlluxioURI(path);
        FileSystemTestUtils.createByteFile(FileSystemUtilsIntegrationTest.sFileSystem, path, MUST_CACHE, 4096);
        Assert.assertFalse("File cannot yet be persisted", FileSystemUtilsIntegrationTest.sFileSystem.getStatus(alluxioPath).isPersisted());
        mThrown.expect(TimeoutException.class);
        FileSystemUtils.persistAndWait(FileSystemUtilsIntegrationTest.sFileSystem, alluxioPath, 1);// 1ms timeout

    }

    @Test
    public void waitPersistIndefiniteTimeoutTest() throws Exception {
        String path = PathUtils.uniqPath();
        AlluxioURI alluxioPath = new AlluxioURI(path);
        FileSystemTestUtils.createByteFile(FileSystemUtilsIntegrationTest.sFileSystem, path, MUST_CACHE, 4096);
        Assert.assertFalse("File cannot yet be persisted", FileSystemUtilsIntegrationTest.sFileSystem.getStatus(alluxioPath).isPersisted());
        FileSystemUtils.persistAndWait(FileSystemUtilsIntegrationTest.sFileSystem, alluxioPath, (-1));
        Assert.assertTrue("File must be persisted", FileSystemUtilsIntegrationTest.sFileSystem.getStatus(alluxioPath).isPersisted());
    }
}

