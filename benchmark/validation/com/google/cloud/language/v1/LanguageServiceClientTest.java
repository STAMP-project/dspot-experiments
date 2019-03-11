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
package com.google.cloud.language.v1;


import AnnotateTextRequest.Features;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;

import static EncodingType.NONE;


@Generated("by GAPIC")
public class LanguageServiceClientTest {
    private static MockLanguageService mockLanguageService;

    private static MockServiceHelper serviceHelper;

    private LanguageServiceClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void analyzeSentimentTest() {
        String language = "language-1613589672";
        AnalyzeSentimentResponse expectedResponse = AnalyzeSentimentResponse.newBuilder().setLanguage(language).build();
        LanguageServiceClientTest.mockLanguageService.addResponse(expectedResponse);
        Document document = Document.newBuilder().build();
        AnalyzeSentimentResponse actualResponse = client.analyzeSentiment(document);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = LanguageServiceClientTest.mockLanguageService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AnalyzeSentimentRequest actualRequest = ((AnalyzeSentimentRequest) (actualRequests.get(0)));
        Assert.assertEquals(document, actualRequest.getDocument());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void analyzeSentimentExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LanguageServiceClientTest.mockLanguageService.addException(exception);
        try {
            Document document = Document.newBuilder().build();
            client.analyzeSentiment(document);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void analyzeEntitiesTest() {
        String language = "language-1613589672";
        AnalyzeEntitiesResponse expectedResponse = AnalyzeEntitiesResponse.newBuilder().setLanguage(language).build();
        LanguageServiceClientTest.mockLanguageService.addResponse(expectedResponse);
        Document document = Document.newBuilder().build();
        EncodingType encodingType = NONE;
        AnalyzeEntitiesResponse actualResponse = client.analyzeEntities(document, encodingType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = LanguageServiceClientTest.mockLanguageService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AnalyzeEntitiesRequest actualRequest = ((AnalyzeEntitiesRequest) (actualRequests.get(0)));
        Assert.assertEquals(document, actualRequest.getDocument());
        Assert.assertEquals(encodingType, actualRequest.getEncodingType());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void analyzeEntitiesExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LanguageServiceClientTest.mockLanguageService.addException(exception);
        try {
            Document document = Document.newBuilder().build();
            EncodingType encodingType = NONE;
            client.analyzeEntities(document, encodingType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void analyzeEntitySentimentTest() {
        String language = "language-1613589672";
        AnalyzeEntitySentimentResponse expectedResponse = AnalyzeEntitySentimentResponse.newBuilder().setLanguage(language).build();
        LanguageServiceClientTest.mockLanguageService.addResponse(expectedResponse);
        Document document = Document.newBuilder().build();
        EncodingType encodingType = NONE;
        AnalyzeEntitySentimentResponse actualResponse = client.analyzeEntitySentiment(document, encodingType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = LanguageServiceClientTest.mockLanguageService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AnalyzeEntitySentimentRequest actualRequest = ((AnalyzeEntitySentimentRequest) (actualRequests.get(0)));
        Assert.assertEquals(document, actualRequest.getDocument());
        Assert.assertEquals(encodingType, actualRequest.getEncodingType());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void analyzeEntitySentimentExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LanguageServiceClientTest.mockLanguageService.addException(exception);
        try {
            Document document = Document.newBuilder().build();
            EncodingType encodingType = NONE;
            client.analyzeEntitySentiment(document, encodingType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void analyzeSyntaxTest() {
        String language = "language-1613589672";
        AnalyzeSyntaxResponse expectedResponse = AnalyzeSyntaxResponse.newBuilder().setLanguage(language).build();
        LanguageServiceClientTest.mockLanguageService.addResponse(expectedResponse);
        Document document = Document.newBuilder().build();
        EncodingType encodingType = NONE;
        AnalyzeSyntaxResponse actualResponse = client.analyzeSyntax(document, encodingType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = LanguageServiceClientTest.mockLanguageService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AnalyzeSyntaxRequest actualRequest = ((AnalyzeSyntaxRequest) (actualRequests.get(0)));
        Assert.assertEquals(document, actualRequest.getDocument());
        Assert.assertEquals(encodingType, actualRequest.getEncodingType());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void analyzeSyntaxExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LanguageServiceClientTest.mockLanguageService.addException(exception);
        try {
            Document document = Document.newBuilder().build();
            EncodingType encodingType = NONE;
            client.analyzeSyntax(document, encodingType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void classifyTextTest() {
        ClassifyTextResponse expectedResponse = ClassifyTextResponse.newBuilder().build();
        LanguageServiceClientTest.mockLanguageService.addResponse(expectedResponse);
        Document document = Document.newBuilder().build();
        ClassifyTextResponse actualResponse = client.classifyText(document);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = LanguageServiceClientTest.mockLanguageService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        ClassifyTextRequest actualRequest = ((ClassifyTextRequest) (actualRequests.get(0)));
        Assert.assertEquals(document, actualRequest.getDocument());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void classifyTextExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LanguageServiceClientTest.mockLanguageService.addException(exception);
        try {
            Document document = Document.newBuilder().build();
            client.classifyText(document);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void annotateTextTest() {
        String language = "language-1613589672";
        AnnotateTextResponse expectedResponse = AnnotateTextResponse.newBuilder().setLanguage(language).build();
        LanguageServiceClientTest.mockLanguageService.addResponse(expectedResponse);
        Document document = Document.newBuilder().build();
        AnnotateTextRequest.Features features = Features.newBuilder().build();
        EncodingType encodingType = NONE;
        AnnotateTextResponse actualResponse = client.annotateText(document, features, encodingType);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = LanguageServiceClientTest.mockLanguageService.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        AnnotateTextRequest actualRequest = ((AnnotateTextRequest) (actualRequests.get(0)));
        Assert.assertEquals(document, actualRequest.getDocument());
        Assert.assertEquals(features, actualRequest.getFeatures());
        Assert.assertEquals(encodingType, actualRequest.getEncodingType());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void annotateTextExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        LanguageServiceClientTest.mockLanguageService.addException(exception);
        try {
            Document document = Document.newBuilder().build();
            AnnotateTextRequest.Features features = Features.newBuilder().build();
            EncodingType encodingType = NONE;
            client.annotateText(document, features, encodingType);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }
}

