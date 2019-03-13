/**
 * Copyright 2016 Naver Corp.
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
package com.navercorp.pinpoint.web.mapper.stat;


import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.AgentStatSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.util.TimeWindowSampler;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.stat.SampledAgentStatDataPoint;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author HyunGil Jeong
 */
@RunWith(MockitoJUnitRunner.class)
public class SampledAgentStatResultExtractorTest {
    private static final long DEFAULT_TIME_INTERVAL = 5 * 1000L;

    private static final TimeWindowSampler ONE_TO_ONE_SAMPLER = new TimeWindowSampler() {
        @Override
        public long getWindowSize(Range range) {
            return SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL;
        }
    };

    private static final TimeWindowSampler TWO_TO_ONE_SAMPLER = new TimeWindowSampler() {
        @Override
        public long getWindowSize(Range range) {
            return (SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL) * 2;
        }
    };

    private static final TimeWindowSampler TEN_TO_ONE_SAMPLER = new TimeWindowSampler() {
        @Override
        public long getWindowSize(Range range) {
            return (SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL) * 10;
        }
    };

    @Mock
    private ResultScanner resultScanner;

    @Mock
    private Result result;

    @Mock
    private AgentStatMapperV2<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> rowMapper;

    @Test
    public void one_to_one_sampler_should_not_down_sample_data_points() throws Exception {
        // Given
        final int numValues = 10;
        final long initialTimestamp = System.currentTimeMillis();
        final long finalTimestamp = initialTimestamp + ((SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL) * numValues);
        final TimeWindow timeWindow = new TimeWindow(new Range(initialTimestamp, finalTimestamp), SampledAgentStatResultExtractorTest.ONE_TO_ONE_SAMPLER);
        final List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> dataPoints = createDataPoints(finalTimestamp, SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL, numValues);
        final Map<Long, List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint>> expectedDataPointSlotMap = getExpectedDataPointSlotMap(timeWindow, dataPoints);
        Mockito.when(this.rowMapper.mapRow(this.result, 0)).thenReturn(dataPoints);
        SampledAgentStatResultExtractorTest.TestAgentStatSampler testAgentStatSampler = new SampledAgentStatResultExtractorTest.TestAgentStatSampler();
        SampledAgentStatResultExtractor<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint, SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint> resultExtractor = new SampledAgentStatResultExtractor(timeWindow, this.rowMapper, testAgentStatSampler);
        // When
        List<SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint> sampledDataPoints = resultExtractor.extractData(this.resultScanner);
        // Then
        for (SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint sampledDataPoint : sampledDataPoints) {
            List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> expectedSampledDataPoints = expectedDataPointSlotMap.get(sampledDataPoint.getBaseTimestamp());
            Assert.assertEquals(expectedSampledDataPoints, sampledDataPoint.getDataPointsToSample());
        }
    }

    @Test
    public void two_to_one_sample_should_down_sample_correctly() throws Exception {
        // Given
        final int numValues = 20;
        final long initialTimestamp = System.currentTimeMillis();
        final long finalTimestamp = initialTimestamp + ((SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL) * numValues);
        final TimeWindow timeWindow = new TimeWindow(new Range(initialTimestamp, finalTimestamp), SampledAgentStatResultExtractorTest.TWO_TO_ONE_SAMPLER);
        final List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> dataPoints = createDataPoints(finalTimestamp, SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL, numValues);
        final Map<Long, List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint>> expectedDataPointSlotMap = getExpectedDataPointSlotMap(timeWindow, dataPoints);
        Mockito.when(this.rowMapper.mapRow(this.result, 0)).thenReturn(dataPoints);
        SampledAgentStatResultExtractorTest.TestAgentStatSampler testAgentStatSampler = new SampledAgentStatResultExtractorTest.TestAgentStatSampler();
        SampledAgentStatResultExtractor<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint, SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint> resultExtractor = new SampledAgentStatResultExtractor(timeWindow, this.rowMapper, testAgentStatSampler);
        // When
        List<SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint> sampledDataPoints = resultExtractor.extractData(this.resultScanner);
        // Then
        for (SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint sampledDataPoint : sampledDataPoints) {
            List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> expectedSampledDataPoints = expectedDataPointSlotMap.get(sampledDataPoint.getBaseTimestamp());
            Assert.assertEquals(expectedSampledDataPoints, sampledDataPoint.getDataPointsToSample());
        }
    }

