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
public class MultiAttributeProjectionTest extends HazelcastTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test(expected = IllegalArgumentException.class)
    public void multiAttribute_attributeNull() {
        Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiAttribute_attributeEmpty() {
        Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiAttribute_attributeNullWithOther() {
        Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute("age", null, "height");
    }

    @Test(expected = IllegalArgumentException.class)
    public void multiAttribute_attributeEmptyWithOther() {
        Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute("age", "", "height");
    }

    @Test
    public void multiAttribute() {
        IMap<String, MultiAttributeProjectionTest.Person> map = getMapWithNodeCount();
        populateMapWithPersons(map);
        Collection<Object[]> result = map.project(Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute("age", "height"));
        Assert.assertThat(result, containsInAnyOrder(new Object[]{ 1.0, 190 }, new Object[]{ 4.0, 123 }));
    }

    @Test
    public void multiAttribute_emptyMap() {
        IMap<String, MultiAttributeProjectionTest.Person> map = getMapWithNodeCount();
        Collection<Object[]> result = map.project(Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute("age", "height"));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void multiAttribute_key() {
        IMap<String, MultiAttributeProjectionTest.Person> map = getMapWithNodeCount();
        populateMapWithPersons(map);
        Collection<Object[]> result = map.project(Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute("__key"));
        Assert.assertThat(result, containsInAnyOrder(new Object[]{ "key1" }, new Object[]{ "key2" }));
    }

    @Test
    public void multiAttribute_this() {
        IMap<String, Integer> map = getMapWithNodeCount();
        map.put("key1", 1);
        map.put("key2", 2);
        Collection<Object[]> result = map.project(Projections.<Map.Entry<String, Integer>>multiAttribute("this"));
        Assert.assertThat(result, containsInAnyOrder(new Object[]{ 1 }, new Object[]{ 2 }));
    }

    @Test
    public void multiAttribute_null() {
        IMap<String, MultiAttributeProjectionTest.Person> map = getMapWithNodeCount();
        map.put("key1", new MultiAttributeProjectionTest.Person(1.0, null));
        map.put("007", new MultiAttributeProjectionTest.Person(null, 144));
        Collection<Object[]> result = map.project(Projections.<Map.Entry<String, MultiAttributeProjectionTest.Person>>multiAttribute("age", "height"));
        Assert.assertThat(result, containsInAnyOrder(new Object[]{ 1.0, null }, new Object[]{ null, 144 }));
    }

    @Test
    public void multiAttribute_nonExistingProperty() {
        IMap<String, MultiAttributeProjectionTest.Person> map = getMapWithNodeCount();
        populateMapWithPersons(map);
        Projection<Map.Entry<String, MultiAttributeProjectionTest.Person>, Object[]> projection = Projections.multiAttribute("age", "height123");
        expected.expect(QueryException.class);
        map.project(projection);
    }

    public static class Person implements DataSerializable {
        public Double age;

        public Integer height;

        public Person() {
        }

        public Person(Double age, Integer height) {
            this.age = age;
            this.height = height;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(age);
            out.writeObject(height);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            age = in.readObject();
            height = in.readObject();
        }
    }
}

