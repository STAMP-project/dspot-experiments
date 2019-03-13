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


import Exchange.HTTP_METHOD;
import Exchange.HTTP_QUERY;
import Exchange.HTTP_URI;
import org.apache.camel.Exchange;
import org.apache.camel.http.common.HttpMethods.GET;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


/**
 * Unit test to verify the algorithm for selecting either GET or POST.
 */
public class HttpProducerSelectMethodTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void noDataDefaultIsGet() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody(null);
        producer.start();
        producer.process(exchange);
        producer.stop();
        assertExchange(exchange);
    }

    @Test
    public void dataDefaultIsPost() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/mypost"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("This is some data to post");
        producer.start();
        producer.process(exchange);
        producer.stop();
        assertExchange(exchange);
    }

    @Test
    public void withMethodPostInHeader() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/mypost"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("");
        exchange.getIn().setHeader(HTTP_METHOD, HttpMethods.POST);
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    @Test
    public void withMethodGetInHeader() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("");
        exchange.getIn().setHeader(HTTP_METHOD, HttpMethods.GET);
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    @Test
    public void withMethodCommonHttpGetInHeader() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("");
        exchange.getIn().setHeader(HTTP_METHOD, GET);
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    @Test
    public void withEndpointQuery() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget2?q=Camel"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("");
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    @Test
    public void withQueryInHeader() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget2"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("");
        exchange.getIn().setHeader(HTTP_QUERY, "q=Camel");
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    @Test
    public void withHttpURIInHeader() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget2"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("");
        exchange.getIn().setHeader(HTTP_URI, (((("http://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget2?q=Camel"));
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    @Test
    public void withQueryInHeaderOverrideEndpoint() throws Exception {
        HttpComponent component = context.getComponent("http4", HttpComponent.class);
        HttpEndpoint endpoiont = ((HttpEndpoint) (component.createEndpoint((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myget2?q=Donkey"))));
        HttpProducer producer = new HttpProducer(endpoiont);
        Exchange exchange = producer.createExchange();
        exchange.getIn().setBody("");
        exchange.getIn().setHeader(HTTP_QUERY, "q=Camel");
        producer.start();
        producer.process(exchange);
        producer.stop();
    }
}

