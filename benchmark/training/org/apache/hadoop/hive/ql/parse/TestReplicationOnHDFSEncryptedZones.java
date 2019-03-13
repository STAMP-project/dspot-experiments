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
package org.apache.hadoop.hive.ql.parse;


import HiveConf.ConfVars.HIVE_IN_TEST.varname;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestReplicationOnHDFSEncryptedZones {
    private static String jksFile = (System.getProperty("java.io.tmpdir")) + "/test.jks";

    @Rule
    public final TestName testName = new TestName();

    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationScenarios.class);

    private static WarehouseInstance primary;

    private static String primaryDbName;

    private static String replicatedDbName;

    private static Configuration conf;

    private static MiniDFSCluster miniDFSCluster;

    @Test
    public void targetAndSourceHaveDifferentEncryptionZoneKeys() throws Throwable {
        DFSTestUtil.createKey("test_key123", TestReplicationOnHDFSEncryptedZones.miniDFSCluster, TestReplicationOnHDFSEncryptedZones.conf);
        WarehouseInstance replica = new WarehouseInstance(TestReplicationOnHDFSEncryptedZones.LOG, TestReplicationOnHDFSEncryptedZones.miniDFSCluster, new HashMap<String, String>() {
            {
                put(varname, "false");
                put(HiveConf.ConfVars.HIVE_SERVER2_ENABLE_DOAS.varname, "false");
                put(HiveConf.ConfVars.HIVE_DISTCP_DOAS_USER.varname, UserGroupInformation.getCurrentUser().getUserName());
            }
        }, "test_key123");
        WarehouseInstance.Tuple tuple = TestReplicationOnHDFSEncryptedZones.primary.run(("use " + (TestReplicationOnHDFSEncryptedZones.primaryDbName))).run("create table encrypted_table (id int, value string)").run("insert into table encrypted_table values (1,'value1')").run("insert into table encrypted_table values (2,'value2')").dump(TestReplicationOnHDFSEncryptedZones.primaryDbName, null);
        replica.run(((((("repl load " + (TestReplicationOnHDFSEncryptedZones.replicatedDbName)) + " from '") + (tuple.dumpLocation)) + "' with('hive.repl.add.raw.reserved.namespace'='true', ") + "'distcp.options.pugpbx'='', 'distcp.options.skipcrccheck'='')")).run(("use " + (TestReplicationOnHDFSEncryptedZones.replicatedDbName))).run(("repl status " + (TestReplicationOnHDFSEncryptedZones.replicatedDbName))).verifyResult(tuple.lastReplicationId).run("select value from encrypted_table").verifyFailure(new String[]{ "value1", "value2" });
    }
}

