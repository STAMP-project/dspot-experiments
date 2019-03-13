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


import java.util.Map;
import org.apache.hive.service.cli.CLIServiceClient;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.SessionHandle;
import org.junit.Assert;
import org.junit.Test;


public class TestHiveServer2 {
    private static MiniHS2 miniHS2 = null;

    private static Map<String, String> confOverlay;

    /**
     * Open a new session and run a test query
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnection() throws Exception {
        String tableName = "TestHiveServer2TestConnection";
        CLIServiceClient serviceClient = TestHiveServer2.miniHS2.getServiceClient();
        SessionHandle sessHandle = serviceClient.openSession("foo", "bar");
        serviceClient.executeStatement(sessHandle, ("DROP TABLE IF EXISTS " + tableName), TestHiveServer2.confOverlay);
        serviceClient.executeStatement(sessHandle, (("CREATE TABLE " + tableName) + " (id INT)"), TestHiveServer2.confOverlay);
        OperationHandle opHandle = serviceClient.executeStatement(sessHandle, "SHOW TABLES", TestHiveServer2.confOverlay);
        RowSet rowSet = serviceClient.fetchResults(opHandle);
        Assert.assertFalse(((rowSet.numRows()) == 0));
        serviceClient.executeStatement(sessHandle, ("DROP TABLE IF EXISTS " + tableName), TestHiveServer2.confOverlay);
        serviceClient.closeSession(sessHandle);
    }

    /**
     * Open a new session and execute a set command
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetVariableValue() throws Exception {
        CLIServiceClient serviceClient = TestHiveServer2.miniHS2.getServiceClient();
        SessionHandle sessHandle = serviceClient.openSession("foo", "bar");
        OperationHandle opHandle = serviceClient.executeStatement(sessHandle, "set system:os.name", TestHiveServer2.confOverlay);
        RowSet rowSet = serviceClient.fetchResults(opHandle);
        Assert.assertEquals(1, rowSet.numRows());
        serviceClient.closeSession(sessHandle);
    }
}

