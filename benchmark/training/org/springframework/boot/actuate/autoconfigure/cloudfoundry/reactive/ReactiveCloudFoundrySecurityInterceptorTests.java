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
package org.springframework.boot.actuate.autoconfigure.cloudfoundry.reactive;


import AccessLevel.FULL;
import AccessLevel.RESTRICTED;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.ORIGIN;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.AccessLevel;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.CloudFoundryAuthorizationException;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Tests for {@link CloudFoundrySecurityInterceptor}.
 *
 * @author Madhura Bhave
 */
public class ReactiveCloudFoundrySecurityInterceptorTests {
    @Mock
    private ReactiveTokenValidator tokenValidator;

    @Mock
    private ReactiveCloudFoundrySecurityService securityService;

    private CloudFoundrySecurityInterceptor interceptor;

    @Test
    public void preHandleWhenRequestIsPreFlightShouldBeOk() {
        MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest.options("/a").header(ORIGIN, "http://example.com").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").build());
        StepVerifier.create(this.interceptor.preHandle(request, "/a")).consumeNextWith(( response) -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK)).verifyComplete();
    }

    @Test
    public void preHandleWhenTokenIsMissingShouldReturnMissingAuthorization() {
        MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest.get("/a").build());
        StepVerifier.create(this.interceptor.preHandle(request, "/a")).consumeNextWith(( response) -> assertThat(response.getStatus()).isEqualTo(Reason.MISSING_AUTHORIZATION.getStatus())).verifyComplete();
    }

    @Test
    public void preHandleWhenTokenIsNotBearerShouldReturnMissingAuthorization() {
        MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest.get("/a").header(AUTHORIZATION, mockAccessToken()).build());
        StepVerifier.create(this.interceptor.preHandle(request, "/a")).consumeNextWith(( response) -> assertThat(response.getStatus()).isEqualTo(Reason.MISSING_AUTHORIZATION.getStatus())).verifyComplete();
    }

    @Test
    public void preHandleWhenApplicationIdIsNullShouldReturnError() {
        this.interceptor = new CloudFoundrySecurityInterceptor(this.tokenValidator, this.securityService, null);
        MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest.get("/a").header(AUTHORIZATION, ("bearer " + (mockAccessToken()))).build());
        StepVerifier.create(this.interceptor.preHandle(request, "/a")).consumeErrorWith(( ex) -> assertThat(((CloudFoundryAuthorizationException) (ex)).getReason()).isEqualTo(Reason.SERVICE_UNAVAILABLE)).verify();
    }

    @Test
    public void preHandleWhenCloudFoundrySecurityServiceIsNullShouldReturnError() {
        this.interceptor = new CloudFoundrySecurityInterceptor(this.tokenValidator, null, "my-app-id");
        MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest.get("/a").header(AUTHORIZATION, mockAccessToken()).build());
        StepVerifier.create(this.interceptor.preHandle(request, "/a")).consumeErrorWith(( ex) -> assertThat(((CloudFoundryAuthorizationException) (ex)).getReason()).isEqualTo(Reason.SERVICE_UNAVAILABLE)).verify();
    }

    @Test
    public void preHandleWhenAccessIsNotAllowedShouldReturnAccessDenied() {
        BDDMockito.given(this.securityService.getAccessLevel(mockAccessToken(), "my-app-id")).willReturn(Mono.just(RESTRICTED));
        BDDMockito.given(this.tokenValidator.validate(ArgumentMatchers.any())).willReturn(Mono.empty());
        MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest.get("/a").header(AUTHORIZATION, ("bearer " + (mockAccessToken()))).build());
        StepVerifier.create(this.interceptor.preHandle(request, "/a")).consumeNextWith(( response) -> assertThat(response.getStatus()).isEqualTo(Reason.ACCESS_DENIED.getStatus())).verifyComplete();
    }

    @Test
    public void preHandleSuccessfulWithFullAccess() {
        String accessToken = mockAccessToken();
        BDDMockito.given(this.securityService.getAccessLevel(accessToken, "my-app-id")).willReturn(Mono.just(FULL));
        BDDMockito.given(this.tokenValidator.validate(ArgumentMatchers.any())).willReturn(Mono.empty());
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/a").header(AUTHORIZATION, ("bearer " + (mockAccessToken()))).build());
        StepVerifier.create(this.interceptor.preHandle(exchange, "/a")).consumeNextWith(( response) -> {
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(((AccessLevel) (exchange.getAttribute("cloudFoundryAccessLevel")))).isEqualTo(AccessLevel.FULL);
        }).verifyComplete();
    }

    @Test
    public void preHandleSuccessfulWithRestrictedAccess() {
        String accessToken = mockAccessToken();
        BDDMockito.given(this.securityService.getAccessLevel(accessToken, "my-app-id")).willReturn(Mono.just(RESTRICTED));
        BDDMockito.given(this.tokenValidator.validate(ArgumentMatchers.any())).willReturn(Mono.empty());
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/info").header(AUTHORIZATION, ("bearer " + (mockAccessToken()))).build());
        StepVerifier.create(this.interceptor.preHandle(exchange, "info")).consumeNextWith(( response) -> {
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(((AccessLevel) (exchange.getAttribute("cloudFoundryAccessLevel")))).isEqualTo(AccessLevel.RESTRICTED);
        }).verifyComplete();
    }
}

