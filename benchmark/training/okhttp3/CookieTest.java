/**
 * Copyright (C) 2015 Square, Inc.
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
package okhttp3;


import HttpDate.MAX_DATE;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;


public final class CookieTest {
    HttpUrl url = HttpUrl.get("https://example.com/");

    @Test
    public void simpleCookie() throws Exception {
        Cookie cookie = Cookie.parse(url, "SID=31d4d96e407aad42");
        Assert.assertEquals("SID=31d4d96e407aad42; path=/", cookie.toString());
    }

    @Test
    public void noEqualsSign() throws Exception {
        Assert.assertNull(Cookie.parse(url, "foo"));
        Assert.assertNull(Cookie.parse(url, "foo; Path=/"));
    }

    @Test
    public void emptyName() throws Exception {
        Assert.assertNull(Cookie.parse(url, "=b"));
        Assert.assertNull(Cookie.parse(url, " =b"));
        Assert.assertNull(Cookie.parse(url, "\r\t \n=b"));
    }

    @Test
    public void spaceInName() throws Exception {
        Assert.assertEquals("a b", Cookie.parse(url, "a b=cd").name());
    }

    @Test
    public void spaceInValue() throws Exception {
        Assert.assertEquals("c d", Cookie.parse(url, "ab=c d").value());
    }

    @Test
    public void trimLeadingAndTrailingWhitespaceFromName() throws Exception {
        Assert.assertEquals("a", Cookie.parse(url, " a=b").name());
        Assert.assertEquals("a", Cookie.parse(url, "a =b").name());
        Assert.assertEquals("a", Cookie.parse(url, "\r\t \na\n\t \n=b").name());
    }

    @Test
    public void emptyValue() throws Exception {
        Assert.assertEquals("", Cookie.parse(url, "a=").value());
        Assert.assertEquals("", Cookie.parse(url, "a= ").value());
        Assert.assertEquals("", Cookie.parse(url, "a=\r\t \n").value());
    }

    @Test
    public void trimLeadingAndTrailingWhitespaceFromValue() throws Exception {
        Assert.assertEquals("", Cookie.parse(url, "a= ").value());
        Assert.assertEquals("b", Cookie.parse(url, "a= b").value());
        Assert.assertEquals("b", Cookie.parse(url, "a=b ").value());
        Assert.assertEquals("b", Cookie.parse(url, "a=\r\t \nb\n\t \n").value());
    }

    @Test
    public void invalidCharacters() throws Exception {
        Assert.assertNull(Cookie.parse(url, "a\u0000b=cd"));
        Assert.assertNull(Cookie.parse(url, "ab=c\u0000d"));
        Assert.assertNull(Cookie.parse(url, "a\u0001b=cd"));
        Assert.assertNull(Cookie.parse(url, "ab=c\u0001d"));
        Assert.assertNull(Cookie.parse(url, "a\tb=cd"));
        Assert.assertNull(Cookie.parse(url, "ab=c\td"));
        Assert.assertNull(Cookie.parse(url, "a\u001fb=cd"));
        Assert.assertNull(Cookie.parse(url, "ab=c\u001fd"));
        Assert.assertNull(Cookie.parse(url, "a\u007fb=cd"));
        Assert.assertNull(Cookie.parse(url, "ab=c\u007fd"));
        Assert.assertNull(Cookie.parse(url, "a\u0080b=cd"));
        Assert.assertNull(Cookie.parse(url, "ab=c\u0080d"));
        Assert.assertNull(Cookie.parse(url, "a\u00ffb=cd"));
        Assert.assertNull(Cookie.parse(url, "ab=c\u00ffd"));
    }

    @Test
    public void maxAge() throws Exception {
        Assert.assertEquals(51000L, Cookie.parse(50000L, url, "a=b; Max-Age=1").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(50000L, url, "a=b; Max-Age=9223372036854724").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(50000L, url, "a=b; Max-Age=9223372036854725").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(50000L, url, "a=b; Max-Age=9223372036854726").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(9223372036854773807L, url, "a=b; Max-Age=1").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(9223372036854773807L, url, "a=b; Max-Age=2").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(9223372036854773807L, url, "a=b; Max-Age=3").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(50000L, url, "a=b; Max-Age=10000000000000000000").expiresAt());
    }

    @Test
    public void maxAgeNonPositive() throws Exception {
        Assert.assertEquals(Long.MIN_VALUE, Cookie.parse(50000L, url, "a=b; Max-Age=-1").expiresAt());
        Assert.assertEquals(Long.MIN_VALUE, Cookie.parse(50000L, url, "a=b; Max-Age=0").expiresAt());
        Assert.assertEquals(Long.MIN_VALUE, Cookie.parse(50000L, url, "a=b; Max-Age=-9223372036854775808").expiresAt());
        Assert.assertEquals(Long.MIN_VALUE, Cookie.parse(50000L, url, "a=b; Max-Age=-9223372036854775809").expiresAt());
        Assert.assertEquals(Long.MIN_VALUE, Cookie.parse(50000L, url, "a=b; Max-Age=-10000000000000000000").expiresAt());
    }

    @Test
    public void domainAndPath() throws Exception {
        Cookie cookie = Cookie.parse(url, "SID=31d4d96e407aad42; Path=/; Domain=example.com");
        Assert.assertEquals("example.com", cookie.domain());
        Assert.assertEquals("/", cookie.path());
        Assert.assertFalse(cookie.hostOnly());
        Assert.assertEquals("SID=31d4d96e407aad42; domain=example.com; path=/", cookie.toString());
    }

    @Test
    public void secureAndHttpOnly() throws Exception {
        Cookie cookie = Cookie.parse(url, "SID=31d4d96e407aad42; Path=/; Secure; HttpOnly");
        Assert.assertTrue(cookie.secure());
        Assert.assertTrue(cookie.httpOnly());
        Assert.assertEquals("SID=31d4d96e407aad42; path=/; secure; httponly", cookie.toString());
    }

    @Test
    public void expiresDate() throws Exception {
        Assert.assertEquals(date("1970-01-01T00:00:00.000+0000"), new Date(Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1970 00:00:00 GMT").expiresAt()));
        Assert.assertEquals(date("2021-06-09T10:18:14.000+0000"), new Date(Cookie.parse(url, "a=b; Expires=Wed, 09 Jun 2021 10:18:14 GMT").expiresAt()));
        Assert.assertEquals(date("1994-11-06T08:49:37.000+0000"), new Date(Cookie.parse(url, "a=b; Expires=Sun, 06 Nov 1994 08:49:37 GMT").expiresAt()));
    }

    @Test
    public void awkwardDates() throws Exception {
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 70 00:00:00 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=Thu, 01 January 1970 00:00:00 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=Thu, 01 Janucember 1970 00:00:00 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=Thu, 1 Jan 1970 00:00:00 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1970 0:00:00 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1970 00:0:00 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1970 00:00:0 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=00:00:00 Thu, 01 Jan 1970 GMT").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=00:00:00 1970 Jan 01").expiresAt());
        Assert.assertEquals(0L, Cookie.parse(url, "a=b; Expires=00:00:00 1970 Jan 1").expiresAt());
    }

    @Test
    public void invalidYear() throws Exception {
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1600 00:00:00 GMT").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 19999 00:00:00 GMT").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 00:00:00 GMT").expiresAt());
    }

    @Test
    public void invalidMonth() throws Exception {
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Foo 1970 00:00:00 GMT").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Foocember 1970 00:00:00 GMT").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 1970 00:00:00 GMT").expiresAt());
    }

    @Test
    public void invalidDayOfMonth() throws Exception {
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 32 Jan 1970 00:00:00 GMT").expiresAt());
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, Jan 1970 00:00:00 GMT").expiresAt());
    }

    @Test
    public void invalidHour() throws Exception {
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1970 24:00:00 GMT").expiresAt());
    }

    @Test
    public void invalidMinute() throws Exception {
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1970 00:60:00 GMT").expiresAt());
    }

    @Test
    public void invalidSecond() throws Exception {
        Assert.assertEquals(MAX_DATE, Cookie.parse(url, "a=b; Expires=Thu, 01 Jan 1970 00:00:60 GMT").expiresAt());
    }

    @Test
    public void domainMatches() throws Exception {
        Cookie cookie = Cookie.parse(url, "a=b; domain=example.com");
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://example.com")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://www.example.com")));
        Assert.assertFalse(cookie.matches(HttpUrl.get("http://square.com")));
    }

    /**
     * If no domain is present, match only the origin domain.
     */
    @Test
    public void domainMatchesNoDomain() throws Exception {
        Cookie cookie = Cookie.parse(url, "a=b");
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://example.com")));
        Assert.assertFalse(cookie.matches(HttpUrl.get("http://www.example.com")));
        Assert.assertFalse(cookie.matches(HttpUrl.get("http://square.com")));
    }

    /**
     * Ignore an optional leading `.` in the domain.
     */
    @Test
    public void domainMatchesIgnoresLeadingDot() throws Exception {
        Cookie cookie = Cookie.parse(url, "a=b; domain=.example.com");
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://example.com")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://www.example.com")));
        Assert.assertFalse(cookie.matches(HttpUrl.get("http://square.com")));
    }

    /**
     * Ignore the entire attribute if the domain ends with `.`.
     */
    @Test
    public void domainIgnoredWithTrailingDot() throws Exception {
        Cookie cookie = Cookie.parse(url, "a=b; domain=example.com.");
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://example.com")));
        Assert.assertFalse(cookie.matches(HttpUrl.get("http://www.example.com")));
        Assert.assertFalse(cookie.matches(HttpUrl.get("http://square.com")));
    }

    @Test
    public void idnDomainMatches() throws Exception {
        Cookie cookie = Cookie.parse(HttpUrl.get("http://?.net/"), "a=b; domain=?.net");
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://?.net/")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://xn--n3h.net/")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://www.?.net/")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://www.xn--n3h.net/")));
    }

    @Test
    public void punycodeDomainMatches() throws Exception {
        Cookie cookie = Cookie.parse(HttpUrl.get("http://xn--n3h.net/"), "a=b; domain=xn--n3h.net");
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://?.net/")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://xn--n3h.net/")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://www.?.net/")));
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://www.xn--n3h.net/")));
    }

    @Test
    public void domainMatchesIpAddress() throws Exception {
        HttpUrl urlWithIp = HttpUrl.get("http://123.45.234.56/");
        Assert.assertNull(Cookie.parse(urlWithIp, "a=b; domain=234.56"));
        Assert.assertEquals("123.45.234.56", Cookie.parse(urlWithIp, "a=b; domain=123.45.234.56").domain());
    }

    @Test
    public void domainMatchesIpv6Address() throws Exception {
        Cookie cookie = Cookie.parse(HttpUrl.get("http://[::1]/"), "a=b; domain=::1");
        Assert.assertEquals("::1", cookie.domain());
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://[::1]/")));
    }

    @Test
    public void domainMatchesIpv6AddressWithCompression() throws Exception {
        Cookie cookie = Cookie.parse(HttpUrl.get("http://[0001:0000::]/"), "a=b; domain=0001:0000::");
        Assert.assertEquals("1::", cookie.domain());
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://[1::]/")));
    }

    @Test
    public void domainMatchesIpv6AddressWithIpv4Suffix() throws Exception {
        Cookie cookie = Cookie.parse(HttpUrl.get("http://[::1:ffff:ffff]/"), "a=b; domain=::1:255.255.255.255");
        Assert.assertEquals("::1:ffff:ffff", cookie.domain());
        Assert.assertTrue(cookie.matches(HttpUrl.get("http://[::1:ffff:ffff]/")));
    }

    @Test
    public void ipv6AddressDoesntMatch() throws Exception {
        Cookie cookie = Cookie.parse(HttpUrl.get("http://[::1]/"), "a=b; domain=::2");
        Assert.assertNull(cookie);
    }

    @Test
    public void ipv6AddressMalformed() throws Exception {
        Cookie cookie = Cookie.parse(HttpUrl.get("http://[::1]/"), "a=b; domain=::2::2");
        Assert.assertEquals("::1", cookie.domain());
    }

    /**
     * These public suffixes were selected by inspecting the publicsuffix.org list. It's possible they
     * may change in the future. If this test begins to fail, please double check they are still
     * present in the public suffix list.
     */
    @Test
    public void domainIsPublicSuffix() {
        HttpUrl ascii = HttpUrl.get("https://foo1.foo.bar.elb.amazonaws.com");
        Assert.assertNotNull(Cookie.parse(ascii, "a=b; domain=foo.bar.elb.amazonaws.com"));
        Assert.assertNull(Cookie.parse(ascii, "a=b; domain=bar.elb.amazonaws.com"));
        Assert.assertNull(Cookie.parse(ascii, "a=b; domain=com"));
        HttpUrl unicode = HttpUrl.get("https://?.?.??.jp");
        Assert.assertNotNull(Cookie.parse(unicode, "a=b; domain=?.??.jp"));
        Assert.assertNull(Cookie.parse(unicode, "a=b; domain=??.jp"));
        HttpUrl punycode = HttpUrl.get("https://xn--ue5a.xn--ue5a.xn--8ltr62k.jp");
        Assert.assertNotNull(Cookie.parse(punycode, "a=b; domain=xn--ue5a.xn--8ltr62k.jp"));
        Assert.assertNull(Cookie.parse(punycode, "a=b; domain=xn--8ltr62k.jp"));
    }

    @Test
    public void hostOnly() throws Exception {
        Assert.assertTrue(Cookie.parse(url, "a=b").hostOnly());
        Assert.assertFalse(Cookie.parse(url, "a=b; domain=example.com").hostOnly());
    }

    @Test
    public void defaultPath() throws Exception {
        Assert.assertEquals("/foo", Cookie.parse(HttpUrl.get("http://example.com/foo/bar"), "a=b").path());
        Assert.assertEquals("/foo", Cookie.parse(HttpUrl.get("http://example.com/foo/"), "a=b").path());
        Assert.assertEquals("/", Cookie.parse(HttpUrl.get("http://example.com/foo"), "a=b").path());
        Assert.assertEquals("/", Cookie.parse(HttpUrl.get("http://example.com/"), "a=b").path());
    }

    @Test
    public void defaultPathIsUsedIfPathDoesntHaveLeadingSlash() throws Exception {
        Assert.assertEquals("/foo", Cookie.parse(HttpUrl.get("http://example.com/foo/bar"), "a=b; path=quux").path());
        Assert.assertEquals("/foo", Cookie.parse(HttpUrl.get("http://example.com/foo/bar"), "a=b; path=").path());
    }

    @Test
    public void pathAttributeDoesntNeedToMatch() throws Exception {
        Assert.assertEquals("/quux", Cookie.parse(HttpUrl.get("http://example.com/"), "a=b; path=/quux").path());
        Assert.assertEquals("/quux", Cookie.parse(HttpUrl.get("http://example.com/foo/bar"), "a=b; path=/quux").path());
    }

    @Test
    public void httpOnly() throws Exception {
        Assert.assertFalse(Cookie.parse(url, "a=b").httpOnly());
        Assert.assertTrue(Cookie.parse(url, "a=b; HttpOnly").httpOnly());
    }

    @Test
    public void secure() throws Exception {
        Assert.assertFalse(Cookie.parse(url, "a=b").secure());
        Assert.assertTrue(Cookie.parse(url, "a=b; Secure").secure());
    }

    @Test
    public void maxAgeTakesPrecedenceOverExpires() throws Exception {
        // Max-Age = 1, Expires = 2. In either order.
        Assert.assertEquals(1000L, Cookie.parse(0L, url, "a=b; Max-Age=1; Expires=Thu, 01 Jan 1970 00:00:02 GMT").expiresAt());
        Assert.assertEquals(1000L, Cookie.parse(0L, url, "a=b; Expires=Thu, 01 Jan 1970 00:00:02 GMT; Max-Age=1").expiresAt());
        // Max-Age = 2, Expires = 1. In either order.
        Assert.assertEquals(2000L, Cookie.parse(0L, url, "a=b; Max-Age=2; Expires=Thu, 01 Jan 1970 00:00:01 GMT").expiresAt());
        Assert.assertEquals(2000L, Cookie.parse(0L, url, "a=b; Expires=Thu, 01 Jan 1970 00:00:01 GMT; Max-Age=2").expiresAt());
    }

    /**
     * If a cookie incorrectly defines multiple 'Max-Age' attributes, the last one defined wins.
     */
    @Test
    public void lastMaxAgeWins() throws Exception {
        Assert.assertEquals(3000L, Cookie.parse(0L, url, "a=b; Max-Age=2; Max-Age=4; Max-Age=1; Max-Age=3").expiresAt());
    }

    /**
     * If a cookie incorrectly defines multiple 'Expires' attributes, the last one defined wins.
     */
    @Test
    public void lastExpiresAtWins() throws Exception {
        Assert.assertEquals(3000L, Cookie.parse(0L, url, ("a=b; " + ((("Expires=Thu, 01 Jan 1970 00:00:02 GMT; " + "Expires=Thu, 01 Jan 1970 00:00:04 GMT; ") + "Expires=Thu, 01 Jan 1970 00:00:01 GMT; ") + "Expires=Thu, 01 Jan 1970 00:00:03 GMT"))).expiresAt());
    }

    @Test
    public void maxAgeOrExpiresMakesCookiePersistent() throws Exception {
        Assert.assertFalse(Cookie.parse(0L, url, "a=b").persistent());
        Assert.assertTrue(Cookie.parse(0L, url, "a=b; Max-Age=1").persistent());
        Assert.assertTrue(Cookie.parse(0L, url, "a=b; Expires=Thu, 01 Jan 1970 00:00:01 GMT").persistent());
    }

    @Test
    public void parseAll() throws Exception {
        Headers headers = new Headers.Builder().add("Set-Cookie: a=b").add("Set-Cookie: c=d").build();
        List<Cookie> cookies = Cookie.parseAll(url, headers);
        Assert.assertEquals(2, cookies.size());
        Assert.assertEquals("a=b; path=/", cookies.get(0).toString());
        Assert.assertEquals("c=d; path=/", cookies.get(1).toString());
    }

    @Test
    public void builder() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").domain("example.com").build();
        Assert.assertEquals("a", cookie.name());
        Assert.assertEquals("b", cookie.value());
        Assert.assertEquals(MAX_DATE, cookie.expiresAt());
        Assert.assertEquals("example.com", cookie.domain());
        Assert.assertEquals("/", cookie.path());
        Assert.assertFalse(cookie.secure());
        Assert.assertFalse(cookie.httpOnly());
        Assert.assertFalse(cookie.persistent());
        Assert.assertFalse(cookie.hostOnly());
    }

    @Test
    public void builderNameValidation() throws Exception {
        try {
            new Cookie.Builder().name(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new Cookie.Builder().name(" a ");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void builderValueValidation() throws Exception {
        try {
            new Cookie.Builder().value(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new Cookie.Builder().value(" b ");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void builderClampsMaxDate() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").hostOnlyDomain("example.com").expiresAt(Long.MAX_VALUE).build();
        Assert.assertEquals("a=b; expires=Fri, 31 Dec 9999 23:59:59 GMT; path=/", cookie.toString());
    }

    @Test
    public void builderExpiresAt() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").hostOnlyDomain("example.com").expiresAt(date("1970-01-01T00:00:01.000+0000").getTime()).build();
        Assert.assertEquals("a=b; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/", cookie.toString());
    }

    @Test
    public void builderClampsMinDate() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").hostOnlyDomain("example.com").expiresAt(date("1970-01-01T00:00:00.000+0000").getTime()).build();
        Assert.assertEquals("a=b; max-age=0; path=/", cookie.toString());
    }

    @Test
    public void builderDomainValidation() throws Exception {
        try {
            new Cookie.Builder().hostOnlyDomain(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new Cookie.Builder().hostOnlyDomain("a/b");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void builderDomain() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").hostOnlyDomain("squareup.com").build();
        Assert.assertEquals("squareup.com", cookie.domain());
        Assert.assertTrue(cookie.hostOnly());
    }

    @Test
    public void builderPath() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").hostOnlyDomain("example.com").path("/foo").build();
        Assert.assertEquals("/foo", cookie.path());
    }

    @Test
    public void builderPathValidation() throws Exception {
        try {
            new Cookie.Builder().path(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            new Cookie.Builder().path("foo");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void builderSecure() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").hostOnlyDomain("example.com").secure().build();
        Assert.assertTrue(cookie.secure());
    }

    @Test
    public void builderHttpOnly() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").hostOnlyDomain("example.com").httpOnly().build();
        Assert.assertTrue(cookie.httpOnly());
    }

    @Test
    public void builderIpv6() throws Exception {
        Cookie cookie = new Cookie.Builder().name("a").value("b").domain("0:0:0:0:0:0:0:1").build();
        Assert.assertEquals("::1", cookie.domain());
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        List<String> cookieStrings = Arrays.asList("a=b; Path=/c; Domain=example.com; Max-Age=5; Secure; HttpOnly", "a= ; Path=/c; Domain=example.com; Max-Age=5; Secure; HttpOnly", "a=b;          Domain=example.com; Max-Age=5; Secure; HttpOnly", "a=b; Path=/c;                     Max-Age=5; Secure; HttpOnly", "a=b; Path=/c; Domain=example.com;            Secure; HttpOnly", "a=b; Path=/c; Domain=example.com; Max-Age=5;         HttpOnly", "a=b; Path=/c; Domain=example.com; Max-Age=5; Secure;         ");
        for (String stringA : cookieStrings) {
            Cookie cookieA = Cookie.parse(0, url, stringA);
            for (String stringB : cookieStrings) {
                Cookie cookieB = Cookie.parse(0, url, stringB);
                if (Objects.equals(stringA, stringB)) {
                    Assert.assertEquals(cookieA.hashCode(), cookieB.hashCode());
                    Assert.assertEquals(cookieA, cookieB);
                } else {
                    Assert.assertNotEquals(cookieA.hashCode(), cookieB.hashCode());
                    Assert.assertNotEquals(cookieA, cookieB);
                }
            }
            Assert.assertNotEquals(null, cookieA);
        }
    }
}

