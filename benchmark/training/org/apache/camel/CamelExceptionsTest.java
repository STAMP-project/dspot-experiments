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
package org.apache.camel;


import ExchangePattern.InOnly;
import ExchangePattern.InOptionalOut;
import ExchangePattern.InOut;
import ServiceStatus.Started;
import ServiceStatus.Starting;
import ServiceStatus.Stopped;
import ServiceStatus.Stopping;
import java.util.Date;
import org.apache.camel.builder.ExpressionBuilder;
import org.junit.Assert;
import org.junit.Test;


public class CamelExceptionsTest extends ContextTestSupport {
    @Test
    public void testExpectedBodyTypeException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        ExpectedBodyTypeException e = new ExpectedBodyTypeException(exchange, Integer.class);
        Assert.assertSame(exchange, e.getExchange());
        Assert.assertEquals(Integer.class, e.getExpectedBodyType());
    }

    @Test
    public void testExpressionEvaluationException() {
        Expression exp = ExpressionBuilder.constantExpression("foo");
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        ExpressionEvaluationException e = new ExpressionEvaluationException(exp, exchange, new IllegalArgumentException("Damn"));
        Assert.assertSame(exchange, e.getExchange());
        Assert.assertSame(exp, e.getExpression());
        Assert.assertNotNull(e.getCause());
    }

    @Test
    public void testFailedToCreateConsumerException() {
        Endpoint endpoint = context.getEndpoint("seda:foo");
        FailedToCreateConsumerException e = new FailedToCreateConsumerException(endpoint, new IllegalArgumentException("Damn"));
        Assert.assertEquals(endpoint.getEndpointUri(), e.getUri());
        Assert.assertNotNull(e.getCause());
    }

    @Test
    public void testFailedToCreateProducerException() {
        Endpoint endpoint = context.getEndpoint("seda:foo");
        FailedToCreateProducerException e = new FailedToCreateProducerException(endpoint, new IllegalArgumentException("Damn"));
        Assert.assertEquals(endpoint.getEndpointUri(), e.getUri());
        Assert.assertNotNull(e.getCause());
    }

    @Test
    public void testInvalidPayloadRuntimeException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        InvalidPayloadRuntimeException e = new InvalidPayloadRuntimeException(exchange, Integer.class);
        Assert.assertSame(exchange, e.getExchange());
        Assert.assertEquals(Integer.class, e.getType());
        InvalidPayloadRuntimeException e2 = new InvalidPayloadRuntimeException(exchange, Integer.class, exchange.getIn());
        Assert.assertSame(exchange, e2.getExchange());
        Assert.assertEquals(Integer.class, e2.getType());
        InvalidPayloadRuntimeException e3 = new InvalidPayloadRuntimeException(exchange, Integer.class, exchange.getIn(), new IllegalArgumentException("Damn"));
        Assert.assertSame(exchange, e3.getExchange());
        Assert.assertEquals(Integer.class, e3.getType());
    }

    @Test
    public void testRuntimeTransformException() {
        RuntimeTransformException e = new RuntimeTransformException("Forced");
        Assert.assertEquals("Forced", e.getMessage());
        Assert.assertNull(e.getCause());
        RuntimeTransformException e2 = new RuntimeTransformException("Forced", new IllegalAccessException("Damn"));
        Assert.assertEquals("Forced", e2.getMessage());
        Assert.assertNotNull(e2.getCause());
        RuntimeTransformException e3 = new RuntimeTransformException(new IllegalAccessException("Damn"));
        Assert.assertEquals("java.lang.IllegalAccessException: Damn", e3.getMessage());
        Assert.assertNotNull(e3.getCause());
    }

    @Test
    public void testRuntimeExpressionException() {
        RuntimeExpressionException e = new RuntimeExpressionException("Forced");
        Assert.assertEquals("Forced", e.getMessage());
        Assert.assertNull(e.getCause());
        RuntimeExpressionException e2 = new RuntimeExpressionException("Forced", new IllegalAccessException("Damn"));
        Assert.assertEquals("Forced", e2.getMessage());
        Assert.assertNotNull(e2.getCause());
        RuntimeExpressionException e3 = new RuntimeExpressionException(new IllegalAccessException("Damn"));
        Assert.assertEquals("java.lang.IllegalAccessException: Damn", e3.getMessage());
        Assert.assertNotNull(e3.getCause());
    }

    @Test
    public void testRollbackExchangeException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        RollbackExchangeException e = new RollbackExchangeException(exchange, new IllegalAccessException("Damn"));
        Assert.assertNotNull(e.getMessage());
        Assert.assertSame(exchange, e.getExchange());
        RollbackExchangeException e2 = new RollbackExchangeException("Forced", exchange, new IllegalAccessException("Damn"));
        Assert.assertNotNull(e2.getMessage());
        Assert.assertSame(exchange, e2.getExchange());
    }

    @Test
    public void testValidationException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        ValidationException e = new ValidationException(exchange, "Forced");
        Assert.assertNotNull(e.getMessage());
        Assert.assertSame(exchange, e.getExchange());
        ValidationException e2 = new ValidationException("Forced", exchange, new IllegalAccessException("Damn"));
        Assert.assertNotNull(e2.getMessage());
        Assert.assertSame(exchange, e2.getExchange());
    }

    @Test
    public void testNoSuchBeanException() {
        NoSuchBeanException e = new NoSuchBeanException("foo");
        Assert.assertEquals("foo", e.getName());
        Assert.assertNull(e.getCause());
        NoSuchBeanException e2 = new NoSuchBeanException("foo", new IllegalArgumentException("Damn"));
        Assert.assertEquals("foo", e2.getName());
        Assert.assertNotNull(e2.getCause());
    }

    @Test
    public void testCamelExecutionException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        CamelExecutionException e = new CamelExecutionException("Forced", exchange);
        Assert.assertNotNull(e.getMessage());
        Assert.assertSame(exchange, e.getExchange());
        Assert.assertNull(e.getCause());
        CamelExecutionException e2 = new CamelExecutionException("Forced", exchange, new IllegalArgumentException("Damn"));
        Assert.assertNotNull(e2.getMessage());
        Assert.assertSame(exchange, e2.getExchange());
        Assert.assertNotNull(e2.getCause());
    }

    @Test
    public void testCamelException() {
        CamelException e = new CamelException();
        Assert.assertNull(e.getCause());
        CamelException e2 = new CamelException("Forced");
        Assert.assertNull(e2.getCause());
        Assert.assertEquals("Forced", e2.getMessage());
        CamelException e3 = new CamelException("Forced", new IllegalArgumentException("Damn"));
        Assert.assertNotNull(e3.getCause());
        Assert.assertEquals("Forced", e3.getMessage());
        CamelException e4 = new CamelException(new IllegalArgumentException("Damn"));
        Assert.assertNotNull(e4.getCause());
        Assert.assertNotNull(e4.getMessage());
    }

    @Test
    public void testServiceStatus() {
        Assert.assertTrue(Started.isStarted());
        Assert.assertFalse(Starting.isStarted());
        Assert.assertFalse(Starting.isStoppable());
        Assert.assertFalse(Stopped.isStarted());
        Assert.assertFalse(Stopping.isStarted());
        Assert.assertTrue(Stopped.isStopped());
        Assert.assertFalse(Starting.isStopped());
        Assert.assertFalse(Started.isStopped());
        Assert.assertFalse(Stopping.isStopped());
        Assert.assertTrue(Stopped.isStartable());
        Assert.assertFalse(Started.isStartable());
        Assert.assertFalse(Starting.isStartable());
        Assert.assertFalse(Stopping.isStartable());
        Assert.assertTrue(Started.isStoppable());
        Assert.assertFalse(Starting.isStoppable());
        Assert.assertFalse(Stopped.isStoppable());
        Assert.assertFalse(Stopping.isStoppable());
    }

    @Test
    public void testRuntimeExchangeException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        RuntimeExchangeException e = new RuntimeExchangeException("Forced", exchange);
        Assert.assertNotNull(e.getMessage());
        Assert.assertSame(exchange, e.getExchange());
        RuntimeExchangeException e2 = new RuntimeExchangeException("Forced", null);
        Assert.assertNotNull(e2.getMessage());
        Assert.assertNull(e2.getExchange());
    }

    @Test
    public void testExchangePattern() {
        Assert.assertTrue(InOnly.isInCapable());
        Assert.assertTrue(InOptionalOut.isInCapable());
        Assert.assertTrue(InOut.isInCapable());
        Assert.assertFalse(InOnly.isFaultCapable());
        Assert.assertTrue(InOptionalOut.isFaultCapable());
        Assert.assertTrue(InOut.isFaultCapable());
        Assert.assertFalse(InOnly.isOutCapable());
        Assert.assertTrue(InOptionalOut.isOutCapable());
        Assert.assertTrue(InOut.isOutCapable());
        Assert.assertEquals(InOnly, ExchangePattern.asEnum("InOnly"));
        Assert.assertEquals(InOut, ExchangePattern.asEnum("InOut"));
        try {
            ExchangePattern.asEnum("foo");
            Assert.fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testInvalidPayloadException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        InvalidPayloadException e = new InvalidPayloadException(exchange, Integer.class);
        Assert.assertSame(exchange, e.getExchange());
        Assert.assertEquals(Integer.class, e.getType());
    }

    @Test
    public void testExchangeTimedOutException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        ExchangeTimedOutException e = new ExchangeTimedOutException(exchange, 5000);
        Assert.assertSame(exchange, e.getExchange());
        Assert.assertEquals(5000, e.getTimeout());
    }

    @Test
    public void testExpressionIllegalSyntaxException() {
        ExpressionIllegalSyntaxException e = new ExpressionIllegalSyntaxException("foo");
        Assert.assertEquals("foo", e.getExpression());
    }

    @Test
    public void testNoFactoryAvailableException() {
        NoFactoryAvailableException e = new NoFactoryAvailableException("killer", new IllegalArgumentException("Damn"));
        Assert.assertNotNull(e.getCause());
        Assert.assertEquals("killer", e.getUri());
    }

    @Test
    public void testCamelExchangeException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        CamelExchangeException e = new CamelExchangeException("Forced", exchange);
        Assert.assertNotNull(e.getMessage());
        Assert.assertSame(exchange, e.getExchange());
    }

    @Test
    public void testNoSuchHeaderException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        NoSuchHeaderException e = new NoSuchHeaderException(exchange, "foo", Integer.class);
        Assert.assertEquals(Integer.class, e.getType());
        Assert.assertEquals("foo", e.getHeaderName());
        Assert.assertSame(exchange, e.getExchange());
    }

    @Test
    public void testNoSuchPropertyException() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        NoSuchPropertyException e = new NoSuchPropertyException(exchange, "foo", Integer.class);
        Assert.assertEquals(Integer.class, e.getType());
        Assert.assertEquals("foo", e.getPropertyName());
        Assert.assertSame(exchange, e.getExchange());
    }

    @Test
    public void testRuntimeCamelException() {
        RuntimeCamelException e = new RuntimeCamelException();
        Assert.assertNull(e.getMessage());
        Assert.assertNull(e.getCause());
    }

    @Test
    public void testFailedToStartRouteException() {
        FailedToStartRouteException e = new FailedToStartRouteException(new IllegalArgumentException("Forced"));
        Assert.assertNotNull(e.getMessage());
        TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
    }

    @Test
    public void testNoTypeConversionAvailableException() {
        NoTypeConversionAvailableException e = new NoTypeConversionAvailableException("foo", Date.class);
        Assert.assertEquals("foo", e.getValue());
        Assert.assertEquals(Date.class, e.getToType());
        Assert.assertEquals(String.class, e.getFromType());
        NoTypeConversionAvailableException e2 = new NoTypeConversionAvailableException(null, Date.class);
        Assert.assertNull(e2.getValue());
        Assert.assertEquals(Date.class, e2.getToType());
        Assert.assertNull(null, e2.getFromType());
    }

    @Test
    public void testResolveEndpointFailedException() {
        ResolveEndpointFailedException e = new ResolveEndpointFailedException("foo:bar");
        Assert.assertEquals("foo:bar", e.getUri());
    }
}

