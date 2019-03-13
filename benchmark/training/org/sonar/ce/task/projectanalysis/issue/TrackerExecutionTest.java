/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.issue;


import Component.Type.FILE;
import Issue.STATUSES;
import Issue.STATUS_CLOSED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolder;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.tracking.Input;
import org.sonar.core.issue.tracking.NonClosedTracking;
import org.sonar.core.issue.tracking.Tracker;
import org.sonar.core.issue.tracking.Tracking;


public class TrackerExecutionTest {
    private final TrackerRawInputFactory rawInputFactory = Mockito.mock(TrackerRawInputFactory.class);

    private final TrackerBaseInputFactory baseInputFactory = Mockito.mock(TrackerBaseInputFactory.class);

    private final ClosedIssuesInputFactory closedIssuesInputFactory = Mockito.mock(ClosedIssuesInputFactory.class);

    private final Tracker<DefaultIssue, DefaultIssue> tracker = Mockito.mock(Tracker.class);

    private final ComponentIssuesLoader componentIssuesLoader = Mockito.mock(ComponentIssuesLoader.class);

    private final AnalysisMetadataHolder analysisMetadataHolder = Mockito.mock(AnalysisMetadataHolder.class);

    private TrackerExecution underTest = new TrackerExecution(baseInputFactory, rawInputFactory, closedIssuesInputFactory, tracker, componentIssuesLoader, analysisMetadataHolder);

    private Input<DefaultIssue> rawInput = Mockito.mock(Input.class);

    private Input<DefaultIssue> openIssuesInput = Mockito.mock(Input.class);

    private Input<DefaultIssue> closedIssuesInput = Mockito.mock(Input.class);

    private NonClosedTracking<DefaultIssue, DefaultIssue> nonClosedTracking = Mockito.mock(NonClosedTracking.class);

    private Tracking<DefaultIssue, DefaultIssue> closedTracking = Mockito.mock(Tracking.class);

