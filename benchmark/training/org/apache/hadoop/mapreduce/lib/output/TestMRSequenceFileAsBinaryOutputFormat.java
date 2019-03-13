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
package org.apache.hadoop.mapreduce.lib.output;


import CompressionType.BLOCK;
import CompressionType.RECORD;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.InvalidJobConfException;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.MapReduceTestUtil;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRSequenceFileAsBinaryOutputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRSequenceFileAsBinaryOutputFormat.class);

    private static final int RECORDS = 10000;

    @Test
    public void testBinary() throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        Path outdir = new Path(System.getProperty("test.build.data", "/tmp"), "outseq");
        Random r = new Random();
        long seed = r.nextLong();
        r.setSeed(seed);
        FileOutputFormat.setOutputPath(job, outdir);
        SequenceFileAsBinaryOutputFormat.setSequenceFileOutputKeyClass(job, IntWritable.class);
        SequenceFileAsBinaryOutputFormat.setSequenceFileOutputValueClass(job, DoubleWritable.class);
        SequenceFileAsBinaryOutputFormat.setCompressOutput(job, true);
        SequenceFileAsBinaryOutputFormat.setOutputCompressionType(job, BLOCK);
        BytesWritable bkey = new BytesWritable();
        BytesWritable bval = new BytesWritable();
        TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
        OutputFormat<BytesWritable, BytesWritable> outputFormat = new SequenceFileAsBinaryOutputFormat();
        OutputCommitter committer = outputFormat.getOutputCommitter(context);
        committer.setupJob(job);
        RecordWriter<BytesWritable, BytesWritable> writer = outputFormat.getRecordWriter(context);
        IntWritable iwritable = new IntWritable();
        DoubleWritable dwritable = new DoubleWritable();
        DataOutputBuffer outbuf = new DataOutputBuffer();
        TestMRSequenceFileAsBinaryOutputFormat.LOG.info("Creating data by SequenceFileAsBinaryOutputFormat");
        try {
            for (int i = 0; i < (TestMRSequenceFileAsBinaryOutputFormat.RECORDS); ++i) {
                iwritable = new IntWritable(r.nextInt());
                iwritable.write(outbuf);
                bkey.set(outbuf.getData(), 0, outbuf.getLength());
                outbuf.reset();
                dwritable = new DoubleWritable(r.nextDouble());
                dwritable.write(outbuf);
                bval.set(outbuf.getData(), 0, outbuf.getLength());
                outbuf.reset();
                writer.write(bkey, bval);
            }
        } finally {
            writer.close(context);
        }
        committer.commitTask(context);
        committer.commitJob(job);
        InputFormat<IntWritable, DoubleWritable> iformat = new org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat<IntWritable, DoubleWritable>();
        int count = 0;
        r.setSeed(seed);
        org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat.setInputPaths(job, outdir);
        TestMRSequenceFileAsBinaryOutputFormat.LOG.info("Reading data by SequenceFileInputFormat");
        for (InputSplit split : iformat.getSplits(job)) {
            RecordReader<IntWritable, DoubleWritable> reader = iformat.createRecordReader(split, context);
            MapContext<IntWritable, DoubleWritable, BytesWritable, BytesWritable> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<IntWritable, DoubleWritable, BytesWritable, BytesWritable>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), split);
            reader.initialize(split, mcontext);
            try {
                int sourceInt;
                double sourceDouble;
                while (reader.nextKeyValue()) {
                    sourceInt = r.nextInt();
                    sourceDouble = r.nextDouble();
                    iwritable = reader.getCurrentKey();
                    dwritable = reader.getCurrentValue();
                    Assert.assertEquals(((((("Keys don't match: " + "*") + (iwritable.get())) + ":") + sourceInt) + "*"), sourceInt, iwritable.get());
                    Assert.assertTrue(((((("Vals don't match: " + "*") + (dwritable.get())) + ":") + sourceDouble) + "*"), ((Double.compare(dwritable.get(), sourceDouble)) == 0));
                    ++count;
                } 
            } finally {
                reader.close();
            }
        }
        Assert.assertEquals("Some records not found", TestMRSequenceFileAsBinaryOutputFormat.RECORDS, count);
    }

    @Test
    public void testSequenceOutputClassDefaultsToMapRedOutputClass() throws IOException {
        Job job = Job.getInstance();
        // Setting Random class to test getSequenceFileOutput{Key,Value}Class
        job.setOutputKeyClass(FloatWritable.class);
        job.setOutputValueClass(BooleanWritable.class);
        Assert.assertEquals("SequenceFileOutputKeyClass should default to ouputKeyClass", FloatWritable.class, SequenceFileAsBinaryOutputFormat.getSequenceFileOutputKeyClass(job));
        Assert.assertEquals(("SequenceFileOutputValueClass should default to " + "ouputValueClass"), BooleanWritable.class, SequenceFileAsBinaryOutputFormat.getSequenceFileOutputValueClass(job));
        SequenceFileAsBinaryOutputFormat.setSequenceFileOutputKeyClass(job, IntWritable.class);
        SequenceFileAsBinaryOutputFormat.setSequenceFileOutputValueClass(job, DoubleWritable.class);
        Assert.assertEquals("SequenceFileOutputKeyClass not updated", IntWritable.class, SequenceFileAsBinaryOutputFormat.getSequenceFileOutputKeyClass(job));
        Assert.assertEquals("SequenceFileOutputValueClass not updated", DoubleWritable.class, SequenceFileAsBinaryOutputFormat.getSequenceFileOutputValueClass(job));
    }

    @Test
    public void testcheckOutputSpecsForbidRecordCompression() throws IOException {
        Job job = Job.getInstance();
        FileSystem fs = FileSystem.getLocal(job.getConfiguration());
        Path outputdir = new Path(((System.getProperty("test.build.data", "/tmp")) + "/output"));
        fs.delete(outputdir, true);
        // Without outputpath, FileOutputFormat.checkoutputspecs will throw
        // InvalidJobConfException
        FileOutputFormat.setOutputPath(job, outputdir);
        // SequenceFileAsBinaryOutputFormat doesn't support record compression
        // It should throw an exception when checked by checkOutputSpecs
        SequenceFileAsBinaryOutputFormat.setCompressOutput(job, true);
        SequenceFileAsBinaryOutputFormat.setOutputCompressionType(job, BLOCK);
        try {
            new SequenceFileAsBinaryOutputFormat().checkOutputSpecs(job);
        } catch (Exception e) {
            Assert.fail((("Block compression should be allowed for " + "SequenceFileAsBinaryOutputFormat:Caught ") + (e.getClass().getName())));
        }
        SequenceFileAsBinaryOutputFormat.setOutputCompressionType(job, RECORD);
        try {
            new SequenceFileAsBinaryOutputFormat().checkOutputSpecs(job);
            Assert.fail(("Record compression should not be allowed for " + "SequenceFileAsBinaryOutputFormat"));
        } catch (InvalidJobConfException ie) {
            // expected
        } catch (Exception e) {
            Assert.fail(((("Expected " + (InvalidJobConfException.class.getName())) + "but caught ") + (e.getClass().getName())));
        }
    }
}

