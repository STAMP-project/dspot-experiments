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


import QSimpleTypesTest_SimpleTypes.simpleTypes;
import QSimpleTypesTest_SimpleTypes.simpleTypes.bbyteList;
import QSimpleTypesTest_SimpleTypes.simpleTypes.calendarList;
import QSimpleTypesTest_SimpleTypes.simpleTypes.dateList;
import QSimpleTypesTest_SimpleTypes.simpleTypes.sstringList;
import QSimpleTypesTest_SimpleTypes.simpleTypes.timeList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

import static PropertyType.SIMPLE;


public class SimpleTypesTest extends AbstractTest {
    public enum MyEnum {

        VAL1,
        VAL2;}

    public static class CustomLiteral {}

    @SuppressWarnings("serial")
    public static class CustomNumber extends Number {
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
    }

    public static class CustomComparableNumber extends SimpleTypesTest.CustomNumber implements Comparable<SimpleTypesTest.CustomComparableNumber> {
        private static final long serialVersionUID = 4398583038967396133L;

        @Override
        public int compareTo(SimpleTypesTest.CustomComparableNumber o) {
            return 0;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SimpleTypesTest.CustomComparableNumber;
        }
    }

    public static class CustomComparableLiteral implements Comparable<SimpleTypesTest.CustomComparableLiteral> {
        @Override
        public int compareTo(SimpleTypesTest.CustomComparableLiteral o) {
            return 0;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SimpleTypesTest.CustomComparableLiteral;
        }
    }

    public static class CustomGenericComparableLiteral<C> implements Comparable<SimpleTypesTest.CustomComparableLiteral> {
        @Override
        public int compareTo(SimpleTypesTest.CustomComparableLiteral o) {
            return 0;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SimpleTypesTest.CustomGenericComparableLiteral;
        }
    }

    @QueryEntity
    @Config(listAccessors = true)
    public static class SimpleTypes {
        transient int test;

        List<Integer> testList;

        Calendar calendar;

        List<Calendar> calendarList;

        long id;

        List<Long> idList;

        BigDecimal bigDecimal;

        List<BigDecimal> bigDecimalList;

        Byte bbyte;

        List<Byte> bbyteList;

        byte bbyte2;

        Short sshort;

        List<Short> sshortList;

        short sshort2;

        Character cchar;

        List<Character> ccharList;

        char cchar2;

        Double ddouble;

        List<Double> ddoubleList;

        double ddouble2;

        Float ffloat;

        List<Float> ffloatList;

        float ffloat2;

        Integer iint;

        List<Integer> iintList;

        int iint2;

        Locale llocale;

        List<Locale> llocaleList;

        Long llong;

        List<Long> llongList;

        long llong2;

        BigInteger bigInteger;

        String sstring;

        List<String> sstringList;

        Date date;

        List<Date> dateList;

        Time time;

        List<Time> timeList;

        Timestamp timestamp;

        List<Timestamp> timestampList;

        Serializable serializable;

        List<Serializable> serializableList;

        Object object;

        List<Object> objectList;

        Class<?> clazz;

        List<Class> classList2;

        List<Class<?>> classList3;

        List<Class<Package>> classList4;

        List<Class<? extends Date>> classList5;

        Package packageAsLiteral;

        List<Package> packageAsLiteralList;

        SimpleTypesTest.CustomLiteral customLiteral;

        List<SimpleTypesTest.CustomLiteral> customLiteralList;

        SimpleTypesTest.CustomComparableLiteral customComparableLiteral;

        List<SimpleTypesTest.CustomComparableLiteral> customComparableLiteralList;

        SimpleTypesTest.CustomNumber customNumber;

        List<SimpleTypesTest.CustomNumber> customNumberList;

        SimpleTypesTest.CustomComparableNumber customComparableNumber;

        List<SimpleTypesTest.CustomComparableNumber> customComparableNumber2;

        SimpleTypesTest.CustomGenericComparableLiteral customComparableLiteral2;

        List<SimpleTypesTest.CustomGenericComparableLiteral> customComparableLiteral2List;

        SimpleTypesTest.CustomGenericComparableLiteral<Number> customComparableLiteral3;

        List<SimpleTypesTest.CustomGenericComparableLiteral<Number>> customComparableLiteral3List;

        Clob clob;

        List<Clob> clobList;

        Blob blob;

        List<Blob> blobList;

        @QueryTransient
        String skipMe;

        SimpleTypesTest.MyEnum myEnum;

        int[] intArray;

        byte[] byteArray;

        long[] longArray;

        float[] floatArray;

        double[] doubleArray;

