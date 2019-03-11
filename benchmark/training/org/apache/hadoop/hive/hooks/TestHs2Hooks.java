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
/**
 * The tests here are heavily based on some timing, so there is some chance to fail.
 */
package org.apache.hadoop.hive.hooks;


import HiveOperation.CREATETABLE_AS_SELECT;
import HookType.POST_EXEC_HOOK;
import HookType.PRE_EXEC_HOOK;
import java.io.Serializable;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.hooks.ExecuteWithHookContext;
import org.apache.hadoop.hive.ql.hooks.HookContext;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveSemanticAnalyzerHook;
import org.apache.hadoop.hive.ql.parse.HiveSemanticAnalyzerHookContext;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.plan.HiveOperation;
import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.service.server.HiveServer2;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests information retrieved from hooks.
 */
public class TestHs2Hooks {
    private static final Logger LOG = LoggerFactory.getLogger(TestHs2Hooks.class);

    private static HiveServer2 hiveServer2;

    public static class PostExecHook implements ExecuteWithHookContext {
        public static String userName;

        public static String ipAddress;

        public static String operation;

        public static Throwable error;

        @Override
        public void run(HookContext hookContext) {
            try {
                if (hookContext.getHookType().equals(POST_EXEC_HOOK)) {
                    TestHs2Hooks.PostExecHook.ipAddress = hookContext.getIpAddress();
                    TestHs2Hooks.PostExecHook.userName = hookContext.getUserName();
                    TestHs2Hooks.PostExecHook.operation = hookContext.getOperationName();
                }
            } catch (Throwable t) {
                TestHs2Hooks.LOG.error(("Error in PostExecHook: " + t), t);
                TestHs2Hooks.PostExecHook.error = t;
            }
        }
    }

    public static class PreExecHook implements ExecuteWithHookContext {
        public static String userName;

        public static String ipAddress;

        public static String operation;

        public static Throwable error;

        @Override
        public void run(HookContext hookContext) {
            try {
                if (hookContext.getHookType().equals(PRE_EXEC_HOOK)) {
                    TestHs2Hooks.PreExecHook.ipAddress = hookContext.getIpAddress();
                    TestHs2Hooks.PreExecHook.userName = hookContext.getUserName();
                    TestHs2Hooks.PreExecHook.operation = hookContext.getOperationName();
                }
            } catch (Throwable t) {
                TestHs2Hooks.LOG.error(("Error in PreExecHook: " + t), t);
                TestHs2Hooks.PreExecHook.error = t;
            }
        }
    }

    public static class SemanticAnalysisHook implements HiveSemanticAnalyzerHook {
        public static String userName;

        public static String command;

        public static HiveOperation commandType;

        public static String ipAddress;

        public static Throwable preAnalyzeError;

        public static Throwable postAnalyzeError;

        @Override
        public ASTNode preAnalyze(HiveSemanticAnalyzerHookContext context, ASTNode ast) throws SemanticException {
            try {
                TestHs2Hooks.SemanticAnalysisHook.userName = context.getUserName();
                TestHs2Hooks.SemanticAnalysisHook.ipAddress = context.getIpAddress();
                TestHs2Hooks.SemanticAnalysisHook.command = context.getCommand();
                TestHs2Hooks.SemanticAnalysisHook.commandType = context.getHiveOperation();
            } catch (Throwable t) {
                TestHs2Hooks.LOG.error(("Error in semantic analysis hook preAnalyze: " + t), t);
                TestHs2Hooks.SemanticAnalysisHook.preAnalyzeError = t;
            }
            return ast;
        }

        @Override
        public void postAnalyze(HiveSemanticAnalyzerHookContext context, List<Task<? extends Serializable>> rootTasks) throws SemanticException {
            try {
                TestHs2Hooks.SemanticAnalysisHook.userName = context.getUserName();
                TestHs2Hooks.SemanticAnalysisHook.ipAddress = context.getIpAddress();
                TestHs2Hooks.SemanticAnalysisHook.command = context.getCommand();
                TestHs2Hooks.SemanticAnalysisHook.commandType = context.getHiveOperation();
            } catch (Throwable t) {
                TestHs2Hooks.LOG.error(("Error in semantic analysis hook postAnalyze: " + t), t);
                TestHs2Hooks.SemanticAnalysisHook.postAnalyzeError = t;
            }
        }
    }

