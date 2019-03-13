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
package org.apache.beam.runners.dataflow.util;


import Duration.ZERO;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link TimeUtil}.
 */
@RunWith(JUnit4.class)
public final class TimeUtilTest {
    @Test
    public void toCloudTimeShouldPrintTimeStrings() {
        Assert.assertEquals("1970-01-01T00:00:00Z", TimeUtil.toCloudTime(new Instant(0)));
        Assert.assertEquals("1970-01-01T00:00:00.001Z", TimeUtil.toCloudTime(new Instant(1)));
    }

    @Test
    public void fromCloudTimeShouldParseTimeStrings() {
        Assert.assertEquals(new Instant(0), TimeUtil.fromCloudTime("1970-01-01T00:00:00Z"));
        Assert.assertEquals(new Instant(1), TimeUtil.fromCloudTime("1970-01-01T00:00:00.001Z"));
        Assert.assertEquals(new Instant(1), TimeUtil.fromCloudTime("1970-01-01T00:00:00.001000Z"));
        Assert.assertEquals(new Instant(1), TimeUtil.fromCloudTime("1970-01-01T00:00:00.001001Z"));
        Assert.assertEquals(new Instant(1), TimeUtil.fromCloudTime("1970-01-01T00:00:00.001000000Z"));
        Assert.assertEquals(new Instant(1), TimeUtil.fromCloudTime("1970-01-01T00:00:00.001000001Z"));
        Assert.assertEquals(new Instant(0), TimeUtil.fromCloudTime("1970-01-01T00:00:00.0Z"));
        Assert.assertEquals(new Instant(0), TimeUtil.fromCloudTime("1970-01-01T00:00:00.00Z"));
        Assert.assertEquals(new Instant(420), TimeUtil.fromCloudTime("1970-01-01T00:00:00.42Z"));
        Assert.assertEquals(new Instant(300), TimeUtil.fromCloudTime("1970-01-01T00:00:00.3Z"));
        Assert.assertEquals(new Instant(20), TimeUtil.fromCloudTime("1970-01-01T00:00:00.02Z"));
        Assert.assertNull(TimeUtil.fromCloudTime(""));
        Assert.assertNull(TimeUtil.fromCloudTime("1970-01-01T00:00:00"));
        Assert.assertNull(TimeUtil.fromCloudTime("1970-01-01T00:00:00.1e3Z"));
    }

    @Test
    public void toCloudDurationShouldPrintDurationStrings() {
        Assert.assertEquals("0s", TimeUtil.toCloudDuration(ZERO));
        Assert.assertEquals("4s", TimeUtil.toCloudDuration(Duration.millis(4000)));
        Assert.assertEquals("4.001s", TimeUtil.toCloudDuration(Duration.millis(4001)));
    }

    @Test
    public void fromCloudDurationShouldParseDurationStrings() {
        Assert.assertEquals(Duration.millis(4000), TimeUtil.fromCloudDuration("4s"));
        Assert.assertEquals(Duration.millis(4001), TimeUtil.fromCloudDuration("4.001s"));
        Assert.assertEquals(Duration.millis(4001), TimeUtil.fromCloudDuration("4.001000s"));
        Assert.assertEquals(Duration.millis(4001), TimeUtil.fromCloudDuration("4.001001s"));
        Assert.assertEquals(Duration.millis(4001), TimeUtil.fromCloudDuration("4.001000000s"));
        Assert.assertEquals(Duration.millis(4001), TimeUtil.fromCloudDuration("4.001000001s"));
        Assert.assertNull(TimeUtil.fromCloudDuration(""));
        Assert.assertNull(TimeUtil.fromCloudDuration("4"));
        Assert.assertNull(TimeUtil.fromCloudDuration("4.1"));
        Assert.assertNull(TimeUtil.fromCloudDuration("4.1s"));
    }
}

