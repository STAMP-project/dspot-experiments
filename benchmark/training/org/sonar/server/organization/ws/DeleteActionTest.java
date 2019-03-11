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


import BillingValidations.Organization;
import System2.INSTANCE;
import WebService.Action;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.db.qualitygate.QGateWithOrgDto;
import org.sonar.db.qualitygate.QualityGateDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.db.webhook.WebhookDbTester;
import org.sonar.db.webhook.WebhookDeliveryDao;
import org.sonar.db.webhook.WebhookDeliveryDbTester;
import org.sonar.db.webhook.WebhookDto;
import org.sonar.server.component.ComponentCleanerService;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.ProjectIndexers;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.organization.BillingValidationsProxy;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.organization.TestOrganizationFlags;
import org.sonar.server.project.Project;
import org.sonar.server.project.ProjectLifeCycleListeners;
import org.sonar.server.qualityprofile.QProfileFactory;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.user.index.UserIndex;
import org.sonar.server.user.index.UserIndexer;
import org.sonar.server.user.index.UserQuery;
import org.sonar.server.ws.WsActionTester;


@RunWith(DataProviderRunner.class)
public class DeleteActionTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private ResourceTypesRule resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT, VIEW, APP).setAllQualifiers(PROJECT, VIEW, APP);

    private ComponentCleanerService spiedComponentCleanerService = Mockito.spy(new ComponentCleanerService(db.getDbClient(), resourceTypes, Mockito.mock(ProjectIndexers.class)));

    private TestOrganizationFlags organizationFlags = TestOrganizationFlags.standalone().setEnabled(true);

    private TestDefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private QProfileFactory qProfileFactory = new org.sonar.server.qualityprofile.QProfileFactoryImpl(dbClient, Mockito.mock(UuidFactory.class), System2.INSTANCE, Mockito.mock(ActiveRuleIndexer.class));

    private UserIndex userIndex = new UserIndex(es.client(), System2.INSTANCE);

    private UserIndexer userIndexer = new UserIndexer(dbClient, es.client());

    private final WebhookDbTester webhookDbTester = db.webhooks();

    private final WebhookDeliveryDao deliveryDao = dbClient.webhookDeliveryDao();

    private final WebhookDeliveryDbTester webhookDeliveryDbTester = db.webhookDelivery();

    private ProjectLifeCycleListeners projectLifeCycleListeners = Mockito.mock(ProjectLifeCycleListeners.class);

    private BillingValidationsProxy billingValidationsProxy = Mockito.mock(BillingValidationsProxy.class);

    private WsActionTester wsTester = new WsActionTester(new DeleteAction(userSession, dbClient, defaultOrganizationProvider, spiedComponentCleanerService, organizationFlags, userIndexer, qProfileFactory, projectLifeCycleListeners, billingValidationsProxy));

    @Test
    public void definition() {
        WebService.Action action = wsTester.getDef();
        assertThat(action.key()).isEqualTo("delete");
        assertThat(action.isPost()).isTrue();
        assertThat(action.description()).isEqualTo(("Delete an organization.<br/>" + "Require 'Administer System' permission on the specified organization. Organization support must be enabled."));
        assertThat(action.isInternal()).isTrue();
        assertThat(action.since()).isEqualTo("6.2");
        assertThat(action.handler()).isNotNull();
        assertThat(action.params()).hasSize(1);
        assertThat(action.responseExample()).isNull();
        assertThat(action.param("organization")).matches(( param) -> param.isRequired()).matches(( param) -> "foo-company".equals(param.exampleValue())).matches(( param) -> "Organization key".equals(param.description()));
    }

    @Test
    public void organization_deletion_also_ensure_that_webhooks_of_this_organization_if_they_exist_are_cleared() {
        OrganizationDto organization = db.organizations().insert();
        db.webhooks().insertWebhook(organization);
        ComponentDto project = db.components().insertPrivateProject(organization);
        WebhookDto projectWebhook = db.webhooks().insertWebhook(project);
        db.webhookDelivery().insert(projectWebhook);
        userSession.logIn().addPermission(ADMINISTER, organization);
        wsTester.newRequest().setParam(OrganizationsWsSupport.PARAM_ORGANIZATION, organization.getKey()).execute();
        assertThat(db.countRowsOfTable(db.getSession(), "webhooks")).isZero();
        assertThat(db.countRowsOfTable(db.getSession(), "webhook_deliveries")).isZero();
    }

    @Test
    public void organization_deletion_also_ensure_that_homepage_on_this_organization_if_it_exists_is_cleared() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user = dbClient.userDao().insert(dbSession, UserTesting.newUserDto().setHomepageType("ORGANIZATION").setHomepageParameter(organization.getUuid()));
        dbSession.commit();
        userSession.logIn().addPermission(ADMINISTER, organization);
        wsTester.newRequest().setParam(OrganizationsWsSupport.PARAM_ORGANIZATION, organization.getKey()).execute();
        UserDto userReloaded = dbClient.userDao().selectUserById(dbSession, user.getId());
        assertThat(userReloaded.getHomepageType()).isNull();
        assertThat(userReloaded.getHomepageParameter()).isNull();
    }

    @Test
    public void organization_deletion_also_ensure_that_homepage_on_project_belonging_to_this_organization_if_it_exists_is_cleared() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        UserDto user = dbClient.userDao().insert(dbSession, UserTesting.newUserDto().setHomepageType("PROJECT").setHomepageParameter(project.uuid()));
        dbSession.commit();
        userSession.logIn().addPermission(ADMINISTER, organization);
        wsTester.newRequest().setParam(OrganizationsWsSupport.PARAM_ORGANIZATION, organization.getKey()).execute();
        UserDto userReloaded = dbClient.userDao().selectUserById(dbSession, user.getId());
        assertThat(userReloaded.getHomepageType()).isNull();
        assertThat(userReloaded.getHomepageParameter()).isNull();
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(ImmutableSet.of(Project.from(project)));
    }

    @Test
    public void fail_with_IllegalStateException_if_organization_support_is_disabled() {
        organizationFlags.setEnabled(false);
        userSession.logIn();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Organization support is disabled");
        try {
            wsTester.newRequest().execute();
        } finally {
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void fail_with_UnauthorizedException_if_user_is_not_logged_in() {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        try {
            wsTester.newRequest().execute();
        } finally {
            Mockito.verifyNoMoreInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void fail_with_IAE_if_key_param_is_missing() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'organization' parameter is missing");
        try {
            wsTester.newRequest().execute();
        } finally {
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void fail_with_IAE_if_key_is_the_one_of_default_organization() {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Default Organization can't be deleted");
        try {
            sendRequest(db.getDefaultOrganization());
        } finally {
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void fail_with_NotFoundException_if_organization_with_specified_key_does_not_exist() {
        logInAsSystemAdministrator();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Organization with key 'foo' not found");
        try {
            sendRequest("foo");
        } finally {
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void fail_with_ForbiddenException_when_user_is_not_administrator_of_specified_organization() {
        OrganizationDto organization = db.organizations().insert();
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        try {
            sendRequest(organization);
        } finally {
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void fail_with_ForbiddenException_when_user_is_system_administrator() {
        OrganizationDto organization = db.organizations().insert();
        userSession.logIn().setSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        try {
            sendRequest(organization);
        } finally {
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void fail_with_ForbiddenException_when_user_is_administrator_of_other_organization() {
        OrganizationDto organization = db.organizations().insert();
        logInAsAdministrator(db.getDefaultOrganization());
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        try {
            sendRequest(organization);
        } finally {
            Mockito.verifyZeroInteractions(projectLifeCycleListeners);
        }
    }

    @Test
    public void delete_specified_organization_if_exists_and_user_is_administrator_of_it() {
        OrganizationDto organization = db.organizations().insert();
        logInAsAdministrator(organization);
        sendRequest(organization);
        verifyOrganizationDoesNotExist(organization);
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(Collections.emptySet());
    }

    @Test
    public void delete_specified_organization_if_exists_and_user_is_organization_administrator() {
        OrganizationDto organization = db.organizations().insert();
        logInAsAdministrator(organization);
        sendRequest(organization);
        verifyOrganizationDoesNotExist(organization);
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(Collections.emptySet());
    }

    @Test
    public void delete_specified_guarded_organization_if_exists_and_user_is_system_administrator() {
        OrganizationDto organization = db.organizations().insert(( dto) -> dto.setGuarded(true));
        logInAsSystemAdministrator();
        sendRequest(organization);
        verifyOrganizationDoesNotExist(organization);
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(Collections.emptySet());
    }

    @Test
    public void delete_branches() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        ComponentDto branch = db.components().insertProjectBranch(project);
        logInAsAdministrator(organization);
        sendRequest(organization);
        verifyOrganizationDoesNotExist(organization);
        assertThat(db.countRowsOfTable(db.getSession(), "projects")).isZero();
        assertThat(db.countRowsOfTable(db.getSession(), "project_branches")).isZero();
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(ImmutableSet.of(Project.from(project)));
    }

    @Test
    public void delete_permissions_templates_and_permissions_and_groups_of_specified_organization() {
        OrganizationDto org = db.organizations().insert();
        OrganizationDto otherOrg = db.organizations().insert();
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup(org);
        GroupDto group2 = db.users().insertGroup(org);
        GroupDto otherGroup1 = db.users().insertGroup(otherOrg);
        GroupDto otherGroup2 = db.users().insertGroup(otherOrg);
        ComponentDto projectDto = db.components().insertPublicProject(org);
        ComponentDto otherProjectDto = db.components().insertPublicProject(otherOrg);
        db.users().insertPermissionOnAnyone(org, "u1");
        db.users().insertPermissionOnAnyone(otherOrg, "not deleted u1");
        db.users().insertPermissionOnUser(org, user1, "u2");
        db.users().insertPermissionOnUser(otherOrg, user1, "not deleted u2");
        db.users().insertPermissionOnGroup(group1, "u3");
        db.users().insertPermissionOnGroup(otherGroup1, "not deleted u3");
        db.users().insertProjectPermissionOnAnyone("u4", projectDto);
        db.users().insertProjectPermissionOnAnyone("not deleted u4", otherProjectDto);
        db.users().insertProjectPermissionOnGroup(group1, "u5", projectDto);
        db.users().insertProjectPermissionOnGroup(otherGroup1, "not deleted u5", otherProjectDto);
        db.users().insertProjectPermissionOnUser(user1, "u6", projectDto);
        db.users().insertProjectPermissionOnUser(user1, "not deleted u6", otherProjectDto);
        PermissionTemplateDto templateDto = db.permissionTemplates().insertTemplate(org);
        PermissionTemplateDto otherTemplateDto = db.permissionTemplates().insertTemplate(otherOrg);
        logInAsAdministrator(org);
        sendRequest(org);
        verifyOrganizationDoesNotExist(org);
        assertThat(dbClient.groupDao().selectByIds(dbSession, ImmutableList.of(group1.getId(), otherGroup1.getId(), group2.getId(), otherGroup2.getId()))).extracting(GroupDto::getId).containsOnly(otherGroup1.getId(), otherGroup2.getId());
        assertThat(dbClient.permissionTemplateDao().selectByUuid(dbSession, templateDto.getUuid())).isNull();
        assertThat(dbClient.permissionTemplateDao().selectByUuid(dbSession, otherTemplateDto.getUuid())).isNotNull();
        assertThat(db.select("select role as \"role\" from USER_ROLES")).extracting(( row) -> ((String) (row.get("role")))).doesNotContain("u2", "u6").contains("not deleted u2", "not deleted u6");
        assertThat(db.select("select role as \"role\" from GROUP_ROLES")).extracting(( row) -> ((String) (row.get("role")))).doesNotContain("u1", "u3", "u4", "u5").contains("not deleted u1", "not deleted u3", "not deleted u4", "not deleted u5");
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(ImmutableSet.of(Project.from(projectDto)));
    }

    @Test
    public void delete_members_of_specified_organization() {
        OrganizationDto org = db.organizations().insert();
        OrganizationDto otherOrg = db.organizations().insert();
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        db.organizations().addMember(org, user1);
        db.organizations().addMember(otherOrg, user1);
        db.organizations().addMember(org, user2);
        userIndexer.commitAndIndex(db.getSession(), Arrays.asList(user1, user2));
        logInAsAdministrator(org);
        sendRequest(org);
        verifyOrganizationDoesNotExist(org);
        assertThat(db.getDbClient().organizationMemberDao().select(db.getSession(), org.getUuid(), user1.getId())).isNotPresent();
        assertThat(db.getDbClient().organizationMemberDao().select(db.getSession(), org.getUuid(), user2.getId())).isNotPresent();
        assertThat(db.getDbClient().organizationMemberDao().select(db.getSession(), otherOrg.getUuid(), user1.getId())).isPresent();
        assertThat(userIndex.search(UserQuery.builder().setOrganizationUuid(org.getUuid()).build(), new SearchOptions()).getTotal()).isEqualTo(0);
        assertThat(userIndex.search(UserQuery.builder().setOrganizationUuid(otherOrg.getUuid()).build(), new SearchOptions()).getTotal()).isEqualTo(1);
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(Collections.emptySet());
    }

    @Test
    public void delete_quality_profiles_of_specified_organization() {
        OrganizationDto org = db.organizations().insert();
        OrganizationDto otherOrg = db.organizations().insert();
        QProfileDto profileInOrg = db.qualityProfiles().insert(org);
        QProfileDto profileInOtherOrg = db.qualityProfiles().insert(otherOrg);
        logInAsAdministrator(org);
        sendRequest(org);
        verifyOrganizationDoesNotExist(org);
        assertThat(db.select("select uuid as \"profileKey\" from org_qprofiles")).extracting(( row) -> ((String) (row.get("profileKey")))).containsOnly(profileInOtherOrg.getKee());
    }

    @Test
    public void delete_quality_gates() {
        QualityGateDto builtInQualityGate = db.qualityGates().insertBuiltInQualityGate();
        OrganizationDto organization = db.organizations().insert();
        db.qualityGates().associateQualityGateToOrganization(builtInQualityGate, organization);
        OrganizationDto otherOrganization = db.organizations().insert();
        db.qualityGates().associateQualityGateToOrganization(builtInQualityGate, otherOrganization);
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization);
        QGateWithOrgDto qualityGateInOtherOrg = db.qualityGates().insertQualityGate(otherOrganization);
        logInAsAdministrator(organization);
        sendRequest(organization);
        verifyOrganizationDoesNotExist(organization);
        assertThat(db.select("select uuid as \"uuid\" from quality_gates")).extracting(( row) -> ((String) (row.get("uuid")))).containsExactlyInAnyOrder(qualityGateInOtherOrg.getUuid(), builtInQualityGate.getUuid());
        assertThat(db.select("select organization_uuid as \"organizationUuid\" from org_quality_gates")).extracting(( row) -> ((String) (row.get("organizationUuid")))).containsOnly(otherOrganization.getUuid());
        // Check built-in quality gate is still available in other organization
        assertThat(db.getDbClient().qualityGateDao().selectByOrganizationAndName(db.getSession(), otherOrganization, "Sonar way")).isNotNull();
        Mockito.verify(projectLifeCycleListeners).onProjectsDeleted(Collections.emptySet());
    }

    @Test
    public void call_billing_validation_on_delete() {
        OrganizationDto organization = db.organizations().insert();
        logInAsAdministrator(organization);
        sendRequest(organization);
        Mockito.verify(billingValidationsProxy).onDelete(ArgumentMatchers.any(Organization.class));
    }

    @Test
    public void delete_organization_alm_binding() {
        OrganizationDto organization = db.organizations().insert();
        db.alm().insertOrganizationAlmBinding(organization, db.alm().insertAlmAppInstall(), true);
        logInAsAdministrator(organization);
        sendRequest(organization);
        assertThat(db.getDbClient().organizationAlmBindingDao().selectByOrganization(db.getSession(), organization)).isNotPresent();
    }
}

