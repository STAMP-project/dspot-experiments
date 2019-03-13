package com.jsoniter;


import com.jsoniter.any.Any;
import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.TestCase;


public class TestNull extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public static class TestObject1 {
        public Boolean field;
    }

    public void test_null_as_Boolean() {
        TestNull.TestObject1 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject1.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject2 {
        public Float field;
    }

    public void test_null_as_Float() {
        TestNull.TestObject2 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject2.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject3 {
        public Double field;
    }

    public void test_null_as_Double() {
        TestNull.TestObject3 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject3.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject4 {
        public Byte field;
    }

    public void test_null_as_Byte() {
        TestNull.TestObject4 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject4.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject5 {
        public Character field;
    }

    public void test_null_as_Character() {
        TestNull.TestObject5 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject5.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject6 {
        public Short field;
    }

    public void test_null_as_Short() {
        TestNull.TestObject6 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject6.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject7 {
        public Integer field;
    }

    public void test_null_as_Integer() {
        TestNull.TestObject7 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject7.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject8 {
        public Long field;
    }

    public void test_null_as_Long() {
        TestNull.TestObject8 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject8.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject9 {
        public BigDecimal field;
    }

    public void test_null_as_BigDecimal() {
        TestNull.TestObject9 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject9.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject10 {
        public BigInteger field;
    }

    public void test_null_as_BigInteger() {
        TestNull.TestObject10 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject10.class);
        TestCase.assertNull(val.field);
    }

    public static class TestObject11 {
        public Any field;
    }

    public void test_null_as_Any() {
        TestNull.TestObject11 val = JsonIterator.deserialize("{\"field\":null}", TestNull.TestObject11.class);
        TestCase.assertNull(val.field.object());
    }
}

