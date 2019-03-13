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
package org.apache.camel.component.zookeepermaster;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.service.ServiceHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MasterEndpointFailoverTest {
    private static final transient Logger LOG = LoggerFactory.getLogger(MasterEndpointFailoverTest.class);

    protected ProducerTemplate template;

    protected CamelContext producerContext;

    protected CamelContext consumerContext1;

    protected CamelContext consumerContext2;

    protected MockEndpoint result1Endpoint;

    protected MockEndpoint result2Endpoint;

    protected AtomicInteger messageCounter = new AtomicInteger(1);

    protected ZKServerFactoryBean serverFactoryBean = new ZKServerFactoryBean();

    protected CuratorFactoryBean zkClientBean = new CuratorFactoryBean();

    @Test
    public void testEndpoint() throws Exception {
        System.out.println("Starting consumerContext1");
        ServiceHelper.startService(consumerContext1);
        assertMessageReceived(result1Endpoint, result2Endpoint);
        System.out.println("Starting consumerContext2");
        ServiceHelper.startService(consumerContext2);
        assertMessageReceivedLoop(result1Endpoint, result2Endpoint, 3);
        System.out.println("Stopping consumerContext1");
        ServiceHelper.stopService(consumerContext1);
        assertMessageReceivedLoop(result2Endpoint, result1Endpoint, 3);
    }
}

