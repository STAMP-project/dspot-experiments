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
package com.navercorp.pinpoint.common.server.bo.stat.join;


import JoinDataSourceListBo.EMPTY_JOIN_DATA_SOURCE_LIST_BO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinDataSourceListBoTest {
    @Test
    public void joinDataSourceListBoListTest() {
        final String id = "test_app";
        final long currentTime = System.currentTimeMillis();
        final List<JoinDataSourceListBo> joinDataSourceListBoList = createJoinDataSourceListBoList(id, currentTime);
        final JoinDataSourceListBo joinDataSourceListBo = JoinDataSourceListBo.joinDataSourceListBoList(joinDataSourceListBoList, currentTime);
        Assert.assertEquals(joinDataSourceListBo.getId(), id);
        Assert.assertEquals(joinDataSourceListBo.getTimestamp(), currentTime);
        List<JoinDataSourceBo> joinDataSourceBoList = joinDataSourceListBo.getJoinDataSourceBoList();
        Collections.sort(joinDataSourceBoList, new JoinDataSourceListBoTest.ComparatorImpl());
        Assert.assertEquals(joinDataSourceBoList.size(), 5);
        JoinDataSourceBo joinDataSourceBo1 = joinDataSourceBoList.get(0);
        Assert.assertEquals(joinDataSourceBo1.getServiceTypeCode(), 1000);
        Assert.assertEquals(joinDataSourceBo1.getUrl(), "jdbc:mysql");
        Assert.assertEquals(joinDataSourceBo1.getAvgActiveConnectionSize(), 60);
        Assert.assertEquals(joinDataSourceBo1.getMinActiveConnectionSize(), 35);
        Assert.assertEquals(joinDataSourceBo1.getMinActiveConnectionAgentId(), "agent_id_1_10");
        Assert.assertEquals(joinDataSourceBo1.getMaxActiveConnectionSize(), 110);
        Assert.assertEquals(joinDataSourceBo1.getMaxActiveConnectionAgentId(), "agent_id_6_50");
        JoinDataSourceBo joinDataSourceBo2 = joinDataSourceBoList.get(1);
        Assert.assertEquals(joinDataSourceBo2.getServiceTypeCode(), 2000);
        Assert.assertEquals(joinDataSourceBo2.getUrl(), "jdbc:mssql");
        Assert.assertEquals(joinDataSourceBo2.getAvgActiveConnectionSize(), 50);
        Assert.assertEquals(joinDataSourceBo2.getMinActiveConnectionSize(), 15);
        Assert.assertEquals(joinDataSourceBo2.getMinActiveConnectionAgentId(), "agent_id_2_10");
        Assert.assertEquals(joinDataSourceBo2.getMaxActiveConnectionSize(), 80);
        Assert.assertEquals(joinDataSourceBo2.getMaxActiveConnectionAgentId(), "agent_id_7_50");
        JoinDataSourceBo joinDataSourceBo3 = joinDataSourceBoList.get(2);
        Assert.assertEquals(joinDataSourceBo3.getServiceTypeCode(), 3000);
        Assert.assertEquals(joinDataSourceBo3.getUrl(), "jdbc:postgre");
        Assert.assertEquals(joinDataSourceBo3.getAvgActiveConnectionSize(), 40);
        Assert.assertEquals(joinDataSourceBo3.getMinActiveConnectionSize(), 35);
        Assert.assertEquals(joinDataSourceBo3.getMinActiveConnectionAgentId(), "agent_id_3_10");
        Assert.assertEquals(joinDataSourceBo3.getMaxActiveConnectionSize(), 100);
        Assert.assertEquals(joinDataSourceBo3.getMaxActiveConnectionAgentId(), "agent_id_8_50");
        JoinDataSourceBo joinDataSourceBo4 = joinDataSourceBoList.get(3);
        Assert.assertEquals(joinDataSourceBo4.getServiceTypeCode(), 4000);
        Assert.assertEquals(joinDataSourceBo4.getUrl(), "jdbc:oracle");
        Assert.assertEquals(joinDataSourceBo4.getAvgActiveConnectionSize(), 70);
        Assert.assertEquals(joinDataSourceBo4.getMinActiveConnectionSize(), 20);
        Assert.assertEquals(joinDataSourceBo4.getMinActiveConnectionAgentId(), "agent_id_4_10");
        Assert.assertEquals(joinDataSourceBo4.getMaxActiveConnectionSize(), 120);
        Assert.assertEquals(joinDataSourceBo4.getMaxActiveConnectionAgentId(), "agent_id_9_50");
        JoinDataSourceBo joinDataSourceBo5 = joinDataSourceBoList.get(4);
        Assert.assertEquals(joinDataSourceBo5.getServiceTypeCode(), 5000);
        Assert.assertEquals(joinDataSourceBo5.getUrl(), "jdbc:cubrid");
        Assert.assertEquals(joinDataSourceBo5.getAvgActiveConnectionSize(), 80);
        Assert.assertEquals(joinDataSourceBo5.getMinActiveConnectionSize(), 35);
        Assert.assertEquals(joinDataSourceBo5.getMinActiveConnectionAgentId(), "agent_id_5_10");
        Assert.assertEquals(joinDataSourceBo5.getMaxActiveConnectionSize(), 130);
        Assert.assertEquals(joinDataSourceBo5.getMaxActiveConnectionAgentId(), "agent_id_10_50");
    }

    @Test
    public void joinDataSourceListBoList2Test() {
        final String id = "test_app";
        final long currentTime = System.currentTimeMillis();
        final List<JoinDataSourceListBo> joinDataSourceListBoList = new ArrayList<JoinDataSourceListBo>(0);
        final JoinDataSourceListBo joinDataSourceListBo = JoinDataSourceListBo.joinDataSourceListBoList(joinDataSourceListBoList, currentTime);
        Assert.assertEquals(joinDataSourceListBo, EMPTY_JOIN_DATA_SOURCE_LIST_BO);
    }

    private class ComparatorImpl implements Comparator<JoinDataSourceBo> {
        @Override
        public int compare(JoinDataSourceBo bo1, JoinDataSourceBo bo2) {
            return (bo1.getServiceTypeCode()) < (bo2.getServiceTypeCode()) ? -1 : 1;
        }
    }
}

