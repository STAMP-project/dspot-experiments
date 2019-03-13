package com.orientechnologies.orient.server.distributed.impl;


import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.server.distributed.impl.task.OTransactionPhase1Task;
import com.orientechnologies.orient.server.distributed.impl.task.OTransactionPhase1TaskResult;
import com.orientechnologies.orient.server.distributed.impl.task.transaction.OTxLockTimeout;
import com.orientechnologies.orient.server.distributed.impl.task.transaction.OTxSuccess;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


public class TestNewDistributedResponseManager {
    @Test
    public void testSimpleQuorum() {
        OTransactionPhase1Task transaction = new OTransactionPhase1Task();
        Set<String> nodes = new HashSet<>();
        nodes.add("one");
        nodes.add("two");
        nodes.add("three");
        ONewDistributedResponseManager responseManager = new ONewDistributedResponseManager(transaction, nodes, nodes, 3, 3, 2);
        Assert.assertFalse(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "one"));
        Assert.assertFalse(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "two"));
        Assert.assertTrue(responseManager.isQuorumReached());
    }

    @Test
    public void testSimpleNoQuorum() {
        OTransactionPhase1Task transaction = new OTransactionPhase1Task();
        Set<String> nodes = new HashSet<>();
        nodes.add("one");
        nodes.add("two");
        nodes.add("three");
        ONewDistributedResponseManager responseManager = new ONewDistributedResponseManager(transaction, nodes, nodes, 3, 3, 2);
        Assert.assertFalse(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "one"));
        Assert.assertFalse(responseManager.collectResponse(new OTransactionPhase1TaskResult(new com.orientechnologies.orient.server.distributed.impl.task.transaction.OTxConcurrentModification(new ORecordId(1, 1), 1)), "two"));
        Assert.assertTrue(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxLockTimeout()), "two"));
        Assert.assertFalse(responseManager.isQuorumReached());
    }

    @Test
    public void testSimpleQuorumLocal() {
        OTransactionPhase1Task transaction = new OTransactionPhase1Task();
        Set<String> nodes = new HashSet<>();
        nodes.add("one");
        nodes.add("two");
        nodes.add("three");
        ONewDistributedResponseManager responseManager = new ONewDistributedResponseManager(transaction, nodes, nodes, 3, 3, 2);
        Assert.assertFalse(responseManager.setLocalResult("one", new OTxSuccess()));
        Assert.assertFalse(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "three"));
        Assert.assertTrue(responseManager.isQuorumReached());
    }

    @Test
    public void testSimpleFinishLocal() {
        OTransactionPhase1Task transaction = new OTransactionPhase1Task();
        Set<String> nodes = new HashSet<>();
        nodes.add("one");
        nodes.add("two");
        nodes.add("three");
        ONewDistributedResponseManager responseManager = new ONewDistributedResponseManager(transaction, nodes, nodes, 3, 3, 2);
        Assert.assertFalse(responseManager.setLocalResult("one", new OTxSuccess()));
        Assert.assertFalse(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "two"));
        Assert.assertTrue(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "three"));
        Assert.assertTrue(responseManager.isQuorumReached());
    }

    @Test
    public void testWaitToComplete() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newCachedThreadPool();
        OTransactionPhase1Task transaction = new OTransactionPhase1Task();
        Set<String> nodes = new HashSet<>();
        nodes.add("one");
        nodes.add("two");
        nodes.add("three");
        ONewDistributedResponseManager responseManager = new ONewDistributedResponseManager(transaction, nodes, nodes, 3, 3, 2);
        responseManager.setLocalResult("one", new OTxSuccess());
        CountDownLatch startedWaiting = new CountDownLatch(1);
        Future<Boolean> future = executor.submit(() -> {
            startedWaiting.countDown();
            return responseManager.waitForSynchronousResponses();
        });
        startedWaiting.await();
        Assert.assertFalse(future.isDone());
        responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "one");
        Assert.assertTrue(future.get());
        Assert.assertTrue(responseManager.isQuorumReached());
    }

    @Test
    public void testWaitToCompleteNoQuorum() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newCachedThreadPool();
        OTransactionPhase1Task transaction = new OTransactionPhase1Task();
        Set<String> nodes = new HashSet<>();
        nodes.add("one");
        nodes.add("two");
        nodes.add("three");
        ONewDistributedResponseManager responseManager = new ONewDistributedResponseManager(transaction, nodes, nodes, 3, 3, 2);
        responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxSuccess()), "one");
        Assert.assertFalse(responseManager.collectResponse(new OTransactionPhase1TaskResult(new com.orientechnologies.orient.server.distributed.impl.task.transaction.OTxConcurrentModification(new ORecordId(1, 1), 1)), "two"));
        CountDownLatch startedWaiting = new CountDownLatch(1);
        Future<Boolean> future = executor.submit(() -> {
            startedWaiting.countDown();
            return responseManager.waitForSynchronousResponses();
        });
        startedWaiting.await();
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(responseManager.collectResponse(new OTransactionPhase1TaskResult(new OTxLockTimeout()), "one"));
        Assert.assertFalse(future.get());
        Assert.assertFalse(responseManager.isQuorumReached());
    }
}

