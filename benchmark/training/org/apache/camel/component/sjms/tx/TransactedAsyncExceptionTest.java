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
package org.apache.camel.component.sjms.tx;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class TransactedAsyncExceptionTest extends CamelTestSupport {
    private static final String BROKER_URI = "vm://tqc_test_broker?broker.persistent=false&broker.useJmx=false";

    private static final int TRANSACTION_REDELIVERY_COUNT = 10;

    @Test
    public void testRouteWithThread() throws Exception {
        String destination = "sjms:queue:async.exception";
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                AtomicInteger counter = new AtomicInteger();
                from((destination + "?acknowledgementMode=SESSION_TRANSACTED&transacted=true")).threads().process(( exchange) -> {
                    if ((counter.incrementAndGet()) < (TransactedAsyncExceptionTest.TRANSACTION_REDELIVERY_COUNT)) {
                        throw new IllegalArgumentException();
                    }
                }).to("mock:async.exception");
            }
        });
        template.sendBody(destination, "begin");
        MockEndpoint mockEndpoint = context.getEndpoint("mock:async.exception", MockEndpoint.class);
        mockEndpoint.expectedMessageCount(1);
        if (!(mockEndpoint.await(getShutdownTimeout(), TimeUnit.SECONDS))) {
            dumpThreads();
        }
        assertMockEndpointsSatisfied(getShutdownTimeout(), TimeUnit.SECONDS);
    }
}

