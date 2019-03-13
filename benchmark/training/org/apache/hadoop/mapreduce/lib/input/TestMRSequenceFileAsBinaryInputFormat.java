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
package org.apache.hadoop.mapreduce.lib.input;


import SequenceFile.Writer;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.MapReduceTestUtil;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Assert;
import org.junit.Test;


public class TestMRSequenceFileAsBinaryInputFormat {
    private static final int RECORDS = 10000;

    @Test
    public void testBinary() throws IOException, InterruptedException {
        Job job = Job.getInstance();
        FileSystem fs = FileSystem.getLocal(job.getConfiguration());
        Path dir = new Path(((System.getProperty("test.build.data", ".")) + "/mapred"));
        Path file = new Path(dir, "testbinary.seq");
        Random r = new Random();
        long seed = r.nextLong();
        r.setSeed(seed);
        fs.delete(dir, true);
        FileInputFormat.setInputPaths(job, dir);
        Text tkey = new Text();
        Text tval = new Text();
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, job.getConfiguration(), file, Text.class, Text.class);
        try {
            for (int i = 0; i < (TestMRSequenceFileAsBinaryInputFormat.RECORDS); ++i) {
                tkey.set(Integer.toString(r.nextInt(), 36));
                tval.set(Long.toString(r.nextLong(), 36));
                writer.append(tkey, tval);
            }
        } finally {
            writer.close();
        }
        TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
        InputFormat<BytesWritable, BytesWritable> bformat = new SequenceFileAsBinaryInputFormat();
        int count = 0;
        r.setSeed(seed);
        BytesWritable bkey = new BytesWritable();
        BytesWritable bval = new BytesWritable();
        Text cmpkey = new Text();
        Text cmpval = new Text();
        DataInputBuffer buf = new DataInputBuffer();
        FileInputFormat.setInputPaths(job, file);
        for (InputSplit split : bformat.getSplits(job)) {
            RecordReader<BytesWritable, BytesWritable> reader = bformat.createRecordReader(split, context);
            MapContext<BytesWritable, BytesWritable, BytesWritable, BytesWritable> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<BytesWritable, BytesWritable, BytesWritable, BytesWritable>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), split);
            reader.initialize(split, mcontext);
            try {
                while (reader.nextKeyValue()) {
                    bkey = reader.getCurrentKey();
                    bval = reader.getCurrentValue();
                    tkey.set(Integer.toString(r.nextInt(), 36));
                    tval.set(Long.toString(r.nextLong(), 36));
                    buf.reset(bkey.getBytes(), bkey.getLength());
                    cmpkey.readFields(buf);
                    buf.reset(bval.getBytes(), bval.getLength());
                    cmpval.readFields(buf);
                    Assert.assertTrue(((((("Keys don't match: " + "*") + (cmpkey.toString())) + ":") + (tkey.toString())) + "*"), cmpkey.toString().equals(tkey.toString()));
                    Assert.assertTrue(((((("Vals don't match: " + "*") + (cmpval.toString())) + ":") + (tval.toString())) + "*"), cmpval.toString().equals(tval.toString()));
                    ++count;
                } 
            } finally {
                reader.close();
            }
        }
        Assert.assertEquals("Some records not found", TestMRSequenceFileAsBinaryInputFormat.RECORDS, count);
    }
}

