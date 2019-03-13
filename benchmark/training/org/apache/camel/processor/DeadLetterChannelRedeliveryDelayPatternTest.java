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
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to verify delay pattern
 */
public class DeadLetterChannelRedeliveryDelayPatternTest extends ContextTestSupport {
    private static int counter;

    @Test
    public void testDelayPatternTest() throws Exception {
        DeadLetterChannelRedeliveryDelayPatternTest.counter = 0;
        MockEndpoint mock = getMockEndpoint("mock:error");
        mock.expectedMessageCount(1);
        long start = System.currentTimeMillis();
        template.sendBody("direct:start", "Hello World");
        long delta = (System.currentTimeMillis()) - start;
        Assert.assertTrue("Should be slower", (delta > 1000));
        assertMockEndpointsSatisfied();
        Assert.assertEquals(3, DeadLetterChannelRedeliveryDelayPatternTest.counter);
    }
}

