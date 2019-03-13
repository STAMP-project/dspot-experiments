/**
 * Copyright 2015-2016 the original author or authors.
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
package org.springframework.security.web.jackson2;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.security.jackson2.SimpleGrantedAuthorityMixinTests;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;


/**
 *
 *
 * @author Rob Winch
 * @since 4.2
 */
public class PreAuthenticatedAuthenticationTokenMixinTests extends AbstractMixinTests {
    // @formatter:off
    private static final String PREAUTH_JSON = (("{" + ((((("\"@class\": \"org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken\"," + "\"principal\": \"principal\", ") + "\"credentials\": \"credentials\", ") + "\"authenticated\": true, ") + "\"details\": null, ") + "\"authorities\": ")) + (SimpleGrantedAuthorityMixinTests.AUTHORITIES_ARRAYLIST_JSON)) + "}";

    // @formatter:on
    PreAuthenticatedAuthenticationToken expected;

    @Test
    public void serializeWhenPrincipalCredentialsAuthoritiesThenSuccess() throws JsonProcessingException, JSONException {
        String serializedJson = mapper.writeValueAsString(expected);
        JSONAssert.assertEquals(PreAuthenticatedAuthenticationTokenMixinTests.PREAUTH_JSON, serializedJson, true);
    }

    @Test
    public void deserializeAuthenticatedUsernamePasswordAuthenticationTokenMixinTest() throws Exception {
        PreAuthenticatedAuthenticationToken deserialized = mapper.readValue(PreAuthenticatedAuthenticationTokenMixinTests.PREAUTH_JSON, PreAuthenticatedAuthenticationToken.class);
        assertThat(deserialized).isNotNull();
        assertThat(deserialized.isAuthenticated()).isTrue();
        assertThat(deserialized.getAuthorities()).isEqualTo(expected.getAuthorities());
    }
}

