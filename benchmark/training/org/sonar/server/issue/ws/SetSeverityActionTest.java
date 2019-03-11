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


import WebService.Action;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.api.web.UserRole;
import org.sonar.core.issue.FieldDiffs;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.issue.IssueDbTester;
import org.sonar.db.issue.IssueDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.issue.IssueFieldsSetter;
import org.sonar.server.issue.TestIssueChangePostProcessor;
import org.sonar.server.issue.index.IssueIndexer;
import org.sonar.server.notification.NotificationManager;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class SetSeverityActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private System2 system2 = Mockito.mock(System2.class);

    private DbClient dbClient = dbTester.getDbClient();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(dbTester);

    private IssueDbTester issueDbTester = new IssueDbTester(dbTester);

    private OperationResponseWriter responseWriter = Mockito.mock(OperationResponseWriter.class);

    private ArgumentCaptor<SearchResponseData> preloadedSearchResponseDataCaptor = ArgumentCaptor.forClass(SearchResponseData.class);

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), dbClient, new org.sonar.server.issue.index.IssueIteratorFactory(dbClient));

    private TestIssueChangePostProcessor issueChangePostProcessor = new TestIssueChangePostProcessor();

    private WsActionTester tester = new WsActionTester(new SetSeverityAction(userSession, dbClient, new org.sonar.server.issue.IssueFinder(dbClient, userSession), new IssueFieldsSetter(), new org.sonar.server.issue.IssueUpdater(dbClient, new org.sonar.server.issue.WebIssueStorage(system2, dbClient, new org.sonar.server.rule.DefaultRuleFinder(dbClient, defaultOrganizationProvider), issueIndexer), Mockito.mock(NotificationManager.class), issueChangePostProcessor), responseWriter));

    @Test
    public void set_severity() {
        IssueDto issueDto = issueDbTester.insertIssue(newIssue().setSeverity(MAJOR));
        setUserWithBrowseAndAdministerIssuePermission(issueDto);
        call(issueDto.getKey(), MINOR);
        Mockito.verify(responseWriter).write(ArgumentMatchers.eq(issueDto.getKey()), preloadedSearchResponseDataCaptor.capture(), ArgumentMatchers.any(Request.class), ArgumentMatchers.any(Response.class));
        verifyContentOfPreloadedSearchResponseData(issueDto);
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(dbTester.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getSeverity()).isEqualTo(MINOR);
        assertThat(issueReloaded.isManualSeverity()).isTrue();
        assertThat(issueChangePostProcessor.calledComponents()).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(issueDto.getComponentUuid());
    }

    @Test
    public void insert_entry_in_changelog_when_setting_severity() {
        IssueDto issueDto = issueDbTester.insertIssue(newIssue().setSeverity(MAJOR));
        setUserWithBrowseAndAdministerIssuePermission(issueDto);
        call(issueDto.getKey(), MINOR);
        List<FieldDiffs> fieldDiffs = dbClient.issueChangeDao().selectChangelogByIssue(dbTester.getSession(), issueDto.getKey());
        assertThat(fieldDiffs).hasSize(1);
        assertThat(fieldDiffs.get(0).diffs()).hasSize(1);
        assertThat(fieldDiffs.get(0).diffs().get("severity").newValue()).isEqualTo(MINOR);
        assertThat(fieldDiffs.get(0).diffs().get("severity").oldValue()).isEqualTo(MAJOR);
    }

    @Test
    public void fail_if_bad_severity() {
        IssueDto issueDto = issueDbTester.insertIssue(newIssue().setSeverity("unknown"));
        setUserWithBrowseAndAdministerIssuePermission(issueDto);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value of parameter 'severity' (unknown) must be one of: [INFO, MINOR, MAJOR, CRITICAL, BLOCKER]");
        call(issueDto.getKey(), "unknown");
    }

    @Test
    public void fail_when_not_authenticated() {
        expectedException.expect(UnauthorizedException.class);
        call("ABCD", MAJOR);
    }

    @Test
    public void fail_when_missing_browse_permission() {
        IssueDto issueDto = issueDbTester.insertIssue();
        logInAndAddProjectPermission(issueDto, UserRole.ISSUE_ADMIN);
        expectedException.expect(ForbiddenException.class);
        call(issueDto.getKey(), MAJOR);
    }

    @Test
    public void fail_when_missing_administer_issue_permission() {
        IssueDto issueDto = issueDbTester.insertIssue();
        logInAndAddProjectPermission(issueDto, UserRole.USER);
        expectedException.expect(ForbiddenException.class);
        call(issueDto.getKey(), MAJOR);
    }

    @Test
    public void test_definition() {
        WebService.Action action = tester.getDef();
        assertThat(action.key()).isEqualTo("set_severity");
        assertThat(action.isPost()).isTrue();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.params()).hasSize(2);
        assertThat(action.responseExample()).isNotNull();
    }
}

