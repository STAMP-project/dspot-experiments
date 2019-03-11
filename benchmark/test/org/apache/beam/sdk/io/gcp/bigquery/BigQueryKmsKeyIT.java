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
package org.apache.beam.sdk.io.gcp.bigquery;


import Method.FILE_LOADS;
import Method.STREAMING_INSERTS;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import java.security.SecureRandom;
import org.apache.beam.sdk.io.gcp.testing.BigqueryClient;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration tests for BigQuery operations that can use KMS keys, for use with DirectRunner.
 *
 * <p>Verification of KMS key usage is done on outputs, but not on any temporary files or tables
 * used.
 */
@RunWith(JUnit4.class)
public class BigQueryKmsKeyIT {
    private static final Logger LOG = LoggerFactory.getLogger(BigQueryKmsKeyIT.class);

    private static final BigqueryClient BQ_CLIENT = new BigqueryClient("BigQueryKmsKeyIT");

    private static final String BIG_QUERY_DATASET_ID = (("bq_query_to_table_" + (System.currentTimeMillis())) + "_") + (new SecureRandom().nextInt(32));

    private static final TableSchema OUTPUT_SCHEMA = new TableSchema().setFields(ImmutableList.of(new TableFieldSchema().setName("fruit").setType("STRING")));

    private static TestPipelineOptions options;

    private static String project;

    private static String kmsKey = "projects/apache-beam-testing/locations/global/keyRings/beam-it/cryptoKeys/test";

    @Test
    public void testWithFileLoads() throws Exception {
        testQueryAndWrite(FILE_LOADS);
    }

    @Test
    public void testWithStreamingInserts() throws Exception {
        testQueryAndWrite(STREAMING_INSERTS);
    }
}

