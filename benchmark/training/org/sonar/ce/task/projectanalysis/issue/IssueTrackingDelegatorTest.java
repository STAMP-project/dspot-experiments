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


import BranchType.LONG;
import BranchType.PULL_REQUEST;
import BranchType.SHORT;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolder;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.tracking.Tracking;


public class IssueTrackingDelegatorTest {
    @Mock
    private ShortBranchOrPullRequestTrackerExecution shortBranchTracker;

    @Mock
    private MergeBranchTrackerExecution mergeBranchTracker;

    @Mock
    private TrackerExecution tracker;

    @Mock
    private AnalysisMetadataHolder analysisMetadataHolder;

    @Mock
    private Component component;

    @Mock
    private Tracking<DefaultIssue, DefaultIssue> trackingResult;

    private IssueTrackingDelegator underTest;

    @Test
    public void delegate_regular_tracker() {
        Mockito.when(analysisMetadataHolder.isShortLivingBranch()).thenReturn(false);
        Mockito.when(analysisMetadataHolder.getBranch()).thenReturn(Mockito.mock(Branch.class));
        underTest.track(component);
        Mockito.verify(tracker).track(component);
        Mockito.verifyZeroInteractions(shortBranchTracker);
        Mockito.verifyZeroInteractions(mergeBranchTracker);
    }

    @Test
    public void delegate_merge_tracker() {
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getType()).thenReturn(LONG);
        Mockito.when(branch.isMain()).thenReturn(false);
        Mockito.when(analysisMetadataHolder.getBranch()).thenReturn(branch);
        Mockito.when(analysisMetadataHolder.isFirstAnalysis()).thenReturn(true);
        underTest.track(component);
        Mockito.verify(mergeBranchTracker).track(component);
        Mockito.verifyZeroInteractions(tracker);
        Mockito.verifyZeroInteractions(shortBranchTracker);
    }

    @Test
    public void delegate_short_branch_tracker() {
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getType()).thenReturn(SHORT);
        Mockito.when(analysisMetadataHolder.getBranch()).thenReturn(Mockito.mock(Branch.class));
        Mockito.when(analysisMetadataHolder.isShortLivingBranch()).thenReturn(true);
        underTest.track(component);
        Mockito.verify(shortBranchTracker).track(component);
        Mockito.verifyZeroInteractions(tracker);
        Mockito.verifyZeroInteractions(mergeBranchTracker);
    }

    @Test
    public void delegate_pull_request_tracker() {
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getType()).thenReturn(PULL_REQUEST);
        Mockito.when(analysisMetadataHolder.getBranch()).thenReturn(Mockito.mock(Branch.class));
        Mockito.when(analysisMetadataHolder.isShortLivingBranch()).thenReturn(true);
        underTest.track(component);
        Mockito.verify(shortBranchTracker).track(component);
        Mockito.verifyZeroInteractions(tracker);
        Mockito.verifyZeroInteractions(mergeBranchTracker);
    }
}

