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
package com.hazelcast.client.executor;


import com.hazelcast.client.executor.tasks.MapPutPartitionAwareRunnable;
import com.hazelcast.client.executor.tasks.MapPutRunnable;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.client.test.executor.tasks.AppendCallable;
import com.hazelcast.client.test.executor.tasks.GetMemberUuidTask;
import com.hazelcast.client.test.executor.tasks.NullCallable;
import com.hazelcast.client.test.executor.tasks.SelectAllMembers;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientExecutorServiceSubmitTest {
    private static final int CLUSTER_SIZE = 3;

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance server;

    private HazelcastInstance client;

    @Test(expected = NullPointerException.class)
    public void testSubmitCallableNullTask() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Callable<String> callable = null;
        service.submit(callable);
    }

    @Test
    public void testSubmitCallableToMember() throws Exception {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Callable<String> getUuidCallable = new GetMemberUuidTask();
        Member member = server.getCluster().getLocalMember();
        Future<String> result = service.submitToMember(getUuidCallable, member);
        Assert.assertEquals(member.getUuid(), result.get());
    }

    @Test
    public void testSubmitCallableToMembers() throws Exception {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Callable<String> getUuidCallable = new GetMemberUuidTask();
        Collection<Member> collection = server.getCluster().getMembers();
        Map<Member, Future<String>> map = service.submitToMembers(getUuidCallable, collection);
        for (Member member : map.keySet()) {
            Future<String> result = map.get(member);
            String uuid = result.get();
            Assert.assertEquals(member.getUuid(), uuid);
        }
    }

    @Test
    public void testSubmitCallable_withMemberSelector() throws Exception {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        MemberSelector selectAll = new SelectAllMembers();
        Future<String> f = service.submit(callable, selectAll);
        Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), f.get());
    }

    @Test
    public void testSubmitCallableToMembers_withMemberSelector() throws Exception {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Callable<String> getUuidCallable = new GetMemberUuidTask();
        MemberSelector selectAll = new SelectAllMembers();
        Map<Member, Future<String>> map = service.submitToMembers(getUuidCallable, selectAll);
        for (Member member : map.keySet()) {
            Future<String> result = map.get(member);
            String uuid = result.get();
            Assert.assertEquals(member.getUuid(), uuid);
        }
    }

    @Test
    public void submitCallableToAllMembers() throws Exception {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        Map<Member, Future<String>> map = service.submitToAllMembers(callable);
        for (Member member : map.keySet()) {
            Future<String> result = map.get(member);
            Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), result.get());
        }
    }

    @Test
    public void submitRunnableToMember_withExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        Member member = server.getCluster().getLocalMember();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submitToMember(runnable, member, new ExecutionCallback() {
            public void onResponse(Object response) {
                responseLatch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        });
        Map map = client.getMap(mapName);
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void submitRunnableToMembers_withMultiExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        Collection<Member> collection = server.getCluster().getMembers();
        final CountDownLatch responseLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final CountDownLatch completeLatch = new CountDownLatch(1);
        service.submitToMembers(runnable, collection, new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                responseLatch.countDown();
            }

            public void onComplete(Map<Member, Object> values) {
                completeLatch.countDown();
            }
        });
        Map map = client.getMap(mapName);
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        HazelcastTestSupport.assertOpenEventually("completeLatch", completeLatch);
        Assert.assertEquals(ClientExecutorServiceSubmitTest.CLUSTER_SIZE, map.size());
    }

    @Test
    public void testSubmitCallableToMember_withExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Callable getUuidCallable = new GetMemberUuidTask();
        Member member = server.getCluster().getLocalMember();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        final AtomicReference<Object> result = new AtomicReference<Object>();
        service.submitToMember(getUuidCallable, member, new ExecutionCallback() {
            @Override
            public void onResponse(Object response) {
                result.set(response);
                responseLatch.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        Assert.assertEquals(member.getUuid(), result.get());
    }

    @Test
    public void submitCallableToMember_withMultiExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final CountDownLatch completeLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        Collection<Member> collection = server.getCluster().getMembers();
        service.submitToMembers(callable, collection, new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                if (value.equals((msg + (AppendCallable.APPENDAGE)))) {
                    responseLatch.countDown();
                }
            }

            public void onComplete(Map<Member, Object> values) {
                for (Member member : values.keySet()) {
                    Object value = values.get(member);
                    if (value.equals((msg + (AppendCallable.APPENDAGE)))) {
                        completeLatch.countDown();
                    }
                }
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        HazelcastTestSupport.assertOpenEventually("completeLatch", completeLatch);
    }

    @Test
    public void submitRunnable_withExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(1);
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        MemberSelector selector = new SelectAllMembers();
        service.submit(runnable, selector, new ExecutionCallback() {
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
    public void submitRunnableToMembers_withExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final CountDownLatch completeLatch = new CountDownLatch(1);
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        MemberSelector selector = new SelectAllMembers();
        service.submitToMembers(runnable, selector, new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                responseLatch.countDown();
            }

            public void onComplete(Map<Member, Object> values) {
                completeLatch.countDown();
            }
        });
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        HazelcastTestSupport.assertOpenEventually("completeLatch", completeLatch);
        Assert.assertEquals(ClientExecutorServiceSubmitTest.CLUSTER_SIZE, map.size());
    }

    @Test
    public void submitCallable_withExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(1);
        String msg = HazelcastTestSupport.randomString();
        Callable runnable = new AppendCallable(msg);
        MemberSelector selector = new SelectAllMembers();
        final AtomicReference<Object> result = new AtomicReference<Object>();
        service.submit(runnable, selector, new ExecutionCallback() {
            public void onResponse(Object response) {
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
    public void submitCallableToMembers_withExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final CountDownLatch completeLatch = new CountDownLatch(1);
        final String msg = HazelcastTestSupport.randomString();
        Callable callable = new AppendCallable(msg);
        MemberSelector selector = new SelectAllMembers();
        service.submitToMembers(callable, selector, new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                if (value.equals((msg + (AppendCallable.APPENDAGE)))) {
                    responseLatch.countDown();
                }
            }

            public void onComplete(Map<Member, Object> values) {
                completeLatch.countDown();
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        HazelcastTestSupport.assertOpenEventually("completeLatch", completeLatch);
    }

    @Test
    public void submitRunnableToAllMembers_withMultiExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final CountDownLatch completeLatch = new CountDownLatch(1);
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        service.submitToAllMembers(runnable, new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                responseLatch.countDown();
            }

            public void onComplete(Map<Member, Object> values) {
                completeLatch.countDown();
            }
        });
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        HazelcastTestSupport.assertOpenEventually("completeLatch", completeLatch);
        Assert.assertEquals(ClientExecutorServiceSubmitTest.CLUSTER_SIZE, map.size());
    }

    @Test
    public void submitCallableToAllMembers_withMultiExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final CountDownLatch completeLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final String msg = HazelcastTestSupport.randomString();
        Callable callable = new AppendCallable(msg);
        service.submitToAllMembers(callable, new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                if (value.equals((msg + (AppendCallable.APPENDAGE)))) {
                    responseLatch.countDown();
                }
            }

            public void onComplete(Map<Member, Object> values) {
                for (Member member : values.keySet()) {
                    Object value = values.get(member);
                    if (value.equals((msg + (AppendCallable.APPENDAGE)))) {
                        completeLatch.countDown();
                    }
                }
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        HazelcastTestSupport.assertOpenEventually("completeLatch", completeLatch);
    }

    @Test
    public void submitCallableWithNullResultToAllMembers_withMultiExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch responseLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        final CountDownLatch completeLatch = new CountDownLatch(ClientExecutorServiceSubmitTest.CLUSTER_SIZE);
        Callable callable = new NullCallable();
        service.submitToAllMembers(callable, new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                if (value == null) {
                    responseLatch.countDown();
                }
            }

            public void onComplete(Map<Member, Object> values) {
                for (Member member : values.keySet()) {
                    Object value = values.get(member);
                    if (value == null) {
                        completeLatch.countDown();
                    }
                }
            }
        });
        HazelcastTestSupport.assertOpenEventually("responseLatch", responseLatch);
        HazelcastTestSupport.assertOpenEventually("completeLatch", completeLatch);
    }

    @Test
    public void submitRunnable() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        service.submit(runnable);
        IMap map = client.getMap(mapName);
        HazelcastTestSupport.assertSizeEventually(1, map);
    }

    @Test
    public void testSubmitRunnable_WithResult() throws InterruptedException, ExecutionException {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable callable = new AppendCallable(msg);
        Future result = service.submit(callable);
        Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), result.get());
    }

    @Test
    public void testSubmitRunnable_withExecutionCallback() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(runnable, new ExecutionCallback() {
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        final AtomicReference<String> result = new AtomicReference<String>();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(callable, new ExecutionCallback<String>() {
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        Future<String> result = service.submitToKeyOwner(callable, "key");
        Assert.assertEquals((msg + (AppendCallable.APPENDAGE)), result.get());
    }

    @Test
    public void submitRunnableToKeyOwner() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        Runnable runnable = new MapPutRunnable(mapName);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submitToKeyOwner(runnable, "key", new ExecutionCallback() {
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String msg = HazelcastTestSupport.randomString();
        Callable<String> callable = new AppendCallable(msg);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<String>();
        service.submitToKeyOwner(callable, "key", new ExecutionCallback<String>() {
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        Member member = server.getCluster().getLocalMember();
        Runnable runnable = new MapPutPartitionAwareRunnable<String>(mapName, key);
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(runnable, new ExecutionCallback() {
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
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
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        IMap map = client.getMap(mapName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(server);
        Member member = server.getCluster().getLocalMember();
        Callable<String> runnable = new com.hazelcast.client.test.executor.tasks.MapPutPartitionAwareCallable<String, String>(mapName, key);
        final AtomicReference<String> result = new AtomicReference<String>();
        final CountDownLatch responseLatch = new CountDownLatch(1);
        service.submit(runnable, new ExecutionCallback<String>() {
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

