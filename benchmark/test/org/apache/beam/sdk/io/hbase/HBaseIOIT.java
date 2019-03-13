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
package org.apache.beam.sdk.io.hbase;


import java.nio.charset.StandardCharsets;
import org.apache.beam.sdk.io.common.IOTestPipelineOptions;
import org.apache.beam.sdk.io.common.TestRow;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test of {@link org.apache.beam.sdk.io.hbase.HBaseIOIT} on an independent HBase instance.
 *
 * <p>This test requires a running instance of HBase. Pass in connection information using
 * PipelineOptions:
 *
 * <pre>
 *
 *  ./gradlew clean integrationTest -p sdks/java/io/hbase/ -DintegrationTestPipelineOptions='[
 *  "--hbaseServerName=1.2.3.4"]' -DintegrationTestRunner=direct
 *  --tests org.apache.beam.sdk.io.hbase.HBaseIOIT
 *
 * </pre>
 */
@RunWith(JUnit4.class)
public class HBaseIOIT {
    /**
     * HBaseIOIT options.
     */
    public interface HBasePipelineOptions extends IOTestPipelineOptions {
        @Description("HBase host")
        @Default.String("HBase-host")
        String getHbaseServerName();

        void setHbaseServerName(String host);
    }

    private static int numberOfRows;

    private static final Configuration conf = HBaseConfiguration.create();

    private static final String TABLE_NAME = "IOTesting";

    private static final byte[] COLUMN_FAMILY = Bytes.toBytes("TestData");

    private static final byte[] COLUMN_HASH = Bytes.toBytes("hash");

    private static Admin admin;

    private static HBaseIOIT.HBasePipelineOptions options;

    @Rule
    public TestPipeline pipelineWrite = TestPipeline.create();

    @Rule
    public TestPipeline pipelineRead = TestPipeline.create();

    /**
     * Tests writing then reading data for a HBase database.
     */
    @Test
    public void testWriteThenRead() {
        runWrite();
        runRead();
    }

    /**
     * Produces test rows.
     */
    private static class ConstructMutations extends DoFn<TestRow, Mutation> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(new org.apache.hadoop.hbase.client.Put(c.element().id().toString().getBytes(StandardCharsets.UTF_8)).addColumn(HBaseIOIT.COLUMN_FAMILY, HBaseIOIT.COLUMN_HASH, Bytes.toBytes(c.element().name())));
        }
    }

    /**
     * Read rows from Table.
     */
    private static class SelectNameFn extends DoFn<Result, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(new String(c.element().getValue(HBaseIOIT.COLUMN_FAMILY, HBaseIOIT.COLUMN_HASH), StandardCharsets.UTF_8));
        }
    }
}

