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


import DoFn.Element;
import DoFn.ProcessElement;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.beam.examples.WordCount;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;


/**
 * Tests {@link HadoopFormatIO} output with batch and stream pipeline.
 */
public class HadoopFormatIOSequenceFileTest {
    private static final Instant START_TIME = new Instant(0);

    private static final String TEST_FOLDER_NAME = "test";

    private static final String LOCKS_FOLDER_NAME = "locks";

    private static final int REDUCERS_COUNT = 2;

    private static final List<String> SENTENCES = Arrays.asList("Hello world this is first streamed event", "Hello again this is sedcond streamed event", "Third time Hello event created", "And last event will was sent now", "Hello from second window", "First event from second window");

    private static final List<String> FIRST_WIN_WORDS = HadoopFormatIOSequenceFileTest.SENTENCES.subList(0, 4);

    private static final List<String> SECOND_WIN_WORDS = HadoopFormatIOSequenceFileTest.SENTENCES.subList(4, 6);

    private static final Duration WINDOW_DURATION = Duration.standardMinutes(1);

    private static final SerializableFunction<KV<String, Long>, KV<Text, LongWritable>> KV_STR_INT_2_TXT_LONGWRITABLE = (KV<String, Long> element) -> KV.of(new Text(element.getKey()), new LongWritable(element.getValue()));

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Test
    @Category(ValidatesRunner.class)
    public void batchTest() {
        String outputDir = getOutputDirPath("batchTest");
        Configuration conf = HadoopFormatIOSequenceFileTest.createWriteConf(SequenceFileOutputFormat.class, Text.class, LongWritable.class, outputDir, HadoopFormatIOSequenceFileTest.REDUCERS_COUNT, "0");
        executeBatchTest(HadoopFormatIO.<Text, LongWritable>write().withConfiguration(conf).withPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath())), outputDir);
        Assert.assertEquals("In lock folder shouldn't be any file", 0, new File(getLocksDirPath()).list().length);
    }

    @Test
    @Category(ValidatesRunner.class)
    public void batchTestWithoutPartitioner() {
        String outputDir = getOutputDirPath("batchTestWithoutPartitioner");
        Configuration conf = HadoopFormatIOSequenceFileTest.createWriteConf(SequenceFileOutputFormat.class, Text.class, LongWritable.class, outputDir, HadoopFormatIOSequenceFileTest.REDUCERS_COUNT, "0");
        executeBatchTest(HadoopFormatIO.<Text, LongWritable>write().withConfiguration(conf).withoutPartitioning().withExternalSynchronization(new HDFSSynchronization(getLocksDirPath())), outputDir);
        Assert.assertEquals("In lock folder shouldn't be any file", 0, new File(getLocksDirPath()).list().length);
    }

    @Test
    public void streamTest() {
        TestStream<String> stringsStream = TestStream.create(StringUtf8Coder.of()).advanceWatermarkTo(HadoopFormatIOSequenceFileTest.START_TIME).addElements(event(HadoopFormatIOSequenceFileTest.FIRST_WIN_WORDS.get(0), 2L)).advanceWatermarkTo(HadoopFormatIOSequenceFileTest.START_TIME.plus(Duration.standardSeconds(27L))).addElements(event(HadoopFormatIOSequenceFileTest.FIRST_WIN_WORDS.get(1), 25L), event(HadoopFormatIOSequenceFileTest.FIRST_WIN_WORDS.get(2), 18L), event(HadoopFormatIOSequenceFileTest.FIRST_WIN_WORDS.get(3), 28L)).advanceWatermarkTo(HadoopFormatIOSequenceFileTest.START_TIME.plus(Duration.standardSeconds(65L))).addElements(event(HadoopFormatIOSequenceFileTest.SECOND_WIN_WORDS.get(0), 61L), event(HadoopFormatIOSequenceFileTest.SECOND_WIN_WORDS.get(1), 63L)).advanceWatermarkToInfinity();
        String outputDirPath = getOutputDirPath("streamTest");
        PCollection<KV<Text, LongWritable>> dataToWrite = pipeline.apply(stringsStream).apply(Window.into(FixedWindows.of(HadoopFormatIOSequenceFileTest.WINDOW_DURATION))).apply(ParDo.of(new HadoopFormatIOSequenceFileTest.ConvertToLowerCaseFn())).apply(new WordCount.CountWords()).apply("ConvertToHadoopFormat", ParDo.of(new HadoopFormatIOSequenceFileTest.ConvertToHadoopFormatFn(HadoopFormatIOSequenceFileTest.KV_STR_INT_2_TXT_LONGWRITABLE))).setTypeDescriptor(TypeDescriptors.kvs(new org.apache.beam.sdk.values.TypeDescriptor<Text>() {}, new org.apache.beam.sdk.values.TypeDescriptor<LongWritable>() {}));
        HadoopFormatIOSequenceFileTest.ConfigTransform<Text, LongWritable> configurationTransformation = new HadoopFormatIOSequenceFileTest.ConfigTransform<>(outputDirPath, Text.class, LongWritable.class);
        dataToWrite.apply(HadoopFormatIO.<Text, LongWritable>write().withConfigurationTransform(configurationTransformation).withExternalSynchronization(new HDFSSynchronization(getLocksDirPath())));
        pipeline.run();
        Map<String, Long> values = loadWrittenDataAsMap(outputDirPath);
        MatcherAssert.assertThat(values.entrySet(), Matchers.equalTo(HadoopFormatIOSequenceFileTest.computeWordCounts(HadoopFormatIOSequenceFileTest.FIRST_WIN_WORDS).entrySet()));
        Assert.assertEquals("In lock folder shouldn't be any file", 0, new File(getLocksDirPath()).list().length);
    }

    private static class ConvertToHadoopFormatFn<InputT, OutputT> extends DoFn<InputT, OutputT> {
        private SerializableFunction<InputT, OutputT> transformFn;

        ConvertToHadoopFormatFn(SerializableFunction<InputT, OutputT> transformFn) {
            this.transformFn = transformFn;
        }

        @DoFn.ProcessElement
        public void processElement(@DoFn.Element
        InputT element, OutputReceiver<OutputT> outReceiver) {
            outReceiver.output(transformFn.apply(element));
        }
    }

    private static class ConvertToLowerCaseFn extends DoFn<String, String> {
        @DoFn.ProcessElement
        public void processElement(@DoFn.Element
        String element, OutputReceiver<String> receiver) {
            receiver.output(element.toLowerCase());
        }

        @Override
        public org.apache.beam.sdk.values.TypeDescriptor<String> getOutputTypeDescriptor() {
            return super.getOutputTypeDescriptor();
        }
    }

    private static class ConfigTransform<KeyT, ValueT> extends PTransform<PCollection<? extends KV<KeyT, ValueT>>, PCollectionView<Configuration>> {
        private String outputDirPath;

        private Class<?> keyClass;

        private Class<?> valueClass;

        private int windowNum = 0;

        private ConfigTransform(String outputDirPath, Class<?> keyClass, Class<?> valueClass) {
            this.outputDirPath = outputDirPath;
            this.keyClass = keyClass;
            this.valueClass = valueClass;
        }

        @Override
        public PCollectionView<Configuration> expand(PCollection<? extends KV<KeyT, ValueT>> input) {
            Configuration conf = HadoopFormatIOSequenceFileTest.createWriteConf(SequenceFileOutputFormat.class, keyClass, valueClass, outputDirPath, HadoopFormatIOSequenceFileTest.REDUCERS_COUNT, String.valueOf(((windowNum)++)));
            return input.getPipeline().apply(Create.<Configuration>of(conf)).apply(View.<Configuration>asSingleton().withDefaultValue(conf));
        }
    }
}

