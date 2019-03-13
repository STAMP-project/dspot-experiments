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


import Mapper.Context;
import TableInputFormat.INPUT_TABLE;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.IndexBuilder.Map;
import org.apache.hadoop.hbase.mapreduce.SampleUploader.Uploader;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.LauncherSecurityManager;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@Category({ MapReduceTests.class, LargeTests.class })
public class TestMapReduceExamples {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMapReduceExamples.class);

    private static HBaseTestingUtility util = new HBaseTestingUtility();

    /**
     * Test SampleUploader from examples
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSampleUploader() throws Exception {
        Configuration configuration = new Configuration();
        Uploader uploader = new Uploader();
        Context ctx = Mockito.mock(org.apache.hadoop.mapreduce.Mapper.Context.class);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ImmutableBytesWritable writer = ((ImmutableBytesWritable) (invocation.getArgument(0)));
                Put put = ((Put) (invocation.getArgument(1)));
                Assert.assertEquals("row", Bytes.toString(writer.get()));
                Assert.assertEquals("row", Bytes.toString(put.getRow()));
                return null;
            }
        }).when(ctx).write(ArgumentMatchers.any(), ArgumentMatchers.any());
        uploader.map(null, new Text("row,family,qualifier,value"), ctx);
        Path dir = TestMapReduceExamples.util.getDataTestDirOnTestFS("testSampleUploader");
        String[] args = new String[]{ dir.toString(), "simpleTable" };
        Job job = SampleUploader.configureJob(configuration, args);
        Assert.assertEquals(SequenceFileInputFormat.class, job.getInputFormatClass());
    }

    /**
     * Test main method of SampleUploader.
     */
    @Test
    public void testMainSampleUploader() throws Exception {
        PrintStream oldPrintStream = System.err;
        SecurityManager SECURITY_MANAGER = System.getSecurityManager();
        LauncherSecurityManager newSecurityManager = new LauncherSecurityManager();
        System.setSecurityManager(newSecurityManager);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String[] args = new String[]{  };
        System.setErr(new PrintStream(data));
        try {
            System.setErr(new PrintStream(data));
            try {
                SampleUploader.main(args);
                Assert.fail("should be SecurityException");
            } catch (SecurityException e) {
                Assert.assertEquals((-1), newSecurityManager.getExitCode());
                Assert.assertTrue(data.toString().contains("Wrong number of arguments:"));
                Assert.assertTrue(data.toString().contains("Usage: SampleUploader <input> <tablename>"));
            }
        } finally {
            System.setErr(oldPrintStream);
            System.setSecurityManager(SECURITY_MANAGER);
        }
    }

    /**
     * Test IndexBuilder from examples
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIndexBuilder() throws Exception {
        Configuration configuration = new Configuration();
        String[] args = new String[]{ "tableName", "columnFamily", "column1", "column2" };
        IndexBuilder.configureJob(configuration, args);
        Assert.assertEquals("tableName", configuration.get("index.tablename"));
        Assert.assertEquals("tableName", configuration.get(INPUT_TABLE));
        Assert.assertEquals("column1,column2", configuration.get("index.fields"));
        Map map = new Map();
        ImmutableBytesWritable rowKey = new ImmutableBytesWritable(Bytes.toBytes("test"));
        Context ctx = Mockito.mock(org.apache.hadoop.mapreduce.Mapper.Context.class);
        Mockito.when(ctx.getConfiguration()).thenReturn(configuration);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ImmutableBytesWritable writer = ((ImmutableBytesWritable) (invocation.getArgument(0)));
                Put put = ((Put) (invocation.getArgument(1)));
                Assert.assertEquals("tableName-column1", Bytes.toString(writer.get()));
                Assert.assertEquals("test", Bytes.toString(put.getRow()));
                return null;
            }
        }).when(ctx).write(ArgumentMatchers.any(), ArgumentMatchers.any());
        Result result = Mockito.mock(Result.class);
        Mockito.when(result.getValue(Bytes.toBytes("columnFamily"), Bytes.toBytes("column1"))).thenReturn(Bytes.toBytes("test"));
        map.setup(ctx);
        map.map(rowKey, result, ctx);
    }

    /**
     * Test main method of IndexBuilder
     */
    @Test
    public void testMainIndexBuilder() throws Exception {
        PrintStream oldPrintStream = System.err;
        SecurityManager SECURITY_MANAGER = System.getSecurityManager();
        LauncherSecurityManager newSecurityManager = new LauncherSecurityManager();
        System.setSecurityManager(newSecurityManager);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String[] args = new String[]{  };
        System.setErr(new PrintStream(data));
        try {
            System.setErr(new PrintStream(data));
            try {
                IndexBuilder.main(args);
                Assert.fail("should be SecurityException");
            } catch (SecurityException e) {
                Assert.assertEquals((-1), newSecurityManager.getExitCode());
                Assert.assertTrue(data.toString().contains("arguments supplied, required: 3"));
                Assert.assertTrue(data.toString().contains("Usage: IndexBuilder <TABLE_NAME> <COLUMN_FAMILY> <ATTR> [<ATTR> ...]"));
            }
        } finally {
            System.setErr(oldPrintStream);
            System.setSecurityManager(SECURITY_MANAGER);
        }
    }
}

