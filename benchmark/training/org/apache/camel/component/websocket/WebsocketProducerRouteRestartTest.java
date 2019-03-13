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
package org.apache.camel.component.websocket;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class WebsocketProducerRouteRestartTest extends CamelTestSupport {
    private static final String ROUTE_ID = WebsocketProducerRouteRestartTest.class.getSimpleName();

    private static List<Object> received = new ArrayList<>();

    private static CountDownLatch latch;

    protected int port;

    @Produce(uri = "direct:shop")
    private ProducerTemplate producer;

    @Test
    public void testWSSuspendResumeRoute() throws Exception {
        context.getRouteController().resumeRoute(WebsocketProducerRouteRestartTest.ROUTE_ID);
        context.getRouteController().resumeRoute(WebsocketProducerRouteRestartTest.ROUTE_ID);
        doTestWSHttpCall();
    }

    @Test
    public void testWSStopStartRoute() throws Exception {
        context.getRouteController().stopRoute(WebsocketProducerRouteRestartTest.ROUTE_ID);
        context.getRouteController().startRoute(WebsocketProducerRouteRestartTest.ROUTE_ID);
        doTestWSHttpCall();
    }

    @Test
    public void testWSRemoveAddRoute() throws Exception {
        context.removeRoute(WebsocketProducerRouteRestartTest.ROUTE_ID);
        context.addRoutes(createRouteBuilder());
        context.getRouteController().startRoute(WebsocketProducerRouteRestartTest.ROUTE_ID);
        doTestWSHttpCall();
    }
}

