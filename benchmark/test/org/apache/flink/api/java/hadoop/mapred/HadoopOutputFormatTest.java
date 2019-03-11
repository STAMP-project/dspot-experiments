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
package org.apache.flink.api.java.hadoop.mapred;


import java.io.IOException;
import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.JobContext;
import org.apache.hadoop.mapred.OutputCommitter;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TaskAttemptContext;
import org.apache.hadoop.util.Progressable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 * Tests for {@link HadoopOutputFormat}.
 */
public class HadoopOutputFormatTest {
    @Test
    public void testOpen() throws Exception {
        OutputFormat<String, Long> dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.DummyOutputFormat.class);
        HadoopOutputFormatTest.DummyOutputCommitter outputCommitter = Mockito.mock(HadoopOutputFormatTest.DummyOutputCommitter.class);
        JobConf jobConf = Mockito.spy(new JobConf());
        Mockito.when(jobConf.getOutputCommitter()).thenReturn(outputCommitter);
        HadoopOutputFormat<String, Long> outputFormat = new HadoopOutputFormat(dummyOutputFormat, jobConf);
        outputFormat.open(1, 1);
        Mockito.verify(jobConf, Mockito.times(2)).getOutputCommitter();
        Mockito.verify(outputCommitter, Mockito.times(1)).setupJob(ArgumentMatchers.any(JobContext.class));
        Mockito.verify(dummyOutputFormat, Mockito.times(1)).getRecordWriter(ArgumentMatchers.nullable(FileSystem.class), ArgumentMatchers.any(JobConf.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(Progressable.class));
    }

