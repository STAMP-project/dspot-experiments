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
 * Unit test for verifying that error handler is wrapped each individual node in a pipeline.
 * Based on CAMEL-1548.
 */
public class ErrorHandlerWrappedEachNodeTest extends ContextTestSupport {
    private static int kabom;

    private static int hi;

    @Test
    public void testKabom() throws Exception {
        ErrorHandlerWrappedEachNodeTest.kabom = 0;
        ErrorHandlerWrappedEachNodeTest.hi = 0;
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedBodiesReceived("Hi Kabom");
        getMockEndpoint("mock:error").expectedMessageCount(0);
        template.sendBody("direct:start", "Kabom");
        assertMockEndpointsSatisfied();
        // we invoke kabom 3 times
        Assert.assertEquals(3, ErrorHandlerWrappedEachNodeTest.kabom);
        // but hi is only invoke 1 time
        Assert.assertEquals(1, ErrorHandlerWrappedEachNodeTest.hi);
    }

    public static final class MyFooBean {
        public void kabom() throws Exception {
            if (((ErrorHandlerWrappedEachNodeTest.kabom)++) < 2) {
                throw new IllegalArgumentException("Kabom");
            }
        }

        public String hi(String payload) throws Exception {
            (ErrorHandlerWrappedEachNodeTest.hi)++;
            return "Hi " + payload;
        }
    }
}

