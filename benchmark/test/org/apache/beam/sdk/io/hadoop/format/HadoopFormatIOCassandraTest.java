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


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import java.io.Serializable;
import org.apache.beam.sdk.io.common.HashingFn;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.apache.hadoop.conf.Configuration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests to validate HadoopFormatIO for embedded Cassandra instance.
 */
@RunWith(JUnit4.class)
public class HadoopFormatIOCassandraTest implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String CASSANDRA_KEYSPACE = "beamdb";

    private static final String CASSANDRA_HOST = "127.0.0.1";

    private static final String CASSANDRA_TABLE = "scientists";

    private static final String CASSANDRA_NATIVE_PORT_PROPERTY = "cassandra.input.native.port";

    private static final String CASSANDRA_THRIFT_PORT_PROPERTY = "cassandra.input.thrift.port";

    private static final String CASSANDRA_THRIFT_ADDRESS_PROPERTY = "cassandra.input.thrift.address";

    private static final String CASSANDRA_PARTITIONER_CLASS_PROPERTY = "cassandra.input.partitioner.class";

    private static final String CASSANDRA_PARTITIONER_CLASS_VALUE = "Murmur3Partitioner";

    private static final String CASSANDRA_KEYSPACE_PROPERTY = "cassandra.input.keyspace";

    private static final String CASSANDRA_COLUMNFAMILY_PROPERTY = "cassandra.input.columnfamily";

    private static final String CASSANDRA_PORT = "9061";

    private static final String CASSANDRA_NATIVE_PORT = "9042";

    private static transient Cluster cluster;

    private static transient Session session;

    private static final long TEST_DATA_ROW_COUNT = 10L;

    private static final EmbeddedCassandraService cassandra = new EmbeddedCassandraService();

    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    /**
     * Test to read data from embedded Cassandra instance and verify whether data is read
     * successfully.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHIFReadForCassandra() {
        // Expected hashcode is evaluated during insertion time one time and hardcoded here.
        String expectedHashCode = "1b9780833cce000138b9afa25ba63486";
        Configuration conf = getConfiguration();
        PCollection<KV<Long, String>> cassandraData = p.apply(HadoopFormatIO.<Long, String>read().withConfiguration(conf).withValueTranslation(myValueTranslate));
        // Verify the count of data retrieved from Cassandra matches expected count.
        PAssert.thatSingleton(cassandraData.apply("Count", Count.globally())).isEqualTo(HadoopFormatIOCassandraTest.TEST_DATA_ROW_COUNT);
        PCollection<String> textValues = cassandraData.apply(Values.create());
        // Verify the output values using checksum comparison.
        PCollection<String> consolidatedHashcode = textValues.apply(Combine.globally(new HashingFn()).withoutDefaults());
        PAssert.that(consolidatedHashcode).containsInAnyOrder(expectedHashCode);
        p.run().waitUntilFinish();
    }

    private final SimpleFunction<Row, String> myValueTranslate = new SimpleFunction<Row, String>() {
        @Override
        public String apply(Row input) {
            return ((input.getInt("id")) + "|") + (input.getString("scientist"));
        }
    };

    /**
     * Test to read data from embedded Cassandra instance based on query and verify whether data is
     * read successfully.
     */
    @Test
    public void testHIFReadForCassandraQuery() {
        Long expectedCount = 1L;
        String expectedChecksum = "f11caabc7a9fc170e22b41218749166c";
        Configuration conf = getConfiguration();
        conf.set("cassandra.input.cql", (((("select * from " + (HadoopFormatIOCassandraTest.CASSANDRA_KEYSPACE)) + ".") + (HadoopFormatIOCassandraTest.CASSANDRA_TABLE)) + " where token(id) > ? and token(id) <= ? and scientist='Faraday1' allow filtering"));
        PCollection<KV<Long, String>> cassandraData = p.apply(HadoopFormatIO.<Long, String>read().withConfiguration(conf).withValueTranslation(myValueTranslate));
        // Verify the count of data retrieved from Cassandra matches expected count.
        PAssert.thatSingleton(cassandraData.apply("Count", Count.globally())).isEqualTo(expectedCount);
        PCollection<String> textValues = cassandraData.apply(Values.create());
        // Verify the output values using checksum comparison.
        PCollection<String> consolidatedHashcode = textValues.apply(Combine.globally(new HashingFn()).withoutDefaults());
        PAssert.that(consolidatedHashcode).containsInAnyOrder(expectedChecksum);
        p.run().waitUntilFinish();
    }

    /**
     * POJO class for scientist data.
     */
    @Table(name = HadoopFormatIOCassandraTest.CASSANDRA_TABLE, keyspace = HadoopFormatIOCassandraTest.CASSANDRA_KEYSPACE)
    public static class Scientist implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "scientist")
        private String name;

        @Column(name = "id")
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return ((id) + ":") + (name);
        }
    }
}

