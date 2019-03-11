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
package org.apache.ignite.internal.processors.cache;


import org.apache.ignite.Ignite;
import org.apache.ignite.failure.FailureContext;
import org.apache.ignite.failure.StopNodeFailureHandler;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests, that cluster could start and activate with all possible values of IGNITE_ALLOW_START_CACHES_IN_PARALLEL.
 */
public class StartCachesInParallelTest extends GridCommonAbstractTest {
    /**
     * IGNITE_ALLOW_START_CACHES_IN_PARALLEL option value before tests.
     */
    private String allowParallel;

    /**
     * Test failure handler.
     */
    private StartCachesInParallelTest.TestStopNodeFailureHandler failureHnd;

    /**
     *
     */
    @Test
    public void testWithEnabledOption() throws Exception {
        doTest("true");
    }

    /**
     *
     */
    @Test
    public void testWithDisabledOption() throws Exception {
        doTest("false");
    }

    /**
     *
     */
    @Test
    public void testWithoutOption() throws Exception {
        doTest(null);
    }

    /**
     *
     */
    private static class TestStopNodeFailureHandler extends StopNodeFailureHandler {
        /**
         * Last failure context.
         */
        private volatile FailureContext lastFailureCtx;

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean handle(Ignite ignite, FailureContext failureCtx) {
            lastFailureCtx = failureCtx;
            return super.handle(ignite, failureCtx);
        }
    }
}

