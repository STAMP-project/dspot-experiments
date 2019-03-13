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


import AuthenticationEvent.Method.FORM;
import ExistingEmailStrategy.FORBID;
import ExistingEmailStrategy.WARN;
import Qualifiers.PROJECT;
import UpdateLoginStrategy.ALLOW;
import com.google.common.collect.Sets;
import java.util.Optional;
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
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.event.AuthenticationEvent.Source;
import org.sonar.server.authentication.event.AuthenticationExceptionMatcher;
import org.sonar.server.authentication.exception.EmailAlreadyExistsRedirectionException;
import org.sonar.server.authentication.exception.UpdateLoginRedirectionException;
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


public class UserRegistrarImplTest {
    private System2 system2 = new AlwaysIncreasingSystem2();

    private static String USER_LOGIN = "github-johndoo";

    private static UserIdentity USER_IDENTITY = UserIdentity.builder().setProviderId("ABCD").setProviderLogin("johndoo").setLogin(UserRegistrarImplTest.USER_LOGIN).setName("John").setEmail("john@email.com").build();

    private static TestIdentityProvider IDENTITY_PROVIDER = new TestIdentityProvider().setKey("github").setName("name of github").setEnabled(true).setAllowsUsersToSignUp(true);

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
    public void authenticate_new_user() {
        organizationFlags.setEnabled(true);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.realm(Method.BASIC, UserRegistrarImplTest.IDENTITY_PROVIDER.getName())).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        UserDto user = db.users().selectUserByLogin(UserRegistrarImplTest.USER_LOGIN).get();
        assertThat(user).isNotNull();
        assertThat(user.isActive()).isTrue();
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getEmail()).isEqualTo("john@email.com");
        assertThat(user.getExternalLogin()).isEqualTo("johndoo");
        assertThat(user.getExternalIdentityProvider()).isEqualTo("github");
        assertThat(user.getExternalId()).isEqualTo("ABCD");
        assertThat(user.isRoot()).isFalse();
        checkGroupMembership(user);
    }

    @Test
    public void authenticate_new_user_generate_login_when_no_login_provided() {
        organizationFlags.setEnabled(true);
        underTest.register(UserRegistration.builder().setUserIdentity(UserIdentity.builder().setProviderId("ABCD").setProviderLogin("johndoo").setName("John Doe").setEmail("john@email.com").build()).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.realm(Method.BASIC, UserRegistrarImplTest.IDENTITY_PROVIDER.getName())).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        UserDto user = db.getDbClient().userDao().selectByEmail(db.getSession(), "john@email.com").get(0);
        assertThat(user).isNotNull();
        assertThat(user.isActive()).isTrue();
        assertThat(user.getLogin()).isNotEqualTo("John Doe").startsWith("john-doe");
        assertThat(user.getEmail()).isEqualTo("john@email.com");
        assertThat(user.getExternalLogin()).isEqualTo("johndoo");
        assertThat(user.getExternalIdentityProvider()).isEqualTo("github");
        assertThat(user.getExternalId()).isEqualTo("ABCD");
    }

    @Test
    public void authenticate_new_user_with_groups() {
        organizationFlags.setEnabled(true);
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group1");
        GroupDto group2 = db.users().insertGroup(db.getDefaultOrganization(), "group2");
        authenticate(UserRegistrarImplTest.USER_LOGIN, "group1", "group2", "group3");
        Optional<UserDto> user = db.users().selectUserByLogin(UserRegistrarImplTest.USER_LOGIN);
        checkGroupMembership(user.get(), group1, group2);
    }

    @Test
    public void authenticate_new_user_and_force_default_group_when_organizations_are_disabled() {
        organizationFlags.setEnabled(false);
        UserDto user = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group1");
        GroupDto defaultGroup = insertDefaultGroup();
        db.users().insertMember(group1, user);
        db.users().insertMember(defaultGroup, user);
        authenticate(user.getLogin(), "group1");
        checkGroupMembership(user, group1, defaultGroup);
    }

    @Test
    public void does_not_force_default_group_when_authenticating_new_user_if_organizations_are_enabled() {
        organizationFlags.setEnabled(true);
        UserDto user = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group1");
        GroupDto defaultGroup = insertDefaultGroup();
        db.users().insertMember(group1, user);
        db.users().insertMember(defaultGroup, user);
        authenticate(user.getLogin(), "group1");
        checkGroupMembership(user, group1);
    }

    @Test
    public void authenticate_new_user_sets_onboarded_flag_to_false_when_onboarding_setting_is_set_to_true() {
        organizationFlags.setEnabled(true);
        settings.setProperty(ONBOARDING_TUTORIAL_SHOW_TO_NEW_USERS.getKey(), true);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.users().selectUserByLogin(UserRegistrarImplTest.USER_LOGIN).get().isOnboarded()).isFalse();
    }

    @Test
    public void authenticate_new_user_sets_onboarded_flag_to_true_when_onboarding_setting_is_set_to_false() {
        organizationFlags.setEnabled(true);
        settings.setProperty(ONBOARDING_TUTORIAL_SHOW_TO_NEW_USERS.getKey(), false);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.users().selectUserByLogin(UserRegistrarImplTest.USER_LOGIN).get().isOnboarded()).isTrue();
    }

    @Test
    public void external_id_is_set_to_provider_login_when_null() {
        organizationFlags.setEnabled(true);
        UserIdentity newUser = UserIdentity.builder().setProviderId(null).setLogin("john").setProviderLogin("johndoo").setName("JOhn").build();
        underTest.register(UserRegistration.builder().setUserIdentity(newUser).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.users().selectUserByLogin(newUser.getLogin()).get()).extracting(UserDto::getLogin, UserDto::getExternalId, UserDto::getExternalLogin).contains("john", "johndoo", "johndoo");
    }

    @Test
    public void authenticate_new_user_update_existing_user_email_when_strategy_is_ALLOW() {
        organizationFlags.setEnabled(true);
        UserDto existingUser = db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        UserIdentity newUser = UserIdentity.builder().setProviderLogin("johndoo").setLogin("new_login").setName(existingUser.getName()).setEmail(existingUser.getEmail()).build();
        underTest.register(UserRegistration.builder().setUserIdentity(newUser).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(ExistingEmailStrategy.ALLOW).setUpdateLoginStrategy(ALLOW).build());
        UserDto newUserReloaded = db.users().selectUserByLogin(newUser.getLogin()).get();
        assertThat(newUserReloaded.getEmail()).isEqualTo(existingUser.getEmail());
        UserDto existingUserReloaded = db.users().selectUserByLogin(existingUser.getLogin()).get();
        assertThat(existingUserReloaded.getEmail()).isNull();
    }

    @Test
    public void throw_EmailAlreadyExistException_when_authenticating_new_user_when_email_already_exists_and_strategy_is_WARN() {
        organizationFlags.setEnabled(true);
        UserDto existingUser = db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        UserIdentity newUser = UserIdentity.builder().setProviderLogin("johndoo").setLogin("new_login").setName(existingUser.getName()).setEmail(existingUser.getEmail()).build();
        expectedException.expect(EmailAlreadyExistsRedirectionException.class);
        underTest.register(UserRegistration.builder().setUserIdentity(newUser).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(WARN).setUpdateLoginStrategy(ALLOW).build());
    }

    @Test
    public void throw_AuthenticationException_when_authenticating_new_user_when_email_already_exists_and_strategy_is_FORBID() {
        db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        Source source = Source.realm(FORM, UserRegistrarImplTest.IDENTITY_PROVIDER.getName());
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(source).withLogin(UserRegistrarImplTest.USER_IDENTITY.getProviderLogin()).andPublicMessage(("You can't sign up because email 'john@email.com' is already used by an existing user. " + "This means that you probably already registered with another account.")));
        expectedException.expectMessage("Email 'john@email.com' is already used");
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(source).setExistingEmailStrategy(ExistingEmailStrategy.FORBID).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
    }

    @Test
    public void throw_AuthenticationException_when_authenticating_new_user_and_email_already_exists_multiple_times() {
        db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        Source source = Source.realm(FORM, UserRegistrarImplTest.IDENTITY_PROVIDER.getName());
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(source).withLogin(UserRegistrarImplTest.USER_IDENTITY.getProviderLogin()).andPublicMessage(("You can't sign up because email 'john@email.com' is already used by an existing user. " + "This means that you probably already registered with another account.")));
        expectedException.expectMessage("Email 'john@email.com' is already used");
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(source).setExistingEmailStrategy(ExistingEmailStrategy.FORBID).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
    }

    @Test
    public void fail_to_authenticate_new_user_when_allow_users_to_signup_is_false() {
        TestIdentityProvider identityProvider = new TestIdentityProvider().setKey("github").setName("Github").setEnabled(true).setAllowsUsersToSignUp(false);
        Source source = Source.realm(FORM, identityProvider.getName());
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(source).withLogin(UserRegistrarImplTest.USER_IDENTITY.getProviderLogin()).andPublicMessage("'github' users are not allowed to sign up"));
        expectedException.expectMessage("User signup disabled for provider 'github'");
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(identityProvider).setSource(source).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
    }

    @Test
    public void authenticate_and_update_existing_user_matching_login() {
        db.users().insertUser(( u) -> u.setLogin(UserRegistrarImplTest.USER_LOGIN).setName("Old name").setEmail("Old email").setExternalId("old id").setExternalLogin("old identity").setExternalIdentityProvider("old provide"));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.users().selectUserByLogin(UserRegistrarImplTest.USER_LOGIN).get()).extracting(UserDto::getName, UserDto::getEmail, UserDto::getExternalId, UserDto::getExternalLogin, UserDto::getExternalIdentityProvider, UserDto::isActive).contains("John", "john@email.com", "ABCD", "johndoo", "github", true);
    }

    @Test
    public void authenticate_and_update_existing_user_matching_external_id() {
        UserDto user = db.users().insertUser(( u) -> u.setLogin("Old login").setName("Old name").setEmail("Old email").setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.users().selectUserByLogin("Old login")).isNotPresent();
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getName, UserDto::getEmail, UserDto::getExternalId, UserDto::getExternalLogin, UserDto::getExternalIdentityProvider, UserDto::isActive).contains(UserRegistrarImplTest.USER_LOGIN, "John", "john@email.com", "ABCD", "johndoo", "github", true);
    }

    @Test
    public void authenticate_and_update_existing_user_matching_external_login() {
        UserDto user = db.users().insertUser(( u) -> u.setLogin("Old login").setName("Old name").setEmail(UserRegistrarImplTest.USER_IDENTITY.getEmail()).setExternalId("Old id").setExternalLogin(UserRegistrarImplTest.USER_IDENTITY.getProviderLogin()).setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.users().selectUserByLogin("Old login")).isNotPresent();
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getName, UserDto::getEmail, UserDto::getExternalId, UserDto::getExternalLogin, UserDto::getExternalIdentityProvider, UserDto::isActive).contains(UserRegistrarImplTest.USER_LOGIN, "John", "john@email.com", "ABCD", "johndoo", "github", true);
    }

    @Test
    public void authenticate_existing_user_and_update_only_login() {
        UserDto user = db.users().insertUser(( u) -> u.setLogin("old login").setName(UserRegistrarImplTest.USER_IDENTITY.getName()).setEmail(UserRegistrarImplTest.USER_IDENTITY.getEmail()).setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.users().selectUserByLogin("Old login")).isNotPresent();
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getName, UserDto::getEmail, UserDto::getExternalId, UserDto::getExternalLogin, UserDto::getExternalIdentityProvider, UserDto::isActive).containsExactlyInAnyOrder(UserRegistrarImplTest.USER_LOGIN, UserRegistrarImplTest.USER_IDENTITY.getName(), UserRegistrarImplTest.USER_IDENTITY.getEmail(), UserRegistrarImplTest.USER_IDENTITY.getProviderId(), UserRegistrarImplTest.USER_IDENTITY.getProviderLogin(), UserRegistrarImplTest.IDENTITY_PROVIDER.getKey(), true);
    }

    @Test
    public void authenticate_existing_user_and_update_only_identity_provider_key() {
        UserDto user = db.users().insertUser(( u) -> u.setLogin(UserRegistrarImplTest.USER_LOGIN).setName(UserRegistrarImplTest.USER_IDENTITY.getName()).setEmail(UserRegistrarImplTest.USER_IDENTITY.getEmail()).setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin(UserRegistrarImplTest.USER_IDENTITY.getProviderLogin()).setExternalIdentityProvider("old identity provider"));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getName, UserDto::getEmail, UserDto::getExternalId, UserDto::getExternalLogin, UserDto::getExternalIdentityProvider, UserDto::isActive).containsExactlyInAnyOrder(UserRegistrarImplTest.USER_LOGIN, UserRegistrarImplTest.USER_IDENTITY.getName(), UserRegistrarImplTest.USER_IDENTITY.getEmail(), UserRegistrarImplTest.USER_IDENTITY.getProviderId(), UserRegistrarImplTest.USER_IDENTITY.getProviderLogin(), UserRegistrarImplTest.IDENTITY_PROVIDER.getKey(), true);
    }

    @Test
    public void authenticate_existing_user_matching_login_when_external_id_is_null() {
        UserDto user = db.users().insertUser(( u) -> u.setLogin(UserRegistrarImplTest.USER_LOGIN).setName("Old name").setEmail("Old email").setExternalId("Old id").setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()));
        underTest.register(UserRegistration.builder().setUserIdentity(UserIdentity.builder().setProviderId(null).setProviderLogin("johndoo").setLogin(UserRegistrarImplTest.USER_LOGIN).setName("John").setEmail("john@email.com").build()).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getName, UserDto::getEmail, UserDto::getExternalId, UserDto::getExternalLogin, UserDto::getExternalIdentityProvider, UserDto::isActive).contains(user.getLogin(), "John", "john@email.com", "johndoo", "johndoo", "github", true);
    }

    @Test
    public void authenticate_existing_user_when_login_is_not_provided() {
        UserDto user = db.users().insertUser(( u) -> u.setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()));
        underTest.register(UserRegistration.builder().setUserIdentity(// No login provided
        UserIdentity.builder().setProviderId(user.getExternalId()).setProviderLogin(user.getExternalLogin()).setName(user.getName()).setEmail(user.getEmail()).build()).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        // No new user is created
        assertThat(db.countRowsOfTable(db.getSession(), "users")).isEqualTo(1);
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getName, UserDto::getEmail, UserDto::getExternalId, UserDto::getExternalLogin, UserDto::getExternalIdentityProvider, UserDto::isActive).contains(user.getLogin(), user.getName(), user.getEmail(), user.getExternalId(), user.getExternalLogin(), user.getExternalIdentityProvider(), true);
    }

    @Test
    public void authenticate_existing_user_with_login_update_and_strategy_is_ALLOW() {
        UserDto user = db.users().insertUser(( u) -> u.setLogin("Old login").setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getExternalLogin).contains(UserRegistrarImplTest.USER_LOGIN, UserRegistrarImplTest.USER_IDENTITY.getProviderLogin());
    }

    @Test
    public void authenticate_existing_user_with_login_update_and_personal_org_does_not_exits_and_strategy_is_WARN() {
        organizationFlags.setEnabled(true);
        UserDto user = db.users().insertUser(( u) -> u.setLogin("Old login").setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()).setOrganizationUuid(null));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(UpdateLoginStrategy.WARN).build());
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getExternalLogin).contains(UserRegistrarImplTest.USER_LOGIN, UserRegistrarImplTest.USER_IDENTITY.getProviderLogin());
    }

    @Test
    public void throw_UpdateLoginRedirectionException_when_authenticating_with_login_update_and_personal_org_exists_and_strategy_is_WARN() {
        organizationFlags.setEnabled(true);
        OrganizationDto organization = db.organizations().insert(( o) -> o.setKey("Old login"));
        db.users().insertUser(( u) -> u.setLogin("Old login").setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()).setOrganizationUuid(organization.getUuid()));
        expectedException.expect(UpdateLoginRedirectionException.class);
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(UpdateLoginStrategy.WARN).build());
    }

    @Test
    public void authenticate_existing_user_and_update_personal_og_key_when_personal_org_exists_and_strategy_is_ALLOW() {
        organizationFlags.setEnabled(true);
        OrganizationDto personalOrganization = db.organizations().insert(( o) -> o.setKey("Old login"));
        UserDto user = db.users().insertUser(( u) -> u.setLogin("Old login").setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()).setOrganizationUuid(personalOrganization.getUuid()));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        assertThat(db.getDbClient().userDao().selectByUuid(db.getSession(), user.getUuid())).extracting(UserDto::getLogin, UserDto::getExternalLogin).contains(UserRegistrarImplTest.USER_LOGIN, UserRegistrarImplTest.USER_IDENTITY.getProviderLogin());
        OrganizationDto organizationReloaded = db.getDbClient().organizationDao().selectByUuid(db.getSession(), personalOrganization.getUuid()).get();
        assertThat(organizationReloaded.getKey()).isEqualTo(UserRegistrarImplTest.USER_LOGIN);
    }

    @Test
    public void fail_to_authenticate_existing_user_when_personal_org_does_not_exist() {
        organizationFlags.setEnabled(true);
        db.users().insertUser(( u) -> u.setLogin("Old login").setExternalId(UserRegistrarImplTest.USER_IDENTITY.getProviderId()).setExternalLogin("old identity").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()).setOrganizationUuid("unknown"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot find personal organization uuid 'unknown' for user 'Old login'");
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
    }

    @Test
    public void authenticate_existing_disabled_user() {
        organizationFlags.setEnabled(true);
        db.users().insertUser(( u) -> u.setLogin(UserRegistrarImplTest.USER_LOGIN).setActive(false).setName("Old name").setEmail("Old email").setExternalId("old id").setExternalLogin("old identity").setExternalIdentityProvider("old provide"));
        underTest.register(UserRegistration.builder().setUserIdentity(UserRegistrarImplTest.USER_IDENTITY).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        UserDto userDto = db.users().selectUserByLogin(UserRegistrarImplTest.USER_LOGIN).get();
        assertThat(userDto.isActive()).isTrue();
        assertThat(userDto.getName()).isEqualTo("John");
        assertThat(userDto.getEmail()).isEqualTo("john@email.com");
        assertThat(userDto.getExternalId()).isEqualTo("ABCD");
        assertThat(userDto.getExternalLogin()).isEqualTo("johndoo");
        assertThat(userDto.getExternalIdentityProvider()).isEqualTo("github");
        assertThat(userDto.isRoot()).isFalse();
    }

    @Test
    public void authenticate_existing_user_when_email_already_exists_and_strategy_is_ALLOW() {
        organizationFlags.setEnabled(true);
        UserDto existingUser = db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        UserDto currentUser = db.users().insertUser(( u) -> u.setEmail(null));
        UserIdentity userIdentity = UserIdentity.builder().setLogin(currentUser.getLogin()).setProviderLogin("johndoo").setName("John").setEmail("john@email.com").build();
        underTest.register(UserRegistration.builder().setUserIdentity(userIdentity).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(ExistingEmailStrategy.ALLOW).setUpdateLoginStrategy(ALLOW).build());
        UserDto currentUserReloaded = db.users().selectUserByLogin(currentUser.getLogin()).get();
        assertThat(currentUserReloaded.getEmail()).isEqualTo("john@email.com");
        UserDto existingUserReloaded = db.users().selectUserByLogin(existingUser.getLogin()).get();
        assertThat(existingUserReloaded.getEmail()).isNull();
    }

    @Test
    public void throw_EmailAlreadyExistException_when_authenticating_existing_user_when_email_already_exists_and_strategy_is_WARN() {
        organizationFlags.setEnabled(true);
        UserDto existingUser = db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        UserDto currentUser = db.users().insertUser(( u) -> u.setEmail(null));
        UserIdentity userIdentity = UserIdentity.builder().setLogin(currentUser.getLogin()).setProviderLogin("johndoo").setName("John").setEmail("john@email.com").build();
        expectedException.expect(EmailAlreadyExistsRedirectionException.class);
        underTest.register(UserRegistration.builder().setUserIdentity(userIdentity).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(WARN).setUpdateLoginStrategy(ALLOW).build());
    }

    @Test
    public void throw_AuthenticationException_when_authenticating_existing_user_when_email_already_exists_and_strategy_is_FORBID() {
        organizationFlags.setEnabled(true);
        UserDto existingUser = db.users().insertUser(( u) -> u.setEmail("john@email.com"));
        UserDto currentUser = db.users().insertUser(( u) -> u.setEmail(null));
        UserIdentity userIdentity = UserIdentity.builder().setLogin(currentUser.getLogin()).setProviderLogin("johndoo").setName("John").setEmail("john@email.com").build();
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.realm(FORM, UserRegistrarImplTest.IDENTITY_PROVIDER.getName())).withLogin(userIdentity.getProviderLogin()).andPublicMessage(("You can't sign up because email 'john@email.com' is already used by an existing user. " + "This means that you probably already registered with another account.")));
        expectedException.expectMessage("Email 'john@email.com' is already used");
        underTest.register(UserRegistration.builder().setUserIdentity(userIdentity).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.realm(FORM, UserRegistrarImplTest.IDENTITY_PROVIDER.getName())).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
    }

    @Test
    public void does_not_fail_to_authenticate_user_when_email_has_not_changed_and_strategy_is_FORBID() {
        organizationFlags.setEnabled(true);
        UserDto currentUser = db.users().insertUser(( u) -> u.setEmail("john@email.com").setExternalIdentityProvider(UserRegistrarImplTest.IDENTITY_PROVIDER.getKey()));
        UserIdentity userIdentity = UserIdentity.builder().setLogin(currentUser.getLogin()).setProviderId(currentUser.getExternalId()).setProviderLogin(currentUser.getExternalLogin()).setName("John").setEmail("john@email.com").build();
        underTest.register(UserRegistration.builder().setUserIdentity(userIdentity).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        UserDto currentUserReloaded = db.users().selectUserByLogin(currentUser.getLogin()).get();
        assertThat(currentUserReloaded.getEmail()).isEqualTo("john@email.com");
    }

    @Test
    public void authenticate_existing_user_and_add_new_groups() {
        organizationFlags.setEnabled(true);
        UserDto user = db.users().insertUser(UserTesting.newUserDto().setLogin(UserRegistrarImplTest.USER_LOGIN).setActive(true).setName("John"));
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group1");
        GroupDto group2 = db.users().insertGroup(db.getDefaultOrganization(), "group2");
        authenticate(UserRegistrarImplTest.USER_LOGIN, "group1", "group2", "group3");
        checkGroupMembership(user, group1, group2);
    }

    @Test
    public void authenticate_existing_user_and_remove_groups() {
        organizationFlags.setEnabled(true);
        UserDto user = db.users().insertUser(UserTesting.newUserDto().setLogin(UserRegistrarImplTest.USER_LOGIN).setActive(true).setName("John"));
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group1");
        GroupDto group2 = db.users().insertGroup(db.getDefaultOrganization(), "group2");
        db.users().insertMember(group1, user);
        db.users().insertMember(group2, user);
        authenticate(UserRegistrarImplTest.USER_LOGIN, "group1");
        checkGroupMembership(user, group1);
    }

    @Test
    public void authenticate_existing_user_and_remove_all_groups_expect_default_when_organizations_are_disabled() {
        organizationFlags.setEnabled(false);
        UserDto user = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group1");
        GroupDto group2 = db.users().insertGroup(db.getDefaultOrganization(), "group2");
        GroupDto defaultGroup = insertDefaultGroup();
        db.users().insertMember(group1, user);
        db.users().insertMember(group2, user);
        db.users().insertMember(defaultGroup, user);
        authenticate(user.getLogin());
        checkGroupMembership(user, defaultGroup);
    }

    @Test
    public void does_not_force_default_group_when_authenticating_existing_user_when_organizations_are_enabled() {
        organizationFlags.setEnabled(true);
        UserDto user = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup(db.getDefaultOrganization(), "group1");
        GroupDto defaultGroup = insertDefaultGroup();
        db.users().insertMember(group1, user);
        db.users().insertMember(defaultGroup, user);
        authenticate(user.getLogin(), "group1");
        checkGroupMembership(user, group1);
    }

    @Test
    public void ignore_groups_on_non_default_organizations() {
        organizationFlags.setEnabled(true);
        OrganizationDto org = db.organizations().insert();
        UserDto user = db.users().insertUser(UserTesting.newUserDto().setLogin(UserRegistrarImplTest.USER_LOGIN).setActive(true).setName("John"));
        String groupName = "a-group";
        GroupDto groupInDefaultOrg = db.users().insertGroup(db.getDefaultOrganization(), groupName);
        GroupDto groupInOrg = db.users().insertGroup(org, groupName);
        // adding a group with the same name than in non-default organization
        underTest.register(UserRegistration.builder().setUserIdentity(UserIdentity.builder().setProviderLogin("johndoo").setLogin(user.getLogin()).setName(user.getName()).setGroups(Sets.newHashSet(groupName)).build()).setProvider(UserRegistrarImplTest.IDENTITY_PROVIDER).setSource(Source.local(Method.BASIC)).setExistingEmailStrategy(FORBID).setUpdateLoginStrategy(ALLOW).build());
        checkGroupMembership(user, groupInDefaultOrg);
    }
}

