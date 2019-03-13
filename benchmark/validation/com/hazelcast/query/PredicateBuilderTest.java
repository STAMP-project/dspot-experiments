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
package com.hazelcast.query;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class PredicateBuilderTest extends HazelcastTestSupport {
    @Test
    public void get_keyAttribute() {
        HazelcastInstance hz = createHazelcastInstance();
        EntryObject entryObject = new PredicateBuilder().getEntryObject();
        Predicate predicate = entryObject.key().get("id").equal("10").and(entryObject.get("name").equal("value1"));
        IMap<PredicateBuilderTest.Id, PredicateBuilderTest.Value> hazelcastLookupMap = hz.getMap("someMap");
        hazelcastLookupMap.put(new PredicateBuilderTest.Id("10"), new PredicateBuilderTest.Value("value1"));
        hazelcastLookupMap.put(new PredicateBuilderTest.Id("20"), new PredicateBuilderTest.Value("value2"));
        hazelcastLookupMap.put(new PredicateBuilderTest.Id("30"), new PredicateBuilderTest.Value("value3"));
        Collection<PredicateBuilderTest.Value> result = hazelcastLookupMap.values(predicate);
        Assert.assertEquals(1, result.size());
        HazelcastTestSupport.assertContains(result, new PredicateBuilderTest.Value("value1"));
    }

    @Test
    public void get_key() {
        HazelcastInstance hz = createHazelcastInstance();
        EntryObject entryObject = new PredicateBuilder().getEntryObject();
        Predicate predicate = entryObject.key().equal(10L);
        IMap<Integer, Integer> hazelcastLookupMap = hz.getMap("someMap");
        hazelcastLookupMap.put(10, 1);
        hazelcastLookupMap.put(30, 2);
        Collection<Integer> result = hazelcastLookupMap.values(predicate);
        Assert.assertEquals(1, result.size());
        HazelcastTestSupport.assertContains(result, 1);
    }

    @Test
    public void get_this() {
        HazelcastInstance hz = createHazelcastInstance();
        EntryObject entryObject = new PredicateBuilder().getEntryObject();
        Predicate predicate = entryObject.get("this").equal(1L);
        IMap<Integer, Integer> hazelcastLookupMap = hz.getMap("someMap");
        hazelcastLookupMap.put(10, 1);
        hazelcastLookupMap.put(30, 2);
        Collection<Integer> result = hazelcastLookupMap.values(predicate);
        Assert.assertEquals(1, result.size());
        HazelcastTestSupport.assertContains(result, 1);
    }

    @Test
    public void get_attribute() {
        HazelcastInstance hz = createHazelcastInstance();
        EntryObject entryObject = new PredicateBuilder().getEntryObject();
        Predicate predicate = entryObject.get("id").equal("10");
        IMap<Integer, PredicateBuilderTest.Id> hazelcastLookupMap = hz.getMap("someMap");
        hazelcastLookupMap.put(1, new PredicateBuilderTest.Id("10"));
        hazelcastLookupMap.put(2, new PredicateBuilderTest.Id("20"));
        hazelcastLookupMap.put(3, new PredicateBuilderTest.Id("30"));
        Collection<PredicateBuilderTest.Id> result = hazelcastLookupMap.values(predicate);
        Assert.assertEquals(1, result.size());
        HazelcastTestSupport.assertContains(result, new PredicateBuilderTest.Id("10"));
    }

    private static final class Id implements Serializable {
        private String id;

        private Id(String id) {
            this.id = id;
        }

        private String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            PredicateBuilderTest.Id id1 = ((PredicateBuilderTest.Id) (o));
            if ((id) != null ? !(id.equals(id1.id)) : (id1.id) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return (id) != null ? id.hashCode() : 0;
        }
    }

    private static final class Value implements Serializable {
        private String name;

        private Value(String name) {
            this.name = name;
        }

        private String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            PredicateBuilderTest.Value value = ((PredicateBuilderTest.Value) (o));
            if ((name) != null ? !(name.equals(value.name)) : (value.name) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return (name) != null ? name.hashCode() : 0;
        }
    }
}

