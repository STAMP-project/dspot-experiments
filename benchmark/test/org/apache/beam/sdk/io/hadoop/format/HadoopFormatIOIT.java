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


import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.common.HashingFn;
import org.apache.beam.sdk.io.common.TestRow;
import org.apache.beam.sdk.io.hadoop.SerializableConfiguration;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.postgresql.ds.PGSimpleDataSource;


/**
 * A test of {@link org.apache.beam.sdk.io.hadoop.format.HadoopFormatIO} on an independent postgres
 * instance.
 *
 * <p>This test requires a running instance of Postgres. Pass in connection information using
 * PipelineOptions:
 *
 * <pre>
 *  ./gradlew integrationTest -p sdks/java/io/hadoop-format/
 *   -DintegrationTestPipelineOptions='[
 *     "--postgresServerName=1.2.3.4",
 *     "--postgresUsername=postgres",
 *     "--postgresDatabaseName=myfancydb",
 *     "--postgresPassword=mypass",
 *     "--postgresSsl=false",
 *     "--numberOfRecords=1000" ]'
 *  --tests org.apache.beam.sdk.io.hadoop.format.HadoopFormatIOIT
 *  -DintegrationTestRunner=direct
 * </pre>
 *
 * <p>Please see 'build_rules.gradle' file for instructions regarding running this test using Beam
 * performance testing framework.
 */
public class HadoopFormatIOIT {
    private static PGSimpleDataSource dataSource;

    private static Integer numberOfRows;

    private static String tableName;

    private static SerializableConfiguration hadoopConfiguration;

    @Rule
    public TestPipeline writePipeline = TestPipeline.create();

    @Rule
    public TestPipeline readPipeline = TestPipeline.create();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void writeAndReadUsingHadoopFormat() {
        writePipeline.apply("Generate sequence", GenerateSequence.from(0).to(HadoopFormatIOIT.numberOfRows)).apply("Produce db rows", ParDo.of(new TestRow.DeterministicallyConstructTestRowFn())).apply("Construct rows for DBOutputFormat", ParDo.of(new HadoopFormatIOIT.ConstructDBOutputFormatRowFn())).apply("Write using Hadoop OutputFormat", HadoopFormatIO.<TestRowDBWritable, NullWritable>write().withConfiguration(HadoopFormatIOIT.hadoopConfiguration.get()).withPartitioning().withExternalSynchronization(new HDFSSynchronization(tmpFolder.getRoot().getAbsolutePath())));
        writePipeline.run().waitUntilFinish();
        PCollection<String> consolidatedHashcode = readPipeline.apply("Read using Hadoop InputFormat", HadoopFormatIO.<LongWritable, TestRowDBWritable>read().withConfiguration(HadoopFormatIOIT.hadoopConfiguration.get())).apply("Get values only", Values.create()).apply("Values as string", ParDo.of(new TestRow.SelectNameFn())).apply("Calculate hashcode", Combine.globally(new HashingFn()));
        PAssert.thatSingleton(consolidatedHashcode).isEqualTo(getExpectedHashForRowCount(HadoopFormatIOIT.numberOfRows));
        readPipeline.run().waitUntilFinish();
    }

    /**
     * Uses the input {@link TestRow} values as seeds to produce new {@link KV}s for {@link HadoopFormatIO}.
     */
    public static class ConstructDBOutputFormatRowFn extends DoFn<TestRow, KV<TestRowDBWritable, NullWritable>> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(KV.of(new TestRowDBWritable(c.element().id(), c.element().name()), NullWritable.get()));
        }
    }
}

