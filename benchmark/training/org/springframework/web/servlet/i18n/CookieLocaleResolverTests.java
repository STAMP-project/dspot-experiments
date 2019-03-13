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
package org.springframework.web.servlet.i18n;


import CookieLocaleResolver.DEFAULT_COOKIE_NAME;
import CookieLocaleResolver.DEFAULT_COOKIE_PATH;
import CookieLocaleResolver.LOCALE_REQUEST_ATTRIBUTE_NAME;
import CookieLocaleResolver.TIME_ZONE_REQUEST_ATTRIBUTE_NAME;
import WebUtils.ERROR_EXCEPTION_ATTRIBUTE;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;

import static CookieLocaleResolver.DEFAULT_COOKIE_NAME;


/**
 *
 *
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @author Rick Evans
 */
public class CookieLocaleResolverTests {
    @Test
    public void testResolveLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("LanguageKoekje", "nl");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("LanguageKoekje");
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals("nl", loc.getLanguage());
    }

    @Test
    public void testResolveLocaleContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("LanguageKoekje", "nl");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("LanguageKoekje");
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals("nl", loc.getLocale().getLanguage());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertNull(getTimeZone());
    }

    @Test
    public void testResolveLocaleContextWithTimeZone() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("LanguageKoekje", "nl GMT+1");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("LanguageKoekje");
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals("nl", loc.getLocale().getLanguage());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), getTimeZone());
    }

    @Test
    public void testResolveLocaleContextWithInvalidLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("LanguageKoekje", "++ GMT+1");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("LanguageKoekje");
        try {
            resolver.resolveLocaleContext(request);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("LanguageKoekje"));
            Assert.assertTrue(ex.getMessage().contains("++ GMT+1"));
        }
    }

    @Test
    public void testResolveLocaleContextWithInvalidLocaleOnErrorDispatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.GERMAN);
        request.setAttribute(ERROR_EXCEPTION_ATTRIBUTE, new ServletException());
        Cookie cookie = new Cookie("LanguageKoekje", "++ GMT+1");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultTimeZone(TimeZone.getTimeZone("GMT+2"));
        resolver.setCookieName("LanguageKoekje");
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals(Locale.GERMAN, loc.getLocale());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), getTimeZone());
    }

    @Test
    public void testResolveLocaleContextWithInvalidTimeZone() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("LanguageKoekje", "nl X-MT");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("LanguageKoekje");
        try {
            resolver.resolveLocaleContext(request);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("LanguageKoekje"));
            Assert.assertTrue(ex.getMessage().contains("nl X-MT"));
        }
    }

    @Test
    public void testResolveLocaleContextWithInvalidTimeZoneOnErrorDispatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(ERROR_EXCEPTION_ATTRIBUTE, new ServletException());
        Cookie cookie = new Cookie("LanguageKoekje", "nl X-MT");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultTimeZone(TimeZone.getTimeZone("GMT+2"));
        resolver.setCookieName("LanguageKoekje");
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals("nl", loc.getLocale().getLanguage());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+2"), getTimeZone());
    }

    @Test
    public void testSetAndResolveLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLocale(request, response, new Locale("nl", ""));
        Cookie cookie = response.getCookie(DEFAULT_COOKIE_NAME);
        Assert.assertNotNull(cookie);
        Assert.assertEquals(DEFAULT_COOKIE_NAME, cookie.getName());
        Assert.assertEquals(null, cookie.getDomain());
        Assert.assertEquals(DEFAULT_COOKIE_PATH, cookie.getPath());
        Assert.assertFalse(cookie.getSecure());
        request = new MockHttpServletRequest();
        request.setCookies(cookie);
        resolver = new CookieLocaleResolver();
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals("nl", loc.getLanguage());
    }

    @Test
    public void testSetAndResolveLocaleContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLocaleContext(request, response, new SimpleLocaleContext(new Locale("nl", "")));
        Cookie cookie = response.getCookie(DEFAULT_COOKIE_NAME);
        request = new MockHttpServletRequest();
        request.setCookies(cookie);
        resolver = new CookieLocaleResolver();
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals("nl", loc.getLocale().getLanguage());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertNull(getTimeZone());
    }

    @Test
    public void testSetAndResolveLocaleContextWithTimeZone() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLocaleContext(request, response, new SimpleTimeZoneAwareLocaleContext(new Locale("nl", ""), TimeZone.getTimeZone("GMT+1")));
        Cookie cookie = response.getCookie(DEFAULT_COOKIE_NAME);
        request = new MockHttpServletRequest();
        request.setCookies(cookie);
        resolver = new CookieLocaleResolver();
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals("nl", loc.getLocale().getLanguage());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), getTimeZone());
    }

    @Test
    public void testSetAndResolveLocaleContextWithTimeZoneOnly() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLocaleContext(request, response, new SimpleTimeZoneAwareLocaleContext(null, TimeZone.getTimeZone("GMT+1")));
        Cookie cookie = response.getCookie(DEFAULT_COOKIE_NAME);
        request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.GERMANY);
        request.setCookies(cookie);
        resolver = new CookieLocaleResolver();
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals(Locale.GERMANY, loc.getLocale());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), getTimeZone());
    }

    @Test
    public void testSetAndResolveLocaleWithCountry() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLocale(request, response, new Locale("de", "AT"));
        Cookie cookie = response.getCookie(DEFAULT_COOKIE_NAME);
        Assert.assertNotNull(cookie);
        Assert.assertEquals(DEFAULT_COOKIE_NAME, cookie.getName());
        Assert.assertEquals(null, cookie.getDomain());
        Assert.assertEquals(DEFAULT_COOKIE_PATH, cookie.getPath());
        Assert.assertFalse(cookie.getSecure());
        Assert.assertEquals("de-AT", cookie.getValue());
        request = new MockHttpServletRequest();
        request.setCookies(cookie);
        resolver = new CookieLocaleResolver();
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals("de", loc.getLanguage());
        Assert.assertEquals("AT", loc.getCountry());
    }

    @Test
    public void testSetAndResolveLocaleWithCountryAsLegacyJava() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLanguageTagCompliant(false);
        resolver.setLocale(request, response, new Locale("de", "AT"));
        Cookie cookie = response.getCookie(DEFAULT_COOKIE_NAME);
        Assert.assertNotNull(cookie);
        Assert.assertEquals(DEFAULT_COOKIE_NAME, cookie.getName());
        Assert.assertEquals(null, cookie.getDomain());
        Assert.assertEquals(DEFAULT_COOKIE_PATH, cookie.getPath());
        Assert.assertFalse(cookie.getSecure());
        Assert.assertEquals("de_AT", cookie.getValue());
        request = new MockHttpServletRequest();
        request.setCookies(cookie);
        resolver = new CookieLocaleResolver();
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals("de", loc.getLanguage());
        Assert.assertEquals("AT", loc.getCountry());
    }

    @Test
    public void testCustomCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("LanguageKoek");
        resolver.setCookieDomain(".springframework.org");
        resolver.setCookiePath("/mypath");
        resolver.setCookieMaxAge(10000);
        resolver.setCookieSecure(true);
        resolver.setLocale(request, response, new Locale("nl", ""));
        Cookie cookie = response.getCookie("LanguageKoek");
        Assert.assertNotNull(cookie);
        Assert.assertEquals("LanguageKoek", cookie.getName());
        Assert.assertEquals(".springframework.org", cookie.getDomain());
        Assert.assertEquals("/mypath", cookie.getPath());
        Assert.assertEquals(10000, cookie.getMaxAge());
        Assert.assertTrue(cookie.getSecure());
        request = new MockHttpServletRequest();
        request.setCookies(cookie);
        resolver = new CookieLocaleResolver();
        resolver.setCookieName("LanguageKoek");
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals("nl", loc.getLanguage());
    }

    @Test
    public void testResolveLocaleWithoutCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals(request.getLocale(), loc);
    }

    @Test
    public void testResolveLocaleContextWithoutCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals(request.getLocale(), loc.getLocale());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertNull(getTimeZone());
    }

    @Test
    public void testResolveLocaleWithoutCookieAndDefaultLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.GERMAN);
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals(Locale.GERMAN, loc);
    }

    @Test
    public void testResolveLocaleContextWithoutCookieAndDefaultLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.GERMAN);
        resolver.setDefaultTimeZone(TimeZone.getTimeZone("GMT+1"));
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals(Locale.GERMAN, loc.getLocale());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), getTimeZone());
    }

    @Test
    public void testResolveLocaleWithCookieWithoutLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        Cookie cookie = new Cookie(DEFAULT_COOKIE_NAME, "");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        Locale loc = resolver.resolveLocale(request);
        Assert.assertEquals(request.getLocale(), loc);
    }

    @Test
    public void testResolveLocaleContextWithCookieWithoutLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        Cookie cookie = new Cookie(DEFAULT_COOKIE_NAME, "");
        request.setCookies(cookie);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        LocaleContext loc = resolver.resolveLocaleContext(request);
        Assert.assertEquals(request.getLocale(), loc.getLocale());
        Assert.assertTrue((loc instanceof TimeZoneAwareLocaleContext));
        Assert.assertNull(getTimeZone());
    }

    @Test
    public void testSetLocaleToNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        Cookie cookie = new Cookie(DEFAULT_COOKIE_NAME, Locale.UK.toString());
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLocale(request, response, null);
        Locale locale = ((Locale) (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME)));
        Assert.assertEquals(Locale.TAIWAN, locale);
        Cookie[] cookies = response.getCookies();
        Assert.assertEquals(1, cookies.length);
        Cookie localeCookie = cookies[0];
        Assert.assertEquals(DEFAULT_COOKIE_NAME, localeCookie.getName());
        Assert.assertEquals("", localeCookie.getValue());
    }

    @Test
    public void testSetLocaleContextToNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        Cookie cookie = new Cookie(DEFAULT_COOKIE_NAME, Locale.UK.toString());
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLocaleContext(request, response, null);
        Locale locale = ((Locale) (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME)));
        Assert.assertEquals(Locale.TAIWAN, locale);
        TimeZone timeZone = ((TimeZone) (request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME)));
        Assert.assertNull(timeZone);
        Cookie[] cookies = response.getCookies();
        Assert.assertEquals(1, cookies.length);
        Cookie localeCookie = cookies[0];
        Assert.assertEquals(DEFAULT_COOKIE_NAME, localeCookie.getName());
        Assert.assertEquals("", localeCookie.getValue());
    }

    @Test
    public void testSetLocaleToNullWithDefault() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        Cookie cookie = new Cookie(DEFAULT_COOKIE_NAME, Locale.UK.toString());
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.CANADA_FRENCH);
        resolver.setLocale(request, response, null);
        Locale locale = ((Locale) (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME)));
        Assert.assertEquals(Locale.CANADA_FRENCH, locale);
        Cookie[] cookies = response.getCookies();
        Assert.assertEquals(1, cookies.length);
        Cookie localeCookie = cookies[0];
        Assert.assertEquals(DEFAULT_COOKIE_NAME, localeCookie.getName());
        Assert.assertEquals("", localeCookie.getValue());
    }

    @Test
    public void testSetLocaleContextToNullWithDefault() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        Cookie cookie = new Cookie(DEFAULT_COOKIE_NAME, Locale.UK.toString());
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.CANADA_FRENCH);
        resolver.setDefaultTimeZone(TimeZone.getTimeZone("GMT+1"));
        resolver.setLocaleContext(request, response, null);
        Locale locale = ((Locale) (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME)));
        Assert.assertEquals(Locale.CANADA_FRENCH, locale);
        TimeZone timeZone = ((TimeZone) (request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME)));
        Assert.assertEquals(TimeZone.getTimeZone("GMT+1"), timeZone);
        Cookie[] cookies = response.getCookies();
        Assert.assertEquals(1, cookies.length);
        Cookie localeCookie = cookies[0];
        Assert.assertEquals(DEFAULT_COOKIE_NAME, localeCookie.getName());
        Assert.assertEquals("", localeCookie.getValue());
    }
}

