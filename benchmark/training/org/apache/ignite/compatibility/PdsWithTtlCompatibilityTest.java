/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.compatibility;


import CacheAtomicityMode.TRANSACTIONAL;
import CacheWriteSynchronizationMode.FULL_SYNC;
import GridCacheAbstractFullApiSelfTest.LOCAL_IP_FINDER;
import WALMode.LOG_ONLY;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.compatibility.persistence.IgnitePersistenceCompatibilityAbstractTest;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.MemoryConfiguration;
import org.apache.ignite.configuration.PersistentStoreConfiguration;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;


/**
 * Test PendingTree upgrading to per-partition basis. Test fill cache with persistence enabled and with ExpirePolicy
 * configured on ignite-2.1 version and check if entries will be correctly expired when a new version node started.
 *
 * Note: Test for ignite-2.3 version will always fails due to entry ttl update fails with assertion on checkpoint lock
 * check.
 */
public class PdsWithTtlCompatibilityTest extends IgnitePersistenceCompatibilityAbstractTest {
    /**
     *
     */
    static final String TEST_CACHE_NAME = PdsWithTtlCompatibilityTest.class.getSimpleName();

    /**
     *
     */
    static final int DURATION_SEC = 10;

    /**
     *
     */
    private static final int ENTRIES_CNT = 100;

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeStartByOldVersionPersistenceData_2_1() throws Exception {
        doTestStartupWithOldVersion("2.1.0");
    }

    /**
     *
     */
    public static class ConfigurationClosure implements IgniteInClosure<IgniteConfiguration> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void apply(IgniteConfiguration cfg) {
            cfg.setLocalHost("127.0.0.1");
            TcpDiscoverySpi disco = new TcpDiscoverySpi();
            disco.setIpFinder(LOCAL_IP_FINDER);
            cfg.setDiscoverySpi(disco);
            cfg.setPeerClassLoadingEnabled(false);
            cfg.setMemoryConfiguration(new MemoryConfiguration().setDefaultMemoryPolicySize(((256L * 1024) * 1024)));
            cfg.setPersistentStoreConfiguration(new PersistentStoreConfiguration().setWalMode(LOG_ONLY).setCheckpointingPageBufferSize(((16L * 1024) * 1024)));
        }
    }

    /**
     *
     */
    public static class PostStartupClosure implements IgniteInClosure<Ignite> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void apply(Ignite ignite) {
            ignite.active(true);
            CacheConfiguration<Object, Object> cacheCfg = new CacheConfiguration();
            cacheCfg.setName(PdsWithTtlCompatibilityTest.TEST_CACHE_NAME);
            cacheCfg.setAtomicityMode(TRANSACTIONAL);
            cacheCfg.setBackups(1);
            cacheCfg.setWriteSynchronizationMode(FULL_SYNC);
            cacheCfg.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, PdsWithTtlCompatibilityTest.DURATION_SEC)));
            cacheCfg.setEagerTtl(true);
            cacheCfg.setGroupName("myGroup");
            IgniteCache<Object, Object> cache = ignite.createCache(cacheCfg);
            PdsWithTtlCompatibilityTest.saveCacheData(cache);
            ignite.active(false);
        }
    }
}

