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
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link HadoopInputFormat}.
 */
public class HadoopInputFormatTest {
    @Test
    public void testConfigureWithConfigurableInstance() {
        HadoopInputFormatTest.ConfigurableDummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.ConfigurableDummyInputFormat.class);
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(inputFormat, String.class, Long.class, new JobConf());
        Mockito.verify(inputFormat, Mockito.times(1)).setConf(ArgumentMatchers.any(JobConf.class));
        hadoopInputFormat.configure(new Configuration());
        Mockito.verify(inputFormat, Mockito.times(2)).setConf(ArgumentMatchers.any(JobConf.class));
    }

    @Test
    public void testConfigureWithJobConfigurableInstance() {
        HadoopInputFormatTest.JobConfigurableDummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.JobConfigurableDummyInputFormat.class);
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(inputFormat, String.class, Long.class, new JobConf());
        Mockito.verify(inputFormat, Mockito.times(1)).configure(ArgumentMatchers.any(JobConf.class));
        hadoopInputFormat.configure(new Configuration());
        Mockito.verify(inputFormat, Mockito.times(2)).configure(ArgumentMatchers.any(JobConf.class));
    }

    @Test
    public void testOpenClose() throws Exception {
        HadoopInputFormatTest.DummyRecordReader recordReader = Mockito.mock(HadoopInputFormatTest.DummyRecordReader.class);
        HadoopInputFormatTest.DummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.DummyInputFormat.class);
        Mockito.when(inputFormat.getRecordReader(ArgumentMatchers.any(InputSplit.class), ArgumentMatchers.any(JobConf.class), ArgumentMatchers.any(Reporter.class))).thenReturn(recordReader);
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(inputFormat, String.class, Long.class, new JobConf());
        hadoopInputFormat.open(getHadoopInputSplit());
        Mockito.verify(inputFormat, Mockito.times(1)).getRecordReader(ArgumentMatchers.any(InputSplit.class), ArgumentMatchers.any(JobConf.class), ArgumentMatchers.any(Reporter.class));
        Mockito.verify(recordReader, Mockito.times(1)).createKey();
        Mockito.verify(recordReader, Mockito.times(1)).createValue();
        Assert.assertThat(hadoopInputFormat.fetched, Is.is(false));
        hadoopInputFormat.close();
        Mockito.verify(recordReader, Mockito.times(1)).close();
    }

    @Test
    public void testOpenWithConfigurableReader() throws Exception {
        HadoopInputFormatTest.ConfigurableDummyRecordReader recordReader = Mockito.mock(HadoopInputFormatTest.ConfigurableDummyRecordReader.class);
        HadoopInputFormatTest.DummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.DummyInputFormat.class);
        Mockito.when(inputFormat.getRecordReader(ArgumentMatchers.any(InputSplit.class), ArgumentMatchers.any(JobConf.class), ArgumentMatchers.any(Reporter.class))).thenReturn(recordReader);
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(inputFormat, String.class, Long.class, new JobConf());
        hadoopInputFormat.open(getHadoopInputSplit());
        Mockito.verify(inputFormat, Mockito.times(1)).getRecordReader(ArgumentMatchers.any(InputSplit.class), ArgumentMatchers.any(JobConf.class), ArgumentMatchers.any(Reporter.class));
        Mockito.verify(recordReader, Mockito.times(1)).setConf(ArgumentMatchers.any(JobConf.class));
        Mockito.verify(recordReader, Mockito.times(1)).createKey();
        Mockito.verify(recordReader, Mockito.times(1)).createValue();
        Assert.assertThat(hadoopInputFormat.fetched, Is.is(false));
    }

    @Test
    public void testCreateInputSplits() throws Exception {
        FileSplit[] result = new FileSplit[1];
        result[0] = getFileSplit();
        HadoopInputFormatTest.DummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.DummyInputFormat.class);
        Mockito.when(inputFormat.getSplits(ArgumentMatchers.any(JobConf.class), ArgumentMatchers.anyInt())).thenReturn(result);
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(inputFormat, String.class, Long.class, new JobConf());
        hadoopInputFormat.createInputSplits(2);
        Mockito.verify(inputFormat, Mockito.times(1)).getSplits(ArgumentMatchers.any(JobConf.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void testReachedEndWithElementsRemaining() throws IOException {
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(new HadoopInputFormatTest.DummyInputFormat(), String.class, Long.class, new JobConf());
        hadoopInputFormat.fetched = true;
        hadoopInputFormat.hasNext = true;
        Assert.assertThat(hadoopInputFormat.reachedEnd(), Is.is(false));
    }

    @Test
    public void testReachedEndWithNoElementsRemaining() throws IOException {
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(new HadoopInputFormatTest.DummyInputFormat(), String.class, Long.class, new JobConf());
        hadoopInputFormat.fetched = true;
        hadoopInputFormat.hasNext = false;
        Assert.assertThat(hadoopInputFormat.reachedEnd(), Is.is(true));
    }

    @Test
    public void testFetchNext() throws IOException {
        HadoopInputFormatTest.DummyRecordReader recordReader = Mockito.mock(HadoopInputFormatTest.DummyRecordReader.class);
        Mockito.when(recordReader.next(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(Long.class))).thenReturn(true);
        HadoopInputFormatTest.DummyInputFormat inputFormat = Mockito.mock(HadoopInputFormatTest.DummyInputFormat.class);
        Mockito.when(inputFormat.getRecordReader(ArgumentMatchers.any(InputSplit.class), ArgumentMatchers.any(JobConf.class), ArgumentMatchers.any(Reporter.class))).thenReturn(recordReader);
        HadoopInputFormat<String, Long> hadoopInputFormat = new HadoopInputFormat(inputFormat, String.class, Long.class, new JobConf());
        hadoopInputFormat.open(getHadoopInputSplit());
        hadoopInputFormat.fetchNext();
        Mockito.verify(recordReader, Mockito.times(1)).next(ArgumentMatchers.nullable(String.class), ArgumentMatchers.anyLong());
        Assert.assertThat(hadoopInputFormat.hasNext, Is.is(true));
        Assert.assertThat(hadoopInputFormat.fetched, Is.is(true));
    }

    @Test
    public void checkTypeInformation() throws Exception {
        HadoopInputFormat<Void, Long> hadoopInputFormat = new HadoopInputFormat(new HadoopInputFormatTest.DummyVoidKeyInputFormat<Long>(), Void.class, Long.class, new JobConf());
        TypeInformation<Tuple2<Void, Long>> tupleType = hadoopInputFormat.getProducedType();
        TypeInformation<Tuple2<Void, Long>> expectedType = new org.apache.flink.api.java.typeutils.TupleTypeInfo(BasicTypeInfo.VOID_TYPE_INFO, BasicTypeInfo.LONG_TYPE_INFO);
        Assert.assertThat(tupleType.isTupleType(), Is.is(true));
        Assert.assertThat(tupleType, Is.is(CoreMatchers.equalTo(expectedType)));
    }

    @Test
    public void testCloseWithoutOpen() throws Exception {
        HadoopInputFormat<Void, Long> hadoopInputFormat = new HadoopInputFormat(new HadoopInputFormatTest.DummyVoidKeyInputFormat<Long>(), Void.class, Long.class, new JobConf());
        hadoopInputFormat.close();
    }

    private class DummyVoidKeyInputFormat<T> extends FileInputFormat<Void, T> {
        public DummyVoidKeyInputFormat() {
        }

        @Override
        public RecordReader<Void, T> getRecordReader(InputSplit inputSplit, JobConf jobConf, Reporter reporter) throws IOException {
            return null;
        }
    }

    private class DummyRecordReader implements RecordReader<String, Long> {
        @Override
        public float getProgress() throws IOException {
            return 0;
        }

        @Override
        public boolean next(String s, Long aLong) throws IOException {
            return false;
        }

        @Override
        public String createKey() {
            return null;
        }

        @Override
        public Long createValue() {
            return null;
        }

        @Override
        public long getPos() throws IOException {
            return 0;
        }

        @Override
        public void close() throws IOException {
        }
    }

    private class ConfigurableDummyRecordReader implements Configurable , RecordReader<String, Long> {
        @Override
        public void setConf(org.apache.hadoop.conf.Configuration configuration) {
        }

        @Override
        public org.apache.hadoop.conf.Configuration getConf() {
            return null;
        }

        @Override
        public boolean next(String s, Long aLong) throws IOException {
            return false;
        }

        @Override
        public String createKey() {
            return null;
        }

        @Override
        public Long createValue() {
            return null;
        }

        @Override
        public long getPos() throws IOException {
            return 0;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public float getProgress() throws IOException {
            return 0;
        }
    }

    private class DummyInputFormat implements InputFormat<String, Long> {
        @Override
        public InputSplit[] getSplits(JobConf jobConf, int i) throws IOException {
            return new InputSplit[0];
        }

        @Override
        public RecordReader<String, Long> getRecordReader(InputSplit inputSplit, JobConf jobConf, Reporter reporter) throws IOException {
            return null;
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

    private class JobConfigurableDummyInputFormat extends HadoopInputFormatTest.DummyInputFormat implements JobConfigurable {
        @Override
        public void configure(JobConf jobConf) {
        }
    }
}

