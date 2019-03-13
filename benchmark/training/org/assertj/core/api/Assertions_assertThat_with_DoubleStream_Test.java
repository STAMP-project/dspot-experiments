/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api;


import java.util.List;
import java.util.stream.DoubleStream;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Assertions_assertThat_with_DoubleStream_Test {
    private DoubleStream intStream = DoubleStream.empty();

    @Test
    public void should_create_Assert() {
        Object assertions = Assertions.assertThat(DoubleStream.of(823952.8, 1.9472305859E9));
        Assertions.assertThat(assertions).isNotNull();
    }

    @Test
    public void should_assert_on_size() {
        Assertions.assertThat(DoubleStream.empty()).isEmpty();
        Assertions.assertThat(DoubleStream.of(123.3, 5674.5, 363.4)).isNotEmpty().hasSize(3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_initialise_actual() {
        DoubleStream iterator = DoubleStream.of(823952.8, 1.9472305859E9);
        List<? extends Double> actual = Assertions.assertThat(iterator).actual;
        Assertions.assertThat(((List<Double>) (actual))).contains(823952.8, Assertions.atIndex(0)).contains(1.9472305859E9, Assertions.atIndex(1));
    }

    @Test
    public void should_allow_null() {
        Assertions.assertThat(Assertions.assertThat(((DoubleStream) (null))).actual).isNull();
    }

    @Test
    public void isEqualTo_should_honor_comparing_the_same_mocked_stream() {
        DoubleStream stream = Mockito.mock(DoubleStream.class);
        Assertions.assertThat(stream).isEqualTo(stream);
    }

    @Test
    public void stream_can_be_asserted_twice() {
        DoubleStream names = DoubleStream.of(823952.8, 1.9472305859E9);
        Assertions.assertThat(names).containsExactly(823952.8, 1.9472305859E9).containsExactly(823952.8, 1.9472305859E9);
    }

    @Test
    public void should_not_consume_stream_when_asserting_non_null() {
        DoubleStream stream = Mockito.mock(DoubleStream.class);
        Assertions.assertThat(stream).isNotNull();
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isInstanceOf_should_check_the_original_stream_without_consuming_it() {
        DoubleStream stream = Mockito.mock(DoubleStream.class);
        Assertions.assertThat(stream).isInstanceOf(DoubleStream.class);
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isInstanceOfAny_should_check_the_original_stream_without_consuming_it() {
        DoubleStream stream = Mockito.mock(DoubleStream.class);
        Assertions.assertThat(stream).isInstanceOfAny(DoubleStream.class, String.class);
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isOfAnyClassIn_should_check_the_original_stream_without_consuming_it() {
        DoubleStream stream = Mockito.mock(DoubleStream.class);
        Assertions.assertThat(stream).isOfAnyClassIn(Double.class, stream.getClass());
    }

    @Test
    public void isExactlyInstanceOf_should_check_the_original_stream() {
        // factory creates use internal classes
        Assertions.assertThat(intStream).isExactlyInstanceOf(intStream.getClass());
    }

    @Test
    public void isNotExactlyInstanceOf_should_check_the_original_stream() {
        Assertions.assertThat(intStream).isNotExactlyInstanceOf(DoubleStream.class);
        try {
            Assertions.assertThat(intStream).isNotExactlyInstanceOf(intStream.getClass());
        } catch (AssertionError e) {
            // ok
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void isNotInstanceOf_should_check_the_original_stream() {
        Assertions.assertThat(intStream).isNotInstanceOf(String.class);
    }

    @Test
    public void isNotInstanceOfAny_should_check_the_original_stream() {
        Assertions.assertThat(intStream).isNotInstanceOfAny(Long.class, String.class);
    }

    @Test
    public void isNotOfAnyClassIn_should_check_the_original_stream() {
        Assertions.assertThat(intStream).isNotOfAnyClassIn(Long.class, String.class);
    }

    @Test
    public void isSameAs_should_check_the_original_stream_without_consuming_it() {
        DoubleStream stream = Mockito.mock(DoubleStream.class);
        Assertions.assertThat(stream).isSameAs(stream);
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void isNotSameAs_should_check_the_original_stream_without_consuming_it() {
        DoubleStream stream = Mockito.mock(DoubleStream.class);
        try {
            Assertions.assertThat(stream).isNotSameAs(stream);
        } catch (AssertionError e) {
            Mockito.verifyZeroInteractions(stream);
            return;
        }
        Assertions.fail("Expected assertionError, because assert notSame on same stream.");
    }
}

