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
package org.sonar.server.issue.ws;


import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.issue.IssueDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.issue.IssueFieldsSetter;
import org.sonar.server.issue.TestIssueChangePostProcessor;
import org.sonar.server.issue.index.IssueIndexer;
import org.sonar.server.notification.NotificationManager;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class AssignActionTest {
    private static final String PREVIOUS_ASSIGNEE = "previous";

    private static final String CURRENT_USER_LOGIN = "john";

    private static final String CURRENT_USER_UUID = "1";

    private static final long PAST = 10000000000L;

    private static final long NOW = 50000000000L;

    private TestSystem2 system2 = new TestSystem2().setNow(AssignActionTest.NOW);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create(system2);

    public DbClient dbClient = db.getDbClient();

    private DbSession session = db.getSession();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), dbClient, new org.sonar.server.issue.index.IssueIteratorFactory(dbClient));

    private OperationResponseWriter responseWriter = Mockito.mock(OperationResponseWriter.class);

    private TestIssueChangePostProcessor issueChangePostProcessor = new TestIssueChangePostProcessor();

    private AssignAction underTest = new AssignAction(system2, userSession, dbClient, new org.sonar.server.issue.IssueFinder(dbClient, userSession), new IssueFieldsSetter(), new org.sonar.server.issue.IssueUpdater(dbClient, new org.sonar.server.issue.WebIssueStorage(system2, dbClient, new org.sonar.server.rule.DefaultRuleFinder(dbClient, defaultOrganizationProvider), issueIndexer), Mockito.mock(NotificationManager.class), issueChangePostProcessor), responseWriter);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void assign_to_someone() {
        IssueDto issue = newIssueWithBrowsePermission();
        UserDto arthur = insertUser("arthur");
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", "arthur").execute();
        checkIssueAssignee(issue.getKey(), arthur.getUuid());
        Optional<IssueDto> optionalIssueDto = dbClient.issueDao().selectByKey(session, issue.getKey());
        assertThat(optionalIssueDto).isPresent();
        assertThat(optionalIssueDto.get().getAssigneeUuid()).isEqualTo(arthur.getUuid());
        assertThat(issueChangePostProcessor.wasCalled()).isFalse();
    }

    @Test
    public void assign_to_me() {
        IssueDto issue = newIssueWithBrowsePermission();
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", "_me").execute();
        checkIssueAssignee(issue.getKey(), AssignActionTest.CURRENT_USER_UUID);
        Optional<IssueDto> optionalIssueDto = dbClient.issueDao().selectByKey(session, issue.getKey());
        assertThat(optionalIssueDto).isPresent();
        assertThat(optionalIssueDto.get().getAssigneeUuid()).isEqualTo(AssignActionTest.CURRENT_USER_UUID);
        assertThat(issueChangePostProcessor.wasCalled()).isFalse();
    }

    @Test
    public void assign_to_me_using_deprecated_me_param() {
        IssueDto issue = newIssueWithBrowsePermission();
        ws.newRequest().setParam("issue", issue.getKey()).setParam("me", "true").execute();
        checkIssueAssignee(issue.getKey(), AssignActionTest.CURRENT_USER_UUID);
        Optional<IssueDto> optionalIssueDto = dbClient.issueDao().selectByKey(session, issue.getKey());
        assertThat(optionalIssueDto).isPresent();
        assertThat(optionalIssueDto.get().getAssigneeUuid()).isEqualTo(AssignActionTest.CURRENT_USER_UUID);
    }

    @Test
    public void unassign() {
        IssueDto issue = newIssueWithBrowsePermission();
        ws.newRequest().setParam("issue", issue.getKey()).execute();
        checkIssueAssignee(issue.getKey(), null);
        Optional<IssueDto> optionalIssueDto = dbClient.issueDao().selectByKey(session, issue.getKey());
        assertThat(optionalIssueDto).isPresent();
        assertThat(optionalIssueDto.get().getAssigneeUuid()).isNull();
        assertThat(issueChangePostProcessor.wasCalled()).isFalse();
    }

    @Test
    public void unassign_with_empty_assignee_param() {
        IssueDto issue = newIssueWithBrowsePermission();
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", "").execute();
        checkIssueAssignee(issue.getKey(), null);
        Optional<IssueDto> optionalIssueDto = dbClient.issueDao().selectByKey(session, issue.getKey());
        assertThat(optionalIssueDto).isPresent();
        assertThat(optionalIssueDto.get().getAssigneeUuid()).isNull();
        assertThat(issueChangePostProcessor.wasCalled()).isFalse();
    }

    @Test
    public void nothing_to_do_when_new_assignee_is_same_as_old_one() {
        UserDto user = insertUser("Bob");
        IssueDto issue = newIssue(user.getUuid());
        setUserWithBrowsePermission(issue);
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", user.getLogin()).execute();
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(db.getSession(), issue.getKey()).get();
        assertThat(issueReloaded.getAssigneeUuid()).isEqualTo(user.getUuid());
        assertThat(issueReloaded.getUpdatedAt()).isEqualTo(AssignActionTest.PAST);
        assertThat(issueReloaded.getIssueUpdateTime()).isEqualTo(AssignActionTest.PAST);
    }

    @Test
    public void fail_when_assignee_does_not_exist() {
        IssueDto issue = newIssueWithBrowsePermission();
        expectedException.expect(NotFoundException.class);
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", "unknown").execute();
    }

    @Test
    public void fail_when_trying_to_assign_hotspot() {
        IssueDto issueDto = db.issues().insertIssue(( i) -> i.setType(RuleType.SECURITY_HOTSPOT));
        setUserWithBrowsePermission(issueDto);
        UserDto arthur = insertUser("arthur");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("It is not allowed to assign a security hotspot");
        ws.newRequest().setParam("issue", issueDto.getKey()).setParam("assignee", "arthur").execute();
    }

    @Test
    public void fail_when_assignee_is_disabled() {
        IssueDto issue = newIssueWithBrowsePermission();
        db.users().insertUser(( user) -> user.setActive(false));
        expectedException.expect(NotFoundException.class);
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", "unknown").execute();
    }

    @Test
    public void fail_when_not_authenticated() {
        IssueDto issue = newIssue(AssignActionTest.PREVIOUS_ASSIGNEE);
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", "_me").execute();
    }

    @Test
    public void fail_when_missing_browse_permission() {
        IssueDto issue = newIssue(AssignActionTest.PREVIOUS_ASSIGNEE);
        setUserWithPermission(issue, UserRole.CODEVIEWER);
        expectedException.expect(ForbiddenException.class);
        ws.newRequest().setParam("issue", issue.getKey()).setParam("assignee", "_me").execute();
    }

    @Test
    public void fail_when_assignee_is_not_member_of_organization_of_project_issue() {
        OrganizationDto org = db.organizations().insert(( organizationDto) -> organizationDto.setKey("Organization key"));
        IssueDto issueDto = db.issues().insertIssue(org, ( i) -> i.setType(RuleType.CODE_SMELL));
        setUserWithBrowsePermission(issueDto);
        OrganizationDto otherOrganization = db.organizations().insert();
        UserDto assignee = db.users().insertUser("arthur");
        db.organizations().addMember(otherOrganization, assignee);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User 'arthur' is not member of organization 'Organization key'");
        ws.newRequest().setParam("issue", issueDto.getKey()).setParam("assignee", "arthur").execute();
    }
}

