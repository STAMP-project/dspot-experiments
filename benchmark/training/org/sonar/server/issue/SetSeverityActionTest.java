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


import BulkChangeAction.ActionContext;
import FieldDiffs.Diff;
import Issue.RESOLUTION_FIXED;
import RuleType.CODE_SMELL;
import RuleType.SECURITY_HOTSPOT;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.FieldDiffs;
import org.sonar.core.issue.IssueChangeContext;
import org.sonar.db.DbTester;
import org.sonar.db.issue.IssueDto;
import org.sonar.server.issue.ws.BulkChangeAction;
import org.sonar.server.tester.AnonymousMockUserSession;
import org.sonar.server.tester.UserSessionRule;


public class SetSeverityActionTest {
    private static final Date NOW = new Date(10000000000L);

    private static final String USER_LOGIN = "john";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private IssueFieldsSetter issueUpdater = new IssueFieldsSetter();

    private SetSeverityAction action = new SetSeverityAction(issueUpdater, userSession);

    @Test
    public void set_severity() {
        IssueDto issueDto = newIssue().setSeverity(MAJOR);
        DefaultIssue issue = issueDto.toDefaultIssue();
        setUserWithBrowseAndAdministerIssuePermission(issueDto);
        BulkChangeAction.ActionContext context = new BulkChangeAction.ActionContext(issue, IssueChangeContext.createUser(SetSeverityActionTest.NOW, userSession.getUuid()), null);
        action.execute(ImmutableMap.of("severity", MINOR), context);
        assertThat(issue.severity()).isEqualTo(MINOR);
        assertThat(issue.isChanged()).isTrue();
        assertThat(issue.manualSeverity()).isTrue();
        assertThat(issue.updateDate()).isEqualTo(SetSeverityActionTest.NOW);
        assertThat(issue.mustSendNotifications()).isTrue();
        Map<String, FieldDiffs.Diff> change = issue.currentChange().diffs();
        assertThat(change.get("severity").newValue()).isEqualTo(MINOR);
        assertThat(change.get("severity").oldValue()).isEqualTo(MAJOR);
    }

    @Test
    public void fail_if_parameter_not_found() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing parameter : 'severity'");
        action.verify(ImmutableMap.of("unknwown", MINOR), Lists.newArrayList(), new AnonymousMockUserSession());
    }

    @Test
    public void support_only_unresolved_issues() {
        IssueDto issueDto = newIssue().setSeverity(MAJOR);
        DefaultIssue issue = issueDto.toDefaultIssue();
        setUserWithBrowseAndAdministerIssuePermission(issueDto);
        assertThat(action.supports(issue.setResolution(null))).isTrue();
        assertThat(action.supports(issue.setResolution(RESOLUTION_FIXED))).isFalse();
    }

    @Test
    public void doesnt_support_security_hotspots() {
        IssueDto issueDto = newIssue().setSeverity(MAJOR);
        DefaultIssue issue = issueDto.toDefaultIssue();
        setUserWithBrowseAndAdministerIssuePermission(issueDto);
        assertThat(action.supports(issue.setType(CODE_SMELL))).isTrue();
        assertThat(action.supports(issue.setType(SECURITY_HOTSPOT))).isFalse();
    }

    @Test
    public void support_only_issues_with_issue_admin_permission() {
        IssueDto authorizedIssue = newIssue().setSeverity(MAJOR);
        setUserWithBrowseAndAdministerIssuePermission(authorizedIssue);
        IssueDto unauthorizedIssue = newIssue().setSeverity(MAJOR);
        assertThat(action.supports(authorizedIssue.toDefaultIssue().setResolution(null))).isTrue();
        assertThat(action.supports(unauthorizedIssue.toDefaultIssue().setResolution(null))).isFalse();
    }
}

