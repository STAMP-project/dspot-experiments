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
package org.apache.camel.component.jclouds;


import JcloudsConstants.LIST_HARDWARE;
import JcloudsConstants.LIST_IMAGES;
import JcloudsConstants.LIST_NODES;
import JcloudsConstants.OPERATION;
import java.util.List;
import java.util.Set;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.junit.Test;


public class JcloudsSpringComputeTest extends CamelSpringTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint result;

    @EndpointInject(uri = "mock:resultlist")
    protected MockEndpoint resultlist;

    @Test
    public void testListImages() throws InterruptedException {
        result.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", null, OPERATION, LIST_IMAGES);
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> images = exchange.getIn().getBody(Set.class);
                assertTrue(((images.size()) > 0));
                for (Object obj : images) {
                    assertTrue((obj instanceof Image));
                }
            }
        }
    }

    @Test
    public void testListHardware() throws InterruptedException {
        result.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", null, OPERATION, LIST_HARDWARE);
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> hardwares = exchange.getIn().getBody(Set.class);
                assertTrue(((hardwares.size()) > 0));
                for (Object obj : hardwares) {
                    assertTrue((obj instanceof Hardware));
                }
            }
        }
    }

    @Test
    public void testListNodes() throws InterruptedException {
        result.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", null, OPERATION, LIST_NODES);
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> nodeMetadatas = exchange.getIn().getBody(Set.class);
                assertEquals("Nodes should be 0", 0, nodeMetadatas.size());
            }
        }
    }

    @Test
    public void testCreateAndListNodes() throws InterruptedException {
        result.expectedMessageCount(2);
        template.sendBodyAndHeaders("direct:start", null, createHeaders("1", "default"));
        template.sendBodyAndHeader("direct:start", null, OPERATION, LIST_NODES);
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> nodeMetadatas = exchange.getIn().getBody(Set.class);
                assertEquals("Nodes should be 1", 1, nodeMetadatas.size());
            }
        }
    }

    @Test
    public void testCreateAndListWithPredicates() throws InterruptedException {
        result.expectedMessageCount(6);
        // Create a node for the default group
        template.sendBodyAndHeaders("direct:start", null, createHeaders("1", "default"));
        // Create a node for the group 'other'
        template.sendBodyAndHeaders("direct:start", null, createHeaders("1", "other"));
        template.sendBodyAndHeaders("direct:start", null, createHeaders("2", "other"));
        template.sendBodyAndHeaders("direct:start", null, listNodeHeaders(null, "other", null));
        template.sendBodyAndHeaders("direct:start", null, listNodeHeaders("3", "other", null));
        template.sendBodyAndHeaders("direct:start", null, listNodeHeaders("3", "other", "RUNNING"));
        result.assertIsSatisfied();
    }

    @Test
    public void testCreateAndDestroyNode() throws InterruptedException {
        result.expectedMessageCount(1);
        template.sendBodyAndHeaders("direct:start", null, createHeaders("1", "default"));
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> nodeMetadatas = exchange.getIn().getBody(Set.class);
                assertEquals("There should be no node running", 1, nodeMetadatas.size());
                for (Object obj : nodeMetadatas) {
                    NodeMetadata nodeMetadata = ((NodeMetadata) (obj));
                    template.sendBodyAndHeaders("direct:start", null, destroyHeaders(nodeMetadata.getId(), null));
                }
            }
        }
    }

    @Test
    public void testCreateAndRebootNode() throws InterruptedException {
        result.expectedMessageCount(1);
        template.sendBodyAndHeaders("direct:start", null, createHeaders("1", "default"));
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> nodeMetadatas = exchange.getIn().getBody(Set.class);
                assertEquals("There should be one node running", 1, nodeMetadatas.size());
                for (Object obj : nodeMetadatas) {
                    NodeMetadata nodeMetadata = ((NodeMetadata) (obj));
                    template.sendBodyAndHeaders("direct:start", null, rebootHeaders(nodeMetadata.getId(), null));
                }
            }
        }
    }

    @Test
    public void testCreateAndSuspendNode() throws InterruptedException {
        result.expectedMessageCount(1);
        template.sendBodyAndHeaders("direct:start", null, createHeaders("1", "default"));
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> nodeMetadatas = exchange.getIn().getBody(Set.class);
                assertEquals("There should be one node running", 1, nodeMetadatas.size());
                for (Object obj : nodeMetadatas) {
                    NodeMetadata nodeMetadata = ((NodeMetadata) (obj));
                    template.sendBodyAndHeaders("direct:start", null, suspendHeaders(nodeMetadata.getId(), null));
                }
            }
        }
    }

    @Test
    public void testCreateSuspendAndResumeNode() throws InterruptedException {
        result.expectedMessageCount(1);
        template.sendBodyAndHeaders("direct:start", null, createHeaders("1", "default"));
        result.assertIsSatisfied();
        List<Exchange> exchanges = result.getExchanges();
        if ((exchanges != null) && (!(exchanges.isEmpty()))) {
            for (Exchange exchange : exchanges) {
                Set<?> nodeMetadatas = exchange.getIn().getBody(Set.class);
                assertEquals("There should be one node running", 1, nodeMetadatas.size());
                for (Object obj : nodeMetadatas) {
                    NodeMetadata nodeMetadata = ((NodeMetadata) (obj));
                    template.sendBodyAndHeaders("direct:start", null, resumeHeaders(nodeMetadata.getId(), null));
                }
            }
        }
    }
}

