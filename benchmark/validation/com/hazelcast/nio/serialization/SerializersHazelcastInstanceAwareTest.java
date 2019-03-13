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
package com.hazelcast.nio.serialization;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class SerializersHazelcastInstanceAwareTest extends HazelcastTestSupport {
    @Test
    public void testPortableFactoryInstance() {
        SerializersHazelcastInstanceAwareTest.HazelcastInstanceAwarePortableFactory factory = new SerializersHazelcastInstanceAwareTest.HazelcastInstanceAwarePortableFactory();
        Config config = new Config();
        config.getSerializationConfig().addPortableFactory(1, factory);
        HazelcastInstance instance = createHazelcastInstance(config);
        Map<String, SerializersHazelcastInstanceAwareTest.PortablePerson> map = instance.getMap("map");
        map.put("1", new SerializersHazelcastInstanceAwareTest.PortablePerson());
        SerializersHazelcastInstanceAwareTest.PortablePerson person = map.get("1");
        Assert.assertNotNull("HazelcastInstance should have been set", person.hz);
    }

    @Test
    public void testPortableFactoryClass() {
        Config config = new Config();
        config.getSerializationConfig().addPortableFactoryClass(1, SerializersHazelcastInstanceAwareTest.HazelcastInstanceAwarePortableFactory.class.getName());
        HazelcastInstance instance = createHazelcastInstance(config);
        Map<String, SerializersHazelcastInstanceAwareTest.PortablePerson> map = instance.getMap("map");
        map.put("1", new SerializersHazelcastInstanceAwareTest.PortablePerson());
        SerializersHazelcastInstanceAwareTest.PortablePerson person = map.get("1");
        Assert.assertNotNull("HazelcastInstance should have been set", person.hz);
    }

    @Test
    public void testDataSerializableFactoryInstance() {
        SerializersHazelcastInstanceAwareTest.HazelcastInstanceAwareDataSerializableFactory factory = new SerializersHazelcastInstanceAwareTest.HazelcastInstanceAwareDataSerializableFactory();
        Config config = new Config();
        config.getSerializationConfig().addDataSerializableFactory(1, factory);
        HazelcastInstance instance = createHazelcastInstance(config);
        Map<String, SerializersHazelcastInstanceAwareTest.DataSerializablePerson> map = instance.getMap("map");
        map.put("1", new SerializersHazelcastInstanceAwareTest.DataSerializablePerson());
        SerializersHazelcastInstanceAwareTest.DataSerializablePerson person = map.get("1");
        Assert.assertNotNull("HazelcastInstance should have been set", person.hz);
    }

    @Test
    public void testDataSerializableFactoryClass() {
        Config config = new Config();
        config.getSerializationConfig().addDataSerializableFactoryClass(1, SerializersHazelcastInstanceAwareTest.HazelcastInstanceAwareDataSerializableFactory.class.getName());
        HazelcastInstance instance = createHazelcastInstance(config);
        Map<String, SerializersHazelcastInstanceAwareTest.DataSerializablePerson> map = instance.getMap("map");
        map.put("1", new SerializersHazelcastInstanceAwareTest.DataSerializablePerson());
        SerializersHazelcastInstanceAwareTest.DataSerializablePerson person = map.get("1");
        Assert.assertNotNull("HazelcastInstance should have been set", person.hz);
    }

    private static class HazelcastInstanceAwarePortableFactory implements HazelcastInstanceAware , PortableFactory {
        private HazelcastInstance hz;

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hz = hazelcastInstance;
        }

        @Override
        public Portable create(int classId) {
            SerializersHazelcastInstanceAwareTest.PortablePerson p = new SerializersHazelcastInstanceAwareTest.PortablePerson();
            p.hz = hz;
            return p;
        }
    }

    private static class PortablePerson implements Portable {
        private HazelcastInstance hz;

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
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
        }
    }

    private static class HazelcastInstanceAwareDataSerializableFactory implements HazelcastInstanceAware , DataSerializableFactory {
        private HazelcastInstance hz;

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hz = hazelcastInstance;
        }

        @Override
        public IdentifiedDataSerializable create(int typeId) {
            SerializersHazelcastInstanceAwareTest.DataSerializablePerson p = new SerializersHazelcastInstanceAwareTest.DataSerializablePerson();
            p.hz = hz;
            return p;
        }
    }

    private static class DataSerializablePerson implements IdentifiedDataSerializable {
        private HazelcastInstance hz;

        @Override
        public int getFactoryId() {
            return 1;
        }

        @Override
        public int getId() {
            return 1;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
        }
    }
}

