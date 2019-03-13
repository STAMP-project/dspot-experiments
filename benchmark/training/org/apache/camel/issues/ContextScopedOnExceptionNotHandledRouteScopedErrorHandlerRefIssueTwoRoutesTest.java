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
package org.apache.camel.issues;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ContextScopedOnExceptionNotHandledRouteScopedErrorHandlerRefIssueTwoRoutesTest extends ContextTestSupport {
    @Test
    public void testOnExceptionErrorHandlerRef() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:handled").expectedMessageCount(1);
        getMockEndpoint("mock:dead").expectedMessageCount(0);
        try {
            template.sendBody("direct:start", "Hello World");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            IllegalArgumentException cause = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Damn", cause.getMessage());
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOnExceptionErrorHandlerRefFoo() throws Exception {
        getMockEndpoint("mock:a").expectedMessageCount(0);
        getMockEndpoint("mock:handled").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        template.sendBody("direct:foo", "Hello Foo");
        assertMockEndpointsSatisfied();
    }
}

