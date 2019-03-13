/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.autoconfigure.cloudfoundry.servlet;


import AccessLevel.FULL;
import AccessLevel.RESTRICTED;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import HttpStatus.OK;
import Reason.ACCESS_DENIED;
import Reason.MISSING_AUTHORIZATION;
import Reason.SERVICE_UNAVAILABLE;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.SecurityResponse;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.Token;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Tests for {@link CloudFoundrySecurityInterceptor}.
 *
 * @author Madhura Bhave
 */
public class CloudFoundrySecurityInterceptorTests {
    @Mock
    private TokenValidator tokenValidator;

    @Mock
    private CloudFoundrySecurityService securityService;

    private CloudFoundrySecurityInterceptor interceptor;

    private MockHttpServletRequest request;

    @Test
    public void preHandleWhenRequestIsPreFlightShouldReturnTrue() {
        this.request.setMethod("OPTIONS");
        this.request.addHeader(ORIGIN, "http://example.com");
        this.request.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("test"));
        assertThat(response.getStatus()).isEqualTo(OK);
    }

    @Test
    public void preHandleWhenTokenIsMissingShouldReturnFalse() {
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("test"));
        assertThat(response.getStatus()).isEqualTo(MISSING_AUTHORIZATION.getStatus());
    }

    @Test
    public void preHandleWhenTokenIsNotBearerShouldReturnFalse() {
        this.request.addHeader("Authorization", mockAccessToken());
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("test"));
        assertThat(response.getStatus()).isEqualTo(MISSING_AUTHORIZATION.getStatus());
    }

    @Test
    public void preHandleWhenApplicationIdIsNullShouldReturnFalse() {
        this.interceptor = new CloudFoundrySecurityInterceptor(this.tokenValidator, this.securityService, null);
        this.request.addHeader("Authorization", ("bearer " + (mockAccessToken())));
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("test"));
        assertThat(response.getStatus()).isEqualTo(SERVICE_UNAVAILABLE.getStatus());
    }

    @Test
    public void preHandleWhenCloudFoundrySecurityServiceIsNullShouldReturnFalse() {
        this.interceptor = new CloudFoundrySecurityInterceptor(this.tokenValidator, null, "my-app-id");
        this.request.addHeader("Authorization", ("bearer " + (mockAccessToken())));
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("test"));
        assertThat(response.getStatus()).isEqualTo(SERVICE_UNAVAILABLE.getStatus());
    }

    @Test
    public void preHandleWhenAccessIsNotAllowedShouldReturnFalse() {
        String accessToken = mockAccessToken();
        this.request.addHeader("Authorization", ("bearer " + accessToken));
        BDDMockito.given(this.securityService.getAccessLevel(accessToken, "my-app-id")).willReturn(RESTRICTED);
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("test"));
        assertThat(response.getStatus()).isEqualTo(ACCESS_DENIED.getStatus());
    }

    @Test
    public void preHandleSuccessfulWithFullAccess() {
        String accessToken = mockAccessToken();
        this.request.addHeader("Authorization", ("Bearer " + accessToken));
        BDDMockito.given(this.securityService.getAccessLevel(accessToken, "my-app-id")).willReturn(FULL);
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("test"));
        ArgumentCaptor<Token> tokenArgumentCaptor = ArgumentCaptor.forClass(Token.class);
        Mockito.verify(this.tokenValidator).validate(tokenArgumentCaptor.capture());
        Token token = tokenArgumentCaptor.getValue();
        assertThat(token.toString()).isEqualTo(accessToken);
        assertThat(response.getStatus()).isEqualTo(OK);
        assertThat(this.request.getAttribute("cloudFoundryAccessLevel")).isEqualTo(FULL);
    }

    @Test
    public void preHandleSuccessfulWithRestrictedAccess() {
        String accessToken = mockAccessToken();
        this.request.addHeader("Authorization", ("Bearer " + accessToken));
        BDDMockito.given(this.securityService.getAccessLevel(accessToken, "my-app-id")).willReturn(RESTRICTED);
        SecurityResponse response = this.interceptor.preHandle(this.request, EndpointId.of("info"));
        ArgumentCaptor<Token> tokenArgumentCaptor = ArgumentCaptor.forClass(Token.class);
        Mockito.verify(this.tokenValidator).validate(tokenArgumentCaptor.capture());
        Token token = tokenArgumentCaptor.getValue();
        assertThat(token.toString()).isEqualTo(accessToken);
        assertThat(response.getStatus()).isEqualTo(OK);
        assertThat(this.request.getAttribute("cloudFoundryAccessLevel")).isEqualTo(RESTRICTED);
    }
}

