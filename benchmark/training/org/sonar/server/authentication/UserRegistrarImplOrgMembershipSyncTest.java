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
package org.sonar.server.authentication;


import ExistingEmailStrategy.ALLOW;
import ExistingEmailStrategy.FORBID;
import Qualifiers.PROJECT;
import com.google.common.collect.ImmutableSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.server.authentication.UserIdentity;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbTester;
import org.sonar.db.alm.AlmAppInstallDto;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.authentication.event.AuthenticationEvent.Source;
import org.sonar.server.es.EsTester;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.OrganizationUpdater;
import org.sonar.server.organization.OrganizationValidationImpl;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.organization.TestOrganizationFlags;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.user.NewUserNotifier;
import org.sonar.server.user.UserUpdater;
import org.sonar.server.user.index.UserIndexer;
import org.sonar.server.usergroups.DefaultGroupFinder;


public class UserRegistrarImplOrgMembershipSyncTest {
    private System2 system2 = new AlwaysIncreasingSystem2();

    private static String USER_LOGIN = "github-johndoo";

    private static UserIdentity USER_IDENTITY = UserIdentity.builder().setProviderId("ABCD").setProviderLogin("johndoo").setLogin(UserRegistrarImplOrgMembershipSyncTest.USER_LOGIN).setName("John").setEmail("john@email.com").build();

    private static TestIdentityProvider GITHUB_PROVIDER = new TestIdentityProvider().setKey("github").setName("Github").setEnabled(true).setAllowsUsersToSignUp(true);

    private static TestIdentityProvider BITBUCKET_PROVIDER = new TestIdentityProvider().setKey("bitbucket").setName("Bitbucket").setEnabled(true).setAllowsUsersToSignUp(true);

    private MapSettings settings = new MapSettings();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(new AlwaysIncreasingSystem2());

    @Rule
    public EsTester es = EsTester.create();

    private UserIndexer userIndexer = new UserIndexer(db.getDbClient(), es.client());

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private OrganizationUpdater organizationUpdater = Mockito.mock(OrganizationUpdater.class);

    private TestOrganizationFlags organizationFlags = TestOrganizationFlags.standalone();

    private CredentialsLocalAuthentication localAuthentication = new CredentialsLocalAuthentication(db.getDbClient());

    private UserUpdater userUpdater = new UserUpdater(system2, Mockito.mock(NewUserNotifier.class), db.getDbClient(), userIndexer, organizationFlags, defaultOrganizationProvider, organizationUpdater, new DefaultGroupFinder(db.getDbClient()), settings.asConfig(), localAuthentication);

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private DefaultGroupFinder defaultGroupFinder = new DefaultGroupFinder(db.getDbClient());

    private UserRegistrarImpl underTest = new UserRegistrarImpl(db.getDbClient(), userUpdater, defaultOrganizationProvider, organizationFlags, new org.sonar.server.organization.OrganizationUpdaterImpl(db.getDbClient(), Mockito.mock(System2.class), UuidFactoryFast.getInstance(), new OrganizationValidationImpl(), settings.asConfig(), null, null, null, permissionService), defaultGroupFinder, new org.sonar.server.organization.MemberUpdater(db.getDbClient(), defaultGroupFinder, userIndexer));

