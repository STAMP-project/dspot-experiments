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
package org.apache.beam.sdk.io.kudu;


import org.apache.beam.sdk.io.common.IOTestPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduTable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A test of {@link org.apache.beam.sdk.io.kudu.KuduIO} on an independent Kudu instance.
 *
 * <p>This test requires a running instance of Kudu. Pass in connection information using
 * PipelineOptions:
 *
 * <pre>
 *  ./gradlew integrationTest -p sdks/java/io/kudu -DintegrationTestPipelineOptions='[
 *    "--kuduMasterAddresses=127.0.0.1",
 *    "--kuduTable=beam-integration-test",
 *    "--numberOfRecords=100000" ]'
 *    --tests org.apache.beam.sdk.io.kudu.KuduIOIT
 *    -DintegrationTestRunner=direct
 * </pre>
 *
 * <p>To start a Kudu server in docker you can use the following:
 *
 * <pre>
 *   docker pull usuresearch/apache-kudu docker run -d --rm --name apache-kudu -p 7051:7051 \
 *     -p 7050:7050 -p 8051:8051 -p 8050:8050 usuresearch/apache-kudu ```
 * </pre>
 *
 * <p>See <a href="https://hub.docker.com/r/usuresearch/apache-kudu/">for information about this
 * image</a>.
 *
 * <p>Once running you may need to visit <a href="http://localhost:8051/masters">the masters
 * list</a> and copy the host (e.g. <code>host: "e94929167e2a"</code>) adding it to your <code>
 * etc/hosts</code> file pointing to localhost e.g.:
 *
 * <pre>
 *   127.0.0.1 localhost e94929167e2a
 * </pre>
 */
public class KuduIOIT {
    private static final Logger LOG = LoggerFactory.getLogger(KuduIOIT.class);

    /**
     * KuduIOIT options.
     */
    public interface KuduPipelineOptions extends IOTestPipelineOptions {
        @Description("Kudu master addresses (comma separated address list)")
        @Default.String("127.0.0.1:7051")
        String getKuduMasterAddresses();

        void setKuduMasterAddresses(String masterAddresses);

        @Description("Kudu table")
        @Default.String("beam-integration-test")
        String getKuduTable();

        void setKuduTable(String name);
    }

    private static KuduIOIT.KuduPipelineOptions options;

    private static KuduClient client;

    private static KuduTable kuduTable;

    @Rule
    public final TestPipeline writePipeline = TestPipeline.create();

    @Rule
    public TestPipeline readPipeline = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testWriteThenRead() throws Exception {
        runWrite();
        runReadAll();
        readPipeline = TestPipeline.create();
        runReadProjectedColumns();
        readPipeline = TestPipeline.create();
        runReadWithPredicates();
    }
}

