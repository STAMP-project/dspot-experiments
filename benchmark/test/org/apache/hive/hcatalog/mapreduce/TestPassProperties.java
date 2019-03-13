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


import Warehouse.DEFAULT_DATABASE_NAME;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.junit.Assert;
import org.junit.Test;


public class TestPassProperties {
    private static final String TEST_DATA_DIR = ((System.getProperty("user.dir")) + "/build/test/data/") + (TestPassProperties.class.getCanonicalName());

    private static final String TEST_WAREHOUSE_DIR = (TestPassProperties.TEST_DATA_DIR) + "/warehouse";

    private static final String INPUT_FILE_NAME = (TestPassProperties.TEST_DATA_DIR) + "/input.data";

    private static IDriver driver;

    private static String[] input;

    private static HiveConf hiveConf;

    @Test
    public void testSequenceTableWriteReadMR() throws Exception {
        Initialize();
        String createTable = "CREATE TABLE bad_props_table(a0 int, a1 String, a2 String) STORED AS SEQUENCEFILE";
        TestPassProperties.driver.run("drop table bad_props_table");
        int retCode1 = TestPassProperties.driver.run(createTable).getResponseCode();
        Assert.assertTrue((retCode1 == 0));
        boolean caughtException = false;
        try {
            Configuration conf = new Configuration();
            conf.set("hive.metastore.uris", "thrift://no.such.machine:10888");
            Job job = new Job(conf, "Write-hcat-seq-table");
            job.setJarByClass(TestPassProperties.class);
            job.setMapperClass(TestPassProperties.Map.class);
            job.setOutputKeyClass(NullWritable.class);
            job.setOutputValueClass(DefaultHCatRecord.class);
            job.setInputFormatClass(TextInputFormat.class);
            TextInputFormat.setInputPaths(job, TestPassProperties.INPUT_FILE_NAME);
            HCatOutputFormat.setOutput(job, OutputJobInfo.create(DEFAULT_DATABASE_NAME, "bad_props_table", null));
            job.setOutputFormatClass(HCatOutputFormat.class);
            HCatOutputFormat.setSchema(job, getSchema());
            job.setNumReduceTasks(0);
            Assert.assertTrue(job.waitForCompletion(true));
            new FileOutputCommitterContainer(job, null).cleanupJob(job);
        } catch (Exception e) {
            caughtException = true;
            Assert.assertTrue(((InvocationTargetException) (e.getCause().getCause().getCause())).getTargetException().getMessage().contains("Could not connect to meta store using any of the URIs provided"));
            Assert.assertTrue(e.getCause().getMessage().contains("Unable to instantiate org.apache.hive.hcatalog.common.HiveClientCache$CacheableHiveMetaStoreClient"));
        }
        Assert.assertTrue(caughtException);
    }

    public static class Map extends Mapper<LongWritable, Text, NullWritable, DefaultHCatRecord> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] cols = value.toString().split(",");
            DefaultHCatRecord record = new DefaultHCatRecord(3);
            record.set(0, Integer.parseInt(cols[0]));
            record.set(1, cols[1]);
            record.set(2, cols[2]);
            context.write(NullWritable.get(), record);
        }
    }
}

