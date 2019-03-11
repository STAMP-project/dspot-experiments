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


import HiveConf.ConfVars.METASTORE_AUTO_CREATE_ALL;
import HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION;
import HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION_RECORD_VERSION;
import junit.framework.TestCase;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.DriverFactory;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hive.common.util.HiveStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMetastoreVersion extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestMetastoreVersion.class);

    protected HiveConf hiveConf;

    private IDriver driver;

    private String metaStoreRoot;

    private String testMetastoreDB;

    private IMetaStoreSchemaInfo metastoreSchemaInfo;

    /**
     * *
     * Test config defaults
     */
    public void testDefaults() {
        System.clearProperty(METASTORE_SCHEMA_VERIFICATION.toString());
        hiveConf = new HiveConf(this.getClass());
        TestCase.assertFalse(hiveConf.getBoolVar(METASTORE_SCHEMA_VERIFICATION));
        TestCase.assertTrue(hiveConf.getBoolVar(METASTORE_AUTO_CREATE_ALL));
    }

    /**
     * *
     * Test schema verification property
     *
     * @throws Exception
     * 		
     */
    public void testVersionRestriction() throws Exception {
        System.setProperty(METASTORE_SCHEMA_VERIFICATION.toString(), "true");
        hiveConf = new HiveConf(this.getClass());
        TestCase.assertTrue(hiveConf.getBoolVar(METASTORE_SCHEMA_VERIFICATION));
        TestCase.assertFalse(hiveConf.getBoolVar(METASTORE_AUTO_CREATE_ALL));
        // session creation should fail since the schema didn't get created
        try {
            SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(hiveConf));
            Hive.get(hiveConf).getMSC();
            TestCase.fail("An exception is expected since schema is not created.");
        } catch (Exception re) {
            TestMetastoreVersion.LOG.info(("Exception in testVersionRestriction: " + re), re);
            String msg = HiveStringUtils.stringifyException(re);
            TestCase.assertTrue(("Expected 'Version information not found in metastore' in: " + msg), msg.contains("Version information not found in metastore"));
        }
    }

    /**
     * *
     * Test that with no verification, and record verification enabled, hive populates the schema
     * and version correctly
     *
     * @throws Exception
     * 		
     */
    public void testMetastoreVersion() throws Exception {
        // let the schema and version be auto created
        System.setProperty(METASTORE_SCHEMA_VERIFICATION.toString(), "false");
        System.setProperty(METASTORE_SCHEMA_VERIFICATION_RECORD_VERSION.toString(), "true");
        hiveConf = new HiveConf(this.getClass());
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(hiveConf));
        driver = DriverFactory.newDriver(hiveConf);
        driver.run("show tables");
        // correct version stored by Metastore during startup
        TestCase.assertEquals(metastoreSchemaInfo.getHiveSchemaVersion(), getVersion(hiveConf));
        setVersion(hiveConf, "foo");
        TestCase.assertEquals("foo", getVersion(hiveConf));
    }

    /**
     * *
     * Test that with verification enabled, hive works when the correct schema is already populated
     *
     * @throws Exception
     * 		
     */
    public void testVersionMatching() throws Exception {
        System.setProperty(METASTORE_SCHEMA_VERIFICATION.toString(), "false");
        hiveConf = new HiveConf(this.getClass());
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(hiveConf));
        driver = DriverFactory.newDriver(hiveConf);
        driver.run("show tables");
        ObjectStore.setSchemaVerified(false);
        hiveConf.setBoolVar(METASTORE_SCHEMA_VERIFICATION, true);
        hiveConf = new HiveConf(this.getClass());
        setVersion(hiveConf, metastoreSchemaInfo.getHiveSchemaVersion());
        driver = DriverFactory.newDriver(hiveConf);
        CommandProcessorResponse proc = driver.run("show tables");
        TestCase.assertTrue(((proc.getResponseCode()) == 0));
    }

    /**
     * Store garbage version in metastore and verify that hive fails when verification is on
     *
     * @throws Exception
     * 		
     */
    public void testVersionMisMatch() throws Exception {
        System.setProperty(METASTORE_SCHEMA_VERIFICATION.toString(), "false");
        hiveConf = new HiveConf(this.getClass());
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(hiveConf));
        driver = DriverFactory.newDriver(hiveConf);
        driver.run("show tables");
        ObjectStore.setSchemaVerified(false);
        System.setProperty(METASTORE_SCHEMA_VERIFICATION.toString(), "true");
        hiveConf = new HiveConf(this.getClass());
        setVersion(hiveConf, "fooVersion");
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(hiveConf));
        driver = DriverFactory.newDriver(hiveConf);
        CommandProcessorResponse proc = driver.run("show tables");
        TestCase.assertTrue(((proc.getResponseCode()) != 0));
    }

    /**
     * Store higher version in metastore and verify that hive works with the compatible
     * version
     *
     * @throws Exception
     * 		
     */
    public void testVersionCompatibility() throws Exception {
        System.setProperty(METASTORE_SCHEMA_VERIFICATION.toString(), "false");
        hiveConf = new HiveConf(this.getClass());
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(hiveConf));
        driver = DriverFactory.newDriver(hiveConf);
        driver.run("show tables");
        System.setProperty(METASTORE_SCHEMA_VERIFICATION.toString(), "true");
        hiveConf = new HiveConf(this.getClass());
        setVersion(hiveConf, "3.9000.0");
        SessionState.start(new org.apache.hadoop.hive.cli.CliSessionState(hiveConf));
        driver = DriverFactory.newDriver(hiveConf);
        CommandProcessorResponse proc = driver.run("show tables");
        TestCase.assertEquals(0, proc.getResponseCode());
    }
}

