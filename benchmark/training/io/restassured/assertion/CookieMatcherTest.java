/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.assertion;


import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Podgurskiy
 */
public class CookieMatcherTest {
    @Test
    public void testSetVersion() throws ParseException {
        String[] cookies = new String[]{ "DEVICE_ID=123; Domain=.test.com; Expires=Thu, 12-Oct-2023 09:34:31 GMT; Path=/; Secure; HttpOnly;", "SPRING_SECURITY_REMEMBER_ME_COOKIE=12345;Version=0;Domain=.test.com;Path=/;Max-Age=1209600", "COOKIE_WITH_ZERO_MAX_AGE=1234;Version=0;Domain=.test.com;Path=/;Max-Age=0", "COOKIE_WITH_NEGATIVE_MAX_AGE=123456;Version=0;Domain=.test.com;Path=/;Max-Age=-1" };
        Cookies result = CookieMatcher.getCookies(cookies);
        Assert.assertEquals(4, result.size());
        Cookie sprintCookie = result.get("SPRING_SECURITY_REMEMBER_ME_COOKIE");
        Assert.assertEquals(0, sprintCookie.getVersion());
        Assert.assertEquals("12345", sprintCookie.getValue());
        Assert.assertEquals(".test.com", sprintCookie.getDomain());
        Assert.assertEquals("/", sprintCookie.getPath());
        Assert.assertEquals(1209600, sprintCookie.getMaxAge());
        Assert.assertEquals(false, sprintCookie.isSecured());
        Assert.assertEquals(false, sprintCookie.isHttpOnly());
        Cookie cookieWithZeroMaxAge = result.get("COOKIE_WITH_ZERO_MAX_AGE");
        Assert.assertEquals(0, cookieWithZeroMaxAge.getVersion());
        Assert.assertEquals("1234", cookieWithZeroMaxAge.getValue());
        Assert.assertEquals(".test.com", cookieWithZeroMaxAge.getDomain());
        Assert.assertEquals("/", cookieWithZeroMaxAge.getPath());
        Assert.assertEquals(0, cookieWithZeroMaxAge.getMaxAge());
        Assert.assertEquals(false, cookieWithZeroMaxAge.isSecured());
        Assert.assertEquals(false, cookieWithZeroMaxAge.isHttpOnly());
        Cookie cookieWithNegativeMaxAge = result.get("COOKIE_WITH_NEGATIVE_MAX_AGE");
        Assert.assertEquals(0, cookieWithNegativeMaxAge.getVersion());
        Assert.assertEquals("123456", cookieWithNegativeMaxAge.getValue());
        Assert.assertEquals(".test.com", cookieWithNegativeMaxAge.getDomain());
        Assert.assertEquals("/", cookieWithNegativeMaxAge.getPath());
        Assert.assertEquals((-1), cookieWithNegativeMaxAge.getMaxAge());
        Assert.assertEquals(false, cookieWithNegativeMaxAge.isSecured());
        Assert.assertEquals(false, cookieWithNegativeMaxAge.isHttpOnly());
        Cookie deviceCookie = result.get("DEVICE_ID");
        Assert.assertEquals((-1), deviceCookie.getVersion());
        Assert.assertEquals("123", deviceCookie.getValue());
        Assert.assertEquals(".test.com", deviceCookie.getDomain());
        Assert.assertEquals("/", deviceCookie.getPath());
        Assert.assertEquals(new SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss Z", Locale.ENGLISH).parse("Thu, 12-Oct-2023 09:34:31 GMT"), deviceCookie.getExpiryDate());
        Assert.assertEquals(true, deviceCookie.isSecured());
        Assert.assertEquals(true, deviceCookie.isHttpOnly());
    }

    @Test
    public void deals_with_empty_cookie_values() {
        // Given
        String[] cookiesAsString = new String[]{ "un=bob; domain=bob.com; path=/", "", "_session_id=asdfwerwersdfwere; domain=bob.com; path=/; HttpOnly" };
        // When
        Cookies cookies = CookieMatcher.getCookies(cookiesAsString);
        // Then
        Assert.assertThat(cookies.size(), Matchers.is(3));
        Assert.assertThat(cookies, Matchers.<Cookie>hasItem(Matchers.nullValue()));
    }
}

