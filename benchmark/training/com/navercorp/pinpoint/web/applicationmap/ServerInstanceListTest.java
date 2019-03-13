/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.web.applicationmap;


import com.navercorp.pinpoint.web.applicationmap.nodes.ServerBuilder;
import com.navercorp.pinpoint.web.applicationmap.nodes.ServerInstanceList;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author emeroad
 */
public class ServerInstanceListTest {
    @Test
    public void testGetAgentIdList() throws Exception {
        AgentInfo agentInfo1 = ServerInstanceListTest.createAgentInfo("agentId1", "testHost");
        AgentInfo agentInfo2 = ServerInstanceListTest.createAgentInfo("agentId2", "testHost");
        Set<AgentInfo> agentInfoSet = new HashSet<>();
        agentInfoSet.add(agentInfo1);
        agentInfoSet.add(agentInfo2);
        ServerBuilder builder = new ServerBuilder();
        builder.addAgentInfo(agentInfoSet);
        ServerInstanceList serverInstanceList = builder.build();
        List<String> agentIdList = serverInstanceList.getAgentIdList();
        Assert.assertThat(agentIdList, Matchers.hasSize(2));
        Assert.assertThat(agentIdList, CoreMatchers.hasItem("agentId1"));
        Assert.assertThat(agentIdList, CoreMatchers.hasItem("agentId2"));
    }
}

