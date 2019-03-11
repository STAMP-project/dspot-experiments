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
import DbCommons.TextRange;
import DbIssues.Flow;
import DbIssues.Location;
import DbIssues.Locations;
import RuleTesting.XOO_X1;
import RuleTesting.XOO_X2;
import RuleTesting.XOO_X3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.source.NewLinesRepository;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.tracking.Tracking;


public class ShortBranchOrPullRequestTrackerExecutionTest {
    static final String FILE_UUID = "FILE_UUID";

    static final String FILE_KEY = "FILE_KEY";

    static final int FILE_REF = 2;

    static final Component FILE = ReportComponent.builder(Component.Type.FILE, ShortBranchOrPullRequestTrackerExecutionTest.FILE_REF).setKey(ShortBranchOrPullRequestTrackerExecutionTest.FILE_KEY).setUuid(ShortBranchOrPullRequestTrackerExecutionTest.FILE_UUID).build();

    @Mock
    private TrackerRawInputFactory rawFactory;

    @Mock
    private TrackerBaseInputFactory baseFactory;

    @Mock
    private TrackerMergeBranchInputFactory mergeFactory;

    @Mock
    private NewLinesRepository newLinesRepository;

    private ShortBranchOrPullRequestTrackerExecution underTest;

    private List<DefaultIssue> rawIssues = new ArrayList<>();

    private List<DefaultIssue> baseIssues = new ArrayList<>();

    private List<DefaultIssue> mergeBranchIssues = new ArrayList<>();

    @Test
    public void simple_tracking_keep_only_issues_having_location_on_changed_lines() {
        final DefaultIssue issue1 = createIssue(2, XOO_X1);
        issue1.setLocations(Locations.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).setEndLine(3)).build());
        rawIssues.add(issue1);
        final DefaultIssue issue2 = createIssue(2, XOO_X1);
        issue2.setLocations(Locations.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).setEndLine(2)).build());
        rawIssues.add(issue2);
        Mockito.when(mergeFactory.hasMergeBranchAnalysis()).thenReturn(false);
        Mockito.when(newLinesRepository.getNewLines(ShortBranchOrPullRequestTrackerExecutionTest.FILE)).thenReturn(Optional.of(new HashSet(Arrays.asList(1, 3))));
        Tracking<DefaultIssue, DefaultIssue> tracking = underTest.track(ShortBranchOrPullRequestTrackerExecutionTest.FILE);
        assertThat(tracking.getUnmatchedBases()).isEmpty();
        assertThat(tracking.getMatchedRaws()).isEmpty();
        assertThat(tracking.getUnmatchedRaws()).containsOnly(issue1);
    }

    @Test
    public void simple_tracking_keep_also_issues_having_secondary_locations_on_changed_lines() {
        final DefaultIssue issueWithSecondaryLocationOnAChangedLine = createIssue(2, XOO_X1);
        issueWithSecondaryLocationOnAChangedLine.setLocations(Locations.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).setEndLine(3)).addFlow(Flow.newBuilder().addLocation(Location.newBuilder().setComponentId(ShortBranchOrPullRequestTrackerExecutionTest.FILE.getUuid()).setTextRange(TextRange.newBuilder().setStartLine(6).setEndLine(8)).build()).build()).build());
        rawIssues.add(issueWithSecondaryLocationOnAChangedLine);
        final DefaultIssue issueWithNoLocationsOnChangedLines = createIssue(2, XOO_X1);
        issueWithNoLocationsOnChangedLines.setLocations(Locations.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).setEndLine(2)).addFlow(Flow.newBuilder().addLocation(Location.newBuilder().setComponentId(ShortBranchOrPullRequestTrackerExecutionTest.FILE.getUuid()).setTextRange(TextRange.newBuilder().setStartLine(11).setEndLine(12)).build()).build()).build());
        rawIssues.add(issueWithNoLocationsOnChangedLines);
        final DefaultIssue issueWithALocationOnADifferentFile = createIssue(2, XOO_X1);
        issueWithALocationOnADifferentFile.setLocations(Locations.newBuilder().setTextRange(TextRange.newBuilder().setStartLine(2).setEndLine(3)).addFlow(Flow.newBuilder().addLocation(Location.newBuilder().setComponentId("anotherUuid").setTextRange(TextRange.newBuilder().setStartLine(6).setEndLine(8)).build()).build()).build());
        rawIssues.add(issueWithALocationOnADifferentFile);
        Mockito.when(mergeFactory.hasMergeBranchAnalysis()).thenReturn(false);
        Mockito.when(newLinesRepository.getNewLines(ShortBranchOrPullRequestTrackerExecutionTest.FILE)).thenReturn(Optional.of(new HashSet(Arrays.asList(7, 10))));
        Tracking<DefaultIssue, DefaultIssue> tracking = underTest.track(ShortBranchOrPullRequestTrackerExecutionTest.FILE);
        assertThat(tracking.getUnmatchedBases()).isEmpty();
        assertThat(tracking.getMatchedRaws()).isEmpty();
        assertThat(tracking.getUnmatchedRaws()).containsOnly(issueWithSecondaryLocationOnAChangedLine);
    }

    @Test
    public void tracking_with_all_results() {
        rawIssues.add(createIssue(1, XOO_X1));
        rawIssues.add(createIssue(2, XOO_X2));
        rawIssues.add(createIssue(3, XOO_X3));
        Mockito.when(mergeFactory.hasMergeBranchAnalysis()).thenReturn(true);
        mergeBranchIssues.add(rawIssues.get(0));
        baseIssues.add(rawIssues.get(0));
        baseIssues.add(rawIssues.get(1));
        Tracking<DefaultIssue, DefaultIssue> tracking = underTest.track(ShortBranchOrPullRequestTrackerExecutionTest.FILE);
        assertThat(tracking.getMatchedRaws()).isEqualTo(Collections.singletonMap(rawIssues.get(1), rawIssues.get(1)));
        assertThat(tracking.getUnmatchedRaws()).containsOnly(rawIssues.get(2));
    }
}

