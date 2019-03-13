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
package com.hazelcast.internal.cluster.impl;


import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExtendedBindMessageTest {
    private ExtendedBindMessage bindMessage;

    private SerializationService serializationService;

    private Address targetAddress;

    @Test
    public void testSerialization_withMultipleLocalAddresses() throws Exception {
        bindMessage = new ExtendedBindMessage(((byte) (1)), localAddresses(), targetAddress, true);
        Data serialized = serializationService.toData(bindMessage);
        ExtendedBindMessage deserialized = serializationService.toObject(serialized);
        Assert.assertEquals(1, deserialized.getSchemaVersion());
        Assert.assertEquals(localAddresses(), deserialized.getLocalAddresses());
        Assert.assertEquals(targetAddress, deserialized.getTargetAddress());
        Assert.assertTrue(deserialized.isReply());
    }

    @Test
    public void testSerialization_whenBindMessageEmpty() {
        bindMessage = new ExtendedBindMessage();
        Data serialized = serializationService.toData(bindMessage);
        ExtendedBindMessage deserialized = serializationService.toObject(serialized);
        Assert.assertEquals(0, deserialized.getSchemaVersion());
        Assert.assertTrue(deserialized.getLocalAddresses().isEmpty());
        Assert.assertNull(null, deserialized.getTargetAddress());
        Assert.assertFalse(deserialized.isReply());
    }
}

