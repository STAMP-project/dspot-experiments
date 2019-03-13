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
package org.apache.nifi.processors.aws.sqs;


import DeleteSQS.QUEUE_URL;
import DeleteSQS.RECEIPT_HANDLE;
import DeleteSQS.REL_FAILURE;
import DeleteSQS.REL_SUCCESS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestDeleteSQS {
    private TestRunner runner = null;

    private DeleteSQS mockDeleteSQS = null;

    private AmazonSQSClient actualSQSClient = null;

    private AmazonSQSClient mockSQSClient = null;

    @Test
    public void testDeleteSingleMessage() {
        runner.setProperty(QUEUE_URL, "https://sqs.us-west-2.amazonaws.com/123456789012/test-queue-000000000");
        final Map<String, String> ffAttributes = new HashMap<>();
        ffAttributes.put("filename", "1.txt");
        ffAttributes.put("sqs.receipt.handle", "test-receipt-handle-1");
        runner.enqueue("TestMessageBody", ffAttributes);
        runner.assertValid();
        runner.run(1);
        ArgumentCaptor<DeleteMessageBatchRequest> captureDeleteRequest = ArgumentCaptor.forClass(DeleteMessageBatchRequest.class);
        Mockito.verify(mockSQSClient, Mockito.times(1)).deleteMessageBatch(captureDeleteRequest.capture());
        DeleteMessageBatchRequest deleteRequest = captureDeleteRequest.getValue();
        Assert.assertEquals("https://sqs.us-west-2.amazonaws.com/123456789012/test-queue-000000000", deleteRequest.getQueueUrl());
        Assert.assertEquals("test-receipt-handle-1", deleteRequest.getEntries().get(0).getReceiptHandle());
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testDeleteWithCustomReceiptHandle() {
        runner.setProperty(QUEUE_URL, "https://sqs.us-west-2.amazonaws.com/123456789012/test-queue-000000000");
        runner.setProperty(RECEIPT_HANDLE, "${custom.receipt.handle}");
        final Map<String, String> ffAttributes = new HashMap<>();
        ffAttributes.put("filename", "1.txt");
        ffAttributes.put("custom.receipt.handle", "test-receipt-handle-1");
        runner.enqueue("TestMessageBody", ffAttributes);
        runner.assertValid();
        runner.run(1);
        ArgumentCaptor<DeleteMessageBatchRequest> captureDeleteRequest = ArgumentCaptor.forClass(DeleteMessageBatchRequest.class);
        Mockito.verify(mockSQSClient, Mockito.times(1)).deleteMessageBatch(captureDeleteRequest.capture());
        DeleteMessageBatchRequest deleteRequest = captureDeleteRequest.getValue();
        Assert.assertEquals("test-receipt-handle-1", deleteRequest.getEntries().get(0).getReceiptHandle());
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testDeleteException() {
        runner.setProperty(QUEUE_URL, "https://sqs.us-west-2.amazonaws.com/123456789012/test-queue-000000000");
        final Map<String, String> ff1Attributes = new HashMap<>();
        ff1Attributes.put("filename", "1.txt");
        ff1Attributes.put("sqs.receipt.handle", "test-receipt-handle-1");
        runner.enqueue("TestMessageBody1", ff1Attributes);
        Mockito.when(mockSQSClient.deleteMessageBatch(Mockito.any(DeleteMessageBatchRequest.class))).thenThrow(new AmazonSQSException("TestFail"));
        runner.assertValid();
        runner.run(1);
        ArgumentCaptor<DeleteMessageBatchRequest> captureDeleteRequest = ArgumentCaptor.forClass(DeleteMessageBatchRequest.class);
        Mockito.verify(mockSQSClient, Mockito.times(1)).deleteMessageBatch(captureDeleteRequest.capture());
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }
}

