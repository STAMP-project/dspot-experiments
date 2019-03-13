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


import ExchangePattern.InOut;
import ZooKeeperMessage.ZOOKEEPER_STATISTICS;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.zookeeper.operations.GetChildrenOperation;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;


public class ZooKeeperProducerTest extends ZooKeeperTestSupport {
    private String zookeeperUri;

    private String testPayload = "TestPayload";

    @Test
    public void testRoundtripOfDataToAndFromZnode() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:consumed-from-node");
        MockEndpoint pipeline = getMockEndpoint("mock:producer-out");
        mock.expectedMessageCount(1);
        pipeline.expectedMessageCount(1);
        Exchange e = createExchangeWithBody(testPayload);
        e.setPattern(InOut);
        template.send("direct:roundtrip", e);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAsyncRoundtripOfDataToAndFromZnode() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:consumed-from-node");
        mock.expectedMessageCount(1);
        Exchange e = createExchangeWithBody(testPayload);
        template.send("direct:roundtrip", e);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void setUsingCreateModeFromHeader() throws Exception {
        ZooKeeperTestSupport.client.createPersistent("/modes-test", "parent for modes");
        for (CreateMode mode : CreateMode.values()) {
            Exchange exchange = createExchangeWithBody(testPayload);
            exchange.getIn().setHeader(ZooKeeperMessage.ZOOKEEPER_CREATE_MODE, mode);
            exchange.getIn().setHeader(ZooKeeperMessage.ZOOKEEPER_NODE, ("/modes-test/" + mode));
            exchange.setPattern(InOut);
            template.send("direct:node-from-header", exchange);
        }
        GetChildrenOperation listing = new GetChildrenOperation(getConnection(), "/modes-test");
        assertEquals(CreateMode.values().length, listing.get().getResult().size());
    }

    @Test
    public void createWithOtherCreateMode() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:create-mode");
        mock.expectedMessageCount(1);
        Exchange e = createExchangeWithBody(testPayload);
        e.setPattern(InOut);
        template.send("direct:create-mode", e);
        assertMockEndpointsSatisfied();
        Stat s = mock.getReceivedExchanges().get(0).getIn().getHeader(ZOOKEEPER_STATISTICS, Stat.class);
        assertEquals(s.getEphemeralOwner(), 0);
    }

    @Test
    public void deleteNode() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:delete");
        mock.expectedMessageCount(1);
        ZooKeeperTestSupport.client.createPersistent("/to-be-deleted", "to be deleted");
        Exchange e = createExchangeWithBody(null);
        e.setPattern(InOut);
        e.getIn().setHeader(ZooKeeperMessage.ZOOKEEPER_OPERATION, "DELETE");
        template.send("direct:delete", e);
        assertMockEndpointsSatisfied();
        assertNull(ZooKeeperTestSupport.client.getConnection().exists("/to-be-deleted", false));
    }

    @Test
    public void setAndGetListing() throws Exception {
        ZooKeeperTestSupport.client.createPersistent("/set-listing", "parent for set and list test");
        Exchange exchange = createExchangeWithBody(testPayload);
        exchange.getIn().setHeader(ZooKeeperMessage.ZOOKEEPER_NODE, "/set-listing/firstborn");
        exchange.setPattern(InOut);
        template.send((("zookeeper://localhost:" + (ZooKeeperTestSupport.getServerPort())) + "/set-listing?create=true&listChildren=true"), exchange);
        List<?> children = exchange.getOut().getMandatoryBody(List.class);
        assertEquals(1, children.size());
        assertEquals("firstborn", children.get(0));
    }

    @Test
    public void testZookeeperMessage() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:consumed-from-node");
        mock.expectedMessageCount(1);
        Exchange exchange = createExchangeWithBody(testPayload);
        template.send("direct:roundtrip", exchange);
        assertMockEndpointsSatisfied();
        Message received = mock.getReceivedExchanges().get(0).getIn();
        assertEquals("/node", ZooKeeperMessage.getPath(received));
        assertNotNull(ZooKeeperMessage.getStatistics(received));
    }
}