    /**
     * Test that hook context properties are correctly set.
     */
    @Test
    public void testHookContexts() throws Throwable {
        Properties connProp = new Properties();
        connProp.setProperty("user", System.getProperty("user.name"));
        connProp.setProperty("password", "");
        HiveConnection connection = new HiveConnection("jdbc:hive2://localhost:10000/default", connProp);
        Statement stmt = connection.createStatement();
        stmt.executeQuery("show databases");
        stmt.executeQuery("show tables");
        Throwable error = TestHs2Hooks.PostExecHook.error;
        if (error != null) {
            throw error;
        }
        error = TestHs2Hooks.PreExecHook.error;
        if (error != null) {
            throw error;
        }
        Assert.assertEquals(System.getProperty("user.name"), TestHs2Hooks.PostExecHook.userName);
        Assert.assertNotNull(TestHs2Hooks.PostExecHook.ipAddress, "ipaddress is null");
        Assert.assertNotNull(TestHs2Hooks.PostExecHook.userName, "userName is null");
        Assert.assertNotNull(TestHs2Hooks.PostExecHook.operation, "operation is null");
        Assert.assertTrue(TestHs2Hooks.PostExecHook.ipAddress, TestHs2Hooks.PostExecHook.ipAddress.contains("127.0.0.1"));
        Assert.assertEquals("SHOWTABLES", TestHs2Hooks.PostExecHook.operation);
        Assert.assertEquals(System.getProperty("user.name"), TestHs2Hooks.PreExecHook.userName);
        Assert.assertNotNull("ipaddress is null", TestHs2Hooks.PreExecHook.ipAddress);
        Assert.assertNotNull("userName is null", TestHs2Hooks.PreExecHook.userName);
        Assert.assertNotNull("operation is null", TestHs2Hooks.PreExecHook.operation);
        Assert.assertTrue(TestHs2Hooks.PreExecHook.ipAddress, TestHs2Hooks.PreExecHook.ipAddress.contains("127.0.0.1"));
        Assert.assertEquals("SHOWTABLES", TestHs2Hooks.PreExecHook.operation);
        error = TestHs2Hooks.SemanticAnalysisHook.preAnalyzeError;
        if (error != null) {
            throw error;
        }
        error = TestHs2Hooks.SemanticAnalysisHook.postAnalyzeError;
        if (error != null) {
            throw error;
        }
        Assert.assertNotNull("semantic hook context ipaddress is null", TestHs2Hooks.SemanticAnalysisHook.ipAddress);
        Assert.assertNotNull("semantic hook context userName is null", TestHs2Hooks.SemanticAnalysisHook.userName);
        Assert.assertNotNull("semantic hook context command is null", TestHs2Hooks.SemanticAnalysisHook.command);
        Assert.assertNotNull("semantic hook context commandType is null", TestHs2Hooks.SemanticAnalysisHook.commandType);
        Assert.assertTrue(TestHs2Hooks.SemanticAnalysisHook.ipAddress, TestHs2Hooks.SemanticAnalysisHook.ipAddress.contains("127.0.0.1"));
        Assert.assertEquals("show tables", TestHs2Hooks.SemanticAnalysisHook.command);
        stmt.close();
        connection.close();
    }

    @Test
    public void testPostAnalysisHookContexts() throws Throwable {
        Properties connProp = new Properties();
        connProp.setProperty("user", System.getProperty("user.name"));
        connProp.setProperty("password", "");
        HiveConnection connection = new HiveConnection("jdbc:hive2://localhost:10000/default", connProp);
        Statement stmt = connection.createStatement();
        stmt.execute("create table testPostAnalysisHookContexts as select '3'");
        Throwable error = TestHs2Hooks.PostExecHook.error;
        if (error != null) {
            throw error;
        }
        error = TestHs2Hooks.PreExecHook.error;
        if (error != null) {
            throw error;
        }
        Assert.assertEquals(CREATETABLE_AS_SELECT, TestHs2Hooks.SemanticAnalysisHook.commandType);
        error = TestHs2Hooks.SemanticAnalysisHook.preAnalyzeError;
        if (error != null) {
            throw error;
        }
        error = TestHs2Hooks.SemanticAnalysisHook.postAnalyzeError;
        if (error != null) {
            throw error;
        }
        stmt.close();
        connection.close();
    }
}

