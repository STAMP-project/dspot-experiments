package com.crossoverjie.algorithm;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class BloomFiltersTest {
    private static int count = 10000000;

    @Test
    public void guavaTest() {
        long star = System.currentTimeMillis();
        BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), BloomFiltersTest.count, 0.01);
        for (int i = 0; i < (BloomFiltersTest.count); i++) {
            filter.put(i);
        }
        Assert.assertTrue(filter.mightContain(1));
        Assert.assertTrue(filter.mightContain(2));
        Assert.assertTrue(filter.mightContain(3));
        Assert.assertFalse(filter.mightContain(BloomFiltersTest.count));
        long end = System.currentTimeMillis();
        System.out.println(("?????" + (end - star)));
    }

    @Test
    public void hashMapTest() {
        long star = System.currentTimeMillis();
        Set<Integer> hashset = new HashSet<>(10000000);
        for (int i = 0; i < 10000000; i++) {
            hashset.add(i);
        }
        Assert.assertTrue(hashset.contains(1));
        Assert.assertTrue(hashset.contains(2));
        Assert.assertTrue(hashset.contains(3));
        long end = System.currentTimeMillis();
        System.out.println(("?????" + (end - star)));
    }

    @Test
    public void bloomFilterTest() {
        long star = System.currentTimeMillis();
        BloomFilters bloomFilters = new BloomFilters(BloomFiltersTest.count);
        for (int i = 0; i < (BloomFiltersTest.count); i++) {
            bloomFilters.add((i + ""));
        }
        Assert.assertTrue(bloomFilters.check((1 + "")));
        Assert.assertTrue(bloomFilters.check((2 + "")));
        Assert.assertTrue(bloomFilters.check((3 + "")));
        Assert.assertTrue(bloomFilters.check((999999 + "")));
        Assert.assertFalse(bloomFilters.check((400230340 + "")));
        long end = System.currentTimeMillis();
        System.out.println(("?????" + (end - star)));
    }
}

