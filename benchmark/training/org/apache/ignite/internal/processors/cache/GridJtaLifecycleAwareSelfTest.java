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


import GridAbstractLifecycleAwareSelfTest.TestLifecycleAware;
import javax.cache.configuration.Factory;
import javax.transaction.TransactionManager;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.jta.CacheTmLookup;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.testframework.junits.common.GridAbstractLifecycleAwareSelfTest;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;


/**
 * Test for {@link LifecycleAware} support for {@link CacheTmLookup}.
 */
public class GridJtaLifecycleAwareSelfTest extends GridAbstractLifecycleAwareSelfTest {
    /**
     *
     */
    private static final String CACHE_NAME = "cache";

    /**
     *
     */
    private boolean near;

    /**
     *
     */
    private GridJtaLifecycleAwareSelfTest.TmConfigurationType tmConfigurationType;

    /**
     *
     */
    @SuppressWarnings("PublicInnerClass")
    public static class TestTxLookup extends GridAbstractLifecycleAwareSelfTest.TestLifecycleAware implements CacheTmLookup {
        /**
         *
         */
        @IgniteInstanceResource
        private Ignite ignite;

        /**
         * {@inheritDoc }
         */
        @Override
        public void start() {
            super.start();
            assertNotNull(ignite);
        }

        /**
         * {@inheritDoc }
         */
        @Nullable
        @Override
        public TransactionManager getTm() {
            return null;
        }
    }

    /**
     *
     */
    public static class TestTxFactory extends GridAbstractLifecycleAwareSelfTest.TestLifecycleAware implements Factory<TransactionManager> {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        @IgniteInstanceResource
        private Ignite ignite;

        /**
         * {@inheritDoc }
         */
        @Override
        public void start() {
            super.start();
            assertNotNull(ignite);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public TransactionManager create() {
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Test
    @Override
    public void testLifecycleAware() throws Exception {
        // No-op, see anothre tests.
    }

    /**
     * {@inheritDoc }
     */
    @Test
    public void testCacheLookupLifecycleAware() throws Exception {
        tmConfigurationType = GridJtaLifecycleAwareSelfTest.TmConfigurationType.CACHE_LOOKUP;
        checkLifecycleAware();
    }

    /**
     * {@inheritDoc }
     */
    @Test
    public void testGlobalLookupLifecycleAware() throws Exception {
        tmConfigurationType = GridJtaLifecycleAwareSelfTest.TmConfigurationType.GLOBAL_LOOKUP;
        checkLifecycleAware();
    }

    /**
     * {@inheritDoc }
     */
    @Test
    public void testFactoryLifecycleAware() throws Exception {
        tmConfigurationType = GridJtaLifecycleAwareSelfTest.TmConfigurationType.FACTORY;
        checkLifecycleAware();
    }

    /**
     *
     */
    private enum TmConfigurationType {

        /**
         *
         */
        CACHE_LOOKUP,
        /**
         *
         */
        GLOBAL_LOOKUP,
        /**
         *
         */
        FACTORY;}
}

