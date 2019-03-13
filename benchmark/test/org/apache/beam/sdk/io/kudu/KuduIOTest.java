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


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kudu.client.KuduException;
import org.apache.kudu.client.RowResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import source.serializedToken;
import source.spec;

import static source.serializedToken;


/**
 * A test of {@link KuduIO} using fake Kudu services.
 *
 * <p>Since Kudu is written in C++ it does not currently lend itself to easy unit tests from a Java
 * environment. The Kudu project is actively working on a solution for this (see <a
 * href="https://issues.apache.org/jira/browse/KUDU-2411">KUDU-2411</a>) which will be used in the
 * future. In the meantime, only rudimentary tests exist here, with the preferred testing being
 * carried out in {@link KuduIOIT}.
 */
public class KuduIOTest {
    private static final Logger LOG = LoggerFactory.getLogger(KuduIOTest.class);

    @Rule
    public final TestPipeline writePipeline = TestPipeline.create();

    @Rule
    public final TestPipeline readPipeline = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final transient ExpectedLogs expectedWriteLogs = ExpectedLogs.none(KuduIOTest.FakeWriter.class);

    @Rule
    public final transient ExpectedLogs expectedReadLogs = ExpectedLogs.none(KuduIOTest.FakeReader.class);

    private KuduService<Integer> mockReadService;

    private KuduService<String> mockWriteService;

    private final int numberRecords = 10;

    private int targetParallelism = 3;// determined by the runner, but direct has min of 3


    /**
     * Tests the read path using a {@link FakeReader}. The {@link KuduService} is mocked to simulate 4
     * tablets and fake the encoding of a scanner for each tablet. The test verifies that the {@link KuduIO} correctly splits into 4 sources and instantiates a reader for each, and that the
     * correct number of records are read.
     */
    @Test
    public void testRead() throws KuduException {
        Mockito.when(mockReadService.createReader(ArgumentMatchers.any())).thenAnswer(new KuduIOTest.FakeReaderAnswer());
        // Simulate the equivalent of Kudu providing an encoded scanner per tablet. Here we encode
        // a range which the fake reader will use to simulate a single tablet read.
        List<byte[]> fakeScanners = Arrays.asList(ByteBuffer.allocate(8).putInt(0).putInt(25).array(), ByteBuffer.allocate(8).putInt(25).putInt(50).array(), ByteBuffer.allocate(8).putInt(50).putInt(75).array(), ByteBuffer.allocate(8).putInt(75).putInt(100).array());
        Mockito.when(mockReadService.createTabletScanners(ArgumentMatchers.any())).thenReturn(fakeScanners);
        PCollection<Integer> output = readPipeline.apply(// the fake reader only deals with a single int
        KuduIO.<Integer>read().withMasterAddresses("mock").withTable("Table").withParseFn(((SerializableFunction<RowResult, Integer>) (( input) -> input.getInt(COL_ID)))).withKuduService(mockReadService).withCoder(BigEndianIntegerCoder.of()));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(((long) (100)));
        readPipeline.run().waitUntilFinish();
        // check that the fake tablet ranges were read
        expectedReadLogs.verifyDebug(String.format(KuduIOTest.FakeReader.LOG_SET_RANGE, 0, 25));
        expectedReadLogs.verifyDebug(String.format(KuduIOTest.FakeReader.LOG_SET_RANGE, 25, 50));
        expectedReadLogs.verifyDebug(String.format(KuduIOTest.FakeReader.LOG_SET_RANGE, 50, 75));
        expectedReadLogs.verifyDebug(String.format(KuduIOTest.FakeReader.LOG_SET_RANGE, 75, 100));
    }

    /**
     * A fake writer which logs operations using a unique id for the writer instance. The initial
     * writer is created with and id of 0 and each deserialized instance will receive a unique integer
     * id.
     *
     * <p>This writer allows tests to verify that sessions are opened and closed and the entities are
     * passed to the write operation. However, the {@code formatFn} is ignored as the mocking required
     * to replicate the {@link Operation} would render it a meaningless check.
     */
    private static class FakeWriter implements KuduService.Writer<Long> {
        private static final Logger LOG = LoggerFactory.getLogger(KuduIOTest.FakeWriter.class);

