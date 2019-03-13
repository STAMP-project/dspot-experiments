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
package org.apache.camel.impl.validator;


import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Message;
import org.apache.camel.TestSupport;
import org.apache.camel.ValidationException;
import org.apache.camel.spi.DataType;
import org.apache.camel.spi.Validator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BeanValidatorOutputValidateTest extends ContextTestSupport {
    public static class TestValidator extends Validator {
        private static final Logger LOG = LoggerFactory.getLogger(BeanValidatorOutputValidateTest.TestValidator.class);

        @Override
        public void validate(Message message, DataType type) throws ValidationException {
            Object body = message.getBody();
            BeanValidatorOutputValidateTest.TestValidator.LOG.info("Validating : [{}]", body);
            if ((body instanceof String) && (body.equals("valid"))) {
                BeanValidatorOutputValidateTest.TestValidator.LOG.info("OK");
            } else {
                throw new ValidationException(message.getExchange(), "Wrong content");
            }
        }
    }

    @Test
    public void testValid() throws InterruptedException {
        getMockEndpoint("mock:out").expectedMessageCount(1);
        getMockEndpoint("mock:invalid").expectedMessageCount(0);
        template.sendBody("direct:in", "valid");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testInvalid() throws InterruptedException {
        getMockEndpoint("mock:out").expectedMessageCount(1);
        getMockEndpoint("mock:invalid").expectedMessageCount(0);
        try {
            template.sendBody("direct:in", "wrong");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(ValidationException.class, e.getCause());
            Assert.assertTrue(e.getCause().getMessage().startsWith("Wrong content"));
        }
        assertMockEndpointsSatisfied();
    }
}

