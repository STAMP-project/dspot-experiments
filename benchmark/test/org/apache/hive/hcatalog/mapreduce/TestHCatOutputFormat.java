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


import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serdeConstants.STRING_TYPE_NAME;


public class TestHCatOutputFormat extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestHCatOutputFormat.class);

    private HiveMetaStoreClient client;

    private HiveConf hiveConf;

    private static final String dbName = "hcatOutputFormatTestDB";

    private static final String tblName = "hcatOutputFormatTestTable";

    public void testSetOutput() throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "test outputformat");
        Map<String, String> partitionValues = new HashMap<String, String>();
        partitionValues.put("colname", "p1");
        // null server url means local mode
        OutputJobInfo info = OutputJobInfo.create(TestHCatOutputFormat.dbName, TestHCatOutputFormat.tblName, partitionValues);
        HCatOutputFormat.setOutput(job, info);
        OutputJobInfo jobInfo = HCatOutputFormat.getJobInfo(job.getConfiguration());
        TestCase.assertNotNull(jobInfo.getTableInfo());
        TestCase.assertEquals(1, jobInfo.getPartitionValues().size());
        TestCase.assertEquals("p1", jobInfo.getPartitionValues().get("colname"));
        TestCase.assertEquals(1, jobInfo.getTableInfo().getDataColumns().getFields().size());
        TestCase.assertEquals("data_column", jobInfo.getTableInfo().getDataColumns().getFields().get(0).getName());
        publishTest(job);
    }

    public void testGetTableSchema() throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "test getTableSchema");
        HCatOutputFormat.setOutput(job, OutputJobInfo.create(TestHCatOutputFormat.dbName, TestHCatOutputFormat.tblName, new HashMap<String, String>() {
            {
                put("colname", "col_value");
            }
        }));
        HCatSchema rowSchema = HCatOutputFormat.getTableSchema(job.getConfiguration());
        TestCase.assertEquals("Row-schema should have exactly one column.", 1, rowSchema.getFields().size());
        TestCase.assertEquals("Row-schema must contain the data column.", "data_column", rowSchema.getFields().get(0).getName());
        TestCase.assertEquals("Data column should have been STRING type.", STRING_TYPE_NAME, rowSchema.getFields().get(0).getTypeString());
        HCatSchema tableSchema = HCatOutputFormat.getTableSchemaWithPartitionColumns(job.getConfiguration());
        TestCase.assertEquals("Table-schema should have exactly 2 columns.", 2, tableSchema.getFields().size());
        TestCase.assertEquals("Table-schema must contain the data column.", "data_column", tableSchema.getFields().get(0).getName());
        TestCase.assertEquals("Data column should have been STRING type.", STRING_TYPE_NAME, tableSchema.getFields().get(0).getTypeString());
        TestCase.assertEquals("Table-schema must contain the partition column.", "colname", tableSchema.getFields().get(1).getName());
        TestCase.assertEquals("Partition column should have been STRING type.", STRING_TYPE_NAME, tableSchema.getFields().get(1).getTypeString());
    }
}

