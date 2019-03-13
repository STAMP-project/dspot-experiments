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
package org.apache.avro;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import org.apache.avro.file.Schema;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertTrue;


@RunWith(Parameterized.class)
public class TestDataFile {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataFile.class);

    @Rule
    public TemporaryFolder DIR = new TemporaryFolder();

    private final CodecFactory codec;

    public TestDataFile(CodecFactory codec) {
        this.codec = codec;
        TestDataFile.LOG.info(("Running with codec: " + codec));
    }

    private static final int COUNT = Integer.parseInt(System.getProperty("test.count", "200"));

    private static final boolean VALIDATE = !("false".equals(System.getProperty("test.validate", "true")));

    private static final long SEED = System.currentTimeMillis();

    private static final String SCHEMA_JSON = "{\"type\": \"record\", \"name\": \"Test\", \"fields\": [" + ("{\"name\":\"stringField\", \"type\":\"string\"}," + "{\"name\":\"longField\", \"type\":\"long\"}]}");

    private static final Schema SCHEMA = new Schema.Parser().parse(TestDataFile.SCHEMA_JSON);

    @Test
    public void runTestsInOrder() throws Exception {
        testGenericWrite();
        testGenericRead();
        testSplits();
        testSyncDiscovery();
        testGenericAppend();
        testReadWithHeader();
        testFSync(false);
        testFSync(true);
    }

    @Test
    public void testSyncInHeader() throws IOException {
        DataFileReader<Object> reader = new DataFileReader(new File("../../../share/test/data/syncInMeta.avro"), new org.apache.avro.generic.GenericDatumReader());
        reader.sync(0);
        for (Object datum : reader)
            Assert.assertNotNull(datum);

    }

    @Test
    public void test12() throws IOException {
        TestDataFile.readFile(new File("../../../share/test/data/test.avro12"), new org.apache.avro.generic.GenericDatumReader());
    }

    @Test
    public void testFlushCount() throws IOException {
        DataFileWriter<Object> writer = new DataFileWriter(new org.apache.avro.generic.GenericDatumWriter());
        writer.setFlushOnEveryBlock(false);
        TestDataFile.TestingByteArrayOutputStream out = new TestDataFile.TestingByteArrayOutputStream();
        writer.create(TestDataFile.SCHEMA, out);
        int currentCount = 0;
        int flushCounter = 0;
        try {
            for (Object datum : new org.apache.avro.util.RandomData(TestDataFile.SCHEMA, TestDataFile.COUNT, ((TestDataFile.SEED) + 1))) {
                currentCount++;
                writer.append(datum);
                writer.sync();
                if ((currentCount % 10) == 0) {
                    flushCounter++;
                    writer.flush();
                }
            }
        } finally {
            writer.close();
        }
        System.out.println(("Total number of flushes: " + (out.flushCount)));
        // Unfortunately, the underlying buffered output stream might flush data
        // to disk when the buffer becomes full, so the only check we can
        // accurately do is that each sync did not lead to a flush and that the
        // file was flushed at least as many times as we called flush. Generally
        // noticed that out.flushCount is almost always 24 though.
        assertTrue((((out.flushCount) < currentCount) && ((out.flushCount) >= flushCounter)));
    }

    private class TestingByteArrayOutputStream extends ByteArrayOutputStream implements Syncable {
        private int flushCount = 0;

        private int syncCount = 0;

        @Override
        public void flush() throws IOException {
            super.flush();
            (flushCount)++;
        }

        @Override
        public void sync() throws IOException {
            (syncCount)++;
        }
    }
}

