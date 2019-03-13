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
package org.apache.beam.sdk.io.cassandra;


import Default.Integer;
import Validation.Required;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import java.io.Serializable;
import java.util.List;
import org.apache.beam.sdk.io.common.IOTestPipelineOptions;
import org.apache.beam.sdk.io.common.TestRow;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A test of {@link CassandraIO} on a concrete and independent Cassandra instance.
 *
 * <p>This test requires a running Cassandra instance at [localhost:9042], and the test dataset must exists.
 *
 * <p>You can run this test directly using gradle with:
 *
 * <pre>{@code
 * ./gradlew integrationTest -p sdks/java/io/cassandra -DintegrationTestPipelineOptions='["--cassandraHost=127.0.0.1","--cassandraPort=9042","--numberOfRecords=1000"]' --tests org.apache.beam.sdk.io.cassandra.CassandraIOIT -DintegrationTestRunner=direct
 * </pre>
 */
@RunWith(JUnit4.class)
public class CassandraIOIT implements Serializable {
    /**
     * CassandraIOIT options.
     */
    public interface CassandraIOITOptions extends IOTestPipelineOptions {
        @Description("Host for Cassandra server (host name/ip address)")
        @Validation.Required
        List<String> getCassandraHost();

        void setCassandraHost(List<String> host);

        @Description("Port for Cassandra server")
        @Default.Integer(9042)
        Integer getCassandraPort();

        void setCassandraPort(Integer port);
    }

    private static final Logger LOG = LoggerFactory.getLogger(CassandraIOIT.class);

    private static CassandraIOIT.CassandraIOITOptions options;

    private static final String KEYSPACE = "BEAM";

    private static final String TABLE = "BEAM_TEST";

    @Rule
    public transient TestPipeline pipelineWrite = TestPipeline.create();

    @Rule
    public transient TestPipeline pipelineRead = TestPipeline.create();

    @Test
    public void testWriteThenRead() {
        runWrite();
        runRead();
    }

    @Test
    public void testWriteThenReadWithWhere() {
        runWrite();
        runReadWithWhere();
    }

    /**
     * Simple Cassandra entity representing a scientist. Used for read test.
     */
    @Table(name = CassandraIOIT.TABLE, keyspace = CassandraIOIT.KEYSPACE)
    private static final class Scientist implements Serializable {
        @PartitionKey
        @Column(name = "id")
        final long id;

        @Column(name = "name")
        final String name;

        Scientist() {
            // Empty constructor needed for deserialization from Cassandra
            this(0, null);
        }

        Scientist(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return ((id) + ": ") + (name);
        }
    }

    private static class CreateScientistFn extends DoFn<TestRow, CassandraIOIT.Scientist> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(new CassandraIOIT.Scientist(c.element().id(), c.element().name()));
        }
    }

    private static class SelectNameFn extends DoFn<CassandraIOIT.Scientist, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(c.element().name);
        }
    }
}

