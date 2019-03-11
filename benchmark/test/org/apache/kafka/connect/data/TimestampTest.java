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
import Timestamp.SCHEMA;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.kafka.connect.errors.DataException;
import org.junit.Assert;
import org.junit.Test;

import static Date.SCHEMA;


public class TimestampTest {
    private static final GregorianCalendar EPOCH;

    private static final GregorianCalendar EPOCH_PLUS_MILLIS;

    private static final int NUM_MILLIS = 2000000000;

    private static final long TOTAL_MILLIS = ((long) (TimestampTest.NUM_MILLIS)) * 2;

    static {
        EPOCH = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        TimestampTest.EPOCH.setTimeZone(TimeZone.getTimeZone("UTC"));
        EPOCH_PLUS_MILLIS = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        TimestampTest.EPOCH_PLUS_MILLIS.setTimeZone(TimeZone.getTimeZone("UTC"));
        TimestampTest.EPOCH_PLUS_MILLIS.add(Calendar.MILLISECOND, TimestampTest.NUM_MILLIS);
        TimestampTest.EPOCH_PLUS_MILLIS.add(Calendar.MILLISECOND, TimestampTest.NUM_MILLIS);
    }

    @Test
    public void testBuilder() {
        Schema plain = SCHEMA;
        Assert.assertEquals(LOGICAL_NAME, plain.name());
        Assert.assertEquals(1, ((Object) (plain.version())));
    }

    @Test
    public void testFromLogical() {
        Assert.assertEquals(0L, Timestamp.fromLogical(SCHEMA, TimestampTest.EPOCH.getTime()));
        Assert.assertEquals(TimestampTest.TOTAL_MILLIS, Timestamp.fromLogical(SCHEMA, TimestampTest.EPOCH_PLUS_MILLIS.getTime()));
    }

    @Test(expected = DataException.class)
    public void testFromLogicalInvalidSchema() {
        Timestamp.fromLogical(Timestamp.builder().name("invalid").build(), TimestampTest.EPOCH.getTime());
    }

    @Test
    public void testToLogical() {
        Assert.assertEquals(TimestampTest.EPOCH.getTime(), Timestamp.toLogical(SCHEMA, 0L));
        Assert.assertEquals(TimestampTest.EPOCH_PLUS_MILLIS.getTime(), Timestamp.toLogical(SCHEMA, TimestampTest.TOTAL_MILLIS));
    }

    @Test(expected = DataException.class)
    public void testToLogicalInvalidSchema() {
        Date.toLogical(Date.builder().name("invalid").build(), 0);
    }
}

