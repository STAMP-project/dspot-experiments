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
package org.apache.flink.dropwizard.metrics;


import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.util.TestMeter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for the FlinkMeterWrapper.
 */
public class FlinkMeterWrapperTest {
    private static final double DELTA = 1.0E-4;

    @Test
    public void testWrapper() {
        Meter meter = new TestMeter();
        FlinkMeterWrapper wrapper = new FlinkMeterWrapper(meter);
        Assert.assertEquals(0, wrapper.getMeanRate(), FlinkMeterWrapperTest.DELTA);
        Assert.assertEquals(5, wrapper.getOneMinuteRate(), FlinkMeterWrapperTest.DELTA);
        Assert.assertEquals(0, wrapper.getFiveMinuteRate(), FlinkMeterWrapperTest.DELTA);
        Assert.assertEquals(0, wrapper.getFifteenMinuteRate(), FlinkMeterWrapperTest.DELTA);
        Assert.assertEquals(100L, wrapper.getCount());
    }

    @Test
    public void testMarkOneEvent() {
        Meter meter = Mockito.mock(Meter.class);
        FlinkMeterWrapper wrapper = new FlinkMeterWrapper(meter);
        wrapper.mark();
        Mockito.verify(meter).markEvent();
    }

    @Test
    public void testMarkSeveralEvents() {
        Meter meter = Mockito.mock(Meter.class);
        FlinkMeterWrapper wrapper = new FlinkMeterWrapper(meter);
        wrapper.mark(5);
        Mockito.verify(meter).markEvent(5);
    }
}

