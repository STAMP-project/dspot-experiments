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


import JwtSerializer.JwtSession;
import System2.INSTANCE;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.user.UserDto;


public class JwtHttpHandlerTest {
    private static final String JWT_TOKEN = "TOKEN";

    private static final String CSRF_STATE = "CSRF_STATE";

    private static final long NOW = 10000000000L;

    private static final long FOUR_MINUTES_AGO = (JwtHttpHandlerTest.NOW) - ((4 * 60) * 1000L);

    private static final long SIX_MINUTES_AGO = (JwtHttpHandlerTest.NOW) - ((6 * 60) * 1000L);

    private static final long TEN_DAYS_AGO = (JwtHttpHandlerTest.NOW) - ((((10 * 24) * 60) * 60) * 1000L);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private ArgumentCaptor<Cookie> cookieArgumentCaptor = ArgumentCaptor.forClass(Cookie.class);

    private ArgumentCaptor<JwtSerializer.JwtSession> jwtArgumentCaptor = ArgumentCaptor.forClass(JwtSession.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private HttpSession httpSession = Mockito.mock(HttpSession.class);

    private System2 system2 = Mockito.spy(INSTANCE);

    private MapSettings settings = new MapSettings();

    private JwtSerializer jwtSerializer = Mockito.mock(JwtSerializer.class);

    private JwtCsrfVerifier jwtCsrfVerifier = Mockito.mock(JwtCsrfVerifier.class);

    private JwtHttpHandler underTest = new JwtHttpHandler(system2, dbClient, settings.asConfig(), jwtSerializer, jwtCsrfVerifier);

    @Test
    public void create_token() {
        UserDto user = db.users().insertUser();
        underTest.generateToken(user, request, response);
        Optional<Cookie> jwtCookie = findCookie("JWT-SESSION");
        assertThat(jwtCookie).isPresent();
        verifyCookie(jwtCookie.get(), JwtHttpHandlerTest.JWT_TOKEN, (((3 * 24) * 60) * 60));
        Mockito.verify(jwtSerializer).encode(jwtArgumentCaptor.capture());
        verifyToken(jwtArgumentCaptor.getValue(), user, (((3 * 24) * 60) * 60), JwtHttpHandlerTest.NOW);
    }

    @Test
    public void generate_csrf_state_when_creating_token() {
        UserDto user = db.users().insertUser();
        underTest.generateToken(user, request, response);
        Mockito.verify(jwtCsrfVerifier).generateState(request, response, (((3 * 24) * 60) * 60));
        Mockito.verify(jwtSerializer).encode(jwtArgumentCaptor.capture());
        JwtSerializer.JwtSession token = jwtArgumentCaptor.getValue();
        assertThat(token.getProperties().get("xsrfToken")).isEqualTo(JwtHttpHandlerTest.CSRF_STATE);
    }

    @Test
    public void generate_token_is_using_session_timeout_from_settings() {
        UserDto user = db.users().insertUser();
        int sessionTimeoutInMinutes = 10;
        settings.setProperty("sonar.web.sessionTimeoutInMinutes", sessionTimeoutInMinutes);
        underTest = new JwtHttpHandler(system2, dbClient, settings.asConfig(), jwtSerializer, jwtCsrfVerifier);
        underTest.generateToken(user, request, response);
        Mockito.verify(jwtSerializer).encode(jwtArgumentCaptor.capture());
        verifyToken(jwtArgumentCaptor.getValue(), user, (sessionTimeoutInMinutes * 60), JwtHttpHandlerTest.NOW);
    }

    @Test
    public void session_timeout_property_cannot_be_updated() {
        UserDto user = db.users().insertUser();
        int firstSessionTimeoutInMinutes = 10;
        settings.setProperty("sonar.web.sessionTimeoutInMinutes", firstSessionTimeoutInMinutes);
        underTest = new JwtHttpHandler(system2, dbClient, settings.asConfig(), jwtSerializer, jwtCsrfVerifier);
        underTest.generateToken(user, request, response);
        // The property is updated, but it won't be taking into account
        settings.setProperty("sonar.web.sessionTimeoutInMinutes", 15);
        underTest.generateToken(user, request, response);
        Mockito.verify(jwtSerializer, Mockito.times(2)).encode(jwtArgumentCaptor.capture());
        verifyToken(jwtArgumentCaptor.getAllValues().get(0), user, (firstSessionTimeoutInMinutes * 60), JwtHttpHandlerTest.NOW);
        verifyToken(jwtArgumentCaptor.getAllValues().get(1), user, (firstSessionTimeoutInMinutes * 60), JwtHttpHandlerTest.NOW);
    }

    @Test
    public void session_timeout_property_cannot_be_zero() {
        settings.setProperty("sonar.web.sessionTimeoutInMinutes", 0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Property sonar.web.sessionTimeoutInMinutes must be strictly positive. Got 0");
        new JwtHttpHandler(system2, dbClient, settings.asConfig(), jwtSerializer, jwtCsrfVerifier);
    }

    @Test
    public void session_timeout_property_cannot_be_negative() {
        settings.setProperty("sonar.web.sessionTimeoutInMinutes", (-10));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Property sonar.web.sessionTimeoutInMinutes must be strictly positive. Got -10");
        new JwtHttpHandler(system2, dbClient, settings.asConfig(), jwtSerializer, jwtCsrfVerifier);
    }

    @Test
    public void session_timeout_property_cannot_be_greater_than_three_months() {
        settings.setProperty("sonar.web.sessionTimeoutInMinutes", (((4 * 30) * 24) * 60));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Property sonar.web.sessionTimeoutInMinutes must not be greater than 3 months (129600 minutes). Got 172800 minutes");
        new JwtHttpHandler(system2, dbClient, settings.asConfig(), jwtSerializer, jwtCsrfVerifier);
    }

    @Test
    public void validate_token() {
        UserDto user = db.users().insertUser();
        addJwtCookie();
        Claims claims = createToken(user.getUuid(), JwtHttpHandlerTest.NOW);
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.of(claims));
        assertThat(underTest.validateToken(request, response).isPresent()).isTrue();
        Mockito.verify(jwtSerializer, Mockito.never()).encode(ArgumentMatchers.any(JwtSession.class));
    }

    @Test
    public void validate_token_refresh_session_when_refresh_time_is_reached() {
        UserDto user = db.users().insertUser();
        addJwtCookie();
        // Token was created 10 days ago and refreshed 6 minutes ago
        Claims claims = createToken(user.getUuid(), JwtHttpHandlerTest.TEN_DAYS_AGO);
        claims.put("lastRefreshTime", JwtHttpHandlerTest.SIX_MINUTES_AGO);
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.of(claims));
        assertThat(underTest.validateToken(request, response).isPresent()).isTrue();
        Mockito.verify(jwtSerializer).refresh(ArgumentMatchers.any(Claims.class), ArgumentMatchers.eq((((3 * 24) * 60) * 60)));
    }

