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
package com.navercorp.pinpoint.web.vo.stat.chart.agent;


import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.ResponseTimeSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.stat.SampledResponseTime;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChartGroup;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class ResponseTimeChartGroupTest {
    private static final int MIN_VALUE_OF_MAX_CONNECTION_SIZE = 20;

    private static final int RANDOM_LIST_MAX_SIZE = 10;

    private static final int RANDOM_AVG_MAX_SIZE = 300000;

    private final ResponseTimeSampler sampler = new ResponseTimeSampler();

    @Test
    public void basicFunctionTest1() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        TimeWindow timeWindow = new TimeWindow(new Range((currentTimeMillis - 300000), currentTimeMillis));
        List<SampledResponseTime> sampledResponseTimeList = createSampledResponseTimeList(timeWindow);
        StatChartGroup responseTimeChartGroup = new ResponseTimeChart.ResponseTimeChartGroup(timeWindow, sampledResponseTimeList);
        assertEquals(sampledResponseTimeList, responseTimeChartGroup);
    }
}

