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
package org.sonar.server.authentication.ws;


import MediaTypes.JSON;
import java.io.StringWriter;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.db.user.UserTesting;
import org.sonar.server.authentication.BasicAuthentication;
import org.sonar.server.authentication.JwtHttpHandler;
import org.sonar.server.authentication.event.AuthenticationException;
import org.sonar.test.JsonAssert;


public class ValidateActionTest {
    StringWriter stringWriter = new StringWriter();

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    FilterChain chain = Mockito.mock(FilterChain.class);

    BasicAuthentication basicAuthentication = Mockito.mock(BasicAuthentication.class);

    JwtHttpHandler jwtHttpHandler = Mockito.mock(JwtHttpHandler.class);

    MapSettings settings = new MapSettings();

    ValidateAction underTest = new ValidateAction(settings.asConfig(), basicAuthentication, jwtHttpHandler);

    @Test
    public void return_true_when_jwt_token_is_set() throws Exception {
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.of(UserTesting.newUserDto()));
        Mockito.when(basicAuthentication.authenticate(request)).thenReturn(Optional.empty());
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setContentType(JSON);
        JsonAssert.assertJson(stringWriter.toString()).isSimilarTo("{\"valid\":true}");
    }

    @Test
    public void return_true_when_basic_auth() throws Exception {
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.empty());
        Mockito.when(basicAuthentication.authenticate(request)).thenReturn(Optional.of(UserTesting.newUserDto()));
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setContentType(JSON);
        JsonAssert.assertJson(stringWriter.toString()).isSimilarTo("{\"valid\":true}");
    }

    @Test
    public void return_true_when_no_jwt_nor_basic_auth_and_no_force_authentication() throws Exception {
        settings.setProperty("sonar.forceAuthentication", "false");
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.empty());
        Mockito.when(basicAuthentication.authenticate(request)).thenReturn(Optional.empty());
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setContentType(JSON);
        JsonAssert.assertJson(stringWriter.toString()).isSimilarTo("{\"valid\":true}");
    }

    @Test
    public void return_false_when_no_jwt_nor_basic_auth_and_force_authentication_is_true() throws Exception {
        settings.setProperty("sonar.forceAuthentication", "true");
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.empty());
        Mockito.when(basicAuthentication.authenticate(request)).thenReturn(Optional.empty());
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setContentType(JSON);
        JsonAssert.assertJson(stringWriter.toString()).isSimilarTo("{\"valid\":false}");
    }

    @Test
    public void return_false_when_jwt_throws_unauthorized_exception() throws Exception {
        Mockito.doThrow(AuthenticationException.class).when(jwtHttpHandler).validateToken(request, response);
        Mockito.when(basicAuthentication.authenticate(request)).thenReturn(Optional.empty());
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setContentType(JSON);
        JsonAssert.assertJson(stringWriter.toString()).isSimilarTo("{\"valid\":false}");
    }

    @Test
    public void return_false_when_basic_authenticator_throws_unauthorized_exception() throws Exception {
        Mockito.when(jwtHttpHandler.validateToken(request, response)).thenReturn(Optional.empty());
        Mockito.doThrow(AuthenticationException.class).when(basicAuthentication).authenticate(request);
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setContentType(JSON);
        JsonAssert.assertJson(stringWriter.toString()).isSimilarTo("{\"valid\":false}");
    }
}

