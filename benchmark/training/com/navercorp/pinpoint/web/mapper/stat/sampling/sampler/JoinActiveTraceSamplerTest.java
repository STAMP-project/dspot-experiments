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


import JoinActiveTraceBo.EMPTY_JOIN_ACTIVE_TRACE_BO;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinActiveTraceBo;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinActiveTraceBo;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinActiveTraceSamplerTest {
    @Test
    public void sampleDataPointsTest() {
        long currentTime = 1487149800000L;
        JoinActiveTraceSampler sampler = new JoinActiveTraceSampler();
        List<JoinActiveTraceBo> joinActiveTraceBoList = createJoinActiveTraceBoList(currentTime);
        AggreJoinActiveTraceBo aggreJoinActiveTraceBo = sampler.sampleDataPoints(1, currentTime, joinActiveTraceBoList, EMPTY_JOIN_ACTIVE_TRACE_BO);
        Assert.assertEquals(aggreJoinActiveTraceBo.getId(), "test_app");
        Assert.assertEquals(aggreJoinActiveTraceBo.getHistogramSchemaType(), 1);
        Assert.assertEquals(aggreJoinActiveTraceBo.getVersion(), 2);
        Assert.assertEquals(aggreJoinActiveTraceBo.getTotalCount(), 130);
        Assert.assertEquals(aggreJoinActiveTraceBo.getMinTotalCount(), 10);
        Assert.assertEquals(aggreJoinActiveTraceBo.getMinTotalCountAgentId(), "app_1_1");
        Assert.assertEquals(aggreJoinActiveTraceBo.getMaxTotalCount(), 560);
        Assert.assertEquals(aggreJoinActiveTraceBo.getMaxTotalCountAgentId(), "app_4_2");
        Assert.assertEquals(aggreJoinActiveTraceBo.getTimestamp(), 1487149800000L);
    }
}

