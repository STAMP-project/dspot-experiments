/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.jdbc;


import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


public class TestRestrictedList {
    private static MiniHS2 miniHS2 = null;

    private static URL oldHiveSiteURL = null;

    private static URL oldHiveMetastoreSiteURL = null;

    private static Map<String, String> expectedRestrictedMap = new HashMap<>();

    private static HiveConf hiveConf = null;

    @Test
    public void testRestrictedList() throws Exception {
        Assert.assertTrue("Test setup failed. MiniHS2 is not initialized", (((TestRestrictedList.miniHS2) != null) && (TestRestrictedList.miniHS2.isStarted())));
        checkRestrictedListMatch();
        try (Connection hs2Conn = DriverManager.getConnection(TestRestrictedList.miniHS2.getJdbcURL(), "hive", "hive");Statement stmt = hs2Conn.createStatement()) {
            for (Map.Entry<String, String> entry : TestRestrictedList.expectedRestrictedMap.entrySet()) {
                String parameter = entry.getKey();
                String value = entry.getValue();
                try {
                    stmt.execute(((("set " + parameter) + "=") + value));
                    Assert.fail(("Exception not thrown for parameter: " + parameter));
                } catch (Exception e1) {
                    Assert.assertTrue(("Unexpected exception: " + (e1.getMessage())), e1.getMessage().contains("Error while processing statement: Cannot modify"));
                }
            }
        } catch (Exception e2) {
            Assert.fail(("Unexpected Exception: " + (e2.getMessage())));
        }
    }
}

