/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.common;


import HiveClientCache.ICacheableMetaStoreClient;
import HiveConf.ConfVars.DYNAMICPARTITIONMAXPARTS;
import HiveConf.ConfVars.METASTORETHRIFTCONNECTIONRETRIES;
import HiveConf.ConfVars.METASTORETHRIFTFAILURERETRIES;
import HiveConf.ConfVars.METASTOREURIS;
import HiveConf.ConfVars.SEMANTIC_ANALYZER_HOOK.varname;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.security.auth.login.LoginException;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hive.hcatalog.NoExitSecurityManager;
import org.apache.hive.hcatalog.cli.SemanticAnalysis.HCatSemanticAnalyzer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHiveClientCache {
    private static final Logger LOG = LoggerFactory.getLogger(TestHiveClientCache.class);

    final HiveConf hiveConf = new HiveConf();

    @Test
    public void testCacheHit() throws IOException, LoginException, MetaException {
        HiveClientCache cache = new HiveClientCache(1000);
        HiveClientCache.ICacheableMetaStoreClient client = ((HiveClientCache.ICacheableMetaStoreClient) (cache.get(hiveConf)));
        Assert.assertNotNull(client);
        client.close();// close shouldn't matter

        // Setting a non important configuration should return the same client only
        hiveConf.setIntVar(DYNAMICPARTITIONMAXPARTS, 10);
        HiveClientCache.ICacheableMetaStoreClient client2 = ((HiveClientCache.ICacheableMetaStoreClient) (cache.get(hiveConf)));
        Assert.assertNotNull(client2);
        Assert.assertSame(client, client2);
        Assert.assertEquals(client.getUsers(), client2.getUsers());
        client2.close();
    }

    @Test
    public void testCacheMiss() throws IOException, LoginException, MetaException {
        HiveClientCache cache = new HiveClientCache(1000);
        IMetaStoreClient client = cache.get(hiveConf);
        Assert.assertNotNull(client);
        // Set different uri as it is one of the criteria deciding whether to return the same client or not
        hiveConf.setVar(METASTOREURIS, " ");// URIs are checked for string equivalence, even spaces make them different

        IMetaStoreClient client2 = cache.get(hiveConf);
        Assert.assertNotNull(client2);
        Assert.assertNotSame(client, client2);
    }

    /**
     * Check that a new client is returned for the same configuration after the expiry time.
     * Also verify that the expiry time configuration is honoured
     */
    @Test
    public void testCacheExpiry() throws IOException, InterruptedException, LoginException, MetaException {
        HiveClientCache cache = new HiveClientCache(1);
        HiveClientCache.ICacheableMetaStoreClient client = ((HiveClientCache.ICacheableMetaStoreClient) (cache.get(hiveConf)));
        Assert.assertNotNull(client);
        Thread.sleep(2500);
        HiveClientCache.ICacheableMetaStoreClient client2 = ((HiveClientCache.ICacheableMetaStoreClient) (cache.get(hiveConf)));
        client.close();
        Assert.assertTrue(client.isClosed());// close() after *expiry time* and *a cache access* should  have tore down the client

        Assert.assertNotNull(client2);
        Assert.assertNotSame(client, client2);
    }

    /**
     * Check that a *new* client is created if asked from different threads even with
     * the same hive configuration
     *
     * @throws ExecutionException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testMultipleThreadAccess() throws InterruptedException, ExecutionException {
        final HiveClientCache cache = new HiveClientCache(1000);
        class GetHiveClient implements Callable<IMetaStoreClient> {
            @Override
            public IMetaStoreClient call() throws IOException, LoginException, MetaException {
                return cache.get(hiveConf);
            }
        }
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<IMetaStoreClient> worker1 = new GetHiveClient();
        Callable<IMetaStoreClient> worker2 = new GetHiveClient();
        Future<IMetaStoreClient> clientFuture1 = executor.submit(worker1);
        Future<IMetaStoreClient> clientFuture2 = executor.submit(worker2);
        IMetaStoreClient client1 = clientFuture1.get();
        IMetaStoreClient client2 = clientFuture2.get();
        Assert.assertNotNull(client1);
        Assert.assertNotNull(client2);
        Assert.assertNotSame(client1, client2);
    }

    @Test
    public void testCloseAllClients() throws IOException, LoginException, MetaException {
        final HiveClientCache cache = new HiveClientCache(1000);
        HiveClientCache.ICacheableMetaStoreClient client1 = ((HiveClientCache.ICacheableMetaStoreClient) (cache.get(hiveConf)));
        hiveConf.setVar(METASTOREURIS, " ");// URIs are checked for string equivalence, even spaces make them different

        HiveClientCache.ICacheableMetaStoreClient client2 = ((HiveClientCache.ICacheableMetaStoreClient) (cache.get(hiveConf)));
        cache.closeAllClientsQuietly();
        Assert.assertTrue(client1.isClosed());
        Assert.assertTrue(client2.isClosed());
    }

    private static class LocalMetaServer implements Runnable {
        public final int MS_PORT = 20101;

        private final HiveConf hiveConf;

        private final SecurityManager securityManager;

        public static final int WAIT_TIME_FOR_BOOTUP = 30000;

        public LocalMetaServer() {
            securityManager = System.getSecurityManager();
            System.setSecurityManager(new NoExitSecurityManager());
            hiveConf = new HiveConf(TestHiveClientCache.class);
            hiveConf.setVar(METASTOREURIS, ("thrift://localhost:" + (MS_PORT)));
            hiveConf.setIntVar(METASTORETHRIFTCONNECTIONRETRIES, 3);
            hiveConf.setIntVar(METASTORETHRIFTFAILURERETRIES, 3);
            hiveConf.set(varname, HCatSemanticAnalyzer.class.getName());
            hiveConf.set(HiveConf.ConfVars.PREEXECHOOKS.varname, "");
            hiveConf.set(HiveConf.ConfVars.POSTEXECHOOKS.varname, "");
            hiveConf.set(HiveConf.ConfVars.HIVE_SUPPORT_CONCURRENCY.varname, "false");
            System.setProperty(HiveConf.ConfVars.PREEXECHOOKS.varname, " ");
            System.setProperty(HiveConf.ConfVars.POSTEXECHOOKS.varname, " ");
        }

        public void start() throws InterruptedException {
            Thread thread = new Thread(this);
            thread.start();
            Thread.sleep(TestHiveClientCache.LocalMetaServer.WAIT_TIME_FOR_BOOTUP);// Wait for the server to bootup

        }

        @Override
        public void run() {
            try {
                HiveMetaStore.main(new String[]{ "-v", "-p", String.valueOf(MS_PORT) });
            } catch (Throwable t) {
                TestHiveClientCache.LOG.error("Exiting. Got exception from metastore: ", t);
            }
        }

        public HiveConf getHiveConf() {
            return hiveConf;
        }

        public void shutDown() {
            System.setSecurityManager(securityManager);
        }
    }
}

