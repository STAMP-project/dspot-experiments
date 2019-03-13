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
package org.apache.flume.sink.kite;


import DatasetSinkConstants.CONFIG_FAILURE_POLICY;
import DatasetSinkConstants.CONFIG_FLUSHABLE_COMMIT_ON_BATCH;
import DatasetSinkConstants.CONFIG_KITE_DATASET_NAME;
import DatasetSinkConstants.CONFIG_KITE_DATASET_URI;
import DatasetSinkConstants.CONFIG_KITE_ERROR_DATASET_URI;
import DatasetSinkConstants.CONFIG_KITE_REPO_URI;
import DatasetSinkConstants.CONFIG_SYNCABLE_SYNC_ON_BATCH;
import DatasetSinkConstants.SAVE_FAILURE_POLICY;
import GenericData.Record;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.sink.kite.parser.EntityParser;
import org.apache.flume.sink.kite.policy.FailurePolicy;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;
import org.kitesdk.data.Dataset;
import org.kitesdk.data.DatasetDescriptor;
import org.kitesdk.data.DatasetWriter;
import org.kitesdk.data.Datasets;
import org.kitesdk.data.PartitionStrategy;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestDatasetSink {
    public static final String FILE_REPO_URI = "repo:file:target/test_repo";

    public static final String DATASET_NAME = "test";

    public static final String FILE_DATASET_URI = "dataset:file:target/test_repo/" + (TestDatasetSink.DATASET_NAME);

    public static final String ERROR_DATASET_URI = "dataset:file:target/test_repo/failed_events";

    public static final File SCHEMA_FILE = new File("target/record-schema.avsc");

    public static final Schema RECORD_SCHEMA = new Schema.Parser().parse(("{\"type\":\"record\",\"name\":\"rec\",\"fields\":[" + (("{\"name\":\"id\",\"type\":\"string\"}," + "{\"name\":\"msg\",\"type\":[\"string\",\"null\"],") + "\"default\":\"default\"}]}")));

    public static final Schema COMPATIBLE_SCHEMA = new Schema.Parser().parse(("{\"type\":\"record\",\"name\":\"rec\",\"fields\":[" + "{\"name\":\"id\",\"type\":\"string\"}]}"));

    public static final Schema INCOMPATIBLE_SCHEMA = new Schema.Parser().parse(("{\"type\":\"record\",\"name\":\"user\",\"fields\":[" + "{\"name\":\"username\",\"type\":\"string\"}]}"));

    public static final Schema UPDATED_SCHEMA = new Schema.Parser().parse(("{\"type\":\"record\",\"name\":\"rec\",\"fields\":[" + ((("{\"name\":\"id\",\"type\":\"string\"}," + "{\"name\":\"priority\",\"type\":\"int\", \"default\": 0},") + "{\"name\":\"msg\",\"type\":[\"string\",\"null\"],") + "\"default\":\"default\"}]}")));

    public static final DatasetDescriptor DESCRIPTOR = new DatasetDescriptor.Builder().schema(TestDatasetSink.RECORD_SCHEMA).build();

    Context config = null;

    Channel in = null;

    List<GenericRecord> expected = null;

    private static final String DFS_DIR = "target/test/dfs";

    private static final String TEST_BUILD_DATA_KEY = "test.build.data";

    private static String oldTestBuildDataProp = null;

    @Test
    public void testOldConfig() throws EventDeliveryException {
        config.put(CONFIG_KITE_DATASET_URI, null);
        config.put(CONFIG_KITE_REPO_URI, TestDatasetSink.FILE_REPO_URI);
        config.put(CONFIG_KITE_DATASET_NAME, TestDatasetSink.DATASET_NAME);
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testDatasetUriOverridesOldConfig() throws EventDeliveryException {
        // CONFIG_KITE_DATASET_URI is still set, otherwise this will cause an error
        config.put(CONFIG_KITE_REPO_URI, "bad uri");
        config.put(CONFIG_KITE_DATASET_NAME, "");
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testFileStore() throws EventDeliveryException, NonRecoverableEventException {
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testParquetDataset() throws EventDeliveryException {
        Datasets.delete(TestDatasetSink.FILE_DATASET_URI);
        Dataset<GenericRecord> created = Datasets.create(TestDatasetSink.FILE_DATASET_URI, format("parquet").build());
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        // the transaction should not commit during the call to process
        TestDatasetSink.assertThrows("Transaction should still be open", IllegalStateException.class, new Callable() {
            @Override
            public Object call() throws EventDeliveryException {
                in.getTransaction().begin();
                return null;
            }
        });
        // The records won't commit until the call to stop()
        Assert.assertEquals("Should not have committed", 0, TestDatasetSink.read(created).size());
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(created));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testPartitionedData() throws EventDeliveryException {
        URI partitionedUri = URI.create("dataset:file:target/test_repo/partitioned");
        try {
            Datasets.create(partitionedUri, new DatasetDescriptor.Builder(TestDatasetSink.DESCRIPTOR).partitionStrategy(// partition by id
            new PartitionStrategy.Builder().identity("id", 10).build()).build());
            config.put(CONFIG_KITE_DATASET_URI, partitionedUri.toString());
            DatasetSink sink = TestDatasetSink.sink(in, config);
            // run the sink
            sink.start();
            sink.process();
            sink.stop();
            Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(partitionedUri)));
            Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        } finally {
            if (Datasets.exists(partitionedUri)) {
                Datasets.delete(partitionedUri);
            }
        }
    }

    @Test
    public void testStartBeforeDatasetCreated() throws EventDeliveryException {
        // delete the dataset created by setup
        Datasets.delete(TestDatasetSink.FILE_DATASET_URI);
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // start the sink
        sink.start();
        // run the sink without a target dataset
        try {
            sink.process();
            Assert.fail("Should have thrown an exception: no such dataset");
        } catch (EventDeliveryException e) {
            // expected
        }
        // create the target dataset
        Datasets.create(TestDatasetSink.FILE_DATASET_URI, TestDatasetSink.DESCRIPTOR);
        // run the sink
        sink.process();
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testDatasetUpdate() throws EventDeliveryException {
        // add an updated record that is missing the msg field
        GenericRecordBuilder updatedBuilder = new GenericRecordBuilder(TestDatasetSink.UPDATED_SCHEMA);
        GenericData.Record updatedRecord = updatedBuilder.set("id", "0").set("priority", 1).set("msg", "Priority 1 message!").build();
        // make a set of the expected records with the new schema
        Set<GenericRecord> expectedAsUpdated = Sets.newHashSet();
        for (GenericRecord record : expected) {
            expectedAsUpdated.add(updatedBuilder.clear("priority").set("id", record.get("id")).set("msg", record.get("msg")).build());
        }
        expectedAsUpdated.add(updatedRecord);
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        // update the dataset's schema
        DatasetDescriptor updated = new DatasetDescriptor.Builder(Datasets.load(TestDatasetSink.FILE_DATASET_URI).getDataset().getDescriptor()).schema(TestDatasetSink.UPDATED_SCHEMA).build();
        Datasets.update(TestDatasetSink.FILE_DATASET_URI, updated);
        // trigger a roll on the next process call to refresh the writer
        sink.roll();
        // add the record to the incoming channel and the expected list
        TestDatasetSink.putToChannel(in, TestDatasetSink.event(updatedRecord, TestDatasetSink.UPDATED_SCHEMA, null, false));
        // process events with the updated schema
        sink.process();
        sink.stop();
        Assert.assertEquals(expectedAsUpdated, TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testMiniClusterStore() throws IOException, EventDeliveryException {
        // setup a minicluster
        MiniDFSCluster cluster = build();
        FileSystem dfs = cluster.getFileSystem();
        Configuration conf = dfs.getConf();
        URI hdfsUri = URI.create(((("dataset:" + (conf.get("fs.defaultFS"))) + "/tmp/repo") + (TestDatasetSink.DATASET_NAME)));
        try {
            // create a repository and dataset in HDFS
            Datasets.create(hdfsUri, TestDatasetSink.DESCRIPTOR);
            // update the config to use the HDFS repository
            config.put(CONFIG_KITE_DATASET_URI, hdfsUri.toString());
            DatasetSink sink = TestDatasetSink.sink(in, config);
            // run the sink
            sink.start();
            sink.process();
            sink.stop();
            Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(hdfsUri)));
            Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        } finally {
            if (Datasets.exists(hdfsUri)) {
                Datasets.delete(hdfsUri);
            }
            cluster.shutdown();
        }
    }

    @Test
    public void testBatchSize() throws EventDeliveryException {
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // release one record per process call
        config.put("kite.batchSize", "2");
        Configurables.configure(sink, config);
        sink.start();
        sink.process();// process the first and second

        sink.roll();// roll at the next process call

        sink.process();// roll and process the third

        Assert.assertEquals(Sets.newHashSet(expected.subList(0, 2)), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        sink.roll();// roll at the next process call

        sink.process();// roll, the channel is empty

        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        sink.stop();
    }

    @Test
    public void testTimedFileRolling() throws InterruptedException, EventDeliveryException {
        // use a new roll interval
        config.put("kite.rollInterval", "1");// in seconds

        DatasetSink sink = TestDatasetSink.sink(in, config);
        Dataset<GenericRecord> records = Datasets.load(TestDatasetSink.FILE_DATASET_URI);
        // run the sink
        sink.start();
        sink.process();
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        Thread.sleep(1100);// sleep longer than the roll interval

        sink.process();// rolling happens in the process method

        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(records));
        // wait until the end to stop because it would close the files
        sink.stop();
    }

    @Test
    public void testCompatibleSchemas() throws EventDeliveryException {
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // add a compatible record that is missing the msg field
        GenericRecordBuilder compatBuilder = new GenericRecordBuilder(TestDatasetSink.COMPATIBLE_SCHEMA);
        GenericData.Record compatibleRecord = compatBuilder.set("id", "0").build();
        // add the record to the incoming channel
        TestDatasetSink.putToChannel(in, TestDatasetSink.event(compatibleRecord, TestDatasetSink.COMPATIBLE_SCHEMA, null, false));
        // the record will be read using the real schema, so create the expected
        // record using it, but without any data
        GenericRecordBuilder builder = new GenericRecordBuilder(TestDatasetSink.RECORD_SCHEMA);
        GenericData.Record expectedRecord = builder.set("id", "0").build();
        expected.add(expectedRecord);
        // run the sink
        sink.start();
        sink.process();
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testIncompatibleSchemas() throws EventDeliveryException {
        final DatasetSink sink = TestDatasetSink.sink(in, config);
        GenericRecordBuilder builder = new GenericRecordBuilder(TestDatasetSink.INCOMPATIBLE_SCHEMA);
        GenericData.Record rec = builder.set("username", "koala").build();
        TestDatasetSink.putToChannel(in, TestDatasetSink.event(rec, TestDatasetSink.INCOMPATIBLE_SCHEMA, null, false));
        // run the sink
        sink.start();
        TestDatasetSink.assertThrows("Should fail", EventDeliveryException.class, new Callable() {
            @Override
            public Object call() throws EventDeliveryException {
                sink.process();
                return null;
            }
        });
        sink.stop();
        Assert.assertEquals("Should have rolled back", ((expected.size()) + 1), TestDatasetSink.remaining(in));
    }

    @Test
    public void testMissingSchema() throws EventDeliveryException {
        final DatasetSink sink = TestDatasetSink.sink(in, config);
        Event badEvent = new SimpleEvent();
        badEvent.setHeaders(Maps.<String, String>newHashMap());
        badEvent.setBody(TestDatasetSink.serialize(expected.get(0), TestDatasetSink.RECORD_SCHEMA));
        TestDatasetSink.putToChannel(in, badEvent);
        // run the sink
        sink.start();
        TestDatasetSink.assertThrows("Should fail", EventDeliveryException.class, new Callable() {
            @Override
            public Object call() throws EventDeliveryException {
                sink.process();
                return null;
            }
        });
        sink.stop();
        Assert.assertEquals("Should have rolled back", ((expected.size()) + 1), TestDatasetSink.remaining(in));
    }

    @Test
    public void testFileStoreWithSavePolicy() throws EventDeliveryException {
        if (Datasets.exists(TestDatasetSink.ERROR_DATASET_URI)) {
            Datasets.delete(TestDatasetSink.ERROR_DATASET_URI);
        }
        config.put(CONFIG_FAILURE_POLICY, SAVE_FAILURE_POLICY);
        config.put(CONFIG_KITE_ERROR_DATASET_URI, TestDatasetSink.ERROR_DATASET_URI);
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testMissingSchemaWithSavePolicy() throws EventDeliveryException {
        if (Datasets.exists(TestDatasetSink.ERROR_DATASET_URI)) {
            Datasets.delete(TestDatasetSink.ERROR_DATASET_URI);
        }
        config.put(CONFIG_FAILURE_POLICY, SAVE_FAILURE_POLICY);
        config.put(CONFIG_KITE_ERROR_DATASET_URI, TestDatasetSink.ERROR_DATASET_URI);
        final DatasetSink sink = TestDatasetSink.sink(in, config);
        Event badEvent = new SimpleEvent();
        badEvent.setHeaders(Maps.<String, String>newHashMap());
        badEvent.setBody(TestDatasetSink.serialize(expected.get(0), TestDatasetSink.RECORD_SCHEMA));
        TestDatasetSink.putToChannel(in, badEvent);
        // run the sink
        sink.start();
        sink.process();
        sink.stop();
        Assert.assertEquals("Good records should have been written", Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should not have rolled back", 0, TestDatasetSink.remaining(in));
        Assert.assertEquals("Should have saved the bad event", Sets.newHashSet(AvroFlumeEvent.newBuilder().setBody(java.nio.ByteBuffer.wrap(badEvent.getBody())).setHeaders(TestDatasetSink.toUtf8Map(badEvent.getHeaders())).build()), TestDatasetSink.read(Datasets.load(TestDatasetSink.ERROR_DATASET_URI, AvroFlumeEvent.class)));
    }

    @Test
    public void testSerializedWithIncompatibleSchemasWithSavePolicy() throws EventDeliveryException {
        if (Datasets.exists(TestDatasetSink.ERROR_DATASET_URI)) {
            Datasets.delete(TestDatasetSink.ERROR_DATASET_URI);
        }
        config.put(CONFIG_FAILURE_POLICY, SAVE_FAILURE_POLICY);
        config.put(CONFIG_KITE_ERROR_DATASET_URI, TestDatasetSink.ERROR_DATASET_URI);
        final DatasetSink sink = TestDatasetSink.sink(in, config);
        GenericRecordBuilder builder = new GenericRecordBuilder(TestDatasetSink.INCOMPATIBLE_SCHEMA);
        GenericData.Record rec = builder.set("username", "koala").build();
        // We pass in a valid schema in the header, but an incompatible schema
        // was used to serialize the record
        Event badEvent = TestDatasetSink.event(rec, TestDatasetSink.INCOMPATIBLE_SCHEMA, TestDatasetSink.SCHEMA_FILE, true);
        TestDatasetSink.putToChannel(in, badEvent);
        // run the sink
        sink.start();
        sink.process();
        sink.stop();
        Assert.assertEquals("Good records should have been written", Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        Assert.assertEquals("Should not have rolled back", 0, TestDatasetSink.remaining(in));
        Assert.assertEquals("Should have saved the bad event", Sets.newHashSet(AvroFlumeEvent.newBuilder().setBody(java.nio.ByteBuffer.wrap(badEvent.getBody())).setHeaders(TestDatasetSink.toUtf8Map(badEvent.getHeaders())).build()), TestDatasetSink.read(Datasets.load(TestDatasetSink.ERROR_DATASET_URI, AvroFlumeEvent.class)));
    }

    @Test
    public void testSerializedWithIncompatibleSchemas() throws EventDeliveryException {
        final DatasetSink sink = TestDatasetSink.sink(in, config);
        GenericRecordBuilder builder = new GenericRecordBuilder(TestDatasetSink.INCOMPATIBLE_SCHEMA);
        GenericData.Record rec = builder.set("username", "koala").build();
        // We pass in a valid schema in the header, but an incompatible schema
        // was used to serialize the record
        TestDatasetSink.putToChannel(in, TestDatasetSink.event(rec, TestDatasetSink.INCOMPATIBLE_SCHEMA, TestDatasetSink.SCHEMA_FILE, true));
        // run the sink
        sink.start();
        TestDatasetSink.assertThrows("Should fail", EventDeliveryException.class, new Callable() {
            @Override
            public Object call() throws EventDeliveryException {
                sink.process();
                return null;
            }
        });
        sink.stop();
        Assert.assertEquals("Should have rolled back", ((expected.size()) + 1), TestDatasetSink.remaining(in));
    }

    @Test
    public void testCommitOnBatch() throws EventDeliveryException {
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        // the transaction should commit during the call to process
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        // but the data won't be visible yet
        Assert.assertEquals(0, TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)).size());
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
    }

    @Test
    public void testCommitOnBatchFalse() throws EventDeliveryException {
        config.put(CONFIG_FLUSHABLE_COMMIT_ON_BATCH, Boolean.toString(false));
        config.put(CONFIG_SYNCABLE_SYNC_ON_BATCH, Boolean.toString(false));
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        // the transaction should not commit during the call to process
        TestDatasetSink.assertThrows("Transaction should still be open", IllegalStateException.class, new Callable() {
            @Override
            public Object call() throws EventDeliveryException {
                in.getTransaction().begin();
                return null;
            }
        });
        // the data won't be visible
        Assert.assertEquals(0, TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)).size());
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
        // the transaction should commit during the call to stop
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }

    @Test
    public void testCommitOnBatchFalseSyncOnBatchTrue() throws EventDeliveryException {
        config.put(CONFIG_FLUSHABLE_COMMIT_ON_BATCH, Boolean.toString(false));
        config.put(CONFIG_SYNCABLE_SYNC_ON_BATCH, Boolean.toString(true));
        try {
            TestDatasetSink.sink(in, config);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testCloseAndCreateWriter() throws EventDeliveryException {
        config.put(CONFIG_FLUSHABLE_COMMIT_ON_BATCH, Boolean.toString(false));
        config.put(CONFIG_SYNCABLE_SYNC_ON_BATCH, Boolean.toString(false));
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        sink.closeWriter();
        sink.commitTransaction();
        sink.createWriter();
        Assert.assertNotNull("Writer should not be null", sink.getWriter());
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
    }

    @Test
    public void testCloseWriter() throws EventDeliveryException {
        config.put(CONFIG_FLUSHABLE_COMMIT_ON_BATCH, Boolean.toString(false));
        config.put(CONFIG_SYNCABLE_SYNC_ON_BATCH, Boolean.toString(false));
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        sink.closeWriter();
        sink.commitTransaction();
        Assert.assertNull("Writer should be null", sink.getWriter());
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        sink.stop();
        Assert.assertEquals(Sets.newHashSet(expected), TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)));
    }

    @Test
    public void testCreateWriter() throws EventDeliveryException {
        config.put(CONFIG_FLUSHABLE_COMMIT_ON_BATCH, Boolean.toString(false));
        config.put(CONFIG_SYNCABLE_SYNC_ON_BATCH, Boolean.toString(false));
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        sink.commitTransaction();
        sink.createWriter();
        Assert.assertNotNull("Writer should not be null", sink.getWriter());
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
        sink.stop();
        Assert.assertEquals(0, TestDatasetSink.read(Datasets.load(TestDatasetSink.FILE_DATASET_URI)).size());
    }

    @Test
    public void testAppendWriteExceptionInvokesPolicy() throws EventDeliveryException, NonRecoverableEventException {
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        // Mock an Event
        Event mockEvent = Mockito.mock(Event.class);
        Mockito.when(mockEvent.getBody()).thenReturn(new byte[]{ 1 });
        // Mock a GenericRecord
        GenericRecord mockRecord = Mockito.mock(GenericRecord.class);
        // Mock an EntityParser
        EntityParser<GenericRecord> mockParser = Mockito.mock(EntityParser.class);
        Mockito.when(mockParser.parse(ArgumentMatchers.eq(mockEvent), ArgumentMatchers.any(GenericRecord.class))).thenReturn(mockRecord);
        sink.setParser(mockParser);
        // Mock a FailurePolicy
        FailurePolicy mockFailurePolicy = Mockito.mock(FailurePolicy.class);
        sink.setFailurePolicy(mockFailurePolicy);
        // Mock a DatasetWriter
        DatasetWriter<GenericRecord> mockWriter = Mockito.mock(DatasetWriter.class);
        Mockito.doThrow(new DataFileWriter.AppendWriteException(new IOException())).when(mockWriter).write(mockRecord);
        sink.setWriter(mockWriter);
        sink.write(mockEvent);
        // Verify that the event was sent to the failure policy
        Mockito.verify(mockFailurePolicy).handle(ArgumentMatchers.eq(mockEvent), ArgumentMatchers.any(Throwable.class));
        sink.stop();
    }

    @Test
    public void testRuntimeExceptionThrowsEventDeliveryException() throws EventDeliveryException, NonRecoverableEventException {
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        // Mock an Event
        Event mockEvent = Mockito.mock(Event.class);
        Mockito.when(mockEvent.getBody()).thenReturn(new byte[]{ 1 });
        // Mock a GenericRecord
        GenericRecord mockRecord = Mockito.mock(GenericRecord.class);
        // Mock an EntityParser
        EntityParser<GenericRecord> mockParser = Mockito.mock(EntityParser.class);
        Mockito.when(mockParser.parse(ArgumentMatchers.eq(mockEvent), ArgumentMatchers.any(GenericRecord.class))).thenReturn(mockRecord);
        sink.setParser(mockParser);
        // Mock a FailurePolicy
        FailurePolicy mockFailurePolicy = Mockito.mock(FailurePolicy.class);
        sink.setFailurePolicy(mockFailurePolicy);
        // Mock a DatasetWriter
        DatasetWriter<GenericRecord> mockWriter = Mockito.mock(DatasetWriter.class);
        Mockito.doThrow(new RuntimeException()).when(mockWriter).write(mockRecord);
        sink.setWriter(mockWriter);
        try {
            sink.write(mockEvent);
            Assert.fail("Should throw EventDeliveryException");
        } catch (EventDeliveryException ex) {
        }
        // Verify that the event was not sent to the failure policy
        Mockito.verify(mockFailurePolicy, Mockito.never()).handle(ArgumentMatchers.eq(mockEvent), ArgumentMatchers.any(Throwable.class));
        sink.stop();
    }

    @Test
    public void testProcessHandlesNullWriter() throws EventDeliveryException, NonRecoverableEventException {
        DatasetSink sink = TestDatasetSink.sink(in, config);
        // run the sink
        sink.start();
        sink.process();
        // explicitly set the writer to null
        sink.setWriter(null);
        // this should not throw an NPE
        sink.process();
        sink.stop();
        Assert.assertEquals("Should have committed", 0, TestDatasetSink.remaining(in));
    }
}

