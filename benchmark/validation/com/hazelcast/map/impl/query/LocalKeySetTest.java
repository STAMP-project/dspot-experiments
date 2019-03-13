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
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestCollectionUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalKeySetTest extends HazelcastTestSupport {
    private IMap<String, String> map;

    private SerializationService serializationService;

    private String localKey1;

    private String localKey2;

    private String localKey3;

    private String remoteKey1;

    private String remoteKey2;

    private String remoteKey3;

    @Test(expected = NullPointerException.class)
    public void whenPredicateNull() {
        map.localKeySet(null);
    }

    @Test
    public void whenMapEmpty() {
        Set<String> result = map.localKeySet(INSTANCE);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void whenSelecting_withoutPredicate() {
        map.put(localKey1, "a");
        map.put(localKey2, "b");
        map.put(remoteKey1, "c");
        Set<String> result = map.localKeySet();
        Assert.assertEquals(TestCollectionUtils.setOf(localKey1, localKey2), result);
    }

    @Test
    public void whenSelectingAllEntries() {
        map.put(localKey1, "a");
        map.put(localKey2, "b");
        map.put(remoteKey1, "c");
        Set<String> result = map.localKeySet(INSTANCE);
        Assert.assertEquals(TestCollectionUtils.setOf(localKey1, localKey2), result);
    }

    @Test
    public void whenSelectingSomeEntries() {
        map.put(localKey1, "good1");
        map.put(localKey2, "bad");
        map.put(localKey3, "good2");
        map.put(remoteKey1, "good3");
        map.put(remoteKey2, "bad");
        map.put(remoteKey3, "good4");
        Set<String> result = map.localKeySet(new LocalKeySetTest.GoodPredicate());
        Assert.assertEquals(TestCollectionUtils.setOf(localKey1, localKey3), result);
    }

    @Test
    public void testResultType() {
        map.put(localKey1, "a");
        Set<String> entries = map.localKeySet(INSTANCE);
        QueryResultCollection collection = HazelcastTestSupport.assertInstanceOf(QueryResultCollection.class, entries);
        QueryResultRow row = ((QueryResultRow) (collection.getRows().iterator().next()));
        Assert.assertEquals(localKey1, serializationService.toObject(row.getKey()));
        Assert.assertNull(row.getValue());
    }

    static class GoodPredicate implements Predicate<String, String> {
        @Override
        public boolean apply(Map.Entry<String, String> mapEntry) {
            return mapEntry.getValue().startsWith("good");
        }
    }
}

