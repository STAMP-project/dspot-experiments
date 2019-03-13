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
package org.apache.nifi.processors.couchbase;


import CoreAttributes.UUID;
import CouchbaseAttributes.Cas;
import CouchbaseAttributes.Cluster;
import CouchbaseAttributes.DocId;
import CouchbaseAttributes.Expiry;
import DocumentType.Binary;
import PersistTo.MASTER;
import PersistTo.NONE;
import PutCouchbaseKey.PERSIST_TO;
import PutCouchbaseKey.REPLICATE_TO;
import ReplicateTo.ONE;
import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.core.ServiceNotAvailableException;
import com.couchbase.client.deps.io.netty.buffer.Unpooled;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.BinaryDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.error.DurabilityException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.attribute.expression.language.exception.AttributeExpressionLanguageException;
import org.apache.nifi.couchbase.CouchbaseConfigurationProperties;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestPutCouchbaseKey {
    private static final String SERVICE_ID = "couchbaseClusterService";

    private TestRunner testRunner;

    @Test
    public void testStaticDocId() throws Exception {
        String bucketName = "bucket-1";
        String docId = "doc-a";
        int expiry = 100;
        long cas = 200L;
        String inFileData = "{\"key\":\"value\"}";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE))).thenReturn(RawJsonDocument.create(docId, expiry, inFileData, cas));
        setupMockBucket(bucket);
        testRunner.enqueue(inFileDataBytes);
        testRunner.setProperty(CouchbaseConfigurationProperties.BUCKET_NAME, bucketName);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docId);
        testRunner.run();
        Mockito.verify(bucket, Mockito.times(1)).upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE));
        testRunner.assertAllFlowFilesTransferred(AbstractCouchbaseProcessor.REL_SUCCESS);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(inFileData);
        outFile.assertAttributeEquals(Cluster.key(), TestPutCouchbaseKey.SERVICE_ID);
        outFile.assertAttributeEquals(CouchbaseAttributes.Bucket.key(), bucketName);
        outFile.assertAttributeEquals(DocId.key(), docId);
        outFile.assertAttributeEquals(Cas.key(), String.valueOf(cas));
        outFile.assertAttributeEquals(Expiry.key(), String.valueOf(expiry));
    }

    @Test
    public void testBinaryDoc() throws Exception {
        String bucketName = "bucket-1";
        String docId = "doc-a";
        int expiry = 100;
        long cas = 200L;
        String inFileData = "12345";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.upsert(ArgumentMatchers.any(BinaryDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE))).thenReturn(BinaryDocument.create(docId, expiry, Unpooled.copiedBuffer(inFileData.getBytes(StandardCharsets.UTF_8)), cas));
        setupMockBucket(bucket);
        testRunner.enqueue(inFileDataBytes);
        testRunner.setProperty(CouchbaseConfigurationProperties.BUCKET_NAME, bucketName);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docId);
        testRunner.setProperty(CouchbaseConfigurationProperties.DOCUMENT_TYPE, Binary.name());
        testRunner.run();
        Mockito.verify(bucket, Mockito.times(1)).upsert(ArgumentMatchers.any(BinaryDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE));
        testRunner.assertAllFlowFilesTransferred(AbstractCouchbaseProcessor.REL_SUCCESS);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(inFileData);
        outFile.assertAttributeEquals(Cluster.key(), TestPutCouchbaseKey.SERVICE_ID);
        outFile.assertAttributeEquals(CouchbaseAttributes.Bucket.key(), bucketName);
        outFile.assertAttributeEquals(DocId.key(), docId);
        outFile.assertAttributeEquals(Cas.key(), String.valueOf(cas));
        outFile.assertAttributeEquals(Expiry.key(), String.valueOf(expiry));
    }

    @Test
    public void testDurabilityConstraint() throws Exception {
        String docId = "doc-a";
        String inFileData = "{\"key\":\"value\"}";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(MASTER), ArgumentMatchers.eq(ONE))).thenReturn(RawJsonDocument.create(docId, inFileData));
        setupMockBucket(bucket);
        testRunner.enqueue(inFileDataBytes);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docId);
        testRunner.setProperty(PERSIST_TO, MASTER.toString());
        testRunner.setProperty(REPLICATE_TO, ONE.toString());
        testRunner.run();
        Mockito.verify(bucket, Mockito.times(1)).upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(MASTER), ArgumentMatchers.eq(ONE));
        testRunner.assertAllFlowFilesTransferred(AbstractCouchbaseProcessor.REL_SUCCESS);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(inFileData);
    }

    @Test
    public void testDocIdExp() throws Exception {
        String docIdExp = "${'someProperty'}";
        String somePropertyValue = "doc-p";
        String inFileData = "{\"key\":\"value\"}";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE))).thenReturn(RawJsonDocument.create(somePropertyValue, inFileData));
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        Map<String, String> properties = new HashMap<>();
        properties.put("someProperty", somePropertyValue);
        testRunner.enqueue(inFileDataBytes, properties);
        testRunner.run();
        ArgumentCaptor<RawJsonDocument> capture = ArgumentCaptor.forClass(RawJsonDocument.class);
        Mockito.verify(bucket, Mockito.times(1)).upsert(capture.capture(), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE));
        Assert.assertEquals(somePropertyValue, capture.getValue().id());
        Assert.assertEquals(inFileData, capture.getValue().content());
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(inFileData);
    }

    @Test
    public void testInvalidDocIdExp() throws Exception {
        String docIdExp = "${invalid_function(someProperty)}";
        String somePropertyValue = "doc-p";
        String inFileData = "{\"key\":\"value\"}";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE))).thenReturn(RawJsonDocument.create(somePropertyValue, inFileData));
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        Map<String, String> properties = new HashMap<>();
        properties.put("someProperty", somePropertyValue);
        testRunner.enqueue(inFileDataBytes, properties);
        try {
            testRunner.run();
            Assert.fail("Exception should be thrown.");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getClass().equals(AttributeExpressionLanguageException.class));
        }
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
    }

    @Test
    public void testInputFlowFileUuid() throws Exception {
        String uuid = "00029362-5106-40e8-b8a9-bf2cecfbc0d7";
        String inFileData = "{\"key\":\"value\"}";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE))).thenReturn(RawJsonDocument.create(uuid, inFileData));
        setupMockBucket(bucket);
        Map<String, String> properties = new HashMap<>();
        properties.put(UUID.key(), uuid);
        testRunner.enqueue(inFileDataBytes, properties);
        testRunner.run();
        ArgumentCaptor<RawJsonDocument> capture = ArgumentCaptor.forClass(RawJsonDocument.class);
        Mockito.verify(bucket, Mockito.times(1)).upsert(capture.capture(), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ReplicateTo.NONE));
        Assert.assertEquals(inFileData, capture.getValue().content());
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(inFileData);
    }

    @Test
    public void testCouchbaseFailure() throws Exception {
        String docId = "doc-a";
        String inFileData = "{\"key\":\"value\"}";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ONE))).thenThrow(new ServiceNotAvailableException());
        setupMockBucket(bucket);
        testRunner.enqueue(inFileDataBytes);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docId);
        testRunner.setProperty(REPLICATE_TO, ONE.toString());
        try {
            testRunner.run();
            Assert.fail("ProcessException should be thrown.");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getClass().equals(ProcessException.class));
        }
        Mockito.verify(bucket, Mockito.times(1)).upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ONE));
        testRunner.assertAllFlowFilesTransferred(AbstractCouchbaseProcessor.REL_FAILURE);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
    }

    @Test
    public void testCouchbaseTempFlowFileError() throws Exception {
        String docId = "doc-a";
        String inFileData = "{\"key\":\"value\"}";
        byte[] inFileDataBytes = inFileData.getBytes(StandardCharsets.UTF_8);
        Bucket bucket = Mockito.mock(Bucket.class);
        CouchbaseException exception = new DurabilityException();
        Mockito.when(bucket.upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ONE))).thenThrow(exception);
        setupMockBucket(bucket);
        testRunner.enqueue(inFileDataBytes);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docId);
        testRunner.setProperty(REPLICATE_TO, ONE.toString());
        testRunner.run();
        Mockito.verify(bucket, Mockito.times(1)).upsert(ArgumentMatchers.any(RawJsonDocument.class), ArgumentMatchers.eq(NONE), ArgumentMatchers.eq(ONE));
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_RETRY).get(0);
        orgFile.assertContentEquals(inFileData);
        orgFile.assertAttributeEquals(key(), exception.getClass().getName());
    }
}

