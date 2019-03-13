/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.session;


import NinjaConstant.applicationCookieDomain;
import NinjaConstant.applicationCookiePrefix;
import NinjaConstant.applicationSecret;
import NinjaConstant.sessionExpireTimeInSeconds;
import NinjaConstant.sessionHttpOnly;
import NinjaConstant.sessionTransferredOverHttpsOnly;
import Session.EXPIRY_TIME_KEY;
import Session.TIMESTAMP_KEY;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.utils.Clock;
import ninja.utils.CookieEncryption;
import ninja.utils.Crypto;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.SecretGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Session.AUTHENTICITY_KEY;
import static Session.ID_KEY;


@RunWith(Parameterized.class)
public class SessionImplTest {
    @Mock
    private Context context;

    @Mock
    private Result result;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private Crypto crypto;

    private CookieEncryption encryption;

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Clock clock;

    @Parameterized.Parameter
    public boolean encrypted;

    @Test
    public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndOnlySentWhenChanged() {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        // put nothing => empty session will not be sent as we send only changed
        // stuff...
        sessionCookie.save(context);
        // no cookie should be set as the flash scope is empty...:
        Mockito.verify(context, Mockito.never()).addCookie(Matchers.any(Cookie.class));
    }

    @Test
    public void testSessionCookieSettingWorks() throws Exception {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("hello", "session!");
        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);
        // a cookie will be set
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // verify some stuff on the set cookie
        Assert.assertEquals("NINJA_SESSION", cookieCaptor.getValue().getName());
        // assert some stuff...
        // Make sure that sign is valid:
        String cookieString = cookieCaptor.getValue().getValue();
        String cookieFromSign = cookieString.substring(((cookieString.indexOf("-")) + 1));
        String computedSign = crypto.signHmacSha1(cookieFromSign);
        Assert.assertEquals(computedSign, cookieString.substring(0, cookieString.indexOf("-")));
        if (encrypted) {
            cookieFromSign = encryption.decrypt(cookieFromSign);
        }
        // Make sure that cookie contains timestamp
        Assert.assertTrue(cookieFromSign.contains(TIMESTAMP_KEY));
    }

    @Test
    public void testHttpsOnlyWorks() throws Exception {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("hello", "session!");
        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);
        // a cookie will be set
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // verify some stuff on the set cookie
        Assert.assertEquals(true, cookieCaptor.getValue().isSecure());
    }

    @Test
    public void testNoHttpsOnlyWorks() throws Exception {
        // setup this testmethod
        Mockito.when(ninjaProperties.getBooleanWithDefault(sessionTransferredOverHttpsOnly, true)).thenReturn(false);
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("hello", "session!");
        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);
        // a cookie will be set
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // verify some stuff on the set cookie
        Assert.assertEquals(false, cookieCaptor.getValue().isSecure());
    }

    @Test
    public void testHttpOnlyWorks() throws Exception {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("hello", "session!");
        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);
        // a cookie will be set
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // verify some stuff on the set cookie
        Assert.assertEquals(true, cookieCaptor.getValue().isHttpOnly());
    }

    @Test
    public void testNoHttpOnlyWorks() throws Exception {
        // setup this testmethod
        Mockito.when(ninjaProperties.getBooleanWithDefault(sessionHttpOnly, true)).thenReturn(false);
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("hello", "session!");
        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);
        // a cookie will be set
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // verify some stuff on the set cookie
        Assert.assertEquals(false, cookieCaptor.getValue().isHttpOnly());
    }

    @Test
    public void testThatCookieSavingAndInitingWorks() {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("key1", "value1");
        sessionCookie.put("key2", "value2");
        sessionCookie.put("key3", "value3");
        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);
        // a cookie will be set
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        // now we simulate a new request => the session storage will generate a
        // new cookie:
        Cookie newSessionCookie = Cookie.builder(cookieCaptor.getValue().getName(), cookieCaptor.getValue().getValue()).build();
        // that will be returned by the httprequest...
        Mockito.when(context.getCookie(cookieCaptor.getValue().getName())).thenReturn(newSessionCookie);
        // init new session from that cookie:
        Session sessionCookie2 = createNewSession();
        sessionCookie2.init(context);
        Assert.assertEquals("value1", sessionCookie2.get("key1"));
        Assert.assertEquals("value2", sessionCookie2.get("key2"));
        Assert.assertEquals("value3", sessionCookie2.get("key3"));
    }

    @Test
    public void testThatCorrectMethodOfNinjaPropertiesIsUsedSoThatStuffBreaksWhenPropertyIsAbsent() {
        // we did not set the cookie prefix
        Mockito.when(ninjaProperties.getOrDie(applicationCookiePrefix)).thenReturn(null);
        // stuff must break => ...
        Session sessionCookie = createNewSession();
        Mockito.verify(ninjaProperties).getOrDie(applicationCookiePrefix);
    }

    @Test
    public void testSessionCookieDelete() {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        final String key = "mykey";
        final String value = "myvalue";
        sessionCookie.put(key, value);
        // value should have been set:
        Assert.assertEquals(value, sessionCookie.get(key));
        // value should be returned when removing:
        Assert.assertEquals(value, sessionCookie.remove(key));
        // after removing, value should not be there anymore:
        Assert.assertNull(sessionCookie.get(key));
    }

    @Test
    public void testGetAuthenticityTokenWorks() {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        String authenticityToken = sessionCookie.getAuthenticityToken();
        String cookieValueWithoutSign = captureFinalCookie(sessionCookie);
        // verify that the authenticity token is set
        Assert.assertTrue(cookieValueWithoutSign.contains((((AUTHENTICITY_KEY) + "=") + authenticityToken)));
        // also make sure the timestamp is there:
        Assert.assertTrue(cookieValueWithoutSign.contains(TIMESTAMP_KEY));
    }

    @Test
    public void testGetIdTokenWorks() {
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        String idToken = sessionCookie.getId();
        String valueWithoutSign = captureFinalCookie(sessionCookie);
        // verify that the id token is set:
        Assert.assertTrue(valueWithoutSign.contains((((ID_KEY) + "=") + idToken)));
        // also make sure the timestamp is there:
        Assert.assertTrue(valueWithoutSign.contains(TIMESTAMP_KEY));
    }

    @Test
    public void testThatCookieUsesContextPath() {
        Mockito.when(context.getContextPath()).thenReturn("/my_context");
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("anykey", "anyvalue");
        sessionCookie.save(context);
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        Assert.assertThat(cookie.getPath(), CoreMatchers.equalTo("/my_context/"));
    }

    @Test
    public void testExpiryTime() {
        // 1. Check that session is still saved when expiry time is set
        Session sessionCookie1 = createNewSession();
        sessionCookie1.init(context);
        sessionCookie1.put("a", "2");
        sessionCookie1.setExpiryTime((10 * 1000L));
        Assert.assertThat(sessionCookie1.get("a"), CoreMatchers.equalTo("2"));
        sessionCookie1.save(context);
        Session sessionCookie2 = roundTrip(sessionCookie1);
        Assert.assertThat(sessionCookie2.get("a"), CoreMatchers.equalTo("2"));
        // 2. Check that session is invalidated when past the expiry time
        // Set the current time past when it is called.
        Mockito.when(clock.currentTimeMillis()).thenReturn(((System.currentTimeMillis()) + (11 * 1000L)));
        Session sessionCookie3 = roundTrip(sessionCookie2);
        Assert.assertNull(sessionCookie3.get("a"));
    }

    @Test
    public void testExpiryTimeRoundTrip() {
        // Round trip the session cookie with an expiry time in the future
        // Then remove the expiration time to make sure it is still valid
        Mockito.when(ninjaProperties.getInteger(sessionExpireTimeInSeconds)).thenReturn(null);
        Session sessionCookie1 = createNewSession();
        sessionCookie1.init(context);
        sessionCookie1.put("a", "2");
        sessionCookie1.setExpiryTime((10 * 1000L));
        Assert.assertThat(sessionCookie1.get("a"), CoreMatchers.equalTo("2"));
        Session sessionCookie2 = roundTrip(sessionCookie1);
        Assert.assertThat(sessionCookie2.get("a"), CoreMatchers.equalTo("2"));
        Assert.assertThat(sessionCookie2.get(EXPIRY_TIME_KEY), CoreMatchers.equalTo("10000"));
        sessionCookie2.setExpiryTime(null);
        Session sessionCookie3 = roundTrip(sessionCookie2);
        Assert.assertNull(sessionCookie3.get(EXPIRY_TIME_KEY));
    }

    @Test
    public void testThatCookieDoesNotUseApplicationDomainWhenNotSet() {
        Mockito.when(ninjaProperties.get(applicationCookieDomain)).thenReturn(null);
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("anykey", "anyvalue");
        sessionCookie.save(context);
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        Assert.assertThat(cookie.getDomain(), CoreMatchers.equalTo(null));
    }

    @Test
    public void testThatCookieUseApplicationDomain() {
        Mockito.when(ninjaProperties.get(applicationCookieDomain)).thenReturn("domain.com");
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("anykey", "anyvalue");
        sessionCookie.save(context);
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        Assert.assertThat(cookie.getDomain(), CoreMatchers.equalTo("domain.com"));
    }

    @Test
    public void testThatCookieClearWorks() {
        String applicationCookieName = (ninjaProperties.getOrDie(applicationCookiePrefix)) + (NinjaConstant.SESSION_SUFFIX);
        // First roundtrip
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("anykey", "anyvalue");
        Session sessionCookieWithValues = roundTrip(sessionCookie);
        // Second roundtrip with cleared session
        sessionCookieWithValues.clear();
        Mockito.when(context.hasCookie(applicationCookieName)).thenReturn(true);
        // Third roundtrip
        String cookieValue = captureFinalCookie(sessionCookieWithValues);
        Assert.assertThat(cookieValue, CoreMatchers.not(CoreMatchers.containsString("anykey")));
        Assert.assertThat(cookieCaptor.getValue().getDomain(), CoreMatchers.equalTo(null));
        Assert.assertThat(cookieCaptor.getValue().getMaxAge(), CoreMatchers.equalTo(0));
    }

    @Test
    public void testThatCookieClearWorksWithApplicationDomain() {
        String applicationCookieName = (ninjaProperties.getOrDie(applicationCookiePrefix)) + (NinjaConstant.SESSION_SUFFIX);
        Mockito.when(ninjaProperties.get(applicationCookieDomain)).thenReturn("domain.com");
        // First roundtrip
        Session sessionCookie = createNewSession();
        sessionCookie.init(context);
        sessionCookie.put("anykey", "anyvalue");
        Session sessionCookieWithValues = roundTrip(sessionCookie);
        // Second roundtrip with cleared session
        sessionCookieWithValues.clear();
        Mockito.when(context.hasCookie(applicationCookieName)).thenReturn(true);
        // Third roundtrip
        String cookieValue = captureFinalCookie(sessionCookieWithValues);
        Assert.assertThat(cookieValue, CoreMatchers.not(CoreMatchers.containsString("anykey")));
        Assert.assertThat(cookieCaptor.getValue().getDomain(), CoreMatchers.equalTo("domain.com"));
        Assert.assertThat(cookieCaptor.getValue().getMaxAge(), CoreMatchers.equalTo(0));
    }

    @Test
    public void testSessionEncryptionKeysMismatch() {
        if (!(encrypted)) {
            Assert.assertTrue("N/A for plain session cookies without encryption", true);
            return;
        }
        // (1) create session with some data and save
        Session session_1 = createNewSession();
        session_1.init(context);
        session_1.put("key", "value");
        session_1.save(context);
        // (2) verify that cookie with our data is created and added to context
        Mockito.verify(context).addCookie(cookieCaptor.capture());
        Assert.assertEquals("value", session_1.get("key"));
        // save reference to our cookie - we will use it to init sessions below
        Cookie cookie = cookieCaptor.getValue();
        // (3) create new session with the same cookie and assert that it still has our data
        Session session_2 = createNewSession();
        Mockito.when(context.getCookie("NINJA_SESSION")).thenReturn(cookie);
        session_2.init(context);
        Assert.assertFalse(session_2.isEmpty());
        Assert.assertEquals("value", session_2.get("key"));
        // (4) now we change our application secret and thus our encryption key is modified
        Mockito.when(ninjaProperties.getOrDie(applicationSecret)).thenReturn(SecretGenerator.generateSecret());
        encryption = new CookieEncryption(ninjaProperties);
        // (5) creating new session with the same cookie above would result in clean session
        // because that cookie was encrypted with another key and decryption with the new key
        // is not possible; usually such a case throws `javax.crypto.BadPaddingException`
        Session session_3 = createNewSession();
        session_3.init(context);
        Assert.assertTrue(session_3.isEmpty());
    }
}

