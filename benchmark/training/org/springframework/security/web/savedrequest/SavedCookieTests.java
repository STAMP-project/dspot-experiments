/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.web.savedrequest;


import java.io.Serializable;
import javax.servlet.http.Cookie;
import org.junit.Test;


public class SavedCookieTests {
    Cookie cookie;

    SavedCookie savedCookie;

    @Test
    public void testGetName() throws Exception {
        assertThat(savedCookie.getName()).isEqualTo(cookie.getName());
    }

    @Test
    public void testGetValue() throws Exception {
        assertThat(savedCookie.getValue()).isEqualTo(cookie.getValue());
    }

    @Test
    public void testGetComment() throws Exception {
        assertThat(savedCookie.getComment()).isEqualTo(cookie.getComment());
    }

    @Test
    public void testGetDomain() throws Exception {
        assertThat(savedCookie.getDomain()).isEqualTo(cookie.getDomain());
    }

    @Test
    public void testGetMaxAge() throws Exception {
        assertThat(savedCookie.getMaxAge()).isEqualTo(cookie.getMaxAge());
    }

    @Test
    public void testGetPath() throws Exception {
        assertThat(savedCookie.getPath()).isEqualTo(cookie.getPath());
    }

    @Test
    public void testGetVersion() throws Exception {
        assertThat(savedCookie.getVersion()).isEqualTo(cookie.getVersion());
    }

    @Test
    public void testGetCookie() throws Exception {
        Cookie other = savedCookie.getCookie();
        assertThat(other.getComment()).isEqualTo(cookie.getComment());
        assertThat(other.getDomain()).isEqualTo(cookie.getDomain());
        assertThat(other.getMaxAge()).isEqualTo(cookie.getMaxAge());
        assertThat(other.getName()).isEqualTo(cookie.getName());
        assertThat(other.getPath()).isEqualTo(cookie.getPath());
        assertThat(other.getSecure()).isEqualTo(cookie.getSecure());
        assertThat(other.getValue()).isEqualTo(cookie.getValue());
        assertThat(other.getVersion()).isEqualTo(cookie.getVersion());
    }

    @Test
    public void testSerializable() throws Exception {
        assertThat(((savedCookie) instanceof Serializable)).isTrue();
    }
}

