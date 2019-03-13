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


import LoggingLevel.DEBUG;
import LoggingLevel.INFO;
import LoggingLevel.WARN;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.TypeConversionException;
import org.junit.Assert;
import org.junit.Test;


public class EnumConverterTest extends ContextTestSupport {
    @Test
    public void testMandatoryConvertEnum() throws Exception {
        LoggingLevel level = context.getTypeConverter().mandatoryConvertTo(LoggingLevel.class, "DEBUG");
        Assert.assertEquals(DEBUG, level);
    }

    @Test
    public void testMandatoryConvertWithExchangeEnum() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        LoggingLevel level = context.getTypeConverter().mandatoryConvertTo(LoggingLevel.class, exchange, "WARN");
        Assert.assertEquals(WARN, level);
    }

    @Test
    public void testCaseInsensitive() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        LoggingLevel level = context.getTypeConverter().mandatoryConvertTo(LoggingLevel.class, exchange, "Warn");
        Assert.assertEquals(WARN, level);
        level = context.getTypeConverter().mandatoryConvertTo(LoggingLevel.class, exchange, "warn");
        Assert.assertEquals(WARN, level);
        level = context.getTypeConverter().mandatoryConvertTo(LoggingLevel.class, exchange, "wARn");
        Assert.assertEquals(WARN, level);
        level = context.getTypeConverter().mandatoryConvertTo(LoggingLevel.class, exchange, "inFO");
        Assert.assertEquals(INFO, level);
    }

    @Test
    public void testMandatoryConvertFailed() throws Exception {
        try {
            context.getTypeConverter().mandatoryConvertTo(LoggingLevel.class, "XXX");
            Assert.fail("Should have thrown an exception");
        } catch (TypeConversionException e) {
            // expected
        }
    }
}

