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
package org.apache.ignite.internal.processors.query.h2.twostep;


import javax.cache.CacheException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Grid cache context is not registered for cache id root cause message test
 */
public class DisappearedCacheWasNotFoundMessageSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final int NODES_COUNT = 2;

    /**
     *
     */
    private static final String ORG = "org";

    /**
     *
     */
    private IgniteCache<String, JoinSqlTestHelper.Person> personCache;

    /**
     *
     */
    private IgniteCache<String, JoinSqlTestHelper.Organization> orgCache;

    /**
     *
     */
    @Test
    public void testDisappearedCacheWasNotFoundMessage() {
        SqlQuery<String, JoinSqlTestHelper.Person> qry = new SqlQuery<String, JoinSqlTestHelper.Person>(JoinSqlTestHelper.Person.class, JoinSqlTestHelper.JOIN_SQL).setArgs("Organization #0");
        qry.setDistributedJoins(true);
        try {
            personCache.query(qry).getAll();
            fail("No CacheException emitted.");
        } catch (CacheException e) {
            boolean exp = e.getMessage().contains("Cache not found on local node (was concurrently destroyed?)");
            if (!exp)
                throw e;

        }
    }
}

