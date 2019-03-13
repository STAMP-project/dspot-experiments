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
package com.hazelcast.query.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DefaultValueCollectorTest {
    private DefaultValueCollector collector;

    @Test
    public void test_emptyCollector() {
        Assert.assertNull(collector.getResult());
    }

    @Test
    public void test_singleObject() {
        collector.addObject(1);
        Assert.assertEquals(1, collector.getResult());
    }

    @Test
    public void test_TwoObjects() {
        collector.addObject(1);
        collector.addObject(2);
        List<Integer> results = assertIsMultiResultAndGetResults(collector.getResult());
        MatcherAssert.assertThat(results, Matchers.hasSize(2));
        MatcherAssert.assertThat(results, Matchers.containsInAnyOrder(1, 2));
    }

    @Test
    public void test_multipleObjects() {
        collector.addObject(1);
        collector.addObject(2);
        collector.addObject(3);
        List<Integer> results = assertIsMultiResultAndGetResults(collector.getResult());
        MatcherAssert.assertThat(results, Matchers.hasSize(3));
        MatcherAssert.assertThat(results, Matchers.containsInAnyOrder(1, 2, 3));
    }

    @Test
    public void test_multipleObjects_sameValues() {
        collector.addObject(1);
        collector.addObject(1);
        List<Integer> results = assertIsMultiResultAndGetResults(collector.getResult());
        MatcherAssert.assertThat(results, Matchers.hasSize(2));
        MatcherAssert.assertThat(results, Matchers.containsInAnyOrder(1, 1));
    }

    @Test
    public void test_multipleObjects_includingNull() {
        collector.addObject(1);
        collector.addObject(null);
        List<Integer> results = assertIsMultiResultAndGetResults(collector.getResult());
        MatcherAssert.assertThat(results, Matchers.hasSize(2));
        MatcherAssert.assertThat(results, Matchers.containsInAnyOrder(1, null));
    }
}

