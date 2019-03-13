/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.auth;


import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


public class LRUAuthenticationCacheTest extends BaseAuthenticationCacheTest {
    @Test
    public void testLRUCache() {
        LRUCache<String, String> cache = new LRUCache<String, String>(3);
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4");
        TestCase.assertEquals("value2", cache.get("key2"));
        TestCase.assertEquals("value3", cache.get("key3"));
        TestCase.assertEquals("value4", cache.get("key4"));
        TestCase.assertNull(cache.get("key1"));
    }

    @Test
    public void testAuthenticationKey() {
        AuthenticationCacheKey key11 = new AuthenticationCacheKey("f1", "k1");
        TestCase.assertTrue(key11.equals(key11));
        TestCase.assertTrue(((key11.hashCode()) != 0));
        AuthenticationCacheKey key12 = new AuthenticationCacheKey("f1", "k2");
        TestCase.assertFalse(key11.equals(key12));
        TestCase.assertFalse(((key11.hashCode()) == (key12.hashCode())));
        AuthenticationCacheKey key21 = new AuthenticationCacheKey("f2", "k1");
        TestCase.assertFalse(key11.equals(key21));
        TestCase.assertFalse(((key11.hashCode()) == (key21.hashCode())));
        AuthenticationCacheKey key22 = new AuthenticationCacheKey("f12", "k2");
        TestCase.assertFalse(key11.equals(key22));
        TestCase.assertFalse(((key11.hashCode()) == (key22.hashCode())));
    }

    @Test
    public void testAuthenticationEntry() {
        UsernamePasswordAuthenticationToken t1 = new UsernamePasswordAuthenticationToken("user1", "password1");
        AuthenticationCacheEntry entry1 = new AuthenticationCacheEntry(t1, 10, 10);
        TestCase.assertTrue(((entry1.hashCode()) != 0));
        TestCase.assertEquals(t1.hashCode(), entry1.hashCode());
        TestCase.assertTrue(entry1.equals(entry1));
        AuthenticationCacheEntry entry1_1 = new AuthenticationCacheEntry(t1, 20, 20);
        TestCase.assertEquals(t1.hashCode(), entry1_1.hashCode());
        TestCase.assertTrue(entry1.equals(entry1_1));
        UsernamePasswordAuthenticationToken t2 = new UsernamePasswordAuthenticationToken("user2", "password2");
        AuthenticationCacheEntry entry2 = new AuthenticationCacheEntry(t2, 5, 10);
        // assertFalse(entry2.hashCode()==entry1.hashCode());
        TestCase.assertFalse(entry2.equals(entry1));
        long currentTime = entry2.getCreated();
        // check live time
        entry2.setLastAccessed((currentTime + 6000));
        TestCase.assertFalse(entry2.hasExpired((currentTime + (10 * 1000))));
        TestCase.assertTrue(entry2.hasExpired(((currentTime + (10 * 1000)) + 1)));
        // check idle time
        entry2.setLastAccessed((currentTime + 2000));
        TestCase.assertFalse(entry2.hasExpired((currentTime + 7000)));
        TestCase.assertTrue(entry2.hasExpired((currentTime + 70001)));
    }

    @Test
    public void testLRUAuthenticationCache() {
        // test max entries
        LRUAuthenticationCacheImpl cache = new LRUAuthenticationCacheImpl(5, 10, 3);
        fillCache(cache);
        UsernamePasswordAuthenticationToken token;
        TestCase.assertNull(cache.get("filtera", "key1"));
        TestCase.assertNotNull((token = ((UsernamePasswordAuthenticationToken) (cache.get("filtera", "key2")))));
        TestCase.assertEquals("user2", token.getPrincipal());
        TestCase.assertNotNull((token = ((UsernamePasswordAuthenticationToken) (cache.get("filterb", "key3")))));
        TestCase.assertEquals("user3", token.getPrincipal());
        TestCase.assertNotNull((token = ((UsernamePasswordAuthenticationToken) (cache.get("filterb", "key4")))));
        TestCase.assertEquals("user4", token.getPrincipal());
        // test remove all
        cache = new LRUAuthenticationCacheImpl(5, 10, 4);
        fillCache(cache);
        cache.removeAll();
        TestCase.assertNull(cache.get("filtera", "key1"));
        TestCase.assertNull(cache.get("filtera", "key2"));
        TestCase.assertNull(cache.get("filterb", "key3"));
        TestCase.assertNull(cache.get("filterb", "key4"));
        // test remove filter
        cache = new LRUAuthenticationCacheImpl(5, 10, 4);
        fillCache(cache);
        cache.removeAll("filtera");
        TestCase.assertNull(cache.get("filtera", "key1"));
        TestCase.assertNull(cache.get("filtera", "key2"));
        TestCase.assertNotNull(cache.get("filterb", "key3"));
        TestCase.assertNotNull(cache.get("filterb", "key4"));
        // test remove one entry
        cache = new LRUAuthenticationCacheImpl(5, 10, 4);
        fillCache(cache);
        cache.remove("filtera", "key1");
        TestCase.assertNull(cache.get("filtera", "key1"));
        TestCase.assertNotNull(cache.get("filtera", "key2"));
        TestCase.assertNotNull(cache.get("filterb", "key3"));
        TestCase.assertNotNull(cache.get("filterb", "key4"));
        // test remove non existing
        cache = new LRUAuthenticationCacheImpl(5, 10, 4);
        fillCache(cache);
        cache.removeAll("filterz");
        cache.remove("filterz", "key999");
        TestCase.assertNotNull(cache.get("filtera", "key1"));
        TestCase.assertNotNull(cache.get("filtera", "key2"));
        TestCase.assertNotNull(cache.get("filterb", "key3"));
        TestCase.assertNotNull(cache.get("filterb", "key4"));
        // test default live time
        cache = new LRUAuthenticationCacheImpl(5, 0, 4);
        fillCache(cache);
        waitForMilliSecs(10);
        TestCase.assertNull(cache.get("filtera", "key1"));
        TestCase.assertNull(cache.get("filtera", "key2"));
        TestCase.assertNull(cache.get("filterb", "key3"));
        TestCase.assertNull(cache.get("filterb", "key4"));
        // test default idle time
        cache = new LRUAuthenticationCacheImpl(0, 10, 4);
        fillCache(cache);
        waitForMilliSecs(10);
        TestCase.assertNull(cache.get("filtera", "key1"));
        TestCase.assertNull(cache.get("filtera", "key2"));
        TestCase.assertNull(cache.get("filterb", "key3"));
        TestCase.assertNull(cache.get("filterb", "key4"));
        cache = new LRUAuthenticationCacheImpl(1, 10, 4);
        fillCache(cache);
        waitForMilliSecs(1);
        TestCase.assertNotNull(cache.get("filtera", "key1"));
        TestCase.assertNotNull(cache.get("filtera", "key2"));
        TestCase.assertNotNull(cache.get("filterb", "key3"));
        TestCase.assertNotNull(cache.get("filterb", "key4"));
        waitForMilliSecs(1500);
        TestCase.assertNull(cache.get("filtera", "key1"));
        TestCase.assertNull(cache.get("filtera", "key2"));
        TestCase.assertNull(cache.get("filterb", "key3"));
        TestCase.assertNull(cache.get("filterb", "key4"));
    }
}

