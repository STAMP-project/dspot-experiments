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


import HadoopFormatIO.OUTPUT_DIR;
import HadoopFormatIO.OUTPUT_FORMAT_CLASS_ATTR;
import HadoopFormatIO.OUTPUT_KEY_CLASS;
import HadoopFormatIO.OUTPUT_VALUE_CLASS;
import HadoopFormatIO.Write;
import HadoopFormats.DEFAULT_PARTITIONER_CLASS_ATTR;
import Pipeline.PipelineExecutionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for {@link HadoopFormatIO.Write}.
 */
@RunWith(MockitoJUnitRunner.class)
public class HadoopFormatIOWriteTest {
    private static final int REDUCERS_COUNT = 2;

    private static final String LOCKS_FOLDER_NAME = "locks";

    private static Configuration conf;

    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * This test validates {@link HadoopFormatIO.Write Write} transform object creation fails with
     * null configuration. {@link HadoopFormatIO.Write.Builder#withConfiguration(Configuration)
     * withConfiguration(Configuration)} method checks configuration is null and throws exception if
     * it is null.
     */
    @Test
    public void testWriteObjectCreationFailsIfConfigurationIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Hadoop configuration cannot be null");
        HadoopFormatIO.<Text, Employee>write().withConfiguration(null).withPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath()));
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Write.Builder#withConfiguration(Configuration) withConfiguration(Configuration)}
     * function when Hadoop OutputFormat class is not provided by the user in configuration.
     */
    @Test
    public void testWriteValidationFailsMissingOutputFormatInConf() {
        Configuration configuration = new Configuration();
        configuration.setClass(OUTPUT_KEY_CLASS, Text.class, Object.class);
        configuration.setClass(OUTPUT_VALUE_CLASS, Employee.class, Object.class);
        Write<Text, Employee> writeWithWrongConfig = HadoopFormatIO.<Text, Employee>write().withConfiguration(configuration).withPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath()));
        p.apply(Create.of(TestEmployeeDataSet.getEmployeeData())).setTypeDescriptor(TypeDescriptors.kvs(new org.apache.beam.sdk.values.TypeDescriptor<Text>() {}, new org.apache.beam.sdk.values.TypeDescriptor<Employee>() {})).apply("Write", writeWithWrongConfig);
        thrown.expect(PipelineExecutionException.class);
        thrown.expectMessage("Configuration must contain \"mapreduce.job.outputformat.class\"");
        p.run().waitUntilFinish();
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Write.Builder#withConfiguration(Configuration) withConfiguration(Configuration)}
     * function when key class is not provided by the user in configuration.
     */
    @Test
    public void testWriteValidationFailsMissingKeyClassInConf() {
        Configuration configuration = new Configuration();
        configuration.setClass(OUTPUT_FORMAT_CLASS_ATTR, TextOutputFormat.class, OutputFormat.class);
        configuration.setClass(OUTPUT_VALUE_CLASS, Employee.class, Object.class);
        runValidationPipeline(configuration);
        thrown.expect(PipelineExecutionException.class);
        thrown.expectMessage("Configuration must contain \"mapreduce.job.output.key.class\"");
        p.run().waitUntilFinish();
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Write.Builder#withConfiguration(Configuration) withConfiguration(Configuration)}
     * function when value class is not provided by the user in configuration.
     */
    @Test
    public void testWriteValidationFailsMissingValueClassInConf() {
        Configuration configuration = new Configuration();
        configuration.setClass(OUTPUT_FORMAT_CLASS_ATTR, TextOutputFormat.class, OutputFormat.class);
        configuration.setClass(OUTPUT_KEY_CLASS, Text.class, Object.class);
        runValidationPipeline(configuration);
        thrown.expect(PipelineExecutionException.class);
        thrown.expectMessage("Configuration must contain \"mapreduce.job.output.value.class\"");
        p.run().waitUntilFinish();
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Write.Builder#withConfiguration(Configuration) withConfiguration(Configuration)}
     * function when job id is not provided by the user in configuration.
     */
    @Test
    public void testWriteValidationFailsMissingJobIDInConf() {
        Configuration configuration = new Configuration();
        configuration.setClass(OUTPUT_FORMAT_CLASS_ATTR, TextOutputFormat.class, OutputFormat.class);
        configuration.setClass(OUTPUT_KEY_CLASS, Text.class, Object.class);
        configuration.setClass(OUTPUT_VALUE_CLASS, Employee.class, Object.class);
        configuration.set(OUTPUT_DIR, tmpFolder.getRoot().getAbsolutePath());
        runValidationPipeline(configuration);
        thrown.expect(PipelineExecutionException.class);
        thrown.expectMessage("Configuration must contain \"mapreduce.job.id\"");
        p.run().waitUntilFinish();
    }

    @Test
    public void testWritingData() throws IOException {
        HadoopFormatIOWriteTest.conf.set(OUTPUT_DIR, tmpFolder.getRoot().getAbsolutePath());
        List<KV<Text, Employee>> data = TestEmployeeDataSet.getEmployeeData();
        PCollection<KV<Text, Employee>> input = p.apply(Create.of(data)).setTypeDescriptor(TypeDescriptors.kvs(new org.apache.beam.sdk.values.TypeDescriptor<Text>() {}, new org.apache.beam.sdk.values.TypeDescriptor<Employee>() {}));
        input.apply("Write", HadoopFormatIO.<Text, Employee>write().withConfiguration(HadoopFormatIOWriteTest.conf).withPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath())));
        p.run();
        List<KV<Text, Employee>> writtenOutput = EmployeeOutputFormat.getWrittenOutput();
        Assert.assertEquals(data.size(), writtenOutput.size());
        Assert.assertTrue(data.containsAll(writtenOutput));
        Assert.assertTrue(writtenOutput.containsAll(data));
        Mockito.verify(EmployeeOutputFormat.getOutputCommitter()).commitJob(Mockito.any());
        Mockito.verify(EmployeeOutputFormat.getOutputCommitter(), Mockito.times(HadoopFormatIOWriteTest.REDUCERS_COUNT)).commitTask(Mockito.any());
    }

    @Test
    public void testWritingDataFailInvalidKeyType() {
        HadoopFormatIOWriteTest.conf.set(OUTPUT_DIR, tmpFolder.getRoot().getAbsolutePath());
        List<KV<String, Employee>> data = new ArrayList<>();
        data.add(KV.of("key", new Employee("name", "address")));
        PCollection<KV<String, Employee>> input = p.apply("CreateData", Create.of(data)).setTypeDescriptor(TypeDescriptors.kvs(new org.apache.beam.sdk.values.TypeDescriptor<String>() {}, new org.apache.beam.sdk.values.TypeDescriptor<Employee>() {}));
        thrown.expect(PipelineExecutionException.class);
        thrown.expectMessage(String.class.getName());
        input.apply("Write", HadoopFormatIO.<String, Employee>write().withConfiguration(HadoopFormatIOWriteTest.conf).withPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath())));
        p.run().waitUntilFinish();
    }

    @Test
    public void testWritingDataFailInvalidValueType() {
        HadoopFormatIOWriteTest.conf.set(OUTPUT_DIR, tmpFolder.getRoot().getAbsolutePath());
        List<KV<Text, Text>> data = new ArrayList<>();
        data.add(KV.of(new Text("key"), new Text("value")));
        org.apache.beam.sdk.values.TypeDescriptor<Text> textTypeDescriptor = new org.apache.beam.sdk.values.TypeDescriptor<Text>() {};
        PCollection<KV<Text, Text>> input = p.apply(Create.of(data)).setTypeDescriptor(TypeDescriptors.kvs(textTypeDescriptor, textTypeDescriptor));
        thrown.expect(PipelineExecutionException.class);
        thrown.expectMessage(Text.class.getName());
        input.apply("Write", HadoopFormatIO.<Text, Text>write().withConfiguration(HadoopFormatIOWriteTest.conf).withPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath())));
        p.run().waitUntilFinish();
    }

    /**
     * This test validates functionality of {@link HadoopFormatIO.Write#populateDisplayData(DisplayData.Builder)
     * populateDisplayData(DisplayData.WriteBuilder)}.
     */
    @Test
    public void testWriteDisplayData() {
        Write<String, String> write = HadoopFormatIO.<String, String>write().withConfiguration(HadoopFormatIOWriteTest.conf).withPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath()));
        DisplayData displayData = DisplayData.from(write);
        MatcherAssert.assertThat(displayData, hasDisplayItem(OUTPUT_FORMAT_CLASS_ATTR, HadoopFormatIOWriteTest.conf.get(OUTPUT_FORMAT_CLASS_ATTR)));
        MatcherAssert.assertThat(displayData, hasDisplayItem(OUTPUT_KEY_CLASS, HadoopFormatIOWriteTest.conf.get(OUTPUT_KEY_CLASS)));
        MatcherAssert.assertThat(displayData, hasDisplayItem(OUTPUT_VALUE_CLASS, HadoopFormatIOWriteTest.conf.get(OUTPUT_VALUE_CLASS)));
        MatcherAssert.assertThat(displayData, hasDisplayItem(HadoopFormatIO.PARTITIONER_CLASS_ATTR, DEFAULT_PARTITIONER_CLASS_ATTR.getName()));
    }
}

