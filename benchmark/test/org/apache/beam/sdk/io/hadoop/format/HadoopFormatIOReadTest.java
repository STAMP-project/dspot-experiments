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
package org.apache.beam.sdk.io.hadoop.format;


import HadoopFormatIO.Read;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.BoundedSource.BoundedReader;
import org.apache.beam.sdk.io.hadoop.SerializableConfiguration;
import org.apache.beam.sdk.io.hadoop.WritableCoder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link HadoopFormatIO.Read}.
 */
@RunWith(JUnit4.class)
public class HadoopFormatIOReadTest {
    private static SerializableConfiguration serConf;

    private static SimpleFunction<Text, String> myKeyTranslate;

    private static SimpleFunction<Employee, String> myValueTranslate;

    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PBegin input = PBegin.in(p);

    @Test
    public void testReadBuildsCorrectly() {
        Read<String, String> read = HadoopFormatIO.<String, String>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withKeyTranslation(HadoopFormatIOReadTest.myKeyTranslate).withValueTranslation(HadoopFormatIOReadTest.myValueTranslate);
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get(), get());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate, read.getKeyTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate, read.getValueTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate.getOutputTypeDescriptor(), read.getValueTypeDescriptor());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate.getOutputTypeDescriptor(), read.getKeyTypeDescriptor());
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} builds correctly in different order of
     * with configuration/key translation/value translation. This test also validates output
     * PCollection key/value classes are set correctly even if Hadoop configuration is set after
     * setting key/value translation.
     */
    @Test
    public void testReadBuildsCorrectlyInDifferentOrder() {
        Read<String, String> read = HadoopFormatIO.<String, String>read().withValueTranslation(HadoopFormatIOReadTest.myValueTranslate).withConfiguration(HadoopFormatIOReadTest.serConf.get()).withKeyTranslation(HadoopFormatIOReadTest.myKeyTranslate);
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get(), get());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate, read.getKeyTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate, read.getValueTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate.getOutputTypeDescriptor(), read.getKeyTypeDescriptor());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate.getOutputTypeDescriptor(), read.getValueTypeDescriptor());
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} object creation if {@link HadoopFormatIO.Read#withConfiguration(Configuration) withConfiguration(Configuration)} is
     * called more than once.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testReadBuildsCorrectlyIfWithConfigurationIsCalledMoreThanOneTime() {
        SerializableConfiguration diffConf = HadoopFormatIOReadTest.loadTestConfiguration(EmployeeInputFormat.class, Employee.class, Text.class);
        Read<String, String> read = HadoopFormatIO.<String, String>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withKeyTranslation(HadoopFormatIOReadTest.myKeyTranslate).withConfiguration(diffConf.get());
        Assert.assertEquals(diffConf.get(), get());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate, read.getKeyTranslationFunction());
        Assert.assertEquals(null, read.getValueTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate.getOutputTypeDescriptor(), read.getKeyTypeDescriptor());
        Assert.assertEquals(diffConf.get().getClass("value.class", Object.class), read.getValueTypeDescriptor().getRawType());
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} transform object creation fails with null
     * configuration. {@link HadoopFormatIO.Read#withConfiguration(Configuration)
     * withConfiguration(Configuration)} method checks configuration is null and throws exception if
     * it is null.
     */
    @Test
    public void testReadObjectCreationFailsIfConfigurationIsNull() {
        thrown.expect(IllegalArgumentException.class);
        HadoopFormatIO.<Text, Employee>read().withConfiguration(null);
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} transform object creation with only
     * configuration.
     */
    @Test
    public void testReadObjectCreationWithConfiguration() {
        Read<Text, Employee> read = HadoopFormatIO.<Text, Employee>read().withConfiguration(HadoopFormatIOReadTest.serConf.get());
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get(), get());
        Assert.assertEquals(null, read.getKeyTranslationFunction());
        Assert.assertEquals(null, read.getValueTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get().getClass("key.class", Object.class), read.getKeyTypeDescriptor().getRawType());
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get().getClass("value.class", Object.class), read.getValueTypeDescriptor().getRawType());
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} transform object creation fails with
     * configuration and null key translation. {@link HadoopFormatIO.Read#withKeyTranslation(SimpleFunction)} withKeyTranslation(SimpleFunction)}
     * checks keyTranslation is null and throws exception if it null value is passed.
     */
    @Test
    public void testReadObjectCreationFailsIfKeyTranslationFunctionIsNull() {
        thrown.expect(IllegalArgumentException.class);
        HadoopFormatIO.<String, Employee>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withKeyTranslation(null);
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} transform object creation with
     * configuration and key translation.
     */
    @Test
    public void testReadObjectCreationWithConfigurationKeyTranslation() {
        Read<String, Employee> read = HadoopFormatIO.<String, Employee>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withKeyTranslation(HadoopFormatIOReadTest.myKeyTranslate);
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get(), get());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate, read.getKeyTranslationFunction());
        Assert.assertEquals(null, read.getValueTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate.getOutputTypeDescriptor().getRawType(), read.getKeyTypeDescriptor().getRawType());
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get().getClass("value.class", Object.class), read.getValueTypeDescriptor().getRawType());
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} transform object creation fails with
     * configuration and null value translation. {@link HadoopFormatIO.Read#withValueTranslation(SimpleFunction)} withValueTranslation(SimpleFunction)}
     * checks valueTranslation is null and throws exception if null value is passed.
     */
    @Test
    public void testReadObjectCreationFailsIfValueTranslationFunctionIsNull() {
        thrown.expect(IllegalArgumentException.class);
        HadoopFormatIO.<Text, String>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withValueTranslation(null);
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} transform object creation with
     * configuration and value translation.
     */
    @Test
    public void testReadObjectCreationWithConfigurationValueTranslation() {
        Read<Text, String> read = HadoopFormatIO.<Text, String>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withValueTranslation(HadoopFormatIOReadTest.myValueTranslate);
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get(), get());
        Assert.assertEquals(null, read.getKeyTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate, read.getValueTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get().getClass("key.class", Object.class), read.getKeyTypeDescriptor().getRawType());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate.getOutputTypeDescriptor().getRawType(), read.getValueTypeDescriptor().getRawType());
    }

    /**
     * This test validates {@link HadoopFormatIO.Read Read} transform object creation with
     * configuration, key translation and value translation.
     */
    @Test
    public void testReadObjectCreationWithConfigurationKeyTranslationValueTranslation() {
        Read<String, String> read = HadoopFormatIO.<String, String>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withKeyTranslation(HadoopFormatIOReadTest.myKeyTranslate).withValueTranslation(HadoopFormatIOReadTest.myValueTranslate);
        Assert.assertEquals(HadoopFormatIOReadTest.serConf.get(), get());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate, read.getKeyTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate, read.getValueTranslationFunction());
        Assert.assertEquals(HadoopFormatIOReadTest.myKeyTranslate.getOutputTypeDescriptor().getRawType(), read.getKeyTypeDescriptor().getRawType());
        Assert.assertEquals(HadoopFormatIOReadTest.myValueTranslate.getOutputTypeDescriptor().getRawType(), read.getValueTypeDescriptor().getRawType());
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Read#validateTransform()
     * Read.validateTransform()} function when Read transform is created without calling {@link HadoopFormatIO.Read#withConfiguration(Configuration)} withConfiguration(Configuration)}.
     */
    @Test
    public void testReadValidationFailsMissingConfiguration() {
        Read<String, String> read = HadoopFormatIO.read();
        thrown.expect(IllegalArgumentException.class);
        read.validateTransform();
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Read#withConfiguration(Configuration) withConfiguration(Configuration)} function
     * when Hadoop InputFormat class is not provided by the user in configuration.
     */
    @Test
    public void testReadValidationFailsMissingInputFormatInConf() {
        Configuration configuration = new Configuration();
        configuration.setClass("key.class", Text.class, Object.class);
        configuration.setClass("value.class", Employee.class, Object.class);
        thrown.expect(IllegalArgumentException.class);
        HadoopFormatIO.<Text, Employee>read().withConfiguration(configuration);
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Read#withConfiguration(Configuration) withConfiguration(Configuration)} function
     * when key class is not provided by the user in configuration.
     */
    @Test
    public void testReadValidationFailsMissingKeyClassInConf() {
        Configuration configuration = new Configuration();
        configuration.setClass("mapreduce.job.inputformat.class", EmployeeInputFormat.class, InputFormat.class);
        configuration.setClass("value.class", Employee.class, Object.class);
        thrown.expect(IllegalArgumentException.class);
        HadoopFormatIO.<Text, Employee>read().withConfiguration(configuration);
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Read#withConfiguration(Configuration) withConfiguration(Configuration)} function
     * when value class is not provided by the user in configuration.
     */
    @Test
    public void testReadValidationFailsMissingValueClassInConf() {
        Configuration configuration = new Configuration();
        configuration.setClass("mapreduce.job.inputformat.class", EmployeeInputFormat.class, InputFormat.class);
        configuration.setClass("key.class", Text.class, Object.class);
        thrown.expect(IllegalArgumentException.class);
        HadoopFormatIO.<Text, Employee>read().withConfiguration(configuration);
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Read#validateTransform()
     * Read.validateTransform()} function when myKeyTranslate's (simple function provided by user for
     * key translation) input type is not same as Hadoop InputFormat's keyClass(Which is property set
     * in configuration as "key.class").
     */
    @Test
    public void testReadValidationFailsWithWrongInputTypeKeyTranslationFunction() {
        SimpleFunction<LongWritable, String> myKeyTranslateWithWrongInputType = new SimpleFunction<LongWritable, String>() {
            @Override
            public String apply(LongWritable input) {
                return input.toString();
            }
        };
        Read<String, Employee> read = HadoopFormatIO.<String, Employee>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withKeyTranslation(myKeyTranslateWithWrongInputType);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(String.format(("Key translation's input type is not same as hadoop InputFormat : %s key " + "class : %s"), HadoopFormatIOReadTest.serConf.get().getClass("mapreduce.job.inputformat.class", InputFormat.class), HadoopFormatIOReadTest.serConf.get().getClass("key.class", Object.class)));
        read.validateTransform();
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Read#validateTransform()
     * Read.validateTransform()} function when myValueTranslate's (simple function provided by user
     * for value translation) input type is not same as Hadoop InputFormat's valueClass(Which is
     * property set in configuration as "value.class").
     */
    @Test
    public void testReadValidationFailsWithWrongInputTypeValueTranslationFunction() {
        SimpleFunction<LongWritable, String> myValueTranslateWithWrongInputType = new SimpleFunction<LongWritable, String>() {
            @Override
            public String apply(LongWritable input) {
                return input.toString();
            }
        };
        Read<Text, String> read = HadoopFormatIO.<Text, String>read().withConfiguration(HadoopFormatIOReadTest.serConf.get()).withValueTranslation(myValueTranslateWithWrongInputType);
        String expectedMessage = String.format(("Value translation's input type is not same as hadoop InputFormat :  " + "%s value class : %s"), HadoopFormatIOReadTest.serConf.get().getClass("mapreduce.job.inputformat.class", InputFormat.class), HadoopFormatIOReadTest.serConf.get().getClass("value.class", Object.class));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedMessage);
        read.validateTransform();
    }

    @Test
    public void testReadingData() {
        Read<Text, Employee> read = HadoopFormatIO.<Text, Employee>read().withConfiguration(HadoopFormatIOReadTest.serConf.get());
        List<KV<Text, Employee>> expected = TestEmployeeDataSet.getEmployeeData();
        PCollection<KV<Text, Employee>> actual = p.apply("ReadTest", read);
        PAssert.that(actual).containsInAnyOrder(expected);
        p.run();
    }

    /**
     * This test validates functionality of {@link HadoopInputFormatBoundedSource#populateDisplayData(DisplayData.Builder)}
     * populateDisplayData(DisplayData.Builder)}.
     */
    @Test
    public void testReadDisplayData() {
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> boundedSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, new HadoopFormatIO.SerializableSplit());
        DisplayData displayData = DisplayData.from(boundedSource);
        Assert.assertThat(displayData, hasDisplayItem("mapreduce.job.inputformat.class", HadoopFormatIOReadTest.serConf.get().get("mapreduce.job.inputformat.class")));
        Assert.assertThat(displayData, hasDisplayItem("key.class", HadoopFormatIOReadTest.serConf.get().get("key.class")));
        Assert.assertThat(displayData, hasDisplayItem("value.class", HadoopFormatIOReadTest.serConf.get().get("value.class")));
    }

    /**
     * This test validates behavior of {@link HadoopInputFormatBoundedSource} if RecordReader object
     * creation fails.
     */
    @Test
    public void testReadIfCreateRecordReaderFails() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("Exception in creating RecordReader");
        InputFormat<Text, Employee> mockInputFormat = Mockito.mock(EmployeeInputFormat.class);
        Mockito.when(mockInputFormat.createRecordReader(Mockito.any(InputSplit.class), Mockito.any(TaskAttemptContext.class))).thenThrow(new IOException("Exception in creating RecordReader"));
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> boundedSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, new HadoopFormatIO.SerializableSplit());
        boundedSource.setInputFormatObj(mockInputFormat);
        SourceTestUtils.readFromSource(boundedSource, p.getOptions());
    }

    /**
     * This test validates behavior of HadoopInputFormatSource if {@link InputFormat#createRecordReader(InputSplit, TaskAttemptContext)} createRecordReader(InputSplit,
     * TaskAttemptContext)} of InputFormat returns null.
     */
    @Test
    public void testReadWithNullCreateRecordReader() throws Exception {
        InputFormat<Text, Employee> mockInputFormat = Mockito.mock(EmployeeInputFormat.class);
        thrown.expect(IOException.class);
        thrown.expectMessage(String.format("Null RecordReader object returned by %s", mockInputFormat.getClass()));
        Mockito.when(mockInputFormat.createRecordReader(Mockito.any(InputSplit.class), Mockito.any(TaskAttemptContext.class))).thenReturn(null);
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> boundedSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, new HadoopFormatIO.SerializableSplit());
        boundedSource.setInputFormatObj(mockInputFormat);
        SourceTestUtils.readFromSource(boundedSource, p.getOptions());
    }

    /**
     * This test validates behavior of {@link HadoopInputFormatBoundedSource.HadoopInputFormatReader#start() start()} method if InputFormat's
     * {@link InputFormat#getSplits(JobContext)} getSplits(JobContext)} returns InputSplitList having
     * zero records.
     */
    @Test
    public void testReadersStartWhenZeroRecords() throws Exception {
        InputFormat mockInputFormat = Mockito.mock(EmployeeInputFormat.class);
        EmployeeInputFormat.EmployeeRecordReader mockReader = Mockito.mock(EmployeeInputFormat.EmployeeRecordReader.class);
        Mockito.when(mockInputFormat.createRecordReader(Mockito.any(InputSplit.class), Mockito.any(TaskAttemptContext.class))).thenReturn(mockReader);
        Mockito.when(mockReader.nextKeyValue()).thenReturn(false);
        InputSplit mockInputSplit = Mockito.mock(EmployeeInputFormat.NewObjectsEmployeeInputSplit.class);
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> boundedSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, new HadoopFormatIO.SerializableSplit(mockInputSplit));
        boundedSource.setInputFormatObj(mockInputFormat);
        BoundedReader<KV<Text, Employee>> reader = boundedSource.createReader(p.getOptions());
        Assert.assertEquals(false, reader.start());
        Assert.assertEquals(Double.valueOf(1), reader.getFractionConsumed());
        reader.close();
    }

    /**
     * This test validates the method getFractionConsumed()- which indicates the progress of the read
     * in range of 0 to 1.
     */
    @Test
    public void testReadersGetFractionConsumed() throws Exception {
        List<KV<Text, Employee>> referenceRecords = TestEmployeeDataSet.getEmployeeData();
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> hifSource = getTestHIFSource(EmployeeInputFormat.class, Text.class, Employee.class, WritableCoder.of(Text.class), AvroCoder.of(Employee.class));
        long estimatedSize = hifSource.getEstimatedSizeBytes(p.getOptions());
        // Validate if estimated size is equal to the size of records.
        Assert.assertEquals(referenceRecords.size(), estimatedSize);
        List<BoundedSource<KV<Text, Employee>>> boundedSourceList = hifSource.split(0, p.getOptions());
        // Validate if split() has split correctly.
        Assert.assertEquals(TestEmployeeDataSet.NUMBER_OF_SPLITS, boundedSourceList.size());
        List<KV<Text, Employee>> bundleRecords = new ArrayList<>();
        for (BoundedSource<KV<Text, Employee>> source : boundedSourceList) {
            List<KV<Text, Employee>> elements = new ArrayList<>();
            BoundedReader<KV<Text, Employee>> reader = source.createReader(p.getOptions());
            float recordsRead = 0;
            // When start is not called, getFractionConsumed() should return 0.
            Assert.assertEquals(Double.valueOf(0), reader.getFractionConsumed());
            boolean start = reader.start();
            Assert.assertEquals(true, start);
            if (start) {
                elements.add(reader.getCurrent());
                boolean advance = reader.advance();
                // Validate if getFractionConsumed() returns the correct fraction based on
                // the number of records read in the split.
                Assert.assertEquals(Double.valueOf(((++recordsRead) / (TestEmployeeDataSet.NUMBER_OF_RECORDS_IN_EACH_SPLIT))), reader.getFractionConsumed());
                Assert.assertEquals(true, advance);
                while (advance) {
                    elements.add(reader.getCurrent());
                    advance = reader.advance();
                    Assert.assertEquals(Double.valueOf(((++recordsRead) / (TestEmployeeDataSet.NUMBER_OF_RECORDS_IN_EACH_SPLIT))), reader.getFractionConsumed());
                } 
                bundleRecords.addAll(elements);
            }
            // Validate if getFractionConsumed() returns 1 after reading is complete.
            Assert.assertEquals(Double.valueOf(1), reader.getFractionConsumed());
            reader.close();
        }
        Assert.assertThat(bundleRecords, Matchers.containsInAnyOrder(referenceRecords.toArray()));
    }

    /**
     * This test validates the method getFractionConsumed()- when a bad progress value is returned by
     * the inputformat.
     */
    @Test
    public void testGetFractionConsumedForBadProgressValue() throws Exception {
        InputFormat<Text, Employee> mockInputFormat = Mockito.mock(EmployeeInputFormat.class);
        EmployeeInputFormat.EmployeeRecordReader mockReader = Mockito.mock(EmployeeInputFormat.EmployeeRecordReader.class);
        Mockito.when(mockInputFormat.createRecordReader(Mockito.any(InputSplit.class), Mockito.any(TaskAttemptContext.class))).thenReturn(mockReader);
        Mockito.when(mockReader.nextKeyValue()).thenReturn(true);
        // Set to a bad value , not in range of 0 to 1
        Mockito.when(mockReader.getProgress()).thenReturn(2.0F);
        InputSplit mockInputSplit = Mockito.mock(EmployeeInputFormat.NewObjectsEmployeeInputSplit.class);
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> boundedSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, new HadoopFormatIO.SerializableSplit(mockInputSplit));
        boundedSource.setInputFormatObj(mockInputFormat);
        BoundedReader<KV<Text, Employee>> reader = boundedSource.createReader(p.getOptions());
        Assert.assertEquals(Double.valueOf(0), reader.getFractionConsumed());
        boolean start = reader.start();
        Assert.assertEquals(true, start);
        if (start) {
            boolean advance = reader.advance();
            Assert.assertEquals(null, reader.getFractionConsumed());
            Assert.assertEquals(true, advance);
            if (advance) {
                advance = reader.advance();
                Assert.assertEquals(null, reader.getFractionConsumed());
            }
        }
        // Validate if getFractionConsumed() returns null after few number of reads as getProgress
        // returns invalid value '2' which is not in the range of 0 to 1.
        Assert.assertEquals(null, reader.getFractionConsumed());
        reader.close();
    }

    /**
     * This test validates that reader and its parent source reads the same records.
     */
    @Test
    public void testReaderAndParentSourceReadsSameData() throws Exception {
        InputSplit mockInputSplit = Mockito.mock(EmployeeInputFormat.NewObjectsEmployeeInputSplit.class);
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> boundedSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, new HadoopFormatIO.SerializableSplit(mockInputSplit));
        BoundedReader<KV<Text, Employee>> reader = boundedSource.createReader(p.getOptions());
        SourceTestUtils.assertUnstartedReaderReadsSameAsItsSource(reader, p.getOptions());
    }

    /**
     * This test verifies that the method {@link HadoopInputFormatBoundedSource.HadoopInputFormatReader#getCurrentSource() getCurrentSource()}
     * returns correct source object.
     */
    @Test
    public void testGetCurrentSourceFunction() throws Exception {
        HadoopFormatIO.SerializableSplit split = new HadoopFormatIO.SerializableSplit();
        BoundedSource<KV<Text, Employee>> source = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, split);
        BoundedReader<KV<Text, Employee>> hifReader = source.createReader(p.getOptions());
        BoundedSource<KV<Text, Employee>> hifSource = hifReader.getCurrentSource();
        Assert.assertEquals(hifSource, source);
    }

    /**
     * This test validates behavior of {@link HadoopInputFormatBoundedSource#createReader(PipelineOptions)} createReader()} method when
     * {@link HadoopInputFormatBoundedSource#split(long, PipelineOptions)} is not called.
     */
    @Test
    public void testCreateReaderIfSplitNotCalled() throws Exception {
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> hifSource = getTestHIFSource(EmployeeInputFormat.class, Text.class, Employee.class, WritableCoder.of(Text.class), AvroCoder.of(Employee.class));
        thrown.expect(IOException.class);
        thrown.expectMessage("Cannot create reader as source is not split yet.");
        hifSource.createReader(p.getOptions());
    }

    /**
     * This test validates behavior of {@link HadoopInputFormatBoundedSource#computeSplitsIfNecessary() computeSplits()} when Hadoop
     * InputFormat's {@link InputFormat#getSplits(JobContext)} returns empty list.
     */
    @Test
    public void testComputeSplitsIfGetSplitsReturnsEmptyList() throws Exception {
        InputFormat<?, ?> mockInputFormat = Mockito.mock(EmployeeInputFormat.class);
        HadoopFormatIO.SerializableSplit mockInputSplit = Mockito.mock(HadoopFormatIO.SerializableSplit.class);
        Mockito.when(mockInputFormat.getSplits(Mockito.any(JobContext.class))).thenReturn(new ArrayList());
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> hifSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, mockInputSplit);
        thrown.expect(IOException.class);
        thrown.expectMessage("Error in computing splits, getSplits() returns a empty list");
        hifSource.setInputFormatObj(mockInputFormat);
        hifSource.computeSplitsIfNecessary();
    }

    /**
     * This test validates behavior of {@link HadoopInputFormatBoundedSource#computeSplitsIfNecessary() computeSplits()} when Hadoop
     * InputFormat's {@link InputFormat#getSplits(JobContext)} getSplits(JobContext)} returns NULL
     * value.
     */
    @Test
    public void testComputeSplitsIfGetSplitsReturnsNullValue() throws Exception {
        InputFormat<Text, Employee> mockInputFormat = Mockito.mock(EmployeeInputFormat.class);
        HadoopFormatIO.SerializableSplit mockInputSplit = Mockito.mock(HadoopFormatIO.SerializableSplit.class);
        Mockito.when(mockInputFormat.getSplits(Mockito.any(JobContext.class))).thenReturn(null);
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> hifSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, mockInputSplit);
        thrown.expect(IOException.class);
        thrown.expectMessage("Error in computing splits, getSplits() returns null.");
        hifSource.setInputFormatObj(mockInputFormat);
        hifSource.computeSplitsIfNecessary();
    }

    /**
     * This test validates behavior of {@link HadoopInputFormatBoundedSource#computeSplitsIfNecessary() computeSplits()} if Hadoop
     * InputFormat's {@link InputFormat#getSplits(JobContext)} getSplits(JobContext)} returns
     * InputSplit list having some null values.
     */
    @Test
    public void testComputeSplitsIfGetSplitsReturnsListHavingNullValues() throws Exception {
        // InputSplit list having null value.
        InputSplit mockInputSplit = Mockito.mock(InputSplit.class, Mockito.withSettings().extraInterfaces(Writable.class));
        List<InputSplit> inputSplitList = new ArrayList<>();
        inputSplitList.add(mockInputSplit);
        inputSplitList.add(null);
        InputFormat<Text, Employee> mockInputFormat = Mockito.mock(EmployeeInputFormat.class);
        Mockito.when(mockInputFormat.getSplits(Mockito.any(JobContext.class))).thenReturn(inputSplitList);
        HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> hifSource = // No key translation required.
        // No value translation required.
        new HadoopFormatIO.HadoopInputFormatBoundedSource(HadoopFormatIOReadTest.serConf, WritableCoder.of(Text.class), AvroCoder.of(Employee.class), null, null, new HadoopFormatIO.SerializableSplit());
        thrown.expect(IOException.class);
        thrown.expectMessage(("Error in computing splits, split is null in InputSplits list populated " + "by getSplits() : "));
        hifSource.setInputFormatObj(mockInputFormat);
        hifSource.computeSplitsIfNecessary();
    }

    /**
     * This test validates records emitted in PCollection are immutable if InputFormat's recordReader
     * returns same objects(i.e. same locations in memory) but with updated values for each record.
     */
    @Test
    public void testImmutablityOfOutputOfReadIfRecordReaderObjectsAreMutable() throws Exception {
        List<BoundedSource<KV<Text, Employee>>> boundedSourceList = getBoundedSourceList(ReuseObjectsEmployeeInputFormat.class, Text.class, Employee.class, WritableCoder.of(Text.class), AvroCoder.of(Employee.class));
        List<KV<Text, Employee>> bundleRecords = new ArrayList<>();
        for (BoundedSource<KV<Text, Employee>> source : boundedSourceList) {
            List<KV<Text, Employee>> elems = SourceTestUtils.readFromSource(source, p.getOptions());
            bundleRecords.addAll(elems);
        }
        List<KV<Text, Employee>> referenceRecords = TestEmployeeDataSet.getEmployeeData();
        Assert.assertThat(bundleRecords, Matchers.containsInAnyOrder(referenceRecords.toArray()));
    }

    /**
     * Test reading if InputFormat implements {@link org.apache.hadoop.conf.Configurable
     * Configurable}.
     */
    @Test
    public void testReadingWithConfigurableInputFormat() throws Exception {
        List<BoundedSource<KV<Text, Employee>>> boundedSourceList = getBoundedSourceList(ConfigurableEmployeeInputFormat.class, Text.class, Employee.class, WritableCoder.of(Text.class), AvroCoder.of(Employee.class));
        for (BoundedSource<KV<Text, Employee>> source : boundedSourceList) {
            // Cast to HadoopInputFormatBoundedSource to access getInputFormat().
            @SuppressWarnings("unchecked")
            HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee> hifSource = ((HadoopFormatIO.HadoopInputFormatBoundedSource<Text, Employee>) (source));
            hifSource.createInputFormatInstance();
            ConfigurableEmployeeInputFormat inputFormatObj = ((ConfigurableEmployeeInputFormat) (hifSource.getInputFormat()));
            Assert.assertEquals(true, inputFormatObj.isConfSet);
        }
    }

    /**
     * This test validates records emitted in PCollection are immutable if InputFormat's {@link org.apache.hadoop.mapreduce.RecordReader RecordReader} returns different objects (i.e.
     * different locations in memory).
     */
    @Test
    public void testImmutablityOfOutputOfReadIfRecordReaderObjectsAreImmutable() throws Exception {
        List<BoundedSource<KV<Text, Employee>>> boundedSourceList = getBoundedSourceList(EmployeeInputFormat.class, Text.class, Employee.class, WritableCoder.of(Text.class), AvroCoder.of(Employee.class));
        List<KV<Text, Employee>> bundleRecords = new ArrayList<>();
        for (BoundedSource<KV<Text, Employee>> source : boundedSourceList) {
            List<KV<Text, Employee>> elems = SourceTestUtils.readFromSource(source, p.getOptions());
            bundleRecords.addAll(elems);
        }
        List<KV<Text, Employee>> referenceRecords = TestEmployeeDataSet.getEmployeeData();
        Assert.assertThat(bundleRecords, Matchers.containsInAnyOrder(referenceRecords.toArray()));
    }

    @Test
    public void testValidateConfigurationWithDBInputFormat() {
        Configuration conf = new Configuration();
        conf.setClass("key.class", LongWritable.class, Object.class);
        conf.setClass("value.class", Text.class, Object.class);
        conf.setClass("mapreduce.job.inputformat.class", DBInputFormat.class, InputFormat.class);
        thrown.expect(IllegalArgumentException.class);
        HadoopFormatIO.<String, String>read().withConfiguration(get()).withKeyTranslation(HadoopFormatIOReadTest.myKeyTranslate).withValueTranslation(HadoopFormatIOReadTest.myValueTranslate);
    }
}

