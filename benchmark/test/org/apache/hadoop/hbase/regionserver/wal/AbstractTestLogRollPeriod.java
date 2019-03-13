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
package org.apache.hadoop.hbase.regionserver.wal;


import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests that verifies that the log is forced to be rolled every "hbase.regionserver.logroll.period"
 */
public abstract class AbstractTestLogRollPeriod {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestLogRollPeriod.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final long LOG_ROLL_PERIOD = 4000;

    /**
     * Tests that the LogRoller perform the roll even if there are no edits
     */
    @Test
    public void testNoEdits() throws Exception {
        TableName tableName = TableName.valueOf("TestLogRollPeriodNoEdits");
        AbstractTestLogRollPeriod.TEST_UTIL.createTable(tableName, "cf");
        try {
            Table table = AbstractTestLogRollPeriod.TEST_UTIL.getConnection().getTable(tableName);
            try {
                HRegionServer server = AbstractTestLogRollPeriod.TEST_UTIL.getRSForFirstRegionInTable(tableName);
                WAL log = server.getWAL(null);
                checkMinLogRolls(log, 5);
            } finally {
                table.close();
            }
        } finally {
            AbstractTestLogRollPeriod.TEST_UTIL.deleteTable(tableName);
        }
    }

    /**
     * Tests that the LogRoller perform the roll with some data in the log
     */
    @Test
    public void testWithEdits() throws Exception {
        final TableName tableName = TableName.valueOf("TestLogRollPeriodWithEdits");
        final String family = "cf";
        AbstractTestLogRollPeriod.TEST_UTIL.createTable(tableName, family);
        try {
            HRegionServer server = AbstractTestLogRollPeriod.TEST_UTIL.getRSForFirstRegionInTable(tableName);
            WAL log = server.getWAL(null);
            final Table table = AbstractTestLogRollPeriod.TEST_UTIL.getConnection().getTable(tableName);
            Thread writerThread = new Thread("writer") {
                @Override
                public void run() {
                    try {
                        long row = 0;
                        while (!(Thread.interrupted())) {
                            Put p = new Put(Bytes.toBytes(String.format("row%d", row)));
                            p.addColumn(Bytes.toBytes(family), Bytes.toBytes("col"), Bytes.toBytes(row));
                            table.put(p);
                            row++;
                            Thread.sleep(((AbstractTestLogRollPeriod.LOG_ROLL_PERIOD) / 16));
                        } 
                    } catch (Exception e) {
                        AbstractTestLogRollPeriod.LOG.warn(e.toString(), e);
                    }
                }
            };
            try {
                writerThread.start();
                checkMinLogRolls(log, 5);
            } finally {
                writerThread.interrupt();
                writerThread.join();
                table.close();
            }
        } finally {
            AbstractTestLogRollPeriod.TEST_UTIL.deleteTable(tableName);
        }
    }
}

