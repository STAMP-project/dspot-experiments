/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.web.vo.chart;


import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.util.TimeWindowSampler;
import com.navercorp.pinpoint.web.vo.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class TimeSeriesChartBuilderTest {
    private static final int TIME_WINDOW_SIZE = 10;

    private static final TimeWindowSampler TIME_WINDOW_SAMPLER = new TimeWindowSampler() {
        @Override
        public long getWindowSize(Range range) {
            return TimeSeriesChartBuilderTest.TIME_WINDOW_SIZE;
        }
    };

    private static class TestPoint implements Point {
        private static final int UNCOLLECTED_VALUE = -1;

        private static final UncollectedPointCreator<TimeSeriesChartBuilderTest.TestPoint> UNCOLLECTED_POINT_CREATOR = new UncollectedPointCreator<TimeSeriesChartBuilderTest.TestPoint>() {
            @Override
            public TimeSeriesChartBuilderTest.TestPoint createUnCollectedPoint(long xVal) {
                return new TimeSeriesChartBuilderTest.TestPoint(xVal, TimeSeriesChartBuilderTest.TestPoint.UNCOLLECTED_VALUE);
            }
        };

        private final long xVal;

        private final int yVal;

        private TestPoint(long xVal, int yVal) {
            this.xVal = xVal;
            this.yVal = yVal;
        }

        @Override
        public long getXVal() {
            return xVal;
        }

        public int getYVal() {
            return yVal;
        }
    }

    @Test
    public void empty() {
        // Given
        int numSlots = 10;
        TimeWindow timeWindow = new TimeWindow(new Range(0, ((TimeSeriesChartBuilderTest.TIME_WINDOW_SIZE) * numSlots)), TimeSeriesChartBuilderTest.TIME_WINDOW_SAMPLER);
        TimeSeriesChartBuilder<TimeSeriesChartBuilderTest.TestPoint> builder = new TimeSeriesChartBuilder(timeWindow, TimeSeriesChartBuilderTest.TestPoint.UNCOLLECTED_POINT_CREATOR);
        List<TimeSeriesChartBuilderTest.TestPoint> points = Collections.emptyList();
        // When
        Chart<TimeSeriesChartBuilderTest.TestPoint> chart = builder.build(points);
        // Then
        List<TimeSeriesChartBuilderTest.TestPoint> sampledPoints = chart.getPoints();
        Assert.assertTrue(sampledPoints.isEmpty());
    }

    @Test
    public void sampled() {
        // Given
        int numSlots = 100;
        TimeWindow timeWindow = new TimeWindow(new Range(0, ((TimeSeriesChartBuilderTest.TIME_WINDOW_SIZE) * numSlots)), TimeSeriesChartBuilderTest.TIME_WINDOW_SAMPLER);
        TimeSeriesChartBuilder<TimeSeriesChartBuilderTest.TestPoint> builder = new TimeSeriesChartBuilder(timeWindow, TimeSeriesChartBuilderTest.TestPoint.UNCOLLECTED_POINT_CREATOR);
        List<TimeSeriesChartBuilderTest.TestPoint> points = new ArrayList<>(((TimeSeriesChartBuilderTest.TIME_WINDOW_SIZE) * numSlots));
        for (int i = 0; i <= ((TimeSeriesChartBuilderTest.TIME_WINDOW_SIZE) * numSlots); i++) {
            points.add(new TimeSeriesChartBuilderTest.TestPoint(i, (i / (TimeSeriesChartBuilderTest.TIME_WINDOW_SIZE))));
        }
        // When
        Chart<TimeSeriesChartBuilderTest.TestPoint> chart = builder.build(points);
        // Then
        List<TimeSeriesChartBuilderTest.TestPoint> sampledPoints = chart.getPoints();
        for (int i = 0; i < (sampledPoints.size()); i++) {
            Assert.assertEquals(i, sampledPoints.get(i).getYVal());
        }
    }
}

