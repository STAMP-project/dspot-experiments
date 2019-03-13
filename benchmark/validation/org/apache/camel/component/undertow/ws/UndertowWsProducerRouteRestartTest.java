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
package org.apache.camel.component.undertow.ws;


import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.undertow.BaseUndertowTest;
import org.junit.Test;


public class UndertowWsProducerRouteRestartTest extends BaseUndertowTest {
    private static final String ROUTE_ID = UndertowWsProducerRouteRestartTest.class.getSimpleName();

    @Produce(uri = "direct:shop")
    private ProducerTemplate producer;

    @Test
    public void testWSSuspendResumeRoute() throws Exception {
        context.getRouteController().resumeRoute(UndertowWsProducerRouteRestartTest.ROUTE_ID);
        context.getRouteController().resumeRoute(UndertowWsProducerRouteRestartTest.ROUTE_ID);
        doTestWSHttpCall();
    }

    @Test
    public void testWSStopStartRoute() throws Exception {
        context.getRouteController().stopRoute(UndertowWsProducerRouteRestartTest.ROUTE_ID);
        context.getRouteController().startRoute(UndertowWsProducerRouteRestartTest.ROUTE_ID);
        doTestWSHttpCall();
    }

    @Test
    public void testWSRemoveAddRoute() throws Exception {
        context.removeRoute(UndertowWsProducerRouteRestartTest.ROUTE_ID);
        context.addRoutes(createRouteBuilder());
        context.getRouteController().startRoute(UndertowWsProducerRouteRestartTest.ROUTE_ID);
        doTestWSHttpCall();
    }
}

