/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;


import QGenericTest_GenericType.genericType;
import QGenericTest_GenericType2.genericType2;
import QGenericTest_ItemType.itemType;
import com.querydsl.apt.domain.rel.SimpleType;
import com.querydsl.apt.domain.rel.SimpleType2;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryTransient;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class GenericTest extends AbstractTest {
    @QueryEntity
    public static class GenericType<T extends GenericTest.ItemType> {
        T itemType;
    }

    @QueryEntity
    @SuppressWarnings("unchecked")
    public static class GenericType2<T extends GenericTest.ItemType> {
        T itemType;

        // simple
        GenericTest.GenericSimpleType prop1;

        GenericTest.GenericSimpleType<?> prop2;

        GenericTest.GenericSimpleType<? extends GenericTest.GenericSimpleType<?>> prop3;

        // comparable
        GenericTest.GenericComparableType comp1;

        GenericTest.GenericComparableType<Number> comp2;

        GenericTest.GenericComparableType<Date> comp3;

        // number
        @QueryTransient
        GenericTest.GenericNumberType num1;// NOTE : doesn't work!


        GenericTest.GenericNumberType<Number> num2;

        GenericTest.GenericNumberType<Date> num3;
    }

    public static class GenericSimpleType<T extends GenericTest.GenericSimpleType<T>> {}

    @SuppressWarnings("unchecked")
    public static class GenericComparableType<T> implements Comparable<GenericTest.GenericComparableType<T>> {
        @Override
        public int compareTo(GenericTest.GenericComparableType<T> o) {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof GenericTest.GenericComparableType;
        }
    }

    @SuppressWarnings({ "unchecked", "serial" })
    public static class GenericNumberType<T> extends Number implements Comparable<GenericTest.GenericNumberType<T>> {
        @Override
        public double doubleValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public int compareTo(GenericTest.GenericNumberType<T> o) {
            return 0;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof GenericTest.GenericNumberType;
        }
    }

    @QueryEntity
    @SuppressWarnings("unchecked")
    public static class ItemType {
        GenericTest.Amount<SimpleType> prop;

        SimpleType2<GenericTest.Amount<SimpleType>> prop2;

        SimpleType2<GenericTest.Amount> prop3;

        SimpleType2<?> prop4;
    }

    public static class Amount<T> {}

    @Test
    public void test() throws IllegalAccessException, NoSuchFieldException {
        Assert.assertNotNull(itemType);
        Assert.assertNotNull(genericType);
        Assert.assertNotNull(genericType2);
        start(QGenericTest_GenericType.class, genericType);
        matchType(GenericTest.ItemType.class, "itemType");
        start(QGenericTest_GenericType2.class, genericType2);
        matchType(GenericTest.ItemType.class, "itemType");
        matchType(GenericTest.GenericSimpleType.class, "prop1");
        matchType(GenericTest.GenericSimpleType.class, "prop2");
        matchType(GenericTest.GenericSimpleType.class, "prop3");
        matchType(GenericTest.GenericComparableType.class, "comp1");
        matchType(GenericTest.GenericComparableType.class, "comp2");
        matchType(GenericTest.GenericComparableType.class, "comp3");
        assertMissing("num1");
        matchType(GenericTest.GenericNumberType.class, "num2");
        matchType(GenericTest.GenericNumberType.class, "num3");
    }
}

