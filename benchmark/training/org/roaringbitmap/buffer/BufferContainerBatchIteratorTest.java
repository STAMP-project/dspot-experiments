package org.roaringbitmap.buffer;


import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class BufferContainerBatchIteratorTest {
    private final int[] expectedValues;

    public BufferContainerBatchIteratorTest(int[] expectedValues) {
        this.expectedValues = expectedValues;
    }

    @Test
    public void shouldRecoverValues512() {
        test(512);
    }

    @Test
    public void shouldRecoverValues1024() {
        test(1024);
    }

    @Test
    public void shouldRecoverValues2048() {
        test(2048);
    }

    @Test
    public void shouldRecoverValues4096() {
        test(4096);
    }

    @Test
    public void shouldRecoverValues8192() {
        test(8192);
    }

    @Test
    public void shouldRecoverValues65536() {
        test(65536);
    }

    @Test
    public void shouldRecoverValuesRandomBatchSizes() {
        IntStream.range(0, 100).forEach(( i) -> test(ThreadLocalRandom.current().nextInt(1, 65536)));
    }
}

