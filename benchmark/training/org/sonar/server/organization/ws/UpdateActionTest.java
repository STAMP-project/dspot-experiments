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
package org.sonar.server.organization.ws;


import Organizations.UpdateWsResponse;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.organization.OrganizationValidationImpl;
import org.sonar.server.organization.TestOrganizationFlags;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Organizations;


public class UpdateActionTest {
    private static final String SOME_KEY = "key";

    private static final long DATE_1 = 1200000L;

    private static final long DATE_2 = 5600000L;

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public DbTester dbTester = DbTester.create(system2);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create();

    private DbClient dbClient = db.getDbClient();

    private TestOrganizationFlags organizationFlags = TestOrganizationFlags.standalone().setEnabled(true);

    private UpdateAction underTest = new UpdateAction(userSession, new OrganizationsWsSupport(new OrganizationValidationImpl(), dbClient), dbTester.getDbClient(), organizationFlags);

    private WsActionTester wsTester = new WsActionTester(underTest);

    @Test
    public void verify_define() {
        WebService.Action action = wsTester.getDef();
        assertThat(action.key()).isEqualTo("update");
        assertThat(action.isPost()).isTrue();
        assertThat(action.description()).isEqualTo(("Update an organization.<br/>" + "Require 'Administer System' permission. Organization support must be enabled."));
        assertThat(action.isInternal()).isTrue();
        assertThat(action.since()).isEqualTo("6.2");
        assertThat(action.handler()).isEqualTo(underTest);
        assertThat(action.params()).hasSize(5);
        assertThat(action.responseExample()).isNull();
        assertThat(action.param("key")).matches(( param) -> param.isRequired()).matches(( param) -> "foo-company".equals(param.exampleValue())).matches(( param) -> "Organization key".equals(param.description()));
        assertThat(action.param("name")).matches(( param) -> !(param.isRequired())).matches(( param) -> "Foo Company".equals(param.exampleValue())).matches(( param) -> param.minimumLength().equals(1)).matches(( param) -> param.maximumLength().equals(255)).matches(( param) -> (param.description()) != null);
        assertThat(action.param("description")).matches(( param) -> !(param.isRequired())).matches(( param) -> "The Foo company produces quality software for Bar.".equals(param.exampleValue())).matches(( param) -> (param.description()) != null);
        assertThat(action.param("url")).matches(( param) -> !(param.isRequired())).matches(( param) -> "https://www.foo.com".equals(param.exampleValue())).matches(( param) -> (param.description()) != null);
        assertThat(action.param("avatar")).matches(( param) -> !(param.isRequired())).matches(( param) -> "https://www.foo.com/foo.png".equals(param.exampleValue())).matches(( param) -> (param.description()) != null);
    }

    @Test
    public void request_fails_with_IllegalStateException_if_organization_support_is_disabled() {
        organizationFlags.setEnabled(false);
        userSession.logIn();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Organization support is disabled");
        wsTester.newRequest().execute();
    }

    @Test
    public void request_succeeds_if_user_is_organization_administrator() {
        OrganizationDto dto = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(dto);
        verifyResponseAndDb(executeKeyRequest(dto.getKey(), "ab"), dto, "ab", UpdateActionTest.DATE_2);
    }

    @Test
    public void request_succeeds_if_user_is_administrator_of_specified_organization() {
        OrganizationDto dto = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(dto);
        verifyResponseAndDb(executeKeyRequest(dto.getKey(), "ab"), dto, "ab", UpdateActionTest.DATE_2);
    }

    @Test
    public void request_fails_with_UnauthorizedException_when_user_is_not_logged_in() {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        wsTester.newRequest().execute();
    }

    @Test
    public void request_fails_if_user_is_not_system_administrator_and_is_not_organization_administrator() {
        OrganizationDto dto = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeKeyRequest(dto.getKey(), "name");
    }

    @Test
    public void request_fails_if_user_is_administrator_of_another_organization() {
        OrganizationDto org = dbTester.organizations().insert();
        logInAsAdministrator(dbTester.getDefaultOrganization());
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeKeyRequest(org.getKey(), "name");
    }

    @Test
    public void request_fails_if_key_is_missing() {
        userSession.logIn();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'key' parameter is missing");
        executeRequest(null, "name", "description", "url", "avatar");
    }

