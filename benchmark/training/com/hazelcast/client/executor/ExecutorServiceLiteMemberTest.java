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


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExecutorServiceLiteMemberTest {
    private TestHazelcastFactory factory;

    private final String defaultMemberConfigXml = "hazelcast-test-executor.xml";

    private Config liteConfig = new XmlConfigBuilder(getClass().getClassLoader().getResourceAsStream(defaultMemberConfigXml)).build().setLiteMember(true);

    private Config config = new XmlConfigBuilder(getClass().getClassLoader().getResourceAsStream(defaultMemberConfigXml)).build();

    private ClientConfig clientConfig = new XmlClientConfigBuilder("classpath:hazelcast-client-test-executor.xml").build();

    public ExecutorServiceLiteMemberTest() throws IOException {
    }

    @Test(expected = RejectedExecutionException.class)
    public void test_executeRunnable_failsWhenNoLiteMemberExists() {
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final String name = randomString();
        final IExecutorService executor = client.getExecutorService(name);
        executor.execute(new ResultSettingRunnable(name), LITE_MEMBER_SELECTOR);
    }

    @Test
    public void test_executeRunnable_onLiteMember() {
        final HazelcastInstance lite1 = newHazelcastLiteMember();
        final HazelcastInstance lite2 = newHazelcastLiteMember();
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final String name = randomString();
        final IExecutorService executor = client.getExecutorService(name);
        executor.execute(new ResultSettingRunnable(name), LITE_MEMBER_SELECTOR);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                final IMap<Object, Object> results = lite1.getMap(name);
                Assert.assertEquals(1, results.size());
                final boolean executedOnLite1 = results.containsKey(lite1.getCluster().getLocalMember());
                final boolean executedOnLite2 = results.containsKey(lite2.getCluster().getLocalMember());
                Assert.assertTrue((executedOnLite1 || executedOnLite2));
            }
        });
    }

    @Test
    public void test_executeRunnable_onAllLiteMembers() {
        final HazelcastInstance lite1 = newHazelcastLiteMember();
        final HazelcastInstance lite2 = newHazelcastLiteMember();
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final String name = randomString();
        final IExecutorService executor = client.getExecutorService(name);
        executor.executeOnMembers(new ResultSettingRunnable(name), LITE_MEMBER_SELECTOR);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                final IMap<Object, Object> results = lite1.getMap(name);
                Assert.assertEquals(2, results.size());
                Assert.assertTrue(results.containsKey(lite1.getCluster().getLocalMember()));
                Assert.assertTrue(results.containsKey(lite2.getCluster().getLocalMember()));
            }
        });
    }

    @Test(expected = RejectedExecutionException.class)
    public void test_submitCallable_failsWhenNoLiteMemberExists() {
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final IExecutorService executor = client.getExecutorService(randomString());
        executor.submit(new LocalMemberReturningCallable(), LITE_MEMBER_SELECTOR);
    }

    @Test
    public void test_submitCallable_onLiteMember() throws InterruptedException, ExecutionException {
        final HazelcastInstance lite1 = newHazelcastLiteMember();
        final HazelcastInstance lite2 = newHazelcastLiteMember();
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final IExecutorService executor = client.getExecutorService(randomString());
        final Future<Member> future = executor.submit(new LocalMemberReturningCallable(), LITE_MEMBER_SELECTOR);
        final Member executedMember = future.get();
        final boolean executedOnLite1 = lite1.getCluster().getLocalMember().equals(executedMember);
        final boolean executedOnLite2 = lite2.getCluster().getLocalMember().equals(executedMember);
        Assert.assertTrue((executedOnLite1 || executedOnLite2));
    }

    @Test
    public void test_submitCallable_onLiteMembers() throws InterruptedException, ExecutionException {
        final HazelcastInstance lite1 = newHazelcastLiteMember();
        final HazelcastInstance lite2 = newHazelcastLiteMember();
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final IExecutorService executor = client.getExecutorService(randomString());
        final Map<Member, Future<Member>> results = executor.submitToMembers(new LocalMemberReturningCallable(), LITE_MEMBER_SELECTOR);
        Assert.assertEquals(2, results.size());
        final Member liteMember1 = lite1.getCluster().getLocalMember();
        final Member liteMember2 = lite2.getCluster().getLocalMember();
        Assert.assertEquals(liteMember1, results.get(liteMember1).get());
        Assert.assertEquals(liteMember2, results.get(liteMember2).get());
    }

    @Test
    public void test_submitCallableWithCallback_toLiteMember() {
        final HazelcastInstance lite1 = newHazelcastLiteMember();
        final HazelcastInstance lite2 = newHazelcastLiteMember();
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final CountingDownExecutionCallback<Member> callback = new CountingDownExecutionCallback<Member>(1);
        final IExecutorService executor = client.getExecutorService(randomString());
        executor.submit(new LocalMemberReturningCallable(), LITE_MEMBER_SELECTOR, callback);
        assertOpenEventually(callback.getLatch());
        final Object result = callback.getResult();
        final boolean executedOnLite1 = lite1.getCluster().getLocalMember().equals(result);
        final boolean executedOnLite2 = lite2.getCluster().getLocalMember().equals(result);
        Assert.assertTrue((executedOnLite1 || executedOnLite2));
    }

    @Test
    public void test_submitCallableWithCallback_toLiteMembers() {
        final HazelcastInstance lite1 = newHazelcastLiteMember();
        final HazelcastInstance lite2 = newHazelcastLiteMember();
        newHazelcastInstance();
        final HazelcastInstance client = newHazelcastClient();
        final ResultHoldingMultiExecutionCallback callback = new ResultHoldingMultiExecutionCallback();
        final IExecutorService executor = client.getExecutorService(randomString());
        executor.submitToMembers(new LocalMemberReturningCallable(), LITE_MEMBER_SELECTOR, callback);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                final Map<Member, Object> results = callback.getResults();
                Assert.assertNotNull(results);
                final Member liteMember1 = lite1.getCluster().getLocalMember();
                final Member liteMember2 = lite2.getCluster().getLocalMember();
                Assert.assertEquals(liteMember1, results.get(liteMember1));
                Assert.assertEquals(liteMember2, results.get(liteMember2));
            }
        });
    }
}

