package com.thinkaurelius.titan.util.datastructures;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RandomRemovalListTest {
    private static final Logger log = LoggerFactory.getLogger(RandomRemovalListTest.class);

    @Test
    public void test1() {
        int max = 1000000;
        RandomRemovalList<Integer> list = new RandomRemovalList<Integer>();
        for (int i = 1; i <= max; i++) {
            list.add(i);
        }
        long sum = 0;
        int subset = max / 10;
        for (int j = 1; j <= subset; j++) {
            sum += list.getRandom();
        }
        double avg = sum / ((double) (subset));
        RandomRemovalListTest.log.debug("Average: {}", avg);
        Assert.assertEquals(avg, (((double) (max)) / 2), (max / 100));
    }

    @Test
    public void test2() {
        runIndividual();
    }

    @Test
    public void test3() {
        long max = 20000;
        RandomRemovalList<Integer> list = new RandomRemovalList<Integer>();
        for (int i = 1; i <= max; i++) {
            list.add(i);
        }
        long sum = 0;
        int numReturned = 0;
        Iterator<Integer> iter = list.iterator();
        while (iter.hasNext()) {
            sum += iter.next();
            numReturned++;
        } 
        Assert.assertEquals(sum, (((max + 1) * max) / 2));
        Assert.assertEquals(numReturned, max);
    }
}

