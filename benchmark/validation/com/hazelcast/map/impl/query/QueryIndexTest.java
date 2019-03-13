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
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
public class QueryIndexTest extends HazelcastTestSupport {
    @Parameterized.Parameter(0)
    public IndexCopyBehavior copyBehavior;

    @Test
    public void testResultsReturned_whenCustomAttributeIndexed() {
        HazelcastInstance h1 = createTestHazelcastInstance();
        IMap<String, CustomObject> imap = h1.getMap("objects");
        imap.addIndex("attribute", true);
        for (int i = 0; i < 10; i++) {
            CustomAttribute attr = new CustomAttribute(i, 200);
            CustomObject object = new CustomObject(("o" + i), UUID.randomUUID(), attr);
            imap.put(object.getName(), object);
        }
        EntryObject entry = new PredicateBuilder().getEntryObject();
        Predicate predicate = entry.get("attribute").greaterEqual(new CustomAttribute(5, 200));
        Collection<CustomObject> values = imap.values(predicate);
        Assert.assertEquals(5, values.size());
    }

    @Test(timeout = 1000 * 60)
    public void testDeletingNonExistingObject() {
        HazelcastInstance instance = createTestHazelcastInstance();
        IMap<Integer, SampleTestObjects.Value> map = instance.getMap(HazelcastTestSupport.randomMapName());
        map.addIndex("name", false);
        map.delete(1);
    }

    @Test(timeout = 1000 * 60)
    public void testInnerIndex() {
        HazelcastInstance instance = createTestHazelcastInstance();
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", false);
        map.addIndex("type.typeName", false);
        for (int i = 0; i < 10; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i), (i < 5 ? null : new SampleTestObjects.ValueType(("type" + i))), i);
            map.put(("" + i), v);
        }
        Predicate predicate = new PredicateBuilder().getEntryObject().get("type.typeName").in("type8", "type6");
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        Assert.assertEquals(2, values.size());
        List<String> typeNames = new ArrayList<String>();
        for (SampleTestObjects.Value configObject : values) {
            typeNames.add(configObject.getType().getTypeName());
        }
        String[] array = typeNames.toArray(new String[0]);
        Arrays.sort(array);
        Assert.assertArrayEquals(typeNames.toString(), new String[]{ "type6", "type8" }, array);
    }

    @Test(timeout = 1000 * 60)
    public void testInnerIndexSql() {
        HazelcastInstance instance = createTestHazelcastInstance();
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", false);
        map.addIndex("type.typeName", false);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i), new SampleTestObjects.ValueType(("type" + i)), i);
            map.put(("" + i), v);
        }
        Predicate predicate = new SqlPredicate("type.typeName='type1'");
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        Assert.assertEquals(1, values.size());
        List<String> typeNames = new ArrayList<String>();
        for (SampleTestObjects.Value configObject : values) {
            typeNames.add(configObject.getType().getTypeName());
        }
        Assert.assertArrayEquals(typeNames.toString(), new String[]{ "type1" }, typeNames.toArray(new String[0]));
    }

    @Test(timeout = 1000 * 60)
    public void issue685RemoveIndexesOnClear() {
        HazelcastInstance instance = createTestHazelcastInstance();
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", true);
        for (int i = 0; i < 4; i++) {
            SampleTestObjects.Value v = new SampleTestObjects.Value(("name" + i));
            map.put(("" + i), v);
        }
        map.clear();
        Predicate predicate = new SqlPredicate("name='name0'");
        Collection<SampleTestObjects.Value> values = map.values(predicate);
        Assert.assertEquals(0, values.size());
    }

    @Test(timeout = 1000 * 60)
    public void testQueryDoesNotMatchOldResults_whenEntriesAreUpdated() {
        HazelcastInstance instance = createTestHazelcastInstance();
        IMap<String, SampleTestObjects.Value> map = instance.getMap("default");
        map.addIndex("name", true);
        map.put("0", new SampleTestObjects.Value("name"));
        map.put("0", new SampleTestObjects.Value("newName"));
        Collection<SampleTestObjects.Value> values = map.values(new SqlPredicate("name='name'"));
        Assert.assertEquals(0, values.size());
    }

    @Test(timeout = 1000 * 60)
    public void testOneIndexedFieldsWithTwoCriteriaField() {
        HazelcastInstance h1 = createTestHazelcastInstance();
        IMap<String, SampleTestObjects.Employee> map = h1.getMap("employees");
        map.addIndex("name", false);
        map.put("1", new SampleTestObjects.Employee(1L, "joe", 30, true, 100.0));
        EntryObject e = new PredicateBuilder().getEntryObject();
        PredicateBuilder a = e.get("name").equal("joe");
        Predicate b = e.get("age").equal("30");
        Collection<SampleTestObjects.Employee> actual = map.values(a.and(b));
        Assert.assertEquals(1, actual.size());
    }

    @Test(timeout = 1000 * 60)
    public void testPredicateNotEqualWithIndex() {
        HazelcastInstance instance = createTestHazelcastInstance();
        IMap<Integer, SampleTestObjects.Value> map1 = instance.getMap("testPredicateNotEqualWithIndex-ordered");
        IMap<Integer, SampleTestObjects.Value> map2 = instance.getMap("testPredicateNotEqualWithIndex-unordered");
        testPredicateNotEqualWithIndex(map1, true);
        testPredicateNotEqualWithIndex(map2, false);
    }
}

