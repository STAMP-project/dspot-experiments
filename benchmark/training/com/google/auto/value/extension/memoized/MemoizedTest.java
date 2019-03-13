/**
 * Copyright (C) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.auto.value.extension.memoized;


import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class MemoizedTest {
    private MemoizedTest.Value value;

    private MemoizedTest.ListValue<Integer, String> listValue;

    @AutoValue
    abstract static class ValueWithKeywordName {
        abstract boolean getNative();

        abstract boolean getNative0();

        @Memoized
        boolean getMemoizedNative() {
            return getNative();
        }

        @Memoized
        boolean getMemoizedNative0() {
            return getNative0();
        }
    }

    @AutoValue
    abstract static class Value {
        private int primitiveCount;

        private int notNullableCount;

        private int nullableCount;

        private int returnsNullCount;

        private int nullableWithTypeAnnotationCount;

        private int returnsNullWithTypeAnnotationCount;

        private int notNullableButReturnsNullCount;

        private int throwsExceptionCount;

        @Nullable
        abstract String string();

        @org.checkerframework.checker.nullness.qual.Nullable
        abstract String stringWithTypeAnnotation();

        abstract MemoizedTest.HashCodeAndToStringCounter counter();

        @Memoized
        int primitive() {
            return ++(primitiveCount);
        }

        @Memoized
        String notNullable() {
            (notNullableCount)++;
            return (("derived " + (string())) + " ") + (notNullableCount);
        }

        @Memoized
        @Nullable
        String nullable() {
            (nullableCount)++;
            return (("nullable derived " + (string())) + " ") + (nullableCount);
        }

        @Memoized
        @Nullable
        String returnsNull() {
            (returnsNullCount)++;
            return null;
        }

        @Memoized
        @org.checkerframework.checker.nullness.qual.Nullable
        String nullableWithTypeAnnotation() {
            (nullableWithTypeAnnotationCount)++;
            return (("nullable derived " + (stringWithTypeAnnotation())) + " ") + (nullableWithTypeAnnotationCount);
        }

        @Memoized
        @org.checkerframework.checker.nullness.qual.Nullable
        String returnsNullWithTypeAnnotation() {
            (returnsNullWithTypeAnnotationCount)++;
            return null;
        }

        @Memoized
        String notNullableButReturnsNull() {
            (notNullableButReturnsNullCount)++;
            return null;
        }

        @Memoized
        String throwsException() throws MemoizedTest.SomeCheckedException {
            (throwsExceptionCount)++;
            throw new MemoizedTest.SomeCheckedException();
        }

        @Override
        @Memoized
        public abstract int hashCode();

        @Override
        @Memoized
        public abstract String toString();
    }

    static final class SomeCheckedException extends Exception {}

    @AutoValue
    abstract static class ListValue<T extends Number, K> {
        abstract T value();

        abstract K otherValue();

        @Memoized
        ImmutableList<T> myTypedList() {
            return ImmutableList.of(value());
        }
    }

    static class HashCodeAndToStringCounter {
        int hashCodeCount;

        int toStringCount;

        @Override
        public int hashCode() {
            return ++(hashCodeCount);
        }

        @Override
        public String toString() {
            return "a string" + (++(toStringCount));
        }
    }

    @AutoValue
    abstract static class HashCodeEqualsOptimization {
        int overrideHashCode;

        int hashCodeCount;

        abstract MemoizedTest.HashCodeEqualsOptimization.EqualsCounter equalsCounter();

        @Memoized
        @Override
        public int hashCode() {
            (hashCodeCount)++;
            return overrideHashCode;
        }

        static class EqualsCounter {
            int equalsCount;

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                (equalsCount)++;
                return true;
            }
        }
    }

    @AutoValue
    abstract static class HashCodeEqualsOptimizationOffWhenEqualsIsFinal {
        int hashCodeCount;

        @Override
        @Memoized
        public int hashCode() {
            (hashCodeCount)++;
            return 1;
        }

        @Override
        public final boolean equals(Object that) {
            return that instanceof MemoizedTest.HashCodeEqualsOptimizationOffWhenEqualsIsFinal;
        }
    }

    @Test
    public void listValueList() {
        assertThat(listValue.myTypedList()).containsExactly(listValue.value());
    }

    @Test
    public void listValueString() {
        assertThat(listValue.otherValue()).isEqualTo("hello");
    }

    @Test
    public void primitive() {
        assertThat(value.primitive()).isEqualTo(1);
        assertThat(value.primitive()).isEqualTo(1);
        assertThat(value.primitiveCount).isEqualTo(1);
    }

    @Test
    public void notNullable() {
        assertThat(value.notNullable()).isEqualTo("derived string 1");
        assertThat(value.notNullable()).isSameAs(value.notNullable());
        assertThat(value.notNullableCount).isEqualTo(1);
    }

    @Test
    public void nullable() {
        assertThat(value.nullable()).isEqualTo("nullable derived string 1");
        assertThat(value.nullable()).isSameAs(value.nullable());
        assertThat(value.nullableCount).isEqualTo(1);
    }

    @Test
    public void nullableWithTypeAnnotation() {
        assertThat(value.nullableWithTypeAnnotation()).isEqualTo("nullable derived stringWithTypeAnnotation 1");
        assertThat(value.nullableWithTypeAnnotation()).isSameAs(value.nullableWithTypeAnnotation());
        assertThat(value.nullableWithTypeAnnotationCount).isEqualTo(1);
    }

    @Test
    public void returnsNull() {
        assertThat(value.returnsNull()).isNull();
        assertThat(value.returnsNull()).isNull();
        assertThat(value.returnsNullCount).isEqualTo(1);
    }

    @Test
    public void returnsNullWithTypeAnnotation() {
        assertThat(value.returnsNullWithTypeAnnotation()).isNull();
        assertThat(value.returnsNullWithTypeAnnotation()).isNull();
        assertThat(value.returnsNullWithTypeAnnotationCount).isEqualTo(1);
    }

    @Test
    public void notNullableButReturnsNull() {
        try {
            value.notNullableButReturnsNull();
            Assert.fail();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("notNullableButReturnsNull() cannot return null");
        }
        assertThat(value.notNullableButReturnsNullCount).isEqualTo(1);
    }

    @Test
    public void methodThrows() {
        // The exception is thrown.
        try {
            value.throwsException();
            Assert.fail();
        } catch (MemoizedTest.SomeCheckedException expected1) {
            // The exception is not memoized.
            try {
                value.throwsException();
                Assert.fail();
            } catch (MemoizedTest.SomeCheckedException expected2) {
                assertThat(expected2).isNotSameAs(expected1);
            }
        }
        assertThat(value.throwsExceptionCount).isEqualTo(2);
    }

    @Test
    public void testHashCode() {
        assertThat(value.hashCode()).isEqualTo(value.hashCode());
        assertThat(value.counter().hashCodeCount).isEqualTo(1);
    }

    @Test
    public void testToString() {
        assertThat(value.toString()).isEqualTo(value.toString());
        assertThat(value.counter().toStringCount).isEqualTo(1);
    }

    @Test
    public void keywords() {
        MemoizedTest.ValueWithKeywordName value = new AutoValue_MemoizedTest_ValueWithKeywordName(true, false);
        assertThat(value.getNative()).isTrue();
        assertThat(value.getMemoizedNative()).isTrue();
        assertThat(value.getNative0()).isFalse();
        assertThat(value.getMemoizedNative0()).isFalse();
    }

    @Test
    public void nullableHasAnnotation() throws ReflectiveOperationException {
        Method nullable = AutoValue_MemoizedTest_Value.class.getDeclaredMethod("nullable");
        assertThat(nullable.isAnnotationPresent(Nullable.class)).isTrue();
    }

    @Test
    public void nullableWithTypeAnnotationHasAnnotation() throws ReflectiveOperationException {
        Method nullable = AutoValue_MemoizedTest_Value.class.getDeclaredMethod("nullableWithTypeAnnotation");
        AnnotatedType returnType = nullable.getAnnotatedReturnType();
        assertThat(returnType.isAnnotationPresent(org.checkerframework.checker.nullness.qual.Nullable.class)).isTrue();
    }

    @Test
    public void nullableConstructorParameter() throws ReflectiveOperationException {
        // Constructor parameters are potentially:
        // [0] @javax.annotation.Nullable String string,
        // [1] @org.checkerframework.checker.nullness.qual.Nullable String stringWithTypeAnnotation,
        // [2] HashCodeAndToStringCounter counter
        // We don't currently copy @javax.annotation.Nullable because it is not a TYPE_USE annotation.
        Constructor<?> constructor = AutoValue_MemoizedTest_Value.class.getDeclaredConstructor(String.class, String.class, MemoizedTest.HashCodeAndToStringCounter.class);
        AnnotatedType paramType = constructor.getAnnotatedParameterTypes()[1];
        assertThat(paramType.isAnnotationPresent(org.checkerframework.checker.nullness.qual.Nullable.class)).isTrue();
    }

    @Test
    public void hashCodeEqualsOptimization() {
        MemoizedTest.HashCodeEqualsOptimization first = new AutoValue_MemoizedTest_HashCodeEqualsOptimization(new MemoizedTest.HashCodeEqualsOptimization.EqualsCounter());
        MemoizedTest.HashCodeEqualsOptimization second = new AutoValue_MemoizedTest_HashCodeEqualsOptimization(new MemoizedTest.HashCodeEqualsOptimization.EqualsCounter());
        first.overrideHashCode = 2;
        second.overrideHashCode = 2;
        assertThat(first.equals(second)).isTrue();
        assertThat(first.equalsCounter().equalsCount).isEqualTo(1);
        MemoizedTest.HashCodeEqualsOptimization otherwiseEqualsButDifferentHashCode = new AutoValue_MemoizedTest_HashCodeEqualsOptimization(new MemoizedTest.HashCodeEqualsOptimization.EqualsCounter());
        otherwiseEqualsButDifferentHashCode.overrideHashCode = 4;
        assertThat(otherwiseEqualsButDifferentHashCode.equals(first)).isFalse();
        assertThat(otherwiseEqualsButDifferentHashCode.equalsCounter().equalsCount).isEqualTo(0);
    }

    @Test
    public void hashCodeEqualsOptimization_otherTypes() {
        MemoizedTest.HashCodeEqualsOptimization optimizedEquals = new AutoValue_MemoizedTest_HashCodeEqualsOptimization(new MemoizedTest.HashCodeEqualsOptimization.EqualsCounter());
        assertThat(optimizedEquals.equals(new Object())).isFalse();
        assertThat(optimizedEquals.equals(null)).isFalse();
        assertThat(optimizedEquals.equalsCounter().equalsCount).isEqualTo(0);
        assertThat(optimizedEquals.hashCodeCount).isEqualTo(0);
    }

    @Test
    public void hashCodeEqualsOptimization_hashCodeIgnoredForSameInstance() {
        MemoizedTest.HashCodeEqualsOptimization optimizedEquals = new AutoValue_MemoizedTest_HashCodeEqualsOptimization(new MemoizedTest.HashCodeEqualsOptimization.EqualsCounter());
        assertThat(optimizedEquals.equals(optimizedEquals)).isTrue();
        assertThat(optimizedEquals.equalsCounter().equalsCount).isEqualTo(0);
        assertThat(optimizedEquals.hashCodeCount).isEqualTo(0);
    }

    @Test
    public void hashCodeEqualsOptimization_offWhenEqualsIsFinal() {
        MemoizedTest.HashCodeEqualsOptimizationOffWhenEqualsIsFinal memoizedHashCodeAndFinalEqualsMethod = new AutoValue_MemoizedTest_HashCodeEqualsOptimizationOffWhenEqualsIsFinal();
        MemoizedTest.HashCodeEqualsOptimizationOffWhenEqualsIsFinal second = new AutoValue_MemoizedTest_HashCodeEqualsOptimizationOffWhenEqualsIsFinal();
        assertThat(memoizedHashCodeAndFinalEqualsMethod.equals(second)).isTrue();
        assertThat(memoizedHashCodeAndFinalEqualsMethod.hashCodeCount).isEqualTo(0);
        memoizedHashCodeAndFinalEqualsMethod.hashCode();
        memoizedHashCodeAndFinalEqualsMethod.hashCode();
        assertThat(memoizedHashCodeAndFinalEqualsMethod.hashCodeCount).isEqualTo(1);
    }
}

