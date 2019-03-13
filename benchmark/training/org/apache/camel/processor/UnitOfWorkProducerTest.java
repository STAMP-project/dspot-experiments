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
package org.apache.camel.processor;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class UnitOfWorkProducerTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    @Test
    public void testSedaBasedUnitOfWorkProducer() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        // sending to seda should cause 2 completed events
        template.sendBody("seda:foo", "Hello World");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // there should be 2 completed events
        // one for the producer template, and another for the Camel route
        Assert.assertEquals(2, UnitOfWorkProducerTest.events.size());
    }

    @Test
    public void testDirectBasedUnitOfWorkProducer() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        // sending to direct should cause 1 completed events
        template.sendBody("direct:bar", "Hello World");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // there should be 1 completed events as direct endpoint will be like a direct method call
        // and the UoW will be re-used
        Assert.assertEquals(1, UnitOfWorkProducerTest.events.size());
    }
}

