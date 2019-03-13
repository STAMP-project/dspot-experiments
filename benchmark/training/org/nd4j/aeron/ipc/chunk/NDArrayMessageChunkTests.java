/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.aeron.ipc.chunk;


import java.nio.ByteBuffer;
import org.agrona.DirectBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.aeron.ipc.NDArrayMessage;
import org.nd4j.aeron.util.BufferUtil;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by agibsonccc on 11/20/16.
 */
public class NDArrayMessageChunkTests {
    @Test
    public void testChunkSerialization() {
        NDArrayMessage message = NDArrayMessage.wholeArrayUpdate(Nd4j.ones(1000));
        int chunkSize = 128;
        int numChunks = NDArrayMessage.numChunksForMessage(message, chunkSize);
        NDArrayMessageChunk[] chunks = NDArrayMessage.chunks(message, chunkSize);
        Assert.assertEquals(numChunks, chunks.length);
        for (int i = 1; i < numChunks; i++) {
            Assert.assertEquals(chunks[0].getMessageType(), chunks[i].getMessageType());
            Assert.assertEquals(chunks[0].getId(), chunks[i].getId());
            Assert.assertEquals(chunks[0].getChunkSize(), chunks[i].getChunkSize());
            Assert.assertEquals(chunks[0].getNumChunks(), chunks[i].getNumChunks());
        }
        ByteBuffer[] concat = new ByteBuffer[chunks.length];
        for (int i = 0; i < (concat.length); i++)
            concat[i] = chunks[i].getData();

        DirectBuffer buffer = NDArrayMessage.toBuffer(message);
        // test equality of direct byte buffer contents vs chunked
        ByteBuffer byteBuffer = buffer.byteBuffer();
        ByteBuffer concatAll = BufferUtil.concat(concat, buffer.capacity());
        byte[] arrays = new byte[byteBuffer.capacity()];
        byteBuffer.rewind();
        byteBuffer.get(arrays);
        byte[] arrays2 = new byte[concatAll.capacity()];
        concatAll.rewind();
        concatAll.get(arrays2);
        Assert.assertArrayEquals(arrays, arrays2);
        NDArrayMessage message1 = NDArrayMessage.fromChunks(chunks);
        Assert.assertEquals(message, message1);
    }
}

