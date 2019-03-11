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
package org.apache.flink.runtime.io.network.partition;


import org.apache.flink.runtime.io.network.buffer.BufferBuilderTestUtils;
import org.apache.flink.runtime.io.network.buffer.BufferConsumer;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic subpartition behaviour tests.
 */
public abstract class SubpartitionTestBase extends TestLogger {
    // ------------------------------------------------------------------------
    @Test
    public void testAddAfterFinish() throws Exception {
        final ResultSubpartition subpartition = createSubpartition();
        try {
            subpartition.finish();
            Assert.assertEquals(1, subpartition.getTotalNumberOfBuffers());
            Assert.assertEquals(0, subpartition.getTotalNumberOfBytes());// only updated after consuming the buffers

            Assert.assertEquals(1, subpartition.getTotalNumberOfBuffers());
            Assert.assertEquals(0, subpartition.getBuffersInBacklog());
            Assert.assertEquals(0, subpartition.getTotalNumberOfBytes());// only updated after consuming the buffers

            BufferConsumer bufferConsumer = BufferBuilderTestUtils.createFilledBufferConsumer(4096, 4096);
            Assert.assertFalse(subpartition.add(bufferConsumer));
            Assert.assertEquals(1, subpartition.getTotalNumberOfBuffers());
            Assert.assertEquals(0, subpartition.getBuffersInBacklog());
            Assert.assertEquals(0, subpartition.getTotalNumberOfBytes());// only updated after consuming the buffers

        } finally {
            if (subpartition != null) {
                subpartition.release();
            }
        }
    }

    @Test
    public void testAddAfterRelease() throws Exception {
        final ResultSubpartition subpartition = createSubpartition();
        try {
            subpartition.release();
            Assert.assertEquals(0, subpartition.getTotalNumberOfBuffers());
            Assert.assertEquals(0, subpartition.getTotalNumberOfBytes());
            Assert.assertEquals(0, subpartition.getTotalNumberOfBuffers());
            Assert.assertEquals(0, subpartition.getBuffersInBacklog());
            Assert.assertEquals(0, subpartition.getTotalNumberOfBytes());
            BufferConsumer bufferConsumer = BufferBuilderTestUtils.createFilledBufferConsumer(4096, 4096);
            Assert.assertFalse(subpartition.add(bufferConsumer));
            Assert.assertEquals(0, subpartition.getTotalNumberOfBuffers());
            Assert.assertEquals(0, subpartition.getBuffersInBacklog());
            Assert.assertEquals(0, subpartition.getTotalNumberOfBytes());
        } finally {
            if (subpartition != null) {
                subpartition.release();
            }
        }
    }

    @Test
    public void testReleaseParent() throws Exception {
        final ResultSubpartition partition = createSubpartition();
        verifyViewReleasedAfterParentRelease(partition);
    }

    @Test
    public void testReleaseParentAfterSpilled() throws Exception {
        final ResultSubpartition partition = createSubpartition();
        partition.releaseMemory();
        verifyViewReleasedAfterParentRelease(partition);
    }
}

