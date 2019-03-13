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
package org.apache.commons.lang3.time;


import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for FastTimeZone
 */
public class FastTimeZoneTest {
    private static final int HOURS_23 = ((23 * 60) * 60) * 1000;

    private static final int HOURS_2 = ((2 * 60) * 60) * 1000;

    private static final int MINUTES_59 = (59 * 60) * 1000;

    private static final int MINUTES_5 = (5 * 60) * 1000;

    @Test
    public void testGetGmtTimeZone() {
        Assertions.assertEquals(0, FastTimeZone.getGmtTimeZone().getRawOffset());
    }

    @Test
    public void testBareGmt() {
        Assertions.assertEquals(FastTimeZone.getGmtTimeZone(), FastTimeZone.getTimeZone("GMT"));
    }

    @Test
    public void testZ() {
        Assertions.assertEquals(FastTimeZone.getGmtTimeZone(), FastTimeZone.getTimeZone("Z"));
    }

    @Test
    public void testUTC() {
        Assertions.assertEquals(FastTimeZone.getGmtTimeZone(), FastTimeZone.getTimeZone("UTC"));
    }

    @Test
    public void testZeroOffsetsReturnSingleton() {
        Assertions.assertEquals(FastTimeZone.getGmtTimeZone(), FastTimeZone.getTimeZone("+0"));
        Assertions.assertEquals(FastTimeZone.getGmtTimeZone(), FastTimeZone.getTimeZone("-0"));
    }

    @Test
    public void testOlson() {
        Assertions.assertEquals(TimeZone.getTimeZone("America/New_York"), FastTimeZone.getTimeZone("America/New_York"));
    }

    @Test
    public void testGmtPrefix() {
        Assertions.assertEquals(FastTimeZoneTest.HOURS_23, FastTimeZone.getGmtTimeZone("GMT+23:00").getRawOffset());
        Assertions.assertEquals((-(FastTimeZoneTest.HOURS_23)), FastTimeZone.getGmtTimeZone("GMT-23:00").getRawOffset());
    }

    @Test
    public void testSign() {
        Assertions.assertEquals(FastTimeZoneTest.HOURS_23, FastTimeZone.getGmtTimeZone("+23:00").getRawOffset());
        Assertions.assertEquals(FastTimeZoneTest.HOURS_2, FastTimeZone.getGmtTimeZone("+2:00").getRawOffset());
        Assertions.assertEquals((-(FastTimeZoneTest.HOURS_23)), FastTimeZone.getGmtTimeZone("-23:00").getRawOffset());
        Assertions.assertEquals((-(FastTimeZoneTest.HOURS_2)), FastTimeZone.getGmtTimeZone("-2:00").getRawOffset());
    }

    @Test
    public void testHoursColonMinutes() {
        Assertions.assertEquals(FastTimeZoneTest.HOURS_23, FastTimeZone.getGmtTimeZone("23:00").getRawOffset());
        Assertions.assertEquals(FastTimeZoneTest.HOURS_2, FastTimeZone.getGmtTimeZone("2:00").getRawOffset());
        Assertions.assertEquals(FastTimeZoneTest.MINUTES_59, FastTimeZone.getGmtTimeZone("00:59").getRawOffset());
        Assertions.assertEquals(FastTimeZoneTest.MINUTES_5, FastTimeZone.getGmtTimeZone("00:5").getRawOffset());
        Assertions.assertEquals(((FastTimeZoneTest.HOURS_23) + (FastTimeZoneTest.MINUTES_59)), FastTimeZone.getGmtTimeZone("23:59").getRawOffset());
        Assertions.assertEquals(((FastTimeZoneTest.HOURS_2) + (FastTimeZoneTest.MINUTES_5)), FastTimeZone.getGmtTimeZone("2:5").getRawOffset());
    }

    @Test
    public void testHoursMinutes() {
        Assertions.assertEquals(FastTimeZoneTest.HOURS_23, FastTimeZone.getGmtTimeZone("2300").getRawOffset());
        Assertions.assertEquals(FastTimeZoneTest.HOURS_2, FastTimeZone.getGmtTimeZone("0200").getRawOffset());
        Assertions.assertEquals(FastTimeZoneTest.MINUTES_59, FastTimeZone.getGmtTimeZone("0059").getRawOffset());
        Assertions.assertEquals(FastTimeZoneTest.MINUTES_5, FastTimeZone.getGmtTimeZone("0005").getRawOffset());
        Assertions.assertEquals(((FastTimeZoneTest.HOURS_23) + (FastTimeZoneTest.MINUTES_59)), FastTimeZone.getGmtTimeZone("2359").getRawOffset());
        Assertions.assertEquals(((FastTimeZoneTest.HOURS_2) + (FastTimeZoneTest.MINUTES_5)), FastTimeZone.getGmtTimeZone("0205").getRawOffset());
    }
}

