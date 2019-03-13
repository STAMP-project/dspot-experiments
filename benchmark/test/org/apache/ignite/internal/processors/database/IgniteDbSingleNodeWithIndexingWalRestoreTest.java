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
package org.apache.ignite.internal.processors.database;


import org.apache.ignite.IgniteBinary;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.persistence.GridCacheDatabaseSharedManager;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test verifies that binary metadata of values stored in cache and indexes upon these values
 * is handled correctly on cluster restart when persistent store is enabled and compact footer is turned on.
 */
public class IgniteDbSingleNodeWithIndexingWalRestoreTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String BINARY_TYPE_NAME = "BinaryPerson";

    /**
     *
     */
    private static final String BINARY_TYPE_FIELD_NAME = "binaryName";

    /**
     *
     */
    private static int ENTRIES_COUNT = 500;

    /**
     *
     */
    private static class RegularPerson {
        /**
         *
         */
        private String regName;

        /**
         *
         */
        public RegularPerson(String regName) {
            this.regName = regName;
        }
    }

    /**
     * Test for values without class created with BinaryObjectBuilder.
     */
    @Test
    public void testClasslessBinaryValuesRestored() throws Exception {
        IgniteEx ig = startGrid(0);
        ig.active(true);
        GridCacheDatabaseSharedManager dbMgr = ((GridCacheDatabaseSharedManager) (ig.context().cache().context().database()));
        dbMgr.enableCheckpoints(false).get();
        IgniteCache<Object, Object> cache = ig.cache("indexedCache").withKeepBinary();
        IgniteBinary bin = ig.binary();
        for (int i = 0; i < (IgniteDbSingleNodeWithIndexingWalRestoreTest.ENTRIES_COUNT); i++) {
            BinaryObjectBuilder bldr = bin.builder(IgniteDbSingleNodeWithIndexingWalRestoreTest.BINARY_TYPE_NAME);
            bldr.setField(IgniteDbSingleNodeWithIndexingWalRestoreTest.BINARY_TYPE_FIELD_NAME, ("Peter" + i));
            cache.put(i, bldr.build());
        }
        stopGrid(0, true);
        ig = startGrid(0);
        ig.active(true);
        cache = ig.cache("indexedCache").withKeepBinary();
        for (int i = 0; i < (IgniteDbSingleNodeWithIndexingWalRestoreTest.ENTRIES_COUNT); i++)
            assertEquals(("Peter" + i), field(IgniteDbSingleNodeWithIndexingWalRestoreTest.BINARY_TYPE_FIELD_NAME));

    }

    /**
     * Test for regular objects stored in cache with compactFooter=true setting
     * (no metainformation to deserialize values is stored with values themselves).
     */
    @Test
    public void testRegularClassesRestored() throws Exception {
        IgniteEx ig = startGrid(0);
        ig.active(true);
        GridCacheDatabaseSharedManager dbMgr = ((GridCacheDatabaseSharedManager) (ig.context().cache().context().database()));
        dbMgr.enableCheckpoints(false).get();
        IgniteCache<Object, Object> cache = ig.cache("indexedCache");
        for (int i = 0; i < (IgniteDbSingleNodeWithIndexingWalRestoreTest.ENTRIES_COUNT); i++)
            cache.put(i, new IgniteDbSingleNodeWithIndexingWalRestoreTest.RegularPerson(("RegularPeter" + i)));

        stopGrid(0, true);
        ig = startGrid(0);
        ig.active(true);
        cache = ig.cache("indexedCache");
        for (int i = 0; i < (IgniteDbSingleNodeWithIndexingWalRestoreTest.ENTRIES_COUNT); i++)
            assertEquals(("RegularPeter" + i), ((IgniteDbSingleNodeWithIndexingWalRestoreTest.RegularPerson) (cache.get(i))).regName);

    }
}

