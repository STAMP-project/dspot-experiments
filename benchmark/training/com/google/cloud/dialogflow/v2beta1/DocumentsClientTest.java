/**
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.dialogflow.v2beta1;


import StatusCode.Code.INVALID_ARGUMENT;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.common.collect.Lists;
import com.google.longrunning.Operation;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class DocumentsClientTest {
    private static MockAgents mockAgents;

    private static MockContexts mockContexts;

    private static MockDocuments mockDocuments;

    private static MockEntityTypes mockEntityTypes;

    private static MockIntents mockIntents;

    private static MockKnowledgeBases mockKnowledgeBases;

    private static MockSessionEntityTypes mockSessionEntityTypes;

    private static MockSessions mockSessions;

    private static MockServiceHelper serviceHelper;

    private DocumentsClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void listDocumentsTest() {
        String nextPageToken = "";
        Document documentsElement = Document.newBuilder().build();
        List<Document> documents = Arrays.asList(documentsElement);
        ListDocumentsResponse expectedResponse = ListDocumentsResponse.newBuilder().setNextPageToken(nextPageToken).addAllDocuments(documents).build();
        DocumentsClientTest.mockDocuments.addResponse(expectedResponse);
        KnowledgeBaseName parent = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
        DocumentsClient.ListDocumentsPagedResponse pagedListResponse = client.listDocuments(parent);
        List<Document> resources = Lists.newArrayList(pagedListResponse.iterateAll());
        Assert.assertEquals(1, resources.size());
        Assert.assertEquals(expectedResponse.getDocumentsList().get(0), resources.get(0));
        List<GeneratedMessageV3> actualRequests = DocumentsClientTest.mockDocuments.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ListDocumentsRequest actualRequest = ((ListDocumentsRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, KnowledgeBaseName.parse(actualRequest.getParent()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void listDocumentsExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DocumentsClientTest.mockDocuments.addException(exception);
        try {
            KnowledgeBaseName parent = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
            client.listDocuments(parent);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void getDocumentTest() {
        DocumentName name2 = DocumentName.of("[PROJECT]", "[KNOWLEDGE_BASE]", "[DOCUMENT]");
        String displayName = "displayName1615086568";
        String mimeType = "mimeType-196041627";
        String contentUri = "contentUri-388807514";
        Document expectedResponse = Document.newBuilder().setName(name2.toString()).setDisplayName(displayName).setMimeType(mimeType).setContentUri(contentUri).build();
        DocumentsClientTest.mockDocuments.addResponse(expectedResponse);
        DocumentName name = DocumentName.of("[PROJECT]", "[KNOWLEDGE_BASE]", "[DOCUMENT]");
        Document actualResponse = client.getDocument(name);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DocumentsClientTest.mockDocuments.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        GetDocumentRequest actualRequest = ((GetDocumentRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, DocumentName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void getDocumentExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DocumentsClientTest.mockDocuments.addException(exception);
        try {
            DocumentName name = DocumentName.of("[PROJECT]", "[KNOWLEDGE_BASE]", "[DOCUMENT]");
            client.getDocument(name);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void createDocumentTest() throws Exception {
        DocumentName name = DocumentName.of("[PROJECT]", "[KNOWLEDGE_BASE]", "[DOCUMENT]");
        String displayName = "displayName1615086568";
        String mimeType = "mimeType-196041627";
        String contentUri = "contentUri-388807514";
        Document expectedResponse = Document.newBuilder().setName(name.toString()).setDisplayName(displayName).setMimeType(mimeType).setContentUri(contentUri).build();
        Operation resultOperation = Operation.newBuilder().setName("createDocumentTest").setDone(true).setResponse(Any.pack(expectedResponse)).build();
        DocumentsClientTest.mockDocuments.addResponse(resultOperation);
        KnowledgeBaseName parent = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
        Document document = Document.newBuilder().build();
        Document actualResponse = client.createDocumentAsync(parent, document).get();
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DocumentsClientTest.mockDocuments.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        CreateDocumentRequest actualRequest = ((CreateDocumentRequest) (actualRequests.get(0)));
        Assert.assertEquals(parent, KnowledgeBaseName.parse(actualRequest.getParent()));
        Assert.assertEquals(document, actualRequest.getDocument());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void createDocumentExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DocumentsClientTest.mockDocuments.addException(exception);
        try {
            KnowledgeBaseName parent = KnowledgeBaseName.of("[PROJECT]", "[KNOWLEDGE_BASE]");
            Document document = Document.newBuilder().build();
            client.createDocumentAsync(parent, document).get();
            Assert.fail("No exception raised");
        } catch (ExecutionException e) {
            Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void deleteDocumentTest() throws Exception {
        Empty expectedResponse = Empty.newBuilder().build();
        Operation resultOperation = Operation.newBuilder().setName("deleteDocumentTest").setDone(true).setResponse(Any.pack(expectedResponse)).build();
        DocumentsClientTest.mockDocuments.addResponse(resultOperation);
        DocumentName name = DocumentName.of("[PROJECT]", "[KNOWLEDGE_BASE]", "[DOCUMENT]");
        Empty actualResponse = client.deleteDocumentAsync(name).get();
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = DocumentsClientTest.mockDocuments.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        DeleteDocumentRequest actualRequest = ((DeleteDocumentRequest) (actualRequests.get(0)));
        Assert.assertEquals(name, DocumentName.parse(actualRequest.getName()));
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteDocumentExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        DocumentsClientTest.mockDocuments.addException(exception);
        try {
            DocumentName name = DocumentName.of("[PROJECT]", "[KNOWLEDGE_BASE]", "[DOCUMENT]");
            client.deleteDocumentAsync(name).get();
            Assert.fail("No exception raised");
        } catch (ExecutionException e) {
            Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }
}

