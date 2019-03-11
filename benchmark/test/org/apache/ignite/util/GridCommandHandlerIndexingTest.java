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
package org.apache.ignite.util;


import java.io.File;
import java.io.Serializable;
import org.apache.ignite.Ignite;
import org.junit.Test;


/**
 *
 */
public class GridCommandHandlerIndexingTest extends GridCommandHandlerTest {
    /**
     * Test cache name.
     */
    private static final String CACHE_NAME = "persons-cache-vi";

    /**
     * Tests that validation doesn't fail if nothing is broken.
     */
    @Test
    public void testValidateIndexesNoErrors() throws Exception {
        prepareGridForTest();
        injectTestSystemOut();
        assertEquals(EXIT_CODE_OK, execute("--cache", "validate_indexes", GridCommandHandlerIndexingTest.CACHE_NAME));
        assertTrue(testOut.toString().contains("no issues found"));
    }

    /**
     * Tests that missing rows in CacheDataTree are detected.
     */
    @Test
    public void testBrokenCacheDataTreeShouldFailValidation() throws Exception {
        Ignite ignite = prepareGridForTest();
        breakCacheDataTree(ignite, GridCommandHandlerIndexingTest.CACHE_NAME, 1);
        injectTestSystemOut();
        assertEquals(EXIT_CODE_OK, execute("--cache", "validate_indexes", GridCommandHandlerIndexingTest.CACHE_NAME, "--check-first", "10000", "--check-through", "10"));
        assertTrue(testOut.toString().contains("issues found (listed above)"));
        assertTrue(testOut.toString().contains("Key is present in SQL index, but is missing in corresponding data page."));
    }

    /**
     * Tests that missing rows in H2 indexes are detected.
     */
    @Test
    public void testBrokenSqlIndexShouldFailValidation() throws Exception {
        Ignite ignite = prepareGridForTest();
        breakSqlIndex(ignite, GridCommandHandlerIndexingTest.CACHE_NAME);
        injectTestSystemOut();
        assertEquals(EXIT_CODE_OK, execute("--cache", "validate_indexes", GridCommandHandlerIndexingTest.CACHE_NAME));
        assertTrue(testOut.toString().contains("issues found (listed above)"));
    }

    /**
     * Tests that corrupted pages in the index partition are detected.
     */
    @Test
    public void testCorruptedIndexPartitionShouldFailValidation() throws Exception {
        Ignite ignite = prepareGridForTest();
        forceCheckpoint();
        File idxPath = indexPartition(ignite, GridCommandHandlerIndexingTest.CACHE_NAME);
        stopAllGrids();
        corruptIndexPartition(idxPath);
        startGrids(2);
        awaitPartitionMapExchange();
        injectTestSystemOut();
        assertEquals(EXIT_CODE_OK, execute("--cache", "validate_indexes", GridCommandHandlerIndexingTest.CACHE_NAME));
        assertTrue(testOut.toString().contains("issues found (listed above)"));
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        int orgId;

        /**
         *
         */
        String name;

        /**
         *
         *
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         */
        public Person(int orgId, String name) {
            this.orgId = orgId;
            this.name = name;
        }
    }
}

