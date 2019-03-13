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


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Iterates over all {@link DataSerializable} and {@link IdentifiedDataSerializable} classes
 * and checks if they have to implement {@link Versioned}.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@SuppressWarnings("WeakerAccess")
public class DataSerializableImplementsVersionedTest {
    private InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();

    private Set<Class<? extends IdentifiedDataSerializable>> idsClasses;

    private Set<Class<? extends DataSerializable>> dsClasses;

    @Test
    public void testIdentifiedDataSerializableForVersionedInterface() throws Exception {
        for (Class<? extends IdentifiedDataSerializable> idsClass : idsClasses) {
            System.out.println(idsClass.getSimpleName());
            IdentifiedDataSerializable identifiedDataSerializable = getInstance(idsClass);
            if (identifiedDataSerializable == null) {
                continue;
            }
            checkInstanceOfVersion(idsClass, identifiedDataSerializable);
        }
    }

    @Test
    public void testDataSerializableForVersionedInterface() throws Exception {
        for (Class<? extends DataSerializable> dsClass : dsClasses) {
            System.out.println(dsClass.getSimpleName());
            DataSerializable dataSerializable = getInstance(dsClass);
            if (dataSerializable == null) {
                continue;
            }
            checkInstanceOfVersion(dsClass, dataSerializable);
        }
    }
}

