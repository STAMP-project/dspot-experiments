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


import Index.OperationSource.USER;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IndexesTest {
    private final InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();

    @Parameterized.Parameter(0)
    public IndexCopyBehavior copyBehavior;

    @Test
    public void testAndWithSingleEntry() {
        Indexes indexes = Indexes.newBuilder(serializationService, copyBehavior).build();
        indexes.addOrGetIndex("name", false);
        indexes.addOrGetIndex("age", true);
        indexes.addOrGetIndex("salary", true);
        for (int i = 0; i < 100; i++) {
            SampleTestObjects.Employee employee = new SampleTestObjects.Employee((i + "Name"), (i % 80), ((i % 2) == 0), (100 + (i % 1000)));
            indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(i), employee, newExtractor()), null, USER);
        }
        int count = 10;
        Set<String> ages = new HashSet<String>(count);
        for (int i = 0; i < count; i++) {
            ages.add(String.valueOf(i));
        }
        EntryObject entryObject = new PredicateBuilder().getEntryObject();
        PredicateBuilder predicate = entryObject.get("name").equal("0Name").and(entryObject.get("age").in(ages.toArray(new String[0])));
        Set<QueryableEntry> results = indexes.query(predicate);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void testIndex() {
        Indexes indexes = Indexes.newBuilder(serializationService, copyBehavior).build();
        indexes.addOrGetIndex("name", false);
        indexes.addOrGetIndex("age", true);
        indexes.addOrGetIndex("salary", true);
        for (int i = 0; i < 2000; i++) {
            SampleTestObjects.Employee employee = new SampleTestObjects.Employee((i + "Name"), (i % 80), ((i % 2) == 0), (100 + (i % 100)));
            indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(i), employee, newExtractor()), null, USER);
        }
        for (int i = 0; i < 10; i++) {
            SqlPredicate predicate = new SqlPredicate("salary=161 and age >20 and age <23");
            Set<QueryableEntry> results = new HashSet<QueryableEntry>(indexes.query(predicate));
            Assert.assertEquals(5, results.size());
        }
    }

    @Test
    public void testIndex2() {
        Indexes indexes = Indexes.newBuilder(serializationService, copyBehavior).build();
        indexes.addOrGetIndex("name", false);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(1), new SampleTestObjects.Value("abc"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(2), new SampleTestObjects.Value("xyz"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(3), new SampleTestObjects.Value("aaa"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(4), new SampleTestObjects.Value("zzz"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(5), new SampleTestObjects.Value("klm"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(6), new SampleTestObjects.Value("prs"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(7), new SampleTestObjects.Value("prs"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(8), new SampleTestObjects.Value("def"), newExtractor()), null, USER);
        indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(9), new SampleTestObjects.Value("qwx"), newExtractor()), null, USER);
        Assert.assertEquals(8, new HashSet<QueryableEntry>(indexes.query(new SqlPredicate("name > 'aac'"))).size());
    }

    /**
     * Imagine we have only keys and nullable values. And we add index for a field of that nullable object.
     * When we execute a query on keys, there should be no returned value from indexing service and it does not
     * throw exception.
     */
    @Test
    public void shouldNotThrowException_withNullValues_whenIndexAddedForValueField() {
        Indexes indexes = Indexes.newBuilder(serializationService, copyBehavior).build();
        indexes.addOrGetIndex("name", false);
        shouldReturnNull_whenQueryingOnKeys(indexes);
    }

    @Test
    public void shouldNotThrowException_withNullValues_whenNoIndexAdded() {
        Indexes indexes = Indexes.newBuilder(serializationService, copyBehavior).build();
        shouldReturnNull_whenQueryingOnKeys(indexes);
    }

    @Test
    public void shouldNotThrowException_withNullValue_whenIndexAddedForKeyField() {
        Indexes indexes = Indexes.newBuilder(serializationService, copyBehavior).build();
        indexes.addOrGetIndex("__key", false);
        for (int i = 0; i < 100; i++) {
            // passing null value to QueryEntry
            indexes.putEntry(new QueryEntry(serializationService, TestUtil.toData(i), null, newExtractor()), null, USER);
        }
        Set<QueryableEntry> query = indexes.query(new SqlPredicate("__key > 10 "));
        Assert.assertEquals(89, query.size());
    }

    @Test
    public void testNoDuplicateIndexes() {
        Indexes indexes = Indexes.newBuilder(serializationService, copyBehavior).build();
        InternalIndex index = indexes.addOrGetIndex("a", false);
        Assert.assertNotNull(index);
        Assert.assertSame(index, indexes.addOrGetIndex("a", false));
        index = indexes.addOrGetIndex("a, b", false);
        Assert.assertNotNull(index);
        Assert.assertSame(index, indexes.addOrGetIndex("a, b", false));
        Assert.assertSame(index, indexes.addOrGetIndex("this.a, b", false));
    }
}

