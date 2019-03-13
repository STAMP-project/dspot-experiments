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
package org.ehcache.clustered.client.docs;


import Timeouts.DEFAULT_OPERATION_TIMEOUT;
import Timeouts.INFINITE_TIMEOUT;
import java.net.URI;
import java.time.Duration;
import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.TimeoutsBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.junit.Test;


public class Resilience {
    @Test
    public void clusteredCacheManagerExample() throws Exception {
        // tag::timeoutsExample[]
        CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder().with(// <4>
        ClusteringServiceConfigurationBuilder.cluster(URI.create("terracotta://localhost/my-application")).timeouts(// <3>
        // <2>
        // <1>
        TimeoutsBuilder.timeouts().read(Duration.ofSeconds(10)).write(DEFAULT_OPERATION_TIMEOUT).connection(INFINITE_TIMEOUT)).autoCreate());
        // end::timeoutsExample[]
    }
}

