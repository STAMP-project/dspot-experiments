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
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.core.issue.IssueChangeContext;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.tester.UserSessionRule;


public class AssignActionTest {
    private static final String ISSUE_CURRENT_ASSIGNEE_UUID = "current assignee uuid";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private IssueChangeContext issueChangeContext = IssueChangeContext.createUser(new Date(), "user_uuid");

    private DefaultIssue issue = new DefaultIssue().setKey("ABC").setAssigneeUuid(AssignActionTest.ISSUE_CURRENT_ASSIGNEE_UUID);

    private ComponentDto project;

    private Context context;

    private OrganizationDto issueOrganizationDto;

    private AssignAction underTest = new AssignAction(db.getDbClient(), new IssueFieldsSetter());

    @Test
    public void assign_issue() {
        UserDto assignee = db.users().insertUser("john");
        db.organizations().addMember(issueOrganizationDto, assignee);
        Map<String, Object> properties = new HashMap<>(ImmutableMap.of("assignee", "john"));
        underTest.verify(properties, Collections.emptyList(), userSession);
        boolean executeResult = underTest.execute(properties, context);
        assertThat(executeResult).isTrue();
        assertThat(issue.assignee()).isEqualTo(assignee.getUuid());
    }

    @Test
    public void unassign_issue_if_assignee_is_empty() {
        Map<String, Object> properties = new HashMap<>(ImmutableMap.of("assignee", ""));
        underTest.verify(properties, Collections.emptyList(), userSession);
        boolean executeResult = underTest.execute(properties, context);
        assertThat(executeResult).isTrue();
        assertThat(issue.assignee()).isNull();
    }

    @Test
    public void unassign_issue_if_assignee_is_null() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("assignee", null);
        underTest.verify(properties, Collections.emptyList(), userSession);
        boolean executeResult = underTest.execute(properties, context);
        assertThat(executeResult).isTrue();
        assertThat(issue.assignee()).isNull();
    }

    @Test
    public void does_not_assign_issue_when_assignee_is_not_member_of_project_issue_organization() {
        OrganizationDto otherOrganizationDto = db.organizations().insert();
        UserDto assignee = db.users().insertUser("john");
        // User is not member of the organization of the issue
        db.organizations().addMember(otherOrganizationDto, assignee);
        Map<String, Object> properties = new HashMap<>(ImmutableMap.of("assignee", "john"));
        underTest.verify(properties, Collections.emptyList(), userSession);
        boolean executeResult = underTest.execute(properties, context);
        assertThat(executeResult).isFalse();
    }

    @Test
    public void fail_if_assignee_is_not_verified() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Assignee is missing from the execution parameters");
        underTest.execute(Collections.emptyMap(), context);
    }

    @Test
    public void fail_if_assignee_does_not_exists() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Unknown user: arthur");
        underTest.verify(ImmutableMap.of("assignee", "arthur"), Collections.singletonList(issue), userSession);
    }

    @Test
    public void fail_if_assignee_is_disabled() {
        db.users().insertUser(( user) -> user.setLogin("arthur").setActive(false));
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Unknown user: arthur");
        underTest.verify(new HashMap(ImmutableMap.of("assignee", "arthur")), Collections.singletonList(issue), userSession);
    }

    @Test
    public void support_only_unresolved_issues() {
        assertThat(underTest.supports(new DefaultIssue().setResolution(null))).isTrue();
        assertThat(underTest.supports(new DefaultIssue().setResolution(RESOLUTION_FIXED))).isFalse();
    }
}

