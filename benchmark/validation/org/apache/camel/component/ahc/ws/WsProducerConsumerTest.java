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
package org.apache.camel.component.ahc.ws;


import java.util.List;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.jetty.server.Server;
import org.junit.Test;


/**
 *
 */
public class WsProducerConsumerTest extends CamelTestSupport {
    protected static final String TEST_MESSAGE = "Hello World!";

    protected static final int PORT = AvailablePortFinder.getNextAvailable();

    protected Server server;

    protected List<Object> messages;

    @Test
    public void testTwoRoutes() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(WsProducerConsumerTest.TEST_MESSAGE);
        template.sendBody("direct:input", WsProducerConsumerTest.TEST_MESSAGE);
        mock.assertIsSatisfied();
    }

    @Test
    public void testTwoRoutesRestartConsumer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(WsProducerConsumerTest.TEST_MESSAGE);
        template.sendBody("direct:input", WsProducerConsumerTest.TEST_MESSAGE);
        mock.assertIsSatisfied();
        resetMocks();
        log.info("Restarting bar route");
        context.getRouteController().stopRoute("bar");
        Thread.sleep(500);
        context.getRouteController().startRoute("bar");
        mock.expectedBodiesReceived(WsProducerConsumerTest.TEST_MESSAGE);
        template.sendBody("direct:input", WsProducerConsumerTest.TEST_MESSAGE);
        mock.assertIsSatisfied();
    }

    @Test
    public void testTwoRoutesRestartProducer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(WsProducerConsumerTest.TEST_MESSAGE);
        template.sendBody("direct:input", WsProducerConsumerTest.TEST_MESSAGE);
        mock.assertIsSatisfied();
        resetMocks();
        log.info("Restarting foo route");
        context.getRouteController().stopRoute("foo");
        Thread.sleep(500);
        context.getRouteController().startRoute("foo");
        mock.expectedBodiesReceived(WsProducerConsumerTest.TEST_MESSAGE);
        template.sendBody("direct:input", WsProducerConsumerTest.TEST_MESSAGE);
        mock.assertIsSatisfied();
    }
}

