package com.baeldung.java.concurrentmap;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;


public class ConcurretMapMemoryConsistencyManualTest {
    @Test
    public void givenConcurrentMap_whenSumParallel_thenCorrect() throws Exception {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        List<Integer> sumList = parallelSum100(map, 1000);
        Assert.assertEquals(1, sumList.stream().distinct().count());
        long wrongResultCount = sumList.stream().filter(( num) -> num != 100).count();
        Assert.assertEquals(0, wrongResultCount);
    }

    @Test
    public void givenHashtable_whenSumParallel_thenCorrect() throws Exception {
        Map<String, Integer> map = new Hashtable<>();
        List<Integer> sumList = parallelSum100(map, 1000);
        Assert.assertEquals(1, sumList.stream().distinct().count());
        long wrongResultCount = sumList.stream().filter(( num) -> num != 100).count();
        Assert.assertEquals(0, wrongResultCount);
    }

    @Test
    public void givenHashMap_whenSumParallel_thenError() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        List<Integer> sumList = parallelSum100(map, 100);
        Assert.assertNotEquals(1, sumList.stream().distinct().count());
        long wrongResultCount = sumList.stream().filter(( num) -> num != 100).count();
        Assert.assertTrue((wrongResultCount > 0));
    }
}

