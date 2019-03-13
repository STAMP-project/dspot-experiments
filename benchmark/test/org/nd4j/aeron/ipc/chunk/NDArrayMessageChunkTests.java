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

