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
package com.hazelcast.quorum.map;


import QuorumType.READ;
import QuorumType.READ_WRITE;
import QuorumType.WRITE;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapQuorumLiteMemberTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    @Test(expected = QuorumException.class)
    public void test_readQuorumNotSatisfied_withLiteMembers() {
        Config config = createConfig("r", READ, 3, false);
        Config liteConfig = createConfig("r", READ, 3, true);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(liteConfig);
        instance.getMap("r").keySet();
    }

    @Test
    public void test_readQuorumSatisfied_withLiteMembers() {
        Config config = createConfig("r", READ, 2, false);
        Config liteConfig = createConfig("r", READ, 2, true);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(liteConfig);
        instance.getMap("r").keySet();
    }

    @Test(expected = QuorumException.class)
    public void test_readReadWriteQuorumNotSatisfied_withLiteMembers() {
        Config config = createConfig("rw", READ_WRITE, 3, false);
        Config liteConfig = createConfig("rw", READ_WRITE, 3, true);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(liteConfig);
        instance.getMap("rw").keySet();
    }

    @Test
    public void test_readReadWriteQuorumSatisfied_withLiteMembers() {
        Config config = createConfig("rw", READ_WRITE, 2, false);
        Config liteConfig = createConfig("rw", READ_WRITE, 2, true);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(liteConfig);
        instance.getMap("rw").keySet();
    }

    @Test(expected = QuorumException.class)
    public void test_readWriteQuorumNotSatisfied_withLiteMembers() {
        Config config = createConfig("w", WRITE, 3, false);
        Config liteConfig = createConfig("w", WRITE, 3, true);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(liteConfig);
        instance.getMap("w").put(0, 0);
    }

    @Test
    public void test_readWriteQuorumSatisfied_withLiteMembers() {
        Config config = createConfig("w", WRITE, 2, false);
        Config liteConfig = createConfig("w", WRITE, 2, true);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(liteConfig);
        instance.getMap("w").put(0, 0);
    }
}

