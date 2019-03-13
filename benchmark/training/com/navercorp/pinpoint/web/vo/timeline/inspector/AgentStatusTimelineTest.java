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


import AgentState.RUNNING;
import AgentState.SHUTDOWN;
import AgentState.UNKNOWN;
import com.navercorp.pinpoint.common.server.util.AgentLifeCycleState;
import com.navercorp.pinpoint.web.vo.AgentStatus;
import com.navercorp.pinpoint.web.vo.Range;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class AgentStatusTimelineTest {
    @Test
    public void nullAgentStatus() {
        // Given
        Range timelineRange = new Range(0, 100);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(0, 100, UNKNOWN));
        // When
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void nullAgentStatus_nullAgentEvents() {
        // Given
        Range timelineRange = new Range(0, 100);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(0, 100, UNKNOWN));
        // When
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void agentStatus() {
        // Given
        Range timelineRange = new Range(100, 200);
        AgentLifeCycleState expectedState = AgentLifeCycleState.RUNNING;
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(100, 200, AgentState.fromAgentLifeCycleState(expectedState)));
        // When
        AgentStatus initialStatus = createAgentStatus(50, expectedState);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void agentStatus_nullAgentEvents() {
        // Given
        Range timelineRange = new Range(100, 200);
        AgentLifeCycleState expectedState = AgentLifeCycleState.RUNNING;
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(100, 200, AgentState.fromAgentLifeCycleState(expectedState)));
        // When
        AgentStatus initialStatus = createAgentStatus(50, expectedState);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void singleLifeCycle_startedBeforeTimelineStartTimestamp() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(100, 200, RUNNING));
        // When
        long agentA = 0;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void singleLifeCycle_startedAfterTimelineStartTimestamp_initialStateRunning() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 150, RUNNING), createSegment(150, 200, RUNNING));
        // When
        long agentA = 150;
        AgentStatus initialStatus = createAgentStatus(50, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertTrue(timeline.isIncludeWarning());
    }

    @Test
    public void singleLifeCycle_startedAfterTimelineStartTimestamp_initialStateShutdown() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 150, SHUTDOWN), createSegment(150, 200, RUNNING));
        // When
        long agentA = 150;
        AgentStatus initialStatus = createAgentStatus(50, AgentLifeCycleState.SHUTDOWN);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void singleLifeCycle_endedBeforeTimelineEndTimestamp() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 180, RUNNING), createSegment(180, 200, SHUTDOWN));
        // When
        long agentA = 0;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void singleLifeCycle_disconnected() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(100, 200, RUNNING));
        // When
        long agentA = 0;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_disconnected() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 150, RUNNING), createSegment(150, 160, UNKNOWN), createSegment(160, 200, RUNNING));
        // When
        long agentA = 0;
        long agentB = 160;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_noOverlap() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 140, RUNNING), createSegment(140, 160, SHUTDOWN), createSegment(160, 200, RUNNING));
        // When
        long agentA = 0;
        long agentB = 160;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_noOverlap2() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 159, RUNNING), createSegment(159, 160, SHUTDOWN), createSegment(160, 200, RUNNING));
        // When
        long agentA = 0;
        long agentB = 160;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_noOverlap3() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 120, SHUTDOWN), createSegment(120, 140, RUNNING), createSegment(140, 160, SHUTDOWN), createSegment(160, 180, RUNNING), createSegment(180, 200, SHUTDOWN));
        // When
        long agentA = 120;
        long agentB = 160;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.SHUTDOWN);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertFalse(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_overlap() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 180, RUNNING), createSegment(180, 200, SHUTDOWN));
        // When
        long agentA = 0;
        long agentB = 120;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertTrue(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_overlap2() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(100, 200, RUNNING));
        // When
        long agentA = 0;
        long agentB = 160;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertTrue(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_overlap3() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(100, 200, RUNNING));
        // When
        long agentA = 80;
        long agentB = 90;
        long agentC = 110;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertTrue(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_overlap4() {
        // Given
        Range timelineRange = new Range(100, 200);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Collections.singletonList(createSegment(100, 200, RUNNING));
        // When
        long agentA = 90;
        long agentB = 130;
        long agentC = 160;
        long agentD = 180;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertTrue(timeline.isIncludeWarning());
    }

    @Test
    public void multipleLifeCycles_mixed() {
        // Given
        Range timelineRange = new Range(100, 300);
        List<AgentStatusTimelineSegment> expectedTimelineSegments = Arrays.asList(createSegment(100, 150, RUNNING), createSegment(150, 160, UNKNOWN), createSegment(160, 250, RUNNING), createSegment(250, 260, SHUTDOWN), createSegment(260, 290, RUNNING), createSegment(290, 300, UNKNOWN));
        // When
        long agentA = 90;
        long agentB = 160;
        long agentC = 220;
        long agentD = 260;
        AgentStatus initialStatus = createAgentStatus(90, AgentLifeCycleState.RUNNING);
        AgentStatusTimeline timeline = build();
        // Then
        Assert.assertEquals(expectedTimelineSegments, timeline.getTimelineSegments());
        Assert.assertTrue(timeline.isIncludeWarning());
    }
}

