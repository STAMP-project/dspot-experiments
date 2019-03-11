/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.commonutil;


import java.time.OffsetDateTime;
import org.junit.Assert;
import org.junit.Test;


public class CommonTimeUtilTest {
    @Test
    public void shouldCorrectlyPlaceTimestampsIntoBuckets() {
        final OffsetDateTime offsetDateTime = OffsetDateTime.parse("2000-01-01T12:34:56.789Z");
        final long time = offsetDateTime.toInstant().toEpochMilli();
        final OffsetDateTime yearOffsetDateTime = OffsetDateTime.parse("2000-02-03T12:34:56.789Z");
        final long yearTime = yearOffsetDateTime.toInstant().toEpochMilli();
        Assert.assertEquals(OffsetDateTime.parse("2000-01-01T12:34:56.789Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(time, TimeBucket.MILLISECOND));
        Assert.assertEquals(OffsetDateTime.parse("2000-01-01T12:34:56.000Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(time, TimeBucket.SECOND));
        Assert.assertEquals(OffsetDateTime.parse("2000-01-01T12:34:00.000Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(time, TimeBucket.MINUTE));
        Assert.assertEquals(OffsetDateTime.parse("2000-01-01T12:00:00.000Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(time, TimeBucket.HOUR));
        Assert.assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00.000Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(time, TimeBucket.DAY));
        Assert.assertEquals(OffsetDateTime.parse("1999-12-27T00:00:00.000Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(time, TimeBucket.WEEK));
        Assert.assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00.000Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(time, TimeBucket.MONTH));
        Assert.assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00.000Z").toInstant().toEpochMilli(), CommonTimeUtil.timeToBucket(yearTime, TimeBucket.YEAR));
    }
}

