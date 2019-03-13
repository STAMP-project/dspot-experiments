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
package com.navercorp.pinpoint.web.vo;


import TRouteResult.NOT_ACCEPTABLE;
import TRouteResult.OK;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.thrift.dto.command.TCmdActiveThreadCountRes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class AgentActiveThreadCountListTest {
    @Test
    public void testName() throws Exception {
        String hostName1 = "hostName1";
        String hostName2 = "hostName2";
        AgentActiveThreadCountFactory factory = new AgentActiveThreadCountFactory();
        factory.setAgentId(hostName1);
        AgentActiveThreadCount status1 = factory.createFail(NOT_ACCEPTABLE.name());
        TCmdActiveThreadCountRes response = new TCmdActiveThreadCountRes();
        response.setActiveThreadCount(Arrays.asList(1, 2, 3, 4));
        factory.setAgentId(hostName2);
        AgentActiveThreadCount status2 = factory.create(response);
        AgentActiveThreadCountList list = new AgentActiveThreadCountList(5);
        list.add(status1);
        list.add(status2);
        ObjectMapper om = new ObjectMapper();
        String listAsString = om.writeValueAsString(list);
        Map map = om.readValue(listAsString, Map.class);
        Assert.assertTrue(map.containsKey(hostName1));
        Assert.assertTrue(map.containsKey(hostName2));
        assertDataWithSerializedJsonString(((Map) (map.get(hostName1))), NOT_ACCEPTABLE, null);
        assertDataWithSerializedJsonString(((Map) (map.get(hostName2))), OK, Arrays.asList(1, 2, 3, 4));
    }

    @Test
    public void testOrderName() throws Exception {
        String hostName1 = "hostName1";
        String hostName2 = "hostName2";
        String hostName3 = "hostName3";
        AgentActiveThreadCountFactory factory = new AgentActiveThreadCountFactory();
        factory.setAgentId(hostName1);
        AgentActiveThreadCount status1 = factory.createFail("UNKNOWN ERROR");
        factory.setAgentId(hostName2);
        AgentActiveThreadCount status2 = factory.createFail("UNKNOWN ERROR");
        factory.setAgentId(hostName3);
        AgentActiveThreadCount status3 = factory.createFail("UNKNOWN ERROR");
        AgentActiveThreadCountList list = new AgentActiveThreadCountList(5);
        list.add(status2);
        list.add(status3);
        list.add(status1);
        final List<AgentActiveThreadCount> sortedList = list.getAgentActiveThreadRepository();
        Assert.assertEquals(sortedList.get(0).getAgentId(), "hostName1");
        Assert.assertEquals(sortedList.get(1).getAgentId(), "hostName2");
        Assert.assertEquals(sortedList.get(2).getAgentId(), "hostName3");
    }
}

