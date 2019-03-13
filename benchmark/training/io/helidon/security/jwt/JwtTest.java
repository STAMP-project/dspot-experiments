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
package io.helidon.security.jwt;


import JwkRSA.ALG_RS256;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.Errors;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link Jwt}
 */
public class JwtTest {
    private static final Logger LOGGER = Logger.getLogger(JwtTest.class.getName());

    @Test
    public void testBuilderBasicJwt() {
        String id = UUID.randomUUID().toString();
        String audience = "id_of_audience";
        Jwt jwt = Jwt.builder().jwtId(id).algorithm(ALG_RS256).audience(audience).build();
        MatcherAssert.assertThat(jwt.jwtId(), CoreMatchers.is(Optional.of(id)));
        MatcherAssert.assertThat(jwt.algorithm(), CoreMatchers.is(Optional.of(ALG_RS256)));
        MatcherAssert.assertThat(jwt.audience(), CoreMatchers.is(Optional.of(CollectionsHelper.listOf(audience))));
    }

    @Test
    public void testOidcJwt() {
        String audience = "id_of_audience";
        String subject = "54564645646465";
        String username = "jarda@jarda.com";
        String issuer = "I am issuer";
        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);
        Instant notBefore = now.minus(2, ChronoUnit.SECONDS);
        Jwt jwt = // time info
        Jwt.builder().jwtId(UUID.randomUUID().toString()).addScope("link").addScope("lank").addScope("lunk").subject(subject).preferredUsername(username).algorithm(ALG_RS256).audience(audience).issuer(issuer).issueTime(now).expirationTime(expiration).notBefore(notBefore).build();
        MatcherAssert.assertThat(jwt.scopes(), CoreMatchers.is(Optional.of(CollectionsHelper.listOf("link", "lank", "lunk"))));
        MatcherAssert.assertThat(jwt.subject(), CoreMatchers.is(Optional.of(subject)));
        MatcherAssert.assertThat(jwt.preferredUsername(), CoreMatchers.is(Optional.of(username)));
        MatcherAssert.assertThat(jwt.issueTime(), CoreMatchers.is(Optional.of(now)));
        MatcherAssert.assertThat(jwt.expirationTime(), CoreMatchers.is(Optional.of(expiration)));
        MatcherAssert.assertThat(jwt.notBefore(), CoreMatchers.is(Optional.of(notBefore)));
        // and this one should be valid
        List<Validator<Jwt>> vals = Jwt.defaultTimeValidators();
        Jwt.addIssuerValidator(vals, issuer, true);
        Jwt.addAudienceValidator(vals, audience, true);
        Errors errors = jwt.validate(vals);
        errors.log(JwtTest.LOGGER);
        errors.checkValid();
        // another try with defaults
        errors = jwt.validate(issuer, audience);
        errors.log(JwtTest.LOGGER);
        errors.checkValid();
    }
}

