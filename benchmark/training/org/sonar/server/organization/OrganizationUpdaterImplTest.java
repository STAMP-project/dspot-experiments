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
package org.sonar.server.organization;


import GlobalPermissions.ALL;
import OrganizationUpdater.KeyConflictException;
import OrganizationUpdater.NewOrganization;
import Qualifiers.PROJECT;
import Subscription.FREE;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.DefaultTemplates;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.qualitygate.QualityGateDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.qualityprofile.BuiltInQProfile;
import org.sonar.server.qualityprofile.BuiltInQProfileRepositoryRule;
import org.sonar.server.qualityprofile.QProfileName;
import org.sonar.server.user.index.UserIndex;
import org.sonar.server.user.index.UserIndexer;
import org.sonar.server.user.index.UserQuery;
import org.sonar.server.usergroups.DefaultGroupCreator;

import static NewOrganization.newOrganizationBuilder;


public class OrganizationUpdaterImplTest {
    private static final long A_DATE = 12893434L;

    private static final String A_LOGIN = "a-login";

    private static final String SLUG_OF_A_LOGIN = "slug-of-a-login";

    private static final String A_NAME = "a name";

    private NewOrganization FULL_POPULATED_NEW_ORGANIZATION = newOrganizationBuilder().setName("a-name").setKey("a-key").setDescription("a-description").setUrl("a-url").setAvatarUrl("a-avatar").build();

    private System2 system2 = new TestSystem2().setNow(OrganizationUpdaterImplTest.A_DATE);

    private static Consumer<OrganizationDto> EMPTY_ORGANIZATION_CONSUMER = ( o) -> {
    };

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public BuiltInQProfileRepositoryRule builtInQProfileRepositoryRule = new BuiltInQProfileRepositoryRule();

    private DbSession dbSession = db.getSession();

    private IllegalArgumentException exceptionThrownByOrganizationValidation = new IllegalArgumentException("simulate IAE thrown by OrganizationValidation");

    private DbClient dbClient = db.getDbClient();

    private UuidFactory uuidFactory = new SequenceUuidFactory();

    private OrganizationValidation organizationValidation = Mockito.mock(OrganizationValidation.class);

    private MapSettings settings = new MapSettings();

    private UserIndexer userIndexer = new UserIndexer(dbClient, es.client());

    private UserIndex userIndex = new UserIndex(es.client(), system2);

    private DefaultGroupCreator defaultGroupCreator = new org.sonar.server.usergroups.DefaultGroupCreatorImpl(dbClient);

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private OrganizationUpdaterImpl underTest = new OrganizationUpdaterImpl(dbClient, system2, uuidFactory, organizationValidation, settings.asConfig(), userIndexer, builtInQProfileRepositoryRule, defaultGroupCreator, permissionService);

