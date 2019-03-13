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
package org.apache.camel.spring.handler;


import org.apache.camel.Processor;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.processor.RedeliveryPolicy;
import org.apache.camel.spring.spi.TransactionErrorHandlerBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ErrorHandlerDefinitionParserTest extends Assert {
    protected ClassPathXmlApplicationContext ctx;

    @Test
    public void testDefaultErrorHandler() {
        DefaultErrorHandlerBuilder errorHandler = ctx.getBean("defaultErrorHandler", DefaultErrorHandlerBuilder.class);
        Assert.assertNotNull(errorHandler);
        RedeliveryPolicy policy = errorHandler.getRedeliveryPolicy();
        Assert.assertNotNull(policy);
        Assert.assertEquals("Wrong maximumRedeliveries", 2, policy.getMaximumRedeliveries());
        Assert.assertEquals("Wrong redeliveryDelay", 0, policy.getRedeliveryDelay());
        Assert.assertEquals("Wrong logStackTrace", false, policy.isLogStackTrace());
        errorHandler = ctx.getBean("errorHandler", DefaultErrorHandlerBuilder.class);
        Assert.assertNotNull(errorHandler);
    }

    @Test
    public void testTransactionErrorHandler() {
        TransactionErrorHandlerBuilder errorHandler = ctx.getBean("transactionErrorHandler", TransactionErrorHandlerBuilder.class);
        Assert.assertNotNull(errorHandler);
        Assert.assertNotNull(errorHandler.getTransactionTemplate());
        Processor processor = errorHandler.getOnRedelivery();
        Assert.assertTrue("It should be MyErrorProcessor", (processor instanceof MyErrorProcessor));
    }

    @Test
    public void testTXErrorHandler() {
        TransactionErrorHandlerBuilder errorHandler = ctx.getBean("txEH", TransactionErrorHandlerBuilder.class);
        Assert.assertNotNull(errorHandler);
        Assert.assertNotNull(errorHandler.getTransactionTemplate());
    }

    @Test
    public void testDeadLetterErrorHandler() {
        DeadLetterChannelBuilder errorHandler = ctx.getBean("deadLetterErrorHandler", DeadLetterChannelBuilder.class);
        Assert.assertNotNull(errorHandler);
        Assert.assertEquals("Get wrong deadletteruri", "log:dead", errorHandler.getDeadLetterUri());
        RedeliveryPolicy policy = errorHandler.getRedeliveryPolicy();
        Assert.assertNotNull(policy);
        Assert.assertEquals("Wrong maximumRedeliveries", 2, policy.getMaximumRedeliveries());
        Assert.assertEquals("Wrong redeliveryDelay", 1000, policy.getRedeliveryDelay());
        Assert.assertEquals("Wrong logStackTrace", true, policy.isLogHandled());
        Assert.assertEquals("Wrong asyncRedeliveryDelayed", true, policy.isAsyncDelayedRedelivery());
    }
}

