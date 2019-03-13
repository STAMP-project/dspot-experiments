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
package com.hazelcast.client.mapreduce;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@Ignore
public class MapReduceLiteMemberTest {
    private TestHazelcastFactory factory;

    private HazelcastInstance client;

    private HazelcastInstance lite;

    private HazelcastInstance lite2;

    private HazelcastInstance instance;

    private HazelcastInstance instance2;

    @Test(timeout = 60000)
    public void testMapper() throws Exception {
        com.hazelcast.mapreduce.MapReduceLiteMemberTest.testMapper(client);
    }

    @Test(timeout = 60000)
    public void testKeyedMapperCollator() throws Exception {
        com.hazelcast.mapreduce.MapReduceLiteMemberTest.testKeyedMapperCollator(client);
    }

    @Test(timeout = 60000)
    public void testMapperComplexMapping() throws Exception {
        com.hazelcast.mapreduce.MapReduceLiteMemberTest.testMapperComplexMapping(client);
    }

    @Test(timeout = 60000)
    public void testMapperCollator() throws Exception {
        com.hazelcast.mapreduce.MapReduceLiteMemberTest.testMapperCollator(client);
    }

    @Test(timeout = 60000)
    public void testMapperReducerCollator() throws Exception {
        com.hazelcast.mapreduce.MapReduceLiteMemberTest.testMapperReducerCollator(client);
    }

    @Test(timeout = 120000)
    public void testMapReduceJobSubmissionWithNoDataNode() throws Exception {
        instance.getLifecycleService().terminate();
        instance2.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(2, lite, lite2);
        ICompletableFuture<Map<String, List<Integer>>> future = com.hazelcast.mapreduce.MapReduceLiteMemberTest.testMapReduceJobSubmissionWithNoDataNode(client);
        try {
            future.get(120, TimeUnit.SECONDS);
            Assert.fail("Map-reduce job should not be submitted when there is no data member");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
        }
    }
}

