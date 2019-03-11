package com.jsoniter;


import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import junit.framework.TestCase;


public class TestAnnotationJsonIgnore extends TestCase {
    public static class TestObject1 {
        @JsonIgnore
        public int field2;
    }

    public void test_ignore() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        TestAnnotationJsonIgnore.TestObject1 obj = iter.read(TestAnnotationJsonIgnore.TestObject1.class);
        TestCase.assertEquals(0, obj.field2);
    }

    public static class TestObject2 {
        @JsonIgnore
        public Serializable field2;
    }

    public void test_ignore_no_constructor_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field2': 100}".replace('\'', '"'));
        TestAnnotationJsonIgnore.TestObject2 obj = iter.read(TestAnnotationJsonIgnore.TestObject2.class);
        TestCase.assertNull(obj.field2);
    }

    public static class TestObject3 {
        String field1;

        @JsonIgnore
        ActionListener fieldXXX;

        @JsonCreator
        public TestObject3(@JsonProperty("field2")
        final String field) {
            field1 = null;
            fieldXXX = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(("field2 is " + field));
                }
            };
        }

        @Override
        public String toString() {
            return (("field1=" + (field1)) + ", field2=") + (fieldXXX);
        }
    }

    public void test_json_ignore_with_creator() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field2\": \"test\"}");
        TestAnnotationJsonIgnore.TestObject3 t = iter.read(TestAnnotationJsonIgnore.TestObject3.class);
        TestCase.assertNotNull(t.fieldXXX);
    }
}

