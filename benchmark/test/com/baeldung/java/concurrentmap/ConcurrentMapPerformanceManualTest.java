package com.baeldung.java.concurrentmap;


import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentMapPerformanceManualTest {
    @Test
    public void givenMaps_whenGetPut500KTimes_thenConcurrentMapFaster() throws Exception {
        final Map<String, Object> hashtable = new Hashtable<>();
        final Map<String, Object> synchronizedHashMap = Collections.synchronizedMap(new HashMap<>());
        final Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
        final long hashtableAvgRuntime = timeElapseForGetPut(hashtable);
        final long syncHashMapAvgRuntime = timeElapseForGetPut(synchronizedHashMap);
        final long concurrentHashMapAvgRuntime = timeElapseForGetPut(concurrentHashMap);
        System.out.println(String.format("Hashtable: %s, syncHashMap: %s, ConcurrentHashMap: %s", hashtableAvgRuntime, syncHashMapAvgRuntime, concurrentHashMapAvgRuntime));
        Assert.assertTrue((hashtableAvgRuntime > concurrentHashMapAvgRuntime));
        Assert.assertTrue((syncHashMapAvgRuntime > concurrentHashMapAvgRuntime));
    }

    @Test
    public void givenConcurrentMap_whenKeyWithSameHashCode_thenPerformanceDegrades() throws InterruptedException {
        class SameHash {
            @Override
            public int hashCode() {
                return 1;
            }
        }
        final int executeTimes = 5000;
        final Map<SameHash, Integer> mapOfSameHash = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        final long sameHashStartTime = System.currentTimeMillis();
        for (int i = 0; i < 2; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < executeTimes; j++) {
                    mapOfSameHash.put(new SameHash(), 1);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        final long mapOfSameHashDuration = (System.currentTimeMillis()) - sameHashStartTime;
        final Map<Object, Integer> mapOfDefaultHash = new ConcurrentHashMap<>();
        executorService = Executors.newFixedThreadPool(2);
        final long defaultHashStartTime = System.currentTimeMillis();
        for (int i = 0; i < 2; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < executeTimes; j++) {
                    mapOfDefaultHash.put(new Object(), 1);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        final long mapOfDefaultHashDuration = (System.currentTimeMillis()) - defaultHashStartTime;
        Assert.assertEquals((executeTimes * 2), mapOfDefaultHash.size());
        Assert.assertEquals((executeTimes * 2), mapOfSameHash.size());
        System.out.println(String.format("same-hash: %s, default-hash: %s", mapOfSameHashDuration, mapOfDefaultHashDuration));
        Assert.assertTrue("same hashCode() should greatly degrade performance", (mapOfSameHashDuration > (mapOfDefaultHashDuration * 10)));
    }
}

