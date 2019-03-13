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


import GlobalPermissions.ALL;
import OrganizationDto.Subscription.FREE;
import Qualifiers.PROJECT;
import UserIndexDefinition.INDEX_TYPE_USER;
import UserMembershipQuery.IN;
import WebService.Action;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.DefaultTemplates;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserMembershipDto;
import org.sonar.db.user.UserMembershipQuery;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.organization.OrganizationAlmBinding;
import org.sonar.server.organization.OrganizationUpdater;
import org.sonar.server.organization.OrganizationValidation;
import org.sonar.server.organization.OrganizationValidationImpl;
import org.sonar.server.organization.TestOrganizationFlags;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.qualityprofile.BuiltInQProfileRepository;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.user.index.UserIndexer;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Organizations.CreateWsResponse;


public class CreateActionTest {
    private static final long NOW = 1200000L;

    private System2 system2 = new TestSystem2().setNow(CreateActionTest.NOW);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(system2).setDisableDefaultOrganization(true);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private MapSettings settings = new MapSettings().setProperty(ORGANIZATIONS_ANYONE_CAN_CREATE, false);

    private OrganizationValidation organizationValidation = new OrganizationValidationImpl();

    private UserIndexer userIndexer = new UserIndexer(dbClient, es.client());

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private OrganizationUpdater organizationUpdater = new org.sonar.server.organization.OrganizationUpdaterImpl(dbClient, system2, UuidFactoryFast.getInstance(), organizationValidation, settings.asConfig(), userIndexer, Mockito.mock(BuiltInQProfileRepository.class), new org.sonar.server.usergroups.DefaultGroupCreatorImpl(dbClient), permissionService);

    private TestOrganizationFlags organizationFlags = TestOrganizationFlags.standalone().setEnabled(true);

    private OrganizationAlmBinding organizationAlmBinding = Mockito.mock(OrganizationAlmBinding.class);

    private WsActionTester wsTester = new WsActionTester(new CreateAction(settings.asConfig(), userSession, dbClient, new OrganizationsWsSupport(organizationValidation, dbClient), organizationValidation, organizationUpdater, organizationFlags, organizationAlmBinding));

