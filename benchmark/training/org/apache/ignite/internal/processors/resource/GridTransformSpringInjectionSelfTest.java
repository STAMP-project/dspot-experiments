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
package org.apache.ignite.internal.processors.resource;


import ResourceType.SPRING_APPLICATION_CONTEXT;
import ResourceType.SPRING_BEAN;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.internal.processors.cache.GridCacheAbstractSelfTest;
import org.apache.ignite.resources.SpringApplicationContextResource;
import org.apache.ignite.resources.SpringResource;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 *
 */
public class GridTransformSpringInjectionSelfTest extends GridCacheAbstractSelfTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTransformResourceInjection() throws Exception {
        Ignite grid = grid(0);
        IgniteCache<String, Integer> cache = grid.createCache(cacheConfiguration(CacheAtomicityMode.ATOMIC));
        try {
            doTransformResourceInjection(cache);
        } finally {
            cache.destroy();
        }
        cache = grid.createCache(cacheConfiguration(CacheAtomicityMode.TRANSACTIONAL));
        try {
            doTransformResourceInjection(cache);
            for (TransactionConcurrency concurrency : TransactionConcurrency.values()) {
                for (TransactionIsolation isolation : TransactionIsolation.values()) {
                    IgniteTransactions txs = grid.transactions();
                    try (Transaction tx = txs.txStart(concurrency, isolation)) {
                        doTransformResourceInjection(cache);
                        tx.commit();
                    }
                }
            }
        } finally {
            cache.destroy();
        }
    }

    /**
     *
     */
    static class SpringResourceInjectionEntryProcessor extends ResourceInjectionEntryProcessorBase<String, Integer> {
        /**
         *
         */
        private transient ApplicationContext appCtx;

        /**
         *
         */
        private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyBean;

        /**
         *
         *
         * @param appCtx
         * 		Context.
         */
        @SpringApplicationContextResource
        public void setApplicationContext(ApplicationContext appCtx) {
            assert appCtx != null;
            checkSet();
            infoSet.set(SPRING_APPLICATION_CONTEXT, true);
            this.appCtx = appCtx;
        }

        /**
         *
         *
         * @param dummyBean
         * 		Resource bean.
         */
        @SpringResource(resourceName = "dummyResourceBean")
        public void setDummyBean(GridSpringResourceInjectionSelfTest.DummyResourceBean dummyBean) {
            assert dummyBean != null;
            checkSet();
            infoSet.set(SPRING_BEAN, true);
            this.dummyBean = dummyBean;
        }
    }
}

