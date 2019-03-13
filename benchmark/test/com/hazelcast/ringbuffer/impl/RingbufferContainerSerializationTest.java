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
package com.hazelcast.ringbuffer.impl;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * This test verifies that the RingbufferContainer can serialize itself
 * correctly using different in memory formats and using enabling/disabling
 * TTL.
 * <p/>
 * This test also forces a delay between the serialization and deserialization. If a ringbuffer is configured
 * with a ttl, we don't want to send over the actual expiration time, because on a different member in the
 * cluster, there could be a big time difference which can lead to the ringbuffer immediately cleaning or cleaning
 * very very late.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferContainerSerializationTest extends HazelcastTestSupport {
    private static final int CLOCK_DIFFERENCE_MS = 2000;

    private InternalSerializationService serializationService;

    private NodeEngineImpl nodeEngine;

    @Test
    public void whenObjectInMemoryFormat_andTTLEnabled() {
        test(InMemoryFormat.OBJECT, 100);
    }

    @Test
    public void whenObjectInMemoryFormat_andTTLDisabled() {
        test(InMemoryFormat.OBJECT, 0);
    }

    @Test
    public void whenBinaryInMemoryFormat_andTTLEnabled() {
        test(InMemoryFormat.BINARY, 100);
    }

    @Test
    public void whenBinaryInMemoryFormat_andTTLDisabled() {
        test(InMemoryFormat.BINARY, 0);
    }
}

