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


import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wire tap unit test
 */
public class WireTapShutdownBeanTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(WireTapShutdownBeanTest.class);

    private static final Exchanger<Void> EXCHANGER = new Exchanger<>();

    @Test
    public void testWireTapShutdown() throws Exception {
        final WireTapShutdownBeanTest.MyTapBean tapBean = ((WireTapShutdownBeanTest.MyTapBean) (context.getRegistry().lookupByName("tap")));
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        WireTapShutdownBeanTest.EXCHANGER.exchange(null);
        // shutdown Camel which should let the inflight wire-tap message route to completion
        context.stop();
        // should allow to shutdown nicely
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertEquals("Hello World", tapBean.getTapped());
        });
    }

    public static class MyTapBean {
        private String tapped;

        public void tapSomething(String body) throws Exception {
            try {
                WireTapShutdownBeanTest.EXCHANGER.exchange(null);
                Thread.sleep(100);
            } catch (Exception e) {
                Assert.fail("Should not be interrupted");
            }
            WireTapShutdownBeanTest.LOG.info("Wire tapping: {}", body);
            tapped = body;
        }

        public String getTapped() {
            return tapped;
        }
    }
}

