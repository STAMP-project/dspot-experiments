package io.protostuff;


import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LinkBufferTest {
    @Test
    public void testWriteLargeVar32() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        b.writeVarInt32(Integer.MAX_VALUE);
        Assert.assertEquals(1, b.getBuffers().size());
        Assert.assertEquals(5, b.getBuffers().get(0).remaining());
    }

    @Test
    public void testBasics() throws Exception {
        LinkBuffer buf = new LinkBuffer(8);
        // put in 4 longs:
        ByteBuffer bigBuf = ByteBuffer.allocate(100);
        bigBuf.limit(100);
        // each one of these writes gets its own byte buffer.
        buf.writeByteBuffer(bigBuf);// 0

        buf.writeByteArray(new byte[100]);// 1

        buf.writeByteArray(new byte[2]);// 2

        buf.writeByteArray(new byte[8]);// 3

        buf.writeInt64(1);
        buf.writeInt64(2);
        buf.writeInt64(3);
        buf.writeInt64(4);
        List<ByteBuffer> lbb = buf.finish();
        Assert.assertEquals(8, lbb.size());
        Assert.assertEquals(100, lbb.get(0).remaining());
        Assert.assertEquals(100, lbb.get(1).remaining());
        Assert.assertEquals(2, lbb.get(2).remaining());
        Assert.assertEquals(8, lbb.get(3).remaining());
        for (int i = 3; i < (lbb.size()); i++) {
            Assert.assertEquals(8, lbb.get(i).remaining());
        }
    }

    @Test
    public void testGetBuffers() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        b.writeInt32(42);
        b.writeInt32(43);
        b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        Assert.assertEquals(2, buffers.size());
        Assert.assertEquals(8, buffers.get(0).remaining());
        Assert.assertEquals(4, buffers.get(1).remaining());
        Assert.assertEquals(42, buffers.get(0).getInt());
        Assert.assertEquals(43, buffers.get(0).getInt());
        Assert.assertEquals(44, buffers.get(1).getInt());
    }

    @Test
    public void testGetBuffersAndAppendData() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        b.writeInt32(42);
        b.writeInt32(43);
        b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        b.writeInt32(45);// new data should not appear in buffers

        Assert.assertEquals(2, buffers.size());
        Assert.assertEquals(8, buffers.get(0).remaining());
        Assert.assertEquals(4, buffers.get(1).remaining());
    }
}

