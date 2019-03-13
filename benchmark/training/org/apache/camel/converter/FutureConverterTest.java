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
package org.apache.camel.converter;


import java.sql.Timestamp;
import java.util.concurrent.Future;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.NoTypeConversionAvailableException;
import org.junit.Assert;
import org.junit.Test;


public class FutureConverterTest extends ContextTestSupport {
    @Test
    public void testConvertFuture() {
        Future<?> future = template.asyncRequestBody("direct:foo", "Hello World");
        String out = context.getTypeConverter().convertTo(String.class, future);
        Assert.assertEquals("Bye World", out);
    }

    @Test
    public void testConvertMandatoryFuture() throws Exception {
        Future<?> future = template.asyncRequestBody("direct:foo", "Hello World");
        String out = context.getTypeConverter().mandatoryConvertTo(String.class, future);
        Assert.assertEquals("Bye World", out);
    }

    @Test
    public void testConvertMandatoryFutureWithExchange() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Future<?> future = template.asyncRequestBody("direct:foo", "Hello World");
        String out = context.getTypeConverter().mandatoryConvertTo(String.class, exchange, future);
        Assert.assertEquals("Bye World", out);
    }

    @Test
    public void testConvertMandatoryFutureWithExchangeFailed() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Future<?> future = template.asyncRequestBody("direct:foo", "Hello World");
        try {
            context.getTypeConverter().mandatoryConvertTo(Timestamp.class, exchange, future);
            Assert.fail("Should have thrown an exception");
        } catch (NoTypeConversionAvailableException e) {
            // expected
        }
    }

    @Test
    public void testConvertFutureWithExchangeFailed() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Future<?> future = template.asyncRequestBody("direct:foo", "Hello World");
        Timestamp out = context.getTypeConverter().convertTo(Timestamp.class, exchange, future);
        Assert.assertNull(out);
    }

    @Test
    public void testConvertFutureCancelled() {
        Future<?> future = template.asyncRequestBody("direct:foo", "Hello World");
        future.cancel(true);
        Object out = context.getTypeConverter().convertTo(String.class, future);
        // should be null since its cancelled
        Assert.assertNull(out);
    }

    @Test
    public void testConvertFutureCancelledThenOkay() {
        Future<?> future = template.asyncRequestBody("direct:foo", "Hello World");
        future.cancel(true);
        Object out = context.getTypeConverter().convertTo(String.class, future);
        // should be null since its cancelled
        Assert.assertNull(out);
        future = template.asyncRequestBody("direct:foo", "Hello World");
        out = context.getTypeConverter().convertTo(String.class, future);
        // not cancelled so we get the result this time
        Assert.assertEquals("Bye World", out);
    }
}

