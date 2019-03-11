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


import SafeModeAction.SAFEMODE_ENTER;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.storm.Config;
import org.apache.storm.hdfs.common.Partitioner;
import org.apache.storm.hdfs.testing.MiniDFSClusterRule;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.MockTupleHelpers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TestHdfsBolt {
    private static final String testRoot = "/unittest";

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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Tuple tuple1 = generateTestTuple(1, "First Tuple", "SFO", "CA");

    Tuple tuple2 = generateTestTuple(1, "Second Tuple", "SJO", "CA");

    private String hdfsURI;

    private DistributedFileSystem fs;

    @Mock
    private OutputCollector collector;

    @Mock
    private TopologyContext topologyContext;

    @Test
    public void testTwoTuplesTwoFiles() throws IOException {
        HdfsBolt bolt = makeHdfsBolt(hdfsURI, 1, 1.0E-5F);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(tuple1);
        bolt.execute(tuple2);
        Mockito.verify(collector).ack(tuple1);
        Mockito.verify(collector).ack(tuple2);
        Assert.assertEquals(2, countNonZeroLengthFiles(TestHdfsBolt.testRoot));
    }

    @Test
    public void testPartitionedOutput() throws IOException {
        HdfsBolt bolt = makeHdfsBolt(hdfsURI, 1, 1000.0F);
        Partitioner partitoner = new Partitioner() {
            @Override
            public String getPartitionPath(Tuple tuple) {
                return (Path.SEPARATOR) + (tuple.getStringByField("city"));
            }
        };
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.withPartitioner(partitoner);
        bolt.execute(tuple1);
        bolt.execute(tuple2);
        Mockito.verify(collector).ack(tuple1);
        Mockito.verify(collector).ack(tuple2);
        Assert.assertEquals(1, countNonZeroLengthFiles(((TestHdfsBolt.testRoot) + "/SFO")));
        Assert.assertEquals(1, countNonZeroLengthFiles(((TestHdfsBolt.testRoot) + "/SJO")));
    }

    @Test
    public void testTwoTuplesOneFile() throws IOException {
        HdfsBolt bolt = makeHdfsBolt(hdfsURI, 2, 10000.0F);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(tuple1);
        Mockito.verifyZeroInteractions(collector);
        bolt.execute(tuple2);
        Mockito.verify(collector).ack(tuple1);
        Mockito.verify(collector).ack(tuple2);
        Assert.assertEquals(1, countNonZeroLengthFiles(TestHdfsBolt.testRoot));
    }

    @Test
    public void testFailedSync() throws IOException {
        HdfsBolt bolt = makeHdfsBolt(hdfsURI, 2, 10000.0F);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(tuple1);
        fs.setSafeMode(SAFEMODE_ENTER);
        // All writes/syncs will fail so this should cause a RuntimeException
        thrown.expect(RuntimeException.class);
        bolt.execute(tuple1);
    }

    // One tuple and one rotation should yield one file with data
    // The failed executions should not cause rotations and any new files
    @Test
    public void testFailureFilecount() throws IOException, InterruptedException {
        HdfsBolt bolt = makeHdfsBolt(hdfsURI, 1, 1.0E-6F);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(tuple1);
        fs.setSafeMode(SAFEMODE_ENTER);
        try {
            bolt.execute(tuple2);
        } catch (RuntimeException e) {
            // 
        }
        try {
            bolt.execute(tuple2);
        } catch (RuntimeException e) {
            // 
        }
        try {
            bolt.execute(tuple2);
        } catch (RuntimeException e) {
            // 
        }
        Assert.assertEquals(1, countNonZeroLengthFiles(TestHdfsBolt.testRoot));
        Assert.assertEquals(0, countZeroLengthFiles(TestHdfsBolt.testRoot));
    }

    @Test
    public void testTickTuples() throws IOException {
        HdfsBolt bolt = makeHdfsBolt(hdfsURI, 10, 10000.0F);
        bolt.prepare(new Config(), topologyContext, collector);
        bolt.execute(tuple1);
        // Should not have flushed to file system yet
        Assert.assertEquals(0, countNonZeroLengthFiles(TestHdfsBolt.testRoot));
        bolt.execute(MockTupleHelpers.mockTickTuple());
        // Tick should have flushed it
        Assert.assertEquals(1, countNonZeroLengthFiles(TestHdfsBolt.testRoot));
    }
}

