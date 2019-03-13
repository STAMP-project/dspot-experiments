package com.alibaba.druid.stat;


import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import junit.framework.TestCase;


public class MemoryTest extends TestCase {
    public void test_0() throws Exception {
        MemoryTest.A item = new MemoryTest.A();
        gc();
        final int COUNT = 1024 * 1024;
        MemoryTest.A[] items = new MemoryTest.A[COUNT];
        long memoryStart = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        for (int i = 0; i < COUNT; ++i) {
            items[i] = new MemoryTest.A();
            // items[i] = Histogram.makeHistogram(20);
        }
        long memoryEnd = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        System.out.println(("memory used : " + (NumberFormat.getInstance().format((memoryEnd - memoryStart)))));
    }

    public static class A {
        private volatile long v;
    }
}

