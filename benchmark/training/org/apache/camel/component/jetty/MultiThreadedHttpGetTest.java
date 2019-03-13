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
package org.apache.camel.component.jetty;


import java.io.InputStream;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;


public class MultiThreadedHttpGetTest extends BaseJettyTest {
    @Test
    public void testHttpGetWithConversion() throws Exception {
        // In this scenario response stream is converted to String
        // so the stream has to be read to the end. When this happens
        // the associated connection is released automatically.
        String endpointName = "seda:withConversion?concurrentConsumers=5";
        sendMessagesTo(endpointName, 5);
    }

    @Test
    public void testHttpGetWithoutConversion() throws Exception {
        // This is needed as by default there are 2 parallel
        // connections to some host and there is nothing that
        // closes the http connection here.
        // Need to set the httpConnectionManager
        PoolingHttpClientConnectionManager httpConnectionManager = new PoolingHttpClientConnectionManager();
        httpConnectionManager.setDefaultMaxPerRoute(5);
        context.getComponent("http", HttpComponent.class).setClientConnectionManager(httpConnectionManager);
        String endpointName = "seda:withoutConversion?concurrentConsumers=5";
        sendMessagesTo(endpointName, 5);
    }

    @Test
    public void testHttpGetWithExplicitStreamClose() throws Exception {
        // We close connections explicitely at the very end of the flow
        // (camel doesn't know when the stream is not needed any more)
        MockEndpoint mockEndpoint = resolveMandatoryEndpoint("mock:results", MockEndpoint.class);
        for (int i = 0; i < 5; i++) {
            mockEndpoint.expectedMessageCount(1);
            template.sendBody("seda:withoutConversion?concurrentConsumers=5", null);
            mockEndpoint.assertIsSatisfied();
            Object response = mockEndpoint.getReceivedExchanges().get(0).getIn().getBody();
            InputStream responseStream = assertIsInstanceOf(InputStream.class, response);
            responseStream.close();
            mockEndpoint.reset();
        }
    }
}

