/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.storage;


import ConnectorStatus.State;
import KafkaStatusBackingStore.GENERATION_KEY_NAME;
import KafkaStatusBackingStore.STATE_KEY_NAME;
import KafkaStatusBackingStore.WORKER_ID_KEY_NAME;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.UnknownServerException;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.runtime.ConnectorStatus;
import org.apache.kafka.connect.runtime.TaskStatus;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.apache.kafka.connect.util.KafkaBasedLog;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;

import static TaskStatus.State.RUNNING;


@SuppressWarnings("unchecked")
public class KafkaStatusBackingStoreTest extends EasyMockSupport {
    private static final String STATUS_TOPIC = "status-topic";

    private static final String WORKER_ID = "localhost:8083";

    private static final String CONNECTOR = "conn";

    private static final ConnectorTaskId TASK = new ConnectorTaskId(KafkaStatusBackingStoreTest.CONNECTOR, 0);

    @Test
    public void putConnectorState() {
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        byte[] value = new byte[0];
        expect(converter.fromConnectData(eq(KafkaStatusBackingStoreTest.STATUS_TOPIC), anyObject(Schema.class), anyObject(Struct.class))).andStubReturn(value);
        final Capture<Callback> callbackCapture = newCapture();
        kafkaBasedLog.send(eq("status-connector-conn"), eq(value), capture(callbackCapture));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                callbackCapture.getValue().onCompletion(null, null);
                return null;
            }
        });
        replayAll();
        ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.RUNNING, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        store.put(status);
        // state is not visible until read back from the log
        Assert.assertEquals(null, store.get(KafkaStatusBackingStoreTest.CONNECTOR));
        verifyAll();
    }

    @Test
    public void putConnectorStateRetriableFailure() {
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        byte[] value = new byte[0];
        expect(converter.fromConnectData(eq(KafkaStatusBackingStoreTest.STATUS_TOPIC), anyObject(Schema.class), anyObject(Struct.class))).andStubReturn(value);
        final Capture<Callback> callbackCapture = newCapture();
        kafkaBasedLog.send(eq("status-connector-conn"), eq(value), capture(callbackCapture));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                callbackCapture.getValue().onCompletion(null, new TimeoutException());
                return null;
            }
        }).andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                callbackCapture.getValue().onCompletion(null, null);
                return null;
            }
        });
        replayAll();
        ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.RUNNING, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        store.put(status);
        // state is not visible until read back from the log
        Assert.assertEquals(null, store.get(KafkaStatusBackingStoreTest.CONNECTOR));
        verifyAll();
    }

    @Test
    public void putConnectorStateNonRetriableFailure() {
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        byte[] value = new byte[0];
        expect(converter.fromConnectData(eq(KafkaStatusBackingStoreTest.STATUS_TOPIC), anyObject(Schema.class), anyObject(Struct.class))).andStubReturn(value);
        final Capture<Callback> callbackCapture = newCapture();
        kafkaBasedLog.send(eq("status-connector-conn"), eq(value), capture(callbackCapture));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                callbackCapture.getValue().onCompletion(null, new UnknownServerException());
                return null;
            }
        });
        replayAll();
        // the error is logged and ignored
        ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.RUNNING, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        store.put(status);
        // state is not visible until read back from the log
        Assert.assertEquals(null, store.get(KafkaStatusBackingStoreTest.CONNECTOR));
        verifyAll();
    }

    @Test
    public void putSafeConnectorIgnoresStaleStatus() {
        byte[] value = new byte[0];
        String otherWorkerId = "anotherhost:8083";
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        // the persisted came from a different host and has a newer generation
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("worker_id", otherWorkerId);
        statusMap.put("state", "RUNNING");
        statusMap.put("generation", 1L);
        expect(converter.toConnectData(KafkaStatusBackingStoreTest.STATUS_TOPIC, value)).andReturn(new SchemaAndValue(null, statusMap));
        // we're verifying that there is no call to KafkaBasedLog.send
        replayAll();
        store.read(KafkaStatusBackingStoreTest.consumerRecord(0, "status-connector-conn", value));
        store.putSafe(new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.UNASSIGNED, KafkaStatusBackingStoreTest.WORKER_ID, 0));
        ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.RUNNING, otherWorkerId, 1);
        Assert.assertEquals(status, store.get(KafkaStatusBackingStoreTest.CONNECTOR));
        verifyAll();
    }

    @Test
    public void putSafeWithNoPreviousValueIsPropagated() {
        final Converter converter = mock(Converter.class);
        final KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        final KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        final byte[] value = new byte[0];
        final Capture<Struct> statusValueStruct = newCapture();
        converter.fromConnectData(eq(KafkaStatusBackingStoreTest.STATUS_TOPIC), anyObject(Schema.class), capture(statusValueStruct));
        EasyMock.expectLastCall().andReturn(value);
        kafkaBasedLog.send(eq(("status-connector-" + (KafkaStatusBackingStoreTest.CONNECTOR))), eq(value), anyObject(Callback.class));
        expectLastCall();
        replayAll();
        final ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.FAILED, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        store.putSafe(status);
        verifyAll();
        Assert.assertEquals(status.state().toString(), statusValueStruct.getValue().get(STATE_KEY_NAME));
        Assert.assertEquals(status.workerId(), statusValueStruct.getValue().get(WORKER_ID_KEY_NAME));
        Assert.assertEquals(status.generation(), statusValueStruct.getValue().get(GENERATION_KEY_NAME));
    }

    @Test
    public void putSafeOverridesValueSetBySameWorker() {
        final byte[] value = new byte[0];
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        final KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        // the persisted came from the same host, but has a newer generation
        Map<String, Object> firstStatusRead = new HashMap<>();
        firstStatusRead.put("worker_id", KafkaStatusBackingStoreTest.WORKER_ID);
        firstStatusRead.put("state", "RUNNING");
        firstStatusRead.put("generation", 1L);
        Map<String, Object> secondStatusRead = new HashMap<>();
        secondStatusRead.put("worker_id", KafkaStatusBackingStoreTest.WORKER_ID);
        secondStatusRead.put("state", "UNASSIGNED");
        secondStatusRead.put("generation", 0L);
        expect(converter.toConnectData(KafkaStatusBackingStoreTest.STATUS_TOPIC, value)).andReturn(new SchemaAndValue(null, firstStatusRead)).andReturn(new SchemaAndValue(null, secondStatusRead));
        expect(converter.fromConnectData(eq(KafkaStatusBackingStoreTest.STATUS_TOPIC), anyObject(Schema.class), anyObject(Struct.class))).andStubReturn(value);
        final Capture<Callback> callbackCapture = newCapture();
        kafkaBasedLog.send(eq("status-connector-conn"), eq(value), capture(callbackCapture));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                callbackCapture.getValue().onCompletion(null, null);
                store.read(KafkaStatusBackingStoreTest.consumerRecord(1, "status-connector-conn", value));
                return null;
            }
        });
        replayAll();
        store.read(KafkaStatusBackingStoreTest.consumerRecord(0, "status-connector-conn", value));
        store.putSafe(new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.UNASSIGNED, KafkaStatusBackingStoreTest.WORKER_ID, 0));
        ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.UNASSIGNED, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        Assert.assertEquals(status, store.get(KafkaStatusBackingStoreTest.CONNECTOR));
        verifyAll();
    }

    @Test
    public void putConnectorStateShouldOverride() {
        final byte[] value = new byte[0];
        String otherWorkerId = "anotherhost:8083";
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        final KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        // the persisted came from a different host and has a newer generation
        Map<String, Object> firstStatusRead = new HashMap<>();
        firstStatusRead.put("worker_id", otherWorkerId);
        firstStatusRead.put("state", "RUNNING");
        firstStatusRead.put("generation", 1L);
        Map<String, Object> secondStatusRead = new HashMap<>();
        secondStatusRead.put("worker_id", KafkaStatusBackingStoreTest.WORKER_ID);
        secondStatusRead.put("state", "UNASSIGNED");
        secondStatusRead.put("generation", 0L);
        expect(converter.toConnectData(KafkaStatusBackingStoreTest.STATUS_TOPIC, value)).andReturn(new SchemaAndValue(null, firstStatusRead)).andReturn(new SchemaAndValue(null, secondStatusRead));
        expect(converter.fromConnectData(eq(KafkaStatusBackingStoreTest.STATUS_TOPIC), anyObject(Schema.class), anyObject(Struct.class))).andStubReturn(value);
        final Capture<Callback> callbackCapture = newCapture();
        kafkaBasedLog.send(eq("status-connector-conn"), eq(value), capture(callbackCapture));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                callbackCapture.getValue().onCompletion(null, null);
                store.read(KafkaStatusBackingStoreTest.consumerRecord(1, "status-connector-conn", value));
                return null;
            }
        });
        replayAll();
        store.read(KafkaStatusBackingStoreTest.consumerRecord(0, "status-connector-conn", value));
        ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.UNASSIGNED, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        store.put(status);
        Assert.assertEquals(status, store.get(KafkaStatusBackingStoreTest.CONNECTOR));
        verifyAll();
    }

    @Test
    public void readConnectorState() {
        byte[] value = new byte[0];
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("worker_id", KafkaStatusBackingStoreTest.WORKER_ID);
        statusMap.put("state", "RUNNING");
        statusMap.put("generation", 0L);
        expect(converter.toConnectData(KafkaStatusBackingStoreTest.STATUS_TOPIC, value)).andReturn(new SchemaAndValue(null, statusMap));
        replayAll();
        store.read(KafkaStatusBackingStoreTest.consumerRecord(0, "status-connector-conn", value));
        ConnectorStatus status = new ConnectorStatus(KafkaStatusBackingStoreTest.CONNECTOR, State.RUNNING, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        Assert.assertEquals(status, store.get(KafkaStatusBackingStoreTest.CONNECTOR));
        verifyAll();
    }

    @Test
    public void putTaskState() {
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        byte[] value = new byte[0];
        expect(converter.fromConnectData(eq(KafkaStatusBackingStoreTest.STATUS_TOPIC), anyObject(Schema.class), anyObject(Struct.class))).andStubReturn(value);
        final Capture<Callback> callbackCapture = newCapture();
        kafkaBasedLog.send(eq("status-task-conn-0"), eq(value), capture(callbackCapture));
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                callbackCapture.getValue().onCompletion(null, null);
                return null;
            }
        });
        replayAll();
        TaskStatus status = new TaskStatus(KafkaStatusBackingStoreTest.TASK, RUNNING, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        store.put(status);
        // state is not visible until read back from the log
        Assert.assertEquals(null, store.get(KafkaStatusBackingStoreTest.TASK));
        verifyAll();
    }

    @Test
    public void readTaskState() {
        byte[] value = new byte[0];
        KafkaBasedLog<String, byte[]> kafkaBasedLog = mock(KafkaBasedLog.class);
        Converter converter = mock(Converter.class);
        KafkaStatusBackingStore store = new KafkaStatusBackingStore(new MockTime(), converter, KafkaStatusBackingStoreTest.STATUS_TOPIC, kafkaBasedLog);
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("worker_id", KafkaStatusBackingStoreTest.WORKER_ID);
        statusMap.put("state", "RUNNING");
        statusMap.put("generation", 0L);
        expect(converter.toConnectData(KafkaStatusBackingStoreTest.STATUS_TOPIC, value)).andReturn(new SchemaAndValue(null, statusMap));
        replayAll();
        store.read(KafkaStatusBackingStoreTest.consumerRecord(0, "status-task-conn-0", value));
        TaskStatus status = new TaskStatus(KafkaStatusBackingStoreTest.TASK, RUNNING, KafkaStatusBackingStoreTest.WORKER_ID, 0);
        Assert.assertEquals(status, store.get(KafkaStatusBackingStoreTest.TASK));
        verifyAll();
    }
}

