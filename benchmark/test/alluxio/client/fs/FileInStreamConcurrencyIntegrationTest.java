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


import PropertyKey.USER_BLOCK_MASTER_CLIENT_THREADS;
import alluxio.AlluxioURI;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.CreateFilePOptions;
import alluxio.test.util.ConcurrencyUtils;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.io.PathUtils;
import com.google.common.base.Throwables;
import java.util.ArrayList;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Tests the concurrency of {@link FileInStream}.
 */
public final class FileInStreamConcurrencyIntegrationTest extends BaseIntegrationTest {
    private static final int BLOCK_SIZE = 30;

    private static int sNumReadThreads = (ServerConfiguration.getInt(USER_BLOCK_MASTER_CLIENT_THREADS)) * 10;

    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    private static FileSystem sFileSystem = null;

    private static CreateFilePOptions sWriteAlluxio;

    /**
     * Tests the concurrent read of {@link FileInStream}.
     */
    @Test
    public void FileInStreamConcurrency() throws Exception {
        String uniqPath = PathUtils.uniqPath();
        FileSystemTestUtils.createByteFile(FileInStreamConcurrencyIntegrationTest.sFileSystem, uniqPath, ((FileInStreamConcurrencyIntegrationTest.BLOCK_SIZE) * 2), FileInStreamConcurrencyIntegrationTest.sWriteAlluxio);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < (FileInStreamConcurrencyIntegrationTest.sNumReadThreads); i++) {
            threads.add(new Thread(new FileInStreamConcurrencyIntegrationTest.FileRead(new AlluxioURI(uniqPath))));
        }
        ConcurrencyUtils.assertConcurrent(threads, 100);
    }

    class FileRead implements Runnable {
        private final AlluxioURI mUri;

        FileRead(AlluxioURI uri) {
            mUri = uri;
        }

        @Override
        public void run() {
            try (FileInStream stream = FileInStreamConcurrencyIntegrationTest.sFileSystem.openFile(mUri)) {
                stream.read();
            } catch (Exception e) {
                Throwables.propagate(e);
            }
        }
    }
}

