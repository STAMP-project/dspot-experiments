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
package org.apache.camel.component.hl7;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class TerserExpressionTest extends CamelTestSupport {
    private static final String PATIENT_ID = "123456";

    @Test
    public void testTerserExpression() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test1");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(TerserExpressionTest.PATIENT_ID);
        template.sendBody("direct:test1", TerserExpressionTest.createADT01Message());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTerserPredicateValue() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test2");
        mock.expectedMessageCount(1);
        template.sendBody("direct:test2", TerserExpressionTest.createADT01Message());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTerserPredicateNull() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test3");
        mock.expectedMessageCount(1);
        template.sendBody("direct:test3", TerserExpressionTest.createADT01Message());
        assertMockEndpointsSatisfied();
    }

    @Test(expected = CamelExecutionException.class)
    public void testTerserInvalidExpression() throws Exception {
        template.sendBody("direct:test4", TerserExpressionTest.createADT01Message());
    }

    @Test(expected = CamelExecutionException.class)
    public void testTerserInvalidMessage() throws Exception {
        template.sendBody("direct:test4", "text instead of message");
    }

    @Test
    public void testTerserAnnotatedMethod() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:test5");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(TerserExpressionTest.PATIENT_ID);
        template.sendBody("direct:test5", TerserExpressionTest.createADT01Message());
        assertMockEndpointsSatisfied();
    }

    public class TerserBean {
        public String patientId(@Hl7Terser("PID-3-1")
        String patientId) {
            return patientId;
        }
    }
}

