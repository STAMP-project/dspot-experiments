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
package com.hazelcast.map.impl.operation;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.nio.ByteOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RemoveBaseOperationWanFlagSerializationTest {
    static final String MAP_NAME = "map";

    @Parameterized.Parameter
    public Version version;

    @Parameterized.Parameter(1)
    public ByteOrder byteOrder;

    @Parameterized.Parameter(2)
    public boolean disableWanReplication;

    @Mock
    Data keyMock;

    @Mock
    private InternalSerializationService serializationServiceMock;

    @Test
    public void testRemoveOperation() throws IOException {
        BaseRemoveOperation original = new RemoveOperation(RemoveBaseOperationWanFlagSerializationTest.MAP_NAME, keyMock, disableWanReplication);
        BaseRemoveOperation deserialized = new RemoveOperation();
        testSerialization(original, deserialized);
    }

    @Test
    public void testDeleteOperation() throws IOException {
        BaseRemoveOperation original = new DeleteOperation(RemoveBaseOperationWanFlagSerializationTest.MAP_NAME, keyMock, disableWanReplication);
        BaseRemoveOperation deserialized = new DeleteOperation();
        testSerialization(original, deserialized);
    }
}

