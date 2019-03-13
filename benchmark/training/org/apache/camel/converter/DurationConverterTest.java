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


import java.time.Duration;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.TypeConversionException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class DurationConverterTest extends ContextTestSupport {
    @Test
    public void testToMillis() throws Exception {
        Duration duration = Duration.parse("PT2H6M20.31S");
        Long millis = context.getTypeConverter().convertTo(long.class, duration);
        Assert.assertNotNull(millis);
        Assert.assertThat(millis, Is.is(7580310L));
    }

    @Test
    public void testToMillisOverflow() throws Exception {
        Duration duration = Duration.parse("P60000000000000D");
        try {
            context.getTypeConverter().convertTo(long.class, duration);
            Assert.fail("Should throw exception");
        } catch (TypeConversionException e) {
            TestSupport.assertIsInstanceOf(ArithmeticException.class, e.getCause());
        }
    }

    @Test
    public void testFromString() throws Exception {
        String durationAsString = "PT2H6M20.31S";
        Duration duration = context.getTypeConverter().convertTo(Duration.class, durationAsString);
        Assert.assertNotNull(duration);
        Assert.assertThat(duration.toString(), Is.is("PT2H6M20.31S"));
    }

    @Test
    public void testToString() throws Exception {
        Duration duration = Duration.parse("PT2H6M20.31S");
        String durationAsString = context.getTypeConverter().convertTo(String.class, duration);
        Assert.assertNotNull(durationAsString);
        Assert.assertThat(durationAsString, Is.is("PT2H6M20.31S"));
    }
}

