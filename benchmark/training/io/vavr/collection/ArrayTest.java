/**
 * __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.collection;


import io.vavr.Value;
import io.vavr.control.Option;
import java.math.BigDecimal;
import org.junit.Test;


public class ArrayTest extends AbstractIndexedSeqTest {
    // -- static narrow
    @Test
    public void shouldNarrowArray() {
        final Array<Double> doubles = of(1.0);
        final Array<Number> numbers = Array.narrow(doubles);
        final int actual = numbers.append(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- transform()
    @Test
    public void shouldTransform() {
        String transformed = of(42).transform(( v) -> String.valueOf(v.get()));
        assertThat(transformed).isEqualTo("42");
    }

    // -- unfold
    @Test
    public void shouldUnfoldRightToEmpty() {
        assertThat(Array.unfoldRight(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldRightSimpleArray() {
        assertThat(Array.unfoldRight(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>(x, (x - 1))))).isEqualTo(of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    }

    @Test
    public void shouldUnfoldLeftToEmpty() {
        assertThat(Array.unfoldLeft(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldLeftSimpleArray() {
        assertThat(Array.unfoldLeft(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void shouldUnfoldToEmpty() {
        assertThat(Array.unfold(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldSimpleArray() {
        assertThat(Array.unfold(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    // -- toString
    @Test
    public void shouldStringifyNil() {
        assertThat(empty().toString()).isEqualTo("Array()");
    }

    @Test
    public void shouldStringifyNonNil() {
        assertThat(of(1, 2, 3).toString()).isEqualTo("Array(1, 2, 3)");
    }

    // -- toArray
    @Test
    public void shouldReturnSelfOnConvertToArray() {
        Value<Integer> value = of(1, 2, 3);
        assertThat(value.toArray()).isSameAs(value);
    }
}

