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
import alluxio.worker.block.io.BlockWriter;
import java.io.File;
import org.junit.Test;


/**
 * Unit tests for {@link BlockWriteHandler}.
 */
public final class BlockWriteHandlerTest extends AbstractWriteHandlerTest {
    private BlockWorker mBlockWorker;

    private BlockWriter mBlockWriter;

    private File mFile;

    @Test
    public void writeFailure() throws Exception {
        mWriteHandler.write(newWriteRequestCommand(0));
        mBlockWriter.close();
        mWriteHandler.write(newWriteRequest(newDataBuffer(AbstractWriteHandlerTest.CHUNK_SIZE)));
        checkErrorCode(mResponseObserver, FAILED_PRECONDITION);
    }
}

