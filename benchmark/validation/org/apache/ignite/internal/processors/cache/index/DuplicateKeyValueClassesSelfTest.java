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
package org.apache.ignite.internal.processors.cache.index;


import java.util.UUID;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.CacheConfiguration;
import org.junit.Test;


/**
 * Make sure that cache can start with multiple key-value classes of the same type.
 */
@SuppressWarnings("unchecked")
public class DuplicateKeyValueClassesSelfTest extends AbstractIndexingCommonTest {
    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "cache";

    /**
     * Test duplicate key class.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDuplicateKeyClass() throws Exception {
        CacheConfiguration ccfg = new CacheConfiguration().setName(DuplicateKeyValueClassesSelfTest.CACHE_NAME).setIndexedTypes(UUID.class, DuplicateKeyValueClassesSelfTest.Clazz1.class, UUID.class, DuplicateKeyValueClassesSelfTest.Clazz2.class);
        grid(0).createCache(ccfg);
    }

    /**
     * Test duplicate value class.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDuplicateValueClass() throws Exception {
        CacheConfiguration ccfg = new CacheConfiguration().setName(DuplicateKeyValueClassesSelfTest.CACHE_NAME).setIndexedTypes(UUID.class, DuplicateKeyValueClassesSelfTest.Clazz1.class, String.class, DuplicateKeyValueClassesSelfTest.Clazz1.class);
        grid(0).createCache(ccfg);
    }

    /**
     * Class 1.
     */
    private static class Clazz1 {
        /**
         * ID.
         */
        @QuerySqlField(index = true)
        int id;
    }

    /**
     * Class 2.
     */
    private static class Clazz2 {
        /**
         * ID.
         */
        @QuerySqlField(index = true)
        int id;
    }
}

