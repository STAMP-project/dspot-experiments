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
package org.axonframework.queryhandling;


import org.axonframework.queryhandling.annotation.AnnotationQueryHandlerAdapter;
import org.junit.Test;


/**
 * Tests resolving query handlers which return primitive types.
 */
public class PrimitiveQueryHandlerResponseTypeTest {
    private final SimpleQueryBus queryBus = SimpleQueryBus.builder().build();

    private final PrimitiveQueryHandlerResponseTypeTest.PrimitiveQueryHandler queryHandler = new PrimitiveQueryHandlerResponseTypeTest.PrimitiveQueryHandler();

    private final AnnotationQueryHandlerAdapter<PrimitiveQueryHandlerResponseTypeTest.PrimitiveQueryHandler> annotationQueryHandlerAdapter = new AnnotationQueryHandlerAdapter(queryHandler);

    @Test
    public void testInt() {
        test(0, Integer.class, int.class);
    }

    @Test
    public void testLong() {
        test(0L, Long.class, long.class);
    }

    @Test
    public void testShort() {
        test(((short) (0)), Short.class, short.class);
    }

    @Test
    public void testFloat() {
        test(0.0F, Float.class, float.class);
    }

    @Test
    public void testDouble() {
        test(0.0, Double.class, double.class);
    }

    @Test
    public void testBoolean() {
        test(false, Boolean.class, boolean.class);
    }

    @Test
    public void testByte() {
        test(((byte) (0)), Byte.class, byte.class);
    }

    @Test
    public void testChar() {
        test('0', Character.class, Character.TYPE);
    }

    private static class PrimitiveQueryHandler {
        @QueryHandler
        public int handle(final Integer query) {
            return query;
        }

        @QueryHandler
        public long handle(final Long query) {
            return query;
        }

        @QueryHandler
        public short handle(final Short query) {
            return query;
        }

        @QueryHandler
        public float handle(final Float query) {
            return query;
        }

        @QueryHandler
        public double handle(final Double query) {
            return query;
        }

        @QueryHandler
        public boolean handle(final Boolean query) {
            return query;
        }

        @QueryHandler
        public byte handle(final Byte query) {
            return query;
        }

        @QueryHandler
        public char handle(final Character query) {
            return query;
        }
    }
}

