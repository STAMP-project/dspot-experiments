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
package org.springframework.security.jackson2;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


/**
 *
 *
 * @author Jitendra Singh
 * @since 4.2
 */
public class SimpleGrantedAuthorityMixinTests extends AbstractMixinTests {
    // @formatter:off
    public static final String AUTHORITY_JSON = "{\"@class\": \"org.springframework.security.core.authority.SimpleGrantedAuthority\", \"authority\": \"ROLE_USER\"}";

    public static final String AUTHORITIES_ARRAYLIST_JSON = ("[\"java.util.Collections$UnmodifiableRandomAccessList\", [" + (SimpleGrantedAuthorityMixinTests.AUTHORITY_JSON)) + "]]";

    public static final String AUTHORITIES_SET_JSON = ("[\"java.util.Collections$UnmodifiableSet\", [" + (SimpleGrantedAuthorityMixinTests.AUTHORITY_JSON)) + "]]";

    public static final String NO_AUTHORITIES_ARRAYLIST_JSON = "[\"java.util.Collections$UnmodifiableRandomAccessList\", []]";

    public static final String EMPTY_AUTHORITIES_ARRAYLIST_JSON = "[\"java.util.Collections$EmptyList\", []]";

    public static final String NO_AUTHORITIES_SET_JSON = "[\"java.util.Collections$UnmodifiableSet\", []]";

    // @formatter:on
    @Test
    public void serializeSimpleGrantedAuthorityTest() throws JsonProcessingException, JSONException {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        String serializeJson = mapper.writeValueAsString(authority);
        JSONAssert.assertEquals(SimpleGrantedAuthorityMixinTests.AUTHORITY_JSON, serializeJson, true);
    }

    @Test
    public void deserializeGrantedAuthorityTest() throws IOException {
        SimpleGrantedAuthority authority = mapper.readValue(SimpleGrantedAuthorityMixinTests.AUTHORITY_JSON, SimpleGrantedAuthority.class);
        assertThat(authority).isNotNull();
        assertThat(authority.getAuthority()).isNotNull().isEqualTo("ROLE_USER");
    }

    @Test(expected = JsonMappingException.class)
    public void deserializeGrantedAuthorityWithoutRoleTest() throws IOException {
        String json = "{\"@class\": \"org.springframework.security.core.authority.SimpleGrantedAuthority\"}";
        mapper.readValue(json, SimpleGrantedAuthority.class);
    }
}

