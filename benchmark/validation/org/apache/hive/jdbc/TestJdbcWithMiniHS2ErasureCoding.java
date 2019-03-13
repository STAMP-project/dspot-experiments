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


import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.processors.ErasureProcessor;
import org.apache.hadoop.hive.shims.HadoopShims;
import org.apache.hadoop.hive.shims.HadoopShims.HdfsErasureCodingShim;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.apache.logging.log4j.core.Appender;
import org.junit.Assert;
import org.junit.Test;


/**
 * Run erasure Coding tests with jdbc.
 */
public class TestJdbcWithMiniHS2ErasureCoding {
    private static final String DB_NAME = "ecTestDb";

    private static MiniHS2 miniHS2 = null;

    private static HiveConf conf;

    private Connection hs2Conn = null;

    /**
     * Test EXPLAIN on fs with Erasure Coding.
     */
    @Test
    public void testExplainErasureCoding() throws Exception {
        try (Statement stmt = hs2Conn.createStatement()) {
            String tableName = "pTableEc";
            stmt.execute(((((" CREATE TABLE " + tableName) + " (userid VARCHAR(64), link STRING, source STRING) ") + "PARTITIONED BY (datestamp STRING, i int) ") + "CLUSTERED BY (userid) INTO 4 BUCKETS STORED AS PARQUET"));
            // insert data to create 2 partitions
            stmt.execute((("INSERT INTO TABLE " + tableName) + " PARTITION (datestamp = '2014-09-23', i = 1)(userid,link) VALUES ('jsmith', 'mail.com')"));
            stmt.execute((("INSERT INTO TABLE " + tableName) + " PARTITION (datestamp = '2014-09-24', i = 2)(userid,link) VALUES ('mac', 'superchunk.com')"));
            String explain = TestJdbcWithMiniHS2ErasureCoding.getExtendedExplain(stmt, ("select userid from " + tableName));
            assertMatchAndCount(explain, " numFiles 4", 2);
            assertMatchAndCount(explain, " numFilesErasureCoded 4", 2);
        }
    }

    /**
     * Test DESCRIBE on fs with Erasure Coding.
     */
    @Test
    public void testDescribeErasureCoding() throws Exception {
        try (Statement stmt = hs2Conn.createStatement()) {
            String table = "pageviews";
            stmt.execute((((" CREATE TABLE " + table) + " (userid VARCHAR(64), link STRING, source STRING) ") + "PARTITIONED BY (datestamp STRING, i int) CLUSTERED BY (userid) INTO 4 BUCKETS STORED AS PARQUET"));
            stmt.execute(((("INSERT INTO TABLE " + table) + " PARTITION (datestamp = '2014-09-23', i = 1)") + "(userid,link) VALUES ('jsmith', 'mail.com')"));
            stmt.execute(((("INSERT INTO TABLE " + table) + " PARTITION (datestamp = '2014-09-24', i = 1)") + "(userid,link) VALUES ('dpatel', 'gmail.com')"));
            String description = TestJdbcWithMiniHS2.getDetailedTableDescription(stmt, table);
            assertMatchAndCount(description, "numFiles=8", 1);
            assertMatchAndCount(description, "numFilesErasureCoded=8", 1);
            assertMatchAndCount(description, "numPartitions=2", 1);
        }
    }

    /**
     * Test MR stats.
     */
    @Test
    public void testMapRedStats() throws Exception {
        // Do log4j magic to save log output
        StringWriter writer = new StringWriter();
        Appender appender = addAppender(writer, "testMapRedStats");
        try (Statement stmt = hs2Conn.createStatement()) {
            String table = "mapredstats";
            stmt.execute("set hive.execution.engine=mr");
            stmt.execute(((" CREATE TABLE " + table) + " (a int) STORED AS PARQUET"));
            stmt.execute((("INSERT INTO TABLE " + table) + " VALUES (3)"));
            try (ResultSet rs = stmt.executeQuery((("select a from " + table) + " order by a"))) {
                while (rs.next()) {
                    int val = rs.getInt(1);
                    Assert.assertEquals(3, val);
                } 
            }
        }
        String output = writer.toString();
        // check for standard stats
        Assert.assertTrue(output.contains("HDFS Read:"));
        Assert.assertTrue(output.contains("HDFS Write:"));
        // check for erasure coding stat
        HadoopShims.HdfsErasureCodingShim erasureShim = ErasureProcessor.getErasureShim(TestJdbcWithMiniHS2ErasureCoding.conf);
        if (erasureShim.isMapReduceStatAvailable()) {
            Assert.assertTrue(output.contains("HDFS EC Read:"));
        }
    }
}

