package org.roaringbitmap.realdata;


import org.junit.Assert;
import org.junit.Test;


public class RealDataBenchmarkWideAndNaiveTest extends RealDataBenchmarkSanityTest {
    @Test
    public void test() throws Exception {
        RealDataBenchmarkWideAndNaive bench = new RealDataBenchmarkWideAndNaive();
        Assert.assertEquals(0, bench.wideAnd_naive(bs));
    }
}

