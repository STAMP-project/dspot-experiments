/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.collector;


import SpanBytesEncoder.JSON_V2;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;
import zipkin2.Callback;
import zipkin2.Span;
import zipkin2.TestObjects;
import zipkin2.storage.StorageComponent;


public class CollectorTest {
    StorageComponent storage = Mockito.mock(StorageComponent.class);

    Callback callback = Mockito.mock(Callback.class);

    Collector collector;

    @Test
    public void unsampledSpansArentStored() {
        Mockito.when(storage.spanConsumer()).thenThrow(new AssertionError());
        collector = Collector.newBuilder(Collector.class).sampler(CollectorSampler.create(0.0F)).storage(storage).build();
        collector.accept(Arrays.asList(TestObjects.CLIENT_SPAN), callback);
    }

    @Test
    public void errorDetectingFormat() {
        CollectorMetrics metrics = Mockito.mock(CollectorMetrics.class);
        collector = Collector.newBuilder(Collector.class).metrics(metrics).storage(storage).build();
        collector.acceptSpans(new byte[]{ 'f', 'o', 'o' }, callback);
        Mockito.verify(metrics).incrementMessagesDropped();
    }

    @Test
    public void convertsSpan2Format() {
        byte[] bytes = JSON_V2.encodeList(Arrays.asList(TestObjects.CLIENT_SPAN));
        collector.acceptSpans(bytes, callback);
        Mockito.verify(collector).acceptSpans(bytes, SpanBytesDecoder.JSON_V2, callback);
        Mockito.verify(collector).accept(Arrays.asList(TestObjects.CLIENT_SPAN), callback);
    }

    @Test
    public void acceptSpansCallback_toStringIncludesSpanIds() {
        Span span2 = TestObjects.CLIENT_SPAN.toBuilder().id("3").build();
        Mockito.when(collector.idString(span2)).thenReturn("3");
        assertThat(collector.acceptSpansCallback(Arrays.asList(TestObjects.CLIENT_SPAN, span2))).hasToString("AcceptSpans([1, 3])");
    }

    @Test
    public void acceptSpansCallback_toStringIncludesSpanIds_noMoreThan3() {
        assertThat(collector.acceptSpansCallback(Arrays.asList(TestObjects.CLIENT_SPAN, TestObjects.CLIENT_SPAN, TestObjects.CLIENT_SPAN, TestObjects.CLIENT_SPAN))).hasToString("AcceptSpans([1, 1, 1, ...])");
    }

    @Test
    public void acceptSpansCallback_onErrorWithNullMessage() {
        Callback<Void> callback = collector.acceptSpansCallback(Arrays.asList(TestObjects.CLIENT_SPAN));
        RuntimeException exception = new RuntimeException();
        callback.onError(exception);
        Mockito.verify(collector).warn("Cannot store spans [1] due to RuntimeException()", exception);
    }

    @Test
    public void acceptSpansCallback_onErrorWithMessage() {
        Callback<Void> callback = collector.acceptSpansCallback(Arrays.asList(TestObjects.CLIENT_SPAN));
        RuntimeException exception = new IllegalArgumentException("no beer");
        callback.onError(exception);
        Mockito.verify(collector).warn("Cannot store spans [1] due to IllegalArgumentException(no beer)", exception);
    }

    @Test
    public void errorAcceptingSpans_onErrorWithNullMessage() {
        String message = collector.errorStoringSpans(Arrays.asList(TestObjects.CLIENT_SPAN), new RuntimeException()).getMessage();
        assertThat(message).isEqualTo("Cannot store spans [1] due to RuntimeException()");
    }

    @Test
    public void errorAcceptingSpans_onErrorWithMessage() {
        RuntimeException exception = new IllegalArgumentException("no beer");
        String message = collector.errorStoringSpans(Arrays.asList(TestObjects.CLIENT_SPAN), exception).getMessage();
        assertThat(message).isEqualTo("Cannot store spans [1] due to IllegalArgumentException(no beer)");
    }

    @Test
    public void errorDecoding_onErrorWithNullMessage() {
        String message = collector.errorReading(new RuntimeException()).getMessage();
        assertThat(message).isEqualTo("Cannot decode spans due to RuntimeException()");
    }

    @Test
    public void errorDecoding_onErrorWithMessage() {
        RuntimeException exception = new IllegalArgumentException("no beer");
        String message = collector.errorReading(exception).getMessage();
        assertThat(message).isEqualTo("Cannot decode spans due to IllegalArgumentException(no beer)");
    }

    @Test
    public void errorDecoding_doesntWrapMalformedException() {
        RuntimeException exception = new IllegalArgumentException("Malformed reading spans");
        String message = collector.errorReading(exception).getMessage();
        assertThat(message).isEqualTo("Malformed reading spans");
    }

    @Test
    public void errorDecoding_doesntWrapTruncatedException() {
        RuntimeException exception = new IllegalArgumentException("Truncated reading spans");
        String message = collector.errorReading(exception).getMessage();
        assertThat(message).isEqualTo("Truncated reading spans");
    }
}

