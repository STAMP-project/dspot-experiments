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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.mapreduce;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchemaUtils;
import org.junit.Test;


public class TestHCatPartitionPublish {
    private static Configuration mrConf = null;

    private static FileSystem fs = null;

    private static MiniMRCluster mrCluster = null;

    private static boolean isServerRunning = false;

    private static HiveConf hcatConf;

    private static HiveMetaStoreClient msc;

    private static SecurityManager securityManager;

    private static Configuration conf = new Configuration(true);

    private static String testName;

    @Test
    public void testPartitionPublish() throws Exception {
        String dbName = "default";
        String tableName = "testHCatPartitionedTable";
        createTable(null, tableName);
        Map<String, String> partitionMap = new HashMap<String, String>();
        partitionMap.put("part1", "p1value1");
        partitionMap.put("part0", "p0value1");
        ArrayList<HCatFieldSchema> hcatTableColumns = new ArrayList<HCatFieldSchema>();
        for (FieldSchema fs : getTableColumns()) {
            hcatTableColumns.add(HCatSchemaUtils.getHCatFieldSchema(fs));
        }
        runMRCreateFail(dbName, tableName, partitionMap, hcatTableColumns);
        List<String> ptns = TestHCatPartitionPublish.msc.listPartitionNames(dbName, tableName, ((short) (10)));
        Assert.assertEquals(0, ptns.size());
        Table table = TestHCatPartitionPublish.msc.getTable(dbName, tableName);
        Assert.assertTrue((table != null));
        Path path = new Path(((table.getSd().getLocation()) + "/part1=p1value1/part0=p0value1"));
        Assert.assertFalse(path.getFileSystem(TestHCatPartitionPublish.conf).exists(path));
    }

    public static class MapFail extends Mapper<LongWritable, Text, BytesWritable, HCatRecord> {
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            {
                throw new IOException("Exception to mimic job failure.");
            }
        }
    }
}

