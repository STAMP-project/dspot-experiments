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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ContainsValueOperationTest extends HazelcastTestSupport {
    private final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);

    private HazelcastInstance member1;

    @Test
    public void test_ContainValueOperation() throws InterruptedException, ExecutionException {
        int value = 1;
        String mapName = HazelcastTestSupport.randomMapName();
        IMap<String, Integer> map = member1.getMap(mapName);
        String key = HazelcastTestSupport.generateKeyNotOwnedBy(member1);
        map.put(key, value);
        Future future = executeOperation(map, key, value);
        Assert.assertTrue(((Boolean) (future.get())));
    }
}

