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


import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.sonar.server.authentication.event.AuthenticationEvent;
import org.sonar.server.authentication.event.AuthenticationEvent.Source;
import org.sonar.server.authentication.event.AuthenticationExceptionMatcher;
import org.sonar.server.es.EsTester;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.OrganizationUpdater;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.organization.TestOrganizationFlags;
import org.sonar.server.user.NewUserNotifier;
import org.sonar.server.user.index.UserIndexer;


public class HttpHeadersAuthenticationTest {
    private MapSettings settings = new MapSettings();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(new AlwaysIncreasingSystem2());

    @Rule
    public EsTester es = EsTester.create();

    private static final String DEFAULT_LOGIN = "john";

    private static final String DEFAULT_NAME = "John";

    private static final String DEFAULT_EMAIL = "john@doo.com";

    private static final String GROUP1 = "dev";

    private static final String GROUP2 = "admin";

    private static final String GROUPS = ((HttpHeadersAuthenticationTest.GROUP1) + ",") + (HttpHeadersAuthenticationTest.GROUP2);

    private static final Long NOW = 1000000L;

    private static final Long CLOSE_REFRESH_TIME = (HttpHeadersAuthenticationTest.NOW) - 1000L;

    private static final UserDto DEFAULT_USER = UserTesting.newUserDto().setLogin(HttpHeadersAuthenticationTest.DEFAULT_LOGIN).setName(HttpHeadersAuthenticationTest.DEFAULT_NAME).setEmail(HttpHeadersAuthenticationTest.DEFAULT_EMAIL).setExternalLogin(HttpHeadersAuthenticationTest.DEFAULT_LOGIN).setExternalIdentityProvider("sonarqube");

    private GroupDto group1;

    private GroupDto group2;

    private GroupDto sonarUsers;

    private System2 system2 = Mockito.mock(System2.class);

    private OrganizationUpdater organizationUpdater = Mockito.mock(OrganizationUpdater.class);

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private TestOrganizationFlags organizationFlags = TestOrganizationFlags.standalone();

    private CredentialsLocalAuthentication localAuthentication = new CredentialsLocalAuthentication(db.getDbClient());

    private UserIndexer userIndexer = new UserIndexer(db.getDbClient(), es.client());

    private UserRegistrarImpl userIdentityAuthenticator = new UserRegistrarImpl(db.getDbClient(), new org.sonar.server.user.UserUpdater(system2, Mockito.mock(NewUserNotifier.class), db.getDbClient(), userIndexer, organizationFlags, defaultOrganizationProvider, organizationUpdater, new org.sonar.server.usergroups.DefaultGroupFinder(db.getDbClient()), settings.asConfig(), localAuthentication), defaultOrganizationProvider, organizationFlags, Mockito.mock(OrganizationUpdater.class), new org.sonar.server.usergroups.DefaultGroupFinder(db.getDbClient()), null);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private JwtHttpHandler jwtHttpHandler = Mockito.mock(JwtHttpHandler.class);

    private AuthenticationEvent authenticationEvent = Mockito.mock(AuthenticationEvent.class);

    private HttpHeadersAuthentication underTest = new HttpHeadersAuthentication(system2, settings.asConfig(), userIdentityAuthenticator, jwtHttpHandler, authenticationEvent);

