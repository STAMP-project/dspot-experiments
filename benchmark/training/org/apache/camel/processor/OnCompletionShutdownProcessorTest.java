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


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.service.ServiceSupport;
import org.junit.Assert;
import org.junit.Test;


public class OnCompletionShutdownProcessorTest extends ContextTestSupport {
    private OnCompletionShutdownProcessorTest.MyProcessor processor = new OnCompletionShutdownProcessorTest.MyProcessor();

    @Test
    public void testSynchronizeComplete() throws Exception {
        Assert.assertEquals("Started", getStatus().name());
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        context.stop();
        Assert.assertEquals("Stopped", getStatus().name());
    }

    public static class MyProcessor extends ServiceSupport implements Processor {
        public MyProcessor() {
        }

        public void process(Exchange exchange) throws Exception {
            exchange.getIn().setBody("Bye World");
        }

        protected void doStart() throws Exception {
            // noop
        }

        protected void doStop() throws Exception {
            // noop
        }
    }
}

