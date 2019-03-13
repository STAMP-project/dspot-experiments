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
package org.apache.ignite.internal.processors.cache.distributed.near;


import java.util.concurrent.TimeUnit;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests distributed SQL queries cancel by user or timeout.
 */
public class IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest extends GridCommonAbstractTest {
    /**
     * Grids count.
     */
    private static final int GRIDS_CNT = 3;

    /**
     * Cache size.
     */
    public static final int CACHE_SIZE = 10000;

    /**
     * Value size.
     */
    public static final int VAL_SIZE = 16;

    /**
     *
     */
    private static final String QRY_1 = "select a._val, b._val from String a, String b";

    /**
     *
     */
    private static final String QRY_2 = "select a._key, count(*) from String a group by a._key";

    /**
     *
     */
    private static final String QRY_3 = "select a._val from String a";

    /**
     *
     */
    @Test
    public void testRemoteQueryExecutionTimeout() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_1, 500, TimeUnit.MILLISECONDS, true, true);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithMergeTableTimeout() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_2, 500, TimeUnit.MILLISECONDS, true, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryExecutionCancel0() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_1, 1, TimeUnit.MILLISECONDS, false, true);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryExecutionCancel1() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_1, 500, TimeUnit.MILLISECONDS, false, true);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryExecutionCancel2() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_1, 1, TimeUnit.SECONDS, false, true);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryExecutionCancel3() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_1, 3, TimeUnit.SECONDS, false, true);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithMergeTableCancel0() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_2, 1, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithMergeTableCancel1() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_2, 500, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithMergeTableCancel2() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_2, 1500, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithMergeTableCancel3() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_2, 3, TimeUnit.SECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithoutMergeTableCancel0() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_3, 1, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithoutMergeTableCancel1() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_3, 500, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithoutMergeTableCancel2() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_3, 1000, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryWithoutMergeTableCancel3() throws Exception {
        testQueryCancel(IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.CACHE_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_3, 3, TimeUnit.SECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testRemoteQueryAlreadyFinishedStop() throws Exception {
        testQueryCancel(100, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.VAL_SIZE, IgniteCacheDistributedQueryStopOnCancelOrTimeoutSelfTest.QRY_3, 3, TimeUnit.SECONDS, false, false);
    }
}

