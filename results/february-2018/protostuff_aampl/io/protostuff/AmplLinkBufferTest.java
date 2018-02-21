package io.protostuff;


public class AmplLinkBufferTest {
    @org.junit.Test
    public void testBasics() throws java.lang.Exception {
        io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
        // put in 4 longs:
        java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
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
        java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
        org.junit.Assert.assertEquals(8, lbb.size());
        org.junit.Assert.assertEquals(100, lbb.get(0).remaining());
        org.junit.Assert.assertEquals(100, lbb.get(1).remaining());
        org.junit.Assert.assertEquals(2, lbb.get(2).remaining());
        org.junit.Assert.assertEquals(8, lbb.get(3).remaining());
        for (int i = 3; i < (lbb.size()); i++) {
            org.junit.Assert.assertEquals(8, lbb.get(i).remaining());
        }
    }

    @org.junit.Test
    public void testGetBuffers() throws java.lang.Exception {
        io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
        b.writeInt32(42);
        b.writeInt32(43);
        b.writeInt32(44);
        java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
        org.junit.Assert.assertEquals(2, buffers.size());
        org.junit.Assert.assertEquals(8, buffers.get(0).remaining());
        org.junit.Assert.assertEquals(4, buffers.get(1).remaining());
        org.junit.Assert.assertEquals(42, buffers.get(0).getInt());
        org.junit.Assert.assertEquals(43, buffers.get(0).getInt());
        org.junit.Assert.assertEquals(44, buffers.get(1).getInt());
    }

    @org.junit.Test
    public void testGetBuffersAndAppendData() throws java.lang.Exception {
        io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
        b.writeInt32(42);
        b.writeInt32(43);
        b.writeInt32(44);
        java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
        b.writeInt32(45);// new data should not appear in buffers

        org.junit.Assert.assertEquals(2, buffers.size());
        org.junit.Assert.assertEquals(8, buffers.get(0).remaining());
        org.junit.Assert.assertEquals(4, buffers.get(1).remaining());
    }
}

