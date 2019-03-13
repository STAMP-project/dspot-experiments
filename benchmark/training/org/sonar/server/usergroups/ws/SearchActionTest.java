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
package org.sonar.server.usergroups.ws;


import MediaTypes.JSON;
import System2.INSTANCE;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.GroupDto;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class SearchActionTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WsActionTester ws = new WsActionTester(new SearchAction(db.getDbClient(), userSession, newGroupWsSupport(), new org.sonar.server.usergroups.DefaultGroupFinder(db.getDbClient())));

    @Test
    public void search_without_parameters() {
        insertDefaultGroup(db.getDefaultOrganization(), "users", 0);
        insertGroup(db.getDefaultOrganization(), "admins", 0);
        insertGroup(db.getDefaultOrganization(), "customer1", 0);
        insertGroup(db.getDefaultOrganization(), "customer2", 0);
        insertGroup(db.getDefaultOrganization(), "customer3", 0);
        loginAsDefaultOrgAdmin();
        SearchWsResponse response = call(ws.newRequest());
        assertThat(response.getGroupsList()).extracting(Group::getName, Group::getDescription, Group::getMembersCount).containsOnly(tuple("admins", "Admins", 0), tuple("customer1", "Customer1", 0), tuple("customer2", "Customer2", 0), tuple("customer3", "Customer3", 0), tuple("users", "Users", 0));
    }

    @Test
    public void search_with_members() {
        insertDefaultGroup(db.getDefaultOrganization(), "users", 5);
        insertGroup(db.getDefaultOrganization(), "admins", 1);
        insertGroup(db.getDefaultOrganization(), "customer1", 0);
        insertGroup(db.getDefaultOrganization(), "customer2", 4);
        insertGroup(db.getDefaultOrganization(), "customer3", 0);
        loginAsDefaultOrgAdmin();
        SearchWsResponse response = call(ws.newRequest());
        assertThat(response.getGroupsList()).extracting(Group::getName, Group::getDescription, Group::getMembersCount).containsOnly(tuple("admins", "Admins", 1), tuple("customer1", "Customer1", 0), tuple("customer2", "Customer2", 4), tuple("customer3", "Customer3", 0), tuple("users", "Users", 5));
    }

    @Test
    public void search_with_query() {
        insertDefaultGroup(db.getDefaultOrganization(), "users", 0);
        insertGroup(db.getDefaultOrganization(), "admins", 0);
        insertGroup(db.getDefaultOrganization(), "customer%_%/1", 0);
        insertGroup(db.getDefaultOrganization(), "customer%_%/2", 0);
        insertGroup(db.getDefaultOrganization(), "customer%_%/3", 0);
        loginAsDefaultOrgAdmin();
        SearchWsResponse response = call(ws.newRequest().setParam(TEXT_QUERY, "tomer%_%/"));
        assertThat(response.getGroupsList()).extracting(Group::getName, Group::getDescription, Group::getMembersCount).containsOnly(tuple("customer%_%/1", "Customer%_%/1", 0), tuple("customer%_%/2", "Customer%_%/2", 0), tuple("customer%_%/3", "Customer%_%/3", 0));
    }

    @Test
    public void search_with_paging() {
        insertDefaultGroup(db.getDefaultOrganization(), "users", 0);
        insertGroup(db.getDefaultOrganization(), "admins", 0);
        insertGroup(db.getDefaultOrganization(), "customer1", 0);
        insertGroup(db.getDefaultOrganization(), "customer2", 0);
        insertGroup(db.getDefaultOrganization(), "customer3", 0);
        loginAsDefaultOrgAdmin();
        SearchWsResponse response = call(ws.newRequest().setParam(PAGE_SIZE, "3"));
        assertThat(response.getPaging()).extracting(Paging::getPageIndex, Paging::getPageSize, Paging::getTotal).containsOnly(1, 3, 5);
        assertThat(response.getGroupsList()).extracting(Group::getName, Group::getDescription, Group::getMembersCount).containsOnly(tuple("admins", "Admins", 0), tuple("customer1", "Customer1", 0), tuple("customer2", "Customer2", 0));
        response = call(ws.newRequest().setParam(PAGE_SIZE, "3").setParam(PAGE, "2"));
        assertThat(response.getPaging()).extracting(Paging::getPageIndex, Paging::getPageSize, Paging::getTotal).containsOnly(2, 3, 5);
        assertThat(response.getGroupsList()).extracting(Group::getName, Group::getDescription, Group::getMembersCount).containsOnly(tuple("customer3", "Customer3", 0), tuple("users", "Users", 0));
        response = call(ws.newRequest().setParam(PAGE_SIZE, "3").setParam(PAGE, "3"));
        assertThat(response.getPaging()).extracting(Paging::getPageIndex, Paging::getPageSize, Paging::getTotal).containsOnly(3, 3, 5);
        assertThat(response.getGroupsList()).isEmpty();
    }

    @Test
    public void search_with_fields() {
        insertDefaultGroup(db.getDefaultOrganization(), "sonar-users", 0);
        loginAsDefaultOrgAdmin();
        assertThat(call(ws.newRequest()).getGroupsList()).extracting(Group::hasId, Group::hasName, Group::hasDescription, Group::hasMembersCount).containsOnly(tuple(true, true, true, true));
        assertThat(call(ws.newRequest().setParam(FIELDS, "")).getGroupsList()).extracting(Group::hasId, Group::hasName, Group::hasDescription, Group::hasMembersCount).containsOnly(tuple(true, true, true, true));
        assertThat(call(ws.newRequest().setParam(FIELDS, "name")).getGroupsList()).extracting(Group::hasId, Group::hasName, Group::hasDescription, Group::hasMembersCount).containsOnly(tuple(true, true, false, false));
        assertThat(call(ws.newRequest().setParam(FIELDS, "description")).getGroupsList()).extracting(Group::hasId, Group::hasName, Group::hasDescription, Group::hasMembersCount).containsOnly(tuple(true, false, true, false));
        assertThat(call(ws.newRequest().setParam(FIELDS, "membersCount")).getGroupsList()).extracting(Group::hasId, Group::hasName, Group::hasDescription, Group::hasMembersCount).containsOnly(tuple(true, false, false, true));
    }

    @Test
    public void search_in_organization() {
        OrganizationDto org = db.organizations().insert();
        GroupDto group = db.users().insertDefaultGroup(org, "users");
        // the group in default org is not returned
        db.users().insertDefaultGroup(db.getDefaultOrganization(), "users");
        loginAsDefaultOrgAdmin();
        userSession.addPermission(ADMINISTER, org);
        SearchWsResponse response = call(ws.newRequest().setParam("organization", org.getKey()));
        assertThat(response.getGroupsList()).extracting(Group::getId, Group::getName).containsOnly(tuple(group.getId().longValue(), "users"));
    }

    @Test
    public void return_default_group() {
        db.users().insertDefaultGroup(db.getDefaultOrganization(), "default");
        loginAsDefaultOrgAdmin();
        SearchWsResponse response = call(ws.newRequest());
        assertThat(response.getGroupsList()).extracting(Group::getName, Group::getDefault).containsOnly(tuple("default", true));
    }

    @Test
    public void fail_when_no_default_group() {
        db.users().insertGroup(db.getDefaultOrganization(), "users");
        loginAsDefaultOrgAdmin();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(String.format("Default group cannot be found on organization '%s'", db.getDefaultOrganization().getUuid()));
        call(ws.newRequest());
    }

    @Test
    public void fail_when_not_logged_in() {
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        call(ws.newRequest());
    }

    @Test
    public void test_json_example() {
        insertDefaultGroup(db.getDefaultOrganization(), "users", 17);
        insertGroup(db.getDefaultOrganization(), "administrators", 2);
        loginAsDefaultOrgAdmin();
        String response = ws.newRequest().setMediaType(JSON).execute().getInput();
        assertJson(response).ignoreFields("id").isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void verify_definition() {
        WebService.Action action = ws.getDef();
        assertThat(action.since()).isEqualTo("5.2");
        assertThat(action.isPost()).isFalse();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).extracting(WebService.Param::key).containsOnly("p", "q", "ps", "f", "organization");
        assertThat(action.param("f").possibleValues()).containsOnly("name", "description", "membersCount");
    }
}

