/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import org.junit.Assert;
import org.junit.Test;


public class TimelineSampleTest {
    private static final Long ELAPSED_MS = 1001L;

    private static final Long ACTIVE_UNITS = 500L;

    private static final Long COMPLETED_UNITS = 843L;

    private static final Long PENDING_UNITS = 14L;

    private static final Long SLOT_MILLIS = 1220012L;

    private static final TimelineSample TIMELINE_SAMPLE = TimelineSample.newBuilder().setElapsedMs(TimelineSampleTest.ELAPSED_MS).setActiveUnits(TimelineSampleTest.ACTIVE_UNITS).setCompletedUnits(TimelineSampleTest.COMPLETED_UNITS).setPendingUnits(TimelineSampleTest.PENDING_UNITS).setSlotMillis(TimelineSampleTest.SLOT_MILLIS).build();

    @Test
    public void testTimelineSampleBuilder() {
        Assert.assertEquals(TimelineSampleTest.ELAPSED_MS, TimelineSampleTest.TIMELINE_SAMPLE.getElapsedMs());
        Assert.assertEquals(TimelineSampleTest.ACTIVE_UNITS, TimelineSampleTest.TIMELINE_SAMPLE.getActiveUnits());
        Assert.assertEquals(TimelineSampleTest.COMPLETED_UNITS, TimelineSampleTest.TIMELINE_SAMPLE.getCompletedUnits());
        Assert.assertEquals(TimelineSampleTest.PENDING_UNITS, TimelineSampleTest.TIMELINE_SAMPLE.getPendingUnits());
        Assert.assertEquals(TimelineSampleTest.SLOT_MILLIS, TimelineSampleTest.TIMELINE_SAMPLE.getSlotMillis());
    }

    @Test
    public void TestEquals() {
        Assert.assertEquals(TimelineSampleTest.TIMELINE_SAMPLE, TimelineSampleTest.TIMELINE_SAMPLE);
    }
}

