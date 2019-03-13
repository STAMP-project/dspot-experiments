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
package org.apache.camel.component.seda;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.SynchronizationAdapter;
import org.junit.Assert;
import org.junit.Test;


public class SedaDiscardIfNoConsumerTest extends ContextTestSupport {
    @Test
    public void testDiscard() throws Exception {
        SedaEndpoint bar = getMandatoryEndpoint("seda:bar", SedaEndpoint.class);
        Assert.assertEquals(0, bar.getCurrentQueueSize());
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(0, bar.getCurrentQueueSize());
    }

    @Test
    public void testDiscardUoW() throws Exception {
        SedaEndpoint bar = getMandatoryEndpoint("seda:bar", SedaEndpoint.class);
        Assert.assertEquals(0, bar.getCurrentQueueSize());
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        final SedaDiscardIfNoConsumerTest.MyCompletion myCompletion = new SedaDiscardIfNoConsumerTest.MyCompletion();
        template.send("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello World");
                exchange.addOnCompletion(myCompletion);
            }
        });
        assertMockEndpointsSatisfied();
        Assert.assertEquals(0, bar.getCurrentQueueSize());
        Assert.assertEquals(true, myCompletion.isCalled());
    }

    private static final class MyCompletion extends SynchronizationAdapter {
        private boolean called;

        @Override
        public void onDone(Exchange exchange) {
            called = true;
        }

        public boolean isCalled() {
            return called;
        }
    }
}

