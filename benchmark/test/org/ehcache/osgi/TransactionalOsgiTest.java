/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.osgi;


import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.transactions.xa.configuration.XAStoreConfiguration;
import org.ehcache.transactions.xa.txmgr.btm.BitronixTransactionManagerLookup;
import org.ehcache.transactions.xa.txmgr.provider.LookupTransactionManagerProviderConfiguration;
import org.ehcache.xml.XmlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class TransactionalOsgiTest {
    @Test
    public void testProgrammaticConfiguration() throws Exception {
        TransactionalOsgiTest.TestMethods.testProgrammaticConfiguration();
    }

    @Test
    public void testXmlConfiguration() throws Exception {
        TransactionalOsgiTest.TestMethods.testXmlConfiguration();
    }

    private static class TestMethods {
        public static void testProgrammaticConfiguration() throws Exception {
            BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
            try (CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withClassLoader(TransactionalOsgiTest.TestMethods.class.getClassLoader()).using(new LookupTransactionManagerProviderConfiguration(BitronixTransactionManagerLookup.class)).withCache("xaCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).add(new XAStoreConfiguration("xaCache")).build()).build(true)) {
                Cache<Long, String> xaCache = cacheManager.getCache("xaCache", Long.class, String.class);
                transactionManager.begin();
                try {
                    xaCache.put(1L, "one");
                } catch (Throwable t) {
                    transactionManager.rollback();
                }
                transactionManager.commit();
            }
            transactionManager.shutdown();
        }

        public static void testXmlConfiguration() throws Exception {
            BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
            try (CacheManager cacheManager = CacheManagerBuilder.newCacheManager(new XmlConfiguration(TransactionalOsgiTest.TestMethods.class.getResource("ehcache-xa-osgi.xml"), TransactionalOsgiTest.TestMethods.class.getClassLoader()))) {
                cacheManager.init();
                Cache<Long, String> xaCache = cacheManager.getCache("xaCache", Long.class, String.class);
                transactionManager.begin();
                try {
                    xaCache.put(1L, "one");
                } catch (Throwable t) {
                    transactionManager.rollback();
                }
                transactionManager.commit();
            }
            transactionManager.shutdown();
        }
    }
}

