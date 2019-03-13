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
package org.apache.hadoop.fs.azure;


import BlobOperationDescriptor.OperationType;
import BlobOperationDescriptor.OperationType.AppendBlock;
import BlobOperationDescriptor.OperationType.CreateBlob;
import BlobOperationDescriptor.OperationType.GetBlob;
import BlobOperationDescriptor.OperationType.GetProperties;
import BlobOperationDescriptor.OperationType.PutBlock;
import BlobOperationDescriptor.OperationType.PutBlockList;
import BlobOperationDescriptor.OperationType.PutPage;
import InterfaceAudience.Private;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.ResponseReceivedEvent;
import com.microsoft.azure.storage.SendingRequestEvent;
import com.microsoft.azure.storage.StorageEvent;
import com.microsoft.azure.storage.blob.BlobInputStream;
import com.microsoft.azure.storage.blob.BlobOutputStream;
import com.microsoft.azure.storage.blob.CloudAppendBlob;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.CloudPageBlob;
import org.apache.hadoop.classification.InterfaceAudience;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for <code>BlobOperationDescriptor</code>.
 */
public class TestBlobOperationDescriptor extends AbstractWasbTestBase {
    private OperationType lastOperationTypeReceived;

    private OperationType lastOperationTypeSent;

    private long lastContentLengthReceived;

    @Test
    public void testAppendBlockOperations() throws Exception {
        CloudBlobContainer container = getTestAccount().getRealContainer();
        OperationContext context = new OperationContext();
        context.getResponseReceivedEventHandler().addListener(new TestBlobOperationDescriptor.ResponseReceivedEventHandler());
        context.getSendingRequestEventHandler().addListener(new TestBlobOperationDescriptor.SendingRequestEventHandler());
        CloudAppendBlob appendBlob = container.getAppendBlobReference("testAppendBlockOperations");
        Assert.assertNull(lastOperationTypeSent);
        Assert.assertNull(lastOperationTypeReceived);
        Assert.assertEquals(0, lastContentLengthReceived);
        try (BlobOutputStream output = appendBlob.openWriteNew(null, null, context)) {
            Assert.assertEquals(CreateBlob, lastOperationTypeReceived);
            Assert.assertEquals(0, lastContentLengthReceived);
            String message = "this is a test";
            output.write(message.getBytes("UTF-8"));
            output.flush();
            Assert.assertEquals(AppendBlock, lastOperationTypeSent);
            Assert.assertEquals(AppendBlock, lastOperationTypeReceived);
            Assert.assertEquals(message.length(), lastContentLengthReceived);
        }
    }

    @Test
    public void testPutBlockOperations() throws Exception {
        CloudBlobContainer container = getTestAccount().getRealContainer();
        OperationContext context = new OperationContext();
        context.getResponseReceivedEventHandler().addListener(new TestBlobOperationDescriptor.ResponseReceivedEventHandler());
        context.getSendingRequestEventHandler().addListener(new TestBlobOperationDescriptor.SendingRequestEventHandler());
        CloudBlockBlob blockBlob = container.getBlockBlobReference("testPutBlockOperations");
        Assert.assertNull(lastOperationTypeSent);
        Assert.assertNull(lastOperationTypeReceived);
        Assert.assertEquals(0, lastContentLengthReceived);
        try (BlobOutputStream output = blockBlob.openOutputStream(null, null, context)) {
            Assert.assertNull(lastOperationTypeReceived);
            Assert.assertEquals(0, lastContentLengthReceived);
            String message = "this is a test";
            output.write(message.getBytes("UTF-8"));
            output.flush();
            Assert.assertEquals(PutBlock, lastOperationTypeSent);
            Assert.assertEquals(PutBlock, lastOperationTypeReceived);
            Assert.assertEquals(message.length(), lastContentLengthReceived);
        }
        Assert.assertEquals(PutBlockList, lastOperationTypeSent);
        Assert.assertEquals(PutBlockList, lastOperationTypeReceived);
        Assert.assertEquals(0, lastContentLengthReceived);
    }

