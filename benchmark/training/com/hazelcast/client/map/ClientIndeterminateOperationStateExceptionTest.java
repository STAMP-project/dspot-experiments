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
package com.hazelcast.client.map;


import SpiDataSerializerHook.BACKUP;
import SpiDataSerializerHook.F_ID;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IndeterminateOperationStateException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientIndeterminateOperationStateExceptionTest extends HazelcastTestSupport {
    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private HazelcastInstance instance1;

    private HazelcastInstance instance2;

    private HazelcastInstance client;

    @Test
    public void shouldFail_whenBackupAckMissed() throws InterruptedException, TimeoutException {
        PacketFiltersUtil.dropOperationsBetween(instance1, instance2, F_ID, Collections.singletonList(BACKUP));
        String key = generateKeyOwnedBy(instance1);
        IMap<Object, Object> map = client.getMap(randomMapName());
        try {
            map.put(key, key);
            Assert.fail();
        } catch (IndeterminateOperationStateException expected) {
        }
        Assert.assertEquals(key, map.get(key));
    }
}

