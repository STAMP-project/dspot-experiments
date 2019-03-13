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
package org.sonar.server.user.ws;


import UserIndexDefinition.INDEX_TYPE_USER;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.db.DbTester;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.CredentialsLocalAuthentication;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.OrganizationUpdater;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.organization.TestOrganizationFlags;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.user.NewUserNotifier;
import org.sonar.server.user.index.UserIndexer;
import org.sonar.server.user.ws.CreateAction.CreateRequest;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Users.CreateWsResponse;


public class CreateActionTest {
    private static final String DEFAULT_GROUP_NAME = "sonar-users";

    private MapSettings settings = new MapSettings();

    private System2 system2 = new AlwaysIncreasingSystem2();

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserIndexer userIndexer = new UserIndexer(db.getDbClient(), es.client());

    private GroupDto defaultGroupInDefaultOrg;

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private TestOrganizationFlags organizationFlags = TestOrganizationFlags.standalone();

    private OrganizationUpdater organizationUpdater = Mockito.mock(OrganizationUpdater.class);

    private CredentialsLocalAuthentication localAuthentication = new CredentialsLocalAuthentication(db.getDbClient());

    private WsActionTester tester = new WsActionTester(new CreateAction(db.getDbClient(), new org.sonar.server.user.UserUpdater(system2, Mockito.mock(NewUserNotifier.class), db.getDbClient(), userIndexer, organizationFlags, defaultOrganizationProvider, organizationUpdater, new org.sonar.server.usergroups.DefaultGroupFinder(db.getDbClient()), settings.asConfig(), localAuthentication), userSessionRule));

    @Test
    public void create_user() {
        logInAsSystemAdministrator();
        CreateWsResponse response = call(CreateRequest.builder().setLogin("john").setName("John").setEmail("john@email.com").setScmAccounts(Collections.singletonList("jn")).setPassword("1234").build());
        assertThat(response.getUser()).extracting(User::getLogin, User::getName, User::getEmail, User::getScmAccountsList, User::getLocal).containsOnly("john", "John", "john@email.com", Collections.singletonList("jn"), true);
        // exists in index
        assertThat(es.client().prepareSearch(INDEX_TYPE_USER).setQuery(boolQuery().must(termQuery(FIELD_LOGIN, "john")).must(termQuery(FIELD_NAME, "John")).must(termQuery(FIELD_EMAIL, "john@email.com")).must(termQuery(FIELD_SCM_ACCOUNTS, "jn"))).get().getHits().getHits()).hasSize(1);
        // exists in db
        Optional<UserDto> dbUser = db.users().selectUserByLogin("john");
        assertThat(dbUser).isPresent();
        assertThat(dbUser.get().isRoot()).isFalse();
        // member of default group in default organization
        assertThat(db.users().selectGroupIdsOfUser(dbUser.get())).containsOnly(defaultGroupInDefaultOrg.getId());
    }

    @Test
    public void create_user_calls_create_personal_organization_if_personal_organizations_are_enabled() {
        logInAsSystemAdministrator();
        enableCreatePersonalOrg(true);
        assertACallToOrganizationCreationWhenUserIsCreated();
    }

    @Test
    public void create_user_calls_create_personal_organization_if_personal_organizations_are_disabled() {
        logInAsSystemAdministrator();
        enableCreatePersonalOrg(false);
        assertACallToOrganizationCreationWhenUserIsCreated();
    }

    @Test
    public void create_user_associates_him_to_default_organization() {
        logInAsSystemAdministrator();
        enableCreatePersonalOrg(true);
        call(CreateRequest.builder().setLogin("john").setName("John").setPassword("1234").build());
        Optional<UserDto> dbUser = db.users().selectUserByLogin("john");
        assertThat(dbUser).isPresent();
        assertThat(db.getDbClient().organizationMemberDao().select(db.getSession(), defaultOrganizationProvider.get().getUuid(), dbUser.get().getId())).isPresent();
    }

    @Test
    public void create_local_user() {
        logInAsSystemAdministrator();
        call(CreateRequest.builder().setLogin("john").setName("John").setPassword("1234").setLocal(true).build());
        assertThat(db.users().selectUserByLogin("john").get()).extracting(UserDto::isLocal, UserDto::getExternalIdentityProvider, UserDto::getExternalLogin, UserDto::isRoot).containsOnly(true, "sonarqube", "john", false);
    }

