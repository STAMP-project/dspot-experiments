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


import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.SampleIdentifiedDataSerializable;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.VersionAwareConstructorFunction;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ArrayDataSerializableFactoryTest {
    @Test
    public void testCreate() throws Exception {
        ConstructorFunction<Integer, IdentifiedDataSerializable>[] constructorFunctions = new ConstructorFunction[1];
        constructorFunctions[0] = new ConstructorFunction<Integer, IdentifiedDataSerializable>() {
            @Override
            public IdentifiedDataSerializable createNew(Integer arg) {
                return new SampleIdentifiedDataSerializable();
            }
        };
        ArrayDataSerializableFactory factory = new ArrayDataSerializableFactory(constructorFunctions);
        Assert.assertNull(factory.create((-1)));
        Assert.assertNull(factory.create(1));
        MatcherAssert.assertThat(factory.create(0), Matchers.instanceOf(SampleIdentifiedDataSerializable.class));
    }

    @Test
    public void testCreateWithoutVersion() throws Exception {
        ConstructorFunction<Integer, IdentifiedDataSerializable>[] constructorFunctions = new ConstructorFunction[1];
        VersionAwareConstructorFunction function = Mockito.mock(VersionAwareConstructorFunction.class);
        constructorFunctions[0] = function;
        ArrayDataSerializableFactory factory = new ArrayDataSerializableFactory(constructorFunctions);
        factory.create(0);
        Mockito.verify(function, Mockito.times(1)).createNew(0);
        Mockito.verify(function, Mockito.times(0)).createNew(eq(0), any(Version.class));
    }

    @Test
    public void testCreateWithVersion() throws Exception {
        ConstructorFunction<Integer, IdentifiedDataSerializable>[] constructorFunctions = new ConstructorFunction[1];
        VersionAwareConstructorFunction function = Mockito.mock(VersionAwareConstructorFunction.class);
        constructorFunctions[0] = function;
        ArrayDataSerializableFactory factory = new ArrayDataSerializableFactory(constructorFunctions);
        Version version = MemberVersion.of(3, 6, 0).asVersion();
        factory.create(0, version);
        Mockito.verify(function, Mockito.times(0)).createNew(0);
        Mockito.verify(function, Mockito.times(1)).createNew(0, version);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWithEmptyConstructorFunctions() {
        new ArrayDataSerializableFactory(new ConstructorFunction[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWithNullConstructorFunctions() {
        new ArrayDataSerializableFactory(null);
    }
}

