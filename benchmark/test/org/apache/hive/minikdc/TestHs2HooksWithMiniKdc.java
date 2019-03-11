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
package org.apache.hive.minikdc;


import PostExecHook.ipAddress;
import PostExecHook.operation;
import PostExecHook.userName;
import SemanticAnalysisHook.command;
import SemanticAnalysisHook.commandType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hive.hooks.TestHs2Hooks.PostExecHook;
import org.apache.hadoop.hive.hooks.TestHs2Hooks.PreExecHook;
import org.apache.hadoop.hive.hooks.TestHs2Hooks.SemanticAnalysisHook;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests information retrieved from hooks, in Kerberos mode.
 */
public class TestHs2HooksWithMiniKdc {
    private static MiniHS2 miniHS2 = null;

    private static MiniHiveKdc miniHiveKdc = null;

    private static Map<String, String> confOverlay = new HashMap<String, String>();

    private Connection hs2Conn;

    /**
     * Test that hook context properties are correctly set.
     */
    @Test
    public void testHookContexts() throws Throwable {
        TestHs2HooksWithMiniKdc.miniHiveKdc.loginUser(MiniHiveKdc.HIVE_TEST_USER_1);
        hs2Conn = DriverManager.getConnection(TestHs2HooksWithMiniKdc.miniHS2.getJdbcURL());
        Statement stmt = hs2Conn.createStatement();
        stmt.executeQuery("show databases");
        stmt.executeQuery("show tables");
        Throwable error = PostExecHook.error;
        if (error != null) {
            throw error;
        }
        error = PreExecHook.error;
        if (error != null) {
            throw error;
        }
        Assert.assertNotNull("ipaddress is null", ipAddress);
        Assert.assertNotNull("userName is null", userName);
        Assert.assertNotNull("operation is null", operation);
        Assert.assertEquals(MiniHiveKdc.HIVE_TEST_USER_1, userName);
        Assert.assertTrue(ipAddress, ipAddress.contains("127.0.0.1"));
        Assert.assertEquals("SHOWTABLES", operation);
        Assert.assertNotNull("ipaddress is null", PreExecHook.ipAddress);
        Assert.assertNotNull("userName is null", PreExecHook.userName);
        Assert.assertNotNull("operation is null", PreExecHook.operation);
        Assert.assertEquals(MiniHiveKdc.HIVE_TEST_USER_1, PreExecHook.userName);
        Assert.assertTrue(PreExecHook.ipAddress, PreExecHook.ipAddress.contains("127.0.0.1"));
        Assert.assertEquals("SHOWTABLES", PreExecHook.operation);
        error = SemanticAnalysisHook.preAnalyzeError;
        if (error != null) {
            throw error;
        }
        error = SemanticAnalysisHook.postAnalyzeError;
        if (error != null) {
            throw error;
        }
        Assert.assertNotNull("semantic hook context ipaddress is null", SemanticAnalysisHook.ipAddress);
        Assert.assertNotNull("semantic hook context userName is null", SemanticAnalysisHook.userName);
        Assert.assertNotNull("semantic hook context command is null", command);
        Assert.assertNotNull("semantic hook context commandType is null", commandType);
        Assert.assertTrue(SemanticAnalysisHook.ipAddress, SemanticAnalysisHook.ipAddress.contains("127.0.0.1"));
        Assert.assertEquals("show tables", command);
    }
}

