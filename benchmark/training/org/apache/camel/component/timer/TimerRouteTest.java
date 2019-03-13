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
package org.apache.camel.component.timer;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TimerRouteTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(TimerRouteTest.class);

    private TimerRouteTest.MyBean bean = new TimerRouteTest.MyBean();

    @Test
    public void testTimerInvokesBeanMethod() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(2);
        assertMockEndpointsSatisfied();
        Assert.assertTrue(("Should have fired 2 or more times was: " + (bean.counter.get())), ((bean.counter.get()) >= 2));
    }

    public static class MyBean {
        public AtomicInteger counter = new AtomicInteger(0);

        public void someMethod() {
            TimerRouteTest.LOG.debug("Invoked someMethod()");
            counter.incrementAndGet();
        }
    }
}

