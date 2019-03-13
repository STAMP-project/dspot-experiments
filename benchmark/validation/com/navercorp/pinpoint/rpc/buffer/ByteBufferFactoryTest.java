package com.navercorp.pinpoint.rpc.buffer;


import ByteBufferFactory.DEFAULT_BYTE_ORDER;
import java.nio.ByteBuffer;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class ByteBufferFactoryTest {
    @Test
    public void directByteBufferFactoryTest() throws Exception {
        ByteBufferFactory byteBufferFactory = ByteBufferFactoryLocator.getFactory("direct");
        ByteBuffer buffer = byteBufferFactory.getBuffer(20);
        assertBufferOrder(buffer, DEFAULT_BYTE_ORDER);
        assertBufferType(buffer, true);
    }

    @Test
    public void heapByteBufferFactoryTest() throws Exception {
        ByteBufferFactory byteBufferFactory = ByteBufferFactoryLocator.getFactory("heap");
        ByteBuffer buffer = byteBufferFactory.getBuffer(20);
        assertBufferOrder(buffer, DEFAULT_BYTE_ORDER);
        assertBufferType(buffer, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownByteBufferFactoryTest() throws Exception {
        ByteBufferFactory byteBufferFactory = ByteBufferFactoryLocator.getFactory("unknown");
    }
}

