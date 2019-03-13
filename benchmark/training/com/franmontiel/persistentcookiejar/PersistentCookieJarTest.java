package com.franmontiel.persistentcookiejar;


import java.util.Collections;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by Francisco J. Montiel on 11/02/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PersistentCookieJarTest {
    private PersistentCookieJar persistentCookieJar;

    private HttpUrl url = HttpUrl.parse("https://domain.com/");

    /**
     * Test that the cookie is stored and also loaded when the a matching url is given
     */
    @Test
    public void regularCookie() {
        Cookie cookie = TestCookieCreator.createPersistentCookie(false);
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(cookie));
        List<Cookie> storedCookies = persistentCookieJar.loadForRequest(url);
        Assert.assertEquals(cookie, storedCookies.get(0));
    }

    /**
     * Test that a stored cookie is not loaded for a non matching url.
     */
    @Test
    public void differentUrlRequest() {
        Cookie cookie = TestCookieCreator.createPersistentCookie(false);
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(cookie));
        List<Cookie> storedCookies = persistentCookieJar.loadForRequest(HttpUrl.parse("https://otherdomain.com"));
        Assert.assertTrue(storedCookies.isEmpty());
    }

    /**
     * Test that when receiving a cookie equal(cookie-name, domain-value, and path-value) to one that is already stored then the old cookie is overwritten by the new one.
     */
    @Test
    public void updateCookie() {
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(TestCookieCreator.createPersistentCookie("name", "first")));
        Cookie newCookie = TestCookieCreator.createPersistentCookie("name", "last");
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(newCookie));
        List<Cookie> storedCookies = persistentCookieJar.loadForRequest(url);
        Assert.assertTrue(((storedCookies.size()) == 1));
        Assert.assertEquals(newCookie, storedCookies.get(0));
    }

    /**
     * Test that a expired cookie is not retrieved
     */
    @Test
    public void expiredCookie() {
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(TestCookieCreator.createExpiredCookie()));
        List<Cookie> cookies = persistentCookieJar.loadForRequest(url);
        Assert.assertTrue(cookies.isEmpty());
    }

    /**
     * Test that when receiving an expired cookie equal(cookie-name, domain-value, and path-value) to one that is already stored then the old cookie is overwritten by the new one.
     */
    @Test
    public void removeCookieWithExpiredOne() {
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(TestCookieCreator.createPersistentCookie(false)));
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(TestCookieCreator.createExpiredCookie()));
        Assert.assertTrue(persistentCookieJar.loadForRequest(url).isEmpty());
    }

    /**
     * Test that the session cookies are cleared without affecting to the persisted cookies
     */
    @Test
    public void clearSessionCookies() {
        Cookie persistentCookie = TestCookieCreator.createPersistentCookie(false);
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(persistentCookie));
        persistentCookieJar.saveFromResponse(url, Collections.singletonList(TestCookieCreator.createNonPersistentCookie()));
        persistentCookieJar.clearSession();
        Assert.assertTrue(((persistentCookieJar.loadForRequest(url).size()) == 1));
        Assert.assertEquals(persistentCookieJar.loadForRequest(url).get(0), persistentCookie);
    }
}

