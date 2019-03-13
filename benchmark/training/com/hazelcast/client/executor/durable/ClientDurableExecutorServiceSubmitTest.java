/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.executor.durable;


import com.hazelcast.client.executor.tasks.MapPutPartitionAwareRunnable;
import com.hazelcast.client.executor.tasks.MapPutRunnable;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.client.test.executor.tasks.AppendCallable;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientDurableExecutorServiceSubmitTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance server;

    private HazelcastInstance client;

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void testSubmitCallableNullTask() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        Callable callable = null;
        service.submit(callable);
    }

    @Test
    public void submitRunnable() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        service.submit(runnable);
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertSizeEventually(1, map);
    }

    @Test
    public void testSubmitRunnable_WithResult() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Object givenResult = "givenResult";
        Future future = service.submit(new MapPutRunnable(mapName), givenResult);
        Object result = future.get();
        IMap map = client.getMap(mapName);
        Assert.assertEquals(givenResult, result);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testSubmitCallable() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable callable = new AppendCallable(msg);
        Future result = service.submit(callable);
        Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), result.get());
    }

    @Test
    public void testSubmitRunnable_withExecutionCallback() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(runnable).andThen(new ExecutionCallback() {
            public void onResponse(Object response) {
                responseLatch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        });
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testSubmitCallable_withExecutionCallback() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        final AtomicReference<String> result = new AtomicReference<String>();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(callable).andThen(new ExecutionCallback<String>() {
            public void onResponse(String response) {
                result.set(response);
                responseLatch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), result.get());
    }

    @Test
    public void submitCallableToKeyOwner() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        Future<String> result = service.submitToKeyOwner(callable, "key");
        Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), result.get());
    }

    @Test
    public void submitRunnableToKeyOwner() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submitToKeyOwner(runnable, "key").andThen(new ExecutionCallback() {
            public void onResponse(Object response) {
                responseLatch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        });
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void submitCallableToKeyOwner_withExecutionCallback() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<String>();
        service.submitToKeyOwner(callable, "key").andThen(new ExecutionCallback<String>() {
            public void onResponse(String response) {
                result.set(response);
                responseLatch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), result.get());
    }

    @Test
    public void submitRunnablePartitionAware() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        final Member member = server.getCluster().getLocalMember();
        // this task should execute on a node owning the given key argument,
        // the action is to put the UUid of the executing node into a map with the given name
        Runnable runnable = new MapPutPartitionAwareRunnable<String>(mapName, key);
        service.submit(runnable);
        final IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertTrue(map.containsKey(member.getUuid()));
            }
        });
    }

    @Test
    public void submitRunnablePartitionAware_withResult() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String expectedResult = "result";
        String mapName = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        final Member member = server.getCluster().getLocalMember();
        Runnable runnable = new MapPutPartitionAwareRunnable<String>(mapName, key);
        Future result = service.submit(runnable, expectedResult);
        final IMap map = client.getMap(mapName);
        Assert.assertEquals(expectedResult, result.get());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertTrue(map.containsKey(member.getUuid()));
            }
        });
    }

    @Test
    public void submitRunnablePartitionAware_withExecutionCallback() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        Member member = server.getCluster().getLocalMember();
        Runnable runnable = new MapPutPartitionAwareRunnable<String>(mapName, key);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(runnable).andThen(new ExecutionCallback() {
            @Override
            public void onResponse(Object response) {
                responseLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertTrue(map.containsKey(member.getUuid()));
    }

    @Test
    public void submitCallablePartitionAware() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        IMap map = client.getMap(mapName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        Member member = server.getCluster().getLocalMember();
        Callable<String> callable = new com.hazelcast.client.test.executor.tasks.MapPutPartitionAwareCallable<String, String>(mapName, key);
        Future<String> result = service.submit(callable);
        Assert.assertEquals(member.getUuid(), result.get());
        Assert.assertTrue(map.containsKey(member.getUuid()));
    }

    @Test
    public void submitCallablePartitionAware_WithExecutionCallback() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        IMap map = client.getMap(mapName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        Member member = server.getCluster().getLocalMember();
        Callable<String> runnable = new com.hazelcast.client.test.executor.tasks.MapPutPartitionAwareCallable<String, String>(mapName, key);
        final AtomicReference<String> result = new AtomicReference<String>();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(runnable).andThen(new ExecutionCallback<String>() {
            public void onResponse(String response) {
                result.set(response);
                responseLatch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertEquals(member.getUuid(), result.get());
        Assert.assertTrue(map.containsKey(member.getUuid()));
    }
}

