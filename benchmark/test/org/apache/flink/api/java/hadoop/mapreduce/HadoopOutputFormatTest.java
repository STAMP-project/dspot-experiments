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
package org.apache.flink.api.java.hadoop.mapreduce;


import java.io.IOException;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link HadoopOutputFormat}.
 */
public class HadoopOutputFormatTest {
    private static final String MAPRED_OUTPUT_PATH = "an/ignored/file/";

    private static final String MAPRED_OUTPUT_DIR_KEY = "mapred.output.dir";

    @Test
    public void testWriteRecord() throws Exception {
        RecordWriter<String, Long> recordWriter = Mockito.mock(HadoopOutputFormatTest.DummyRecordWriter.class);
        HadoopOutputFormat<String, Long> hadoopOutputFormat = setupHadoopOutputFormat(new HadoopOutputFormatTest.DummyOutputFormat(), Job.getInstance(), recordWriter, null, new Configuration());
        hadoopOutputFormat.writeRecord(new org.apache.flink.api.java.tuple.Tuple2<String, Long>());
        Mockito.verify(recordWriter, Mockito.times(1)).write(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(Long.class));
    }

    @Test
    public void testOpen() throws Exception {
        OutputFormat<String, Long> dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.DummyOutputFormat.class);
        OutputCommitter outputCommitter = setupOutputCommitter(true);
        Mockito.when(dummyOutputFormat.getOutputCommitter(ArgumentMatchers.any(TaskAttemptContext.class))).thenReturn(outputCommitter);
        HadoopOutputFormat<String, Long> hadoopOutputFormat = setupHadoopOutputFormat(dummyOutputFormat, Job.getInstance(), new HadoopOutputFormatTest.DummyRecordWriter(), setupOutputCommitter(true), new Configuration());
        hadoopOutputFormat.open(1, 4);
        Mockito.verify(hadoopOutputFormat.outputCommitter, Mockito.times(1)).setupJob(ArgumentMatchers.any(JobContext.class));
        Mockito.verify(hadoopOutputFormat.mapreduceOutputFormat, Mockito.times(1)).getRecordWriter(ArgumentMatchers.any(TaskAttemptContext.class));
    }

    @Test
    public void testCloseWithNeedsTaskCommitTrue() throws Exception {
        RecordWriter<String, Long> recordWriter = Mockito.mock(HadoopOutputFormatTest.DummyRecordWriter.class);
        OutputCommitter outputCommitter = setupOutputCommitter(true);
        HadoopOutputFormat<String, Long> hadoopOutputFormat = setupHadoopOutputFormat(new HadoopOutputFormatTest.DummyOutputFormat(), Job.getInstance(), recordWriter, outputCommitter, new Configuration());
        hadoopOutputFormat.close();
        Mockito.verify(outputCommitter, Mockito.times(1)).commitTask(ArgumentMatchers.nullable(TaskAttemptContext.class));
        Mockito.verify(recordWriter, Mockito.times(1)).close(ArgumentMatchers.nullable(TaskAttemptContext.class));
    }

    @Test
    public void testCloseWithNeedsTaskCommitFalse() throws Exception {
        RecordWriter<String, Long> recordWriter = Mockito.mock(HadoopOutputFormatTest.DummyRecordWriter.class);
        OutputCommitter outputCommitter = setupOutputCommitter(false);
        HadoopOutputFormat<String, Long> hadoopOutputFormat = setupHadoopOutputFormat(new HadoopOutputFormatTest.DummyOutputFormat(), Job.getInstance(), recordWriter, outputCommitter, new Configuration());
        hadoopOutputFormat.close();
        Mockito.verify(outputCommitter, Mockito.times(0)).commitTask(ArgumentMatchers.nullable(TaskAttemptContext.class));
        Mockito.verify(recordWriter, Mockito.times(1)).close(ArgumentMatchers.nullable(TaskAttemptContext.class));
    }

    @Test
    public void testConfigure() throws Exception {
        HadoopOutputFormatTest.ConfigurableDummyOutputFormat outputFormat = Mockito.mock(HadoopOutputFormatTest.ConfigurableDummyOutputFormat.class);
        HadoopOutputFormat<String, Long> hadoopOutputFormat = setupHadoopOutputFormat(outputFormat, Job.getInstance(), null, null, new Configuration());
        hadoopOutputFormat.configure(new org.apache.flink.configuration.Configuration());
        Mockito.verify(outputFormat, Mockito.times(1)).setConf(ArgumentMatchers.any(Configuration.class));
    }

    @Test
    public void testFinalizedGlobal() throws Exception {
        HadoopOutputFormat<String, Long> hadoopOutputFormat = setupHadoopOutputFormat(new HadoopOutputFormatTest.DummyOutputFormat(), Job.getInstance(), null, null, new Configuration());
        hadoopOutputFormat.finalizeGlobal(1);
        Mockito.verify(hadoopOutputFormat.outputCommitter, Mockito.times(1)).commitJob(ArgumentMatchers.any(JobContext.class));
    }

    class DummyRecordWriter extends RecordWriter<String, Long> {
        @Override
        public void write(String key, Long value) throws IOException, InterruptedException {
        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        }
    }

    class DummyOutputFormat extends OutputFormat<String, Long> {
        @Override
        public RecordWriter<String, Long> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
            return null;
        }

        @Override
        public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {
        }

        @Override
        public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
            final OutputCommitter outputCommitter = Mockito.mock(OutputCommitter.class);
            Mockito.doNothing().when(outputCommitter).setupJob(ArgumentMatchers.any(JobContext.class));
            return outputCommitter;
        }
    }

    class ConfigurableDummyOutputFormat extends HadoopOutputFormatTest.DummyOutputFormat implements Configurable {
        @Override
        public void setConf(Configuration configuration) {
        }

        @Override
        public Configuration getConf() {
            return null;
        }
    }
}

