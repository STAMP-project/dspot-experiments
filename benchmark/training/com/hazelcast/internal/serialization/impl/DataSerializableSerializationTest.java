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
package com.hazelcast.internal.serialization.impl;


import InternalSerializationService.VERSION_1;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.VersionedDataSerializableFactory;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.Version;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DataSerializableSerializationTest extends HazelcastTestSupport {
    private SerializationService ss = new DefaultSerializationServiceBuilder().setVersion(VERSION_1).build();

    @Test
    public void serializeAndDeserialize_DataSerializable() {
        DataSerializableSerializationTest.DSPerson person = new DataSerializableSerializationTest.DSPerson("James Bond");
        DataSerializableSerializationTest.DSPerson deserialized = ss.toObject(ss.toData(person));
        Assert.assertEquals(person.getClass(), deserialized.getClass());
        Assert.assertEquals(person.name, deserialized.name);
    }

    @Test
    public void serializeAndDeserialize_IdentifiedDataSerializable() {
        DataSerializableSerializationTest.IDSPerson person = new DataSerializableSerializationTest.IDSPerson("James Bond");
        SerializationService ss = new DefaultSerializationServiceBuilder().addDataSerializableFactory(1, new DataSerializableSerializationTest.IDSPersonFactory()).setVersion(VERSION_1).build();
        DataSerializableSerializationTest.IDSPerson deserialized = ss.toObject(ss.toData(person));
        Assert.assertEquals(person.getClass(), deserialized.getClass());
        Assert.assertEquals(person.name, deserialized.name);
    }

    @Test
    public void serializeAndDeserialize_IdentifiedDataSerializable_versionedFactory() {
        DataSerializableSerializationTest.IDSPerson person = new DataSerializableSerializationTest.IDSPerson("James Bond");
        SerializationService ss = new DefaultSerializationServiceBuilder().addDataSerializableFactory(1, new DataSerializableSerializationTest.IDSPersonFactoryVersioned()).setVersion(VERSION_1).build();
        DataSerializableSerializationTest.IDSPerson deserialized = ss.toObject(ss.toData(person));
        Assert.assertEquals(person.getClass(), deserialized.getClass());
        Assert.assertEquals(person.name, deserialized.name);
    }

    @Test
    public void testClarifiedExceptionsForUnsupportedClassTypes() {
        class LocalClass implements DataSerializable {
            @Override
            public void writeData(ObjectDataOutput out) {
            }

            @Override
            public void readData(ObjectDataInput in) {
            }
        }
        DataSerializable anonymousInstance = new DataSerializable() {
            @Override
            public void writeData(ObjectDataOutput out) {
            }

            @Override
            public void readData(ObjectDataInput in) {
            }
        };
        DataSerializable[] throwingInstances = new DataSerializable[]{ new LocalClass(), anonymousInstance, new DataSerializableSerializationTest.NonStaticMemberClass() };
        for (DataSerializable throwingInstance : throwingInstances) {
            try {
                ss.toObject(ss.toData(throwingInstance));
            } catch (HazelcastSerializationException e) {
                HazelcastTestSupport.assertInstanceOf(NoSuchMethodException.class, e.getCause());
                HazelcastTestSupport.assertContains(e.getCause().getMessage(), "can't conform to DataSerializable");
                HazelcastTestSupport.assertInstanceOf(NoSuchMethodException.class, e.getCause().getCause());
                continue;
            }
            Assert.fail((("deserialization of '" + (throwingInstance.getClass())) + "' is expected to fail"));
        }
        for (DataSerializable throwingInstance : throwingInstances) {
            try {
                ss.toObject(ss.toData(throwingInstance), throwingInstance.getClass());
            } catch (HazelcastSerializationException e) {
                HazelcastTestSupport.assertInstanceOf(InstantiationException.class, e.getCause());
                HazelcastTestSupport.assertContains(e.getCause().getMessage(), "can't conform to DataSerializable");
                HazelcastTestSupport.assertInstanceOf(InstantiationException.class, e.getCause().getCause());
                continue;
            }
            Assert.fail((("deserialization of '" + (throwingInstance.getClass())) + "' is expected to fail"));
        }
    }

    public class NonStaticMemberClass implements DataSerializable {
        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }
    }

    private static class DSPerson implements DataSerializable {
        private String name;

        DSPerson() {
        }

        DSPerson(String name) {
            this.name = name;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeUTF(name);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            name = in.readUTF();
        }
    }

    private static class IDSPerson implements IdentifiedDataSerializable {
        private String name;

        IDSPerson() {
        }

        IDSPerson(String name) {
            this.name = name;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeUTF(name);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            name = in.readUTF();
        }

        @Override
        public int getFactoryId() {
            return 1;
        }

        @Override
        public int getId() {
            return 2;
        }
    }

    private static class IDSPersonFactory implements DataSerializableFactory {
        @Override
        public IdentifiedDataSerializable create(int typeId) {
            return new DataSerializableSerializationTest.IDSPerson();
        }
    }

    private static class IDSPersonFactoryVersioned implements VersionedDataSerializableFactory {
        @Override
        public IdentifiedDataSerializable create(int typeId) {
            return new DataSerializableSerializationTest.IDSPerson();
        }

        @Override
        public IdentifiedDataSerializable create(int typeId, Version version) {
            throw new RuntimeException("Should not be used outside of the versioned context");
        }
    }
}

