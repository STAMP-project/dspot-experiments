/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.compatibility.persistence;


import GridCacheAbstractFullApiSelfTest.LOCAL_IP_FINDER;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import static org.apache.ignite.compatibility.persistence.PersistenceBasicCompatibilityTest.TestEnum.A;
import static org.apache.ignite.compatibility.persistence.PersistenceBasicCompatibilityTest.TestEnum.B;
import static org.apache.ignite.compatibility.persistence.PersistenceBasicCompatibilityTest.TestEnum.C;


/**
 * Test for new and old style persistent storage folders generation and compatible startup of current ignite version
 */
public class FoldersReuseCompatibilityTest extends IgnitePersistenceCompatibilityAbstractTest {
    /**
     * Cache name for test.
     */
    private static final String CACHE_NAME = "dummy";

    /**
     * Key to store in previous version of ignite
     */
    private static final String KEY = "StringFromPrevVersion";

    /**
     * Value to store in previous version of ignite
     */
    private static final String VAL = "ValueFromPrevVersion";

    /**
     * Key to store in previous version of ignite
     */
    private static final String KEY_OBJ = "ObjectFromPrevVersion";

    /**
     * Test startup of current ignite version using DB storage folder from previous version of Ignite. Expected to start
     * successfully with existing DB
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testFoldersReuseCompatibility_2_2() throws Exception {
        runFoldersReuse("2.2.0");
    }

    /**
     * Test startup of current ignite version using DB storage folder from previous version of Ignite. Expected to start
     * successfully with existing DB
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testFoldersReuseCompatibility_2_1() throws Exception {
        runFoldersReuse("2.1.0");
    }

    /**
     * Started node test actions closure.
     */
    private static class PostStartupClosure implements IgniteInClosure<Ignite> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void apply(Ignite ignite) {
            ignite.active(true);
            final IgniteCache<Object, Object> cache = ignite.getOrCreateCache(FoldersReuseCompatibilityTest.CACHE_NAME);
            cache.put(FoldersReuseCompatibilityTest.KEY, FoldersReuseCompatibilityTest.VAL);
            cache.put("1", "2");
            cache.put(1, 2);
            cache.put(1L, 2L);
            cache.put(A, "Enum_As_Key");
            cache.put("Enum_As_Value", B);
            cache.put(C, C);
            cache.put("Serializable", new PersistenceBasicCompatibilityTest.TestSerializable(42));
            cache.put(new PersistenceBasicCompatibilityTest.TestSerializable(42), "Serializable_As_Key");
            cache.put("Externalizable", new PersistenceBasicCompatibilityTest.TestExternalizable(42));
            cache.put(new PersistenceBasicCompatibilityTest.TestExternalizable(42), "Externalizable_As_Key");
            cache.put(FoldersReuseCompatibilityTest.KEY_OBJ, new PersistenceBasicCompatibilityTest.TestStringContainerToBePrinted(FoldersReuseCompatibilityTest.VAL));
        }
    }

    /**
     * Setup compatible node closure.
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
            FoldersReuseCompatibilityTest.configPersistence(cfg);
        }
    }
}

