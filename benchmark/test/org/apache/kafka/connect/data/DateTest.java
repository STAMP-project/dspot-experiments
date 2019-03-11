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


import Date.LOGICAL_NAME;
import Date.SCHEMA;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.kafka.connect.errors.DataException;
import org.junit.Assert;
import org.junit.Test;

import static Date.SCHEMA;


public class DateTest {
    private static final GregorianCalendar EPOCH;

    private static final GregorianCalendar EPOCH_PLUS_TEN_THOUSAND_DAYS;

    private static final GregorianCalendar EPOCH_PLUS_TIME_COMPONENT;

    static {
        EPOCH = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        DateTest.EPOCH.setTimeZone(TimeZone.getTimeZone("UTC"));
        EPOCH_PLUS_TIME_COMPONENT = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 1);
        DateTest.EPOCH_PLUS_TIME_COMPONENT.setTimeZone(TimeZone.getTimeZone("UTC"));
        EPOCH_PLUS_TEN_THOUSAND_DAYS = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        DateTest.EPOCH_PLUS_TEN_THOUSAND_DAYS.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateTest.EPOCH_PLUS_TEN_THOUSAND_DAYS.add(Calendar.DATE, 10000);
    }

    @Test
    public void testBuilder() {
        Schema plain = SCHEMA;
        Assert.assertEquals(LOGICAL_NAME, plain.name());
        Assert.assertEquals(1, ((Object) (plain.version())));
    }

    @Test
    public void testFromLogical() {
        Assert.assertEquals(0, Date.fromLogical(SCHEMA, DateTest.EPOCH.getTime()));
        Assert.assertEquals(10000, Date.fromLogical(SCHEMA, DateTest.EPOCH_PLUS_TEN_THOUSAND_DAYS.getTime()));
    }

    @Test(expected = DataException.class)
    public void testFromLogicalInvalidSchema() {
        Date.fromLogical(Date.builder().name("invalid").build(), DateTest.EPOCH.getTime());
    }

    @Test(expected = DataException.class)
    public void testFromLogicalInvalidHasTimeComponents() {
        Date.fromLogical(SCHEMA, DateTest.EPOCH_PLUS_TIME_COMPONENT.getTime());
    }

    @Test
    public void testToLogical() {
        Assert.assertEquals(DateTest.EPOCH.getTime(), Date.toLogical(SCHEMA, 0));
        Assert.assertEquals(DateTest.EPOCH_PLUS_TEN_THOUSAND_DAYS.getTime(), Date.toLogical(SCHEMA, 10000));
    }

    @Test(expected = DataException.class)
    public void testToLogicalInvalidSchema() {
        Date.toLogical(Date.builder().name("invalid").build(), 0);
    }
}