    @Test
    public void track_tracks_only_nonClosed_issues_if_tracking_returns_complete_from_Tracker() {
        ReportComponent component = ReportComponent.builder(FILE, 1).build();
        Mockito.when(rawInputFactory.create(component)).thenReturn(rawInput);
        Mockito.when(baseInputFactory.create(component)).thenReturn(openIssuesInput);
        Mockito.when(closedIssuesInputFactory.create(ArgumentMatchers.any())).thenThrow(new IllegalStateException("closedIssuesInputFactory should not be called"));
        Mockito.when(nonClosedTracking.isComplete()).thenReturn(true);
        Mockito.when(analysisMetadataHolder.isFirstAnalysis()).thenReturn(false);
        Mockito.when(tracker.trackNonClosed(rawInput, openIssuesInput)).thenReturn(nonClosedTracking);
        Mockito.when(tracker.trackClosed(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new IllegalStateException("trackClosed should not be called"));
        Tracking<DefaultIssue, DefaultIssue> tracking = underTest.track(component);
        assertThat(tracking).isSameAs(nonClosedTracking);
        Mockito.verify(tracker).trackNonClosed(rawInput, openIssuesInput);
        Mockito.verifyNoMoreInteractions(tracker);
    }

    @Test
    public void track_does_not_track_nonClosed_issues_if_tracking_returns_incomplete_but_this_is_first_analysis() {
        ReportComponent component = ReportComponent.builder(FILE, 1).build();
        Mockito.when(rawInputFactory.create(component)).thenReturn(rawInput);
        Mockito.when(baseInputFactory.create(component)).thenReturn(openIssuesInput);
        Mockito.when(closedIssuesInputFactory.create(ArgumentMatchers.any())).thenThrow(new IllegalStateException("closedIssuesInputFactory should not be called"));
        Mockito.when(nonClosedTracking.isComplete()).thenReturn(false);
        Mockito.when(analysisMetadataHolder.isFirstAnalysis()).thenReturn(true);
        Mockito.when(tracker.trackNonClosed(rawInput, openIssuesInput)).thenReturn(nonClosedTracking);
        Mockito.when(tracker.trackClosed(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new IllegalStateException("trackClosed should not be called"));
        Tracking<DefaultIssue, DefaultIssue> tracking = underTest.track(component);
        assertThat(tracking).isSameAs(nonClosedTracking);
        Mockito.verify(tracker).trackNonClosed(rawInput, openIssuesInput);
        Mockito.verifyNoMoreInteractions(tracker);
    }

    @Test
    public void track_tracks_nonClosed_issues_and_then_closedOnes_if_tracking_returns_incomplete() {
        ReportComponent component = ReportComponent.builder(FILE, 1).build();
        Mockito.when(rawInputFactory.create(component)).thenReturn(rawInput);
        Mockito.when(baseInputFactory.create(component)).thenReturn(openIssuesInput);
        Mockito.when(closedIssuesInputFactory.create(component)).thenReturn(closedIssuesInput);
        Mockito.when(nonClosedTracking.isComplete()).thenReturn(false);
        Mockito.when(analysisMetadataHolder.isFirstAnalysis()).thenReturn(false);
        Mockito.when(tracker.trackNonClosed(rawInput, openIssuesInput)).thenReturn(nonClosedTracking);
        Mockito.when(tracker.trackClosed(nonClosedTracking, closedIssuesInput)).thenReturn(closedTracking);
        Tracking<DefaultIssue, DefaultIssue> tracking = underTest.track(component);
        assertThat(tracking).isSameAs(closedTracking);
        Mockito.verify(tracker).trackNonClosed(rawInput, openIssuesInput);
        Mockito.verify(tracker).trackClosed(nonClosedTracking, closedIssuesInput);
        Mockito.verifyNoMoreInteractions(tracker);
    }

    @Test
    public void track_loadChanges_on_matched_closed_issues() {
        ReportComponent component = ReportComponent.builder(FILE, 1).build();
        Mockito.when(rawInputFactory.create(component)).thenReturn(rawInput);
        Mockito.when(baseInputFactory.create(component)).thenReturn(openIssuesInput);
        Mockito.when(closedIssuesInputFactory.create(component)).thenReturn(closedIssuesInput);
        Mockito.when(nonClosedTracking.isComplete()).thenReturn(false);
        Mockito.when(analysisMetadataHolder.isFirstAnalysis()).thenReturn(false);
        Mockito.when(tracker.trackNonClosed(rawInput, openIssuesInput)).thenReturn(nonClosedTracking);
        Mockito.when(tracker.trackClosed(nonClosedTracking, closedIssuesInput)).thenReturn(closedTracking);
        Set<DefaultIssue> mappedClosedIssues = IntStream.range(1, (2 + (new Random().nextInt(2)))).mapToObj(( i) -> new DefaultIssue().setKey(("closed" + i)).setStatus(STATUS_CLOSED)).collect(Collectors.toSet());
        ArrayList<DefaultIssue> mappedBaseIssues = new ArrayList(mappedClosedIssues);
        STATUSES.stream().filter(( t) -> !(Issue.STATUS_CLOSED.equals(t))).forEach(( s) -> mappedBaseIssues.add(new DefaultIssue().setKey(s).setStatus(s)));
        Collections.shuffle(mappedBaseIssues);
        Mockito.when(closedTracking.getMatchedRaws()).thenReturn(mappedBaseIssues.stream().collect(uniqueIndex(( i) -> new DefaultIssue().setKey(("raw_for_" + (i.key()))), ( i) -> i)));
        Tracking<DefaultIssue, DefaultIssue> tracking = underTest.track(component);
        assertThat(tracking).isSameAs(closedTracking);
        Mockito.verify(tracker).trackNonClosed(rawInput, openIssuesInput);
        Mockito.verify(tracker).trackClosed(nonClosedTracking, closedIssuesInput);
        Mockito.verify(componentIssuesLoader).loadLatestDiffChangesForReopeningOfClosedIssues(mappedClosedIssues);
        Mockito.verifyNoMoreInteractions(tracker);
    }
}

