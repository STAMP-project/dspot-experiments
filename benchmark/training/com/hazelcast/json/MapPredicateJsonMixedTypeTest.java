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
package com.hazelcast.json;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.json.Json;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapPredicateJsonMixedTypeTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    TestHazelcastInstanceFactory factory;

    HazelcastInstance instance;

    @Test
    public void testPutGet() {
        IMap map = instance.getMap(HazelcastTestSupport.randomMapName());
        map.put("k_int", 5);
        map.put("k_json", HazelcastJson.fromString("10"));
        map.put("k_int_2", 11);
        Assert.assertEquals(5, map.get("k_int"));
        Assert.assertEquals(10, Json.parse(toJsonString()).asInt());
        Assert.assertEquals(11, map.get("k_int_2"));
    }

    @Test
    public void testThisPredicate() {
        IMap map = instance.getMap(HazelcastTestSupport.randomMapName());
        map.put("k_int", 5);
        map.put("k_json", HazelcastJson.fromString("10"));
        map.put("k_int_2", 11);
        Collection vals = map.values(Predicates.greaterEqual("this", 8));
        Assert.assertEquals(2, vals.size());
        HazelcastTestSupport.assertContains(vals, HazelcastJson.fromString("10"));
        HazelcastTestSupport.assertContains(vals, 11);
    }

    @Test
    public void testPortableWithSamePath() {
        IMap map = instance.getMap(HazelcastTestSupport.randomMapName());
        map.put("k_1", new MapPredicateJsonMixedTypeTest.Person("a", 15, false));
        map.put("k_2", new MapPredicateJsonMixedTypeTest.Person("b", 27, true));
        map.put("k_3", createNameAgeOnDuty("c", 4, false));
        map.put("k_4", createNameAgeOnDuty("d", 77, false));
        Collection vals = map.keySet(Predicates.greaterEqual("age", 20));
        Assert.assertEquals(2, vals.size());
        HazelcastTestSupport.assertContains(vals, "k_2");
        HazelcastTestSupport.assertContains(vals, "k_4");
    }

    public static class Person implements Portable {
        private String name;

        private long age;

        private boolean onDuty;

        public Person() {
        }

        public Person(String name, int age, boolean onDuty) {
            this.name = name;
            this.age = age;
            this.onDuty = onDuty;
        }

        @Override
        public int getFactoryId() {
            return 1;
        }

        @Override
        public int getClassId() {
            return 1;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeUTF("name", this.name);
            writer.writeLong("age", this.age);
            writer.writeBoolean("onDuty", this.onDuty);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            this.name = reader.readUTF("name");
            this.age = reader.readLong("age");
            this.onDuty = reader.readBoolean("onDuty");
        }
    }
}

