package com.baeldung.java.concurrentmap;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentMapAggregateStatusManualTest {
    private ExecutorService executorService;

    private Map<String, Integer> concurrentMap;

    private List<Integer> mapSizes;

    private int MAX_SIZE = 100000;

    @Test
    public void givenConcurrentMap_whenSizeWithoutConcurrentUpdates_thenCorrect() throws InterruptedException {
        Runnable collectMapSizes = () -> {
            for (int i = 0; i < (MAX_SIZE); i++) {
                concurrentMap.put(String.valueOf(i), i);
                mapSizes.add(concurrentMap.size());
            }
        };
        Runnable retrieveMapData = () -> {
            for (int i = 0; i < (MAX_SIZE); i++) {
                concurrentMap.get(String.valueOf(i));
            }
        };
        executorService.execute(retrieveMapData);
        executorService.execute(collectMapSizes);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        for (int i = 1; i <= (MAX_SIZE); i++) {
            Assert.assertEquals("map size should be consistently reliable", i, mapSizes.get((i - 1)).intValue());
        }
        Assert.assertEquals(MAX_SIZE, concurrentMap.size());
    }

    @Test
    public void givenConcurrentMap_whenUpdatingAndGetSize_thenError() throws InterruptedException {
        Runnable collectMapSizes = () -> {
            for (int i = 0; i < (MAX_SIZE); i++) {
                mapSizes.add(concurrentMap.size());
            }
        };
        Runnable updateMapData = () -> {
            for (int i = 0; i < (MAX_SIZE); i++) {
                concurrentMap.put(String.valueOf(i), i);
            }
        };
        executorService.execute(updateMapData);
        executorService.execute(collectMapSizes);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        Assert.assertNotEquals("map size collected with concurrent updates not reliable", MAX_SIZE, mapSizes.get(((MAX_SIZE) - 1)).intValue());
        Assert.assertEquals(MAX_SIZE, concurrentMap.size());
    }
}

