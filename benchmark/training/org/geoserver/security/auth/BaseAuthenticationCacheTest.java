/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.auth;


import junit.framework.TestCase;
import org.springframework.security.core.Authentication;


/**
 * Base Unit tests for AuthenticationCache implementations.
 */
public abstract class BaseAuthenticationCacheTest extends TestCase {
    protected static final int TIME_LIVE = 2;

    protected static final int TIME_IDLE = 1;

    protected static final int MAX_ENTRIES = 1000;

    protected static final String SAMPLE_CACHE_KEY = "SAMPLE_CACHE_KEY";

    protected static final String SAMPLE_FILTER = "SAMPLE_FILTER";

    protected static final String OTHER_CACHE_KEY = "WRONG_CACHE_KEY";

    AuthenticationCache cache;

    public void testWriteAndRead() {
        Authentication auth = putAuthenticationInCache();
        Authentication authenticationFromCache = cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY);
        TestCase.assertNotNull(authenticationFromCache);
        TestCase.assertEquals(auth, authenticationFromCache);
    }

    public void testExpireByAccess() throws InterruptedException {
        putAuthenticationInCache();
        Thread.sleep((((BaseAuthenticationCacheTest.TIME_IDLE) * 1000) / 2));
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        Thread.sleep((((BaseAuthenticationCacheTest.TIME_IDLE) + 1) * 1000));
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
    }

    public void testExpireByCreation() throws InterruptedException {
        putAuthenticationInCache();
        Thread.sleep((((BaseAuthenticationCacheTest.TIME_IDLE) * 1000) / 2));
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        Thread.sleep((((BaseAuthenticationCacheTest.TIME_IDLE) * 1000) / 2));
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        Thread.sleep((((BaseAuthenticationCacheTest.TIME_IDLE) * 1000) / 2));
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        Thread.sleep(((BaseAuthenticationCacheTest.TIME_LIVE) * 1000));
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
    }

    public void testRemoveAuthentication() {
        putAuthenticationInCache();
        cache.remove(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY);
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
    }

    public void testRemoveUnexistingAuthentication() {
        cache.remove(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.OTHER_CACHE_KEY);
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.OTHER_CACHE_KEY));
    }

    public void testRemoveAll() {
        putAuthenticationInCache();
        putOtherAuthenticationInCache();
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.OTHER_CACHE_KEY));
        cache.removeAll();
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.OTHER_CACHE_KEY));
    }

    public void testRemoveAllByFilter() {
        putAuthenticationInCache();
        putOtherAuthenticationInCache();
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        TestCase.assertNotNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.OTHER_CACHE_KEY));
        cache.removeAll(BaseAuthenticationCacheTest.SAMPLE_FILTER);
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.SAMPLE_CACHE_KEY));
        TestCase.assertNull(cache.get(BaseAuthenticationCacheTest.SAMPLE_FILTER, BaseAuthenticationCacheTest.OTHER_CACHE_KEY));
    }
}

