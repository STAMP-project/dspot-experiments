/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.serialization.upcasting;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 *
 * @author Rene de Waele
 */
public class GenericUpcasterChainTest {
    @Test
    public void testChainWithSingleUpcasterCanUpcastMultipleEvents() {
        Object a = "a";
        Object b = "b";
        Object c = "c";
        Upcaster<Object> testSubject = new GenericUpcasterChain(new GenericUpcasterChainTest.AToBUpcaster(a, b));
        Stream<Object> result = testSubject.upcast(Stream.of(a, b, a, c));
        TestCase.assertEquals(Arrays.asList(b, b, b, c), result.collect(Collectors.toList()));
    }

    @Test
    public void testUpcastingResultOfOtherUpcaster() {
        Object a = "a";
        Object b = "b";
        Object c = "c";
        Upcaster<Object> testSubject = new GenericUpcasterChain(new GenericUpcasterChainTest.AToBUpcaster(a, b), new GenericUpcasterChainTest.AToBUpcaster(b, c));
        Stream<Object> result = testSubject.upcast(Stream.of(a, b, a, c));
        TestCase.assertEquals(Arrays.asList(c, c, c, c), result.collect(Collectors.toList()));
    }

    @Test
    public void testUpcastingResultOfOtherUpcasterOnlyWorksIfUpcastersAreInCorrectOrder() {
        Object a = "a";
        Object b = "b";
        Object c = "c";
        Upcaster<Object> testSubject = new GenericUpcasterChain(new GenericUpcasterChainTest.AToBUpcaster(b, c), new GenericUpcasterChainTest.AToBUpcaster(a, b));
        Stream<Object> result = testSubject.upcast(Stream.of(a, b, a, c));
        TestCase.assertEquals(Arrays.asList(b, c, b, c), result.collect(Collectors.toList()));
    }

    @Test
    public void testRemainderAddedAndUpcasted() {
        Object a = "a";
        Object b = "b";
        Object c = "c";
        Upcaster<Object> testSubject = new GenericUpcasterChain(new GenericUpcasterChainTest.UpcasterWithRemainder(a, b), new GenericUpcasterChainTest.AToBUpcaster(b, c));
        Stream<Object> result = testSubject.upcast(Stream.of(a, b, a, c));
        TestCase.assertEquals(Arrays.asList(a, c, a, c, a, c), result.collect(Collectors.toList()));
    }

    @Test
    public void testRemainderReleasedAfterUpcasting() {
        Object a = "a";
        Object b = "b";
        Object c = "c";
        Object d = "d";
        Upcaster<Object> testSubject = new GenericUpcasterChain(new GenericUpcasterChainTest.ConditionalUpcaster(a, b, c), new GenericUpcasterChainTest.ConditionalUpcaster(c, d, a));
        Stream<Object> result = testSubject.upcast(Stream.of(a, d, a, b, d, a));
        TestCase.assertEquals(Arrays.asList(a, d, a, a), result.collect(Collectors.toList()));
    }

    @Test
    public void testUpcastToMultipleTypes() {
        Object a = "a";
        Object b = "b";
        Object c = "c";
        Upcaster<Object> testSubject = new GenericUpcasterChain(new GenericUpcasterChainTest.MultipleTypesUpcaster(), new GenericUpcasterChainTest.AToBUpcaster(b, c));
        Stream<Object> result = testSubject.upcast(Stream.of(a, b, c));
        TestCase.assertEquals(Arrays.asList(a, a, c, c, c, c), result.collect(Collectors.toList()));
    }

    private static class AToBUpcaster extends SingleEntryUpcaster<Object> {
        private final Object a;

        private final Object b;

        private AToBUpcaster(Object a, Object b) {
            this.a = a;
            this.b = b;
        }

        @Override
        protected boolean canUpcast(Object intermediateRepresentation) {
            return intermediateRepresentation == (a);
        }

        @Override
        protected Object doUpcast(Object intermediateRepresentation) {
            return b;
        }
    }

    private static class UpcasterWithRemainder implements Upcaster<Object> {
        private final List<Object> remainder;

        public UpcasterWithRemainder(Object... remainder) {
            this.remainder = Arrays.asList(remainder);
        }

        @Override
        public Stream<Object> upcast(Stream<Object> intermediateRepresentations) {
            return Stream.concat(intermediateRepresentations, remainder.stream());
        }
    }

    private static class ConditionalUpcaster implements Upcaster<Object> {
        private final Object first;

        private final Object second;

        private final Object replacement;

        public ConditionalUpcaster(Object first, Object second, Object replacement) {
            this.first = first;
            this.second = second;
            this.replacement = replacement;
        }

        @Override
        public Stream<Object> upcast(Stream<Object> intermediateRepresentations) {
            AtomicReference<Object> previous = new AtomicReference<>();
            return Stream.concat(intermediateRepresentations.filter(( entry) -> !((entry == (first)) && (previous.compareAndSet(null, entry)))).flatMap(( entry) -> {
                if ((entry == (second)) && (previous.compareAndSet(first, null))) {
                    return Stream.of(replacement);
                }
                return Optional.ofNullable(previous.getAndSet(null)).map(( cached) -> Stream.of(cached, entry)).orElse(Stream.of(entry));
            }), Stream.generate(previous::get).limit(1).filter(Objects::nonNull));
        }
    }

    private static class MultipleTypesUpcaster implements Upcaster<Object> {
        @Override
        public Stream<Object> upcast(Stream<Object> intermediateRepresentations) {
            return intermediateRepresentations.flatMap(( entry) -> Stream.of(entry, entry));
        }
    }
}

