/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.repository;


import RepositoryRecordSchema.CREATE_OR_UPDATE_SCHEMA_V1;
import RepositoryRecordSchema.REPOSITORY_RECORD_SCHEMA_V1;
import RepositoryRecordSchema.SWAP_IN_SCHEMA_V1;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.controller.queue.FlowFileQueue;
import org.apache.nifi.controller.repository.claim.StandardResourceClaimManager;
import org.junit.Assert;
import org.junit.Test;


public class SchemaRepositoryRecordSerdeTest {
    public static final String TEST_QUEUE_IDENTIFIER = "testQueueIdentifier";

    private StandardResourceClaimManager resourceClaimManager;

    private SchemaRepositoryRecordSerde schemaRepositoryRecordSerde;

    private Map<String, FlowFileQueue> queueMap;

    private FlowFileQueue flowFileQueue;

    private ByteArrayOutputStream byteArrayOutputStream;

    private DataOutputStream dataOutputStream;

    @Test
    public void testV1CreateCantHandleLongAttributeName() throws IOException {
        REPOSITORY_RECORD_SCHEMA_V1.writeTo(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put(stringBuilder.toString(), "testValue");
        schemaRepositoryRecordSerde.serializeRecord(createCreateFlowFileRecord(attributes), dataOutputStream, CREATE_OR_UPDATE_SCHEMA_V1, REPOSITORY_RECORD_SCHEMA_V1);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertNotEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testV1CreateCantHandleLongAttributeValue() throws IOException {
        REPOSITORY_RECORD_SCHEMA_V1.writeTo(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put("testName", stringBuilder.toString());
        schemaRepositoryRecordSerde.serializeRecord(createCreateFlowFileRecord(attributes), dataOutputStream, CREATE_OR_UPDATE_SCHEMA_V1, REPOSITORY_RECORD_SCHEMA_V1);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertNotEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testV2CreateCanHandleLongAttributeName() throws IOException {
        schemaRepositoryRecordSerde.writeHeader(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put(stringBuilder.toString(), "testValue");
        schemaRepositoryRecordSerde.serializeRecord(createCreateFlowFileRecord(attributes), dataOutputStream);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testV2CreateCanHandleLongAttributeValue() throws IOException {
        schemaRepositoryRecordSerde.writeHeader(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put("testName", stringBuilder.toString());
        schemaRepositoryRecordSerde.serializeRecord(createCreateFlowFileRecord(attributes), dataOutputStream);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testRoundTripCreateV1ToV2() throws IOException {
        REPOSITORY_RECORD_SCHEMA_V1.writeTo(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("testName", "testValue");
        schemaRepositoryRecordSerde.serializeRecord(createCreateFlowFileRecord(attributes), dataOutputStream, CREATE_OR_UPDATE_SCHEMA_V1, REPOSITORY_RECORD_SCHEMA_V1);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testV1SwapInCantHandleLongAttributeName() throws IOException {
        REPOSITORY_RECORD_SCHEMA_V1.writeTo(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put(stringBuilder.toString(), "testValue");
        StandardRepositoryRecord record = createCreateFlowFileRecord(attributes);
        record.setSwapLocation("fake");
        Assert.assertEquals(RepositoryRecordType.SWAP_IN, record.getType());
        schemaRepositoryRecordSerde.serializeRecord(record, dataOutputStream, SWAP_IN_SCHEMA_V1, REPOSITORY_RECORD_SCHEMA_V1);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertNotEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testV1SwapInCantHandleLongAttributeValue() throws IOException {
        REPOSITORY_RECORD_SCHEMA_V1.writeTo(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put("testName", stringBuilder.toString());
        StandardRepositoryRecord record = createCreateFlowFileRecord(attributes);
        record.setSwapLocation("fake");
        Assert.assertEquals(RepositoryRecordType.SWAP_IN, record.getType());
        schemaRepositoryRecordSerde.serializeRecord(record, dataOutputStream, SWAP_IN_SCHEMA_V1, REPOSITORY_RECORD_SCHEMA_V1);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertNotEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testV2SwapInCanHandleLongAttributeName() throws IOException {
        schemaRepositoryRecordSerde.writeHeader(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put(stringBuilder.toString(), "testValue");
        StandardRepositoryRecord record = createCreateFlowFileRecord(attributes);
        record.setSwapLocation("fake");
        Assert.assertEquals(RepositoryRecordType.SWAP_IN, record.getType());
        schemaRepositoryRecordSerde.serializeRecord(record, dataOutputStream);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testV2SwapInCanHandleLongAttributeValue() throws IOException {
        schemaRepositoryRecordSerde.writeHeader(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 65536; i++) {
            stringBuilder.append('a');
        }
        attributes.put("testName", stringBuilder.toString());
        StandardRepositoryRecord record = createCreateFlowFileRecord(attributes);
        record.setSwapLocation("fake");
        Assert.assertEquals(RepositoryRecordType.SWAP_IN, record.getType());
        schemaRepositoryRecordSerde.serializeRecord(record, dataOutputStream);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertEquals(attributes, repositoryRecord.getCurrent().getAttributes());
    }

    @Test
    public void testRoundTripSwapInV1ToV2() throws IOException {
        REPOSITORY_RECORD_SCHEMA_V1.writeTo(dataOutputStream);
        Map<String, String> attributes = new HashMap<>();
        attributes.put("testName", "testValue");
        StandardRepositoryRecord record = createCreateFlowFileRecord(attributes);
        record.setSwapLocation("fake");
        Assert.assertEquals(RepositoryRecordType.SWAP_IN, record.getType());
        schemaRepositoryRecordSerde.serializeRecord(record, dataOutputStream, SWAP_IN_SCHEMA_V1, REPOSITORY_RECORD_SCHEMA_V1);
        DataInputStream dataInputStream = createDataInputStream();
        schemaRepositoryRecordSerde.readHeader(dataInputStream);
        RepositoryRecord repositoryRecord = schemaRepositoryRecordSerde.deserializeRecord(dataInputStream, 2);
        Assert.assertEquals(attributes, repositoryRecord.getCurrent().getAttributes());
        Assert.assertEquals(RepositoryRecordType.SWAP_IN, repositoryRecord.getType());
    }
}

