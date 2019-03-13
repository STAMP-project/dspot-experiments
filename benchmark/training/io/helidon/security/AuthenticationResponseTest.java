/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.security;


import SecurityResponse.SecurityStatus.ABSTAIN;
import SecurityResponse.SecurityStatus.FAILURE;
import io.helidon.common.OptionalHelper;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link AuthenticationResponse}.
 */
public class AuthenticationResponseTest {
    @Test
    public void testFail() {
        String message = "aMessage";
        AuthenticationResponse response = AuthenticationResponse.failed(message);
        response.statusCode().ifPresent(( it) -> fail(("Status code should not be present: " + it)));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(Optional.empty()));
        OptionalHelper.from(response.description()).ifPresentOrElse(( it) -> assertThat(it, is(message)), () -> fail("Description should have been filled"));
        response.throwable().ifPresent(( it) -> fail("Throwable should not be filled"));
    }

    @Test
    public void testFailWithException() {
        String message = "aMessage";
        Throwable throwable = new SecurityException("test");
        AuthenticationResponse response = AuthenticationResponse.failed(message, throwable);
        response.statusCode().ifPresent(( it) -> fail(("Status code should not be present: " + it)));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(Optional.empty()));
        OptionalHelper.from(response.description()).ifPresentOrElse(( it) -> assertThat(it, is(message)), () -> fail("Description should have been filled"));
        OptionalHelper.from(response.throwable()).ifPresentOrElse(( it) -> assertThat(it, sameInstance(throwable)), () -> fail("Throwable should not be filled"));
    }

    @Test
    public void testAbstain() {
        AuthenticationResponse response = AuthenticationResponse.abstain();
        response.statusCode().ifPresent(( it) -> fail(("Status code should not be present: " + it)));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(ABSTAIN));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(Optional.empty()));
        response.description().ifPresent(( it) -> fail("Description should not be filled"));
    }

    @Test
    public void testSuccessSubject() {
        Principal myPrincipal = Principal.create("aUser");
        Subject subject = Subject.builder().principal(myPrincipal).build();
        AuthenticationResponse response = AuthenticationResponse.success(subject);
        validateSuccessResponse(response, myPrincipal, subject);
    }

    @Test
    public void testSuccessPrincipal() {
        Principal myPrincipal = Principal.create("aUser");
        AuthenticationResponse response = AuthenticationResponse.success(myPrincipal);
        validateSuccessResponse(response, myPrincipal, null);
    }
}

