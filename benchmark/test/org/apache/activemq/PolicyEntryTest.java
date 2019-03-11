/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq;


import org.junit.Assert;
import org.junit.Test;


public class PolicyEntryTest extends RuntimeConfigTestSupport {
    String configurationSeed = "policyEntryTest";

    @Test
    public void testMod() throws Exception {
        final String brokerConfig = (configurationSeed) + "-policy-ml-broker";
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml"));
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        verifyQueueLimit("Before", 1024);
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml-mod"), RuntimeConfigTestSupport.SLEEP);
        verifyQueueLimit("After", 4194304);
        // change to existing dest
        verifyQueueLimit("Before", 4194304);
    }

    @Test
    public void testAddNdMod() throws Exception {
        final String brokerConfig = (configurationSeed) + "-policy-ml-broker";
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml"));
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        verifyQueueLimit("Before", 1024);
        verifyTopicLimit("Before", brokerService.getSystemUsage().getMemoryUsage().getLimit());
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml-add"), RuntimeConfigTestSupport.SLEEP);
        verifyTopicLimit("After", 2048L);
        verifyQueueLimit("After", 2048);
        // change to existing dest
        verifyTopicLimit("Before", 2048L);
    }

    @Test
    public void testModParentPolicy() throws Exception {
        final String brokerConfig = (configurationSeed) + "-policy-ml-broker";
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml-parent"));
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        verifyQueueLimit("queue.test", 1024);
        verifyQueueLimit("queue.child.test", 2048);
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml-parent-mod"), RuntimeConfigTestSupport.SLEEP);
        verifyQueueLimit("queue.test2", 4194304);
        // change to existing dest
        verifyQueueLimit("queue.test", 4194304);
        // verify no change
        verifyQueueLimit("queue.child.test", 2048);
    }

    @Test
    public void testModChildPolicy() throws Exception {
        final String brokerConfig = (configurationSeed) + "-policy-ml-broker";
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml-parent"));
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        verifyQueueLimit("queue.test", 1024);
        verifyQueueLimit("queue.child.test", 2048);
        applyNewConfig(brokerConfig, ((configurationSeed) + "-policy-ml-child-mod"), RuntimeConfigTestSupport.SLEEP);
        // verify no change
        verifyQueueLimit("queue.test", 1024);
        // change to existing dest
        verifyQueueLimit("queue.child.test", 4194304);
        // new dest change
        verifyQueueLimit("queue.child.test2", 4194304);
    }
}

