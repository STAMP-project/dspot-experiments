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
package org.apache.hive.service.auth;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.AuthenticationException;
import junit.framework.Assert;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.server.HiveServer2;
import org.junit.Test;


public class TestCustomAuthentication {
    private static HiveServer2 hiveserver2;

    private static HiveConf hiveConf;

    private static byte[] hiveConfBackup;

    @Test
    public void testCustomAuthentication() throws Exception {
        String url = "jdbc:hive2://localhost:10000/default";
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        try {
            DriverManager.getConnection(url, "wronguser", "pwd");
            Assert.fail("Expected Exception");
        } catch (SQLException e) {
            Assert.assertNotNull(e.getMessage());
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("Peer indicated failure: Error validating the login"));
        }
        Connection connection = DriverManager.getConnection(url, "hiveuser", "hive");
        connection.close();
        System.out.println(">>> PASSED testCustomAuthentication");
    }

    public static class SimpleAuthenticationProviderImpl implements PasswdAuthenticationProvider {
        private Map<String, String> userMap = new HashMap<String, String>();

        public SimpleAuthenticationProviderImpl() {
            init();
        }

        private void init() {
            userMap.put("hiveuser", "hive");
        }

        @Override
        public void Authenticate(String user, String password) throws AuthenticationException {
            if (!(userMap.containsKey(user))) {
                throw new AuthenticationException(("Invalid user : " + user));
            }
            if (!(userMap.get(user).equals(password))) {
                throw new AuthenticationException(("Invalid passwd : " + password));
            }
        }
    }
}

