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
package org.apache.hadoop.hbase.client;


import HConstants.HBASE_CLIENT_OPERATION_TIMEOUT;
import HConstants.HBASE_RPC_READ_TIMEOUT_KEY;
import HConstants.HBASE_RPC_TIMEOUT_KEY;
import HConstants.HBASE_RPC_WRITE_TIMEOUT_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.client.AbstractTestCITimeout.SleepCoprocessor.SLEEP_TIME;


/**
 * Based class for testing rpc timeout logic for {@link ConnectionImplementation}.
 */
public abstract class AbstractTestCIRpcTimeout extends AbstractTestCITimeout {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestCIRpcTimeout.class);

    private TableName tableName;

    @Test
    public void testRpcTimeout() throws IOException {
        Configuration c = new Configuration(AbstractTestCITimeout.TEST_UTIL.getConfiguration());
        try (Table table = AbstractTestCITimeout.TEST_UTIL.getConnection().getTableBuilder(tableName, null).setRpcTimeout(((SLEEP_TIME) / 2)).setReadRpcTimeout(((SLEEP_TIME) / 2)).setWriteRpcTimeout(((SLEEP_TIME) / 2)).setOperationTimeout(((SLEEP_TIME) * 100)).build()) {
            execute(table);
            Assert.fail("Get should not have succeeded");
        } catch (RetriesExhaustedException e) {
            AbstractTestCIRpcTimeout.LOG.info("We received an exception, as expected ", e);
        }
        // Again, with configuration based override
        c.setInt(HBASE_RPC_TIMEOUT_KEY, ((SLEEP_TIME) / 2));
        c.setInt(HBASE_RPC_READ_TIMEOUT_KEY, ((SLEEP_TIME) / 2));
        c.setInt(HBASE_RPC_WRITE_TIMEOUT_KEY, ((SLEEP_TIME) / 2));
        c.setInt(HBASE_CLIENT_OPERATION_TIMEOUT, ((SLEEP_TIME) * 100));
        try (Connection conn = ConnectionFactory.createConnection(c)) {
            try (Table table = conn.getTable(tableName)) {
                execute(table);
                Assert.fail("Get should not have succeeded");
            } catch (RetriesExhaustedException e) {
                AbstractTestCIRpcTimeout.LOG.info("We received an exception, as expected ", e);
            }
        }
    }
}

