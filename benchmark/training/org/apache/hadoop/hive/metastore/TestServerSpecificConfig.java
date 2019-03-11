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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore;


import java.io.IOException;
import java.net.URL;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.server.HiveServer2;
import org.junit.Assert;
import org.junit.Test;


public class TestServerSpecificConfig {
    private static URL oldDefaultHiveSite = HiveConf.getHiveSiteLocation();

    /**
     * Verify if appropriate server configuration (metastore, hiveserver2) get
     * loaded when the embedded clients are loaded
     *
     * Checks values used in the configs used for testing.
     *
     * @throws IOException
     * 		
     * @throws Throwable
     * 		
     */
    @Test
    public void testServerConfigsEmbeddedMetastore() throws IOException, Throwable {
        // set hive-site.xml to default hive-site.xml that has embedded metastore
        HiveConf.setHiveSiteLocation(TestServerSpecificConfig.oldDefaultHiveSite);
        HiveConf conf = new HiveConf();
        // check config properties expected with embedded metastore client
        Assert.assertTrue(HiveConf.isLoadMetastoreConfig());
        Assert.assertEquals("from.hivemetastore-site.xml", conf.get("hive.dummyparam.test.server.specific.config.override"));
        Assert.assertEquals("from.hivemetastore-site.xml", conf.get("hive.dummyparam.test.server.specific.config.metastoresite"));
        Assert.assertEquals("from.hive-site.xml", conf.get("hive.dummyparam.test.server.specific.config.hivesite"));
        // verify that hiveserver2 config is not loaded
        Assert.assertFalse(HiveConf.isLoadHiveServer2Config());
        Assert.assertNull(conf.get("hive.dummyparam.test.server.specific.config.hiveserver2site"));
        // check if hiveserver2 config gets loaded when HS2 is started
        new HiveServer2();
        conf = new HiveConf();
        verifyHS2ConfParams(conf);
        Assert.assertEquals("from.hivemetastore-site.xml", conf.get("hive.dummyparam.test.server.specific.config.metastoresite"));
    }

    /**
     * Ensure that system properties still get precedence. Config params set as
     * -hiveconf on commandline get set as system properties They should have the
     * final say
     */
    @Test
    public void testSystemPropertyPrecedence() {
        // Using property defined in HiveConf.ConfVars to test System property
        // overriding
        final String OVERRIDE_KEY = "hive.conf.restricted.list";
        try {
            HiveConf.setHiveSiteLocation(TestServerSpecificConfig.oldDefaultHiveSite);
            System.setProperty(OVERRIDE_KEY, "from.sysprop");
            HiveConf conf = new HiveConf();
            // ensure metatore site.xml does not get to override this
            Assert.assertEquals("from.sysprop", conf.get(OVERRIDE_KEY));
            // get HS2 site.xml loaded
            new HiveServer2();
            conf = new HiveConf();
            Assert.assertTrue(HiveConf.isLoadHiveServer2Config());
            // ensure hiveserver2 site.xml does not get to override this
            Assert.assertEquals("from.sysprop", conf.get(OVERRIDE_KEY));
        } finally {
            System.getProperties().remove(OVERRIDE_KEY);
        }
    }

    /**
     * Test to ensure that HiveConf does not try to load hivemetastore-site.xml,
     * when remote metastore is used.
     *
     * @throws IOException
     * 		
     * @throws Throwable
     * 		
     */
    @Test
    public void testHiveMetastoreRemoteConfig() throws IOException, Throwable {
        // switch to hive-site.xml with remote metastore
        setHiveSiteWithRemoteMetastore();
        // Set HiveConf statics to default values
        resetDefaults();
        // create hiveconf again to run initialization code, to see if value changes
        HiveConf conf = new HiveConf();
        // check the properties expected in hive client without metastore
        verifyMetastoreConfNotLoaded(conf);
        Assert.assertEquals("from.hive-site.xml", conf.get("hive.dummyparam.test.server.specific.config.override"));
        // get HS2 site.xml loaded
        new HiveServer2();
        conf = new HiveConf();
        verifyHS2ConfParams(conf);
        verifyMetastoreConfNotLoaded(conf);
    }
}

