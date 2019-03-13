/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.processors.kite;


import StoreInKiteDataset.KITE_DATASET_URI;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData.Record;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kitesdk.data.Dataset;
import org.kitesdk.data.DatasetDescriptor;
import org.kitesdk.data.Datasets;
import org.kitesdk.data.spi.DefaultConfiguration;
import org.kitesdk.minicluster.MiniCluster;


@Ignore("Does not work on windows")
public class TestKiteProcessorsCluster {
    public static MiniCluster cluster = null;

    public static DatasetDescriptor descriptor = new DatasetDescriptor.Builder().schema(TestUtil.USER_SCHEMA).build();

    @Test
    public void testBasicStoreToHive() throws IOException {
        String datasetUri = "dataset:hive:ns/test";
        Dataset<Record> dataset = Datasets.create(datasetUri, TestKiteProcessorsCluster.descriptor, Record.class);
        TestRunner runner = TestRunners.newTestRunner(StoreInKiteDataset.class);
        runner.assertNotValid();
        runner.setProperty(KITE_DATASET_URI, datasetUri);
        runner.assertValid();
        List<Record> users = Lists.newArrayList(TestUtil.user("a", "a@example.com"), TestUtil.user("b", "b@example.com"), TestUtil.user("c", "c@example.com"));
        runner.enqueue(TestUtil.streamFor(users));
        runner.run();
        runner.assertAllFlowFilesTransferred("success", 1);
        List<Record> stored = Lists.newArrayList(((Iterable<Record>) (dataset.newReader())));
        Assert.assertEquals("Records should match", users, stored);
        Datasets.delete(datasetUri);
    }

    @Test
    public void testSchemaFromDistributedFileSystem() throws IOException {
        Schema expected = SchemaBuilder.record("Test").fields().requiredLong("id").requiredString("color").optionalDouble("price").endRecord();
        Path schemaPath = new Path("hdfs:/tmp/schema.avsc");
        FileSystem fs = schemaPath.getFileSystem(DefaultConfiguration.get());
        OutputStream out = fs.create(schemaPath);
        out.write(TestUtil.bytesFor(expected.toString(), Charset.forName("utf8")));
        out.close();
        Schema schema = AbstractKiteProcessor.getSchema(schemaPath.toString(), DefaultConfiguration.get());
        Assert.assertEquals("Schema from file should match", expected, schema);
    }
}

