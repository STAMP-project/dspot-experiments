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
package com.hazelcast.projection.impl;


import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.projection.Projection;
import com.hazelcast.projection.Projections;
import com.hazelcast.query.QueryException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SingleAttributeProjectionTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test(expected = IllegalArgumentException.class)
    public void singleAttribute_attributeNull() {
        Projections.<Map.Entry<String, SingleAttributeProjectionTest.Person>, Double>singleAttribute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void singleAttribute_attributeEmpty() {
        Projections.<Map.Entry<String, SingleAttributeProjectionTest.Person>, Double>singleAttribute("");
    }

    @Test
    public void singleAttribute() {
        IMap<String, SingleAttributeProjectionTest.Person> map = getMapWithNodeCount();
        populateMapWithPersons(map);
        Collection<Double> result = map.project(Projections.<Map.Entry<String, SingleAttributeProjectionTest.Person>, Double>singleAttribute("age"));
        Assert.assertThat(result, containsInAnyOrder(1.0, 4.0, 7.0));
    }

    @Test
    public void singleAttribute_key() {
        IMap<String, SingleAttributeProjectionTest.Person> map = getMapWithNodeCount();
        populateMapWithPersons(map);
        Collection<String> result = map.project(Projections.<Map.Entry<String, SingleAttributeProjectionTest.Person>, String>singleAttribute("__key"));
        Assert.assertThat(result, containsInAnyOrder("key1", "key2", "key3"));
    }

    @Test
    public void singleAttribute_this() {
        IMap<String, Integer> map = getMapWithNodeCount();
        map.put("key1", 1);
        map.put("key2", 2);
        Collection<Integer> result = map.project(Projections.<Map.Entry<String, Integer>, Integer>singleAttribute("this"));
        Assert.assertThat(result, containsInAnyOrder(1, 2));
    }

    @Test
    public void singleAttribute_emptyMap() {
        IMap<String, SingleAttributeProjectionTest.Person> map = getMapWithNodeCount();
        Collection<Double> result = map.project(Projections.<Map.Entry<String, SingleAttributeProjectionTest.Person>, Double>singleAttribute("age"));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void singleAttribute_null() {
        IMap<String, SingleAttributeProjectionTest.Person> map = getMapWithNodeCount();
        map.put("key1", new SingleAttributeProjectionTest.Person(1.0));
        map.put("007", new SingleAttributeProjectionTest.Person(null));
        Collection<Double> result = map.project(Projections.<Map.Entry<String, SingleAttributeProjectionTest.Person>, Double>singleAttribute("age"));
        Assert.assertThat(result, containsInAnyOrder(null, 1.0));
    }

    @Test
    public void singleAttribute_nonExistingProperty() {
        IMap<String, SingleAttributeProjectionTest.Person> map = getMapWithNodeCount();
        populateMapWithPersons(map);
        Projection<Map.Entry<String, SingleAttributeProjectionTest.Person>, Double> projection = Projections.singleAttribute("age123");
        expected.expect(QueryException.class);
        map.project(projection);
    }

    public static class Person implements DataSerializable {
        public Double age;

        public Person() {
        }

        public Person(Double age) {
            this.age = age;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(age);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            age = in.readObject();
        }
    }
}

