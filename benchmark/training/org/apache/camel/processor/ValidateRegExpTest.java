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


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.processor.validation.PredicateValidationException;
import org.junit.Assert;
import org.junit.Test;


public class ValidateRegExpTest extends ContextTestSupport {
    protected Endpoint startEndpoint;

    protected MockEndpoint resultEndpoint;

    @Test
    public void testSendMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        template.sendBody(startEndpoint, "01.01.2010");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSendNotMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(0);
        try {
            template.sendBody(startEndpoint, "1.1.2010");
            Assert.fail("CamelExecutionException expected");
        } catch (CamelExecutionException e) {
            // expected
            PredicateValidationException cause = TestSupport.assertIsInstanceOf(PredicateValidationException.class, e.getCause());
            // as the Expression could be different between the DSL and simple language, here we just check part of the message
            Assert.assertTrue("Get a wrong exception message", cause.getMessage().startsWith("Validation failed for Predicate"));
            Assert.assertTrue(cause.getMessage().contains("^\\d{2}\\.\\d{2}\\.\\d{4}$"));
            String body = cause.getExchange().getIn().getBody(String.class);
            Assert.assertEquals("1.1.2010", body);
        }
        assertMockEndpointsSatisfied();
    }
}

