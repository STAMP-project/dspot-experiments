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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests for GmtTimeZone
 */
public class GmtTimeZoneTest {
    @Test
    public void hoursOutOfRange() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new GmtTimeZone(false, 24, 0));
    }

    @Test
    public void hoursInRange() {
        Assertions.assertEquals((((23 * 60) * 60) * 1000), new GmtTimeZone(false, 23, 0).getRawOffset());
    }

    @Test
    public void minutesOutOfRange() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new GmtTimeZone(false, 0, 60));
    }

    @Test
    public void minutesInRange() {
        Assertions.assertEquals(((59 * 60) * 1000), new GmtTimeZone(false, 0, 59).getRawOffset());
    }

    @Test
    public void getOffset() {
        Assertions.assertEquals(0, new GmtTimeZone(false, 0, 0).getOffset(234304));
    }

    @Test
    public void setRawOffset() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new GmtTimeZone(false, 0, 0).setRawOffset(0));
    }

    @Test
    public void getRawOffset() {
        Assertions.assertEquals(0, new GmtTimeZone(false, 0, 0).getRawOffset());
    }

    @Test
    public void getID() {
        Assertions.assertEquals("GMT+00:00", new GmtTimeZone(false, 0, 0).getID());
        Assertions.assertEquals("GMT+01:02", new GmtTimeZone(false, 1, 2).getID());
        Assertions.assertEquals("GMT+11:22", new GmtTimeZone(false, 11, 22).getID());
        Assertions.assertEquals("GMT-01:02", new GmtTimeZone(true, 1, 2).getID());
        Assertions.assertEquals("GMT-11:22", new GmtTimeZone(true, 11, 22).getID());
    }

    @Test
    public void useDaylightTime() {
        Assertions.assertFalse(new GmtTimeZone(false, 0, 0).useDaylightTime());
    }

    @Test
    public void inDaylightTime() {
        Assertions.assertFalse(new GmtTimeZone(false, 0, 0).useDaylightTime());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("[GmtTimeZone id=\"GMT-12:00\",offset=-43200000]", new GmtTimeZone(true, 12, 0).toString());
    }

    @Test
    public void testGetOffset() {
        Assertions.assertEquals((((-((6 * 60) + 30)) * 60) * 1000), new GmtTimeZone(true, 6, 30).getOffset(1, 1, 1, 1, 1, 1));
    }
}

