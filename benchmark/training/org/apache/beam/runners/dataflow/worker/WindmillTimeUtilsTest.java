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
package org.apache.beam.runners.dataflow.worker;


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link WindmillTimeUtils}.
 */
@RunWith(JUnit4.class)
public class WindmillTimeUtilsTest {
    @Test
    public void testWindmillToHarnessWatermark() {
        Assert.assertEquals(null, WindmillTimeUtils.windmillToHarnessWatermark(Long.MIN_VALUE));
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, WindmillTimeUtils.windmillToHarnessWatermark(Long.MAX_VALUE));
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, WindmillTimeUtils.windmillToHarnessWatermark(((Long.MAX_VALUE) - 17)));
        Assert.assertEquals(new Instant(16), WindmillTimeUtils.windmillToHarnessWatermark(16999));
        Assert.assertEquals(new Instant(17), WindmillTimeUtils.windmillToHarnessWatermark(17120));
        Assert.assertEquals(new Instant(17), WindmillTimeUtils.windmillToHarnessWatermark(17000));
        Assert.assertEquals(new Instant((-17)), WindmillTimeUtils.windmillToHarnessWatermark((-16987)));
        Assert.assertEquals(new Instant((-17)), WindmillTimeUtils.windmillToHarnessWatermark((-17000)));
        Assert.assertEquals(new Instant((-18)), WindmillTimeUtils.windmillToHarnessTimestamp((-17001)));
    }

    @Test
    public void testWindmillToHarnessTimestamp() {
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, WindmillTimeUtils.windmillToHarnessTimestamp(Long.MAX_VALUE));
        Assert.assertEquals(TIMESTAMP_MAX_VALUE, WindmillTimeUtils.windmillToHarnessTimestamp(((Long.MAX_VALUE) - 17)));
        Assert.assertEquals(new Instant(16), WindmillTimeUtils.windmillToHarnessWatermark(16999));
        Assert.assertEquals(new Instant(17), WindmillTimeUtils.windmillToHarnessTimestamp(17120));
        Assert.assertEquals(new Instant(17), WindmillTimeUtils.windmillToHarnessTimestamp(17000));
        Assert.assertEquals(new Instant((-17)), WindmillTimeUtils.windmillToHarnessTimestamp((-16987)));
        Assert.assertEquals(new Instant((-17)), WindmillTimeUtils.windmillToHarnessTimestamp((-17000)));
        Assert.assertEquals(new Instant((-18)), WindmillTimeUtils.windmillToHarnessTimestamp((-17001)));
    }

    @Test
    public void testHarnessToWindmillTimestamp() {
        Assert.assertEquals(Long.MAX_VALUE, WindmillTimeUtils.harnessToWindmillTimestamp(TIMESTAMP_MAX_VALUE));
        Assert.assertEquals((-1000), WindmillTimeUtils.harnessToWindmillTimestamp(new Instant((-1))));
        Assert.assertEquals(1000, WindmillTimeUtils.harnessToWindmillTimestamp(new Instant(1)));
        Assert.assertEquals(((Long.MIN_VALUE) + 1), WindmillTimeUtils.harnessToWindmillTimestamp(new Instant(Long.MIN_VALUE)));
        Assert.assertEquals(((Long.MIN_VALUE) + 1), WindmillTimeUtils.harnessToWindmillTimestamp(new Instant((((Long.MIN_VALUE) / 1000) - 1))));
    }
}

