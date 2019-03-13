/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.util;


import com.google.common.testing.EqualsTester;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link Either}.
 */
@RunWith(JUnit4.class)
public class EitherTest {
    @SuppressWarnings("unchecked")
    @Test
    public void leftConsume() {
        Either<Integer, String> underTest = Either.ofLeft(42);
        Consumer<Integer> mockIntegerConsumer = Mockito.mock(Consumer.class);
        Consumer<String> mockStringConsumer = Mockito.mock(Consumer.class);
        underTest.consume(mockIntegerConsumer, mockStringConsumer);
        Mockito.verify(mockIntegerConsumer, Mockito.times(1)).accept(ArgumentMatchers.eq(42));
        Mockito.verify(mockStringConsumer, Mockito.never()).accept(ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void leftMap() {
        Either<Integer, String> underTest = Either.ofLeft(42);
        Function<Integer, Boolean> mockIntegerFunction = Mockito.mock(Function.class);
        Function<String, Boolean> mockStringFunction = Mockito.mock(Function.class);
        Mockito.when(mockIntegerFunction.apply(ArgumentMatchers.eq(42))).thenReturn(true);
        assertThat(underTest.map(mockIntegerFunction, mockStringFunction)).isTrue();
        Mockito.verify(mockIntegerFunction, Mockito.times(1)).apply(ArgumentMatchers.eq(42));
        Mockito.verify(mockStringFunction, Mockito.never()).apply(ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void rightConsume() {
        Either<Integer, String> underTest = Either.ofRight("cat");
        Consumer<Integer> mockIntegerConsumer = Mockito.mock(Consumer.class);
        Consumer<String> mockStringConsumer = Mockito.mock(Consumer.class);
        underTest.consume(mockIntegerConsumer, mockStringConsumer);
        Mockito.verify(mockIntegerConsumer, Mockito.never()).accept(ArgumentMatchers.any());
        Mockito.verify(mockStringConsumer, Mockito.times(1)).accept(ArgumentMatchers.eq("cat"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void rightMap() {
        Either<Integer, String> underTest = Either.ofRight("cat");
        Function<Integer, Boolean> mockIntegerFunction = Mockito.mock(Function.class);
        Function<String, Boolean> mockStringFunction = Mockito.mock(Function.class);
        Mockito.when(mockStringFunction.apply(ArgumentMatchers.eq("cat"))).thenReturn(true);
        assertThat(underTest.map(mockIntegerFunction, mockStringFunction)).isTrue();
        Mockito.verify(mockIntegerFunction, Mockito.never()).apply(ArgumentMatchers.any());
        Mockito.verify(mockStringFunction, Mockito.times(1)).apply(ArgumentMatchers.eq("cat"));
    }

    @Test
    public void equalsAndHashCode() {
        new EqualsTester().addEqualityGroup(Either.ofLeft(null), Either.ofLeft(null)).addEqualityGroup(Either.ofLeft(1), Either.ofLeft(1)).addEqualityGroup(Either.ofLeft(2), Either.ofLeft(2)).addEqualityGroup(Either.ofRight(1), Either.ofRight(1)).addEqualityGroup(Either.ofRight("cat"), Either.ofRight("cat")).addEqualityGroup(Either.ofRight("dog"), Either.ofRight("dog")).testEquals();
    }
}

