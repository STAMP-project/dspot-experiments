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
package org.apache.beam.examples.cookbook;


import Method.DIRECT_READ;
import Method.EXPORT;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryOptions;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * An end-to-end test for {@link org.apache.beam.examples.cookbook.BigQueryTornadoes}.
 *
 * <p>This test reads the public samples of weather data from BigQuery, counts the number of
 * tornadoes that occur in each month, and writes the results to BigQuery. It requires
 * "BigQueryTornadoesIT" BigQuery dataset to be created before running.
 *
 * <p>Running instructions:
 *
 * <pre>
 *  ./gradlew integrationTest -p examples/java/ -DintegrationTestPipelineOptions='[
 *  "--tempLocation=gs://your-location/"]'
 *  --tests org.apache.beam.examples.cookbook.BigQueryTornadoesIT
 *  -DintegrationTestRunner=direct
 * </pre>
 *
 * <p>Check {@link org.apache.beam.examples.cookbook.BigQueryTornadoes} form more configuration
 * options via PipelineOptions.
 */
@RunWith(JUnit4.class)
public class BigQueryTornadoesIT {
    private static final String DEFAULT_OUTPUT_CHECKSUM = "1ab4c7ec460b94bbb3c3885b178bf0e6bed56e1f";

    /**
     * Options for the BigQueryTornadoes Integration Test.
     */
    public interface BigQueryTornadoesITOptions extends BigQueryTornadoes.Options , BigQueryOptions , TestPipelineOptions {}

    @Test
    public void testE2EBigQueryTornadoesWithExport() throws Exception {
        BigQueryTornadoesIT.BigQueryTornadoesITOptions options = TestPipeline.testingPipelineOptions().as(BigQueryTornadoesIT.BigQueryTornadoesITOptions.class);
        options.setReadMethod(EXPORT);
        setOutput(String.format("%s.%s", "BigQueryTornadoesIT", ("monthly_tornadoes_" + (System.currentTimeMillis()))));
        runE2EBigQueryTornadoesTest(options);
    }

    @Test
    public void testE2eBigQueryTornadoesWithStorageApi() throws Exception {
        BigQueryTornadoesIT.BigQueryTornadoesITOptions options = TestPipeline.testingPipelineOptions().as(BigQueryTornadoesIT.BigQueryTornadoesITOptions.class);
        options.setReadMethod(DIRECT_READ);
        setOutput(String.format("%s.%s", "BigQueryTornadoesIT", ("monthly_tornadoes_storage_" + (System.currentTimeMillis()))));
        runE2EBigQueryTornadoesTest(options);
    }
}

