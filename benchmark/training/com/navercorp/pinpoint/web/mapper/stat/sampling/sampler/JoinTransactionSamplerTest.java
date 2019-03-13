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


import JoinTransactionBo.EMPTY_JOIN_TRANSACTION_BO;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinTransactionBo;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinTransactionBo;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinTransactionSamplerTest {
    @Test
    public void sampleDataPointsTest() {
        long currentTime = 1487149800000L;
        JoinTransactionSampler joinTransactionSampler = new JoinTransactionSampler();
        List<JoinTransactionBo> joinTransactionBoList = createJoinTransactionBoList(currentTime);
        AggreJoinTransactionBo aggreJoinTransactionBo = joinTransactionSampler.sampleDataPoints(1, currentTime, joinTransactionBoList, EMPTY_JOIN_TRANSACTION_BO);
        Assert.assertEquals(aggreJoinTransactionBo.getId(), "test_app");
        Assert.assertEquals(aggreJoinTransactionBo.getCollectInterval(), 5000);
        Assert.assertEquals(aggreJoinTransactionBo.getTotalCount(), 130);
        Assert.assertEquals(aggreJoinTransactionBo.getMinTotalCount(), 10);
        Assert.assertEquals(aggreJoinTransactionBo.getMinTotalCountAgentId(), "app_1_1");
        Assert.assertEquals(aggreJoinTransactionBo.getMaxTotalCount(), 560);
        Assert.assertEquals(aggreJoinTransactionBo.getMaxTotalCountAgentId(), "app_4_2");
        Assert.assertEquals(aggreJoinTransactionBo.getTimestamp(), 1487149800000L);
    }
}

