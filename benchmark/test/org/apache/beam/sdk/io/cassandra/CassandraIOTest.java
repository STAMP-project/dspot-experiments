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


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import junit.framework.TestCase;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.cassandra.CassandraIO.CassandraSource.TokenRange;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Objects;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CassandraSource.distance;
import static CassandraSource.getEstimatedSizeBytesFromTokenRanges;
import static CassandraSource.getRingFraction;
import static CassandraSource.isMurmur3Partitioner;


/**
 * Tests of {@link CassandraIO}.
 */
@RunWith(JUnit4.class)
public class CassandraIOTest implements Serializable {
    private static final long NUM_ROWS = 20L;

    private static final String CASSANDRA_KEYSPACE = "beam_ks";

    private static final String CASSANDRA_HOST = "127.0.0.1";

    private static final int CASSANDRA_PORT = 9142;

    private static final String CASSANDRA_USERNAME = "cassandra";

    private static final String CASSANDRA_ENCRYPTED_PASSWORD = "Y2Fzc2FuZHJh";// Base64 encoded version of "cassandra"


    private static final String CASSANDRA_TABLE = "scientist";

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraIOTest.class);

    private static final String STORAGE_SERVICE_MBEAN = "org.apache.cassandra.db:type=StorageService";

    private static final String JMX_PORT = "7199";

    private static final long SIZE_ESTIMATES_UPDATE_INTERVAL = 5000L;

    private static final long STARTUP_TIMEOUT = 45000L;

    private static Cluster cluster;

    private static Session session;

    private static long startupTime;

    @Rule
    public transient TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testEstimatedSizeBytes() throws Exception {
        CassandraIOTest.insertRecords();
        PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
        CassandraIO.Read<CassandraIOTest.Scientist> read = CassandraIO.<CassandraIOTest.Scientist>read().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withTable(CassandraIOTest.CASSANDRA_TABLE);
        CassandraIO.CassandraSource<CassandraIOTest.Scientist> source = new CassandraIO.CassandraSource<>(read, null);
        long estimatedSizeBytes = source.getEstimatedSizeBytes(pipelineOptions);
        // the size is non determanistic in Cassandra backend
        TestCase.assertTrue(((estimatedSizeBytes >= (4608L * 0.9F)) && (estimatedSizeBytes <= (4608L * 1.1F))));
    }

    @Test
    public void testRead() throws Exception {
        CassandraIOTest.insertRecords();
        PCollection<CassandraIOTest.Scientist> output = pipeline.apply(CassandraIO.<CassandraIOTest.Scientist>read().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withTable(CassandraIOTest.CASSANDRA_TABLE).withCoder(SerializableCoder.of(CassandraIOTest.Scientist.class)).withEntity(CassandraIOTest.Scientist.class));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(CassandraIOTest.NUM_ROWS);
        PCollection<KV<String, Integer>> mapped = output.apply(MapElements.via(new org.apache.beam.sdk.transforms.SimpleFunction<CassandraIOTest.Scientist, KV<String, Integer>>() {
            @Override
            public KV<String, Integer> apply(CassandraIOTest.Scientist scientist) {
                return KV.of(scientist.name, scientist.id);
            }
        }));
        PAssert.that(mapped.apply("Count occurrences per scientist", Count.perKey())).satisfies(( input) -> {
            for (KV<String, Long> element : input) {
                assertEquals(element.getKey(), ((NUM_ROWS) / 10), element.getValue().longValue());
            }
            return null;
        });
        pipeline.run();
    }

    @Test
    public void testReadWithWhere() throws Exception {
        CassandraIOTest.insertRecords();
        PCollection<CassandraIOTest.Scientist> output = pipeline.apply(CassandraIO.<CassandraIOTest.Scientist>read().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withTable(CassandraIOTest.CASSANDRA_TABLE).withCoder(SerializableCoder.of(CassandraIOTest.Scientist.class)).withEntity(CassandraIOTest.Scientist.class).withWhere("person_id=10"));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(1L);
        pipeline.run();
    }

    @Test
    public void testWrite() {
        ArrayList<CassandraIOTest.Scientist> scientists = buildScientists(CassandraIOTest.NUM_ROWS);
        pipeline.apply(org.apache.beam.sdk.transforms.Create.of(scientists)).apply(CassandraIO.<CassandraIOTest.Scientist>write().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withEntity(CassandraIOTest.Scientist.class));
        // table to write to is specified in the entity in @Table annotation (in that cas person)
        pipeline.run();
        List<Row> results = getRows();
        Assert.assertEquals(CassandraIOTest.NUM_ROWS, results.size());
        for (Row row : results) {
            TestCase.assertTrue(row.getString("person_name").matches("Name (\\d*)"));
        }
    }

    @Test
    public void testReadWithPasswordDecryption() throws Exception {
        CassandraIOTest.insertRecords();
        String sessionReadUID = "session-read-" + (UUID.randomUUID());
        PasswordDecrypter readPwdDecrypter = new TestPasswordDecrypter(sessionReadUID);
        PCollection<CassandraIOTest.Scientist> output = pipeline.apply(CassandraIO.<CassandraIOTest.Scientist>read().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withUsername(CassandraIOTest.CASSANDRA_USERNAME).withEncryptedPassword(CassandraIOTest.CASSANDRA_ENCRYPTED_PASSWORD).withPasswordDecrypter(readPwdDecrypter).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withTable(CassandraIOTest.CASSANDRA_TABLE).withCoder(SerializableCoder.of(CassandraIOTest.Scientist.class)).withEntity(CassandraIOTest.Scientist.class));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(CassandraIOTest.NUM_ROWS);
        PCollection<KV<String, Integer>> mapped = output.apply(MapElements.via(new org.apache.beam.sdk.transforms.SimpleFunction<CassandraIOTest.Scientist, KV<String, Integer>>() {
            @Override
            public KV<String, Integer> apply(CassandraIOTest.Scientist scientist) {
                return KV.of(scientist.name, scientist.id);
            }
        }));
        PAssert.that(mapped.apply("Count occurrences per scientist", Count.perKey())).satisfies(( input) -> {
            for (KV<String, Long> element : input) {
                assertEquals(element.getKey(), ((NUM_ROWS) / 10), element.getValue().longValue());
            }
            return null;
        });
        pipeline.run();
        TestCase.assertTrue((1L <= (TestPasswordDecrypter.getNbCallsBySession(sessionReadUID))));
    }

    @Test
    public void testWriteWithPasswordDecryption() {
        ArrayList<CassandraIOTest.Scientist> scientists = buildScientists(CassandraIOTest.NUM_ROWS);
        String sessionWriteUID = "session-write-" + (UUID.randomUUID());
        PasswordDecrypter writePwdDecrypter = new TestPasswordDecrypter(sessionWriteUID);
        pipeline.apply(org.apache.beam.sdk.transforms.Create.of(scientists)).apply(CassandraIO.<CassandraIOTest.Scientist>write().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withUsername(CassandraIOTest.CASSANDRA_USERNAME).withEncryptedPassword(CassandraIOTest.CASSANDRA_ENCRYPTED_PASSWORD).withPasswordDecrypter(writePwdDecrypter).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withEntity(CassandraIOTest.Scientist.class));
        pipeline.run();
        List<Row> results = getRows();
        Assert.assertEquals(CassandraIOTest.NUM_ROWS, results.size());
        for (Row row : results) {
            TestCase.assertTrue(row.getString("person_name").matches("Name (\\d*)"));
        }
        TestCase.assertTrue((1L <= (TestPasswordDecrypter.getNbCallsBySession(sessionWriteUID))));
    }

    @Test
    public void testSplit() throws Exception {
        CassandraIOTest.insertRecords();
        PipelineOptions options = PipelineOptionsFactory.create();
        CassandraIO.Read<CassandraIOTest.Scientist> read = CassandraIO.<CassandraIOTest.Scientist>read().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withTable(CassandraIOTest.CASSANDRA_TABLE).withEntity(CassandraIOTest.Scientist.class).withCoder(SerializableCoder.of(CassandraIOTest.Scientist.class));
        // initialSource will be read without splitting (which does not happen in production)
        // so we need to provide splitQueries to avoid NPE in source.reader.start()
        String splitQuery = QueryBuilder.select().from(CassandraIOTest.CASSANDRA_KEYSPACE, CassandraIOTest.CASSANDRA_TABLE).toString();
        CassandraIO.CassandraSource<CassandraIOTest.Scientist> initialSource = new CassandraIO.CassandraSource<>(read, Collections.singletonList(splitQuery));
        int desiredBundleSizeBytes = 2000;
        List<BoundedSource<CassandraIOTest.Scientist>> splits = initialSource.split(desiredBundleSizeBytes, options);
        SourceTestUtils.assertSourcesEqualReferenceSource(initialSource, splits, options);
        int expectedNumSplits = ((int) (initialSource.getEstimatedSizeBytes(options))) / desiredBundleSizeBytes;
        Assert.assertEquals(expectedNumSplits, splits.size());
        int nonEmptySplits = 0;
        for (BoundedSource<CassandraIOTest.Scientist> subSource : splits) {
            if ((readFromSource(subSource, options).size()) > 0) {
                nonEmptySplits += 1;
            }
        }
        Assert.assertEquals("Wrong number of empty splits", expectedNumSplits, nonEmptySplits);
    }

    @Test
    public void testDelete() throws Exception {
        CassandraIOTest.insertRecords();
        List<Row> results = getRows();
        Assert.assertEquals(CassandraIOTest.NUM_ROWS, results.size());
        CassandraIOTest.Scientist einstein = new CassandraIOTest.Scientist();
        einstein.id = 0;
        einstein.name = "Einstein";
        pipeline.apply(org.apache.beam.sdk.transforms.Create.of(einstein)).apply(CassandraIO.<CassandraIOTest.Scientist>delete().withHosts(Arrays.asList(CassandraIOTest.CASSANDRA_HOST)).withPort(CassandraIOTest.CASSANDRA_PORT).withKeyspace(CassandraIOTest.CASSANDRA_KEYSPACE).withEntity(CassandraIOTest.Scientist.class));
        pipeline.run();
        results = getRows();
        Assert.assertEquals(((CassandraIOTest.NUM_ROWS) - 1), results.size());
    }

    @Test
    public void testValidPartitioner() {
        Assert.assertTrue(isMurmur3Partitioner(CassandraIOTest.cluster));
    }

    @Test
    public void testDistance() {
        BigInteger distance = distance(new BigInteger("10"), new BigInteger("100"));
        Assert.assertEquals(BigInteger.valueOf(90), distance);
        distance = distance(new BigInteger("100"), new BigInteger("10"));
        Assert.assertEquals(new BigInteger("18446744073709551526"), distance);
    }

    @Test
    public void testRingFraction() {
        // simulate a first range taking "half" of the available tokens
        List<TokenRange> tokenRanges = new ArrayList<>();
        tokenRanges.add(new TokenRange(1, 1, BigInteger.valueOf(Long.MIN_VALUE), new BigInteger("0")));
        Assert.assertEquals(0.5, getRingFraction(tokenRanges), 0);
        // add a second range to cover all tokens available
        tokenRanges.add(new TokenRange(1, 1, new BigInteger("0"), BigInteger.valueOf(Long.MAX_VALUE)));
        Assert.assertEquals(1.0, getRingFraction(tokenRanges), 0);
    }

    @Test
    public void testEstimatedSizeBytesFromTokenRanges() {
        List<TokenRange> tokenRanges = new ArrayList<>();
        // one partition containing all tokens, the size is actually the size of the partition
        tokenRanges.add(new TokenRange(1, 1000, BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE)));
        Assert.assertEquals(1000, getEstimatedSizeBytesFromTokenRanges(tokenRanges));
        // one partition with half of the tokens, we estimate the size to the double of this partition
        tokenRanges = new ArrayList();
        tokenRanges.add(new TokenRange(1, 1000, BigInteger.valueOf(Long.MIN_VALUE), new BigInteger("0")));
        Assert.assertEquals(2000, getEstimatedSizeBytesFromTokenRanges(tokenRanges));
        // we have three partitions covering all tokens, the size is the sum of partition size *
        // partition count
        tokenRanges = new ArrayList();
        tokenRanges.add(new TokenRange(1, 1000, BigInteger.valueOf(Long.MIN_VALUE), new BigInteger("-3")));
        tokenRanges.add(new TokenRange(1, 1000, new BigInteger("-2"), new BigInteger("10000")));
        tokenRanges.add(new TokenRange(2, 3000, new BigInteger("10001"), BigInteger.valueOf(Long.MAX_VALUE)));
        Assert.assertEquals(8000, getEstimatedSizeBytesFromTokenRanges(tokenRanges));
    }

    /**
     * Simple Cassandra entity used in test.
     */
    @Table(name = CassandraIOTest.CASSANDRA_TABLE, keyspace = CassandraIOTest.CASSANDRA_KEYSPACE)
    static class Scientist implements Serializable {
        @Column(name = "person_name")
        String name;

        @PartitionKey
        @Column(name = "person_id")
        int id;

        @Override
        public String toString() {
            return ((id) + ":") + (name);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CassandraIOTest.Scientist scientist = ((CassandraIOTest.Scientist) (o));
            return ((id) == (scientist.id)) && (Objects.equal(name, scientist.name));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, id);
        }
    }
}

