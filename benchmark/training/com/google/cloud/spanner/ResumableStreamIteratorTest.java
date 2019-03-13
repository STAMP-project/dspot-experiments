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
package com.google.cloud.spanner;


import DoNotConstructDirectly.ALLOWED;
import ErrorCode.FAILED_PRECONDITION;
import ErrorCode.UNAVAILABLE;
import SpannerImpl.ResumableStreamIterator;
import com.google.common.collect.AbstractIterator;
import com.google.protobuf.ByteString;
import com.google.spanner.v1.PartialResultSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static ErrorCode.FAILED_PRECONDITION;
import static ErrorCode.UNAVAILABLE;


/**
 * Unit tests for {@link SpannerImpl.ResumableStreamIterator}.
 */
@RunWith(JUnit4.class)
public class ResumableStreamIteratorTest {
    interface Starter {
        SpannerImpl.CloseableIterator<PartialResultSet> startStream(@Nullable
        ByteString resumeToken) {
        }
    }

    interface ResultSetStream {
        PartialResultSet next();

        void close();
    }

    static class RetryableException extends SpannerException {
        RetryableException(ErrorCode code, @Nullable
        String message) {
            // OK to instantiate SpannerException directly for this unit test.
            super(ALLOWED, code, true, message, null);
        }
    }

    static class NonRetryableException extends SpannerException {
        NonRetryableException(ErrorCode code, @Nullable
        String message) {
            super(ALLOWED, code, false, message, null);
        }
    }

    static class ResultSetIterator extends AbstractIterator<PartialResultSet> implements SpannerImpl.CloseableIterator<PartialResultSet> {
        final ResumableStreamIteratorTest.ResultSetStream stream;

        ResultSetIterator(ResumableStreamIteratorTest.ResultSetStream stream) {
            this.stream = stream;
        }

        @Override
        protected PartialResultSet computeNext() {
            PartialResultSet next = stream.next();
            if (next == null) {
                endOfData();
            }
            return next;
        }

        @Override
        public void close(@Nullable
        String message) {
            stream.close();
        }
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    ResumableStreamIteratorTest.Starter starter = Mockito.mock(ResumableStreamIteratorTest.Starter.class);

    ResumableStreamIterator iterator;

    @Test
    public void simple() {
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(null, "a")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "b")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b").inOrder();
    }

    @Test
    public void simpleWithRestartTokens() {
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r2"), "b")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b").inOrder();
    }

    @Test
    public void restart() {
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r2"), "b")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        ResumableStreamIteratorTest.ResultSetStream s2 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(starter.startStream(ByteString.copyFromUtf8("r2"))).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s2));
        Mockito.when(s2.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r3"), "c")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r4"), "d")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b", "c", "d").inOrder();
    }

    @Test
    public void restartWithHoldBack() {
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r2"), "b")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "X")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "X")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        ResumableStreamIteratorTest.ResultSetStream s2 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(starter.startStream(ByteString.copyFromUtf8("r2"))).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s2));
        Mockito.when(s2.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r3"), "c")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r4"), "d")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b", "c", "d").inOrder();
    }

    @Test
    public void restartWithHoldBackMidStream() {
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "b")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "c")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r2"), "d")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        ResumableStreamIteratorTest.ResultSetStream s2 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(starter.startStream(ByteString.copyFromUtf8("r2"))).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s2));
        Mockito.when(s2.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r3"), "e")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "f")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b", "c", "d", "e", "f").inOrder();
    }

    @Test
    public void nonRetryableError() {
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r2"), "b")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "X")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "X")).thenThrow(new ResumableStreamIteratorTest.NonRetryableException(FAILED_PRECONDITION, "failed by test"));
        Iterator<String> strings = ResumableStreamIteratorTest.stringIterator(iterator);
        assertThat(strings.next()).isEqualTo("a");
        assertThat(strings.next()).isEqualTo("b");
        expectedException.expect(SpannerMatchers.isSpannerException(FAILED_PRECONDITION));
        assertThat(strings.next()).isNotEqualTo("X");
    }

    @Test
    public void bufferLimitSimple() {
        initWithLimit(1);
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(null, "a")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "b")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b").inOrder();
    }

    @Test
    public void bufferLimitSimpleWithRestartTokens() {
        initWithLimit(1);
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r2"), "b")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b").inOrder();
    }

    @Test
    public void bufferLimitRestart() {
        initWithLimit(1);
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r2"), "b")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        ResumableStreamIteratorTest.ResultSetStream s2 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(starter.startStream(ByteString.copyFromUtf8("r2"))).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s2));
        Mockito.when(s2.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r3"), "c")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r4"), "d")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b", "c", "d").inOrder();
    }

    @Test
    public void bufferLimitRestartWithinLimitAtStartOfResults() {
        initWithLimit(1);
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(null, "XXXXXX")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        ResumableStreamIteratorTest.ResultSetStream s2 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s2));
        Mockito.when(s2.next()).thenReturn(ResumableStreamIteratorTest.resultSet(null, "a")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "b")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b").inOrder();
    }

    @Test
    public void bufferLimitRestartWithinLimitMidResults() {
        initWithLimit(1);
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "XXXXXX")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        ResumableStreamIteratorTest.ResultSetStream s2 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(starter.startStream(ByteString.copyFromUtf8("r1"))).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s2));
        Mockito.when(s2.next()).thenReturn(ResumableStreamIteratorTest.resultSet(null, "b")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "c")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b", "c").inOrder();
    }

    @Test
    public void bufferLimitMissingTokensUnsafeToRetry() {
        initWithLimit(1);
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "b")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "c")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        assertThat(ResumableStreamIteratorTest.consumeAtMost(3, iterator)).containsExactly("a", "b", "c").inOrder();
        expectedException.expect(SpannerMatchers.isSpannerException(UNAVAILABLE));
        iterator.next();
    }

    @Test
    public void bufferLimitMissingTokensSafeToRetry() {
        initWithLimit(1);
        ResumableStreamIteratorTest.ResultSetStream s1 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(startStream(null)).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s1));
        Mockito.when(s1.next()).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r1"), "a")).thenReturn(ResumableStreamIteratorTest.resultSet(null, "b")).thenReturn(ResumableStreamIteratorTest.resultSet(ByteString.copyFromUtf8("r3"), "c")).thenThrow(new ResumableStreamIteratorTest.RetryableException(UNAVAILABLE, "failed by test"));
        ResumableStreamIteratorTest.ResultSetStream s2 = Mockito.mock(ResumableStreamIteratorTest.ResultSetStream.class);
        Mockito.when(starter.startStream(ByteString.copyFromUtf8("r3"))).thenReturn(new ResumableStreamIteratorTest.ResultSetIterator(s2));
        Mockito.when(s2.next()).thenReturn(ResumableStreamIteratorTest.resultSet(null, "d")).thenReturn(null);
        assertThat(ResumableStreamIteratorTest.consume(iterator)).containsExactly("a", "b", "c", "d").inOrder();
    }
}