    @Test
    public void ten_to_one_sample_should_down_sample_correctly() throws Exception {
        // Given
        final int numValues = 100;
        final long initialTimestamp = System.currentTimeMillis();
        final long finalTimestamp = initialTimestamp + ((SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL) * numValues);
        final TimeWindow timeWindow = new TimeWindow(new Range(initialTimestamp, finalTimestamp), SampledAgentStatResultExtractorTest.TEN_TO_ONE_SAMPLER);
        final List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> dataPoints = createDataPoints(finalTimestamp, SampledAgentStatResultExtractorTest.DEFAULT_TIME_INTERVAL, numValues);
        final Map<Long, List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint>> expectedDataPointSlotMap = getExpectedDataPointSlotMap(timeWindow, dataPoints);
        Mockito.when(this.rowMapper.mapRow(this.result, 0)).thenReturn(dataPoints);
        SampledAgentStatResultExtractorTest.TestAgentStatSampler testAgentStatSampler = new SampledAgentStatResultExtractorTest.TestAgentStatSampler();
        SampledAgentStatResultExtractor<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint, SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint> resultExtractor = new SampledAgentStatResultExtractor(timeWindow, this.rowMapper, testAgentStatSampler);
        // When
        List<SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint> sampledDataPoints = resultExtractor.extractData(this.resultScanner);
        // Then
        for (SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint sampledDataPoint : sampledDataPoints) {
            List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> expectedSampledDataPoints = expectedDataPointSlotMap.get(sampledDataPoint.getBaseTimestamp());
            Assert.assertEquals(expectedSampledDataPoints, sampledDataPoint.getDataPointsToSample());
        }
    }

    private static class TestAgentStatSampler implements AgentStatSampler<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint, SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint> {
        @Override
        public SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint sampleDataPoints(int timeWindowIndex, long timestamp, List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> dataPoints, SampledAgentStatResultExtractorTest.TestAgentStatDataPoint previousDataPoint) {
            return new SampledAgentStatResultExtractorTest.TestSampledAgentStatDataPoint(timestamp, dataPoints);
        }
    }

    private static class TestAgentStatDataPoint implements AgentStatDataPoint {
        private String agentId;

        private long startTimestamp;

        private long timestamp;

        private int value;

        @Override
        public String getAgentId() {
            return agentId;
        }

        @Override
        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        @Override
        public long getStartTimestamp() {
            return startTimestamp;
        }

        @Override
        public void setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public AgentStatType getAgentStatType() {
            return AgentStatType.UNKNOWN;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return ((((((((("TestAgentStatDataPoint{" + "agentId='") + (agentId)) + '\'') + ", startTimestamp=") + (startTimestamp)) + ", timestamp=") + (timestamp)) + ", value=") + (value)) + '}';
        }
    }

    private static class TestSampledAgentStatDataPoint implements SampledAgentStatDataPoint {
        private final long baseTimestamp;

        private final List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> dataPointsToSample;

        private TestSampledAgentStatDataPoint(long baseTimestamp, List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> dataPointsToSample) {
            this.baseTimestamp = baseTimestamp;
            this.dataPointsToSample = dataPointsToSample;
        }

        public long getBaseTimestamp() {
            return baseTimestamp;
        }

        public List<SampledAgentStatResultExtractorTest.TestAgentStatDataPoint> getDataPointsToSample() {
            return dataPointsToSample;
        }

        @Override
        public String toString() {
            return (((("TestSampledAgentStatDataPoint{" + "baseTimestamp=") + (baseTimestamp)) + ", dataPointsToSample=") + (dataPointsToSample)) + '}';
        }
    }
}

