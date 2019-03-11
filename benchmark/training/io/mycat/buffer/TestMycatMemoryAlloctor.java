package io.mycat.buffer;


import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author zagnix
 * @unknown 2017-01-18 11:19
 */
public class TestMycatMemoryAlloctor {
    private ConcurrentHashMap<Long, ByteBuf> freeMaps = new ConcurrentHashMap<>();

    final MyCatMemoryAllocator memoryAllocator = new MyCatMemoryAllocator(((Runtime.getRuntime().availableProcessors()) * 2));

    @Test
    public void testMemAlloc() {
        /**
         * 20000000
         */
        for (int i = 0; i < 10000; i++) {
            ByteBuffer byteBuffer = getBuffer(8194);
            byteBuffer.put("helll world".getBytes());
            byteBuffer.flip();
            byte[] src = new byte[byteBuffer.remaining()];
            byteBuffer.get(src);
            Assert.assertEquals("helll world", new String(src));
            free(byteBuffer);
        }
    }
}

