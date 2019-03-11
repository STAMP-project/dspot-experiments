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
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class EditCommentActionTest {
    private static final long NOW = 10000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private System2 system2 = Mockito.mock(System2.class);

    private DbClient dbClient = dbTester.getDbClient();

    private IssueDbTester issueDbTester = new IssueDbTester(dbTester);

    private OperationResponseWriter responseWriter = Mockito.mock(OperationResponseWriter.class);

    private ArgumentCaptor<SearchResponseData> preloadedSearchResponseDataCaptor = ArgumentCaptor.forClass(SearchResponseData.class);

    private WsActionTester tester = new WsActionTester(new EditCommentAction(system2, userSession, dbClient, new org.sonar.server.issue.IssueFinder(dbClient, userSession), responseWriter));

    @Test
    public void edit_comment() {
        IssueDto issueDto = issueDbTester.insertIssue();
        UserDto user = dbTester.users().insertUser();
        IssueChangeDto commentDto = issueDbTester.insertComment(issueDto, user, "please fix it");
        loginWithBrowsePermission(user, UserRole.USER, issueDto);
        call(commentDto.getKey(), "please have a look");
        Mockito.verify(responseWriter).write(ArgumentMatchers.eq(issueDto.getKey()), preloadedSearchResponseDataCaptor.capture(), ArgumentMatchers.any(Request.class), ArgumentMatchers.any(Response.class));
        verifyContentOfPreloadedSearchResponseData(issueDto);
        IssueChangeDto issueComment = dbClient.issueChangeDao().selectCommentByKey(dbTester.getSession(), commentDto.getKey()).get();
        assertThat(issueComment.getChangeData()).isEqualTo("please have a look");
        assertThat(issueComment.getUpdatedAt()).isEqualTo(EditCommentActionTest.NOW);
    }

    @Test
    public void edit_comment_using_deprecated_key_parameter() {
        IssueDto issueDto = issueDbTester.insertIssue();
        UserDto user = dbTester.users().insertUser();
        IssueChangeDto commentDto = issueDbTester.insertComment(issueDto, user, "please fix it");
        loginWithBrowsePermission(user, UserRole.USER, issueDto);
        tester.newRequest().setParam("key", commentDto.getKey()).setParam("text", "please have a look").execute();
        Mockito.verify(responseWriter).write(ArgumentMatchers.eq(issueDto.getKey()), preloadedSearchResponseDataCaptor.capture(), ArgumentMatchers.any(Request.class), ArgumentMatchers.any(Response.class));
        verifyContentOfPreloadedSearchResponseData(issueDto);
        IssueChangeDto issueComment = dbClient.issueChangeDao().selectCommentByKey(dbTester.getSession(), commentDto.getKey()).get();
        assertThat(issueComment.getChangeData()).isEqualTo("please have a look");
        assertThat(issueComment.getUpdatedAt()).isEqualTo(EditCommentActionTest.NOW);
    }

    @Test
    public void fail_when_comment_does_not_belong_to_current_user() {
        IssueDto issueDto = issueDbTester.insertIssue();
        UserDto user = dbTester.users().insertUser();
        IssueChangeDto commentDto = issueDbTester.insertComment(issueDto, user, "please fix it");
        UserDto another = dbTester.users().insertUser();
        loginWithBrowsePermission(another, UserRole.USER, issueDto);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You can only edit your own comments");
        call(commentDto.getKey(), "please have a look");
    }

    @Test
    public void fail_when_comment_has_not_user() {
        IssueDto issueDto = issueDbTester.insertIssue();
        UserDto user = dbTester.users().insertUser();
        IssueChangeDto commentDto = issueDbTester.insertComment(issueDto, null, "please fix it");
        loginWithBrowsePermission(user, UserRole.USER, issueDto);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("You can only edit your own comments");
        call(commentDto.getKey(), "please have a look");
    }

    @Test
    public void fail_when_missing_comment_key() {
        userSession.logIn("john");
        expectedException.expect(IllegalArgumentException.class);
        call(null, "please fix it");
    }

    @Test
    public void fail_when_comment_does_not_exist() {
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
        UserDto user = dbTester.users().insertUser();
        IssueChangeDto commentDto = issueDbTester.insertComment(issueDto, user, "please fix it");
        loginWithBrowsePermission(user, UserRole.USER, issueDto);
        expectedException.expect(IllegalArgumentException.class);
        call(commentDto.getKey(), "");
    }

    @Test
    public void fail_when_not_authenticated() {
        expectedException.expect(UnauthorizedException.class);
        call("ABCD", "please fix it");
    }

    @Test
    public void fail_when_not_enough_permission() {
        IssueDto issueDto = issueDbTester.insertIssue();
        UserDto user = dbTester.users().insertUser();
        IssueChangeDto commentDto = issueDbTester.insertComment(issueDto, user, "please fix it");
        loginWithBrowsePermission(user, UserRole.CODEVIEWER, issueDto);
        expectedException.expect(ForbiddenException.class);
        call(commentDto.getKey(), "please have a look");
    }

    @Test
    public void test_definition() {
        WebService.Action action = tester.getDef();
        assertThat(action.key()).isEqualTo("edit_comment");
        assertThat(action.isPost()).isTrue();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.params()).hasSize(2);
        assertThat(action.responseExample()).isNotNull();
    }
}

