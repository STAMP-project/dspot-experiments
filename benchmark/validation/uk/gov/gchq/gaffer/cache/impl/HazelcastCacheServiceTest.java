/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.cache.impl;


import CacheProperties.CACHE_CONFIG_FILE;
import java.util.Properties;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.cache.ICache;
import uk.gov.gchq.gaffer.cache.exception.CacheOperationException;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.exception.OverwritingException;


public class HazelcastCacheServiceTest {
    private static HazelcastCacheService service = new HazelcastCacheService();

    private Properties cacheProperties = new Properties();

    private static final String CACHE_NAME = "test";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    @Test
    public void shouldThrowAnExceptionWhenConfigFileIsMisConfigured() {
        HazelcastCacheServiceTest.service.shutdown();
        final String madeUpFile = "/made/up/file.xml";
        cacheProperties.setProperty(CACHE_CONFIG_FILE, madeUpFile);
        try {
            HazelcastCacheServiceTest.service.initialise(cacheProperties);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(madeUpFile));
        }
    }

    @Test
    public void shouldAllowUserToConfigureCacheUsingConfigFilePath() {
        // given
        initialiseWithTestConfig();
        // when
        ICache<String, Integer> cache = HazelcastCacheServiceTest.service.getCache(HazelcastCacheServiceTest.CACHE_NAME);
        // then
        Assert.assertEquals(0, cache.size());
    }

    @Test
    public void shouldReUseCacheIfOneExists() throws CacheOperationException {
        // given
        initialiseWithTestConfig();
        ICache<String, Integer> cache = HazelcastCacheServiceTest.service.getCache(HazelcastCacheServiceTest.CACHE_NAME);
        cache.put("key", 1);
        // when
        ICache<String, Integer> sameCache = HazelcastCacheServiceTest.service.getCache(HazelcastCacheServiceTest.CACHE_NAME);
        // then
        Assert.assertEquals(1, sameCache.size());
        Assert.assertEquals(new Integer(1), sameCache.get("key"));
    }

    @Test
    public void shouldShareCachesBetweenServices() throws CacheOperationException {
        // given
        initialiseWithTestConfig();
        HazelcastCacheService service1 = new HazelcastCacheService();
        service1.initialise(cacheProperties);
        // when
        service1.getCache(HazelcastCacheServiceTest.CACHE_NAME).put("Test", 2);
        // then
        Assume.assumeTrue("No caches found - probably due to error 'Network is unreachable'", (1 == (HazelcastCacheServiceTest.service.getCache(HazelcastCacheServiceTest.CACHE_NAME).size())));
        Assert.assertEquals(2, HazelcastCacheServiceTest.service.getCache(HazelcastCacheServiceTest.CACHE_NAME).get("Test"));
    }

    @Test
    public void shouldAddEntriesToCache() throws CacheOperationException {
        initialiseWithTestConfig();
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test", 1);
        Assert.assertEquals(((Integer) (1)), HazelcastCacheServiceTest.service.getFromCache(HazelcastCacheServiceTest.CACHE_NAME, "test"));
    }

    @Test
    public void shouldOnlyUpdateIfInstructed() throws CacheOperationException {
        initialiseWithTestConfig();
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test", 1);
        try {
            HazelcastCacheServiceTest.service.putSafeInCache(HazelcastCacheServiceTest.CACHE_NAME, "test", 2);
            Assert.fail("Expected an exception");
        } catch (final OverwritingException e) {
            Assert.assertEquals(((Integer) (1)), HazelcastCacheServiceTest.service.getFromCache(HazelcastCacheServiceTest.CACHE_NAME, "test"));
        }
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test", 2);
        Assert.assertEquals(((Integer) (2)), HazelcastCacheServiceTest.service.getFromCache(HazelcastCacheServiceTest.CACHE_NAME, "test"));
    }

    @Test
    public void shouldBeAbleToDeleteCacheEntries() throws CacheOperationException {
        initialiseWithTestConfig();
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test", 1);
        HazelcastCacheServiceTest.service.removeFromCache(HazelcastCacheServiceTest.CACHE_NAME, "test");
        Assert.assertEquals(0, HazelcastCacheServiceTest.service.sizeOfCache(HazelcastCacheServiceTest.CACHE_NAME));
    }

    @Test
    public void shouldBeAbleToClearCache() throws CacheOperationException {
        initialiseWithTestConfig();
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test1", 1);
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test2", 2);
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test3", 3);
        HazelcastCacheServiceTest.service.clearCache(HazelcastCacheServiceTest.CACHE_NAME);
        Assert.assertEquals(0, HazelcastCacheServiceTest.service.sizeOfCache(HazelcastCacheServiceTest.CACHE_NAME));
    }

    @Test
    public void shouldGetAllKeysFromCache() throws CacheOperationException {
        initialiseWithTestConfig();
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test1", 1);
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test2", 2);
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test3", 3);
        Assert.assertEquals(3, HazelcastCacheServiceTest.service.sizeOfCache(HazelcastCacheServiceTest.CACHE_NAME));
        Assert.assertThat(HazelcastCacheServiceTest.service.getAllKeysFromCache(HazelcastCacheServiceTest.CACHE_NAME), IsCollectionContaining.hasItems("test1", "test2", "test3"));
    }

    @Test
    public void shouldGetAllValues() throws CacheOperationException {
        initialiseWithTestConfig();
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test1", 1);
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test2", 2);
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "test3", 3);
        HazelcastCacheServiceTest.service.putInCache(HazelcastCacheServiceTest.CACHE_NAME, "duplicate", 3);
        Assert.assertEquals(4, HazelcastCacheServiceTest.service.sizeOfCache(HazelcastCacheServiceTest.CACHE_NAME));
        Assert.assertEquals(4, HazelcastCacheServiceTest.service.getAllValuesFromCache(HazelcastCacheServiceTest.CACHE_NAME).size());
        Assert.assertThat(HazelcastCacheServiceTest.service.getAllValuesFromCache(HazelcastCacheServiceTest.CACHE_NAME), IsCollectionContaining.hasItems(1, 2, 3));
    }
}

