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


import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.DeadlockSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.stat.SampledDeadlock;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChartGroup;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class DeadlockChartGroupTest {
    private static final int RANDOM_LIST_MAX_SIZE = 11;// Random API's upper bound field is exclusive


    private static final int RANDOM_MAX_DEADLOCKED_SIZE = 301;// Random API's upper bound field is exclusive


    private final DeadlockSampler sampler = new DeadlockSampler();

    @Test
    public void basicFunctionTest1() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        TimeWindow timeWindow = new TimeWindow(new Range((currentTimeMillis - 300000), currentTimeMillis));
        List<SampledDeadlock> sampledDeadlockList = createSampledResponseTimeList(timeWindow);
        StatChartGroup deadlockChartGroup = new DeadlockChart.DeadlockChartGroup(timeWindow, sampledDeadlockList);
        assertEquals(sampledDeadlockList, deadlockChartGroup);
    }
}

