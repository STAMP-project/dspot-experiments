/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.extraction;


import Granularities.DAY;
import Granularities.NONE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.junit.Assert;
import org.junit.Test;


public class TimeFormatExtractionFnTest {
    private static final long[] timestamps = new long[]{ DateTimes.of("2015-01-01T23:00:00Z").getMillis(), DateTimes.of("2015-01-02T23:00:00Z").getMillis(), DateTimes.of("2015-03-03T23:00:00Z").getMillis(), DateTimes.of("2015-03-04T23:00:00Z").getMillis(), DateTimes.of("2015-05-02T23:00:00Z").getMillis(), DateTimes.of("2015-12-21T23:00:00Z").getMillis() };

    @Test
    public void testDayOfWeekExtraction() throws Exception {
        TimeFormatExtractionFn fn = new TimeFormatExtractionFn("EEEE", null, null, null, false);
        Assert.assertEquals("Thursday", fn.apply(TimeFormatExtractionFnTest.timestamps[0]));
        Assert.assertEquals("Friday", fn.apply(TimeFormatExtractionFnTest.timestamps[1]));
        Assert.assertEquals("Tuesday", fn.apply(TimeFormatExtractionFnTest.timestamps[2]));
        Assert.assertEquals("Wednesday", fn.apply(TimeFormatExtractionFnTest.timestamps[3]));
        Assert.assertEquals("Saturday", fn.apply(TimeFormatExtractionFnTest.timestamps[4]));
        Assert.assertEquals("Monday", fn.apply(TimeFormatExtractionFnTest.timestamps[5]));
        testSerde(fn, "EEEE", null, null, NONE);
    }

    @Test
    public void testLocalizedExtraction() throws Exception {
        TimeFormatExtractionFn fn = new TimeFormatExtractionFn("EEEE", null, "is", null, false);
        Assert.assertEquals("fimmtudagur", fn.apply(TimeFormatExtractionFnTest.timestamps[0]));
        Assert.assertEquals("f?studagur", fn.apply(TimeFormatExtractionFnTest.timestamps[1]));
        Assert.assertEquals("?ri?judagur", fn.apply(TimeFormatExtractionFnTest.timestamps[2]));
        Assert.assertEquals("mi?vikudagur", fn.apply(TimeFormatExtractionFnTest.timestamps[3]));
        Assert.assertEquals("laugardagur", fn.apply(TimeFormatExtractionFnTest.timestamps[4]));
        Assert.assertEquals("m?nudagur", fn.apply(TimeFormatExtractionFnTest.timestamps[5]));
        testSerde(fn, "EEEE", null, "is", NONE);
    }

    @Test
    public void testGranularExtractionWithNullPattern() throws Exception {
        TimeFormatExtractionFn fn = new TimeFormatExtractionFn(null, null, null, Granularities.DAY, false);
        Assert.assertEquals("2015-01-01T00:00:00.000Z", fn.apply(TimeFormatExtractionFnTest.timestamps[0]));
        Assert.assertEquals("2015-01-02T00:00:00.000Z", fn.apply(TimeFormatExtractionFnTest.timestamps[1]));
        Assert.assertEquals("2015-03-03T00:00:00.000Z", fn.apply(TimeFormatExtractionFnTest.timestamps[2]));
        Assert.assertEquals("2015-03-04T00:00:00.000Z", fn.apply(TimeFormatExtractionFnTest.timestamps[3]));
        Assert.assertEquals("2015-05-02T00:00:00.000Z", fn.apply(TimeFormatExtractionFnTest.timestamps[4]));
        Assert.assertEquals("2015-12-21T00:00:00.000Z", fn.apply(TimeFormatExtractionFnTest.timestamps[5]));
        testSerde(fn, null, null, null, DAY);
    }

    @Test
    public void testTimeZoneExtraction() throws Exception {
        TimeFormatExtractionFn fn = new TimeFormatExtractionFn("'In Berlin ist es schon 'EEEE", DateTimes.inferTzFromString("Europe/Berlin"), "de", null, false);
        Assert.assertEquals("In Berlin ist es schon Freitag", fn.apply(TimeFormatExtractionFnTest.timestamps[0]));
        Assert.assertEquals("In Berlin ist es schon Samstag", fn.apply(TimeFormatExtractionFnTest.timestamps[1]));
        Assert.assertEquals("In Berlin ist es schon Mittwoch", fn.apply(TimeFormatExtractionFnTest.timestamps[2]));
        Assert.assertEquals("In Berlin ist es schon Donnerstag", fn.apply(TimeFormatExtractionFnTest.timestamps[3]));
        Assert.assertEquals("In Berlin ist es schon Sonntag", fn.apply(TimeFormatExtractionFnTest.timestamps[4]));
        Assert.assertEquals("In Berlin ist es schon Dienstag", fn.apply(TimeFormatExtractionFnTest.timestamps[5]));
        testSerde(fn, "'In Berlin ist es schon 'EEEE", DateTimes.inferTzFromString("Europe/Berlin"), "de", NONE);
    }

    @Test
    public void testSerdeFromJson() throws Exception {
        final ObjectMapper objectMapper = new DefaultObjectMapper();
        final String json = "{ \"type\" : \"timeFormat\", \"format\" : \"HH\" }";
        TimeFormatExtractionFn extractionFn = ((TimeFormatExtractionFn) (objectMapper.readValue(json, ExtractionFn.class)));
        Assert.assertEquals("HH", extractionFn.getFormat());
        Assert.assertEquals(null, extractionFn.getLocale());
        Assert.assertEquals(null, extractionFn.getTimeZone());
        // round trip
        Assert.assertEquals(extractionFn, objectMapper.readValue(objectMapper.writeValueAsBytes(extractionFn), ExtractionFn.class));
    }

    @Test
    public void testCacheKey() {
        TimeFormatExtractionFn fn = new TimeFormatExtractionFn("'In Berlin ist es schon 'EEEE", DateTimes.inferTzFromString("Europe/Berlin"), "de", null, false);
        TimeFormatExtractionFn fn2 = new TimeFormatExtractionFn("'In Berlin ist es schon 'EEEE", DateTimes.inferTzFromString("Europe/Berlin"), "de", null, true);
        TimeFormatExtractionFn fn3 = new TimeFormatExtractionFn("'In Berlin ist es schon 'EEEE", DateTimes.inferTzFromString("Europe/Berlin"), "de", null, true);
        TimeFormatExtractionFn fn4 = new TimeFormatExtractionFn(null, null, null, null, false);
        Assert.assertFalse(Arrays.equals(fn.getCacheKey(), fn2.getCacheKey()));
        Assert.assertFalse(Arrays.equals(fn.getCacheKey(), fn4.getCacheKey()));
        Assert.assertTrue(Arrays.equals(fn2.getCacheKey(), fn3.getCacheKey()));
    }
}

