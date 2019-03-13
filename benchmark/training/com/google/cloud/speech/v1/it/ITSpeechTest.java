/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.speech.v1.it;


import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.common.io.Resources;
import com.google.common.truth.Truth;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITSpeechTest {
    private static SpeechClient speechClient;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void syncRecognize() {
        RecognizeResponse response = ITSpeechTest.speechClient.recognize(config(), audio());
        Truth.assertThat(response.getResultsCount()).isGreaterThan(0);
        Truth.assertThat(response.getResults(0).getAlternativesCount()).isGreaterThan(0);
        String text = response.getResults(0).getAlternatives(0).getTranscript();
        Truth.assertThat(text).isEqualTo("hello");
    }

    @Test
    public void longrunningRecognize() throws Exception {
        LongRunningRecognizeResponse response = ITSpeechTest.speechClient.longRunningRecognizeAsync(config(), audio()).get();
        Truth.assertThat(response.getResultsCount()).isGreaterThan(0);
        Truth.assertThat(response.getResults(0).getAlternativesCount()).isGreaterThan(0);
        String text = response.getResults(0).getAlternatives(0).getTranscript();
        Truth.assertThat(text).isEqualTo("hello");
    }

    @Test
    public void streamingRecognize() throws Exception {
        byte[] audioBytes = Resources.toByteArray(new URL("https://storage.googleapis.com/gapic-toolkit/hello.flac"));
        StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig.newBuilder().setConfig(config()).build();
        ITSpeechTest.ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver = new ITSpeechTest.ResponseApiStreamingObserver<>();
        ApiStreamObserver<StreamingRecognizeRequest> requestObserver = ITSpeechTest.speechClient.streamingRecognizeCallable().bidiStreamingCall(responseObserver);
        // The first request must **only** contain the audio configuration:
        requestObserver.onNext(StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build());
        // Subsequent requests must **only** contain the audio data.
        requestObserver.onNext(StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(audioBytes)).build());
        // Mark transmission as completed after sending the data.
        requestObserver.onCompleted();
        List<StreamingRecognizeResponse> responses = responseObserver.future().get();
        Truth.assertThat(responses.size()).isGreaterThan(0);
        Truth.assertThat(responses.get(0).getResultsCount()).isGreaterThan(0);
        Truth.assertThat(responses.get(0).getResults(0).getAlternativesCount()).isGreaterThan(0);
        String text = responses.get(0).getResults(0).getAlternatives(0).getTranscript();
        Truth.assertThat(text).isEqualTo("hello");
    }

    private static class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {
        private final SettableFuture<List<T>> future = SettableFuture.create();

        private final List<T> messages = new ArrayList<T>();

        @Override
        public void onNext(T message) {
            messages.add(message);
        }

        @Override
        public void onError(Throwable t) {
            future.setException(t);
        }

        @Override
        public void onCompleted() {
            future.set(messages);
        }

        // Returns the SettableFuture object to get received messages / exceptions.
        public SettableFuture<List<T>> future() {
            return future;
        }
    }
}