        static final String LOG_OPEN_SESSION = "FakeWriter[%d] openSession";

        static final String LOG_WRITE = "FakeWriter[%d] write";

        static final String LOG_WRITE_VALUE = "FakeWriter value[%d]";

        static final String LOG_CLOSE_SESSION = "FakeWriter[%d] closeSession";

        // share a counter across instances to uniquely identify the writers
        private static final AtomicInteger counter = new AtomicInteger(0);

        private transient int id = 0;// set on deserialization


        @Override
        public void openSession() {
            KuduIOTest.FakeWriter.LOG.debug(String.format(KuduIOTest.FakeWriter.LOG_OPEN_SESSION, id));
        }

        @Override
        public void write(Long entity) {
            KuduIOTest.FakeWriter.LOG.debug(String.format(KuduIOTest.FakeWriter.LOG_WRITE, entity));
            KuduIOTest.FakeWriter.LOG.debug(String.format(KuduIOTest.FakeWriter.LOG_WRITE_VALUE, entity));
        }

        @Override
        public void closeSession() {
            KuduIOTest.FakeWriter.LOG.debug(String.format(KuduIOTest.FakeWriter.LOG_CLOSE_SESSION, id));
        }

        @Override
        public void close() {
            // called on teardown which give no guarantees
            KuduIOTest.FakeWriter.LOG.debug("FakeWriter[{}] close {}", id);
        }

        /**
         * Sets the unique id on deserialzation using the shared counter.
         */
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            id = KuduIOTest.FakeWriter.counter.incrementAndGet();
        }
    }

    /**
     * A fake reader which will return ascending integers from either 0 to 99 unless or using the
     * range specified in the serlialized token in the source. This is faking the behavior of the
     * scanner serialization in Kudu.
     */
    private static class FakeReader extends BoundedSource.BoundedReader<Integer> {
        private static final Logger LOG = LoggerFactory.getLogger(KuduIOTest.FakeReader.class);

        static final String LOG_SET_RANGE = "FakeReader serializedToken gives range %d - %d";

        private final KuduIO.KuduSource<Integer> source;

        private int lowerInclusive = 0;

        private int upperExclusive = 100;

        private int current = 0;

        private RowResult mockRecord = Mockito.mock(RowResult.class);// simulate a row from Kudu


        FakeReader(KuduIO.KuduSource<Integer> source) {
            this.source = source;
            // any request for an int from the mocked row will return the current value
            Mockito.when(mockRecord.getInt(ArgumentMatchers.any())).thenAnswer(((Answer<Integer>) (( invocation) -> current)));
        }

        @Override
        public boolean start() {
            // simulate the deserialization of a tablet scanner
            if ((serializedToken) != null) {
                ByteBuffer bb = ByteBuffer.wrap(serializedToken);
                lowerInclusive = bb.getInt();
                upperExclusive = bb.getInt();
                KuduIOTest.FakeReader.LOG.debug(String.format(KuduIOTest.FakeReader.LOG_SET_RANGE, lowerInclusive, upperExclusive));
            }
            current = lowerInclusive;
            return true;
        }

        @Override
        public boolean advance() {
            (current)++;
            return (current) < (upperExclusive);
        }

        @Override
        public Integer getCurrent() {
            return spec.getParseFn().apply(mockRecord);
        }

        @Override
        public void close() {
        }

        @Override
        public BoundedSource<Integer> getCurrentSource() {
            return source;
        }
    }

    // required to be a static class for serialization
    static class FakeReaderAnswer implements Serializable , Answer<KuduIOTest.FakeReader> {
        @Override
        public KuduIOTest.FakeReader answer(InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            return new KuduIOTest.FakeReader(((KuduIO.KuduSource<Integer>) (args[0])));
        }
    }
}

