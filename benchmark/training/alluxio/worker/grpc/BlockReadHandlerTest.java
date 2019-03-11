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


import Status.Code.FAILED_PRECONDITION;
import alluxio.worker.block.BlockWorker;
import alluxio.worker.block.io.BlockReader;
import org.junit.Test;


public final class BlockReadHandlerTest extends ReadHandlerTest {
    private BlockWorker mBlockWorker;

    private BlockReader mBlockReader;

    /**
     * Tests read failure.
     */
    @Test
    public void readFailure() throws Exception {
        long fileSize = ((ReadHandlerTest.CHUNK_SIZE) * 10) + 1;
        populateInputFile(0, 0, (fileSize - 1));
        mBlockReader.close();
        mReadHandlerNoException.onNext(buildReadRequest(0, fileSize));
        checkErrorCode(mResponseObserver, FAILED_PRECONDITION);
    }
}

