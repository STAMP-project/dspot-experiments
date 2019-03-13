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


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class PortableClassVersionTest {
    private static final int FACTORY_ID = TestSerializationConstants.PORTABLE_FACTORY_ID;

    @Test
    public void testDifferentClassVersions() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortable();
            }
        }).build();
        SerializationService serializationService2 = new DefaultSerializationServiceBuilder().addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortableV2();
            }
        }).build();
        PortableClassVersionTest.testDifferentClassVersions(serializationService, serializationService2);
    }

    @Test
    public void testDifferentClassAndServiceVersions() {
        SerializationService serializationService = new DefaultSerializationServiceBuilder().setPortableVersion(1).addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortable();
            }
        }).build();
        SerializationService serializationService2 = new DefaultSerializationServiceBuilder().setPortableVersion(2).addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortableV2();
            }
        }).build();
        PortableClassVersionTest.testDifferentClassVersions(serializationService, serializationService2);
    }

    @Test
    public void testDifferentClassVersionsUsingDataWriteAndRead() throws Exception {
        InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortable();
            }
        }).build();
        InternalSerializationService serializationService2 = new DefaultSerializationServiceBuilder().addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortableV2();
            }
        }).build();
        PortableClassVersionTest.testDifferentClassVersionsUsingDataWriteAndRead(serializationService, serializationService2);
    }

    @Test
    public void testDifferentClassAndServiceVersionsUsingDataWriteAndRead() throws Exception {
        InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().setPortableVersion(1).addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortable();
            }
        }).build();
        InternalSerializationService serializationService2 = new DefaultSerializationServiceBuilder().setPortableVersion(2).addPortableFactory(PortableClassVersionTest.FACTORY_ID, new PortableFactory() {
            public Portable create(int classId) {
                return new NamedPortableV2();
            }
        }).build();
        PortableClassVersionTest.testDifferentClassVersionsUsingDataWriteAndRead(serializationService, serializationService2);
    }

    @Test
    public void testPreDefinedDifferentVersionsWithInnerPortable() {
        InternalSerializationService serializationService = PortableTest.createSerializationService(1);
        serializationService.getPortableContext().registerClassDefinition(PortableClassVersionTest.createInnerPortableClassDefinition(1));
        InternalSerializationService serializationService2 = PortableTest.createSerializationService(2);
        serializationService2.getPortableContext().registerClassDefinition(PortableClassVersionTest.createInnerPortableClassDefinition(2));
        NamedPortable[] nn = new NamedPortable[1];
        nn[0] = new NamedPortable("name", 123);
        InnerPortable inner = new InnerPortable(new byte[]{ 0, 1, 2 }, new char[]{ 'c', 'h', 'a', 'r' }, new short[]{ 3, 4, 5 }, new int[]{ 9, 8, 7, 6 }, new long[]{ 0, 1, 5, 7, 9, 11 }, new float[]{ 0.6543F, -3.56F, 45.67F }, new double[]{ 456.456, 789.789, 321.321 }, nn);
        MainPortable mainWithInner = new MainPortable(((byte) (113)), true, 'x', ((short) (-500)), 56789, (-50992225L), 900.5678F, (-897543.3678909), "this is main portable object created for testing!", inner);
        PortableClassVersionTest.testPreDefinedDifferentVersions(serializationService, serializationService2, mainWithInner);
    }

    @Test
    public void testPreDefinedDifferentVersionsWithNullInnerPortable() {
        InternalSerializationService serializationService = PortableTest.createSerializationService(1);
        serializationService.getPortableContext().registerClassDefinition(PortableClassVersionTest.createInnerPortableClassDefinition(1));
        InternalSerializationService serializationService2 = PortableTest.createSerializationService(2);
        serializationService2.getPortableContext().registerClassDefinition(PortableClassVersionTest.createInnerPortableClassDefinition(2));
        MainPortable mainWithNullInner = new MainPortable(((byte) (113)), true, 'x', ((short) (-500)), 56789, (-50992225L), 900.5678F, (-897543.3678909), "this is main portable object created for testing!", null);
        PortableClassVersionTest.testPreDefinedDifferentVersions(serializationService, serializationService2, mainWithNullInner);
    }
}

