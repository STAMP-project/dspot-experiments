/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.builder;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ReflectionDiffBuilderTest {
    private static final ToStringStyle SHORT_STYLE = ToStringStyle.SHORT_PREFIX_STYLE;

    @SuppressWarnings("unused")
    private static class TypeTestClass implements Diffable<ReflectionDiffBuilderTest.TypeTestClass> {
        private final ToStringStyle style = ReflectionDiffBuilderTest.SHORT_STYLE;

        private final boolean booleanField = true;

        private final boolean[] booleanArrayField = new boolean[]{ true };

        private final byte byteField = ((byte) (255));

        private final byte[] byteArrayField = new byte[]{ ((byte) (255)) };

        private char charField = 'a';

        private char[] charArrayField = new char[]{ 'a' };

        private final double doubleField = 1.0;

        private final double[] doubleArrayField = new double[]{ 1.0 };

        private final float floatField = 1.0F;

        private final float[] floatArrayField = new float[]{ 1.0F };

        int intField = 1;

        private final int[] intArrayField = new int[]{ 1 };

        private final long longField = 1L;

        private final long[] longArrayField = new long[]{ 1L };

        private final short shortField = 1;

        private final short[] shortArrayField = new short[]{ 1 };

        private final Object objectField = null;

        private final Object[] objectArrayField = new Object[]{ null };

        private static int staticField;

        private transient String transientField;

        @Override
        public DiffResult diff(final ReflectionDiffBuilderTest.TypeTestClass obj) {
            return new ReflectionDiffBuilder(this, obj, style).build();
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, false);
        }

        @Override
        public boolean equals(final Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj, false);
        }
    }

    @SuppressWarnings("unused")
    private static class TypeTestChildClass extends ReflectionDiffBuilderTest.TypeTestClass {
        String field = "a";
    }

    @Test
    public void test_no_differences() {
        final ReflectionDiffBuilderTest.TypeTestClass firstObject = new ReflectionDiffBuilderTest.TypeTestClass();
        final ReflectionDiffBuilderTest.TypeTestClass secondObject = new ReflectionDiffBuilderTest.TypeTestClass();
        final DiffResult list = firstObject.diff(secondObject);
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    @Test
    public void test_primitive_difference() {
        final ReflectionDiffBuilderTest.TypeTestClass firstObject = new ReflectionDiffBuilderTest.TypeTestClass();
        firstObject.charField = 'c';
        final ReflectionDiffBuilderTest.TypeTestClass secondObject = new ReflectionDiffBuilderTest.TypeTestClass();
        final DiffResult list = firstObject.diff(secondObject);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
    }

    @Test
    public void test_array_difference() {
        final ReflectionDiffBuilderTest.TypeTestClass firstObject = new ReflectionDiffBuilderTest.TypeTestClass();
        firstObject.charArrayField = new char[]{ 'c' };
        final ReflectionDiffBuilderTest.TypeTestClass secondObject = new ReflectionDiffBuilderTest.TypeTestClass();
        final DiffResult list = firstObject.diff(secondObject);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
    }

    @Test
    public void test_transient_field_difference() {
        final ReflectionDiffBuilderTest.TypeTestClass firstObject = new ReflectionDiffBuilderTest.TypeTestClass();
        firstObject.transientField = "a";
        final ReflectionDiffBuilderTest.TypeTestClass secondObject = new ReflectionDiffBuilderTest.TypeTestClass();
        firstObject.transientField = "b";
        final DiffResult list = firstObject.diff(secondObject);
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    @Test
    public void test_no_differences_inheritance() {
        final ReflectionDiffBuilderTest.TypeTestChildClass firstObject = new ReflectionDiffBuilderTest.TypeTestChildClass();
        final ReflectionDiffBuilderTest.TypeTestChildClass secondObject = new ReflectionDiffBuilderTest.TypeTestChildClass();
        final DiffResult list = firstObject.diff(secondObject);
        Assertions.assertEquals(0, list.getNumberOfDiffs());
    }

    @Test
    public void test_difference_in_inherited_field() {
        final ReflectionDiffBuilderTest.TypeTestChildClass firstObject = new ReflectionDiffBuilderTest.TypeTestChildClass();
        firstObject.intField = 99;
        final ReflectionDiffBuilderTest.TypeTestChildClass secondObject = new ReflectionDiffBuilderTest.TypeTestChildClass();
        final DiffResult list = firstObject.diff(secondObject);
        Assertions.assertEquals(1, list.getNumberOfDiffs());
    }
}

