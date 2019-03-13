package org.gridkit.jvmtool.heapdump.io;


import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Test;


public class PagedVirtualMemoryTest {
    @Test
    public void singleRun() {
        PagedVirtualMemoryTest.TestMemory mem = new PagedVirtualMemoryTest.TestMemory();
        setLimit((16 << 10));
        for (int i = 0; i != 16; ++i) {
            verify(mem, (i << 10), (1 << 10));
        }
        System.out.println(mem.getFaultCount());
    }

    @Test
    public void randomAccessRun() {
        PagedVirtualMemoryTest.TestMemory mem = new PagedVirtualMemoryTest.TestMemory();
        int limit = 16 << 10;
        setLimit(limit);
        Random rnd = new Random();
        for (int i = 0; i != 10000; ++i) {
            long n = rnd.nextInt((limit - 64));
            verify(mem, n, 64);
        }
        System.out.println(mem.getFaultCount());
    }

    private class TestMemory extends SimplePagedVirtualMemory {
        private long faultCount;

        public TestMemory() {
            super(new ByteBufferPageManager(64, 4096, 4096));
        }

        public long getFaultCount() {
            return faultCount;
        }

        @SuppressWarnings("unused")
        public void resetFaultCount() {
            faultCount = 0;
        }

        @Override
        public int readPage(long offset, ByteBuffer page) {
            ++(faultCount);
            Random rnd = new Random();
            long n = offset;
            while ((page.remaining()) > 0) {
                rnd.setSeed(n);
                page.put(((byte) (rnd.nextInt())));
                ++n;
            } 
            return ((int) (n - offset));
        }
    }
}

