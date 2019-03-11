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
package org.apache.beam.sdk.io.text;


import TextIO.TypedWrite;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.Compression;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.common.FileBasedIOITHelper;
import org.apache.beam.sdk.io.common.HashingFn;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration tests for {@link org.apache.beam.sdk.io.TextIO}.
 *
 * <p>Run this test using the command below. Pass in connection information via PipelineOptions:
 *
 * <pre>
 *  ./gradlew integrationTest -p sdks/java/io/file-based-io-tests
 *  -DintegrationTestPipelineOptions='[
 *  "--numberOfRecords=100000",
 *  "--filenamePrefix=output_file_path",
 *  "--compressionType=GZIP"
 *  ]'
 *  --tests org.apache.beam.sdk.io.text.TextIOIT
 *  -DintegrationTestRunner=direct
 * </pre>
 *
 * <p>Please see 'build_rules.gradle' file for instructions regarding running this test using Beam
 * performance testing framework.
 */
@RunWith(JUnit4.class)
public class TextIOIT {
    private static final Logger LOG = LoggerFactory.getLogger(TextIOIT.class);

    private static String filenamePrefix;

    private static Integer numberOfTextLines;

    private static Compression compressionType;

    private static Integer numShards;

    private static String bigQueryDataset;

    private static String bigQueryTable;

    private static boolean gatherGcsPerformanceMetrics;

    private static final String FILEIOIT_NAMESPACE = TextIOIT.class.getName();

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Test
    public void writeThenReadAll() {
        TypedWrite<String, Object> write = TextIO.write().to(TextIOIT.filenamePrefix).withOutputFilenames().withCompression(TextIOIT.compressionType);
        if ((TextIOIT.numShards) != null) {
            write = write.withNumShards(TextIOIT.numShards);
        }
        PCollection<String> testFilenames = pipeline.apply("Generate sequence", GenerateSequence.from(0).to(TextIOIT.numberOfTextLines)).apply("Produce text lines", ParDo.of(new FileBasedIOITHelper.DeterministicallyConstructTestTextLineFn())).apply("Collect write start time", ParDo.of(new org.apache.beam.sdk.testutils.metrics.TimeMonitor(TextIOIT.FILEIOIT_NAMESPACE, "startTime"))).apply("Write content to files", write).getPerDestinationOutputFilenames().apply(Values.create()).apply("Collect write end time", ParDo.of(new org.apache.beam.sdk.testutils.metrics.TimeMonitor(TextIOIT.FILEIOIT_NAMESPACE, "middleTime")));
        PCollection<String> consolidatedHashcode = testFilenames.apply("Read all files", TextIO.readAll().withCompression(Compression.AUTO)).apply("Collect read end time", ParDo.of(new org.apache.beam.sdk.testutils.metrics.TimeMonitor(TextIOIT.FILEIOIT_NAMESPACE, "endTime"))).apply("Calculate hashcode", Combine.globally(new HashingFn()));
        String expectedHash = FileBasedIOITHelper.getExpectedHashForLineCount(TextIOIT.numberOfTextLines);
        PAssert.thatSingleton(consolidatedHashcode).isEqualTo(expectedHash);
        testFilenames.apply("Delete test files", ParDo.of(new FileBasedIOITHelper.DeleteFileFn()).withSideInputs(consolidatedHashcode.apply(View.asSingleton())));
        PipelineResult result = pipeline.run();
        result.waitUntilFinish();
        gatherAndPublishMetrics(result);
    }
}

