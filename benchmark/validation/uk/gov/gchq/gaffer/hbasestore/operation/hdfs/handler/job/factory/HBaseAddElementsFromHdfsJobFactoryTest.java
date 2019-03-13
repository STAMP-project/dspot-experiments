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
package uk.gov.gchq.gaffer.hbasestore.operation.hdfs.handler.job.factory;


import HBaseStoreConstants.OPERATION_HDFS_STAGING_PATH;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.generator.OneToOneElementGenerator;
import uk.gov.gchq.gaffer.hbasestore.HBaseProperties;
import uk.gov.gchq.gaffer.hbasestore.HBaseStore;
import uk.gov.gchq.gaffer.hbasestore.SingleUseMiniHBaseStore;
import uk.gov.gchq.gaffer.hbasestore.operation.hdfs.mapper.AddElementsFromHdfsMapper;
import uk.gov.gchq.gaffer.hbasestore.operation.hdfs.reducer.AddElementsFromHdfsReducer;
import uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs;
import uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.TextJobInitialiser;
import uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.TextMapperGenerator;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class HBaseAddElementsFromHdfsJobFactoryTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private String inputDir;

    private String outputDir;

    private String stagingDir;

    private String failureDir;

    @Test
    public void shouldSetupJob() throws IOException, StoreException {
        // Given
        final JobConf localConf = createLocalConf();
        final FileSystem fs = FileSystem.getLocal(localConf);
        fs.mkdirs(new Path(outputDir));
        final HBaseAddElementsFromHdfsJobFactory factory = new HBaseAddElementsFromHdfsJobFactory();
        final Job job = Mockito.mock(Job.class);
        final AddElementsFromHdfs operation = new AddElementsFromHdfs.Builder().addInputMapperPair(new Path(inputDir).toString(), HBaseAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName()).outputPath(outputDir).failurePath(failureDir).jobInitialiser(new TextJobInitialiser()).option(OPERATION_HDFS_STAGING_PATH, stagingDir).build();
        final HBaseStore store = new SingleUseMiniHBaseStore();
        final Schema schema = Schema.fromJson(StreamUtil.schemas(getClass()));
        final HBaseProperties properties = HBaseProperties.loadStoreProperties(StreamUtil.storeProps(getClass()));
        store.initialise("graphId", schema, properties);
        BDDMockito.given(job.getConfiguration()).willReturn(localConf);
        // When
        factory.setupJob(job, operation, HBaseAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName(), store);
        // Then
        Mockito.verify(job).setJarByClass(factory.getClass());
        Mockito.verify(job).setJobName(((("Ingest HDFS data: Generator=" + (HBaseAddElementsFromHdfsJobFactoryTest.TextMapperGeneratorImpl.class.getName())) + ", output=") + (outputDir)));
        Mockito.verify(job).setMapperClass(AddElementsFromHdfsMapper.class);
        Mockito.verify(job).setMapOutputKeyClass(ImmutableBytesWritable.class);
        Mockito.verify(job).setMapOutputValueClass(KeyValue.class);
        Mockito.verify(job).setReducerClass(AddElementsFromHdfsReducer.class);
        Mockito.verify(job).setOutputKeyClass(ImmutableBytesWritable.class);
        Mockito.verify(job).setOutputValueClass(KeyValue.class);
        Mockito.verify(job).setOutputFormatClass(HFileOutputFormat2.class);
        Assert.assertEquals(fs.makeQualified(new Path(outputDir)).toString(), job.getConfiguration().get("mapreduce.output.fileoutputformat.outputdir"));
        Mockito.verify(job).setNumReduceTasks(1);
    }

    public static final class TextMapperGeneratorImpl extends TextMapperGenerator {
        public TextMapperGeneratorImpl() {
            super(new HBaseAddElementsFromHdfsJobFactoryTest.ExampleGenerator());
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

