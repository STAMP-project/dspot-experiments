package com.orientechnologies.common.concur.lock;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class SimpleRWNotThreadBoundTest {
    @Test
    public void testWriteWaitRead() throws InterruptedException {
        OSimpleRWLockManager<String> manager = new ONotThreadRWLockManager();
        manager.acquireReadLock("aaa", 0);
        CountDownLatch error = new CountDownLatch(1);
        new Thread(() -> {
            try {
                manager.acquireWriteLock("aaa", 10);
            } catch (OLockException e) {
                error.countDown();
            }
        }).start();
        Assert.assertTrue(error.await(20, TimeUnit.MILLISECONDS));
        manager.releaseReadLock("aaa");
    }

    @Test
    public void testReadWaitWrite() throws InterruptedException {
        OSimpleRWLockManager<String> manager = new ONotThreadRWLockManager();
        manager.acquireWriteLock("aaa", 0);
        CountDownLatch error = new CountDownLatch(1);
        new Thread(() -> {
            try {
                manager.acquireReadLock("aaa", 10);
            } catch (OLockException e) {
                error.countDown();
            }
        }).start();
        Assert.assertTrue(error.await(20, TimeUnit.MILLISECONDS));
        manager.releaseWriteLock("aaa");
    }

    @Test
    public void testReadReleaseAcquireWrite() throws InterruptedException {
        OSimpleRWLockManager<String> manager = new ONotThreadRWLockManager();
        manager.acquireReadLock("aaa", 0);
        CountDownLatch error = new CountDownLatch(1);
        new Thread(() -> {
            try {
                manager.acquireWriteLock("aaa", 10);
            } catch (OLockException e) {
                error.countDown();
            }
        }).start();
        Assert.assertTrue(error.await(20, TimeUnit.MILLISECONDS));
        CountDownLatch ok = new CountDownLatch(1);
        new Thread(() -> {
            try {
                manager.acquireWriteLock("aaa", 10);
                ok.countDown();
            } catch (OLockException e) {
            }
        }).start();
        manager.releaseReadLock("aaa");
        Assert.assertTrue(ok.await(20, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testReadReadWaitWrite() throws InterruptedException {
        OSimpleRWLockManager<String> manager = new ONotThreadRWLockManager();
        manager.acquireReadLock("aaa", 0);
        CountDownLatch ok = new CountDownLatch(1);
        new Thread(() -> {
            try {
                manager.acquireReadLock("aaa", 10);
                ok.countDown();
            } catch (OLockException e) {
            }
        }).start();
        CountDownLatch error = new CountDownLatch(1);
        new Thread(() -> {
            try {
                manager.acquireWriteLock("aaa", 10);
            } catch (OLockException e) {
                error.countDown();
            }
        }).start();
        Assert.assertTrue(ok.await(20, TimeUnit.MILLISECONDS));
        Assert.assertTrue(error.await(20, TimeUnit.MILLISECONDS));
        manager.releaseReadLock("aaa");
    }
}

