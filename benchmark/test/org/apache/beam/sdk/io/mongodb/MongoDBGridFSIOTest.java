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
package org.apache.beam.sdk.io.mongodb;


import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.mongodb.MongoDbGridFSIO.Read.BoundedGridFSSource;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.SourceTestUtils;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.Max;
import org.apache.beam.sdk.values.PCollection;
import org.bson.types.ObjectId;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test on the MongoDbGridFSIO.
 */
public class MongoDBGridFSIOTest {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBGridFSIOTest.class);

    @ClassRule
    public static final TemporaryFolder MONGODB_LOCATION = new TemporaryFolder();

    private static final String DATABASE = "gridfs";

    private static final MongodStarter mongodStarter = MongodStarter.getDefaultInstance();

    private static MongodExecutable mongodExecutable;

    private static MongodProcess mongodProcess;

    private static int port;

    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testFullRead() {
        PCollection<String> output = pipeline.apply(MongoDbGridFSIO.read().withUri(("mongodb://localhost:" + (MongoDBGridFSIOTest.port))).withDatabase(MongoDBGridFSIOTest.DATABASE));
        PAssert.thatSingleton(output.apply("Count All", Count.globally())).isEqualTo(5000L);
        PAssert.that(output.apply("Count PerElement", Count.perElement())).satisfies(( input) -> {
            for (KV<String, Long> element : input) {
                assertEquals(500L, element.getValue().longValue());
            }
            return null;
        });
        pipeline.run();
    }

    @Test
    public void testReadWithParser() {
        PCollection<org.apache.beam.sdk.values.KV<String, Integer>> output = pipeline.apply(MongoDbGridFSIO.read().withUri(("mongodb://localhost:" + (MongoDBGridFSIOTest.port))).withDatabase(MongoDBGridFSIOTest.DATABASE).withBucket("mapBucket").<org.apache.beam.sdk.values.KV<String, Integer>>withParser(( input, callback) -> {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(input.getInputStream(), StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                while (line != null) {
                    try (Scanner scanner = new Scanner(line.trim())) {
                        scanner.useDelimiter("\\t");
                        long timestamp = scanner.nextLong();
                        String name = scanner.next();
                        int score = scanner.nextInt();
                        callback.output(org.apache.beam.sdk.values.KV.of(name, score), new Instant(timestamp));
                    }
                    line = reader.readLine();
                } 
            }
        }).withSkew(new Duration(3610000L)).withCoder(KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of())));
        PAssert.thatSingleton(output.apply("Count All", Count.globally())).isEqualTo(50100L);
        PAssert.that(output.apply("Max PerElement", Max.integersPerKey())).satisfies(( input) -> {
            for (KV<String, Integer> element : input) {
                assertEquals(101, element.getValue().longValue());
            }
            return null;
        });
        pipeline.run();
    }

    @Test
    public void testSplit() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        MongoDbGridFSIO.Read<String> read = MongoDbGridFSIO.read().withUri(("mongodb://localhost:" + (MongoDBGridFSIOTest.port))).withDatabase(MongoDBGridFSIOTest.DATABASE);
        BoundedGridFSSource src = new BoundedGridFSSource(read, null);
        // make sure 2 files can fit in
        long desiredBundleSizeBytes = (((src.getEstimatedSizeBytes(options)) * 2L) / 5L) + 1000;
        List<? extends BoundedSource<ObjectId>> splits = src.split(desiredBundleSizeBytes, options);
        int expectedNbSplits = 3;
        Assert.assertEquals(expectedNbSplits, splits.size());
        SourceTestUtils.assertSourcesEqualReferenceSource(src, splits, options);
        int nonEmptySplits = 0;
        int count = 0;
        for (BoundedSource<ObjectId> subSource : splits) {
            List<ObjectId> result = SourceTestUtils.readFromSource(subSource, options);
            if ((result.size()) > 0) {
                nonEmptySplits += 1;
            }
            count += result.size();
        }
        Assert.assertEquals(expectedNbSplits, nonEmptySplits);
        Assert.assertEquals(5, count);
    }

    @Test
    public void testWriteMessage() throws Exception {
        ArrayList<String> data = new ArrayList<>(100);
        ArrayList<Integer> intData = new ArrayList<>(100);
        for (int i = 0; i < 1000; i++) {
            data.add(("Message " + i));
        }
        for (int i = 0; i < 100; i++) {
            intData.add(i);
        }
        pipeline.apply("String", Create.of(data)).apply("StringInternal", MongoDbGridFSIO.write().withUri(("mongodb://localhost:" + (MongoDBGridFSIOTest.port))).withDatabase(MongoDBGridFSIOTest.DATABASE).withChunkSize(100L).withBucket("WriteTest").withFilename("WriteTestData"));
        pipeline.apply("WithWriteFn", Create.of(intData)).apply("WithWriteFnInternal", MongoDbGridFSIO.<Integer>write(( output, outStream) -> {
            // one byte per output
            outStream.write(output.byteValue());
        }).withUri(("mongodb://localhost:" + (MongoDBGridFSIOTest.port))).withDatabase(MongoDBGridFSIOTest.DATABASE).withBucket("WriteTest").withFilename("WriteTestIntData"));
        pipeline.run();
        Mongo client = null;
        try {
            StringBuilder results = new StringBuilder();
            client = new Mongo("localhost", MongoDBGridFSIOTest.port);
            DB database = client.getDB(MongoDBGridFSIOTest.DATABASE);
            GridFS gridfs = new GridFS(database, "WriteTest");
            List<GridFSDBFile> files = gridfs.find("WriteTestData");
            Assert.assertTrue(((files.size()) > 0));
            for (GridFSDBFile file : files) {
                Assert.assertEquals(100, file.getChunkSize());
                int l = ((int) (file.getLength()));
                try (InputStream ins = file.getInputStream()) {
                    DataInputStream dis = new DataInputStream(ins);
                    byte[] b = new byte[l];
                    dis.readFully(b);
                    results.append(new String(b, StandardCharsets.UTF_8));
                }
            }
            String dataString = results.toString();
            for (int x = 0; x < 1000; x++) {
                Assert.assertTrue(dataString.contains(("Message " + x)));
            }
            files = gridfs.find("WriteTestIntData");
            boolean[] intResults = new boolean[100];
            for (GridFSDBFile file : files) {
                int l = ((int) (file.getLength()));
                try (InputStream ins = file.getInputStream()) {
                    DataInputStream dis = new DataInputStream(ins);
                    byte[] b = new byte[l];
                    dis.readFully(b);
                    for (byte aB : b) {
                        intResults[aB] = true;
                    }
                }
            }
            for (int x = 0; x < 100; x++) {
                Assert.assertTrue(("Did not get a result for " + x), intResults[x]);
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}

