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
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class InheritErrorHandlerFalseTest extends ContextTestSupport {
    private static int counter;

    @Test
    public void testInheritErrorHandlerFalse() throws Exception {
        InheritErrorHandlerFalseTest.counter = 0;
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "Hello World");
            Assert.fail("Should throw exception");
        } catch (Exception e) {
            IllegalArgumentException iae = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Damn", iae.getMessage());
        }
        assertMockEndpointsSatisfied();
        Assert.assertEquals(1, InheritErrorHandlerFalseTest.counter);
    }

    public static class MyProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            (InheritErrorHandlerFalseTest.counter)++;
            exchange.setException(new IllegalArgumentException("Damn"));
        }
    }
}

