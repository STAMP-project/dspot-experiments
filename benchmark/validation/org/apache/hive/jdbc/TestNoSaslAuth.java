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
package org.apache.hive.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import junit.framework.Assert;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.session.HiveSessionHook;
import org.apache.hive.service.cli.session.HiveSessionHookContext;
import org.junit.Test;


public class TestNoSaslAuth {
    private static MiniHS2 miniHS2 = null;

    private static String sessionUserName = "";

    public static class NoSaslSessionHook implements HiveSessionHook {
        public static boolean checkUser = false;

        @Override
        public void run(HiveSessionHookContext sessionHookContext) throws HiveSQLException {
            if (TestNoSaslAuth.NoSaslSessionHook.checkUser) {
                Assert.assertEquals(sessionHookContext.getSessionUser(), TestNoSaslAuth.sessionUserName);
            }
        }
    }

    private Connection hs2Conn = null;

    /**
     * Initiate a non-sasl connection. The session hook will verfiy the user name
     * set correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnection() throws Exception {
        TestNoSaslAuth.sessionUserName = "user1";
        hs2Conn = DriverManager.getConnection(((TestNoSaslAuth.miniHS2.getJdbcURL()) + ";auth=noSasl"), TestNoSaslAuth.sessionUserName, "foo");
    }
}

