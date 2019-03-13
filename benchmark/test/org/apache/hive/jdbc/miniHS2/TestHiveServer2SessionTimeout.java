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
import org.apache.hadoop.util.StringUtils;
import org.apache.hive.service.cli.CLIServiceClient;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.SessionHandle;
import org.junit.Assert;
import org.junit.Test;


public class TestHiveServer2SessionTimeout {
    private static MiniHS2 miniHS2 = null;

    private Map<String, String> confOverlay;

    @Test
    public void testConnection() throws Exception {
        CLIServiceClient serviceClient = TestHiveServer2SessionTimeout.miniHS2.getServiceClient();
        SessionHandle sessHandle = serviceClient.openSession("foo", "bar");
        OperationHandle handle = serviceClient.executeStatement(sessHandle, "SELECT 1", confOverlay);
        Thread.sleep(7000);
        try {
            serviceClient.closeOperation(handle);
            Assert.fail("Operation should have been closed by timeout!");
        } catch (HiveSQLException e) {
            Assert.assertTrue(StringUtils.stringifyException(e), e.getMessage().contains("Invalid OperationHandle"));
        }
    }
}

