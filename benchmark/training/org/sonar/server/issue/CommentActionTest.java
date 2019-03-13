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
package org.sonar.server.issue;


import Action.Context;
import Issue.RESOLUTION_FIXED;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.server.tester.AnonymousMockUserSession;


public class CommentActionTest {
    private CommentAction action;

    private IssueFieldsSetter issueUpdater = Mockito.mock(IssueFieldsSetter.class);

    @Test
    public void should_execute() {
        String comment = "My bulk change comment";
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("comment", comment);
        DefaultIssue issue = Mockito.mock(DefaultIssue.class);
        Action.Context context = Mockito.mock(Context.class);
        Mockito.when(context.issue()).thenReturn(issue);
        action.execute(properties, context);
        Mockito.verify(issueUpdater).addComment(ArgumentMatchers.eq(issue), ArgumentMatchers.eq(comment), ArgumentMatchers.any());
    }

    @Test
    public void should_verify_fail_if_parameter_not_found() {
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("unknwown", "unknown value");
        try {
            action.verify(properties, Lists.newArrayList(), new AnonymousMockUserSession());
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class).hasMessage("Missing parameter : 'comment'");
        }
        Mockito.verifyZeroInteractions(issueUpdater);
    }

    @Test
    public void should_support_all_issues() {
        assertThat(action.supports(new DefaultIssue().setResolution(null))).isTrue();
        assertThat(action.supports(new DefaultIssue().setResolution(RESOLUTION_FIXED))).isTrue();
    }
}

