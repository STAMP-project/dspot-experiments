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


import Exchange.TIMER_COUNTER;
import Exchange.TIMER_FIRED_TIME;
import Exchange.TIMER_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for fired time exchange property
 */
public class TimerFiredTimeTest extends ContextTestSupport {
    @Test
    public void testFired() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getExchanges().get(0);
        Assert.assertEquals("hello", exchange.getProperty(TIMER_NAME));
        Assert.assertNotNull(exchange.getProperty(TIMER_FIRED_TIME));
        Assert.assertNotNull(exchange.getIn().getHeader("firedTime"));
        Assert.assertEquals(Long.valueOf(1), exchange.getProperty(TIMER_COUNTER));
    }
}

