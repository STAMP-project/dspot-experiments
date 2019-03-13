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


import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService.Action;
import org.sonar.api.server.ws.WebService.Param;
import org.sonar.api.utils.System2;
import org.sonar.api.web.UserRole;
import org.sonar.core.issue.FieldDiffs;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
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


public class SetTagsActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private System2 system2 = Mockito.mock(System2.class);

    private DbClient dbClient = db.getDbClient();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private OperationResponseWriter responseWriter = Mockito.mock(OperationResponseWriter.class);

    private IssueIndexer issueIndexer = new IssueIndexer(es.client(), dbClient, new org.sonar.server.issue.index.IssueIteratorFactory(dbClient));

    private ArgumentCaptor<SearchResponseData> preloadedSearchResponseDataCaptor = ArgumentCaptor.forClass(SearchResponseData.class);

    private TestIssueChangePostProcessor issueChangePostProcessor = new TestIssueChangePostProcessor();

    private WsActionTester ws = new WsActionTester(new SetTagsAction(userSession, dbClient, new org.sonar.server.issue.IssueFinder(dbClient, userSession), new IssueFieldsSetter(), new org.sonar.server.issue.IssueUpdater(dbClient, new org.sonar.server.issue.WebIssueStorage(system2, dbClient, new org.sonar.server.rule.DefaultRuleFinder(dbClient, defaultOrganizationProvider), issueIndexer), Mockito.mock(NotificationManager.class), issueChangePostProcessor), responseWriter));

    @Test
    public void set_tags() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        call(issueDto.getKey(), "bug", "todo");
        Mockito.verify(responseWriter).write(ArgumentMatchers.eq(issueDto.getKey()), preloadedSearchResponseDataCaptor.capture(), ArgumentMatchers.any(Request.class), ArgumentMatchers.any(Response.class));
        verifyContentOfPreloadedSearchResponseData(issueDto);
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(db.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getTags()).containsOnly("bug", "todo");
        assertThat(issueChangePostProcessor.wasCalled()).isFalse();
    }

    @Test
    public void remove_existing_tags_when_value_is_not_set() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        call(issueDto.getKey());
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(db.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getTags()).isEmpty();
        assertThat(issueChangePostProcessor.wasCalled()).isFalse();
    }

    @Test
    public void remove_existing_tags_when_value_is_empty_string() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        call(issueDto.getKey(), "");
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(db.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getTags()).isEmpty();
    }

    @Test
    public void set_tags_using_deprecated_key_param() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        ws.newRequest().setParam("key", issueDto.getKey()).setParam("tags", "bug").execute();
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(db.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getTags()).containsOnly("bug");
    }

    @Test
    public void tags_are_stored_as_lowercase() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        call(issueDto.getKey(), "bug", "Convention");
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(db.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getTags()).containsOnly("bug", "convention");
    }

    @Test
    public void empty_tags_are_ignored() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        call(issueDto.getKey(), "security", "", "convention");
        IssueDto issueReloaded = dbClient.issueDao().selectByKey(db.getSession(), issueDto.getKey()).get();
        assertThat(issueReloaded.getTags()).containsOnly("security", "convention");
    }

    @Test
    public void insert_entry_in_changelog_when_setting_tags() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        call(issueDto.getKey(), "new-tag");
        List<FieldDiffs> fieldDiffs = dbClient.issueChangeDao().selectChangelogByIssue(db.getSession(), issueDto.getKey());
        assertThat(fieldDiffs).hasSize(1);
        assertThat(fieldDiffs.get(0).diffs()).hasSize(1);
        assertThat(fieldDiffs.get(0).diffs().get("tags").oldValue()).isEqualTo("old-tag");
        assertThat(fieldDiffs.get(0).diffs().get("tags").newValue()).isEqualTo("new-tag");
    }

    @Test
    public void fail_when_tag_use_bad_format() {
        IssueDto issueDto = db.issues().insertIssue(newIssue().setTags(Collections.singletonList("old-tag")));
        logIn(issueDto);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Tags 'pol op' are invalid. Rule tags accept only the characters: a-z, 0-9, '+', '-', '#', '.'");
        call(issueDto.getKey(), "pol op");
    }

    @Test
    public void fail_when_not_authenticated() {
        expectedException.expect(UnauthorizedException.class);
        call("ABCD", "bug");
    }

    @Test
    public void fail_when_missing_browse_permission() {
        IssueDto issueDto = db.issues().insertIssue();
        logInAndAddProjectPermission(issueDto, UserRole.ISSUE_ADMIN);
        expectedException.expect(ForbiddenException.class);
        call(issueDto.getKey(), "bug");
    }

    @Test
    public void test_definition() {
        Action action = ws.getDef();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.isPost()).isTrue();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.params()).hasSize(2);
        Param query = action.param("issue");
        assertThat(query.isRequired()).isTrue();
        assertThat(query.description()).isNotEmpty();
        assertThat(query.exampleValue()).isNotEmpty();
        Param pageSize = action.param("tags");
        assertThat(pageSize.isRequired()).isFalse();
        assertThat(pageSize.defaultValue()).isNull();
        assertThat(pageSize.description()).isNotEmpty();
        assertThat(pageSize.exampleValue()).isNotEmpty();
    }
}

