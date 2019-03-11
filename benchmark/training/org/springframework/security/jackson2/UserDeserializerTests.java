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
import java.io.IOException;
import java.util.Collections;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


/**
 *
 *
 * @author Jitendra Singh
 * @since 4.2
 */
public class UserDeserializerTests extends AbstractMixinTests {
    public static final String USER_PASSWORD = "\"1234\"";

    // @formatter:off
    public static final String USER_JSON = ((((((((("{" + (("\"@class\": \"org.springframework.security.core.userdetails.User\", " + "\"username\": \"admin\",") + " \"password\": ")) + (UserDeserializerTests.USER_PASSWORD)) + ", ") + "\"accountNonExpired\": true, ") + "\"accountNonLocked\": true, ") + "\"credentialsNonExpired\": true, ") + "\"enabled\": true, ") + "\"authorities\": ") + (SimpleGrantedAuthorityMixinTests.AUTHORITIES_SET_JSON)) + "}";

    // @formatter:on
    @Test
    public void serializeUserTest() throws JsonProcessingException, JSONException {
        User user = createDefaultUser();
        String userJson = mapper.writeValueAsString(user);
        JSONAssert.assertEquals(UserDeserializerTests.userWithPasswordJson(user.getPassword()), userJson, true);
    }

    @Test
    public void serializeUserWithoutAuthority() throws JsonProcessingException, JSONException {
        User user = new User("admin", "1234", Collections.<GrantedAuthority>emptyList());
        String userJson = mapper.writeValueAsString(user);
        JSONAssert.assertEquals(UserDeserializerTests.userWithNoAuthoritiesJson(), userJson, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeUserWithNullPasswordEmptyAuthorityTest() throws IOException {
        String userJsonWithoutPasswordString = UserDeserializerTests.USER_JSON.replace(SimpleGrantedAuthorityMixinTests.AUTHORITIES_SET_JSON, "[]");
        mapper.readValue(userJsonWithoutPasswordString, User.class);
    }

    @Test
    public void deserializeUserWithNullPasswordNoAuthorityTest() throws Exception {
        String userJsonWithoutPasswordString = removeNode(UserDeserializerTests.userWithNoAuthoritiesJson(), mapper, "password");
        User user = mapper.readValue(userJsonWithoutPasswordString, User.class);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isNull();
        assertThat(user.getAuthorities()).isEmpty();
        assertThat(user.isEnabled()).isEqualTo(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeUserWithNoClassIdInAuthoritiesTest() throws Exception {
        String userJson = UserDeserializerTests.USER_JSON.replace(SimpleGrantedAuthorityMixinTests.AUTHORITIES_SET_JSON, "[{\"authority\": \"ROLE_USER\"}]");
        mapper.readValue(userJson, User.class);
    }

    @Test
    public void deserializeUserWithClassIdInAuthoritiesTest() throws IOException {
        User user = mapper.readValue(UserDeserializerTests.userJson(), User.class);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("1234");
        assertThat(user.getAuthorities()).hasSize(1).contains(new SimpleGrantedAuthority("ROLE_USER"));
    }
}

