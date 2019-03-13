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
package org.springframework.boot.actuate.autoconfigure.cloudfoundry;


import HttpStatus.FORBIDDEN;
import HttpStatus.UNAUTHORIZED;
import Reason.ACCESS_DENIED;
import Reason.INVALID_AUDIENCE;
import Reason.INVALID_ISSUER;
import Reason.INVALID_SIGNATURE;
import Reason.INVALID_TOKEN;
import Reason.MISSING_AUTHORIZATION;
import Reason.SERVICE_UNAVAILABLE;
import Reason.TOKEN_EXPIRED;
import Reason.UNSUPPORTED_TOKEN_SIGNING_ALGORITHM;
import org.junit.Test;


/**
 * Tests for {@link CloudFoundryAuthorizationException}.
 *
 * @author Madhura Bhave
 */
public class CloudFoundryAuthorizationExceptionTests {
    @Test
    public void statusCodeForInvalidTokenReasonShouldBe401() {
        assertThat(createException(INVALID_TOKEN).getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void statusCodeForInvalidIssuerReasonShouldBe401() {
        assertThat(createException(INVALID_ISSUER).getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void statusCodeForInvalidAudienceReasonShouldBe401() {
        assertThat(createException(INVALID_AUDIENCE).getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void statusCodeForInvalidSignatureReasonShouldBe401() {
        assertThat(createException(INVALID_SIGNATURE).getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void statusCodeForMissingAuthorizationReasonShouldBe401() {
        assertThat(createException(MISSING_AUTHORIZATION).getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void statusCodeForUnsupportedSignatureAlgorithmReasonShouldBe401() {
        assertThat(createException(UNSUPPORTED_TOKEN_SIGNING_ALGORITHM).getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void statusCodeForTokenExpiredReasonShouldBe401() {
        assertThat(createException(TOKEN_EXPIRED).getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void statusCodeForAccessDeniedReasonShouldBe403() {
        assertThat(createException(ACCESS_DENIED).getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    public void statusCodeForServiceUnavailableReasonShouldBe503() {
        assertThat(createException(SERVICE_UNAVAILABLE).getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}

