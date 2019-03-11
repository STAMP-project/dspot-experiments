/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.compatibility.persistence;


import CacheAtomicityMode.TRANSACTIONAL;
import CacheWriteSynchronizationMode.FULL_SYNC;
import GridCacheAbstractFullApiSelfTest.LOCAL_IP_FINDER;
import WALMode.LOG_ONLY;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;


/**
 * Saves data using previous version of ignite and then load this data using actual version
 */
public class MigratingToWalV2SerializerWithCompactionTest extends IgnitePersistenceCompatibilityAbstractTest {
    /**
     *
     */
    private static final String TEST_CACHE_NAME = MigratingToWalV2SerializerWithCompactionTest.class.getSimpleName();

    /**
     * Entries count.
     */
    private static final int ENTRIES = 300;

    /**
     * Wal segment size.
     */
    private static final int WAL_SEGMENT_SIZE = 1024 * 1024;

    /**
     * Entry payload size.
     */
    private static final int PAYLOAD_SIZE = 20000;

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCompactingOldWalFiles() throws Exception {
        doTestStartupWithOldVersion("2.3.0");
    }

    /**
     *
     */
    private static class PostStartupClosure implements IgniteInClosure<Ignite> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void apply(Ignite ignite) {
            ignite.active(true);
            CacheConfiguration<Object, Object> cacheCfg = new CacheConfiguration();
            cacheCfg.setName(MigratingToWalV2SerializerWithCompactionTest.TEST_CACHE_NAME);
            cacheCfg.setAtomicityMode(TRANSACTIONAL);
            cacheCfg.setBackups(0);
            cacheCfg.setWriteSynchronizationMode(FULL_SYNC);
            IgniteCache<Object, Object> cache = ignite.getOrCreateCache(cacheCfg);
            for (int i = 0; i < (MigratingToWalV2SerializerWithCompactionTest.ENTRIES); i++) {
                // At least 20MB of raw data in total.
                final byte[] val = new byte[20000];
                ThreadLocalRandom.current().nextBytes(val);
                val[i] = 1;
                cache.put(i, val);
            }
        }
    }

    /**
     *
     */
    private static class ConfigurationClosure implements IgniteInClosure<IgniteConfiguration> {
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
            DataStorageConfiguration memCfg = new DataStorageConfiguration().setDefaultDataRegionConfiguration(new DataRegionConfiguration().setPersistenceEnabled(true)).setWalSegmentSize(MigratingToWalV2SerializerWithCompactionTest.WAL_SEGMENT_SIZE).setWalMode(LOG_ONLY).setWalHistorySize(100);
            cfg.setDataStorageConfiguration(memCfg);
        }
    }
}

