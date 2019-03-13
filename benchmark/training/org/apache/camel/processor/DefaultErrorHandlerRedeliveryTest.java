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
import org.apache.camel.RuntimeCamelException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test to verify that redelivery counters is working as expected.
 */
public class DefaultErrorHandlerRedeliveryTest extends ContextTestSupport {
    private static int counter;

    @Test
    public void testRedeliveryTest() throws Exception {
        DefaultErrorHandlerRedeliveryTest.counter = 0;
        try {
            template.sendBody("direct:start", "Hello World");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            // expected
        }
        Assert.assertEquals(3, DefaultErrorHandlerRedeliveryTest.counter);// One call + 2 re-deliveries

    }

    @Test
    public void testNoRedeliveriesTest() throws Exception {
        DefaultErrorHandlerRedeliveryTest.counter = 0;
        try {
            template.sendBody("direct:no", "Hello World");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            // expected
        }
        Assert.assertEquals(1, DefaultErrorHandlerRedeliveryTest.counter);// One call

    }

    @Test
    public void testOneRedeliveryTest() throws Exception {
        DefaultErrorHandlerRedeliveryTest.counter = 0;
        try {
            template.sendBody("direct:one", "Hello World");
            Assert.fail("Should have thrown exception");
        } catch (RuntimeCamelException e) {
            // expected
        }
        Assert.assertEquals(2, DefaultErrorHandlerRedeliveryTest.counter);// One call + 1 re-delivery

    }
}

