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


import CouchbaseAttributes.Cas;
import CouchbaseAttributes.Cluster;
import CouchbaseAttributes.DocId;
import CouchbaseAttributes.Expiry;
import DocumentType.Binary;
import ProvenanceEventType.FETCH;
import com.couchbase.client.core.BackpressureException;
import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.core.ServiceNotAvailableException;
import com.couchbase.client.core.endpoint.kv.AuthenticationException;
import com.couchbase.client.core.state.NotConnectedException;
import com.couchbase.client.deps.io.netty.buffer.ByteBuf;
import com.couchbase.client.deps.io.netty.buffer.Unpooled;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.BinaryDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.couchbase.client.java.error.DurabilityException;
import com.couchbase.client.java.error.RequestTooBigException;
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
import org.mockito.Mockito;


public class TestGetCouchbaseKey {
    private static final String SERVICE_ID = "couchbaseClusterService";

    private TestRunner testRunner;

    @Test
    public void testStaticDocId() throws Exception {
        String bucketName = "bucket-1";
        String docId = "doc-a";
        Bucket bucket = Mockito.mock(Bucket.class);
        String content = "{\"key\":\"value\"}";
        int expiry = 100;
        long cas = 200L;
        Mockito.when(bucket.get(docId, RawJsonDocument.class)).thenReturn(RawJsonDocument.create(docId, expiry, content, cas));
        setupMockBucket(bucket);
        testRunner.setProperty(CouchbaseConfigurationProperties.BUCKET_NAME, bucketName);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docId);
        testRunner.enqueue(new byte[0]);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
        outFile.assertAttributeEquals(Cluster.key(), TestGetCouchbaseKey.SERVICE_ID);
        outFile.assertAttributeEquals(CouchbaseAttributes.Bucket.key(), bucketName);
        outFile.assertAttributeEquals(DocId.key(), docId);
        outFile.assertAttributeEquals(Cas.key(), String.valueOf(cas));
        outFile.assertAttributeEquals(Expiry.key(), String.valueOf(expiry));
    }

    @Test
    public void testDocIdExp() throws Exception {
        String docIdExp = "${'someProperty'}";
        String somePropertyValue = "doc-p";
        Bucket bucket = Mockito.mock(Bucket.class);
        String content = "{\"key\":\"value\"}";
        Mockito.when(bucket.get(somePropertyValue, RawJsonDocument.class)).thenReturn(RawJsonDocument.create(somePropertyValue, content));
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        byte[] inFileData = "input FlowFile data".getBytes(StandardCharsets.UTF_8);
        Map<String, String> properties = new HashMap<>();
        properties.put("someProperty", somePropertyValue);
        testRunner.enqueue(inFileData, properties);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
    }

    @Test
    public void testDocIdExpWithEmptyFlowFile() throws Exception {
        String docIdExp = "doc-s";
        String docId = "doc-s";
        Bucket bucket = Mockito.mock(Bucket.class);
        String content = "{\"key\":\"value\"}";
        Mockito.when(bucket.get(docId, RawJsonDocument.class)).thenReturn(RawJsonDocument.create(docId, content));
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        testRunner.enqueue(new byte[0]);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
    }

    @Test
    public void testDocIdExpWithInvalidExpression() throws Exception {
        String docIdExp = "${nonExistingFunction('doc-s')}";
        String docId = "doc-s";
        Bucket bucket = Mockito.mock(Bucket.class);
        String content = "{\"key\":\"value\"}";
        Mockito.when(bucket.get(docId, RawJsonDocument.class)).thenReturn(RawJsonDocument.create(docId, content));
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        testRunner.enqueue(new byte[0]);
        try {
            testRunner.run();
            Assert.fail("Exception should be thrown.");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getClass().equals(AttributeExpressionLanguageException.class));
        }
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
    }

    @Test
    public void testDocIdExpWithInvalidExpressionOnFlowFile() throws Exception {
        String docIdExp = "${nonExistingFunction(someProperty)}";
        Bucket bucket = Mockito.mock(Bucket.class);
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        String inputFileDataStr = "input FlowFile data";
        byte[] inFileData = inputFileDataStr.getBytes(StandardCharsets.UTF_8);
        Map<String, String> properties = new HashMap<>();
        properties.put("someProperty", "someValue");
        testRunner.enqueue(inFileData, properties);
        try {
            testRunner.run();
            Assert.fail("Exception should be thrown.");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getClass().equals(AttributeExpressionLanguageException.class));
        }
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
    }

    @Test
    public void testInputFlowFileContent() throws Exception {
        Bucket bucket = Mockito.mock(Bucket.class);
        String inFileDataStr = "doc-in";
        String content = "{\"key\":\"value\"}";
        Mockito.when(bucket.get(inFileDataStr, RawJsonDocument.class)).thenReturn(RawJsonDocument.create(inFileDataStr, content));
        setupMockBucket(bucket);
        byte[] inFileData = inFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_ORIGINAL).get(0);
        orgFile.assertContentEquals(inFileDataStr);
    }

    @Test
    public void testPutToAttribute() throws Exception {
        Bucket bucket = Mockito.mock(Bucket.class);
        String inFileDataStr = "doc-in";
        String content = "some-value";
        Mockito.when(bucket.get(inFileDataStr, RawJsonDocument.class)).thenReturn(RawJsonDocument.create(inFileDataStr, content));
        setupMockBucket(bucket);
        byte[] inFileData = inFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.setProperty(GetCouchbaseKey.PUT_VALUE_TO_ATTRIBUTE, "targetAttribute");
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        // Result is put to Attribute, so no need to pass it to original.
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(inFileDataStr);
        outFile.assertAttributeEquals("targetAttribute", content);
        Assert.assertEquals(1, testRunner.getProvenanceEvents().size());
        Assert.assertEquals(FETCH, testRunner.getProvenanceEvents().get(0).getEventType());
    }

    @Test
    public void testPutToAttributeNoTargetAttribute() throws Exception {
        Bucket bucket = Mockito.mock(Bucket.class);
        String inFileDataStr = "doc-in";
        String content = "some-value";
        Mockito.when(bucket.get(inFileDataStr, RawJsonDocument.class)).thenReturn(RawJsonDocument.create(inFileDataStr, content));
        setupMockBucket(bucket);
        byte[] inFileData = inFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.setProperty(GetCouchbaseKey.PUT_VALUE_TO_ATTRIBUTE, "${expressionReturningNoValue}");
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 1);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_FAILURE).get(0);
        outFile.assertContentEquals(inFileDataStr);
    }

    @Test
    public void testBinaryDocument() throws Exception {
        Bucket bucket = Mockito.mock(Bucket.class);
        String inFileDataStr = "doc-in";
        String content = "binary";
        ByteBuf buf = Unpooled.copiedBuffer(content.getBytes(StandardCharsets.UTF_8));
        Mockito.when(bucket.get(inFileDataStr, BinaryDocument.class)).thenReturn(BinaryDocument.create(inFileDataStr, buf));
        setupMockBucket(bucket);
        byte[] inFileData = inFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.setProperty(CouchbaseConfigurationProperties.DOCUMENT_TYPE, Binary.toString());
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_ORIGINAL).get(0);
        orgFile.assertContentEquals(inFileDataStr);
    }

    @Test
    public void testBinaryDocumentToAttribute() throws Exception {
        Bucket bucket = Mockito.mock(Bucket.class);
        String inFileDataStr = "doc-in";
        String content = "binary";
        ByteBuf buf = Unpooled.copiedBuffer(content.getBytes(StandardCharsets.UTF_8));
        Mockito.when(bucket.get(inFileDataStr, BinaryDocument.class)).thenReturn(BinaryDocument.create(inFileDataStr, buf));
        setupMockBucket(bucket);
        byte[] inFileData = inFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.setProperty(CouchbaseConfigurationProperties.DOCUMENT_TYPE, Binary.toString());
        testRunner.setProperty(GetCouchbaseKey.PUT_VALUE_TO_ATTRIBUTE, "targetAttribute");
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile outFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_SUCCESS).get(0);
        outFile.assertContentEquals(inFileDataStr);
        outFile.assertAttributeEquals("targetAttribute", "binary");
    }

    @Test
    public void testCouchbaseFailure() throws Exception {
        Bucket bucket = Mockito.mock(Bucket.class);
        String inFileDataStr = "doc-in";
        Mockito.when(bucket.get(inFileDataStr, RawJsonDocument.class)).thenThrow(new ServiceNotAvailableException());
        setupMockBucket(bucket);
        byte[] inFileData = inFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        try {
            testRunner.run();
            Assert.fail("ProcessException should be thrown.");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getClass().equals(ProcessException.class));
        }
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
    }

    @Test
    public void testCouchbaseConfigurationError() throws Exception {
        String docIdExp = "doc-c";
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.get(docIdExp, RawJsonDocument.class)).thenThrow(new AuthenticationException());
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        String inputFileDataStr = "input FlowFile data";
        byte[] inFileData = inputFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        try {
            testRunner.run();
            Assert.fail("ProcessException should be thrown.");
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getClass().equals(ProcessException.class));
            Assert.assertTrue(e.getCause().getCause().getClass().equals(AuthenticationException.class));
        }
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
    }

    @Test
    public void testCouchbaseInvalidInputError() throws Exception {
        String docIdExp = "doc-c";
        Bucket bucket = Mockito.mock(Bucket.class);
        CouchbaseException exception = new RequestTooBigException();
        Mockito.when(bucket.get(docIdExp, RawJsonDocument.class)).thenThrow(exception);
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        String inputFileDataStr = "input FlowFile data";
        byte[] inFileData = inputFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 1);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_FAILURE).get(0);
        orgFile.assertContentEquals(inputFileDataStr);
        orgFile.assertAttributeEquals(key(), exception.getClass().getName());
    }

    @Test
    public void testCouchbaseTempClusterError() throws Exception {
        String docIdExp = "doc-c";
        Bucket bucket = Mockito.mock(Bucket.class);
        CouchbaseException exception = new BackpressureException();
        Mockito.when(bucket.get(docIdExp, RawJsonDocument.class)).thenThrow(exception);
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        String inputFileDataStr = "input FlowFile data";
        byte[] inFileData = inputFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_RETRY).get(0);
        orgFile.assertContentEquals(inputFileDataStr);
        orgFile.assertAttributeEquals(key(), exception.getClass().getName());
    }

    @Test
    public void testCouchbaseTempFlowFileError() throws Exception {
        String docIdExp = "doc-c";
        Bucket bucket = Mockito.mock(Bucket.class);
        // There is no suitable CouchbaseException for temp flowfile error, currently.
        CouchbaseException exception = new DurabilityException();
        Mockito.when(bucket.get(docIdExp, RawJsonDocument.class)).thenThrow(exception);
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        String inputFileDataStr = "input FlowFile data";
        byte[] inFileData = inputFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_RETRY).get(0);
        orgFile.assertContentEquals(inputFileDataStr);
        orgFile.assertAttributeEquals(key(), exception.getClass().getName());
        Assert.assertEquals(true, orgFile.isPenalized());
    }

    @Test
    public void testCouchbaseFatalError() throws Exception {
        String docIdExp = "doc-c";
        Bucket bucket = Mockito.mock(Bucket.class);
        CouchbaseException exception = new NotConnectedException();
        Mockito.when(bucket.get(docIdExp, RawJsonDocument.class)).thenThrow(exception);
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        String inputFileDataStr = "input FlowFile data";
        byte[] inFileData = inputFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 1);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 0);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_RETRY).get(0);
        orgFile.assertContentEquals(inputFileDataStr);
        orgFile.assertAttributeEquals(key(), exception.getClass().getName());
    }

    @Test
    public void testDocumentNotFound() throws Exception {
        String docIdExp = "doc-n";
        Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.get(docIdExp, RawJsonDocument.class)).thenReturn(null);
        setupMockBucket(bucket);
        testRunner.setProperty(AbstractCouchbaseProcessor.DOC_ID, docIdExp);
        String inputFileDataStr = "input FlowFile data";
        byte[] inFileData = inputFileDataStr.getBytes(StandardCharsets.UTF_8);
        testRunner.enqueue(inFileData);
        testRunner.run();
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_SUCCESS, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_ORIGINAL, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_RETRY, 0);
        testRunner.assertTransferCount(AbstractCouchbaseProcessor.REL_FAILURE, 1);
        MockFlowFile orgFile = testRunner.getFlowFilesForRelationship(AbstractCouchbaseProcessor.REL_FAILURE).get(0);
        orgFile.assertContentEquals(inputFileDataStr);
        orgFile.assertAttributeEquals(key(), DocumentDoesNotExistException.class.getName());
    }
}

