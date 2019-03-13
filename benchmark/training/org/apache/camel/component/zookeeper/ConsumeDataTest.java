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
package org.apache.camel.component.zookeeper;


import EventType.NodeDeleted;
import ZooKeeperMessage.ZOOKEEPER_EVENT_TYPE;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.junit.Test;


public class ConsumeDataTest extends ZooKeeperTestSupport {
    @Test
    public void shouldAwaitCreationAndGetDataNotification() throws Exception {
        EventType[] expectedEventTypes = new EventType[]{ EventType.NodeCreated, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDataChanged, EventType.NodeDeleted };
        MockEndpoint mock = getMockEndpoint("mock:zookeeper-data");
        mock.expectedMessageCount(expectedEventTypes.length);
        createCamelNode();
        updateNode(10);
        ZooKeeperTestSupport.delay(500);
        ZooKeeperTestSupport.client.delete("/camel");
        MockEndpoint.assertIsSatisfied(30, TimeUnit.SECONDS);
        int lastVersion = -1;
        for (int i = 0; i < (mock.getExchanges().size()); i++) {
            assertEquals(expectedEventTypes[i], mock.getExchanges().get(i).getIn().getHeader(ZOOKEEPER_EVENT_TYPE));
            if (!(NodeDeleted.equals(expectedEventTypes[i]))) {
                // As a delete event does not carry statistics, ignore it in the version check.
                int version = ZooKeeperMessage.getStatistics(mock.getExchanges().get(i).getIn()).getVersion();
                assertTrue("Version did not increase", (lastVersion < version));
                lastVersion = version;
            }
        }
    }

    @Test
    public void deletionOfAwaitedNodeCausesNoFailure() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:zookeeper-data");
        mock.expectedMinimumMessageCount(11);
        createCamelNode();
        ZooKeeperTestSupport.delay(500);
        // by now we are back waiting for a change so delete the node
        ZooKeeperTestSupport.client.delete("/camel");
        // recreate and update a number of times.
        createCamelNode();
        updateNode(10);
        MockEndpoint.assertIsSatisfied(30, TimeUnit.SECONDS);
        ZooKeeperTestSupport.client.delete("/camel");
    }
}

