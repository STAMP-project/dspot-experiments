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
package org.sonar.server.issue.workflow;


import Function.Context;
import Issue.RESOLUTION_FIXED;
import Issue.RESOLUTION_REMOVED;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.issue.Issue;
import org.sonar.core.issue.DefaultIssue;


public class SetClosedTest {
    private Context context = Mockito.mock(Context.class);

    @Test
    public void should_resolve_as_fixed() {
        Issue issue = new DefaultIssue().setBeingClosed(true).setOnDisabledRule(false);
        Mockito.when(context.issue()).thenReturn(issue);
        SetClosed.INSTANCE.execute(context);
        Mockito.verify(context, Mockito.times(1)).setResolution(RESOLUTION_FIXED);
    }

    @Test
    public void should_resolve_as_removed_when_rule_is_disabled() {
        Issue issue = new DefaultIssue().setBeingClosed(true).setOnDisabledRule(true);
        Mockito.when(context.issue()).thenReturn(issue);
        SetClosed.INSTANCE.execute(context);
        Mockito.verify(context, Mockito.times(1)).setResolution(RESOLUTION_REMOVED);
    }

    @Test
    public void line_number_must_be_unset() {
        Issue issue = new DefaultIssue().setBeingClosed(true).setLine(10);
        Mockito.when(context.issue()).thenReturn(issue);
        SetClosed.INSTANCE.execute(context);
        Mockito.verify(context).unsetLine();
    }
}

