/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.data;


import Time.LOGICAL_NAME;
import Time.SCHEMA;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.kafka.connect.errors.DataException;
import org.junit.Assert;
import org.junit.Test;

import static Time.SCHEMA;


public class TimeTest {
    private static final GregorianCalendar EPOCH;

    private static final GregorianCalendar EPOCH_PLUS_DATE_COMPONENT;

    private static final GregorianCalendar EPOCH_PLUS_TEN_THOUSAND_MILLIS;

    static {
        EPOCH = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        TimeTest.EPOCH.setTimeZone(TimeZone.getTimeZone("UTC"));
        EPOCH_PLUS_TEN_THOUSAND_MILLIS = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        TimeTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.setTimeZone(TimeZone.getTimeZone("UTC"));
        TimeTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.add(Calendar.MILLISECOND, 10000);
        EPOCH_PLUS_DATE_COMPONENT = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        TimeTest.EPOCH_PLUS_DATE_COMPONENT.setTimeZone(TimeZone.getTimeZone("UTC"));
        TimeTest.EPOCH_PLUS_DATE_COMPONENT.add(Calendar.DATE, 10000);
    }

    @Test
    public void testBuilder() {
        Schema plain = SCHEMA;
        Assert.assertEquals(LOGICAL_NAME, plain.name());
        Assert.assertEquals(1, ((Object) (plain.version())));
    }

    @Test
    public void testFromLogical() {
        Assert.assertEquals(0, Time.fromLogical(SCHEMA, TimeTest.EPOCH.getTime()));
        Assert.assertEquals(10000, Time.fromLogical(SCHEMA, TimeTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.getTime()));
    }

    @Test(expected = DataException.class)
    public void testFromLogicalInvalidSchema() {
        Time.fromLogical(Time.builder().name("invalid").build(), TimeTest.EPOCH.getTime());
    }

    @Test(expected = DataException.class)
    public void testFromLogicalInvalidHasDateComponents() {
        Time.fromLogical(SCHEMA, TimeTest.EPOCH_PLUS_DATE_COMPONENT.getTime());
    }

    @Test
    public void testToLogical() {
        Assert.assertEquals(TimeTest.EPOCH.getTime(), Time.toLogical(SCHEMA, 0));
        Assert.assertEquals(TimeTest.EPOCH_PLUS_TEN_THOUSAND_MILLIS.getTime(), Time.toLogical(SCHEMA, 10000));
    }

    @Test(expected = DataException.class)
    public void testToLogicalInvalidSchema() {
        Time.toLogical(Time.builder().name("invalid").build(), 0);
    }
}

