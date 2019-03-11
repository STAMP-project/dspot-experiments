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
package alluxio.worker.grpc;


import PropertyKey.MASTER_MOUNT_TABLE_ROOT_UFS;
import PropertyKey.WORKER_TIERED_STORE_LEVEL0_DIRS_PATH;
import PropertyKey.WORKER_TIERED_STORE_LEVELS;
import Status.Code.NOT_FOUND;
import alluxio.AlluxioTestDirectory;
import alluxio.ConfigurationRule;
import alluxio.conf.PropertyKey;
import alluxio.conf.ServerConfiguration;
import alluxio.network.protocol.databuffer.DataBuffer;
import alluxio.worker.block.BlockStore;
import alluxio.worker.block.BlockWorker;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;


public class UfsFallbackBlockWriteHandlerTest extends AbstractWriteHandlerTest {
    private static final long TEST_SESSION_ID = 123L;

    private static final long TEST_WORKER_ID = 456L;

    private static final int PARTIAL_WRITTEN = 512;

    private OutputStream mOutputStream;

    private BlockWorker mBlockWorker;

    private BlockStore mBlockStore;

    /**
     * The file used to hold the data written by the test.
     */
    private File mFile;

    private long mPartialChecksum;

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(new HashMap<PropertyKey, String>() {
        {
            put(MASTER_MOUNT_TABLE_ROOT_UFS, AlluxioTestDirectory.createTemporaryDirectory("UfsFallbackBlockWriteHandlerTest-RootUfs").getAbsolutePath());
            put(WORKER_TIERED_STORE_LEVEL0_DIRS_PATH, AlluxioTestDirectory.createTemporaryDirectory("UfsFallbackBlockWriteHandlerTest-WorkerDataFolder").getAbsolutePath());
            put(WORKER_TIERED_STORE_LEVELS, "1");
        }
    }, ServerConfiguration.global());

    @Test
    public void noTempBlockFound() throws Exception {
        // remove the block partially created
        mBlockStore.abortBlock(UfsFallbackBlockWriteHandlerTest.TEST_SESSION_ID, AbstractWriteHandlerTest.TEST_BLOCK_ID);
        mWriteHandler.write(newFallbackInitRequest(UfsFallbackBlockWriteHandlerTest.PARTIAL_WRITTEN));
        checkErrorCode(mResponseObserver, NOT_FOUND);
    }

    @Test
    public void tempBlockWritten() throws Exception {
        DataBuffer buffer = newDataBuffer(AbstractWriteHandlerTest.CHUNK_SIZE);
        long checksum = (mPartialChecksum) + (AbstractWriteHandlerTest.getChecksum(buffer));
        mWriteHandler.write(newFallbackInitRequest(UfsFallbackBlockWriteHandlerTest.PARTIAL_WRITTEN));
        mWriteHandler.write(newWriteRequest(buffer));
        mWriteHandler.onCompleted();
        checkComplete(mResponseObserver);
        checkWriteData(checksum, ((UfsFallbackBlockWriteHandlerTest.PARTIAL_WRITTEN) + (AbstractWriteHandlerTest.CHUNK_SIZE)));
    }
}