    @Test
    public void validate_token_does_not_refresh_session_when_refresh_time_is_not_reached() {
        UserDto user = db.users().insertUser();
        addJwtCookie();
        // Token was created 10 days ago and refreshed 4 minutes ago
        Claims claims = createToken(user.getUuid(), JwtHttpHandlerTest.TEN_DAYS_AGO);
        claims.put("lastRefreshTime", JwtHttpHandlerTest.FOUR_MINUTES_AGO);
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.of(claims));
        assertThat(underTest.validateToken(request, response).isPresent()).isTrue();
        Mockito.verify(jwtSerializer, Mockito.never()).refresh(ArgumentMatchers.any(Claims.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void validate_token_does_not_refresh_session_when_disconnected_timeout_is_reached() {
        UserDto user = db.users().insertUser();
        addJwtCookie();
        // Token was created 4 months ago, refreshed 4 minutes ago, and it expired in 5 minutes
        Claims claims = createToken(user.getUuid(), ((JwtHttpHandlerTest.NOW) - (((((4L * 30) * 24) * 60) * 60) * 1000)));
        claims.setExpiration(new Date(((JwtHttpHandlerTest.NOW) + ((5 * 60) * 1000))));
        claims.put("lastRefreshTime", JwtHttpHandlerTest.FOUR_MINUTES_AGO);
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.of(claims));
        assertThat(underTest.validateToken(request, response).isPresent()).isFalse();
    }

    @Test
    public void validate_token_does_not_refresh_session_when_user_is_disabled() {
        addJwtCookie();
        UserDto user = addUser(false);
        Claims claims = createToken(user.getLogin(), JwtHttpHandlerTest.NOW);
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.of(claims));
        assertThat(underTest.validateToken(request, response).isPresent()).isFalse();
    }

    @Test
    public void validate_token_does_not_refresh_session_when_token_is_no_more_valid() {
        addJwtCookie();
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.empty());
        assertThat(underTest.validateToken(request, response).isPresent()).isFalse();
    }

    @Test
    public void validate_token_does_nothing_when_no_jwt_cookie() {
        underTest.validateToken(request, response);
        Mockito.verifyZeroInteractions(httpSession, jwtSerializer);
        assertThat(underTest.validateToken(request, response).isPresent()).isFalse();
    }

    @Test
    public void validate_token_does_nothing_when_empty_value_in_jwt_cookie() {
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("JWT-SESSION", "") });
        underTest.validateToken(request, response);
        Mockito.verifyZeroInteractions(httpSession, jwtSerializer);
        assertThat(underTest.validateToken(request, response).isPresent()).isFalse();
    }

    @Test
    public void validate_token_verify_csrf_state() {
        UserDto user = db.users().insertUser();
        addJwtCookie();
        Claims claims = createToken(user.getUuid(), JwtHttpHandlerTest.NOW);
        claims.put("xsrfToken", JwtHttpHandlerTest.CSRF_STATE);
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.of(claims));
        underTest.validateToken(request, response);
        Mockito.verify(jwtCsrfVerifier).verifyState(request, JwtHttpHandlerTest.CSRF_STATE, user.getUuid());
    }

    @Test
    public void validate_token_refresh_state_when_refreshing_token() {
        UserDto user = db.users().insertUser();
        addJwtCookie();
        // Token was created 10 days ago and refreshed 6 minutes ago
        Claims claims = createToken(user.getUuid(), JwtHttpHandlerTest.TEN_DAYS_AGO);
        claims.put("xsrfToken", "CSRF_STATE");
        Mockito.when(jwtSerializer.decode(JwtHttpHandlerTest.JWT_TOKEN)).thenReturn(Optional.of(claims));
        underTest.validateToken(request, response);
        Mockito.verify(jwtSerializer).refresh(ArgumentMatchers.any(Claims.class), ArgumentMatchers.anyInt());
        Mockito.verify(jwtCsrfVerifier).refreshState(request, response, "CSRF_STATE", (((3 * 24) * 60) * 60));
    }

    @Test
    public void remove_token() {
        underTest.removeToken(request, response);
        verifyCookie(findCookie("JWT-SESSION").get(), null, 0);
        Mockito.verify(jwtCsrfVerifier).removeState(request, response);
    }
}

