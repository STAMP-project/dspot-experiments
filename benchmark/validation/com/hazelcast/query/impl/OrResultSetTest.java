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


import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.QueryException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class OrResultSetTest extends HazelcastTestSupport {
    @Test
    public void size() {
        int size = 100000;
        Set<QueryableEntry> entries1 = generateEntries(size);
        Set<QueryableEntry> entries2 = generateEntries(size);
        List<Set<QueryableEntry>> indexedResults = new ArrayList<Set<QueryableEntry>>();
        indexedResults.add(entries1);
        indexedResults.add(entries2);
        OrResultSet resultSet = new OrResultSet(indexedResults);
        int sizeMethod = resultSet.size();
        int sizeIterator = 0;
        for (QueryableEntry queryableEntry : resultSet) {
            sizeIterator++;
        }
        Assert.assertEquals((2 * size), sizeIterator);
        Assert.assertEquals((2 * size), sizeMethod);
    }

    @Test
    public void contains() {
        int size = 100000;
        Set<QueryableEntry> entries1 = generateEntries(size);
        Set<QueryableEntry> entries2 = generateEntries(size);
        List<Set<QueryableEntry>> indexedResults = new ArrayList<Set<QueryableEntry>>();
        indexedResults.add(entries1);
        indexedResults.add(entries2);
        OrResultSet resultSet = new OrResultSet(indexedResults);
        Set<QueryableEntry> combinedEntries = new HashSet<QueryableEntry>(entries1);
        combinedEntries.addAll(entries2);
        for (QueryableEntry entry : combinedEntries) {
            HazelcastTestSupport.assertContains(resultSet, entry);
        }
        HazelcastTestSupport.assertNotContains(resultSet, new OrResultSetTest.DummyEntry());
    }

    private class DummyEntry extends QueryableEntry {
        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public Object getKey() {
            return null;
        }

        @Override
        public Comparable getAttributeValue(String attributeName) throws QueryException {
            return null;
        }

        @Override
        public Object getTargetObject(boolean key) {
            return null;
        }

        @Override
        public Object setValue(Object value) {
            return null;
        }

        @Override
        public Data getKeyData() {
            return null;
        }

        @Override
        public Data getValueData() {
            return null;
        }
    }
}

