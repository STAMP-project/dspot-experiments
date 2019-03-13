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
package com.hazelcast.map.impl.recordstore;


import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.JVMUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LazyEntryViewFromRecordTest {
    private static final int ENTRY_VIEW_COST_IN_BYTES = 77 + (4 * (JVMUtil.REFERENCE_COST_IN_BYTES));

    private final String key = "key";

    private final String value = "value";

    private Record<Data> recordInstance;

    private EntryView view;

    @Test
    public void test_getKey() throws Exception {
        Assert.assertEquals(key, view.getKey());
    }

    @Test
    public void test_getValue() throws Exception {
        Assert.assertEquals(value, view.getValue());
    }

    @Test
    public void test_getCost() throws Exception {
        Assert.assertEquals(LazyEntryViewFromRecordTest.ENTRY_VIEW_COST_IN_BYTES, view.getCost());
    }

    @Test
    public void test_getCreationTime() throws Exception {
        Assert.assertEquals(0, view.getCreationTime());
    }

    @Test
    public void test_getExpirationTime() throws Exception {
        Assert.assertEquals(0, view.getExpirationTime());
    }

    @Test
    public void test_getHits() throws Exception {
        Assert.assertEquals(0, view.getHits());
    }

    @Test
    public void test_getLastAccessTime() throws Exception {
        Assert.assertEquals(0, view.getLastAccessTime());
    }

    @Test
    public void test_getLastStoredTime() throws Exception {
        Assert.assertEquals(0, view.getLastStoredTime());
    }

    @Test
    public void test_getLastUpdateTime() throws Exception {
        Assert.assertEquals(0, view.getLastUpdateTime());
    }

    @Test
    public void test_getVersion() throws Exception {
        Assert.assertEquals(0, view.getVersion());
    }

    @Test
    public void test_getTtl() throws Exception {
        Assert.assertEquals(0, view.getTtl());
    }

    @Test
    public void test_getRecord() throws Exception {
        Assert.assertEquals(recordInstance, getRecord());
    }

    @Test
    public void test_equals() throws Exception {
        EntryView entryView = createDefaultEntryView();
        Assert.assertTrue(((view.equals(entryView)) && (entryView.equals(view))));
    }

    @Test
    public void test_equals_whenSameReference() throws Exception {
        Assert.assertTrue(view.equals(view));
    }

    @Test
    public void test_equals_whenSuppliedObjectIsNotEntryView() throws Exception {
        Assert.assertFalse(view.equals(this));
    }

    @Test
    public void test_hashCode() throws Exception {
        EntryView entryView = createDefaultEntryView();
        Assert.assertEquals(entryView.hashCode(), view.hashCode());
    }

    @Test
    public void test_toString() throws Exception {
        String expected = ((((((((((((((((((((("EntryView{key=" + (view.getKey())) + ", value=") + (view.getValue())) + ", cost=") + (view.getCost())) + ", version=") + (view.getVersion())) + ", creationTime=") + (view.getCreationTime())) + ", expirationTime=") + (view.getExpirationTime())) + ", hits=") + (view.getHits())) + ", lastAccessTime=") + (view.getLastAccessTime())) + ", lastStoredTime=") + (view.getLastStoredTime())) + ", lastUpdateTime=") + (view.getLastUpdateTime())) + ", ttl=") + (view.getTtl())) + '}';
        Assert.assertEquals(expected, view.toString());
    }
}

