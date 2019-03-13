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


import com.navercorp.pinpoint.common.server.bo.stat.join.JoinDataSourceBo;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinDataSourceListBo;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinDataSourceBo;
import com.navercorp.pinpoint.web.vo.stat.AggreJoinDataSourceListBo;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinDataSourceSamplerTest {
    @Test
    public void sampleDataPointsTest() {
        final String id = "test_app";
        JoinDataSourceSampler sampler = new JoinDataSourceSampler();
        long timestamp = new Date().getTime();
        AggreJoinDataSourceListBo aggreJoinDataSourceListBo = sampler.sampleDataPoints(0, timestamp, createJoinDataSourceListBoList(id, timestamp), new JoinDataSourceListBo());
        Assert.assertEquals(aggreJoinDataSourceListBo.getId(), id);
        Assert.assertEquals(aggreJoinDataSourceListBo.getTimestamp(), timestamp);
        List<AggreJoinDataSourceBo> joinDataSourceBoList = aggreJoinDataSourceListBo.getAggreJoinDataSourceBoList();
        joinDataSourceBoList.sort(new JoinDataSourceSamplerTest.ComparatorImpl());
        Assert.assertEquals(joinDataSourceBoList.size(), 5);
        AggreJoinDataSourceBo aggreJoinDataSourceBo1 = joinDataSourceBoList.get(0);
        Assert.assertEquals(aggreJoinDataSourceBo1.getServiceTypeCode(), 1000);
        Assert.assertEquals(aggreJoinDataSourceBo1.getUrl(), "jdbc:mysql");
        Assert.assertEquals(aggreJoinDataSourceBo1.getAvgActiveConnectionSize(), 60);
        Assert.assertEquals(aggreJoinDataSourceBo1.getMinActiveConnectionSize(), 35);
        Assert.assertEquals(aggreJoinDataSourceBo1.getMinActiveConnectionAgentId(), "agent_id_1_10");
        Assert.assertEquals(aggreJoinDataSourceBo1.getMaxActiveConnectionSize(), 110);
        Assert.assertEquals(aggreJoinDataSourceBo1.getMaxActiveConnectionAgentId(), "agent_id_6_50");
        AggreJoinDataSourceBo aggreJoinDataSourceBo2 = joinDataSourceBoList.get(1);
        Assert.assertEquals(aggreJoinDataSourceBo2.getServiceTypeCode(), 2000);
        Assert.assertEquals(aggreJoinDataSourceBo2.getUrl(), "jdbc:mssql");
        Assert.assertEquals(aggreJoinDataSourceBo2.getAvgActiveConnectionSize(), 50);
        Assert.assertEquals(aggreJoinDataSourceBo2.getMinActiveConnectionSize(), 15);
        Assert.assertEquals(aggreJoinDataSourceBo2.getMinActiveConnectionAgentId(), "agent_id_2_10");
        Assert.assertEquals(aggreJoinDataSourceBo2.getMaxActiveConnectionSize(), 80);
        Assert.assertEquals(aggreJoinDataSourceBo2.getMaxActiveConnectionAgentId(), "agent_id_7_50");
        AggreJoinDataSourceBo aggreJoinDataSourceBo3 = joinDataSourceBoList.get(2);
        Assert.assertEquals(aggreJoinDataSourceBo3.getServiceTypeCode(), 3000);
        Assert.assertEquals(aggreJoinDataSourceBo3.getUrl(), "jdbc:postgre");
        Assert.assertEquals(aggreJoinDataSourceBo3.getAvgActiveConnectionSize(), 40);
        Assert.assertEquals(aggreJoinDataSourceBo3.getMinActiveConnectionSize(), 35);
        Assert.assertEquals(aggreJoinDataSourceBo3.getMinActiveConnectionAgentId(), "agent_id_3_10");
        Assert.assertEquals(aggreJoinDataSourceBo3.getMaxActiveConnectionSize(), 100);
        Assert.assertEquals(aggreJoinDataSourceBo3.getMaxActiveConnectionAgentId(), "agent_id_8_50");
        AggreJoinDataSourceBo aggreJoinDataSourceBo4 = joinDataSourceBoList.get(3);
        Assert.assertEquals(aggreJoinDataSourceBo4.getServiceTypeCode(), 4000);
        Assert.assertEquals(aggreJoinDataSourceBo4.getUrl(), "jdbc:oracle");
        Assert.assertEquals(aggreJoinDataSourceBo4.getAvgActiveConnectionSize(), 70);
        Assert.assertEquals(aggreJoinDataSourceBo4.getMinActiveConnectionSize(), 20);
        Assert.assertEquals(aggreJoinDataSourceBo4.getMinActiveConnectionAgentId(), "agent_id_4_10");
        Assert.assertEquals(aggreJoinDataSourceBo4.getMaxActiveConnectionSize(), 120);
        Assert.assertEquals(aggreJoinDataSourceBo4.getMaxActiveConnectionAgentId(), "agent_id_9_50");
        AggreJoinDataSourceBo aggreJoinDataSourceBo5 = joinDataSourceBoList.get(4);
        Assert.assertEquals(aggreJoinDataSourceBo5.getServiceTypeCode(), 5000);
        Assert.assertEquals(aggreJoinDataSourceBo5.getUrl(), "jdbc:cubrid");
        Assert.assertEquals(aggreJoinDataSourceBo5.getAvgActiveConnectionSize(), 80);
        Assert.assertEquals(aggreJoinDataSourceBo5.getMinActiveConnectionSize(), 35);
        Assert.assertEquals(aggreJoinDataSourceBo5.getMinActiveConnectionAgentId(), "agent_id_5_10");
        Assert.assertEquals(aggreJoinDataSourceBo5.getMaxActiveConnectionSize(), 130);
        Assert.assertEquals(aggreJoinDataSourceBo5.getMaxActiveConnectionAgentId(), "agent_id_10_50");
    }

    private class ComparatorImpl implements Comparator<JoinDataSourceBo> {
        @Override
        public int compare(JoinDataSourceBo bo1, JoinDataSourceBo bo2) {
            return (bo1.getServiceTypeCode()) < (bo2.getServiceTypeCode()) ? -1 : 1;
        }
    }
}

