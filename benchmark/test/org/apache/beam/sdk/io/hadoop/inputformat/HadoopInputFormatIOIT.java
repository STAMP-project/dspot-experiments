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
package org.apache.beam.sdk.io.hadoop.inputformat;


import JdbcIO.DataSourceConfiguration;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.common.HashingFn;
import org.apache.beam.sdk.io.common.TestRow;
import org.apache.beam.sdk.io.hadoop.SerializableConfiguration;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Reshuffle;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.io.LongWritable;
import org.junit.Rule;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;


/**
 * A test of {@link org.apache.beam.sdk.io.hadoop.inputformat.HadoopInputFormatIO} on an independent
 * postgres instance.
 *
 * <p>This test requires a running instance of Postgres. Pass in connection information using
 * PipelineOptions:
 *
 * <pre>
 *  ./gradlew integrationTest -p sdks/java/io/hadoop-input-format/
 *   -DintegrationTestPipelineOptions='[
 *     "--postgresServerName=1.2.3.4",
 *     "--postgresUsername=postgres",
 *     "--postgresDatabaseName=myfancydb",
 *     "--postgresPassword=mypass",
 *     "--postgresSsl=false",
 *     "--numberOfRecords=1000" ]'
 *  --tests org.apache.beam.sdk.io.hadoop.inputformat.HadoopInputFormatIOIT
 *  -DintegrationTestRunner=direct
 * </pre>
 *
 * <p>Please see 'build_rules.gradle' file for instructions regarding running this test using Beam
 * performance testing framework.
 */
public class HadoopInputFormatIOIT {
    private static PGSimpleDataSource dataSource;

    private static Integer numberOfRows;

    private static String tableName;

    private static SerializableConfiguration hadoopConfiguration;

    @Rule
    public TestPipeline writePipeline = TestPipeline.create();

    @Rule
    public TestPipeline readPipeline = TestPipeline.create();

    @Test
    public void readUsingHadoopInputFormat() {
        writePipeline.apply("Generate sequence", GenerateSequence.from(0).to(HadoopInputFormatIOIT.numberOfRows)).apply("Produce db rows", ParDo.of(new TestRow.DeterministicallyConstructTestRowFn())).apply("Prevent fusion before writing", Reshuffle.viaRandomKey()).apply("Write using JDBCIO", JdbcIO.<TestRow>write().withDataSourceConfiguration(DataSourceConfiguration.create(HadoopInputFormatIOIT.dataSource)).withStatement(String.format("insert into %s values(?, ?)", HadoopInputFormatIOIT.tableName)).withPreparedStatementSetter(new TestRowDBWritable.PrepareStatementFromTestRow()));
        writePipeline.run().waitUntilFinish();
        PCollection<String> consolidatedHashcode = readPipeline.apply("Read using HadoopInputFormat", HadoopInputFormatIO.<LongWritable, TestRowDBWritable>read().withConfiguration(HadoopInputFormatIOIT.hadoopConfiguration.get())).apply("Get values only", Values.create()).apply("Values as string", ParDo.of(new TestRow.SelectNameFn())).apply("Calculate hashcode", Combine.globally(new HashingFn()));
        PAssert.thatSingleton(consolidatedHashcode).isEqualTo(TestRow.getExpectedHashForRowCount(HadoopInputFormatIOIT.numberOfRows));
        readPipeline.run().waitUntilFinish();
    }
}

