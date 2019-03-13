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
package com.navercorp.pinpoint.web.vo.timeline.inspector;


import AgentLifeCycleState.RUNNING;
import AgentState.UNSTABLE_RUNNING;
import com.navercorp.pinpoint.web.vo.AgentStatus;
import com.navercorp.pinpoint.web.vo.Range;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class AgentStatusTimelineBuilderTest {
    private static final long FROM = 10000;

    private static final long TO = 20000;

    private static final long DIFF = (AgentStatusTimelineBuilderTest.TO) - (AgentStatusTimelineBuilderTest.FROM);

    private static final AgentStatus DEFAULT_STATUS;

    static {
        DEFAULT_STATUS = AgentStatusTimelineBuilderTest.createAgentStatus(0, RUNNING);
    }

    @Test
    public void defaultTest1() {
        // Given
        Range timelineRange = new Range(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        // When
        long startTime = getRandomLong(((AgentStatusTimelineBuilderTest.FROM) + 1), ((AgentStatusTimelineBuilderTest.FROM) + ((AgentStatusTimelineBuilderTest.DIFF) / 2)));
        long endTime = getRandomLong(startTime, ((AgentStatusTimelineBuilderTest.TO) - 1));
        AgentStatusTimelineSegment timelineSegment = createTimelineSegment(startTime, endTime);
        AgentStatusTimeline timeline = build();
        // Then
        List<AgentStatusTimelineSegment> actualTimelineSegmentList = timeline.getTimelineSegments();
        Assert.assertTrue(((actualTimelineSegmentList.size()) == 3));
        AgentStatusTimelineSegment first = actualTimelineSegmentList.get(0);
        assertTimelineSegment(first, AgentStatusTimelineBuilderTest.FROM, startTime, AgentState.RUNNING);
        AgentStatusTimelineSegment unstableTimelineSegment = actualTimelineSegmentList.get(1);
        assertTimelineSegment(unstableTimelineSegment, startTime, endTime, UNSTABLE_RUNNING);
        AgentStatusTimelineSegment last = actualTimelineSegmentList.get(2);
        assertTimelineSegment(last, endTime, AgentStatusTimelineBuilderTest.TO, AgentState.RUNNING);
    }

    @Test
    public void defaultTest2() {
        // Given
        Range timelineRange = new Range(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        // When
        long firstStartTime = ThreadLocalRandom.current().nextLong(((AgentStatusTimelineBuilderTest.FROM) + 1), ((AgentStatusTimelineBuilderTest.FROM) + ((AgentStatusTimelineBuilderTest.DIFF) / 4)));
        long firstEndTime = ThreadLocalRandom.current().nextLong((firstStartTime + 1), ((AgentStatusTimelineBuilderTest.FROM) + ((AgentStatusTimelineBuilderTest.DIFF) / 2)));
        AgentStatusTimelineSegment timelineSegment1 = createTimelineSegment(firstStartTime, firstEndTime);
        long secondStartTime = ThreadLocalRandom.current().nextLong((firstEndTime + 1), ((AgentStatusTimelineBuilderTest.TO) - ((AgentStatusTimelineBuilderTest.DIFF) / 4)));
        long secondEndTime = ThreadLocalRandom.current().nextLong((secondStartTime + 1), ((AgentStatusTimelineBuilderTest.TO) - 1));
        AgentStatusTimelineSegment timelineSegment2 = createTimelineSegment(secondStartTime, secondEndTime);
        AgentStatusTimeline timeline = build();
        // Then
        List<AgentStatusTimelineSegment> actualTimelineSegmentList = timeline.getTimelineSegments();
        Assert.assertTrue(((actualTimelineSegmentList.size()) == 5));
        AgentStatusTimelineSegment first = actualTimelineSegmentList.get(0);
        assertTimelineSegment(first, AgentStatusTimelineBuilderTest.FROM, firstStartTime, AgentState.RUNNING);
        AgentStatusTimelineSegment unstableTimelineSegment1 = actualTimelineSegmentList.get(1);
        assertTimelineSegment(unstableTimelineSegment1, firstStartTime, firstEndTime, UNSTABLE_RUNNING);
        AgentStatusTimelineSegment middle = actualTimelineSegmentList.get(2);
        assertTimelineSegment(middle, firstEndTime, secondStartTime, AgentState.RUNNING);
        AgentStatusTimelineSegment unstableTimelineSegment2 = actualTimelineSegmentList.get(3);
        assertTimelineSegment(unstableTimelineSegment2, secondStartTime, secondEndTime, UNSTABLE_RUNNING);
        AgentStatusTimelineSegment last = actualTimelineSegmentList.get(4);
        assertTimelineSegment(last, secondEndTime, AgentStatusTimelineBuilderTest.TO, AgentState.RUNNING);
    }

    @Test
    public void boundaryValueTest1() {
        // Given
        Range timelineRange = new Range(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        // When
        long endTime = getRandomLong(((AgentStatusTimelineBuilderTest.FROM) + 1), ((AgentStatusTimelineBuilderTest.TO) - 1));
        AgentStatusTimelineSegment timelineSegment = createTimelineSegment(AgentStatusTimelineBuilderTest.FROM, endTime);
        AgentStatusTimeline timeline = build();
        // Then
        List<AgentStatusTimelineSegment> actualTimelineSegmentList = timeline.getTimelineSegments();
        Assert.assertTrue(((actualTimelineSegmentList.size()) == 2));
        AgentStatusTimelineSegment unstableTimelineSegment = actualTimelineSegmentList.get(0);
        assertTimelineSegment(unstableTimelineSegment, AgentStatusTimelineBuilderTest.FROM, endTime, UNSTABLE_RUNNING);
        AgentStatusTimelineSegment last = actualTimelineSegmentList.get(1);
        assertTimelineSegment(last, endTime, AgentStatusTimelineBuilderTest.TO, AgentState.RUNNING);
    }

    @Test
    public void boundaryValueTest2() {
        // Given
        Range timelineRange = new Range(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        // When
        long startTime = ThreadLocalRandom.current().nextLong(((AgentStatusTimelineBuilderTest.FROM) + 1), ((AgentStatusTimelineBuilderTest.FROM) + ((AgentStatusTimelineBuilderTest.DIFF) / 2)));
        AgentStatusTimelineSegment timelineSegment = createTimelineSegment(startTime, AgentStatusTimelineBuilderTest.TO);
        AgentStatusTimeline timeline = build();
        // Then
        List<AgentStatusTimelineSegment> actualTimelineSegmentList = timeline.getTimelineSegments();
        Assert.assertTrue(((actualTimelineSegmentList.size()) == 2));
        AgentStatusTimelineSegment first = actualTimelineSegmentList.get(0);
        assertTimelineSegment(first, AgentStatusTimelineBuilderTest.FROM, startTime, AgentState.RUNNING);
        AgentStatusTimelineSegment unstableTimelineSegment = actualTimelineSegmentList.get(1);
        assertTimelineSegment(unstableTimelineSegment, startTime, AgentStatusTimelineBuilderTest.TO, UNSTABLE_RUNNING);
    }

    @Test
    public void overBoundaryValueTest1() {
        // Given
        Range timelineRange = new Range(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        // When
        long warningEndTime = ThreadLocalRandom.current().nextLong(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        AgentStatusTimelineSegment timelineSegment = createTimelineSegment(((AgentStatusTimelineBuilderTest.FROM) - 1), warningEndTime);
        AgentStatusTimeline timeline = build();
        // Then
        List<AgentStatusTimelineSegment> actualTimelineSegmentList = timeline.getTimelineSegments();
        Assert.assertTrue(((actualTimelineSegmentList.size()) == 1));
        AgentStatusTimelineSegment timelineSegment1 = actualTimelineSegmentList.get(0);
        assertTimelineSegment(timelineSegment1, AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO, AgentState.RUNNING);
    }

    @Test
    public void overBoundaryValueTest2() {
        // Given
        Range timelineRange = new Range(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        // When
        long warningStartTime = ThreadLocalRandom.current().nextLong(AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO);
        AgentStatusTimelineSegment timelineSegment = createTimelineSegment(warningStartTime, ((AgentStatusTimelineBuilderTest.TO) + 1));
        AgentStatusTimeline timeline = build();
        // Then
        List<AgentStatusTimelineSegment> actualTimelineSegmentList = timeline.getTimelineSegments();
        Assert.assertTrue(((actualTimelineSegmentList.size()) == 1));
        AgentStatusTimelineSegment timelineSegment1 = actualTimelineSegmentList.get(0);
        assertTimelineSegment(timelineSegment1, AgentStatusTimelineBuilderTest.FROM, AgentStatusTimelineBuilderTest.TO, AgentState.RUNNING);
    }
}

