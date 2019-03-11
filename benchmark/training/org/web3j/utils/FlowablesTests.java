package org.web3j.utils;


import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class FlowablesTests {
    @Test
    public void testRangeFlowable() throws InterruptedException {
        int count = 10;
        Flowable<BigInteger> flowable = Flowables.range(BigInteger.ZERO, BigInteger.valueOf((count - 1)));
        List<BigInteger> expected = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            expected.add(BigInteger.valueOf(i));
        }
        runRangeTest(flowable, expected);
    }

    @Test
    public void testRangeDescendingFlowable() throws InterruptedException {
        int count = 10;
        Flowable<BigInteger> flowable = Flowables.range(BigInteger.ZERO, BigInteger.valueOf((count - 1)), false);
        List<BigInteger> expected = new ArrayList<>(count);
        for (int i = count - 1; i >= 0; i--) {
            expected.add(BigInteger.valueOf(i));
        }
        runRangeTest(flowable, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeFlowableIllegalLowerBound() throws InterruptedException {
        Flowables.range(BigInteger.valueOf((-1)), BigInteger.ONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeFlowableIllegalBounds() throws InterruptedException {
        Flowables.range(BigInteger.TEN, BigInteger.ONE);
    }
}

