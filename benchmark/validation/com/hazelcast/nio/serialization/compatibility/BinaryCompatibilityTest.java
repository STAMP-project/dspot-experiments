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
package com.hazelcast.nio.serialization.compatibility;


import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.QuickTest;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
public class BinaryCompatibilityTest {
    private static final int NULL_OBJECT = -1;

    private static Map<String, Data> dataMap = new HashMap<String, Data>();

    // OPTIONS
    private static Object[] objects = ReferenceObjects.allTestObjects;

    private static boolean[] unsafeAllowedOpts = new boolean[]{ true, false };

    private static ByteOrder[] byteOrders = new ByteOrder[]{ ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };

    private static byte[] versions = new byte[]{ 1 };

    @Parameterized.Parameter
    public boolean allowUnsafe;

    @Parameterized.Parameter(1)
    public Object object;

    @Parameterized.Parameter(2)
    public ByteOrder byteOrder;

    @Parameterized.Parameter(3)
    public byte version;

    @Test
    public void readAndVerifyBinaries() {
        String key = createObjectKey();
        SerializationService serializationService = createSerializationService();
        Object readObject = serializationService.toObject(BinaryCompatibilityTest.dataMap.get(key));
        boolean equals = BinaryCompatibilityTest.equals(object, readObject);
        if (!equals) {
            System.out.println((((((object.getClass().getSimpleName()) + ": ") + (object)) + " != ") + readObject));
        }
        TestCase.assertTrue(equals);
    }

    @Test
    public void basicSerializeDeserialize() {
        SerializationService serializationService = createSerializationService();
        Data data = serializationService.toData(object);
        Object readObject = serializationService.toObject(data);
        TestCase.assertTrue(BinaryCompatibilityTest.equals(object, readObject));
    }
}

