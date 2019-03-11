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
package org.sonar.server.authentication.event;


import LoggerLevel.INFO;
import Method.BASIC;
import Method.EXTERNAL;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;


public class AuthenticationEventImplTest {
    private static final String LOGIN_129_CHARS = "012345678901234567890123456789012345678901234567890123456789" + "012345678901234567890123456789012345678901234567890123456789012345678";

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AuthenticationEventImpl underTest = new AuthenticationEventImpl();

    @Test
    public void login_success_fails_with_NPE_if_request_is_null() {
        logTester.setLevel(INFO);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("request can't be null");
        underTest.loginSuccess(null, "login", AuthenticationEvent.Source.sso());
    }

    @Test
    public void login_success_fails_with_NPE_if_source_is_null() {
        logTester.setLevel(INFO);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("source can't be null");
        underTest.loginSuccess(Mockito.mock(HttpServletRequest.class), "login", null);
    }

    @Test
    public void login_success_does_not_interact_with_request_if_log_level_is_above_DEBUG() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        logTester.setLevel(INFO);
        underTest.loginSuccess(request, "login", AuthenticationEvent.Source.sso());
        Mockito.verifyZeroInteractions(request);
    }

    @Test
    public void login_success_creates_DEBUG_log_with_empty_login_if_login_argument_is_null() {
        underTest.loginSuccess(AuthenticationEventImplTest.mockRequest(), null, AuthenticationEvent.Source.sso());
        verifyLog("login success [method|SSO][provider|SSO|sso][IP||][login|]");
    }

    @Test
    public void login_success_creates_DEBUG_log_with_method_provider_and_login() {
        underTest.loginSuccess(AuthenticationEventImplTest.mockRequest(), "foo", AuthenticationEvent.Source.realm(BASIC, "some provider name"));
        verifyLog("login success [method|BASIC][provider|REALM|some provider name][IP||][login|foo]");
    }

    @Test
    public void login_success_prevents_log_flooding_on_login_starting_from_128_chars() {
        underTest.loginSuccess(AuthenticationEventImplTest.mockRequest(), AuthenticationEventImplTest.LOGIN_129_CHARS, AuthenticationEvent.Source.realm(BASIC, "some provider name"));
        verifyLog(("login success [method|BASIC][provider|REALM|some provider name][IP||][login|012345678901234567890123456789012345678901234567890123456789" + "01234567890123456789012345678901234567890123456789012345678901234567...(129)]"));
    }

    @Test
    public void login_success_logs_remote_ip_from_request() {
        underTest.loginSuccess(AuthenticationEventImplTest.mockRequest("1.2.3.4"), "foo", AuthenticationEvent.Source.realm(EXTERNAL, "bar"));
        verifyLog("login success [method|EXTERNAL][provider|REALM|bar][IP|1.2.3.4|][login|foo]");
    }

    @Test
    public void login_success_logs_X_Forwarded_For_header_from_request() {
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5"));
        underTest.loginSuccess(request, "foo", AuthenticationEvent.Source.realm(EXTERNAL, "bar"));
        verifyLog("login success [method|EXTERNAL][provider|REALM|bar][IP|1.2.3.4|2.3.4.5][login|foo]");
    }

    @Test
    public void login_success_logs_X_Forwarded_For_header_from_request_and_supports_multiple_headers() {
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5", "6.5.4.3"), Arrays.asList("9.5.6.7"), Arrays.asList("6.3.2.4"));
        underTest.loginSuccess(request, "foo", AuthenticationEvent.Source.realm(EXTERNAL, "bar"));
        verifyLog("login success [method|EXTERNAL][provider|REALM|bar][IP|1.2.3.4|2.3.4.5,6.5.4.3,9.5.6.7,6.3.2.4][login|foo]");
    }

    @Test
    public void login_failure_fails_with_NPE_if_request_is_null() {
        logTester.setLevel(INFO);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("request can't be null");
        underTest.loginFailure(null, AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.sso()).build());
    }

    @Test
    public void login_failure_fails_with_NPE_if_AuthenticationException_is_null() {
        logTester.setLevel(INFO);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("AuthenticationException can't be null");
        underTest.loginFailure(Mockito.mock(HttpServletRequest.class), null);
    }

    @Test
    public void login_failure_does_not_interact_with_arguments_if_log_level_is_above_DEBUG() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        AuthenticationException exception = Mockito.mock(AuthenticationException.class);
        logTester.setLevel(INFO);
        underTest.loginFailure(request, exception);
        Mockito.verifyZeroInteractions(request, exception);
    }

    @Test
    public void login_failure_creates_DEBUG_log_with_empty_login_if_AuthenticationException_has_no_login() {
        AuthenticationException exception = AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.sso()).setMessage("message").build();
        underTest.loginFailure(AuthenticationEventImplTest.mockRequest(), exception);
        verifyLog("login failure [cause|message][method|SSO][provider|SSO|sso][IP||][login|]");
    }

    @Test
    public void login_failure_creates_DEBUG_log_with_empty_cause_if_AuthenticationException_has_no_message() {
        AuthenticationException exception = AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.sso()).setLogin("FoO").build();
        underTest.loginFailure(AuthenticationEventImplTest.mockRequest(), exception);
        verifyLog("login failure [cause|][method|SSO][provider|SSO|sso][IP||][login|FoO]");
    }

    @Test
    public void login_failure_creates_DEBUG_log_with_method_provider_and_login() {
        AuthenticationException exception = AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.realm(BASIC, "some provider name")).setMessage("something got terribly wrong").setLogin("BaR").build();
        underTest.loginFailure(AuthenticationEventImplTest.mockRequest(), exception);
        verifyLog("login failure [cause|something got terribly wrong][method|BASIC][provider|REALM|some provider name][IP||][login|BaR]");
    }

    @Test
    public void login_failure_prevents_log_flooding_on_login_starting_from_128_chars() {
        AuthenticationException exception = AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.realm(BASIC, "some provider name")).setMessage("pop").setLogin(AuthenticationEventImplTest.LOGIN_129_CHARS).build();
        underTest.loginFailure(AuthenticationEventImplTest.mockRequest(), exception);
        verifyLog(("login failure [cause|pop][method|BASIC][provider|REALM|some provider name][IP||][login|012345678901234567890123456789012345678901234567890123456789" + "01234567890123456789012345678901234567890123456789012345678901234567...(129)]"));
    }

    @Test
    public void login_failure_logs_remote_ip_from_request() {
        AuthenticationException exception = AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.realm(EXTERNAL, "bar")).setMessage("Damn it!").setLogin("Baaad").build();
        underTest.loginFailure(AuthenticationEventImplTest.mockRequest("1.2.3.4"), exception);
        verifyLog("login failure [cause|Damn it!][method|EXTERNAL][provider|REALM|bar][IP|1.2.3.4|][login|Baaad]");
    }

    @Test
    public void login_failure_logs_X_Forwarded_For_header_from_request() {
        AuthenticationException exception = AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.realm(EXTERNAL, "bar")).setMessage("Hop la!").setLogin("foo").build();
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5"));
        underTest.loginFailure(request, exception);
        verifyLog("login failure [cause|Hop la!][method|EXTERNAL][provider|REALM|bar][IP|1.2.3.4|2.3.4.5][login|foo]");
    }

    @Test
    public void login_failure_logs_X_Forwarded_For_header_from_request_and_supports_multiple_headers() {
        AuthenticationException exception = AuthenticationException.newBuilder().setSource(AuthenticationEvent.Source.realm(EXTERNAL, "bar")).setMessage("Boom!").setLogin("foo").build();
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5", "6.5.4.3"), Arrays.asList("9.5.6.7"), Arrays.asList("6.3.2.4"));
        underTest.loginFailure(request, exception);
        verifyLog("login failure [cause|Boom!][method|EXTERNAL][provider|REALM|bar][IP|1.2.3.4|2.3.4.5,6.5.4.3,9.5.6.7,6.3.2.4][login|foo]");
    }

    @Test
    public void logout_success_fails_with_NPE_if_request_is_null() {
        logTester.setLevel(INFO);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("request can't be null");
        underTest.logoutSuccess(null, "foo");
    }

    @Test
    public void logout_success_does_not_interact_with_request_if_log_level_is_above_DEBUG() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        logTester.setLevel(INFO);
        underTest.logoutSuccess(request, "foo");
        Mockito.verifyZeroInteractions(request);
    }

    @Test
    public void logout_success_creates_DEBUG_log_with_empty_login_if_login_argument_is_null() {
        underTest.logoutSuccess(AuthenticationEventImplTest.mockRequest(), null);
        verifyLog("logout success [IP||][login|]");
    }

    @Test
    public void logout_success_creates_DEBUG_log_with_login() {
        underTest.logoutSuccess(AuthenticationEventImplTest.mockRequest(), "foo");
        verifyLog("logout success [IP||][login|foo]");
    }

    @Test
    public void logout_success_logs_remote_ip_from_request() {
        underTest.logoutSuccess(AuthenticationEventImplTest.mockRequest("1.2.3.4"), "foo");
        verifyLog("logout success [IP|1.2.3.4|][login|foo]");
    }

    @Test
    public void logout_success_logs_X_Forwarded_For_header_from_request() {
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5"));
        underTest.logoutSuccess(request, "foo");
        verifyLog("logout success [IP|1.2.3.4|2.3.4.5][login|foo]");
    }

    @Test
    public void logout_success_logs_X_Forwarded_For_header_from_request_and_supports_multiple_headers() {
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5", "6.5.4.3"), Arrays.asList("9.5.6.7"), Arrays.asList("6.3.2.4"));
        underTest.logoutSuccess(request, "foo");
        verifyLog("logout success [IP|1.2.3.4|2.3.4.5,6.5.4.3,9.5.6.7,6.3.2.4][login|foo]");
    }

    @Test
    public void logout_failure_with_NPE_if_request_is_null() {
        logTester.setLevel(INFO);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("request can't be null");
        underTest.logoutFailure(null, "bad csrf");
    }

    @Test
    public void login_fails_with_NPE_if_error_message_is_null() {
        logTester.setLevel(INFO);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("error message can't be null");
        underTest.logoutFailure(Mockito.mock(HttpServletRequest.class), null);
    }

    @Test
    public void logout_does_not_interact_with_request_if_log_level_is_above_DEBUG() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        logTester.setLevel(INFO);
        underTest.logoutFailure(request, "bad csrf");
        Mockito.verifyZeroInteractions(request);
    }

    @Test
    public void logout_creates_DEBUG_log_with_error() {
        underTest.logoutFailure(AuthenticationEventImplTest.mockRequest(), "bad token");
        verifyLog("logout failure [error|bad token][IP||]");
    }

    @Test
    public void logout_logs_remote_ip_from_request() {
        underTest.logoutFailure(AuthenticationEventImplTest.mockRequest("1.2.3.4"), "bad token");
        verifyLog("logout failure [error|bad token][IP|1.2.3.4|]");
    }

    @Test
    public void logout_logs_X_Forwarded_For_header_from_request() {
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5"));
        underTest.logoutFailure(request, "bad token");
        verifyLog("logout failure [error|bad token][IP|1.2.3.4|2.3.4.5]");
    }

    @Test
    public void logout_logs_X_Forwarded_For_header_from_request_and_supports_multiple_headers() {
        HttpServletRequest request = AuthenticationEventImplTest.mockRequest("1.2.3.4", Arrays.asList("2.3.4.5", "6.5.4.3"), Arrays.asList("9.5.6.7"), Arrays.asList("6.3.2.4"));
        underTest.logoutFailure(request, "bad token");
        verifyLog("logout failure [error|bad token][IP|1.2.3.4|2.3.4.5,6.5.4.3,9.5.6.7,6.3.2.4]");
    }
}

