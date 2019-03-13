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
package com.hazelcast.replicatedmap;


import InMemoryFormat.BINARY;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicatedMapPutSerializationTest extends HazelcastTestSupport {
    static AtomicInteger deSerializationCount = new AtomicInteger(0);

    @Test
    public void testPutShouldNotDeserializeData() {
        String mapName = HazelcastTestSupport.randomName();
        Config config = new Config();
        config.getReplicatedMapConfig(mapName).setInMemoryFormat(BINARY);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory();
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        ReplicatedMap<String, ReplicatedMapPutSerializationTest.SerializationCountingData> map = instance1.getReplicatedMap(mapName);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        ReplicatedMapPutSerializationTest.SerializationCountingData value = new ReplicatedMapPutSerializationTest.SerializationCountingData();
        map.put(key, value);
        map.put(key, value);
        // only deserialized once in the proxy
        Assert.assertEquals(1, ReplicatedMapPutSerializationTest.deSerializationCount.get());
    }

    static class SerializationCountingData implements DataSerializable {
        public SerializationCountingData() {
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            ReplicatedMapPutSerializationTest.deSerializationCount.incrementAndGet();
        }
    }
}

