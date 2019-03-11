package org.kairosdb.util;


import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;


public class ByteBufferDataInputTest {
    @Test
    public void test_readUnsignedShort42() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(((short) (42)));
        buf.rewind();
        ByteBufferDataInput dataInput = new ByteBufferDataInput(buf);
        Assert.assertEquals(42, dataInput.readUnsignedShort());
    }

    @Test
    public void test_readUnsignedShort255() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(((short) (255)));
        buf.rewind();
        ByteBufferDataInput dataInput = new ByteBufferDataInput(buf);
        Assert.assertEquals(255, dataInput.readUnsignedShort());
    }

    @Test
    public void test_readUnsignedShort1024() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(((short) (1024)));
        buf.rewind();
        ByteBufferDataInput dataInput = new ByteBufferDataInput(buf);
        Assert.assertEquals(1024, dataInput.readUnsignedShort());
    }

    @Test
    public void test_readUnsignedShort32767() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(((short) (32767)));
        buf.rewind();
        ByteBufferDataInput dataInput = new ByteBufferDataInput(buf);
        Assert.assertEquals(32767, dataInput.readUnsignedShort());
    }

    @Test
    public void test_readUnsignedShort65000() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(((short) (65000)));
        buf.rewind();
        ByteBufferDataInput dataInput = new ByteBufferDataInput(buf);
        Assert.assertEquals(65000, dataInput.readUnsignedShort());
    }
}

