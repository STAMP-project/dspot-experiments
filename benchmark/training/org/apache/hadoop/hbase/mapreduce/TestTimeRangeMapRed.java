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
package org.apache.hadoop.hbase.mapreduce;


import Durability.SKIP_WAL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.MapWritable;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MapReduceTests.class, LargeTests.class })
public class TestTimeRangeMapRed {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTimeRangeMapRed.class);

    private static final Logger log = LoggerFactory.getLogger(TestTimeRangeMapRed.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private Admin admin;

    private static final byte[] KEY = Bytes.toBytes("row1");

    private static final NavigableMap<Long, Boolean> TIMESTAMP = new TreeMap<>();

    static {
        TestTimeRangeMapRed.TIMESTAMP.put(((long) (1245620000)), false);
        TestTimeRangeMapRed.TIMESTAMP.put(((long) (1245620005)), true);// include

        TestTimeRangeMapRed.TIMESTAMP.put(((long) (1245620010)), true);// include

        TestTimeRangeMapRed.TIMESTAMP.put(((long) (1245620055)), true);// include

        TestTimeRangeMapRed.TIMESTAMP.put(((long) (1245620100)), true);// include

        TestTimeRangeMapRed.TIMESTAMP.put(((long) (1245620150)), false);
        TestTimeRangeMapRed.TIMESTAMP.put(((long) (1245620250)), false);
    }

    static final long MINSTAMP = 1245620005;

    static final long MAXSTAMP = 1245620100 + 1;// maxStamp itself is excluded. so increment it.


    static final TableName TABLE_NAME = TableName.valueOf("table123");

    static final byte[] FAMILY_NAME = Bytes.toBytes("text");

    static final byte[] COLUMN_NAME = Bytes.toBytes("input");

    private static class ProcessTimeRangeMapper extends TableMapper<ImmutableBytesWritable, MapWritable> implements Configurable {
        private Configuration conf = null;

        private Table table = null;

        @Override
        public void map(ImmutableBytesWritable key, Result result, Context context) throws IOException {
            List<Long> tsList = new ArrayList<>();
            for (Cell kv : result.listCells()) {
                tsList.add(kv.getTimestamp());
            }
            List<Put> puts = new ArrayList<>();
            for (Long ts : tsList) {
                Put put = new Put(key.get());
                put.setDurability(SKIP_WAL);
                put.addColumn(TestTimeRangeMapRed.FAMILY_NAME, TestTimeRangeMapRed.COLUMN_NAME, ts, Bytes.toBytes(true));
                puts.add(put);
            }
            table.put(puts);
        }

        @Override
        public Configuration getConf() {
            return conf;
        }

        @Override
        public void setConf(Configuration configuration) {
            this.conf = configuration;
            try {
                Connection connection = ConnectionFactory.createConnection(conf);
                table = connection.getTable(TestTimeRangeMapRed.TABLE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testTimeRangeMapRed() throws IOException, ClassNotFoundException, InterruptedException {
        final HTableDescriptor desc = new HTableDescriptor(TestTimeRangeMapRed.TABLE_NAME);
        final HColumnDescriptor col = new HColumnDescriptor(TestTimeRangeMapRed.FAMILY_NAME);
        col.setMaxVersions(Integer.MAX_VALUE);
        desc.addFamily(col);
        admin.createTable(desc);
        List<Put> puts = new ArrayList<>();
        for (Map.Entry<Long, Boolean> entry : TestTimeRangeMapRed.TIMESTAMP.entrySet()) {
            Put put = new Put(TestTimeRangeMapRed.KEY);
            put.setDurability(SKIP_WAL);
            put.addColumn(TestTimeRangeMapRed.FAMILY_NAME, TestTimeRangeMapRed.COLUMN_NAME, entry.getKey(), Bytes.toBytes(false));
            puts.add(put);
        }
        Table table = TestTimeRangeMapRed.UTIL.getConnection().getTable(desc.getTableName());
        table.put(puts);
        runTestOnTable();
        verify(table);
        table.close();
    }
}

