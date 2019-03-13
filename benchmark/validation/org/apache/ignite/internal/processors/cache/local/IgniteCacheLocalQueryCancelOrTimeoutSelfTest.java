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
package org.apache.ignite.internal.processors.cache.local;


import java.util.concurrent.TimeUnit;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests local query cancellations and timeouts.
 */
public class IgniteCacheLocalQueryCancelOrTimeoutSelfTest extends GridCommonAbstractTest {
    /**
     * Cache size.
     */
    private static final int CACHE_SIZE = 10000;

    /**
     *
     */
    private static final String QUERY = "select a._val, b._val from String a, String b";

    /**
     * Tests cancellation.
     */
    @Test
    public void testQueryCancel() {
        testQuery(false, 1, TimeUnit.SECONDS);
    }

    /**
     * Tests cancellation with zero timeout.
     */
    @Test
    public void testQueryCancelZeroTimeout() {
        testQuery(false, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Tests timeout.
     */
    @Test
    public void testQueryTimeout() {
        testQuery(true, 1, TimeUnit.SECONDS);
    }
}

