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


import System2.INSTANCE;
import WebService.Action;
import java.util.Collections;
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
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.issue.IssueChangeDto;
import org.sonar.db.issue.IssueDbTester;
import org.sonar.db.issue.IssueDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.issue.IssueFieldsSetter;
import org.sonar.server.issue.IssueUpdater;
import org.sonar.server.issue.TestIssueChangePostProcessor;
import org.sonar.server.issue.WebIssueStorage;
import org.sonar.server.issue.index.IssueIndexer;
import org.sonar.server.notification.NotificationManager;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class AddCommentActionTest {
    private static final long NOW = 10000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private System2 system2 = Mockito.mock(System2.class);

    private DbClient dbClient = dbTester.getDbClient();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(dbTester);

    private IssueDbTester issueDbTester = new IssueDbTester(dbTester);

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), dbClient, new org.sonar.server.issue.index.IssueIteratorFactory(dbClient));

    private WebIssueStorage serverIssueStorage = new WebIssueStorage(system2, dbClient, new org.sonar.server.rule.DefaultRuleFinder(dbClient, defaultOrganizationProvider), issueIndexer);

    private TestIssueChangePostProcessor issueChangePostProcessor = new TestIssueChangePostProcessor();

    private IssueUpdater issueUpdater = new IssueUpdater(dbClient, serverIssueStorage, Mockito.mock(NotificationManager.class), issueChangePostProcessor);

    private OperationResponseWriter responseWriter = Mockito.mock(OperationResponseWriter.class);

    private ArgumentCaptor<SearchResponseData> preloadedSearchResponseDataCaptor = ArgumentCaptor.forClass(SearchResponseData.class);

    private WsActionTester tester = new WsActionTester(new AddCommentAction(system2, userSession, dbClient, new org.sonar.server.issue.IssueFinder(dbClient, userSession), issueUpdater, new IssueFieldsSetter(), responseWriter));

    @Test
    public void add_comment() {
        IssueDto issueDto = issueDbTester.insertIssue();
        loginWithBrowsePermission(issueDto, UserRole.USER);
        call(issueDto.getKey(), "please fix it");
        Mockito.verify(responseWriter).write(ArgumentMatchers.eq(issueDto.getKey()), preloadedSearchResponseDataCaptor.capture(), ArgumentMatchers.any(Request.class), ArgumentMatchers.any(Response.class));
        verifyContentOfPreloadedSearchResponseData(issueDto);
        IssueChangeDto issueComment = dbClient.issueChangeDao().selectByTypeAndIssueKeys(dbTester.getSession(), Collections.singletonList(issueDto.getKey()), TYPE_COMMENT).get(0);
        assertThat(issueComment.getKey()).isNotNull();
        assertThat(issueComment.getUserUuid()).isEqualTo(userSession.getUuid());
        assertThat(issueComment.getChangeType()).isEqualTo(TYPE_COMMENT);
        assertThat(issueComment.getChangeData()).isEqualTo("please fix it");
        assertThat(issueComment.getCreatedAt()).isNotNull();
        assertThat(issueComment.getUpdatedAt()).isNotNull();
        assertThat(issueComment.getIssueKey()).isEqualTo(issueDto.getKey());
        assertThat(issueComment.getIssueChangeCreationDate()).isNotNull();
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(dbTester.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getIssueUpdateTime()).isEqualTo(AddCommentActionTest.NOW);
        assertThat(issueChangePostProcessor.wasCalled()).isFalse();
    }

    @Test
    public void fail_when_missing_issue_key() {
        userSession.logIn("john");
        expectedException.expect(IllegalArgumentException.class);
        call(null, "please fix it");
    }

    @Test
    public void fail_when_issue_does_not_exist() {
        userSession.logIn("john");
        expectedException.expect(NotFoundException.class);
        call("ABCD", "please fix it");
    }

    @Test
    public void fail_when_missing_comment_text() {
        userSession.logIn("john");
        expectedException.expect(IllegalArgumentException.class);
        call("ABCD", null);
    }

    @Test
    public void fail_when_empty_comment_text() {
        IssueDto issueDto = issueDbTester.insertIssue();
        loginWithBrowsePermission(issueDto, UserRole.USER);
        expectedException.expect(IllegalArgumentException.class);
        call(issueDto.getKey(), "");
    }

    @Test
    public void fail_when_not_authenticated() {
        expectedException.expect(UnauthorizedException.class);
        call("ABCD", "please fix it");
    }

    @Test
    public void fail_when_not_enough_permission() {
        IssueDto issueDto = issueDbTester.insertIssue();
        loginWithBrowsePermission(issueDto, UserRole.CODEVIEWER);
        expectedException.expect(ForbiddenException.class);
        call(issueDto.getKey(), "please fix it");
    }

    @Test
    public void test_definition() {
        WebService.Action action = tester.getDef();
        assertThat(action.key()).isEqualTo("add_comment");
        assertThat(action.isPost()).isTrue();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.params()).hasSize(2);
        assertThat(action.responseExampleAsString()).isNotEmpty();
    }
}