        short[] shortArray;

        @QueryType(SIMPLE)
        byte[] byteArrayAsSimple;
    }

    @Test
    public void list_access() {
        // date / time
        dateList.get(0).after(new Date());
        timeList.get(0).after(new Time(0L));
        calendarList.get(0).before(Calendar.getInstance());
        // numeric
        bbyteList.get(0).abs();
        // string
        sstringList.get(0).toLowerCase();
        // boolean
        // QSimpleTypes.simpleTypes.b
    }

    @Test
    public void simple_types() throws IllegalAccessException, NoSuchFieldException {
        start(QSimpleTypesTest_SimpleTypes.class, simpleTypes);
        match(NumberPath.class, "id");
        matchType(Long.class, "id");
        match(NumberPath.class, "bigDecimal");
        matchType(BigDecimal.class, "bigDecimal");
        match(NumberPath.class, "bigInteger");
        matchType(BigInteger.class, "bigInteger");
        // match(PNumber.class, "bbyte");
        match(NumberPath.class, "bbyte2");
        matchType(Byte.class, "bbyte");
        match(NumberPath.class, "ddouble");
        matchType(Double.class, "ddouble");
        match(NumberPath.class, "ddouble2");
        matchType(Double.class, "ddouble2");
        match(NumberPath.class, "ffloat");
        matchType(Float.class, "ffloat");
        match(NumberPath.class, "ffloat2");
        matchType(Float.class, "ffloat2");
        // match(PNumber.class, "iint");
        match(NumberPath.class, "iint2");
        matchType(Integer.class, "iint2");
        match(NumberPath.class, "llong");
        matchType(Long.class, "llong");
        match(NumberPath.class, "llong2");
        matchType(Long.class, "llong2");
        match(ComparablePath.class, "cchar");
        matchType(Character.class, "cchar");
        match(ComparablePath.class, "cchar2");
        matchType(Character.class, "cchar2");
        match(StringPath.class, "sstring");
        match(DateTimePath.class, "date");
        matchType(Date.class, "date");
        match(DateTimePath.class, "calendar");
        matchType(Calendar.class, "calendar");
        // match(PDateTime.class, "timestamp");
        match(TimePath.class, "time");
        matchType(Time.class, "time");
        match(SimplePath.class, "llocale");
        matchType(Locale.class, "llocale");
        match(SimplePath.class, "serializable");
        matchType(Serializable.class, "serializable");
        match(SimplePath.class, "object");
        matchType(Object.class, "object");
        match(SimplePath.class, "clazz");
        matchType(Class.class, "clazz");
        match(SimplePath.class, "packageAsLiteral");
        matchType(Package.class, "packageAsLiteral");
        match(SimplePath.class, "clob");
        matchType(Clob.class, "clob");
        match(SimplePath.class, "blob");
        matchType(Blob.class, "blob");
        match(EnumPath.class, "myEnum");
        matchType(SimpleTypesTest.MyEnum.class, "myEnum");
    }

    @Test
    public void custom_literal() throws IllegalAccessException, NoSuchFieldException {
        start(QSimpleTypesTest_SimpleTypes.class, simpleTypes);
        match(SimplePath.class, "customLiteral");
        matchType(SimpleTypesTest.CustomLiteral.class, "customLiteral");
    }

    @Test
    public void custom_comparableLiteral() throws IllegalAccessException, NoSuchFieldException {
        start(QSimpleTypesTest_SimpleTypes.class, simpleTypes);
        match(ComparablePath.class, "customComparableLiteral");
        matchType(SimpleTypesTest.CustomComparableLiteral.class, "customComparableLiteral");
    }

    @Test
    public void custom_number() throws IllegalAccessException, NoSuchFieldException {
        start(QSimpleTypesTest_SimpleTypes.class, simpleTypes);
        match(SimplePath.class, "customNumber");
        matchType(SimpleTypesTest.CustomNumber.class, "customNumber");
    }

    @Test
    public void custom_comparableNumber() throws IllegalAccessException, NoSuchFieldException {
        start(QSimpleTypesTest_SimpleTypes.class, simpleTypes);
        match(NumberPath.class, "customComparableNumber");
        matchType(SimpleTypesTest.CustomComparableNumber.class, "customComparableNumber");
    }

    @Test
    public void skipped_field1() {
        start(QSimpleTypesTest_SimpleTypes.class, simpleTypes);
        assertMissing("skipMe");
    }

    @Test
    public void skipped_field2() {
        start(QSimpleTypesTest_SimpleTypes.class, simpleTypes);
        assertMissing("test");
    }
}