    @Test
    public void testPutPageOperations() throws Exception {
        CloudBlobContainer container = getTestAccount().getRealContainer();
        OperationContext context = new OperationContext();
        context.getResponseReceivedEventHandler().addListener(new TestBlobOperationDescriptor.ResponseReceivedEventHandler());
        context.getSendingRequestEventHandler().addListener(new TestBlobOperationDescriptor.SendingRequestEventHandler());
        CloudPageBlob pageBlob = container.getPageBlobReference("testPutPageOperations");
        Assert.assertNull(lastOperationTypeSent);
        Assert.assertNull(lastOperationTypeReceived);
        Assert.assertEquals(0, lastContentLengthReceived);
        try (BlobOutputStream output = pageBlob.openWriteNew(1024, null, null, context)) {
            Assert.assertEquals(CreateBlob, lastOperationTypeReceived);
            Assert.assertEquals(0, lastContentLengthReceived);
            final int pageSize = 512;
            byte[] buffer = new byte[pageSize];
            output.write(buffer);
            output.flush();
            Assert.assertEquals(PutPage, lastOperationTypeSent);
            Assert.assertEquals(PutPage, lastOperationTypeReceived);
            Assert.assertEquals(buffer.length, lastContentLengthReceived);
        }
    }

    @Test
    public void testGetBlobOperations() throws Exception {
        CloudBlobContainer container = getTestAccount().getRealContainer();
        OperationContext context = new OperationContext();
        context.getResponseReceivedEventHandler().addListener(new TestBlobOperationDescriptor.ResponseReceivedEventHandler());
        context.getSendingRequestEventHandler().addListener(new TestBlobOperationDescriptor.SendingRequestEventHandler());
        CloudBlockBlob blockBlob = container.getBlockBlobReference("testGetBlobOperations");
        Assert.assertNull(lastOperationTypeSent);
        Assert.assertNull(lastOperationTypeReceived);
        Assert.assertEquals(0, lastContentLengthReceived);
        String message = "this is a test";
        try (BlobOutputStream output = blockBlob.openOutputStream(null, null, context)) {
            Assert.assertNull(lastOperationTypeReceived);
            Assert.assertEquals(0, lastContentLengthReceived);
            output.write(message.getBytes("UTF-8"));
            output.flush();
            Assert.assertEquals(PutBlock, lastOperationTypeSent);
            Assert.assertEquals(PutBlock, lastOperationTypeReceived);
            Assert.assertEquals(message.length(), lastContentLengthReceived);
        }
        Assert.assertEquals(PutBlockList, lastOperationTypeSent);
        Assert.assertEquals(PutBlockList, lastOperationTypeReceived);
        Assert.assertEquals(0, lastContentLengthReceived);
        try (BlobInputStream input = blockBlob.openInputStream(null, null, context)) {
            Assert.assertEquals(GetProperties, lastOperationTypeSent);
            Assert.assertEquals(GetProperties, lastOperationTypeReceived);
            Assert.assertEquals(0, lastContentLengthReceived);
            byte[] buffer = new byte[1024];
            int numBytesRead = input.read(buffer);
            Assert.assertEquals(GetBlob, lastOperationTypeSent);
            Assert.assertEquals(GetBlob, lastOperationTypeReceived);
            Assert.assertEquals(message.length(), lastContentLengthReceived);
            Assert.assertEquals(numBytesRead, lastContentLengthReceived);
        }
    }

    /**
     * The ResponseReceivedEvent is fired after the Azure Storage SDK receives a
     * response.
     */
    @InterfaceAudience.Private
    class ResponseReceivedEventHandler extends StorageEvent<ResponseReceivedEvent> {
        /**
         * Called after the Azure Storage SDK receives a response.
         *
         * @param event
         * 		The connection, operation, and request state.
         */
        @Override
        public void eventOccurred(ResponseReceivedEvent event) {
            responseReceived(event);
        }
    }

    /**
     * The SendingRequestEvent is fired before the Azure Storage SDK sends a
     * request.
     */
    @InterfaceAudience.Private
    class SendingRequestEventHandler extends StorageEvent<SendingRequestEvent> {
        /**
         * Called before the Azure Storage SDK sends a request.
         *
         * @param event
         * 		The connection, operation, and request state.
         */
        @Override
        public void eventOccurred(SendingRequestEvent event) {
            sendingRequest(event);
        }
    }
}

