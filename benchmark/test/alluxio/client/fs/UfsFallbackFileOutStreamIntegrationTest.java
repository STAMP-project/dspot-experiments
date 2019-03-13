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


import FileSystem.Factory;
import PersistenceState.PERSISTED;
import PersistenceState.TO_BE_PERSISTED;
import WritePType.ASYNC_THROUGH;
import alluxio.AlluxioURI;
import alluxio.client.file.FileSystem;
import alluxio.client.file.URIStatus;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.CreateFilePOptions;
import alluxio.testutils.IntegrationTestUtils;
import alluxio.util.CommonUtils;
import alluxio.util.io.PathUtils;
import java.io.Closeable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Integration tests for {@link FileOutStream} of under storage type being async
 * persist.
 */
@RunWith(Parameterized.class)
public class UfsFallbackFileOutStreamIntegrationTest extends AbstractFileOutStreamIntegrationTest {
    protected static final int WORKER_MEMORY_SIZE = 1500;

    protected static final int BUFFER_BYTES = 100;

    @Parameterized.Parameter
    public int mFileLength;

    @Parameterized.Parameter(1)
    public int mBlockSize;

    @Parameterized.Parameter(2)
    public int mUserFileBufferSize;

    @Test
    public void shortCircuitWrite() throws Exception {
        try (Closeable c = toResource()) {
            FileSystem fs = Factory.create(ServerConfiguration.global());
            AlluxioURI filePath = new AlluxioURI(PathUtils.uniqPath());
            CreateFilePOptions op = CreateFilePOptions.newBuilder().setWriteType(ASYNC_THROUGH).setRecursive(true).build();
            writeIncreasingBytesToFile(fs, filePath, mFileLength, op);
            CommonUtils.sleepMs(1);
            // check the file is completed but not persisted
            URIStatus status = mFileSystem.getStatus(filePath);
            Assert.assertEquals(TO_BE_PERSISTED.toString(), status.getPersistenceState());
            Assert.assertTrue(status.isCompleted());
            IntegrationTestUtils.waitForPersist(mLocalAlluxioClusterResource, filePath);
            status = mFileSystem.getStatus(filePath);
            Assert.assertEquals(PERSISTED.toString(), status.getPersistenceState());
            checkFileInAlluxio(filePath, mFileLength);
            checkFileInUnderStorage(filePath, mFileLength);
        }
    }
}

