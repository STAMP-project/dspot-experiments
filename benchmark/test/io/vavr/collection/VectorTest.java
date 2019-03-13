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


import io.vavr.Serializables;
import io.vavr.Value;
import io.vavr.control.Option;
import java.io.InvalidObjectException;
import java.math.BigDecimal;
import org.junit.Test;


public class VectorTest extends AbstractIndexedSeqTest {
    // -- static narrow
    @Test
    public void shouldNarrowVector() {
        final Vector<Double> doubles = of(1.0);
        final Vector<Number> numbers = Vector.narrow(doubles);
        final int actual = numbers.append(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- primitives
    @Test
    public void shouldAddNullToPrimitiveVector() {
        final Vector<Integer> primitives = rangeClosed(0, 2);
        assertThat(primitives.append(null)).isEqualTo(of(0, 1, 2, null));
        assertThat(primitives.prepend(null)).isEqualTo(of(null, 0, 1, 2));
        assertThat(primitives.update(1, ((Integer) (null)))).isEqualTo(of(0, null, 2));
    }

    @Test
    public void shouldAddObjectToPrimitiveVector() {
        final String object = "String";
        final Vector<Object> primitives = Vector.narrow(rangeClosed(0, 2));
        assertThat(primitives.append(object)).isEqualTo(of(0, 1, 2, object));
        assertThat(primitives.prepend(object)).isEqualTo(of(object, 0, 1, 2));
        assertThat(primitives.update(1, object)).isEqualTo(of(0, object, 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowForVoidType() {
        ArrayType.of(void.class);
    }

    // -- transform()
    @Test
    public void shouldTransform() {
        final String transformed = of(42).transform(( v) -> String.valueOf(v.get()));
        assertThat(transformed).isEqualTo("42");
    }

    // -- unfold
    @Test
    public void shouldUnfoldRightToEmpty() {
        assertThat(Vector.unfoldRight(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldRightSimpleVector() {
        assertThat(Vector.unfoldRight(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>(x, (x - 1))))).isEqualTo(of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    }

    @Test
    public void shouldUnfoldLeftToEmpty() {
        assertThat(Vector.unfoldLeft(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldLeftSimpleVector() {
        assertThat(Vector.unfoldLeft(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void shouldUnfoldToEmpty() {
        assertThat(Vector.unfold(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldSimpleVector() {
        assertThat(Vector.unfold(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    // -- dropRightWhile
    @Test
    public void shouldDropRightWhileNoneOnNil() {
        assertThat(empty().dropRightWhile(( ignored) -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldDropRightWhileNoneIfPredicateIsFalse() {
        assertThat(of(1, 2, 3).dropRightWhile(( ignored) -> false)).isEqualTo(of(1, 2, 3));
    }

    @Test
    public void shouldDropRightWhileAllIfPredicateIsTrue() {
        assertThat(of(1, 2, 3).dropRightWhile(( ignored) -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldDropRightWhileCorrect() {
        assertThat(ofAll("abc  ".toCharArray()).dropRightWhile(Character::isWhitespace)).isEqualTo(ofAll("abc".toCharArray()));
    }

    // -- toString
    @Test
    public void shouldStringifyNil() {
        assertThat(empty().toString()).isEqualTo("Vector()");
    }

    @Test
    public void shouldStringifyNonNil() {
        assertThat(of(null, 1, 2, 3).toString()).isEqualTo("Vector(null, 1, 2, 3)");
    }

    // -- Cons test
    @Test(expected = InvalidObjectException.class)
    public void shouldNotSerializeEnclosingClass() throws Throwable {
        Serializables.callReadObject(List.of(1));
    }

    // -- toVector
    @Test
    public void shouldReturnSelfOnConvertToVector() {
        final Value<Integer> value = of(1, 2, 3);
        assertThat(value.toVector()).isSameAs(value);
    }
}

