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


import TruePredicate.INSTANCE;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.impl.predicates.InstanceOfPredicate;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
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
public class MapValuesTest extends HazelcastTestSupport {
    private IMap<String, String> map;

    private SerializationService serializationService;

    @Test(expected = NullPointerException.class)
    public void whenPredicateNull() {
        map.values(null);
    }

    @Test
    public void whenMapEmpty() {
        Collection<String> result = map.values(INSTANCE);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void whenSelecting_withoutPredicate() {
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        Collection<String> result = map.values();
        Assert.assertEquals(3, result.size());
        HazelcastTestSupport.assertContains(result, "a");
        HazelcastTestSupport.assertContains(result, "b");
        HazelcastTestSupport.assertContains(result, "c");
    }

    @Test
    public void whenSelectingAllEntries() {
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        Collection<String> result = map.values(INSTANCE);
        Assert.assertEquals(3, result.size());
        HazelcastTestSupport.assertContains(result, "a");
        HazelcastTestSupport.assertContains(result, "b");
        HazelcastTestSupport.assertContains(result, "c");
    }

    @Test
    public void whenSelectingSomeEntries() {
        map.put("1", "good1");
        map.put("2", "bad");
        map.put("3", "good2");
        Collection<String> result = map.values(new MapValuesTest.GoodPredicate());
        Assert.assertEquals(2, result.size());
        HazelcastTestSupport.assertContains(result, "good1");
        HazelcastTestSupport.assertContains(result, "good2");
    }

    @Test
    public void testResultType() {
        map.put("1", "a");
        Collection<String> entries = map.values(INSTANCE);
        QueryResultCollection collection = HazelcastTestSupport.assertInstanceOf(QueryResultCollection.class, entries);
        QueryResultRow row = ((QueryResultRow) (collection.getRows().iterator().next()));
        // there should only be a value, no key
        Assert.assertNull(row.getKey());
        Assert.assertEquals(serializationService.toData("a"), row.getValue());
    }

    @Test
    public void testSerializationServiceNullClassLoaderProblem() throws Exception {
        // if the classloader is null the following call throws NullPointerException
        map.values(new InstanceOfPredicate(SampleTestObjects.PortableEmployee.class));
    }

    static class GoodPredicate implements Predicate<String, String> {
        @Override
        public boolean apply(Map.Entry<String, String> mapEntry) {
            return mapEntry.getValue().startsWith("good");
        }
    }
}

