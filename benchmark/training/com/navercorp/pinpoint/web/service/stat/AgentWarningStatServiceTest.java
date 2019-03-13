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
package com.navercorp.pinpoint.web.service.stat;


import com.navercorp.pinpoint.common.server.bo.stat.DeadlockThreadCountBo;
import com.navercorp.pinpoint.web.dao.stat.DeadlockDao;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.timeline.inspector.AgentStatusTimelineSegment;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Taejin Koo
 */
@RunWith(MockitoJUnitRunner.class)
public class AgentWarningStatServiceTest {
    private static final long CURRENT_TIME = System.currentTimeMillis();

    private static final long START_TIME = (AgentWarningStatServiceTest.CURRENT_TIME) - (TimeUnit.DAYS.toMillis(1));

    private static final long TIME = 60000 * 5;

    @Mock
    private DeadlockDao deadlockDao;

    @InjectMocks
    private AgentWarningStatService agentWarningStatService = new AgentWarningStatServiceImpl();

    @Test
    public void selectTest1() throws Exception {
        Range range = new Range(((AgentWarningStatServiceTest.CURRENT_TIME) - (AgentWarningStatServiceTest.TIME)), AgentWarningStatServiceTest.CURRENT_TIME);
        List<DeadlockThreadCountBo> mockData = createMockData(10, 5000);
        Mockito.when(deadlockDao.getAgentStatList("pinpoint", range)).thenReturn(mockData);
        List<AgentStatusTimelineSegment> timelineSegmentList = agentWarningStatService.select("pinpoint", range);
        Assert.assertTrue(((timelineSegmentList.size()) == 1));
    }

    @Test
    public void selectTest2() throws Exception {
        Range range = new Range(((AgentWarningStatServiceTest.CURRENT_TIME) - (AgentWarningStatServiceTest.TIME)), AgentWarningStatServiceTest.CURRENT_TIME);
        List<DeadlockThreadCountBo> mockData = createMockData(10, 70000);
        Mockito.when(deadlockDao.getAgentStatList("pinpoint", range)).thenReturn(mockData);
        List<AgentStatusTimelineSegment> timelineSegmentList = agentWarningStatService.select("pinpoint", range);
        Assert.assertTrue(((timelineSegmentList.size()) == 10));
    }
}

