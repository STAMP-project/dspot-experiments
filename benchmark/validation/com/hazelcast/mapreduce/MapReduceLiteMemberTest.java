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
package com.hazelcast.mapreduce;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapReduceLiteMemberTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    private HazelcastInstance instance;

    private HazelcastInstance instance2;

    private HazelcastInstance lite;

    private HazelcastInstance lite2;

    @Test(timeout = 60000)
    public void testMapper_fromLiteMember() throws Exception {
        MapReduceLiteMemberTest.testMapper(lite);
    }

    @Test(timeout = 60000)
    public void testMapper() throws Exception {
        MapReduceLiteMemberTest.testMapper(instance);
    }

    @Test(timeout = 60000)
    public void testKeyedMapperCollator_fromLiteMember() throws Exception {
        MapReduceLiteMemberTest.testKeyedMapperCollator(lite);
    }

    @Test(timeout = 60000)
    public void testKeyedMapperCollator() throws Exception {
        MapReduceLiteMemberTest.testKeyedMapperCollator(instance);
    }

    @Test(timeout = 60000)
    public void testMapperComplexMapping_fromLiteMember() throws Exception {
        MapReduceLiteMemberTest.testMapperComplexMapping(lite);
    }

    @Test(timeout = 60000)
    public void testMapperComplexMapping() throws Exception {
        MapReduceLiteMemberTest.testMapperComplexMapping(instance);
    }

    @Test(timeout = 60000)
    public void testMapperReducerChunked_fromLiteMember() throws Exception {
        MapReduceLiteMemberTest.testMapperReducerChunked(lite);
    }

    @Test(timeout = 60000)
    public void testMapperReducerChunked() throws Exception {
        MapReduceLiteMemberTest.testMapperReducerChunked(instance);
    }

    @Test(timeout = 60000)
    public void testMapperCollator_fromLiteMember() throws Exception {
        MapReduceLiteMemberTest.testMapperCollator(lite);
    }

    @Test(timeout = 60000)
    public void testMapperCollator() throws Exception {
        MapReduceLiteMemberTest.testMapperCollator(instance);
    }

    @Test(timeout = 60000)
    public void testMapperReducerCollator_fromLiteMember() throws Exception {
        MapReduceLiteMemberTest.testMapperReducerCollator(lite);
    }

    @Test(timeout = 60000)
    public void testMapperReducerCollator() throws Exception {
        MapReduceLiteMemberTest.testMapperReducerCollator(instance);
    }

    @Test(expected = IllegalStateException.class, timeout = 60000)
    public void testMapReduceJobSubmissionWithNoDataNode() {
        instance.shutdown();
        instance2.shutdown();
        HazelcastTestSupport.assertClusterSizeEventually(2, lite, lite2);
        MapReduceLiteMemberTest.testMapReduceJobSubmissionWithNoDataNode(lite);
    }
}

