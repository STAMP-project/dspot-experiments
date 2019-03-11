package com.netflix.astyanax.partitioner;


import BigInteger127Partitioner.MAXIMUM;
import BigInteger127Partitioner.MINIMUM;
import com.netflix.astyanax.connectionpool.TokenRange;
import java.util.List;
import org.junit.Test;


public class PartitionerTest {
    @Test
    public void testSplit() {
        BigInteger127Partitioner partitioner = new BigInteger127Partitioner();
        List<TokenRange> ranges = partitioner.splitTokenRange(4);
        for (TokenRange range : ranges) {
            System.out.println(range);
        }
    }

    @Test
    public void testSplitWithStartEnd() {
        BigInteger127Partitioner partitioner = new BigInteger127Partitioner();
        List<TokenRange> ranges = partitioner.splitTokenRange(MINIMUM.toString(), MAXIMUM.toString(), 4);
        for (TokenRange range : ranges) {
            System.out.println(range);
        }
    }

    @Test
    public void testSplitWithZeros() {
        BigInteger127Partitioner partitioner = new BigInteger127Partitioner();
        List<TokenRange> ranges = partitioner.splitTokenRange("0", "0", 4);
        for (TokenRange range : ranges) {
            System.out.println(range);
        }
    }
}

