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
import java.util.List;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.hadoop.mapreduce.wrapper.HadoopInputSplit;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link HadoopInputFormat}.
 */
public class HadoopInputFormatTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testConfigure() throws Exception {
        HadoopInputFormatTest.ConfigurableDummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.ConfigurableDummyInputFormat.class);
        HadoopInputFormat<String, Long> hadoopInputFormat = setupHadoopInputFormat(inputFormat, Job.getInstance(), null);
        hadoopInputFormat.configure(new Configuration());
        Mockito.verify(inputFormat, Mockito.times(1)).setConf(ArgumentMatchers.any(org.apache.hadoop.conf.Configuration.class));
    }

    @Test
    public void testCreateInputSplits() throws Exception {
        HadoopInputFormatTest.DummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.DummyInputFormat.class);
        HadoopInputFormat<String, Long> hadoopInputFormat = setupHadoopInputFormat(inputFormat, Job.getInstance(), null);
        hadoopInputFormat.createInputSplits(2);
        Mockito.verify(inputFormat, Mockito.times(1)).getSplits(ArgumentMatchers.any(JobContext.class));
    }

    @Test
    public void testOpen() throws Exception {
        HadoopInputFormatTest.DummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.DummyInputFormat.class);
        Mockito.when(inputFormat.createRecordReader(ArgumentMatchers.nullable(InputSplit.class), ArgumentMatchers.any(TaskAttemptContext.class))).thenReturn(new HadoopInputFormatTest.DummyRecordReader());
        HadoopInputSplit inputSplit = Mockito.mock(HadoopInputSplit.class);
        HadoopInputFormat<String, Long> hadoopInputFormat = setupHadoopInputFormat(inputFormat, Job.getInstance(), null);
        hadoopInputFormat.open(inputSplit);
        Mockito.verify(inputFormat, Mockito.times(1)).createRecordReader(ArgumentMatchers.nullable(InputSplit.class), ArgumentMatchers.any(TaskAttemptContext.class));
        Assert.assertThat(hadoopInputFormat.fetched, Is.is(false));
    }

    @Test
    public void testClose() throws Exception {
        HadoopInputFormatTest.DummyRecordReader recordReader = Mockito.mock(HadoopInputFormatTest.DummyRecordReader.class);
        HadoopInputFormat<String, Long> hadoopInputFormat = setupHadoopInputFormat(new HadoopInputFormatTest.DummyInputFormat(), Job.getInstance(), recordReader);
        hadoopInputFormat.close();
        Mockito.verify(recordReader, Mockito.times(1)).close();
    }

    @Test
    public void testCloseWithoutOpen() throws Exception {
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(new HadoopInputFormatTest.DummyInputFormat(), String.class, Long.class, Job.getInstance());
        hadoopInputFormat.close();
    }

    @Test
    public void testFetchNextInitialState() throws Exception {
        HadoopInputFormatTest.DummyRecordReader recordReader = new HadoopInputFormatTest.DummyRecordReader();
        HadoopInputFormat<String, Long> hadoopInputFormat = setupHadoopInputFormat(new HadoopInputFormatTest.DummyInputFormat(), Job.getInstance(), recordReader);
        hadoopInputFormat.fetchNext();
        Assert.assertThat(hadoopInputFormat.fetched, Is.is(true));
        Assert.assertThat(hadoopInputFormat.hasNext, Is.is(false));
    }

    @Test
    public void testFetchNextRecordReaderHasNewValue() throws Exception {
        HadoopInputFormatTest.DummyRecordReader recordReader = Mockito.mock(HadoopInputFormatTest.DummyRecordReader.class);
        Mockito.when(recordReader.nextKeyValue()).thenReturn(true);
        HadoopInputFormat<String, Long> hadoopInputFormat = setupHadoopInputFormat(new HadoopInputFormatTest.DummyInputFormat(), Job.getInstance(), recordReader);
        hadoopInputFormat.fetchNext();
        Assert.assertThat(hadoopInputFormat.fetched, Is.is(true));
        Assert.assertThat(hadoopInputFormat.hasNext, Is.is(true));
    }

    @Test
    public void testFetchNextRecordReaderThrowsException() throws Exception {
        HadoopInputFormatTest.DummyRecordReader recordReader = Mockito.mock(HadoopInputFormatTest.DummyRecordReader.class);
        Mockito.when(recordReader.nextKeyValue()).thenThrow(new InterruptedException());
        HadoopInputFormat<String, Long> hadoopInputFormat = setupHadoopInputFormat(new HadoopInputFormatTest.DummyInputFormat(), Job.getInstance(), recordReader);
        exception.expect(IOException.class);
        hadoopInputFormat.fetchNext();
        Assert.assertThat(hadoopInputFormat.hasNext, Is.is(true));
    }

    @Test
    public void checkTypeInformation() throws Exception {
        HadoopInputFormat<Void, Long> hadoopInputFormat = new HadoopInputFormat(new HadoopInputFormatTest.DummyVoidKeyInputFormat<Long>(), Void.class, Long.class, Job.getInstance());
        TypeInformation<Tuple2<Void, Long>> tupleType = hadoopInputFormat.getProducedType();
        TypeInformation<Tuple2<Void, Long>> expectedType = new org.apache.flink.api.java.typeutils.TupleTypeInfo(BasicTypeInfo.VOID_TYPE_INFO, BasicTypeInfo.LONG_TYPE_INFO);
        Assert.assertThat(tupleType.isTupleType(), Is.is(true));
        Assert.assertThat(tupleType, Is.is(CoreMatchers.equalTo(expectedType)));
    }

    private class DummyVoidKeyInputFormat<T> extends FileInputFormat<Void, T> {
        public DummyVoidKeyInputFormat() {
        }

        @Override
        public RecordReader<Void, T> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            return null;
        }
    }

    private class DummyRecordReader extends RecordReader<String, Long> {
        @Override
        public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            return false;
        }

        @Override
        public String getCurrentKey() throws IOException, InterruptedException {
            return null;
        }

        @Override
        public Long getCurrentValue() throws IOException, InterruptedException {
            return null;
        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            return 0;
        }

        @Override
        public void close() throws IOException {
        }
    }

    private class DummyInputFormat extends InputFormat<String, Long> {
        @Override
        public List<InputSplit> getSplits(JobContext jobContext) throws IOException, InterruptedException {
            return null;
        }

        @Override
        public RecordReader<String, Long> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
            return new HadoopInputFormatTest.DummyRecordReader();
        }
    }

    private class ConfigurableDummyInputFormat extends HadoopInputFormatTest.DummyInputFormat implements Configurable {
        @Override
        public void setConf(org.apache.hadoop.conf.Configuration configuration) {
        }

        @Override
        public org.apache.hadoop.conf.Configuration getConf() {
            return null;
        }
    }
}