    @Test
    public void testConfigureWithConfigurable() {
        HadoopOutputFormatTest.ConfigurableDummyOutputFormat dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.ConfigurableDummyOutputFormat.class);
        JobConf jobConf = Mockito.mock(JobConf.class);
        HadoopOutputFormat<String, Long> outputFormat = new HadoopOutputFormat(dummyOutputFormat, jobConf);
        outputFormat.configure(Matchers.<Configuration>any());
        Mockito.verify(dummyOutputFormat, Mockito.times(1)).setConf(ArgumentMatchers.any(org.apache.hadoop.conf.Configuration.class));
    }

    @Test
    public void testConfigureWithJobConfigurable() {
        HadoopOutputFormatTest.JobConfigurableDummyOutputFormat dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.JobConfigurableDummyOutputFormat.class);
        JobConf jobConf = Mockito.mock(JobConf.class);
        HadoopOutputFormat<String, Long> outputFormat = new HadoopOutputFormat(dummyOutputFormat, jobConf);
        outputFormat.configure(Matchers.<Configuration>any());
        Mockito.verify(dummyOutputFormat, Mockito.times(1)).configure(ArgumentMatchers.any(JobConf.class));
    }

    @Test
    public void testCloseWithTaskCommit() throws Exception {
        OutputFormat<String, Long> dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.DummyOutputFormat.class);
        HadoopOutputFormatTest.DummyOutputCommitter outputCommitter = Mockito.mock(HadoopOutputFormatTest.DummyOutputCommitter.class);
        Mockito.when(outputCommitter.needsTaskCommit(ArgumentMatchers.nullable(TaskAttemptContext.class))).thenReturn(true);
        HadoopOutputFormatTest.DummyRecordWriter recordWriter = Mockito.mock(HadoopOutputFormatTest.DummyRecordWriter.class);
        JobConf jobConf = Mockito.mock(JobConf.class);
        HadoopOutputFormat<String, Long> outputFormat = new HadoopOutputFormat(dummyOutputFormat, jobConf);
        outputFormat.recordWriter = recordWriter;
        outputFormat.outputCommitter = outputCommitter;
        outputFormat.close();
        Mockito.verify(recordWriter, Mockito.times(1)).close(ArgumentMatchers.nullable(Reporter.class));
        Mockito.verify(outputCommitter, Mockito.times(1)).commitTask(ArgumentMatchers.nullable(TaskAttemptContext.class));
    }

    @Test
    public void testCloseWithoutTaskCommit() throws Exception {
        OutputFormat<String, Long> dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.DummyOutputFormat.class);
        HadoopOutputFormatTest.DummyOutputCommitter outputCommitter = Mockito.mock(HadoopOutputFormatTest.DummyOutputCommitter.class);
        Mockito.when(outputCommitter.needsTaskCommit(ArgumentMatchers.any(TaskAttemptContext.class))).thenReturn(false);
        HadoopOutputFormatTest.DummyRecordWriter recordWriter = Mockito.mock(HadoopOutputFormatTest.DummyRecordWriter.class);
        JobConf jobConf = Mockito.mock(JobConf.class);
        HadoopOutputFormat<String, Long> outputFormat = new HadoopOutputFormat(dummyOutputFormat, jobConf);
        outputFormat.recordWriter = recordWriter;
        outputFormat.outputCommitter = outputCommitter;
        outputFormat.close();
        Mockito.verify(recordWriter, Mockito.times(1)).close(ArgumentMatchers.any(Reporter.class));
        Mockito.verify(outputCommitter, Mockito.times(0)).commitTask(ArgumentMatchers.any(TaskAttemptContext.class));
    }

    @Test
    public void testWriteRecord() throws Exception {
        OutputFormat<String, Long> dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.DummyOutputFormat.class);
        HadoopOutputFormatTest.DummyRecordWriter recordWriter = Mockito.mock(HadoopOutputFormatTest.DummyRecordWriter.class);
        JobConf jobConf = Mockito.mock(JobConf.class);
        HadoopOutputFormat<String, Long> outputFormat = new HadoopOutputFormat(dummyOutputFormat, jobConf);
        outputFormat.recordWriter = recordWriter;
        outputFormat.writeRecord(new org.apache.flink.api.java.tuple.Tuple2("key", 1L));
        Mockito.verify(recordWriter, Mockito.times(1)).write(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
    }

    @Test
    public void testFinalizeGlobal() throws Exception {
        OutputFormat<String, Long> dummyOutputFormat = Mockito.mock(HadoopOutputFormatTest.DummyOutputFormat.class);
        HadoopOutputFormatTest.DummyOutputCommitter outputCommitter = Mockito.mock(HadoopOutputFormatTest.DummyOutputCommitter.class);
        JobConf jobConf = Mockito.spy(new JobConf());
        Mockito.when(jobConf.getOutputCommitter()).thenReturn(outputCommitter);
        HadoopOutputFormat<String, Long> outputFormat = new HadoopOutputFormat(dummyOutputFormat, jobConf);
        outputFormat.finalizeGlobal(1);
        commitJob(ArgumentMatchers.any(JobContext.class));
    }

    private class DummyOutputFormat implements OutputFormat<String, Long> {
        @Override
        public RecordWriter<String, Long> getRecordWriter(FileSystem fileSystem, JobConf jobConf, String s, Progressable progressable) throws IOException {
            return null;
        }

        @Override
        public void checkOutputSpecs(FileSystem fileSystem, JobConf jobConf) throws IOException {
        }
    }

    private class ConfigurableDummyOutputFormat extends HadoopOutputFormatTest.DummyOutputFormat implements Configurable {
        @Override
        public void setConf(org.apache.hadoop.conf.Configuration configuration) {
        }

        @Override
        public org.apache.hadoop.conf.Configuration getConf() {
            return null;
        }
    }

    private class JobConfigurableDummyOutputFormat extends HadoopOutputFormatTest.DummyOutputFormat implements JobConfigurable {
        @Override
        public void configure(JobConf jobConf) {
        }
    }

    private class DummyOutputCommitter extends OutputCommitter {
        @Override
        public void setupJob(JobContext jobContext) throws IOException {
        }

        @Override
        public void setupTask(TaskAttemptContext taskAttemptContext) throws IOException {
        }

        @Override
        public boolean needsTaskCommit(TaskAttemptContext taskAttemptContext) throws IOException {
            return false;
        }

        @Override
        public void commitTask(TaskAttemptContext taskAttemptContext) throws IOException {
        }

        @Override
        public void abortTask(TaskAttemptContext taskAttemptContext) throws IOException {
        }
    }

    private class DummyRecordWriter implements RecordWriter<String, Long> {
        @Override
        public void write(String s, Long aLong) throws IOException {
        }

        @Override
        public void close(Reporter reporter) throws IOException {
        }
    }
}

