package org.roaringbitmap;


import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestRankIterator {
    @Parameterized.Parameter
    public FastRankRoaringBitmap bitmap;

    @Parameterized.Parameter(1)
    public Integer advance;

    @Test
    public void testAdvance() {
        long start = System.nanoTime();
        if ((advance) == 0) {
            testBitmapRanksOnNext(bitmap);
            long ms = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
            System.out.println((("next: " + ms) + "ms"));
        } else {
            testBitmapRanksOnAdvance(bitmap, advance);
            long ms = TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - start));
            System.out.println((((("advance by " + (advance)) + ": ") + ms) + "ms"));
        }
    }
}