    @Test
    public void request_with_only_key_param_succeeds_and_updates_only_updateAt_field() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(org);
        verifyResponseAndDb(executeKeyRequest(org.getKey(), null), org, org.getName(), UpdateActionTest.DATE_2);
    }

    @Test
    public void request_succeeds_if_name_is_two_chars_long() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(org);
        verifyResponseAndDb(executeKeyRequest(org.getKey(), "ab"), org, "ab", UpdateActionTest.DATE_2);
    }

    @Test
    public void request_succeeds_if_name_is_64_char_long() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(org);
        String name = OrganizationsWsTestSupport.STRING_65_CHARS_LONG.substring(0, 64);
        verifyResponseAndDb(executeKeyRequest(org.getKey(), name), org, name, UpdateActionTest.DATE_2);
    }

    @Test
    public void request_succeeds_if_description_url_and_avatar_are_not_specified() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(org);
        Organizations.UpdateWsResponse response = executeKeyRequest(org.getKey(), "bar", null, null, null);
        verifyResponseAndDb(response, org, "bar", UpdateActionTest.DATE_2);
    }

    @Test
    public void request_succeeds_if_description_url_and_avatar_are_specified() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(org);
        Organizations.UpdateWsResponse response = executeKeyRequest(org.getKey(), "bar", "moo", "doo", "boo");
        verifyResponseAndDb(response, org, "bar", "moo", "doo", "boo", UpdateActionTest.DATE_2);
    }

    @Test
    public void request_fails_if_description_is_257_chars_long() {
        userSession.logIn();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'description' length (257) is longer than the maximum authorized (256)");
        executeKeyRequest(UpdateActionTest.SOME_KEY, "bar", OrganizationsWsTestSupport.STRING_257_CHARS_LONG, null, null);
    }

    @Test
    public void request_succeeds_if_description_is_256_chars_long() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        String description = OrganizationsWsTestSupport.STRING_257_CHARS_LONG.substring(0, 256);
        logInAsAdministrator(org);
        Organizations.UpdateWsResponse response = executeKeyRequest(org.getKey(), "bar", description, null, null);
        verifyResponseAndDb(response, org, "bar", description, org.getUrl(), org.getAvatarUrl(), UpdateActionTest.DATE_2);
    }

    @Test
    public void request_fails_if_url_is_257_chars_long() {
        userSession.logIn();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'url' length (257) is longer than the maximum authorized (256)");
        executeKeyRequest(UpdateActionTest.SOME_KEY, "bar", null, OrganizationsWsTestSupport.STRING_257_CHARS_LONG, null);
    }

    @Test
    public void request_succeeds_if_url_is_256_chars_long() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        String url = OrganizationsWsTestSupport.STRING_257_CHARS_LONG.substring(0, 256);
        logInAsAdministrator(org);
        Organizations.UpdateWsResponse response = executeKeyRequest(org.getKey(), "bar", null, url, null);
        verifyResponseAndDb(response, org, "bar", org.getDescription(), url, org.getAvatarUrl(), UpdateActionTest.DATE_2);
    }

    @Test
    public void request_fails_if_avatar_is_257_chars_long() {
        userSession.logIn();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'avatar' length (257) is longer than the maximum authorized (256)");
        executeKeyRequest(UpdateActionTest.SOME_KEY, "bar", null, null, OrganizationsWsTestSupport.STRING_257_CHARS_LONG);
    }

    @Test
    public void request_succeeds_if_avatar_is_256_chars_long() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        String avatar = OrganizationsWsTestSupport.STRING_257_CHARS_LONG.substring(0, 256);
        logInAsAdministrator(org);
        Organizations.UpdateWsResponse response = executeKeyRequest(org.getKey(), "bar", null, null, avatar);
        verifyResponseAndDb(response, org, "bar", org.getDescription(), org.getUrl(), avatar, UpdateActionTest.DATE_2);
    }

    @Test
    public void request_removes_optional_parameters_when_associated_parameter_are_empty() {
        OrganizationDto org = mockForSuccessfulUpdate(UpdateActionTest.DATE_1, UpdateActionTest.DATE_2);
        logInAsAdministrator(org);
        Organizations.UpdateWsResponse response = executeKeyRequest(org.getKey(), "bla", "", "", "");
        verifyResponseAndDb(response, org, "bla", null, null, null, UpdateActionTest.DATE_2);
    }
}

