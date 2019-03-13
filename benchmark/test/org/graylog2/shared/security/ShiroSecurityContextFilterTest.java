/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.shared.security;


import HttpHeaders.AUTHORIZATION;
import SecurityContext.BASIC_AUTH;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.SecurityContext;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ShiroSecurityContextFilterTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private SecurityContext securityContext;

    private ShiroSecurityContextFilter filter;

    @Test
    public void filterWithoutAuthorizationHeaderShouldDoNothing() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
        final ArgumentCaptor<SecurityContext> argument = ArgumentCaptor.forClass(SecurityContext.class);
        Mockito.verify(requestContext).setSecurityContext(argument.capture());
        assertThat(argument.getValue()).isExactlyInstanceOf(ShiroSecurityContext.class);
        assertThat(argument.getValue().getAuthenticationScheme()).isNull();
    }

    @Test
    public void filterWithNonBasicAuthorizationHeaderShouldDoNothing() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        headers.putSingle(AUTHORIZATION, "Foobar");
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
        final ArgumentCaptor<SecurityContext> argument = ArgumentCaptor.forClass(SecurityContext.class);
        Mockito.verify(requestContext).setSecurityContext(argument.capture());
        assertThat(argument.getValue()).isExactlyInstanceOf(ShiroSecurityContext.class);
        assertThat(argument.getValue().getAuthenticationScheme()).isNull();
    }

    @Test(expected = BadRequestException.class)
    public void filterWithMalformedBasicAuthShouldThrowBadRequestException() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        headers.putSingle(AUTHORIZATION, "Basic ****");
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
    }

    @Test(expected = BadRequestException.class)
    public void filterWithBasicAuthAndMalformedCredentialsShouldThrowBadRequestException() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        final String credentials = Base64.getEncoder().encodeToString("user_pass".getBytes(StandardCharsets.US_ASCII));
        headers.putSingle(AUTHORIZATION, ("Basic " + credentials));
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
    }

    @Test
    public void filterWithBasicAuthAndCredentialsShouldCreateShiroSecurityContextWithUsernamePasswordToken() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        final String credentials = Base64.getEncoder().encodeToString("user:pass".getBytes(StandardCharsets.US_ASCII));
        headers.putSingle(AUTHORIZATION, ("Basic " + credentials));
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
        final ArgumentCaptor<ShiroSecurityContext> argument = ArgumentCaptor.forClass(ShiroSecurityContext.class);
        Mockito.verify(requestContext).setSecurityContext(argument.capture());
        final ShiroSecurityContext securityContext = argument.getValue();
        assertThat(securityContext).isExactlyInstanceOf(ShiroSecurityContext.class);
        assertThat(securityContext.getAuthenticationScheme()).isEqualTo(BASIC_AUTH);
        assertThat(securityContext.getUsername()).isEqualTo("user");
        assertThat(securityContext.getToken()).isExactlyInstanceOf(UsernamePasswordToken.class);
    }

    @Test
    public void filterWithBasicAuthAndSessionIdShouldCreateShiroSecurityContextWithSessionIdToken() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        final String credentials = Base64.getEncoder().encodeToString("test:session".getBytes(StandardCharsets.US_ASCII));
        headers.putSingle(AUTHORIZATION, ("Basic " + credentials));
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
        final ArgumentCaptor<ShiroSecurityContext> argument = ArgumentCaptor.forClass(ShiroSecurityContext.class);
        Mockito.verify(requestContext).setSecurityContext(argument.capture());
        final ShiroSecurityContext securityContext = argument.getValue();
        assertThat(securityContext).isExactlyInstanceOf(ShiroSecurityContext.class);
        assertThat(securityContext.getAuthenticationScheme()).isEqualTo(BASIC_AUTH);
        assertThat(securityContext.getToken()).isExactlyInstanceOf(SessionIdToken.class);
    }

    @Test
    public void filterWithBasicAuthAndTokenShouldCreateShiroSecurityContextWithAccessTokenAuthToken() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        final String credentials = Base64.getEncoder().encodeToString("test:token".getBytes(StandardCharsets.US_ASCII));
        headers.putSingle(AUTHORIZATION, ("Basic " + credentials));
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
        final ArgumentCaptor<ShiroSecurityContext> argument = ArgumentCaptor.forClass(ShiroSecurityContext.class);
        Mockito.verify(requestContext).setSecurityContext(argument.capture());
        final ShiroSecurityContext securityContext = argument.getValue();
        assertThat(securityContext).isExactlyInstanceOf(ShiroSecurityContext.class);
        assertThat(securityContext.getAuthenticationScheme()).isEqualTo(BASIC_AUTH);
        assertThat(securityContext.getToken()).isExactlyInstanceOf(AccessTokenAuthToken.class);
    }

    @Test
    public void filterWithBasicAuthAndPasswordWithColonShouldCreateShiroSecurityContextWithUsernamePasswordToken() throws Exception {
        final MultivaluedHashMap<String, String> headers = new MultivaluedHashMap();
        final String credentials = Base64.getEncoder().encodeToString("user:pass:word".getBytes(StandardCharsets.US_ASCII));
        headers.putSingle(AUTHORIZATION, ("Basic " + credentials));
        Mockito.when(requestContext.getHeaders()).thenReturn(headers);
        filter.filter(requestContext);
        final ArgumentCaptor<ShiroSecurityContext> argument = ArgumentCaptor.forClass(ShiroSecurityContext.class);
        Mockito.verify(requestContext).setSecurityContext(argument.capture());
        final ShiroSecurityContext securityContext = argument.getValue();
        assertThat(securityContext).isExactlyInstanceOf(ShiroSecurityContext.class);
        assertThat(securityContext.getAuthenticationScheme()).isEqualTo(BASIC_AUTH);
        assertThat(securityContext.getUsername()).isEqualTo("user");
        assertThat(securityContext.getPassword()).isEqualTo("pass:word");
        assertThat(securityContext.getToken()).isExactlyInstanceOf(UsernamePasswordToken.class);
    }
}

