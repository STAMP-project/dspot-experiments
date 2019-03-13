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
package org.apache.hive.service.cli.session;


import HiveConf.ConfVars.HIVE_EXECUTION_ENGINE;
import HiveConf.ConfVars.HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT;
import HiveConf.ConfVars.HIVE_SERVER2_WEBUI_MAX_GRAPH_SIZE;
import HiveConf.ConfVars.HIVE_SERVER2_WEBUI_SHOW_GRAPH;
import HiveConf.ConfVars.HIVE_SERVER2_WEBUI_SHOW_STATS;
import TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.QueryInfo;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.rpc.thrift.TProtocolVersion;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test QueryDisplay and its consumers like WebUI.
 */
public class TestQueryDisplay {
    private HiveConf conf;

    private SessionManager sessionManager;

    /**
     * Test if query display captures information on current/historic SQL operations.
     */
    @Test
    public void testQueryDisplay() throws Exception {
        HiveSession session = sessionManager.createSession(new org.apache.hive.service.cli.SessionHandle(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8), HIVE_CLI_SERVICE_PROTOCOL_V8, "testuser", "", "", new HashMap<String, String>(), false, "");
        SessionState.start(conf);
        OperationHandle opHandle1 = session.executeStatement("show databases", null);
        SessionState.start(conf);
        OperationHandle opHandle2 = session.executeStatement("show tables", null);
        List<QueryInfo> liveSqlOperations;
        List<QueryInfo> historicSqlOperations;
        liveSqlOperations = sessionManager.getOperationManager().getLiveQueryInfos();
        historicSqlOperations = sessionManager.getOperationManager().getHistoricalQueryInfos();
        Assert.assertEquals(liveSqlOperations.size(), 2);
        Assert.assertEquals(historicSqlOperations.size(), 0);
        verifyDDL(liveSqlOperations.get(0), "show databases", opHandle1.getHandleIdentifier().toString(), false);
        verifyDDL(liveSqlOperations.get(1), "show tables", opHandle2.getHandleIdentifier().toString(), false);
        session.closeOperation(opHandle1);
        liveSqlOperations = sessionManager.getOperationManager().getLiveQueryInfos();
        historicSqlOperations = sessionManager.getOperationManager().getHistoricalQueryInfos();
        Assert.assertEquals(liveSqlOperations.size(), 1);
        Assert.assertEquals(historicSqlOperations.size(), 1);
        verifyDDL(historicSqlOperations.get(0), "show databases", opHandle1.getHandleIdentifier().toString(), true);
        verifyDDL(liveSqlOperations.get(0), "show tables", opHandle2.getHandleIdentifier().toString(), false);
        session.closeOperation(opHandle2);
        liveSqlOperations = sessionManager.getOperationManager().getLiveQueryInfos();
        historicSqlOperations = sessionManager.getOperationManager().getHistoricalQueryInfos();
        Assert.assertEquals(liveSqlOperations.size(), 0);
        Assert.assertEquals(historicSqlOperations.size(), 2);
        verifyDDL(historicSqlOperations.get(1), "show databases", opHandle1.getHandleIdentifier().toString(), true);
        verifyDDL(historicSqlOperations.get(0), "show tables", opHandle2.getHandleIdentifier().toString(), true);
        session.close();
    }

    /**
     * Test if webui captures information on current/historic SQL operations.
     */
    @Test
    public void testWebUI() throws Exception {
        HiveSession session = sessionManager.createSession(new org.apache.hive.service.cli.SessionHandle(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8), HIVE_CLI_SERVICE_PROTOCOL_V8, "testuser", "", "", new HashMap<String, String>(), false, "");
        SessionState.start(conf);
        OperationHandle opHandle1 = session.executeStatement("show databases", null);
        SessionState.start(conf);
        OperationHandle opHandle2 = session.executeStatement("show tables", null);
        verifyDDLHtml("show databases", opHandle1.getHandleIdentifier().toString());
        verifyDDLHtml("show tables", opHandle2.getHandleIdentifier().toString());
        session.closeOperation(opHandle1);
        session.closeOperation(opHandle2);
        verifyDDLHtml("show databases", opHandle1.getHandleIdentifier().toString());
        verifyDDLHtml("show tables", opHandle2.getHandleIdentifier().toString());
        session.close();
    }