    @Test
    public void create_user_when_authenticating_new_user() {
        startWithSso();
        setNotUserInToken();
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, HttpHeadersAuthenticationTest.GROUPS);
        underTest.authenticate(request, response);
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, group1, group2, sonarUsers);
        verifyTokenIsUpdated(HttpHeadersAuthenticationTest.NOW);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void use_login_when_name_is_not_provided() {
        startWithSso();
        setNotUserInToken();
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, null, null, null);
        underTest.authenticate(request, response);
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, null, sonarUsers);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void update_user_when_authenticating_exiting_user() {
        startWithSso();
        setNotUserInToken();
        insertUser(UserTesting.newUserDto().setLogin(HttpHeadersAuthenticationTest.DEFAULT_LOGIN).setName("old name").setEmail("old email"), group1);
        // Name, email and groups are different
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, HttpHeadersAuthenticationTest.GROUP2);
        underTest.authenticate(request, response);
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, group2);
        verifyTokenIsUpdated(HttpHeadersAuthenticationTest.NOW);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void remove_groups_when_group_headers_is_empty() {
        startWithSso();
        setNotUserInToken();
        insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1);
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, "");
        underTest.authenticate(request, response);
        verityUserHasNoGroup(HttpHeadersAuthenticationTest.DEFAULT_LOGIN);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void remove_groups_when_group_headers_is_null() {
        startWithSso();
        setNotUserInToken();
        insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1);
        Map<String, String> headerValuesByName = new HashMap<>();
        headerValuesByName.put("X-Forwarded-Login", HttpHeadersAuthenticationTest.DEFAULT_LOGIN);
        headerValuesByName.put("X-Forwarded-Groups", null);
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(headerValuesByName);
        underTest.authenticate(request, response);
        verityUserHasNoGroup(HttpHeadersAuthenticationTest.DEFAULT_LOGIN);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void does_not_update_groups_when_no_group_headers() {
        startWithSso();
        setNotUserInToken();
        insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1, sonarUsers);
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, null);
        underTest.authenticate(request, response);
        verityUserGroups(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, group1, sonarUsers);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void does_not_update_user_when_user_is_in_token_and_refresh_time_is_close() {
        startWithSso();
        UserDto user = insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1);
        setUserInToken(user, HttpHeadersAuthenticationTest.CLOSE_REFRESH_TIME);
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "new name", "new email", HttpHeadersAuthenticationTest.GROUP2);
        underTest.authenticate(request, response);
        // User is not updated
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, group1);
        verifyTokenIsNotUpdated();
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void update_user_when_user_in_token_but_refresh_time_is_old() {
        startWithSso();
        UserDto user = insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1);
        // Refresh time was updated 6 minutes ago => more than 5 minutes
        setUserInToken(user, ((HttpHeadersAuthenticationTest.NOW) - ((6 * 60) * 1000L)));
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "new name", "new email", HttpHeadersAuthenticationTest.GROUP2);
        underTest.authenticate(request, response);
        // User is updated
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "new name", "new email", group2);
        verifyTokenIsUpdated(HttpHeadersAuthenticationTest.NOW);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void update_user_when_user_in_token_but_no_refresh_time() {
        startWithSso();
        UserDto user = insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1);
        setUserInToken(user, null);
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "new name", "new email", HttpHeadersAuthenticationTest.GROUP2);
        underTest.authenticate(request, response);
        // User is updated
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "new name", "new email", group2);
        verifyTokenIsUpdated(HttpHeadersAuthenticationTest.NOW);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void use_refresh_time_from_settings() {
        settings.setProperty("sonar.web.sso.refreshIntervalInMinutes", "10");
        startWithSso();
        UserDto user = insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1);
        // Refresh time was updated 6 minutes ago => less than 10 minutes ago so not updated
        setUserInToken(user, ((HttpHeadersAuthenticationTest.NOW) - ((6 * 60) * 1000L)));
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "new name", "new email", HttpHeadersAuthenticationTest.GROUP2);
        underTest.authenticate(request, response);
        // User is not updated
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, group1);
        verifyTokenIsNotUpdated();
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void update_user_when_login_from_token_is_different_than_login_from_request() {
        startWithSso();
        insertUser(HttpHeadersAuthenticationTest.DEFAULT_USER, group1);
        setUserInToken(HttpHeadersAuthenticationTest.DEFAULT_USER, HttpHeadersAuthenticationTest.CLOSE_REFRESH_TIME);
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest("AnotherLogin", "Another name", "Another email", HttpHeadersAuthenticationTest.GROUP2);
        underTest.authenticate(request, response);
        verifyUserInDb("AnotherLogin", "Another name", "Another email", group2, sonarUsers);
        verifyTokenIsUpdated(HttpHeadersAuthenticationTest.NOW);
        Mockito.verify(authenticationEvent).loginSuccess(request, "AnotherLogin", Source.sso());
    }

    @Test
    public void use_headers_from_settings() {
        settings.setProperty("sonar.web.sso.loginHeader", "head-login");
        settings.setProperty("sonar.web.sso.nameHeader", "head-name");
        settings.setProperty("sonar.web.sso.emailHeader", "head-email");
        settings.setProperty("sonar.web.sso.groupsHeader", "head-groups");
        startWithSso();
        setNotUserInToken();
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(ImmutableMap.of("head-login", HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "head-name", HttpHeadersAuthenticationTest.DEFAULT_NAME, "head-email", HttpHeadersAuthenticationTest.DEFAULT_EMAIL, "head-groups", HttpHeadersAuthenticationTest.GROUPS));
        underTest.authenticate(request, response);
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, group1, group2, sonarUsers);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void detect_group_header_even_with_wrong_case() {
        settings.setProperty("sonar.web.sso.loginHeader", "login");
        settings.setProperty("sonar.web.sso.nameHeader", "name");
        settings.setProperty("sonar.web.sso.emailHeader", "email");
        settings.setProperty("sonar.web.sso.groupsHeader", "Groups");
        startWithSso();
        setNotUserInToken();
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(ImmutableMap.of("login", HttpHeadersAuthenticationTest.DEFAULT_LOGIN, "name", HttpHeadersAuthenticationTest.DEFAULT_NAME, "email", HttpHeadersAuthenticationTest.DEFAULT_EMAIL, "groups", HttpHeadersAuthenticationTest.GROUPS));
        underTest.authenticate(request, response);
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, group1, group2, sonarUsers);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void trim_groups() {
        startWithSso();
        setNotUserInToken();
        HttpServletRequest request = HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, null, null, "  dev ,    admin ");
        underTest.authenticate(request, response);
        verifyUserInDb(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, null, group1, group2, sonarUsers);
        Mockito.verify(authenticationEvent).loginSuccess(request, HttpHeadersAuthenticationTest.DEFAULT_LOGIN, Source.sso());
    }

    @Test
    public void does_not_authenticate_when_no_header() {
        startWithSso();
        setNotUserInToken();
        underTest.authenticate(HttpHeadersAuthenticationTest.createRequest(Collections.emptyMap()), response);
        verifyUserNotAuthenticated();
        verifyTokenIsNotUpdated();
        Mockito.verifyZeroInteractions(authenticationEvent);
    }

    @Test
    public void does_not_authenticate_when_not_enabled() {
        startWithoutSso();
        underTest.authenticate(HttpHeadersAuthenticationTest.createRequest(HttpHeadersAuthenticationTest.DEFAULT_LOGIN, HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, HttpHeadersAuthenticationTest.GROUPS), response);
        verifyUserNotAuthenticated();
        Mockito.verifyZeroInteractions(jwtHttpHandler, authenticationEvent);
    }

    @Test
    public void throw_AuthenticationException_when_BadRequestException_is_generated() {
        startWithSso();
        setNotUserInToken();
        expectedException.expect(AuthenticationExceptionMatcher.authenticationException().from(Source.sso()).withoutLogin().andNoPublicMessage());
        expectedException.expectMessage("Use only letters, numbers, and .-_@ please.");
        try {
            underTest.authenticate(HttpHeadersAuthenticationTest.createRequest("invalid login", HttpHeadersAuthenticationTest.DEFAULT_NAME, HttpHeadersAuthenticationTest.DEFAULT_EMAIL, HttpHeadersAuthenticationTest.GROUPS), response);
        } finally {
            Mockito.verifyZeroInteractions(authenticationEvent);
        }
    }
}

