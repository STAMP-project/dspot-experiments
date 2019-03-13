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
package org.apache.camel.component.bean;


import java.util.Map;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.ExpressionEvaluationException;
import org.apache.camel.TestSupport;
import org.apache.camel.TypeConversionException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class BeanParameterInvalidValueTest extends ContextTestSupport {
    @Test
    public void testBeanParameterInvalidValueA() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.sendBody("direct:a", "World");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            TypeConversionException cause = TestSupport.assertIsInstanceOf(TypeConversionException.class, e.getCause().getCause());
            Assert.assertEquals(String.class, cause.getFromType());
            Assert.assertEquals(int.class, cause.getToType());
            Assert.assertEquals("A", cause.getValue());
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterInvalidValueB() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.sendBody("direct:b", "World");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            TypeConversionException cause = TestSupport.assertIsInstanceOf(TypeConversionException.class, e.getCause().getCause());
            Assert.assertEquals(String.class, cause.getFromType());
            Assert.assertEquals(int.class, cause.getToType());
            Assert.assertEquals("true", cause.getValue());
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterNullC() throws Exception {
        // should be an empty string
        getMockEndpoint("mock:result").expectedBodiesReceived("");
        template.sendBody("direct:c", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterInvalidValueD() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        try {
            template.sendBody("direct:d", "World");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            ExpressionEvaluationException cause = TestSupport.assertIsInstanceOf(ExpressionEvaluationException.class, e.getCause());
            Assert.assertTrue(cause.getCause().getMessage().startsWith("Unknown function: xxx at location 0"));
        }
        assertMockEndpointsSatisfied();
    }

    public static class MyBean {
        public String echo(String body, int times) {
            if (body == null) {
                // use an empty string for no body
                return "";
            }
            if (times > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < times; i++) {
                    sb.append(body);
                }
                return sb.toString();
            }
            return body;
        }

        public String heads(String body, Map<?, ?> headers) {
            return ((headers.get("hello")) + " ") + body;
        }
    }
}

