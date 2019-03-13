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


import JoinMemoryBo.EMPTY_JOIN_MEMORY_BO;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinMemoryBoTest {
    @Test
    public void joinMemoryBoListTest() throws Exception {
        List<JoinMemoryBo> joinMemoryBoList = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo1 = new JoinMemoryBo("agent1", 1496988667231L, 3000, 2000, 5000, "agent1", "agent1", 500, 50, 600, "agent1", "agent1");
        JoinMemoryBo joinMemoryBo2 = new JoinMemoryBo("agent2", 1496988667231L, 4000, 1000, 7000, "agent2", "agent2", 400, 150, 600, "agent2", "agent2");
        JoinMemoryBo joinMemoryBo3 = new JoinMemoryBo("agent3", 1496988667231L, 5000, 3000, 8000, "agent3", "agent3", 200, 100, 200, "agent3", "agent3");
        JoinMemoryBo joinMemoryBo4 = new JoinMemoryBo("agent4", 1496988667231L, 1000, 100, 3000, "agent4", "agent4", 100, 900, 1000, "agent4", "agent4");
        JoinMemoryBo joinMemoryBo5 = new JoinMemoryBo("agent5", 1496988667231L, 2000, 1000, 6000, "agent5", "agent5", 300, 100, 2900, "agent5", "agent5");
        joinMemoryBoList.add(joinMemoryBo1);
        joinMemoryBoList.add(joinMemoryBo2);
        joinMemoryBoList.add(joinMemoryBo3);
        joinMemoryBoList.add(joinMemoryBo4);
        joinMemoryBoList.add(joinMemoryBo5);
        JoinMemoryBo joinMemoryBo = JoinMemoryBo.joinMemoryBoList(joinMemoryBoList, 1496988667231L);
        Assert.assertEquals("agent1", joinMemoryBo.getId());
        Assert.assertEquals(1496988667231L, joinMemoryBo.getTimestamp());
        Assert.assertEquals(3000, joinMemoryBo.getHeapUsed());
        Assert.assertEquals(100, joinMemoryBo.getMinHeapUsed());
        Assert.assertEquals(8000, joinMemoryBo.getMaxHeapUsed());
        Assert.assertEquals("agent4", joinMemoryBo.getMinHeapAgentId());
        Assert.assertEquals("agent3", joinMemoryBo.getMaxHeapAgentId());
        Assert.assertEquals(300, joinMemoryBo.getNonHeapUsed());
        Assert.assertEquals(50, joinMemoryBo.getMinNonHeapUsed());
        Assert.assertEquals(2900, joinMemoryBo.getMaxNonHeapUsed());
        Assert.assertEquals("agent1", joinMemoryBo.getMinNonHeapAgentId());
        Assert.assertEquals("agent5", joinMemoryBo.getMaxNonHeapAgentId());
    }

    @Test
    public void joinMemoryBoList2Test() {
        List<JoinMemoryBo> joinMemoryBoList = new ArrayList<JoinMemoryBo>();
        JoinMemoryBo joinMemoryBo = JoinMemoryBo.joinMemoryBoList(joinMemoryBoList, 1496988667231L);
        Assert.assertEquals(joinMemoryBo, EMPTY_JOIN_MEMORY_BO);
    }
}

