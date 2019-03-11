/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.hdfs.bolt;


import java.io.File;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.storm.Config;
import org.apache.storm.hdfs.testing.MiniDFSClusterRule;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AvroGenericRecordBoltTest {
    private static final String testRoot = "/unittest";

    private static final String schemaV1 = "{\"type\":\"record\"," + (("\"name\":\"myrecord\"," + "\"fields\":[{\"name\":\"foo1\",\"type\":\"string\"},") + "{ \"name\":\"int1\", \"type\":\"int\" }]}");

    private static final String schemaV2 = "{\"type\":\"record\"," + ((("\"name\":\"myrecord\"," + "\"fields\":[{\"name\":\"foo1\",\"type\":\"string\"},") + "{ \"name\":\"bar\", \"type\":\"string\", \"default\":\"baz\" },") + "{ \"name\":\"int1\", \"type\":\"int\" }]}");

    private static Schema schema1;

    private static Schema schema2;

    private static Tuple tuple1;

    private static Tuple tuple2;

    @Rule
    public MiniDFSClusterRule dfsClusterRule = new MiniDFSClusterRule(() -> {
        Configuration conf = new Configuration();
        conf.set("fs.trash.interval", "10");
        conf.setBoolean("dfs.permissions", true);
        File baseDir = new File("./target/hdfs/").getAbsoluteFile();
        FileUtil.fullyDelete(baseDir);
        conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, baseDir.getAbsolutePath());
        return conf;
    });

    @Mock
    private OutputCollector collector;

    @Mock
    private TopologyContext topologyContext;

    private DistributedFileSystem fs;

    private String hdfsURI;

    @Test
    public void multipleTuplesOneFile() throws IOException {
        AvroGenericRecordBolt bolt = makeAvroBolt(hdfsURI, 1, 1.0F, AvroGenericRecordBoltTest.schemaV1);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        Assert.assertEquals(1, countNonZeroLengthFiles(AvroGenericRecordBoltTest.testRoot));
        verifyAllAvroFiles(AvroGenericRecordBoltTest.testRoot);
    }

    @Test
    public void multipleTuplesMutliplesFiles() throws IOException {
        AvroGenericRecordBolt bolt = makeAvroBolt(hdfsURI, 1, 1.0E-4F, AvroGenericRecordBoltTest.schemaV1);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        Assert.assertEquals(4, countNonZeroLengthFiles(AvroGenericRecordBoltTest.testRoot));
        verifyAllAvroFiles(AvroGenericRecordBoltTest.testRoot);
    }

    @Test
    public void forwardSchemaChangeWorks() throws IOException {
        AvroGenericRecordBolt bolt = makeAvroBolt(hdfsURI, 1, 1000.0F, AvroGenericRecordBoltTest.schemaV1);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple2);
        // Schema change should have forced a rotation
        Assert.assertEquals(2, countNonZeroLengthFiles(AvroGenericRecordBoltTest.testRoot));
        verifyAllAvroFiles(AvroGenericRecordBoltTest.testRoot);
    }

    @Test
    public void backwardSchemaChangeWorks() throws IOException {
        AvroGenericRecordBolt bolt = makeAvroBolt(hdfsURI, 1, 1000.0F, AvroGenericRecordBoltTest.schemaV2);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple2);
        // Schema changes should have forced file rotations
        Assert.assertEquals(2, countNonZeroLengthFiles(AvroGenericRecordBoltTest.testRoot));
        verifyAllAvroFiles(AvroGenericRecordBoltTest.testRoot);
    }

    @Test
    public void schemaThrashing() throws IOException {
        AvroGenericRecordBolt bolt = makeAvroBolt(hdfsURI, 1, 1000.0F, AvroGenericRecordBoltTest.schemaV2);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple2);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple2);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple2);
        bolt.execute(AvroGenericRecordBoltTest.tuple1);
        bolt.execute(AvroGenericRecordBoltTest.tuple2);
        // Two distinct schema should result in only two files
        Assert.assertEquals(2, countNonZeroLengthFiles(AvroGenericRecordBoltTest.testRoot));
        verifyAllAvroFiles(AvroGenericRecordBoltTest.testRoot);
    }
}

