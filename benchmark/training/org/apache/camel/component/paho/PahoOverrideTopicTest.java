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
package org.apache.camel.component.paho;


import PahoConstants.CAMEL_PAHO_OVERRIDE_TOPIC;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Test;


public class PahoOverrideTopicTest extends CamelTestSupport {
    MqttConnectOptions connectOptions = new MqttConnectOptions();

    BrokerService broker;

    int mqttPort = AvailablePortFinder.getNextAvailable();

    // Tests
    @Test
    public void shouldOverride() throws InterruptedException {
        // Given
        getMockEndpoint("mock:test").expectedMessageCount(1);
        // When
        template.sendBodyAndHeader("direct:test", "Hello World", CAMEL_PAHO_OVERRIDE_TOPIC, "myoverride");
        // Then
        assertMockEndpointsSatisfied();
    }
}

