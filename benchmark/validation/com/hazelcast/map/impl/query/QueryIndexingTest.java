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
package com.hazelcast.map.impl.query;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryIndexingTest extends HazelcastTestSupport {
    private int count = 2000;

    private Map<Integer, SampleTestObjects.Employee> employees;

    private TestHazelcastInstanceFactory nodeFactory;

    private HazelcastInstance h1;

    private HazelcastInstance h2;

    private Predicate predicate;

    @Test
    public void testResultsHaveNullFields_whenPredicateTestsForNull() {
        IMap<Integer, SampleTestObjects.Employee> map = h1.getMap("employees");
        map.putAll(employees);
        HazelcastTestSupport.waitAllForSafeState(h1, h2);
        Collection<SampleTestObjects.Employee> matchingEntries = runQueryNTimes(3, h2.<String, SampleTestObjects.Employee>getMap("employees"));
        Assert.assertEquals(((count) / 2), matchingEntries.size());
        // N queries result in getters called N times
        QueryIndexingTest.assertGettersCalledNTimes(matchingEntries, 3);
        QueryIndexingTest.assertFieldsAreNull(matchingEntries);
    }

    @Test
    public void testResultsHaveNullFields_whenUsingIndexes() {
        IMap<Integer, SampleTestObjects.Employee> map = h1.getMap("employees");
        map.addIndex("name", false);
        map.addIndex("city", true);
        map.putAll(employees);
        HazelcastTestSupport.waitAllForSafeState(h1, h2);
        Collection<SampleTestObjects.Employee> matchingEntries = runQueryNTimes(3, h2.<String, SampleTestObjects.Employee>getMap("employees"));
        Assert.assertEquals(((count) / 2), matchingEntries.size());
        QueryIndexingTest.assertFieldsAreNull(matchingEntries);
    }
}

