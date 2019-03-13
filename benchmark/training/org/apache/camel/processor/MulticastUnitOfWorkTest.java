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
import org.apache.camel.spi.Synchronization;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to verify unit of work with multicast.
 */
public class MulticastUnitOfWorkTest extends ContextTestSupport {
    private static String sync;

    private static String lastOne;

    @Test
    public void testMulticastUOW() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        // will run B and then A, where A will be the last one
        Assert.assertEquals("onCompleteA", MulticastUnitOfWorkTest.sync);
        Assert.assertEquals("onCompleteA", MulticastUnitOfWorkTest.lastOne);
    }

    private static final class MyUOWProcessor implements Processor {
        private String id;

        private MyUOWProcessor(String id) {
            this.id = id;
        }

        public void process(Exchange exchange) throws Exception {
            exchange.getUnitOfWork().addSynchronization(new Synchronization() {
                public void onComplete(Exchange exchange) {
                    MulticastUnitOfWorkTest.sync = "onComplete" + (id);
                    MulticastUnitOfWorkTest.lastOne = MulticastUnitOfWorkTest.sync;
                }

                public void onFailure(Exchange exchange) {
                    MulticastUnitOfWorkTest.sync = "onFailure" + (id);
                    MulticastUnitOfWorkTest.lastOne = MulticastUnitOfWorkTest.sync;
                }
            });
        }
    }
}

