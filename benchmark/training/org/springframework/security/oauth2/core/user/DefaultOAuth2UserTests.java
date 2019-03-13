/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.oauth2.core.user;


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.SerializationUtils;


/**
 * Tests for {@link DefaultOAuth2User}.
 *
 * @author Vedran Pavic
 * @author Joe Grandja
 */
public class DefaultOAuth2UserTests {
    private static final SimpleGrantedAuthority AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");

    private static final Set<GrantedAuthority> AUTHORITIES = Collections.singleton(DefaultOAuth2UserTests.AUTHORITY);

    private static final String ATTRIBUTE_NAME_KEY = "username";

    private static final String USERNAME = "test";

    private static final Map<String, Object> ATTRIBUTES = Collections.singletonMap(DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY, DefaultOAuth2UserTests.USERNAME);

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAuthoritiesIsNullThenThrowIllegalArgumentException() {
        new DefaultOAuth2User(null, DefaultOAuth2UserTests.ATTRIBUTES, DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAuthoritiesIsEmptyThenThrowIllegalArgumentException() {
        new DefaultOAuth2User(Collections.emptySet(), DefaultOAuth2UserTests.ATTRIBUTES, DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAttributesIsNullThenThrowIllegalArgumentException() {
        new DefaultOAuth2User(DefaultOAuth2UserTests.AUTHORITIES, null, DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenAttributesIsEmptyThenThrowIllegalArgumentException() {
        new DefaultOAuth2User(DefaultOAuth2UserTests.AUTHORITIES, Collections.emptyMap(), DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenNameAttributeKeyIsNullThenThrowIllegalArgumentException() {
        new DefaultOAuth2User(DefaultOAuth2UserTests.AUTHORITIES, DefaultOAuth2UserTests.ATTRIBUTES, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenNameAttributeKeyIsInvalidThenThrowIllegalArgumentException() {
        new DefaultOAuth2User(DefaultOAuth2UserTests.AUTHORITIES, DefaultOAuth2UserTests.ATTRIBUTES, "invalid");
    }

    @Test
    public void constructorWhenAllParametersProvidedAndValidThenCreated() {
        DefaultOAuth2User user = new DefaultOAuth2User(DefaultOAuth2UserTests.AUTHORITIES, DefaultOAuth2UserTests.ATTRIBUTES, DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY);
        assertThat(user.getName()).isEqualTo(DefaultOAuth2UserTests.USERNAME);
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next()).isEqualTo(DefaultOAuth2UserTests.AUTHORITY);
        assertThat(user.getAttributes()).containsOnlyKeys(DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY);
    }

    // gh-4917
    @Test
    public void constructorWhenCreatedThenIsSerializable() {
        DefaultOAuth2User user = new DefaultOAuth2User(DefaultOAuth2UserTests.AUTHORITIES, DefaultOAuth2UserTests.ATTRIBUTES, DefaultOAuth2UserTests.ATTRIBUTE_NAME_KEY);
        SerializationUtils.serialize(user);
    }
}