    @Test
    public void create_creates_unguarded_organization_with_properties_from_NewOrganization_arg() throws KeyConflictException {
        builtInQProfileRepositoryRule.initialize();
        UserDto user = db.users().insertUser();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, FULL_POPULATED_NEW_ORGANIZATION.getKey()).get();
        assertThat(organization.getUuid()).isNotEmpty();
        assertThat(organization.getKey()).isEqualTo(FULL_POPULATED_NEW_ORGANIZATION.getKey());
        assertThat(organization.getName()).isEqualTo(FULL_POPULATED_NEW_ORGANIZATION.getName());
        assertThat(organization.getDescription()).isEqualTo(FULL_POPULATED_NEW_ORGANIZATION.getDescription());
        assertThat(organization.getUrl()).isEqualTo(FULL_POPULATED_NEW_ORGANIZATION.getUrl());
        assertThat(organization.getAvatarUrl()).isEqualTo(FULL_POPULATED_NEW_ORGANIZATION.getAvatar());
        assertThat(organization.isGuarded()).isFalse();
        assertThat(organization.getSubscription()).isEqualTo(FREE);
        assertThat(organization.getCreatedAt()).isEqualTo(OrganizationUpdaterImplTest.A_DATE);
        assertThat(organization.getUpdatedAt()).isEqualTo(OrganizationUpdaterImplTest.A_DATE);
    }

    @Test
    public void create_creates_owners_group_with_all_permissions_for_new_organization_and_add_current_user_to_it() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
        verifyGroupOwners(user, FULL_POPULATED_NEW_ORGANIZATION.getKey(), FULL_POPULATED_NEW_ORGANIZATION.getName());
    }

    @Test
    public void create_creates_members_group_and_add_current_user_to_it() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
        verifyMembersGroup(user, FULL_POPULATED_NEW_ORGANIZATION.getKey());
    }

    @Test
    public void create_does_not_require_description_url_and_avatar_to_be_non_null() throws KeyConflictException {
        builtInQProfileRepositoryRule.initialize();
        UserDto user = db.users().insertUser();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.create(dbSession, user, newOrganizationBuilder().setKey("key").setName("name").build(), OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, "key").get();
        assertThat(organization.getKey()).isEqualTo("key");
        assertThat(organization.getName()).isEqualTo("name");
        assertThat(organization.getDescription()).isNull();
        assertThat(organization.getUrl()).isNull();
        assertThat(organization.getAvatarUrl()).isNull();
        assertThat(organization.isGuarded()).isFalse();
    }

    @Test
    public void create_creates_default_template_for_new_organization() throws KeyConflictException {
        builtInQProfileRepositoryRule.initialize();
        UserDto user = db.users().insertUser();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, FULL_POPULATED_NEW_ORGANIZATION.getKey()).get();
        GroupDto ownersGroup = dbClient.groupDao().selectByName(dbSession, organization.getUuid(), "Owners").get();
        int defaultGroupId = dbClient.organizationDao().getDefaultGroupId(dbSession, organization.getUuid()).get();
        PermissionTemplateDto defaultTemplate = dbClient.permissionTemplateDao().selectByName(dbSession, organization.getUuid(), "default template");
        assertThat(defaultTemplate.getName()).isEqualTo("Default template");
        assertThat(defaultTemplate.getDescription()).isEqualTo(("Default permission template of organization " + (FULL_POPULATED_NEW_ORGANIZATION.getName())));
        DefaultTemplates defaultTemplates = dbClient.organizationDao().getDefaultTemplates(dbSession, organization.getUuid()).get();
        assertThat(defaultTemplates.getProjectUuid()).isEqualTo(defaultTemplate.getUuid());
        assertThat(defaultTemplates.getApplicationsUuid()).isNull();
        assertThat(dbClient.permissionTemplateDao().selectGroupPermissionsByTemplateId(dbSession, defaultTemplate.getId())).extracting(PermissionTemplateGroupDto::getGroupId, PermissionTemplateGroupDto::getPermission).containsOnly(tuple(ownersGroup.getId(), UserRole.ADMIN), tuple(ownersGroup.getId(), UserRole.ISSUE_ADMIN), tuple(ownersGroup.getId(), UserRole.SECURITYHOTSPOT_ADMIN), tuple(ownersGroup.getId(), GlobalPermissions.SCAN_EXECUTION), tuple(defaultGroupId, UserRole.USER), tuple(defaultGroupId, UserRole.CODEVIEWER));
    }

    @Test
    public void create_add_current_user_as_member_of_organization() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        OrganizationDto result = underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
        assertThat(dbClient.organizationMemberDao().select(dbSession, result.getUuid(), user.getId())).isPresent();
        assertThat(userIndex.search(UserQuery.builder().setOrganizationUuid(result.getUuid()).setTextQuery(user.getLogin()).build(), new SearchOptions()).getTotal()).isEqualTo(1L);
    }

    @Test
    public void create_associates_to_built_in_quality_profiles() throws KeyConflictException {
        BuiltInQProfile builtIn1 = builtInQProfileRepositoryRule.add(LanguageTesting.newLanguage("foo"), "qp1", true);
        BuiltInQProfile builtIn2 = builtInQProfileRepositoryRule.add(LanguageTesting.newLanguage("foo"), "qp2");
        builtInQProfileRepositoryRule.initialize();
        insertRulesProfile(builtIn1);
        insertRulesProfile(builtIn2);
        UserDto user = db.users().insertUser();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, FULL_POPULATED_NEW_ORGANIZATION.getKey()).get();
        List<QProfileDto> profiles = dbClient.qualityProfileDao().selectOrderedByOrganizationUuid(dbSession, organization);
        assertThat(profiles).extracting(( p) -> new QProfileName(p.getLanguage(), p.getName())).containsExactlyInAnyOrder(builtIn1.getQProfileName(), builtIn2.getQProfileName());
        assertThat(dbClient.qualityProfileDao().selectDefaultProfile(dbSession, organization, "foo").getName()).isEqualTo("qp1");
    }

    @Test
    public void create_associates_to_built_in_quality_gate() throws KeyConflictException {
        QualityGateDto builtInQualityGate = db.qualityGates().insertBuiltInQualityGate();
        builtInQProfileRepositoryRule.initialize();
        UserDto user = db.users().insertUser();
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, ( o) -> {
        });
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, FULL_POPULATED_NEW_ORGANIZATION.getKey()).get();
        assertThat(dbClient.qualityGateDao().selectDefault(dbSession, organization).getUuid()).isEqualTo(builtInQualityGate.getUuid());
    }

    @Test
    public void create_calls_consumer() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        Boolean[] isConsumerCalled = new Boolean[]{ false };
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, ( o) -> {
            isConsumerCalled[0] = true;
        });
        assertThat(isConsumerCalled[0]).isEqualTo(true);
    }

    @Test
    public void create_throws_NPE_if_NewOrganization_arg_is_null() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("newOrganization can't be null");
        underTest.create(dbSession, user, null, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
    }

    @Test
    public void create_throws_exception_thrown_by_checkValidKey() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        Mockito.when(organizationValidation.checkKey(FULL_POPULATED_NEW_ORGANIZATION.getKey())).thenThrow(exceptionThrownByOrganizationValidation);
        createThrowsExceptionThrownByOrganizationValidation(user);
    }

    @Test
    public void create_throws_exception_thrown_by_checkValidDescription() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        Mockito.when(organizationValidation.checkDescription(FULL_POPULATED_NEW_ORGANIZATION.getDescription())).thenThrow(exceptionThrownByOrganizationValidation);
        createThrowsExceptionThrownByOrganizationValidation(user);
    }

    @Test
    public void create_throws_exception_thrown_by_checkValidUrl() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        Mockito.when(organizationValidation.checkUrl(FULL_POPULATED_NEW_ORGANIZATION.getUrl())).thenThrow(exceptionThrownByOrganizationValidation);
        createThrowsExceptionThrownByOrganizationValidation(user);
    }

    @Test
    public void create_throws_exception_thrown_by_checkValidAvatar() throws KeyConflictException {
        UserDto user = db.users().insertUser();
        Mockito.when(organizationValidation.checkAvatar(FULL_POPULATED_NEW_ORGANIZATION.getAvatar())).thenThrow(exceptionThrownByOrganizationValidation);
        createThrowsExceptionThrownByOrganizationValidation(user);
    }

    @Test
    public void create_fails_with_KeyConflictException_if_org_with_key_in_NewOrganization_arg_already_exists_in_db() throws KeyConflictException {
        db.organizations().insertForKey(FULL_POPULATED_NEW_ORGANIZATION.getKey());
        UserDto user = db.users().insertUser();
        expectedException.expect(KeyConflictException.class);
        expectedException.expectMessage((("Organization key '" + (FULL_POPULATED_NEW_ORGANIZATION.getKey())) + "' is already used"));
        underTest.create(dbSession, user, FULL_POPULATED_NEW_ORGANIZATION, OrganizationUpdaterImplTest.EMPTY_ORGANIZATION_CONSUMER);
    }

    @Test
    public void createForUser_creates_guarded_organization_with_key_name_and_description_generated_from_user_login_and_name_and_associated_to_user() {
        UserDto user = db.users().insertUser(OrganizationUpdaterImplTest.A_LOGIN);
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.createForUser(dbSession, user);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN).get();
        assertThat(organization.getUuid()).isNotEmpty();
        assertThat(organization.getKey()).isEqualTo(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        assertThat(organization.getName()).isEqualTo(user.getName());
        assertThat(organization.getDescription()).isEqualTo(((user.getName()) + "'s personal organization"));
        assertThat(organization.getUrl()).isNull();
        assertThat(organization.getAvatarUrl()).isNull();
        assertThat(organization.isGuarded()).isTrue();
        assertThat(organization.getSubscription()).isEqualTo(FREE);
        assertThat(organization.getCreatedAt()).isEqualTo(OrganizationUpdaterImplTest.A_DATE);
        assertThat(organization.getUpdatedAt()).isEqualTo(OrganizationUpdaterImplTest.A_DATE);
        assertThat(db.getDbClient().userDao().selectByUuid(dbSession, user.getUuid()).getOrganizationUuid()).isEqualTo(organization.getUuid());
    }

    @Test
    public void createForUser_fails_with_ISE_if_organization_with_slug_of_login_already_exists() {
        UserDto user = db.users().insertUser(OrganizationUpdaterImplTest.A_LOGIN);
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        db.organizations().insertForKey(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(((("Can't create organization with key '" + (OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN)) + "' ") + "because an organization with this key already exists"));
        underTest.createForUser(dbSession, user);
    }

    @Test
    public void createForUser_gives_all_permissions_for_new_organization_to_current_user() {
        UserDto user = db.users().insertUser(( dto) -> dto.setLogin(A_LOGIN).setName(A_NAME));
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.createForUser(dbSession, user);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN).get();
        assertThat(dbClient.userPermissionDao().selectGlobalPermissionsOfUser(dbSession, user.getId(), organization.getUuid())).containsOnly(ALL.toArray(new String[ALL.size()]));
    }

    @Test
    public void createForUser_creates_members_group_and_add_current_user_to_it() {
        UserDto user = db.users().insertUser(( dto) -> dto.setLogin(A_LOGIN).setName(A_NAME));
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.createForUser(dbSession, user);
        verifyMembersGroup(user, OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
    }

    @Test
    public void createForUser_creates_default_template_for_new_organization() {
        UserDto user = db.users().insertUser(( dto) -> dto.setLogin(A_LOGIN).setName(A_NAME));
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.createForUser(dbSession, user);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN).get();
        int defaultGroupId = dbClient.organizationDao().getDefaultGroupId(dbSession, organization.getUuid()).get();
        PermissionTemplateDto defaultTemplate = dbClient.permissionTemplateDao().selectByName(dbSession, organization.getUuid(), "default template");
        assertThat(defaultTemplate.getName()).isEqualTo("Default template");
        assertThat(defaultTemplate.getDescription()).isEqualTo(("Default permission template of organization " + (OrganizationUpdaterImplTest.A_NAME)));
        DefaultTemplates defaultTemplates = dbClient.organizationDao().getDefaultTemplates(dbSession, organization.getUuid()).get();
        assertThat(defaultTemplates.getProjectUuid()).isEqualTo(defaultTemplate.getUuid());
        assertThat(defaultTemplates.getApplicationsUuid()).isNull();
        assertThat(dbClient.permissionTemplateDao().selectGroupPermissionsByTemplateId(dbSession, defaultTemplate.getId())).extracting(PermissionTemplateGroupDto::getGroupId, PermissionTemplateGroupDto::getPermission).containsOnly(tuple(defaultGroupId, UserRole.USER), tuple(defaultGroupId, UserRole.CODEVIEWER));
        assertThat(dbClient.permissionTemplateCharacteristicDao().selectByTemplateIds(dbSession, Collections.singletonList(defaultTemplate.getId()))).extracting(PermissionTemplateCharacteristicDto::getWithProjectCreator, PermissionTemplateCharacteristicDto::getPermission).containsOnly(tuple(true, UserRole.ADMIN), tuple(true, UserRole.ISSUE_ADMIN), tuple(true, UserRole.SECURITYHOTSPOT_ADMIN), tuple(true, GlobalPermissions.SCAN_EXECUTION));
    }

    @Test
    public void createForUser_add_current_user_as_member_of_organization() {
        UserDto user = db.users().insertUser(( dto) -> dto.setLogin(A_LOGIN).setName(A_NAME));
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        builtInQProfileRepositoryRule.initialize();
        db.qualityGates().insertBuiltInQualityGate();
        underTest.createForUser(dbSession, user);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN).get();
        assertThat(dbClient.organizationMemberDao().select(dbSession, organization.getUuid(), user.getId())).isPresent();
    }

    @Test
    public void createForUser_associates_to_built_in_quality_profiles() {
        UserDto user = db.users().insertUser(OrganizationUpdaterImplTest.A_LOGIN);
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        db.qualityGates().insertBuiltInQualityGate();
        BuiltInQProfile builtIn1 = builtInQProfileRepositoryRule.add(LanguageTesting.newLanguage("foo"), "qp1");
        BuiltInQProfile builtIn2 = builtInQProfileRepositoryRule.add(LanguageTesting.newLanguage("foo"), "qp2");
        builtInQProfileRepositoryRule.initialize();
        insertRulesProfile(builtIn1);
        insertRulesProfile(builtIn2);
        underTest.createForUser(dbSession, user);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN).get();
        List<QProfileDto> profiles = dbClient.qualityProfileDao().selectOrderedByOrganizationUuid(dbSession, organization);
        assertThat(profiles).extracting(( p) -> new QProfileName(p.getLanguage(), p.getName())).containsExactlyInAnyOrder(builtIn1.getQProfileName(), builtIn2.getQProfileName());
    }

    @Test
    public void createForUser_associates_to_built_in_quality_gate() {
        QualityGateDto builtInQualityGate = db.qualityGates().insertBuiltInQualityGate();
        UserDto user = db.users().insertUser(OrganizationUpdaterImplTest.A_LOGIN);
        Mockito.when(organizationValidation.generateKeyFrom(OrganizationUpdaterImplTest.A_LOGIN)).thenReturn(OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN);
        enableCreatePersonalOrg(true);
        builtInQProfileRepositoryRule.initialize();
        underTest.createForUser(dbSession, user);
        OrganizationDto organization = dbClient.organizationDao().selectByKey(dbSession, OrganizationUpdaterImplTest.SLUG_OF_A_LOGIN).get();
        assertThat(dbClient.qualityGateDao().selectDefault(dbSession, organization).getUuid()).isEqualTo(builtInQualityGate.getUuid());
    }

    @Test
    public void createForUser_has_no_effect_if_setting_for_feature_is_not_set() {
        checkSizeOfTables();
        /* argument is not even read */
        /* argument is not even read */
        underTest.createForUser(null, null);
        checkSizeOfTables();
    }

    @Test
    public void createForUser_has_no_effect_if_setting_for_feature_is_disabled() {
        enableCreatePersonalOrg(false);
        checkSizeOfTables();
        /* argument is not even read */
        /* argument is not even read */
        underTest.createForUser(null, null);
        checkSizeOfTables();
    }

    @Test
    public void update_personal_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setKey("old login"));
        Mockito.when(organizationValidation.generateKeyFrom("new_login")).thenReturn("new_login");
        underTest.updateOrganizationKey(dbSession, organization, "new_login");
        OrganizationDto organizationReloaded = dbClient.organizationDao().selectByUuid(dbSession, organization.getUuid()).get();
        assertThat(organizationReloaded.getKey()).isEqualTo("new_login");
    }

    @Test
    public void does_not_update_personal_organization_when_generated_organization_key_does_not_change() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setKey("login"));
        Mockito.when(organizationValidation.generateKeyFrom("Login")).thenReturn("login");
        underTest.updateOrganizationKey(dbSession, organization, "Login");
        OrganizationDto organizationReloaded = dbClient.organizationDao().selectByUuid(dbSession, organization.getUuid()).get();
        assertThat(organizationReloaded.getKey()).isEqualTo("login");
    }

    @Test
    public void fail_to_update_personal_organization_when_new_key_already_exist() {
        OrganizationDto organization = db.organizations().insert();
        db.organizations().insert(( o) -> o.setKey("new_login"));
        Mockito.when(organizationValidation.generateKeyFrom("new_login")).thenReturn("new_login");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Can't create organization with key 'new_login' because an organization with this key already exists");
        underTest.updateOrganizationKey(dbSession, organization, "new_login");
    }
}

