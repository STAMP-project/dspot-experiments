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
package org.apache.activemq.java;


import java.util.concurrent.TimeUnit;
import org.apache.activemq.RuntimeConfigTestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.plugin.java.JavaRuntimeConfigurationBroker;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavaDestinationsTest extends RuntimeConfigTestSupport {
    public static final Logger LOG = LoggerFactory.getLogger(JavaDestinationsTest.class);

    private JavaRuntimeConfigurationBroker javaConfigBroker;

    @Test
    public void testMod() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setDestinations(new ActiveMQDestination[]{ new ActiveMQQueue("ORIGINAL") });
        startBroker(brokerService);
        Assert.assertTrue("broker alive", brokerService.isStarted());
        printDestinations();
        Assert.assertTrue("contains original", containsDestination(new ActiveMQQueue("ORIGINAL")));
        JavaDestinationsTest.LOG.info("Adding destinations");
        // apply new config
        javaConfigBroker.setDestinations(new ActiveMQDestination[]{ new ActiveMQTopic("BEFORE"), new ActiveMQQueue("ORIGINAL"), new ActiveMQQueue("AFTER") });
        printDestinations();
        Assert.assertTrue("contains destinations", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return ((containsDestination(new ActiveMQQueue("ORIGINAL"))) && (containsDestination(new ActiveMQTopic("BEFORE")))) && (containsDestination(new ActiveMQQueue("AFTER")));
            }
        }, TimeUnit.MILLISECONDS.convert(RuntimeConfigTestSupport.SLEEP, TimeUnit.SECONDS)));
        JavaDestinationsTest.LOG.info("Removing destinations");
        // apply new config
        javaConfigBroker.setDestinations(new ActiveMQDestination[]{ new ActiveMQTopic("BEFORE"), new ActiveMQQueue("AFTER") });
        printDestinations();
        Assert.assertTrue("contains destinations", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return ((containsDestination(new ActiveMQQueue("ORIGINAL"))) && (containsDestination(new ActiveMQTopic("BEFORE")))) && (containsDestination(new ActiveMQQueue("AFTER")));
            }
        }, TimeUnit.MILLISECONDS.convert(RuntimeConfigTestSupport.SLEEP, TimeUnit.SECONDS)));
    }
}

