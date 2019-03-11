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
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.session.HiveSessionHook;
import org.apache.hive.service.cli.session.HiveSessionHookContext;
import org.junit.Test;


public class TestMultiSessionsHS2WithLocalClusterSpark {
    public static final String TEST_TAG = "miniHS2.localClusterSpark.tag";

    public static final String TEST_TAG_VALUE = "miniHS2.localClusterSpark.value";

    private static final int PARALLEL_NUMBER = 3;

    public static class LocalClusterSparkSessionHook implements HiveSessionHook {
        @Override
        public void run(HiveSessionHookContext sessionHookContext) throws HiveSQLException {
            sessionHookContext.getSessionConf().set(TestMultiSessionsHS2WithLocalClusterSpark.TEST_TAG, TestMultiSessionsHS2WithLocalClusterSpark.TEST_TAG_VALUE);
        }
    }

    private static MiniHS2 miniHS2 = null;

    private static HiveConf conf;

    private static Path dataFilePath;

    private static String dbName = "sparkTestDb";

    private ThreadLocal<Connection> localConnection = new ThreadLocal<Connection>();

    private ThreadLocal<Statement> localStatement = new ThreadLocal<Statement>();

    private ExecutorService pool = null;

    /**
     * Run nonSpark query
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNonSparkQuery() throws Exception {
        String tableName = "kvTable1";
        setupTable(tableName);
        Callable<Void> runNonSparkQuery = getNonSparkQueryCallable(tableName);
        runInParallel(runNonSparkQuery);
        dropTable(tableName);
    }

    /**
     * Run spark query
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSparkQuery() throws Exception {
        String tableName = "kvTable2";
        setupTable(tableName);
        Callable<Void> runSparkQuery = getSparkQueryCallable(tableName);
        runInParallel(runSparkQuery);
        dropTable(tableName);
    }
}

