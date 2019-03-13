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
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Synchronization;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to verify unit of work with seda. That the UnitOfWork is able to route using seda
 * but keeping the same UoW.
 */
public class SedaUnitOfWorkTest extends ContextTestSupport {
    private volatile Object foo;

    private volatile Object kaboom;

    private volatile String sync;

    private volatile String lastOne;

    @Test
    public void testSedaUOW() throws Exception {
        NotifyBuilder notify = whenDone(2).create();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Hello World", "foo", "bar");
        assertMockEndpointsSatisfied();
        notify.matchesMockWaitTime();
        Assert.assertEquals("onCompleteA", sync);
        Assert.assertEquals("onCompleteA", lastOne);
        Assert.assertEquals("Should have propagated the header inside the Synchronization.onComplete() callback", "bar", foo);
    }

    @Test
    public void testSedaUOWWithException() throws Exception {
        NotifyBuilder notify = whenDone(2).create();
        template.sendBodyAndHeader("direct:start", "Hello World", "kaboom", "yes");
        notify.matchesMockWaitTime();
        Assert.assertEquals("onFailureA", sync);
        Assert.assertEquals("onFailureA", lastOne);
        Assert.assertEquals("Should have propagated the header inside the Synchronization.onFailure() callback", "yes", kaboom);
    }

    private static final class MyUOWProcessor implements Processor {
        private SedaUnitOfWorkTest test;

        private String id;

        private MyUOWProcessor(SedaUnitOfWorkTest test, String id) {
            this.test = test;
            this.id = id;
        }

        public void process(Exchange exchange) throws Exception {
            exchange.getUnitOfWork().addSynchronization(new Synchronization() {
                public void onComplete(Exchange exchange) {
                    test.sync = "onComplete" + (id);
                    test.lastOne = test.sync;
                    test.foo = exchange.getIn().getHeader("foo");
                }

                public void onFailure(Exchange exchange) {
                    test.sync = "onFailure" + (id);
                    test.lastOne = test.sync;
                    test.kaboom = exchange.getIn().getHeader("kaboom");
                }
            });
        }
    }
}

