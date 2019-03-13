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


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.tracking.Input;
import org.sonar.core.issue.tracking.NonClosedTracking;
import org.sonar.core.issue.tracking.Tracker;


public class MergeBranchTrackerExecutionTest {
    @Mock
    private TrackerRawInputFactory rawInputFactory;

    @Mock
    private TrackerMergeBranchInputFactory mergeInputFactory;

    @Mock
    private Tracker<DefaultIssue, DefaultIssue> tracker;

    @Mock
    private Component component;

    private MergeBranchTrackerExecution underTest;

    @Test
    public void testTracking() {
        Input<DefaultIssue> rawInput = Mockito.mock(Input.class);
        Input<DefaultIssue> mergeInput = Mockito.mock(Input.class);
        NonClosedTracking<DefaultIssue, DefaultIssue> result = Mockito.mock(NonClosedTracking.class);
        Mockito.when(rawInputFactory.create(component)).thenReturn(rawInput);
        Mockito.when(mergeInputFactory.create(component)).thenReturn(mergeInput);
        Mockito.when(tracker.trackNonClosed(rawInput, mergeInput)).thenReturn(result);
        assertThat(underTest.track(component)).isEqualTo(result);
    }
}

