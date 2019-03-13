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
package org.apache.camel.component.http4;


import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.protocol.HTTP;
import org.junit.Test;


/**
 * Unit test that show custom header filter useful to send Connection Close header
 */
public class HttpProducerExplicitConnectionCloseTest extends BaseHttpTest {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mockResultEndpoint;

    private HttpServer localServer;

    @Test
    public void noDataDefaultIsGet() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        component.setConnectionTimeToLive(1000L);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget?connectionClose=true"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody(null);
        producer.start();
        producer.process(exchange);
        producer.stop();
        assertEquals(HTTP.CONN_CLOSE, exchange.getOut().getHeader("connection"));
        assertExchange(exchange);
    }
}

