package com.baeldung.infinispan.service;


import com.baeldung.infinispan.AbstractIntegrationTest;
import java.util.function.Supplier;
import org.junit.Test;


public class TransactionalServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void whenLockingAnEntry_thenItShouldBeInaccessible() throws InterruptedException {
        Runnable backGroundJob = () -> transactionalService.startBackgroundBatch();
        Thread backgroundThread = new Thread(backGroundJob);
        transactionalService.getQuickHowManyVisits();
        backgroundThread.start();
        Thread.sleep(100);// lets wait our thread warm up

        assertThat(timeThis(() -> transactionalService.getQuickHowManyVisits())).isGreaterThan(500).isLessThan(1000);
    }
}

