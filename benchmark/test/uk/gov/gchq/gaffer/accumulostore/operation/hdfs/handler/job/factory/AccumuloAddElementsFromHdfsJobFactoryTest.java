/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.accumulostore.operation.hdfs.handler.job.factory;


import AccumuloAddElementsFromHdfsJobFactory.INGEST_HDFS_DATA_GENERATOR_S_OUTPUT_S;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.accumulo.core.client.mapreduce.AccumuloFileOutputFormat;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.handler.job.partitioner.GafferKeyRangePartitioner;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.handler.job.partitioner.GafferRangePartitioner;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.mapper.AddElementsFromHdfsMapper;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.reducer.AccumuloKeyValueReducer;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.generator.OneToOneElementGenerator;
import uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs;
import uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.TextMapperGenerator;
import uk.gov.gchq.gaffer.hdfs.operation.partitioner.NoPartitioner;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.SplitStoreFromFile;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class AccumuloAddElementsFromHdfsJobFactoryTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    public String inputDir;

    public String outputDir;

    public String splitsDir;

    public String splitsFile;

    @Test
    public void shouldSetupJob() throws IOException {
        // Given
        final JobConf localConf = createLocalConf();
        final FileSystem fs = FileSystem.getLocal(localConf);
        fs.mkdirs(new Path(outputDir));
        fs.mkdirs(new Path(splitsDir));
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(splitsFile), true)))) {
            writer.write("1");
        }
        final AccumuloAddElementsFromHdfsJobFactory factory = new AccumuloAddElementsFromHdfsJobFactory();
        final Job job = Mockito.mock(Job.class);
        final AddElementsFromHdfs operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).useProvidedSplits(true).splitsFilePath(splitsFile).build();
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        BDDMockito.given(job.getConfiguration()).willReturn(localConf);
        // When
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Mockito.verify(job).setJarByClass(factory.getClass());
        Mockito.verify(job).setJobName(String.format(INGEST_HDFS_DATA_GENERATOR_S_OUTPUT_S, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), outputDir));
        Mockito.verify(job).setMapperClass(AddElementsFromHdfsMapper.class);
        Mockito.verify(job).setMapOutputKeyClass(Key.class);
        Mockito.verify(job).setMapOutputValueClass(Value.class);
        Mockito.verify(job).setCombinerClass(AccumuloKeyValueReducer.class);
        Mockito.verify(job).setReducerClass(AccumuloKeyValueReducer.class);
        Mockito.verify(job).setOutputKeyClass(Key.class);
        Mockito.verify(job).setOutputValueClass(Value.class);
        job.setOutputFormatClass(AccumuloFileOutputFormat.class);
        Assert.assertEquals(fs.makeQualified(new Path(outputDir)).toString(), job.getConfiguration().get("mapreduce.output.fileoutputformat.outputdir"));
        Mockito.verify(job).setNumReduceTasks(2);
        Mockito.verify(job).setPartitionerClass(GafferKeyRangePartitioner.class);
        Assert.assertEquals(splitsFile, job.getConfiguration().get(((GafferRangePartitioner.class.getName()) + ".cutFile")));
    }

    @Test
    public void shouldSetupAccumuloPartitionerWhenSetupJobAndPartitionerFlagIsTrue() throws IOException {
        setupAccumuloPartitionerWithGivenPartitioner(GafferKeyRangePartitioner.class);
    }

    @Test
    public void shouldSetupAccumuloPartitionerWhenSetupJobAndPartitionerIsNull() throws IOException {
        setupAccumuloPartitionerWithGivenPartitioner(null);
    }

    @Test
    public void shouldNotSetupAccumuloPartitionerWhenSetupJobAndPartitionerFlagIsFalse() throws IOException {
        setupAccumuloPartitionerWithGivenPartitioner(NoPartitioner.class);
    }

    @Test
    public void shouldSetNoMoreThanMaxNumberOfReducersSpecified() throws IOException, OperationException, StoreException {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        store.initialise("graphId", schema, properties);
        final JobConf localConf = createLocalConf();
        final FileSystem fs = FileSystem.getLocal(localConf);
        fs.mkdirs(new Path(outputDir));
        fs.mkdirs(new Path(splitsDir));
        final BufferedWriter writer = new BufferedWriter(new FileWriter(splitsFile));
        for (int i = 100; i < 200; i++) {
            writer.write((i + "\n"));
        }
        writer.close();
        final SplitStoreFromFile splitTable = new SplitStoreFromFile.Builder().inputPath(splitsFile).build();
        store.execute(splitTable, new uk.gov.gchq.gaffer.store.Context(new User()));
        final AccumuloAddElementsFromHdfsJobFactory factory = new AccumuloAddElementsFromHdfsJobFactory();
        final Job job = Job.getInstance(localConf);
        // When
        AddElementsFromHdfs operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).maxReducers(10).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) <= 10));
        // When
        operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).maxReducers(100).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) <= 100));
        // When
        operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).maxReducers(1000).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) <= 1000));
    }

    @Test
    public void shouldSetNoLessThanMinNumberOfReducersSpecified() throws IOException, OperationException, StoreException {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        store.initialise("graphId", schema, properties);
        final JobConf localConf = createLocalConf();
        final FileSystem fs = FileSystem.getLocal(localConf);
        fs.mkdirs(new Path(outputDir));
        fs.mkdirs(new Path(splitsDir));
        final BufferedWriter writer = new BufferedWriter(new FileWriter(splitsFile));
        for (int i = 100; i < 200; i++) {
            writer.write((i + "\n"));
        }
        writer.close();
        final SplitStoreFromFile splitTable = new SplitStoreFromFile.Builder().inputPath(splitsFile).build();
        store.execute(splitTable, new uk.gov.gchq.gaffer.store.Context(new User()));
        final AccumuloAddElementsFromHdfsJobFactory factory = new AccumuloAddElementsFromHdfsJobFactory();
        final Job job = Job.getInstance(localConf);
        // When
        AddElementsFromHdfs operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).minReducers(10).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) >= 10));
        // When
        operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).minReducers(100).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) >= 100));
        // When
        operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).minReducers(1000).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) >= 1000));
    }

    @Test
    public void shouldSetNumberOfReducersBetweenMinAndMaxSpecified() throws IOException, OperationException, StoreException {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        store.initialise("graphId", schema, properties);
        final JobConf localConf = createLocalConf();
        final FileSystem fs = FileSystem.getLocal(localConf);
        fs.mkdirs(new Path(outputDir));
        fs.mkdirs(new Path(splitsDir));
        final BufferedWriter writer = new BufferedWriter(new FileWriter(splitsFile));
        for (int i = 100; i < 200; i++) {
            writer.write((i + "\n"));
        }
        writer.close();
        final SplitStoreFromFile splitTable = new SplitStoreFromFile.Builder().inputPath(splitsFile).build();
        store.execute(splitTable, new uk.gov.gchq.gaffer.store.Context(new User()));
        final AccumuloAddElementsFromHdfsJobFactory factory = new AccumuloAddElementsFromHdfsJobFactory();
        final Job job = Job.getInstance(localConf);
        // When
        AddElementsFromHdfs operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).minReducers(10).maxReducers(20).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) >= 10));
        Assert.assertTrue(((job.getNumReduceTasks()) <= 20));
        // When
        operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).minReducers(100).maxReducers(200).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) >= 100));
        Assert.assertTrue(((job.getNumReduceTasks()) <= 200));
        // When
        operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).minReducers(1000).maxReducers(2000).splitsFilePath("target/data/splits.txt").build();
        factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Assert.assertTrue(((job.getNumReduceTasks()) >= 1000));
        Assert.assertTrue(((job.getNumReduceTasks()) <= 2000));
    }

    @Test
    public void shouldThrowExceptionWhenMaxReducersSetOutsideOfRange() throws IOException, OperationException, StoreException {
        // Given
        final SingleUseMockAccumuloStore store = new SingleUseMockAccumuloStore();
        final Schema schema = Schema.fromJson(StreamUtil.schemas(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        final AccumuloProperties properties = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(AccumuloAddElementsFromHdfsJobFactoryTest.class));
        store.initialise("graphId", schema, properties);
        final JobConf localConf = createLocalConf();
        final FileSystem fs = FileSystem.getLocal(localConf);
        fs.mkdirs(new Path(outputDir));
        fs.mkdirs(new Path(splitsDir));
        final BufferedWriter writer = new BufferedWriter(new FileWriter(splitsFile));
        for (int i = 100; i < 200; i++) {
            writer.write((i + "\n"));
        }
        writer.close();
        final SplitStoreFromFile splitTable = new SplitStoreFromFile.Builder().inputPath(splitsFile).build();
        store.execute(splitTable, new uk.gov.gchq.gaffer.store.Context(new User()));
        final AccumuloAddElementsFromHdfsJobFactory factory = new AccumuloAddElementsFromHdfsJobFactory();
        final Job job = Job.getInstance(localConf);
        // When
        AddElementsFromHdfs operation = new AddElementsFromHdfs.Builder().outputPath(outputDir).addInputMapperPair(inputDir, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).minReducers(100).maxReducers(101).splitsFilePath("target/data/splits.txt").build();
        // Then
        try {
            factory.setupJob(job, operation, AccumuloAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("not a valid range"));
        }
    }

    public static final class TextMapperGeneratorImpl extends TextMapperGenerator {
        public TextMapperGeneratorImpl() {
            super(new AccumuloAddElementsFromHdfsJobFactoryTest.ExampleGenerator());
        }
    }

    public static final class ExampleGenerator implements OneToOneElementGenerator<String> {
        @Override
        public Element _apply(final String domainObject) {
            final String[] parts = domainObject.split(",");
            return new Entity(parts[0], parts[1]);
        }
    }
}