    @Test
    public void authenticate_new_github_user_syncs_organization() {
        organizationFlags.setEnabled(true);
        OrganizationDto organization = db.organizations().insert();
        db.users().insertDefaultGroup(organization, "Members");
        AlmAppInstallDto gitHubInstall = db.alm().insertAlmAppInstall(( a) -> a.setAlm(GITHUB));
        db.alm().insertOrganizationAlmBinding(organization, gitHubInstall, true);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplOrgMembershipSyncTest.USER_IDENTITY).setProvider(UserRegistrarImplOrgMembershipSyncTest.GITHUB_PROVIDER).setSource(Source.realm(Method.BASIC, UserRegistrarImplOrgMembershipSyncTest.GITHUB_PROVIDER.getName())).setExistingEmailStrategy(ALLOW).setUpdateLoginStrategy(UpdateLoginStrategy.ALLOW).setOrganizationAlmIds(ImmutableSet.of(gitHubInstall.getOrganizationAlmId())).build());
        UserDto user = db.users().selectUserByLogin(UserRegistrarImplOrgMembershipSyncTest.USER_LOGIN).get();
        db.organizations().assertUserIsMemberOfOrganization(organization, user);
    }

    @Test
    public void authenticate_new_github_user_does_not_sync_organization_when_no_org_alm_ids_provided() {
        organizationFlags.setEnabled(true);
        OrganizationDto organization = db.organizations().insert();
        db.users().insertDefaultGroup(organization, "Members");
        AlmAppInstallDto gitHubInstall = db.alm().insertAlmAppInstall(( a) -> a.setAlm(GITHUB));
        db.alm().insertOrganizationAlmBinding(organization, gitHubInstall, true);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplOrgMembershipSyncTest.USER_IDENTITY).setProvider(UserRegistrarImplOrgMembershipSyncTest.GITHUB_PROVIDER).setSource(Source.realm(Method.BASIC, UserRegistrarImplOrgMembershipSyncTest.GITHUB_PROVIDER.getName())).setExistingEmailStrategy(ALLOW).setUpdateLoginStrategy(UpdateLoginStrategy.ALLOW).setOrganizationAlmIds(null).build());
        UserDto user = db.users().selectUserByLogin(UserRegistrarImplOrgMembershipSyncTest.USER_LOGIN).get();
        db.organizations().assertUserIsNotMemberOfOrganization(organization, user);
    }

    @Test
    public void authenticate_new_bitbucket_user_does_not_sync_organization() {
        organizationFlags.setEnabled(true);
        OrganizationDto organization = db.organizations().insert();
        db.users().insertDefaultGroup(organization, "Members");
        AlmAppInstallDto gitHubInstall = db.alm().insertAlmAppInstall(( a) -> a.setAlm(BITBUCKETCLOUD));
        db.alm().insertOrganizationAlmBinding(organization, gitHubInstall, true);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplOrgMembershipSyncTest.USER_IDENTITY).setProvider(UserRegistrarImplOrgMembershipSyncTest.BITBUCKET_PROVIDER).setSource(Source.realm(Method.BASIC, UserRegistrarImplOrgMembershipSyncTest.BITBUCKET_PROVIDER.getName())).setExistingEmailStrategy(ALLOW).setUpdateLoginStrategy(UpdateLoginStrategy.ALLOW).setOrganizationAlmIds(ImmutableSet.of(gitHubInstall.getOrganizationAlmId())).build());
        UserDto user = db.users().selectUserByLogin(UserRegistrarImplOrgMembershipSyncTest.USER_LOGIN).get();
        db.organizations().assertUserIsNotMemberOfOrganization(organization, user);
    }

    @Test
    public void authenticate_new_user_using_unknown_alm_does_not_sync_organization() {
        organizationFlags.setEnabled(true);
        OrganizationDto organization = db.organizations().insert();
        db.users().insertDefaultGroup(organization, "Members");
        AlmAppInstallDto almAppInstall = db.alm().insertAlmAppInstall(( a) -> a.setAlm(GITHUB));
        db.alm().insertOrganizationAlmBinding(organization, almAppInstall, true);
        TestIdentityProvider identityProvider = new TestIdentityProvider().setKey("unknown").setName("unknown").setEnabled(true).setAllowsUsersToSignUp(true);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplOrgMembershipSyncTest.USER_IDENTITY).setProvider(identityProvider).setSource(Source.realm(Method.BASIC, identityProvider.getName())).setExistingEmailStrategy(ALLOW).setUpdateLoginStrategy(UpdateLoginStrategy.ALLOW).setOrganizationAlmIds(ImmutableSet.of(almAppInstall.getOrganizationAlmId())).build());
        UserDto user = db.users().selectUserByLogin(UserRegistrarImplOrgMembershipSyncTest.USER_LOGIN).get();
        db.organizations().assertUserIsNotMemberOfOrganization(organization, user);
    }

    @Test
    public void authenticate_existing_github_user_does_not_sync_organization() {
        organizationFlags.setEnabled(true);
        OrganizationDto organization = db.organizations().insert();
        db.users().insertDefaultGroup(organization, "Members");
        AlmAppInstallDto gitHubInstall = db.alm().insertAlmAppInstall(( a) -> a.setAlm(GITHUB));
        db.alm().insertOrganizationAlmBinding(organization, gitHubInstall, true);
        UserDto user = db.users().insertUser(( u) -> u.setLogin("Old login").setExternalId(UserRegistrarImplOrgMembershipSyncTest.USER_IDENTITY.getProviderId()).setExternalIdentityProvider(UserRegistrarImplOrgMembershipSyncTest.GITHUB_PROVIDER.getKey()));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplOrgMembershipSyncTest.USER_IDENTITY).setProvider(UserRegistrarImplOrgMembershipSyncTest.GITHUB_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(UpdateLoginStrategy.ALLOW).setOrganizationAlmIds(ImmutableSet.of(gitHubInstall.getOrganizationAlmId())).build());
        db.organizations().assertUserIsNotMemberOfOrganization(organization, user);
    }

    @Test
    public void authenticate_disabled_github_user_syncs_organization() {
        organizationFlags.setEnabled(true);
        OrganizationDto organization = db.organizations().insert();
        db.users().insertDefaultGroup(organization, "Members");
        AlmAppInstallDto gitHubInstall = db.alm().insertAlmAppInstall(( a) -> a.setAlm(GITHUB));
        db.alm().insertOrganizationAlmBinding(organization, gitHubInstall, true);
        UserDto user = db.users().insertDisabledUser(( u) -> u.setLogin(UserRegistrarImplOrgMembershipSyncTest.USER_LOGIN));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplOrgMembershipSyncTest.USER_IDENTITY).setProvider(UserRegistrarImplOrgMembershipSyncTest.GITHUB_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(UpdateLoginStrategy.ALLOW).setOrganizationAlmIds(ImmutableSet.of(gitHubInstall.getOrganizationAlmId())).build());
        db.organizations().assertUserIsMemberOfOrganization(organization, user);
    }
}

