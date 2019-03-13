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


import JoinMemoryBo.EMPTY_JOIN_MEMORY_BO;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinMemoryBo;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinMemoryBo;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinMemorySamplerTest {
    public static final String ID = "test_app";

    @Test
    public void sampleDataPointsTest() throws Exception {
        long currentTime = 1487149800000L;
        List<JoinMemoryBo> joinMemoryBoList = createJoinMemoryBoList(currentTime);
        AggreJoinMemoryBo aggreJoinMemoryBo = new JoinMemorySampler().sampleDataPoints(1, currentTime, joinMemoryBoList, EMPTY_JOIN_MEMORY_BO);
        Assert.assertEquals(aggreJoinMemoryBo.getId(), JoinMemorySamplerTest.ID);
        Assert.assertEquals(aggreJoinMemoryBo.getTimestamp(), currentTime);
        Assert.assertEquals(aggreJoinMemoryBo.getHeapUsed(), 3000);
        Assert.assertEquals(aggreJoinMemoryBo.getMinHeapUsed(), 100);
        Assert.assertEquals(aggreJoinMemoryBo.getMaxHeapUsed(), 8000);
        Assert.assertEquals(aggreJoinMemoryBo.getMinHeapAgentId(), "app_4_1");
        Assert.assertEquals(aggreJoinMemoryBo.getMaxHeapAgentId(), "app_3_2");
        Assert.assertEquals(aggreJoinMemoryBo.getNonHeapUsed(), 300);
        Assert.assertEquals(aggreJoinMemoryBo.getMinNonHeapUsed(), 50);
        Assert.assertEquals(aggreJoinMemoryBo.getMaxNonHeapUsed(), 2900);
        Assert.assertEquals(aggreJoinMemoryBo.getMinNonHeapAgentId(), "app_1_3");
        Assert.assertEquals(aggreJoinMemoryBo.getMaxNonHeapAgentId(), "app_5_4");
    }
}

