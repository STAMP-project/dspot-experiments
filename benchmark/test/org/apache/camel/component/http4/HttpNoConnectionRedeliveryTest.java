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


import Exchange.REDELIVERED;
import Exchange.REDELIVERY_COUNTER;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


public class HttpNoConnectionRedeliveryTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void httpConnectionOk() throws Exception {
        Exchange exchange = template.request("direct:start", null);
        assertExchange(exchange);
    }

    @Test
    public void httpConnectionNotOk() throws Exception {
        // stop server so there are no connection
        // and wait for it to terminate
        localServer.stop();
        localServer.awaitTermination(5000, TimeUnit.MILLISECONDS);
        Exchange exchange = template.request("direct:start", null);
        assertTrue(exchange.isFailed());
        ConnectException cause = assertIsInstanceOf(ConnectException.class, exchange.getException());
        assertTrue(cause.getMessage().contains("failed"));
        assertEquals(true, exchange.getIn().getHeader(REDELIVERED));
        assertEquals(4, exchange.getIn().getHeader(REDELIVERY_COUNTER));
    }
}

