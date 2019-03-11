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
import org.junit.Test;


/**
 * Test for cancel of query containing distributed joins.
 */
public class IgniteCacheQueryStopOnCancelOrTimeoutDistributedJoinSelfTest extends IgniteCacheQueryAbstractDistributedJoinSelfTest {
    /**
     *
     */
    @Test
    public void testCancel1() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 1, TimeUnit.MILLISECONDS, false, true);
    }

    /**
     *
     */
    @Test
    public void testCancel2() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 50, TimeUnit.MILLISECONDS, false, true);
    }

    /**
     *
     */
    @Test
    public void testCancel3() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 100, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testCancel4() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 500, TimeUnit.MILLISECONDS, false, false);
    }

    /**
     *
     */
    @Test
    public void testTimeout1() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 1, TimeUnit.MILLISECONDS, true, true);
    }

    /**
     *
     */
    @Test
    public void testTimeout2() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 50, TimeUnit.MILLISECONDS, true, true);
    }

    /**
     *
     */
    @Test
    public void testTimeout3() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 100, TimeUnit.MILLISECONDS, true, false);
    }

    /**
     *
     */
    @Test
    public void testTimeout4() throws Exception {
        testQueryCancel(grid(0), "pe", IgniteCacheQueryAbstractDistributedJoinSelfTest.QRY_LONG, 500, TimeUnit.MILLISECONDS, true, false);
    }
}

