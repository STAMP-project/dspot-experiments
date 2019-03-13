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
package org.apache.activemq.store.kahadb;


import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.openwire.OpenWireFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KahaDBStoreOpenWireVersionTest {
    private static final Logger LOG = LoggerFactory.getLogger(KahaDBStoreOpenWireVersionTest.class);

    private final String KAHADB_DIRECTORY_BASE = "./target/activemq-data/";

    private final int NUM_MESSAGES = 10;

    private BrokerService broker = null;

    private String storeDir;

    @Rule
    public TestName name = new TestName();

    @Test(timeout = 60000)
    public void testConfiguredVersionWorksOnReload() throws Exception {
        final int INITIAL_STORE_VERSION = (OpenWireFormat.DEFAULT_STORE_VERSION) - 1;
        final int RELOAD_STORE_VERSION = (OpenWireFormat.DEFAULT_STORE_VERSION) - 1;
        doTestStoreVersionConfigrationOverrides(INITIAL_STORE_VERSION, RELOAD_STORE_VERSION);
    }

    @Test(timeout = 60000)
    public void testOlderVersionWorksWithDefaults() throws Exception {
        final int INITIAL_STORE_VERSION = OpenWireFormat.DEFAULT_LEGACY_VERSION;
        final int RELOAD_STORE_VERSION = OpenWireFormat.DEFAULT_STORE_VERSION;
        doTestStoreVersionConfigrationOverrides(INITIAL_STORE_VERSION, RELOAD_STORE_VERSION);
    }

    @Test(timeout = 60000)
    public void testNewerVersionWorksWhenOlderIsConfigured() throws Exception {
        final int INITIAL_STORE_VERSION = OpenWireFormat.DEFAULT_STORE_VERSION;
        final int RELOAD_STORE_VERSION = OpenWireFormat.DEFAULT_LEGACY_VERSION;
        doTestStoreVersionConfigrationOverrides(INITIAL_STORE_VERSION, RELOAD_STORE_VERSION);
    }

    /**
     * This test shows that a corrupted index/rebuild will still
     * honor the storeOpenWireVersion set on the BrokerService.
     * This wasn't the case before AMQ-6082
     */
    @Test(timeout = 60000)
    public void testStoreVersionCorrupt() throws Exception {
        final int create = 6;
        final int reload = 6;
        createBroker(create);
        populateStore();
        // blow up the index so it has to be recreated
        corruptIndex();
        stopBroker();
        createBroker(reload);
        Assert.assertEquals(create, broker.getStoreOpenWireVersion());
        assertStoreIsUsable();
    }
}