    @Test
    public void create_none_local_user() {
        logInAsSystemAdministrator();
        call(CreateRequest.builder().setLogin("john").setName("John").setLocal(false).build());
        assertThat(db.users().selectUserByLogin("john").get()).extracting(UserDto::isLocal, UserDto::getExternalIdentityProvider, UserDto::getExternalLogin, UserDto::isRoot).containsOnly(false, "sonarqube", "john", false);
    }

    @Test
    public void create_user_with_comma_in_scm_account() {
        logInAsSystemAdministrator();
        CreateWsResponse response = call(CreateRequest.builder().setLogin("john").setName("John").setEmail("john@email.com").setScmAccounts(Collections.singletonList("j,n")).setPassword("1234").build());
        assertThat(response.getUser().getScmAccountsList()).containsOnly("j,n");
    }

    @Test
    public void create_user_with_empty_email() {
        logInAsSystemAdministrator();
        call(CreateRequest.builder().setLogin("john").setName("John").setPassword("1234").setEmail("").build());
        assertThat(db.users().selectUserByLogin("john").get()).extracting(UserDto::getExternalLogin).containsOnly("john");
    }

    @Test
    public void create_user_with_deprecated_scmAccounts_parameter() {
        logInAsSystemAdministrator();
        tester.newRequest().setParam("login", "john").setParam("name", "John").setParam("password", "1234").setParam("scmAccounts", "jn").execute();
        assertThat(db.users().selectUserByLogin("john").get().getScmAccountsAsList()).containsOnly("jn");
    }

    @Test
    public void create_user_with_deprecated_scm_accounts_parameter() {
        logInAsSystemAdministrator();
        tester.newRequest().setParam("login", "john").setParam("name", "John").setParam("password", "1234").setParam("scm_accounts", "jn").execute();
        assertThat(db.users().selectUserByLogin("john").get().getScmAccountsAsList()).containsOnly("jn");
    }

    @Test
    public void reactivate_user() {
        logInAsSystemAdministrator();
        db.users().insertUser(UserTesting.newUserDto("john", "John", "john@email.com").setActive(false));
        userIndexer.indexOnStartup(new HashSet());
        call(CreateRequest.builder().setLogin("john").setName("John").setEmail("john@email.com").setScmAccounts(Collections.singletonList("jn")).setPassword("1234").build());
        assertThat(db.users().selectUserByLogin("john").get().isActive()).isTrue();
    }

    @Test
    public void fail_to_reactivate_user_when_active_user_exists() {
        logInAsSystemAdministrator();
        UserDto user = db.users().insertUser();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("An active user with login '%s' already exists", user.getLogin()));
        call(CreateRequest.builder().setLogin(user.getLogin()).setName("John").setPassword("1234").build());
    }

    @Test
    public void fail_when_missing_login() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Login is mandatory and must not be empty");
        call(CreateRequest.builder().setLogin(null).setName("John").setPassword("1234").build());
    }

    @Test
    public void fail_when_login_is_too_short() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'login' length (1) is shorter than the minimum authorized (2)");
        call(CreateRequest.builder().setLogin("a").setName("John").setPassword("1234").build());
    }

    @Test
    public void fail_when_missing_name() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Name is mandatory and must not be empty");
        call(CreateRequest.builder().setLogin("john").setName(null).setPassword("1234").build());
    }

    @Test
    public void fail_when_missing_password() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Password is mandatory and must not be empty");
        call(CreateRequest.builder().setLogin("john").setName("John").setPassword(null).build());
    }

    @Test
    public void fail_when_password_is_set_on_none_local_user() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Password should only be set on local user");
        call(CreateRequest.builder().setLogin("john").setName("John").setPassword("1234").setLocal(false).build());
    }

    @Test
    public void fail_when_email_is_invalid() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Email 'invalid-email' is not valid");
        call(CreateRequest.builder().setLogin("pipo").setName("John").setPassword("1234").setEmail("invalid-email").build());
    }

    @Test
    public void throw_ForbiddenException_if_not_system_administrator() {
        userSessionRule.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("");
        expectedException.expect(ForbiddenException.class);
        executeRequest("john");
    }
}

