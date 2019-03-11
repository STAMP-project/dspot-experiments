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


import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DestinationsTest extends RuntimeConfigTestSupport {
    public static final Logger LOG = LoggerFactory.getLogger(DestinationsTest.class);

    @Test
    public void testMod() throws Exception {
        String configurationSeed = "destinationTest";
        final String brokerConfig = configurationSeed + "-destinations";
        applyNewConfig(brokerConfig, (configurationSeed + "-original"));
        startBroker(brokerConfig);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        printDestinations();
        Assert.assertTrue("contains original", containsDestination(new ActiveMQQueue("ORIGINAL")));
        DestinationsTest.LOG.info("Adding destinations");
        applyNewConfig(brokerConfig, (configurationSeed + "-add"), RuntimeConfigTestSupport.SLEEP);
        printDestinations();
        Assert.assertTrue("contains original", containsDestination(new ActiveMQQueue("ORIGINAL")));
        Assert.assertTrue("contains before", containsDestination(new ActiveMQTopic("BEFORE")));
        Assert.assertTrue("contains after", containsDestination(new ActiveMQQueue("AFTER")));
        DestinationsTest.LOG.info("Removing destinations");
        applyNewConfig(brokerConfig, (configurationSeed + "-remove"), RuntimeConfigTestSupport.SLEEP);
        printDestinations();
        Assert.assertTrue("contains original", containsDestination(new ActiveMQQueue("ORIGINAL")));
        Assert.assertTrue("contains before", containsDestination(new ActiveMQTopic("BEFORE")));
        Assert.assertTrue("contains after", containsDestination(new ActiveMQQueue("AFTER")));
    }
}

