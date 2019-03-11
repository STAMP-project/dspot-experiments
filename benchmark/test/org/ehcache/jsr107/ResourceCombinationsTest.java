/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.jsr107;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.cache.Cache;
import javax.cache.CacheManager;
import org.ehcache.config.Builder;
import org.ehcache.config.Configuration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ResourceCombinationsTest {
    private final ResourcePools resources;

    @Rule
    public final TemporaryFolder diskPath = new TemporaryFolder();

    public ResourceCombinationsTest(Builder<? extends ResourcePools> resources) {
        this.resources = resources.build();
    }

    @Test
    public void testBasicCacheOperation() throws IOException, URISyntaxException {
        Configuration config = new DefaultConfiguration(ResourceCombinationsTest.class.getClassLoader(), new DefaultPersistenceConfiguration(diskPath.newFolder()));
        try (CacheManager cacheManager = new EhcacheCachingProvider().getCacheManager(URI.create("dummy"), config)) {
            Cache<String, String> cache = cacheManager.createCache("test", Eh107Configuration.fromEhcacheCacheConfiguration(CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resources)));
            cache.put("foo", "bar");
            Assert.assertThat(cache.get("foo"), Is.is("bar"));
        }
    }
}

