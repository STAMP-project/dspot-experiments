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
package org.apache.hive.jdbc.miniHS2;


import ConfVars.HIVE_SUPPORT_CONCURRENCY;
import ConfVars.HIVE_SUPPORT_CONCURRENCY.varname;
import ConfVars.HIVE_ZOOKEEPER_SESSION_TIMEOUT;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Test;


public class TestMiniHS2 {
    private MiniHS2 miniHS2;

    /**
     * Test if the MiniHS2 configuration gets passed down to the session
     * configuration
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConfInSession() throws Exception {
        HiveConf hiveConf = new HiveConf();
        final String DUMMY_CONF_KEY = "hive.test.minihs2.dummy.config";
        final String DUMMY_CONF_VAL = "dummy.val";
        hiveConf.set(DUMMY_CONF_KEY, DUMMY_CONF_VAL);
        // also check a config that has default in hiveconf
        final String ZK_TIMEOUT_KEY = HIVE_ZOOKEEPER_SESSION_TIMEOUT.varname;
        final String ZK_TIMEOUT = "2562";
        hiveConf.set(ZK_TIMEOUT_KEY, ZK_TIMEOUT);
        // check the config used very often!
        hiveConf.setBoolVar(HIVE_SUPPORT_CONCURRENCY, false);
        miniHS2 = new MiniHS2(hiveConf);
        miniHS2.start(new HashMap<String, String>());
        Connection hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL(), System.getProperty("user.name"), "bar");
        Statement stmt = hs2Conn.createStatement();
        checkConfVal(DUMMY_CONF_KEY, ((DUMMY_CONF_KEY + "=") + DUMMY_CONF_VAL), stmt);
        checkConfVal(ZK_TIMEOUT_KEY, ((ZK_TIMEOUT_KEY + "=") + ZK_TIMEOUT), stmt);
        checkConfVal(varname, (((HIVE_SUPPORT_CONCURRENCY.varname) + "=") + "false"), stmt);
        stmt.close();
        hs2Conn.close();
    }
}

