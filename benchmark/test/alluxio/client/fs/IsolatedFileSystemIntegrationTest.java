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


import HeartbeatContext.WORKER_BLOCK_SYNC;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import PropertyKey.USER_FILE_BUFFER_BYTES;
import PropertyKey.WORKER_MEMORY_SIZE;
import PropertyKey.WORKER_TIERED_STORE_RESERVER_ENABLED;
import alluxio.AlluxioURI;
import alluxio.Constants;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.client.file.URIStatus;
import alluxio.grpc.CreateFilePOptions;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.io.PathUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests on Alluxio client (do not reuse the {@link LocalAlluxioCluster}).
 */
public class IsolatedFileSystemIntegrationTest extends BaseIntegrationTest {
    private static final int WORKER_CAPACITY_BYTES = 200 * (Constants.MB);

    private static final int USER_QUOTA_UNIT_BYTES = 1000;

    @ClassRule
    public static ManuallyScheduleHeartbeat sManuallySchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.WORKER_BLOCK_SYNC);

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(WORKER_MEMORY_SIZE, IsolatedFileSystemIntegrationTest.WORKER_CAPACITY_BYTES).setProperty(USER_BLOCK_SIZE_BYTES_DEFAULT, (100 * (Constants.MB))).setProperty(USER_FILE_BUFFER_BYTES, IsolatedFileSystemIntegrationTest.USER_QUOTA_UNIT_BYTES).setProperty(WORKER_TIERED_STORE_RESERVER_ENABLED, false).build();

    private FileSystem mFileSystem = null;

    private CreateFilePOptions mWriteBoth;

    @Test
    public void lockBlockTest1() throws Exception {
        String uniqPath = PathUtils.uniqPath();
        int numOfFiles = 5;
        int fileSize = (IsolatedFileSystemIntegrationTest.WORKER_CAPACITY_BYTES) / numOfFiles;
        List<AlluxioURI> files = new ArrayList<>();
        for (int k = 0; k < numOfFiles; k++) {
            FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + k), fileSize, mWriteBoth);
            files.add(new AlluxioURI((uniqPath + k)));
        }
        for (int k = 0; k < numOfFiles; k++) {
            Assert.assertEquals(100, mFileSystem.getStatus(files.get(k)).getInAlluxioPercentage());
        }
        FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + numOfFiles), fileSize, mWriteBoth);
        files.add(new AlluxioURI((uniqPath + numOfFiles)));
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        Assert.assertFalse(((mFileSystem.getStatus(files.get(0)).getInAlluxioPercentage()) == 100));
        for (int k = 1; k <= numOfFiles; k++) {
            Assert.assertTrue(((mFileSystem.getStatus(files.get(k)).getInAlluxioPercentage()) == 100));
        }
    }

    @Test
    public void lockBlockTest2() throws Exception {
        String uniqPath = PathUtils.uniqPath();
        FileInStream is;
        ByteBuffer buf;
        int numOfFiles = 5;
        int fileSize = (IsolatedFileSystemIntegrationTest.WORKER_CAPACITY_BYTES) / numOfFiles;
        List<AlluxioURI> files = new ArrayList<>();
        for (int k = 0; k < numOfFiles; k++) {
            FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + k), fileSize, mWriteBoth);
            files.add(new AlluxioURI((uniqPath + k)));
        }
        for (int k = 0; k < numOfFiles; k++) {
            URIStatus info = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
            is = mFileSystem.openFile(files.get(k), FileSystemTestUtils.toOpenFileOptions(mWriteBoth));
            buf = ByteBuffer.allocate(((int) (info.getBlockSizeBytes())));
            Assert.assertTrue(((is.read(buf.array())) != (-1)));
            is.close();
        }
        FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + numOfFiles), fileSize, mWriteBoth);
        files.add(new AlluxioURI((uniqPath + numOfFiles)));
        for (int k = 1; k < numOfFiles; k++) {
            URIStatus info = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
        }
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        URIStatus info = mFileSystem.getStatus(files.get(numOfFiles));
        Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
    }

    @Test
    public void lockBlockTest3() throws Exception {
        String uniqPath = PathUtils.uniqPath();
        FileInStream is;
        ByteBuffer buf;
        int numOfFiles = 5;
        int fileSize = (IsolatedFileSystemIntegrationTest.WORKER_CAPACITY_BYTES) / numOfFiles;
        List<AlluxioURI> files = new ArrayList<>();
        for (int k = 0; k < numOfFiles; k++) {
            FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + k), fileSize, mWriteBoth);
            files.add(new AlluxioURI((uniqPath + k)));
        }
        for (int k = 0; k < numOfFiles; k++) {
            URIStatus info = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
            is = mFileSystem.openFile(files.get(k), FileSystemTestUtils.toOpenFileOptions(mWriteBoth));
            buf = ByteBuffer.allocate(((int) (info.getBlockSizeBytes())));
            int r = is.read(buf.array());
            if (k < (numOfFiles - 1)) {
                Assert.assertTrue((r != (-1)));
            }
            is.close();
        }
        FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + numOfFiles), fileSize, mWriteBoth);
        files.add(new AlluxioURI((uniqPath + numOfFiles)));
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        URIStatus info = mFileSystem.getStatus(files.get(0));
        Assert.assertFalse(((info.getInAlluxioPercentage()) == 100));
        for (int k = 1; k <= numOfFiles; k++) {
            info = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
        }
    }

    @Test
    public void unlockBlockTest1() throws Exception {
        String uniqPath = PathUtils.uniqPath();
        FileInStream is;
        ByteBuffer buf;
        int numOfFiles = 5;
        int fileSize = (IsolatedFileSystemIntegrationTest.WORKER_CAPACITY_BYTES) / numOfFiles;
        List<AlluxioURI> files = new ArrayList<>();
        for (int k = 0; k < numOfFiles; k++) {
            FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + k), fileSize, mWriteBoth);
            files.add(new AlluxioURI((uniqPath + k)));
        }
        for (int k = 0; k < numOfFiles; k++) {
            URIStatus info = mFileSystem.getStatus(files.get(k));
            is = mFileSystem.openFile(files.get(k), FileSystemTestUtils.toOpenFileOptions(mWriteBoth));
            buf = ByteBuffer.allocate(((int) (info.getBlockSizeBytes())));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
            Assert.assertTrue(((is.read(buf.array())) != (-1)));
            is.close();
        }
        FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + numOfFiles), fileSize, mWriteBoth);
        files.add(new AlluxioURI((uniqPath + numOfFiles)));
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        URIStatus info = mFileSystem.getStatus(files.get(0));
        Assert.assertFalse(((info.getInAlluxioPercentage()) == 100));
        for (int k = 1; k <= numOfFiles; k++) {
            URIStatus in = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((in.getInAlluxioPercentage()) == 100));
        }
    }

    @Test
    public void unlockBlockTest2() throws Exception {
        String uniqPath = PathUtils.uniqPath();
        FileInStream is;
        ByteBuffer buf;
        int numOfFiles = 5;
        int fileSize = (IsolatedFileSystemIntegrationTest.WORKER_CAPACITY_BYTES) / numOfFiles;
        List<AlluxioURI> files = new ArrayList<>();
        for (int k = 0; k < numOfFiles; k++) {
            FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + k), fileSize, mWriteBoth);
            files.add(new AlluxioURI((uniqPath + k)));
        }
        for (int k = 0; k < numOfFiles; k++) {
            URIStatus info = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
            is = mFileSystem.openFile(files.get(k), FileSystemTestUtils.toOpenFileOptions(mWriteBoth));
            buf = ByteBuffer.allocate(((int) (info.getBlockSizeBytes())));
            Assert.assertTrue(((is.read(buf.array())) != (-1)));
            is.seek(0);
            buf.clear();
            Assert.assertTrue(((is.read(buf.array())) != (-1)));
            is.close();
        }
        FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + numOfFiles), fileSize, mWriteBoth);
        files.add(new AlluxioURI((uniqPath + numOfFiles)));
        for (int k = 1; k < numOfFiles; k++) {
            URIStatus info = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
        }
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        URIStatus info = mFileSystem.getStatus(files.get(numOfFiles));
        Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
    }

    @Test
    public void unlockBlockTest3() throws Exception {
        String uniqPath = PathUtils.uniqPath();
        FileInStream is;
        ByteBuffer buf1;
        ByteBuffer buf2;
        int numOfFiles = 5;
        int fileSize = (IsolatedFileSystemIntegrationTest.WORKER_CAPACITY_BYTES) / numOfFiles;
        List<AlluxioURI> files = new ArrayList<>();
        for (int k = 0; k < numOfFiles; k++) {
            FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + k), fileSize, mWriteBoth);
            files.add(new AlluxioURI((uniqPath + k)));
        }
        for (int k = 0; k < numOfFiles; k++) {
            URIStatus info = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((info.getInAlluxioPercentage()) == 100));
            is = mFileSystem.openFile(files.get(k), FileSystemTestUtils.toOpenFileOptions(mWriteBoth));
            buf1 = ByteBuffer.allocate(((int) (info.getBlockSizeBytes())));
            Assert.assertTrue(((is.read(buf1.array())) != (-1)));
            buf2 = ByteBuffer.allocate(((int) (info.getBlockSizeBytes())));
            is.seek(0);
            Assert.assertTrue(((is.read(buf2.array())) != (-1)));
            is.close();
        }
        FileSystemTestUtils.createByteFile(mFileSystem, (uniqPath + numOfFiles), fileSize, mWriteBoth);
        files.add(new AlluxioURI((uniqPath + numOfFiles)));
        HeartbeatScheduler.execute(WORKER_BLOCK_SYNC);
        URIStatus info = mFileSystem.getStatus(files.get(0));
        Assert.assertFalse(((info.getInAlluxioPercentage()) == 100));
        for (int k = 1; k <= numOfFiles; k++) {
            URIStatus in = mFileSystem.getStatus(files.get(k));
            Assert.assertTrue(((in.getInAlluxioPercentage()) == 100));
        }
    }
}

