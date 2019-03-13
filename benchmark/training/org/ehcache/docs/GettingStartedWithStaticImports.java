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
/**
 * CSOFF: AvoidStaticImport
 */
package org.ehcache.docs;


import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.junit.Test;


// CSON: AvoidStaticImport
/**
 * Samples to get started with Ehcache 3. Java 7 syntax
 */
@SuppressWarnings("unused")
public class GettingStartedWithStaticImports {
    @Test
    @SuppressWarnings("try")
    public void cachemanagerExample() {
        // tag::java7Example[]
        try (CacheManager cacheManager = // <2>
        // <1>
        CacheManagerBuilder.newCacheManagerBuilder().withCache("preConfigured", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10))).build(true)) {
            // <3>
            // Same code as before [...]
        }
        // end::java7Example[]
    }
}

