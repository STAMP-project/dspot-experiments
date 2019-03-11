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
package org.apache.hive.service.cli.thrift;


import ConfVars.HIVE_SERVER2_AUTHENTICATION;
import ConfVars.HIVE_SERVER2_ENABLE_DOAS;
import ConfVars.HIVE_SERVER2_THRIFT_BIND_HOST;
import ConfVars.HIVE_SERVER2_THRIFT_MAX_MESSAGE_SIZE;
import ConfVars.HIVE_SERVER2_THRIFT_PORT;
import ConfVars.HIVE_SERVER2_TRANSPORT_MODE;
import HiveAuthConstants.AuthTypes.NONE;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.server.HiveServer2;
import org.junit.Assert;
import org.junit.Test;


public class TestThriftCliServiceMessageSize {
    protected static int port;

    protected static String host = "localhost";

    protected static HiveServer2 hiveServer2;

    protected static ThriftCLIServiceClient client;

    protected static HiveConf hiveConf;

    protected static String USERNAME = "anonymous";

    protected static String PASSWORD = "anonymous";

    @Test
    public void testMessageSize() throws Exception {
        String transportMode = "binary";
        TestThriftCliServiceMessageSize.hiveConf.setBoolVar(HIVE_SERVER2_ENABLE_DOAS, false);
        TestThriftCliServiceMessageSize.hiveConf.setVar(HIVE_SERVER2_THRIFT_BIND_HOST, TestThriftCliServiceMessageSize.host);
        TestThriftCliServiceMessageSize.hiveConf.setIntVar(HIVE_SERVER2_THRIFT_PORT, TestThriftCliServiceMessageSize.port);
        TestThriftCliServiceMessageSize.hiveConf.setVar(HIVE_SERVER2_AUTHENTICATION, NONE.toString());
        TestThriftCliServiceMessageSize.hiveConf.setVar(HIVE_SERVER2_TRANSPORT_MODE, transportMode);
        HiveServer2 hiveServer2 = new HiveServer2();
        String url = ("jdbc:hive2://localhost:" + (TestThriftCliServiceMessageSize.port)) + "/default";
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        try {
            // First start HS2 with high message size limit. This should allow connections
            TestThriftCliServiceMessageSize.hiveConf.setIntVar(HIVE_SERVER2_THRIFT_MAX_MESSAGE_SIZE, ((100 * 1024) * 1024));
            TestThriftCliServiceMessageSize.startHiveServer2WithConf(hiveServer2, TestThriftCliServiceMessageSize.hiveConf);
            System.out.println(("Started Thrift CLI service with message size limit " + (TestThriftCliServiceMessageSize.hiveConf.getIntVar(HIVE_SERVER2_THRIFT_MAX_MESSAGE_SIZE))));
            // With the high message size limit this connection should work
            Connection connection = DriverManager.getConnection(url, "hiveuser", "hive");
            Statement stmt = connection.createStatement();
            Assert.assertNotNull("Statement is null", stmt);
            stmt.execute("set hive.support.concurrency = false");
            connection.close();
            TestThriftCliServiceMessageSize.stopHiveServer2(hiveServer2);
            // Now start HS2 with low message size limit. This should prevent any connections
            TestThriftCliServiceMessageSize.hiveConf.setIntVar(HIVE_SERVER2_THRIFT_MAX_MESSAGE_SIZE, 1);
            hiveServer2 = new HiveServer2();
            TestThriftCliServiceMessageSize.startHiveServer2WithConf(hiveServer2, TestThriftCliServiceMessageSize.hiveConf);
            System.out.println(("Started Thrift CLI service with message size limit " + (TestThriftCliServiceMessageSize.hiveConf.getIntVar(HIVE_SERVER2_THRIFT_MAX_MESSAGE_SIZE))));
            Exception caughtException = null;
            try {
                // This should fail
                connection = DriverManager.getConnection(url, "hiveuser", "hive");
            } catch (Exception err) {
                caughtException = err;
            }
            // Verify we hit an error while connecting
            Assert.assertNotNull(caughtException);
        } finally {
            TestThriftCliServiceMessageSize.stopHiveServer2(hiveServer2);
            hiveServer2 = null;
        }
    }
}

