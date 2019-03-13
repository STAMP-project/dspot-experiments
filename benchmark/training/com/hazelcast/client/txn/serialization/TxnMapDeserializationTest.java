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
package com.hazelcast.client.txn.serialization;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class TxnMapDeserializationTest extends HazelcastTestSupport {
    private static final Object EXISTING_KEY = new SampleIdentified(1);

    private static final Object EXISTING_VALUE = new SampleIdentified(2);

    private static final Object NEW_KEY = new SampleIdentified(3);

    private static final Object NEW_VALUE = new SampleIdentified(4);

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private TransactionalMap<Object, Object> map;

    private TransactionContext context;

    @Test
    public void testMapUpdate() {
        TestCase.assertEquals(TxnMapDeserializationTest.EXISTING_VALUE, map.put(TxnMapDeserializationTest.EXISTING_KEY, TxnMapDeserializationTest.NEW_VALUE));
    }

    @Test
    public void testMapPut() {
        TestCase.assertNull(map.put(TxnMapDeserializationTest.NEW_KEY, TxnMapDeserializationTest.NEW_VALUE));
    }

    @Test
    public void testMapPutTwiceToSameKey() {
        TestCase.assertNull(map.put(TxnMapDeserializationTest.NEW_KEY, TxnMapDeserializationTest.NEW_VALUE));
        TestCase.assertEquals(TxnMapDeserializationTest.NEW_VALUE, map.put(TxnMapDeserializationTest.NEW_KEY, TxnMapDeserializationTest.NEW_VALUE));
    }

    @Test
    public void testMapPutGet() {
        TestCase.assertEquals(TxnMapDeserializationTest.EXISTING_VALUE, map.put(TxnMapDeserializationTest.EXISTING_KEY, TxnMapDeserializationTest.NEW_VALUE));
        TestCase.assertEquals(TxnMapDeserializationTest.NEW_VALUE, map.get(TxnMapDeserializationTest.EXISTING_KEY));
    }

    @Test
    public void testMapGet() {
        TestCase.assertEquals(TxnMapDeserializationTest.EXISTING_VALUE, map.get(TxnMapDeserializationTest.EXISTING_KEY));
    }

    @Test
    public void testMapValues() {
        assertContains(map.values(), TxnMapDeserializationTest.EXISTING_VALUE);
    }

    @Test
    public void testMapKeySet() {
        assertContains(map.keySet(), TxnMapDeserializationTest.EXISTING_KEY);
    }

    @Test
    public void testMapContainsKey() {
        TestCase.assertTrue(map.containsKey(TxnMapDeserializationTest.EXISTING_KEY));
    }

    @Test
    public void testMapRemove() {
        TestCase.assertEquals(TxnMapDeserializationTest.EXISTING_VALUE, map.remove(TxnMapDeserializationTest.EXISTING_KEY));
    }

    @Test
    public void testMapRemoveIfEqual() {
        TestCase.assertTrue(map.remove(TxnMapDeserializationTest.EXISTING_KEY, TxnMapDeserializationTest.EXISTING_VALUE));
    }

    @Test
    public void testMapReplace() {
        TestCase.assertTrue(map.replace(TxnMapDeserializationTest.EXISTING_KEY, TxnMapDeserializationTest.EXISTING_VALUE, TxnMapDeserializationTest.NEW_VALUE));
    }

    @Test
    public void testMapReplaceAndGet() {
        TestCase.assertEquals(TxnMapDeserializationTest.EXISTING_VALUE, map.replace(TxnMapDeserializationTest.EXISTING_KEY, TxnMapDeserializationTest.NEW_VALUE));
    }

    @Test
    public void testGetForUpdate() {
        TestCase.assertEquals(TxnMapDeserializationTest.EXISTING_VALUE, map.getForUpdate(TxnMapDeserializationTest.EXISTING_KEY));
    }
}

