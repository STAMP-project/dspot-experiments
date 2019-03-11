/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.protocols.raft;


import CommunicationStrategy.ANY;
import CommunicationStrategy.LEADER;
import MultiRaftProtocol.TYPE;
import Partitioner.MURMUR3;
import ReadConsistency.LINEARIZABLE;
import ReadConsistency.SEQUENTIAL;
import Recovery.CLOSE;
import Recovery.RECOVER;
import io.atomix.primitive.partition.Partitioner;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Multi-Raft protocol configuration.
 */
public class MultiRaftProtocolConfigTest {
    @Test
    public void testConfig() throws Exception {
        MultiRaftProtocolConfig config = new MultiRaftProtocolConfig();
        Assert.assertEquals(TYPE, config.getType());
        Assert.assertNull(config.getGroup());
        Assert.assertSame(MURMUR3, config.getPartitioner());
        Assert.assertEquals(Duration.ofMillis(250), config.getMinTimeout());
        Assert.assertEquals(Duration.ofSeconds(30), config.getMaxTimeout());
        Assert.assertEquals(SEQUENTIAL, config.getReadConsistency());
        Assert.assertEquals(LEADER, config.getCommunicationStrategy());
        Assert.assertEquals(RECOVER, config.getRecoveryStrategy());
        Assert.assertEquals(0, config.getMaxRetries());
        Assert.assertEquals(Duration.ofMillis(100), config.getRetryDelay());
        Partitioner<String> partitioner = ( k, p) -> null;
        config.setGroup("test");
        config.setPartitioner(partitioner);
        config.setMinTimeout(Duration.ofSeconds(1));
        config.setMaxTimeout(Duration.ofSeconds(10));
        config.setReadConsistency(LINEARIZABLE);
        config.setCommunicationStrategy(ANY);
        config.setRecoveryStrategy(CLOSE);
        config.setMaxRetries(5);
        config.setRetryDelay(Duration.ofSeconds(1));
        Assert.assertEquals("test", config.getGroup());
        Assert.assertSame(partitioner, config.getPartitioner());
        Assert.assertEquals(Duration.ofSeconds(1), config.getMinTimeout());
        Assert.assertEquals(Duration.ofSeconds(10), config.getMaxTimeout());
        Assert.assertEquals(LINEARIZABLE, config.getReadConsistency());
        Assert.assertEquals(ANY, config.getCommunicationStrategy());
        Assert.assertEquals(CLOSE, config.getRecoveryStrategy());
        Assert.assertEquals(5, config.getMaxRetries());
        Assert.assertEquals(Duration.ofSeconds(1), config.getRetryDelay());
    }
}

