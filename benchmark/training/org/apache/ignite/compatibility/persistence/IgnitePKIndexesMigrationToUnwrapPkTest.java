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


import GridCacheAbstractFullApiSelfTest.LOCAL_IP_FINDER;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;


/**
 * Test to check that starting node with PK index of the old format present doesn't break anything.
 */
public class IgnitePKIndexesMigrationToUnwrapPkTest extends IgnitePersistenceCompatibilityAbstractTest {
    /**
     *
     */
    private static String TABLE_NAME = "TEST_IDX_TABLE";

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSecondaryIndexesMigration_2_5() throws Exception {
        doTestStartupWithOldVersion("2.5.0");
    }

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSecondaryIndexesMigration_2_4() throws Exception {
        doTestStartupWithOldVersion("2.4.0");
    }

    /**
     *
     */
    private static class PostStartupClosure implements IgniteInClosure<Ignite> {
        /**
         *
         */
        boolean createTable;

        /**
         *
         *
         * @param createTable
         * 		{@code true} In case table should be created
         */
        public PostStartupClosure(boolean createTable) {
            this.createTable = createTable;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void apply(Ignite ignite) {
            ignite.active(true);
            IgniteEx igniteEx = ((IgniteEx) (ignite));
            if (createTable)
                IgnitePKIndexesMigrationToUnwrapPkTest.initializeTable(igniteEx, IgnitePKIndexesMigrationToUnwrapPkTest.TABLE_NAME);

            IgnitePKIndexesMigrationToUnwrapPkTest.assertDontUsingPkIndex(igniteEx, IgnitePKIndexesMigrationToUnwrapPkTest.TABLE_NAME);
            ignite.active(false);
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
            DataStorageConfiguration memCfg = new DataStorageConfiguration().setDefaultDataRegionConfiguration(new DataRegionConfiguration().setPersistenceEnabled(true).setInitialSize(((1024 * 1024) * 10)).setMaxSize(((1024 * 1024) * 15))).setSystemRegionInitialSize(((1024 * 1024) * 10)).setSystemRegionMaxSize(((1024 * 1024) * 15));
            cfg.setDataStorageConfiguration(memCfg);
        }
    }
}

