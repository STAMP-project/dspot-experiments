/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.transforms.windowing;


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import org.apache.beam.sdk.transforms.windowing.Trigger.OnceTrigger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the {@link AfterProcessingTime}.
 */
@RunWith(JUnit4.class)
public class AfterProcessingTimeTest {
    @Test
    public void testFireDeadline() throws Exception {
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, AfterProcessingTime.pastFirstElementInPane().getWatermarkThatGuaranteesFiring(new IntervalWindow(new Instant(0), new Instant(10))));
    }

    @Test
    public void testContinuation() throws Exception {
        OnceTrigger firstElementPlus1 = AfterProcessingTime.pastFirstElementInPane().plusDelayOf(Duration.standardHours(1));
        Assert.assertEquals(AfterSynchronizedProcessingTime.ofFirstElement(), firstElementPlus1.getContinuationTrigger());
    }

    /**
     * Basic test of compatibility check between identical triggers.
     */
    @Test
    public void testCompatibilityIdentical() throws Exception {
        Trigger t1 = AfterProcessingTime.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(1L));
        Trigger t2 = AfterProcessingTime.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(1L));
        Assert.assertTrue(t1.isCompatible(t2));
    }

    @Test
    public void testToString() {
        Trigger trigger = AfterProcessingTime.pastFirstElementInPane();
        Assert.assertEquals("AfterProcessingTime.pastFirstElementInPane()", trigger.toString());
    }

    @Test
    public void testWithDelayToString() {
        Trigger trigger = AfterProcessingTime.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(5));
        Assert.assertEquals("AfterProcessingTime.pastFirstElementInPane().plusDelayOf(5 minutes)", trigger.toString());
    }

    @Test
    public void testBuiltUpToString() {
        Trigger trigger = AfterWatermark.pastEndOfWindow().withLateFirings(AfterProcessingTime.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(10)));
        String expected = "AfterWatermark.pastEndOfWindow()" + ((".withLateFirings(AfterProcessingTime" + ".pastFirstElementInPane()") + ".plusDelayOf(10 minutes))");
        Assert.assertEquals(expected, trigger.toString());
    }
}

