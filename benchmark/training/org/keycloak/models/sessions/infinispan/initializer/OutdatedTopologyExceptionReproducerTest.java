/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.models.sessions.infinispan.initializer;


import InfinispanConnectionProvider.REALM_CACHE_NAME;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Reproducer for KEYCLOAK-3306. Uncomment the snippet for adding StateTransferInterceptor to have test fixed.
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Ignore
public class OutdatedTopologyExceptionReproducerTest {
    protected static final Logger logger = Logger.getLogger(OutdatedTopologyExceptionReproducerTest.class);

    private static final int THREADS_COUNT = 20;

    @Test
    public void testListener() throws Exception {
        EmbeddedCacheManager node1 = null;
        EmbeddedCacheManager node2 = null;
        try {
            node1 = createManager();
            Cache<String, Object> node1Cache = node1.getCache(REALM_CACHE_NAME);
            OutdatedTopologyExceptionReproducerTest.logger.info("Node1Cache started");
            List<OutdatedTopologyExceptionReproducerTest.CacheOperations> cacheOpsList = new ArrayList<>();
            AtomicReference<Exception> exceptionHolder = new AtomicReference<>();
            for (int i = 0; i < (OutdatedTopologyExceptionReproducerTest.THREADS_COUNT); i++) {
                String key = "key-" + i;
                OutdatedTopologyExceptionReproducerTest.CacheOperations cacheOps = new OutdatedTopologyExceptionReproducerTest.CacheOperations(node1Cache, key, exceptionHolder);
                cacheOps.start();
                cacheOpsList.add(cacheOps);
            }
            OutdatedTopologyExceptionReproducerTest.logger.infof("All CacheOperations threads started");
            node2 = createManager();
            Cache<String, Object> node2Cache = node2.getCache(REALM_CACHE_NAME);
            OutdatedTopologyExceptionReproducerTest.logger.info("Node2Cache started");
            for (OutdatedTopologyExceptionReproducerTest.CacheOperations cacheOps : cacheOpsList) {
                cacheOps.stopMe();
            }
            for (OutdatedTopologyExceptionReproducerTest.CacheOperations cacheOps : cacheOpsList) {
                cacheOps.join();
            }
            OutdatedTopologyExceptionReproducerTest.logger.info("All CacheOperations threads stopped");
            Exception ex = exceptionHolder.get();
            if (ex != null) {
                Assert.fail(((("Some exception was thrown. It was: " + (ex.getClass().getName())) + ": ") + (ex.getMessage())));
            }
            OutdatedTopologyExceptionReproducerTest.logger.info("Test finished successfuly");
        } finally {
            node2.stop();
            node1.stop();
        }
    }

    private class CacheOperations extends Thread {
        private final Cache<String, Object> cache;

        private final AtomicBoolean stopped = new AtomicBoolean(false);

        private final AtomicReference<Exception> exceptionHolder;

        private final String key;

        public CacheOperations(Cache<String, Object> cache, String key, AtomicReference<Exception> exceptionHolder) {
            this.cache = cache;
            this.key = key;
            this.exceptionHolder = exceptionHolder;
        }

        @Override
        public void run() {
            try {
                while (!(stopped.get())) {
                    cache.putForExternalRead(key, new Object());
                    cache.remove(key);
                } 
            } catch (Exception e) {
                exceptionHolder.set(e);
                throw e;
            }
        }

        public void stopMe() {
            stopped.set(true);
        }
    }
}

