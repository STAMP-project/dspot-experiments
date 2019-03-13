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
package org.apache.camel.impl;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Produce;
import org.apache.camel.spi.CamelBeanPostProcessor;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class CamelProduceInterfaceEventNotifierTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    private CamelBeanPostProcessor postProcessor;

    @Test
    public void testPostProcessor() throws Exception {
        CamelProduceInterfaceEventNotifierTest.events.clear();
        int before = CamelProduceInterfaceEventNotifierTest.events.size();
        Assert.assertEquals(0, before);
        CamelProduceInterfaceEventNotifierTest.MySender sender = new CamelProduceInterfaceEventNotifierTest.MySender();
        postProcessor.postProcessBeforeInitialization(sender, "foo");
        postProcessor.postProcessAfterInitialization(sender, "foo");
        getMockEndpoint("mock:result").expectedMessageCount(1);
        sender.hello.sayHello("Hello World");
        assertMockEndpointsSatisfied();
        int after = CamelProduceInterfaceEventNotifierTest.events.size();
        // should be 2 events
        Assert.assertEquals(2, after);
        Assert.assertTrue(((CamelProduceInterfaceEventNotifierTest.events.get(0)) instanceof CamelEvent.ExchangeSendingEvent));
        Assert.assertTrue(((CamelProduceInterfaceEventNotifierTest.events.get(1)) instanceof CamelEvent.ExchangeSentEvent));
    }

    interface FooService {
        void sayHello(String hello);
    }

    class MySender {
        @Produce("mock:result")
        CamelProduceInterfaceEventNotifierTest.FooService hello;
    }
}

