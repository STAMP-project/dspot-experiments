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
package com.google.cloud.speech.v1p1beta1;


import RecognitionConfig.AudioEncoding;
import StatusCode.Code.INVALID_ARGUMENT;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.grpc.testing.MockStreamObserver;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.longrunning.Operation;
import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.Assert;
import org.junit.Test;


@Generated("by GAPIC")
public class SpeechClientTest {
    private static MockSpeech mockSpeech;

    private static MockServiceHelper serviceHelper;

    private SpeechClient client;

    private LocalChannelProvider channelProvider;

    @Test
    @SuppressWarnings("all")
    public void recognizeTest() {
        RecognizeResponse expectedResponse = RecognizeResponse.newBuilder().build();
        SpeechClientTest.mockSpeech.addResponse(expectedResponse);
        RecognitionConfig.AudioEncoding encoding = AudioEncoding.FLAC;
        int sampleRateHertz = 44100;
        String languageCode = "en-US";
        RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding).setSampleRateHertz(sampleRateHertz).setLanguageCode(languageCode).build();
        String uri = "gs://bucket_name/file_name.flac";
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(uri).build();
        RecognizeResponse actualResponse = client.recognize(config, audio);
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = SpeechClientTest.mockSpeech.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        RecognizeRequest actualRequest = ((RecognizeRequest) (actualRequests.get(0)));
        Assert.assertEquals(config, actualRequest.getConfig());
        Assert.assertEquals(audio, actualRequest.getAudio());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void recognizeExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        SpeechClientTest.mockSpeech.addException(exception);
        try {
            RecognitionConfig.AudioEncoding encoding = AudioEncoding.FLAC;
            int sampleRateHertz = 44100;
            String languageCode = "en-US";
            RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding).setSampleRateHertz(sampleRateHertz).setLanguageCode(languageCode).build();
            String uri = "gs://bucket_name/file_name.flac";
            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(uri).build();
            client.recognize(config, audio);
            Assert.fail("No exception raised");
        } catch (InvalidArgumentException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("all")
    public void longRunningRecognizeTest() throws Exception {
        LongRunningRecognizeResponse expectedResponse = LongRunningRecognizeResponse.newBuilder().build();
        Operation resultOperation = Operation.newBuilder().setName("longRunningRecognizeTest").setDone(true).setResponse(Any.pack(expectedResponse)).build();
        SpeechClientTest.mockSpeech.addResponse(resultOperation);
        RecognitionConfig.AudioEncoding encoding = AudioEncoding.FLAC;
        int sampleRateHertz = 44100;
        String languageCode = "en-US";
        RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding).setSampleRateHertz(sampleRateHertz).setLanguageCode(languageCode).build();
        String uri = "gs://bucket_name/file_name.flac";
        RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(uri).build();
        LongRunningRecognizeResponse actualResponse = client.longRunningRecognizeAsync(config, audio).get();
        Assert.assertEquals(expectedResponse, actualResponse);
        List<GeneratedMessageV3> actualRequests = SpeechClientTest.mockSpeech.getRequests();
        Assert.assertEquals(1, actualRequests.size());
        LongRunningRecognizeRequest actualRequest = ((LongRunningRecognizeRequest) (actualRequests.get(0)));
        Assert.assertEquals(config, actualRequest.getConfig());
        Assert.assertEquals(audio, actualRequest.getAudio());
        Assert.assertTrue(channelProvider.isHeaderSent(ApiClientHeaderProvider.getDefaultApiClientHeaderKey(), GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
    }

    @Test
    @SuppressWarnings("all")
    public void longRunningRecognizeExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        SpeechClientTest.mockSpeech.addException(exception);
        try {
            RecognitionConfig.AudioEncoding encoding = AudioEncoding.FLAC;
            int sampleRateHertz = 44100;
            String languageCode = "en-US";
            RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding).setSampleRateHertz(sampleRateHertz).setLanguageCode(languageCode).build();
            String uri = "gs://bucket_name/file_name.flac";
            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(uri).build();
            client.longRunningRecognizeAsync(config, audio).get();
            Assert.fail("No exception raised");
        } catch (ExecutionException e) {
            Assert.assertEquals(InvalidArgumentException.class, e.getCause().getClass());
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }

    @Test
    @SuppressWarnings("all")
    public void streamingRecognizeTest() throws Exception {
        StreamingRecognizeResponse expectedResponse = StreamingRecognizeResponse.newBuilder().build();
        SpeechClientTest.mockSpeech.addResponse(expectedResponse);
        StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder().build();
        MockStreamObserver<StreamingRecognizeResponse> responseObserver = new MockStreamObserver();
        BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable = client.streamingRecognizeCallable();
        ApiStreamObserver<StreamingRecognizeRequest> requestObserver = callable.bidiStreamingCall(responseObserver);
        requestObserver.onNext(request);
        requestObserver.onCompleted();
        List<StreamingRecognizeResponse> actualResponses = responseObserver.future().get();
        Assert.assertEquals(1, actualResponses.size());
        Assert.assertEquals(expectedResponse, actualResponses.get(0));
    }

    @Test
    @SuppressWarnings("all")
    public void streamingRecognizeExceptionTest() throws Exception {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
        SpeechClientTest.mockSpeech.addException(exception);
        StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder().build();
        MockStreamObserver<StreamingRecognizeResponse> responseObserver = new MockStreamObserver();
        BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable = client.streamingRecognizeCallable();
        ApiStreamObserver<StreamingRecognizeRequest> requestObserver = callable.bidiStreamingCall(responseObserver);
        requestObserver.onNext(request);
        try {
            List<StreamingRecognizeResponse> actualResponses = responseObserver.future().get();
            Assert.fail("No exception thrown");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof InvalidArgumentException));
            InvalidArgumentException apiException = ((InvalidArgumentException) (e.getCause()));
            Assert.assertEquals(INVALID_ARGUMENT, apiException.getStatusCode().getCode());
        }
    }
}