    /**
     * Test for the HiveConf option HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT.
     */
    @Test
    public void checkWebuiExplainOutput() throws Exception {
        // check cases when HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT is set and not set
        boolean[] webuiExplainConfValues = new boolean[]{ true, false };
        for (boolean confValue : webuiExplainConfValues) {
            conf.setBoolVar(HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT, confValue);
            HiveSession session = sessionManager.createSession(new org.apache.hive.service.cli.SessionHandle(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8), HIVE_CLI_SERVICE_PROTOCOL_V8, "testuser", "", "", new HashMap<String, String>(), false, "");
            SessionState.start(conf);
            OperationHandle opHandle = session.executeStatement("show tables", null);
            session.closeOperation(opHandle);
            // STAGE PLANS is something which will be shown as part of EXPLAIN query
            verifyDDLHtml("STAGE PLANS", opHandle.getHandleIdentifier().toString(), confValue);
            // Check that the following message is not shown when this option is set
            verifyDDLHtml("Set configuration hive.server2.webui.explain.output to true to view future query plans", opHandle.getHandleIdentifier().toString(), (!confValue));
            session.close();
        }
    }

    /**
     * Test for the HiveConf options HIVE_SERVER2_WEBUI_SHOW_GRAPH,
     * HIVE_SERVER2_WEBUI_MAX_GRAPH_SIZE.
     */
    @Test
    public void checkWebuiShowGraph() throws Exception {
        // WebUI-related boolean confs must be set before build, since the implementation of
        // QueryProfileTmpl.jamon depends on them.
        // They depend on HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT being set to true.
        conf.setBoolVar(HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT, true);
        conf.setBoolVar(HIVE_SERVER2_WEBUI_SHOW_GRAPH, true);
        HiveSession session = sessionManager.createSession(new org.apache.hive.service.cli.SessionHandle(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8), HIVE_CLI_SERVICE_PROTOCOL_V8, "testuser", "", "", new HashMap<String, String>(), false, "");
        SessionState.start(conf);
        session.getSessionConf().setIntVar(HIVE_SERVER2_WEBUI_MAX_GRAPH_SIZE, 0);
        testGraphDDL(session, true);
        session.getSessionConf().setIntVar(HIVE_SERVER2_WEBUI_MAX_GRAPH_SIZE, 40);
        testGraphDDL(session, false);
        session.close();
        resetConfToDefaults();
    }

    /**
     * Test for the HiveConf option HIVE_SERVER2_WEBUI_SHOW_STATS, which is available for MapReduce
     * jobs only.
     */
    @Test
    public void checkWebUIShowStats() throws Exception {
        // WebUI-related boolean confs must be set before build. HIVE_SERVER2_WEBUI_SHOW_STATS depends
        // on HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT and HIVE_SERVER2_WEBUI_SHOW_GRAPH being set to true.
        conf.setVar(HIVE_EXECUTION_ENGINE, "mr");
        conf.setBoolVar(HIVE_SERVER2_WEBUI_EXPLAIN_OUTPUT, true);
        conf.setBoolVar(HIVE_SERVER2_WEBUI_SHOW_GRAPH, true);
        conf.setIntVar(HIVE_SERVER2_WEBUI_MAX_GRAPH_SIZE, 40);
        conf.setBoolVar(HIVE_SERVER2_WEBUI_SHOW_STATS, true);
        HiveSession session = sessionManager.createSession(new org.apache.hive.service.cli.SessionHandle(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8), HIVE_CLI_SERVICE_PROTOCOL_V8, "testuser", "", "", new HashMap<String, String>(), false, "");
        SessionState.start(conf);
        OperationHandle opHandleSetup = session.executeStatement("CREATE TABLE statsTable (i int)", null);
        session.closeOperation(opHandleSetup);
        OperationHandle opHandleMrQuery = session.executeStatement("INSERT INTO statsTable VALUES (0)", null);
        session.closeOperation(opHandleMrQuery);
        // INSERT queries include  a MapReduce task.
        verifyDDLHtml("Counters", opHandleMrQuery.getHandleIdentifier().toString(), true);
        session.close();
        resetConfToDefaults();
    }
}

