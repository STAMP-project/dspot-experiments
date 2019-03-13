/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.mock.web;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link MockCookie}.
 *
 * @author Vedran Pavic
 * @author Sam Brannen
 * @since 5.1
 */
public class MockCookieTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void constructCookie() {
        MockCookie cookie = new MockCookie("SESSION", "123");
        assertCookie(cookie, "SESSION", "123");
        Assert.assertNull(cookie.getDomain());
        Assert.assertEquals((-1), cookie.getMaxAge());
        Assert.assertNull(cookie.getPath());
        Assert.assertFalse(cookie.isHttpOnly());
        Assert.assertFalse(cookie.getSecure());
        Assert.assertNull(cookie.getSameSite());
    }

    @Test
    public void setSameSite() {
        MockCookie cookie = new MockCookie("SESSION", "123");
        cookie.setSameSite("Strict");
        Assert.assertEquals("Strict", cookie.getSameSite());
    }

    @Test
    public void parseHeaderWithoutAttributes() {
        MockCookie cookie;
        cookie = MockCookie.parse("SESSION=123");
        assertCookie(cookie, "SESSION", "123");
        cookie = MockCookie.parse("SESSION=123;");
        assertCookie(cookie, "SESSION", "123");
    }

    @Test
    public void parseHeaderWithAttributes() {
        MockCookie cookie = MockCookie.parse("SESSION=123; Domain=example.com; Max-Age=60; Path=/; Secure; HttpOnly; SameSite=Lax");
        assertCookie(cookie, "SESSION", "123");
        Assert.assertEquals("example.com", cookie.getDomain());
        Assert.assertEquals(60, cookie.getMaxAge());
        Assert.assertEquals("/", cookie.getPath());
        Assert.assertTrue(cookie.getSecure());
        Assert.assertTrue(cookie.isHttpOnly());
        Assert.assertEquals("Lax", cookie.getSameSite());
    }

    @Test
    public void parseNullHeader() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Set-Cookie header must not be null");
        MockCookie.parse(null);
    }

    @Test
    public void parseInvalidHeader() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid Set-Cookie header 'BOOM'");
        MockCookie.parse("BOOM");
    }

    @Test
    public void parseInvalidAttribute() {
        String header = "SESSION=123; Path=";
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage((("No value in attribute 'Path' for Set-Cookie header '" + header) + "'"));
        MockCookie.parse(header);
    }
}