    @Test
    public void create_organization() {
        settings.setProperty(ORGANIZATIONS_ANYONE_CAN_CREATE, true);
        db.qualityGates().insertBuiltInQualityGate();
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        CreateWsResponse response = wsTester.newRequest().setParam("name", "orgFoo").setParam("description", "My org desc").setParam("url", "my url").setParam("avatar", "my avatar").executeProtobuf(CreateWsResponse.class);
        assertThat(response.getOrganization().getKey()).isEqualTo("orgfoo");
        assertThat(response.getOrganization().getName()).isEqualTo("orgFoo");
        assertThat(response.getOrganization().getDescription()).isEqualTo("My org desc");
        assertThat(response.getOrganization().getUrl()).isEqualTo("my url");
        assertThat(response.getOrganization().getAvatar()).isEqualTo("my avatar");
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, "orgfoo").get();
        assertThat(organization.getName()).isEqualTo("orgFoo");
        assertThat(organization.getDescription()).isEqualTo("My org desc");
        assertThat(organization.getUrl()).isEqualTo("my url");
        assertThat(organization.getAvatarUrl()).isEqualTo("my avatar");
        assertThat(organization.getSubscription()).isEqualTo(FREE);
    }

    @Test
    public void request_creates_owners_group_with_all_permissions_for_new_organization_and_add_current_user_to_it() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        executeRequest("orgFoo");
        DbSession dbSession = db.getSession();
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, "orgfoo").get();
        Optional<GroupDto> groupDtoOptional = dbClient.groupDao().selectByName(dbSession, organization.getUuid(), "Owners");
        assertThat(groupDtoOptional).isNotEmpty();
        GroupDto groupDto = groupDtoOptional.get();
        assertThat(groupDto.getDescription()).isEqualTo("Owners of organization orgFoo");
        assertThat(dbClient.groupPermissionDao().selectGlobalPermissionsOfGroup(dbSession, groupDto.getOrganizationUuid(), groupDto.getId())).containsOnly(ALL.toArray(new String[ALL.size()]));
        List<UserMembershipDto> members = dbClient.groupMembershipDao().selectMembers(dbSession, UserMembershipQuery.builder().organizationUuid(organization.getUuid()).groupId(groupDto.getId()).membership(IN).build(), 0, Integer.MAX_VALUE);
        assertThat(members).extracting(UserMembershipDto::getLogin).containsOnly(user.getLogin());
    }

    @Test
    public void request_creates_members_group_and_add_current_user_to_it() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        executeRequest("orgFoo");
        DbSession dbSession = db.getSession();
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, "orgfoo").get();
        Optional<GroupDto> groupDtoOptional = dbClient.groupDao().selectByName(dbSession, organization.getUuid(), "Members");
        assertThat(groupDtoOptional).isNotEmpty();
        GroupDto groupDto = groupDtoOptional.get();
        assertThat(groupDto.getDescription()).isEqualTo("All members of the organization");
        assertThat(dbClient.groupPermissionDao().selectGlobalPermissionsOfGroup(dbSession, groupDto.getOrganizationUuid(), groupDto.getId())).isEmpty();
        List<UserMembershipDto> members = dbClient.groupMembershipDao().selectMembers(dbSession, UserMembershipQuery.builder().organizationUuid(organization.getUuid()).groupId(groupDto.getId()).membership(IN).build(), 0, Integer.MAX_VALUE);
        assertThat(members).extracting(UserMembershipDto::getLogin).containsOnly(user.getLogin());
    }

    @Test
    public void request_creates_default_template_for_owner_group() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        executeRequest("orgFoo");
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, "orgfoo").get();
        GroupDto ownersGroup = dbClient.groupDao().selectByName(dbSession, organization.getUuid(), "Owners").get();
        GroupDto defaultGroup = dbClient.groupDao().selectByName(dbSession, organization.getUuid(), "Members").get();
        PermissionTemplateDto defaultTemplate = dbClient.permissionTemplateDao().selectByName(dbSession, organization.getUuid(), "default template");
        assertThat(defaultTemplate.getName()).isEqualTo("Default template");
        assertThat(defaultTemplate.getDescription()).isEqualTo("Default permission template of organization orgFoo");
        DefaultTemplates defaultTemplates = dbClient.organizationDao().getDefaultTemplates(dbSession, organization.getUuid()).get();
        assertThat(defaultTemplates.getProjectUuid()).isEqualTo(defaultTemplate.getUuid());
        assertThat(defaultTemplates.getApplicationsUuid()).isNull();
        assertThat(dbClient.permissionTemplateDao().selectGroupPermissionsByTemplateId(dbSession, defaultTemplate.getId())).extracting(PermissionTemplateGroupDto::getGroupId, PermissionTemplateGroupDto::getPermission).containsOnly(tuple(ownersGroup.getId(), UserRole.ADMIN), tuple(ownersGroup.getId(), UserRole.ISSUE_ADMIN), tuple(ownersGroup.getId(), UserRole.SECURITYHOTSPOT_ADMIN), tuple(ownersGroup.getId(), GlobalPermissions.SCAN_EXECUTION), tuple(defaultGroup.getId(), UserRole.USER), tuple(defaultGroup.getId(), UserRole.CODEVIEWER));
    }

    @Test
    public void set_user_as_member_of_organization() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user).setSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        executeRequest("foo", "bar");
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, "bar").get();
        assertThat(dbClient.organizationMemberDao().select(dbSession, organization.getUuid(), user.getId())).isPresent();
        assertThat(es.client().prepareSearch(INDEX_TYPE_USER).setQuery(boolQuery().must(termQuery(FIELD_ORGANIZATION_UUIDS, organization.getUuid())).must(termQuery(FIELD_UUID, user.getUuid()))).get().getHits().getHits()).hasSize(1);
    }

    @Test
    public void create_organization_with_name_having_one_character() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        wsTester.newRequest().setParam(OrganizationsWsSupport.PARAM_NAME, "a").execute();
        OrganizationDto organization = dbClient.organizationDao().selectByKey(db.getSession(), "a").get();
        assertThat(organization.getKey()).isEqualTo("a");
        assertThat(organization.getName()).isEqualTo("a");
    }

    @Test
    public void bind_organization_when_installation_id_is_set() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        wsTester.newRequest().setParam(OrganizationsWsSupport.PARAM_NAME, "foo").setParam("installationId", "ABCD").execute();
        Mockito.verify(organizationAlmBinding).bindOrganization(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.any(OrganizationDto.class), ArgumentMatchers.eq("ABCD"), ArgumentMatchers.eq(true));
    }

    @Test
    public void does_not_bind_organization_when_organizationAlmBinding_is_null() {
        wsTester = new WsActionTester(new CreateAction(settings.asConfig(), userSession, dbClient, new OrganizationsWsSupport(organizationValidation, dbClient), organizationValidation, organizationUpdater, organizationFlags, null));
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        wsTester.newRequest().setParam(OrganizationsWsSupport.PARAM_NAME, "foo").setParam("installationId", "ABCD").execute();
        Mockito.verifyZeroInteractions(organizationAlmBinding);
    }

    @Test
    public void does_not_bind_organization_when_installation_id_is_not_set() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        wsTester.newRequest().setParam(OrganizationsWsSupport.PARAM_NAME, "foo").execute();
        Mockito.verifyZeroInteractions(organizationAlmBinding);
    }

    @Test
    public void request_succeeds_if_user_is_system_administrator_and_logged_in_users_cannot_create_organizations() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        verifyResponseAndDb(executeRequest("foo"), "foo", "foo", CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_user_is_system_administrator_and_logged_in_users_can_create_organizations() {
        createUserAndLogInAsSystemAdministrator();
        settings.setProperty(ORGANIZATIONS_ANYONE_CAN_CREATE, true);
        db.qualityGates().insertBuiltInQualityGate();
        verifyResponseAndDb(executeRequest("foo"), "foo", "foo", CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_user_is_not_system_administrator_and_logged_in_users_can_create_organizations() {
        UserDto user = db.users().insertUser();
        userSession.logIn(user);
        settings.setProperty(ORGANIZATIONS_ANYONE_CAN_CREATE, true);
        db.qualityGates().insertBuiltInQualityGate();
        verifyResponseAndDb(executeRequest("foo"), "foo", "foo", CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_name_is_two_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        verifyResponseAndDb(executeRequest("ab"), "ab", "ab", CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_key_is_2_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        verifyResponseAndDb(executeRequest("foo", "ab"), "foo", "ab", CreateActionTest.NOW);
    }

    @Test
    public void requests_succeeds_if_key_is_32_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        String key = OrganizationsWsTestSupport.STRING_65_CHARS_LONG.substring(0, 32);
        verifyResponseAndDb(executeRequest("foo", key), "foo", key, CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_description_url_and_avatar_are_not_specified() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        CreateWsResponse response = executeRequest("foo", "bar", null, null, null);
        verifyResponseAndDb(response, "foo", "bar", null, null, null, CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_description_url_and_avatar_are_specified() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        CreateWsResponse response = executeRequest("foo", "bar", "moo", "doo", "boo");
        verifyResponseAndDb(response, "foo", "bar", "moo", "doo", "boo", CreateActionTest.NOW);
    }

    @Test
    public void request_generates_key_ignoring_multiple_following_spaces() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        String name = "ab   cd";
        CreateWsResponse response = executeRequest(name);
        verifyResponseAndDb(response, name, "ab-cd", CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_description_is_256_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        String description = OrganizationsWsTestSupport.STRING_257_CHARS_LONG.substring(0, 256);
        db.qualityGates().insertBuiltInQualityGate();
        CreateWsResponse response = executeRequest("foo", "bar", description, null, null);
        verifyResponseAndDb(response, "foo", "bar", description, null, null, CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_url_is_256_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        String url = OrganizationsWsTestSupport.STRING_257_CHARS_LONG.substring(0, 256);
        db.qualityGates().insertBuiltInQualityGate();
        CreateWsResponse response = executeRequest("foo", "bar", null, url, null);
        verifyResponseAndDb(response, "foo", "bar", null, url, null, CreateActionTest.NOW);
    }

    @Test
    public void request_succeeds_if_avatar_is_256_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        String avatar = OrganizationsWsTestSupport.STRING_257_CHARS_LONG.substring(0, 256);
        db.qualityGates().insertBuiltInQualityGate();
        CreateWsResponse response = executeRequest("foo", "bar", null, null, avatar);
        verifyResponseAndDb(response, "foo", "bar", null, null, avatar, CreateActionTest.NOW);
    }

    @Test
    public void request_fails_if_name_param_is_missing() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'name' parameter is missing");
        executeRequest(null);
    }

    @Test
    public void requests_fails_if_key_contains_non_ascii_chars_but_dash() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Key '" + ("ab@" + "' contains at least one invalid char")));
        executeRequest("foo", "ab@");
    }

    @Test
    public void request_fails_if_key_starts_with_a_dash() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Key '" + ("-ab" + "' contains at least one invalid char")));
        executeRequest("foo", "-ab");
    }

    @Test
    public void request_fails_if_key_ends_with_a_dash() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Key '" + ("ab-" + "' contains at least one invalid char")));
        executeRequest("foo", "ab-");
    }

    @Test
    public void request_fails_if_key_contains_space() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Key '" + ("a b" + "' contains at least one invalid char")));
        executeRequest("foo", "a b");
    }

    @Test
    public void request_fails_if_key_is_specified_and_already_exists_in_DB() {
        createUserAndLogInAsSystemAdministrator();
        OrganizationDto org = db.organizations().insert(( o) -> o.setKey("the-key"));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((("Key '" + (org.getKey())) + "' is already used. Specify another one."));
        executeRequest("foo", org.getKey());
    }

    @Test
    public void request_fails_if_key_computed_from_name_already_exists_in_DB() {
        createUserAndLogInAsSystemAdministrator();
        String key = "key";
        db.organizations().insert(( o) -> o.setKey(key));
        String name = "Key";
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((((("Key '" + key) + "' generated from name '") + name) + "' is already used. Specify one."));
        executeRequest(name);
    }

    @Test
    public void request_fails_if_url_is_257_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'url' length (257) is longer than the maximum authorized (256)");
        executeRequest("foo", "bar", null, OrganizationsWsTestSupport.STRING_257_CHARS_LONG, null);
    }

    @Test
    public void request_fails_if_description_is_257_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'description' length (257) is longer than the maximum authorized (256)");
        executeRequest("foo", "bar", OrganizationsWsTestSupport.STRING_257_CHARS_LONG, null, null);
    }

    @Test
    public void request_fails_if_avatar_is_257_chars_long() {
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'avatar' length (257) is longer than the maximum authorized (256)");
        executeRequest("foo", "bar", null, null, OrganizationsWsTestSupport.STRING_257_CHARS_LONG);
    }

    @Test
    public void request_fails_if_user_is_not_logged_in_and_logged_in_users_cannot_create_organizations() {
        userSession.anonymous();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeRequest("name");
    }

    @Test
    public void request_fails_if_user_is_not_logged_in_and_logged_in_users_can_create_organizations() {
        userSession.anonymous();
        settings.setProperty(ORGANIZATIONS_ANYONE_CAN_CREATE, true);
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        executeRequest("name");
    }

    @Test
    public void request_fails_if_user_is_not_system_administrator_and_logged_in_users_cannot_create_organizations() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeRequest("name");
    }

    @Test
    public void request_fails_with_IllegalStateException_if_organization_support_is_disabled() {
        organizationFlags.setEnabled(false);
        createUserAndLogInAsSystemAdministrator();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Organization support is disabled");
        executeJsonRequest("Foo Company", "foo-company", "The Foo company produces quality software for Bar.", "https://www.foo.com", "https://www.foo.com/foo.png");
    }

    @Test
    public void verify_define() {
        WebService.Action action = wsTester.getDef();
        assertThat(action.key()).isEqualTo("create");
        assertThat(action.isPost()).isTrue();
        assertThat(action.description()).isEqualTo(("Create an organization.<br />" + "Requires 'Administer System' permission unless any logged in user is allowed to create an organization (see appropriate setting). Organization support must be enabled."));
        assertThat(action.isInternal()).isTrue();
        assertThat(action.since()).isEqualTo("6.2");
        assertThat(action.handler()).isNotNull();
        assertThat(action.params()).hasSize(6);
        assertThat(action.responseExample()).isEqualTo(getClass().getResource("create-example.json"));
        assertThat(action.param("name")).matches(WebService.Param::isRequired).matches(( param) -> "Foo Company".equals(param.exampleValue())).matches(( param) -> param.minimumLength().equals(1)).matches(( param) -> param.maximumLength().equals(255)).matches(( param) -> (param.description()) != null);
        assertThat(action.param("key")).matches(( param) -> !(param.isRequired())).matches(( param) -> "foo-company".equals(param.exampleValue())).matches(( param) -> param.minimumLength().equals(1)).matches(( param) -> param.maximumLength().equals(255)).matches(( param) -> (param.description()) != null);
        assertThat(action.param("description")).matches(( param) -> !(param.isRequired())).matches(( param) -> "The Foo company produces quality software for Bar.".equals(param.exampleValue())).matches(( param) -> (param.description()) != null);
        assertThat(action.param("url")).matches(( param) -> !(param.isRequired())).matches(( param) -> "https://www.foo.com".equals(param.exampleValue())).matches(( param) -> (param.description()) != null);
        assertThat(action.param("avatar")).matches(( param) -> !(param.isRequired())).matches(( param) -> "https://www.foo.com/foo.png".equals(param.exampleValue())).matches(( param) -> (param.description()) != null);
        assertThat(action.param("installationId")).matches(( param) -> !(param.isRequired())).matches(( param) -> param.isInternal());
    }

    @Test
    public void verify_response_example() {
        createUserAndLogInAsSystemAdministrator();
        db.qualityGates().insertBuiltInQualityGate();
        String response = executeJsonRequest("Foo Company", "foo-company", "The Foo company produces quality software for Bar.", "https://www.foo.com", "https://www.foo.com/foo.png");
        assertJson(response).isSimilarTo(wsTester.getDef().responseExampleAsString());
    }
}

