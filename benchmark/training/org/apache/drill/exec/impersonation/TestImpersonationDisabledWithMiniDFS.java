/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.impersonation;


import org.apache.drill.categories.SecurityTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Note to future devs, please do not put random tests here. Make sure that they actually require
 * access to a DFS instead of the local filesystem implementation used by default in the rest of
 * the tests. Running this mini cluster is slow and it is best for these tests to only cover
 * necessary cases.
 */
@Category({ SlowTest.class, SecurityTest.class })
public class TestImpersonationDisabledWithMiniDFS extends BaseTestImpersonation {
    /**
     * When working on merging the Drill fork of parquet a bug was found that only manifested when
     * run on a cluster. It appears that the local implementation of the Hadoop FileSystem API
     * never fails to provide all of the bytes that are requested in a single read. The API is
     * designed to allow for a subset of the requested bytes be returned, and a client can decide
     * if they want to do processing on teh subset that are available now before requesting the rest.
     *
     * For parquet's block compression of page data, we need all of the bytes. This test is here as
     * a sanitycheck  to make sure we don't accidentally introduce an issue where a subset of the bytes
     * are read and would otherwise require testing on a cluster for the full contract of the read method
     * we are using to be exercised.
     */
    @Test
    public void testReadLargeParquetFileFromDFS() throws Exception {
        BaseTestQuery.test(String.format("USE %s", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME));
        BaseTestQuery.test("SELECT * FROM tmp.`large_employee`");
    }

    // DRILL-3037
    @Test
    public void testSimpleQuery() throws Exception {
        final String query = String.format("SELECT sales_city, sales_country FROM tmp.dfsRegion ORDER BY region_id DESC LIMIT 2");
        BaseTestQuery.testBuilder().optionSettingQueriesForTestQuery(String.format("USE %s", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)).sqlQuery(query).unOrdered().baselineColumns("sales_city", "sales_country").baselineValues("Santa Fe", "Mexico").baselineValues("Santa Anita", "Mexico").go();
    }
}

