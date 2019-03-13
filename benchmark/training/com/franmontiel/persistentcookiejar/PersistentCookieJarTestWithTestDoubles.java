package com.franmontiel.persistentcookiejar;


import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import okhttp3.Cookie;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * Created by Francisco J. Montiel on 11/02/16.
 */
public class PersistentCookieJarTestWithTestDoubles {
    @Test
    public void saveFromResponse_WithPersistentCookie_ShouldSaveCookieInSessionAndPersistence() {
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        CookiePersistor cookiePersistor = Mockito.mock(CookiePersistor.class);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, cookiePersistor);
        List<Cookie> responseCookies = Collections.singletonList(TestCookieCreator.createPersistentCookie(false));
        persistentCookieJar.saveFromResponse(TestCookieCreator.DEFAULT_URL, responseCookies);
        ArgumentCaptor<Collection<Cookie>> cookieCacheArgCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(cookieCache, Mockito.atLeastOnce()).addAll(cookieCacheArgCaptor.capture());
        Assert.assertEquals(responseCookies.get(0), cookieCacheArgCaptor.getValue().iterator().next());
        ArgumentCaptor<Collection<Cookie>> cookiePersistorArgCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(cookiePersistor, Mockito.times(1)).saveAll(cookiePersistorArgCaptor.capture());
        Assert.assertEquals(responseCookies.get(0), cookieCacheArgCaptor.getValue().iterator().next());
    }

    @Test
    public void saveFromResponse_WithNonPersistentCookie_ShouldSaveCookieOnlyInSession() {
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        CookiePersistor cookiePersistor = Mockito.mock(CookiePersistor.class);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, cookiePersistor);
        final List<Cookie> responseCookies = Collections.singletonList(TestCookieCreator.createNonPersistentCookie());
        persistentCookieJar.saveFromResponse(TestCookieCreator.DEFAULT_URL, responseCookies);
        ArgumentCaptor<Collection<Cookie>> cookieCacheArgCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(cookieCache, Mockito.atLeastOnce()).addAll(cookieCacheArgCaptor.capture());
        Assert.assertEquals(responseCookies.get(0), cookieCacheArgCaptor.getValue().iterator().next());
        ArgumentCaptor<Collection<Cookie>> cookiePersistorArgCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(cookiePersistor, Mockito.atLeast(0)).saveAll(cookiePersistorArgCaptor.capture());
        // Method was not called OR Method called with empty collection
        Assert.assertTrue(((cookiePersistorArgCaptor.getAllValues().isEmpty()) || (cookiePersistorArgCaptor.getValue().isEmpty())));
    }

    @Test
    public void loadForRequest_WithMatchingUrl_ShouldReturnMatchingCookies() {
        Cookie savedCookie = TestCookieCreator.createNonPersistentCookie();
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        Iterator<Cookie> iterator = Mockito.mock(Iterator.class);
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        Mockito.when(iterator.next()).thenReturn(savedCookie);
        Mockito.when(cookieCache.iterator()).thenReturn(iterator);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, Mockito.mock(CookiePersistor.class));
        List<Cookie> requestCookies = persistentCookieJar.loadForRequest(TestCookieCreator.DEFAULT_URL);
        Assert.assertEquals(savedCookie, requestCookies.get(0));
    }

    @Test
    public void loadForRequest_WithNonMatchingUrl_ShouldReturnEmptyCookieList() {
        Cookie savedCookie = TestCookieCreator.createNonPersistentCookie();
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        Iterator<Cookie> iterator = Mockito.mock(Iterator.class);
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        Mockito.when(iterator.next()).thenReturn(savedCookie);
        Mockito.when(cookieCache.iterator()).thenReturn(iterator);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, Mockito.mock(CookiePersistor.class));
        List<Cookie> requestCookies = persistentCookieJar.loadForRequest(TestCookieCreator.OTHER_URL);
        Assert.assertTrue(requestCookies.isEmpty());
    }

    @Test
    public void loadForRequest_WithExpiredCookieMatchingUrl_ShouldReturnEmptyCookieList() {
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        Iterator<Cookie> iterator = Mockito.mock(Iterator.class);
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        Mockito.when(iterator.next()).thenReturn(TestCookieCreator.createExpiredCookie());
        Mockito.when(cookieCache.iterator()).thenReturn(iterator);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, Mockito.mock(CookiePersistor.class));
        List<Cookie> cookies = persistentCookieJar.loadForRequest(TestCookieCreator.DEFAULT_URL);
        Assert.assertTrue(cookies.isEmpty());
    }

    @Test
    public void loadForRequest_WithExpiredCookieMatchingUrl_ShouldRemoveTheCookie() {
        Cookie savedCookie = TestCookieCreator.createExpiredCookie();
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        Iterator<Cookie> iterator = Mockito.mock(Iterator.class);
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        Mockito.when(iterator.next()).thenReturn(savedCookie);
        Mockito.when(cookieCache.iterator()).thenReturn(iterator);
        CookiePersistor cookiePersistor = Mockito.mock(CookiePersistor.class);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, cookiePersistor);
        persistentCookieJar.loadForRequest(TestCookieCreator.DEFAULT_URL);
        Mockito.verify(iterator, Mockito.times(1)).remove();
        ArgumentCaptor<Collection<Cookie>> cookiePersistorArgCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(cookiePersistor).removeAll(cookiePersistorArgCaptor.capture());
        Assert.assertEquals(savedCookie, cookiePersistorArgCaptor.getValue().iterator().next());
    }

    @Test
    public void clearSession_ShouldClearOnlySessionCookies() {
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        CookiePersistor cookiePersistor = Mockito.mock(CookiePersistor.class);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, cookiePersistor);
        persistentCookieJar.clearSession();
        Mockito.verify(cookieCache, Mockito.times(1)).clear();
        Mockito.verify(cookiePersistor, Mockito.times(2)).loadAll();
    }

    @Test
    public void clear_ShouldClearAllCookies() {
        CookieCache cookieCache = Mockito.mock(CookieCache.class);
        CookiePersistor cookiePersistor = Mockito.mock(CookiePersistor.class);
        PersistentCookieJar persistentCookieJar = new PersistentCookieJar(cookieCache, cookiePersistor);
        persistentCookieJar.clear();
        Mockito.verify(cookieCache, Mockito.times(1)).clear();
        Mockito.verify(cookiePersistor, Mockito.times(1)).clear();
    }
}

