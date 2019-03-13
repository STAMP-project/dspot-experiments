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
import io.vavr.Tuple;
import io.vavr.Value;
import io.vavr.control.Option;
import java.io.InvalidObjectException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.TreeSet;
import org.junit.Test;


public class ListTest extends AbstractLinearSeqTest {
    // -- static narrow
    @Test
    public void shouldNarrowList() {
        final List<Double> doubles = of(1.0);
        final List<Number> numbers = List.narrow(doubles);
        final int actual = numbers.append(new BigDecimal("2.0")).sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    // -- ofAll(NavigableSet)
    @Test
    public void shouldAcceptNavigableSet() {
        final TreeSet<Integer> javaSet = new TreeSet<>();
        javaSet.add(2);
        javaSet.add(1);
        assertThat(List.ofAll(javaSet)).isEqualTo(List.of(1, 2));
    }

    // -- peek
    @Test(expected = NoSuchElementException.class)
    public void shouldFailPeekOfNil() {
        empty().peek();
    }

    @Test
    public void shouldPeekOfNonNil() {
        assertThat(of(1).peek()).isEqualTo(1);
        assertThat(of(1, 2).peek()).isEqualTo(1);
    }

    // -- peekOption
    @Test
    public void shouldPeekOption() {
        assertThat(empty().peekOption()).isSameAs(Option.none());
        assertThat(of(1).peekOption()).isEqualTo(Option.of(1));
        assertThat(of(1, 2).peekOption()).isEqualTo(Option.of(1));
    }

    // -- pop
    @Test(expected = NoSuchElementException.class)
    public void shouldFailPopOfNil() {
        empty().pop();
    }

    @Test
    public void shouldPopOfNonNil() {
        assertThat(of(1).pop()).isSameAs(empty());
        assertThat(of(1, 2).pop()).isEqualTo(of(2));
    }

    // -- popOption
    @Test
    public void shouldPopOption() {
        assertThat(empty().popOption()).isSameAs(Option.none());
        assertThat(of(1).popOption()).isEqualTo(Option.of(empty()));
        assertThat(of(1, 2).popOption()).isEqualTo(Option.of(of(2)));
    }

    // -- pop2
    @Test(expected = NoSuchElementException.class)
    public void shouldFailPop2OfNil() {
        empty().pop2();
    }

    @Test
    public void shouldPop2OfNonNil() {
        assertThat(of(1).pop2()).isEqualTo(Tuple.of(1, empty()));
        assertThat(of(1, 2).pop2()).isEqualTo(Tuple.of(1, of(2)));
    }

    // -- pop2Option
    @Test
    public void shouldPop2Option() {
        assertThat(empty().pop2Option()).isSameAs(Option.none());
        assertThat(of(1).pop2Option()).isEqualTo(Option.of(Tuple.of(1, empty())));
        assertThat(of(1, 2).pop2Option()).isEqualTo(Option.of(Tuple.of(1, of(2))));
    }

    // -- push
    @Test
    public void shouldPushElements() {
        assertThat(empty().push(1)).isEqualTo(of(1));
        assertThat(empty().push(1, 2, 3)).isEqualTo(of(3, 2, 1));
        assertThat(empty().pushAll(of(1, 2, 3))).isEqualTo(of(3, 2, 1));
        assertThat(of(0).push(1)).isEqualTo(of(1, 0));
        assertThat(of(0).push(1, 2, 3)).isEqualTo(of(3, 2, 1, 0));
        assertThat(of(0).pushAll(of(1, 2, 3))).isEqualTo(of(3, 2, 1, 0));
    }

    // -- transform()
    @Test
    public void shouldTransform() {
        final String transformed = of(42).transform(( v) -> String.valueOf(v.get()));
        assertThat(transformed).isEqualTo("42");
    }

    // -- toString
    @Test
    public void shouldStringifyNil() {
        assertThat(empty().toString()).isEqualTo("List()");
    }

    @Test
    public void shouldStringifyNonNil() {
        assertThat(of(1, 2, 3).toString()).isEqualTo("List(1, 2, 3)");
    }

    // -- unfold
    @Test
    public void shouldUnfoldRightToEmpty() {
        assertThat(List.unfoldRight(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldRightSimpleList() {
        assertThat(List.unfoldRight(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>(x, (x - 1))))).isEqualTo(of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    }

    @Test
    public void shouldUnfoldLeftToEmpty() {
        assertThat(List.unfoldLeft(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldLeftSimpleList() {
        assertThat(List.unfoldLeft(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void shouldUnfoldToEmpty() {
        assertThat(List.unfold(0, ( x) -> Option.none())).isEqualTo(empty());
    }

    @Test
    public void shouldUnfoldSimpleList() {
        assertThat(List.unfold(10, ( x) -> x == 0 ? Option.none() : Option.of(new Tuple2<>((x - 1), x)))).isEqualTo(of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    // -- Cons test
    @Test(expected = InvalidObjectException.class)
    public void shouldNotSerializeEnclosingClass() throws Throwable {
        Serializables.callReadObject(List.of(1));
    }

    @Test(expected = InvalidObjectException.class)
    public void shouldNotDeserializeListWithSizeLessThanOne() throws Throwable {
        try {
            /* This implementation is stable regarding jvm impl changes of object serialization. The index of the number
            of List elements is gathered dynamically.
             */
            final byte[] listWithOneElement = Serializables.serialize(List.of(0));
            final byte[] listWithTwoElements = Serializables.serialize(List.of(0, 0));
            int index = -1;
            for (int i = 0; (i < (listWithOneElement.length)) && (index == (-1)); i++) {
                final byte b1 = listWithOneElement[i];
                final byte b2 = listWithTwoElements[i];
                if (b1 != b2) {
                    if ((b1 != 1) || (b2 != 2)) {
                        throw new IllegalStateException("Difference does not indicate number of elements.");
                    } else {
                        index = i;
                    }
                }
            }
            if (index == (-1)) {
                throw new IllegalStateException("Hack incomplete - index not found");
            }
            /* Hack the serialized data and fake zero elements. */
            listWithOneElement[index] = 0;
            Serializables.deserialize(listWithOneElement);
        } catch (IllegalStateException x) {
            throw (x.getCause()) != null ? x.getCause() : x;
        }
    }

    // -- toList
    @Test
    public void shouldReturnSelfOnConvertToList() {
        final Value<Integer> value = of(1, 2, 3);
        assertThat(value.toList()).isSameAs(value);
    }

    // -- spliterator
    @Test
    public void shouldHaveSizedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().hasCharacteristics(((Spliterator.SIZED) | (Spliterator.SUBSIZED)))).isTrue();
    }

    @Test
    public void shouldReturnSizeWhenSpliterator() {
        assertThat(of(1, 2, 3).spliterator().getExactSizeIfKnown()).isEqualTo(3);
    }
}

