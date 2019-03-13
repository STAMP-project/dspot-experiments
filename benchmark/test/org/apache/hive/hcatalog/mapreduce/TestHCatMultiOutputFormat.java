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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchemaUtils;
import org.apache.hive.hcatalog.mapreduce.MultiOutputFormat.JobConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHCatMultiOutputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestHCatMultiOutputFormat.class);

    private static final String DATABASE = "default";

    private static final String[] tableNames = new String[]{ "test1", "test2", "test3" };

    private static final String[] tablePerms = new String[]{ "755", "750", "700" };

    private static Path warehousedir = null;

    private static HashMap<String, HCatSchema> schemaMap = new HashMap<String, HCatSchema>();

    private static HiveMetaStoreClient hmsc;

    private static MiniMRCluster mrCluster;

    private static Configuration mrConf;

    private static HiveConf hiveConf;

    private static File workDir;

    static {
        TestHCatMultiOutputFormat.schemaMap.put(TestHCatMultiOutputFormat.tableNames[0], new HCatSchema(TestHCatMultiOutputFormat.ColumnHolder.hCattest1Cols));
        TestHCatMultiOutputFormat.schemaMap.put(TestHCatMultiOutputFormat.tableNames[1], new HCatSchema(TestHCatMultiOutputFormat.ColumnHolder.hCattest2Cols));
        TestHCatMultiOutputFormat.schemaMap.put(TestHCatMultiOutputFormat.tableNames[2], new HCatSchema(TestHCatMultiOutputFormat.ColumnHolder.hCattest3Cols));
    }

    /**
     * Private class which holds all the data for the test cases
     */
    private static class ColumnHolder {
        private static ArrayList<HCatFieldSchema> hCattest1Cols = new ArrayList<HCatFieldSchema>();

        private static ArrayList<HCatFieldSchema> hCattest2Cols = new ArrayList<HCatFieldSchema>();

        private static ArrayList<HCatFieldSchema> hCattest3Cols = new ArrayList<HCatFieldSchema>();

        private static ArrayList<FieldSchema> partitionCols = new ArrayList<FieldSchema>();

        private static ArrayList<FieldSchema> test1Cols = new ArrayList<FieldSchema>();

        private static ArrayList<FieldSchema> test2Cols = new ArrayList<FieldSchema>();

        private static ArrayList<FieldSchema> test3Cols = new ArrayList<FieldSchema>();

        private static HashMap<String, List<FieldSchema>> colMapping = new HashMap<String, List<FieldSchema>>();

        static {
            try {
                FieldSchema keyCol = new FieldSchema("key", serdeConstants.STRING_TYPE_NAME, "");
                TestHCatMultiOutputFormat.ColumnHolder.test1Cols.add(keyCol);
                TestHCatMultiOutputFormat.ColumnHolder.test2Cols.add(keyCol);
                TestHCatMultiOutputFormat.ColumnHolder.test3Cols.add(keyCol);
                TestHCatMultiOutputFormat.ColumnHolder.hCattest1Cols.add(HCatSchemaUtils.getHCatFieldSchema(keyCol));
                TestHCatMultiOutputFormat.ColumnHolder.hCattest2Cols.add(HCatSchemaUtils.getHCatFieldSchema(keyCol));
                TestHCatMultiOutputFormat.ColumnHolder.hCattest3Cols.add(HCatSchemaUtils.getHCatFieldSchema(keyCol));
                FieldSchema valueCol = new FieldSchema("value", serdeConstants.STRING_TYPE_NAME, "");
                TestHCatMultiOutputFormat.ColumnHolder.test1Cols.add(valueCol);
                TestHCatMultiOutputFormat.ColumnHolder.test3Cols.add(valueCol);
                TestHCatMultiOutputFormat.ColumnHolder.hCattest1Cols.add(HCatSchemaUtils.getHCatFieldSchema(valueCol));
                TestHCatMultiOutputFormat.ColumnHolder.hCattest3Cols.add(HCatSchemaUtils.getHCatFieldSchema(valueCol));
                FieldSchema extraCol = new FieldSchema("extra", serdeConstants.STRING_TYPE_NAME, "");
                TestHCatMultiOutputFormat.ColumnHolder.test3Cols.add(extraCol);
                TestHCatMultiOutputFormat.ColumnHolder.hCattest3Cols.add(HCatSchemaUtils.getHCatFieldSchema(extraCol));
                TestHCatMultiOutputFormat.ColumnHolder.colMapping.put("test1", TestHCatMultiOutputFormat.ColumnHolder.test1Cols);
                TestHCatMultiOutputFormat.ColumnHolder.colMapping.put("test2", TestHCatMultiOutputFormat.ColumnHolder.test2Cols);
                TestHCatMultiOutputFormat.ColumnHolder.colMapping.put("test3", TestHCatMultiOutputFormat.ColumnHolder.test3Cols);
            } catch (HCatException e) {
                TestHCatMultiOutputFormat.LOG.error("Error in setting up schema fields for the table", e);
                throw new RuntimeException(e);
            }
        }

        static {
            TestHCatMultiOutputFormat.ColumnHolder.partitionCols.add(new FieldSchema("ds", serdeConstants.STRING_TYPE_NAME, ""));
            TestHCatMultiOutputFormat.ColumnHolder.partitionCols.add(new FieldSchema("cluster", serdeConstants.STRING_TYPE_NAME, ""));
        }
    }

    /**
     * Simple test case.
     * <ol>
     * <li>Submits a mapred job which writes out one fixed line to each of the tables</li>
     * <li>uses hive fetch task to read the data and see if it matches what was written</li>
     * </ol>
     *
     * @throws Exception
     * 		if any error occurs
     */
    @Test
    public void testOutputFormat() throws Throwable {
        HashMap<String, String> partitionValues = new HashMap<String, String>();
        partitionValues.put("ds", "1");
        partitionValues.put("cluster", "ag");
        ArrayList<OutputJobInfo> infoList = new ArrayList<OutputJobInfo>();
        infoList.add(OutputJobInfo.create("default", TestHCatMultiOutputFormat.tableNames[0], partitionValues));
        infoList.add(OutputJobInfo.create("default", TestHCatMultiOutputFormat.tableNames[1], partitionValues));
        infoList.add(OutputJobInfo.create("default", TestHCatMultiOutputFormat.tableNames[2], partitionValues));
        Job job = new Job(TestHCatMultiOutputFormat.hiveConf, "SampleJob");
        job.setMapperClass(TestHCatMultiOutputFormat.MyMapper.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(MultiOutputFormat.class);
        job.setNumReduceTasks(0);
        JobConfigurer configurer = MultiOutputFormat.createConfigurer(job);
        for (int i = 0; i < (TestHCatMultiOutputFormat.tableNames.length); i++) {
            configurer.addOutputFormat(TestHCatMultiOutputFormat.tableNames[i], HCatOutputFormat.class, BytesWritable.class, HCatRecord.class);
            HCatOutputFormat.setOutput(configurer.getJob(TestHCatMultiOutputFormat.tableNames[i]), infoList.get(i));
            HCatOutputFormat.setSchema(configurer.getJob(TestHCatMultiOutputFormat.tableNames[i]), TestHCatMultiOutputFormat.schemaMap.get(TestHCatMultiOutputFormat.tableNames[i]));
        }
        configurer.configure();
        Path filePath = createInputFile();
        FileInputFormat.addInputPath(job, filePath);
        Assert.assertTrue(job.waitForCompletion(true));
        ArrayList<String> outputs = new ArrayList<String>();
        for (String tbl : TestHCatMultiOutputFormat.tableNames) {
            outputs.add(getTableData(tbl, "default").get(0));
        }
        Assert.assertEquals((("Comparing output of table " + (TestHCatMultiOutputFormat.tableNames[0])) + " is not correct"), outputs.get(0), "a,a,1,ag");
        Assert.assertEquals((("Comparing output of table " + (TestHCatMultiOutputFormat.tableNames[1])) + " is not correct"), outputs.get(1), "a,1,ag");
        Assert.assertEquals((("Comparing output of table " + (TestHCatMultiOutputFormat.tableNames[2])) + " is not correct"), outputs.get(2), "a,a,extra,1,ag");
        // Check permisssion on partition dirs and files created
        for (int i = 0; i < (TestHCatMultiOutputFormat.tableNames.length); i++) {
            Path partitionFile = new Path(((((TestHCatMultiOutputFormat.warehousedir) + "/") + (TestHCatMultiOutputFormat.tableNames[i])) + "/ds=1/cluster=ag/part-m-00000"));
            FileSystem fs = partitionFile.getFileSystem(TestHCatMultiOutputFormat.mrConf);
            Assert.assertEquals((("File permissions of table " + (TestHCatMultiOutputFormat.tableNames[i])) + " is not correct"), fs.getFileStatus(partitionFile).getPermission(), new FsPermission(TestHCatMultiOutputFormat.tablePerms[i]));
            Assert.assertEquals((("File permissions of table " + (TestHCatMultiOutputFormat.tableNames[i])) + " is not correct"), fs.getFileStatus(partitionFile.getParent()).getPermission(), new FsPermission(TestHCatMultiOutputFormat.tablePerms[i]));
            Assert.assertEquals((("File permissions of table " + (TestHCatMultiOutputFormat.tableNames[i])) + " is not correct"), fs.getFileStatus(partitionFile.getParent().getParent()).getPermission(), new FsPermission(TestHCatMultiOutputFormat.tablePerms[i]));
        }
        TestHCatMultiOutputFormat.LOG.info("File permissions verified");
    }

    private static class MyMapper extends Mapper<LongWritable, Text, BytesWritable, HCatRecord> {
        private int i = 0;

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            HCatRecord record = null;
            String[] splits = value.toString().split(",");
            switch (i) {
                case 0 :
                    record = new DefaultHCatRecord(2);
                    record.set(0, splits[0]);
                    record.set(1, splits[1]);
                    break;
                case 1 :
                    record = new DefaultHCatRecord(1);
                    record.set(0, splits[0]);
                    break;
                case 2 :
                    record = new DefaultHCatRecord(3);
                    record.set(0, splits[0]);
                    record.set(1, splits[1]);
                    record.set(2, "extra");
                    break;
                default :
                    Assert.fail("This should not happen!!!!!");
            }
            MultiOutputFormat.write(TestHCatMultiOutputFormat.tableNames[i], null, record, context);
            (i)++;
        }
    }
}

