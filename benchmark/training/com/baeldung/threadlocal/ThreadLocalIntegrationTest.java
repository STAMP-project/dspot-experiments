package com.baeldung.threadlocal;


import SharedMapWithUserContext.userContextPerUserId;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class ThreadLocalIntegrationTest {
    @Test
    public void givenThreadThatStoresContextInAMap_whenStartThread_thenShouldSetContextForBothUsers() throws InterruptedException, ExecutionException {
        // when
        SharedMapWithUserContext firstUser = new SharedMapWithUserContext(1);
        SharedMapWithUserContext secondUser = new SharedMapWithUserContext(2);
        new Thread(firstUser).start();
        new Thread(secondUser).start();
        Thread.sleep(3000);
        // then
        Assert.assertEquals(userContextPerUserId.size(), 2);
    }

    @Test
    public void givenThreadThatStoresContextInThreadLocal_whenStartThread_thenShouldStoreContextInThreadLocal() throws InterruptedException, ExecutionException {
        // when
        ThreadLocalWithUserContext firstUser = new ThreadLocalWithUserContext(1);
        ThreadLocalWithUserContext secondUser = new ThreadLocalWithUserContext(2);
        new Thread(firstUser).start();
        new Thread(secondUser).start();
        Thread.sleep(3000);
    }
}

