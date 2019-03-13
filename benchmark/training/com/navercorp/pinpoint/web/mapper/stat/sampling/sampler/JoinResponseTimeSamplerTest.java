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
package com.navercorp.pinpoint.web.mapper.stat.sampling.sampler;


import JoinResponseTimeBo.EMPTY_JOIN_RESPONSE_TIME_BO;
import JoinResponseTimeBo.UNCOLLECTED_VALUE;
import JoinResponseTimeBo.UNKNOWN_AGENT;
import JoinResponseTimeBo.UNKNOWN_ID;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinResponseTimeBo;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinResponseTimeBo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinResponseTimeSamplerTest {
    @Test
    public void sampleDataPointsTest() {
        long currentTime = 1487149800000L;
        JoinResponseTimeSampler joinResponseTimeSampler = new JoinResponseTimeSampler();
        List<JoinResponseTimeBo> joinResponseTimeBoList = createJoinResponseTimeList(currentTime);
        AggreJoinResponseTimeBo aggreJoinResponseTimeBo = joinResponseTimeSampler.sampleDataPoints(1, currentTime, joinResponseTimeBoList, EMPTY_JOIN_RESPONSE_TIME_BO);
        Assert.assertEquals(aggreJoinResponseTimeBo.getId(), "test_app");
        Assert.assertEquals(aggreJoinResponseTimeBo.getTimestamp(), 1487149800000L);
        Assert.assertEquals(aggreJoinResponseTimeBo.getAvg(), 3000);
        Assert.assertEquals(2, aggreJoinResponseTimeBo.getMinAvg());
        Assert.assertEquals("app_1_1", aggreJoinResponseTimeBo.getMinAvgAgentId());
        Assert.assertEquals(9000, aggreJoinResponseTimeBo.getMaxAvg());
        Assert.assertEquals("app_2_1", aggreJoinResponseTimeBo.getMaxAvgAgentId());
    }

    @Test
    public void sampleDataPoints2Test() {
        long currentTime = 1487149800000L;
        JoinResponseTimeSampler joinResponseTimeSampler = new JoinResponseTimeSampler();
        List<JoinResponseTimeBo> joinResponseTimeBoList = new ArrayList<JoinResponseTimeBo>();
        AggreJoinResponseTimeBo aggreJoinResponseTimeBo = joinResponseTimeSampler.sampleDataPoints(1, currentTime, joinResponseTimeBoList, EMPTY_JOIN_RESPONSE_TIME_BO);
        Assert.assertEquals(aggreJoinResponseTimeBo.getId(), UNKNOWN_ID);
        Assert.assertEquals(aggreJoinResponseTimeBo.getTimestamp(), 1487149800000L);
        Assert.assertEquals(aggreJoinResponseTimeBo.getAvg(), UNCOLLECTED_VALUE);
        Assert.assertEquals(UNCOLLECTED_VALUE, aggreJoinResponseTimeBo.getMinAvg());
        Assert.assertEquals(UNKNOWN_AGENT, aggreJoinResponseTimeBo.getMinAvgAgentId());
        Assert.assertEquals(UNCOLLECTED_VALUE, aggreJoinResponseTimeBo.getMaxAvg());
        Assert.assertEquals(UNKNOWN_AGENT, aggreJoinResponseTimeBo.getMaxAvgAgentId());
    }
}

