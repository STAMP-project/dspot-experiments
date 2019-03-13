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
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class AsyncLoopTest extends ContextTestSupport {
    private static final String BASE_PAYLOAD = "<Hello n='4'/>";

    MockEndpoint resultEndpoint;

    @Test
    public void testCounterLoop() throws Exception {
        performLoopTest("direct:a", 8);
    }

    @Test
    public void testExpressionLoop() throws Exception {
        performLoopTest("direct:b", 6);
    }

    @Test
    public void testExpressionClauseLoop() throws Exception {
        performLoopTest("direct:c", 4);
    }

    @Test
    public void testLoopAsBlock() throws Exception {
        MockEndpoint lastEndpoint = resolveMandatoryEndpoint("mock:last", MockEndpoint.class);
        lastEndpoint.expectedMessageCount(1);
        lastEndpoint.expectedBodiesReceived(((AsyncLoopTest.BASE_PAYLOAD) + (new String(new char[2]).replace("\u0000", " Hello Camel"))));
        performLoopTest("direct:d", 2);
        lastEndpoint.assertIsSatisfied();
    }

    @Test
    public void testLoopWithInvalidExpression() throws Exception {
        try {
            performLoopTest("direct:b", 4, "invalid");
            Assert.fail("Exception expected for invalid expression");
        } catch (RuntimeCamelException e) {
            // expected
        }
    }

    @Test
    public void testLoopProperties() throws Exception {
        MockEndpoint lastEndpoint = resolveMandatoryEndpoint("mock:last", MockEndpoint.class);
        lastEndpoint.expectedMessageCount(1);
        lastEndpoint.expectedBodiesReceived(((AsyncLoopTest.BASE_PAYLOAD) + (new String(new char[10]).replace("\u0000", " Hello Camel"))));
        performLoopTest("direct:e", 10);
        lastEndpoint.assertIsSatisfied();
    }
}

